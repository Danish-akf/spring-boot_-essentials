package com.MDS.entity;

import jakarta.persistence.Entity;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;


import java.util.Set;

public class EntityScanner {

    public static Set<Class<?>> getAllEntities(String basePackage) {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .forPackage(basePackage)
                .addScanners(Scanners.TypesAnnotated)
        );

        return reflections.getTypesAnnotatedWith(Entity.class);
    }
}
