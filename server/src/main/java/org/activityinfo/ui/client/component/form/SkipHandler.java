package org.activityinfo.ui.client.component.form;
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

import com.google.common.collect.Lists;
import org.activityinfo.core.shared.expr.ExprLexer;
import org.activityinfo.core.shared.expr.ExprNode;
import org.activityinfo.core.shared.expr.ExprParser;
import org.activityinfo.core.shared.expr.resolver.SimpleBooleanPlaceholderExprResolver;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.model.form.FormField;

import java.util.List;

/**
 * @author yuriyz on 7/30/14.
 */
public class SkipHandler {

    private final SimpleFormPanel simpleFormPanel;
    private List<FormField> fieldsWithSkipExpression = Lists.newArrayList();

    public SkipHandler(SimpleFormPanel simpleFormPanel) {
        this.simpleFormPanel = simpleFormPanel;
    }

    public void onValueChange() {
        for (FormField formField : fieldsWithSkipExpression) {
            applySkipLogic(formField);
        }
    }

    private void applySkipLogic(FormField field) {
        if (field.hasSkipExpression()) {
            ExprLexer lexer = new ExprLexer(field.getSkipExpression());
            ExprParser parser = new ExprParser(lexer, new SimpleBooleanPlaceholderExprResolver(FormInstance.fromResource(simpleFormPanel.getInstance()), simpleFormPanel.getFormClass()));
            ExprNode<Boolean> expr = parser.parse();

            FieldContainer fieldContainer = simpleFormPanel.getFieldContainer(field.getId());
            fieldContainer.getFieldWidget().setReadOnly(expr.evalReal());
        }
    }

    public void formClassChanged() {
        fieldsWithSkipExpression = Lists.newArrayList();
        for (FormField formField : simpleFormPanel.getFormClass().getFields()) {
            if (formField.hasSkipExpression()) {
                fieldsWithSkipExpression.add(formField);
            }
        }
    }
}
