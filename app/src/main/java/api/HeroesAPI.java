package api;

import android.media.Image;

import java.util.List;
import java.util.Map;

import model.Heroes;
import model.ImageResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface HeroesAPI {

    //Using Class
    @POST("heroes")
    Call<Void> registerHeroes(@Body Heroes heroes);

    @GET("heroes")
    Call<List<Heroes>> getHeroes();

    //Using Field
    @FormUrlEncoded
    @POST("heroes")
    Call<Void> addHero (@Field("name") String name, @Field("desc") String desc);

    //Using FieldMap
    @FormUrlEncoded
    @POST("heroes")
    Call<Void> addHero2 (@FieldMap Map<String, String> map);

    //For uploading image
    @Multipart
    @POST("upload")
    Call<ImageResponse> uploadImage(@Part MultipartBody.Part img);

}
