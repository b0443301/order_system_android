package com.example.user.order_system;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import static com.example.user.order_system.R.id.account;
import static com.example.user.order_system.R.id.password;

public class RegisterActivity extends AppCompatActivity {
    AutoCompleteTextView accountACTV, passwordACTV, checkpassACTV, emailACTV, usernameACTV, teleACTV, addressACTV;
    String json = "", result = "", session = "";
    String url = "http://192.168.0.156/index.php?";
    BackGroundWorker backGroundWorker;
    View mProgressView;
    View mRegisterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountACTV = (AutoCompleteTextView) findViewById(R.id.accountACTV);
        passwordACTV = (AutoCompleteTextView) findViewById(R.id.passwordACTV);
        checkpassACTV = (AutoCompleteTextView) findViewById(R.id.checkpassACTV);
        emailACTV = (AutoCompleteTextView) findViewById(R.id.emailACTV);
        usernameACTV = (AutoCompleteTextView) findViewById(R.id.usernameACTV);
        teleACTV = (AutoCompleteTextView) findViewById(R.id.teleACTV);
        addressACTV = (AutoCompleteTextView) findViewById(R.id.addressACTV);
        mProgressView = findViewById(R.id.progressBar);
        mRegisterView = findViewById(R.id.registerView);

        Intent intent = RegisterActivity.this.getIntent();
        accountACTV.setText(intent.getStringExtra("account"));
        passwordACTV.setText(intent.getStringExtra("password"));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountACTV.setError(null);
                passwordACTV.setError(null);
                checkpassACTV.setError(null);
                emailACTV.setError(null);
                usernameACTV.setError(null);
                teleACTV.setError(null);
                addressACTV.setError(null);

                if (accountACTV.getText().toString().isEmpty()) {
                    accountACTV.setError(getString(R.string.error_field_required_account));
                    accountACTV.requestFocus();
                } else if (passwordACTV.getText().toString().isEmpty()) {
                    passwordACTV.setError(getString(R.string.error_field_required_password));
                    passwordACTV.requestFocus();
                } else if (checkpassACTV.getText().toString().isEmpty()) {
                    checkpassACTV.setError(getString(R.string.error_field_required_checkpass));
                    checkpassACTV.requestFocus();
                } else if (emailACTV.getText().toString().isEmpty()) {
                    emailACTV.setError(getString(R.string.error_field_required_email));
                    emailACTV.requestFocus();
                } else if (usernameACTV.getText().toString().isEmpty()) {
                    usernameACTV.setError(getString(R.string.error_field_required_username));
                    usernameACTV.requestFocus();
                } else if (teleACTV.getText().toString().isEmpty()) {
                    teleACTV.setError(getString(R.string.error_field_required_telephone));
                    teleACTV.requestFocus();
                }  else if (addressACTV.getText().toString().isEmpty()) {
                    addressACTV.setError(getString(R.string.error_field_required_address));
                    addressACTV.requestFocus();
                } else if (!passwordACTV.getText().toString().equals(checkpassACTV.getText().toString())) {
                    checkpassACTV.setError("請核對密碼");
                    checkpassACTV.requestFocus();
                } else {
                    RegisterData registerData = new RegisterData();
                    registerData.mAccount = accountACTV.getText().toString();
                    registerData.mPassword = passwordACTV.getText().toString();
                    registerData.mMail = emailACTV.getText().toString();
                    registerData.mUsername = usernameACTV.getText().toString();
                    registerData.mTelephone = teleACTV.getText().toString();
                    registerData.mAddress = addressACTV.getText().toString();

                    backGroundWorker = new BackGroundWorker(registerData);
                    backGroundWorker.execute();

                    showProgress(true);
                }
            }
        });
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class BackGroundWorker extends AsyncTask<Void, Void, Void> {
        // <傳入參數, 處理中更新介面參數, 處理後傳出參數>

        RegisterData registerData;

        BackGroundWorker(RegisterData registerData) {
            this.registerData = registerData;
        }

//        String mAccount = "", mPassword = "", mUsername = "", mMail = "", mTelephone = "", mAddress = "";
//        BackGroundWorker(String mAccount, String mPassword, String mUsername, String mMail, String mTelephone, String mAddress) {
//            this.mAccount = mAccount;
//            this.mPassword = mPassword;
//            this.mUsername = mUsername;
//            this.mMail = mMail;
//            this.mTelephone = mTelephone;
//            this.mAddress = mAddress;
//        }

        // @Override
        protected Void doInBackground(Void... arg0) {
            // 在背景中處理的耗時工作
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet get = new HttpGet(url + "command=register&account=" + registerData.mAccount + "&password=" + registerData.mPassword +
                    "&mail=" + registerData.mMail + "&username=" + registerData.mUsername + "&telephone=" + registerData.mTelephone + "&address=" + registerData.mAddress);
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
            // 背景工作處理完後需作的事
            try {
                JSONObject jsonObject = new JSONObject(json);
                result = jsonObject.getString("result");
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                backGroundWorker = null;
                showProgress(false);

                if (result.equals("register_success")) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        session = jsonObject.getString("session");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {

                        Intent intent = new Intent(RegisterActivity.this, UserinterfaceActivity.class);
                        intent.putExtra("session", session);
                        Toast.makeText(RegisterActivity.this, "歡迎", Toast.LENGTH_LONG).show();
                        startActivity(intent);
                    }
                } else if (result.equals("register_same_account")) {
                    Toast.makeText(RegisterActivity.this, "已經有相同帳號", Toast.LENGTH_LONG).show();
                } else if (result.equals("register_fail")) {
                    Toast.makeText(RegisterActivity.this, "錯誤", Toast.LENGTH_LONG).show();
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

    class RegisterData {
        String mAccount = "", mPassword = "",  mMail = "", mUsername = "", mTelephone = "", mAddress = "";
    }
}
