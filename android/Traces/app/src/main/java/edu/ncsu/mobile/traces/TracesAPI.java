
package edu.ncsu.mobile.traces;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TracesAPI {

    private SearchLocation searchLocation;
    private List<Intel> intel = new ArrayList<Intel>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

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

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
