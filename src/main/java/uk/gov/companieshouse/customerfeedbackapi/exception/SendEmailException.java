package uk.gov.companieshouse.customerfeedbackapi.exception;

public class SendEmailException extends Exception {
  public SendEmailException(String message, Throwable cause) {
    super(message, cause);
  }

  public SendEmailException(String message) {
    super(message);
  }
}
