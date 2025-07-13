package com.MDS.controller;

import com.MDS.service.GenericCsvExportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/csv")
public class CsvExportController {

    @Autowired
    private GenericCsvExportService genericCsvExportService;

    // 📤 Download CSV for all entities dynamically
    @GetMapping("/download/all")
    public void downloadAllEntitiesCsv(HttpServletResponse response) {
        try {
            // Set headers once
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment; filename=all_entities.csv");

            // Delegate all writing to the service
            genericCsvExportService.downloadAllEntitiesToCsv("com.MDS.entity", response.getWriter());
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    // 📥 Upload CSV dynamically for given entity
    @PostMapping("/upload/{entityName}")
    public ResponseEntity<String> uploadCsv(@PathVariable String entityName,
                                            @RequestParam("file") MultipartFile file) {
        try {
            genericCsvExportService.uploadCsv(entityName, file);
            return ResponseEntity.ok("CSV uploaded for entity: " + entityName);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Upload failed: " + e.getMessage());
        }
    }
}
