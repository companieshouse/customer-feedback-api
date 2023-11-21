package uk.gov.companieshouse.customerfeedbackapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CustomerFeedbackApiApplication {

  public static final String CFAPI_APP_NAMESPACE = "customer-feedback-api";

  public static void main(String[] args) {
    SpringApplication.run(CustomerFeedbackApiApplication.class, args);
  }
}
