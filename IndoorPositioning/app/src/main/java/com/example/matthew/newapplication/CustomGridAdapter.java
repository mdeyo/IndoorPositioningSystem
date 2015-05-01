package com.example.matthew.newapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Matthew on 1/7/2015.
 */
public class CustomGridAdapter extends BaseAdapter {

    int[] images;
    private Context context;
    private ImageView imageView, border;
    ImageView biggerImageView;

    //Constructor to initialize values
    public CustomGridAdapter(Context context, int[] images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public int getCount() {
        // Number of times getView method call depends upon gridValues.length
        //return gridValues.length;
        return images.length;
    }

    public int getGridValue(int position) {
        return images[position];
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {

        return 0;
    }

    public void setAsCurrent(int position) {

    }
/*
    public GridObject getObject(int position){
        return gridValues[position];
    }
*/

    // Number of times getView method call depends upon gridValues.length

    public View getView(int position, View convertView, ViewGroup parent) {

        // LayoutInflator to call external grid_item.xml file

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from grid_item.xml ( Defined Below )

            gridView = inflater.inflate(R.layout.item, null);

            // set value into textview

            //TextView textView = (TextView) gridView .findViewById(R.id.grid_item_label);

            //textView.setText(gridValues[position]);

            // set image based on selected text

            imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
            // border = (ImageView) gridView.findViewById(R.id.item_background);
            //imageView.setImageResource(R.drawable.placemarker2);

            // biggerImageView = (ImageView) gridView.findViewById(R.id.item_background);


            // if(gridValues.equals("999")){
            //     imageView.setImageResource(R.drawable.placemarker);
            // }
            //String arrLabel = gridValues[ position ];

        } else {

            gridView = (View) convertView;
        }

        if (images[position] == -1) {
            // show nothing
        } else if (images[position] == 0) {
            imageView.setImageResource(R.drawable.placemarker0);
        } else if (images[position] == 1) {
            imageView.setImageResource(R.drawable.placemarker1);
        } else if (images[position] == 2) {
            imageView.setImageResource(R.drawable.placemarker2);
        } else if (images[position] == 9) {
            //Log.d("position",String.valueOf(position));
            imageView.setImageResource(R.drawable.path_horizontal);
            //gridView.setBackgroundResource(R.drawable.horizontal_path);
        } else if (images[position] == 10) {
            //Log.d("position",String.valueOf(position));
            imageView.setImageResource(R.drawable.path_horizontal_down_left);
            //gridView.setBackgroundResource(R.drawable.horizontal_path);
        } else if (images[position] == 11) {
            //Log.d("position",String.valueOf(position));
            imageView.setImageResource(R.drawable.path_horizontal_up_right);
            //gridView.setBackgroundResource(R.drawable.horizontal_path);
        } else if (images[position] > 2) {
            imageView.setImageResource(R.drawable.placemarker3);
        }
        return gridView;
    }
}