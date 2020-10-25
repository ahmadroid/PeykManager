package ir.ahmadandroid.mapproject.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.core.constants.Constants;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.layers.Layer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ir.ahmadandroid.mapproject.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.mapbox.api.directions.v5.DirectionsCriteria.GEOMETRY_POLYLINE;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_CAP_ROUND;
import static com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineCap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineJoin;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineOpacity;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

@SuppressLint("MissingPermission")
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, PermissionsListener {

    private MapView mpView;
    private MapboxMap mapboxMap;
    public static final String LAYER_ID = "LAYER_ID";
    public static final String SOURCE_ID = "SOURCE_ID";
    public static final String ICON_ID = "SOURCE_ID";
    public static final String TAG = "LocationActivity";
    public static final LatLng MY_POINT = new LatLng(34.185257, 48.152425);
    private Button btn;
    private PermissionsManager permissionsManager;
    private ImageView imgCenter;
    private @Nullable
    Style originalStyle;
    private Point originPoint, destinationPoint;

    private static final float NAVIGATION_LINE_WIDTH = 6;
    private static final float NAVIGATION_LINE_OPACITY = .8f;
    private static final String DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID = "DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID";
    private static final String DRIVING_ROUTE_POLYLINE_SOURCE_ID = "DRIVING_ROUTE_POLYLINE_SOURCE_ID";
    private static final int DRAW_SPEED_MILLISECONDS = 10;
    private MapboxDirections mapboxDirectionsClient;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));

        setContentView(R.layout.activity_location);

        mpView = findViewById(R.id.mpVeiw_location);
        mpView.onCreate(savedInstanceState);
        mpView.getMapAsync(this);

        btn = findViewById(R.id.btn_location);
        btn.setOnClickListener(this);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;

        //create(placed) marker on map start
        addIconOnMap(mapboxMap);

        if (mapboxMap.getStyle() != null) {
            enabledLocationComponent(mapboxMap.getStyle());
        }
    }

    private void enabledLocationComponent(@NonNull Style style) {
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            btn.setVisibility(View.VISIBLE);

            //show current location
//            currentLocation(style);

            //create icon in screen center start
            addIconInScreenCenter();

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    private void addIconInScreenCenter() {
        imgCenter = new ImageView(LocationActivity.this);
        imgCenter.setImageResource(R.drawable.locaion);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT
                , ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        imgCenter.setLayoutParams(params);
        mpView.addView(imgCenter);
    }

    private void addIconOnMap(@NonNull MapboxMap mapboxMap) {

        mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                //save original style
                originalStyle = style;

                originPoint = Point.fromLngLat(MY_POINT.getLongitude(), MY_POINT.getLatitude());
                Source geoSource = new GeoJsonSource(SOURCE_ID, Feature.fromGeometry(originPoint));
                style.addSource(geoSource);
                style.addImage(ICON_ID, BitmapFactory.decodeResource(getResources(), R.drawable.locaion));
                style.addLayer(new SymbolLayer(LAYER_ID, SOURCE_ID)
                        .withProperties(
                                PropertyFactory.iconImage(ICON_ID),
                                PropertyFactory.iconAllowOverlap(true),
                                PropertyFactory.iconIgnorePlacement(true)
                        ));
                        // Add a source and LineLayer for the snaking directions route line
                        style.addSource(new GeoJsonSource(DRIVING_ROUTE_POLYLINE_SOURCE_ID));
                        style.addLayerBelow(new LineLayer(DRIVING_ROUTE_POLYLINE_LINE_LAYER_ID,
                                DRIVING_ROUTE_POLYLINE_SOURCE_ID)
                                .withProperties(
                                        lineWidth(NAVIGATION_LINE_WIDTH),
                                        lineOpacity(NAVIGATION_LINE_OPACITY),
                                        lineCap(LINE_CAP_ROUND),
                                        lineJoin(LINE_JOIN_ROUND),
                                        lineColor(Color.parseColor("#2196F3"))
                                ), LAYER_ID);

                //create(placed) marker on map end
                enabledLocationComponent(style);


            }
        });


    }

    @SuppressLint("MissingPermission")
    private void currentLocation(@Nullable Style style) {

        LocationComponent locationComponent = mapboxMap.getLocationComponent();

        locationComponent.activateLocationComponent(LocationComponentActivationOptions.builder(LocationActivity.this, style)
                .build());

        locationComponent.setLocationComponentEnabled(true);

        locationComponent.setCameraMode(CameraMode.TRACKING);
        locationComponent.setRenderMode(RenderMode.COMPASS);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(btn)) {

            Layer destination_layer = originalStyle.getLayer("DESTINATION_LAYER_ID");
            if (destination_layer != null) {
                originalStyle.removeLayer("DESTINATION_LAYER_ID");
            }
            Source destination_source = originalStyle.getSource("DESTINATION_SOURCE_ID");
            if (destination_source != null) {
                originalStyle.removeSource("DESTINATION_SOURCE_ID");
            }

            //insert destination location
            insertDestination();
        }
    }

    private void insertDestination() {
        LatLng target = mapboxMap.getCameraPosition().target;
        destinationPoint = Point.fromLngLat(target.getLongitude(), target.getLatitude());
        Source source = new GeoJsonSource("DESTINATION_SOURCE_ID", Feature.fromGeometry(destinationPoint));
        if (originalStyle != null) {
            originalStyle.addSource(source);
            originalStyle.addImage("DESTINATION_ICON_ID", BitmapFactory.decodeResource(getResources(),
                    R.drawable.carpices));
            originalStyle.addLayer(new SymbolLayer("DESTINATION_LAYER_ID", "DESTINATION_SOURCE_ID")
                    .withProperties(
                            PropertyFactory.iconImage("DESTINATION_ICON_ID"),
                            PropertyFactory.iconAllowOverlap(true),
                            PropertyFactory.iconIgnorePlacement(true)
                    ));
        }

        getDirectionsRoute(originPoint,destinationPoint);
    }

    /**
     * Build the Mapbox Directions API request
     *
     * @param origin      The starting point for the directions route
     * @param destination The final point for the directions route
     */
    private void getDirectionsRoute(Point origin, Point destination) {
        mapboxDirectionsClient = MapboxDirections.builder()
                .origin(origin)
                .destination(destination)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .geometries(GEOMETRY_POLYLINE)
                .alternatives(true)
                .steps(true)
                .accessToken(getString(R.string.mapbox_access_token))
                .build();

        mapboxDirectionsClient.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
// Create log messages in case no response or routes are present
                if (response.body() == null) {
                    Timber.d("No routes found, make sure you set the right user and access token.");

                    return;
                } else if (response.body().routes().size() < 1) {
                    Timber.d("No /routes found");

                    return;
                }

                List<DirectionsRoute> routes = response.body().routes();
                DirectionsRoute currentRoute = routes.get(0);
//                Double distance = currentRoute.distance();
//                Double duration = currentRoute.duration();
//                TextView textView=findViewById(R.id.feature_info);
//                int kMeter=0,meter=0,hour=0,min=0;
//                String dis,time;
//                if (distance != null) {
//                    kMeter = (int) (distance / 1000);
//                    meter = (int) (distance % 1000);
//                }
//                if (kMeter > 0) {
//
//                    dis = kMeter + " کیلومتر و " + meter + " متر";
//                } else {
//                    dis = meter + " متر";
//                }
//                if (duration != null) {
//                    hour = (int) (duration / 3600);
//                    min = (int) (duration % 3600) / 60;
//                }
//                if (hour > 0) {
//                    time = hour + " ساعت و " + min + " دقیقه";
//                } else {
//                    time = String.valueOf(min) + " دقیقه";
//                }
//                textView.append("مسافت: " + dis + "\n");
//                textView.append("زمان: " + time);

// Get the route from the Mapbox Directions API response
//                DirectionsRoute currentRoute = response.body().routes().get(0);

// Start the step-by-step process of drawing the route
                runnable = new DrawRouteRunnable(mapboxMap, currentRoute.legs().get(0).steps(), handler);
                handler.postDelayed(runnable, DRAW_SPEED_MILLISECONDS);
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(LocationActivity.this,
                        R.string.snaking_directions_activity_error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Runnable class which goes through the route and draws each {@link LegStep} of the Directions API route
     */
    private static class DrawRouteRunnable implements Runnable {
        private MapboxMap mapboxMap;
        private List<LegStep> steps;
        private List<Feature> drivingRoutePolyLineFeatureList;
        private Handler handler;
        private int counterIndex;

        DrawRouteRunnable(MapboxMap mapboxMap, List<LegStep> steps, Handler handler) {
            this.mapboxMap = mapboxMap;
            this.steps = steps;
            this.handler = handler;
            //counterIndex determine whole points between start and end line
            this.counterIndex = 0;
            drivingRoutePolyLineFeatureList = new ArrayList<>();
        }

        @Override
        public void run() {
            //تمام نقاط یا به عبارتی شهرهای سر راه رو با یک خط وصل میکنه
            if (counterIndex < steps.size()) {
                LegStep singleStep = steps.get(counterIndex);
                if (singleStep != null && singleStep.geometry() != null) {
                    LineString lineStringRepresentingSingleStep = LineString.fromPolyline(
                            singleStep.geometry(), Constants.PRECISION_5);
                    Feature featureLineString = Feature.fromGeometry(lineStringRepresentingSingleStep);
                    drivingRoutePolyLineFeatureList.add(featureLineString);
                }
                if (mapboxMap.getStyle() != null) {
                    GeoJsonSource source = mapboxMap.getStyle().getSourceAs(DRIVING_ROUTE_POLYLINE_SOURCE_ID);
                    if (source != null) {
                        source.setGeoJson(FeatureCollection.fromFeatures(drivingRoutePolyLineFeatureList));
                    }
                }
                counterIndex++;
                handler.postDelayed(this, DRAW_SPEED_MILLISECONDS);
            }
        }
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, getString(R.string.user_location_permission_explanation), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            if (mapboxMap.getStyle() != null) {
                enabledLocationComponent(mapboxMap.getStyle());
            } else {
                Toast.makeText(this, getString(R.string.user_location_permission_not_granted), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mpView.onResume();
        btn.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mpView.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mpView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mpView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mpView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mpView.onLowMemory();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mpView.onSaveInstanceState(outState);
    }
}