package link.cjyong.com.linklink.view;

import android.graphics.Point;

/**
 * Created by cjyong on 2017/3/22.
 * 连连看中表明方块的类
 */

public class Piece
{
    // 保存方块对象的所对应的图片
    private PieceImage image;
    // 该方块的左上角的x坐标
    private int beginX;
    // 该方块的左上角的y座标
    private int beginY;
    // 该对象在Piece[][]数组中第一维的索引值
    private int indexX;
    // 该对象在Piece[][]数组中第二维的索引值
    private int indexY;
    //该对象是否为特殊Piece,可消多次
    private boolean isSpecial=false;
    //该对象的点击次数
    private int clickedNum=3;

    /**
     * 带参数的构造函数,对Piece内各个属性进行初始化
     * @param image
     * @param beginX
     * @param beginY
     * @param indexX
     * @param indexY
     */
    public Piece(PieceImage image, int beginX, int beginY, int indexX, int indexY)
    {
        this.image = image;
        this.beginX = beginX;
        this.beginY = beginY;
        this.indexX = indexX;
        this.indexY = indexY;
        this.isSpecial = false;
        this.clickedNum = 3;
    }


    /**
     * 带参数的构造函数,对Piece内各个属性进行初始化
     * @param indexX
     * @param indexY
     */
    public Piece(int indexX , int indexY)
    {
        this.indexX = indexX;
        this.indexY = indexY;
    }


    /**
     * 获取方块的中心点的坐标
     * @return 返回方块的中心点
     */
    public Point getCenter()
    {
        return new Point(getImage().getImage().getWidth() / 2
                + getBeginX(), getBeginY()
                + getImage().getImage().getHeight() / 2);
    }

    /**
     * 判断两个方块内的图片是否是一致的
     * @param other
     * @return 两个方块图片是否一致
     */
    public boolean isSameImage(Piece other)
    {
        if (image == null)
        {
            if (other.image != null)
                return false;
        }
        // 只要Piece封装图片ID相同，即可认为两个Piece相等。
        return image.getImageId() == other.image.getImageId();
    }

    //Getter and Setter
    public PieceImage getImage()
    {
        return image;
    }

    public void setImage(PieceImage image)
    {
        this.image = image;
    }

    public int getBeginX()
    {
        return beginX;
    }

    public void setBeginX(int beginX)
    {
        this.beginX = beginX;
    }

    public int getBeginY()
    {
        return beginY;
    }

    public void setBeginY(int beginY)
    {
        this.beginY = beginY;
    }

    public int getIndexX()
    {
        return indexX;
    }

    public void setIndexX(int indexX)
    {
        this.indexX = indexX;
    }

    public int getIndexY()
    {
        return indexY;
    }

    public void setIndexY(int indexY)
    {
        this.indexY = indexY;
    }

    public int getClickedNum() {
        return clickedNum;
    }

    public void setClickedNum(int clickedNum) {
        this.clickedNum = clickedNum;
    }

    public boolean isSpecial() {
        return isSpecial;
    }

    public void setSpecial(boolean special) {
        isSpecial = special;
    }
}
