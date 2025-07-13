package com.MDS.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import java.lang.reflect.Type;
import java.util.*;

@Service
public class DynamicRepositoryService {

    @Autowired
    private List<JpaRepository<?, ?>> repositories;

    private final Map<Class<?>, JpaRepository<?, ?>> repositoryMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (JpaRepository<?, ?> repo : repositories) {
            Class<?> domainClass = extractEntityClass(repo);
            if (domainClass != null) {
                repositoryMap.put(domainClass, repo);
            }
        }
    }

    public JpaRepository<?, ?> getRepository(Class<?> entityClass) {
        return repositoryMap.get(entityClass);
    }
    public Map<Class<?>, JpaRepository<?, ?>> getAllRepositories() {
        return repositoryMap;
    }

    private Class<?> extractEntityClass(JpaRepository<?, ?> repository) {
        Type[] genericInterfaces = repository.getClass().getGenericInterfaces();

        for (Type type : genericInterfaces) {
            if (type instanceof ParameterizedType pt) {
                Type[] args = pt.getActualTypeArguments();
                if (args.length > 0 && args[0] instanceof Class<?>) {
                    Class<?> candidate = (Class<?>) args[0];
                    if (candidate.isAnnotationPresent(Entity.class)) {
                        return candidate;
                    }
                }
            }
        }

        return null;
    }
}
