package com.example.user.order_system;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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

public class StartActivity extends AppCompatActivity {
    String url = "http://192.168.0.156/index.php?";
    String json = "", version = "";
    BackGroundWorker backGroundWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        backGroundWorker = new BackGroundWorker();
        backGroundWorker.execute();
    }

    private class BackGroundWorker extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=version");
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
        protected void onProgressUpdate(Void... value) {
            super.onProgressUpdate(value);
            // 背景工作處理"中"更新的事
        }

        @Override
        protected void onPostExecute(Void value) {
            super.onPostExecute(value);
            backGroundWorker = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);
                version = jsonObject.getString("version");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (version.isEmpty()) {
                    Toast.makeText(StartActivity.this, "無法連線伺服器", Toast.LENGTH_LONG).show();
                } else if (version.equals(getResources().getString(R.string.app_ver))) {
                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(StartActivity.this, "版本過低，請更新APP", Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // 背景工作處理前需作的事
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            // 背景工作被取消時作的事
        }
    }
}
