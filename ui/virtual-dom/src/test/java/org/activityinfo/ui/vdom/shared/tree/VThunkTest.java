package org.activityinfo.ui.vdom.shared.tree;

import org.activityinfo.ui.vdom.shared.diff.Diff;
import org.activityinfo.ui.vdom.shared.diff.VDiff;
import org.activityinfo.ui.vdom.shared.html.HtmlRenderer;
import org.activityinfo.ui.vdom.shared.html.HtmlTag;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class VThunkTest {


    public class Sqrt extends VThunk<Sqrt> {

        private final double value;

        public Sqrt(double value) {
            this.value = value;
        }

        @Override
        public VTree render() {
            evaluationCount ++;
            return new VText(Double.toString(Math.sqrt(value)));
        }

        @Override
        public boolean shouldUpdate(Sqrt previousProperties) {
            return value != previousProperties.value;
        }
    }

    public VTree render(double value, Locale locale) {
        if(locale == Locale.ENGLISH) {
            return new VNode(HtmlTag.P, new VText("The square root of " + value + " is "), new Sqrt(value));
        } else {
            return new VNode(HtmlTag.P, new VText("La racine carree de " + value + " est "), new Sqrt(value));
        }
    }

    public String toString(VTree tree) {
        HtmlRenderer renderer = new HtmlRenderer();
        tree.accept(renderer);
        return renderer.getHtml();
    }


    private int evaluationCount;

    @Before
    public void resetEvaluationCount() {
        evaluationCount = 0;
    }

    @Test
    public void initialRender() {

        assertThat(toString(render(4.0, Locale.ENGLISH)), equalTo("<p>The square root of 4.0 is 2.0</p>"));
        assertThat(evaluationCount, equalTo(1));

        assertThat(toString(render(4.0, Locale.FRANCE)), equalTo("<p>La racine carree de 4.0 est 2.0</p>"));
        assertThat(evaluationCount, equalTo(2));
    }

    @Test
    public void memoized() {
        VTree a = render(16.0, Locale.ENGLISH);
        String expectedResult = "<p>The square root of 16.0 is 4.0</p>";

        assertThat(toString(a), equalTo(expectedResult));
        assertThat(toString(a), equalTo(expectedResult));
        assertThat(toString(a), equalTo(expectedResult));

        assertThat(evaluationCount, equalTo(1));
    }


    @Test
    public void noUpdate() {
        VTree a = render(16.0, Locale.ENGLISH);
        assertThat(toString(a), equalTo("<p>The square root of 16.0 is 4.0</p>"));

        VTree b = render(16.0, Locale.ENGLISH);
        VDiff diff = Diff.diff(a, b);

        assertThat(diff.isEmpty(), equalTo(true));
        assertThat(evaluationCount, equalTo(1));

    }

}