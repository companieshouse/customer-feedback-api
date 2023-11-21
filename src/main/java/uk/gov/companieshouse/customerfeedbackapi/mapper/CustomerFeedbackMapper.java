package uk.gov.companieshouse.customerfeedbackapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.customerfeedbackapi.model.dao.CustomerFeedbackDAO;
import uk.gov.companieshouse.customerfeedbackapi.model.dto.CustomerFeedbackDTO;

@Component
@Mapper(componentModel = "spring")
public interface CustomerFeedbackMapper {

  @Mapping(target = "data.customerEmail", source = "customerEmail")
  @Mapping(target = "data.customerFeedback", source = "customerFeedback")
  @Mapping(target = "data.customerName", source = "customerName")
  @Mapping(target = "data.kind", source = "kind")
  @Mapping(target = "data.sourceUrl", source = "sourceUrl")
  CustomerFeedbackDAO dtoToDao(CustomerFeedbackDTO customerFeedbackDTO);
}
