package edu.ncsu.mobile.traces;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static edu.ncsu.mobile.traces.R.id;
import static edu.ncsu.mobile.traces.R.layout;


public class MapsActivity extends FragmentActivity implements LocationListener,GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient googleAPI;
    private SearchView search;
    private RelativeLayout rel_layout;
    private static final String LOG_APPTAG = "Traces App";
    protected GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private AddressAPIQuery addressQuery = new AddressAPIQuery(null, null, null, null);
    private CoordinateAPIQuery coordinateAPIQuery = new CoordinateAPIQuery(null, null, null, null, null);

    // Sliding (up) panel references
    private EditText mAddressEditText;
    private EditText mRadiusEditText;
    private EditText mFromDateEditText;
    private EditText mUntilDateEditText;
    private SlidingUpPanelLayout mSlidingPanelLayout;

    // DatePicker references (Advanced Search Drawer)
    private DatePickerDialog mFromDatePickerDialog;
    private DatePickerDialog mUntilDatePickerDialog;
    private SimpleDateFormat mDateFormatter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleAPI = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(layout.activity_maps);
        setUpMapIfNeeded();
        mMap.setOnMapLongClickListener(this);

        search = new SearchView(MapsActivity.this);
        rel_layout = (RelativeLayout) findViewById(id.rl);

        // Reference to sliding panel
        mSlidingPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mDateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        // Advanced Search fields
        mAddressEditText = (EditText) findViewById(R.id.addressText);
        mRadiusEditText = (EditText) findViewById(id.radiusText);

        mFromDateEditText = (EditText) findViewById(id.fromDateText);
        mFromDateEditText.setInputType(InputType.TYPE_NULL);

        mUntilDateEditText = (EditText) findViewById(id.untilDateText);
        mUntilDateEditText.setInputType(InputType.TYPE_NULL);

        this.setDateTimeField();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 30, 0, 0);
        search.setQueryHint("Enter Location");
        search.setBackgroundColor(Color.WHITE);
        search.getBackground().setAlpha(205);

        search.setLayoutParams(params);
        rel_layout.addView(search);


        //***setOnQueryTextListener***
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

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
                //Do Nothing
                return false;
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(id.map))
                    .getMap();
        }
    }

    private void centerMapToCurrentLocation() {
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(googleAPI);

        if (myLocation != null) {
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
        } else {
            errorToast("Cannot determine current location.\nLets pretend we work for Google!");
            myLocation = new Location("");
            myLocation.setLatitude(37.4219928);
            myLocation.setLongitude(-122.0840694);
        }

        plotTweetsByDefaultLocation(myLocation);
    }

    private void plotTweetsByDefaultLocation(Location myLocation) {
        coordinateAPIQuery.lat = myLocation.getLatitude()+"";
        coordinateAPIQuery.lng = myLocation.getLongitude()+"";
        coordinateAPIQuery.rad = null;
        coordinateAPIQuery.since = null;
        coordinateAPIQuery.until = null;
        plotTweetsOnMap(new CoordinateGet(), coordinateAPIQuery);
    }

    private void retrieveTweetLocationsAndPlot() {
        // only street is required, the rest can be set to null or empty string
        addressQuery.s = search.getQuery().toString().trim();
        addressQuery.rad = null;
        addressQuery.since = null;
        addressQuery.until = null;
        plotTweetsOnMap(new AddressGet(), addressQuery);
    }

    private void zoomToNewLocation(LatLng loc) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 13.9f));
    }

    // If original is true then gets the full size image URL which can be very large
    private String betterImageURL(String url, boolean original) {
        int s = url.lastIndexOf("_normal.");
        return url.substring(0, s) + (original ? "" : "_bigger") + url.substring(s + 7);
    }

    private void plotTweetsOnMap(BaseGet queryGet, BaseAPIQuery queryData) {
        TracesAPIWrapper resultWrapper;
        TracesAPI result;

        try {
            resultWrapper = queryGet.execute(queryData).get();
        } catch (InterruptedException|ExecutionException t) {
            errorToast("Thread Killed: " + t.getMessage());
            return;
        }

        if (resultWrapper.error != null) {
            errorToast(resultWrapper.error);
            return;
        } else {
            result = resultWrapper.api;
        }

        mMap.clear();
        for (Intel tweet : result.getIntel()) {
            User user = tweet.getUser();
            final String userName = user.getName();
            final String profileImageUrl = betterImageURL(user.getProfileImageUrlHttps(), false);
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
                        Log.e(LOG_APPTAG, "Error adding bitmap marker.", e);
                    }
                }
            });
        }

        edu.ncsu.mobile.traces.Location search_loc = result.getSearchLocation().getLocation();
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

        /* Re-scale the profile image to decent size & return */
        //return Bitmap.createScaledBitmap(output, 120, 120, false);
        //Image is 73x73 so don't want to resize since it looks like crap
        return output;
    }

/* Toast that takes in Error Message. */
    private void errorToast(String error)
    {
        Log.d(LOG_APPTAG, error);
        Toast.makeText(getBaseContext(), error,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapLongClick(final LatLng latLng) {
       //Probably should not put a marker when no results are found
       mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("You long-pressed here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))).setAlpha(0.5f);
        // **Would be Cool if we can get to give a Explosive or Fadeout animation to the marker when tweets load on map**
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                coordinateAPIQuery.lat = latLng.latitude + "";
                coordinateAPIQuery.lng = latLng.longitude + "";
                coordinateAPIQuery.rad = null;
                coordinateAPIQuery.since = null;
                coordinateAPIQuery.until = null;
                plotTweetsOnMap(new CoordinateGet(), coordinateAPIQuery);
            }
        });
    }

    /**
     * Retrieves values from the text fields in the advanced search box,
     * hides the drawer, and submits the search results to be displayed
     *
     * @param view the view
     */
    public void sendSearchValues(View view) {
        addressQuery.s = mAddressEditText.getText().toString();
        addressQuery.rad = mRadiusEditText.getText().toString();
        addressQuery.since = mFromDateEditText.getText().toString();
        addressQuery.until = mUntilDateEditText.getText().toString();

        mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

        plotTweetsOnMap(new AddressGet(), addressQuery);
    }

    @Override
    public void onConnected(Bundle bundle) {
        centerMapToCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onStart() {
        super.onStart();
        googleAPI.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        googleAPI.disconnect();
    }

    private void setDateTimeField() {
        mFromDateEditText.setOnClickListener(this);
        mUntilDateEditText.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        mFromDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mFromDateEditText.setText(mDateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mUntilDatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                mUntilDateEditText.setText(mDateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == mFromDateEditText) {
            mFromDatePickerDialog.show();
        } else if(view == mUntilDateEditText) {
            mUntilDatePickerDialog.show();
        }
    }
}
