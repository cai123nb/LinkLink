package link.cjyong.com.linklink.element;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import link.cjyong.com.linklink.Activity.mainActivity.MainActivity;

/**
 * Created by cjyong on 2017/3/22.
 * 返回主页面的Listener,代码重用
 */

public class HomeListener implements View.OnClickListener
{
    private Activity activity;
    public HomeListener(Activity activity)
    {
        this.activity = activity;
    }
    @Override
    public void onClick(View source)
    {
        Intent i = new Intent(activity , MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(i);
    }
}
