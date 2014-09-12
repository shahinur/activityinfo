package org.activityinfo.ui.vdom.shared.diff;

import org.activityinfo.ui.vdom.shared.VDomLogger;
import org.activityinfo.ui.vdom.shared.diff.component.*;
import org.activityinfo.ui.vdom.shared.dom.TestRenderContext;
import org.activityinfo.ui.vdom.shared.tree.VTree;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.activityinfo.ui.vdom.shared.html.H.div;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

public class PatchComponentTest {

    @Before
    public void setUp() {

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
        assertThat("dirty", context.isDirty(), equalTo(true));
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

        // initial render
        State state = new State("X", "Y", "Z");
        TestRenderContext context = new TestRenderContext();
        MainPage mainPage = new MainPage(state);
        context.render(mainPage);

        // Save a reference to the form so we can make sure
        // its children are unmounted
        FormComponent form = (FormComponent) mainPage.vNode;

        // Now hide the form, which *should* lead to the form
        // and all its children being unmounted
        VDomLogger.event("hiding main page");
        mainPage.setVisible(false);
        context.updateDirty();

        assertThat("form unmounted", form.isMounted(), equalTo(false));
        assertThat("fields unmounted", Arrays.asList(form.vNode.children()),
                Matchers.<VTree>everyItem(Matchers.<VTree>hasProperty("mounted", equalTo(false))));
    }

    @Test
    public void unmountCalledRecursivelyOnComponentReplacement() {

        FooComponent foo = new FooComponent();
        BarComponent bar = new BarComponent();

        TestRenderContext context = new TestRenderContext();
        VDomLogger.event("foo");
        context.render(foo);

        VDomLogger.event("bar");
        context.render(bar);

        VDomLogger.event("foo");
        context.render(foo);

        VDomLogger.event("bar");
        context.render(bar);

        assertFalse(foo.getChildren().get(0).isMounted());
        assertFalse(foo.getChildren().get(1).isMounted());
    }
}