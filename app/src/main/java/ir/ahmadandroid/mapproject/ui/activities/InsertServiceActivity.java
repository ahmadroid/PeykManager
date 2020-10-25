package ir.ahmadandroid.mapproject.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.application.App;
import ir.ahmadandroid.mapproject.model.ApiKey;
import ir.ahmadandroid.mapproject.model.Service;
import ir.ahmadandroid.mapproject.model.responserequest.MessageToken;
import ir.ahmadandroid.mapproject.remote.RetrofitService;
import ir.ahmadandroid.mapproject.utils.ErrorHandler;
import ir.ahmadandroid.mapproject.utils.MyActivity;
import ir.ahmadandroid.mapproject.utils.ShamsiDate;
import ir.ahmadandroid.mapproject.utils.Utility;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class InsertServiceActivity extends MyActivity implements View.OnClickListener {

    private static final String TAG ="InsertServiceActivity";
    public static final int REQ_CODE=123;
    private SharedPreferences preferences;
    private EditText edtIdentifyCode,edtSender,edtSenderMobile,edtSenderAddress,edtReceiver,edtReceiverMobile,edtReceiverAddress,
            edtRentMount,edtPacket;
    private double latitude=0,longitude=0;
    private String sendDate;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_service);
        init();
    }

    private void init() {
        preferences=Utility.getPreferences(InsertServiceActivity.this);
        edtIdentifyCode=findViewById(R.id.edt_driver_insert_service);
        edtSender=findViewById(R.id.edt_sender_insert_service);
        edtSenderMobile=findViewById(R.id.edt_senderMobile_insert_service);
        edtSenderAddress=findViewById(R.id.edt_senderAddress_insert_service);
        edtReceiver=findViewById(R.id.edt_receiver_insert_service);
        edtReceiverMobile=findViewById(R.id.edt_receiverMobile_insert_service);
        edtReceiverAddress=findViewById(R.id.edt_receiverAddress_insert_service);
        edtRentMount=findViewById(R.id.edt_rent_insert_service);
        edtPacket=findViewById(R.id.edt_packet_insert_service);
        btnConfirm=findViewById(R.id.btn_confirm_insert_service);
        btnConfirm.setOnClickListener(this);
        edtIdentifyCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btnConfirm)) {
            insertServiceToDatabase();
        } else if (view.equals(edtIdentifyCode)) {
            Intent driverIntent = new Intent(InsertServiceActivity.this,SelectDriverFromOnMapActivity.class);
            if (driverIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(driverIntent,REQ_CODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQ_CODE && resultCode==RESULT_OK){
            if (data!=null){
                String identifyCode = data.getStringExtra("identifyCode");
                edtIdentifyCode.setText(identifyCode);
            }
        }
    }

    private void insertServiceToDatabase() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
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

        Map<String, String> params=new HashMap<>();
        ApiKey apiKey=new ApiKey();
        Service service = getService();
        params.put("identifyCode",String.valueOf(service.getIdentifyCode()));
        params.put("sender",service.getSender());
        params.put("senderMobile",service.getSenderMobile());
        params.put("senderAddress",service.getSenderAddress());
        params.put("receiver",service.getReceiver());
        params.put("receiverMobile",service.getReceiverMobile());
        params.put("receiverAddress",service.getReceiverAddress());
        params.put("rentMount",String.valueOf(service.getRentMount()));
        params.put("latitude",String.valueOf(service.getLatitude()));
        params.put("longitude",String.valueOf(service.getLongitude()));
        params.put("packet",service.getPacket());
        params.put("sendDate",service.getSendDate());
        params.put("nationalCode",getNationalCode());
        params.put("apiKey",apiKey.getApiKey());
        params.put("secretKey",apiKey.getSecretKey());

        Call<MessageToken> call = retrofitService.insertService(params);
        call.enqueue(new Callback<MessageToken>() {
            @Override
            public void onResponse(Call<MessageToken> call, retrofit2.Response<MessageToken> response) {
                if (response.isSuccessful()){
                    Toast.makeText(InsertServiceActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    if (preferences!=null){
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY,response.body().getToken());
                        editor.apply();
                    }
                }else {
                    ErrorHandler errorHandler=new ErrorHandler(retrofit);
                    MessageToken error = errorHandler.parseError(response);
                    Toast.makeText(InsertServiceActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                    if (preferences!=null){
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY,error.getToken());
                        editor.apply();
                    }
                }
                finish();
            }

            @Override
            public void onFailure(Call<MessageToken> call, Throwable t) {
                Toast.makeText(InsertServiceActivity.this, getString(R.string.message_not_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getToken() {
        String token="";
        if (preferences!=null){
            token=preferences.getString(Utility.PREFE_TOKEN_KEY,"");
        }
        return token;
    }

    private String getNationalCode() {
        String nationalCode="";
        if (preferences!=null){
            nationalCode=preferences.getString(Utility.PREFE_PERSON_NATIONAL_CODE_KEY,"");
        }
        return nationalCode;
    }

    private Service getService() {
        Locale locale=new Locale("en");
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hhmmss",locale);
        String time = simpleDateFormat.format(new Date());
        String date = ShamsiDate.getCurrentShamsidate1();
        Service service=new Service();
        service.setIdentifyCode(Integer.parseInt(edtIdentifyCode.getText().toString()));
        service.setSender(edtSender.getText().toString());
        service.setSenderMobile(edtSenderMobile.getText().toString());
        service.setSenderAddress(edtSenderAddress.getText().toString());
        service.setReceiver(edtReceiver.getText().toString());
        service.setReceiverMobile(edtReceiverMobile.getText().toString());
        service.setReceiverAddress(edtReceiverAddress.getText().toString());
        service.setRentMount(Integer.parseInt(edtRentMount.getText().toString()));
        service.setPacket(edtPacket.getText().toString());
        service.setLatitude(latitude);
        service.setLongitude(longitude);
        service.setSendDate(date+time);
        return service;
    }
}