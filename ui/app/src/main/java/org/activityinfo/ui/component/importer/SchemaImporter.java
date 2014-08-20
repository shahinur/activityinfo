package org.activityinfo.ui.component.importer;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.io.importing.data.PastedTable;

import java.util.Iterator;
import java.util.List;

public class SchemaImporter {

    private ProgressListener progressListener;

    public Iterator<?> getMissingColumns() {
        return null;
    }

    public boolean parseColumns(PastedTable source) {
        return false;
    }

    public void persist(AsyncCallback<Void> asyncCallback) {

    }

    public List<SafeHtml> getWarnings() {
        return null;
    }

    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public ProgressListener getProgressListener() {
        return progressListener;
    }


    public interface ProgressListener {
        void submittingBatch(int batchNumber, int batchCount);
    }

    public void clearWarnings() {

    }

    public void processRows() {

    }
}
