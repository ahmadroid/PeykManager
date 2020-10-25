package ir.ahmadandroid.mapproject.model.responserequest;

import java.util.ArrayList;
import java.util.List;

import ir.ahmadandroid.mapproject.model.Person;

public class PersonListToken {

    private List<Person> personList=new ArrayList<>();
    private String token;

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
