package link.cjyong.com.linklink.Activity.startActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import link.cjyong.com.linklink.Activity.mainActivity.MainActivity;
import link.cjyong.com.linklink.Activity.registerActivity.RegisterActivity;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.service.BackgroundMusicService;

/**
 * Created by cjyong on 2017/4/7.
 * 开场动画Activity类,用于播放开场动画
 */

public class StartAnimationActivity extends Activity
{
    private ImageView iv;   //用于播放动画的ImageView
    private AnimationDrawable ad;   //ImageView的内部动态壁纸
    private Animation am;   //动画
    private SharedPreferences preferences;  //访问本地存储的接口
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置横屏和无边框
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.start_main);
        //初始化界面
        init();
        //播放动画
        am.start();
    }

    /**
     * 对界面进行初始化操作
     */
    private void init()
    {
        //获取动画
        iv = (ImageView) findViewById(R.id.image);
        ad = (AnimationDrawable) iv.getBackground();
        am = AnimationUtils.loadAnimation(this, R.anim.alphaanimation);
        //绑定动画
        iv.setAnimation(am);
        //设置动画监听器
        am.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ad.start();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //判断用户是否第一次登陆游戏
                preferences = getSharedPreferences("gameData",0);
                String username = preferences.getString("username",null);
                if(username==null) {
                    Intent intent = new Intent(StartAnimationActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(StartAnimationActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //启动背景音乐Service
        Intent intent = new Intent(this,BackgroundMusicService.class);
        startService(intent);

    }


}
