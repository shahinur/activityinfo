    package org.activityinfo.ui.client.style;

import com.bedatadriven.rebar.style.client.IconSet;
import com.google.gwt.core.shared.GWT;

/**
 * Application-wide icons
 */
public interface Icons extends IconSet {

    Icons INSTANCE = GWT.create(Icons.class);

    @Source(value = "icons/fontawesome.svg", glyph = 0xf055)
    String add();

    @Source(value = "icons/add.svg")
    String add2();

    @Source(value = "icons/fontawesome.svg", glyph = 0xf0d7)
    String caretDown();

    @Source(value = "icons/icomoon/plus.svg")
    String plus();

    @Source(value = "icons/icomoon/remove.svg")
    String remove();

    @Source(value = "icons/icomoon/remove2.svg")
    String remove2();

    @Source(value = "icons/icomoon/pencil.svg")
    String edit();

    @Source(value = "icons/icomoon/undo.svg")
    String undo();

    @Source(value = "icons/icomoon/redo.svg")
    String redo();

    @Source(value = "icons/icomoon/cogs.svg")
    String bulkEdit();

    @Source(value = "icons/icomoon/cog.svg")
    String configure();

    @Source(value = "icons/icomoon/filter.svg")
    String filter();

    @Source(value = "icons/icomoon/wrench.svg")
    String wrench();

    @Source(value = "icons/ocha.svg", glyph = 0xe61a)
    String form();

    @Source(value = "icons/ocha.svg", glyph = 0xe6c4)
    String location();

    @Source(value = "icons/fontawesome.svg", glyph = 0xf115)
    String folder();

    @Source(value = "icons/fontawesome.svg", glyph = 0xf114)
    String folderOpen();

    @Source(value = "icons/fontawesome.svg", glyph = 0xf057)
    String delete();

    @Source("icons/icomoon/mobile.svg")
    String mobileDevice();

    @Source("icons/icomoon/file-excel.svg")
    String excelFile();

    @Source("icons/icomoon/cloud-upload.svg")
    String importIcon();

    @Source("icons/icomoon/table.svg")
    String table();

    @Source("icons/icomoon/earth.svg")
    String map();

    @Source("icons/icomoon/info.svg")
    String overview();

    @Source("icons/icomoon/arrow-up.svg")
    String arrowUp();

    @Source("icons/icomoon/arrow-down.svg")
    String arrowDown();

    @Source("icons/icomoon/arrow-left.svg")
    String arrowLeft();

    @Source("icons/icomoon/arrow-right.svg")
    String arrowRight();

    /**
     * Symbolizes a problem connecting to the server
     */
    @Source("icons/disconnected.svg")
    String connectionProblem();

    /**
     * Symbolizes an unexpected exception (that is, a bug!)
     */
    @Source(value = "icons/fontawesome.svg", glyph = 0xf188)
    String exception();

}
