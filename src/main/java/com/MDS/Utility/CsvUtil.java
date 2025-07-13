package com.MDS.Utility;

import java.io.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
public class CsvUtil {

    public static <T> void writeToCsv(Writer writer, List<T> data, Class<?> clazz) {
        try {
            Field[] fields = clazz.getDeclaredFields();

            // Write CSV header
            for (int i = 0; i < fields.length; i++) {
                fields[i].setAccessible(true);
                writer.append(fields[i].getName());
                if (i < fields.length - 1) writer.append(",");
            }
            writer.append("\n");

            // Write data rows
            for (T obj : data) {
                for (int i = 0; i < fields.length; i++) {
                    fields[i].setAccessible(true);
                    Object value = fields[i].get(obj);
                    writer.append(value != null ? value.toString() : "");
                    if (i < fields.length - 1) writer.append(",");
                }
                writer.append("\n");
            }

            writer.flush();
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to write CSV for " + clazz.getSimpleName(), e);
        }
    }

    public static List<?> readFromCsv(InputStream inputStream, Class<?> entityClass) {
        List<Object> result = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String headerLine = reader.readLine(); // read header
            if (headerLine == null) {
                throw new RuntimeException("CSV file is empty");
            }
            String[] headers = headerLine.split(",");

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                Object obj = entityClass.getDeclaredConstructor().newInstance();

                for (int i = 0; i < headers.length; i++) {
                    String fieldName = headers[i].trim();
                    String value = i < values.length ? values[i].trim() : "";

                    try {
                        Field field = entityClass.getDeclaredField(fieldName);
                        field.setAccessible(true);

                        Class<?> fieldType = field.getType();
                        if (fieldType == String.class) {
                            field.set(obj, value);
                        } else if (fieldType == int.class || fieldType == Integer.class) {
                            field.set(obj, value.isEmpty() ? 0 : Integer.parseInt(value));
                        } else if (fieldType == long.class || fieldType == Long.class) {
                            field.set(obj, value.isEmpty() ? 0L : Long.parseLong(value));
                        } else if (fieldType == double.class || fieldType == Double.class) {
                            field.set(obj, value.isEmpty() ? 0.0 : Double.parseDouble(value));
                        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                            field.set(obj, Boolean.parseBoolean(value));
                        } else {
                            // You can add more type conversions here if needed
                        }
                    } catch (NoSuchFieldException e) {
                        // Field not found on entity, ignore or log
                    }
                }
                result.add(obj);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV for " + entityClass.getSimpleName(), e);
        }
        return result;
    }
}
