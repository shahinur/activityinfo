package org.activityinfo.store.load;

import com.google.common.collect.Lists;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.service.store.ImportWriter;
import org.activityinfo.service.store.InstanceWriter;

import java.util.List;

public class BulkWriter implements ImportWriter {

    private final BulkLoadTransaction tx;

    private List<BulkInstanceWriter> formClasses = Lists.newArrayList();

    public BulkWriter(BulkLoadTransaction tx) {
        this.tx = tx;
    }

    @Override
    public InstanceWriter createFormClass(FormClass formClass) {

        BulkInstanceWriter writer = new BulkInstanceWriter(tx, formClass);
        formClasses.add(writer);
        return writer;
    }

//    public void flush(UpdateTransaction tx) {
//        for(BulkInstanceWriter writer : formClasses) {
//            writer.flushFormClass(tx);
//        }
//    }
}
