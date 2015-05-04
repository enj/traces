package edu.ncsu.mobile.traces;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.app.ActionBarDrawerToggle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.koushikdutta.ion.Ion;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static edu.ncsu.mobile.traces.R.id;
import static edu.ncsu.mobile.traces.R.layout;


public class MapsActivity extends FragmentActivity implements LocationListener,GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private GoogleApiClient googleAPI;
    private boolean firstRun;
    private SearchView search;
    private RelativeLayout rel_layout;
    private static final String LOG_APPTAG = "Traces App";
    protected GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected HashMap<Marker, CustomMarker> mMarkersHashMap;
    private ArrayList<CustomMarker> customMarkersArray = new ArrayList<>();
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

    // Drawer layout
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
//    private String[] mDrawerStrings;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private ArrayList<Intel> mTweetIntel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstRun = true;
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

                if (query.trim().isEmpty()) {
                    Toast.makeText(getBaseContext(), "Enter Location",
                            Toast.LENGTH_SHORT).show();
                } else {
                    retrieveTweetLocationsAndPlot();
                    search.setQuery("", false);
                    search.setIconified(true);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do Nothing
                return false;
            }
        });

        // set listener on keyboard search button for the address field
        mAddressEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    sendSearchValues(findViewById(v.getId()));
                    return true;
                }
                return false;
            }
        });

        // set listener on keyboard radius button for the radius field
        mRadiusEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideSoftKeyboard();
                    mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    sendSearchValues(findViewById(v.getId()));
                    return true;
                }
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

        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                    marker.showInfoWindow();
                    return true;
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
        }

    }

    private void centerMapToCurrentLocation() {

        // only get location once per app run
                       if (!firstRun)
                     return;

        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(googleAPI);
        if (myLocation != null) {
            double latitude = myLocation.getLatitude();
            double longitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(latLng));
        } else {
            errorToast("Cannot determine current location.\n Lets pretend we work for Google!");
            myLocation = new Location("");
            myLocation.setLatitude(37.4219928);
            myLocation.setLongitude(-122.0840694);
        }

        plotTweetsByDefaultLocation(myLocation);
    }

    private void plotTweetsByDefaultLocation(Location myLocation) {
        coordinateAPIQuery.lat = myLocation.getLatitude() + "";
        coordinateAPIQuery.lng = myLocation.getLongitude() + "";
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
        int s = url.lastIndexOf("_normal");
        return (url.substring(0, s) + (original ? "" : "_bigger")) + url.substring(s + 7);
    }

    private void plotTweetsOnMap(BaseGet queryGet, BaseAPIQuery queryData) {
        try {
            TracesAPIWrapper resultWrapper;
            TracesAPI result;

            try {
                resultWrapper = queryGet.execute(queryData).get();
            } catch (InterruptedException | ExecutionException t) {
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

            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mTweetIntel = new ArrayList<Intel>();//String[result.getIntel().size()];
            final String listTitle = result.getSearchLocation().getAddress();

            // Initialize the HashMap for Markers and MyMarker object
            mMarkersHashMap = new HashMap<>();
            customMarkersArray = new ArrayList<>();
            for (Intel tweet : result.getIntel()) {
                User user = tweet.getUser();
                final String userName = user.getName();
                final String profileImageUrl = betterImageURL(user.getProfileImageUrlHttps(), false);
                final String tweetText = tweet.getText();
                final long retweetCount = tweet.getRetweetCount();
                final String dataUrl = user.getUrl();
                final long favCount = tweet.getFavoriteCount();
                //final String profileLocation = user.getProfileLocation();
                final edu.ncsu.mobile.traces.Location loc = tweet.getLocation();
                mTweetIntel.add(tweet);

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

                            customMarkersArray.add(new CustomMarker(userName, tweetText, profileImageUrl, userPos, retweetCount, favCount, dataUrl));
                        } catch (Exception e) {
                            Log.e(LOG_APPTAG, "Error adding bitmap marker.", e);
                        }
                    }
                });
            }

            mTitle = mDrawerTitle = getTitle();
            //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerList = (ListView) findViewById(R.id.left_drawer);
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

            //this.getActionBar().setDisplayHomeAsUpEnabled(true);
            //this.getActionBar().setHomeButtonEnabled(true);

            final CustomAdapter adapter = new CustomAdapter(getApplicationContext(), customMarkersArray);
            mDrawerList.setAdapter(adapter);

            mDrawerList.bringToFront();
            mDrawerList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // TODO Auto-generated method stub
                    if (scrollState == SCROLL_STATE_IDLE) {
                        mDrawerList.bringToFront();
//                    mDrawerLayout.requestLayout();
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    // TODO Auto-generated method stub

                }
            });

            plotMarkers(customMarkersArray);
            edu.ncsu.mobile.traces.Location search_loc = result.getSearchLocation().getLocation();
            zoomToNewLocation(new LatLng(search_loc.getLat(), search_loc.getLng()));
        } catch (Exception e) {
            errorToast("Unknown error: " + e.getClass().getSimpleName());
        }
    }

    private void plotMarkers(ArrayList<CustomMarker> customMarkersArray) {
        if(customMarkersArray.size() > 0)
        {
            for (CustomMarker myMarker : customMarkersArray)
            {
                Bitmap bmImg = null;
                try {
                    bmImg = Ion.with(getApplicationContext())
                            .load(myMarker.getmProfileImgHttpUrl()).asBitmap().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }


                int ColorIndicatorValue;
                //ColorIndicatorValue Should vary based on retweet Count/Our Custom defined Formula
                if (myMarker.getRetweetCount() > 5) {
                    ColorIndicatorValue = Color.RED;
                } else if (myMarker.getRetweetCount() >= 1 && myMarker.getRetweetCount() <= 5) {
                    ColorIndicatorValue = Color.YELLOW;
                } else {
                    ColorIndicatorValue = Color.GREEN;
                }

                Bitmap mapMarkerImg = getCircleCroppedBitmap(bmImg, ColorIndicatorValue);
                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(mapMarkerImg))
                        .title(myMarker.getmUserName())
                        .snippet(myMarker.getmTweetText())
                        .position(myMarker.getmLocation());

                Marker currentMarker = mMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, myMarker);
                mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
            }
        }
    }


    public boolean bounceMarker(final Marker marker){

        //Make the marker bounce
        final Handler handler = new Handler();

        final long startTime = SystemClock.uptimeMillis();
        final long duration = 2000;

        Projection proj = mMap.getProjection();
        final LatLng markerLatLng = marker.getPosition();
        Point startPoint = proj.toScreenLocation(markerLatLng);
        startPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - startTime;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lng = t * markerLatLng.longitude + (1 - t) * startLatLng.longitude;
                double lat = t * markerLatLng.latitude + (1 - t) * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });

        //return false; //have not consumed the event
        return true; //have consumed the event
    }



    public Bitmap getCircleCroppedBitmap(Bitmap bitmap,int colorIndicator) {
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
//        return Bitmap.createScaledBitmap(output, 120, 120, false);
        //Image is 73x73 so don't want to resize since it looks like crap

        Bitmap borderedOutput = getColorBorderedBitmapVersion(output, colorIndicator);

        return Bitmap.createScaledBitmap(borderedOutput, 100, 100, false);
    }

    private Bitmap getColorBorderedBitmapVersion(Bitmap bitmap,int color) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int radius = Math.min(h / 2, w / 2);
        Bitmap output = Bitmap.createBitmap(w + 8, h + 8, Bitmap.Config.ARGB_8888);

        Paint p = new Paint();
        p.setAntiAlias(true);

        Canvas c = new Canvas(output);
        c.drawARGB(0, 0, 0, 0);
        p.setStyle(Paint.Style.FILL);

        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);

        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        c.drawBitmap(bitmap, 4, 4, p);
        p.setXfermode(null);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(color);
        p.setStrokeWidth(3);
        c.drawCircle((w / 2) + 4, (h / 2) + 4, radius, p);
        return output;
    }

    /* Toast that takes in Error Message. */
    private void errorToast(String error) {
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
        this.verifyDates();

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
        firstRun = false;
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

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            bounceMarker(marker);

            // * Felt it'd be cool if we Auto-center Marker position to center of the map screen
            int zoom = (int)mMap.getCameraPosition().zoom;
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude + (double)90/Math.pow(2, zoom), marker.getPosition().longitude), zoom);
            mMap.animateCamera(cu);

            View v  = getLayoutInflater().inflate(layout.info_window, null);
            CustomMarker myMarker = mMarkersHashMap.get(marker);
            ImageView markerIcon = (ImageView) v.findViewById(id.popUpImageView);
            TextView markerTweet = (TextView)v.findViewById(id.popUpTweetContent);
            TextView markerTitle = (TextView)v.findViewById(id.popUpTitle);

