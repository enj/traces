package edu.ncsu.mobile.traces;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    public static abstract class BaseAPIQuery {

        protected static final String base = "https://stalkhere.appspot.com/api/";
        protected String full;
        protected String since;
        protected String until;
        protected String rad; // determine how to get default values from DB so user can have preference

        protected void setCommonParam(String radius, String since, String until) {
            rad = radius;
            this.since = since;
            this.until = until;
        }
    }

    public static class AddressAPIQuery extends BaseAPIQuery {

        protected String s;

        public AddressAPIQuery(String street, String radius, String since, String until) {
            full = base + "address";
            s = street;
            setCommonParam(radius, since, until);
        }
    }

    // Determine if we need this at all
    // Write associated async task if we decide to keep it
    // Need to update API do return exact same data structure before write async task
    private class CoordinateAPUQuery extends BaseAPIQuery {
        
        protected String lat;
        protected String lng;
        
        public CoordinateAPUQuery(String latitude, String longitude, String radius, String since, String until) {
            full = base + "coordinate";
            lat = latitude;
            lng = longitude;
            setCommonParam(radius, since, until);
        }
    }

    Webb r = Webb.create();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // only street is required, the rest can be set to null or empty string
        new addressGet().execute(new AddressAPIQuery("ncsu", "3", "", null));
    }

    private class addressGet extends AsyncTask<AddressAPIQuery, Void, JSONObject> {

        protected JSONObject doInBackground(AddressAPIQuery... query) {
            
            AddressAPIQuery q = query[0];
            Response<JSONObject> d;

            try {
                d = r.get(q.full)
                     .param("s", q.s)
                     .param("rad", q.rad)
                     .param("since", q.since)
                     .param("until", q.until)
                     .asJsonObject();
            } catch (WebbException e) {
                return null;
            }

            if (d.isSuccess()) {
                return d.getBody();
            } else {
                try {
                    return (JSONObject) d.getErrorBody();
                } catch (ClassCastException e) {
                    return null;
                }
            }

        }

        protected void onPostExecute(JSONObject result) {

            if (result == null) {
                updateText("Network Error");
                return;
            }

            try {
                updateText(result.getString("Error"));
                return;
            } catch (JSONException e) {
                //no JSON error so keep going
            }

            try {

                JSONArray tweets = result.getJSONArray("intel");
                String output = "";
                for (int i = 0; i < tweets.length(); i++) {
                    JSONObject tweet = tweets.getJSONObject(i);
                    JSONObject user = tweet.getJSONObject("user");

                    String name = user.optString("name") + "\n";
                    String text = tweet.optString("text") + "\n\n";
                    output += name + text;
                }

                updateText(output);

            } catch (JSONException e) {
                updateText(e.getMessage());
            }

        }

        protected void updateText(String output) {
            TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);
            jsonParsed.setMovementMethod(new ScrollingMovementMethod());
            jsonParsed.setText(output);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
