package org.activityinfo.ui.vdom.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;
import org.activityinfo.ui.vdom.client.render.DomBuilder;
import org.activityinfo.ui.vdom.shared.dom.DomNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class DomBuilderTest extends GWTTestCase {
    @Override
    public String getModuleName() {
        return "org.activityinfo.ui.vdom.VDomOverlays";
    }

    public void testDomBuilder() {
        VTree vtree = renderTree();

        DomBuilder builder = new DomBuilder(new SimpleRenderContext());
        DomNode node = builder.render(vtree);

        validateDom((Node) node);

    }


    public void testWidget() {
        VDomWidget widget = new VDomWidget();
        RootPanel.get().add(widget);

        widget.update(renderTree());
    }


    private VTree renderTree() {
        return div(Styles.PANEL,
                    h1("Hello world"),
                    p(className(Styles.LEAD_PARA), t("This too shall pass")));
    }


    private void validateDom(Node node) {
        Document doc = Document.get();
        doc.getBody().appendChild(node);

        assertEquals("Hello world", doc.getElementsByTagName("h1").getItem(0).getInnerText());

        NodeList graphs = doc.getElementsByTagName("p");
        assertEquals(1, graphs.getLength());

        Element p = graphs.getItem(0).cast();
        assertEquals("lead-para", p.getClassName());
        assertEquals("This too shall pass", p.getInnerText());
    }

}
