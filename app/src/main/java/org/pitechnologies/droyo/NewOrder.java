package org.pitechnologies.droyo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pitech09 on 3/16/2016.
 */
public class NewOrder extends Activity implements LocationListener {
    Button pick, merchant, direction, btnClosePopup;
    BigDecimal lat, lng;
    String latitude, longitude, pickee, deliver, mlatitude, mlongitude, order_numbe, provider,distancee;
    TextView picked, delivered, ordernumber, date, time, quantity, amount, merchantid, cusine, customername, customeraddress, customermobile;
    private GoogleApiClient client;
    InputStream is=null;
    String  myJSON, change, result=null, line=null, latt, lngg, empid;
    int code;
    SharedPreferences spStateButton;
    SharedPreferences.Editor spEditor;
    double latituded, longituded, dislat, dislng, dislat1, dislng2;
    LocationManager locationManager;
    Criteria criteria;
    float distance;
    ProgressDialog pd = null;


    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        setContentView(R.layout.neworder);
        getData();
        spStateButton= getApplicationContext().getSharedPreferences("Button_State", 0);
        spEditor = spStateButton.edit();
        ordernumber = (TextView) findViewById(R.id.ordernumber);
        date = (TextView) findViewById(R.id.orderdate);
        time = (TextView) findViewById(R.id.ordertime);
        quantity = (TextView) findViewById(R.id.orderquantity);
        amount = (TextView) findViewById(R.id.orderamount);
        merchantid = (TextView) findViewById(R.id.merchantid);
        cusine = (TextView) findViewById(R.id.cusineid);
        customername = (TextView) findViewById(R.id.customername);
        customeraddress = (TextView) findViewById(R.id.customeraddress);
        customermobile = (TextView) findViewById(R.id.customermobile);
        pick = (Button) findViewById(R.id.picking);
        merchant = (Button) findViewById(R.id.merchant);
        picked = (TextView) findViewById(R.id.picked);
        picked.setText("Picked");
        delivered = (TextView) findViewById(R.id.delivered);
        delivered.setText("Delivered");
        SharedPreferences prefs = getSharedPreferences("user2", MODE_PRIVATE);
        empid = prefs.getString("emp_id", null);




        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                pickee = picked.getText().toString();
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewOrder.this);
                alertDialogBuilder.setMessage("You Have Picked?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        locate();
                        insert();
                        Toast.makeText(NewOrder.this, "You Have Picked The Item", Toast.LENGTH_LONG).show();
                        Intent main = new Intent(NewOrder.this, MainActivity.class);
                        startActivity(main);
                        finish();

                    }

                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogBuilder.setCancelable(true);
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });



       /* direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent direction = new Intent(NewOrder.this, Direction.class);
                direction.putExtra("latitude", latitude);
                direction.putExtra("longitude", longitude);

                startActivity(direction);

            }
        });*/

        merchant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              Intent merchant = new Intent(NewOrder.this, MerchantDirection.class);
                merchant.putExtra("mlatitude", mlatitude);
                merchant.putExtra("mlongitude", mlongitude);
                startActivity(merchant);
            }
        });

    }

    public void locate(){
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!= ConnectionResult.SUCCESS){

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, NewOrder.this, requestCode);
            dialog.show();

        }else {

            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            criteria = new Criteria();
            provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 10000, 0, this);
        }
    }

public void insert()
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("id", change));
        nameValuePairs.add(new BasicNameValuePair("pick","Picked"));

        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://droyoo.planyourshadi.in/pickupdate.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }

        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
    }





    protected void showList(){
        try {

            JSONArray peoples = new JSONArray(myJSON);
            JSONObject c = peoples.getJSONObject(0);

            change = c.getString("order_id");
            date.setText(c.getString("order_date"));
            time.setText(c.getString("order_time"));
            order_numbe = c.getString("order_no");
            ordernumber.setText(c.getString("order_no"));
            quantity.setText(c.getString("order_qty"));
            amount.setText(c.getString("order_amt"));
            merchantid.setText(c.getString("merchant_id"));
            cusine.setText(c.getString("cuisine_id"));
            customername.setText(c.getString("customer_name"));
            customeraddress.setText(c.getString("customer_address"));
            customermobile.setText(c.getString("customer_mobile"));
            latitude = c.getString("latitude");
            mlatitude = c.getString("mlatitude");
            mlongitude = c.getString("mlongitude");

            lat = new BigDecimal(latitude);
            longitude = c.getString("longitude");
            lng = new BigDecimal(longitude);
            /*if (c.getString("customer_name").equals("Anshul")){
                pick.setEnabled(false);
            }*/


            dislat = Double.valueOf(mlatitude);
            dislng = Double.valueOf(mlongitude);
            dislat1 = Double.valueOf(latitude);
            dislng2 = Double.valueOf(longitude);
            Location locationA = new Location("");
            locationA.setLatitude(dislat);
            locationA.setLongitude(dislng);

            Location locationB = new Location("");
            locationB.setLatitude(dislat1);
            locationB.setLongitude(dislng2);
            distance = locationA.distanceTo(locationB)/1000;
            distancee = String.format("%.02f", distance);

            /*Toast.makeText(getApplicationContext(), mlatitude + " " ,
                    Toast.LENGTH_LONG).show();*/
            /*JSONObject a  = new JSONObject(myJSON);

            String b = a.toString().substring(1, a.toString().length() - 1);*/

            Log.i("tagconvertstr", "[" + change + "]");
            //Log.i("..........", "" + street);
            // loop and add it to array or arraylist

            SharedPreferences.Editor customerdetail = getSharedPreferences("user3", MODE_PRIVATE).edit();
            customerdetail.putString("order_numbe",order_numbe).apply();


        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }




    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {
            public void onPreExecute() {
                pd = ProgressDialog.show(NewOrder.this, "",
                        "Please Wait...", true);



            }
            @Override
            protected String doInBackground(String... params) {

                SharedPreferences myprefs= getSharedPreferences("emp", MODE_WORLD_READABLE);
                String session_id= myprefs.getString("em", null);
                InputStream inputStream = null;
                String result = null;
                try {

                    String postReceiverUrl = "http://droyoo.planyourshadi.in/order.php";

                    HttpClient httpClient = new DefaultHttpClient();


                    HttpPost httpPost = new HttpPost(postReceiverUrl);


                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("eid", session_id));
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity resEntity = response.getEntity();

                    inputStream = resEntity.getContent();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    Log.i("tagconvertstr", "["+result+"]");
                    System.out.println(e);
                } finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON = result;
                showList();
                pd.dismiss();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewOrder Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.pitechnologies.droyo/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "NewOrder Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.pitechnologies.droyo/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        latituded = location.getLatitude();
        longituded = location.getLongitude();
        latt=String.valueOf(latituded);
        lngg=String.valueOf(longituded);
        insert3();
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

    public void insert3()
    {


        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("id", empid));
        nameValuePairs.add(new BasicNameValuePair("latitude",latt));
        nameValuePairs.add(new BasicNameValuePair("longitude",lngg));
        nameValuePairs.add(new BasicNameValuePair("ordernumber",order_numbe));
        nameValuePairs.add(new BasicNameValuePair("distance",distancee));

        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://droyoo.planyourshadi.in/map.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());
            Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();
        }

        try
        {
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(is,"iso-8859-1"),8);
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
            Log.e("pass 2", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 2", e.toString());
        }

        try
        {
            JSONObject json_data = new JSONObject(result);
            code=(json_data.getInt("code"));

        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
    }
}
