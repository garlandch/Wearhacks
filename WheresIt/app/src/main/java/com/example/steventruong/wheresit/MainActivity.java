package com.example.steventruong.wheresit;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends ActionBarActivity implements View.OnClickListener{

    private Button mainBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //onCreate when app starts.
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainBtn = (Button) findViewById(R.id.mainButton);
        mainBtn.setOnClickListener(this);

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.main_tool_bar);
        setSupportActionBar(myToolbar);

        myToolbar.setBackgroundColor(Color.rgb(255, 204, 0));
        setTitle("WearsIt");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainButton:
                Intent i = new Intent(getApplicationContext(), ListItemActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; 
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            
        }

        return super.onOptionsItemSelected(item);
    }


}
