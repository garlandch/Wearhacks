package com.example.steventruong.wheresit;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    private Button mainBtn;
    private static final UUID APP_UUID = UUID.fromString("af17efe7-2141-4eb2-b62a-19fc1b595595");
    private PebbleKit.PebbleDataReceiver mDataReceiver;
    private String mVoiceQuery;

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
        myToolbar.setTitleTextColor(Color.rgb(255, 255, 255));
        myToolbar.setSubtitleTextColor(Color.rgb(255, 255, 255));

        Log.d("Test2", "ONCREATE");
        setupPebbleReceiver();
        setTitle("WearsIt");
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
    protected void onResume() {
        super.onResume();

        //setupPebbleReceiver();

        if(mDataReceiver == null) {
            mDataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                    // Always ACK
                    PebbleKit.sendAckToPebble(context, transactionId);
                    Log.i("receiveData", "Got message from Pebble!");

                    mVoiceQuery = dict.getString(Keys.KEY_RESULT);

                    Log.d("Test", mVoiceQuery);

                    // TODO: Search for item and return location
                    PebbleDictionary resultDict = new PebbleDictionary();
                    resultDict.addInt32(Keys.KEY_RESULT, Keys.RESULT_ROOM1); // Replace room1 with actual key
                    PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, resultDict);

                    // Reset string
                    mVoiceQuery = "";
                }

            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }
    }

    public void setupPebbleReceiver()
    {
        // Register to get updates from Pebble
        final Handler handler = new Handler();
        Log.d("Test2", "TESTPRINT");
        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(APP_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data)
            {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    /* Update your UI here. */
                    }
                });

                Log.d("Test", "Received Data");
                // Did the user request a voice search?
                if (data.getInteger(Keys.KEY_CHOICE) != null)
                {
                    mVoiceQuery = data.getString(Keys.KEY_RESULT);

                    Log.d("Test", mVoiceQuery);

                    // TODO: Search for item and return location
                    PebbleDictionary resultDict = new PebbleDictionary();
                    resultDict.addInt32(Keys.KEY_RESULT, Keys.RESULT_ROOM1); // Replace room1 with actual key
                    PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, resultDict);

                    // Reset string
                    mVoiceQuery = "";
                }
                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);
            }
        });
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
        if (id == R.id.action_pebble)
        {
            sideloadInstall(getApplicationContext(), "Where's_It.pbw");
        }
        return super.onOptionsItemSelected(item);
    }
    /**
     * Alternative sideloading method
     * Source: http://forums.getpebble.com/discussion/comment/103733/#Comment_103733
     */
    public static void sideloadInstall(Context ctx, String assetFilename) {
        try {
            // Read .pbw from assets
            Intent intent = new Intent(Intent.ACTION_VIEW);
            File file = new File(ctx.getExternalFilesDir(null), assetFilename);
            InputStream is = ctx.getResources().getAssets().open(assetFilename);
            OutputStream os = new FileOutputStream(file);
            byte[] pbw = new byte[is.available()];
            is.read(pbw);
            os.write(pbw);
            is.close();
            os.close();

            // Install via Pebble Android app
            intent.setDataAndType(Uri.fromFile(file), "application/pbw");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(ctx, "App install failed: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
    }
}