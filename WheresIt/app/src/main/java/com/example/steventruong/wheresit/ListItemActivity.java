package com.example.steventruong.wheresit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

public class ListItemActivity extends ActionBarActivity {

    ListView listView;

    static final String[] ITEM =
            new String[] { "Bag" , "Bike", "Computer"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        listView = (ListView) findViewById(R.id.list);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.rgb(255, 204, 0));
        setTitle("Living Room");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView.setAdapter(new ItemArrayAdapter(this, ITEM));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}