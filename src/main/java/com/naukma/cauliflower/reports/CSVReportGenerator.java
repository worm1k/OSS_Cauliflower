package com.naukma.cauliflower.reports;


import com.opencsv.CSVWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Created by WormiK on 06.12.2014.
 */
public class CSVReportGenerator implements ReportGenerator {

    ByteArrayOutputStream baos;
    CSVWriter writer;

    public CSVReportGenerator(ResultSet resultSet) throws IOException, SQLException {
        this.baos = new ByteArrayOutputStream();
        this.writer = new CSVWriter(new OutputStreamWriter(baos));
        writer.writeAll(resultSet, true);
    }

    @Override
    public void writeInStream(OutputStream out) throws IOException {
        baos.writeTo(out);
        baos.close();
    }
}