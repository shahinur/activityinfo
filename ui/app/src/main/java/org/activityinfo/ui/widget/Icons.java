package org.activityinfo.ui.widget;

import org.activityinfo.ui.style.icons.FontAwesome;

/**
 * Application-wide icons
 */
public class Icons {


    public String add() {
        return FontAwesome.PLUS_CIRCLE.getClassNames();
    }

    public String add2() {
        return "";
    }

    public String caretDown() {
        return FontAwesome.CARET_DOWN.getClassNames();
    }

    public String plus() {
        return FontAwesome.PLUS.getClassNames();
    }

    public String remove() {
        return FontAwesome.TRASH_O.getClassNames();
    }

    public String remove2() {
        return remove();
    }

    public String edit() {
        return FontAwesome.PENCIL.getClassNames();
    }

    public String undo() {
        return FontAwesome.UNDO.getClassNames();
    }

    public String redo() {
        return "";
    }

    public String bulkEdit() {
        return FontAwesome.COGS.getClassNames();
    }

    public String configure() {
        return FontAwesome.COG.getClassNames();
    }

    public String filter() {
        return FontAwesome.FILTER.getClassNames();
    }

    public String wrench() {
        return FontAwesome.WRENCH.getClassNames();
    }

    public String form() {
        return FontAwesome.CLIPBOARD.getClassNames();
    }

    public String location() {
        return FontAwesome.MAP_MARKER.getClassNames();
    }

    public String folder() {
        return FontAwesome.FOLDER.getClassNames();
    }

    public String folderOpen() {
        return FontAwesome.FOLDER_OPEN.getClassNames();
    }

    public String delete() {
        return FontAwesome.TRASH_O.getClassNames();
    }

    public String mobileDevice() {
        return FontAwesome.MOBILE.getClassNames();
    }

    public String excelFile() {
        return FontAwesome.FILE_EXCEL_O.getClassNames();
    }

    public String importIcon() {
        return FontAwesome.CLOUD_UPLOAD.getClassNames();
    }

    public String table() {
        return FontAwesome.TABLE.getClassNames();
    }

    public String map() {
        return FontAwesome.GLOBE.getClassNames();
    }

    public String overview() {
        return FontAwesome.INFO_CIRCLE.getClassNames();
    }

    public String arrowUp() {
        return FontAwesome.ARROW_UP.getClassNames();
    }

    public String arrowDown() {
        return FontAwesome.ARROW_DOWN.getClassNames();
    }

    public String arrowLeft() {
        return FontAwesome.ARROW_LEFT.getClassNames();
    }

    public String arrowRight() {
        return FontAwesome.ARROW_RIGHT.getClassNames();
    }

    /**
     * Symbolizes a problem connecting to the server
     */
    public String connectionProblem(){
        return FontAwesome.BOLT.getClassNames();
    }

    /**
     * Symbolizes an unexpected exception (that is, a bug!)
     */
    public String exception() {
        return FontAwesome.BUG.getClassNames();
    }

}
