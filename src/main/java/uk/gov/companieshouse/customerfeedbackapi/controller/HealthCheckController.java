package uk.gov.companieshouse.customerfeedbackapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @GetMapping("/customer-feedback/healthcheck")
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public ResponseEntity<String> healthcheck() {
    return new ResponseEntity("Customer Feedback API Service is healthy", HttpStatus.OK);
  }
}
