package com.example.steventruong.wheresit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;

    public ItemArrayAdapter(Context context, String[] values) {
        super(context, R.layout.list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);
        textView.setText(values[position]);

        // Change icon based on name
        String s = values[position];

        System.out.println(s);

        if (s.equals("Bag")) {
            imageView.setImageResource(R.drawable.bag);
        } else if (s.equals("Bike")) {
            imageView.setImageResource(R.drawable.bike);
        } else {//Computer
            imageView.setImageResource(R.drawable.computer);
        }

        return rowView;
    }
}