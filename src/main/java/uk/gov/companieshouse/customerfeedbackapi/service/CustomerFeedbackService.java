package uk.gov.companieshouse.customerfeedbackapi.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.customerfeedbackapi.exception.SendEmailException;
import uk.gov.companieshouse.customerfeedbackapi.mapper.CustomerFeedbackMapper;
import uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDAO;
import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;
import uk.gov.companieshouse.customerfeedbackapi.repository.CustomerFeedbackRepository;
import uk.gov.companieshouse.customerfeedbackapi.utils.ApiLogger;

import uk.gov.companieshouse.customerfeedbackapi.utils.ApiClientService;
import uk.gov.companieshouse.api.InternalApiClient;
// import uk.gov.companieshouse.sdk.manager.ApiClientManager;

@Service
public class CustomerFeedbackService {

  private final CustomerFeedbackMapper customerFeedbackMapper;
  private final CustomerFeedbackRepository customerFeedbackRepository;

  @Value("${send-email-flag}")
  private boolean emailSendFlag;

  @Value("${customer-feedback-email}")
  private String customerFeedbackEmail;

  @Value("${kafka-api-endpoint}")
  private String kafkaApiEndpoint;

  @Value("${app-id}")
  private String appId;

  @Autowired
  private ApiClientService apiClientService;

  @Autowired
  public CustomerFeedbackService(
      CustomerFeedbackMapper customerFeedbackMapper,
      CustomerFeedbackRepository customerFeedbackRepository) {
    this.customerFeedbackMapper = customerFeedbackMapper;
    this.customerFeedbackRepository = customerFeedbackRepository;
  }

  public void createCustomerFeedback(CustomerFeedbackDTO customerFeedbackDTO, String requestId)
      throws SendEmailException {

    ApiLogger.debugContext(requestId, "Processing customer feedback");

    var customerFeedbackDAO = customerFeedbackMapper.dtoToDao(customerFeedbackDTO);

    customerFeedbackDAO.setCreatedAt(LocalDateTime.now());

    boolean emailSent = emailSendFlag && !customerFeedbackDAO.getData().getSourceUrl().isBlank();
    customerFeedbackDAO.setEmailSent(emailSent);

    ApiLogger.debugContext(requestId, "Inserting customer feedback record");
    CustomerFeedbackDAO createdCustomerFeedback =
        customerFeedbackRepository.insert(customerFeedbackDAO);

    ApiLogger.debugContext(requestId, "TRK 5");
    // InternalApiClient internalApiClient = ApiClientManager.getPrivateSDK();
    InternalApiClient internalApiClient = apiClientService.getPrivateApiClient();
    ApiLogger.debugContext(requestId, "TRK 6");
  }
}
