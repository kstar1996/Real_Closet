package com.example.eujin.real_closet;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ProcessingResultActivity extends AppCompatActivity {

    private Button mButtonOK;

    private Uri mOriginalImageUri;
    private Uri mProcessedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_result);

        mButtonOK = findViewById(R.id.btnOK);

        mOriginalImageUri = Uri.parse(getIntent().getStringExtra("uri"));

        mButtonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProcessingResultActivity.this, "OK!!!!!!", Toast.LENGTH_SHORT).show();

                mProcessedImageUri = mOriginalImageUri;

                Intent resultIntent = new Intent();
                resultIntent.putExtra("processedUri", mProcessedImageUri.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}