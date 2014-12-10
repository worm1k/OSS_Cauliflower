package com.naukma.cauliflower.reports;

import java.io.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


/**
 * Created by WormiK on 06.12.2014.
 */
public class CSVReportGenerator implements ReportGenerator {

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    public CSVReportGenerator(ResultSet resultSet) throws IOException, SQLException {
        StringBuilder stringBuilder = new StringBuilder("");
        ResultSetMetaData meta;
            if(resultSet == null) {
            } else {
                try {
                    meta = resultSet.getMetaData();

                    for (int i = 1; i <=meta.getColumnCount(); ++i) {
                        if (i == meta.getColumnCount()) {
                            stringBuilder.append(meta.getColumnName(i));
                        } else {
                            stringBuilder.append(meta.getColumnName(i) + ",");
                        }
                    }

                    stringBuilder.append("\r\n");

                    while (resultSet.next()) {
                        for(int i = 1; i<=meta.getColumnCount(); ++i) {
                            if (i == meta.getColumnCount()) {
                                stringBuilder.append(resultSet.getString(i));
                            } else {
                                stringBuilder.append(resultSet.getString(i) + ",");
                            }
                        }
                        stringBuilder.append("\r\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                System.out.println("SB: " + stringBuilder.toString());
                baos.write(stringBuilder.toString().getBytes());
            }

    }

    @Override
    public void writeInStream(OutputStream out) throws IOException {
        System.out.println("out: " + out.toString());
        baos.writeTo(out);
        System.out.println("out: " + out.toString());
        baos.close();
    }
}