package org.activityinfo.ui.client.importer;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.testing.StubScheduler;
import com.google.gwt.junit.GWTMockUtilities;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.core.shared.importing.model.ColumnAction;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.model.MapExistingAction;
import org.activityinfo.core.shared.importing.source.SourceColumn;
import org.activityinfo.core.shared.importing.source.SourceRow;
import org.activityinfo.core.shared.importing.strategy.FieldImporterColumn;
import org.activityinfo.core.shared.importing.strategy.ImportTarget;
import org.activityinfo.core.shared.importing.validation.ValidatedRow;
import org.activityinfo.core.shared.importing.validation.ValidatedRowTable;
import org.activityinfo.core.shared.importing.validation.ValidationResult;
import org.activityinfo.legacy.shared.adapter.ResourceLocatorAdaptor;
import org.activityinfo.promise.Promise;
import org.activityinfo.server.command.CommandTestCase2;
import org.activityinfo.ui.client.component.importDialog.Importer;
import org.junit.Before;

import java.util.List;

import static org.activityinfo.core.client.PromiseMatchers.assertResolves;

public class AbstractImporterTest  {
    public static final int COLUMN_WIDTH = 30;

    protected ImportModel importModel;
    protected StubScheduler scheduler;
    protected List<ImportTarget> targets;
    protected Importer importer;

    @Before
    public final void setupAdapters() {

        scheduler = new StubScheduler();

        // disable GWT.create so that references in static initializers
        // don't sink our test

        GWTMockUtilities.disarm();
    }

    protected void dumpHeaders(List<FieldImporterColumn> importColumns) {

        System.out.print("  ");
        for(FieldImporterColumn col : importColumns) {
            System.out.print("  " + cell(col.getAccessor().getHeading()));
        }
        System.out.println();
        System.out.println(Strings.repeat("-", COLUMN_WIDTH * importColumns.size()));

    }

    private String cell(String text) {
        int width = COLUMN_WIDTH - 2;
        if(text.length() < width) {
            return Strings.padEnd(text, width, ' ');
        } else {
            return text.substring(0, width);
        }
    }

    protected void dumpRows(ValidatedRowTable table) {
        int numRows = table.getRows().size();
        int numColumns = table.getColumns().size();

        for(int i=0;i!=numRows;++i) {
            SourceRow sourceRow = importModel.getSource().getRows().get(i);
            ValidatedRow resultRow = table.getRows().get(i);

            for(int j=0;j!=numColumns;++j) {
                FieldImporterColumn column = table.getColumns().get(j);
                String importedValue = Strings.nullToEmpty(column.getAccessor().getValue(sourceRow));
                ValidationResult result = resultRow.getResult(j);

                String cell = "";
                if(result.wasConverted()) {
                    cell = importedValue + " [" + result.getConvertedValue() + "]";
                } else {
                    cell = importedValue;
                }

                System.out.print(" " + icon(result) + Strings.padEnd(cell, COLUMN_WIDTH - 2, ' '));
            }
            System.out.println();
        }
    }

    private String icon(ValidationResult status) {
        if(status.hasTypeConversionError()) {
            return "x";
        } else if(status.wasConverted() && status.getConfidence() < 0.9) {
            return "!";
        } else {
            return " ";
        }
    }

    protected ColumnAction target(String debugFieldPath) {
        if(targets == null) {
            targets = importer.getImportTargets();
        }
        List<String> options = Lists.newArrayList();
        for(ImportTarget target : targets) {
            if(target.getLabel().equals(debugFieldPath)) {
                return new MapExistingAction(target);
            }
            options.add(target.getLabel());
        }
        throw new RuntimeException(String.format("No field matching '%s', we have: %s",
                debugFieldPath, options));
    }

    protected int columnIndex(String header) {
        for(SourceColumn column : importModel.getSource().getColumns()) {
            if(column.getHeader().trim().equals(header)) {
                return column.getIndex();
            }
        }
        throw new RuntimeException("No imported column with header " + header);
    }

    protected void dumpList(final String title, Iterable<?> items) {
        System.out.println(title + ":");
        System.out.println("-----------------------");
        System.out.println(Joiner.on("\n").join(items));
        System.out.println("-----------------------");
        System.out.println();
    }

    protected void showValidationGrid(ValidatedRowTable rowTable) {
        dumpHeaders(rowTable.getColumns());
        dumpRows(rowTable);
    }

}
