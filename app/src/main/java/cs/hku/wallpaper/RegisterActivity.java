package cs.hku.wallpaper;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import cs.hku.wallpaper.model.UserResp;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class RegisterActivity extends Activity {

    EditText register_username;
    EditText register_password;
    Button registerBtn;
    TextView toLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InitView();
        SetOnClick();
    }

    public void InitView(){
        register_username = findViewById(R.id.register_username);
        register_password = findViewById(R.id.register_password);
        registerBtn = findViewById(R.id.registerBtn);
        toLogin = findViewById(R.id.to_login);
    }

    public void SetOnClick(){
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = register_username.getText().toString();
                String password = register_password.getText().toString();
                ViseHttp.POST("/user/register")
                        .addParam("name", username)
                        .addParam("password", password)
                        .request(new ACallback<UserResp>() {
                            @Override
                            public void onSuccess(UserResp data) {
                                Log.d(TAG, "onSuccess: " + data);
                                int status = 0;
                                int uid = -1;
                                String message = "";

                                status = data.getStatus();
                                uid = data.getUid();
                                message = data.getMessage();


                                if (status == 0) {
                                    Toast.makeText(getApplicationContext(), "Register failed, " + message , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                SharedPreferences sharedPref = getSharedPreferences("wallpaper_user", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("wallpaper_uid", uid);
                                editor.apply();
                                editor.commit();
                                Intent intent = new Intent(RegisterActivity.this, BoardActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onFail(int errCode, String errMsg) {
                                Toast.makeText(getApplicationContext(), "Register failed, " + errMsg + "; code = " + errCode, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
