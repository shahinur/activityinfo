package org.activityinfo.ui.style;
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

import org.activityinfo.ui.vdom.shared.dom.DomElement;
import org.activityinfo.ui.vdom.shared.tree.VComponent;
import org.activityinfo.ui.vdom.shared.tree.VTree;

/**
 * @author yuriyz on 9/22/14.
 */
public class InputControl extends VComponent<InputControl> {

    private InputControlType type;
    private String name;
    private String placeholder;

    public InputControl(InputControlType type, String name, String placeholder) {
        this.type = type;
        this.name = name;
        this.placeholder = placeholder;
    }

    @Override
    protected VTree render() {
        return Forms.input(type, name, placeholder);
    }

    public String getValueAsString() {
        return ((DomElement) getDomNode()).getInputValue();
    }

    public void setValue(String value) {
        ((DomElement) getDomNode()).setInputValue(value);
    }

    public InputControlType getType() {
        return type;
    }

    public void setType(InputControlType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }
}
