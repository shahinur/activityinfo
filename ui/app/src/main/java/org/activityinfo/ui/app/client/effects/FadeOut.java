package org.activityinfo.ui.app.client.effects;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import org.activityinfo.promise.Promise;

public class FadeOut extends Animation {

    private Promise<Void> completed;
    private Element element;

    public FadeOut(Element element) {
        this.element = element;
        this.completed = new Promise<Void>();
    }

    @Override
    protected void onUpdate(double progress) {
        element.getStyle().setOpacity(1 - progress);
    }

    @Override
    protected void onComplete() {
        completed.resolve(null);
    }

    public Promise<Void> getCompleted() {
        element.getStyle().setDisplay(Style.Display.NONE);
        return completed;
    }
}
