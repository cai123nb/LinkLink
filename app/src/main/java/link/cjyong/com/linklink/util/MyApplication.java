package link.cjyong.com.linklink.util;

import android.app.Application;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import java.util.HashMap;

import link.cjyong.com.linklink.R;

/**
 * Created by cjyong on 2017/3/27.
 * 项目的实例类,用于存储一些全局变量和方便工具类获取上下文
 */

public class MyApplication extends Application
{
    //为需要的工具类提供Application的实例,方便调用上下文资源
    private static MyApplication instance;
    //播放音效的道具,用于上下文播放音效
    private static SoundPool soundPool;
    //存放音乐ID的hashmap
    private static HashMap<Integer,Integer> soundID = new HashMap<Integer, Integer>();
    //音量的大小
    private static float soundVolumn = 0;
    //背景音乐是否播放 1:正在播放 0:暂停中
    private static int backIsOn = 1;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        instance = this;
        initSoundPool();
    }

    /**
     * 初始化SoundPool
     */
    private void initSoundPool()
    {
        //当前系统的SDK版本大于等于21(Android 5.0)时
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            //传入音频数量
            builder.setMaxStreams(5);
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            //设置音频流的合适的属性
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
            //加载一个AudioAttributes
            builder.setAudioAttributes(attrBuilder.build());
            soundPool = builder.build();
        }
        //当系统的SDK版本小于21时
        else {//设置最多可容纳2个音频流，音频的品质为5
            soundPool = new SoundPool(5, AudioManager.STREAM_SYSTEM, 5);
        }

        soundID.put(1, soundPool.load(this, R.raw.dis,1));
        soundID.put(2,soundPool.load(this,R.raw.bomb,2));

        soundVolumn = 1;
    }

    //Getter and Setter

    public static HashMap<Integer,Integer> getSoundID(){return soundID;}

    public static MyApplication getInstance() {
        return instance;
    }

    public static SoundPool getSoundPool()
    {
        return soundPool;
    }

    public static float getSoundVolumn() {
        return soundVolumn;
    }

    public static void setSoundVolumn(float soundVolumn) {
        MyApplication.soundVolumn = soundVolumn;
    }

    public static int getBackIsOn() {
        return backIsOn;
    }

    public static void setBackIsOn(int backIsOn) {
        MyApplication.backIsOn = backIsOn;
    }
}
