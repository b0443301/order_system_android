package com.example.user.order_system;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AutoCompleteTextView;

public class RegisterActivity extends AppCompatActivity {
    AutoCompleteTextView accountACTV, passwordACTV, checkpassACTV, usernameACTV, teleACTV, emailACTV, addressACTV;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}
