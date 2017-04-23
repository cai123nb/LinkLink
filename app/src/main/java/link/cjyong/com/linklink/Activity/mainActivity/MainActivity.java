package link.cjyong.com.linklink.Activity.mainActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import link.cjyong.com.linklink.Activity.shopActivity.ShopActivity;
import link.cjyong.com.linklink.Activity.thirdHurdleActivity.ThirdHurdleActivity;
import link.cjyong.com.linklink.Activity.registerActivity.RegisterActivity;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.Activity.firstHurdleActivity.FirstHurdleActivity;
import link.cjyong.com.linklink.Activity.secondHurdleActivity.SecondHurdleActivity;
import link.cjyong.com.linklink.service.BackgroundMusicService;
import link.cjyong.com.linklink.service.SendMailThread;
import link.cjyong.com.linklink.util.MyApplication;
import link.cjyong.com.linklink.view.BackpackNode;

/**
 * Created by cjyong on 2017/3/20.
 * 游戏主画面Activity类
 */

public class MainActivity extends AppCompatActivity
{
    //访问游戏内部数据存储接口
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    //反馈输入窗口
    private EditText et;
    //窗口的布局
    private LinearLayout ll;
    //相关的按钮
    private Button btnBag,btnShop,btnAddDiamond,btnAddGold,btnExit,btnSetting,btnAuthor,btnFeedback,btnResetUser;
    //金币,砖石,用户名显示
    private TextView tvDiamond,tvGold,tvUsername;
    //反馈窗口
    private AlertDialog fedbackAD;
    //鸣谢窗口
    private AlertDialog thanksAD;
    //设置窗口
    private AlertDialog settingAD;
    private ImageButton soundEffect;
    private ImageButton soundBack;
    //退出窗口
    private AlertDialog exitAD;

