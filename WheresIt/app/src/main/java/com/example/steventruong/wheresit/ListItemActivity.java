package com.example.steventruong.wheresit;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

public class ListItemActivity extends ActionBarActivity {

    ListView listView;

    static final String[] ITEM =
            new String[] { "Car" , "Keys"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        listView = (ListView) findViewById(R.id.list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        listView.setAdapter(new ItemArrayAdapter(this, ITEM));

    }

}