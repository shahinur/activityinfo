package org.activityinfo.ui.vdom;

import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.junit.Test;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.activityinfo.ui.vdom.shared.html.H.style;

public class ExampleTest {



    private VTree render(int count) {
        return div(style().textAlign("center")
                          .verticalAlign("center")
                          .lineHeight(100+count)
                          .border("1px solid red")
                          .width(100+count)
                          .height(100+count),
                Integer.toString(count));

    }

    @Test
    public void example() {




    }

}