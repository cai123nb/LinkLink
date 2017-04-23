package link.cjyong.com.linklink.view;

import android.graphics.Bitmap;

/**
 * Created by cjyong on 2017/3/22.
 * 存储在Piece中的图片类
 */

public class PieceImage
{
    //图片的Bitmap资源
    private Bitmap image;
    //图片的ID,用于表明图片是否一致
    private int imageId;

    /**
     * 带参数的构造函数,用于PieceImage的初始化
     * @param image
     * @param imageId
     */
    public PieceImage(Bitmap image, int imageId)
    {
        super();
        this.image = image;
        this.imageId = imageId;
    }

    /**
     * 根据PieceImage的Id对Piece进行判断,判断图片是否想相同
     * @param pi
     * @return 图片是否相同
     */
    public boolean isSameImage(PieceImage pi)
    {
        return this.imageId==pi.getImageId();
    }

    //Getter and Setter
    public Bitmap getImage()
    {
        return image;
    }

    public void setImage(Bitmap image)
    {
        this.image = image;
    }

    public int getImageId()
    {
        return imageId;
    }

    public void setImageId(int imageId)
    {
        this.imageId = imageId;
    }

}
