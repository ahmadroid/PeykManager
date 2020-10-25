package ir.ahmadandroid.mapproject.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.adapter.PersonListAdapter;
import ir.ahmadandroid.mapproject.database.MyDatabase;
import ir.ahmadandroid.mapproject.model.Person;
import ir.ahmadandroid.mapproject.utils.Utility;

public class PersonListActivity extends AppCompatActivity {

    private List<Person> personList;
    private RecyclerView recyclerView;
    private PersonListAdapter adapter;
    private SharedPreferences preferences;
    private CardView emptyView;
    private MyDatabase myDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_list);
        init();
    }

    private void init() {
        recyclerView=findViewById(R.id.recyvler_Person_list);
        emptyView=findViewById(R.id.crd_layout_person_list);
        preferences= Utility.getPreferences(PersonListActivity.this);
        myDatabase=new MyDatabase(PersonListActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        emptyView.setVisibility(View.INVISIBLE);
        personList=new ArrayList<>();
//        getPersonList();
        getPersonListByDatabase();
    }

    private void getPersonListByDatabase() {
        List<Person> personList = myDatabase.readPersonListFromDb();
        configureRecyclerView(personList);
    }

    private void configureRecyclerView(List<Person> personList) {
        adapter=new PersonListAdapter(PersonListActivity.this,personList);
        recyclerView.setLayoutManager(new LinearLayoutManager(PersonListActivity.this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        if (personList.size()==0){
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void getPersonList() {
//        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
//        clientBuilder.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//                Request request = original.newBuilder()
//                        .addHeader("Content-Type","application/json")
//                        .addHeader(Utility.TOKEN_KEY,getToken())
//                        .method(original.method(),original.body())
//                        .build();
//                return chain.proceed(request);
//            }
//        });
//        OkHttpClient client = clientBuilder.build();
//
//        Retrofit retrofit=new Retrofit.Builder()
//                .baseUrl(App.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(client)
//                .build();
//        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
//
//        NationalCodeApiKey nationalCodeApiKey=new NationalCodeApiKey();
//        nationalCodeApiKey.setNationalCode(getNationalCode());
//        Call<PersonListToken> call = retrofitService.getPersonList(nationalCodeApiKey);
//
//        call.enqueue(new Callback<PersonListToken>() {
//            @Override
//            public void onResponse(Call<PersonListToken> call, retrofit2.Response<PersonListToken> response) {
//                if (response.isSuccessful()){
//                    List<Person> personList = response.body().getPersonList();
//                    configureRecyclerView(personList);
//                    if (preferences!=null){
//                        SharedPreferences.Editor editor=preferences.edit();
//                        editor.putString(Utility.PREFE_TOKEN_KEY,response.body().getToken());
//                        editor.apply();
//                    }
//                }else {
//                    ErrorHandler errorHandler=new ErrorHandler(retrofit);
//                    MessageToken error = errorHandler.parseError(response);
//                    Toast.makeText(PersonListActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//                    if (preferences!=null){
//                        SharedPreferences.Editor editor=preferences.edit();
//                        editor.putString(Utility.PREFE_TOKEN_KEY,error.getToken());
//                        editor.apply();
//                    }
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<PersonListToken> call, Throwable t) {
//                Toast.makeText(PersonListActivity.this, getString(R.string.message_not_connect), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private String getToken(){
        String token="";
        if (preferences!=null){
            token=preferences.getString(Utility.PREFE_TOKEN_KEY,"");
        }
        return token;
    }

    private String getNationalCode(){
        String nationalCode="";
        if (preferences!=null){
            nationalCode=preferences.getString(Utility.PREFE_PERSON_NATIONAL_CODE_KEY,"");
        }
        return nationalCode;
    }
}