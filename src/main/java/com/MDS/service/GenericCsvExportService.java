package com.MDS.service;

import com.MDS.Utility.CsvUtil;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GenericCsvExportService {

    @Autowired
    private DynamicRepositoryService dynamicRepositoryService;

    // Method to download CSV for all entities
    public void downloadAllEntitiesToCsv(String basePackage, PrintWriter writer) throws Exception {
        Set<Class<?>> entities = getAllEntities(basePackage);
        System.out.println("Found entities: " + entities);
        Map<Class<?>, JpaRepository<?, ?>> repoMap = dynamicRepositoryService.getAllRepositories();

        for (Class<?> entityClass : entities) {
            JpaRepository<?, ?> repo = repoMap.get(entityClass);
            if (repo != null) {
                List<?> data = repo.findAll();
                if (!data.isEmpty()) {
                    writer.write("### " + entityClass.getSimpleName() + " ###\n");
                    CsvUtil.writeToCsv(writer, data, entityClass);
                    writer.write("\n\n");
                }
            }
        }
        writer.flush(); // Ensure all content is written out
    }

    // Upload CSV dynamically for given entity
    public void uploadCsv(String entityName, MultipartFile file) throws Exception {
        String fullClassName = "com.MDS.entity." + entityName;
        Class<?> entityClass = Class.forName(fullClassName);

        JpaRepository repository = dynamicRepositoryService.getRepository(entityClass);
        List<?> entities = CsvUtil.readFromCsv(file.getInputStream(), entityClass);

        repository.saveAll(entities);
    }

    // Get all entity classes in a package
    private Set<Class<?>> getAllEntities(String basePackage) {
        Reflections reflections = new Reflections(
                new org.reflections.util.ConfigurationBuilder()
                        .forPackage(basePackage)
                        .addScanners(new org.reflections.scanners.TypeAnnotationsScanner())
        );
        return reflections.getTypesAnnotatedWith(jakarta.persistence.Entity.class);
    }
}
