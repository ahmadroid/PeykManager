package ir.ahmadandroid.mapproject.remote;

import java.util.Map;

import ir.ahmadandroid.mapproject.model.responserequest.DriverLatLongListToken;
import ir.ahmadandroid.mapproject.model.responserequest.IdentifyCodeToken;
import ir.ahmadandroid.mapproject.model.responserequest.PersonListToken;
import ir.ahmadandroid.mapproject.model.responserequest.MessageToken;
import ir.ahmadandroid.mapproject.model.responserequest.PersonToken;
import ir.ahmadandroid.mapproject.model.responserequest.ServiceListToken;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface RetrofitService {

    @GET("Login.php")
    Call<PersonToken> login(@QueryMap Map<String,String> params);

    @GET("EditPersonInfo.php")
    Call<MessageToken> editPersonInfo(@QueryMap Map<String,String> params);

    @GET("GetPersonList.php")
    Call<PersonListToken> getPersonList(@QueryMap Map<String,String> params);

    @GET("GetLastIdentifyCode.php")
    Call<IdentifyCodeToken> getLastIdentifyCode(@QueryMap Map<String,String> params);

    @GET("InsertPersonToDB.php")
    Call<MessageToken> insertPerson(@QueryMap Map<String,String> params);

    @GET("GetDriverLatitudeLongitude.php")
    Call<DriverLatLongListToken> getDriverLatLongList(@QueryMap Map<String,String> params);

    @GET("ReadAllService.php")
    Call<ServiceListToken> getServiceList(@QueryMap Map<String,String> params);

    @GET("InsertServiceToDB.php")
    Call<MessageToken> insertService(@QueryMap Map<String, String> params);


    // POST METHOD
//    @POST("Login.php")
//    Call<PersonToken> login(@Body NationalCodePsApiKey nationalCodePsApiKey);
//
//    @POST("InsertPersonToDB.php")
//    Call<MessageToken> insertPerson(@Body PersonNationalCodeApiKey personNationalCodeApiKey);
//
//    @POST("GetLastIdentifyCode.php")
//    Call<IdentifyCodeToken> getLastIdentifyCode(@Body NationalCodeApiKey nationalCodeApiKey);
//
//    @PUT("EditPersonInfo.php")
//    Call<MessageToken> editPersonInfo(@Body PersonNationalCodeApiKey personNationalCodeApiKey);

//    @POST("GetPersonList.php")
//    Call<PersonListToken> getPersonList(@Body NationalCodeApiKey nationalCodeApiKey);
}
