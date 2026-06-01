package com.fitnesscenter.app.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class InventarizationReportRs {
    private Long zoneId;
    private String zoneName; // Добавим имя зоны сразу в отчет для удобства
    private List<String> discrepancies;
    private Integer totalScanned;
    private LocalDate date;
    private String sessionId; // Чтобы можно было отследить конкретную операцию
}