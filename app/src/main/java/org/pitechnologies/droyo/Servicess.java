package org.pitechnologies.droyo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Servicess extends Service implements LocationListener {
      String myJSON, change, empid, lat, lng, result, line = null;
        int code;
        InputStream is=null;
        Location location;
        LocationManager locationManager;
        double latitude, longitude;
    private static final long UPDATE_INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {

        Toast.makeText(getApplicationContext(), "Your Tracking Started", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                getData();
            }
        }, 0, 5000);

        location();

         return START_STICKY;
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
    public void location(){

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        location = locationManager.getLastKnownLocation(provider);
        if(location!=null){
            onLocationChanged(location);
        }else{
            Toast.makeText(getApplicationContext(), "Cannot Get Location", Toast.LENGTH_LONG).show();
        }
        locationManager.requestLocationUpdates(provider, 10000, 0, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        empid = String.valueOf(change);
        lat=String.valueOf(latitude);
        lng=String.valueOf(longitude);
        insert();
        //Toast.makeText(getApplicationContext(), lat +"-"+lng, Toast.LENGTH_LONG).show();

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
    public void insert()
    {

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("empid", empid));
        nameValuePairs.add(new BasicNameValuePair("latitude",lat));
        nameValuePairs.add(new BasicNameValuePair("longitude", lng));
        //Log.i("tagconvertstr", "[" + empid+" " + lat + lng +"]");
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://droyoo.planyourshadi.in/employeelocation.php");
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
            Log.e("pass 1", "connection success ");
        }
        catch(Exception e)
        {
            Log.e("Fail 1", e.toString());

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
            JSONObject jsonObj = new JSONObject(myJSON);
            change = jsonObj.getString("emp_id");
            SharedPreferences.Editor customerdetail = getSharedPreferences("user2", MODE_PRIVATE).edit();
            customerdetail.putString("emp_id", change).commit();
            Toast.makeText(getApplicationContext(), change + " ", Toast.LENGTH_LONG).show();
            Log.i("tagconvertstr", "[" + change + "]");

        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }



    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {
                SharedPreferences myprefs= getSharedPreferences("user", MODE_WORLD_READABLE);
                String session_id= myprefs.getString("session_id", null);
                InputStream inputStream = null;
                String result = null;
                try {

                    String postReceiverUrl = "http://droyoo.planyourshadi.in/getdata.php";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(postReceiverUrl);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                    nameValuePairs.add(new BasicNameValuePair("username", session_id));
                    //Log.i("tagconvertstr", "[" + session_id + "]");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity resEntity = response.getEntity();

                    inputStream = resEntity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null)
                    {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    Log.i("tagconvertstr", "[" + result + "]");
                    System.out.println(e);
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myJSON = result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


}
