package ir.ahmadandroid.mapproject.model.responserequest;

import ir.ahmadandroid.mapproject.model.ApiKey;

public class NationalCodeApiKey {

    private String nationalCode;
    private ApiKey apiKey;

    public NationalCodeApiKey() {
        this.apiKey = new ApiKey();
    }

    public ApiKey getApiKey() {
        return apiKey;
    }

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }
}
