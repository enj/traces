package edu.ncsu.mobile.traces;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

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
        if(convertView ==null) {
            //1. Inflate view from your custom xml file
            view = layoutInflater.inflate(R.layout.custom_listview_item, null);
            // Dont pass 'parent' here , not needed
            Log.d(TAG,"New view created :"+ counter++);


            final TextView textView_UserName = (TextView) view.findViewById(R.id.listView_UserTitle);
            final TextView textView_TweetContent = (TextView) view.findViewById(R.id.listView_tweet);
            final ImageView  imageView_Profile = (ImageView) view.findViewById(R.id.listView_profileImage);

            viewHolder = new ViewHolder(textView_UserName, imageView_Profile,textView_TweetContent);
            view.setTag(viewHolder);


        }else{
            view=convertView;
            Log.d(TAG,"View recycled!");
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
        Bitmap mapMarkerImg = new MapsActivity().getCircleCroppedBitmap(bmImg,colorValue);
        viewHolder.listView_profileImage.setImageBitmap(mapMarkerImg);

        //3. Return the inflated view here
        return view;
    }

    /**
     * calling findViewByIds() is expensive. ViewHolder will help us avoid it.
     */
    class ViewHolder{

        TextView listView_UserTitle;
        TextView listView_tweet;
        ImageView listView_profileImage;

        public ViewHolder(TextView tv_UserName, ImageView imageView,TextView tv_TweetContent) {
            this.listView_UserTitle = tv_UserName;
            this.listView_profileImage = imageView;
            this.listView_tweet = tv_TweetContent;
        }
    }


}
