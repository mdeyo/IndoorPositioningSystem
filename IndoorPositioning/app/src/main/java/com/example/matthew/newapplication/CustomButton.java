package com.example.matthew.newapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class CustomButton extends Button {

    //	private List<HistoryMenuItem> itemList;
    private Context context;

    public CustomButton(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
//		this.itemList = itemList;
        this.context = ctx;
//        this.onFinishInflate();
//        setContentView(R.layout.activity_choose_path);
        inflate(ctx, R.layout.path_button_layout, null);

        Log.d("tag", "setting up");

//        View v =this; //= this.getView();
//        if (v == null) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.path_button_layout, null);
//        }

//		HistoryMenuItem item = itemList.get(position);
        TextView text = (TextView) v.findViewById(R.id.optionNumber);
        text.setText("Option number 2");

        TextView text2 = (TextView) v.findViewById(R.id.points);
        text2.setText("40 points");

    }

//	public int getCount() {
//		if (itemList != null)
//			return itemList.size();
//		return 0;
//	}

//	public HistoryMenuItem getItem(int position) {
//		if (itemList != null)
//			return itemList.get(position);
//		return null;
//	}

//	public long getItemId(int position) {
//		if (itemList != null)
//			return itemList.get(position).hashCode();
//		return 0;
//	}

    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d("tag", "trying to getView");
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.path_button_layout, null);
        }

//		HistoryMenuItem item = itemList.get(position);
        TextView text = (TextView) v.findViewById(R.id.optionNumber);
        text.setText("Option number 2");

        TextView text2 = (TextView) v.findViewById(R.id.points);
        text2.setText("40 points");

//		TextView text3 = (TextView) v.findViewById(R.id.historyDishRating);
//		text3.setText("Rated "+item.getRating().substring(0, 1)+"/5 stars");

        //TextView text4 = (TextView) v.findViewById(R.id.historyDishDescription);
        //text4.setText(item.getDescription());

//		TextView text5 = (TextView) v.findViewById(R.id.date);
//		text5.setText(item.getTime().toString());
//
        return v;

    }

//	public List<HistoryMenuItem> getItemList() {
//		return itemList;
//	}
//
//	public void setItemList(List<HistoryMenuItem> itemList) {
//		this.itemList = itemList;
//	}
}