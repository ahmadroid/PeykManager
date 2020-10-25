package ir.ahmadandroid.mapproject.application;

import android.app.Application;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import ir.ahmadandroid.mapproject.R;

public class App extends Application {

    public static final String BASE_URL = "http://192.168.1.102:8080/mapDir/";
//    public static final String BASE_URL = "https://netpeykonline.ir/netPeykApp/";
//    public static final String BASE_URL = "http://127.1.1.0:8080/mapDir/";

    public static final String SEARCH_USER_URI = BASE_URL + "searchforusername.php";
    public static final String READ_CODE_URI = BASE_URL + "readlastcodeobject.php";
    public static final String UPLOAD_IMAGE_TO_SERVER_URI = BASE_URL + "UploadToServer.php";
    public static final String UPLOAD_IMAGE_SHOP_TO_SERVER_URI = BASE_URL + "UploadImageShopToServer.php";
    public static final String DOWN_IMAGE_FROM_SERVER_URI = BASE_URL + "uploads/";

    @Override
    public void onCreate() {
        super.onCreate();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
//                                .setDefaultFontPath("fonts/BYekanNew.ttf")
                                .setDefaultFontPath("fonts/Yekan.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }
}

