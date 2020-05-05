package cs.hku.wallpaper.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;
import com.vise.xsnow.http.callback.UCallback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cs.hku.wallpaper.R;
import cs.hku.wallpaper.utils.Util;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;

    ImageView imageView;
    Button setBtn;
    Button deleteBtn;
    Button local_upload_img;
    Bitmap currentBit;

    FragmentActivity fa;
    File currentFile;

    EditText upload_img_name;
    EditText upload_img_class;
    private View root;

    private int MAXVALUE = 100;
    private int currentProgress = 0;
    private ProgressDialog progressDialog;
    //更新UI界面
    @SuppressLint("HandlerLeak")
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    progressDialog.setProgress(currentProgress);
                    if(currentProgress>=MAXVALUE){
                        Toast.makeText(getActivity(), "Upload successfully", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();//关闭进度条对话框
                    }
                    break;
                case 1:
                    Toast.makeText(getActivity(), "not login yet", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();//关闭进度条对话框
                    break;
            }
        }
    };


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        fa = getActivity();
        imageView = root.findViewById(R.id.local_img);
        setBtn = root.findViewById(R.id.local_set_img);
        deleteBtn = root.findViewById(R.id.local_delete_img);
        local_upload_img = root.findViewById(R.id.local_upload_img);
        SetOnClick();
        EasyPhotos.createAlbum(this, true, GlideEngine.getInstance())
                .setFileProviderAuthority("cs.hku.wallpaper.ui.dashboard.DashboardFragment")
                .setCount(1)//参数说明：最大可选数，默认1
                .start(new SelectCallback() {
                    @Override
                    public void onResult(ArrayList<Photo> photos, boolean isOriginal) {
                        Log.d("big bow:", String.valueOf(photos.size()));
                        Photo current_photo = photos.get(0);
                        InitMainScreen(current_photo);
                    }
                });
        return root;
    }

    public void InitMainScreen(Photo photo){
        String path = photo.path;
        currentFile = new File(path);
        if(currentFile.exists()){
            currentBit  = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(currentBit);
        }
    }

    public void SetOnClick(){
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentBit == null) {
                    Toast.makeText(getContext(), "No image choose", Toast.LENGTH_SHORT).show();
                    return;
                }
                WallpaperManager manager = WallpaperManager.getInstance(getContext());
                try {
                    manager.setBitmap(currentBit);
                    Toast.makeText(getContext(), "set wall paper successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        local_upload_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //动态加载布局生成View对象
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                View longinDialogView = layoutInflater.inflate(R.layout.activity_upload, null);

                //获取布局中的控件
                upload_img_name = (EditText)longinDialogView.findViewById(R.id.upload_img_name);
                upload_img_class = (EditText)longinDialogView.findViewById(R.id.upload_img_class);

                //创建一个AlertDialog对话框
                AlertDialog longinDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Upload")
                        .setView(longinDialogView)                //加载自定义的对话框式样
                        .setPositiveButton(R.string.sure_upload, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showSmsCopyDialog();
                            }
                        })
                        .setNeutralButton(R.string.cancel_upload, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                longinDialog.show();
            }
        });
    }

    /**
     * 显示一个带进度条的对话框
     */
    private void showSmsCopyDialog() {


        progressDialog = new ProgressDialog(getActivity()); //注意：这里的上下文必须是this，而不能用getApplicationContext()
        progressDialog.setIcon(R.mipmap.ic_launcher); //设置对话框的图标
        progressDialog.setTitle("Uploading...");    //设置对话框标题
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); //指定进度条的样式为水平
        progressDialog.show();

        //开启一个线程
        new Thread(){
            @Override
            public void run() {
                int uid = Util.getUid(getActivity());
                if (uid == -1) {
                    handler.sendEmptyMessage(1);
                    return;
                }
                ViseHttp.UPLOAD("/image/upload", new UCallback() {
                    @Override
                    public void onProgress(long currentLength, long totalLength, float percent) {
                        Log.d(TAG, "onProgress: "+currentLength +" : " + totalLength);
                        MAXVALUE = (int) totalLength;
                        progressDialog.setMax(MAXVALUE);        //设置进度条的最大值
                        currentProgress = (int) currentLength;
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {

                    }
                })
                        .addImageFile("img", currentFile)
                        .addParam("imageName", upload_img_name.getText().toString())
                        .addParam("imageClass", upload_img_class.getText().toString())
                        .addParam("uid", String.valueOf(uid))
                        .request(new ACallback<Object>() {
                            @Override
                            public void onSuccess(Object data) {
                                //请求成功
                            }

                            @Override
                            public void onFail(int errCode, String errMsg) {
                                //请求失败，errCode为错误码，errMsg为错误描述
                            }
                        });
            }
        }.start();
    }

}
