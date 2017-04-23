package link.cjyong.com.linklink.board;

import link.cjyong.com.linklink.element.LinkInfo;
import link.cjyong.com.linklink.view.Piece;

/**
 * Created by cjyong on 2017/3/23.
 */

public interface GameService
{
    /**
     * 控制游戏开始的方法
     */
    void start();

    /**
     * 定义一个接口方法, 用于返回一个二维数组
     *
     * @return 存放方块对象的二维数组
     */
    Piece[][] getPieces();

    /**
     * 判断参数Piece[][]数组中是否还存在非空的Piece对象
     *
     * @return 如果还剩Piece对象返回true, 没有返回false
     */
    boolean hasPieces();

    /**
     * 根据鼠标的x座标和y座标, 查找出一个Piece对象
     *
     * @param touchX 鼠标点击的x座标
     * @param touchY 鼠标点击的y座标
     * @return 返回对应的Piece对象, 没有返回null
     */
    Piece findPiece(float touchX, float touchY);

    /**
     * 判断两个Piece是否可以相连, 可以连接, 返回LinkInfo对象
     *
     * @param p1 第一个Piece对象
     * @param p2 第二个Piece对象
     * @return 如果可以相连，返回LinkInfo对象, 如果两个Piece不可以连接, 返回null
     */
    LinkInfo link(Piece p1, Piece p2);

    /**
     * 判断是否进入死锁,即是否存在可以连接的两个方块
     * @return
     */
    boolean haveUsefulLinked();

    /**
     * 重新洗牌,将Piece重新打乱
     */
    void resetting();

    /**
     * 清除某一类的图标,爆炸卡的使用效果
     */
    void clearOneKindPiece();

    /**
     * 清除一张卡,在Piece会移动的情况下
     * @param piece
     */
    void clearOnePieceInMove(Piece piece);


    /**
     * 提示卡的实现,寻找两个可以互相连接的Piece
     * @return 2个可以互相连接的Piece
     */
    Piece[] getUsefulPieces();

    /**
     * 移动Piece,一般根据配置文件,进行对应的移动操作
     * @param piece1
     * @param piece2
     */
    void movePieces(Piece piece1,Piece piece2);

    /**
     * 左移动Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void leftMovePieces(Piece piece1,Piece piece2);

    /**
     * 右移动Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void rightMovePieces(Piece piece1,Piece piece2);

    /**
     * 上移动Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void upMovePieces(Piece piece1,Piece piece2);

    /**
     * 下移动Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void downMovePieces(Piece piece1,Piece piece2);

    /**
     * 中间左右移动Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void midLeftRightMovePices(Piece piece1,Piece piece2);

    /**
     * 中间上下移动Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void midUpDownMovePieces(Piece piece1,Piece piece2);

    /**
     * 下移动Piece,并自动填充空余的Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void downMovePiecesAndFilling(Piece piece1,Piece piece2);

    /**
     * 下移动特殊Piece,并自动填充空余的Piece
     * @param piece1    连接的Piece
     * @param piece2    连接的Piece
     */
    void downMoveSpecialPiecesAndFilling(Piece piece1,Piece piece2);

    /**
     * 添加特殊的Piece
     * @param num
     */
    void addSpecialPieces(int num);
}
