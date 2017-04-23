package link.cjyong.com.linklink.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.os.IBinder;

import java.io.IOException;

/**
 * Created by cjyong on 2017/4/19.
 * 后台Service进行背景音乐的播放
 */

public class BackgroundMusicService extends Service
{
    // 当前的状态,0x11 代表没有播放 ；0x12代表 正在播放；0x13代表暂停
    int status = 0x11;
    //访问Assetsn内部资源
    AssetManager am;
    //音乐列表
    String[] musics = new String[] { "background1.mp3", "background2.mp3",
             };
    //播放器
    MediaPlayer mPlayer;
    //记录当前播放的音乐
    int current = 0;
    //每次调用startCommand时,会调用两次,用这个参数进行辨别
    int first = 1;

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        am = getAssets();
        // 创建MediaPlayer
        mPlayer = new MediaPlayer();
        //设置无限循环播放
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                prepareAndPlay(musics[current]);
            }
        });
        //播放背景音乐
        prepareAndPlay(musics[current]);
        status= 0x12;
        System.out.println("Service is created!");
        super.onCreate();
    }


    /**
     * 播放背景音乐
     * @param music
     */
    private void prepareAndPlay(String music)
    {
        try
        {
            // 打开指定音乐文件
            AssetFileDescriptor afd = am.openFd(music);
            mPlayer.reset();
            // 使用MediaPlayer加载指定的声音文件。
            mPlayer.setDataSource(afd.getFileDescriptor(),
                    afd.getStartOffset(), afd.getLength());
            // 准备声音
            mPlayer.prepare();
            // 播放
            mPlayer.start();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        int control = intent.getIntExtra("control", -1);
        switch (control)
        {
            // 播放音乐
            case 1:
                // 原来处于没有播放状态
                if (status == 0x11)
                {
                    // 准备、并播放音乐
                    prepareAndPlay(musics[current]);
                    status = 0x12;
                }
                // 原来处于播放状态
                else if (status == 0x12)
                {
                    break;
                }
                // 原来处于暂停状态
                else if (status == 0x13)
                {
                    // 播放
                    mPlayer.start();
                    // 改变状态
                    status = 0x12;
                }
                break;
            // 转换背景音乐
            case 2:
                if(first%2==0) {
                    if (status == 0x12)  // 如果原来正在播放
                    {
                        // 停止播放
                        mPlayer.stop();
                    }
                    //切换音乐
                    prepareAndPlay(musics[1 - current]);
                    current = 1 - current;
                    status = 0x12;
                }
                first++;
                break;
            //暂停播放音乐
            case 3:
                if(status== 0x13 || status == 0x11)
                {
                    //当前未播放音乐
                    break;
                }
                else
                {
                    //当前正在播放音乐
                    mPlayer.pause();
                    status= 0x13;
                    break;
                }
        }
        return super.onStartCommand(intent,flags,startId);
    }

}

