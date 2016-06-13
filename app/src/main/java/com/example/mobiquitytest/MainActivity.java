package com.example.mobiquitytest;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.SharedPreferencesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.mobiquitytest.database.CityDatabaseContract;
import com.example.mobiquitytest.database.CityDatabaseHelper;
import com.example.mobiquitytest.entities.Main;
import com.example.mobiquitytest.entities.Result;
import com.example.mobiquitytest.network.RetrofitHelper;
import com.example.mobiquitytest.utils.DoubleHelper;
import com.example.mobiquitytest.utils.GlobalVariables;
import com.example.mobiquitytest.utils.MenuHelper;
import com.example.mobiquitytest.utils.SharedPreferencesHelper;
import com.example.mobiquitytest.utils.TempFormatter;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.example.mobiquitytest.database.CityDatabaseContract.MarkerEntry;

import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    // TODO: 6/11/16 Icons in DrawerLayout
    // TODO: 6/11/16 Check if Play Services installed
    // TODO: 6/11/16 Custom marker (it is clickable but a bigger marker would be better)
    // TODO: 6/11/16 Refactor map code
    // TODO: 6/12/16 Save onRotation (Fragment Retention)
    // TODO: 6/12/16 Check internet connection
    // TODO: 6/12/16 Add Snackbar on error
    // TODO: 6/12/16 Load the generated screenshot from the picture taken to Facebook and Twitter
    // TODO: 6/12/16 Consider portrait and landscape adjustments to the picture

    private static final String TAG = "MainActivityTAG_";

    public static final float ZOOM_ZIP_CODE = 12;

    private static final int LOCATION_PERMISSION_CODE = 10;

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private CardView mCardView;
    private GoogleMap mGoogleMap;

    private LinkedHashMap<Marker, Main> mMarkers;

    SupportMapFragment mMapFragment;
    AutocompleteFilter mAutocompleteFilter;
    PlaceAutocompleteFragment mAutocompleteFragment;

    PictureFragment mPictureFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.a_main_toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.a_main_drawer);
        mNavigationView = (NavigationView) findViewById(R.id.a_main_nav);
        mCardView = (CardView) findViewById(R.id.a_main_card);

        mMarkers = new LinkedHashMap<>();

        setSupportActionBar(mToolbar);

        setupDrawer();
        //setupMap();

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.a_main_map_f);
        mMapFragment.getMapAsync(this);

        setupAutocomplete();

        mPictureFragment = (PictureFragment) getSupportFragmentManager().findFragmentById(R.id.picture_fragment);

    }

    private void loadSavedMarkers() {
        CityDatabaseHelper cityDatabaseHelper = new CityDatabaseHelper(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = cityDatabaseHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(MarkerEntry.TABLE_NAME, null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                buildMarkerToPersist(cursor);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void buildMarkerToPersist(Cursor cursor) {
        String address = cursor.getString(cursor.getColumnIndex(MarkerEntry.KEY_ADDRESS));
        String name = cursor.getString(cursor.getColumnIndex(MarkerEntry.KEY_NAME));
        Double longitude = cursor.getDouble(cursor.getColumnIndex(MarkerEntry.KEY_LONGITUDE));
        Double latitude = cursor.getDouble(cursor.getColumnIndex(MarkerEntry.KEY_LATITUDE));
        Double temp = cursor.getDouble(cursor.getColumnIndex(MarkerEntry.KEY_TEMP));
        Double max = cursor.getDouble(cursor.getColumnIndex(MarkerEntry.KEY_MAX));
        Double min = cursor.getDouble(cursor.getColumnIndex(MarkerEntry.KEY_MIN));
        Main aux = new Main(temp, max, min);
        addMarker(new LatLng(latitude, longitude), address, name, false, aux);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (GlobalVariables.getInstance().getFragmentoActivoIndice()==2) {
                switchFragment(1);
            }
            else
            {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.m_settings_unit);
        MenuHelper.setMenuItemTitle(getApplicationContext(), menuItem);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.m_settings_unit) {
            MenuHelper.setMenuItemTitle(getApplicationContext(), item);
            SharedPreferencesHelper.changeUnit(getApplicationContext());
            updateAllMarkers();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mCardView.setVisibility(View.VISIBLE);
        mGoogleMap = googleMap;
        setupMapSettings();
        checkLocationPermissions();
    }

    private void setupMapSettings() {
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.setPadding(0, 200, 0, 0);
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.d(TAG, "onMarkerClick: " + marker);
                Log.d(TAG, "onMarkerClick: " + marker.getId());
                GlobalVariables.getInstance().setPlaceName(marker.getTitle());
                GlobalVariables.getInstance().setPlaceTemperature(marker.getSnippet().toString());
                switchFragment(2);
                return false;
            }
        });
        loadSavedMarkers();
    }

    private void checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Log.d(TAG, "onCreate: " + "Show explanation");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            }
        } else {
            Log.d(TAG, "onCreate: " + "Permission already granted!");
            addLocationServices();
        }
    }

    private void addLocationServices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            final Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    String name = location.getProvider().toUpperCase()
                            + " [" + DoubleHelper.truncateDouble(location.getLatitude(), 4)
                            + "," + DoubleHelper.truncateDouble(location.getLongitude(), 4) + "]";
                    addMarker(new LatLng(location.getLatitude(), location.getLongitude()), name, name, true, null);

                    return false;
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: Good to go!");
                    addLocationServices();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Bad user");
                }
            }
        }
    }

    private void setupDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_gallery) {
                    switchFragment(1);
                }

                if (id == R.id.nav_exit) {
                    finish();
                }

                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void setupAutocomplete() {
        mAutocompleteFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS)
                .build();

        mAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.a_main_autocomplete_f);
        mAutocompleteFragment.setHint(getString(R.string.enter_zip_code));
        mAutocompleteFragment.setFilter(mAutocompleteFilter);
        mAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                if (mGoogleMap == null || place == null) {
                    return;
                }

                addMarker(place.getLatLng(), place.getAddress().toString(), place.getName().toString(), true, null);
            }

            @Override
            public void onError(Status status) {
                Log.e(TAG, "onError: " + status.getStatusMessage());
            }
        });
    }

    private void updateAllMarkers() {
        for (Map.Entry<Marker, Main> entry : mMarkers.entrySet()) {
            Marker marker = entry.getKey();
            Main main = entry.getValue();
            updateMarker(main, marker);
        }
    }

    private void updateMarker(Main main, Marker marker) {
        marker.setSnippet(TempFormatter.formatTemp(getApplicationContext(), main));
        marker.showInfoWindow();
    }

    private void addMarker(LatLng latLng, String address, String name, boolean loadRemote, Main localMain) {
        final Marker marker = createMarker(latLng, address, name);

        if (loadRemote) {
            retrofitCall(latLng, address, name, marker);
        } else {
            updateMarker(localMain, marker);
            mMarkers.put(marker, localMain);
        }
        mGoogleMap.stopAnimation();
        animateCameraMarker(latLng);
    }

    private void retrofitCall(final LatLng latLng, final String address, final String name, final Marker marker) {
        Call<Result> resultCall = RetrofitHelper.buildWeatherCall(getApplicationContext(), latLng.latitude, latLng.longitude);
        resultCall.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                Main main = response.body().getMain();
                updateMarker(main, marker);
                mMarkers.put(marker, main);
                persistMarker(latLng, address, name, main);
                Log.d(TAG, "onResponse: " + marker.getSnippet());
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t);
            }
        });
    }

    @NonNull
    private Marker createMarker(LatLng latLng, String address, String name) {
        final Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(address)
                        .draggable(false)
                        .snippet(getString(R.string.loading_temperature) + name)
        );

        marker.showInfoWindow();
        return marker;
    }

    private void animateCameraMarker(LatLng latLng) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(ZOOM_ZIP_CODE)
                .build();

        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void persistMarker(LatLng latLng, String address, String name, Main main) {
        CityDatabaseHelper usersDatabaseHelper = new CityDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = usersDatabaseHelper.getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(MarkerEntry.KEY_NAME, name);
            values.put(MarkerEntry.KEY_ADDRESS, address);
            values.put(MarkerEntry.KEY_LATITUDE, latLng.latitude);
            values.put(MarkerEntry.KEY_LONGITUDE, latLng.longitude);
            values.put(MarkerEntry.KEY_TEMP, main.getTemp());
            values.put(MarkerEntry.KEY_MAX, main.getTempMax());
            values.put(MarkerEntry.KEY_MIN, main.getTempMin());

            db.insertOrThrow(MarkerEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    public void switchFragment(int index)
    {
        GlobalVariables.getInstance().setFragmentoActivoIndice(index);
        switch (index) {
            case 1:

                mMapFragment.getView().setVisibility(View.VISIBLE);
                mCardView.setVisibility(View.VISIBLE);
                mAutocompleteFragment.getView().setVisibility(View.VISIBLE);
                mPictureFragment.getView().setVisibility(View.INVISIBLE);
                break;
            case 2:
                mMapFragment.getView().setVisibility(View.INVISIBLE);
                mCardView.setVisibility(View.INVISIBLE);
                mAutocompleteFragment.getView().setVisibility(View.INVISIBLE);
                mPictureFragment.getView().setVisibility(View.VISIBLE);
                mPictureFragment.updateViews();
                break;
        }

    }


}
