package com.fitnesscenter.app.service;

import com.fitnesscenter.app.dto.response.InventarizationRs;
import com.fitnesscenter.app.dto.response.InventarizationReportRs;
import com.fitnesscenter.app.dto.response.InventarizationAllRs;
import com.fitnesscenter.app.entity.Equipment;
import com.fitnesscenter.app.entity.Inventarization;
import com.fitnesscenter.app.exception.EntityNotFoundException;
import com.fitnesscenter.app.repository.EquipmentRepository;
import com.fitnesscenter.app.repository.InventarizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
import com.itextpdf.text.FontFactory;

// Apache POI импорты
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
@RequiredArgsConstructor
public class InventarizationService {
    private final InventarizationRepository inventarizationRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public List<InventarizationRs> startInventarization(Long zoneId) {
        List<Equipment> equipmentList = equipmentRepository.findByZoneIdAndDeletedFalse(zoneId);
        List<Inventarization> records = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            Inventarization inv = new Inventarization();
            inv.setEquipmentInventoryNumber(eq.getId());
            inv.setCount(1);
            inv.setRealCount(null);
            inv.setDate(LocalDate.now());
            records.add(inv);
        }

        return inventarizationRepository.saveAll(records).stream()
                .map(this::mapToRs)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<InventarizationAllRs> startInventarization() {
        List<Equipment> equipmentList = equipmentRepository.findAllByDeletedFalse();
        List<Inventarization> records = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            Inventarization inv = new Inventarization();
            inv.setEquipmentInventoryNumber(eq.getId());
            inv.setCount(1);
            inv.setRealCount(null);
            inv.setDate(LocalDate.now());
            records.add(inv);
        }

        inventarizationRepository.saveAll(records);

        Map<Long, List<Equipment>> byZone = equipmentList.stream()
                .collect(Collectors.groupingBy(Equipment::getZoneId));

