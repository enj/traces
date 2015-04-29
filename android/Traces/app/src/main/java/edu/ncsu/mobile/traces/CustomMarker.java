package edu.ncsu.mobile.traces;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Vikas on 4/29/2015.
 */
public class CustomMarker
{
    private String mUserName;
    private String mProfileImgHttpUrl;
    private String mTweetText;
    private LatLng mLocation;
    private long retweetCount;
    private long favoriteCount;

    public CustomMarker(String userName, String tweetContent, String profileImgUrl,LatLng usrLocation,long rtCount, long favCount)
    {
        this.mUserName = userName;
        this.mLocation = usrLocation;
        this.mTweetText = tweetContent;
        this.mProfileImgHttpUrl = profileImgUrl;
        this.setRetweetCount(rtCount);
        this.setFavoriteCount(favCount);

    }

    public String getmUserName()
    {
        return mUserName;
    }

    public void setmUserName(String mUserName)
    {
        this.mUserName = mUserName;
    }

    public String getmProfileImgHttpUrl()
    {
        return mProfileImgHttpUrl;
    }

    public void setmProfileImgHttpUrl(String icon)
    {
        this.mProfileImgHttpUrl = icon;
    }

    public void setmLocation(LatLng userLocation)
    {
        this.mLocation = userLocation;
    }


    public LatLng getmLocation()
    {
        return mLocation;
    }

    public void setmTweetText(String mTweetText) {
        this.mTweetText = mTweetText;
    }


    public String getmTweetText() {
        return mTweetText;
    }


    public long getRetweetCount() {
        return retweetCount;
    }

    public void setRetweetCount(long retweetCount) {
        this.retweetCount = retweetCount;
    }

    public long getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(long favoriteCount) {
        this.favoriteCount = favoriteCount;
    }
}
