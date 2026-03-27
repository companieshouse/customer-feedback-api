package uk.gov.companieshouse.customerfeedbackapi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;
import uk.gov.companieshouse.customerfeedbackapi.utils.Helper;

@SpringBootTest(
        properties = "send-email-flag=false",
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@Testcontainers
class CustomerFeedbackControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.5");

    @Autowired
    private MockMvc mvc;

    Helper helper = new Helper();

    @Test
    void testCreateCustomerFeedbackSuccessTest() throws Exception {
        String email = "Test@Test.com";
        String feedback = "Something went wrong";
        String name = "A User";
        String kind = "feedback";
        String sourceUrl = "http://chs.local";
        CustomerFeedbackDTO customerFeedbackDTO =
                helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);

        mvc.perform(
                        post("/customer-feedback")
                                .contentType("application/json")
                                .header("ERIC-Identity", "123")
                                .header("X-Request-Id", "123456")
                                .content(helper.writeToJson(customerFeedbackDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customer_email").value(email))
                .andExpect(jsonPath("$.customer_feedback").value(feedback))
                .andExpect(jsonPath("$.customer_name").value(name))
                .andExpect(jsonPath("$.kind").value(kind))
                .andExpect(jsonPath("$.source_url").value(sourceUrl));
    }

    @Test
    void testCreateCustomerFeedbackEmptyFeedbackFailureTest() throws Exception {
        String email = "test@test.com";
        String feedback = "";
        String name = "A User";
        String kind = "feedback";
        String sourceUrl = "http://chs.local";
        CustomerFeedbackDTO customerFeedbackDTO =
                helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);

        mvc.perform(
                        post("/customer-feedback")
                                .contentType("application/json")
                                .header("ERIC-Identity", "123")
                                .header("X-Request-Id", "123456")
                                .content(helper.writeToJson(customerFeedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("customer_feedback is required"));
    }

    @Test
    void testCreateCustomerFeedbackEmailLengthFailureTest() throws Exception {
        String email =
                "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890@test.com";
        String feedback = "Something went wrong";
        String name = "A User";
        String kind = "feedback";
        String sourceUrl = "http://chs.local";
        CustomerFeedbackDTO customerFeedbackDTO =
                helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);

        mvc.perform(
                        post("/customer-feedback")
                                .contentType("application/json")
                                .header("ERIC-Identity", "123")
                                .header("X-Request-Id", "123456")
                                .content(helper.writeToJson(customerFeedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("customer_email must not exceed 100 characters"));
    }

    @Test
    void testCreateCustomerFeedbackWrongKindFailureTest() throws Exception {
        String email = "test@test.com";
        String feedback = "The wrong kind of kind";
        String name = "A User";
        String kind = "duckfeed";
        String sourceUrl = "http://chs.local";
        CustomerFeedbackDTO customerFeedbackDTO =
                helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);

        mvc.perform(
                        post("/customer-feedback")
                                .contentType("application/json")
                                .header("ERIC-Identity", "123")
                                .header("X-Request-Id", "123456")
                                .content(helper.writeToJson(customerFeedbackDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0]").value("kind must be 'feedback'"));
    }
}