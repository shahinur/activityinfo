package org.activityinfo.migrator;

import com.google.common.collect.Multimap;
import org.activityinfo.model.resource.Resource;
import org.activityinfo.model.resource.ResourceId;
import org.activityinfo.model.resource.Resources;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

/**
 * Writes resources out as an SQL dump file
 */
public class MySqlResourceDumpWriter implements ResourceWriter {

    private final BufferedWriter output;
    private int writtenCount = 0;

    public MySqlResourceDumpWriter(File file) throws IOException {
        System.out.println("Writing SQL dump to " + file);
        this.output = new BufferedWriter(new FileWriter(file));

    }


    @Override
    public void beginResources() throws Exception {
        this.append("TRUNCATE TABLE resource;\n");
        this.append("INSERT INTO resource" +
                          " (id, version, sub_tree_version, owner_id, class_id, label, content)" +
                          " VALUES\n");
    }


    @Override
    public void writeResource(Resource resource) throws IOException {

        if(writtenCount > 0) {
            append(',');
            append('\n');
        }
        append("(");
        appendParameter(resource.getId());
        append(",1,1,");
        appendParameter(resource.getOwnerId());
        append(',');
        appendParameter(resource.isString("classId"));
        append(',');
        appendParameter(ResourceWriters.getLabel(resource));
        append(',');
        appendParameter(Resources.toJson(resource));
        append(')');

        writtenCount++;

        if(writtenCount % 1000 == 0) {
            System.out.println(writtenCount + " Resources written");
        }
    }


    @Override
    public void endResources() throws Exception {
        append(";\n");
    }

    @Override
    public void writeUserIndex(Multimap<ResourceId, ResourceId> resources) throws Exception {
        this.append("INSERT INTO user_root_index" +
                          " (user_id, resource_id)" +
                          " VALUES\n");

        boolean needsComma = false;
        for (Map.Entry<ResourceId, ResourceId> entry : resources.entries()) {
            if(needsComma) {
                append(",\n");
            }
            append("(");
            appendParameter(entry.getKey());
            append(',');
            appendParameter(entry.getValue());
            append(")");
            needsComma=true;
        }
        append(";\n");
    }

    private void appendParameter(String string) throws IOException {
        if(string == null) {
            append("NULL");
        } else {
            append('\'');
            append(StringEscapeUtils.escapeSql(string));
            append('\'');
        }
    }

    private void appendParameter(ResourceId id) throws IOException {
        append('\'');
        append(id.asString());
        append('\'');
    }
    
    private void append(String string) throws IOException {
        this.output.append(string);
    }

    private void append(char c) throws IOException {
        output.write(c);
    }

    public void close() throws IOException {
        output.close();
    }

}
