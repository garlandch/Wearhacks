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
        textView.setText(getItem(position).identifier);

        // Change icon based on name
//        String s = values[position];
//
//        System.out.println(s);
//
//        if (s.equals("Car")) {
//            imageView.setImageResource(R.drawable.car);
//        } else if (s.equals("Keys")) {
//            imageView.setImageResource(R.drawable.key);
//        }

        return rowView;
    }
}