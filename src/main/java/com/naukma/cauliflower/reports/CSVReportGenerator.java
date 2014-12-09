package com.naukma.cauliflower.reports;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by WormiK on 06.12.2014.
 */
public class CSVReportGenerator {

    public CSVReportGenerator() {
    }

    public String buildCSV(ResultSet resultSet) throws SQLException {

        StringBuilder stringBuilder = new StringBuilder("");
        ResultSetMetaData meta;
        if(resultSet == null) {
            return "";
        }

        /**
         * creating the header row
         */
        try {
            meta = resultSet.getMetaData();


            for(int i = 0; i < meta.getColumnCount(); i++) {
                if(i == meta.getColumnCount() - 1) {
                    stringBuilder.append(meta.getColumnName(i));
                }else {
                    stringBuilder.append(meta.getColumnName(i) + ",");
                }
            }

            while(resultSet.next()) {
                for(int i = 0; i < meta.getColumnCount(); ++i) {
                    if(i == meta.getColumnCount() - 1) {
                        stringBuilder.append(resultSet.getString(i));
                    }else {
                        stringBuilder.append(resultSet.getString(i) + ",");
                    }
                }
                stringBuilder.append("\r\n");
            }
        }
        catch(SQLException err) {
            /******/
        }

        return stringBuilder.toString();
    }

}
