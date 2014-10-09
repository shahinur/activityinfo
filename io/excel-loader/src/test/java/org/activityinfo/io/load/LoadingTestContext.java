package org.activityinfo.io.load;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.form.FormField;
import org.activityinfo.model.record.Record;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.UserResource;
import org.activityinfo.service.blob.BlobId;
import org.activityinfo.service.blob.BlobMetadata;
import org.activityinfo.service.store.ResourceCursor;
import org.activityinfo.service.store.StoreLoader;
import org.activityinfo.service.tasks.TaskContext;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

public class LoadingTestContext implements TaskContext {

    private List<Resource> loadedResources = new ArrayList<>();


    @Override
    public OutputStream createBlob(BlobId blobId, String filename, String contentType) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public UserResource getResource(ResourceId resourceId) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public ResourceCursor openCursor(ResourceId formClassId) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public StoreLoader beginLoad(ResourceId parentId) throws Exception {
        return new Loader();
    }

    @Override
    public ByteSource getBlob(BlobId blobId) {
        return Resources.asByteSource(Resources.getResource(blobId.asString()));
    }

    @Override
    public BlobMetadata getBlobMetadata(BlobId blobId) {
        return BlobMetadata.attachment(blobId, blobId.asString());
    }

    private class Loader implements StoreLoader {

        @Override
        public void create(Resource resource, boolean hasChildren) {
            loadedResources.add(resource.copy());
        }

        @Override
        public long commit() {
            return 1;
        }
    }

    public List<LoadedForm> getLoadedForms() {
        List<LoadedForm> forms = Lists.newArrayList();
        for(Resource resource : loadedResources) {
            if(FormClass.CLASS_ID.equals(resource.getClassId())) {
                forms.add(new LoadedForm(FormClass.fromResource(resource)));
            }
        }
        return forms;
    }

    public class LoadedForm {
        private final Map<String, FormField> fieldMap = new HashMap<>();
        private FormClass formClass;

        public LoadedForm(FormClass formClass) {
            this.formClass = formClass;
            for(FormField field : formClass.getFields()) {
                fieldMap.put(field.getLabel(), field);
            }
        }

        public FormClass formClass() {
            return formClass;
        }

        public FormField field(String fieldName) {
            assertThat(fieldMap.keySet(), hasItem(fieldName));
            return fieldMap.get(fieldName);
        }

        public List<String> fieldStringValues(String fieldName) {
            FormField field = field(fieldName);
            List<String> values = Lists.newArrayList();
            for(Resource resource : loadedResources) {
                if(resource.getClassId().equals(formClass.getId())) {
                    Record record = resource.getValue();
                    values.add("" + record.get(field.getName()));
                }
            }
            return values;
        }

        public int instanceCount() {
            int count = 0;
            for(Resource resource : loadedResources) {
                if(resource.getClassId().equals(formClass.getId())) {
                    count ++;
                }
            }
            return count;
        }
    }

}
