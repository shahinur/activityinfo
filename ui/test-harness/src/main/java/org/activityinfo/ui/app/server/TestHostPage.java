package org.activityinfo.ui.app.server;

import com.google.common.collect.Lists;
import org.activityinfo.ui.app.client.chrome.Chrome;
import org.activityinfo.ui.app.client.chrome.PageContext;
import org.activityinfo.ui.app.client.chrome.tree.NavigationTree;
import org.activityinfo.ui.style.tree.TreeComponent;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class TestHostPage {


    /**
     * Renders the page skeleton
     */
    public static VNode renderPage(PageContext pageContext, VNode vNode) {

        return html(
            header(pageContext),
                body(
                    vNode,
                    Chrome.historyIFrame()
                ));
    }

    private static VNode header(PageContext pageContext) {

        List<VTree> decls = Lists.newArrayList();
        decls.add(meta(charset(UTF_8_CHARSET)));
        decls.add(meta(viewport(DEVICE_WIDTH, 1.0, 1.0)));
        decls.add(title(pageContext.getApplicationTitle()));
        decls.add(link(stylesheet(pageContext.getStylesheetUrl())));
        decls.add(classStylesheet(TreeComponent.class));
        decls.add(classStylesheet(NavigationTree.class));
        decls.add(script("/less-1.7.4.min.js"));
        decls.add(script(pageContext.getBootstrapScriptUrl()));

        return head(Children.toArray(decls));
    }

    private static VNode classStylesheet(Class clazz) {

        PropMap propMap = new PropMap();
        propMap.set("rel", "stylesheet/less");
        propMap.set("type", "text/css");
        propMap.set("href", "/style/" + clazz.getName());
        return link(propMap);
    }

}
