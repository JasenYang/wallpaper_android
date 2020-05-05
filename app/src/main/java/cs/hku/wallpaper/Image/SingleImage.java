package cs.hku.wallpaper.Image;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.IOException;

import cs.hku.wallpaper.R;

public class SingleImage extends AppCompatActivity {
    ImageView iv;
    Button setBtn;
    Button delBtn;
    String currentImgUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        InitView();
        SetClickListener();
    }
    public void InitView(){
        iv = findViewById(R.id.current_img);
        setBtn = findViewById(R.id.set_wallpaper);
        delBtn = findViewById(R.id.img_delete);
        Intent intent = getIntent();
        currentImgUrl = intent.getStringExtra("img");
        Glide.with(getApplicationContext()).load(currentImgUrl).into(iv);
    }

    public void SetClickListener(){
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getApplicationContext()).asBitmap().load(currentImgUrl).into(new SimpleTarget<Bitmap>() {

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
                            manager.setBitmap(resource);
                            Toast.makeText(getApplicationContext(), "set wall paper successfully", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
