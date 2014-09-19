package org.activityinfo.ui.app.client.page.pivot;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.activityinfo.model.analysis.cube.*;
import org.activityinfo.model.form.FormClass;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.table.Bucket;
import org.activityinfo.service.cubes.CubeBuilder;
import org.activityinfo.store.test.TestResourceStore;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.activityinfo.model.analysis.cube.AttributeModel.newNominal;
import static org.activityinfo.model.analysis.cube.AttributeModel.newOrdered;

public class CubeTest {

    public static final String SYSTEM_RELATED = "System related";
    public static final String MANAGEMENT_RELATED = "Management Related";
    public static final String INITIAL_PIPED_SCHEME = "Initial Piped Scheme";
    public static final String PIPED_SCHEME_EXTENSION_REFUGEES = "Piped Scheme Extension Refugees";
    public static final String PIPED_SCHEME_EXTENSION_HOST_COMMUNITY = "Piped Scheme Extension Host Community";
    public static final String HAND_DUG_WELLS = "Hand Dug Wells";
    public static final String EMERGENCY = "Emergency";
    public static final String REPORTED = "Reported";
    public static final String PER_DESIGN = "Per Design";
    public static final String FUNCTIONAL = "Functional";
    public static final String NON_FUNCTIONAL = "Non-functional";
    public static final String REAL = "Real";
    public static final String OBSERVED = "Observed";
    public static final String ABOVE_STANDARD = "Above standard";
    public static final String ACCEPTABLE = "Acceptable";
    public static final String PROBLEMATIC = "Problematic";
    public static final String CRITICAL = "Critical";

    private DimensionModel time;
    private DimensionModel costComponent;
    private DimensionModel system;
    private DimensionModel serviceLevel;
    private DimensionModel population;
    private DimensionModel source;
    private MeasureModel costMeasure;
    private MeasureModel userMeasure;
    private MeasureModel waterPointMeasure;

    private TestResourceStore store;

    private FormClass costs;
    private FormClass wp;
    private FormClass camp;
    private FormClass survey;
    private FormClass design;
    private List<FormClass> forms = Lists.newArrayList();

    private CubeModel cube = new CubeModel();
    private AttributeModel componentAttrib;
    private AttributeModel yearAttribute;


    public void defineDimensions() {


        // DIMENSIONS

        // Time - interested only in year by year
        time = DimensionModel.newDimension("Time");
        yearAttribute = time.addAttribute(newOrdered("Year", asList("2011", "2012", "2013", "2014")));

        // Costs - corresponds to the lifecycle components
        costComponent = DimensionModel.newDimension("Cost Components");
        costComponent.addAttribute(newNominal("Component Type", asList(SYSTEM_RELATED, MANAGEMENT_RELATED)));
        componentAttrib = costComponent.addAttribute(newNominal("Component", asList("CapEx", "OpEx", "CapManEx", "ExDS", "ExIDS")));

        // System - between different schemes down to water points
        system = DimensionModel.newDimension("System");
        system.addAttribute(newNominal("Scheme", Arrays.asList(
            INITIAL_PIPED_SCHEME, PIPED_SCHEME_EXTENSION_REFUGEES,
            PIPED_SCHEME_EXTENSION_HOST_COMMUNITY,
            HAND_DUG_WELLS,
            EMERGENCY)));
        
        system.addAttribute(newNominal("Water Point"));
        system.addAttribute(newNominal("Water Point", asList(FUNCTIONAL, NON_FUNCTIONAL)));


        // Service level - quality of service delivered
        serviceLevel = DimensionModel.newDimension("Service Level");
        serviceLevel.addAttribute(newOrdered("Service Level",
            asList(ABOVE_STANDARD, ACCEPTABLE, PROBLEMATIC, CRITICAL)));

        // Population
        population = DimensionModel.newDimension("Population");
        population.addAttribute(newNominal("Community", asList("Host", "Refugee")));
        population.addAttribute(newNominal("Camp", asList("Bambisi")));
        population.addAttribute(newNominal("Block", asList("A", "B", "C")));
        population.addAttribute(newNominal("Household"));
        cube.addDimensions(population);

        // Source
        source = DimensionModel.newDimension("Source");
        source.addAttribute(newNominal("Stage", asList(PER_DESIGN, REAL)));
        source.addAttribute(newNominal("Source", asList(REPORTED, OBSERVED)));

        cube.addDimensions(time, costComponent, system, serviceLevel, source);
    }

