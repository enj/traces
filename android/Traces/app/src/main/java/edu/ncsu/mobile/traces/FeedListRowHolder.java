package edu.ncsu.mobile.traces;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class FeedListRowHolder extends RecyclerView.ViewHolder{
    public ImageView thumbnail;
    public TextView userName;
    public TextView tweet;
    public TextView retweet;
    public TextView favs;


    public FeedListRowHolder(View view) {
        super(view);
        this.thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
        this.userName = (TextView) view.findViewById(R.id.title);
        this.tweet = (TextView) view.findViewById(R.id.tweet);
        this.retweet = (TextView) view.findViewById(R.id.retweet);
        this.favs = (TextView) view.findViewById(R.id.favs);
    }

}
