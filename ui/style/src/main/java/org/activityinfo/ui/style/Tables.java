package org.activityinfo.ui.style;

import org.activityinfo.ui.vdom.shared.tree.VTree;

import java.util.List;

import static org.activityinfo.ui.vdom.shared.html.H.*;

public class Tables {

    public interface TableBuilder<T> {
        VTree renderRow(T row);
    }

    public static <T> VTree responsiveTable(List<T> rows, TableBuilder<T> builder) {

        // TODO(alex) TABLE_EMAIL should be just one of the factors
        return div(BaseStyles.TABLE_RESPONSIVE,
                table(classNames(BaseStyles.TABLE, BaseStyles.TABLE_EMAIL),
                   tableBody(
                       tableRows(rows, builder))));
    }

    private static <T> VTree[] tableRows(List<T> rows, TableBuilder<T> builder) {
        VTree[] tr = new VTree[rows.size()];
        for(int i=0;i!=rows.size();++i) {
            tr[i] = builder.renderRow(rows.get(i));
        }
        return tr;
    }

}
