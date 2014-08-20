package org.activityinfo.ui.style;

import com.google.gwt.dom.client.Document;
import org.activityinfo.ui.vdom.shared.tree.VNode;

import static org.activityinfo.ui.vdom.shared.html.H.*;

/**
 * Full screen pre-loader displayed upon application startup
 */
public class PagePreLoader {

    public static final String ID = "preloader";

    public static final String STATUS_ID = "status";

    public static final String ICON_CLASS_NAMES = "fa fa-spinner fa-spin";

    public static final VNode preLoader() {
        return div( id(ID),
                div( id(STATUS_ID),
                        i(className(ICON_CLASS_NAMES))));
    }

    public static void hidePreloader() {
        Document.get().getBody().addClassName(BaseStyles.PAGE_LOADED.getClassNames());
    }
//
//    public static class FadeOut extends Animation {
//
//        private Element element;
//
//        public FadeOut(Element element) {
//            this.element = element;
//        }
//
//        @Override
//        protected void onUpdate(double progress) {
//            element.getStyle().setOpacity(1.0 - progress);
//        }
//    }
//
//    public static void hidePreLoader() {
//
//        final Element preloaderElement = Document.get().getElementById(ID);
//        final Element statusElement = Document.get().getElementById(STATUS_ID);
//
//        new FadeOut(statusElement).run(400);
//        new Timer() {
//            @Override
//            public void run() {
//                new FadeOut(preloaderElement).run(350);
//            }
//        };
////        // Page Preloader
////        jQuery('#status').fadeOut();
////        jQuery('#preloader').delay(350).fadeOut(function(){
////            jQuery('body').delay(350).css({'overflow':'visible'});
////        });
//    }

}
