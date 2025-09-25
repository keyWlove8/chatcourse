package com.k8.chat.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: k8
 * @CreateTime: 2025-09-23
 * @Version: 1.0
 */
public class DocumentLoaderFactory {
    public static Document parseDocument(String fileName, InputStream inputStream) throws Exception {
        if (fileName == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        if (!fileName.contains(".")) throw new IllegalArgumentException("File name error");
        String extension = getFileExtension(fileName).toLowerCase();

        switch (extension) {
            case "pdf":
                return DocumentParsers.parsePdf(inputStream,fileName);
            case "doc":
                return DocumentParsers.parseDoc(inputStream,fileName);
            case "docx":
                return DocumentParsers.parseDocx(inputStream,fileName);
            case "xlsx":
                return DocumentParsers.parseXlsx(inputStream,fileName);
            case "txt":
            case "text":
                return DocumentParsers.parseTxt(inputStream,fileName);
            case "md":
            case "markdown":
                return DocumentParsers.parseMarkdown(inputStream,fileName);
            case "csv":
                return DocumentParsers.parseCsv(inputStream,fileName);
            default:
                throw new UnsupportedOperationException("不支持的文件类型: " + extension);
        }
    }

    /**
     * 提取文件扩展�?
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }



    public class DocumentParsers {

        /**
         * 解析PDF文件并返回Document对象
         */
        private static Document parsePdf(InputStream inputStream, String fileName) throws Exception {
            try (PDDocument document = PDDocument.load(inputStream)) {
                PDFTextStripper stripper = new PDFTextStripper();
                String content = stripper.getText(document);

                // 创建元数�?
                Metadata metadata = createMetadata(fileName, "pdf");
                return Document.from(content, metadata);
            }
        }

        /**
         * 解析Word (docx) 文件并返回Document对象
         */
        private static Document parseDocx(InputStream inputStream, String fileName) throws Exception {
            try (XWPFDocument document = new XWPFDocument(inputStream)) {
                List<XWPFParagraph> paragraphs = document.getParagraphs();
                String content = paragraphs.stream()
                        .map(XWPFParagraph::getText)
                        .collect(Collectors.joining("\n"));

                // 创建元数�?
                Metadata metadata = createMetadata(fileName, "docx");
                return Document.from(content, metadata);
            }
        }

        /**
         * 解析doc格式的文件并返回Document对象
         */
        private static Document parseDoc(InputStream inputStream, String fileName) throws Exception {
            try (HWPFDocument document = new HWPFDocument(inputStream);
                 WordExtractor extractor = new WordExtractor(document)) {
                // 获取所有段落文�?
                String[] paragraphs = extractor.getParagraphText();
                // 过滤空段落并拼接
                StringBuilder content = new StringBuilder();
                for (String para : paragraphs) {
                    String trimmedPara = para.trim();
                    if (!trimmedPara.isEmpty()) {
                        content.append(trimmedPara).append("\n");
                    }
                }
                // 移除最后一个多余的换行�?
                if (content.length() > 0) {
                    content.setLength(content.length() - 1);
                }

                // 创建元数�?
                Metadata metadata = createMetadata(fileName, "doc");
                return Document.from(content.toString(), metadata);
            }
        }

        /**
         * 解析Excel (xlsx) 文件并返回Document对象
         */
        private static Document parseXlsx(InputStream inputStream, String fileName) throws Exception {
            StringBuilder result = new StringBuilder();
            try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    XSSFSheet sheet = workbook.getSheetAt(i);
                    result.append("工作�? ").append(sheet.getSheetName()).append("\n");

                    for (Row row : sheet) {
                        for (Cell cell : row) {
                            String cellValue = cell.toString();
                            result.append(cellValue).append("\t");
                        }
                        result.append("\n");
                    }
                    result.append("\n");
                }
            }

            // 创建元数�?
            Metadata metadata = createMetadata(fileName, "xlsx");
            return Document.from(result.toString(), metadata);
        }

        /**
         * 解析文本文件并返回Document对象
         */
        private static Document parseTxt(InputStream inputStream, String fileName) throws Exception {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }

                // 创建元数�?
                Metadata metadata = createMetadata(fileName, "txt");
                return Document.from(content.toString(), metadata);
            }
        }

        /**
         * 解析Markdown文件并返回Document对象
         */
        private static Document parseMarkdown(InputStream inputStream, String fileName) throws Exception {
            // 先解析为文本
            String markdownContent;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                markdownContent = content.toString();
            }

            // 转换为纯文本内容
            Parser parser = Parser.builder().build();
            Node document = parser.parse(markdownContent);
            TextContentRenderer renderer = TextContentRenderer.builder().build();
            String content = renderer.render(document);

            // 创建元数�?
            Metadata metadata = createMetadata(fileName, "md");
            return Document.from(content, metadata);
        }

        /**
         * 解析CSV文件并返回Document对象
         */
        private static Document parseCsv(InputStream inputStream, String fileName) throws Exception {
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                 CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader())) {

                StringBuilder content = new StringBuilder();
                // 添加表头
                List<String> headers = csvParser.getHeaderNames();
                content.append(headers.stream().collect(Collectors.joining(","))).append("\n");

                // 添加内容�?
                for (CSVRecord record : csvParser) {
                    content.append(record.stream().collect(Collectors.joining(","))).append("\n");
                }

                // 创建元数�?
                Metadata metadata = createMetadata(fileName, "csv");
                return Document.from(content.toString(), metadata);
            }
        }

        /**
         * 创建元数据辅助方�?
         */
        private static Metadata createMetadata(String fileName, String fileType) {
            // 通用写法：先创建空Metadata，再通过add方法添加键值对
            Map<String,String> map = new HashMap<>();
            map.put("file_name", fileName);
            map.put("file_type", fileType);
            map.put("processing_time", String.valueOf(System.currentTimeMillis()));
            map.put("source", "uploaded_file");
            Metadata metadata = Metadata.from(map);
            return metadata;
        }
    }


}
