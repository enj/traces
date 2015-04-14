package edu.ncsu.mobile.traces;

public class SavedTweets {

    private Long columnID;
    private double latitude;
    private double longitude;
    private String createdAt;
    private Long favoriteCount;
    private Long tweetID;
    private Long retweetCount;
    private String text;
    private String description;
    private Long userID;
    private String userProfileLocation;
    private String userName;
    private String profileImageUrlHttps;
    private String userScreenName;
    private String userURL;
    private Integer possiblySensitive;
    private Double distance;

    public Long getColumnID() {
        return columnID;
    }

    public void setColumnID(Long columnID) {
        this.columnID = columnID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(Long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public Long getTweetID() {
        return tweetID;
    }

    public void setTweetID(Long tweetID) {
        this.tweetID = tweetID;
    }

    public Long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(Long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getUserID() {
        return userID;
    }

    public void setUserID(Long userID) {
        this.userID = userID;
    }

    public String getUserProfileLocation() {
        return userProfileLocation;
    }

    public void setUserProfileLocation(String userProfileLocation) {
        this.userProfileLocation = userProfileLocation;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileImageUrlHttps() {
        return profileImageUrlHttps;
    }

    public void setProfileImageUrlHttps(String profileImageUrlHttps) {
        this.profileImageUrlHttps = profileImageUrlHttps;
    }

    public String getUserScreenName() {
        return userScreenName;
    }

    public void setUserScreenName(String userScreenName) {
        this.userScreenName = userScreenName;
    }

    public String getUserURL() {
        return userURL;
    }

    public void setUserURL(String userURL) {
        this.userURL = userURL;
    }

    public Integer getPossiblySensitive() {
        return possiblySensitive;
    }

    public void setPossiblySensitive(Integer possiblySensitive) {
        this.possiblySensitive = possiblySensitive;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return tweetID.toString();
    }
}
