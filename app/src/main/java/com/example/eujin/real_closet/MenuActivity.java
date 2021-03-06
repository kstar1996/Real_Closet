package com.example.eujin.real_closet;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionCloudImageLabelerOptions;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MenuActivity extends AppCompatActivity {

    private static final int GET_IMAGE_FROM_GALLERY_REQUEST = 2; //any positive number
    private static final int IMAGE_PROCESSING_REQUEST = 3;

    private static final int CLOTHES_TYPE_AMBIGUOUS = 0;
    private static final int CLOTHES_TYPE_TOP = 1;
    private static final int CLOTHES_TYPE_BOTTOM = 2;
    private static final int CLOTHES_TYPE_ONE_PIECE = 3;

    private static final String[] CLOTHES_TYPE_TO_STRING = {"Ambiguous", "Top", "Bottom", "One Piece"};

    private static final String TAG = "MenuActivity";

    private ImageButton mButtonGallery;
    private Button mButtonUpload;
    private TextView mTextViewShowUploads;

    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private Uri mProcessedImageUri;
    private int mClothesType;

    private StorageReference mStorageTop;
    private StorageReference mStorageBottom;
    private StorageReference mStorageOne;

    private DatabaseReference mDatabaseTop;
    private DatabaseReference mDatabaseBottom;
    private DatabaseReference mDatabaseOne;

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
        mClothesType = CLOTHES_TYPE_AMBIGUOUS; // INIT

        mStorageTop= FirebaseStorage.getInstance().getReference("top");//save in folder top
        mDatabaseTop = FirebaseDatabase.getInstance().getReference("top");


        mStorageBottom= FirebaseStorage.getInstance().getReference("bottom");//save in folder bottom
        mDatabaseBottom = FirebaseDatabase.getInstance().getReference("bottom");


        mStorageOne= FirebaseStorage.getInstance().getReference("one");//save in folder one
        mDatabaseOne = FirebaseDatabase.getInstance().getReference("one");




        mButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CutOut.activity().start(MenuActivity.this);
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
                    try {
                        FirebaseVisionImage image = FirebaseVisionImage.fromFilePath(getApplicationContext(), mProcessedImageUri);
                        FirebaseVisionCloudImageLabelerOptions options = new FirebaseVisionCloudImageLabelerOptions.Builder().setConfidenceThreshold(0.7f).build();

                        FirebaseVisionImageLabeler labeler = FirebaseVision.getInstance().getCloudImageLabeler(options);
                        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                                // Task completed successfully
                                ArrayList<String> textArray = new ArrayList<String>();
                                for (FirebaseVisionImageLabel label: firebaseVisionImageLabels) {
//                                    String text = label.getText();
//                                    String entityId = label.getEntityId();
//                                    float confidence = label.getConfidence();
//                                    String msg = "text:" + text + ", entityId:" + entityId + ", conf:" + confidence;
//                                    Toast.makeText(MenuActivity.this, msg, Toast.LENGTH_LONG).show();

                                    String text = label.getText();
                                    textArray.add(text);
                                }
                                int cntTop = 0, cntBottom = 0, cntOnePiece = 0;
                                for (String type : textArray) {
                                    switch (type) {
                                        case "Hood":
                                        case "Outerwear":
                                        case "Hoodie":
                                        case "Top":
                                        case "Shirt":
                                        case "T-shirt":
                                        case "Sleeve":
                                        case "Blouse":
                                        case "Jersey":
                                        case "Polo Shirt":
                                        case "Sweater":
                                            cntTop++;
                                            break;
                                        case "Pants":
                                        case "Denim":
                                        case "Jeans":
                                        case "Shorts":
                                        case "Board Short":
                                        case "Trunks":
                                        case "Trousers":
                                        case "Pocket":
                                            cntBottom++;
                                            break;
                                        case "Dress":
                                        case "Day Dress":
                                        case "Cocktail Dress":
                                        case "Bridal Party Dress":
                                            cntOnePiece+=5;
                                            break;
                                        case "A-line":
                                        case "One-piece Garment":
                                        case "Jacket":
                                        case "Coat":
                                        case "Blazer":
                                        case "Suit":
                                        case "Overcoat":
                                        case "Parka":
                                            cntOnePiece++;
                                            break;
                                        default:
                                            break;
                                    }
                                }

                                if (cntTop > cntBottom && cntTop > cntOnePiece) mClothesType = CLOTHES_TYPE_TOP;
                                else if (cntBottom > cntTop && cntBottom > cntOnePiece) mClothesType = CLOTHES_TYPE_BOTTOM;
                                else if (cntOnePiece > cntTop && cntOnePiece > cntBottom) mClothesType = CLOTHES_TYPE_ONE_PIECE;
                                else mClothesType = CLOTHES_TYPE_AMBIGUOUS;

                                Toast.makeText(MenuActivity.this, CLOTHES_TYPE_TO_STRING[mClothesType] + ":" + cntTop + ", " + cntBottom + ", " + cntOnePiece, Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

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
        // 여기서 mClothesType 종류에 따라서 파일 이름 다르게 설정하고 업로드 하면 됨.
        // mClothesType = ambiguous 하다면 타입을 직접 선택할 수 있도록 select box 하나 넣는것도 괜찮을 듯 함.

        if (mProcessedImageUri != null) {//check if we actually chose image

            switch(mClothesType){
                case CLOTHES_TYPE_TOP:
                    final StorageReference TopReference = mStorageTop.child(System.currentTimeMillis()
                            +"."+getFileExtension(mProcessedImageUri));


                    mUploadTask = TopReference.putFile(mProcessedImageUri)
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

                                    String uploadId = mDatabaseTop.push().getKey();
                                    mDatabaseTop.child(uploadId).setValue(upload);
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

                    break;

                case CLOTHES_TYPE_BOTTOM:
                    final StorageReference BottomReference = mStorageBottom.child(System.currentTimeMillis()
                            +"."+getFileExtension(mProcessedImageUri));


                    mUploadTask = BottomReference.putFile(mProcessedImageUri)
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

                                    String uploadId = mDatabaseBottom.push().getKey();
                                    mDatabaseBottom.child(uploadId).setValue(upload);
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


                    break;

                case CLOTHES_TYPE_ONE_PIECE:
                    final StorageReference OneReference = mStorageOne.child(System.currentTimeMillis()
                            +"."+getFileExtension(mProcessedImageUri));


                    mUploadTask = OneReference.putFile(mProcessedImageUri)
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

                                    String uploadId = mDatabaseOne.push().getKey();
                                    mDatabaseOne.child(uploadId).setValue(upload);
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



                    break;

            }


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

