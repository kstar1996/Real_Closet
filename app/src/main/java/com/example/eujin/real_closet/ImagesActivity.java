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
    private RecyclerView mRecyclerViewOne;

    private ImageAdapter mAdapterTop;
    private ImageAdapter mAdapterBottom;
    private ImageAdapter mAdapterOne;


    private ProgressBar mProgressCircle;

    private FirebaseStorage mStorageTop;
    private FirebaseStorage mStorageBottom;
    private FirebaseStorage mStorageOne;

    private DatabaseReference mDatabaseTop;//get items and fill into list
    private DatabaseReference mDatabaseBottom;
    private DatabaseReference mDatabaseOne;


    private ValueEventListener mDBListenerTop;
    private ValueEventListener mDBListenerBottom;
    private ValueEventListener mDBListenerOne;


    private List<UploadActivity> mUploadsTop;//more flexible
    private List<UploadActivity> mUploadsBottom;
    private List<UploadActivity> mUploadsOne;


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

        mRecyclerViewOne =findViewById(R.id.recycler_view_one);
        mRecyclerViewOne.setHasFixedSize(true);
        mRecyclerViewOne.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));


        mProgressCircle = findViewById(R.id.progress_circle);
        mUploadsTop = new ArrayList<>();//initialize as arraylist
        mUploadsBottom = new ArrayList<>();//initialize as arraylist
        mUploadsOne = new ArrayList<>();//initialize as arraylist

        mAdapterTop = new ImageAdapter(ImagesActivity.this, mUploadsTop);
        mAdapterBottom = new ImageAdapter(ImagesActivity.this, mUploadsBottom);
        mAdapterOne = new ImageAdapter(ImagesActivity.this, mUploadsOne);

        mRecyclerViewTop.setAdapter(mAdapterTop);
        mRecyclerViewBottom.setAdapter(mAdapterBottom);
        mRecyclerViewOne.setAdapter(mAdapterOne);

        mAdapterTop.setOnItemClickListener(ImagesActivity.this);
        mAdapterBottom.setOnItemClickListener(ImagesActivity.this);
        mAdapterOne.setOnItemClickListener(ImagesActivity.this);

        mStorageTop = FirebaseStorage.getInstance();
        mStorageBottom = FirebaseStorage.getInstance();
        mStorageOne = FirebaseStorage.getInstance();

        mDatabaseTop = FirebaseDatabase.getInstance().getReference("top");
        mDatabaseBottom = FirebaseDatabase.getInstance().getReference("bottom");
        mDatabaseOne = FirebaseDatabase.getInstance().getReference("one");


        mDBListenerTop = mDatabaseTop.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploadsTop.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadActivity uploadActivity = postSnapshot.getValue(UploadActivity.class);

                    uploadActivity.setKey(postSnapshot.getKey());

                    mUploadsTop.add(uploadActivity);

                }

                mAdapterTop.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });


        mDBListenerBottom = mDatabaseBottom.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploadsBottom.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadActivity uploadActivity = postSnapshot.getValue(UploadActivity.class);

                    uploadActivity.setKey(postSnapshot.getKey());

                    mUploadsBottom.add(uploadActivity);

                }

                mAdapterBottom.notifyDataSetChanged();

                mProgressCircle.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }
        });




        mDBListenerOne = mDatabaseOne.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploadsOne.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    UploadActivity uploadActivity = postSnapshot.getValue(UploadActivity.class);

                    uploadActivity.setKey(postSnapshot.getKey());

                    mUploadsOne.add(uploadActivity);

                }

                mAdapterOne.notifyDataSetChanged();

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
        UploadActivity selectedItemTop = mUploadsTop.get(position);
        UploadActivity selectedItemBottom = mUploadsBottom.get(position);
        UploadActivity selectedItemOne = mUploadsOne.get(position);

        final String selectedKeyTop = selectedItemTop.getKey();
        final String selectedKeyBottom = selectedItemBottom.getKey();
        final String selectedKeyOne = selectedItemOne.getKey();

        StorageReference imageRefTop = mStorageTop.getReferenceFromUrl(selectedItemTop.getImageUrl());
        StorageReference imageRefBottom = mStorageBottom.getReferenceFromUrl(selectedItemBottom.getImageUrl());
        StorageReference imageRefOne = mStorageOne.getReferenceFromUrl(selectedItemOne.getImageUrl());

        imageRefTop.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseTop.child(selectedKeyTop).removeValue();

                Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();

            }
        });


        imageRefBottom.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabaseBottom.child(selectedKeyBottom).removeValue();

                Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();

            }
        });

        imageRefOne.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                mDatabaseOne.child(selectedKeyOne).removeValue();

                Toast.makeText(ImagesActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();

            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDatabaseTop.removeEventListener(mDBListenerTop);
        mDatabaseBottom.removeEventListener(mDBListenerBottom);
        mDatabaseOne.removeEventListener(mDBListenerOne);
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