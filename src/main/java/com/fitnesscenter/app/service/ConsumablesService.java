package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.response.ConsumablesRs;
import com.fitnesscenter.app.dto.response.ConsumablesZonesRs;
import com.fitnesscenter.app.entity.Consumables;
import com.fitnesscenter.app.entity.ConsumablesZone;
import com.fitnesscenter.app.exception.NegativeStockException;
import com.fitnesscenter.app.repository.ConsumablesRepository;
import com.fitnesscenter.app.repository.ConsumablesZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

// iText импорты
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;

// Apache POI импорты с полным именем для Font
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@RequiredArgsConstructor
public class ConsumablesService {
    private final ConsumablesRepository consumablesRepository;
    private final ConsumablesZoneRepository consumablesZoneRepository;

    public ConsumablesRs getConsumablesById(Long id) {
        Consumables consumables = consumablesRepository.findById(id).orElseThrow();
        return mapToRs(consumables);
    }

    public List<ConsumablesRs> getAllConsumables() {
        return consumablesRepository.findAll().stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public ConsumablesZonesRs addIncome(Long consumableId, Long zoneId, Integer amount) {
        ConsumablesZone zoneStock = consumablesZoneRepository
                .findByConsumablesIdAndZoneId(consumableId, zoneId)
                .orElse(new ConsumablesZone());

        zoneStock.setConsumablesId(consumableId);
        zoneStock.setZoneId(zoneId);

        Integer currentCount = zoneStock.getCount();
        if (currentCount == null) {
            currentCount = 0;
        }
        zoneStock.setCount(currentCount + amount);

        ConsumablesZone saved = consumablesZoneRepository.save(zoneStock);
        return mapToZoneRs(saved);
    }

    @Transactional
    public ConsumablesZonesRs addExpense(Long consumableId, Long zoneId, Integer amount) {
        ConsumablesZone zoneStock = consumablesZoneRepository
                .findByConsumablesIdAndZoneId(consumableId, zoneId)
                .orElseThrow(() -> new NegativeStockException("No stock found"));

        Integer currentCount = zoneStock.getCount();
        if (currentCount == null) {
            currentCount = 0;
        }

        if (currentCount < amount) {
            throw new NegativeStockException("Insufficient stock. Available: " + currentCount + ", requested: " + amount);
        }

        zoneStock.setCount(currentCount - amount);
        ConsumablesZone saved = consumablesZoneRepository.save(zoneStock);
        return mapToZoneRs(saved);
    }

    public Integer getCurrentBalance(Long consumableId, Long zoneId) {
        return consumablesZoneRepository
                .findByConsumablesIdAndZoneId(consumableId, zoneId)
                .map(ConsumablesZone::getCount)
                .orElse(0);
    }

    private ConsumablesRs mapToRs(Consumables entity) {
        return ConsumablesRs.builder()
                .id(entity.getId())
                .name(entity.getName())
                .realCount(entity.getRealCount())
                .build();
    }

    private ConsumablesZonesRs mapToZoneRs(ConsumablesZone entity) {
        return ConsumablesZonesRs.builder()
                .consumablesId(entity.getConsumablesId())
                .zoneId(entity.getZoneId())
                .count(entity.getCount())
                .build();
    }

    public byte[] exportBalanceReport(String format) {
        List<ConsumablesZone> allStocks = consumablesZoneRepository.findAll();

        if ("pdf".equalsIgnoreCase(format)) {
            return exportToPdf(allStocks);
        } else if ("excel".equalsIgnoreCase(format)) {
            return exportToExcel(allStocks);
        }
        return new byte[0];
    }

    private byte[] exportToPdf(List<ConsumablesZone> stocks) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Заголовок - используем FontFactory
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Отчёт по остаткам расходных материалов", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // Таблица
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Заголовки таблицы
            String[] headers = {"ID расходника", "Название", "ID зоны", "Остаток"};
            for (String header : headers) {
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Данные
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            for (ConsumablesZone stock : stocks) {
                String name = consumablesRepository.findById(stock.getConsumablesId())
                        .map(c -> c.getName())
                        .orElse("Неизвестно");

                PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(stock.getConsumablesId()), normalFont));
                PdfPCell cell2 = new PdfPCell(new Phrase(name, normalFont));
                PdfPCell cell3 = new PdfPCell(new Phrase(String.valueOf(stock.getZoneId()), normalFont));
                PdfPCell cell4 = new PdfPCell(new Phrase(String.valueOf(stock.getCount()), normalFont));

                cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

                table.addCell(cell1);
                table.addCell(cell2);
                table.addCell(cell3);
                table.addCell(cell4);
            }

            document.add(table);
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private byte[] exportToExcel(List<ConsumablesZone> stocks) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Отчёт по остаткам");

            // Заголовки
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID расходника", "Название", "ID зоны", "Остаток"};

            // Используем полное имя org.apache.poi.ss.usermodel.Font
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Данные
            int rowNum = 1;
            for (ConsumablesZone stock : stocks) {
                Row row = sheet.createRow(rowNum++);

                String name = consumablesRepository.findById(stock.getConsumablesId())
                        .map(c -> c.getName())
                        .orElse("Неизвестно");

                row.createCell(0).setCellValue(stock.getConsumablesId());
                row.createCell(1).setCellValue(name);
                row.createCell(2).setCellValue(stock.getZoneId());
                row.createCell(3).setCellValue(stock.getCount());
            }

            // Автоширина колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed: " + e.getMessage(), e);
        }
    }
}