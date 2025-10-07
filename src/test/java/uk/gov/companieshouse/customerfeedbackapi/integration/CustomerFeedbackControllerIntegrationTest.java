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
    String EMAIL = "Test@Test.com";
    String FEEDBACK = "Something went wrong";
    String NAME = "A User";
    String KIND = "feedback";
    String SOURCE_URL = "http://chs.local";
    LocalDateTime CREATED_AT = LocalDateTime.now();
    boolean SENT_EMAIL = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL, CREATED_AT, SENT_EMAIL);

    when(customerFeedbackRepository.insert(any(CustomerFeedbackDAO.class)))
        .thenReturn(customerFeedbackDAO);

    mvc.perform(
            post("/customer-feedback")
                .contentType("application/json")
                .header("ERIC-Identity", "123")
                .header("X-Request-Id", "123456")
                .content(helper.writeToJson(customerFeedbackDTO)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.customer_email").value(EMAIL))
        .andExpect(jsonPath("$.customer_feedback").value(FEEDBACK))
        .andExpect(jsonPath("$.customer_name").value(NAME))
        .andExpect(jsonPath("$.kind").value(KIND))
        .andExpect(jsonPath("$.source_url").value(SOURCE_URL));
  }

  @Test
  void testCreateCustomerFeedbackEmptyFeedbackFailureTest() throws Exception {
    String EMAIL = "test@test.com";
    String FEEDBACK = "";
    String NAME = "A User";
    String KIND = "feedback";
    String SOURCE_URL = "http://chs.local";
    LocalDateTime CREATED_AT = LocalDateTime.now();
    boolean SENT_EMAIL = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL, CREATED_AT, SENT_EMAIL);

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
    String EMAIL =
        "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890@test.com";
    String FEEDBACK = "Something went wrong";
    String NAME = "A User";
    String KIND = "feedback";
    String SOURCE_URL = "http://chs.local";
    LocalDateTime CREATED_AT = LocalDateTime.now();
    boolean SENT_EMAIL = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL, CREATED_AT, SENT_EMAIL);

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
    String EMAIL = "test@test.com";
    String FEEDBACK = "The wrong kind of kind";
    String NAME = "A User";
    String KIND = "duckfeed";
    String SOURCE_URL = "http://chs.local";
    LocalDateTime CREATED_AT = LocalDateTime.now();
    boolean SENT_EMAIL = true;
    CustomerFeedbackDTO customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL);
    CustomerFeedbackDAO customerFeedbackDAO =
        helper.generateCustomerFeedbackDAO(
            EMAIL, FEEDBACK, NAME, KIND, SOURCE_URL, CREATED_AT, SENT_EMAIL);

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
