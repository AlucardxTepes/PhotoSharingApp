/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.alucard;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText usernameField;
    EditText passwordField;
    Button signUpButton;
    TextView changeSignUpModeTextView;
    RelativeLayout mRelativeLayout;
    ImageView logo;
    boolean signUpModeActive;


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.changeSignUpMode:
                if (signUpModeActive) {
                    signUpModeActive = false;
                    changeSignUpModeTextView.setText("Sign Up");
                    signUpButton.setText("Log In");
                } else {
                    signUpModeActive = true;
                    changeSignUpModeTextView.setText("Log In");
                    signUpButton.setText("Sign Up");
                }
                break;
            case R.id.logo:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                break;
        }

    }

    @Override
    public boolean onKey(View view, int i, KeyEvent keyEvent) {

        if (i == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            signUpOrLogin(view);


        }

        return false;
    }

    public void signUpOrLogin(View view) {

        if (signUpModeActive) {
            // sign up
            ParseUser user = new ParseUser();
            user.setUsername(String.valueOf(usernameField.getText()));
            user.setPassword(String.valueOf(passwordField.getText()));

            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "Signup Successful", Toast.LENGTH_SHORT).show();
                        showUserList();
                    } else {
                        Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_SHORT).show();

                    }
                }
            });
        } else {
            // log in
            ParseUser.logInInBackground(usernameField.getText().toString(),
                    passwordField.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (e == null) {
                                Log.i("AppInfo", "Logged in");
                                showUserList();
                            } else {
                                Log.i("AppInfo", e.getMessage());
                            }

                        }
                    });
        }


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }

        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        changeSignUpModeTextView = (TextView) findViewById(R.id.changeSignUpMode);
        logo = (ImageView) findViewById(R.id.logo);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        signUpModeActive = true;

        changeSignUpModeTextView.setOnClickListener(this);
        logo.setOnClickListener(this);
        mRelativeLayout.setOnClickListener(this);
        usernameField.setOnKeyListener(this);
        passwordField.setOnKeyListener(this);


        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    private void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserList.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
