package com.example.steventruong.wheresit;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.EstimoteSDK;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ListItemActivity extends ActionBarActivity {

    private final String APP_ID = "wearhacks-wheresit-applica-0cr";
    private final String APP_TOKEN = "4bc2480717509544f1bd0ca34bbbe49d";
    private ListView mItems;
    private ItemArrayAdapter mAdapter;

    private BeaconManager mBeaconManager;
    private List<Nearable> mNearables;


    static final String[] ITEM =
            new String[] { "Bag" , "Bike", "Computer"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        mBeaconManager.setNearableListener(new BeaconManager.NearableListener() {
            @Override
            public void onNearablesDiscovered(List<Nearable> list) {
                if (mNearables.size() != list.size()) {
                    mNearables = list;
                    mAdapter.clear();
                    mAdapter.addAll(list);
                    mAdapter.notifyDataSetChanged();
                }

                Log.d("TEST", "msize" + list.size());
            }
        });

        mBeaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
            @Override
            public void onEnteredRegion(Region region, List<Beacon> list) {
                myToolbar.setTitle("Garage");
                //mNearables.clear();
            }

            @Override
            public void onExitedRegion(Region region) {
                myToolbar.setTitle("No Nearby Beacons");
                //mNearables.clear();
            }
        });

        mBeaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                //Toast.makeText(getApplicationContext(), "Fouund something!", Toast.LENGTH_SHORT).show();

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

        myToolbar.setBackgroundColor(Color.rgb(255, 204, 0));
        setTitle("Living Room");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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