package com.example.user.order_system;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class RegisterActivity extends AppCompatActivity {
    AutoCompleteTextView accountACTV, passwordACTV, checkpassACTV, usernameACTV, teleACTV, emailACTV, addressACTV;
    boolean cancel = false;
    View focusView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountACTV = (AutoCompleteTextView) findViewById(R.id.accountACTV);
        passwordACTV = (AutoCompleteTextView) findViewById(R.id.passwordACTV);
        checkpassACTV = (AutoCompleteTextView) findViewById(R.id.checkpassACTV);
        usernameACTV = (AutoCompleteTextView) findViewById(R.id.usernameACTV);
        teleACTV = (AutoCompleteTextView) findViewById(R.id.teleACTV);
        emailACTV = (AutoCompleteTextView) findViewById(R.id.emailACTV);
        addressACTV = (AutoCompleteTextView) findViewById(R.id.addressACTV);

        Intent intent = RegisterActivity.this.getIntent();
        accountACTV.setText(intent.getStringExtra("account"));
        passwordACTV.setText(intent.getStringExtra("password"));


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                accountACTV.setError(null);
                passwordACTV.setError(null);
                checkpassACTV.setError(null);
                usernameACTV.setError(null);
                teleACTV.setError(null);
                emailACTV.setError(null);
                addressACTV.setError(null);

                if (accountACTV.getText().toString().isEmpty()) {
                    accountACTV.setError(getString(R.string.error_field_required_account));
                    accountACTV.requestFocus();
                }
                else if (passwordACTV.getText().toString().isEmpty()) {
                    passwordACTV.setError(getString(R.string.error_field_required_password));
                    passwordACTV.requestFocus();
                }
                else if (checkpassACTV.getText().toString().isEmpty()) {
                    checkpassACTV.setError(getString(R.string.error_field_required_checkpass));
                    checkpassACTV.requestFocus();
                }
                else if (usernameACTV.getText().toString().isEmpty()) {
                    usernameACTV.setError(getString(R.string.error_field_required_username));
                    usernameACTV.requestFocus();
                }
                else if (teleACTV.getText().toString().isEmpty()) {
                    teleACTV.setError(getString(R.string.error_field_required_telephone));
                    teleACTV.requestFocus();
                }
                else if (emailACTV.getText().toString().isEmpty()) {
                    emailACTV.setError(getString(R.string.error_field_required_email));
                    emailACTV.requestFocus();
                }
                else if (addressACTV.getText().toString().isEmpty()) {
                    addressACTV.setError(getString(R.string.error_field_required_address));
                    addressACTV.requestFocus();
                }


            }
        });
    }
}
