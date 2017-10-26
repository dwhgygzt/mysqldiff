package cn.guzt.diff.util.export;

import cn.guzt.diff.properties.Constant;
import cn.guzt.diff.util.mybatis.bean.DbProperties;
import com.alibaba.fastjson.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExportUtil {

    private  static Logger logger = Logger.getLogger(ExportUtil.class);

    public static void exportDiffFile(DbProperties original, DbProperties target,
        List<JSONObject> tableDiff, List<JSONObject> columnDiff, List<JSONObject> indexDiff,
        List<JSONObject> partitionDiff, List<JSONObject> functionDiff, List<JSONObject> procedureDiff) {
        Workbook wb = new XSSFWorkbook();
        CellStyle paramCellStyle =wb.createCellStyle();
        paramCellStyle.setAlignment(CellStyle.ALIGN_CENTER);
        paramCellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        paramCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
        paramCellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderBottom(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderTop(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderLeft(CellStyle.SOLID_FOREGROUND);
        paramCellStyle.setBorderRight(CellStyle.SOLID_FOREGROUND);
        Font titleFont = wb.createFont();
        titleFont.setBold(true);
        titleFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        paramCellStyle.setFont(titleFont);

        Sheet sheet1 = wb.createSheet("表信息差异");
        createRowCell(original, target,sheet1,Constant.TABLE,tableDiff,paramCellStyle);

        Sheet sheet2 = wb.createSheet("列信息差异");
        createRowCell(original, target,sheet2,Constant.COLUMN,columnDiff,paramCellStyle);

        Sheet sheet3 = wb.createSheet("表索引信息差异");
        createRowCell(original, target,sheet3,Constant.INDEX,indexDiff,paramCellStyle);

        Sheet sheet4 = wb.createSheet("表分区信息差异");
        createRowCell(original, target,sheet4,Constant.PARTITION,partitionDiff,paramCellStyle);

        Sheet sheet5 = wb.createSheet("自定义函数信息差异");
        createRowCell(original, target,sheet5,Constant.FUNCTION,functionDiff,paramCellStyle,true);

        Sheet sheet6 = wb.createSheet("自定义存储过程信息差异");
        createRowCell(original, target,sheet6,Constant.PROCEDURE,procedureDiff,paramCellStyle,true);

        Calendar calendar = Calendar.getInstance();
        String fileName = "."+File.separator+"export"+File.separator+original.getDbId()+"与"+target.getDbId()+"比对结果"+ calendar.get(Calendar.YEAR)+"-"+ (calendar.get(Calendar.MONTH)+1)+"-"+calendar.get(Calendar.DATE)+".xls";
        if(wb instanceof XSSFWorkbook){
            fileName += "x";
        }
        File file = new File(fileName);
        if(file.exists()){
            file.delete();
        }

        logger.info("------------------------------------------");
        logger.info("导出比对的EXCEL文件路径："+file.getAbsolutePath());
        logger.info("------------------------------------------");

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            wb.write(out);
            wb.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e);
        }finally {
            if (out != null){
                try {
                    out.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                    logger.error(e);
                }
            }
        }
    }

    /**
     * excel 创建行和列信息
     * @param sheet
     * @param columnTitle
     * @param rowList
     * @param paramCellStyle
     */
    public static void createRowCell(DbProperties original, DbProperties target,Sheet sheet, String[][] columnTitle, List<JSONObject> rowList,CellStyle paramCellStyle){
        boolean isCodeCompare = false;
        createRowCell(original, target, sheet, columnTitle, rowList, paramCellStyle, isCodeCompare);
    }

    /**
     * excel 创建行和列信息
     * @param sheet
     * @param columnTitle
     * @param rowList
     * @param paramCellStyle
     */
    public static void createRowCell(DbProperties original, DbProperties target,Sheet sheet, String[][] columnTitle, List<JSONObject> rowList,CellStyle paramCellStyle,boolean isCodeCompare){
        Row row = null;
        Row headerRow = null;
        headerRow = sheet.createRow(0);
        Cell cellCompareResult = headerRow.createCell(0);
        cellCompareResult.setCellStyle(paramCellStyle);
        cellCompareResult.setCellValue(Constant.COMPARE_MSG);
        int width = 30*256;
        // 设置列宽
        sheet.setColumnWidth(0,width);
        for(int i = 0; i< columnTitle.length; i++){
            if (Constant.KEY_FLAG.equals(columnTitle[i][2])){
                // 不同的颜色标注出 作为key列
                paramCellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            }
            Cell cell = headerRow.createCell(i+1);
            cell.setCellStyle(paramCellStyle);
            cell.setCellValue(columnTitle[i][0]);
            paramCellStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            // 设置列宽
            sheet.setColumnWidth(i+1,width);
        }
        if (isCodeCompare){
            Cell cell = headerRow.createCell(columnTitle.length + 1);
            cell.setCellStyle(paramCellStyle);
            cell.setCellValue(original.getDbId()+"中的代码(去除空行和注释行)");

            cell = headerRow.createCell(columnTitle.length + 2);
            cell.setCellStyle(paramCellStyle);
            cell.setCellValue(target.getDbId()+"中的代码(去除空行和注释行)");
            // 设置列宽
            sheet.setColumnWidth(columnTitle.length + 1,width);
            sheet.setColumnWidth(columnTitle.length + 2,width);
        }

        for(int i=0; i<rowList.size(); i++){
            row = sheet.createRow(i+1);
            row.createCell(0).setCellValue(rowList.get(i).getString(Constant.COMPARE_MSG));
            for(int j=0; j<columnTitle.length; j++){
                row.createCell(j+1).setCellValue(rowList.get(i).getString(columnTitle[j][1]));
            }
            if (isCodeCompare){
                row.createCell(columnTitle.length+1).setCellValue(rowList.get(i).getString(Constant.CODE_ORIGINAL));
                row.createCell(columnTitle.length+2).setCellValue(rowList.get(i).getString(Constant.CODE_TARGET));
            }
        }
    }
}
