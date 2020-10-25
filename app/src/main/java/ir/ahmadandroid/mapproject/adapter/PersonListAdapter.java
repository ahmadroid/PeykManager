package ir.ahmadandroid.mapproject.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.model.Person;
import ir.ahmadandroid.mapproject.ui.activities.EditPersonInformationActivity;

public class PersonListAdapter extends RecyclerView.Adapter<PersonListAdapter.MyViewHolder> {

    private Context context;
    private Activity activity;
    private List<Person>personList=new ArrayList<>();

    public PersonListAdapter(Activity activity, List<Person> personList) {
        this.activity = activity;
        this.personList = personList;
        this.context=activity.getApplicationContext();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_person_view, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.bind(activity,personList.get(position));
    }

    @Override
    public int getItemCount() {
        if (personList.size()<5){
            return 5;
        }else {
            return personList.size();
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtName,txtIdentifyCode,txtState;
        private ImageView imgPerson;
        private RelativeLayout layout;
        private Person person;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIdentifyCode=itemView.findViewById(R.id.txt_identifyCode_person_view);
            txtName=itemView.findViewById(R.id.txt_name_person_view);
            txtState=itemView.findViewById(R.id.txt_state_person_view);
            layout=itemView.findViewById(R.id.rel_layout_person_list);
        }

        void bind(Activity activity, Person person){
            if (person.getState()==1){
                txtState.setText(context.getString(R.string.driver_state_active));
                txtState.setTextColor(Color.GREEN);
            }else if (person.getState()==0){
                txtState.setText(context.getString(R.string.driver_state_deActive));
                txtState.setTextColor(Color.RED);
            }
//            String text = context.getString(R.string.txt_identifyCode) + " " + person.getIdentifyCode();
            String text = String.valueOf(person.getIdentifyCode());
            txtIdentifyCode.setText(text);
            txtName.setText(person.getName());
            layout.setOnClickListener(this);
            this.person=person;
        }

        @Override
        public void onClick(View view) {
            if (view.equals(layout)) {
                Intent editIntent = new Intent(context, EditPersonInformationActivity.class);
                editIntent.putExtra("person",person);
                if (editIntent.resolveActivity(activity.getPackageManager())!=null){
                    activity.startActivity(editIntent);
                }
            }
        }
    }
}
