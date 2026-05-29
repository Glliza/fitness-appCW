package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.response.ConsumablesRs;
import com.fitnesscenter.app.dto.response.ConsumablesZonesRs;
import com.fitnesscenter.app.entity.Consumables;
import com.fitnesscenter.app.entity.ConsumablesZone;
import com.fitnesscenter.app.exception.NegativeStockException;
import com.fitnesscenter.app.repository.ConsumablesRepository;
import com.fitnesscenter.app.repository.ConsumablesZoneRepository;
import com.fitnesscenter.app.repository.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.text.pdf.BaseFont;

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
import com.fitnesscenter.app.entity.Zone;

// Apache POI импорты
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
    private final ZoneRepository zoneRepository;

    public ConsumablesRs getConsumablesById(Long id) {
        Consumables consumables = consumablesRepository.findById(id).orElseThrow();
        return mapToRs(consumables);
    }

    // Новый метод с пагинацией
    public Page<ConsumablesRs> getAllConsumables(Pageable pageable) {
        return consumablesRepository.findAll(pageable)
                .map(this::mapToRs);
    }

    // Старый метод для совместимости
    public List<ConsumablesRs> getAllConsumablesList() {
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

    // Метод для получения балансов по всем зонам для таблицы (с пагинацией по зонам)
    public Page<ZoneBalanceDto> getZoneBalances(Pageable pageable) {
        Page<Zone> zonesPage = zoneRepository.findAllByDeletedFalse(pageable);
        return zonesPage.map(zone -> {
            ZoneBalanceDto dto = new ZoneBalanceDto();
            dto.setZoneId(zone.getId());
            dto.setZoneName(zone.getName());
            return dto;
        });
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
        System.out.println("Exporting report with " + allStocks.size() + " records");

        if ("pdf".equalsIgnoreCase(format)) {
            return exportToPdf(allStocks);
        } else if ("excel".equalsIgnoreCase(format)) {
            return exportToExcel(allStocks);
        }
        return new byte[0];
    }

    // Вспомогательный DTO для балансов
    public static class ZoneBalanceDto {
        private Long zoneId;
        private String zoneName;
        private java.util.Map<Long, Integer> balances = new java.util.HashMap<>();

        public Long getZoneId() { return zoneId; }
        public void setZoneId(Long zoneId) { this.zoneId = zoneId; }
        public String getZoneName() { return zoneName; }
        public void setZoneName(String zoneName) { this.zoneName = zoneName; }
        public java.util.Map<Long, Integer> getBalances() { return balances; }
        public void setBalances(java.util.Map<Long, Integer> balances) { this.balances = balances; }
    }


    private byte[] exportToPdf(List<ConsumablesZone> stocks) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Загружаем шрифт с поддержкой кириллицы
            BaseFont baseFont = BaseFont.createFont("src/main/resources/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font titleFont = new Font(baseFont, 16, Font.BOLD);
            Font headerFont = new Font(baseFont, 12, Font.BOLD);
            Font normalFont = new Font(baseFont, 10, Font.NORMAL);

            // Заголовок
            Paragraph title = new Paragraph("Отчёт по остаткам расходных материалов", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            // Дата
            Paragraph datePara = new Paragraph("Дата формирования: " + java.time.LocalDate.now(), normalFont);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            document.add(datePara);
            document.add(Chunk.NEWLINE);

            // Таблица
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{15f, 25f, 15f, 30f, 15f});

            // Заголовки
            String[] headers = {"ID расходника", "Название", "ID зоны", "Название зоны", "Остаток"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                cell.setPadding(5);
                table.addCell(cell);
            }

            // Данные
            for (ConsumablesZone stock : stocks) {
                String name = consumablesRepository.findById(stock.getConsumablesId())
                        .map(Consumables::getName)
                        .orElse("Неизвестно");

                String zoneName = "Неизвестно";
                if (stock.getZoneId() != null) {
                    zoneName = zoneRepository.findById(stock.getZoneId())
                            .map(com.fitnesscenter.app.entity.Zone::getName)
                            .orElse("Зона не найдена");
                }

                table.addCell(new PdfPCell(new Phrase(String.valueOf(stock.getConsumablesId()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(name, normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(stock.getZoneId()), normalFont)));
                table.addCell(new PdfPCell(new Phrase(zoneName, normalFont)));

                PdfPCell countCell = new PdfPCell(new Phrase(String.valueOf(stock.getCount()), normalFont));
                countCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(countCell);
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

            // Стиль для заголовков
            CellStyle headerStyle = workbook.createCellStyle();
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(org.apache.poi.ss.usermodel.IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(org.apache.poi.ss.usermodel.FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            headerStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // Стиль для данных
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderTop(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderLeft(org.apache.poi.ss.usermodel.BorderStyle.THIN);
            dataStyle.setBorderRight(org.apache.poi.ss.usermodel.BorderStyle.THIN);

            // Заголовки (5 колонок)
            Row headerRow = sheet.createRow(0);
            String[] headers = {"ID расходника", "Название", "ID зоны", "Название зоны", "Остаток"};

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

                // Получаем название зоны
                String zoneName = "Неизвестно";
                if (stock.getZoneId() != null) {
                    try {
                        zoneName = zoneRepository.findById(stock.getZoneId())
                                .map(zone -> zone.getName())
                                .orElse("Зона не найдена");
                    } catch (Exception e) {
                        zoneName = "Ошибка";
                    }
                }

                Cell cell0 = row.createCell(0);
                cell0.setCellValue(stock.getConsumablesId());
                cell0.setCellStyle(dataStyle);

                Cell cell1 = row.createCell(1);
                cell1.setCellValue(name);
                cell1.setCellStyle(dataStyle);

                Cell cell2 = row.createCell(2);
                cell2.setCellValue(stock.getZoneId());
                cell2.setCellStyle(dataStyle);

                Cell cell3 = row.createCell(3);
                cell3.setCellValue(zoneName);
                cell3.setCellStyle(dataStyle);

                Cell cell4 = row.createCell(4);
                cell4.setCellValue(stock.getCount());
                cell4.setCellStyle(dataStyle);
            }

            // Автоширина колонок
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // Добавляем небольшой отступ
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 500);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Excel generation failed: " + e.getMessage(), e);
        }
    }
}