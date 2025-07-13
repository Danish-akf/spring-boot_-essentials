package com.MDS.controller;

import com.MDS.entity.Product;
import com.MDS.repository.ProductRepository;
import com.MDS.service.ProductService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.saveProduct(product);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Data is stored successfully");
        response.put("product", savedProduct);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/export")
    public void exportToCsv(HttpServletResponse response) {
        response.setContentType("text/csv");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=product.csv";
        response.setHeader(headerKey, headerValue);

        List<Product> products = productRepository.findAll();

        String[] csvHeader = {"product_id", "product_name", "product_description"};

        try (PrintWriter writer = response.getWriter();
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(csvHeader))) {

            for (Product product : products) {
                csvPrinter.printRecord(
                        product.getId(),
                        product.getName(),
                        product.getDescription()
                );
            }

            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export data to CSV file", e);
        }
    }
}