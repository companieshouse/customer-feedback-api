package uk.gov.companieshouse.customerfeedbackapi.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDAO;
import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;
import uk.gov.companieshouse.customerfeedbackapi.repository.CustomerFeedbackRepository;
import uk.gov.companieshouse.customerfeedbackapi.utils.Helper;

@SpringBootTest(properties="send-email-flag=false")
@AutoConfigureMockMvc
class CustomerFeedbackControllerIntegrationTest {

  Helper helper = new Helper();

  @Autowired private MockMvc mvc;

  @MockitoBean protected CustomerFeedbackRepository customerFeedbackRepository;

  @Test
  void testCreateCustomerFeedbackSuccessTest() throws Exception {
    String email = "Test@Test.com";
    String feedback = "Something went wrong";
    String name = "A User";
    String kind = "feedback";
    String sourceUrl = "http://chs.local";
    LocalDateTime createdAt = LocalDateTime.now();
    boolean sentEmail = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            email, feedback, name, kind, sourceUrl, createdAt, sentEmail);

    when(customerFeedbackRepository.insert(any(CustomerFeedbackDAO.class)))
        .thenReturn(customerFeedbackDAO);

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
    LocalDateTime createdAt = LocalDateTime.now();
    boolean sentEmail = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            email, feedback, name, kind, sourceUrl, createdAt, sentEmail);

    when(customerFeedbackRepository.insert(any(CustomerFeedbackDAO.class)))
        .thenReturn(customerFeedbackDAO);

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
    LocalDateTime createdAt = LocalDateTime.now();
    boolean sentEmail = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            email, feedback, name, kind, sourceUrl, createdAt, sentEmail);

    when(customerFeedbackRepository.insert(any(CustomerFeedbackDAO.class)))
        .thenReturn(customerFeedbackDAO);

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
    LocalDateTime createdAt = LocalDateTime.now();
    boolean sentEmail = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(email, feedback, name, kind, sourceUrl);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            email, feedback, name, kind, sourceUrl, createdAt, sentEmail);

    when(customerFeedbackRepository.insert(any(CustomerFeedbackDAO.class)))
        .thenReturn(customerFeedbackDAO);

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
