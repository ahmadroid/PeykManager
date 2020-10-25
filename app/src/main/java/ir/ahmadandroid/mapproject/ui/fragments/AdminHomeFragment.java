package ir.ahmadandroid.mapproject.ui.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.ahmadandroid.mapproject.R;
import ir.ahmadandroid.mapproject.application.App;
import ir.ahmadandroid.mapproject.model.ApiKey;
import ir.ahmadandroid.mapproject.model.responserequest.DriverLatLongListToken;
import ir.ahmadandroid.mapproject.model.DriverLatitudeLongitude;
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
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAnchor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;

public class AdminHomeFragment extends Fragment implements OnMapReadyCallback, PermissionsListener {

    private static final String TAG = "AdminHomeFragment";
    private MapView mpView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private SharedPreferences preferences;
    private static final String SOURCE_ID = "SOURCE_ID";
    private static final String RED_ICON_ID = "RED_ICON_ID";
    private static final String YELLOW_ICON_ID = "YELLOW_ICON_ID";
    private static final String LAYER_ID = "LAYER_ID";
    private static final String ICON_PROPERTY = "ICON_PROPERTY";
    private static final String ICON_ID = "ICON_ID";

    public AdminHomeFragment() {
        // Required empty public constructor
    }

    public static AdminHomeFragment newInstance(String param1, String param2) {
        AdminHomeFragment fragment = new AdminHomeFragment();
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
        preferences = Utility.getPreferences(requireContext());
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_home, container, false);
        mpView = view.findViewById(R.id.mpView_admin_fragment);
        mpView.onCreate(savedInstanceState);
        mpView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        //TODO
        getDriverLatLong();
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {

            //TODO
//            currentLocation(loadedMapStyle);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(requireActivity());
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
                    Log.i(TAG,response.body().getLatLongList().toString());
                    showDriverLatLongOnMap(latLongList);
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.TOKEN_KEY, response.body().getToken());
                        editor.apply();
                    }
                } else {
                    ErrorHandler errorHandler = new ErrorHandler(retrofit);
                    MessageToken error = errorHandler.parseError(response);
                    Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    if (preferences != null) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(Utility.TOKEN_KEY, error.getToken());
                        editor.apply();
                    }
                }
            }

            @Override
            public void onFailure(Call<DriverLatLongListToken> call, Throwable t) {
                Toast.makeText(requireContext(), getString(R.string.message_not_connect), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDriverLatLongOnMap(List<DriverLatitudeLongitude> latLongList) {
        List<Feature> features = new ArrayList<>();
        for (DriverLatitudeLongitude driverLatLong : latLongList) {
            Feature feature = Feature.fromGeometry(Point.fromLngLat(driverLatLong.getLongitude(), driverLatLong.getLatitude()));
            features.add(feature);
        }

        mapboxMap.setStyle(
                new Style.Builder().fromUri("mapbox://styles/mapbox/cjf4m44iw0uza2spb3q0a7s41")
                        // Add the SymbolLayer icon image to the map style
                        .withImage(ICON_ID, BitmapFactory.decodeResource(requireContext().getResources(), R.drawable.mapbox_marker_icon_default))
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

    @SuppressLint("MissingPermission")
    private void currentLocation(Style loadedMapStyle) {
        LocationComponent locationComponent = mapboxMap.getLocationComponent();
        locationComponent.activateLocationComponent(LocationComponentActivationOptions
                .builder(requireContext(), loadedMapStyle)
                .build());
        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(requireContext(), R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enableLocationComponent(mapboxMap.getStyle());
            }
        } else {
            Toast.makeText(requireContext(), R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            requireActivity().finish();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mpView.onDestroy();
    }

}