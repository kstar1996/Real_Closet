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

    public String getmName() {
        return mName;
    }

    public void setmName(String name){
        mName=name;

    }

    public String getmImageUrl(){
        return mImageUrl;
    }

    public void setmImageUrl(String imageUrl){
        mImageUrl=imageUrl;
    }
}
