package edu.ncsu.mobile.traces;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
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

import com.goebl.david.WebbException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;

import java.util.concurrent.ExecutionException;


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
            zoomToNewLocation(latLng);
        }
    }

    private void plotTweetsByDefaultLocation(Location myLocation) {
        //JSON Data for default location of user based on lat-lng[myLocation.getLatitude/Longitude()]
        coordinateAPIQuery.lat = myLocation.getLatitude()+"";
        coordinateAPIQuery.lng = myLocation.getLongitude()+"";
        coordinateAPIQuery.rad = null;
        coordinateAPIQuery.since = null;
        coordinateAPIQuery.until = null;
        plotTweetsOnMap(new CoordinateGet(), coordinateAPIQuery);
    }

    AddressAPIQuery addressQuery = new AddressAPIQuery(null, null, null, null);
    CoordinateAPIQuery coordinateAPIQuery = new CoordinateAPIQuery(null, null, null, null, null);

    private void retrieveTweetLocationsAndPlot() {
        // only street is required, the rest can be set to null or empty string
        addressQuery.s = search.getQuery().toString().trim();
        addressQuery.rad = null;
        addressQuery.since = null;
        addressQuery.until = null;
        plotTweetsOnMap(new AddressGet(), addressQuery);
    }

    private void zoomToNewLocation(LatLng loc) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
    }

    private void plotTweetsOnMap(BaseGet queryGet, BaseAPIQuery queryData) {
        TracesAPI result;
        try {
            result = queryGet.execute(queryData).get();
        } catch (WebbException we) {
            // Network error -> Figure out how to tell UI
            Log.e(LOG_APPTAG, "Network Error", we);
            return;
        } catch (RuntimeException re) {
            //API error -> Also figure out what to tell UI here -> need Error class
            Log.e(LOG_APPTAG, "API Error", re);
            return;
        } catch (InterruptedException|ExecutionException meh) {
            // Eh? -> no idea
            Log.e(LOG_APPTAG, "Thread killed", meh);
            return;
        }

        for (Intel tweet : result.getIntel()) {
            User user = tweet.getUser();
            final String userName = user.getName();
            final String profileImageUrl = user.getProfileImageUrlHttps();
            final String tweetText = tweet.getText();
            //final String profileLocation = user.getProfileLocation();
            final edu.ncsu.mobile.traces.Location loc = tweet.getLocation();

            final LatLng userPos = new LatLng(
                    loc.getLat(),
                    loc.getLng()
            );
                 /* Create markers for the tweet data.
                    Must run this on the UI thread since it's a UI operation.
                 */
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        Bitmap bmImg = Ion.with(getApplicationContext())
                                .load(profileImageUrl).asBitmap().get();
                        Bitmap mapMarkerImg = getCircleCroppedBitmap(bmImg);
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromBitmap(mapMarkerImg))
                                .title(userName)
                                .snippet(tweetText)
                                .position(userPos));
                    } catch (Exception e) {
                        Log.e(LOG_APPTAG, "Error processing JSON", e);
                    }
                }
            });
        }

        SearchLocation search_loc = result.getSearchLocation();
        zoomToNewLocation(new LatLng(search_loc.getLat(), search_loc.getLng()));
    }

    private Bitmap getCircleCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
//      canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

     //return output;

        /* Yay!! Lets re-scale the profile image to decent size & return */
        Bitmap scaledBitmapOutput = Bitmap.createScaledBitmap(output, 100, 100, false);
        return scaledBitmapOutput;
    }


/* Toast that takes in Method name & Error Message. */
    private void errorToast(String method, String errorMessage)
    {
        Log.d("EXCEPTION: " + method,  errorMessage);


        Toast.makeText(getBaseContext(),method+":->"+errorMessage,
                Toast.LENGTH_SHORT).show();


        /*
        //Here's how to use :
            catch(Exception e)
            {
                errorToast("methodName", e.getMessage());
            }
            */
    }

}
