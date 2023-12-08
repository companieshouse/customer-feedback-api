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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.chskafka.SendEmail;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateSendEmailPost;
import uk.gov.companieshouse.customerfeedbackapi.api.ApiClientService;
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
  private final ApiClientService apiClientService;

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
          CustomerFeedbackRepository customerFeedbackRepository, ApiClientService apiClientService) {
    this.customerFeedbackMapper = customerFeedbackMapper;
    this.customerFeedbackRepository = customerFeedbackRepository;
    this.apiClientService = apiClientService;
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

    ApiLogger.debugContext(requestId, "TRK 1");


    if (emailSent) {
      ApiLogger.debugContext(requestId, "TRK 2");
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
      ApiLogger.debugContext(requestId, "Calling send-email endpoint: " + kafkaApiEndpoint);
      try {
        // connection = (HttpURLConnection) new URL(kafkaApiEndpoint).openConnection();
        connection = (HttpURLConnection) new URL("http://e3026301-81ad-4942-b46f-5d7bae841463.mock.pstmn.io/send-email").openConnection();
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
        // throw new SendEmailException("Error sending customer feedback email: " + ex.toString());
        ApiLogger.debugContext(requestId, "TRK ignore error for now: " + ex.toString());
      } finally {
        if (connection != null) {
          connection.disconnect();
        }
      }
      System.err.println("NSDBG kafkaApiEndpoint: "+kafkaApiEndpoint);
      // http://chs-kafka-api:4081/send-email
      ApiLogger.debugContext(requestId, "TRK 3 - starting SDK");
      SendEmail sendEmail = new SendEmail();
      // SendEmail(String appId, String messageId, String messageType, String jsonData)
      ApiLogger.debugContext(requestId, "TRK 4");
      sendEmail.setAppId( appId );
      sendEmail.setMessageId( UUID.randomUUID().toString() );
      sendEmail.setMessageType( "customer-feedback" );
      json_data.put("SDK", "SDK submission");
      sendEmail.setJsonData( json_data.toString() );
      ApiLogger.debugContext(requestId, "TRK 5");
      InternalApiClient internalApiClient = apiClientService.getInternalApiClient();
      ApiLogger.debugContext(requestId, "TRK 6");
      // internalApiClient.setBasePath("http://chs-kafka-api:4081");
      internalApiClient.setBasePath("http://e3026301-81ad-4942-b46f-5d7bae841463.mock.pstmn.io");
      ApiLogger.debugContext(requestId, "TRK 7 sendEmail: " + sendEmail);
      String uri = "/send-email";
      // String uri = "/send-email?app_id="+appId;
      // String uri = "/send-email?"+requestBody;
      PrivateSendEmailPost sendEmailPost = internalApiClient.sendEmailHandler().postSendEmail(uri,sendEmail);
      ApiLogger.debugContext(requestId, "TRK 7.1 sendEmailPost: " + sendEmailPost);
      try {
          sendEmailPost.execute();
      } catch (ApiErrorResponseException ex) {
          ApiLogger.debugContext(requestId, "TRK 8 ignore error for now: " + ex.toString());
          HttpStatus statusCode = HttpStatus.valueOf(ex.getStatusCode());
          if (!statusCode.is2xxSuccessful()) {
              ApiLogger.debug("TRK 9.1 Unsuccessful call to endpoint" + ex);
              //throw new ServiceUnavailableException(ex.getMessage());
          } else {
              ApiLogger.debug("TRK 9.2 Error occurred while calling endpoint" + ex);
              //throw new RuntimeException(ex);
          }
      }
      ApiLogger.debugContext(requestId, "TRK A");
      ApiLogger.debugContext(requestId, "TRK B");
      ApiLogger.debugContext(requestId, "TRK Z");
    }
  }
}
