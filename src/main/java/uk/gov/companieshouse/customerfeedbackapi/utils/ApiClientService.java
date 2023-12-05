package uk.gov.companieshouse.customerfeedbackapi.utils;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiClientManager;

@Component
public class ApiClientService {

    public InternalApiClient getPrivateApiClient() {
        return ApiClientManager.getPrivateSDK();
    }
}
