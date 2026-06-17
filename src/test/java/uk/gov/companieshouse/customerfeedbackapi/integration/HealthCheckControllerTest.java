package uk.gov.companieshouse.customerfeedbackapi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.companieshouse.customerfeedbackapi.controller.HealthCheckController;

@WebMvcTest(HealthCheckController.class)
class HealthCheckControllerTest {
    @Autowired
    private MockMvc mvc;

    @Test
    void HealthCheckEndpointTest() throws Exception {
        this.mvc
                .perform(get("/customer-feedback/healthcheck"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer Feedback API Service is healthy"));
    }
}
