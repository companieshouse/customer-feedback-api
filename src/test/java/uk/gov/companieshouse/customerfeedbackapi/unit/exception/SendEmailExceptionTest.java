package uk.gov.companieshouse.customerfeedbackapi.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.customerfeedbackapi.exception.SendEmailException;

@ExtendWith(MockitoExtension.class)
class SendEmailExceptionTest {

  @Test
  void testSendEmailException() {
    String msg = "message";
    var exception = new SendEmailException(msg);
    assertEquals(msg, exception.getMessage());
  }
}
