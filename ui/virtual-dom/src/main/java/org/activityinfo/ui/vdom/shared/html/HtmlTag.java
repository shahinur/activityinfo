package org.activityinfo.ui.vdom.shared.html;

import org.activityinfo.ui.vdom.shared.tree.PropMap;
import org.activityinfo.ui.vdom.shared.tree.Tag;
import org.activityinfo.ui.vdom.shared.tree.VNode;
import org.activityinfo.ui.vdom.shared.tree.VTree;

@SuppressWarnings("SpellCheckingInspection")
public enum HtmlTag implements Tag {

    // Sourced from: https://developer.mozilla.org/en-US/docs/Web/Guide/HTML/HTML5/HTML5_element_list

    // Document metadata

    HTML,
    HEAD,
    META(true) {


    },
    LINK(true) {

        public VNode shortcutIcon(String rel, String href, String type) {
            return create("shortcut icon", href, type);
        }

        public VNode stylesheet(String href) {
            return create("stylesheet", href, null);
        }

        private VNode create(String rel, String href, String type) {
            return new VNode(this, new PropMap()
                    .set("rel", rel)
                    .set("href", href)
                    .set("type", type));
        }
    },
    TITLE,
    BASE,
    STYLE,

    // Scripting
    SCRIPT,
    NOSCRIPT,

    // Sections

    BODY,
    SECTION,
    NAV,
    ARTICLE,
    ASIDE,
    H1,
    H2,
    H3,
    H4,
    H5,
    H6,
    HEADER,
    FOOTER,
    ADDRESS,
    MAIN,

    // Grouping content

    P,
    HR(true),
    PRE,
    BLOCKQUOTE,
    OL,
    UL,
    LI,
    DL,
    DT,
    DD,
    FIGURE,
    FIGCAPTION,
    DIV,

    // Text-level semantics

    A,
    EM,
    STRONG,
    SMALL,
    S,
    CITE,
    Q,
    DFN,
    ABBR,
    DATA,
    TIME,
    CODE,
    VAR,
    SAMP,
    KBD,
    SUB,
    SUP,
    I,
    B,
    U,
    MARK,
    RUBY,
    RT,
    RP,
    BDI,
    BDO,
    SPAN,
    BR(true),
    WBR(true),

    // Embedded content

    IMG(true),
    IFRAME,
    EMBED(true),
    PARAM(true),
    VIDEO,
    AUDIO,
    SOURCE(true),
    TRACK,
    CANVAS,
    MAP,
    AREA,
    SVG,
    MATH,

    // Tabular Data

    TABLE,
    CAPTION,
    COLGROUP,
    COL(true),
    TBODY,
    THEAD,
    TFOOT,
    TR,
    TD,
    TH,

    // Forms

    FORM,
    FIELDSET,
    LEGEND,
    LABEL,
    INPUT(true),
    BUTTON,
    SELECT,
    DATALIST,
    OPTGROUP,
    OPTION,
    TEXTAREA,
    KEYGEN,
    OUTPUT,
    PROGRESS,
    METER
    ;

    private boolean singleton;

    HtmlTag() {
        this(false);
    }

    HtmlTag(boolean singleton) {
        this.singleton = singleton;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public VNode create(VTree... children) {
        assert !singleton;

        return new VNode(this, children);
    }

}
