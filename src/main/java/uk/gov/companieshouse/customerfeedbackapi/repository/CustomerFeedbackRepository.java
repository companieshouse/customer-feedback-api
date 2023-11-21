package uk.gov.companieshouse.customerfeedbackapi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDAO;

@Repository
public interface CustomerFeedbackRepository extends MongoRepository<CustomerFeedbackDAO, String> {

}
