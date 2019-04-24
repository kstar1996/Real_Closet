package com.example.eujin.real_closet;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gabrielbb.cutout.CutOut;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;


public class MenuActivity extends AppCompatActivity {

    private static final int GET_IMAGE_FROM_GALLERY_REQUEST = 2; //any positive number
    private static final int IMAGE_PROCESSING_REQUEST = 3;

    private static final String TAG = "MenuActivity";

    private ImageButton mButtonGallery;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mProcessedImageUri;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mButtonGallery = findViewById(R.id.btnGallery);
        mButtonUpload = findViewById(R.id.btnUpload);
        mTextViewShowUploads = findViewById(R.id.my_closet);
        mEditTextFileName = findViewById(R.id.edit_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);

        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CutOut.activity().bordered(Color.BLUE).start(MenuActivity.this);
//                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(MenuActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
            }
        });

        mTextViewShowUploads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagesActivity();
            }
        });
    }

//    private void openFileChooser() {
//        // Open Gallery. Not used.
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, GET_IMAGE_FROM_GALLERY_REQUEST);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CutOut.CUTOUT_ACTIVITY_REQUEST_CODE:
                    mProcessedImageUri = CutOut.getUri(data); // Get image.
                    // Use firebase ML kit. (image labeling)


                    Picasso.with(this).load(mProcessedImageUri).into(mImageView);
                    break;
//                case GET_IMAGE_FROM_GALLERY_REQUEST:
//                    if (data != null && data.getData() != null) {
//                        imageProcessing(data.getData());
//                    }
//                    break;
//                case IMAGE_PROCESSING_REQUEST:
//                    if (data != null) {
//                        Toast.makeText(this, "received", Toast.LENGTH_SHORT).show();
//                        mProcessedImageUri = Uri.parse(data.getStringExtra("processedUri"));
//                        Picasso.with(this).load(mProcessedImageUri).into(mImageView);
//                    }
//                    break;
            }
        }
    }

//    private void imageProcessing(Uri originalImageUri) {
//        Toast.makeText(this, "Inside imageProcessing", Toast.LENGTH_SHORT).show();
//
//        Intent intent = new Intent(this, ProcessingResultActivity.class);
//        intent.putExtra("uri", originalImageUri.toString());
//        startActivityForResult(intent, IMAGE_PROCESSING_REQUEST);
//    }

    private String getFileExtension(Uri uri){
        ContentResolver cR =getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

        //responsibility is to get file extension from image
    }

    private void uploadFile() {

        if (mProcessedImageUri != null) {//check if we actually chose image
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    +"."+getFileExtension(mProcessedImageUri));

            mUploadTask = fileReference.putFile(mProcessedImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 500);

                            Toast.makeText(MenuActivity.this,"Upload successful", Toast.LENGTH_LONG).show();

                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful());

                            Uri downloadUrl = urlTask.getResult();

                            Log.d(TAG, "onSuccess:firebase download url:" + downloadUrl.toString());
                            UploadActivity upload = new UploadActivity(mEditTextFileName.getText().toString().trim(),downloadUrl.toString());

                            String uploadId = mDatabaseRef.push().getKey();
                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MenuActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int)progress);
                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            // Toast.makeText(getApplicationContext(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity() {
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }
}