    //道具的数量
    private int timeNum;
    private int boomNum;
    private int resetNum;
    private int helpNum;
    //背包弹窗
    private PopupWindow ppWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置横屏
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        //游戏初始化操作
        init();
    }

    /**
     * 对界面进行初始化操作
     */
    private void init()
    {
        //绑定组件
        btnBag = (Button) findViewById(R.id.bagBtn);
        btnShop = (Button) findViewById(R.id.shopBtn);
        btnAddDiamond = (Button) findViewById(R.id.addDiamond);
        btnAddGold = (Button) findViewById(R.id.addGold);
        btnExit = (Button) findViewById(R.id.exitBtn);
        btnSetting = (Button) findViewById(R.id.settingBtn);
        btnAuthor = (Button) findViewById(R.id.authorBtn);
        btnFeedback = (Button) findViewById(R.id.feedback);
        btnResetUser = (Button) findViewById(R.id.resetUser);
        tvDiamond = (TextView) findViewById(R.id.diamondTv);
        tvGold = (TextView) findViewById(R.id.goldTv);
        tvUsername = (TextView) findViewById(R.id.username);
        ll = (LinearLayout) findViewById(R.id.hsvll);

        //获取用户基本信息
        preferences = getSharedPreferences("gameData",0);
        editor = preferences.edit();
        int gold = preferences.getInt("gold",0);
        int diamond = preferences.getInt("diamond",0);
        String username = preferences.getString("username",null);
        tvUsername.setText(username);
        tvGold.setText(tvGold.getText()+" "+gold);
        tvDiamond.setText(tvDiamond.getText()+" "+diamond);
        timeNum = preferences.getInt("timeTool",0);
        boomNum = preferences.getInt("bombTool",0);
        resetNum = preferences.getInt("resetTool",0);
        helpNum = preferences.getInt("helpTool",0);

        //初始化背包弹框
        View contentView = LayoutInflater.from(this).inflate(R.layout.backpack, null);
        ((BackpackNode) contentView.findViewById(R.id.boom)).setToolNums(boomNum);
        ((BackpackNode) contentView.findViewById(R.id.time)).setToolNums(timeNum);
        ((BackpackNode) contentView.findViewById(R.id.reset)).setToolNums(resetNum);
        ((BackpackNode) contentView.findViewById(R.id.help)).setToolNums(helpNum);
        ppWindow = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT,true);
        ppWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.backpack));



        //反馈功能的实现
        et = new EditText(this);
        fedbackAD = new AlertDialog.Builder(MainActivity.this)
                .setTitle("请输入你的建议或者问题")
                .setView(et)
                .setPositiveButton("提交", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(et.getText().toString().equals(""))
                        {
                            //输入为空
                            Toast.makeText(MainActivity.this,"你不能提交空的反馈哦!",Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //提交反馈
                            new SendMailThread(tvUsername.getText().toString(),et.getText().toString(), SendMailThread.MailType.feedbackMail).start();
                            Toast.makeText(MainActivity.this,"反馈已提交,谢谢你的支持!",Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("取消",null)
                .create();

        //鸣谢窗口的简单弹窗实现
        thanksAD = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Thanks")
                .setMessage("制作者: cjyong小组 \n 联系方式: 2686600303@qq.com\n")
                .setPositiveButton("确定",null)
                .setNegativeButton("取消",null)
                .create();

        //设置窗口的简单实现
        LayoutInflater inflater = getLayoutInflater();
        View settingLayout = inflater.inflate(R.layout.setting,null);
        soundEffect = (ImageButton) settingLayout.findViewById(R.id.soundEffect);
        soundBack = (ImageButton) settingLayout.findViewById(R.id.soundBack);
        //初始化两个Butoon
        if(MyApplication.getSoundVolumn()==0)
        {
            soundEffect.setImageResource(R.drawable.soundeffect2);
        }
        else
        {
            soundEffect.setImageResource(R.drawable.soundeffect1);
        }

        if(MyApplication.getBackIsOn()==1)
        {
            soundBack.setImageResource(R.drawable.soundback1);
        }
        else
        {
            soundBack.setImageResource(R.drawable.soundback2);
        }
        soundEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MyApplication.getSoundVolumn()==0)
                {
                    //此时没有音效
                    soundEffect.setImageResource(R.drawable.soundeffect1);
                    MyApplication.setSoundVolumn(1);
                }
                else
                {
                    //此时有音效
                    soundEffect.setImageResource(R.drawable.soundeffect2);
                    MyApplication.setSoundVolumn(0);
                }
            }
        });

        soundBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加背景音乐的处理
                if(MyApplication.getBackIsOn()==1)
                {
                    //正在播放
                    MyApplication.setBackIsOn(0);
                    soundBack.setImageResource(R.drawable.soundback2);
                    //发送暂停信息
                    Intent intent = new Intent(MainActivity.this,BackgroundMusicService.class);
                    intent.putExtra("control",3);
                    startService(intent);
                }
                else
                {
                    //没有播放
                    MyApplication.setBackIsOn(1);
                    soundBack.setImageResource(R.drawable.soundback1);
                    Intent intent = new Intent(MainActivity.this,BackgroundMusicService.class);
                    intent.putExtra("control",1);
                    startService(intent);
                }
            }
        });
        settingAD = new AlertDialog.Builder(this)
                .setTitle("设置")
                .setView(settingLayout)
                .setPositiveButton("确定",null)
                .setNegativeButton("取消",null)
                .create();



        //设置背包监听器
        btnBag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加启动背包的活动
                ppWindow.showAsDropDown(v);
            }
        });

        //设置商店监听器
        btnShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加启动商店的活动
                Intent intent = new Intent(MainActivity.this,ShopActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //设置添加钻石监听器
        /*
        btnAddDiamond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加启动添加钻石的活动
                Intent intent = new Intent(MainActivity.this,ShopActivity.class);
                startActivity(intent);
                finish();
            }
        });
        */
        btnAddDiamond.setVisibility(View.INVISIBLE);

        //设置添加金币监听器
        btnAddGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加启动添加金币的活动
                Intent intent = new Intent(MainActivity.this,ShopActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //设置退出监听器
        exitAD = new AlertDialog.Builder(MainActivity.this)
            .setTitle("退出程序")
            .setMessage("您确认要退出吗?")
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which)
                {
                    //停止背景音乐Service运行
                    Intent intent = new Intent(MainActivity.this,BackgroundMusicService.class);
                    stopService(intent);
                    //结束游戏
                    finish();
                    System.exit(0);
                }
            })
            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which)
                { return;}
            }).create();


        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加启动动退出的活动
                exitAD.show();
            }
        });

        //设置反馈监听器
        btnFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加启动动退出的活动
                fedbackAD.show();
            }
        });

        //设置设置按钮监听器
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里添加启动设置的活动
                settingAD.show();
            }
        });

        //设置鸣谢监听器
        btnAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thanksAD.show();
            }
        });

        //设置重置按钮监听器
        btnResetUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetConfirm();
            }
        });

        //添加关卡
        addHurdles();

    }

    /**
     * 添加不同的游戏大关卡
     */
    void addHurdles()
    {
        for(int i=0;i<10;i++)
        {
            if(i==0)
            {
                //第一个大关卡
                Button btn1 = new Button(this);
                btn1.setText("第"+(i+1)+"章节");
                btn1.setHeight(800);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //启动第一关关卡
                        Intent intent = new Intent(MainActivity.this
                                , FirstHurdleActivity.class);
                        startActivity(intent);
                        // 结束该Activity
                        finish();
                    }
                });
                ll.addView(btn1);
            }
            else if(i==1)
            {
                //添加第二个大关卡
                Button btn1 = new Button(this);
                btn1.setText("第"+(i+1)+"章节");
                btn1.setHeight(800);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //启动第一关关卡
                        Intent intent = new Intent(MainActivity.this
                                , SecondHurdleActivity.class);
                        startActivity(intent);
                        // 结束该Activity
                        finish();
                    }
                });
                ll.addView(btn1);
            }
            else if(i==2)
            {
                //添加第三大关
                Button btn1 = new Button(this);
                btn1.setText("第"+(i+1)+"章节");
                btn1.setHeight(800);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //启动第一关关卡
                        Intent intent = new Intent(MainActivity.this
                                , ThirdHurdleActivity.class);
                        startActivity(intent);
                        // 结束该Activity
                        finish();
                    }
                });
                ll.addView(btn1);
            }
            else
            {
                Button btn = new Button(this);
                btn.setText("第"+(i+1)+"章节");
                btn.setHeight(800);
                ll.addView(btn);
            }

        }
    }


    /**
     *用户重置窗口的弹窗确认
     */
    private void resetConfirm()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("重置游戏数据")
                .setMessage("您确认要全部重置吗?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        editor.clear();
                        editor.commit();
                        Toast.makeText(MainActivity.this,"重置成功,请重新注册",Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    { return;}
                }).create();
        alertDialog.show();
    }

}
