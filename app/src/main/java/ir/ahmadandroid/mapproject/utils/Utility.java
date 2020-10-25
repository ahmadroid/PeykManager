package ir.ahmadandroid.mapproject.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.File;

public class Utility {

    public static final String PREFERENCE_NAME="netPeykPref";
    public static final String PREFE_TOKEN_KEY ="token";
    public static final String PREFE_PERSON_NAME_KEY ="personName";
    public static final String PREFE_PERSON_ID_KEY ="personId";
    public static final String PREFE_PERSON_NATIONAL_CODE_KEY ="personNationalCode";
    public static final String PREFE_PERSON_IDENTIFY_CODE_KEY ="personIdentifyCode";
    public static final String PREFE_PERSON_MOBILE_KEY ="personmMobile";
    public static final String PREFE_PERSON_STATE_KEY ="personState";
    public static final String PREFE_PERSON_ADMIN_KEY ="personAdmin";
    public static final String DIRECTORY_NAME="netPeykDir";
    public static final String TOKEN_KEY="mpken-key";
    public static final String PERSON_KEY = "person";


    public static String getDirectoryPath(Context context){
        File subDir=new File(context.getExternalFilesDir(null).getAbsolutePath(),DIRECTORY_NAME);
        if (!subDir.exists()){
            subDir.mkdirs();
        }
        return subDir.getAbsolutePath();
    }

    public static SharedPreferences getPreferences(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        return sharedPreferences;
    }
}
