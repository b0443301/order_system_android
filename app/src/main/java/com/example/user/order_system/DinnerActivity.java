package com.example.user.order_system;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
    ArrayList<String> itemList = new ArrayList<>();
    ArrayList<EditText> numberEDList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dinner);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        itemlayout = (LinearLayout) findViewById(R.id.itemlayout);
        spinner = (Spinner) findViewById(R.id.spinner);

        selectStorenameData = new SelectStorenameData();//定義函式搜尋類別
        selectStorenameData.execute();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            selectStorenameData = null;//
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
                        // debuger
                        storenameList.add("!!!CHOICE!!!");//預設(defult)像請選擇按鍵
                        for (int i = 0; i < storename.length(); i++) {
                            storenameList.add(storename.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {//要先從資料庫抓新加入的storename資料,才能觸發spinner,storenameList.add(storename.getString(i))之後
                        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(DinnerActivity.this, android.R.layout.simple_spinner_item, storenameList);//制式寫法,把storenamelist寫道spinner
                        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(spinnerArrayAdapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                itemList.clear();
                                itemlayout.removeAllViews();
                                if (position > 0) {
                                    storename = storenameList.get(position);
                                    selectItemData = new SelectItemData();//定義函式搜尋類別
                                    selectItemData.execute();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
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
            selectItemData = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);//結果錯誤時印出來
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {//finally一定會執行
                if (result.equals("select_item_list_fail")) {
                    Toast.makeText(DinnerActivity.this, "select_item_list_fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_item_list_no_data")) {
                    Toast.makeText(DinnerActivity.this, "select_item_list_no_data", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_item_list_store_not_found")) {
                    Toast.makeText(DinnerActivity.this, "select_item_list_store_not_found", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_item_list_success")) {
                    Toast.makeText(DinnerActivity.this, "select_item_list_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray item = jsonObject.getJSONArray("item");//動態產生
                        for (int i = 0; i < item.length(); i++) {
                            itemList.add(item.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        for (int i = 0; i < itemList.size(); i = i + 2) {//用itemList一次抓菜單名稱和菜單價錢,訂購數量由使用者自行輸入,不需從資料庫抓值給它
                            TextView name = new TextView(DinnerActivity.this);
                            name.setText(itemList.get(i));
                            itemlayout.addView(name);
                            TextView price = new TextView(DinnerActivity.this);
                            price.setText(itemList.get(i + 1));
                            itemlayout.addView(price);
                            EditText number = new EditText(DinnerActivity.this);
                            number.setInputType(InputType.TYPE_CLASS_NUMBER);
                            itemlayout.addView(number);
                            numberEDList.add(number);
                        }
                    }
                }
            }
        }
    }
}