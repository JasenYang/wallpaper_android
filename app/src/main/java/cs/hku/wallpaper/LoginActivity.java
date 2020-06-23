package cs.hku.wallpaper;


import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;


import cs.hku.wallpaper.main.Drawer;
import cs.hku.wallpaper.model.UserResp;
import cs.hku.wallpaper.utils.Util;
import okhttp3.MediaType;


public class LoginActivity extends Activity {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    Button loginBtn;
    EditText login_username;
    EditText login_password;
    TextView to_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Util.InitNetWork(this);
        InitView();
        SetOnClick();
        if (Util.getUid(this) != -1) {
            Intent intent = new Intent(LoginActivity.this, BoardActivity.class);
            startActivity(intent);
        }
    }

    public void InitView(){
        login_username = findViewById(R.id.login_username);
        login_password = findViewById(R.id.login_password);
        to_register = findViewById(R.id.to_register);
        loginBtn = findViewById(R.id.loginBtn);
    }

    public void SetOnClick(){
        to_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 暂时打开开关
                Intent intent = new Intent(LoginActivity.this, Drawer.class);
                startActivity(intent);
                //todo 上面两行代码需要删除

                String username = login_username.getText().toString();
                String password = login_password.getText().toString();

                ViseHttp.POST("/user/login")
                        .addParam("name", username)
                        .addParam("password", password)
                        .request(new ACallback<UserResp>() {
                            @Override
                            public void onSuccess(UserResp data) {
                                int status = 0;
                                int uid = -1;
                                String message = "";
                                status = data.getStatus();
                                uid = data.getUid();
                                message = data.getMessage();
                                if (status == 0) {
                                    Toast.makeText(getApplicationContext(), "Login failed, " + message, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SharedPreferences sharedPref = getSharedPreferences("wallpaper_user", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("wallpaper_uid", uid);
                                editor.apply();
                                editor.commit();
                                Intent intent = new Intent(LoginActivity.this, BoardActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail(int errCode, String errMsg) {
                                Toast.makeText(getApplicationContext(), "Request failed, " + errMsg + "; code = " + errCode, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

}

