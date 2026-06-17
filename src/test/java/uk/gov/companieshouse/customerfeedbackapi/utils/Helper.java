package uk.gov.companieshouse.customerfeedbackapi.utils;

import java.time.LocalDateTime;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectWriter;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDAO;
import uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDataDAO;
import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;

public class Helper {

    public CustomerFeedbackDTO generateCustomerFeedbackDTO(
            String customerEmail,
            String customerFeedback,
            String customerName,
            String kind,
            String sourceUrl) {
        CustomerFeedbackDTO customerFeedbackDTO = new CustomerFeedbackDTO();

        customerFeedbackDTO.setCustomerEmail(customerEmail);
        customerFeedbackDTO.setCustomerFeedback(customerFeedback);
        customerFeedbackDTO.setCustomerName(customerName);
        customerFeedbackDTO.setKind(kind);
        customerFeedbackDTO.setSourceUrl(sourceUrl);
        return customerFeedbackDTO;
    }

    public CustomerFeedbackDAO generateCustomerFeedbackDAO(
            String customerEmail,
            String customerFeedback,
            String customerName,
            String kind,
            String sourceUrl,
            LocalDateTime createdAt,
            boolean emailSent) {
        CustomerFeedbackDAO customerFeedbackDAO = new CustomerFeedbackDAO();
        CustomerFeedbackDataDAO customerFeedbackDataDAO = new CustomerFeedbackDataDAO();

        customerFeedbackDataDAO.setCustomerEmail(customerEmail);
        customerFeedbackDataDAO.setCustomerFeedback(customerFeedback);
        customerFeedbackDataDAO.setCustomerName(customerName);
        customerFeedbackDataDAO.setKind(kind);
        customerFeedbackDataDAO.setSourceUrl(sourceUrl);
        customerFeedbackDAO.setData(customerFeedbackDataDAO);
        customerFeedbackDAO.setCreatedAt(createdAt);
        customerFeedbackDAO.setEmailSent(emailSent);
        return customerFeedbackDAO;
    }

    public String writeToJson(Object object) {
        ObjectMapper mapper = JsonMapper.builder()
                .disable(SerializationFeature.WRAP_ROOT_VALUE)
                .build();
        ObjectWriter ow = mapper.writer();
        return ow.writeValueAsString(object);
    }
}