        List<InventarizationAllRs> result = new ArrayList<>();
        for (Map.Entry<Long, List<Equipment>> entry : byZone.entrySet()) {
            List<InventarizationRs> items = entry.getValue().stream()
                    .map(eq -> InventarizationRs.builder()
                            .equipmentInventoryNumber(eq.getId())
                            .count(1)
                            .build())
                    .collect(Collectors.toList());

            result.add(InventarizationAllRs.builder()
                    .zoneId(entry.getKey())
                    .zoneName("Zone " + entry.getKey())
                    .items(items)
                    .build());
        }
        return result;
    }

    @Transactional
    public InventarizationRs performStep(Long inventarizationId, Integer actualCount) {
        Inventarization inv = inventarizationRepository.findById(inventarizationId)
                .orElseThrow(() -> new EntityNotFoundException("Inventarization", inventarizationId));
        inv.setRealCount(actualCount);
        return mapToRs(inventarizationRepository.save(inv));
    }

    @Transactional
    public InventarizationReportRs finishInventarization(Long zoneId) {
        List<Equipment> equipmentList = equipmentRepository.findByZoneIdAndDeletedFalse(zoneId);
        List<String> discrepancies = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            inventarizationRepository.findByEquipmentInventoryNumber(eq.getId())
                    .stream()
                    .filter(inv -> inv.getRealCount() != null && !inv.getCount().equals(inv.getRealCount()))
                    .forEach(inv -> discrepancies.add("Equipment " + eq.getId() +
                            ": expected " + inv.getCount() + ", actual " + inv.getRealCount()));
        }

        return InventarizationReportRs.builder()
                .zoneId(zoneId)
                .discrepancies(discrepancies)
                .totalScanned(equipmentList.size())
                .date(LocalDate.now())
                .build();
    }

    @Transactional
    public InventarizationReportRs finishInventarization() {
        List<Equipment> equipmentList = equipmentRepository.findAllByDeletedFalse();
        List<String> discrepancies = new ArrayList<>();

        for (Equipment eq : equipmentList) {
            inventarizationRepository.findByEquipmentInventoryNumber(eq.getId())
                    .stream()
                    .filter(inv -> inv.getRealCount() != null && !inv.getCount().equals(inv.getRealCount()))
                    .forEach(inv -> discrepancies.add("Equipment " + eq.getId() +
                            ": expected " + inv.getCount() + ", actual " + inv.getRealCount()));
        }

        return InventarizationReportRs.builder()
                .zoneId(null)
                .discrepancies(discrepancies)
                .totalScanned(equipmentList.size())
                .date(LocalDate.now())
                .build();
    }

    public String generateDiscrepancyReport(Long zoneId) {
        return "Discrepancy report for zone " + zoneId;
    }

    private InventarizationRs mapToRs(Inventarization entity) {
        return InventarizationRs.builder()
                .id(entity.getId())
                .equipmentInventoryNumber(entity.getEquipmentInventoryNumber())
                .count(entity.getCount())
                .realCount(entity.getRealCount())
                .date(entity.getDate())
                .build();
    }

    public byte[] exportInventarizationReport(Long zoneId, String format) {
        List<Inventarization> records = inventarizationRepository.findAll().stream()
                .filter(inv -> inv.getRealCount() != null)
                .filter(inv -> !inv.getCount().equals(inv.getRealCount()))
                .toList();

        if ("pdf".equalsIgnoreCase(format)) {
            return exportInventarizationToPdf(records, zoneId);
        } else if ("excel".equalsIgnoreCase(format)) {
            return exportInventarizationToExcel(records, zoneId);
        }
        return new byte[0];
    }

    private byte[] exportInventarizationToPdf(List<Inventarization> discrepancies, Long zoneId) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            // Используем FontFactory для шрифтов (нет конфликта)
            com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Отчёт о расхождениях по инвентаризации", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph zoneInfo = new Paragraph("Зона ID: " + (zoneId == null ? "Все зоны" : zoneId));
            zoneInfo.setAlignment(Element.ALIGN_CENTER);
            document.add(zoneInfo);
            document.add(Chunk.NEWLINE);

            if (discrepancies.isEmpty()) {
                Paragraph noDiscrepancies = new Paragraph("Расхождений не обнаружено");
                noDiscrepancies.setAlignment(Element.ALIGN_CENTER);
                document.add(noDiscrepancies);
            } else {
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);

                String[] headers = {"Инв. номер", "Ожидалось", "Фактически", "Расхождение"};
                com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell);
                }

                com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
                for (Inventarization inv : discrepancies) {
                    int diff = inv.getCount() - inv.getRealCount();

                    PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(inv.getEquipmentInventoryNumber()), normalFont));
                    PdfPCell cell2 = new PdfPCell(new Phrase(String.valueOf(inv.getCount()), normalFont));
                    PdfPCell cell3 = new PdfPCell(new Phrase(String.valueOf(inv.getRealCount()), normalFont));
                    PdfPCell cell4 = new PdfPCell(new Phrase(String.valueOf(diff), normalFont));

                    cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell4.setHorizontalAlignment(Element.ALIGN_CENTER);

                    table.addCell(cell1);
                    table.addCell(cell2);
                    table.addCell(cell3);
                    table.addCell(cell4);
                }

                document.add(table);
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed: " + e.getMessage(), e);
        }
    }

    private byte[] exportInventarizationToExcel(List<Inventarization> discrepancies, Long zoneId) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Отчёт о расхождениях");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Инв. номер", "Ожидалось", "Фактически", "Расхождение"};

            // Используем полное имя для org.apache.poi.ss.usermodel.Font
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Inventarization inv : discrepancies) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(inv.getEquipmentInventoryNumber());
                row.createCell(1).setCellValue(inv.getCount());
                row.createCell(2).setCellValue(inv.getRealCount());
                row.createCell(3).setCellValue(inv.getCount() - inv.getRealCount());
            }

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