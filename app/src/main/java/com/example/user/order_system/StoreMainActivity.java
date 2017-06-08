package com.example.user.order_system;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class StoreMainActivity extends AppCompatActivity {
    LinearLayout itemlayout;
    String url = "http://192.168.0.156/index.php?";
    String json = "", session = "", mAccount = "", storename = "", result = "", telephone = "", address = "";
    AutoCompleteTextView storenameACTV, teleACTV, addressACTV;
    Menu menu;
    MenuItem menuItem;
    UpdataUserData updataUserData;
    SelectUserData selectUserData;


    // String namelist[] ;
    //String pricelist[] ;
//    List<String []> name = new ArrayList<>();
//    List<String []> price = new ArrayList<>();
//    AutoCompleteTextView autoCompleteTextView;
    ArrayList<AutoCompleteTextView> nameACTVList = new ArrayList<>();//泛型可陣列不同型別,這裡是用Array陣列AutoCompleteTextView定義
    ArrayList<AutoCompleteTextView> priceACTVList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> priceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);//標頭圖形
        setSupportActionBar(toolbar);

        itemlayout = (LinearLayout) findViewById(R.id.itemlayout);
        storenameACTV = (AutoCompleteTextView) findViewById(R.id.storenameACTV);
        teleACTV = (AutoCompleteTextView) findViewById(R.id.teleACTV);
        addressACTV = (AutoCompleteTextView) findViewById(R.id.addressACTV);

        Intent intent = StoreMainActivity.this.getIntent();//把封裝好的資料給StoreActivity
        session = intent.getStringExtra("session");//拿裡面的資料用標籤"session"
        mAccount = intent.getStringExtra("account");

        selectUserData = new SelectUserData();//定義函式搜尋類別
        selectUserData.execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoCompleteTextView name = new AutoCompleteTextView(StoreMainActivity.this);
                itemlayout.addView(name);
                AutoCompleteTextView price = new AutoCompleteTextView(StoreMainActivity.this);
                price.setInputType(InputType.TYPE_CLASS_NUMBER);
                itemlayout.addView(price);

                nameACTVList.add(name);//新增nameACYV裡的值,在存檔
                priceACTVList.add(price);
            }
        });
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
//                for (int i = 0; i < name; i++) {
//                    name = name.get(i);
//
//                }
//                for (int j = 0; j < price; j++) {
//                    price =
//                }
                nameList.clear();//先清空namelist新增的值
                priceList.clear();

                for (AutoCompleteTextView e : nameACTVList) {//對新加入的name,進行存檔,列印出來
                    nameList.add(e.getText().toString());
                }
                for (AutoCompleteTextView e : priceACTVList) {//對新加入的price,進行存檔,列印出來
                    priceList.add(e.getText().toString());
                }

                storename = storenameACTV.getText().toString();
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
        protected Void doInBackground(Void... arg0) {//再backgroundworker建立連線
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=select_store&account=" + mAccount);
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
            nameList.clear();
            priceList.clear();
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);//結果錯誤時印出來
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {//finally一定會執行
                if (result.equals("select_store_fail")) {
                    Toast.makeText(StoreMainActivity.this, "select_store_fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_store_user_not_found")) {
                    Toast.makeText(StoreMainActivity.this, "select_store_user_not_found", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_store_no_data")) {
                    Toast.makeText(StoreMainActivity.this, "select_store_no_data", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_store_success")) {
                    Toast.makeText(StoreMainActivity.this, "select_store_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        storename = jsonObject.getString("storename");
                        telephone = jsonObject.getString("telephone");
                        address = jsonObject.getString("address");
                        JSONArray item = jsonObject.getJSONArray("item");//動態產生
                        for (int i = 0; i < item.length(); i += 2) {//一次進兩位,["Abc","789"]
                            nameList.add(item.getString(i));//一次進兩位,["123"],0,2從開始算
                            priceList.add(item.getString(i + 1));//一次進兩位,["456"],1,3從開始算
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        storenameACTV.setText(storename);
                        teleACTV.setText(telephone);
                        addressACTV.setText(address);

                        for (int i = 0; i < nameList.size(); i++) {//namelist可以用.size()[因為是array才用,視情況],.length()
                            AutoCompleteTextView name = new AutoCompleteTextView(StoreMainActivity.this);
                            name.setText(nameList.get(i));//設定namelist拿到的值給name上(動態產生出來的)
                            itemlayout.addView(name);
                            AutoCompleteTextView price = new AutoCompleteTextView(StoreMainActivity.this);
                            price.setText(priceList.get(i));
                            itemlayout.addView(price);

                            //nameACTVList.add(name);//新增nameACYV裡的值,在存檔,職責的部分,有些部分是U/I做,有的在伺服器幫我們做,如果不做sever會有疊加
                            //priceACTVList.add(price);
                        }
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
            String getString = url + "command=update_store&account=" + mAccount + "&storename=" + storename + "&telephone=" + telephone + "&address=" + address;
            for (String e : nameList) {
                getString = getString + "&name[]=" + e;
            }
            for (String e : priceList) {
                getString = getString + "&price[]=" + e;
            }

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
                if (result.equals("update_store_fail")) {
                    Toast.makeText(StoreMainActivity.this, "update_store_fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("update_store_user_not_found")) {
                    Toast.makeText(StoreMainActivity.this, "update_store_user_not_found", Toast.LENGTH_LONG).show();
                } else if (result.equals("update_store_success")) {
                    Toast.makeText(StoreMainActivity.this, "update_store_success", Toast.LENGTH_LONG).show();
                } else if (result.equals("update_store_new_data")) {
                    Toast.makeText(StoreMainActivity.this, "update_store_new_data", Toast.LENGTH_LONG).show();
                }
            }
        }
    }




}
