package org.activityinfo.ui.vdom.shared.html;

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.ui.vdom.shared.tree.*;

import javax.annotation.Nonnull;
import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.HtmlTag.*;

public class H {

    public static final String UTF_8_CHARSET = "UTF-8";

    public static final String DEVICE_WIDTH = "device-width";

    public static VNode html(VTree... children) {
        return new VNode(HtmlTag.HTML, children);
    }

    public static VNode head(VTree... children) {
        return new VNode(HtmlTag.HEAD, children);
    }

    public static VNode meta(PropMap propMap) {
        return new VNode(HtmlTag.META, propMap);
    }

    public static PropMap charset(String charset) {
        return new PropMap().set("charset", charset);
    }

    public static PropMap viewport(String width, double initialScale, double maximumScale) {
        return new PropMap()
                .set("name", "viewport")
                .set("content", "width=" + width + ", initial-scale=" + initialScale +
                                ", maximum-scale=" + maximumScale);
    }

    public static VNode link(PropMap propMap) {
        return new VNode(HtmlTag.LINK, propMap);
    }

    public static PropMap stylesheet(String href) {
        return new PropMap().set("rel", "stylesheet").set("href", href);
    }

    public static VNode title(String title) {
        return new VNode(HtmlTag.TITLE, t(title));
    }

    public static VNode body(VTree... children) {
        return new VNode(HtmlTag.BODY, children);
    }

    public static VNode body(PropMap propMap, VTree... children) {
        return new VNode(HtmlTag.BODY, propMap, children);
    }

    public static VNode body(@Nonnull Style style, VTree... children) {
        return new VNode(HtmlTag.BODY, PropMap.withStyle(style), children);
    }

    public static VNode div(@Nonnull Style style, String text) {
        return new VNode(DIV, PropMap.withStyle(style), new VText(text));
    }

    public static VNode div(String text) {
        return new VNode(DIV, new VText(text));
    }

    public static VNode div(HasClassNames classNames, VTree... children) {
        return new VNode(DIV, PropMap.withClasses(classNames), children);
    }
    public static VNode div(PropMap propMap, VTree... children) {
        return new VNode(DIV, propMap, children);
    }

    public static VNode div(String className, VTree... children) {
        return new VNode(DIV, PropMap.withClasses(className), children);
    }

    public static VNode table(PropMap propMap, VTree... children) {
        return new VNode(TABLE, propMap, children);
    }

    public static VNode tableBody(VTree... children) {
        return new VNode(TBODY, children);
    }

    public static VNode tableRow(VTree... children) {
        return new VNode(TR, children);
    }

    public static VNode tableCell(VTree... children) {
        return new VNode(TD, children);
    }

    public static VNode section(PropMap propMap, VTree... children) {
        return new VNode(SECTION, propMap, children);
    }

    public static VNode section(PropMap propMap) {
        return new VNode(SECTION, propMap);
    }

    public static VNode section(VTree... children) {
        return new VNode(SECTION, children);
    }

    public static Style style() {
        return new Style();
    }

    public static PropMap props() {
        return new PropMap();
    }

    public static VNode ul() {
        return new VNode(UL);
    }

    public static VNode ul(VTree... children) {
        return new VNode(UL, children);
    }


    public static VNode ul(String className, VTree... children) {
        return new VNode(UL, PropMap.withClasses(className), children);
    }

    public static VNode ul(HasClassNames className, VTree... children) {
        return new VNode(UL, PropMap.withClasses(className.getClassNames()), children);
    }

    public static VNode ul(PropMap propMap, VTree... children) {
        return new VNode(UL, propMap, children);
    }

    public static VNode li(VTree... children) {
        return new VNode(LI, null, children);
    }
    public static VNode li(PropMap propMap, VTree... children) {
        return new VNode(LI, propMap, children);
    }


    public static VNode li(String text) {
        return li(t(text));
    }

    public static VNode link(SafeUri href, VTree... children) {
        return new VNode(A, href(href), children);
    }

    public static VNode a(PropMap propMap, VTree... children) {
        return new VNode(A, propMap, children);
    }

    public static PropMap href(SafeUri uri) {
        return new PropMap().set("href", uri.asString());
    }

    public static VText t(String text) {
        return new VText(text);
    }

    public static VText space() {
        return new VText(" ");
    }

