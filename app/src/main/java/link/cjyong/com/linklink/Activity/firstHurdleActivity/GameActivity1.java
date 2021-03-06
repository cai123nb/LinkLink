package link.cjyong.com.linklink.Activity.firstHurdleActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.util.Timer;
import java.util.TimerTask;

import link.cjyong.com.linklink.Activity.mainActivity.MainActivity;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.board.GameService;
import link.cjyong.com.linklink.board.impl.GameServiceImpl;
import link.cjyong.com.linklink.element.GameConf;
import link.cjyong.com.linklink.element.LinkInfo;
import link.cjyong.com.linklink.service.BackgroundMusicService;
import link.cjyong.com.linklink.util.MyApplication;
import link.cjyong.com.linklink.view.GameView;
import link.cjyong.com.linklink.view.Piece;

/**
 * Created by cjyong on 2017/3/23.
 * 第一大关的游戏执行关卡Activity类
 */

public class GameActivity1 extends Activity
{
    //道具按钮和暂停按钮
    private Button pauseBtn,helpBtn,timeBtn,resetBtn,boomBtn;
    //方块放置的View
    private GameView gameView;
    //给个道具的使用次数限制
    private int timeTool,haveTimeTool;
    private int helpTool,haveHelpTool;
    private int boomTool,haveBombTool;
    private int resetTool,haveResetTool;
    //游戏的配置
    private GameConf config;
    //游戏的要求和时间显示
    private TextView timeLeft,taskRequirements;
    //游戏中gameService对象
    private GameService gameService;
    //定时器
    private Timer timer = new Timer();
    //游戏中动态时间显示
    private int time;
    //用户选中的时间
    private Piece selected = null;
    //用户是否在游戏
    private boolean isPlaying;
    //访问游戏内部数据的接口
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    // 失败后弹出的对话框
    private AlertDialog.Builder lostDialog;
    // 游戏胜利后的对话框
    private AlertDialog.Builder successDialog;
    //设置窗口
    private AlertDialog settingAD;
    private ImageButton soundEffect;
    private ImageButton soundBack;


    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    timeLeft.setText(""+time);
                    time--;
                    //时间小于0，游戏失败
                    if (time < 0) {
                        stopTimer();
                        isPlaying = false;
                        lostDialog.show();
                        return;
                    }
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置横屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.game_main);

        //初始化游戏界面
        init();

        //开始游戏
        startGame(config.getGameTime());
    }

    /**
     * 对游戏进行初始化操作
     */
    private void init()
    {
        //获取传递过来的参数
        config =  getIntent().getParcelableExtra("gameConf");

        //绑定组件
        gameView = (GameView) findViewById(R.id.gameView);
        timeLeft = (TextView) findViewById(R.id.timeLeft);
        gameService = new GameServiceImpl(this.config);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        taskRequirements = (TextView) findViewById(R.id.taskRequirements);
        helpBtn = (Button) findViewById(R.id.helpTool);
        timeBtn = (Button) findViewById(R.id.addTimeTool);
        resetBtn = (Button) findViewById(R.id.resetTool);
        boomBtn = (Button) findViewById(R.id.boomTool);

        //设置组件信息
        taskRequirements.setText(config.getTaskRequirements());

        //获取游戏接口
        preferences = getSharedPreferences("gameData",0);
        editor = preferences.edit();

        //设置道具使用次数
        haveBombTool = preferences.getInt("bombTool",0);
        haveTimeTool = preferences.getInt("timeTool",0);
        haveHelpTool = preferences.getInt("helpTool",0);
        haveResetTool = preferences.getInt("resetTool",0);

        timeTool = config.getTimeTool()<haveTimeTool?config.getTimeTool():haveTimeTool;
        helpTool = config.getHelpTool()<haveHelpTool?config.getHelpTool():haveHelpTool;
        boomTool = config.getBoomTool()<haveBombTool?config.getBoomTool():haveBombTool;
        resetTool = config.getResetTool()<haveResetTool?config.getResetTool():haveResetTool;
        updateTool();



        // 初始化游戏失败的对话框
        lostDialog = createDialog("Lost", "游戏失败！ 重新开始", R.drawable.lost)
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                startGame(config.getGameTime());
                            }
                        }
                )
                .setNegativeButton("取消",new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Intent  intent = new Intent(GameActivity1.this, FirstHurdleActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                );
        // 初始化游戏胜利的对话框
        successDialog = createDialog("Success", "游戏胜利！ 重新开始",
                R.drawable.success).setPositiveButton("再来一盘",
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        startGame(config.getGameTime());
                    }
                })
                .setNegativeButton("返回",new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(GameActivity1.this,FirstHurdleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

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
                    Intent intent = new Intent(GameActivity1.this,BackgroundMusicService.class);
                    intent.putExtra("control",3);
                    startService(intent);
                }
                else
                {
                    //没有播放
                    MyApplication.setBackIsOn(1);
                    soundBack.setImageResource(R.drawable.soundback1);
                    Intent intent = new Intent(GameActivity1.this,BackgroundMusicService.class);
                    intent.putExtra("control",1);
                    startService(intent);
                }
            }
        });
        settingAD = new AlertDialog.Builder(this)
                .setTitle("设置")
                .setView(settingLayout)
                .setPositiveButton("确定",null)
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //切换背景音乐
                        /*
                        Intent tintent = new Intent(GameActivity1.this,BackgroundMusicService.class);
                        tintent.putExtra("control",2);
                        startService(tintent);
                        */
                        Intent intent = new Intent(GameActivity1.this,FirstHurdleActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .create();



        //绑定监听器
        this.gameView.setOnTouchListener(new View.OnTouchListener() {
                                             public boolean onTouch(View view, MotionEvent e) {
                                                 if (!isPlaying) {
                                                     return false;
                                                 }
                                                 if (e.getAction() == MotionEvent.ACTION_DOWN) {
                                                     gameViewTouchDown(e);
                                                 }
                                                 if (e.getAction() == MotionEvent.ACTION_UP) {
                                                     gameViewTouchUp(e);
                                                 }
                                                 return true;
                                             }
                                         }
        );
        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这里应该添加暂停的选项
                settingAD.show();
            }
        });
        //提示卡使用
        helpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Piece[] tmp1 =  gameService.getUsefulPieces();
                gameView.setSelectedPiece(tmp1[0]);
                gameView.setHelpedPiece(tmp1[1]);
                helpTool--;
                haveHelpTool--;
                updateTool();
                gameView.postInvalidate();
            }
        });

        //加时卡使用
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time+=15;
                timeTool--;
                haveTimeTool--;
                updateTool();
            }
        });

        //爆炸卡使用
        boomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getSoundPool().play(MyApplication.getSoundID().get(2),MyApplication.getSoundVolumn(), MyApplication.getSoundVolumn(), 0, 0, 1);
                gameService.clearOneKindPiece();
                boomTool--;
                haveBombTool--;
                updateTool();
                if (!gameService.hasPieces()) {
                    //游戏胜利
                    success(time);
                    stopTimer();
                    gameView.setLinkInfo(null);
                    isPlaying = false;
                }
                gameView.postInvalidate();
            }
        });

        //重置卡的使用
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gameService.resetting();
                resetTool--;
                haveResetTool--;
                updateTool();
                gameView.postInvalidate();
            }
        });

        //为GameView绑定Service
        gameView.setGameService(gameService);


    }

    @Override
    protected void onPause() {
        //暂停游戏
        super.onPause();
        stopTimer();

    }

    @Override
    protected  void onResume()
    {
        // 如果处于游戏状态中
        if (isPlaying)
        {
            // 以剩余时间重写开始游戏
            startGame(time);
        }
        super.onResume();
    }


    /**
     * 点击GameView的响应事件,进行消除等操作
     * @param event
     */
    private void gameViewTouchDown(MotionEvent event) {
        //获取GameServiceImpl中Piece[][]数组
        Piece[][] pieces = gameService.getPieces();
        //获取用户点击的X坐标和Y坐标
        float touchX = event.getX();
        float touchY = event.getY();
        Piece currentPiece = gameService.findPiece(touchX, touchY);
        if (currentPiece == null)
            return;
        this.gameView.setSelectedPiece(currentPiece);
        //第一个被选中
        if (this.selected == null) {
            this.selected = currentPiece;
            this.gameView.postInvalidate();
            return;
        }

        //第二个被选中
        if (this.selected != null) {
            LinkInfo linkInfo = this.gameService.link(this.selected, currentPiece);
            if (linkInfo == null) {
                this.selected = currentPiece;
                this.gameView.postInvalidate();
            } else {
                //连接成功
                handleSuccessLink(linkInfo, this.selected, currentPiece, pieces);
            }
        }
    }

    /**
     * 当用户手指离开GameView时响应函数
     * @param event
     */
    private void gameViewTouchUp(MotionEvent event) {
        this.gameView.postInvalidate();
    }

    /**
     * 处理连接成功的方块时的函数
     * @param linkInfo
     * @param prePiece
     * @param currentPiece
     * @param pieces
     */
    private void handleSuccessLink(LinkInfo linkInfo, Piece prePiece, Piece currentPiece, Piece[][] pieces) {
        MyApplication.getSoundPool().play(MyApplication.getSoundID().get(1),MyApplication.getSoundVolumn(), MyApplication.getSoundVolumn(), 0, 0, 1);
        this.gameView.setLinkInfo(linkInfo);
        this.gameView.setSelectedPiece(null);
        this.gameView.setHelpedPiece(null);
        this.gameView.postInvalidate();
        pieces[prePiece.getIndexX()][prePiece.getIndexY()] = null;
        pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;
        this.selected = null;
        if (!this.gameService.hasPieces()) {
            //游戏胜利
            success(time);
            stopTimer();
            this.gameView.setLinkInfo(null);
            isPlaying = false;
        }
        else {
            if(!this.gameService.haveUsefulLinked()) {
                Toast.makeText(this, "没有可以连接的图标,重置中", Toast.LENGTH_SHORT).show();
                this.gameService.resetting();
            }
            this.gameView.postInvalidate();
        }
    }

    /**
     * 暂停游戏
     */
    private void stopTimer() {
        this.timer.cancel();
        this.timer = null;
    }

    /**
     * 开始游戏操作或者暂停后重新开始游戏
     * @param gameTime
     */
    private void startGame(int gameTime) {
        //如果之前timer还未取消，取消timer
        if (this.timer != null) {
            stopTimer();
        }
        this.time = gameTime;
        if (time == config.getGameTime()) {
            gameView.startGame();
        }
        isPlaying = true;
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 1000);
        this.selected = null;
    }


    /**
     * 游戏成功后,进行的相关操作
     * @param leftTime
     */
    private void success(int leftTime)
    {
        //评分标准,后期应进行不同标准的匹配
        int starScore = 1;
        if(leftTime >15)
        {
            starScore = 3;
        }
        else if(leftTime >10)
        {
            starScore = 2;
        }

        //获取用户的基础数据
        int gold = preferences.getInt("gold",0);
        int diamond = preferences.getInt("diamond",0);

        int currentScore = preferences.getInt(config.getBarrierID(),-1);
        //获取下一关的分数
        int barrier = Integer.parseInt(config.getBarrierID());
        if (barrier % 10 >= 6) {
            barrier = barrier + 10 - 5;
        } else {
            barrier += 1;
        }
        String nextBarrier = barrier+"";
        int nextBarrierScore = preferences.getInt(nextBarrier,-1);

        if(starScore>currentScore)
        {
            //更新分数
            editor.putInt(config.getBarrierID(),starScore);
        }
        if(nextBarrierScore<=-1)
        {
            //下一关未开启,解锁
            editor.putInt(nextBarrier,0);
        }

        //设置奖励
        //简单奖励系统
        if(starScore<=1)
        {
            gold+=100;
            diamond+=1;
        }
        else if(starScore<=2)
        {
            gold+=200;
            diamond+=2;
        }
        else
        {
            gold+=300;
            diamond+=3;
        }
        editor.putInt("gold",gold);
        editor.putInt("diamond",diamond);
        editor.commit();
        this.successDialog.show();
    }

    /**
     * 自定义的窗口创建模式.后期可以使用不同的窗口库进行重写
     * @param title
     * @param message
     * @param imageResource
     * @return
     */
    private AlertDialog.Builder createDialog(String title, String message,
                                             int imageResource)
    {
        return new AlertDialog.Builder(this).setTitle(title)
                .setMessage(message).setIcon(imageResource).setCancelable(false);
    }

    /**
     * 更新道具显示
     */
    private void updateTool()
    {
        if(timeTool<=0) {
            timeBtn.setEnabled(false);
            timeBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            timeBtn.setText("加时卡x"+timeTool);
        }
        if(helpTool<=0){
            helpBtn.setEnabled(false);
            helpBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            helpBtn.setText("提示卡x"+helpTool);
        }
        if(boomTool<=0)
        {
            boomBtn.setEnabled(false);
            boomBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            boomBtn.setText("爆炸卡x"+boomTool);
        }
        if(resetTool<=0)
        {
            resetBtn.setEnabled(false);
            resetBtn.setVisibility(View.INVISIBLE);
        }
        else
        {
            resetBtn.setText("重置卡x"+resetTool);
        }
        //更新数据到内部
        editor.putInt("timeTool",haveTimeTool);
        editor.putInt("resetTool",haveResetTool);
        editor.putInt("bombTool",haveBombTool);
        editor.putInt("helpTool",haveHelpTool);
        editor.commit();

    }

}
