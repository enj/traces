package edu.ncsu.mobile.traces;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MapsActivity extends FragmentActivity implements LocationListener {

    private SearchView search;
    private RelativeLayout rel_layout;
    private static final String LOG_APPTAG = "Traces App";
    Location myLocation = null;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();


        search = new SearchView(MapsActivity.this);
        rel_layout = (RelativeLayout) findViewById(R.id.rl);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                (int) RadioGroup.LayoutParams.WRAP_CONTENT, (int) RadioGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 30, 0, 0);
        search.setQueryHint("Enter Location");
        search.setBackgroundColor(Color.WHITE);
        search.getBackground().setAlpha(205);

        search.setLayoutParams(params);
        rel_layout.addView(search);

        //***setOnQueryTextFocusChangeListener***
        search.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

                Toast.makeText(getBaseContext(), String.valueOf(hasFocus),
                        Toast.LENGTH_SHORT).show();
            }
        });

        //***setOnQueryTextListener***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                // TODO Auto-generated method stub

                if(query.trim().isEmpty()){
                    Toast.makeText(getBaseContext(), "Enter Location",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    retrieveTweetLocationsAndPlot();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // TODO Auto-generated method stub

                Toast.makeText(getBaseContext(), newText,
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        centerMapToCurrentLocation();
        new Thread(new Runnable() {
            public void run() {
                try {
                    plotTweetsByDefaultLocation(myLocation);
                } catch (Exception e) {
                    Log.e(LOG_APPTAG, "Cannot retrieve geo locations", e);
                    return;
                }
            }
        }).start();


    }

    private void centerMapToCurrentLocation() {
        //Zoom to current Location
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        myLocation = locationManager.getLastKnownLocation(bestProvider);
        if (myLocation != null) {
            onLocationChanged(myLocation);
            locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    private void plotTweetsByDefaultLocation(Location myLocation) {
        //JSON Data for default location of user based on lat-lng[myLocation.getLatitude/Longitude()]
        JSONArray tweetsData = null;
    }

    private void retrieveTweetLocationsAndPlot() {
        String location = search.getQuery().toString().trim(); // temp search field placed North for testing.

        // Get JSON Data based on search field values or use default -> ("ncsu",5,"",null)

        JSONArray tweetsData = null;
        int tweetDataLength = 0; //temp variable :will be replaced by -> tweetsData.length()

        for (int i = 0; i < tweetDataLength; i++) {
            JSONObject tweet = null;
            JSONObject user = null;
            try {
                tweet = tweetsData.getJSONObject(i);
                user = tweet.getJSONObject("user");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String userName = user.optString("name");
            final String profileImageUrl = user.optString("profile_image_url_https");
            final String tweetText = tweet.optString("text");
            final String profileLocation = tweet.optString("profile_location");

            final LatLng userPos = new LatLng(
                    Double.parseDouble(tweet.optString("lat")),
                    Double.parseDouble(tweet.optString("lng")));
                 /* Create markers for the tweet data.
                    Must run this on the UI thread since it's a UI operation.
                 */
            runOnUiThread(new Runnable() {
                public void run() {
                    try {

                        Bitmap bmImg = Ion.with(getApplicationContext())
                                .load(profileImageUrl).asBitmap().get();

                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(bmImg))
                                .title(userName)
                                .snippet(profileLocation)
                                .position(userPos));

                    } catch (Exception e) {
                        Log.e(LOG_APPTAG, "Error processing JSON", e);
                    }
                }
            });


        }


    }

}
