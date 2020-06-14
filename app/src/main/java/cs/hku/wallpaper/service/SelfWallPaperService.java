package cs.hku.wallpaper.service;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import cs.hku.wallpaper.R;


@SuppressLint("Registered")
public class SelfWallPaperService extends WallpaperService {

    @Override
    public Engine onCreateEngine() {
        return new VideoEngine();
    }

    class VideoEngine extends Engine {
        private MediaPlayer mp;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            setTouchEventsEnabled(true);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            //把视频输出到SurfaceHolder上面
            if (mp != null && mp.isPlaying())
                return;
            //可以设置SD卡的视频
            mp = MediaPlayer.create(getApplicationContext(), R.raw.video);
            mp.setSurface(holder.getSurface());
            //重复播放
            mp.setLooping(true);
            mp.start();
        }

        @Override
        public void onDestroy() {
            if (mp != null) {
                mp.stop();
                mp.release();
            }
            super.onDestroy();
        }

        @Override
        public void onTouchEvent(MotionEvent event) {
            float x1 = 0 ;
            float y1 = 0;
            float x2 = 0;
            float y2 = 0;
            //继承了Activity的onTouchEvent方法，直接监听点击事件
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                //当手指按下的时候
                x1 = event.getX();
                y1 = event.getY();
            }
            if(event.getAction() == MotionEvent.ACTION_UP) {
                //当手指离开的时候
                x2 = event.getX();
                y2 = event.getY();
                if(y1 - y2 > 50) {
                    //Slide up
//                    mp.pause();
//                    Toast.makeText(getApplicationContext(), "Slide up, stop wallpaper", Toast.LENGTH_SHORT).show();
                } else if(y2 - y1 > 50) {
                    //Slide down
//                    mp.start();
//                    Toast.makeText(getApplicationContext(), "Slide down, restart wallpaper", Toast.LENGTH_SHORT).show();
                } else if(x1 - x2 > 50) {
                    //Slide left
//                    mp.reset();
//                    Toast.makeText(getApplicationContext(), "Slide left, replay wallpaper", Toast.LENGTH_SHORT).show();
                } else if(x2 - x1 > 50) {
                    //Slide right
//                    Toast.makeText(getApplicationContext(), "Slide right, do what you like to do", Toast.LENGTH_SHORT).show();
                }
            }
            super.onTouchEvent(event);
        }
    }

}
