package org.activityinfo.io.msaccess;

import com.healthmarketscience.jackcess.*;

import java.io.File;
import java.io.IOException;

public class Importer {

    public static void main(String[] args) throws IOException {
        Database db = DatabaseBuilder.open(new File("/home/alex/dev/emis/source/2012/SSD2012_Pri_Tables.mdb"));
        for(String tableName : db.getTableNames()) {
            Table table = db.getTable(tableName);
            System.out.println(tableName);

            dumpPropertyMap(table.getProperties(), "  ");
            System.out.println("  Columns:");
            for(Column column : table.getColumns()) {
                System.out.println("    " + column.getName());
                dumpPropertyMap(table.getProperties(), "      ");

            }
        }

    }

    private static void dumpPropertyMap(PropertyMap properties, String indent) throws IOException {
        System.out.println(indent + "Properties:");
        for (PropertyMap.Property property : properties) {
            String stringValue = "" + property.getValue();
            if(stringValue.length() > 25) {
                stringValue = stringValue.substring(0, 25) + "...";
            }
            System.out.println(indent + "  " + property.getName() + "=" +  stringValue);
        }
    }
}
