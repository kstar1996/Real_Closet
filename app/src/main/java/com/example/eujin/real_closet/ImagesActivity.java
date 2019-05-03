package com.example.eujin.real_closet;


import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ImagesActivity extends AppCompatActivity implements ImageAdapter.OnItemClickListener, DeleteDialogActivity.DialogListener {

    private RecyclerView mRecyclerViewTop;
    private RecyclerView mRecyclerViewBottom;
    private RecyclerView mRecyclerViewBBottom;

    private ImageAdapter mAdapter;

    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;//get items and fill into list

    private ValueEventListener mDBListener;
    private List<UploadActivity> mUploads;//more flexible


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mRecyclerViewTop = findViewById(R.id.recycler_view_top);
        mRecyclerViewTop.setHasFixedSize(true);
        mRecyclerViewTop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mRecyclerViewBottom=findViewById(R.id.recycler_view_bottom);
        mRecyclerViewBottom.setHasFixedSize(true);
        mRecyclerViewBottom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mRecyclerViewBBottom=findViewById(R.id.recycler_view_bbottom);
        mRecyclerViewBBottom.setHasFixedSize(true);
        mRecyclerViewBBottom.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        mProgressCircle = findViewById(R.id.progress_circle);
        mUploads = new ArrayList<>();//initialize as arraylist

        mAdapter = new ImageAdapter(ImagesActivity.this, mUploads);

        mRecyclerViewTop.setAdapter(mAdapter);
        mRecyclerViewBottom.setAdapter(mAdapter);
        mRecyclerViewBBottom.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(ImagesActivity.this);

        mStorage = FirebaseStorage.getInstance();

        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDBListener = mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadActivity uploadActivity = postSnapshot.getValue(UploadActivity.class);

                    uploadActivity.setKey(postSnapshot.getKey());

                    mUploads.add(uploadActivity);

                }

                mAdapter.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(this, "Normal click at position: " + position, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onInfoClick(int position) {
        Toast.makeText(this, "Info click at position: " + position, Toast.LENGTH_SHORT).show();//text at whatever click


    }

    @Override
    public void onDeleteClick(int position) {

        openDialog(); //dialog activity before actually deleting picture

        onYesClicked();
        UploadActivity selectedItem = mUploads.get(position);
        final String selectedKey = selectedItem.getKey();

        StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseRef.child(selectedKey).removeValue();
                Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();

            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseRef.removeEventListener(mDBListener);
    }

    public void openDialog(){
        DeleteDialogActivity dialog= new DeleteDialogActivity();
        dialog.show(getSupportFragmentManager(), "example dialog");
    }


    @Override
    public void onYesClicked(){
        Toast.makeText(this, "Yes clicked", Toast.LENGTH_SHORT).show();

    }
}