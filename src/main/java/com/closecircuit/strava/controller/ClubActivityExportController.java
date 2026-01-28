package com.closecircuit.strava.controller;

import com.closecircuit.strava.service.ClubActivityExcelService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClubActivityExportController {

    private final ClubActivityExcelService excelService;

    public ClubActivityExportController(ClubActivityExcelService excelService) {
        this.excelService = excelService;
    }

    @GetMapping("/export/club-activities.xlsx")
    public ResponseEntity<byte[]> exportExcel() {

        byte[] excel = excelService.exportToExcel();

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=club-activities.xlsx"
                )
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                        )
                )
                .body(excel);
    }
}
