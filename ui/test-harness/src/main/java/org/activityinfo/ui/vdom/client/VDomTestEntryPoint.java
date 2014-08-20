package org.activityinfo.ui.vdom.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.style;

public class VDomTestEntryPoint implements EntryPoint {


    private VDomWidget widget;

    private int count = 0;

    public VTree render(int count) {

        VTree tree = new MyWidget();

        return div(PropMap.withStyle(
                style().textAlign("center")
                        .verticalAlign("center")
                        .lineHeight(100 + count)
                        .border("1px solid red")
                        .width(100 + count)
                        .height(100 + count)),
                tree);
    }

    @Override
    public void onModuleLoad() {

        widget = new VDomWidget();
        RootPanel.get().add(widget);

//        // 2: Initialise the document

        widget.update(render(count));


        // 3: Wire up the update logic
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                count++;
                widget.update(render(count));
                return true;
            }
        }, 1000);
    }
}
