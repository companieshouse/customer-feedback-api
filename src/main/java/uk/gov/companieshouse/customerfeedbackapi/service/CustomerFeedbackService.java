package uk.gov.companieshouse.customerfeedbackapi.service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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
    CustomerFeedbackDAO createdCustomerFeedback =
        customerFeedbackRepository.insert(customerFeedbackDAO);

    if (emailSent) {
      JSONObject json_data = new JSONObject();
      json_data.put("customer_feedback", customerFeedbackDTO.getCustomerFeedback());
      json_data.put("customer_name", customerFeedbackDTO.getCustomerName());
      json_data.put("customer_email", customerFeedbackDTO.getCustomerEmail());
      json_data.put("source_url", customerFeedbackDTO.getSourceUrl());
      json_data.put("kind", customerFeedbackDTO.getKind());
      json_data.put("to", customerFeedbackEmail);
      json_data.put("date", JSONObject.NULL);
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
              + json_data.toString();
      HttpURLConnection connection = null;
      ApiLogger.debugContext(requestId, "Calling send-email endpoint");
      try {
        connection = (HttpURLConnection) new URL(kafkaApiEndpoint).openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
          out.write(requestBody.getBytes(StandardCharsets.UTF_8));
          out.flush();
        }
        int responseCode = connection.getResponseCode();
        ApiLogger.debugContext(requestId, "Response code from endpoint: " + responseCode);
      } catch (IOException ex) {
        throw new SendEmailException("Error sending customer feedback email: " + ex.toString());
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
    }
  }
}
