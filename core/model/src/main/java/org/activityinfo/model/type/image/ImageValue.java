package org.activityinfo.model.type.image;
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

import org.activityinfo.model.resource.IsRecord;
import org.activityinfo.model.resource.Record;
import org.activityinfo.model.type.FieldTypeClass;
import org.activityinfo.model.type.FieldValue;

/**
 * @author yuriyz on 8/6/14.
 */
public class ImageValue implements FieldValue, IsRecord {

    private final String mimeType;
    private final String filename;
    private final String blobId;

    private int height;
    private int width;

    @Override
    public FieldTypeClass getTypeClass() {
        return ImageType.TYPE_CLASS;
    }

    public ImageValue(String mimeType, String filename, String blobId) {
        this.mimeType = mimeType;
        this.filename = filename;
        this.blobId = blobId;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getFilename() {
        return filename;
    }

    public String getBlobId() {
        return blobId;
    }

    public int getHeight() {
        return height;
    }

    public ImageValue setHeight(int height) {
        this.height = height;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public ImageValue setWidth(int width) {
        this.width = width;
        return this;
    }

    @Override
    public Record asRecord() {
        return new Record()
                .set(TYPE_CLASS_FIELD_NAME, getTypeClass().getId())
                .set("mimeType", mimeType)
                .set("width", width)
                .set("height", height)
                .set("filename", filename)
                .set("blobId", blobId);
    }

    public static FieldValue fromRecord(Record record) {
        return new ImageValue(record.getString("mimeType"), record.getString("filename"), record.getString("blobId"))
                .setHeight(record.getInt("height"))
                .setWidth(record.getInt("width"));
    }
}
