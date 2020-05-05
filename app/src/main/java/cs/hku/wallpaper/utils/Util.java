package cs.hku.wallpaper.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.vise.xsnow.http.ViseHttp;

import cs.hku.wallpaper.constants.Constants;

public class Util {
    public static int getUid(Activity activity){
        if (activity == null) {
            return -1;
        }
        SharedPreferences sharedPref = activity.getSharedPreferences("wallpaper_user", Context.MODE_PRIVATE);
        if (null == sharedPref) {
            return -1;
        }
        return sharedPref.getInt("wallpaper_uid", -1);
    }

    public static void InitNetWork(Context context){
        ViseHttp.init(context);
        ViseHttp.CONFIG()
                //配置请求主机地址
                .baseUrl(Constants.IP);
    }
}
