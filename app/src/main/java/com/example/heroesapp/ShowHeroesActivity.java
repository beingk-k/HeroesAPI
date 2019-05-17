package com.example.heroesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

import api.HeroesAPI;
import model.Heroes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowHeroesActivity extends AppCompatActivity {

    private final static String BASE_URL = "http://10.0.2.2:3000/";
    private TextView tvData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_heroes);

        tvData = findViewById(R.id.tvData);

        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        HeroesAPI heroesAPI = retrofit.create(HeroesAPI.class);

        Call<List<Heroes>> listCall = heroesAPI.getHeroes();

        listCall.enqueue(new Callback<List<Heroes>>() {
            @Override
            public void onResponse(Call<List<Heroes>> call, Response<List<Heroes>> response) {

                if(!response.isSuccessful()){
                    Toast.makeText(ShowHeroesActivity.this, "Code: "+response.code(), Toast.LENGTH_SHORT).show();
                    return;
                }

                List<Heroes> heroesList =response.body();
                for (Heroes heroes : heroesList){
                    String content = "";
                    content += "Name    :" + heroes.getName() + "\n";
                    content += "Desc    :" + heroes.getDesc() + "\n";
                    content += "-----------------------\n";

                    tvData.append(content);
                }

            }

            @Override
            public void onFailure(Call<List<Heroes>> call, Throwable t) {
                Toast.makeText(ShowHeroesActivity.this, "Error: "+t.getMessage(), Toast.LENGTH_SHORT).show();
                return;

            }
        });

    }
}
