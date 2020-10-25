package ir.ahmadandroid.mapproject.utils;

import java.io.IOException;
import java.lang.annotation.Annotation;

import ir.ahmadandroid.mapproject.model.responserequest.MessageToken;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ErrorHandler {

    private Retrofit retrofit;

    public ErrorHandler(Retrofit retrofit){
        this.retrofit=retrofit;
    }
    public MessageToken parseError(Response response){
        Converter<ResponseBody, MessageToken> converter=retrofit.responseBodyConverter(MessageToken.class,new Annotation[0]);
        MessageToken responseError=null;
        try {
            responseError=converter.convert(response.errorBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseError;
    }


}
