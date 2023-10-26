package uk.gov.companieshouse.customerfeedbackapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
// import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDAO;

@Repository
public interface CustomerFeedbackRepository extends MongoRepository<CustomerFeedbackDAO, String> {

  // @Query("{transaction_id:'?0'}")
  // CustomerFeedbackDAO findByTransactionId(String transactionId);
}
