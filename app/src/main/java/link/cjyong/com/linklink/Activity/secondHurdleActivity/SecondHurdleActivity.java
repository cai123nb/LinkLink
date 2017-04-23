package link.cjyong.com.linklink.Activity.secondHurdleActivity;

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
import link.cjyong.com.linklink.util.MyApplication;
import link.cjyong.com.linklink.view.Barrier;

/**
 * Created by cjyong on 2017/4/3.
 * 第二大关游戏关卡Activity类
 */

public class SecondHurdleActivity extends Activity
{
    //关卡图标
    private Barrier firstBarrier,secondBarrier,thirdBarrier,fourthBarrier,fifthBarrier,sixthBarrier;
    //关卡的星级显示
    private RatingBar firstRb,secondRb,thirdRb,fourthRb,fifthRb,sixthRb;
    //返回按钮
    private Button returnbtn;
    //游戏的配置属性
    private GameConf gameConf;
    //修改和访问本地存储数据的接口
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
        //初始化界面
        init();
    }

    /**
     * 对界面进行初始化操作
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

        //获取用户通关数据
        preferences = getSharedPreferences("gameData",0);
        editor = preferences.edit();
        int first = preferences.getInt("21",-1);
        int second = preferences.getInt("22",-1);
        int third = preferences.getInt("23",-1);
        int fourth = preferences.getInt("24",-1);
        int fifth  = preferences.getInt("25",-1);
        int sixth = preferences.getInt("26",-1);
        if(first>-1)
        {
            //解锁
            firstRb.setVisibility(View.VISIBLE);
            firstRb.setRating(first);
        }
        else
        {
            firstBarrier.setLocked(true);
            firstBarrier.setEnabled(false);
            firstBarrier.postInvalidate();
        }

        if(second>-1)
        {
            //解锁
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
            //解锁
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
            //解锁
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
            //解锁
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
            //解锁
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
                //这里添加第一小关
                Intent intent = new Intent(SecondHurdleActivity.this,GameActivity2.class);
                //传入对应的参数
                gameConf = new GameConf("21",6, 7, 2, 10,40,"1星:20秒\n2星15秒\n3星8秒",0,1,1,1,1,1);
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
                //这里添加第二小关
                Intent intent = new Intent(SecondHurdleActivity.this,GameActivity2.class);
                //传入对应的参数
                gameConf = new GameConf("22",6, 7, 2, 10, 40,"1星:25秒\n2星15秒\n3星10秒",0,1,1,1,1,2);
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
                //这里添加第三小关
                Intent intent = new Intent(SecondHurdleActivity.this,GameActivity2.class);
                //传入对应的参数
                gameConf = new GameConf("23",7, 6, 2, 10, 40,"1星:20秒\n2星15秒\n3星10秒",0,1,1,1,1,3);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gameConf",gameConf);
                intent.putExtras(bundle);
                startActivity(intent);
                // 结束该Activity
                finish();
            }
        });
        fourthBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加第四小关
                Intent intent = new Intent(SecondHurdleActivity.this,GameActivity2.class);
                //传入对应的参数
                gameConf = new GameConf("24",7, 6, 2, 10, 40,"1星:20秒\n2星15秒\n3星10秒",0,1,1,1,1,4);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gameConf",gameConf);
                intent.putExtras(bundle);
                startActivity(intent);
                // 结束该Activity
                finish();
            }
        });
        fifthBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加第五小关
                Intent intent = new Intent(SecondHurdleActivity.this,GameActivity2.class);
                //传入对应的参数
                gameConf = new GameConf("25",7, 7, 2, 10, 40,"1星:40秒\n2星30秒\n3星25秒",0,1,1,1,1,5);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gameConf",gameConf);
                intent.putExtras(bundle);
                startActivity(intent);
                // 结束该Activity
                finish();
            }
        });
        sixthBarrier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加第六小关
                Intent intent = new Intent(SecondHurdleActivity.this,GameActivity2.class);
                //传入对应的参数
                gameConf = new GameConf("26",7, 7, 2, 10, 40,"1星:60秒\n2星50秒\n3星35秒",0,1,1,1,1,6);
                Bundle bundle = new Bundle();
                bundle.putParcelable("gameConf",gameConf);
                intent.putExtras(bundle);
                startActivity(intent);
                // 结束该Activity
                finish();
            }
        });

        returnbtn.setOnClickListener(new HomeListener(this));

        Intent intent = new Intent(SecondHurdleActivity.this,BackgroundMusicService.class);
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
