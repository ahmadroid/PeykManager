package ir.ahmadandroid.mapproject.model.responserequest;

import ir.ahmadandroid.mapproject.model.ApiKey;
import ir.ahmadandroid.mapproject.model.Person;

public class PersonNationalCodeApiKey {

    private Person person;
    private ApiKey apiKey;
    private String nationalCode;

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public PersonNationalCodeApiKey() {
        this.apiKey = new ApiKey();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}
