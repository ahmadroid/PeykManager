package ir.ahmadandroid.mapproject.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Service implements Parcelable {

    private int id;
    private int identifyCode;
    private String sender;
    private String senderMobile;
    private String senderAddress;
    private String receiver;
    private String receiverMobile;
    private String receiverAddress;
    private int rentMount;
    private double latitude;
    private double longitude;
    private String state;
    private String sendDate;
    private String receiveDate;
    private String packet;

    public String getPacket() {
        return packet;
    }

    public void setPacket(String packet) {
        this.packet = packet;
    }

    public String getSendDate() {
        return sendDate;
    }

    public void setSendDate(String sendDate) {
        this.sendDate = sendDate;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
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

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderMobile() {
        return senderMobile;
    }

    public void setSenderMobile(String senderMobile) {
        this.senderMobile = senderMobile;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public int getRentMount() {
        return rentMount;
    }

    public void setRentMount(int rentMount) {
        this.rentMount = rentMount;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Service() {
    }

    public Service(int id, int identifyCode, String sender, String senderMobile, String senderAddress, String receiver, String receiverMobile, String receiverAddress, int rentMount, double latitude, double longitude, String state, String sendDate, String receiveDate,String packet) {

        this.id = id;
        this.identifyCode = identifyCode;
        this.sender = sender;
        this.senderMobile = senderMobile;
        this.senderAddress = senderAddress;
        this.receiver = receiver;
        this.receiverMobile = receiverMobile;
        this.receiverAddress = receiverAddress;
        this.rentMount = rentMount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
        this.sendDate = sendDate;
        this.receiveDate = receiveDate;
        this.packet=packet;
    }

    protected Service(Parcel in) {
        id = in.readInt();
        identifyCode = in.readInt();
        sender = in.readString();
        senderMobile = in.readString();
        senderAddress = in.readString();
        receiver = in.readString();
        receiverMobile = in.readString();
        receiverAddress = in.readString();
        rentMount = in.readInt();
        latitude = in.readDouble();
        longitude = in.readDouble();
        state=in.readString();
        sendDate=in.readString();
        receiveDate=in.readString();
        packet=in.readString();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(identifyCode);
        dest.writeString(sender);
        dest.writeString(senderMobile);
        dest.writeString(senderAddress);
        dest.writeString(receiver);
        dest.writeString(receiverMobile);
        dest.writeString(receiverAddress);
        dest.writeInt(rentMount);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(state);
        dest.writeString(sendDate);
        dest.writeString(receiveDate);
        dest.writeString(packet);
    }


}
