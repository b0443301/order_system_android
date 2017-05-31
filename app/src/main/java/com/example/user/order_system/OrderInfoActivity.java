package com.example.user.order_system;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
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

public class OrderInfoActivity extends AppCompatActivity {


    String session = "", mAccount = "", result = "", mail = "", username = "", telephone = "", address = "";
    String url = "http://192.168.0.156/index.php?";
    String json = "";
    BackGroundWorker backGroundWorker;
    TextView accountTV;
    AutoCompleteTextView emailACTV, usernameACTV, teleACTV, addressACTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountTV = (TextView) findViewById(R.id.accountTV);
        emailACTV = (AutoCompleteTextView) findViewById(R.id.emailACTV);
        usernameACTV = (AutoCompleteTextView) findViewById(R.id.usernameACTV);
        teleACTV = (AutoCompleteTextView) findViewById(R.id.teleACTV);
        addressACTV = (AutoCompleteTextView) findViewById(R.id.addressACTV);

        Intent intent = OrderInfoActivity.this.getIntent();
        session = intent.getStringExtra("session");
        mAccount = intent.getStringExtra("account");
        accountTV.setText(mAccount);

        backGroundWorker = new BackGroundWorker();
        backGroundWorker.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.userinterface, menu);

        return true;
    }

    private class BackGroundWorker extends AsyncTask<Void, Void, Void> {
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
                if (result.equals("select_user_fail")) {
                    Toast.makeText(OrderInfoActivity.this, "select_user_fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_user_not_found")) {
                    Toast.makeText(OrderInfoActivity.this, "select_user_not_found", Toast.LENGTH_LONG).show();

                } else if (result.equals("select_user_success")) {
                    Toast.makeText(OrderInfoActivity.this, "select_user_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        mail = jsonObject.getString("mail");
                        username = jsonObject.getString("username");
                        telephone = jsonObject.getString("telephone");
                        address = jsonObject.getString("address");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        emailACTV.setText(mail);
                        usernameACTV.setText(username);
                        teleACTV.setText(telephone);
                        addressACTV.setText(address);


                    }
                }

            }
        }
    }
}
