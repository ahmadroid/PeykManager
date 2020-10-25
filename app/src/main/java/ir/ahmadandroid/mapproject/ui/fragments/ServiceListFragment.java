package ir.ahmadandroid.mapproject.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.adapter.ServiceListAdapter;
import ir.ahmadandroid.mapproject.application.App;
import ir.ahmadandroid.mapproject.model.ApiKey;
import ir.ahmadandroid.mapproject.model.Person;
import ir.ahmadandroid.mapproject.model.Service;
import ir.ahmadandroid.mapproject.model.responserequest.MessageToken;
import ir.ahmadandroid.mapproject.model.responserequest.ServiceListToken;
import ir.ahmadandroid.mapproject.remote.RetrofitService;
import ir.ahmadandroid.mapproject.ui.activities.InsertServiceActivity;
import ir.ahmadandroid.mapproject.utils.ErrorHandler;
import ir.ahmadandroid.mapproject.utils.Utility;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ServiceListFragment";
    private ServiceListAdapter adapter;
    private RecyclerView recyclerView;
    private List<Service> serviceList;
    private CardView emptyView;
    private SharedPreferences preferences;
    private FloatingActionButton fltBtn;

    public ServiceListFragment() {
        // Required empty public constructor
    }

    public static ServiceListFragment newInstance(String param1, String param2) {
        ServiceListFragment fragment = new ServiceListFragment();
        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        serviceList = new ArrayList<>();
        preferences=Utility.getPreferences(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_list, container, false);
        recyclerView = view.findViewById(R.id.recycler_service_list_fragment);
        emptyView = view.findViewById(R.id.crd_layout_service_list_fragment);
        fltBtn= view.findViewById(R.id.fltBtn_insert_service_list_fragment);
        fltBtn.setOnClickListener(this);
        emptyView.setVisibility(View.INVISIBLE);
        return view;
    }

    //get service list from database
    private void getServiceList(){
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

        Map<String,String> params=new HashMap<>();
        ApiKey apiKey=new ApiKey();
        params.put("apiKey",apiKey.getApiKey());
        params.put("secretKey",apiKey.getSecretKey());
        params.put("nationalCode",getNationalCode());

        Call<ServiceListToken> call = retrofitService.getServiceList(params);
        call.enqueue(new Callback<ServiceListToken>() {
            @Override
            public void onResponse(Call<ServiceListToken> call, retrofit2.Response<ServiceListToken> response) {
                if (response.isSuccessful()){
                    List<Service> serviceList = response.body().getServiceList();
                    configureRecyclerView(serviceList);
                    if (preferences!=null){
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY,response.body().getToken());
                        editor.apply();
                    }
                }else {
                    ErrorHandler errorHandler=new ErrorHandler(retrofit);
                    MessageToken error = errorHandler.parseError(response);
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    if (preferences!=null){
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(Utility.PREFE_TOKEN_KEY,error.getToken());
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServiceListToken> call, Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.message_not_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configureRecyclerView(List<Service> serviceList) {
        adapter=new ServiceListAdapter(requireActivity(),serviceList);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        if (serviceList.size()==0){
            emptyView.setVisibility(View.VISIBLE);
        }else {
            emptyView.setVisibility(View.INVISIBLE);
        }
    }

    //get nationalCode from sharedPreferences
    private String getNationalCode() {
        String nationalCode="";
        if (preferences!=null){
            nationalCode=preferences.getString(Utility.PREFE_PERSON_NATIONAL_CODE_KEY,"");
        }
        return nationalCode;
    }

    //get token from sharedPreferences
    private String getToken() {
        String token = "";
        if (preferences != null) {
            token = preferences.getString(Utility.PREFE_TOKEN_KEY, "");
        }
        return token;
    }

    @Override
    public void onResume() {
        super.onResume();

        getServiceList();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(fltBtn)) {
            Intent serviceIntent = new Intent(requireContext(), InsertServiceActivity.class);
            startActivity(serviceIntent);
        }
    }
}