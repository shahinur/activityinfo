package org.activityinfo.ui.app.client.page.pivot;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.model.analysis.PivotTableModelClass;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.promise.Promise;
import org.activityinfo.ui.app.client.Application;
import org.activityinfo.ui.app.client.store.InstanceState;
import org.activityinfo.ui.flux.store.Store;
import org.activityinfo.ui.flux.store.StoreChangeListener;
import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * View-ish object that listens for changes to the
 */
public class PivotTablePreview extends VComponent implements StoreChangeListener {

    private Application application;
    private final InstanceState workingDraft;
    private Promise<List<Bucket>> buckets;

    public PivotTablePreview(Application application, InstanceState workingDraft) {
        this.application = application;
        this.workingDraft = workingDraft;
    }

    @Override
    protected void componentDidMount() {
        this.workingDraft.addChangeListener(this);
    }

    @Override
    public void onStoreChanged(Store store) {
        requestData();
        refresh();
    }

    private void requestData() {
        buckets = application.getRemoteService().queryCube(
            PivotTableModelClass.INSTANCE.toBean(workingDraft.getUpdatedResource().getValue()));
        buckets.then(new AsyncCallback<List<Bucket>>() {
            @Override
            public void onFailure(Throwable caught) {
                refresh();
            }

            @Override
            public void onSuccess(List<Bucket> result) {
                refresh();
            }
        });
    }

    @Override
    protected void componentWillUnmount() {
        this.workingDraft.removeChangeListener(this);
    }

    @Override
    protected VTree render() {
        if(buckets != null) {
            switch (buckets.getState()) {
                case FULFILLED:
                    return renderList(buckets.get());
                case REJECTED:
                    return p("Failed");
                case PENDING:
                    return p("Loading...");
            }
        }
        return p("");
    }

    private VTree renderList(List<Bucket> buckets) {

        return ul(map(buckets, new H.Render<Bucket>() {

            @Override
            public VTree render(Bucket item) {
                return li("Error rendering table, values = " + item.getValue());
            }
        }));
    }
}
