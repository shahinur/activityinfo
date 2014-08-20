package org.activityinfo.ui.style;

import com.google.gwt.safehtml.shared.SafeUri;
import org.activityinfo.ui.vdom.shared.tree.VTree;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class Media {

    public static VTree media(VTree media, SafeUri uri, VTree title, VTree summary) {
        return div(className(BaseStyles.MEDIA),
                a(href(uri).setClass(BaseStyles.PULL_LEFT), media),
                div(className(BaseStyles.MEDIA_BODY),
                    h4(className(BaseStyles.TEXT_PRIMARY), title),
                    p(className(BaseStyles.EMAIL_SUMMARY), summary)));
    }


}
