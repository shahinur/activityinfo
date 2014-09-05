package org.activityinfo.ui.style;

import com.google.common.collect.Lists;
import org.activityinfo.ui.vdom.shared.html.Children;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.style.BaseStyles.*;
import static org.activityinfo.ui.vdom.shared.html.H.*;

public class Panel extends VComponent {

    private PanelStyle style;
    private VTree title;
    private VTree content;
    private VTree footer;
    private VTree introParagraph;

    public Panel() {
    }

    public Panel(VTree content) {
        this.style = PanelStyle.DEFAULT;
        this.content = content;
    }

    public Panel(String title, VTree content) {
        this.style = PanelStyle.DEFAULT;
        this.title = t(title);
        this.content = content;
    }

    public PanelStyle getStyle() {
        return style;
    }

    public void setStyle(PanelStyle style) {
        this.style = style;
    }

    public VTree getTitle() {
        return title;
    }

    public void setTitle(VTree title) {
        this.title = title;
    }

    public VTree getContent() {
        return content;
    }

    public void setContent(VTree content) {
        this.content = content;
    }

    public VTree getFooter() {
        return footer;
    }

    public void setFooter(VTree footer) {
        this.footer = footer;
    }

    public VTree getIntroParagraph() {
        return introParagraph;
    }

    public void setIntroParagraph(VTree... content) {
        this.introParagraph = p(content);
    }

    @Override
    protected VTree render() {
        List<VTree> children = Lists.newArrayList();
        if(title != null) {
            children.add(heading());
        }

        children.add(div(PANEL_BODY, content));

        if(footer != null) {
            children.add(div(PANEL_FOOTER, footer));
        }

        return div(PanelStyle.DEFAULT.getClassNames(), Children.toArray(children));
    }

    private VTree heading() {
        List<VTree> headerElements = Lists.newArrayList();
        if(title != null) {
            headerElements.add(h4(className(PANEL_TITLE), title));
        }
        if(introParagraph != null) {
            headerElements.add(introParagraph);
        }
        return div(PANEL_HEADING, Children.toArray(headerElements));
    }

    @Override
    public String getPropertiesForDebugging() {
        return "title = " + title;
    }
}
