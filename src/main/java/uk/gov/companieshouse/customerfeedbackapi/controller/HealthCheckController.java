package uk.gov.companieshouse.customerfeedbackapi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @GetMapping("/customer-feedback/healthcheck")
  public ResponseEntity<String> healthcheck() {
    return ResponseEntity.ok("Customer Feedback API Service is healthy");
  }
}
