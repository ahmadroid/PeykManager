package ir.ahmadandroid.mapproject.model.responserequest;

import java.util.List;

import ir.ahmadandroid.mapproject.model.DriverLatitudeLongitude;

public class DriverLatLongListToken {

    private List<DriverLatitudeLongitude> latLongList;
    private String token;

    public List<DriverLatitudeLongitude> getLatLongList() {
        return latLongList;
    }

    public void setLatLongList(List<DriverLatitudeLongitude> latLongList) {
        this.latLongList = latLongList;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
