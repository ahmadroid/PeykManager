package ir.ahmadandroid.mapproject.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.SwitchCompat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ir.ahmadandroid.mapproject.application.App;
import ir.ahmadandroid.mapproject.database.MyDatabase;
import ir.ahmadandroid.mapproject.model.ApiKey;
import ir.ahmadandroid.mapproject.model.responserequest.IdentifyCodeToken;
import ir.ahmadandroid.mapproject.model.Person;
import ir.ahmadandroid.mapproject.model.responserequest.MessageToken;
import ir.ahmadandroid.mapproject.remote.RetrofitService;
import ir.ahmadandroid.mapproject.utils.ErrorHandler;
import ir.ahmadandroid.mapproject.utils.MyActivity;
import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.utils.Utility;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertPersonActivity extends MyActivity implements View.OnClickListener {

    private EditText edtName, edtMobile, edtNationalCode, edtIdentifyCode;
    private SharedPreferences preferences;
    private String TAG = "InsertPersonActivity";
    private Button btnRegister;
    private SwitchCompat swState, swAdmin;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_person);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        btnRegister.setEnabled(false);
        getLastIdentifyCodeFromData();

    }

    private void insertPerson() {
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
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
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

        Call<MessageToken> call = retrofitService.insertPerson(params);
        call.enqueue(new Callback<MessageToken>() {
            @Override
            public void onResponse(Call<MessageToken> call, retrofit2.Response<MessageToken> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(InsertPersonActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY, response.body().getToken());
                        editor.apply();
                    }
                    Intent editIntent = new Intent(InsertPersonActivity.this, EditPersonInformationActivity.class);
                    if (editIntent.resolveActivity(getPackageManager())!=null){
                        editIntent.putExtra("person",getPerson());
                        startActivity(editIntent);
                    }
                    finish();
                } else {
                    ErrorHandler errorHandler = new ErrorHandler(retrofit);
                    MessageToken error = errorHandler.parseError(response);
                    Toast.makeText(InsertPersonActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY, error.getToken());
                        editor.apply();
                    }
                    finish();
                }
            }

            @Override
            public void onFailure(Call<MessageToken> call, Throwable t) {
                Toast.makeText(InsertPersonActivity.this, getString(R.string.message_not_connect), Toast.LENGTH_LONG).show();
//                Toast.makeText(InsertPersonActivity.this, "insert person error", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void init() {
        myDatabase=new MyDatabase(InsertPersonActivity.this);
        preferences = Utility.getPreferences(InsertPersonActivity.this);
        edtIdentifyCode = findViewById(R.id.edt_identifyCode_insert_person);
        edtNationalCode = findViewById(R.id.edt_nationalCode_insert_person);
        edtName = findViewById(R.id.edt_name_insert_person);
        edtMobile = findViewById(R.id.edt_mobile_insert_person);
        btnRegister = findViewById(R.id.btn_register_insert_person);
        btnRegister.setOnClickListener(this);
        swState = findViewById(R.id.swtch_state_insert_person);
        swState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO show message
                if (isChecked) {
                    swState.setText(getString(R.string.sw_state_active));
                } else {
                    swState.setText(getString(R.string.sw_state_deActive));
                }
            }
        });
        swAdmin = findViewById(R.id.swtch_admin_insert_person);
        swAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swAdmin.setText(getString(R.string.sw_admin));
                } else {
                    swAdmin.setText(getString(R.string.sw_user));
                }
            }

        });
    }

    private void getLastIdentifyCodeFromData() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                Request request = original.newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader(Utility.TOKEN_KEY, getToken())
                        .method(original.method(), original.body())
                        .build();
                return chain.proceed(request);
            }
        });
        OkHttpClient client = clientBuilder.build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(App.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        ApiKey apiKey=new ApiKey();
        Map<String, String> params=new HashMap<>();
        params.put("nationalCode",getNationalCode());
        params.put("apiKey",apiKey.getApiKey());
        params.put("secretKey",apiKey.getSecretKey());
        Call<IdentifyCodeToken> call = retrofitService.getLastIdentifyCode(params);
        call.enqueue(new Callback<IdentifyCodeToken>() {
            @Override
            public void onResponse(Call<IdentifyCodeToken> call, retrofit2.Response<IdentifyCodeToken> response) {
                if (response.isSuccessful()) {
                    edtIdentifyCode.setText(String.valueOf(response.body().getIdentifyCode() + 1));
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY, response.body().getToken());
                        editor.apply();
                    }
                    btnRegister.setEnabled(true);
                } else {
                    ErrorHandler errorHandler = new ErrorHandler(retrofit);
                    MessageToken error = errorHandler.parseError(response);
                    Toast.makeText(InsertPersonActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY, error.getToken());
                        editor.apply();
                    }
                    finish();
                }

            }

            @Override
            public void onFailure(Call<IdentifyCodeToken> call, Throwable t) {
                Toast.makeText(InsertPersonActivity.this, getString(R.string.message_not_connect), Toast.LENGTH_LONG).show();
//                Toast.makeText(InsertPersonActivity.this, "getlastcode error", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private Person getPerson() {
        Person person=new Person();
        person.setIdentifyCode(Integer.parseInt(edtIdentifyCode.getText().toString()));
        person.setNationalCode(edtNationalCode.getText().toString());
        person.setName(edtName.getText().toString());
        person.setMobile(edtMobile.getText().toString());
        if (swState.isChecked()) {
            person.setState((short) 1);
        } else if (!swState.isChecked()) {
            person.setState((short) 0);
        }
        if (swAdmin.isChecked()) {
            person.setIsAdmin((short) 1);
        } else if (!swAdmin.isChecked()) {
            person.setIsAdmin((short) 0);
        }
        return person;
    }

    private String getNationalCode() {
        String nationalCode = "";
        if (preferences != null) {
            nationalCode = preferences.getString(Utility.PREFE_PERSON_NATIONAL_CODE_KEY, "");
        }
        return nationalCode;
    }

    private String getToken() {
        String token = "";
        if (preferences != null) {
            token = preferences.getString(Utility.PREFE_TOKEN_KEY, "");
        }
        return token;
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnRegister)) {
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
            insertPerson();
        }
    }

}