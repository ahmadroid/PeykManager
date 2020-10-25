package ir.ahmadandroid.mapproject.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Person implements Parcelable {

    private int id;
    private int identifyCode;
    private String nationalCode;
    private String name;
    private String mobile;
    private short isAdmin;
    private short state;

    public static final String PERSON_ID="id";
    public static final String PERSON_IDENTIFYCODE="identifyCode";
    public static final String PERSON_NATIONALCODE="nationalCode";
    public static final String PERSON_NAME="name";
    public static final String PERSON_MOBILE="mobile";
    public static final String PERSON_ISADMIN="isAdmin";
    public static final String PERSON_STATE="state";

    public Person() {
    }

    public Person(int id, int identifyCode, String nationalCode, String name, String mobile, short isAdmin, short state) {
        this.id = id;
        this.identifyCode = identifyCode;
        this.nationalCode = nationalCode;
        this.name = name;
        this.mobile = mobile;
        this.isAdmin = isAdmin;
        this.state = state;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public short getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(short isAdmin) {
        this.isAdmin = isAdmin;
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

    public String getNationalCode() {
        return nationalCode;
    }

    public void setNationalCode(String nationalCode) {
        this.nationalCode = nationalCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Person> CREATOR=new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(identifyCode);
        dest.writeString(nationalCode);
        dest.writeString(name);
        dest.writeString(mobile);
        dest.writeInt(state);
        dest.writeInt(isAdmin);
    }

    public Person(Parcel parcel){
        this.id=parcel.readInt();
        this.identifyCode=parcel.readInt();
        this.nationalCode=parcel.readString();
        this.name=parcel.readString();
        this.mobile=parcel.readString();
        this.state= (short) parcel.readInt();
        this.isAdmin= (short) parcel.readInt();

    }

    public static List<Person> cursorToPersonList(Cursor cursor) {
        List<Person> personList = new ArrayList<>();
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    Person person = new Person();
                    person.setId(cursor.getInt(cursor.getColumnIndex(Person.PERSON_ID)));
                    person.setIdentifyCode(cursor.getInt(cursor.getColumnIndex(Person.PERSON_IDENTIFYCODE)));
                    person.setNationalCode(cursor.getString(cursor.getColumnIndex(Person.PERSON_NATIONALCODE)));
                    person.setName(cursor.getString(cursor.getColumnIndex(Person.PERSON_NAME)));
                    person.setMobile(cursor.getString(cursor.getColumnIndex(Person.PERSON_MOBILE)));
                    person.setIsAdmin(cursor.getShort(cursor.getColumnIndex(Person.PERSON_ISADMIN)));
                    person.setState(cursor.getShort(cursor.getColumnIndex(Person.PERSON_STATE)));
                    Log.i("personTag","person nationalCoed"+person.getNationalCode());
                    personList.add(person);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return personList;
    }

    public static ContentValues writeToValues(Person person) {
        ContentValues values = new ContentValues();
        values.put(Person.PERSON_IDENTIFYCODE, person.getIdentifyCode());
        values.put(Person.PERSON_NATIONALCODE, person.getNationalCode());
        values.put(Person.PERSON_NAME, person.getName());
        values.put(Person.PERSON_MOBILE, person.getMobile());
        values.put(Person.PERSON_ISADMIN, person.getIsAdmin());
        values.put(Person.PERSON_STATE, person.getState());
        return values;
    }

    public static List<Person> getPersonList(){
        List<Person> personList=new ArrayList<>();
        Log.i("personTag","getPersonList");
        personList= Arrays.asList(
                new Person(1,1233,"0068565747","ahmad goudarzi","09125084200",(short)1,(short)1),
                new Person(1,1233,"0068565701","nafas goudarzi","09125084200",(short)0,(short)1),
                new Person(1,1233,"0068565702","ali goudarzi","09125084200",(short)0,(short)1),
                new Person(1,1233,"0068565703","hadi goudarzi","09125084200",(short)0,(short)1),
                new Person(1,1233,"0068565704","asad goudarzi","09125084200",(short)0,(short)1)
        );
        return personList;
    }
}
