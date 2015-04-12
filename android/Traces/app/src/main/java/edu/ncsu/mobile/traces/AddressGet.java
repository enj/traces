package edu.ncsu.mobile.traces;


import android.os.AsyncTask;

import com.goebl.david.Response;
import com.goebl.david.Webb;
import com.google.gson.Gson;

public class AddressGet extends AsyncTask<AddressAPIQuery, Void, TracesAPI> {

    Webb r = Webb.create();

    protected TracesAPI doInBackground(AddressAPIQuery... query) {

        AddressAPIQuery q = query[0];
        Response<String> d;

        d = r.get(q.full)
                .param("s", q.s)
                .param("rad", q.rad)
                .param("since", q.since)
                .param("until", q.until)
                .asString();

        if (d.isSuccess()) {
            return new Gson().fromJson(d.getBody(), TracesAPI.class);
        } else {
            throw new RuntimeException((String) d.getErrorBody());
        }

    }
}