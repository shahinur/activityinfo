package org.activityinfo.model.analysis.cube;

import com.google.common.collect.ImmutableList;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;

import java.util.List;

/**
 *
 * <em>Attributes</em> are used to capture relevant details about the dimension.
 * For example, color, category, sub-category, price, and size, are all
 * attributes which are used to capture the details of the Dimension Product.
 * Similarly, date, month, year, hour, minute, and seconds, are the attributes
 * used to capture the details of the Time dimension.
 *
 * <p>Attributes have one or more <em>members</em>. </p>
 */
public class AttributeModel {
    private ResourceId id;
    private String label;
    private boolean ordered;
    private ImmutableList<String> members;
    private MembershipType membershipType;


    public AttributeModel() {
    }

    /**
     * Creates a new, ordered attribute with a newly generated cuid and
     * a closed list of members.
     * @param label the attribute's label
     * @param members the members of the attribute
     * @return a new {@code AttributeModel}
     */
    public static AttributeModel newOrdered(String label, List<String> members) {
        AttributeModel attributeModel = new AttributeModel();
        attributeModel.id = Resources.generateId();
        attributeModel.label = label;
        attributeModel.ordered = true;
        attributeModel.members = ImmutableList.copyOf(members);
        attributeModel.membershipType = MembershipType.CLOSED;
        return attributeModel;
    }

    /**
     * Creates a new, nominal attribute with a newly generated cuid and
     * a closed set of members.
     * @param label the attribute's label
     * @param members the members of the attribute
     * @return a new {@code AttributeModel}
     */
    public static AttributeModel newNominal(String label, Iterable<String> members) {
        AttributeModel attributeModel = new AttributeModel();
        attributeModel.id = Resources.generateId();
        attributeModel.label = label;
        attributeModel.ordered = false;
        attributeModel.membershipType = MembershipType.CLOSED;
        attributeModel.members = ImmutableList.copyOf(members);
        return attributeModel;
    }

    /**
     * Creates a new, nominal attribute with a newly generated cuid and
     * an open set of members. The total member list will be derived from
     * the source data.
     *
     * @param label the attribute's label
     * @return a new {@code AttributeModel}
     */
    public static AttributeModel newNominal(String label) {
        AttributeModel attributeModel = new AttributeModel();
        attributeModel.id = Resources.generateId();
        attributeModel.label = label;
        attributeModel.ordered = false;
        attributeModel.membershipType = MembershipType.OPEN;
        return attributeModel;
    }

    public ResourceId getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public List<String> getMembers() {
        return members;
    }

    public MembershipType getMembershipType() {
        return membershipType;
    }
}
