package ir.ahmadandroid.mapproject.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.model.Person;
import ir.ahmadandroid.mapproject.utils.Utility;


public class ProfileFragment extends Fragment {

    private TextView txtIdentifyCode,txtNationalCode,txtMobile,txtName,txtState;
    private ImageView imgPerson;
    private static final String ARG_PERSON="person";
    private Person person;
    private SharedPreferences preferences;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(Person person) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
//        args.putParcelable(ARG_PERSON,person);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle arg = getArguments();
//            person= arg.getParcelable(ARG_PERSON);
        }
        preferences=Utility.getPreferences(requireContext());
        person = getPerson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        txtIdentifyCode=view.findViewById(R.id.txt_identifyCode_profile_fragment);
        txtNationalCode=view.findViewById(R.id.txt_nationalCode_profile_content);
        txtName=view.findViewById(R.id.txt_name_profile_content);
        txtMobile=view.findViewById(R.id.txt_mobile_profile_content);
        txtState=view.findViewById(R.id.txt_state_profile_fragment);
        imgPerson=view.findViewById(R.id.img_person_profile_fragment);
        txtIdentifyCode.setText(String.valueOf(person.getIdentifyCode()));
        txtNationalCode.setText(person.getNationalCode());
        txtName.setText(person.getName());
        txtMobile.setText(person.getMobile());
        if (person.getState()==1){
            if (getContext()!=null ){
                txtState.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.state_back_active));
                txtState.setText(getString(R.string.sw_state_active));
            }
        }else if (person.getState()==0){
            if (getContext()!=null ){
                txtState.setBackground(ContextCompat.getDrawable(getContext(),R.drawable.state_back_deactive));
                txtState.setText(getString(R.string.sw_state_deActive));
            }
        }
        return view;
    }

    private Person getPerson() {
        Person person = new Person();
        if (preferences != null) {
            person.setId(preferences.getInt(Utility.PREFE_PERSON_ID_KEY, 0));
            person.setIdentifyCode(preferences.getInt(Utility.PREFE_PERSON_IDENTIFY_CODE_KEY, 0));
            person.setNationalCode(preferences.getString(Utility.PREFE_PERSON_NATIONAL_CODE_KEY, ""));
            person.setName(preferences.getString(Utility.PREFE_PERSON_NAME_KEY, ""));
            person.setMobile(preferences.getString(Utility.PREFE_PERSON_MOBILE_KEY, ""));
            person.setState((short) preferences.getInt(Utility.PREFE_PERSON_STATE_KEY, 0));
            person.setIsAdmin((short) preferences.getInt(Utility.PREFE_PERSON_ADMIN_KEY, 0));
        }

        return person;
    }
}