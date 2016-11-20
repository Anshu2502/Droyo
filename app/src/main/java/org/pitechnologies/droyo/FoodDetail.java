package org.pitechnologies.droyo;

/**
 * Created by Pitech09 on 3/30/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class FoodDetail extends Activity{

    TextView foodnamee, foodpricee, text, quantity;
    Button b1,b2,add;
    int count,i, a,b, code;
    String foodname, foodprice, foodid;
    double aa;
    String order_quantity, order_amount;
    String line=null, result=null;
    InputStream is=null;
    ProgressDialog pd = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.singlemessage);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        text = (TextView) findViewById(R.id.tex1);
        foodnamee = (TextView) findViewById(R.id.foodname);
        foodpricee = (TextView) findViewById(R.id.foodprice);
        quantity = (TextView) findViewById(R.id.quantity);
        b1 = (Button) findViewById(R.id.button1);
        b2 = (Button) findViewById(R.id.button2);
        add = (Button) findViewById(R.id.button3);
        count = 1;
         Intent intent = getIntent();
         foodname = intent.getStringExtra("KEY1");
         foodprice = intent.getStringExtra("KEY2");
         foodid = intent.getStringExtra("KEY3");
        Toast.makeText(getApplicationContext(), foodid, Toast.LENGTH_LONG).show();
         aa = Double.parseDouble(foodprice);
         i = (int)aa;
        foodnamee.setText(foodname);
        foodpricee.setText("Basic Price: " + foodprice);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                a = count++;
                order_quantity = (String.valueOf(a));
                b = a * i;

                order_amount = (String.valueOf(b));
                quantity.setText("Quantity: " + a);
                text.setText("Your Total is: " + b);

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (a <= 0) {
                    Toast.makeText(getApplicationContext(), "Please Add", Toast.LENGTH_LONG).show();
                } else {
                    a = count--;
                    order_quantity = (String.valueOf(a));
                    b = a * i;
                    order_amount = (String.valueOf(b));
                    quantity.setText("Quantity =" + a);
                    text.setText("Your Total is: " + b);

                }
            }
        });


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                insert();
                Toast.makeText(getApplicationContext(), "Added Successfully", Toast.LENGTH_LONG).show();
                Intent st = new Intent(FoodDetail.this, FoodList.class);
                startActivity(st);
                finish();

            }

        });


 }

   public void insert()
    {
        pd = ProgressDialog.show(FoodDetail.this, "", "Please Wait...", true);

        SharedPreferences prefs = getSharedPreferences("user1", MODE_PRIVATE);
        String customerorder = prefs.getString("order-number",null);
        String customername = prefs.getString("customer_name", null);
        String customernumber = prefs.getString("customer_number", null);
        String customeraddress = prefs.getString("customer_address",null);
        String lat = prefs.getString("customer_lat",null);
        String lng = prefs.getString("customer_lng",null);
        String radio = prefs.getString("radio_buttons",null);
        String merchantid = prefs.getString("merchant_id",null);
        String currentdatetime = prefs.getString("current_date_time", null);
        
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("currentdatime", currentdatetime));
        nameValuePairs.add(new BasicNameValuePair("order_time", radio));
        nameValuePairs.add(new BasicNameValuePair("order_number", customerorder));
        nameValuePairs.add(new BasicNameValuePair("order_quantity",order_quantity));
        nameValuePairs.add(new BasicNameValuePair("order_singleprice",foodprice));
        nameValuePairs.add(new BasicNameValuePair("order_totalprice",order_amount));
        nameValuePairs.add(new BasicNameValuePair("merchant_id",merchantid));
        nameValuePairs.add(new BasicNameValuePair("food_id",foodid));
        nameValuePairs.add(new BasicNameValuePair("customer_name",customername));
        nameValuePairs.add(new BasicNameValuePair("customer_address",customeraddress));
        nameValuePairs.add(new BasicNameValuePair("customer_number",customernumber));
        nameValuePairs.add(new BasicNameValuePair("customer_address_lat",lat));
        nameValuePairs.add(new BasicNameValuePair("customer_address_lng",lng));
        nameValuePairs.add(new BasicNameValuePair("order_status", "Pending"));
        //Log.i("tagconvertstr", "[" + empid + " " + name1 + email1 + "]");
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://droyoo.planyourshadi.in/customer_order_insert.php");
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
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
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
            pd.dismiss();

        }
        catch(Exception e)
        {
            Log.e("Fail 3", e.toString());
        }
}

}