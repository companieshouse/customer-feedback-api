package uk.gov.companieshouse.customerfeedbackapi.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.customerfeedbackapi.exception.SendEmailException;
import uk.gov.companieshouse.customerfeedbackapi.mapper.CustomerFeedbackMapper;
import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;
import uk.gov.companieshouse.customerfeedbackapi.repository.CustomerFeedbackRepository;
import uk.gov.companieshouse.customerfeedbackapi.utils.ApiLogger;

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
    customerFeedbackRepository.insert(customerFeedbackDAO);

    if (emailSent) {
      JSONObject jsonData = new JSONObject();
      jsonData.put("customer_feedback", customerFeedbackDTO.getCustomerFeedback());
      jsonData.put("customer_name", customerFeedbackDTO.getCustomerName());
      jsonData.put("customer_email", customerFeedbackDTO.getCustomerEmail());
      jsonData.put("source_url", customerFeedbackDTO.getSourceUrl());
      jsonData.put("kind", customerFeedbackDTO.getKind());
      jsonData.put("to", customerFeedbackEmail);
      jsonData.put("date", JSONObject.NULL);
      String requestBody =
          "app_id="
              + appId
              + "&"
              + "message_id="
              + UUID.randomUUID().toString()
              + "&"
              + "message_type="
              + "customer-feedback"
              + "&"
              + "json_data="
              + jsonData.toString();
      HttpURLConnection connection = null;
      ApiLogger.debugContext(requestId, "Calling send-email endpoint: " + kafkaApiEndpoint);
      try {
        connection = (HttpURLConnection) new URI(kafkaApiEndpoint).toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
          out.write(requestBody.getBytes(StandardCharsets.UTF_8));
          out.flush();
        }
        int responseCode = connection.getResponseCode();
        ApiLogger.debugContext(requestId, "Response code from endpoint: " + responseCode);
      } catch (IOException | URISyntaxException | IllegalArgumentException ex) {
        throw new SendEmailException("Error sending customer feedback email: " + ex.toString());
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }
  }
}
