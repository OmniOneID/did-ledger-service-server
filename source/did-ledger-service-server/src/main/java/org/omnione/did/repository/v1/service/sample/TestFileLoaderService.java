package org.omnione.did.repository.v1.service.sample;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestFileLoaderService {

    private final Map<String, String> fileContentsMap = new HashMap<>();

    @PostConstruct
    public void loadFiles() throws IOException {
        Path startPath = Paths.get("./sample/data");

        Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(file)) {
                    String content = Files.readString(file);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map<String, Object> jsonMap = objectMapper.readValue(content, Map.class);
                    content = objectMapper.writeValueAsString(jsonMap);

                    fileContentsMap.put(file.getFileName().toString(), content);
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public String getFileContent(String fileName) {
        return fileContentsMap.get(fileName);
    }
}
