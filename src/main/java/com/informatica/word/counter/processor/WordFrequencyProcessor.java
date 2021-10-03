/*
 * Copyright information here.
 */
package com.informatica.word.counter.processor;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/** 
 * Word frequency calculator for a given content. 
**/
@Component
public class WordFrequencyProcessor implements Processor {

    private static final String EXCLUDED_CHARACTERS = "[^a-z0-9 -]";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Override
    public void process(final Exchange exchange) throws Exception {

        String result = exchange.getIn().getBody(String.class);

        Map<String, Long> wordCountMap = new HashMap<>();

        Stream.of(result.toLowerCase().replaceAll(EXCLUDED_CHARACTERS, " ").split("\\s+"))
                .collect(Collectors.groupingBy(k -> k, () -> wordCountMap, Collectors.counting()));

        final Map<String, Long> sortedWordCountMap = wordCountMap.entrySet().stream()
                .sorted((Map.Entry.comparingByKey()))
                .sorted((Map.Entry.<String, Long> comparingByValue().reversed()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        exchange.getIn().setBody(OBJECT_MAPPER.writeValueAsString(sortedWordCountMap));

    }

}
