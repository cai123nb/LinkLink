package link.cjyong.com.linklink.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import link.cjyong.com.linklink.util.ImageUtil;

/**
 * Created by cjyong on 2017/4/16.
 * 背包显示的具体道具类
 */

public class BackpackNode extends ImageView
{
    //ImageView用户绘图的画笔
    private Paint paint;
    //道具的数量,默认是0个
    private int toolNums;

    public BackpackNode(Context context) {
        super(context);
        paint = new Paint();
    }

    public BackpackNode(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    public BackpackNode(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
    }

    /**
     * 重写Ondraw函数,添加数量显示
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if(drawable!=null && toolNums>0) {
            Bitmap bm = ((BitmapDrawable) drawable).getBitmap();
            paint.reset();
            canvas.drawBitmap(bm,0,0,paint);
            //添加数量显示
            float posX = (float)0.6 * getWidth();
            float posY = (float)0.6 * getHeight();
            paint.setTextSize(40);
            canvas.drawText(""+toolNums,posX,posY,paint);
        }
        else {
            Bitmap bmback = ImageUtil.getBackNodeBackgroundImage();
            paint.reset();
            canvas.drawBitmap(bmback,0,0,paint);
        }
    }

    //Getter and Setter
    public int getToolNums() {
        return toolNums;
    }

    public void setToolNums(int toolNums) {
        this.toolNums = toolNums;
    }
}
