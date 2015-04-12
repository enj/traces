package edu.ncsu.mobile.traces;

public abstract class BaseAPIQuery {

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

