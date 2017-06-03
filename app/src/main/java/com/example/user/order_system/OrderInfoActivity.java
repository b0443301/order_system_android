package com.example.user.order_system;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
    SelectUserData selectUserData;//新增搜尋功能
    UpdataUserData updataUserData;
    TextView accountTV;
    AutoCompleteTextView emailACTV, usernameACTV, teleACTV, addressACTV;
    Menu menu;
    MenuItem menuItem;
//
//    final static class SocketState {
//        static final int close = 0;
//        static final int open = 1;
//        static final int connect = 2;
//        static final int disconnect = 3;
//    }
//
//    static final int ok = 0;
//
//    final class FileState {
//        final int close = 0;
//        final int open = 1;
//        final int read = 2;
//        final int write = 3;
//    }

    //int socketState = SocketState.close;

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

        selectUserData = new SelectUserData();
        selectUserData.execute();
        SelectUserData selectUserData01 = new SelectUserData();
        selectUserData.execute();
        SelectUserData selectUserData02 = new SelectUserData();
        selectUserData.execute();

//        SocketState test = new SocketState();
//        FileState file = new FileState();

        // Integer.MAX_VALUE

//        ok = 10;
//
//        switch (socketState) {
//            case SocketState.close:
//                break;
//            case SocketState.open:
//                break;
//            case SocketState.connect:
//                break;
//            case SocketState.disconnect:
//                break;
//            default:
//                break;
//        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.userinterface, menu);
        this.menu = menu;
        menuItem = menu.findItem(R.id.action_settings);
        menuItem.setTitle("存檔");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {//
            if (menuItem.getTitle().equals("存檔")) {
                mail = emailACTV.getText().toString();//將mailACTV字串拿到的資料給mail,之後再給
                username = usernameACTV.getText().toString();
                telephone = teleACTV.getText().toString();
                address = addressACTV.getText().toString();
                updataUserData = new UpdataUserData();
                updataUserData.execute();
            }
        }
        return true;
    }


    private class SelectUserData extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {//建立跟網頁的連線
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=select_user&account=" + mAccount);
            try {//json的制式寫法
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
            try {//例外處理
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {//一定會執行
                if (result.equals("select_user_fail")) {//hotcode寫法
                    Toast.makeText(OrderInfoActivity.this, "select_user_fail", Toast.LENGTH_LONG).show();//網頁顯示select_user_fail
                } else if (result.equals("select_user_not_found")) {
                    Toast.makeText(OrderInfoActivity.this, "select_user_not_found", Toast.LENGTH_LONG).show();

                } else if (result.equals("select_user_success")) {
                    Toast.makeText(OrderInfoActivity.this, "select_user_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);

                        mail = jsonObject.getString("mail");//從作業系統拿mail資料要設成全域變數,不然要設成區域變數<void void void>
                        username = jsonObject.getString("username");//資料庫把最新的資料纏回U/I上顯示
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


    private class UpdataUserData extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=update_user&account=" + mAccount + "&username=" + username + "&mail=" + mail + "&telephone=" + telephone + "&address=" + address);
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
                if (result.equals("update_user_fail")) {//資料庫確認資料有無更新成功
                    Toast.makeText(OrderInfoActivity.this, "update_user_fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("update_user_not_found")) {
                    Toast.makeText(OrderInfoActivity.this, "update_user_not_found", Toast.LENGTH_LONG).show();

                } else if (result.equals("update_user_success")) {
                    Toast.makeText(OrderInfoActivity.this, "update_user_success", Toast.LENGTH_LONG).show();
//                    try {
//                        JSONObject jsonObject = new JSONObject(json);
//                        username = jsonObject.getString("username");
//                        username = jsonObject.getString("username");
//                        telephone = jsonObject.getString("telephone");
//                        address = jsonObject.getString("address");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    } finally {
//                        usernameACTV.setText(username);
//                        teleACTV.setText(telephone);
//                        addressACTV.setText(address);
//
//
//                    }
                }

            }
        }
    }


}
