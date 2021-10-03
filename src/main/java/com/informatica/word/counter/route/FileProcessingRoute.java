/*
 * Copyright information here.
 */
package com.informatica.word.counter.route;

import org.apache.camel.ErrorHandlerFactory;
import org.apache.camel.LoggingLevel;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.informatica.word.counter.dto.ProcessingStatus;
import com.informatica.word.counter.processor.WordFrequencyProcessor;
import com.informatica.word.counter.service.FileProcessingService;

/** 
 * Camel route which consumes a file for processing and writes the result to an output file.
**/
@Component
public class FileProcessingRoute extends RouteBuilder {

    /** logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileProcessingRoute.class);

    @Autowired
    private WordFrequencyProcessor wordFrequencyProcessor;

    @Autowired
    private FileProcessingService service;

    @Value("${file.location.input:}")
    private String inputEndpoint;

    @Value("${file.location.processed:}")
    private String outputEndpoint;
    
    @Value("${file.location.deadletter:}")
    private String deadLetterEndpoint;
    
    @Value("${file.processing.threads.start.size:1}")
    private int poolSize;
    
    @Value("${file.processing.threads.max.size:3}")
    private int maxPoolsize;

    @Override
    public void configure() throws Exception {
        
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Listening for input files from {} and result will be written to {}", inputEndpoint, outputEndpoint);
        }
        
        final String logName = getClass().getName();

        from("file:" + inputEndpoint + "?readLock=markerFile")
                .process(exchange -> exchange.setProperty("START_TIME", System.currentTimeMillis()))
                .errorHandler(handlerError())
                .threads(poolSize, maxPoolsize)
                .log(LoggingLevel.DEBUG, logName, "Received file with identifier - ${header.CamelFileName} ")
                .process(wordFrequencyProcessor)
                .to("file:" + outputEndpoint)
                .process(saveStatus())
                .process(exchange -> exchange.setProperty("PROCESSING_TIME",
                        System.currentTimeMillis() - (Long) exchange.getProperty("START_TIME")))
                .log(LoggingLevel.INFO, "performance",
                        "Total processing time for file with identifier ${header.CamelFileName} : ${exchangeProperty.PROCESSING_TIME}ms");
    }

    private ErrorHandlerFactory handlerError() {

        Processor exceptionLoggingProcessor = (exchange -> {
            Long fileId = exchange.getIn().getHeader("CamelFileName", Long.class);
            LOGGER.error("Sending file with id {} to DeadLetter EndPoint", fileId, exchange.getException());
            service.updateStatus(fileId, ProcessingStatus.FAILURE);
        });

        return deadLetterChannel("file:" + deadLetterEndpoint ).useOriginalMessage()
                .onExceptionOccurred(exceptionLoggingProcessor);

    }

    private Processor saveStatus() {
        return exchange -> service.updateStatus(exchange.getIn().getHeader("CamelFileName", Long.class),
                ProcessingStatus.SUCCESS);
    }

}
