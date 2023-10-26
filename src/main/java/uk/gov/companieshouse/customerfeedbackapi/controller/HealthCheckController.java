package uk.gov.companieshouse.customerfeedbackapi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @GetMapping("/customer-feedback/healthcheck")
  public String healthcheck() {
    return "Customer Feedback API Service is healthy";
  }
}
