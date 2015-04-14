package edu.ncsu.mobile.traces;

import android.os.AsyncTask;
import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.goebl.david.WebbException;
import com.google.gson.Gson;

public abstract class BaseGet extends AsyncTask<BaseAPIQuery, Void, TracesAPIWrapper> {

    private static Webb r = Webb.create();
    private static Request req;
    private static Response<String> resp;
    private static Gson gson = new Gson();
    private static TracesAPIWrapper result = new TracesAPIWrapper(null, null);

    abstract void applyParam(Request req, BaseAPIQuery q);

    protected TracesAPIWrapper doInBackground(BaseAPIQuery... query) {

        BaseAPIQuery q = query[0];

        req = r.get(q.full)
               .param("rad", q.rad)
               .param("since", q.since)
               .param("until", q.until);

        applyParam(req, q);

        try {
            resp = req.asString();

            if (resp.isSuccess()) {
                result.api = gson.fromJson(resp.getBody(), TracesAPI.class);
                result.error = null;
            } else {
                result.api = null;
                result.error = "API Error: " + resp.getErrorBody();
            }

        } catch (WebbException we) {
            result.api = null;
            result.error = "Network Error: " + we.getMessage();
        }

        return result;
    }
}