    public void defineMeasures() {
        // Measures
        costMeasure = MeasureModel.newFlowMeasure("Cost");
        userMeasure = MeasureModel.newStockMeasure("Number of water users");
        waterPointMeasure = MeasureModel.newStockMeasure("Number of water points");

        cube.addMeasures(costMeasure, userMeasure, waterPointMeasure);
    }

    public void loadData() throws IOException {
        store = new TestResourceStore();
        store.load("lcca.json");

        for(Resource resource : store.all()) {
            if(FormClass.CLASS_ID.asString().equals(resource.isString("classId"))) {
                forms.add(FormClass.fromResource(resource));
            }
        }

        costs = findForm("Documented Cost Information");
        wp = findForm("Water Collection Point information over time - Monthly Reports");
        camp = findForm("Camp Information per month  - Monthly Reports");
        survey = findForm("Bambasi Water Service Ladders Survey");
        design = findForm("System Design");
    }

    private FormClass findForm(String label) {
        for(FormClass form : forms) {
            if(form.getLabel().equals(label)) {
                return form;
            }
        }
        throw new IllegalArgumentException(label);
    }

    public void defineMappings() {

        SourceMapping costMapping = new SourceMapping(costs);

        MeasureMapping measure = costMapping.addMeasure(costMeasure);
        measure.setValueExpression("[Expenditure]*([Allocation water programme]/100)");
        measure.addLoadings(
            on("CapEx").withFactor("[Initial Cost - Not specified]/100"),
            on("CapEx").withFactor("[Initial Cost - Cap Hard]/100"),
            on("CapEx").withFactor("[Initial Cost - Cap Soft]/100"),
            on("CapEx").withFactor("[Extension Cost - Not specified]/100"),
            on("CapEx").withFactor("[Extension Cost - Hard]/100"),
            on("CapEx").withFactor("[Extension Cost - Soft]/100"),
            on("OpEx").withFactor("[Operational Cost]/100"),
            on("OpEx").withFactor("[Maintenance Cost]/100"),
            on("OpEx").withFactor("[Operational & Maintenance Cost]/100"));

        measure.addLoadings(
            on("CapEx").withFactor("[Initial Cost - Not specified]/100"),
            on("CapEx").withFactor("[Initial Cost - Cap Hard]/100"),
            on("CapEx").withFactor("[Initial Cost - Cap Soft]/100"),
            on("CapEx").withFactor("[Extension Cost - Not specified]/100"),
            on("CapEx").withFactor("[Extension Cost - Hard]/100"),
            on("CapEx").withFactor("[Extension Cost - Soft]/100"),
            on("OpEx").withFactor("[Operational Cost]/100"),
            on("OpEx").withFactor("[Maintenance Cost]/100"),
            on("OpEx").withFactor("[Operational & Maintenance Cost]/100"));

        costMapping.addAttributeMappings(new AttributeMapping(yearAttribute.getId(), "[Year of Expediture]"));
        costMapping.addLoadings(
            on(HAND_DUG_WELLS).where("[System Identifier] == [Hand Dug Wells (All)]"),
            on(INITIAL_PIPED_SCHEME).where("[System Identifier] == [Initial Piped Scheme]"),
            on(PIPED_SCHEME_EXTENSION_REFUGEES).where("[System Identifier] == [Piped Scheme Extension Refugees]"),
            on(PIPED_SCHEME_EXTENSION_HOST_COMMUNITY).where("[System Identifier] == [Piped Scheme Extension Host Community]"),
            on(EMERGENCY).where("[System Identifier] == [Emergency (Trucking/prov. PS)]"),
            on(EMERGENCY).where("([System Identifier] == [Whole Camp (Excluding Host Community)]) && ([Year of expediture] == '2012')"),
            on(EMERGENCY).where("([System Identifier] == [Whole Area (Including Host Community)]) && ([Year of expediture] == '2012')"),
            on(INITIAL_PIPED_SCHEME)
                .where("([System Identifier] == [Extended Piped Scheme EXCL. Host Comm.]) && ([Year of expediture] == '2013')")
                .withFactor(0.79428571428571),
            on(PIPED_SCHEME_EXTENSION_REFUGEES)
                .where("([System Identifier] == [Extended Piped Scheme EXCL. Host Comm.]) && ([Year of expediture] == '2013')")
                .withFactor(0.20571428571429),
            on(INITIAL_PIPED_SCHEME)
                .where("([System Identifier] == [Whole Camp (Excluding Host Community)]) && ([Year of expediture] == '2013')")
                .withFactor(0.91700301972921),
            on(PIPED_SCHEME_EXTENSION_REFUGEES)
                .where("([System Identifier] == [Whole Camp (Excluding Host Community)]) && ([Year of expediture] == '2013')")
                .withFactor(0.08286986165478),
            on(HAND_DUG_WELLS)
                .where("([System Identifier] == [Whole Camp (Excluding Host Community)]) && ([Year of expediture] == '2013')")
                .withFactor(0.00012711861601));

        costMapping.addLoadings(
            on("Per Design").where("[Cost typology] == [Budgeted]"),
            on("Real").where("[Cost typology] == [Spent]")
        );

        costMapping.addLoadings(
            on("Acceptable").where("[Cost typology] == [Budgeted]")
        );

        cube.addMappings(costMapping);

        SourceMapping popMapping = new SourceMapping(camp);
        popMapping.addAttributeMappings(new AttributeMapping(yearAttribute.getId(), "[Year]"));
        popMapping.addMeasure(userMeasure)
            .setValueExpression("[Camp Population]")
            .addLoadings(on("Refugee"));

        popMapping.addMeasure(userMeasure)
            .setValueExpression("[Host community population (estimate)]")
            .addLoadings(on("Host"));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[Emergency Taps]")
            .addLoadings(on(EMERGENCY), on(FUNCTIONAL), on(PER_DESIGN), on(REPORTED));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[HDW refugies]")
            .addLoadings(on(HAND_DUG_WELLS), on(FUNCTIONAL), on(PER_DESIGN), on(REPORTED), on("Refugee"));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[HDW host]")
            .addLoadings(on(HAND_DUG_WELLS), on(FUNCTIONAL), on(PER_DESIGN), on(REPORTED), on("Host"));


        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[Planned Taps]")
            .addLoadings(on(INITIAL_PIPED_SCHEME), on(PER_DESIGN), on(REPORTED), on("Refugee"));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[Actual Taps - intial scheme]")
            .addLoadings(on(INITIAL_PIPED_SCHEME), on(REAL), on("Refugee"), on(REPORTED));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[fnct taps]")
            .addLoadings(on(INITIAL_PIPED_SCHEME), on(REAL), on(FUNCTIONAL), on("Refugee"), on(REPORTED));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[Actual Taps - intial scheme] - [fnct taps]")
            .addLoadings(on(INITIAL_PIPED_SCHEME), on(REAL), on(NON_FUNCTIONAL), on("Refugee"), on(REPORTED));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[extension piped ref]")
            .addLoadings(on(PIPED_SCHEME_EXTENSION_REFUGEES), on(REPORTED), on("Refugee"));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[extension piped host]")
            .addLoadings(on(PIPED_SCHEME_EXTENSION_HOST_COMMUNITY), on(REPORTED), on("Host"));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[Wql tp above]*[fnct taps]/100")
            .addLoadings(on(INITIAL_PIPED_SCHEME), on(REPORTED), on("Refugee"), on(ABOVE_STANDARD));

        popMapping.addMeasure(waterPointMeasure)
            .setValueExpression("[Wql tp std]*[fnct taps]/100")
            .addLoadings(on(INITIAL_PIPED_SCHEME), on(REPORTED), on("Refugee"), on(ACCEPTABLE));


        cube.addMappings(popMapping);
    }

    @Before
    public void setUp() throws Exception {
        loadData();
        defineDimensions();
        defineMeasures();
        defineMappings();
    }

    @Test
    public void testSimple() throws Exception {
        CubeBuilder cubeBuilder = new CubeBuilder(store);
        List<Bucket> buckets = cubeBuilder.buildCube(cube,
            Arrays.asList(componentAttrib.getId()),
            Collections.singleton(costMeasure.getId()));
    }

    private AttributeLoading on(String memberName) {
        AttributeLoading loading = new AttributeLoading();
        loading.setAttributeId(findAttributeOfMember(memberName));
        loading.setMemberName(memberName);
        return loading;
    }

    private ResourceId findAttributeOfMember(String memberName) {
        for(DimensionModel dim : cube.getDimensions()) {
            for(AttributeModel attribute : dim.getAttributes()) {
                if(attribute.getMembershipType() == MembershipType.CLOSED) {
                    for (String member : Preconditions.checkNotNull(attribute.getMembers(), attribute.getLabel())) {
                        if (member.equalsIgnoreCase(memberName)) {
                            return attribute.getId();
                        }
                    }
                }
            }
        }
        throw new IllegalArgumentException("No such member: " + memberName);
    }
}
