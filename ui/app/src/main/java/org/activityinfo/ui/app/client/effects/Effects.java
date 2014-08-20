package org.activityinfo.ui.app.client.effects;

import com.google.common.base.Function;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import org.activityinfo.promise.Promise;

import javax.annotation.Nullable;

public class Effects {

    private final Promise<Void> trigger;


    public Effects() {
        trigger = Promise.done();
    }

    private Effects(Promise<Void> trigger) {
        this.trigger = trigger;
    }

    public static Effects fadeOut(Element element, final int duration) {
        return new Effects().thenFadeOut(element, duration);
    }

    public static Effects fadeOut(String id, final int duration) {
        return new Effects().thenFadeOut(Document.get().getElementById(id), duration);
    }

    public static Effects firstWait(int delayMilliseconds) {
        return new Effects().thenWait(delayMilliseconds);
    }

    public Effects thenFadeOut(final String elementId) {
        return thenFadeOut(Document.get().getElementById(elementId), 400);
    }

    public Effects thenFadeOut(final Element element, final int duration) {
        Promise<Void> completed = trigger.join(new Function<Void, Promise<Void>>() {
            @Override
            public Promise<Void> apply(@Nullable Void input) {
                FadeOut effect = new FadeOut(element);
                effect.run(duration);
                return effect.getCompleted();
            }
        });

        return new Effects(completed);
    }

    public Effects thenWait(int milliseconds) {
        final Promise<Void> elapsed = new Promise<>();
        Timer timer = new Timer() {
            @Override
            public void run() {
                elapsed.resolve(null);
            }
        };
        timer.schedule(milliseconds);
        return new Effects(elapsed);
    }

    public Effects then(final Runnable runnable) {
        Promise<Void> completed = trigger.then(new Function<Void, Void>() {
            @Nullable
            @Override
            public Void apply(@Nullable Void input) {
                runnable.run();
                return null;
            }
        });
        return new Effects(completed);
    }
}
