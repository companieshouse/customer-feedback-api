package uk.gov.companieshouse.customerfeedbackapi.controller;

import static uk.gov.companieshouse.customerfeedbackapi.utils.Constants.ERIC_REQUEST_ID_KEY;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.customerfeedbackapi.exception.SendEmailException;
import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;
import uk.gov.companieshouse.customerfeedbackapi.service.CustomerFeedbackService;
import uk.gov.companieshouse.customerfeedbackapi.utils.ApiLogger;

@RestController
public class CustomerFeedbackController {

  private final CustomerFeedbackService customerFeedbackService;

  @Autowired
  public CustomerFeedbackController(CustomerFeedbackService customerFeedbackService) {
    this.customerFeedbackService = customerFeedbackService;
  }

  @PostMapping("/customer-feedback")
  public ResponseEntity<CustomerFeedbackDTO> feedback(
      @Valid @RequestBody CustomerFeedbackDTO customerFeedbackDTO,
      @RequestHeader(value = ERIC_REQUEST_ID_KEY) String requestId)
      throws SendEmailException {

    ApiLogger.infoContext(requestId, "Customer feedback submitted");
    this.customerFeedbackService.createCustomerFeedback(customerFeedbackDTO, requestId);
    return ResponseEntity.status(HttpStatus.CREATED).body(customerFeedbackDTO);
  }
}
