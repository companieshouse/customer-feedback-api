package uk.gov.companieshouse.customerfeedbackapi.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CustomerFeedbackDTO {
  @Size(max = 100, message = "customer_email must not exceed 100 characters")
  @JsonProperty("customer_email")
  private String customerEmail;

  @NotBlank(message = "customer_feedback is required")
  @JsonProperty("customer_feedback")
  private String customerFeedback;

  @Size(max = 100, message = "customer_name must not exceed 100 characters")
  @JsonProperty("customer_name")
  private String customerName;

  @Pattern(regexp = "^feedback$", message = "kind must be 'feedback'")
  @JsonProperty("kind")
  private String kind;

  @JsonProperty("source_url")
  private String sourceUrl;

  public String getCustomerEmail() {
    return customerEmail != null && customerEmail.length() > 0 ? customerEmail : "(not provided)";
  }

  public void setCustomerEmail(String customerEmail) {
    this.customerEmail = customerEmail;
  }

  public String getCustomerFeedback() {
    return customerFeedback;
  }

  public void setCustomerFeedback(String customerFeedback) {
    this.customerFeedback = customerFeedback;
  }

  public String getCustomerName() {
    return customerName != null && customerName.length() > 0 ? customerName : "(not provided)";
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getKind() {
    return kind != null && kind.length() > 0 ? kind : "feedback";
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getSourceUrl() {
    return sourceUrl;
  }

  public void setSourceUrl(String sourceUrl) {
    this.sourceUrl = sourceUrl;
  }
}
