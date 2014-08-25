package org.activityinfo.io.importing;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.teklabs.gwt.i18n.server.LocaleProxy;
import org.activityinfo.io.importing.data.PastedTable;
import org.activityinfo.io.importing.model.ImportModel;
import org.activityinfo.io.importing.strategy.FieldImportStrategies;
import org.activityinfo.io.importing.validation.ValidatedRowTable;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.formTree.FormTreePrettyPrinter;
import org.activityinfo.model.legacy.CuidAdapter;
import org.activityinfo.model.type.converter.JvmConverterFactory;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.service.store.AsyncFormTreeBuilder;
import org.activityinfo.store.test.TestResourceStore;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;

public class ImportGeoTest extends AbstractImporterTest {

    private ResourceLocator resourceLocator;
    private AsyncFormTreeBuilder formTreeBuilder;

    @Before
    public void setupLocale() {
        LocaleProxy.initialize();
    }


    @Before
    public void setUp() throws IOException {
        resourceLocator = new TestResourceStore().load("somalia-admin.json").createLocator();
        formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);
    }


    @Ignore("to fix")
    @Test
    public void test() throws IOException {

        FormTree formTree = assertResolves(formTreeBuilder.apply(CuidAdapter.locationFormClass(1451)));
        FormTreePrettyPrinter.print(formTree);

        importModel = new ImportModel(formTree);


        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(Resources.toString(getResource("somali-camps-cleaned.txt"),
                        Charsets.UTF_8));
        source.parseAllRows();

        importModel.setSource(source);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));

        assertThat(importer.getImportTargets(), contains(
                hasProperty("label", Matchers.equalTo("Name")),
                hasProperty("label", Matchers.equalTo("Alternate Name")),
                hasProperty("label", Matchers.equalTo("Region Name")),
                hasProperty("label", Matchers.equalTo("Region Code")),
                hasProperty("label", Matchers.equalTo("District Name")),
                hasProperty("label", Matchers.equalTo("District Code")),
                hasProperty("label", Matchers.equalTo("Latitude")),
                hasProperty("label", Matchers.equalTo("Longitude"))));

        dumpList("COLUMNS", source.getColumns());

        // Step 2: User maps imported columns to FormFields
        dumpList("FIELDS", importer.getImportTargets());
        importModel.setColumnAction(columnIndex("Region"), target("Region Name"));
        importModel.setColumnAction(columnIndex("Admin2"), target("District Name"));
        importModel.setColumnAction(columnIndex("Village Name"), target("Name"));
        importModel.setColumnAction(columnIndex("Pcode"), target("Alternate Name"));
        importModel.setColumnAction(columnIndex("Latitude"), target("Latitude"));
        importModel.setColumnAction(columnIndex("Longitude"), target("Longitude"));


        // Step 3: Validate for user
        ValidatedRowTable validatedResult = assertResolves(importer.validateRows(importModel));
        showValidationGrid(validatedResult);

        assertResolves(importer.persist(importModel));
    }
}
