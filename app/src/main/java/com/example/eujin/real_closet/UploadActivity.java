package com.example.eujin.real_closet;


import com.google.firebase.database.Exclude;

public class UploadActivity {

    private String mName;
    private String mImageUrl;
    private String mKey;


    public UploadActivity(){
        //empty constructor needed
        //don't delete
        //지우지마아아

    }

    public UploadActivity(String name, String imageUrl){

//        if(name.trim().equals("")){
//            name="No Name";
//        }


        mName="";
        mImageUrl=imageUrl;

    }

    public String getName() {
        return mName;
    }

    public void setName(String name){ mName=name; }

    public String getImageUrl(){
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl){
        mImageUrl=imageUrl;
    }

    @Exclude
    public String getKey(){
        return mKey;
    }

    @Exclude
    public void setKey(String key){
        mKey=key;
    }
}
