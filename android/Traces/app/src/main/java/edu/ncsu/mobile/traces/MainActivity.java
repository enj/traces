package edu.ncsu.mobile.traces;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends ActionBarActivity {

    Webb r = Webb.create();
    String base = "https://stalkhere.appspot.com/api/address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new jsonGet().execute(base);
    }

    private class jsonGet extends AsyncTask<String, Void, JSONObject> {

        protected JSONObject doInBackground(String... urls) {

            JSONObject result = r
                    .get(urls[0])
                    .param("s", "ncsu")
                    .ensureSuccess()
                    .asJsonObject()
                    .getBody();

            return result;
        }

        protected void onPostExecute(JSONObject result) {

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
                TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);
                jsonParsed.setMovementMethod(new ScrollingMovementMethod());
                jsonParsed.setText(output);

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
