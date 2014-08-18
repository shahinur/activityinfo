package org.activityinfo.ui.vdom.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import org.activityinfo.ui.vdom.client.patch.Patch;
import org.activityinfo.ui.vdom.shared.diff.Diff;
import org.activityinfo.ui.vdom.shared.diff.VDiff;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.style;

public class VDomTestEntryPoint implements EntryPoint {

    private final ElementBuilder elementBuilder = new ElementBuilder();

    private int count;
    private VTree tree;

    // 1: Create a function that declares what the DOM should look like
    private VTree render(int count) {
        return div(style().textAlign("center")
                        .verticalAlign("center")
                        .lineHeight(100+count)
                        .border("1px solid red")
                        .width(100+count)
                        .height(100+count),
                Integer.toString(count));
    }


    @Override
    public void onModuleLoad() {

        // 2: Initialise the document

        count = 0;      // We need some app data. Here we just store a count.
        tree = render(count);

        final Node rootNode = elementBuilder.createElement(tree);     // Create an initial root DOM node ...
        Document.get().getBody().appendChild(rootNode);    // ... and it should be in the document

        // 3: Wire up the update logic
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                count++;
                VTree newTree = render(count);
                VDiff diff = Diff.diff(tree, newTree);

                Patch patch = new Patch();
                patch.patch(rootNode, diff);
                tree = newTree;
                return true;
            }
        }, 1000);
    }
}
