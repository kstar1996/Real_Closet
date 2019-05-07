package com.example.eujin.real_closet;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 7177; //any number
    List<AuthUI.IdpConfig> providers;
    Button btn_signout;

    private static int count = 5;
    private EditText mName;
    private EditText mPassword;
    private TextView mInfo;
    private Button mButtonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_signout=(Button)findViewById(R.id.logout_btn);
        btn_signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Logout
                AuthUI.getInstance()
                        .signOut(MainActivity.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btn_signout.setEnabled(false);
                                showSignInOptions();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, ""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //Init provider
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(), //Email builder
                new AuthUI.IdpConfig.PhoneBuilder().build(), //Phone Builder
                new AuthUI.IdpConfig.FacebookBuilder().build(), //Facebook Builder
                new AuthUI.IdpConfig.GoogleBuilder().build() //Google Builder
        );
        showSignInOptions();

    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.MyTheme)
                .build(),MY_REQUEST_CODE
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==MY_REQUEST_CODE){
            IdpResponse response=IdpResponse.fromResultIntent(data);
            if(resultCode==RESULT_OK){
                //get user
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //Show email on Toast
                Toast.makeText(this, ""+user.getEmail(),Toast.LENGTH_SHORT).show();
                //Set Button signout
                btn_signout.setEnabled(true);
            }
            else{
                Toast.makeText(this, ""+response.getError().getMessage(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}





        /*

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
}*/


