package com.example.eujin.real_closet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int count = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Name=findViewById(R.id.etName);
        Password=findViewById((R.id.etPassword));
        Info=findViewById(R.id.tvInfo);
        Login=findViewById(R.id.btnLogin);

        Info.setText("# of attempts remaining: 5");

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(Name.getText().toString(),Password.getText().toString());

            }
        });

    }

    private void validate(String userName, String userPassword){
        if ((userName.equals("a")) && (userPassword.equals("a"))){
            Intent intent=new Intent(MainActivity.this, MenuActivity.class);
            //start and destination
            startActivity(intent);
        }

        else{
            count--; //after 5 times button is disabled
            Info.setText("# of attemps remaining: " + String.valueOf(count));
            if(count==0){
                Login.setEnabled(false); //disable button
            }
        }
    }
}
