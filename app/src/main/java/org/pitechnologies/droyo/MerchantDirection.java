package org.pitechnologies.droyo;

/**
 * Created by Pitech09 on 3/19/2016.
 */
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.w3c.dom.Document;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * Created by Pitech09 on 3/17/2016.
 */
public class MerchantDirection extends FragmentActivity implements LocationListener {
    Document document;
    BigDecimal lat2, lng2;
    GMapV2GetRouteDirection v2GetRouteDirection;
    LatLng fromPosition;
    LatLng toPosition;
    int Radius=6371;
    GoogleMap mGoogleMap;
    MarkerOptions markerOptions;
    ArrayList<LatLng> mMarkerPoints;
    double mLatitude,longitudee, latitudee;
    double mLongitude, latt2, lngg2;
    float distance;
    Button currentlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.direction);

        Intent intent = getIntent();
        String fName = intent.getStringExtra("mlatitude");
        String lName = intent.getStringExtra("mlongitude");
        latt2 = Double.parseDouble(fName);
        lngg2 = Double.parseDouble(lName);


        Log.i("tagconvertstr", "[" + fName + " " + longitudee + "]");
        Toast.makeText(getApplicationContext(), latt2 + " " + lngg2,
                Toast.LENGTH_LONG).show();


        v2GetRouteDirection = new GMapV2GetRouteDirection();
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!= ConnectionResult.SUCCESS){ // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else { // Google Play Services are available

            // Initializing
            mMarkerPoints = new ArrayList<LatLng>();

            // Getting reference to SupportMapFragment of the activity_main
            SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            mGoogleMap = fm.getMap();

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location From GPS
            Location location = locationManager.getLastKnownLocation(provider);
            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
            // Enable MyLocation Button in the Map
            mGoogleMap.setMyLocationEnabled(true);
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
            mGoogleMap.getUiSettings().setCompassEnabled(true);
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            mGoogleMap.getUiSettings().setAllGesturesEnabled(true);
            //mGoogleMap.setTrafficEnabled(true);
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(12));
            markerOptions = new MarkerOptions();
            fromPosition = new LatLng(mLatitude, mLongitude);
            //new LatLng(22.749494, 75.897290);
            toPosition = new LatLng(latt2, lngg2);

            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(fromPosition));

            // Zoom in the Google Map
            mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            //mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mLatitude, mLongitude)).title("You are here!"));
            GetRouteTask getRoute = new GetRouteTask();
            getRoute.execute();

            //kmeter.setText(String.valueOf(distance));
            // Toast.makeText(getApplicationContext(), "Distance: "+test+"KM", Toast.LENGTH_LONG).show();


            // Getting LocationManager object from System Service LOCATION_SERVICE

            //Log.i("tagconvertstr", "[" + "Distance :" + TotalDistance + "]");


        }

    }
    private class GetRouteTask extends AsyncTask<String, Void, String> {

        private ProgressDialog Dialog;
        String response = "";
        @Override
        protected void onPreExecute() {
            Dialog = new ProgressDialog(MerchantDirection.this);
            Dialog.setMessage("Loading route...");
            Dialog.show();

        }

        @Override
        protected String doInBackground(String... urls) {
            //LatLng startPoint = new LatLng(22.749494, 75.897290);




            //Double.parseDouble("75.8833");
            //location.getLongitude();
            //double latitude = getIntent().getDoubleExtra("lat", 0);

            // Receiving longitude from MainActivity screen
            //double longitude = getIntent().getDoubleExtra("lng", 0);

            // Creating a LatLng object for the current location
            LatLng latLng = new LatLng(mLatitude, mLongitude);
            //Get All Route values
            document = v2GetRouteDirection.getDocument(fromPosition, toPosition, GMapV2GetRouteDirection.MODE_DRIVING);
            response = "Success";
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            mGoogleMap.clear();
            if(response.equalsIgnoreCase("Success")){
                ArrayList<LatLng> directionPoint = v2GetRouteDirection.getDirection(document);
                PolylineOptions rectLine = new PolylineOptions().width(10).color(Color.RED);
                MarkerOptions marker = new MarkerOptions().title("HERE");
                for (int i = 0; i < directionPoint.size(); i++) {
                    rectLine.add(directionPoint.get(i));
                }

                // Adding route on the map
                mGoogleMap.addPolyline(rectLine);
                markerOptions.position(toPosition);
                markerOptions.draggable(true);
                mGoogleMap.addMarker(markerOptions);

            }

            Dialog.dismiss();
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }






    @Override
    public void onLocationChanged(Location location) {
        // Draw the marker, if destination location is not set
        TextView tvLocation = (TextView) findViewById(R.id.tv_location);

        // Getting latitude of the current location
        mLatitude = location.getLatitude();
        //Double.parseDouble("22.7167");
        //location.getLatitude();

        // Getting longitude of the current location
        mLongitude = location.getLongitude();

        String name1=String.valueOf(mLatitude);
        String email1=String.valueOf(mLongitude);


        //Double.parseDouble("75.8833");
        //location.getLongitude();
        //double latitude = getIntent().getDoubleExtra("lat", 0);

        // Receiving longitude from MainActivity screen
        //double longitude = getIntent().getDoubleExtra("lng", 0);

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(mLatitude, mLongitude);

        // Showing the current location in Google Map
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

        // Setting latitude and longitude in the TextView tv_location
        tvLocation.setText("Latitude:" +  name1  + ", Longitude:"+ email1 );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
