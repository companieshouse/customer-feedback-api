package uk.gov.companieshouse.customerfeedbackapi.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import uk.gov.companieshouse.customerfeedbackapi.controller.CustomerFeedbackController;
import uk.gov.companieshouse.customerfeedbackapi.exception.*;
import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;
import uk.gov.companieshouse.customerfeedbackapi.service.CustomerFeedbackService;
import uk.gov.companieshouse.customerfeedbackapi.utils.Helper;

@ExtendWith(MockitoExtension.class)
class CustomerFeedbackControllerTest {

  Helper helper = new Helper();

  private CustomerFeedbackDTO customerFeedbackDTO;

  private static final String REQUEST_ID = UUID.randomUUID().toString();
  private static final String EMAIL_ADDRESS = "Test@Test.com";
  private static final String FEEDBACK = "Something went wrong";
  private static final String NAME = "A User";
  private static final String KIND = "feedback";
  private static final String SOURCE_URL = "http://chs.local";

  @Mock private CustomerFeedbackService customerFeedbackService;

  @InjectMocks private CustomerFeedbackController customerFeedbackController;

  @BeforeEach
  void init() {
    customerFeedbackDTO =
        helper.generateCustomerFeedbackDTO(EMAIL_ADDRESS, FEEDBACK, NAME, KIND, SOURCE_URL);
  }

  @Test
  void testCreateCustomerFeedbackSuccessTest() throws SendEmailException {

    var feedbackResponse = customerFeedbackController.feedback(customerFeedbackDTO, REQUEST_ID);

    assertEquals(HttpStatus.CREATED.value(), feedbackResponse.getStatusCode().value());
    assertEquals(customerFeedbackDTO, feedbackResponse.getBody());

    verify(customerFeedbackService).createCustomerFeedback(customerFeedbackDTO, REQUEST_ID);
  }
}
