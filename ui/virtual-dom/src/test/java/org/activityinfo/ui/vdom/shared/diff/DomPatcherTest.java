package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.diff.component.FormComponent;
import org.activityinfo.ui.vdom.shared.diff.component.MainPage;
import org.activityinfo.ui.vdom.shared.diff.component.State;
import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.junit.Before;
import org.junit.Test;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class DomPatcherTest {

    @Before
    public void setUp() {
        VDomLogger.ENABLED = true;

        FormComponent.constructionCount = 0;
        FormComponent.mountCount = 0;
        FormComponent.willUnmountCount = 0;
    }

    @Test
    public void test()  {

        State state = new State("A", "B", "C");

        TestRenderContext context = new TestRenderContext();
        context.render(new MainPage(state));

        // ensure initial rendering is correct and that
        assertThat(context.getDomRoot().toString(), equalTo("<ol><li>A</li><li>B</li><li>C</li></ol>"));
        assertThat("listener is attached", state.listener, notNullValue());

        // update our state object, which should result in the FormComponent forcing an update
        VDomLogger.event("state updated B -> X ");
        state.update(1, "X");
        assertThat("dirty", context.dirty, equalTo(true));
        context.updateDirty();
        assertThat(context.getDomRoot().toString(), equalTo("<ol><li>A</li><li>X</li><li>C</li></ol>"));



        // tear down
        VDomLogger.event("tear down");

        context.render(div("the end"));

        assertThat("construction count", FormComponent.constructionCount, equalTo(1));
        assertThat("mount count", FormComponent.mountCount, equalTo(1));
        assertThat("unmount count", FormComponent.willUnmountCount, equalTo(1));
    }

    @Test
    public void dirtyDescendantsOfDirtyThunks()  {

        State state = new State("X", "Y", "Z");

        TestRenderContext context = new TestRenderContext();
        MainPage mainPage = new MainPage(state);
        context.render(mainPage);

        assertThat(context.getDomRoot().toString(), equalTo("<ol><li>X</li><li>Y</li><li>Z</li></ol>"));

        state.update(2, "Q");
        mainPage.pretendAChangeWasTriggered();

        context.updateDirty();

        assertThat(context.getDomRoot().toString(), equalTo("<ol><li>X</li><li>Y</li><li>Q</li></ol>"));

        // make sure the new thunk gets mounted!
        state.update(0, "Z");
        context.updateDirty();

        assertThat(context.getDomRoot().toString(), equalTo("<ol><li>Z</li><li>Y</li><li>Q</li></ol>"));
    }

    @Test
    public void unmountCalled() {
        State state = new State("X", "Y", "Z");
        TestRenderContext context = new TestRenderContext();
        MainPage mainPage = new MainPage(state);

        context.render(mainPage);

        mainPage.setVisible(false);

        context.updateDirty();

        assertThat("unmounted", state.listener, is(nullValue()));
    }


}