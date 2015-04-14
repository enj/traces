package edu.ncsu.mobile.traces;

public class TracesAPIWrapper {

    protected TracesAPI api;
    protected String error;

    public TracesAPIWrapper(TracesAPI api, String error) {
        this.api = api;
        this.error = error;
    }
}
