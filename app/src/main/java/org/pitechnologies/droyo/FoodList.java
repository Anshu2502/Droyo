package org.pitechnologies.droyo;

/**
 * Created by Pitech09 on 3/30/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


public class FoodList extends Activity {


    String myJSON, foodid, foodname, foodprice, order_id;
    private static final String TAG_ID = "food_id";
    private static final String TAG_FOODPRICE = "food_price";
    private static final String TAG_FOODNAME = "food_name";
    ProgressBar progressBar;
    Button pick;
    String line=null, result=null;
    InputStream is=null;
    int code;
    ArrayList<HashMap<String, String>> personList;
    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.noticelist);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        list = (ListView) findViewById(R.id.listView);
        personList = new ArrayList<HashMap<String,String>>();
        pick = (Button) findViewById(R.id.picky);
        getData();


        pick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(), "You Have Picked The Item",
                        Toast.LENGTH_LONG).show();
                Intent startintent = new Intent(FoodList.this, NewOrder.class);
                startActivity(startintent);
                finish();
            }
        });

 }
    protected void showList(){
        try {
            JSONArray peoples = new JSONArray(myJSON);
            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
               if(c.has("food_id"))
                if(c.has("food_price"))
                    if(c.has("food_name"))
                progressBar.setVisibility(View.GONE);
                foodid = c.getString("food_id");
                foodprice = c.getString("food_price");
                foodname = c.getString("food_name");
                HashMap<String,String> persons = new HashMap<String,String>();
                persons.put(TAG_ID, foodid);
                persons.put(TAG_FOODPRICE,foodprice);
                persons.put(TAG_FOODNAME, foodname);
                personList.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    FoodList.this, personList, R.layout.list_item1,
                    new String[]{TAG_FOODPRICE,TAG_FOODNAME},
                    new int[]{R.id.name, R.id.date}
            );

            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position,
                                        long id) {


                    Intent intent = new Intent(FoodList.this, FoodDetail.class);
                    intent.putExtra("KEY1", personList.get(position).get(TAG_FOODNAME));
                    intent.putExtra("KEY2", personList.get(position).get(TAG_FOODPRICE));
                    intent.putExtra("KEY3", personList.get(position).get(TAG_ID));
                    startActivity(intent);
                      finish();




                        /*ModelClass obj = getItem(position);
                        String name = obj.getName();*/


                    // Simple Toast to show the position Selected
                    Log.d("SELECT_POSITION", "Position For this List Item = " + position);
                }

            });

        } catch (JSONException e) {
            Log.i("tagconvertstr", "["+myJSON+"]");
        }
    }





    public void getData(){
        class GetDataJSON extends AsyncTask<String, Void, String>{


            @Override
            protected String doInBackground(String... params) {
                InputStream inputStream = null;
                String result = null;
                try {

                    String postReceiverUrl = "http://droyoo.planyourshadi.in/food.php";
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
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }

}

