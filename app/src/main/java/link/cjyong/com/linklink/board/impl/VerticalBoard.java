package link.cjyong.com.linklink.board.impl;

import java.util.ArrayList;
import java.util.List;

import link.cjyong.com.linklink.board.AbstractBoard;
import link.cjyong.com.linklink.element.GameConf;
import link.cjyong.com.linklink.view.Piece;

/**
 * Created by cjyong on 2017/3/23.
 * Board的实现类
 */

public class VerticalBoard extends AbstractBoard
{
    protected List<Piece> createPieces(GameConf config,
                                       Piece[][] pieces)
    {
        // 创建一个Piece集合, 该集合里面存放初始化游戏时所需的Piece对象
        List<Piece> notNullPieces = new ArrayList<Piece>();
        for (int i = 1; i < pieces.length; i++)
        {
            for (int j = 1; j < pieces[i].length; j++)
            {
                // 加入判断, 符合一定条件才去构造Piece对象, 并加到集合中
                if (i % 2 == 0)
                {
                    // 如果x能被2整除, 即单数列不会创建方块
                    // 先构造一个Piece对象, 只设置它在Piece[][]数组中的索引值，
                    // 所需要的PieceImage由其父类负责设置。
                    Piece piece = new Piece(i, j);
                    // 添加到Piece集合中
                    notNullPieces.add(piece);
                }
            }
        }
        return notNullPieces;
    }
}
