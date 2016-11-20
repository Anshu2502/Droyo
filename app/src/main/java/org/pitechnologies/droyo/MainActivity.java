package org.pitechnologies.droyo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements LocationListener {
    Button b1, b2,b3, b4,b5,b6,b7, btnClosePopup;
    double latitude, longitude;
    String myJSON, myyJSON, change, latlngg;
    InputStream is=null;
    String lat, lng,empid,result=null;
    String line=null;
    int code;
    LatLng latLng;
    private PopupWindow pwindo;
    Location  location;
    ProgressDialog pd = null;

    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getApplicationContext(), Servicess.class));
        b1 = (Button) findViewById(R.id.norder);
        b2 = (Button) findViewById(R.id.createorder);
        b3 = (Button) findViewById(R.id.orderHistory);
        b4 = (Button) findViewById(R.id.help);
        b5 = (Button) findViewById(R.id.profile);
        b6 = (Button) findViewById(R.id.getdirection);
        b7 = (Button) findViewById(R.id.delivered);

        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b6.setEnabled(false);
                b7.setEnabled(false);
                insertdelivered();
            }
        });


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        if(status!= ConnectionResult.SUCCESS){

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        }else {

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            location = locationManager.getLastKnownLocation(provider);
            if(location!=null){
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 10000, 0, this);
        }







       b1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //moveTaskToBack(true);
               Intent intent1 = new Intent(MainActivity.this, NewOrder.class);
               startActivity(intent1);

               //finish();
           }
       });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveTaskToBack(true);
                Intent intent2 = new Intent(MainActivity.this, CreateOrder.class);
                startActivity(intent2);
                //moveTaskToBack(true);
                //finish();


            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // moveTaskToBack(true);
                Intent intent3 = new Intent(MainActivity.this, OrderHistory.class);
                startActivity(intent3);
                //moveTaskToBack(true);
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
                Intent intent4 = new Intent(Intent.ACTION_DIAL);
                intent4.setData(Uri.parse("tel:0123456789"));
                startActivity(intent4);
                //moveTaskToBack(true);
            }
        });

        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //moveTaskToBack(true);
                Intent intent5 = new Intent(MainActivity.this, EmployeeProfile.class);
                startActivity(intent5);
                //moveTaskToBack(true);
            }
        });

    }





    private void initiatePopupWindow() {
        try {

            LayoutInflater inflater = (LayoutInflater) MainActivity.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.screen_popup,
                    (ViewGroup) findViewById(R.id.popup_element));
            pwindo = new PopupWindow(layout, 300, 370, true);
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(cancel_button_click_listener);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            Intent myIntent = new Intent(MainActivity.this, MainActivity.class);
            MainActivity.this.startActivity(myIntent);
            //pwindo.dismiss();
        }
    };

     protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            change = jsonObj.getString("emp_id");
            SharedPreferences.Editor customerdetail = getSharedPreferences("user2", MODE_PRIVATE).edit();
            customerdetail.putString("emp_id", change).commit();
           // Toast.makeText(getApplicationContext(), change + " ", Toast.LENGTH_LONG).show();
            Log.i("tagconvertstr", "[" + change + "]");

        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        empid = String.valueOf(change);
       lat=String.valueOf(latitude);
       lng=String.valueOf(longitude);
        latLng = new LatLng(latitude, longitude);
        latlngg = (String.valueOf(latLng));
        Log.i("tagconvertstr", "[" +latlngg +"]");
        //insert();
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
            /*Toast.makeText(getApplicationContext(), "Invalid IP Address",
                    Toast.LENGTH_LONG).show();*/
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

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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

    protected void status(){
        try {
            JSONArray peoples = new JSONArray(myyJSON);
            JSONObject c = peoples.getJSONObject(0);
           String jj =   c.getString("order_status");
            Toast.makeText(getApplicationContext(), jj + " ", Toast.LENGTH_LONG).show();
            if(jj.equals("Picked")){
                b6.setEnabled(true);
                b7.setEnabled(true);
            }else{
                b6.setEnabled(false);
                b7.setEnabled(false);
            }

        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getStatus(){
       class GetDataJSON extends AsyncTask<String, Void, String> {
           public void onPreExecute() {
               pd = ProgressDialog.show(MainActivity.this, "",
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
                }
                finally {
                    try{if(inputStream != null)inputStream.close();}catch(Exception squish){}
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                myyJSON = result;
                status();
                pd.dismiss();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

    public void insertdelivered()

    {
        SharedPreferences myprefs= getSharedPreferences("user3", MODE_WORLD_READABLE);
        String order= myprefs.getString("order_numbe", null);
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("order_no", order));
        nameValuePairs.add(new BasicNameValuePair("order_staus", "Delivered"));
        //Log.i("tagconvertstr", "[" + empid + " " + name1 + email1 + "]");
        try
        {

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://droyoo.planyourshadi.in/foodlistbutton.php");
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

    public void onPreExecute() {
        pd = ProgressDialog.show(MainActivity.this, "",
                "Please Wait...", true);



    }
   /* public void insert2()
    {
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

        nameValuePairs.add(new BasicNameValuePair("id", change));
        nameValuePairs.add(new BasicNameValuePair("deliver",deliver));

        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://droyoo.planyourshadi.in/deliverupdate.php");
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
    }*/




}