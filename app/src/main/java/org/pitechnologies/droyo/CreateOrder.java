package org.pitechnologies.droyo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateOrder extends FragmentActivity {
    ArrayList<String> listItems=new ArrayList<>();
    ArrayAdapter<String> adapter;
    private RadioGroup radioGroup;
    Spinner sp;
    EditText customer_name, customer_number, customer_address, area_name, city_name, state_name;
    Button foood;
    String s, ss, lat, lng, radio, myJSON, ordernumber,street, area, city, state;
    RadioButton allinone;
    double longitudee, latitudee;
    int i;
    ProgressDialog pd = null, pdd = null;
    private ArrayList<String> countryList = new ArrayList<String>();

    private class BackTask extends AsyncTask<Void,Void,Void> {
        ArrayList<String> list;
        protected void onPreExecute(){
            super.onPreExecute();
            list=new ArrayList<>();
            pdd = ProgressDialog.show(CreateOrder.this, "",
                    "Please Wait...", true);
        }

        protected Void doInBackground(Void...params){
            InputStream is=null;
            String result="";
            try{
                HttpClient httpclient=new DefaultHttpClient();
                HttpPost httppost= new HttpPost("http://droyoo.planyourshadi.in/merchant.php");
                HttpResponse response=httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            }catch(IOException e){
                e.printStackTrace();
            }
             try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    result+=line;
                }
                is.close();
               }catch(Exception e){
                e.printStackTrace();
            }

            try{
                JSONArray jArray =new JSONArray(result);
                for(int i=0;i<jArray.length();i++){
                    JSONObject jsonObject=jArray.getJSONObject(i);
                    list.add(jsonObject.getString("restaurant_name"));
                    s = jsonObject.getString("merchant_id");

                    countryList.add(s);
                }
            }
            catch(JSONException e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            listItems.addAll(list);
            adapter.notifyDataSetChanged();
            pdd.dismiss();
        }
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createorder);

        getData();
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);
        customer_name= (EditText) findViewById(R.id.customername);
        customer_number = (EditText) findViewById(R.id.customernumber);
        customer_address = (EditText) findViewById(R.id.customeraddress);
        area_name = (EditText)findViewById(R.id.area);
        city_name = (EditText)findViewById(R.id.city);
        state_name = (EditText)findViewById(R.id.state);



        foood = (Button) findViewById(R.id.food);
        sp=(Spinner)findViewById(R.id.spinner);
        adapter=new ArrayAdapter<String>(this,R.layout.spinner_layout,R.id.txt,listItems);
        sp.setAdapter(adapter);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                int selectedId = radioGroup.getCheckedRadioButtonId();
                allinone = (RadioButton) findViewById(selectedId);
                radio = allinone.getText().toString();
                Toast.makeText(CreateOrder.this, radio, Toast.LENGTH_SHORT).show();

            }


        });

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ss= countryList.get(arg2);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String currentDateandTime = sdf.format(new Date());
        Toast.makeText(getApplicationContext(), currentDateandTime , Toast.LENGTH_LONG).show();

        foood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                street = customer_address.getText().toString();
                area = area_name.getText().toString();
                city = city_name.getText().toString();
                state = state_name.getText().toString();
                String complete_address =  street+" ,"+area+" ,"+city+" ,"+state;
                getlatlng();


                SharedPreferences.Editor customerdetail = getSharedPreferences("user1", MODE_PRIVATE).edit();
                customerdetail.putString("order-number",ordernumber).apply();
                customerdetail.putString("customer_name", customer_name.getText().toString()).apply();
                customerdetail.putString("customer_number",customer_number.getText().toString()).apply();
                customerdetail.putString("customer_address", complete_address).apply();

                customerdetail.putString("customer_lat", lat).apply();
                customerdetail.putString("customer_lng", lng).apply();
                customerdetail.putString("radio_buttons",radio).apply();
                customerdetail.putString("merchant_id",ss).apply();
                customerdetail.putString("current_date_time", currentDateandTime).apply();

                moveTaskToBack(true);
                Intent intent = new Intent(CreateOrder.this, FoodList.class);
                startActivity(intent);
                finish();
            }
        });

    }
    
    public void getlatlng(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        area = area_name.getText().toString();
        city = city_name.getText().toString();
        state = state_name.getText().toString();


        try {
            String locations = area+" ,"+city+" ,"+state;
                    //customer_address.getText().toString();
            Geocoder gc = new Geocoder(this);
            List<Address> address= gc.getFromLocationName(locations, 1000); // get the found Address Objects

            List<LatLng> ll = new ArrayList<LatLng>(address.size()); // A list to save the coordinates if they are available
            for(Address a : address){
                if(a.hasLatitude() && a.hasLongitude()){
                    ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    longitudee = a.getLongitude();
                    latitudee =  a.getLatitude();
                    lng = String.valueOf(longitudee);
                    lat = String.valueOf(latitudee);
                    Log.i("tagconvertstr", "[" + lat + " " + lng+ "]");
                    Toast.makeText(getApplicationContext(), lat + "-" + lng , Toast.LENGTH_LONG).show();
                }
            }
        } catch (IOException e) {
        }
    }


    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
           ordernumber = jsonObj.getString("order_no");
            TextView ordernumberr = (TextView) findViewById(R.id.ordernumber);
            ordernumberr.setText(ordernumber);
            Toast.makeText(getApplicationContext(), ordernumber + " ", Toast.LENGTH_LONG).show();
            Log.i("tagconvertstr", "[" + ordernumber + "]");

        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String>{
            public void onPreExecute() {
                pd = ProgressDialog.show(CreateOrder.this, "",
                        "Please Wait...", true);
            }

            @Override
            protected String doInBackground(String... params) {
                InputStream inputStream = null;
                String result = null;
                try {

                    String postReceiverUrl = "http://droyoo.planyourshadi.in/order_number.php";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(postReceiverUrl);
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
                    try{
                        if(inputStream != null)inputStream.close();
                    }
                    catch(Exception squish){}
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

    public void onStart(){
        super.onStart();
        BackTask bt=new BackTask();
        bt.execute();
    }

}
