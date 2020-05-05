package cs.hku.wallpaper.Image;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cs.hku.wallpaper.R;
import cs.hku.wallpaper.model.ClassResp;
import cs.hku.wallpaper.model.ImageAndText;
import cs.hku.wallpaper.model.ImgResp;
import cs.hku.wallpaper.utils.BitmapHelper;
import cs.hku.wallpaper.utils.Util;

import static cs.hku.wallpaper.utils.Util.getUid;

public class NetImageActivity extends AppCompatActivity {
    GridView gv;
    ArrayList<String> imageUrls = new ArrayList<>();
    ArrayList<String> names = new ArrayList<>();
    String resource = "http://192.168.1.117:6789/public/";
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    InitAdapter();
                    SetOnClick();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_image);
        InitView();
        InitData();
    }
    public void InitView(){
        gv = findViewById(R.id.grid_view);
    }

    public void InitData() {
        String classify = getIntent().getStringExtra("classify");
        ViseHttp.POST("/image/fetch")
                .addParam("imageClass", classify)
                .addParam("uid", String.valueOf(Util.getUid(this)))
                .request(new ACallback<ImgResp>() {
                    @Override
                    public void onSuccess(ImgResp resp) {
                        int status = resp.getStatus();
                        List<String> filenames = resp.getFilename();
                        String message = resp.getMessage();
                        if (status == 0) {
                            Toast.makeText(getApplicationContext(), "fetch image failed, " + message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (String file : filenames) {
                            String[] arr = file.split("@");
                            names.add(arr[0]);
                            imageUrls.add(resource + arr[1]);
                        }
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(getApplicationContext(), "Fetch image failed, " + errMsg + "; code = " + errCode, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void InitAdapter(){
        ImageAdapter imageAdapter = new ImageAdapter(this, imageUrls, names);
        gv.setAdapter(imageAdapter);
    }
    public void SetOnClick(){
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NetImageActivity.this, SingleImage.class);
                intent.putExtra("img", imageUrls.get(position));
                startActivity(intent);
            }
        });

    }

}
