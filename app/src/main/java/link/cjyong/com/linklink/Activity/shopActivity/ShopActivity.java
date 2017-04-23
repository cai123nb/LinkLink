package link.cjyong.com.linklink.Activity.shopActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import link.cjyong.com.linklink.Activity.mainActivity.MainActivity;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.element.HomeListener;

/**
 * Created by cjyong on 2017/4/16.
 * 商店Activity类
 */

public class ShopActivity extends Activity
{
    //各个道具数量显示图标
    private TextView tvBomb,tvTime,tvHelp,tvRest,tvGold,tvDiamond;
    //对应道具的购买的按钮
    private Button buyBomb,buyTime,buyHelp,buyRest,buyGold,btnReturn;
    //访问游戏内部数据的接口
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //道具,金币和钻石的数量
    private int bombNum;
    private int timeNum;
    private int helpNum;
    private int resetNum;
    private int goldNum;
    private int diamondNum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //设置横屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.shop_main);
        //初始化界面
        init();
    }

    /**
     * 对界面进行初始化操作
     */
    private void init()
    {
        //绑定组件
        tvBomb = (TextView) findViewById(R.id.bombtoolnum);
        tvTime = (TextView) findViewById(R.id.timetoolnum);
        tvHelp = (TextView) findViewById(R.id.helptoolnum);
        tvRest = (TextView) findViewById(R.id.resettoolnum);
        tvGold = (TextView) findViewById(R.id.goldnum);
        tvDiamond = (TextView) findViewById(R.id.diamondnum);
        buyBomb = (Button) findViewById(R.id.buybomb);
        buyTime = (Button) findViewById(R.id.buytime);
        buyHelp = (Button) findViewById(R.id.buyhelp);
        buyRest = (Button) findViewById(R.id.buyreset);
        buyGold = (Button) findViewById(R.id.buygold);
        btnReturn = (Button) findViewById(R.id.returnHome);

        //获取用户的使用数据
        preferences = getSharedPreferences("gameData",0);
        editor = preferences.edit();
        timeNum = preferences.getInt("timeTool",0);
        bombNum = preferences.getInt("bombTool",0);
        resetNum = preferences.getInt("resetTool",0);
        helpNum = preferences.getInt("helpTool",0);
        goldNum = preferences.getInt("gold",0);
        diamondNum = preferences.getInt("diamond",0);

        //绑定数值
        tvBomb.setText(""+bombNum);
        tvRest.setText(""+resetNum);
        tvHelp.setText(""+helpNum);
        tvTime.setText(""+timeNum);
        tvGold.setText(""+goldNum);
        tvDiamond.setText(""+diamondNum);

        //绑定监听器
        buyBomb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goldNum>=300)
                {
                    goldNum-=300;
                    bombNum++;
                    updateIcons();
                }
                else
                {
                    Toast.makeText(ShopActivity.this,"金币不足!",Toast.LENGTH_LONG).show();
                }
            }
        });
        buyGold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(diamondNum>=5)
                {
                    goldNum+=200;
                    diamondNum-=5;
                    updateIcons();
                }
                else
                {
                    Toast.makeText(ShopActivity.this,"钻石不足!",Toast.LENGTH_LONG).show();
                }
            }
        });
        buyTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goldNum>=200)
                {
                    goldNum-=200;
                    timeNum++;
                    updateIcons();
                }
                else
                {
                    Toast.makeText(ShopActivity.this,"金币不足!",Toast.LENGTH_LONG).show();
                }
            }
        });
        buyRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goldNum>=150)
                {
                    goldNum-=150;
                    resetNum++;
                    updateIcons();
                }
                else
                {
                    Toast.makeText(ShopActivity.this,"金币不足!",Toast.LENGTH_LONG).show();
                }
            }
        });
        buyHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goldNum>=100)
                {
                    goldNum-=100;
                    helpNum++;
                    updateIcons();
                }
                else
                {
                    Toast.makeText(ShopActivity.this,"金币不足!",Toast.LENGTH_LONG).show();
                }
            }
        });

        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData();
                Intent intent = new Intent(ShopActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * 购买之后将数据更新到页面
     */
    private void updateIcons()
    {
        tvBomb.setText(""+bombNum);
        tvRest.setText(""+resetNum);
        tvHelp.setText(""+helpNum);
        tvTime.setText(""+timeNum);
        tvGold.setText(""+goldNum);
        tvDiamond.setText(""+diamondNum);
        Toast.makeText(this,"购买成功",Toast.LENGTH_SHORT).show();
    }

    /**
     * 将更新后的数据备份到本地
     */
    private void updateData()
    {
        editor.putInt("timeTool",timeNum);
        editor.putInt("resetTool",resetNum);
        editor.putInt("bombTool",bombNum);
        editor.putInt("helpTool",helpNum);
        editor.putInt("gold",goldNum);
        editor.putInt("diamond",diamondNum);
        editor.commit();
    }

}
