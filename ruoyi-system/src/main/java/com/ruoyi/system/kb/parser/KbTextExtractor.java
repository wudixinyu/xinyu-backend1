package com.ruoyi.system.kb.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.springframework.stereotype.Component;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;

/**
 * 知识库文档文本提取器（txt/md/pdf/docx）
 *
 * @author ruoyi
 * @date 2026-10-14
 */
@Component
public class KbTextExtractor
{
    /**
     * 按文件类型提取纯文本
     *
     * @param file 本地文件
     * @param fileType 扩展名（txt/md/markdown/pdf/docx）
     * @return 提取的文本
     */
    public String extract(File file, String fileType)
    {
        if (file == null || !file.exists())
        {
            throw new ServiceException("文档文件不存在或已被删除");
        }
        String type = StringUtils.isEmpty(fileType) ? "" : fileType.toLowerCase();
        switch (type)
        {
            case "txt":
            case "md":
            case "markdown":
                return readPlainText(file);
            case "pdf":
                return readPdf(file);
            case "docx":
                return readDocx(file);
            default:
                throw new ServiceException("不支持的文件格式：" + fileType);
        }
    }

    private String readPlainText(File file)
    {
        try
        {
            byte[] bytes = Files.readAllBytes(file.toPath());
            // 去除 UTF-8 BOM
            int offset = 0;
            if (bytes.length >= 3 && (bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF)
            {
                offset = 3;
            }
            String text = new String(bytes, offset, bytes.length - offset, StandardCharsets.UTF_8);
            // 出现大量替换符说明并非 UTF-8，回退 GBK
            if (text.indexOf('\uFFFD') >= 0)
            {
                text = new String(bytes, offset, bytes.length - offset, Charset.forName("GBK"));
            }
            return text;
        }
        catch (Exception e)
        {
            throw new ServiceException("读取文本文件失败：" + e.getMessage());
        }
    }

    private String readPdf(File file)
    {
        try (PDDocument document = Loader.loadPDF(file))
        {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        }
        catch (Exception e)
        {
            throw new ServiceException("解析PDF失败：" + e.getMessage());
        }
    }

    private String readDocx(File file)
    {
        try (InputStream in = new FileInputStream(file); XWPFDocument document = new XWPFDocument(in);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document))
        {
            return extractor.getText();
        }
        catch (Exception e)
        {
            throw new ServiceException("解析Word(docx)失败：" + e.getMessage());
        }
    }
}
