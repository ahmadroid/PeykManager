package ir.ahmadandroid.mapproject.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.application.App;
import ir.ahmadandroid.mapproject.model.ApiKey;
import ir.ahmadandroid.mapproject.model.DriverLatitudeLongitude;
import ir.ahmadandroid.mapproject.model.responserequest.DriverLatLongListToken;
import ir.ahmadandroid.mapproject.model.responserequest.MessageToken;
import ir.ahmadandroid.mapproject.remote.RetrofitService;
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

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class SelectDriverFromOnMapActivity extends AppCompatActivity implements OnMapReadyCallback, PermissionsListener, MapboxMap.OnMapClickListener {

    private static final String LAYER_ID = "LAYER_ID";
    private static final String TAG = "SelectDriverFromOnMap";
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String ICON_PROPERTY = "ICON_PROPERTY";
    private static final String ICON_ID = "ICON_ID";
    private MapView mpView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_select_driver_from_on_map);

        mpView = findViewById(R.id.mpView_select_driver);
        mpView.onCreate(savedInstanceState);
        mpView.getMapAsync(this);
        mpView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );

        preferences = Utility.getPreferences(this);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        getDriverLatLong();
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            mapboxMap.addOnMapClickListener(SelectDriverFromOnMapActivity.this);

            Toast.makeText(SelectDriverFromOnMapActivity.this, R.string.tap_on_marker_instruction,
                    Toast.LENGTH_SHORT).show();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        return handleClickIcon(mapboxMap.getProjection().toScreenLocation(point));
    }

    /**
     * This method handles click events for SymbolLayer symbols.
     *
     * @param screenPoint the point on screen clicked
     */
    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap.queryRenderedFeatures(screenPoint, LAYER_ID);
        if (!features.isEmpty()) {

            // this json be contains latitude and longitude of point where marker on it.

            String json = features.get(0).toJson();
            // json => {"type":"Feature","id":"","geometry":{"type":"Point","coordinates":[48.380143, 34.181876]}}
            try {

                JSONObject jsonObject = new JSONObject(json);
                String identifyCode = jsonObject.getString("id");
                Intent codeIntent = new Intent(SelectDriverFromOnMapActivity.this, InsertServiceActivity.class);
                codeIntent.putExtra("identifyCode", identifyCode);
                setResult(RESULT_OK, codeIntent);
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    private void getDriverLatLong() {
        //get driver latitude and longitude list from database
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

        Map<String, String> params = new HashMap<>();
        ApiKey apiKey = new ApiKey();
        params.put("nationalCode", getNationalCode());
        params.put("apiKey", apiKey.getApiKey());
        params.put("secretKey", apiKey.getSecretKey());

        Call<DriverLatLongListToken> call = retrofitService.getDriverLatLongList(params);
        call.enqueue(new Callback<DriverLatLongListToken>() {
            @Override
            public void onResponse(Call<DriverLatLongListToken> call, retrofit2.Response<DriverLatLongListToken> response) {
                if (response.isSuccessful()) {
                    List<DriverLatitudeLongitude> latLongList = response.body().getLatLongList();
                    Log.i(TAG, response.body().getLatLongList().toString());
                    showDriverLatLongOnMap(latLongList);
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.TOKEN_KEY, response.body().getToken());
                        editor.apply();
                    }
                } else {
                    ErrorHandler errorHandler = new ErrorHandler(retrofit);
                    MessageToken error = errorHandler.parseError(response);
                    Toast.makeText(SelectDriverFromOnMapActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.TOKEN_KEY, error.getToken());
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverLatLongListToken> call, Throwable t) {
                Toast.makeText(SelectDriverFromOnMapActivity.this, getString(R.string.message_not_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDriverLatLongOnMap(List<DriverLatitudeLongitude> latLongList) {
        List<Feature> features = new ArrayList<>();
        for (DriverLatitudeLongitude driverLatLong : latLongList) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("type", "Feature");
                jsonObject.put("id", String.valueOf(driverLatLong.getIdentifyCode()));
                JSONObject geometryJson = new JSONObject();
                geometryJson.put("type", "Point");
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(0, driverLatLong.getLongitude());
                jsonArray.put(1, driverLatLong.getLatitude());
                geometryJson.put("coordinates", jsonArray);
                jsonObject.put("geometry", geometryJson);
                String json = jsonObject.toString();
                Feature feature = Feature.fromJson(json);
                features.add(feature);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        mapboxMap.setStyle(
                new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                        // Add the SymbolLayer icon image to the map style
                        .withImage(ICON_ID, BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default))
                        // Adding a GeoJson source for the SymbolLayer icons.
                        .withSource(new GeoJsonSource(SOURCE_ID, FeatureCollection.fromFeatures(features)))
                        // Adding the actual SymbolLayer to the map style. An offset is added that the bottom of the red
                        // marker icon gets fixed to the coordinate, rather than the middle of the icon being fixed to
                        // the coordinate point. This is offset is not always needed and is dependent on the image
                        // that you use for the SymbolLayer icon.
                        .withLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                                .withProperties(
                                        iconImage(ICON_ID),
                                        iconAllowOverlap(true),
                                        iconIgnorePlacement(true)
                                )
                        ), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        // Map is set up and the style has loaded. Now you can add additional data or make other map adjustments.

                        enableLocationComponent(style);
                    }
                }
        );
    }

    private String getToken() {
        String token = "";
        if (preferences != null) {
            token = preferences.getString(Utility.PREFE_TOKEN_KEY, "");
        }
        return token;
    }

    private String getNationalCode() {
        String nationalCode = "";
        if (preferences != null) {
            nationalCode = preferences.getString(Utility.PREFE_PERSON_NATIONAL_CODE_KEY, "");
        }
        return nationalCode;
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(SelectDriverFromOnMapActivity.this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            } else {
                Toast.makeText(SelectDriverFromOnMapActivity.this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    //permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onStart() {
        super.onStart();
        mpView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mpView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mpView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mpView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mpView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mpView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mpView.onDestroy();
    }

}