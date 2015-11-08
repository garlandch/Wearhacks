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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class MainActivity extends ActionBarActivity implements View.OnClickListener
{
    private ImageButton mainBtn;
    private TextView mainTxtView;
    private static final UUID APP_UUID = UUID.fromString("379e58e4-fa62-4418-b3f6-05c3392ba1bd");
    private PebbleKit.PebbleDataReceiver mDataReceiver;
    private String mVoiceQuery;
    private Firebase rootRef;
    private HashMap<String,Integer> roomMapper;
    public static int resultRoom = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //onCreate when app starts.
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        rootRef.setAndroidContext(this);
        rootRef = new Firebase("https://sweltering-inferno-8588.firebaseio.com/");
        roomMapper = new HashMap<String,Integer>();
        roomMapper.put("Entrance",0);
        roomMapper.put("Hack Room", 1);

        mainBtn = (Button) findViewById(R.id.mainButton);
        mainBtn.setOnClickListener(this);

        mainTxtView = (TextView) findViewById(R.id.main_text_view);

        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000);

        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(1000);

        out.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                mainTxtView.startAnimation(in);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });

        in.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
                mainTxtView.startAnimation(out);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.main_tool_bar);
        setSupportActionBar(myToolbar);

        myToolbar.setBackgroundColor(Color.rgb(44, 133, 142));
        myToolbar.setTitleTextColor(Color.rgb(255, 255, 255));
        myToolbar.setSubtitleTextColor(Color.rgb(255, 255, 255));

        setupPebbleReceiver();
        setTitle("WheresIt");

        mainTxtView.startAnimation(in);
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

    public static void setResultRoom(int i) {
        resultRoom = i;
    }
    @Override
    protected void onResume() {
        super.onResume();

        //setupPebbleReceiver();

        if(mDataReceiver == null) {
            mDataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary dict)
                {
                    // Always ACK
                    Log.d("test", "Got message from Pebble!");
                    PebbleKit.sendAckToPebble(context, transactionId);

                    // the string dictated by the user from the pebble app
                    mVoiceQuery = dict.getString(Keys.KEY_CHOICE);
                    if (mVoiceQuery != null)
                    {
                        // TODO: Search for item and return location
                        int resultRoom  = 0;

                        rootRef.child("rooms").addValueEventListener( new ValueEventListener(){
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Iterable<DataSnapshot> iteratable = snapshot.getChildren();
                                Iterator iterator = iteratable.iterator();
                                while (iterator.hasNext())
                                {
                                    DataSnapshot response = (DataSnapshot)iterator.next();
                                    ArrayList<String> test = (ArrayList) response.getValue();
                                    for (String s: test) {
                                        if(s.equals(mVoiceQuery))
                                        {
                                            MainActivity.setResultRoom(roomMapper.get(response.getKey()));
                                        }
                                    }
                                }
                            }
                            @Override public void onCancelled(FirebaseError error) { }
                        });

                        PebbleDictionary resultDict = new PebbleDictionary();
                        resultDict.addInt32(Keys.KEY_RESULT, MainActivity.resultRoom); // Replace room1 with actual key
                        PebbleKit.sendDataToPebble(getApplicationContext(), APP_UUID, resultDict);

                        // Reset string
                        mVoiceQuery = "";
                    }
                }

            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }
    }

    public void setupPebbleReceiver()
    {
        // Register to get updates from Pebble
        final Handler handler = new Handler();
        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(APP_UUID) {
            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                    /* Update your UI here. */
                    }
                });

                // Did the user request a voice search?
                mVoiceQuery = data.getString(Keys.KEY_RESULT);
                if (mVoiceQuery != null) {
                    // TODO: Search for item and return location
                    Firebase ref = new Firebase("https://sweltering-inferno-8588.firebaseio.com/");
                    ref.child("rooms").addValueEventListener( new ValueEventListener(){
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Iterable<DataSnapshot> iteratable = snapshot.getChildren();
                            Iterator iterator = iteratable.iterator();
                            while (iterator.hasNext())
                            {
                                Object response = iterator.next();
                                Log.d("test", response.toString());
                            }
                        }
                        @Override public void onCancelled(FirebaseError error) { }
                    });


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
