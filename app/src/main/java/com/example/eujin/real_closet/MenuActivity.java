package com.example.eujin.real_closet;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MenuActivity extends AppCompatActivity {

    //Variables

    private Button btnGallery;
    private ImageView imageView;


    private Uri filePath;






    private static final int PICK_FROM_GALLERY;

    static { PICK_FROM_GALLERY = 1; }
    private static final int PICK_FROM_CAMERA = 2;
    private static final String TAG = "Closet";

    ImageButton btn_camera, btn_gallery;

    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //init view


        btn_camera = (ImageButton) findViewById(R.id.btnCamera);
        btn_gallery = (ImageButton) findViewById(R.id.btnGallery);

        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();

            }
        });

        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                goToGallery();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            if (tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e(TAG, tempFile.getAbsolutePath() + "Delete Complete");
                        tempFile = null;
                    }
                }
            }
            return;
        }

        if (requestCode == PICK_FROM_GALLERY) {
            Uri photoUri = data.getData();
            Log.d(TAG, "PICK_FROM_GALLERY photoUri: " + photoUri);

            Cursor cursor = null;

            try {
                String[] proj = { MediaStore.Images.Media.DATA };

                assert photoUri != null;
                cursor = getContentResolver().query(photoUri, proj, null, null, null);

                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                cursor.moveToFirst();
                tempFile = new File(cursor.getString(column_index));
                Log.d(TAG, "tempFile Uri: " + Uri.fromFile(tempFile));
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            setImage();
        } else if (requestCode == PICK_FROM_CAMERA) {
            setImage();
        }
    }

    private void goToGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_GALLERY);
    }

    private void takePhoto(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            finish();
            e.printStackTrace();
        }

        if (tempFile != null) {
            Uri photoUri = Uri.fromFile(tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PICK_FROM_CAMERA);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "closet_" + timeStamp;

        File storageDir = new File(Environment.getExternalStorageDirectory() + "/closet/");
        if (!storageDir.exists())
            storageDir.mkdirs();

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        return image;
    }

    private void setImage() {
        ImageView imageView = findViewById(R.id.imageView);

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap originalBm = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        Log.d(TAG, "setImage: " + tempFile.getAbsolutePath());

        imageView.setImageBitmap(originalBm);

        tempFile = null;
    }
}
