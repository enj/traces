package edu.ncsu.mobile.traces;

// Determine if we need this at all
// Write associated async task if we decide to keep it
// Need to update API do return exact same data structure before write async task
public class CoordinateAPIQuery extends BaseAPIQuery {

    protected String lat;
    protected String lng;

    public CoordinateAPIQuery(String latitude, String longitude, String radius, String since, String until) {
        full = base + "coordinate";
        lat = latitude;
        lng = longitude;
        setCommonParam(radius, since, until);
    }
}
