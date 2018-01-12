package jayantb95.googlelocationservices;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener {


    private static final String TAG = "MainActivity";
    private static final int REQUEST_LOCATION = 0;

    private TextView txtLocation;
    private TextView txtLongitude;
    private TextView txtLatitude;
    private TextView txtTime;
    private TextView txtBearing;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();

        txtLocation = (TextView) findViewById(R.id.txt_location);
        txtLongitude = (TextView) findViewById(R.id.txt_longitude);
        txtLatitude = (TextView) findViewById(R.id.txt_latitude);
        txtTime = (TextView) findViewById(R.id.txt_time);
        txtBearing = (TextView) findViewById(R.id.txt_bearing);
    }

    @Override
    public void onLocationChanged(Location location) {

        txtLocation.setText(String.valueOf(location));
        txtLatitude.setText(String.valueOf(location.getLatitude()));
        txtLongitude.setText(String.valueOf(location.getLongitude()));
        txtTime.setText(String.valueOf(location.getTime()));
        txtBearing.setText(String.valueOf(location.getBearing()));

        Log.i(TAG, "location: " + location.toString());
        Log.i(TAG, "latitude: " + location.getLatitude());
        Log.i(TAG, "longitude: " + location.getLongitude());
        Log.i(TAG, "time: " + location.getTime());
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(500);

        if (checkLocationPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            txtLocation.setText(mLastLocation.toString());
            txtLatitude.setText(String.valueOf(mLastLocation.getLatitude()));
            txtLongitude.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(MainActivity.this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            //should we show explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.message_location_permission)
                        .setPositiveButton(R.string.text_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                //no explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed");
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
}