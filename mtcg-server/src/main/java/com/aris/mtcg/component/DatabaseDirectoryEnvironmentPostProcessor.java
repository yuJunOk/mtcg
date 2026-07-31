package com.aris.mtcg.component;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 在数据源初始化前确保 SQLite 父目录存在
 *
 * @author pengYuJun
 */
public class DatabaseDirectoryEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String SQLITE_PREFIX = "jdbc:sqlite:";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String datasourceUrl = environment.getProperty("spring.datasource.url");
        if (datasourceUrl == null || !datasourceUrl.startsWith(SQLITE_PREFIX)) {
            return;
        }
        String pathPart = datasourceUrl.substring(SQLITE_PREFIX.length());
        int queryIndex = pathPart.indexOf('?');
        if (queryIndex >= 0) {
            pathPart = pathPart.substring(0, queryIndex);
        }
        Path dbPath = Paths.get(pathPart);
        Path parent = dbPath.getParent();
        if (parent == null || Files.exists(parent)) {
            return;
        }
        try {
            Files.createDirectories(parent);
        } catch (IOException e) {
            throw new IllegalStateException("failed to create database directory: " + parent.toAbsolutePath(), e);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
