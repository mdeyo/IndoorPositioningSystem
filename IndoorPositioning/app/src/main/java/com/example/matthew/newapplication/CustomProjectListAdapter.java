package com.example.matthew.newapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Matthew on 1/7/2015.
 */
public class CustomProjectListAdapter extends ArrayAdapter {

        private Context mContext;
        private int id;
        private ArrayList<String> items;
        Typeface tf;

        public CustomProjectListAdapter(Context context, int textViewResourceId , ArrayList<String> list )
        {
            super(context, textViewResourceId, list);
            mContext = context;
            id = textViewResourceId;
            items = list ;
            tf = Typeface.createFromAsset(context.getAssets(), "myriad_pro.ttf");

        }

        @Override
        public View getView(int position, View v, ViewGroup parent)
        {
            View mView = v ;
            if(mView == null){
                LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }

            TextView text = (TextView) mView.findViewById(R.id.text1);

            if(items.get(position) != null )
            {
                //text.setTextColor(Color.WHITE);
                text.setText(items.get(position));
                //text.setBackgroundColor(Color.RED);
                //int color = Color.argb( 200, 255, 64, 64 );
                //text.setBackgroundColor( color );
                text.setTypeface(tf);


            }

            return mView;
        }
}
