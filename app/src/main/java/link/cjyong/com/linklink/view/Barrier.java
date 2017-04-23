package link.cjyong.com.linklink.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import link.cjyong.com.linklink.util.ImageUtil;

/**
 * Created by cjyong on 2017/3/29.
 * 用于表明关卡的图像类,后期需要进行美化和拓展
 */

public class Barrier extends View
{
    //关卡图标
    private Bitmap bitmap;
    //关卡是否解锁
    private boolean locked;
    //锁的图标
    private Bitmap lockBitmap;

    /**
     * 带参数的构造函数,对Image类进行初始化操作
     * @param context
     * @param sttrs
     */
    public Barrier(Context context, AttributeSet sttrs)
    {
        super(context,sttrs);
        this.bitmap = ImageUtil.getBarrierImage();
        this.lockBitmap = ImageUtil.getLockImage(context);
        this.locked = false;
    }


    /**
     * 重载了绘图函数,进行锁的绘制操作
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        //绘制图标
        canvas.drawBitmap(bitmap,0,0,null);
        if(this.locked)
        {
            //绘制锁的图标
            canvas.drawBitmap(lockBitmap,10,10,null);
        }
    }

    //Getter and Setter
    public Bitmap getLockBitmap() {
        return lockBitmap;
    }

    public void setLockBitmap(Bitmap lockBitmap) {
        this.lockBitmap = lockBitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

}
