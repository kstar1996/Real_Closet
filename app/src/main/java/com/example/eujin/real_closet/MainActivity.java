package com.example.eujin.real_closet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static int count = 5;
    private EditText mName;
    private EditText mPassword;
    private TextView mInfo;
    private Button mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mName =findViewById(R.id.etName);
        mPassword =findViewById((R.id.etPassword));
        mInfo =findViewById(R.id.tvInfo);
        mButtonLogin =findViewById(R.id.btnLogin);

        mInfo.setText("# of attempts remaining: 5");

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(mName.getText().toString(), mPassword.getText().toString());

            }
        });
    }

    private void validate(String userName, String userPassword){
        if ((userName.equals("a")) && (userPassword.equals("a"))){
            Intent intent=new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }

        else{
            count--; //after 5 times button is disabled
            mInfo.setText("# of attemps remaining: " + String.valueOf(count));
            if(count==0){
                mButtonLogin.setEnabled(false); //disable button
            }
        }
    }
}