    public static VNode span(String text) {
        return new VNode(SPAN, new VText(text));
    }
    public static VNode span(PropMap propMap) {
        return new VNode(SPAN, propMap);
    }

    public static VNode span(HasClassNames classNames, String text) {
        return new VNode(SPAN, PropMap.withClasses(classNames), new VText(text));
    }

    public static VNode h1(VTree... children) {
        return new VNode(H1, children);
    }

    public static VNode h1(String text) {
        return new VNode(H1, t(text));
    }

    public static VNode h2(VTree... children) {
        return new VNode(H2, children);
    }

    public static VNode h2(String text) {
        return new VNode(H2, t(text));
    }

    public static VNode h3(VTree... children) {
        return new VNode(H3, children);
    }

    public static VNode h3(PropMap propMap, VTree... children) {
        return new VNode(H3, propMap, children);
    }

    public static VNode h3(String text) {
        return new VNode(H3, t(text));
    }

    public static VNode h4(VTree... children) {
        return new VNode(H4, children);
    }

    public static VNode h4(PropMap propMap, VTree... children) {
        return new VNode(H4, propMap, children);
    }


    public static VNode h4(String text) {
        return new VNode(H4, t(text));
    }

    public static VNode h5(VTree... children) {
        return new VNode(H5, children);
    }
    public static VNode h5(String text) {
        return new VNode(H5, t(text));
    }

    public static VNode h6(VTree... children) {
        return new VNode(H6, children);
    }

    public static VNode h6(String text) {
        return new VNode(H6, t(text));
    }

    public static VNode p(String text) { return new VNode(P, t(text)); }

    public static VNode p(VTree... children) { return new VNode(P, children); }

    public static VNode p(PropMap propMap, VTree... children) { return new VNode(P, propMap, children); }

    public static VNode strong(String text) { return new VNode(STRONG, t(text)); }

    public static VNode form(PropMap propMap, VTree... children) {
        return new VNode(HtmlTag.FORM, propMap, children);
    }

    public static VNode form(VTree... children) {
        return new VNode(HtmlTag.FORM, children);
    }

    public static PropMap className(HasClassNames className) {
        return PropMap.withClasses(className.getClassNames());
    }

    public static PropMap className(String className) {
        return PropMap.withClasses(className);
    }

    public static PropMap classNames(HasClassNames class1, HasClassNames class2) {
        return PropMap.withClasses(class1.getClassNames() + " " + class2.getClassNames());
    }

    public interface Render<T> {
        VTree render(T item);
    }

    public static <T> VTree[] map(List<T> items, Render<T> render) {
        VTree[] nodes = new VTree[items.size()];
        for(int i=0;i!=items.size();++i) {
            nodes[i] = render.render(items.get(i));
        }
        return nodes;
    }

    public static VNode script(String src) {
        return new VNode(HtmlTag.SCRIPT,
                new PropMap()
                .set("language", "javascript")
                .set("src", src));
    }

    public static PropMap classNames(HasClassNames class1, HasClassNames class2, HasClassNames class3) {
        return PropMap.withClasses(
                class1.getClassNames() + " " +
                class2.getClassNames() + " " +
                class3.getClassNames());
    }

    public static PropMap classNames(HasClassNames class1,
                                     HasClassNames class2,
                                     HasClassNames class3,
                                     HasClassNames class4) {
        return PropMap.withClasses(
                class1.getClassNames() + " " +
                class2.getClassNames() + " " +
                class3.getClassNames() + " " +
                class4.getClassNames());
    }

    public static PropMap id(String id) {
        return props().set("id", id);
    }

    public static VNode i(PropMap propMap, VTree... children) {
        return new VNode(HtmlTag.I, propMap, children);
    }

    public static PropMap classNames(HasClassNames class1,
                                     HasClassNames class2,
                                     HasClassNames class3,
                                     HasClassNames class4,
                                     HasClassNames... classNames) {
        StringBuilder className = new StringBuilder(class1.getClassNames());
        className.append(" ").append(class2.getClassNames());
        className.append(" ").append(class3.getClassNames());
        className.append(" ").append(class4.getClassNames());
        for(HasClassNames name : classNames) {
            className.append(" ");
            className.append(name.getClassNames());
        }
        return PropMap.withClasses(className.toString());
    }
}
