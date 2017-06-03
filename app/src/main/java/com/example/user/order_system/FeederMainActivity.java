package com.example.user.order_system;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class FeederMainActivity extends AppCompatActivity {
    BackGroundWorker backGroundWorker;
    AutoCompleteTextView storenameACTV;
    String session = "", mAccount = "", result = "",storename="";
    String url = "http://192.168.0.156/index.php?";
    String json = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeder_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storenameACTV = (AutoCompleteTextView) findViewById(R.id.storenameACTV);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.userinterface, menu);

        return true;
    }private class BackGroundWorker extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=select_user&account=" + mAccount);
            try {
                HttpResponse response = httpClient.execute(get);
                HttpEntity entity = response.getEntity();
                json = EntityUtils.toString(entity);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpClient.getConnectionManager().shutdown();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Void value) {
            super.onPostExecute(value);
            backGroundWorker = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (result.equals("select_feeder_fail")) {
                    Toast.makeText(FeederMainActivity.this, "select_feeder__fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_feeder_not_found")) {
                    Toast.makeText(FeederMainActivity.this, "select_feeder_not_found", Toast.LENGTH_LONG).show();

                } else if (result.equals("select_feeder_success")) {
                    Toast.makeText(FeederMainActivity.this, "select_feeder_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        storename = jsonObject.getString("storename");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        storenameACTV.setText(storename);
                    }
                }

            }
        }
    }






}
