package com.example.eujin.real_closet;

public class UploadActivity {

    private String mName;
    private String mImageUrl;


    public UploadActivity(){
        //empty constructor needed
        //don't delete

    }

    public UploadActivity(String name, String imageUrl){

        if(name.trim().equals("")){
            name="No Name";
        }

        mName=name;
        mImageUrl=imageUrl;

    }

    public String getName() {
        return mName;
    }

    public void setName(String name){
        mName=name;

    }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl=imageUrl;
    }
}
