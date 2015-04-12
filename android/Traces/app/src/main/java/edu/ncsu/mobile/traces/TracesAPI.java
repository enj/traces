
package edu.ncsu.mobile.traces;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class TracesAPI {

    @SerializedName("search_location")
    @Expose
    private SearchLocation searchLocation;
    @Expose
    private List<Intel> intel = new ArrayList<Intel>();

    /**
     * 
     * @return
     *     The searchLocation
     */
    public SearchLocation getSearchLocation() {
        return searchLocation;
    }

    /**
     * 
     * @param searchLocation
     *     The search_location
     */
    public void setSearchLocation(SearchLocation searchLocation) {
        this.searchLocation = searchLocation;
    }

    /**
     * 
     * @return
     *     The intel
     */
    public List<Intel> getIntel() {
        return intel;
    }

    /**
     * 
     * @param intel
     *     The intel
     */
    public void setIntel(List<Intel> intel) {
        this.intel = intel;
    }

}
