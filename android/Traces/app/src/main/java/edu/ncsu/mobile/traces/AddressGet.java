package edu.ncsu.mobile.traces;

import com.goebl.david.Request;

public class AddressGet extends BaseGet {

    @Override
    void applyParam(Request req, BaseAPIQuery q) {
        req.param("s", ((AddressAPIQuery) q).s);
    }

}