package link.cjyong.com.linklink.Activity.thirdHurdleActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;

import link.cjyong.com.linklink.Activity.firstHurdleActivity.FirstHurdleActivity;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.element.GameConf;
import link.cjyong.com.linklink.element.HomeListener;
import link.cjyong.com.linklink.service.BackgroundMusicService;
import link.cjyong.com.linklink.util.ImageUtil;
import link.cjyong.com.linklink.util.MyApplication;
import link.cjyong.com.linklink.view.Barrier;

/**
 * Created by cjyong on 2017/4/10.
 * 第三大关关卡选择Activity类
 */

public class ThirdHurdleActivity extends Activity
{
    //6个关卡选择
    private Barrier firstBarrier,secondBarrier,thirdBarrier,fourthBarrier,fifthBarrier,sixthBarrier;
    //6个关卡的星级表示
    private RatingBar firstRb,secondRb,thirdRb,fourthRb,fifthRb,sixthRb;
    //返回按钮
    private Button returnbtn;
    //游戏配置文件
    private GameConf gameConf;
    //访问游戏内部数据的接口
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置横屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.first_hurdle);
        //初始化游戏
        init();
    }

    /**
     * 对游戏进行初始化操作
     */
    protected void init()
    {
        //绑定组件
        firstBarrier = (Barrier) findViewById(R.id.first_hurdle);
        secondBarrier = (Barrier) findViewById(R.id.second_hurdle);
        thirdBarrier = (Barrier) findViewById(R.id.thirld_hurdle);
        fourthBarrier = (Barrier) findViewById(R.id.fourth_hurlde);
        fifthBarrier = (Barrier) findViewById(R.id.fifth_hurdle);
        sixthBarrier = (Barrier) findViewById(R.id.sixth_hurdle);
        returnbtn = (Button) findViewById(R.id.return_home);
        firstRb = (RatingBar) findViewById(R.id.first_rb);
        secondRb = (RatingBar) findViewById(R.id.second_rd);
        thirdRb = (RatingBar) findViewById(R.id.thirld_rd);
        fourthRb = (RatingBar) findViewById(R.id.fourth_rd);
        fifthRb = (RatingBar) findViewById(R.id.fifth_rd);
        sixthRb = (RatingBar) findViewById(R.id.sixth_rd);
        //将ImageUtil内部值恢复成原值
        ImageUtil.lessImage=1;

        //获取用户通关数据
        preferences = getSharedPreferences("gameData",0);
        editor = preferences.edit();
        int first = preferences.getInt("31",-1);
        int second = preferences.getInt("32",-1);
        int third = preferences.getInt("33",-1);
        /*
        int fourth = preferences.getInt("34",-1);
        int fifth  = preferences.getInt("35",-1);
        int sixth = preferences.getInt("36",-1);
        */
        //屏蔽后三关,没有实现
        int fourth = -1;
        int fifth = -1;
        int sixth = -1;
        //初始化关卡
        if(first>-1)
        {
            firstRb.setVisibility(View.VISIBLE);
            firstRb.setRating(first);
            secondBarrier.setEnabled(true);
        }
        else
        {
            firstBarrier.setLocked(true);
            firstBarrier.setEnabled(false);
            firstBarrier.postInvalidate();
        }

        if(second>-1)
        {
            secondRb.setVisibility(View.VISIBLE);
            secondRb.setRating(second);
            secondBarrier.setEnabled(true);
        }
        else
        {
            secondBarrier.setLocked(true);
            secondBarrier.setEnabled(false);
            secondBarrier.postInvalidate();
        }

        if(third>-1)
        {
            thirdRb.setVisibility(View.VISIBLE);
            thirdRb.setRating(third);
        }
        else
        {
            thirdBarrier.setLocked(true);
            thirdBarrier.setEnabled(false);
            thirdBarrier.postInvalidate();
        }
        if(fourth>-1)
        {
            fourthRb.setVisibility(View.VISIBLE);
            fourthRb.setRating(fourth);
            thirdBarrier.setEnabled(true);
        }
        else
        {
            fourthBarrier.setLocked(true);
            fourthBarrier.setEnabled(false);
            fourthBarrier.postInvalidate();
        }

        if(fifth>-1)
        {
            fifthRb.setVisibility(View.VISIBLE);
            fifthRb.setRating(fifth);
            fifthBarrier.setEnabled(true);
        }
        else
        {
            fifthBarrier.setLocked(true);
            fifthBarrier.setEnabled(false);
            fifthBarrier.postInvalidate();
        }

        if(sixth>-1)
        {
            sixthRb.setVisibility(View.VISIBLE);
            sixthRb.setRating(sixth);
            sixthBarrier.setEnabled(true);
        }
        else
        {
            sixthBarrier.setLocked(true);
            sixthBarrier.setEnabled(false);
            sixthBarrier.postInvalidate();
        }

        //绑定监听器
        firstBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加第一关
                Intent intent = new Intent(ThirdHurdleActivity.this,GameActivity31.class);
                //传入对应的参数
                gameConf = new GameConf("31",7, 7, 2, 10,5,"1星:5秒\n2星10步\n3星20秒",0,1,1,1,1);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gameConf",gameConf);
                intent.putExtras(bundle);
                startActivity(intent);
                // 结束该Activity
                finish();
            }
        });
        secondBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加第二关
                Intent intent = new Intent(ThirdHurdleActivity.this,GameActivity32.class);
                //传入对应的参数
                gameConf = new GameConf("32",7, 7, 10, 10,5,"1星:50步\n2星40步\n3星20步",0,0,2,0,2);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gameConf",gameConf);
                intent.putExtras(bundle);
                startActivity(intent);
                // 结束该Activity
                finish();
            }
        });
        thirdBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加第三关
                Intent intent = new Intent(ThirdHurdleActivity.this,GameActivity33.class);
                //传入对应的参数
                gameConf = new GameConf("33",7, 7, 10, 10,3,"1星:50步\n2星40步\n3星20步",0,0,2,0,2);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gameConf",gameConf);
                intent.putExtras(bundle);
                startActivity(intent);
                // 结束该Activity
                finish();
            }
        });

        returnbtn.setOnClickListener(new HomeListener(this));

        Intent intent = new Intent(ThirdHurdleActivity.this,BackgroundMusicService.class);
        if(MyApplication.getBackIsOn()==1)
        {
            intent.putExtra("control",1);
        }
        else
        {
            intent.putExtra("control",3);
        }
        startService(intent);

    }


}
