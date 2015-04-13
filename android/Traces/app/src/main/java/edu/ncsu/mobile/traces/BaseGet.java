package edu.ncsu.mobile.traces;


import android.os.AsyncTask;

import com.goebl.david.Request;
import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.google.gson.Gson;

public abstract class BaseGet extends AsyncTask<BaseAPIQuery, Void, TracesAPI> {

    private static Webb r = Webb.create();
    private static Request req;
    private static Response<String> resp;
    private static Gson gson = new Gson();

    abstract void applyParam(Request req, BaseAPIQuery q);

    protected TracesAPI doInBackground(BaseAPIQuery... query) {

        BaseAPIQuery q = query[0];

        req = r.get(q.full)
               .param("rad", q.rad)
               .param("since", q.since)
               .param("until", q.until);

        applyParam(req, q);
        resp = req.asString();

        if (resp.isSuccess()) {
            return gson.fromJson(resp.getBody(), TracesAPI.class);
        } else {
            throw new RuntimeException((String) resp.getErrorBody());
        }

    }
}