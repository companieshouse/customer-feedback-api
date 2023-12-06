package uk.gov.companieshouse.customerfeedbackapi.api.impl;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.customerfeedbackapi.api.ApiClientService;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Component
public class ApiClientServiceImpl implements ApiClientService {

    public InternalApiClient getInternalApiClient() {
        return ApiSdkManager.getPrivateSDK();
    }
}
