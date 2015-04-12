package edu.ncsu.mobile.traces;

public class AddressAPIQuery extends BaseAPIQuery {

    protected String s;

    public AddressAPIQuery(String street, String radius, String since, String until) {
        full = base + "address";
        s = street;
        setCommonParam(radius, since, until);
    }
}
