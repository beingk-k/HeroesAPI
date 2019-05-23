package com.example.heroesapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import api.HeroesAPI;
import model.Heroes;
import model.ImageResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import url.Url;

public class AddHeroActivity extends AppCompatActivity {

    private final static String BASE_URL = "http://10.0.2.2:3000/";
    private EditText etName, etDesc;
    private Button btnRegister, btnShow;
    private ImageView imgHero;
    String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_hero);

        etName = findViewById(R.id.etName);
        etDesc = findViewById(R.id.etDesc);
        btnRegister = findViewById(R.id.btnRegister);
        btnShow = findViewById(R.id.btnShow);
        imgHero = findViewById(R.id.imgHero);

        //Function to import image        loadFromURL();

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddHeroActivity.this, ShowHeroesActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });

        imgHero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BrowseImage();
            }
        });
    }


    //Load Image Using URL
   /* private void strictMode(){
        android.os.StrictMode.ThreadPolicy threadPolicy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
        android.os.StrictMode.setThreadPolicy(threadPolicy);
    }

    private void loadFromURL(){
        strictMode();
        try {
            String imgURL = "http://images6.fanpop.com/image/photos/39600000/arya-stark-arya-stark-39631351-1280-1019.jpg";
            URL url = new URL(imgURL);
            imgHero.setImageBitmap(BitmapFactory.decodeStream((InputStream)url.getContent()));
        } catch (IOException e){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }*/


   //Browse Image Function
   private void BrowseImage(){
       Intent intent = new Intent(Intent.ACTION_PICK);
       intent.setType("image/*");
       startActivityForResult(intent, 0);
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (data == null){
                Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show();
            }
        }
        Uri uri = data.getData();
        imagePath = getRealPathFromUri(uri);
        previewImage(imagePath);

    }

    private String getRealPathFromUri (Uri uri){
    String[] projection = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int colIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(colIndex);
        cursor.close();
        return result;
    }

    private void previewImage(String imagePath){
        File imgFile = new File(imagePath);
        if (imgFile.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imgHero.setImageBitmap(myBitmap);
        }
    }

       private void StrictMode(){
        android.os.StrictMode.ThreadPolicy threadPolicy = new android.os.StrictMode.ThreadPolicy.Builder().permitAll().build();
        android.os.StrictMode.setThreadPolicy(threadPolicy);
    }

    private void saveImageOnly(){
       File file = new File(imagePath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("imageFile", file.getName(), requestBody);

        HeroesAPI heroesAPI = Url.getInstance().create(HeroesAPI.class);
        Call<ImageResponse> responseBodyCall = heroesAPI.uploadImage(body);

        StrictMode();

        try{
            Response<ImageResponse> imageResponseResponse = responseBodyCall.execute();
            //Retrieving update name of the image
            String imageName = imageResponseResponse.body().getFilename();
        } catch (IOException e){
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    //Register Heroes Function
    private void Register(){
       saveImageOnly();
        String name = etName.getText().toString();
        String desc = etDesc.getText().toString();

        Map<String,String> map = new HashMap<>();
        map.put("name", name);
        map.put("desc", desc);

        Heroes heroes = new Heroes(name, desc);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        HeroesAPI heroesAPI = retrofit.create(HeroesAPI.class);

        //Using Class      //Call<Void> voidCall = heroesAPI.registerHeroes(heroes);
        //Using Field     //Call<Void> voidCall = heroesAPI.addHero(name, desc);

        //Using FieldMap
        Call<Void> voidCall = heroesAPI.addHero2(map);

        voidCall.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if(!response.isSuccessful())
                {
                    Toast.makeText(AddHeroActivity.this, "" + response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(AddHeroActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(AddHeroActivity.this, "Error: "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
