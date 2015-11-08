package com.example.steventruong.wheresit;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class ListItemActivity extends ActionBarActivity {

    private final String APP_ID = "wearhacks-wheresit-applica-0cr";
    private final String APP_TOKEN = "4bc2480717509544f1bd0ca34bbbe49d";
    private static final UUID APP_UUID = UUID.fromString("379e58e4-fa62-4418-b3f6-05c3392ba1bd");
    private ListView mItems;
    private ItemArrayAdapter mAdapter;

    private Firebase rootRef;
    private BeaconManager mBeaconManager;
    private List<Nearable> mNearables;
    private static String mVoiceQuery;
    private PebbleKit.PebbleDataReceiver mDataReceiver;
    private HashMap<String,Integer> roomMapper;

    static final String[] ITEM =
            new String[] { "Bag" , "Bike", "Computer"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Firebase rootref
        rootRef.setAndroidContext(this);
        rootRef = new Firebase("https://sweltering-inferno-8588.firebaseio.com/");


        roomMapper = new HashMap<String,Integer>();
        roomMapper.put("Entrance", 0);
        roomMapper.put("Hack Room", 1);


        //initialize SDK
        EstimoteSDK.initialize(getApplicationContext(), APP_ID, APP_TOKEN);
        EstimoteSDK.enableDebugLogging(false);

        //set UI
        setContentView(R.layout.list_activity);

        final Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mItems = (ListView) findViewById(R.id.list);

        mNearables = new ArrayList<>();
        mAdapter = new ItemArrayAdapter(this, mNearables);

        mItems.setAdapter(mAdapter);

        //Initilize Estimote
        mBeaconManager = new BeaconManager(getApplicationContext());
        final Region livingRoom = new Region("Living Room", UUID.fromString("b9407f30-f5f8-466e-aff9-25556b57fe6d"), 25966, 60904);
        //final Region bag = new Region("Bag", UUID.fromString("d0d3fa86-ca76-45ec-9bd9-6af4792aca5a"), 38840, 61326);
        //final Region bike = new Region("Bike", UUID.fromString("d0d3fa86-ca76-45ec-9bd9-6af4d1ebb1b1"), 30629, 54712 );


        mBeaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                Toast.makeText(getApplicationContext(), "Entered Room", Toast.LENGTH_SHORT).show();
                if (list.size() > 0 && list.get(0).getProximityUUID().equals("b9407f30-f5f8-466e-aff9-25556b57fe6d"))
                {
                    myToolbar.setTitle("Living Room");
                }
                else
                {
                    myToolbar.setTitle("Garage");
                }
                //mNearables.clear();
            }

            @Override
            public void onExitedRegion(Region region) {
                myToolbar.setTitle("No Nearby Beacons");
                Toast.makeText(getApplicationContext(), "Exited", Toast.LENGTH_SHORT).show();
            }
        });

        mBeaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> list) {
                    mNearables = list;
                    mAdapter.clear();
                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    rootRef.child(myToolbar.getTitle().toString()).setValue(list);
            }
        });

        mBeaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                mBeaconManager.startMonitoring(livingRoom);
                //mBeaconManager.startRanging(bag);
                mBeaconManager.startNearableDiscovery();
            }
        });

        //mBeaconManager.startNearableDiscovery();

        myToolbar.setBackgroundColor(Color.rgb(255,255,255));
        setTitle("Searching...");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onResume() {
        super.onResume();

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
                        // Search for item and return location
                        int resultRoom  = -1;

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