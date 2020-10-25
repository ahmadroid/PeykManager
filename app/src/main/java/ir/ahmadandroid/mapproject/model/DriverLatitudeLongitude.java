package ir.ahmadandroid.mapproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DriverLatitudeLongitude implements Parcelable {

    private int id;
    private int identifyCode;
    private double latitude;
    private double longitude;

    public DriverLatitudeLongitude() {
    }

    public DriverLatitudeLongitude(int id, int identifyCode, double latitude, double longitude) {
        this.id = id;
        this.identifyCode = identifyCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected DriverLatitudeLongitude(Parcel in) {
        id = in.readInt();
        identifyCode = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdentifyCode() {
        return identifyCode;
    }

    public void setIdentifyCode(int identifyCode) {
        this.identifyCode = identifyCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(identifyCode);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Creator<DriverLatitudeLongitude> CREATOR = new Creator<DriverLatitudeLongitude>() {
        @Override
        public DriverLatitudeLongitude createFromParcel(Parcel in) {
            return new DriverLatitudeLongitude(in);
        }

        @Override
        public DriverLatitudeLongitude[] newArray(int size) {
            return new DriverLatitudeLongitude[size];
        }
    };
}
