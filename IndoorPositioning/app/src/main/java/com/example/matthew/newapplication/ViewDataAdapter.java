package com.example.matthew.newapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Matthew on 1/9/2015.
 */
public class ViewDataAdapter extends ArrayAdapter<GridData> {

        private List<GridData> itemList;
        private Context context;

        public ViewDataAdapter(List<GridData> itemList, Context ctx) {
            super(ctx, android.R.layout.simple_list_item_2, itemList);
            this.itemList = itemList;
            this.context = ctx;
        }

        public int getCount() {
            if (itemList != null)
                return itemList.size();
            return 0;
        }

        public GridData getItem(int position) {
            if (itemList != null)
                return itemList.get(position);
            return null;
        }

        public long getItemId(int position) {
            if (itemList != null)
                return itemList.get(position).hashCode();
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.data_item_layout, null);
            }

            GridData item = itemList.get(position);
            TextView text = (TextView) v.findViewById(R.id.positionText);
            text.setText(item.printPosition());

            TextView text2 = (TextView) v.findViewById(R.id.routersText);
            text2.setText(item.printRouters());

            //TextView text3 = (TextView) v.findViewById(R.id.historyDishRating);
            //text3.setText("Rated "+item.getRating().substring(0, 1)+"/5 stars");

            //TextView text4 = (TextView) v.findViewById(R.id.historyDishDescription);
            //text4.setText(item.getDescription());

            //TextView text5 = (TextView) v.findViewById(R.id.date);
            //text5.setText(item.getTime().toString());

            return v;

        }

        public List<GridData> getItemList() {
            return itemList;
        }

        public void setItemList(List<GridData> itemList) {
            this.itemList = itemList;
        }
    }


