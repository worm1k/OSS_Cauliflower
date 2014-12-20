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


    private static final int MERGE_COMPANY_ROW_HEIGHT = 1250;

    private static final int MERGE_REPORT_ROW_HEIGHT = 800;

    private static final int TABLE_LEFT_OFFSET = 1;

    private static final int TABLE_RIGHT_OFFSET = 1;

    private static final double COMPANY_NAME_FONT_SIZE = 24;

    private static final double REPORT_NAME_FONT_SIZE = 20;

    private static final int INITIAL_LETTER_WIDTH = 250;

    private static final int FILTER_WIDTH_OFFSET = 1250;

    private static final int MIN_SIDE_SEPARATOR_WIDTH = 1500;

    private static final double FONT_SCALE = 1.6;

    private static final int UNITS_PER_CHARACTER = 256;

    /** Width of region between provider name and report name regions*/
    private static final int NAMES_SEPARATOR_WIDTH = 150;

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
    private static final int DATA_X_OFFSET = TABLE_LEFT_OFFSET;

    /**
     * Value = {@value}, row position to start filling data with .
     */
    private static final int DATA_Y_OFFSET = 3;

    private String reportName;
    private ResultSet resultSet;
    private ResultSetMetaData metaData;
    private HSSFWorkbook workbook;
    private HSSFSheet sheet;
    private int columnsAmount;

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
        columnsAmount = metaData.getColumnCount() + TABLE_LEFT_OFFSET + TABLE_RIGHT_OFFSET;
        generateDocument();
    }

    /**
     * Generates look up of a document and fills it with data.
     */
    private void generateDocument() throws SQLException {
        //Fills data and returns how many rows were inserted
        int rowsInserted = fillData();

        createMergedRegions(rowsInserted);

        //Font for company name
        Font companyNameCellFont = workbook.createFont();
        companyNameCellFont.setColor(HSSFColor.WHITE.index);
        companyNameCellFont.setFontHeightInPoints((short) COMPANY_NAME_FONT_SIZE);
        companyNameCellFont.setFontName("Segoe UI Semilight");

        //Fill the merged region with company name
        Row row = CellUtil.getRow(0, sheet);
        row.setHeight((short) MERGE_COMPANY_ROW_HEIGHT);
        Cell cell = CellUtil.getCell(row, 0);
        CellUtil.setCellStyleProperty(cell, workbook, "fillForegroundColor", HSSFColor.AQUA.index);
        CellUtil.setCellStyleProperty(cell, workbook, "fillPattern", CellStyle.SOLID_FOREGROUND);
        CellUtil.setCellStyleProperty(cell, workbook, "alignment", HSSFCellStyle.ALIGN_CENTER);
        CellUtil.setCellStyleProperty(cell, workbook, "verticalAlignment", HSSFCellStyle.VERTICAL_CENTER);
        cell.getCellStyle().setFont(companyNameCellFont);
        cell.setCellValue(PROVIDER_NAME);

        {//Fill the one line between company name and report name merge regions.
            HSSFCellStyle tempCellStyle = workbook.createCellStyle();
            tempCellStyle.setBorderTop(CellStyle.BORDER_THICK);
            tempCellStyle.setTopBorderColor(HSSFColor.WHITE.index);
            tempCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
            tempCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
            Row tempRow = CellUtil.getRow(1, sheet);
            tempRow.setHeight((short) NAMES_SEPARATOR_WIDTH);
            for (int j = 0; j < columnsAmount; j++)
                CellUtil.getCell(tempRow, j).setCellStyle(tempCellStyle);
        }

        //Font for report name
        Font reportNameCellFont = workbook.createFont();
        reportNameCellFont.setColor(HSSFColor.AQUA.index);
        reportNameCellFont.setFontHeightInPoints((short) REPORT_NAME_FONT_SIZE);
        reportNameCellFont.setFontName("Segoe UI Semilight");

        //Fill the merged region with report name
        row = CellUtil.getRow(2, sheet);
        row.setHeight((short) MERGE_REPORT_ROW_HEIGHT);
        cell = CellUtil.getCell(row, 0);
        CellUtil.setCellStyleProperty(cell, workbook, "fillForegroundColor", HSSFColor.WHITE.index);
        CellUtil.setCellStyleProperty(cell, workbook, "fillPattern", CellStyle.SOLID_FOREGROUND);
        CellUtil.setCellStyleProperty(cell, workbook, "alignment", HSSFCellStyle.ALIGN_CENTER);
        CellUtil.setCellStyleProperty(cell, workbook, "verticalAlignment", HSSFCellStyle.VERTICAL_BOTTOM);
        cell.getCellStyle().setFont(reportNameCellFont);
        cell.setCellValue(reportName);


        //Set data style
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setAlignment(CellStyle.ALIGN_LEFT);
        dataStyle.setFillForegroundColor(HSSFColor.WHITE.index);
        dataStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        dataStyle.setBorderTop(CellStyle.BORDER_THIN);
        dataStyle.setBorderBottom(CellStyle.BORDER_THIN);
        dataStyle.setBorderLeft(CellStyle.BORDER_THIN);
        dataStyle.setBorderRight(CellStyle.BORDER_THIN);
        dataStyle.setTopBorderColor(HSSFColor.GREY_25_PERCENT.index);
        dataStyle.setBottomBorderColor(HSSFColor.GREY_25_PERCENT.index);
        dataStyle.setLeftBorderColor(HSSFColor.GREY_25_PERCENT.index);
        dataStyle.setRightBorderColor(HSSFColor.GREY_25_PERCENT.index);
        //Set column names cell style
        CellStyle columnNameCellStyle = workbook.createCellStyle();
        columnNameCellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        columnNameCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        //Fill table with these styles
        setDataStyle(dataStyle, columnNameCellStyle, rowsInserted);

        //Calculates the final width of columns
        double minimumTableWidth = ((Math.max(COMPANY_NAME_FONT_SIZE, REPORT_NAME_FONT_SIZE) / FONT_SCALE) / (columnsAmount))
                                    * PROVIDER_NAME.length() * UNITS_PER_CHARACTER;

        double currentTableWidth = 0;
        for (int i = 0; i < columnsAmount; i++)
            currentTableWidth += sheet.getColumnWidth(i);
        if (currentTableWidth < minimumTableWidth) {
            double difference = minimumTableWidth - currentTableWidth;
            sheet.setColumnWidth(0, (int) (sheet.getColumnWidth(0) + difference / 2));
            sheet.setColumnWidth(columnsAmount - 1, (int) (sheet.getColumnWidth(columnsAmount - 1) + difference / 2));
        } else {
            double difference = currentTableWidth - minimumTableWidth;
            sheet.setColumnWidth(0, Math.max(MIN_SIDE_SEPARATOR_WIDTH, (int) (sheet.getColumnWidth(0) - difference / 2)));
            sheet.setColumnWidth(columnsAmount - 1,
                                 Math.max(MIN_SIDE_SEPARATOR_WIDTH,
                                 (int) (sheet.getColumnWidth(columnsAmount - 1) - difference / 2)));
        }

    }

    /**
     * Creates provider name and report name regions, separator between them, left, right and bottom separators. Also
     * modifies their styles.
     * @param rowsInserted amount of inserted rows with data
     */
    private void createMergedRegions(int rowsInserted) {
        sheet.addMergedRegion(new CellRangeAddress(
                0,                                                      //first row (0-based)
                0,                                                      //last row  (0-based)
                0,                                                      //first column (0-based)
                columnsAmount - 1                                       //last column  (0-based)
        ));
        sheet.addMergedRegion(new CellRangeAddress(
                1,                            //first row (0-based)
                1,                            //last row  (0-based)
                0,                                                      //first column (0-based)
                columnsAmount - 1                                    //last column  (0-based)
        ));

        CellRangeAddress reportNameRegion = new CellRangeAddress(
                2,                          //first row (0-based)
                2, //last row  (0-based)
                0,                                                        //first column (0-based)
                columnsAmount - 1                                      //last column  (0-based)
        );

        CellRangeAddress leftSeparator = new CellRangeAddress(
                DATA_Y_OFFSET,
                DATA_Y_OFFSET + rowsInserted,
                0,
                0
        );

        CellRangeAddress rightSeparator = new CellRangeAddress(
                DATA_Y_OFFSET,
                DATA_Y_OFFSET + rowsInserted,
                columnsAmount - 1,
                columnsAmount - 1
        );

        CellRangeAddress bottomSeparator = new CellRangeAddress(
                DATA_Y_OFFSET + rowsInserted,
                DATA_Y_OFFSET + rowsInserted,
                TABLE_LEFT_OFFSET,
                columnsAmount - TABLE_RIGHT_OFFSET - 1
        );

        setLeftRightBorders(reportNameRegion, CellStyle.BORDER_THIN, HSSFColor.WHITE.index);
        setPureBackGround(rightSeparator, HSSFColor.WHITE.index);
        setPureBackGround(leftSeparator, HSSFColor.WHITE.index);
        setPureBackGround(bottomSeparator, HSSFColor.WHITE.index);

        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, bottomSeparator, sheet, workbook);
        RegionUtil.setBottomBorderColor(HSSFColor.AQUA.index, bottomSeparator, sheet, workbook);

        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, leftSeparator, sheet, workbook);
        RegionUtil.setBottomBorderColor(HSSFColor.AQUA.index, leftSeparator, sheet, workbook);

        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, rightSeparator, sheet, workbook);
        RegionUtil.setBottomBorderColor(HSSFColor.AQUA.index, rightSeparator, sheet, workbook);

        sheet.addMergedRegion(reportNameRegion);
        sheet.addMergedRegion(leftSeparator);
        sheet.addMergedRegion(rightSeparator);
        sheet.addMergedRegion(bottomSeparator);
    }


    /**
     * Set borders on left and right side for selected region
     *
     * @param region region to create borders
     * @param border type of border
     * @param color  color of the border
     */
    private void setLeftRightBorders(CellRangeAddress region, short border, short color) {
        RegionUtil.setBorderLeft(border, region, sheet, workbook);
        RegionUtil.setBorderRight(border, region, sheet, workbook);
        RegionUtil.setLeftBorderColor(color, region, sheet, workbook);
        RegionUtil.setRightBorderColor(color, region, sheet, workbook);
    }

    /**
     * Fills background with color
     *
     * @param region region of cells to be filled with color
     * @param color  color to fill with
     */
    private void setPureBackGround(CellRangeAddress region, short color) {
        Row row = CellUtil.getRow(region.getFirstRow(), sheet);
        Cell cell = CellUtil.getCell(row, region.getFirstColumn());
        CellUtil.setCellStyleProperty(cell, workbook, "fillForegroundColor", color);
        CellUtil.setCellStyleProperty(cell, workbook, "fillPattern", CellStyle.SOLID_FOREGROUND);
    }

    /**
     * Set style for data cells
     *
     * @param dataStyle           style of cell
     * @param columnNameCellStyle style of column header cell
     * @param rowsInserted        How many rows inserted fillData method
     * @throws java.sql.SQLException
     */
    private void setDataStyle(CellStyle dataStyle, CellStyle columnNameCellStyle, int rowsInserted) throws SQLException {
        int rowCount = DATA_Y_OFFSET + 1;
        int columnsCount = DATA_X_OFFSET;
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            Row row = CellUtil.getRow(rowCount - 1, sheet);
            Cell cell = CellUtil.getCell(row, columnsCount + i);
            cell.setCellStyle(columnNameCellStyle);
        }
        for (int i = 0; i < rowsInserted - 1; i++) {//minus one because we don't take into consideration column names row
            Row row = CellUtil.getRow(rowCount + i, sheet);
            for (int j = 0; j < metaData.getColumnCount(); j++) {
                CellUtil.getCell(row, columnsCount + j).setCellStyle(dataStyle);
            }
        }
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
            widths[i] = metaData.getColumnLabel(i + 1).length();
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