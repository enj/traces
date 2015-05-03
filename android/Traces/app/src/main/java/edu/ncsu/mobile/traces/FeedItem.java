package edu.ncsu.mobile.traces;

/**
 * Created by Tyrone on 4/9/2015.
 */
public class FeedItem {
    public String userName;
    public String thumbnail;
    public String tweet;
    public String retweet;
    public String favs;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTweet() {return tweet; }

    public void setTweet(String tweet) { this.tweet = tweet; }

    public String getRetweet() {return retweet; }

    public void setRetweet(String retweet) { this.retweet = retweet; }

    public String getFavs() { return favs; }

    public void setFavs(String favs) { this.favs = favs; }
}