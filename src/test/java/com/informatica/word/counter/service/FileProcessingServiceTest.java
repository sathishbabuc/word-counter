/*
 * Copyright information here.
 */
package com.informatica.word.counter.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.informatica.word.counter.db.FileEntity;
import com.informatica.word.counter.db.FileStatusRepository;
import com.informatica.word.counter.dto.ProcessingStatus;

/**
 * JUnit test class for File Processing Service.
 **/
@ExtendWith(MockitoExtension.class)
public class FileProcessingServiceTest {

    @Mock
    private FileStatusRepository repository;

    @InjectMocks
    private FileProcessingService service;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "fileInputLocation", System.getProperty("user.dir") + "\\");
        ReflectionTestUtils.setField(service, "processedFileLocation", System.getProperty("user.dir") + "\\");
    }

    @Nested
    class Add {

        @Test
        void success() {

            FileEntity expectedEntity = new FileEntity();
            expectedEntity.setId(555L);
            expectedEntity.setName("TEST_FILE.txt");
            expectedEntity.setStatus(ProcessingStatus.IN_PROGRESS.getStorageValue());

            when(repository.save(any(FileEntity.class))).thenReturn(expectedEntity);

            service.add("test_file.txt", "some text".getBytes());
        }

    }

    @Nested
    class GetStatus {

        @Test
        void valid() {

            FileEntity expectedEntity = new FileEntity();
            expectedEntity.setId(111L);
            expectedEntity.setName("TEST_FILE.txt");
            expectedEntity.setStatus(ProcessingStatus.SUCCESS.getStorageValue());

            when(repository.findById(111L)).thenReturn(Optional.of(expectedEntity));

            String actual = service.getStatus(111L);

            assertEquals(ProcessingStatus.SUCCESS.getDisplayValue(), actual);
        }

        @Test
        void Notfound() {

            when(repository.findById(404L)).thenReturn(Optional.ofNullable(null));

            String actual = service.getStatus(404L);

            assertNull(actual);
        }

    }

    @Nested
    class GetResult {

        @Test
        void valid() {

            try {
                Files.write(Path.of(System.getProperty("user.dir"), String.valueOf(999L)), "{\"test\":5}".getBytes(),
                        StandardOpenOption.CREATE);
            } catch (IOException e) {
                // Ignore. Not an issue for tests
            }

            Map<String, Integer> actual = service.getResult(999L);

            assertEquals(5, actual.get("test"));
        }

        @Test
        void Notfound() {

            Map<String, Integer> actual = service.getResult(404L);

            assertNull(actual);
        }

    }

}
