package com.naukma.cauliflower.reports;


import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.ss.util.RegionUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;


/**
 * @author Nikitin Alex
 * @author Hladchuk Maxim
 * @version 1.0 .
 */
public class XLSReportGenerator implements ReportGenerator{

    /**
     * Value = {@value}, row position to start filling data with .
     */
    private static final String PROVIDER_NAME = "Cauliflower";

    @Override
    public void writeInStream(OutputStream os) throws IOException {
        this.createXlsFile().write(os);
    }

    private enum XMLType {
        Number,
        Text,
        Bool,
        Date,
        ToString
    }

    /**
     * Value = {@value}, column position to start filling data with .
     */

    /**
     * Value = {@value}, row position to start filling data with .
     */

    private String reportName;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;

    /**
     * Constructor.
     *
     * @param reportName name of the report
     * @param resultSet  ResultSet of data to fill the report
     * @see java.sql.ResultSet
     */
    public XLSReportGenerator(String reportName, ResultSet resultSet) throws SQLException {
        this.resultSet = resultSet;
        metaData = resultSet.getMetaData();
        this.reportName = reportName;
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet("Report");
        generateDocument();
    }

    /**
     * Generates look up of a document and fills it with data.
     */


    /**
     * Set borders on left and right side for selected region
     *
     * @param region region to create borders
     * @param border type of border
     * @param color  color of the border
     */

    /**
     * Fills backfround with one color
     *
     * @param region region of cells to be filled with color
     * @param color  color to fill with
     */

    /**
     * Set style for data cells
     *
     * @param dataStyle           style of cell
     * @param columnNameCellStyle style of column header cell
     * @param rowsInserted        How many rows inserted fillData method
     * @throws java.sql.SQLException
     */

    /**
     * Counts  coefficient to make the column width fit the text width
     *
     * @param index Index of metaData column label
     * @return counted coefficient
     * @throws java.sql.SQLException
     */
    private double countWidthCoef(int index) throws SQLException {
        double res = INITIAL_CELL_WIDTH / INITIAL_LETTER_WIDTH;
        double coef = (double) (INITIAL_CELL_WIDTH / INITIAL_LETTER_WIDTH) / metaData.getColumnLabel(index).length();
        if (coef > 4)
            res = (res / 3.9);
        else if (coef > 2)
            res = (res / 1.9);
        else if (coef < 1)
            res = metaData.getColumnLabel(index).length();
        return res;

    }

    /**
     * Fills column names and cells with data
     *
     * @return How many rows were inserted
     * @throws java.sql.SQLException
     */
    private int fillData() throws SQLException {
        int rowsInserted = 1;//1 for column names row
        //Array to save column width, to fit all inserted data.
        double[] widths = new double[metaData.getColumnCount()];
        int currentRowNumber = DATA_Y_OFFSET + 1;//+1 for column names row
        Row titleRow = CellUtil.getRow(DATA_Y_OFFSET, sheet);

        for (int i = 0; i < metaData.getColumnCount(); i++) {
            String s = metaData.getColumnLabel(i + 1);
            CellUtil.getCell(titleRow, i + DATA_X_OFFSET).setCellValue(s.charAt(0) + s.substring(1).toLowerCase());
            widths[i] = countWidthCoef(i + 1);
        }
        sheet.setAutoFilter(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), DATA_X_OFFSET, metaData.getColumnCount()));

        while (resultSet.next()) {
            Row dataRow = CellUtil.getRow(currentRowNumber, sheet);
            for (int j = 0; j < metaData.getColumnCount(); j++) {
                Cell cell = CellUtil.getCell(dataRow, DATA_X_OFFSET + j);
                XMLType type = getTypeID(metaData.getColumnType(j + 1));
                switch (type) {
                    case Text:
                        String s = resultSet.getString(j + 1);
                        if (s != null)
                            widths[j] = Math.max(widths[j], s.length());
                        cell.setCellValue(s);
                        break;
                    case Number:
                        Double d = resultSet.getDouble(j + 1);
                        if (d != null)
                            widths[j] = Math.max(widths[j], d.toString().length());
                        cell.setCellValue(d);
                        break;
                    case Date:
                        Date date = resultSet.getDate(j + 1);
                        if (date != null)
                            widths[j] = Math.max(widths[j], date.toString().length());
                        cell.setCellValue(resultSet.getDate(j + 1));
                        break;
                    case Bool:
                        Boolean b = resultSet.getBoolean(j + 1);
                        if (b != null)
                            widths[j] = Math.max(widths[j], b.toString().length());
                        cell.setCellValue(b);
                        break;
                    case ToString:
                        Object o = resultSet.getObject(j + 1).toString();
                        if (o != null) {
                            widths[j] = Math.max(widths[j], o.toString().length());
                            cell.setCellValue(o.toString());
                        }
                        break;
                }
            }

            currentRowNumber++;
            rowsInserted++;
        }
        for (int i = 1; i <= widths.length; i++) {
            sheet.setColumnWidth(i, (int) (widths[i - 1] * INITIAL_LETTER_WIDTH + FILTER_WIDTH_OFFSET));
        }
        return rowsInserted;
    }


    /**
     * Finds xls-supported type for sql types
     * to convert data from ResultSet
     *
     * @param typeCode sql type id
     * @return xls-supported type
     */
    private XMLType getTypeID(int typeCode) {
        switch (typeCode) {
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.REAL:
            case Types.NUMERIC:
                return XMLType.Number;
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.LONGNVARCHAR:
                return XMLType.Text;
            case Types.BOOLEAN:
                return XMLType.Bool;
            case Types.DATE:
                return XMLType.Date;
            default:
                return XMLType.ToString;
        }

    }


    /**
     * Generates xls report
     *
     */
    public HSSFWorkbook  createXlsFile() {
        return workbook;
    }

}