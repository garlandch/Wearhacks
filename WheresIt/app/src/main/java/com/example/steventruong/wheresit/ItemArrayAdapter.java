package com.example.steventruong.wheresit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.estimote.sdk.Nearable;

import java.util.List;

public class ItemArrayAdapter extends ArrayAdapter<Nearable> {

    public final static String BIKE = "7c420af88b16a0a6";
    public final static String BAG = "792aca5aef8e97b8";

    private final Context context;
    private List<Nearable> values;

    public ItemArrayAdapter(Context context, List<Nearable> values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);

        if(getItem(position).identifier.equals(BIKE)) {
            imageView.setImageResource(R.drawable.bike);
            textView.setText("Bike");
        } else if(getItem(position).identifier.equals(BAG)) {
            imageView.setImageResource(R.drawable.bag);
            textView.setText("Bag");
        }

        return rowView;
    }
}