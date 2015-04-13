package edu.ncsu.mobile.traces;

import com.goebl.david.Request;

public class CoordinateGet extends BaseGet {

    @Override
    void applyParam(Request req, BaseAPIQuery q) {
        req.param("lat", ((CoordinateAPIQuery) q).lat);
        req.param("lng", ((CoordinateAPIQuery) q).lng);
    }

}