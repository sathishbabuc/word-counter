/*
 * Copyright information here.
 */
package com.informatica.word.counter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.informatica.word.counter.dto.FileSummary;
import com.informatica.word.counter.exception.FileProcessingException;
import com.informatica.word.counter.service.FileProcessingService;

/**
 * Web MVC test for REST endpoints.
 **/
@WebMvcTest(controllers = { RESTController.class })
public class RESTControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FileProcessingService service;

    @Nested
    class GetStatus {
        @Test
        void notFound() throws Exception {
            when(service.getStatus(123L)).thenReturn(null);
            mvc.perform(get("/word-counter/status/123")).andExpect(status().isNotFound())
                    .andExpect(content().string("Requested detail not found."));
        }

        @Test
        void success() throws Exception {
            when(service.getStatus(123L)).thenReturn("Success");
            mvc.perform(get("/word-counter/status/123")).andExpect(status().isOk())
                    .andExpect(content().string("Success"));
        }
    }

    @Nested
    class GetResult {
        @Test
        void notFound() throws Exception {
            when(service.getResult(333L)).thenReturn(null);
            mvc.perform(get("/word-counter/result/333")).andExpect(status().isNotFound())
                    .andExpect(content().string("Requested detail not found."));
        }

        @Test
        void success() throws Exception {
            Map<String, Integer> expected = new HashMap<>();
            expected.put("test", 5);
            when(service.getResult(333L)).thenReturn(expected);
            mvc.perform(get("/word-counter/result/333")).andExpect(status().isOk())
                    .andExpect(content().string("{\"test\":5}"));
        }
    }

    @Nested
    class UploadFile {
        @Test
        void success() throws Exception {

            FileSummary expectedSummary = new FileSummary();
            expectedSummary.setId(555L);
            expectedSummary.setName("TEST_FILE.txt");
            expectedSummary.setStatus("In Progress");

            MockMultipartFile file = new MockMultipartFile("file", "TEST_FILE.txt", MediaType.MULTIPART_FORM_DATA_VALUE,
                    "some text".getBytes());

            when(service.add(any(String.class), any(byte[].class))).thenReturn(expectedSummary);

            mvc.perform(MockMvcRequestBuilders.multipart("/word-counter/upload/").file(file))
                    .andExpect(status().isOk())
                    .andExpect(content().string("{\"id\":555,\"name\":\"TEST_FILE.txt\",\"status\":\"In Progress\"}"));

        }
        
        @Test
        void failure() throws Exception {

            MockMultipartFile file = new MockMultipartFile("file", "TEST_FILE.txt", MediaType.MULTIPART_FORM_DATA_VALUE,
                    "some text".getBytes());

            when(service.add(any(String.class), any(byte[].class))).thenThrow(new FileProcessingException("Oops. Failed."));

            mvc.perform(MockMvcRequestBuilders.multipart("/word-counter/upload/").file(file))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("Unknown Error. Contact system Adminsitrator"));

        }

    }

}
