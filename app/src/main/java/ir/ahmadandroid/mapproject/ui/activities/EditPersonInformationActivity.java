package ir.ahmadandroid.mapproject.ui.activities;

import androidx.appcompat.widget.SwitchCompat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.application.App;
import ir.ahmadandroid.mapproject.database.MyDatabase;
import ir.ahmadandroid.mapproject.model.ApiKey;
import ir.ahmadandroid.mapproject.model.responserequest.MessageToken;
import ir.ahmadandroid.mapproject.model.Person;
import ir.ahmadandroid.mapproject.remote.RetrofitService;
import ir.ahmadandroid.mapproject.utils.ErrorHandler;
import ir.ahmadandroid.mapproject.utils.MyActivity;
import ir.ahmadandroid.mapproject.utils.Utility;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditPersonInformationActivity extends MyActivity implements View.OnClickListener {

    private EditText edtName, edtMobile, edtNationalCode, edtIdentifyCode;
    private SharedPreferences preferences;
    private String TAG = "InsertPersonActivity";
    private Button btnEdit;
    private SwitchCompat swState,swAdmin;
    private Person person;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_person_information);
        init();
        Bundle extras = getIntent().getExtras();
        if (extras!=null){
            person=extras.getParcelable(Utility.PERSON_KEY);
        }
        if (person!=null){
            edtIdentifyCode.setText(String.valueOf(person.getIdentifyCode()));
            edtNationalCode.setText(person.getNationalCode());
            edtName.setText(person.getName());
            edtMobile.setText(person.getMobile());
            if (person.getIsAdmin()==1){
                swAdmin.setChecked(true);
            }else if (person.getIsAdmin()==0){
                swAdmin.setChecked(false);
            }
            if (person.getState()==1){
                swState.setChecked(true);
            }else if (person.getState()==0){
                swState.setChecked(false);
            }
        }
    }

    //TODO insert delete button

    private void init() {
        myDatabase=new MyDatabase(EditPersonInformationActivity.this);
        person=new Person();
        preferences = Utility.getPreferences(EditPersonInformationActivity.this);
        edtIdentifyCode = findViewById(R.id.edt_identifyCode_edit_info_person);
        edtNationalCode = findViewById(R.id.edt_nationalCode_edit_info_person);
        edtName = findViewById(R.id.edt_name_edit_info_person);
        edtMobile = findViewById(R.id.edt_mobile_edit_info_person);
        btnEdit = findViewById(R.id.btn_edit_edit_info_person);
        btnEdit.setOnClickListener(this);
        swState=findViewById(R.id.swtch_state_edit_info_person);
        swState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO show message
                if (isChecked){
                    swState.setText(getString(R.string.sw_state_active));
                }else {
                    swState.setText(getString(R.string.sw_state_deActive));
                }
            }
        });
        swAdmin=findViewById(R.id.swtch_admin_edit_info_person);
        swAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    swAdmin.setText(getString(R.string.sw_admin));
                }else {
                    swAdmin.setText(getString(R.string.sw_user));
                }
            }

        });
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnEdit)){
            if (edtName.getText().toString().isEmpty()){
                edtName.setError(getString(R.string.message_full_field));
                edtName.requestFocus();
                return;
            }
            if (edtMobile.getText().toString().isEmpty()){
                edtMobile.setError(getString(R.string.message_full_field));
                edtMobile.requestFocus();
                return;
            }
            if (edtNationalCode.getText().toString().isEmpty()){
                edtNationalCode.setError(getString(R.string.message_full_field));
                edtNationalCode.requestFocus();
                return;
            }
            editInfo();
        }
    }

    private void editInfo(){
        OkHttpClient.Builder clientBuilder=new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request=original.newBuilder()
                        .addHeader("Content-Type","application/json")
                        .addHeader(Utility.TOKEN_KEY,getToken())
                        .method(original.method(),original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit=new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(App.BASE_URL)
                .client(client)
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Person person = getPerson();
        ApiKey apiKey=new ApiKey();
        Map<String, String> params = new HashMap<>();
        params.put("identifyCode",String.valueOf(person.getIdentifyCode()));
        params.put("nationalCode",person.getNationalCode());
        params.put("name",person.getName());
        params.put("mobile",person.getMobile());
        params.put("isAdmin",String.valueOf(person.getIsAdmin()));
        params.put("state",String.valueOf(person.getState()));
        params.put("code",getNationalCode());
        params.put("apiKey",String.valueOf(apiKey.getApiKey()));
        params.put("secretKey",String.valueOf(apiKey.getSecretKey()));
        Call<MessageToken> call = retrofitService.editPersonInfo(params);
        call.enqueue(new Callback<MessageToken>() {
            @Override
            public void onResponse(Call<MessageToken> call, retrofit2.Response<MessageToken> response) {
                if (response.isSuccessful()){
                    Toast.makeText(EditPersonInformationActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY, response.body().getToken());
                        editor.apply();
                    }
                }else {
                    ErrorHandler errorHandler=new ErrorHandler(retrofit);
                    MessageToken error = errorHandler.parseError(response);
                    Toast.makeText(EditPersonInformationActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY, error.getToken());
                        editor.apply();
                    }
                }

            }

            @Override
            public void onFailure(Call<MessageToken> call, Throwable t) {
                Toast.makeText(EditPersonInformationActivity.this, getString(R.string.message_not_connect), Toast.LENGTH_LONG).show();
            }
        });
    }

    @NotNull
    private Person getPerson() {
        Person person=new Person();
        if (swState.isChecked()){
            person.setState((short) 1);
        }else if (!swState.isChecked()){
            person.setState((short) 0);
        }
        if (swAdmin.isChecked()){
            person.setIsAdmin((short) 1);
        }else if (!swAdmin.isChecked()){
            person.setIsAdmin((short) 0);
        }
        person.setIdentifyCode(Integer.parseInt(edtIdentifyCode.getText().toString()));
        person.setNationalCode(edtNationalCode.getText().toString());
        person.setName(edtName.getText().toString());
        person.setMobile(edtMobile.getText().toString());
        return person;
    }

    private String getNationalCode() {
        String nationalCode="";
        if (preferences != null) {
            nationalCode=preferences.getString(Utility.PREFE_PERSON_NATIONAL_CODE_KEY,"");
        }
        return nationalCode;
    }

    private String getToken() {
        String token="";
        if (preferences!=null){
            token=preferences.getString(Utility.PREFE_TOKEN_KEY,"");
        }
        return token;
    }
}