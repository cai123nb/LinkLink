package link.cjyong.com.linklink.board;

import java.util.List;

import link.cjyong.com.linklink.util.ImageUtil;
import link.cjyong.com.linklink.element.GameConf;
import link.cjyong.com.linklink.util.MyApplication;
import link.cjyong.com.linklink.view.Piece;
import link.cjyong.com.linklink.view.PieceImage;

/**
 * Created by cjyong on 2017/3/23.
 * GameView产生的Board类,用于产生不同的Board
 */

public abstract class AbstractBoard
{
    // 定义一个抽象方法, 让子类去实现
    protected abstract List<Piece> createPieces(GameConf config,
                                                Piece[][] pieces);

    public Piece[][] create(GameConf config)
    {
        // 创建Piece[][]数组
        Piece[][] pieces = new Piece[config.getxSize()][config.getySize()];
        // 返回非空的Piece集合, 该集合由子类去创建
        List<Piece> notNullPieces = createPieces(config, pieces);
        // 根据非空Piece对象的集合的大小来取图片
        List<PieceImage> playImages = ImageUtil.getPlayImages(notNullPieces.size());
        // 所有图片的宽、高都是相同的
        int imageWidth = playImages.get(0).getImage().getWidth();
        int imageHeight = playImages.get(0).getImage().getHeight();
        // 遍历非空的Piece集合
        for (int i = 0; i < notNullPieces.size(); i++)
        {
            // 依次获取每个Piece对象
            Piece piece = notNullPieces.get(i);
            piece.setImage(playImages.get(i));
            // 计算每个方块左上角的X、Y座标
            piece.setBeginX(piece.getIndexX() * imageWidth
                    + config.getBeginImageX());
            piece.setBeginY(piece.getIndexY() * imageHeight
                    + config.getBeginImageY());
            // 将该方块对象放入方块数组的相应位置处
            pieces[piece.getIndexX()][piece.getIndexY()] = piece;
        }
        return pieces;
    }
}
