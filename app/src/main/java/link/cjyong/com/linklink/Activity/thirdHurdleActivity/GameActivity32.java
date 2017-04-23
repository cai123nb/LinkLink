package link.cjyong.com.linklink.Activity.thirdHurdleActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import link.cjyong.com.linklink.Activity.firstHurdleActivity.FirstHurdleActivity;
import link.cjyong.com.linklink.Activity.firstHurdleActivity.GameActivity1;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.board.GameService;
import link.cjyong.com.linklink.board.impl.GameServiceImpl;
import link.cjyong.com.linklink.element.GameConf;
import link.cjyong.com.linklink.element.LinkInfo;
import link.cjyong.com.linklink.service.BackgroundMusicService;
import link.cjyong.com.linklink.util.ImageUtil;
import link.cjyong.com.linklink.util.MyApplication;
import link.cjyong.com.linklink.view.GameView;
import link.cjyong.com.linklink.view.Piece;
import link.cjyong.com.linklink.view.PieceImage;

/**
 * Created by cjyong on 2017/4/10.
 * 第三大关第二小关游戏Activity类
 */

public class GameActivity32 extends Activity
{
    //道具按钮
    private Button pauseBtn,helpBtn,timeBtn,resetBtn,boomBtn;
    //给个道具的使用次数限制
    private int timeTool,haveTimeTool;
    private int helpTool,haveHelpTool;
    private int boomTool,haveBombTool;
    private int resetTool,haveResetTool;
    //需要完成的任务图片
    private ImageView taskImage;
    //游戏的主画板
    private GameView gameView;
    //游戏的配置文件
    private GameConf config;
    //剩余的次数要求和任务需求
    private TextView timesLeft,taskRequirements;
    //游戏的Service类
    private GameService gameService;
    //剩余的个数
    private int step;
    //使用的步数
    private int useStep;
    //用户点击Piece
    private Piece selected = null;
    //是否在暂停游戏
    private boolean isPlaying;
    //任务需要消除的图片
    private PieceImage taskPieceImage;
    //访问游戏内部的数据的接口
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    // 游戏胜利后的对话框
    private AlertDialog.Builder successDialog;
    //设置窗口
    private AlertDialog settingAD;
    private ImageButton soundEffect;
    private ImageButton soundBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置横屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.game_main32);
        //设置ImageUtil获取图片的数量为少量
        ImageUtil.lessImage = 3;
        //初始化游戏
        init();
        //开始游戏
        startGame(config.getGameTime());
    }

    /**
     * 对游戏进行初始化操作
     */
    private void init()
    {
        //绑定组件
        config =  getIntent().getParcelableExtra("gameConf");
        gameView = (GameView) findViewById(R.id.gameView);
        timesLeft = (TextView) findViewById(R.id.timesLeft);
        timesLeft.setText(config.getGameTime()+"");
        taskImage = (ImageView) findViewById(R.id.taskImage);
        taskPieceImage = ImageUtil.getRandomPieceImage();
        taskImage.setImageBitmap(taskPieceImage.getImage());
        gameService = new GameServiceImpl(this.config);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        taskRequirements = (TextView) findViewById(R.id.taskRequirements);
        helpBtn = (Button) findViewById(R.id.helpTool);
        resetBtn = (Button) findViewById(R.id.resetTool);
        timeBtn = (Button) findViewById(R.id.addTimeTool);
        boomBtn = (Button) findViewById(R.id.boomTool);

        useStep = 0;

        //设置任务要求
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
                        Intent  intent = new Intent(GameActivity32.this,ThirdHurdleActivity.class);
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
                    Intent intent = new Intent(GameActivity32.this,BackgroundMusicService.class);
                    intent.putExtra("control",3);
                    startService(intent);
                }
                else
                {
                    //没有播放
                    MyApplication.setBackIsOn(1);
                    soundBack.setImageResource(R.drawable.soundback1);
                    Intent intent = new Intent(GameActivity32.this,BackgroundMusicService.class);
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
                        Intent tintent = new Intent(GameActivity32.this,BackgroundMusicService.class);
                        tintent.putExtra("control",2);
                        startService(tintent);
                        */
                        Intent intent = new Intent(GameActivity32.this,ThirdHurdleActivity.class);
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
            }
        });

        //爆炸卡使用
        boomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        //播放背景音乐

    }

    @Override
    protected  void onResume()
    {
        // 如果处于游戏状态中
        if (isPlaying)
        {
            // 以剩余时间重写开始游戏
            startGame(step);
        }
        super.onResume();
    }


    /**
     * 点击GameView的响应事件,进行消除等操作
     * @param event
     */
    private void gameViewTouchDown(MotionEvent event) {
        //获取GameSERVICEImpl中Piece[][]数组
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
        useStep+=1;
        //判断是否为任务需求的PieceImage
        if(taskPieceImage.isSameImage(prePiece.getImage())) {
            step--;
            timesLeft.setText(step+"");
        }
        this.gameView.setLinkInfo(linkInfo);
        this.gameView.setSelectedPiece(null);
        this.gameView.setHelpedPiece(null);
        this.gameView.postInvalidate();
        //进行移位操作,这里只实现下降填充操作
        this.gameService.downMovePiecesAndFilling(prePiece,currentPiece);
        this.selected = null;
        if (step<=0) {
            //游戏胜利
            success(useStep);
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
     * 开始游戏操作或者暂停后重新开始游戏
     * @param step
     */
    private void startGame(int step) {
        //如果之前timer还未取消，取消timer
        this.step = step;
        if (step == config.getGameTime()) {
            gameView.startGame();
            timesLeft.setText(step+"");
        }
        isPlaying = true;
        this.selected = null;
    }




    /**
     * 游戏成功后,进行的相关操作
     * @param useStep
     */
    private void success(int useStep)
    {
        //评分标准,后期应进行不同标准的匹配
        int starScore = 1;
        if(useStep < 10)
        {
            starScore = 3;
        }
        else if(useStep <25)
        {
            starScore = 2;
        }

        //获取用户的基础数据
        int gold = preferences.getInt("gold",0);
        int diamond = preferences.getInt("diamond",0);

        int currentScore = preferences.getInt(config.getBarrierID(),-1);
        //获取下一关的分数
        int barrier = Integer.valueOf(config.getBarrierID());
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
