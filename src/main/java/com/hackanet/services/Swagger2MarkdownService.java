package com.hackanet.services;

import io.github.swagger2markup.Swagger2MarkupConverter;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Iskander Valiev
 * created by isko
 * on 12/9/19
 */
public class Swagger2MarkdownService {

    public static void createMd() {
        Path localSwaggerFile = Paths.get("/Users/isko/Desktop/Projects/hackanet-backend/swagger.json");
        Path outputDirectory = Paths.get("/Users/isko/Desktop/Projects/hackanet-backend/overview.adoc");

        Swagger2MarkupConverter.from(localSwaggerFile)
                .build()
                .toFile(outputDirectory);
    }

    public static void main(String[] args) {
        createMd();
    }
}
