package com.example.eujin.real_closet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<UploadActivity> mUploads;

    public ImageAdapter(Context context, List<UploadActivity> uploads){
        mContext=context;
        mUploads=uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        UploadActivity uploadCurrent = mUploads.get(position);
        holder.textViewName.setText(uploadCurrent.getName());
        //use picasso
        Picasso.with(mContext)
                .load(uploadCurrent.getImageUrl())
                .fit()//resize image to not full size image
                .centerInside() //or centercrop
                .into(holder.imagesView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView textViewName;
        public ImageView imagesView;


        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName=itemView.findViewById(R.id.text_view_name);
            imagesView=itemView.findViewById(R.id.image_view_upload);
        }
    }
}
