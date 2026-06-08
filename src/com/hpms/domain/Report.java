package com.hpms.domain;

import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID reportId;
    private final UUID patientId;
    private final UUID generatedById;
    private final LocalDate dateRangeStart;
    private final LocalDate dateRangeEnd;
    private LocalDateTime generatedAt;
    private String filePath;
    private String status;

    public Report(UUID patientId, UUID generatedById, LocalDate dateRangeStart, LocalDate dateRangeEnd) {
        this.reportId = UUID.randomUUID();
        this.patientId = patientId;
        this.generatedById = generatedById;
        this.dateRangeStart = dateRangeStart;
        this.dateRangeEnd = dateRangeEnd;
        this.status = "PENDING";
    }

    public void generate() {
        this.generatedAt = LocalDateTime.now();
        this.filePath = "reports/report-" + reportId + ".txt";
        this.status = "GENERATED";
    }

    public Path download() {
        if (!"GENERATED".equals(status) && !"ARCHIVED".equals(status)) {
            throw new IllegalStateException("Report is not available for download.");
        }
        return Path.of(filePath);
    }

    public void archive() {
        this.status = "ARCHIVED";
    }

    public UUID getReportId() {
        return reportId;
    }

    public UUID getPatientId() {
        return patientId;
    }

    public UUID getGeneratedById() {
        return generatedById;
    }

    public LocalDate getDateRangeStart() {
        return dateRangeStart;
    }

    public LocalDate getDateRangeEnd() {
        return dateRangeEnd;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getStatus() {
        return status;
    }
}