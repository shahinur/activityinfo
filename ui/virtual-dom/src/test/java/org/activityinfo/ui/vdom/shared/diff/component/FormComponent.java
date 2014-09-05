package org.activityinfo.ui.vdom.shared.diff.component;

import org.activityinfo.ui.vdom.shared.html.H;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

public class FormComponent extends VComponent<FormComponent> implements StateListener {

    private final State state;

    public static int constructionCount = 0;

    public static int mountCount = 0;

    public static int willUnmountCount = 0;


    public FormComponent(State state) {
        this.state = state;
        constructionCount++;
    }

    @Override
    protected void componentDidMount() {
        mountCount ++;

        state.addListener(this);
    }

    @Override
    protected void componentWillUnmount() {
        willUnmountCount ++;

        state.removeListener(this);
    }

    @Override
    protected VTree render() {
        VTree[] items = H.map(state.getNames(), new H.Render<String>() {
            @Override
            public VTree render(String item) {
                return new FieldComponent(item);
            }
        });

        return new VNode(HtmlTag.OL, items);
    }

    @Override
    public void onChanged() {
        refresh();
    }
}
