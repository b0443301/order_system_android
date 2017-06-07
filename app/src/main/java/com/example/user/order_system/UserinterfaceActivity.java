package com.example.user.order_system;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

public class UserinterfaceActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private boolean mTwoPane;
    Menu menu;
    MenuItem menuItem;
    String url = "http://192.168.0.156/index.php?";
    String json = "", result = "", mAccount = "", session = "";
    UpdateOrderItemShow updateOrderItemShow;
    ArrayList<String> storenameList = new ArrayList<>();
    ArrayList<String> itemList = new ArrayList<>();
    ArrayList<String> numberList = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    FloatingActionButton fab;

    private ListView listView;
    //String[] list ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userinterface);


        Intent intent = UserinterfaceActivity.this.getIntent();
        session = intent.getStringExtra("session");
        mAccount = intent.getStringExtra("account");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        if (findViewById(R.id.item_detail_container) != null) {
//            // The detail container view will be present only in the
//            // large-screen layouts (res/values-w900dp).
//            // If this view is present, then the
//            // activity should be in two-pane mode.
//            mTwoPane = true;
//        }

        listView = (ListView) findViewById(R.id.listview);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserinterfaceActivity.this, DinnerActivity.class);
                intent.putExtra("account", mAccount);
                intent.putExtra("session", session);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);//預設畫面是訂餐者

        updateOrderItemShow = new UpdateOrderItemShow();
        updateOrderItemShow.execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.userinterface, menu);
        this.menu = menu;
        menuItem = menu.findItem(R.id.action_settings);
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
            if (menuItem.getTitle().equals(getString(R.string.menu_Item_info))) {
                Intent intent = new Intent(UserinterfaceActivity.this, OrderInfoActivity.class);
                intent.putExtra("session", session);
                intent.putExtra("account", mAccount);
                startActivity(intent);
            } else if (menuItem.getTitle().equals(getString(R.string.store_Item_info))) {
                Intent intent = new Intent(UserinterfaceActivity.this, StoreMainActivity.class);
                intent.putExtra("session", session);
                intent.putExtra("account", mAccount);
                startActivity(intent);
            } else if (menuItem.getTitle().equals(getString(R.string.employee_Item_info))) {
                Intent intent = new Intent(UserinterfaceActivity.this, FeederMainActivity.class);
                intent.putExtra("session", session);
                intent.putExtra("account", mAccount);
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.orderperson) {
            UserinterfaceActivity.this.setTitle(getString(R.string.order));
            menuItem.setTitle(getString(R.string.menu_Item_info));
            fab.setVisibility(View.VISIBLE);
        }
        if (id == R.id.strore) {
            UserinterfaceActivity.this.setTitle(getString(R.string.store));
            menuItem.setTitle(getString(R.string.store_Item_info));
            fab.setVisibility(View.INVISIBLE);
        }
        if (id == R.id.feeder) {
            UserinterfaceActivity.this.setTitle(getString(R.string.feeder));
            menuItem.setTitle(getString(R.string.employee_Item_info));
            fab.setVisibility(View.INVISIBLE);
        }
        if (id == R.id.setting) {
            Intent intent = new Intent(UserinterfaceActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
// else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class UpdateOrderItemShow extends AsyncTask<Void, Void, Void> {
        //        String account,storename;
//        ArrayList<String> itemList,numberList;
//
//        OrderDinner(String account, String storename, ArrayList<String> itemList, ArrayList<String> numberList){
//            this.account = account;
//            this.storename = storename;
//            this.itemList = itemList;
//            this.numberList = numberList;
//        }
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>
        @Override
        protected Void doInBackground(Void... arg0) {//再backgroundworker建立連線
            // 在背景中處理的耗時工作
            String getString = url + "command=select_dinner_by_account&account=" + mAccount;
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
            updateOrderItemShow = null;
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (result.equals("select_dinner_by_account_fail")) {//資料庫確認資料有無更新成功
                    Toast.makeText(UserinterfaceActivity.this, "select_dinner_by_account_fail", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_dinner_by_account_not_data")) {
                    Toast.makeText(UserinterfaceActivity.this, "select_dinner_by_account_not_data", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_dinner_by_account_user_not_found")) {
                    Toast.makeText(UserinterfaceActivity.this, "select_dinner_by_account_user_not_found", Toast.LENGTH_LONG).show();
                } else if (result.equals("select_dinner_by_account_success")) {
                    Toast.makeText(UserinterfaceActivity.this, "select_dinner_by_account_success", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray storenameJSON = jsonObject.getJSONArray("storename");
                        JSONArray itemnameJSON = jsonObject.getJSONArray("itemname");
                        JSONArray numberJSON = jsonObject.getJSONArray("number");
                        for (int i = 0; i < storenameJSON.length(); i++) {
                            storenameList.add(storenameJSON.getString(i));
                        }
                        for (int i = 0; i < itemnameJSON.length(); i++) {
                            itemList.add(itemnameJSON.getString(i));
                        }
                        for (int i = 0; i < numberJSON.length(); i++) {
                            numberList.add(numberJSON.getString(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        for (String e : storenameList) {
                            list.add(e);
                        }
                        for (String e : itemList) {
                            list.add(e);
                        }
                        for (String e : numberList) {
                            list.add(e);
                        }

                        ArrayAdapter<String>  listAdapter = new ArrayAdapter(UserinterfaceActivity.this, android.R.layout.simple_list_item_1, list);
                        listView.setAdapter(listAdapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(getApplicationContext(), "你選擇的是" + list.get(position), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    }
}




