package uk.gov.companieshouse.customerfeedbackapi.model.dao;

import java.time.LocalDateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "customer_feedback")
public class CustomerFeedbackDAO {

  @Id private String id;

  @Field("data")
  private CustomerFeedbackDataDAO data;

  @Field("created_at")
  private LocalDateTime createdAt;

  @Field("email_sent")
  private boolean emailSent;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public CustomerFeedbackDataDAO getData() {
    return data;
  }

  public void setData(CustomerFeedbackDataDAO data) {
    this.data = data;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public boolean getEmailSent() {
    return emailSent;
  }

  public void setEmailSent(boolean emailSent) {
    this.emailSent = emailSent;
  }
}
