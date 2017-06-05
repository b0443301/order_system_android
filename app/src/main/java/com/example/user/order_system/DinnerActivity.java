package com.example.user.order_system;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
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

public class DinnerActivity extends AppCompatActivity {

    LinearLayout itemlayout;
    String url = "http://192.168.0.156/index.php?";
    String json = "", result = "", storename = "";
    SelectStorenameData selectStorenameData;
    SelectItemData selectItemData;
    Spinner spinner;
    ArrayList<String> storenameList = new ArrayList<>();
    ArrayList<EditText> numberEDList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        itemlayout = (LinearLayout) findViewById(R.id.itemlayout);

        spinner = (Spinner) findViewById(R.id.spinner);//制式寫法,把storenamelist寫道spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, storenameList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectItemData = new SelectItemData();//定義函式搜尋類別
                selectItemData.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        selectStorenameData = new SelectStorenameData();//定義函式搜尋類別
        selectStorenameData.execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                TextView name = new TextView(DinnerActivity.this);
//                name.setText("123");
//                itemlayout.addView(name);
//                TextView price = new TextView(DinnerActivity.this);
//                price.setText("456");
//                itemlayout.addView(price);
//                EditText number = new EditText(DinnerActivity.this);
//                number.setInputType(InputType.TYPE_CLASS_NUMBER);
//                itemlayout.addView(number);
//                numberEDList.add(number);
            }
        });
    }

    private class SelectStorenameData extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {//再backgroundworker建立連線
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=select_store_list");
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
            selectStorenameData = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);//結果錯誤時印出來
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {//finally一定會執行
                if (result.equals("select_store_list_no_data")) {
                    Toast.makeText(DinnerActivity.this, "select_store_list_no_data", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_store_list_success")) {
                    Toast.makeText(DinnerActivity.this, "select_store_list_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray storename = jsonObject.getJSONArray("storename");//動態產生
                        for (int i = 0; i < storename.length(); i++) {
                            storenameList.add(storename.getString(i));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {


//                        storenameACTV.setText(storename);
//                        teleACTV.setText(telephone);
//                        addressACTV.setText(address);
//
//                        for (int i = 0; i < nameList.size(); i++) {//namelist可以用.size()[因為是array才用,視情況],.length()
//                            AutoCompleteTextView name = new AutoCompleteTextView(StoreMainActivity.this);
//                            name.setText(nameList.get(i));//設定namelist拿到的值給name上(動態產生出來的)
//                            itemlayout.addView(name);
//                            AutoCompleteTextView price = new AutoCompleteTextView(StoreMainActivity.this);
//                            price.setText(priceList.get(i));
//                            itemlayout.addView(price);
//
//                            //nameACTVList.add(name);//新增nameACYV裡的值,在存檔,職責的部分,有些部分是U/I做,有的在伺服器幫我們做,如果不做sever會有疊加
//                            //priceACTVList.add(price);
                    }
                }
            }
        }
    }

    private class SelectItemData extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {//再backgroundworker建立連線
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=select_item_list&storename=" + storename);
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
            selectStorenameData = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);//結果錯誤時印出來
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {//finally一定會執行
                if (result.equals("select_store_list_no_data")) {
                    Toast.makeText(DinnerActivity.this, "select_store_list_no_data", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_store_list_success")) {
                    Toast.makeText(DinnerActivity.this, "select_store_list_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray storename = jsonObject.getJSONArray("storename");//動態產生
                        for (int i = 0; i < storename.length(); i++) {
                            storenameList.add(storename.getString(i));

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {


//                        storenameACTV.setText(storename);
//                        teleACTV.setText(telephone);
//                        addressACTV.setText(address);
//
//                        for (int i = 0; i < nameList.size(); i++) {//namelist可以用.size()[因為是array才用,視情況],.length()
//                            AutoCompleteTextView name = new AutoCompleteTextView(StoreMainActivity.this);
//                            name.setText(nameList.get(i));//設定namelist拿到的值給name上(動態產生出來的)
//                            itemlayout.addView(name);
//                            AutoCompleteTextView price = new AutoCompleteTextView(StoreMainActivity.this);
//                            price.setText(priceList.get(i));
//                            itemlayout.addView(price);
//
//                            //nameACTVList.add(name);//新增nameACYV裡的值,在存檔,職責的部分,有些部分是U/I做,有的在伺服器幫我們做,如果不做sever會有疊加
//                            //priceACTVList.add(price);
                    }
                }
            }
        }
    }

}