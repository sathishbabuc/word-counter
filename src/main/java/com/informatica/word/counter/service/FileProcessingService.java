/*
 * Copyright information here.
 */
package com.informatica.word.counter.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.informatica.word.counter.db.FileEntity;
import com.informatica.word.counter.db.FileStatusRepository;
import com.informatica.word.counter.dto.FileSummary;
import com.informatica.word.counter.dto.ProcessingStatus;
import com.informatica.word.counter.exception.FileProcessingException;

/**
 * File Processing Service Apis.
 */
@Service
public class FileProcessingService {

    /** logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingService.class);

    @Value("${file.location.input:}")
    private String fileInputLocation;

    @Value("${file.location.processed:}")
    private String processedFileLocation;

    @Autowired
    private FileStatusRepository fileStatusRepository;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Adds a file for processing.
     * @param name , the name of the file
     * @param content, the file content.
     * @return the summary of the file added for processing.
     * @exception exception in case add fails.
     */
    public FileSummary add(final String name, final byte[] content) {
        FileEntity entity = new FileEntity();
        entity.setName(name);
        entity.setStatus(ProcessingStatus.IN_PROGRESS.getStorageValue());
        FileEntity result = fileStatusRepository.save(entity);
        LOGGER.debug("Added entity {} ", result);
        try {
            Files.write(Path.of(fileInputLocation, String.valueOf(result.getId())), content,
                    StandardOpenOption.CREATE);
        } catch (IOException e) {
            LOGGER.error("Error storing file {} ", name, e);
            throw new FileProcessingException(e.getMessage(), e);
        }

        return convertToSummary(result);
    }

    /**
     * gets the status of a file by identifier.
     * @param fileIdentifier , the unique identifier of the file in the system.
     * @return the processing status of the file. 
     */
    public String getStatus(final Long fileIdentifier) {
        Optional<FileEntity> result = fileStatusRepository.findById(fileIdentifier);

        if (result.isPresent()) {
            return ProcessingStatus.forStorageValue(result.get().getStatus()).getDisplayValue();
        }

        return null;
    }

    /**
     * update the status of a file by identifier.
     * @param fileIdentifier , the unique identifier of the file in the system.
     * @param status, the processing status
     * @return the processing status of the file. 
     */
    public void updateStatus(final Long fileIdentifier, final ProcessingStatus status) {
        FileEntity entity = new FileEntity();
        entity.setId(fileIdentifier);
        entity.setStatus(status.getStorageValue());

        Optional<FileEntity> result = fileStatusRepository.findById(fileIdentifier);

        if (result.isPresent()) {
            entity.setName(result.get().getName());
        }
        fileStatusRepository.save(entity);
        LOGGER.debug("Saved entity {} ", entity);

    }

    /**
     * Fetches the summary of all the files uploaded in the system.
     * @return the list of file summary.
     */
    public List<FileSummary> getAllSummary() {
        
        LOGGER.debug("Found total of {} records", fileStatusRepository.count());
        
        List<FileSummary> summaries = StreamSupport.stream(fileStatusRepository.findAll().spliterator(), false)
                .map(e -> convertToSummary(e)).collect(Collectors.toList());

        return summaries;
    }

    /**
     * Fetches the processed file result.
     * @return the list of file summary.
     */
    public Map<String, Integer> getResult(final Long fileIdentifier) {

        File file = new File(processedFileLocation + fileIdentifier);
        Map<String, Integer> result = null;
        try {
            String data = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            result = (Map<String, Integer>) OBJECT_MAPPER.readValue(data, Map.class);
        } catch (FileNotFoundException e) {
            LOGGER.error("File Not found for id [{}] ", fileIdentifier);
        }
        catch (IOException e) {
            LOGGER.error("Error retrieving file for id [{}] ", fileIdentifier, e);
            throw new FileProcessingException(e.getMessage(), e);
        }

        return result;
    }

    private FileSummary convertToSummary(final FileEntity entity) {
        FileSummary summary = new FileSummary();
        if (entity != null) {
            summary.setId(entity.getId());
            summary.setName(entity.getName());
            summary.setStatus(ProcessingStatus.forStorageValue(entity.getStatus()).getDisplayValue());
        }
        return summary;
    }

}
