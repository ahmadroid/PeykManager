package ir.ahmadandroid.mapproject.model.responserequest;

import ir.ahmadandroid.mapproject.model.Person;

public class PersonToken {

    private String token;
    private Person person;

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
