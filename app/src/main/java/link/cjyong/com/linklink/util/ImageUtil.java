package link.cjyong.com.linklink.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import link.cjyong.com.linklink.R;
import link.cjyong.com.linklink.view.PieceImage;

/**
 * Created by cjyong on 2017/3/23.
 * 用来获取资源中图片的工具类
 */

public class ImageUtil
{
    // 保存所有连连看图片资源值(int类型)
    private static List<Integer> imageValues = getImageValues();
    //表明该工具类选取图片时,是否选择较少的图片
    public static int lessImage = 1;
    //保存该工具类用来获取图片而使用的设备上下文
    private static Context context = MyApplication.getInstance().getApplicationContext();


    //获取连连看所有图片的ID（约定所有图片ID以p_开头）
    public static List<Integer> getImageValues()
    {
        try
        {
            // 得到R.drawable所有的属性, 即获取drawable目录下的所有图片
            Field[] drawableFields = R.drawable.class.getFields();
            List<Integer> resourceValues = new ArrayList<Integer>();
            for (Field field : drawableFields)
            {
                // 如果该Field的名称以p_开头
                if (field.getName().startsWith("p_")==true)
                {

                    resourceValues.add(field.getInt(R.drawable.class));
                }
            }
            return resourceValues;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * 随机从sourceValues的集合中获取size个图片ID, 返回结果为图片ID的集合
     *
     * @param sourceValues 从中获取的集合
     * @param size 需要获取的个数
     * @return size个图片ID的集合
     */
    public static List<Integer> getRandomValues(List<Integer> sourceValues,
                                                int size)
    {
        // 创建一个随机数生成器
        Random random = new Random();
        // 创建结果集合
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < size; i++)
        {
            try
            {
                // 随机获取一个数字，大于、小于sourceValues.size()的数值
                int index = random.nextInt(sourceValues.size()/lessImage);
                // 从图片ID集合中获取该图片对象
                Integer image = sourceValues.get(index);
                // 添加到结果集中
                result.add(image);
            }
            catch (IndexOutOfBoundsException e)
            {
                return result;
            }
        }
        return result;
    }

    /**
     * 从drawable目录中中获取size个图片资源ID(以p_为前缀的资源名称), 其中size为游戏数量
     *
     * @param size 需要获取的图片ID的数量
     * @return size个图片ID的集合
     */
    public static List<Integer> getPlayValues(int size)
    {
        if (size % 2 != 0)
        {
            // 如果该数除2有余数，将size加1
            size += 1;
        }
        // 再从所有的图片值中随机获取size的一半数量
        List<Integer> playImageValues = getRandomValues(imageValues, size / 2);
        // 将playImageValues集合的元素增加一倍（保证所有图片都有与之配对的图片）
        playImageValues.addAll(playImageValues);
        // 将所有图片ID随机“洗牌”
        Collections.shuffle(playImageValues);
        return playImageValues;
    }

    /**
     * 将图片ID集合转换PieceImage对象集合，PieceImage封装了图片ID与图片本身
     *
     * @param size
     * @return size个PieceImage对象的集合
     */
    public static List<PieceImage> getPlayImages(int size)
    {
        // 获取图片ID组成的集合
        List<Integer> resourceValues = getPlayValues(size);
        List<PieceImage> result = new ArrayList<PieceImage>();
        // 遍历每个图片ID
        for (Integer value : resourceValues)
        {
            // 加载图片
            Bitmap bm = BitmapFactory.decodeResource(
                    context.getResources(),  value);
            // 封装图片ID与图片本身
            PieceImage pieceImage = new PieceImage(bm, value);
            result.add(pieceImage);
        }
        return result;
    }

    /**
     * 从设备上下文中随机获取一张照片,并封装成PieceImage对象,返回
     * @return 一个随机的PieceImage
     */
    public static PieceImage getRandomPieceImage()
    {
        List<Integer> resourceValue = getPlayValues(2);
        List<PieceImage> result = new ArrayList<PieceImage>();
        PieceImage pieceImage = null;
        for(Integer value : resourceValue) {
            Bitmap bm = BitmapFactory.decodeResource(
                    context.getResources(),  value);
            // 封装图片ID与图片本身
            pieceImage = new PieceImage(bm, value);
            break;
        }
        return pieceImage;
    }


    /**
     * 获取特殊Piece的标识符
     * @return 返回表明特殊Piece的标识符Bitmap
     */
    public static Bitmap getSpecialPieceSignImage()
    {
        //后期需要对标识符进行替换
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.changed);
        return bm;
    }

    /**
     * 获取用户点击选择的标识符
     * @return 返回表明用户点击选择的标识符Bitmap
     */
    public static Bitmap getSelectImage()
    {
        //后期需要对标识符进行替换
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.selected);
        return bm;
    }

    /**
     * 获取关卡的标识符
     * @return 返回关卡的标识符Bitmap
     */
    public static Bitmap getBarrierImage()
    {
        //后期需要根据不同的关卡,传递参数进行不同标识符选择
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.barrier);
        return bm;
    }

    /**
     * 获取关卡解锁的标识符
     * @return 返回关卡解锁的标识符Bitmap
     */
    public static Bitmap getLockImage(Context context)
    {
        //后期需要对标识符进行替换
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.lock);
        return bm;
    }

    /**
     * 获取提示卡所提示的标识符
     * @return 返回提示的标识符Bitmap
     */
    public static Bitmap getHelpedImage()
    {
        //后期需要对标识符进行替换
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.helped);
        return bm;
    }

    /**
     * 默认背包空背景
     * @return 返回背包空背景Bitmap
     */
    public static Bitmap getBackNodeBackgroundImage()
    {
        //后期需要对标识符进行替换
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.backpackbaground);
        return bm;
    }
}
