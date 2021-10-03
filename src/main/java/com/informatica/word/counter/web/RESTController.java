/*
 * Copyright information here.
 */
package com.informatica.word.counter.web;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.informatica.word.counter.dto.FileSummary;
import com.informatica.word.counter.exception.FileProcessingException;
import com.informatica.word.counter.service.FileProcessingService;

import io.swagger.v3.oas.annotations.Operation;

/**
 * Exposes REST Endpoint for all the user actions.
 **/
@RestController
@RequestMapping("/word-counter")
@Validated
public class RESTController {

    /** logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RESTController.class);

    @Autowired
    private FileProcessingService service;

    @Operation(summary = "Endpoint to upload a file for processing.")
    @PostMapping(value = "/upload/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<FileSummary> upload(@RequestParam("file") MultipartFile file) throws IOException {

        LOGGER.debug("File uploaded for processing with name - {} ", file.getOriginalFilename());

        FileSummary result = service.add(file.getOriginalFilename(), file.getBytes());

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "Retrieves the analysis result of the file by id.")
    @GetMapping(value = "/result/{id}")
    public ResponseEntity<Object> result(@PathVariable("id") final @Valid Long id) {

        LOGGER.debug("Result Requested for file id - {} ", id);

        Map<String, Integer> result = service.getResult(id);

        if (result != null) {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested detail not found.");

    }

    @Operation(summary = "Retrieves the processing status of the file by id.")
    @GetMapping(value = "/status/{id}")
    public ResponseEntity<String> status(@PathVariable("id") final @Valid Long id) {

        LOGGER.debug("Status Requested for file id - {} ", id);

        String status = service.getStatus(id);
        if (status != null) {
            return ResponseEntity.status(HttpStatus.OK).body(status);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Requested detail not found.");
    }

    @Operation(summary = "Retrieves the processing summary of the all files.")
    @GetMapping(value = "/status/all")
    public ResponseEntity<List<FileSummary>> getAll() {

        LOGGER.debug("Result summary of all the uploaded files requested.");

        return ResponseEntity.status(HttpStatus.OK).body(service.getAllSummary());
    }

    @ExceptionHandler({ FileProcessingException.class, Exception.class })
    public ResponseEntity<String> handleUnknownException(final FileProcessingException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Unknown Error. Contact system Adminsitrator");
    }
}
