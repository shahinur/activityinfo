package org.activityinfo.ui.vdom.shared.html;

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.ui.vdom.shared.tree.*;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import static org.activityinfo.ui.vdom.shared.html.HtmlTag.*;

public class H {

    public static VNode html(VTree... children) {
        return new VNode(HtmlTag.HTML, children);
    }

    public static VNode head(VTree... children) {
        return new VNode(HtmlTag.HEAD, children);
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

    public static VNode body(Style style, VTree... children) {
        return new VNode(HtmlTag.BODY, style.asPropMap(), children);
    }

    public static VNode div(Style style, String text) {
        return new VNode(DIV, style.asPropMap(), new VText(text));
    }

    public static VNode div(HasClassNames classNames, VTree... children) {
        return new VNode(DIV, PropMap.withClasses(classNames), children);
    }

    public static VNode div(String className, VTree... children) {
        return new VNode(DIV, PropMap.withClasses(className), children);
    }

    public static VNode section(VTree... children) {
        return new VNode(SECTION, null, children);
    }

    public static Style style() {
        return new Style();
    }

    public static Map<String, String> props(String... keyValues) {
        Map<String, String> propMap = new HashMap<>();
        for(int i=0;i<keyValues.length;i+=2) {
            propMap.put(keyValues[0], keyValues[1]);
        }
        return propMap;
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

    public static VNode span(HasClassNames classNames, String text) {
        return new VNode(SPAN, PropMap.withClasses(classNames), new VText(text));
    }

    public static VNode h1(VTree... children) {
        return new VNode(H1, children);
    }

    public static VTree button(String classNames, VTree... children) {
        return new VNode(BUTTON, PropMap.withClasses(classNames));
    }

    public static VTree button(PropMap propMap, VTree... children) {
        return new VNode(BUTTON, propMap, children);
    }


    public static PropMap className(HasClassNames className) {
        return PropMap.withClasses(className.getClassNames());
    }

    public static PropMap classNames(HasClassNames class1, HasClassNames class2) {
        return PropMap.withClasses(class1.getClassNames() + " " + class2.getClassNames());
    }

    public static PropMap classNames(HasClassNames class1, HasClassNames class2, HasClassNames class3) {
        return PropMap.withClasses(
                class1.getClassNames() + " " +
                class2.getClassNames() + " " +
                class3.getClassNames());
    }

    public static PropMap classNames(HasClassNames class1, HasClassNames class2, HasClassNames class3, HasClassNames class4) {
        return PropMap.withClasses(
                class1.getClassNames() + " " +
                class2.getClassNames() + " " +
                class3.getClassNames() + " " +
                class4.getClassNames());
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

    public static VTree h2(VTree... children) {
        return new VNode(HtmlTag.H2, children);
    }

    public static class Meta {


        private static VNode create(String name, String content) {
            return new VNode(HtmlTag.META, new PropMap().set("name", name).set("content", content));
        }

        public static VNode charset(Charset charset) {
            return new VNode(HtmlTag.META, new PropMap().set("charset", charset.name()));
        }

        public static VNode viewport(String content) {
            return create("viewport", content);
        }

    }

    public static class Link {

        public static VNode stylesheet(String href) {
            return new VNode(HtmlTag.LINK, new PropMap()
                    .set("rel", "stylesheet")
                    .set("href", href));
        }

    }
}
