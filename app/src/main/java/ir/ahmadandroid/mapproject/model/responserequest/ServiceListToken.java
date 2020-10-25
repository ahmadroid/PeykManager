package ir.ahmadandroid.mapproject.model.responserequest;

import java.util.ArrayList;
import java.util.List;

import ir.ahmadandroid.mapproject.model.Service;

public class ServiceListToken {

    private List<Service> serviceList=new ArrayList<>();
    private String token;

    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
