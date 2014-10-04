package org.activityinfo.store.blob;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.sun.jersey.core.header.ContentDisposition;
import org.activityinfo.model.auth.AuthenticatedUser;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.joda.time.DateTime;

import javax.ws.rs.core.MediaType;
import java.text.ParseException;
import java.util.Date;

class UserBlob {
    public static final String KIND = "UserBlob";

    public static final long UNKNOWN_SIZE = 0L;

    private static final String USER_PROPERTY = "user";
    private static final String FILE_NAME_PROPERTY = "filename";
    private static final String CONTENT_TYPE_PROPERTY = "contentType";
    private static final String SIZE_PROPERTY = "size";
    private static final String CONTENT_DISPOSITION_PROPERTY = "contentDisposition";
    private static final String CREATION_TIME_PROPERTY = "creationDate";

    private int userId;
    private BlobId blobId;
    private BlobMetadata metadata;
    private DateTime creationTime;
    private long size = UNKNOWN_SIZE;

    public static UserBlob newBlob(AuthenticatedUser user, BlobMetadata metadata) {
        UserBlob userBlob = new UserBlob();
        userBlob.userId = user.getId();
        userBlob.blobId = metadata.getBlobId();
        userBlob.metadata = metadata;
        userBlob.size = UNKNOWN_SIZE;
        userBlob.creationTime = new DateTime();
        return userBlob;
    }

    public static UserBlob fromEntity(Entity entity) {

        UserBlob userBlob = new UserBlob();
        userBlob.userId = ((Number)entity.getProperty(USER_PROPERTY)).intValue();
        userBlob.blobId = BlobId.valueOf(entity.getKey().getName());

        if (entity.getProperty(FILE_NAME_PROPERTY) != null) {
            String filename = (String) entity.getProperty(FILE_NAME_PROPERTY);
            userBlob.metadata = BlobMetadata.attachment(userBlob.blobId, filename, mediaTypeFromFilename(filename));

        } else {
            userBlob.metadata = new BlobMetadata(userBlob.blobId,
                parseContentDisposition(entity),
                MediaType.valueOf(((String) entity.getProperty(CONTENT_TYPE_PROPERTY))));
        }
        userBlob.size = (Long)entity.getProperty(SIZE_PROPERTY);

        if(entity.getProperty(CREATION_TIME_PROPERTY) == null) {
            userBlob.creationTime = new DateTime((Date)entity.getProperty(CREATION_TIME_PROPERTY));
        } else {
            // early versions did not store creation time.
            userBlob.creationTime = new DateTime(2014, 10, 1, 0, 0, 0);
        }

        return userBlob;
    }

    private static ContentDisposition parseContentDisposition(Entity entity) {
        if(entity.getProperty(CONTENT_DISPOSITION_PROPERTY) != null) {
            try {
                return new ContentDisposition((String)entity.getProperty(CONTENT_DISPOSITION_PROPERTY));
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    private static MediaType mediaTypeFromFilename(String filename) {
        if(filename.toLowerCase().endsWith(".jpg")) {
            return MediaType.valueOf("image/jpeg");
        } else if(filename.toLowerCase().endsWith(".csv")) {
            return MediaType.valueOf("text/csv");
        } else {
            return MediaType.APPLICATION_OCTET_STREAM_TYPE;
        }
    }

    static Key metadataKey(BlobId blobId) {
        return KeyFactory.createKey(KIND, blobId.asString());
    }

    public int getUserId() {
        return userId;
    }

    public BlobId getBlobId() {
        return blobId;
    }

    public BlobMetadata getMetadata() {
        return metadata;
    }

    public long getSize() {
        return size;
    }

    public DateTime getCreationTime() {
        return creationTime;
    }

    public Entity toEntity() {
        Entity entity = new Entity(metadataKey(blobId));
        entity.setProperty(USER_PROPERTY, userId);
        entity.setUnindexedProperty(CONTENT_DISPOSITION_PROPERTY, metadata.getBlobId());
        entity.setUnindexedProperty(CONTENT_TYPE_PROPERTY, metadata.getContentType());
        entity.setUnindexedProperty(CREATION_TIME_PROPERTY, creationTime.toDate());
        if(size == UNKNOWN_SIZE) {
            entity.setProperty(SIZE_PROPERTY, UNKNOWN_SIZE);
        } else {
            entity.setUnindexedProperty(SIZE_PROPERTY, UNKNOWN_SIZE);
        }
        return entity;
    }

    public String getGcsKey() {
        return blobId.asString();
    }
}
