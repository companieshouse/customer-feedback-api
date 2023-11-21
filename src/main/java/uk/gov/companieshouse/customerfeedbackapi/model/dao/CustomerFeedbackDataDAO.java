package uk.gov.companieshouse.customerfeedbackapi.model.dao;

import org.springframework.data.mongodb.core.mapping.Field;

public class CustomerFeedbackDataDAO {

  @Field("customer_email")
  private String customerEmail;

  @Field("customer_feedback")
  private String customerFeedback;

  @Field("customer_name")
  private String customerName;

  @Field("kind")
  private String kind;

  @Field("source_url")
  private String sourceUrl;

  public String getCustomerEmail() {
    return customerEmail;
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
    return customerName;
  }

  public void setCustomerName(String customerName) {
    this.customerName = customerName;
  }

  public String getKind() {
    return kind;
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
