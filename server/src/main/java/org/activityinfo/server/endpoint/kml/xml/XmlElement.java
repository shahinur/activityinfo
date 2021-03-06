package org.activityinfo.server.endpoint.kml.xml;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class XmlElement {

    private String name;
    private String namespace = null;

    private StringBuilder innerText = new StringBuilder(0);
    private boolean closed;
    private Map<String, XmlAttribute> attributes = new HashMap<String, XmlAttribute>();

    private Map<String, String> namespacePrefixes = new HashMap<String, String>();

    public XmlElement(String name) {
        this.name = name;
    }

    public XmlElement(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public XmlElement nsPrefix(String prefix, String ns) {
        namespacePrefixes.put(ns, prefix);
        return this;
    }

    public String getName() {
        return this.name;
    }

    protected XmlAttribute getAttribute(String namespace, String name) {
        String key = namespace + ":" + name;
        XmlAttribute attrib = attributes.get(key);

        if (attrib == null) {
            attrib = new XmlAttribute(namespace, name);

            attributes.put(key, attrib);
        }
        return attrib;
    }

    protected XmlAttribute getAttribute(String name) {
        XmlAttribute attrib = attributes.get(name);

        if (attrib == null) {
            attrib = new XmlAttribute(name);
            attributes.put(name, attrib);
        }
        return attrib;
    }

    public XmlElement at(String name, String value) {
        getAttribute(name).setValue(value);
        return this;
    }

    public XmlElement at(String namespace, String name, String value) {
        getAttribute(namespace, name).setValue(value);
        return this;
    }

    public XmlElement at(String name, boolean value) {
        getAttribute(name).setValue(value ? "true" : "false");
        return this;
    }

    public XmlElement styleName(String className) {
        if (className != null) {
            getAttribute("class").append(className, ' ');
        }
        return this;
    }

    public XmlElement styleName(String className, int suffix) {
        return styleName(className + suffix);
    }

    public XmlElement styleNameIf(String className, boolean condition) {
        if (condition) {
            styleName(className);
        }
        return this;
    }

    public Collection<XmlAttribute> getAttributes() {
        return attributes.values();
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public XmlElement text(String text) {
        innerText.append(text);
        return this;
    }

    public XmlElement text(Collection<?> elements, String delimeter) {
        boolean first = true;
        for (Object element : elements) {
            if (!first) {
                innerText.append(delimeter);
            }
            innerText.append(element.toString());

            first = false;
        }
        return this;
    }

    public XmlElement nbsp() {
        innerText.append("&nbsp;");
        return this;
    }

    public XmlElement close() {
        closed = true;
        return this;
    }

    public String getInnerText() {
        if (innerText.length() == 0) {
            return null;
        } else {
            return innerText.toString();
        }
    }

    public boolean isClosed() {
        return closed;
    }

    public String getNamespace() {
        return namespace == null ? "" : namespace;
    }

    public Map<String, String> getNamespacePrefixes() {
        return namespacePrefixes;
    }
}
