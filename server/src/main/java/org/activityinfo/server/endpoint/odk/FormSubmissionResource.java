package org.activityinfo.server.endpoint.odk;

import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.images.Image;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.inject.Inject;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.image.ImageRowValue;
import org.activityinfo.model.type.image.ImageValue;
import org.activityinfo.model.type.primitive.TextValue;
import org.activityinfo.server.command.ResourceLocatorSync;
import org.activityinfo.service.blob.BlobFieldStorageService;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.OdkFormSubmissionBackupService;
import org.w3c.dom.Element;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.appengine.api.images.ImagesServiceFactory.makeImage;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.SERVICE_UNAVAILABLE;

@Path("/submission")
public class FormSubmissionResource {
    private static final Logger LOGGER = Logger.getLogger(FormSubmissionResource.class.getName());

    final private OdkFieldValueParserFactory factory;
    final private ResourceLocatorSync locator;
    final private AuthenticationTokenService authenticationTokenService;
    final private BlobFieldStorageService blobFieldStorageService;
    final private OdkFormSubmissionBackupService odkFormSubmissionBackupService;

    @Inject
    public FormSubmissionResource(OdkFieldValueParserFactory factory, ResourceLocatorSync locator,
                                  AuthenticationTokenService authenticationTokenService,
                                  BlobFieldStorageService blobFieldStorageService,
                                  OdkFormSubmissionBackupService odkFormSubmissionBackupService) {
        this.factory = factory;
        this.locator = locator;
        this.authenticationTokenService = authenticationTokenService;
        this.blobFieldStorageService = blobFieldStorageService;
        this.odkFormSubmissionBackupService = odkFormSubmissionBackupService;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_XML)
    public Response submit(byte bytes[]) {
        ResourceId resourceId = ResourceId.generateId();
        try {
            odkFormSubmissionBackupService.backup(resourceId, ByteSource.wrap(bytes));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Form submission could not be backed up to GCS", e);
            LOGGER.info(new String(bytes, Charsets.UTF_8));
        }

        XFormInstance instance = new XFormInstance(bytes);

        AuthenticationToken authenticationToken = instance.getAuthenticationToken();
        int formClassId;
        AuthenticatedUser user;
        try {
            int userId = authenticationTokenService.getUserId(authenticationToken);
            formClassId = authenticationTokenService.getFormClassId(authenticationToken);
            user = new AuthenticatedUser("XYZ", userId, "@");
        } catch (EntityNotFoundException entityNotFoundException) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        } catch (RuntimeException runtimeException) {
            throw runtimeException;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        FormClass formClass = locator.getFormClass(CuidAdapter.activityFormClass(formClassId));
        FormInstance formInstance = new FormInstance(ResourceId.valueOf(instance.getId()), formClass.getId());

        for (FormField formField : formClass.getFields()) {
            Optional<Element> element = instance.getFieldContent(formField.getId());
            if (element.isPresent()) {
                formInstance.set(formField.getId(), tryParse(formInstance, formField, element));
            }
        }

        for (FieldValue fieldValue : formInstance.getFieldValueMap().values()) {
            if (fieldValue instanceof ImageValue) {
                persistImageData(user, instance, (ImageValue) fieldValue);
            }
        }

        formInstance.set(ResourceId.valueOf("backupBlobId"), resourceId.asString());

        locator.persist(formInstance);
        return Response.status(CREATED).build();
    }

    private FieldValue tryParse(FormInstance formInstance, FormField formField, Optional<Element> element) {
        try {
            OdkFieldValueParser odkFieldValueParser = factory.fromFieldType(formField.getType());
            return odkFieldValueParser.parse((Element) element);

        } catch (Exception e) {
            String text = OdkHelper.extractText(element.get());

            if (text == null) {
                LOGGER.log(Level.SEVERE, "Malformed Element in form instance prevents parsing", e);
            } else if (!text.equals("")) {
                LOGGER.log(Level.WARNING, "Can't parse form instance contents, storing as text", e);
                formInstance.set(formField.getId(), TextValue.valueOf(text));
            }
        }
        return null;
    }

    private void persistImageData(AuthenticatedUser user, XFormInstance instance, ImageValue fieldValue) {
        ImageRowValue imageRowValue = fieldValue.getValues().get(0);
        if (imageRowValue.getFilename() != null) {
            try {
                BodyPart bodyPart = instance.findBodyPartByFilename(imageRowValue.getFilename());
                Image image = makeImage(ByteStreams.toByteArray(bodyPart.getInputStream()));

                String contentDisposition = bodyPart.getDisposition();
                String mimeType = bodyPart.getContentType();
                ByteSource byteSource = ByteSource.wrap(image.getImageData());
                imageRowValue.setMimeType(mimeType);
                imageRowValue.setHeight(image.getHeight());
                imageRowValue.setWidth(image.getWidth());


                blobFieldStorageService.put(user, contentDisposition, mimeType,
                        new BlobId(imageRowValue.getBlobId()), byteSource);

            } catch (MessagingException messagingException) {
                LOGGER.log(Level.SEVERE, "Unable to parse input", messagingException);
                throw new WebApplicationException(Response.status(BAD_REQUEST).build());
            } catch (IOException ioException) {
                LOGGER.log(Level.SEVERE, "Could not write image to GCS", ioException);
                throw new WebApplicationException(Response.status(SERVICE_UNAVAILABLE).build());
            }
        }
    }
}
