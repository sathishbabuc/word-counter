/*
 * Copyright information here.
 */
package com.informatica.word.counter.processor;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.camel.Exchange;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * JUnit test class for Word frequency calculator.
 **/
public class WordFrequencyProcessorTest extends CamelTestSupport {

    private WordFrequencyProcessor processor = new WordFrequencyProcessor();

    @Test
    void nullExchange() {
        assertThrows(NullPointerException.class, () -> processor.process(null));
    }

    @Test
    void withValidText() throws Exception {
        Exchange exchange = createExchangeWithBody(
                "Mass Ingestion Databases can ingest data at scale from common relational databases.");

        processor.process(exchange);

        String output = exchange.getMessage().getBody(String.class);

        JSONAssert.assertEquals(
                "{ \"databases\" : 2, \"at\" : 1, \"can\" : 1, \"common\" : 1, \"data\" : 1, \"from\" : 1, \"ingest\" : 1, \"ingestion\" : 1, \"mass\" : 1, \"relational\" : 1, \"scale\" : 1 }",
                output, true);

    }

}
