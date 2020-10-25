package ir.ahmadandroid.mapproject.model.responserequest;

import ir.ahmadandroid.mapproject.model.ApiKey;

public class NationalCodePsApiKey {

    private String nationalCode;
    private String pass;
    private ApiKey apiKey;


    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }


    public NationalCodePsApiKey() {
        this.apiKey = new ApiKey();
    }


}
