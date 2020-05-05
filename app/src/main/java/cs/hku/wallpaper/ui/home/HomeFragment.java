package cs.hku.wallpaper.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.ArrayList;
import java.util.List;

import cs.hku.wallpaper.BoardActivity;
import cs.hku.wallpaper.Image.NetImageActivity;
import cs.hku.wallpaper.LoginActivity;
import cs.hku.wallpaper.R;
import cs.hku.wallpaper.model.ClassResp;
import cs.hku.wallpaper.model.UserResp;

import static cs.hku.wallpaper.utils.Util.getUid;


public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    ListView listView;
    ArrayList<String> data;
    View root;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0:
                    InitListView(root);
                    SetOnClick();
                    break;
            }
        }
    };
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
            }
        });
        InitData();
        return root;
    }
    public void InitData() {
        ViseHttp.POST("/class/fetch")
                .addParam("uid", String.valueOf(getUid(getActivity())))
                .request(new ACallback<ClassResp>() {
                    @Override
                    public void onSuccess(ClassResp resp) {
                        int status = resp.getStatus();
                        List<String> classify = resp.getClassify();
                        String message = resp.getMessage();
                        if (status == 0) {
                            Toast.makeText(getActivity(), "fetch classify failed, " + message, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        data = new ArrayList<String>();
                        data.addAll(classify);
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        Toast.makeText(getActivity(), "Fetch classify failed, " + errMsg + "; code = " + errCode, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void InitListView(View root){
        listView = root.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireActivity(), android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
    }

    public void SetOnClick(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String classify = data.get(position);
                Log.d("Big bow", data.get(position));
                Intent intent = new Intent(getActivity(), NetImageActivity.class);
                intent.putExtra("classify", classify);
                startActivity(intent);
            }
        });
    }
}
