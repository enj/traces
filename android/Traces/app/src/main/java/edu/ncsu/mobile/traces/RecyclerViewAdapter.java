package edu.ncsu.mobile.traces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;


public class RecyclerViewAdapter extends RecyclerView.Adapter<FeedListRowHolder> {

    private List<FeedItem> feedItemList;

    public RecyclerViewAdapter(List<FeedItem> feedItemList) {
        //super(feedItemList);
        this.feedItemList = feedItemList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public FeedListRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item, null);
        FeedListRowHolder mh = new FeedListRowHolder(v);
        return mh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(FeedListRowHolder feedListRowHolder, int i) {
        FeedItem feedItem = feedItemList.get(i);
        //feedListRowHolder.thumbnail.getImageBitmap(Html.fromHtml(feedItem.getThumbnail()));
        feedListRowHolder.userName.setText(Html.fromHtml(feedItem.getUserName()));
        feedListRowHolder.tweet.setText(Html.fromHtml(feedItem.getTweet()));
        feedListRowHolder.retweet.setText(Html.fromHtml(feedItem.getRetweet()));
        feedListRowHolder.favs.setText(Html.fromHtml(feedItem.getFavs()));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    //Converts image URL into bitmap
    /* public Bitmap getImageBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }
        return bm;
    } */
}
