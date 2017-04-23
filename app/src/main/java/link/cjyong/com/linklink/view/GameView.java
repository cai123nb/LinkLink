package link.cjyong.com.linklink.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

import link.cjyong.com.linklink.util.ImageUtil;
import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.board.GameService;
import link.cjyong.com.linklink.element.LinkInfo;
import link.cjyong.com.linklink.util.MyApplication;

/**
 * Created by cjyong on 2017/3/22.
 * 游戏使用的主画板类
 */

public class GameView extends View
{
    // 游戏逻辑的实现类
    private GameService gameService;
    // 保存当前已经被选中的方块
    private Piece selectedPiece;
    //保存当前提示所选中的方块
    private Piece helpedPiece;
    // 连接信息对象
    private LinkInfo linkInfo;
    //画笔工具
    private Paint paint;
    // 选中标识的图片对象
    private Bitmap selectImage;
    //提示选中的图片对象
    private Bitmap helpedImage;

    /**
     * 带参数的构造函数,对GameView进行初始化操作
     * @param context
     * @param attrs
     */
    public GameView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.paint = new Paint();
        // 使用位图平铺作为连接线条
        this.paint.setShader(new BitmapShader(BitmapFactory
                .decodeResource(context.getResources(), R.drawable.heart)
                , Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        // 设置连接线的粗细
        this.paint.setStrokeWidth(9);
        this.selectImage = ImageUtil.getSelectImage();
        this.helpedImage = ImageUtil.getHelpedImage();
    }

    public void setLinkInfo(LinkInfo linkInfo)
    {
        this.linkInfo = linkInfo;
    }

    public void setGameService(GameService gameService)
    {
        this.gameService = gameService;
    }

    /**
     * 重载绘图函数,进行自定义绘图操作
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        if (this.gameService == null)
            return;
        Piece[][] pieces = gameService.getPieces();
        if (pieces != null)
        {
            // 遍历pieces二维数组
            for (int i = 0; i < pieces.length; i++)
            {
                for (int j = 0; j < pieces[i].length; j++)
                {
                    // 如果二维数组中该元素不为空（即有方块），将这个方块的图片画出来
                    if (pieces[i][j] != null)
                    {
                        // 得到这个Piece对象
                        Piece piece = pieces[i][j];
                        canvas.drawBitmap(piece.getImage().getImage(),
                                piece.getBeginX(), piece.getBeginY(), null);
                        if(piece.isSpecial()) {
                            //绘制特殊图片的标志
                            canvas.drawBitmap(ImageUtil.getSpecialPieceSignImage(),
                                    piece.getBeginX(), piece.getBeginY(), null);
                        }
                    }
                }
            }
        }
        // 如果当前对象中有linkInfo对象, 即连接信息
        if (this.linkInfo != null)
        {
            // 绘制连接线
            drawLine(this.linkInfo, canvas);
            // 处理完后清空linkInfo对象
            this.linkInfo = null;
        }
        // 画选中标识的图片
        if (this.selectedPiece != null)
        {
            canvas.drawBitmap(this.selectImage, this.selectedPiece.getBeginX(),
                    this.selectedPiece.getBeginY(), null);
        }
        if(this.helpedPiece!=null)
        {
            canvas.drawBitmap(this.helpedImage,this.helpedPiece.getBeginX(),this.helpedPiece.getBeginY(),null);
        }

    }

    /**
     * 绘制连接信息
     * @param linkInfo
     * @param canvas
     */
    private void drawLine(LinkInfo linkInfo, Canvas canvas)
    {
        // 获取LinkInfo中封装的所有连接点
        List<Point> points = linkInfo.getLinkPoints();
        // 依次遍历linkInfo中的每个连接点
        for (int i = 0; i < points.size() - 1; i++)
        {
            // 获取当前连接点与下一个连接点
            Point currentPoint = points.get(i);
            Point nextPoint = points.get(i + 1);
            // 绘制连线
            canvas.drawLine(currentPoint.x , currentPoint.y,
                    nextPoint.x, nextPoint.y, this.paint);
        }
    }

    /**
     * 开始游戏操作
     */
    public void startGame()
    {
        this.gameService.start();
        this.postInvalidate();
    }

    //Getter and Setter

    public GameService getGameService() {
        return gameService;
    }

    public Piece getSelectedPiece() {
        return selectedPiece;
    }

    public void setSelectedPiece(Piece selectedPiece) {
        this.selectedPiece = selectedPiece;
    }

    public Piece getHelpedPiece() {
        return helpedPiece;
    }

    public void setHelpedPiece(Piece helpedPiece) {
        this.helpedPiece = helpedPiece;
    }

    public LinkInfo getLinkInfo() {
        return linkInfo;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Bitmap getSelectImage() {
        return selectImage;
    }

    public void setSelectImage(Bitmap selectImage) {
        this.selectImage = selectImage;
    }

    public Bitmap getHelpedImage() {
        return helpedImage;
    }

    public void setHelpedImage(Bitmap helpedImage) {
        this.helpedImage = helpedImage;
    }
}
