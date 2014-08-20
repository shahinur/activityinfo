package org.activityinfo.io.importing;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.activityinfo.io.importing.data.PastedTable;
import org.activityinfo.io.importing.match.ColumnMappingGuesser;
import org.activityinfo.io.importing.model.ImportModel;
import org.activityinfo.io.importing.model.MapExistingAction;
import org.activityinfo.io.importing.source.SourceColumn;
import org.activityinfo.io.importing.strategy.FieldImportStrategies;
import org.activityinfo.io.importing.validation.ValidatedRowTable;
import org.activityinfo.model.form.FormInstance;
import org.activityinfo.model.formTree.FormTree;
import org.activityinfo.model.formTree.FormTreePrettyPrinter;
import org.activityinfo.model.legacy.criteria.FieldCriteria;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.type.FieldValue;
import org.activityinfo.model.type.ReferenceValue;
import org.activityinfo.model.type.converter.JvmConverterFactory;
import org.activityinfo.model.type.time.LocalDate;
import org.activityinfo.promise.Promise;
import org.activityinfo.service.store.ResourceLocator;
import org.activityinfo.service.store.AsyncFormTreeBuilder;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static org.activityinfo.model.legacy.CuidAdapter.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

//@SuppressWarnings("GwtClientClassFromNonInheritedModule")
public class ImportSimpleTest extends AbstractImporterTest {

    private static final ResourceId HOUSEHOLD_SURVEY_FORM_CLASS = activityFormClass(1);

    public static final ResourceId MODHUPUR = resourceId(ADMIN_ENTITY_DOMAIN, 24);

    private ResourceLocator resourceLocator;
    private AsyncFormTreeBuilder formTreeBuilder;

    @Before
    public void setUp() throws IOException {
        resourceLocator = new TestResourceStore().load("/dbunit/brac-import.json").createLocator();
        formTreeBuilder = new AsyncFormTreeBuilder(resourceLocator);
    }

    @Test
    public void test() throws IOException {

        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        FormTreePrettyPrinter.print(formTree);

        importModel = new ImportModel(formTree);

        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));
        source.parseAllRows();

        assertThat(source.getRows().size(), equalTo(63));

        importModel.setSource(source);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));

        dumpList("COLUMNS", source.getColumns());

        // Step 2: User maps imported columns to FormFields
        dumpList("FIELDS", importer.getImportTargets());
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultMale"));
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultFemale"));
        importModel.setColumnAction(columnIndex("_CREATION_DATE"), target("Start Date"));
        importModel.setColumnAction(columnIndex("_SUBMISSION_DATE"), target("End Date"));
        importModel.setColumnAction(columnIndex("district"), target("District Name"));
        importModel.setColumnAction(columnIndex("upazila"), target("Upzilla Name"));
        importModel.setColumnAction(columnIndex("Partner"), target("Partner Name"));


        // Step 3: Validate for user
        ValidatedRowTable validatedResult = assertResolves(importer.validateRows(importModel));
        showValidationGrid(validatedResult);

        assertResolves(importer.persist(importModel));


        // AND... verify
        ResourceId adminFieldId = field(HOUSEHOLD_SURVEY_FORM_CLASS, LOCATION_FIELD);
        List<FormInstance> instances = assertResolves(resourceLocator.queryInstances(new FieldCriteria(adminFieldId,
                new ReferenceValue(MODHUPUR))));

        assertThat(instances.size(), equalTo(1));

        FormInstance instance = instances.get(0);
        assertThat(instance.get(field(HOUSEHOLD_SURVEY_FORM_CLASS, START_DATE_FIELD)),
                equalTo((FieldValue)new LocalDate(2012,12,19)));

        assertThat(instance.get(field(HOUSEHOLD_SURVEY_FORM_CLASS, END_DATE_FIELD)),
                equalTo((FieldValue)new LocalDate(2012,12,19)));
    }

    @Test
    public void testExceptionHandling() throws IOException {


        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        importModel = new ImportModel(formTree);

        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));

        importModel.setSource(source);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultMale"));
        importModel.setColumnAction(columnIndex("MEMBER_NO_ADULT_FEMALE"), target("NumAdultFemale"));
        importModel.setColumnAction(columnIndex("_CREATION_DATE"), target("Start Date"));
        importModel.setColumnAction(columnIndex("_SUBMISSION_DATE"), target("End Date"));
        importModel.setColumnAction(columnIndex("district"), target("District Name"));
        importModel.setColumnAction(columnIndex("upazila"), target("Upzilla Name"));
       // importModel.setColumnAction(columnIndex("Partner"), target("Partner Name"));

        Promise<Void> result = importer.persist(importModel);
        assertThat(result.getState(), equalTo(Promise.State.REJECTED));
    }

    @Test
    public void columnMappingGuesser() throws IOException {
        FormTree formTree = assertResolves(formTreeBuilder.apply(HOUSEHOLD_SURVEY_FORM_CLASS));
        FormTreePrettyPrinter.print(formTree);

        importModel = new ImportModel(formTree);

        // Step 1: User pastes in data to import
        PastedTable source = new PastedTable(
                Resources.toString(getResource("org/activityinfo/core/shared/importing/qis.csv"), Charsets.UTF_8));

        importModel.setSource(source);
        importer = new Importer(resourceLocator, formTree, FieldImportStrategies.get(JvmConverterFactory.get()));

        dumpList("COLUMNS", source.getColumns());
        dumpList("FIELDS", importer.getImportTargets());

        // Step 2: Guesser guess mapping
        final ColumnMappingGuesser guesser = new ColumnMappingGuesser(importModel, importer.getImportTargets());
        guesser.guess();

        assertMapping("Partner", "Partner Name");
       // assertMapping("district", "District Name");
        //assertMapping("upazila", "Upzilla Name");
    }

    private void assertMapping(String sourceColumnLabel, String targetColumnLabel) {
        final SourceColumn sourceColumn = importModel.getSourceColumn(columnIndex(sourceColumnLabel));
        assertNotNull(sourceColumn);

        final MapExistingAction columnAction = (MapExistingAction) importModel.getColumnAction(sourceColumn);
        assertTrue(columnAction.getTarget().getLabel().equals(targetColumnLabel));
    }

}
