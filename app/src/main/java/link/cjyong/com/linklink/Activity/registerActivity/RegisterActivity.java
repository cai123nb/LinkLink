package link.cjyong.com.linklink.Activity.registerActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import link.cjyong.com.linklink.Activity.mainActivity.MainActivity;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.service.SendMailThread;
import link.cjyong.com.linklink.util.HttpUtil;

/**
 * Created by cjyong on 2017/4/8.
 * 用于用户注册的Activity类
 */

public class RegisterActivity extends Activity
{
    //我同意的点击框
    private CheckBox checkBox;
    //用户输入的用户名,邮箱地址和激活码
    private EditText etUsername,etEmailAddress,etActivatedCode;
    //发送和注册的按钮
    private Button sendActivated,register;
    //激活码
    private String activatedCode;
    //访问本地存储数据的接口
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    //定时器防止用户无限点击注册,导致线程崩溃,设置20秒间隔
    private Timer timer;
    //20秒间隔时间
    private int time;
    //处理定时器传递过来的时间信息
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    sendActivated.setText(" "+time+"秒再次发送");
                    sendActivated.setEnabled(false);
                    time--;
                    if (time < 0) {
                        sendActivated.setText("发送激活码");
                        sendActivated.setEnabled(true);
                        timer.cancel();
                        timer = null;
                        return;
                    }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置横屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.register_main);

        //初始化操作
        init();

    }

    /**
     * 对界面进行初始化操作
     */
    private void init()
    {
        //绑定组件
        checkBox = (CheckBox) findViewById(R.id.agree_cb);
        etUsername = (EditText) findViewById(R.id.username);
        etEmailAddress = (EditText) findViewById(R.id.email);
        etActivatedCode = (EditText) findViewById(R.id.activatedCode);
        sendActivated = (Button) findViewById(R.id.Send);
        register = (Button) findViewById(R.id.register);

        //生成激活码
        activatedCode = generateActivatedCode(5);

        //添加监听器
        //发送注册码
        sendActivated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodToSend()) {
                    //发送邮件
                    new SendMailThread(etUsername.getText().toString(),etEmailAddress.getText().toString(),activatedCode, SendMailThread.MailType.registerMail).start();
                    Toast.makeText(RegisterActivity.this,"发送成功,请及时查收",Toast.LENGTH_LONG).show();
                    //设置20秒CD时间
                    time=20;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessage(0x123);
                        }
                    }, 0, 1000);
                }
            }
        });

        //进行注册
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(goodToRegister())
                {
                    register();
                }
            }
        });
    }

    /**
     * 产生固定长度的随机激活码
     * @param length   激活码的长度
     * @return  生成的激活码
     */
    private String generateActivatedCode(int length)
    {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 判断用户输入是否有误,是否可以联网等,以判断是否可以进行注册操作
     * @return  条件是否符合注册标准
     */
    private boolean goodToSend()
    {
        if(!checkBox.isChecked())
        {
            //没有点击同意按钮
            Toast.makeText(this,"请点击按钮:我同意以上观点",Toast.LENGTH_SHORT).show();
            return false;
        }
        if(etUsername.getText().toString().equals("")  || etEmailAddress.getText().toString().equals(""))
        {
            //信息没有完全输入
            Toast.makeText(this,"请输入你的用户名和邮箱",Toast.LENGTH_SHORT).show();
            return false;
        }
        //判断是否输入正确的邮箱地址
        String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern regex = Pattern.compile(check);
        Matcher matcher = regex.matcher(etEmailAddress.getText().toString());
        boolean isMatched = matcher.matches();
        if(!isMatched)
        {
            Toast.makeText(this,"你输入的邮箱有误,请重新输入",Toast.LENGTH_SHORT).show();
            return false;
        }


        //判断网络是否可用
        String html = null;
        // 当前所连接的网络可用
        try {
            html = HttpUtil.sendGetRequest("http://www.bilibili.com", false, null, "utf-8");
            if(html!=null && html.contains("<meta http-equiv="))
            {
                return true;
            }
            else
            {
                Toast.makeText(this,"请确认你的网络已正确连接",Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断用户输入激活码是否对应
     * @return  激活码是否正确
     */
    private boolean goodToRegister()
    {
        if(!goodToSend())
        {
            return false;
        }

        if(etActivatedCode.getText().toString().equals(""))
        {
            Toast.makeText(this,"请输入你的激活码",Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!etActivatedCode.getText().toString().equals(activatedCode))
        {
            Toast.makeText(this,"激活码不对应,请再次确认你的邮箱",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 用户注册成功后,进行对应操作
     */
    private void register()
    {
        preferences = getSharedPreferences("gameData",0);
        editor = preferences.edit();
        String username = etUsername.getText().toString();
        //存储用户名
        editor.putString("username",username);
        //开启第一个大关第一小关
        editor.putInt("11",0);
        editor.commit();
        Toast.makeText(this,"恭喜你:"+username+"注册成功",Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
