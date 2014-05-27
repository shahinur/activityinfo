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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.legacy.client.type.IndicatorNumberFormat;
import org.activityinfo.legacy.shared.model.ActivityDTO;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.model.IndicatorGroup;
import org.activityinfo.legacy.shared.model.SiteDTO;

import java.util.List;

public class IndicatorSection extends LayoutContainer implements FormSection<SiteDTO> {

    private List<Field> indicatorFields = Lists.newArrayList();

    public IndicatorSection(ActivityDTO activity) {

        TableLayout layout = new TableLayout(3);
        layout.setCellPadding(5);
        layout.setCellVerticalAlign(Style.VerticalAlignment.TOP);

        setLayout(layout);
        setStyleAttribute("fontSize", "8pt");
        setScrollMode(Scroll.AUTOY);

        for (IndicatorGroup group : activity.groupIndicators()) {

            if (group.getName() != null) {
                addGroupHeader(group.getName());
            }

            for (IndicatorDTO indicator : group.getIndicators()) {
                if (indicator.getAggregation() != IndicatorDTO.AGGREGATE_SITE_COUNT) {
                    addIndicator(indicator);
                }
            }
        }
    }

    private void addGroupHeader(String name) {

        TableData layoutData = new TableData();
        layoutData.setColspan(3);

        Text header = new Text(name);
        header.setStyleAttribute("fontSize", "9pt");
        header.setStyleAttribute("fontWeight", "bold");
        header.setStyleAttribute("marginTop", "6pt");

        add(header, layoutData);
    }

    private void addIndicator(IndicatorDTO indicator) {

        String name = indicator.getName();
        if (indicator.isMandatory()) {
            name += " *";
        }
        Text indicatorLabel = new Text(Format.htmlEncode(name));
        indicatorLabel.setStyleAttribute("fontSize", "9pt");
        add(indicatorLabel);

        FormFieldType type = indicator.getType();

        Field indicatorField = new NumberField();

        if (type == FormFieldType.QUANTITY) {
            NumberField numberField = new NumberField();
            indicatorField = numberField;

            numberField.setFormat(IndicatorNumberFormat.INSTANCE);
            numberField.setWidth(50);
            numberField.setStyleAttribute("textAlign", "right");
            if (indicator.isMandatory()) {
                numberField.setAllowBlank(false);
            }

            add(numberField);

            Text unitLabel = new Text(indicator.getUnits());
            unitLabel.setStyleAttribute("fontSize", "9pt");

            add(unitLabel);
        } else if (type == FormFieldType.FREE_TEXT) {
            TextField textField = new TextField();

            indicatorField = textField;

            textField.setAutoWidth(true);
            if (indicator.isMandatory()) {
                textField.setAllowBlank(false);
            }
            add(textField);
            add(new Text()); // avoid layout shift
        } else if (type == FormFieldType.NARRATIVE) {
            TextArea textArea = new TextArea();
            indicatorField = textArea;

            textArea.setAutoWidth(true);
            if (indicator.isMandatory()) {
                textArea.setAllowBlank(false);
            }
            add(textArea);
            add(new Text()); // avoid layout shift
        }

        indicatorField.setName(indicator.getPropertyName());

        if (indicator.getDescription() != null && !indicator.getDescription().isEmpty()) {
            ToolTipConfig tip = new ToolTipConfig();
            tip.setDismissDelay(0);
            tip.setShowDelay(100);
            tip.setText(indicator.getDescription());

            indicatorField.setToolTip(tip);
        }

        indicatorFields.add(indicatorField);
    }

    @Override
    public boolean validate() {
        boolean valid = true;
        for (Field field : indicatorFields) {
            valid &= field.validate();
        }
        return valid;
    }

    @Override
    public void updateModel(SiteDTO m) {
        for (Field field : indicatorFields) {
            m.set(field.getName(), field.getValue());
        }
    }

    @Override
    public void updateForm(SiteDTO m) {
        for (Field field : indicatorFields) {
            field.setValue(m.get(field.getName()));
        }
    }

    @Override
    public Component asComponent() {
        return this;
    }
}
