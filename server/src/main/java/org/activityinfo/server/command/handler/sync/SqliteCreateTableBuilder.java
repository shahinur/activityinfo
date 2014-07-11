package org.activityinfo.server.command.handler.sync;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

public class SqliteCreateTableBuilder {


    private String tableName;
    private SqliteBatchBuilder batch;

    public SqliteCreateTableBuilder(SqliteBatchBuilder batch, String tableName) {
        this.batch = batch;
        this.tableName = tableName;
    }


    public void execute(EntityManager entityManager) {
        ((HibernateEntityManager) entityManager).getSession().doWork(new Work() {

            @Override
            public void execute(Connection connection) throws SQLException {

                DatabaseMetaData dbmeta = connection.getMetaData();


                ResultSet columns = dbmeta.getColumns(null, null, tableName, null);
                StringBuilder columnList = new StringBuilder();
                while(columns.next()) {
                    if(columnList.length() > 0) {
                        columnList.append(",");
                    }
                    String columnName = columns.getString("COLUMN_NAME");
                    String typeName = columns.getString("TYPE_NAME");

                    columnList.append(columnName);
                    columnList.append(" ");

                    if(typeName.toLowerCase().contains("int")) {
                        columnList.append("int");
                    } else if(typeName.equalsIgnoreCase("double")) {
                        columnList.append("double");
                    } else {
                        columnList.append("text");
                    }
                }
                columns.close();

                ResultSet keysResultSet = dbmeta.getPrimaryKeys(null, null, tableName);
                Set<String> keys = Sets.newHashSet();
                while(keysResultSet.next()) {
                    keys.add(keysResultSet.getString("COLUMN_NAME"));
                }
                keysResultSet.close();

                if(keys.size() > 0) {
                    columnList
                            .append(", PRIMARY KEY (")
                            .append(Joiner.on(",").join(keys))
                            .append(")");
                }

                try {
                    batch.addStatement("CREATE TABLE IF NOT EXISTS " + tableName + " (" + columnList.toString() + ")");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

}
