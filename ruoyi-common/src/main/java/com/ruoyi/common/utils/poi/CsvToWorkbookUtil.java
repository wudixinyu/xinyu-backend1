package com.ruoyi.common.utils.poi;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import com.ruoyi.common.exception.ServiceException;

/**
 * CSV 转 Excel 工作簿工具
 * <p>
 * POI 的 WorkbookFactory 不支持 CSV，而亚马逊等平台的广告报表常以 CSV 导出。
 * 本工具将 CSV 文本解析后生成内存 XSSFWorkbook 流，供 {@link ExcelUtil#importExcel(InputStream)}
 * 复用表头匹配与类型转换逻辑。
 * <p>
 * 特性：UTF-8/UTF-16 BOM 识别、双引号包裹字段、"" 转义、\r\n/\n/\r 换行、
 * 货币符号与千分位清洗（如 $1,234.56 -> 1234.56）。
 *
 * @author ruoyi
 */
public class CsvToWorkbookUtil
{
    /** 带货币符号/千分位的数值样式（清洗后交给 ExcelUtil 做类型转换） */
    private static final Pattern MONEY_PATTERN = Pattern.compile("^[€$£¥]?-?[\\d,]*\\.?\\d+%?$");

    private CsvToWorkbookUtil()
    {
    }

    /**
     * 将 CSV 输入流转换为 xlsx 工作簿输入流
     *
     * @param csvIn CSV 输入流
     * @return xlsx 工作簿输入流
     */
    public static InputStream toWorkbookStream(InputStream csvIn)
    {
        try
        {
            byte[] bytes = readAll(csvIn);
            String text = decode(bytes);
            List<List<String>> rows = parse(text);
            if (rows.isEmpty())
            {
                throw new ServiceException("CSV文件内容为空");
            }
            XSSFWorkbook wb = new XSSFWorkbook();
            try
            {
                Sheet sheet = wb.createSheet("sheet1");
                for (int r = 0; r < rows.size(); r++)
                {
                    Row row = sheet.createRow(r);
                    List<String> cells = rows.get(r);
                    for (int c = 0; c < cells.size(); c++)
                    {
                        Cell cell = row.createCell(c);
                        cell.setCellValue(clean(cells.get(c)));
                    }
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                wb.write(bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
            finally
            {
                wb.close();
            }
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("CSV解析失败：" + e.getMessage());
        }
    }

    private static byte[] readAll(InputStream is) throws Exception
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = is.read(buf)) != -1)
        {
            bos.write(buf, 0, n);
        }
        return bos.toByteArray();
    }

    /**
     * 按 BOM 识别编码（UTF-8 / UTF-16LE / UTF-16BE），默认 UTF-8
     */
    private static String decode(byte[] bytes)
    {
        if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF)
        {
            return new String(bytes, 3, bytes.length - 3, StandardCharsets.UTF_8);
        }
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE)
        {
            return new String(bytes, 2, bytes.length - 2, Charset.forName("UTF-16LE"));
        }
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF)
        {
            return new String(bytes, 2, bytes.length - 2, Charset.forName("UTF-16BE"));
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * CSV 状态机解析：支持双引号包裹字段与 "" 转义，\r\n/\n/\r 均为换行
     */
    private static List<List<String>> parse(String text)
    {
        List<List<String>> rows = new ArrayList<List<String>>();
        List<String> row = new ArrayList<String>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean fieldStarted = false;
        int i = 0;
        int len = text.length();
        while (i < len)
        {
            char ch = text.charAt(i);
            if (inQuotes)
            {
                if (ch == '"')
                {
                    if (i + 1 < len && text.charAt(i + 1) == '"')
                    {
                        field.append('"');
                        i += 2;
                        continue;
                    }
                    inQuotes = false;
                    i++;
                    continue;
                }
                field.append(ch);
                i++;
                continue;
            }
            if (ch == '"' && !fieldStarted)
            {
                inQuotes = true;
                fieldStarted = true;
                i++;
                continue;
            }
            if (ch == ',')
            {
                row.add(field.toString());
                field.setLength(0);
                fieldStarted = false;
                i++;
                continue;
            }
            if (ch == '\r' || ch == '\n')
            {
                if (ch == '\r' && i + 1 < len && text.charAt(i + 1) == '\n')
                {
                    i++;
                }
                row.add(field.toString());
                field.setLength(0);
                fieldStarted = false;
                if (!isEmptyRow(row))
                {
                    rows.add(row);
                }
                row = new ArrayList<String>();
                i++;
                continue;
            }
            field.append(ch);
            fieldStarted = true;
            i++;
        }
        // 末行（文件不以换行结尾）
        if (field.length() > 0 || !row.isEmpty())
        {
            row.add(field.toString());
            if (!isEmptyRow(row))
            {
                rows.add(row);
            }
        }
        return rows;
    }

    private static boolean isEmptyRow(List<String> row)
    {
        for (String cell : row)
        {
            if (cell != null && !cell.trim().isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 清洗货币符号与千分位（仅对数值样式字符串生效），并去除首尾空白
     */
    private static String clean(String value)
    {
        if (value == null)
        {
            return "";
        }
        String v = value.trim();
        if (!v.isEmpty() && MONEY_PATTERN.matcher(v).matches() && (v.indexOf('€') >= 0 || v.indexOf('$') >= 0 || v.indexOf('£') >= 0 || v.indexOf('¥') >= 0 || v.indexOf(',') >= 0))
        {
            v = v.replaceAll("[€$£¥,]", "");
        }
        return v;
    }
}
