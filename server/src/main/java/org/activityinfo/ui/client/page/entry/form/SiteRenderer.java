package org.activityinfo.ui.client.page.entry.form;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Strings;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.legacy.shared.model.*;
import org.activityinfo.legacy.shared.type.IndicatorValueFormatter;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.NarrativeType;
import org.activityinfo.model.type.barcode.BarcodeType;
import org.activityinfo.model.type.image.ImageType;
import org.activityinfo.model.type.number.QuantityType;
import org.activityinfo.model.type.primitive.TextType;

import java.util.List;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.htmlEscape;

public class SiteRenderer {

    private final IndicatorValueFormatter indicatorValueFormatter;
    private boolean hideEmptyValues;

    public SiteRenderer(IndicatorValueFormatter indicatorValueFormatter) {
        super();
        this.indicatorValueFormatter = indicatorValueFormatter;
    }

    public String renderLocation(SiteDTO site, ActivityDTO activity) {
        StringBuilder html = new StringBuilder();

        html.append("<table cellspacing='0'>");
        if (!activity.getLocationType().isAdminLevel()) {
            html.append("<tr><td>");
            html.append(activity.getLocationType().getName()).append(": ");
            html.append("</td><td>");
            html.append(site.getLocationName());
            if (!Strings.isNullOrEmpty(site.getLocationAxe())) {
                html.append("<br>").append(site.getLocationAxe());
            }
            html.append("</td></tr>");
        }
        for (AdminLevelDTO level : activity.getAdminLevels()) {
            String entity = site.getAdminEntity(level.getId());
            if (entity != null) {
                html.append("<tr><td>");
                html.append(level.getName()).append(":</td><td>");
                html.append(entity);
                html.append("</td></tr>");
            }
        }

        html.append("</table>");
        return html.toString();
    }

    public String renderSite(SiteDTO site, ActivityDTO activity, boolean renderComments) {
        StringBuilder html = new StringBuilder();

        if (renderComments && site.getComments() != null) {
            String commentsHtml = site.getComments();
            commentsHtml = commentsHtml.replace("\n", "<br/>");
            html.append("<p class='comments'><span class='groupName'>");
            html.append(I18N.CONSTANTS.comments());
            html.append(":</span> ");
            html.append(commentsHtml);
            html.append("</p>");
        }

        html.append(renderIndicators(site, activity));
        return html.toString();
    }

    private String renderIndicators(SiteDTO site, ActivityDTO activity) {
        StringBuilder html = new StringBuilder();
        html.append("<table class='indicatorTable' cellspacing='0'>");
        boolean hasContent = renderIndicatorGroup(html, activity.getFields(), site);
        html.append("</table>");

        return hasContent ? html.toString() : "";
    }

    private boolean renderIndicatorGroup(StringBuilder html,
                                         List<IsFormField> fields,
                                         SiteDTO site) {
        StringBuilder groupHtml = new StringBuilder();
        boolean empty = true;

        for (IsFormField field : fields) {
            SafeHtml value = renderFieldValue(site, field);
            if (value == null && !hideEmptyValues) {
                value = SafeHtmlUtils.fromSafeConstant("-");
            }
            if (value != null) {
                renderFieldRow(groupHtml, field, value);
                empty = false;
            }

        }
        if(!empty) {
            html.append(groupHtml);
            return true;
        } else {
            return false;
        }
    }


    private SafeHtml renderFieldValue(SiteDTO site, IsFormField field) {
        if(field instanceof IndicatorDTO) {
            return renderIndicatorValue(site, (IndicatorDTO) field);
        } else {
            return renderAttributeList(site, (AttributeGroupDTO) field);
        }
    }

    private void renderFieldRow(StringBuilder groupHtml, IsFormField field, SafeHtml value) {
        groupHtml.append("<tr><td class='indicatorHeading");
        groupHtml.append("'>")
                .append(htmlEscape(field.getLabel()))
                .append("</td>");

        if (field.getTypeClass() == QuantityType.TYPE_CLASS) {
            IndicatorDTO indicator = (IndicatorDTO) field;
            groupHtml
                    .append("<td class='indicatorValue'>")
                    .append(value.asString())
                    .append("</td><td class='indicatorUnits'>")
                    .append(renderUnits(indicator))
                    .append("</td>");
        } else {
            groupHtml
                    .append("<td colspan=2>")
                    .append(value.asString())
                    .append("</td>");
        }
        groupHtml.append("</tr>");
    }

    private String renderUnits(IndicatorDTO indicator) {
        if(Strings.isNullOrEmpty(indicator.getUnits())) {
            return null;
        } else {
            return SafeHtmlUtils.htmlEscape(indicator.getUnits());
        }
    }

    private SafeHtml renderIndicatorValue(SiteDTO site, IndicatorDTO indicator) {
        Object value = site.getIndicatorValue(indicator);
        if (value == null) {
            return null;
        }
        if (indicator.getType() == FieldTypeClass.QUANTITY) {
            if (indicator.getAggregation() == IndicatorDTO.AGGREGATE_SITE_COUNT) {
                return SafeHtmlUtils.fromSafeConstant("1");
            } else {
                Double doubleValue = site.getIndicatorDoubleValue(indicator);

                if (doubleValue == null || (indicator.getAggregation() == IndicatorDTO.AGGREGATE_SUM && doubleValue == 0.0)) {
                    return null;
                } else {
                    return SafeHtmlUtils.fromTrustedString(indicatorValueFormatter.format((Double) value));
                }
            }
        } else if (indicator.getType() == TextType.TYPE_CLASS ||
                   indicator.getType() == BarcodeType.TYPE_CLASS) {

            if (value instanceof String) {
                return SafeHtmlUtils.fromSafeConstant((String) value);
            }

        } else if (indicator.getType() == NarrativeType.TYPE_CLASS) {
            SafeHtmlBuilder html = new SafeHtmlBuilder();
            html.appendEscapedLines((String) value);
            return html.toSafeHtml();

        } else if (indicator.getType() == ImageType.TYPE_CLASS) {
            if(value instanceof String) {
                return SafeHtmlUtils.fromTrustedString(
                    "<a href=\"" + fullSizeImage((String)value) + "\" target=\"_blank\">" +
                        "<img src=\"" + thumbnailUrl((String) value) + "\"></a>");
            }
        }

        Log.warn("Didn't know how to render type " + indicator.getType() + " and value of class " + value.getClass().getName() );
        return null;
    }

    private String fullSizeImage(String blobId) {
        return "/service/blob/" + blobId;
    }

    private String thumbnailUrl(String blobId) {
        return "/service/blob/" + blobId + "/thumbnail?width=120&height=120";
    }


    protected SafeHtml renderAttributeList(SiteDTO site, AttributeGroupDTO group) {
        StringBuilder html = new StringBuilder();
        boolean needsComma = false;
        for (AttributeDTO attribute : group.getAttributes()) {
            boolean value = site.getAttributeValue(attribute.getId());
            if (value) {
                if(needsComma) {
                    html.append(", ");
                }
                html.append(SafeHtmlUtils.htmlEscape(attribute.getName()));
                needsComma = true;
            }
        }
        if(html.length() > 0) {
            return SafeHtmlUtils.fromTrustedString(html.toString());
        } else {
            return null;
        }
    }

    public void setHideEmptyValues(boolean hideEmptyValues) {
        this.hideEmptyValues = hideEmptyValues;
    }
}