//            TextView markerFavoriteCount = (TextView)v.findViewById(id.textFavorite);
            Bitmap bmImg = null;
            try {
                bmImg = Ion.with(getApplicationContext())
                        .load(myMarker.getmProfileImgHttpUrl()).asBitmap().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            int colorValue = Color.LTGRAY;
            Bitmap mapMarkerImg = getCircleCroppedBitmap(bmImg,colorValue);
            markerIcon.setImageBitmap(mapMarkerImg);
            markerTitle.setText(myMarker.getmUserName());
            markerTweet.setText(myMarker.getmTweetText());
//            markerFavoriteCount.setText(""+myMarker.getFavoriteCount());
            return v;
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        /*
        Fragment fragment = new PlanetFragment();
        Bundle args = new Bundle();
        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
        */
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        //getActionBar().setTitle(mTitle);
    }

    @Override
    public void onBackPressed() {

        if (mSlidingPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
            mSlidingPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }



    private void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    private void verifyDates() {
        String fromDate = mFromDateEditText.getText().toString().trim();
        String untilDate = mUntilDateEditText.getText().toString().trim();

        boolean fromDateFilled = (fromDate != null) && (!fromDate.isEmpty());
        boolean untilDateFilled = (untilDate != null) && (!untilDate.isEmpty());
        boolean bothDatesFilled = fromDateFilled && untilDateFilled;

        if (bothDatesFilled && fromDate.equals(untilDate)) {
            String[] dateArray = untilDate.split("-");
            int year = Integer.valueOf(dateArray[0]);
            int month = Integer.valueOf(dateArray[1]) - 1; //months are 0-based
            int day = Integer.valueOf(dateArray[2]);

            System.out.println("Year: " + year + " Month: " + month + " Day: " + day);

                    Calendar date = Calendar.getInstance();
            date.set(year, month, day);

            System.out.println("Before: " + date.toString());

            date.add(Calendar.DAY_OF_MONTH, 1);

            System.out.println("After: " + date.toString());

            System.out.println("Formatted: " + mDateFormatter.format(date.getTime()));

            mUntilDateEditText.setText(mDateFormatter.format(date.getTime()));
        }
    }




}
