
package edu.ncsu.mobile.traces;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Intel {

    @Expose
    private Location location;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("favorite_count")
    @Expose
    private Long favoriteCount;
    @Expose
    private Long id;
    @SerializedName("possibly_sensitive")
    @Expose
    private Boolean possiblySensitive;
    @SerializedName("retweet_count")
    @Expose
    private Long retweetCount;
    @Expose
    private String text;
    @Expose
    private User user;
    @Expose
    private Double distance;

    /**
     * 
     * @return
     *     The location
     */
    public Location getLocation() {
        return location;
    }

    /**
     * 
     * @param location
     *     The location
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * 
     * @return
     *     The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * 
     * @param createdAt
     *     The created_at
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * 
     * @return
     *     The favoriteCount
     */
    public Long getFavoriteCount() {
        return favoriteCount;
    }

    /**
     * 
     * @param favoriteCount
     *     The favorite_count
     */
    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    /**
     * 
     * @return
     *     The id
     */
    public Long getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The possiblySensitive
     */
    public Boolean getPossiblySensitive() {
        return possiblySensitive;
    }

    /**
     * 
     * @param possiblySensitive
     *     The possibly_sensitive
     */
    public void setPossiblySensitive(Boolean possiblySensitive) {
        this.possiblySensitive = possiblySensitive;
    }

    /**
     * 
     * @return
     *     The retweetCount
     */
    public Long getRetweetCount() {
        return retweetCount;
    }

    /**
     * 
     * @param retweetCount
     *     The retweet_count
     */
    public void setRetweetCount(Long retweetCount) {
        this.retweetCount = retweetCount;
    }

    /**
     * 
     * @return
     *     The text
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param text
     *     The text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * 
     * @return
     *     The user
     */
    public User getUser() {
        return user;
    }

    /**
     * 
     * @param user
     *     The user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 
     * @return
     *     The distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * 
     * @param distance
     *     The distance
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }

}
