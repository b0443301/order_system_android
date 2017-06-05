package com.example.user.order_system;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

    AutoCompleteTextView storenameACTV;
    String session = "", mAccount = "", result = "", storename = "";
    String url = "http://192.168.0.156/index.php?";
    String json = "";
    Menu menu;
    MenuItem menuItem;
    UpdataUserData updataUserData;
    SelectUserData selectUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeder_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        storenameACTV = (AutoCompleteTextView) findViewById(R.id.storenameACTV);

        Intent intent = FeederMainActivity.this.getIntent();//把封裝好的資料給StoreActivity
        session = intent.getStringExtra("session");//拿裡面的資料用標籤"session"
        mAccount = intent.getStringExtra("account");

        selectUserData = new SelectUserData();//定義函式搜尋類別
        selectUserData.execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {//建立存檔右上圖形...
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.userinterface, menu);
        this.menu = menu;
        menuItem = menu.findItem(R.id.action_settings);
        menuItem.setTitle(getResources().getString(R.string.menuItem_save));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (menuItem.getTitle().equals(getResources().getString(R.string.menuItem_save))) {
                storename = storenameACTV.getText().toString();
                updataUserData = new UpdataUserData();
                updataUserData.execute();
            }
        }

        return true;
    }

    private class SelectUserData extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {//再backgroundworker建立連線
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=select_feeder&account=" + mAccount);
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
            selectUserData = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);//結果錯誤時印出來
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {//finally一定會執行
                if (result.equals("select_feeder_no_data")) {
                    Toast.makeText(FeederMainActivity.this, "select_feeder_no_data", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_feeder_user_not_found")) {
                    Toast.makeText(FeederMainActivity.this, "select_feeder_user_not_found", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_store_success")) {
                    Toast.makeText(FeederMainActivity.this, "select_store_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        storename = jsonObject.getString("storename");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        storenameACTV.setText(storename);
                    }
                } else if (result.equals("select_feeder_fail")) {
                    Toast.makeText(FeederMainActivity.this, "select_feeder_fail", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private class UpdataUserData extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {
            // 在背景中處理的耗時工作
            String getString = url + "command=update_feeder&account=" + mAccount + "&storename=" + storename;

            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(getString);
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
            updataUserData = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (result.equals("update_feeder_fail")) {
                    Toast.makeText(FeederMainActivity.this, "update_feeder_fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("update_feeder_user_not_found")) {
                    Toast.makeText(FeederMainActivity.this, "update_feeder_user_not_found", Toast.LENGTH_LONG).show();
                } else if (result.equals("update_feeder_success")) {
                    Toast.makeText(FeederMainActivity.this, "update_feeder_success", Toast.LENGTH_LONG).show();
                } else if (result.equals("update_feeder_store_not_found")) {
                    Toast.makeText(FeederMainActivity.this, "update_feeder_store_not_found", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}



