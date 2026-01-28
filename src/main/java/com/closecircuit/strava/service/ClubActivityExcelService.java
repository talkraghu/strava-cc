package com.closecircuit.strava.service;

import com.closecircuit.strava.entity.ClubActivityLog;
import com.closecircuit.strava.repository.ClubActivityRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
public class ClubActivityExcelService {

    private final ClubActivityRepository repository;

    public ClubActivityExcelService(ClubActivityRepository repository) {
        this.repository = repository;
    }

    public byte[] exportToExcel() {
        // Fetch and sort activities by collectedAt ascending
        List<ClubActivityLog> activities = repository.findAll();
        activities.sort(Comparator.comparing(ClubActivityLog::getCollectedAt));

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Club Activities");

            createHeader(sheet, workbook);
            fillData(sheet, activities);

            autoSizeColumns(sheet, 9);

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export Excel", e);
        }
    }

    private void createHeader(Sheet sheet, Workbook workbook) {
        String[] headers = {
                "Activity ID",
                "Name",
                "Athlete",
                "Type",
                "Distance",
                "Moving Time",
                "Elevation Gain",
                "Device",
                "Collected At"
        };

        Row header = sheet.createRow(0);
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(style);
        }
    }

    private void fillData(Sheet sheet, List<ClubActivityLog> activities) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        int rowIdx = 1;

        for (ClubActivityLog a : activities) {
            Row row = sheet.createRow(rowIdx++);

            row.createCell(0).setCellValue(a.getActivityId() != null ? a.getActivityId() : 0);
            row.createCell(1).setCellValue(a.getName() != null ? a.getName() : "");
            row.createCell(2).setCellValue(a.getAthleteName() != null ? a.getAthleteName() : "");
            row.createCell(3).setCellValue(a.getType() != null ? a.getType() : "");
            row.createCell(4).setCellValue(a.getDistance() != null ? a.getDistance() : 0);
            row.createCell(5).setCellValue(a.getMovingTime() != null ? a.getMovingTime() : 0);
            row.createCell(6).setCellValue(a.getElevationGain() != null ? a.getElevationGain() : 0);
            row.createCell(7).setCellValue(a.getDeviceName() != null ? a.getDeviceName() : "");
            row.createCell(8).setCellValue(
                    a.getCollectedAt() != null
                            ? a.getCollectedAt().atZone(ZoneId.systemDefault()).format(formatter)
                            : ""
            );
        }
    }

    private void autoSizeColumns(Sheet sheet, int count) {
        for (int i = 0; i < count; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
