package ir.ahmadandroid.mapproject.database;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import ir.ahmadandroid.mapproject.model.Person;

public class MyFireBaseDatabase {

    private DatabaseReference id;
    private DatabaseReference identifyCode;
    private DatabaseReference nationalCode;
    private DatabaseReference name;
    private DatabaseReference mobile;
    private DatabaseReference isAdmin;
    private DatabaseReference state;
    private DatabaseReference tb_person;

    public MyFireBaseDatabase(){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        this.tb_person = database.getReference("tb_person");

        //TODO get id unique => use id or identifyCode (get from static method)
//        DatabaseReference person = tb_person.child(getNewId());
        DatabaseReference person = tb_person.child("person");

        //TODO autoIncrement
        this.id = person.child("id");
        this.identifyCode = person.child("identifyCode");
        this.nationalCode = person.child("nationalCode");
        this.name = person.child("name");
        this.mobile = person.child("mobile");
        this.isAdmin = person.child("isAdmin");
        this.state = person.child("state");
    }

    public void insertPersonToFBDB(){
        List<Person> personList = Person.getPersonList();
        int counter=0;
        for (Person person:personList){
            this.id.setValue(person.getId());
            this.identifyCode.setValue(person.getIdentifyCode());
            this.nationalCode.setValue(person.getNationalCode());
            this.name.setValue(person.getName());
            this.mobile.setValue(person.getMobile());
//            this.isAdmin.setValue(person.getIsAdmin());
//            this.state.setValue(person.getState());
        }
    }

    public Person readPersonFromFBDB(){
        Person person=new Person();
        this.tb_person.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                getPersonList(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return person;
    }

    private void getPersonList(DataSnapshot snapshot) {
        for (DataSnapshot dSnapshot:snapshot.getChildren()){

        }
    }
}
