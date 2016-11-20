package org.pitechnologies.droyo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class EmployeeProfile extends Activity {
String myJSON, change;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee);
        getData();

    }

    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);

            change = jsonObj.getString("result");
            Toast.makeText(getApplicationContext(), change + " " ,
                    Toast.LENGTH_LONG).show();
            /*JSONObject a  = new JSONObject(myJSON);

            String b = a.toString().substring(1, a.toString().length() - 1);*/

            Log.i("tagconvertstr", "[" + change + "]");
                //Log.i("..........", "" + street);
                // loop and add it to array or arraylist

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

                    String postReceiverUrl = "http://demo2.piresearch.in/getdata.php";
                    //"http://progresscard.progresscard.in/progress_card/messages/get_messages.php";
                    // HttpClient
                    HttpClient httpClient = new DefaultHttpClient();

                    // post header
                    HttpPost httpPost = new HttpPost(postReceiverUrl);

                    // add your data
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
