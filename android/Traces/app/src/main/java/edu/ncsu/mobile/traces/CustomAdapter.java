package edu.ncsu.mobile.traces;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Akhil on 15/1/2015.
 */
public class CustomAdapter extends BaseAdapter {


    private static final String TAG = "MYTAG";
    private final Context context;
    private final ArrayList<CustomMarker> arrayList;
    private final LayoutInflater layoutInflater;
    private int counter;

    public CustomAdapter(Context context, ArrayList<CustomMarker> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    //Will be called once for each item in list
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            //1. Inflate view from your custom xml file
            view = layoutInflater.inflate(R.layout.custom_listview_item, null);
            // Dont pass 'parent' here , not needed
            Log.d(TAG, "New view created :" + counter++);


            final TextView textView_UserName = (TextView) view.findViewById(R.id.listView_UserTitle);
            final TextView textView_TweetContent = (TextView) view.findViewById(R.id.listView_tweet);
            final ImageView imageView_Profile = (ImageView) view.findViewById(R.id.listView_profileImage);
            final TextView textView_RtCount = (TextView) view.findViewById(R.id.textView_RtCount);
            final TextView textView_FavoriteCount = (TextView) view.findViewById(R.id.textView_FavoriteCount);
//            final WebView wv_TweetContent = (WebView) view.findViewById(R.id.webView_TweetContent);
                viewHolder = new ViewHolder(textView_UserName, imageView_Profile, textView_TweetContent, textView_RtCount, textView_FavoriteCount);
//                viewHolder = new ViewHolder(textView_UserName, imageView_Profile, textView_TweetContent, textView_RtCount, textView_FavoriteCount, wv_TweetContent);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            Log.d(TAG, "View recycled!");
            viewHolder = (ViewHolder) view.getTag();

        }

        //2. Associate data with the UI elements
        viewHolder.listView_UserTitle.setText(arrayList.get(position).getmUserName());
        viewHolder.listView_tweet.setText(arrayList.get(position).getmTweetText());

        Bitmap bmImg = null;
        try {
            bmImg = Ion.with(this.context)
                    .load(arrayList.get(position).getmProfileImgHttpUrl()).asBitmap().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int colorValue = Color.LTGRAY;
        Bitmap mapMarkerImg = new MapsActivity().getCircleCroppedBitmap(bmImg, colorValue);
        viewHolder.listView_profileImage.setImageBitmap(mapMarkerImg);

        viewHolder.textView_RtCount.setText(arrayList.get(position).getRetweetCount() + "");
        viewHolder.textView_FavoriteCount.setText(arrayList.get(position).getFavoriteCount() + "");

//
//        if (!arrayList.get(position).getDataUrl().equalsIgnoreCase("")) {
//
//        /*Web View Load */
//            // Let's display the progress in the activity title bar, like the
//            // browser app does.
//            viewHolder.webView_TweetContent.getSettings().setJavaScriptEnabled(true);
//
//            viewHolder.webView_TweetContent.getSettings().setUseWideViewPort(true);
//            viewHolder.webView_TweetContent.getSettings().setLoadWithOverviewMode(true);
//            viewHolder.webView_TweetContent.getSettings().setDisplayZoomControls(true);
//
//            viewHolder.webView_TweetContent.setWebViewClient(new WebViewClient() {
//                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                    Toast.makeText(context, "Oh no! " + description, Toast.LENGTH_SHORT).show();
//                }
//            });
//            viewHolder.webView_TweetContent.loadUrl(arrayList.get(position).getDataUrl());
//            viewHolder.webView_TweetContent.setVisibility(View.VISIBLE);
//        }
//        else{
//            viewHolder.webView_TweetContent.setVisibility(View.GONE);
//        }
//
        //3. Return the inflated view here
        return view;
    }

    /**
     * calling findViewByIds() is expensive. ViewHolder will help us avoid it.
     */
    class ViewHolder {

        TextView listView_UserTitle;
        TextView listView_tweet;
        ImageView listView_profileImage;

        TextView textView_RtCount;
        TextView textView_FavoriteCount;
//        WebView webView_TweetContent;

        public ViewHolder(TextView tv_UserName, ImageView imageView, TextView tv_TweetContent, TextView tv_RtCount, TextView tv_FavCount) {
            this.listView_UserTitle = tv_UserName;
            this.listView_profileImage = imageView;
            this.listView_tweet = tv_TweetContent;
            this.textView_RtCount = tv_RtCount;
            this.textView_FavoriteCount = tv_FavCount;
//            this.webView_TweetContent = wv_TweetContent;
        }
    }

}
