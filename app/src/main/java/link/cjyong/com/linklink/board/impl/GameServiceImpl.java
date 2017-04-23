package link.cjyong.com.linklink.board.impl;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import link.cjyong.com.linklink.board.AbstractBoard;
import link.cjyong.com.linklink.board.GameService;
import link.cjyong.com.linklink.element.GameConf;
import link.cjyong.com.linklink.element.LinkInfo;
import link.cjyong.com.linklink.util.ImageUtil;
import link.cjyong.com.linklink.view.Piece;
import link.cjyong.com.linklink.view.PieceImage;

/**
 * Created by cjyong on 2017/3/23.
 * GameService的实现类
 */

public class GameServiceImpl implements GameService
{
    // 定义一个Piece[][]数组，只提供getter方法
    private Piece[][] pieces;
    // 游戏配置对象
    private GameConf config;
    public GameServiceImpl(GameConf config)
    {
        // 将游戏的配置对象设置本类中
        this.config = config;
    }

    @Override
    public void start()
    {
        // 定义一个AbstractBoard对象
        AbstractBoard board = null;
        // 获取一个随机数, 可取值0、1、2、3四值。
        /*
        if(config2!=null)
        {
            board = new FullBoard();
            this.pieces = board.create(config);
            if(!haveUsefulLinked()) {
                resetting();
            }
            return;
        }
        */
        int index = config.getTaskKind();
        // 随机生成AbstractBoard的子类实例
        switch (index)
        {
            case 1:
                // 0返回VerticalBoard(竖向)
                board = new VerticalBoard();
                break;
            case 2:
                // 1返回HorizontalBoard(横向)
                board = new HorizontalBoard();
                break;
            case 0:
                // 默认返回FullBoard
                board = new FullBoard();
                break;
        }
        // 初始化Piece[][]数组
        this.pieces = board.create(config);
        if(!haveUsefulLinked()) {
            resetting();
        }
    }

    // 直接返回本对象的Piece[][]数组
    @Override
    public Piece[][] getPieces()
    {
        return this.pieces;
    }

    // 实现接口的hasPieces方法
    @Override
    public boolean hasPieces()
    {
        // 遍历Piece[][]数组的每个元素
        for (int i = 0; i < pieces.length; i++)
        {
            for (int j = 0; j < pieces[i].length; j++)
            {
                // 只要任意一个数组元素不为null，也就是还剩有非空的Piece对象
                if (pieces[i][j] != null)
                {
                    return true;
                }
            }
        }
        return false;
    }

    // 根据触碰点的位置查找相应的方块
    @Override
    public Piece findPiece(float touchX, float touchY)
    {
        // 由于在创建Piece对象的时候, 将每个Piece的开始座标加了
        // GameConf中设置的beginImageX/beginImageY值, 因此这里要减去这个值
        int relativeX = (int) touchX - this.config.getBeginImageX();
        int relativeY = (int) touchY - this.config.getBeginImageY();
        // 如果鼠标点击的地方比board中第一张图片的开始x座标和开始y座标要小, 即没有找到相应的方块
        if (relativeX < 0 || relativeY < 0)
        {
            return null;
        }
        // 获取relativeX座标在Piece[][]数组中的第一维的索引值
        // 第二个参数为每张图片的宽
        int indexX = getIndex(relativeX, GameConf.PIECE_WIDTH);
        // 获取relativeY座标在Piece[][]数组中的第二维的索引值
        // 第二个参数为每张图片的高
        int indexY = getIndex(relativeY, GameConf.PIECE_HEIGHT);
        // 这两个索引比数组的最小索引还小, 返回null
        if (indexX < 0 || indexY < 0)
        {
            return null;
        }
        // 这两个索引比数组的最大索引还大(或者等于), 返回null
        if (indexX >= this.config.getxSize()
                || indexY >= this.config.getySize())
        {
            return null;
        }
        // 返回Piece[][]数组的指定元素
        return this.pieces[indexX][indexY];
    }

    // 工具方法, 根据relative座标计算相对于Piece[][]数组的第一维
    // 或第二维的索引值 ，size为每张图片边的长或者宽
    private int getIndex(int relative, int size)
    {
        // 表示座标relative不在该数组中
        int index = -1;
        // 让座标除以边长, 没有余数, 索引减1
        // 例如点了x座标为20, 边宽为10, 20 % 10 没有余数,
        // index为1, 即在数组中的索引为1(第二个元素)
        if (relative % size == 0)
        {
            index = relative / size - 1;
        }
        else
        {
            // 有余数, 例如点了x座标为21, 边宽为10, 21 % 10有余数, index为2
            // 即在数组中的索引为2(第三个元素)
            index = relative / size;
        }
        return index;
    }

    // 实现接口的link方法
    @Override
    public LinkInfo link(Piece p1, Piece p2)
    {
        // 两个Piece是同一个, 即选中了同一个方块, 返回null
        if (p1.equals(p2))
            return null;
        // 如果p1的图片与p2的图片不相同, 则返回null
        if (!p1.isSameImage(p2))
            return null;
        // 如果p2在p1的左边, 则需要重新执行本方法, 两个参数互换
        if (p2.getIndexX() < p1.getIndexX())
            return link(p2, p1);
        // 获取p1的中心点
        Point p1Point = p1.getCenter();
        // 获取p2的中心点
        Point p2Point = p2.getCenter();
        // 如果两个Piece在同一行
        if (p1.getIndexY() == p2.getIndexY())
        {
            // 它们在同一行并可以相连
            if (!isXBlock(p1Point, p2Point, GameConf.PIECE_WIDTH))
            {
                return new LinkInfo(p1Point, p2Point);
            }
        }
        // 如果两个Piece在同一列
        if (p1.getIndexX() == p2.getIndexX())
        {
            if (!isYBlock(p1Point, p2Point, GameConf.PIECE_HEIGHT))
            {
                // 它们之间没有真接障碍, 没有转折点
                return new LinkInfo(p1Point, p2Point);
            }
        }
        // 有一个转折点的情况
        // 获取两个点的直角相连的点, 即只有一个转折点
        Point cornerPoint = getCornerPoint(p1Point, p2Point,
                GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT);
        if (cornerPoint != null)
        {
            return new LinkInfo(p1Point, cornerPoint, p2Point);
        }
        // 该map的key存放第一个转折点, value存放第二个转折点,
        // map的size()说明有多少种可以连的方式
        Map<Point, Point> turns = getLinkPoints(p1Point, p2Point,
                GameConf.PIECE_WIDTH, GameConf.PIECE_WIDTH);
        if (turns.size() != 0)
        {
            return getShortcut(p1Point, p2Point, turns,
                    getDistance(p1Point, p2Point));
        }
        return null;
    }

    //判断死锁
    @Override
    public boolean haveUsefulLinked()
    {
        boolean hasUsefulLink = false;
        //将二维数组转换为一维数组
        Piece[] thisPiece;
        int len = 0;
        // 计算一维数组长度
        for (Piece[] element : pieces) {
            len += element.length;
        }

        // 复制元素
        thisPiece = new Piece[len];
        int index = 0;
        for (Piece [] element : pieces) {
            for (Piece element2 : element) {
                thisPiece[index++] = element2;
            }
        }

        //遍历是否存在连接的图像
        for(int i=0;i<len;i++)
            for(int j=i+1;j<len;j++)
            {
                if(thisPiece[i]!=null && thisPiece[j]!=null)
                {
                    LinkInfo linkInfo = link(thisPiece[i],thisPiece[j]);
                    if(linkInfo!=null)
                    {
                        hasUsefulLink = true;
                        return hasUsefulLink;
                    }
                }
            }
        return hasUsefulLink;
    }

    //打乱
    @Override
    public void resetting()
    {
        /* old version
        //将二维数组转换为一维非空数组
        Piece[] thisPiece;
        int len = 0;
        // 计算一维数组长度
        for (Piece[] element : pieces) {
            len += element.length;
        }

        // 复制元素非空元素
        thisPiece = new Piece[len];
        int index = 0;
        for (Piece [] element : pieces) {
            for (Piece element2 : element)
            {
                if(element2!=null) {
                    thisPiece[index++] = element2;
                }
            }
        }


        do
        {
            //随机替换2个
            Random random = new Random();
            int index1 = random.nextInt(index-1);
            int index2 = index-1-index1;
            if(index1==index2)
                index2--;
           // System.out.println("index: "+index+" index1:"+index1+" index2:"+index2);
            PieceImage tmp = thisPiece[index1].getImage();
            thisPiece[index1].setImage(thisPiece[index2].getImage());
            thisPiece[index2].setImage(tmp);
        }
        while (!haveUsefulLinked());
        */
        //将二维数组转换为一维非空数组
        Piece[] thisPiece;
        int len = 0;
        // 计算一维数组长度
        for (Piece[] element : pieces) {
            len += element.length;
        }

        // 复制元素非空元素
        thisPiece = new Piece[len];
        int index = 0;
        for (Piece [] element : pieces) {
            for (Piece element2 : element)
            {
                if(element2!=null) {
                    thisPiece[index++] = element2;
                }
            }
        }
        //洗牌操作
        do {
            PieceImage tmp = thisPiece[0].getImage();
            for(int i=0;i<index-1;i++)
            {
                thisPiece[i].setImage(thisPiece[i+1].getImage());
            }
            thisPiece[index-1].setImage(tmp);
        }while (!haveUsefulLinked());
    }

    //返回连个可以互相连接的Piece,提示卡实现函数
    @Override
    public Piece[] getUsefulPieces()
    {
        Piece []  linked = new Piece[2];
        //将二维数组转换为一维数组
        Piece[] thisPiece;
        int len = 0;
        // 计算一维数组长度
        for (Piece[] element : pieces) {
            len += element.length;
        }

        // 复制元素
        thisPiece = new Piece[len];
        int index = 0;
        for (Piece [] element : pieces) {
            for (Piece element2 : element) {
                thisPiece[index++] = element2;
            }
        }

        //遍历是否存在连接的图像
        for(int i=0;i<len;i++)
            for(int j=i+1;j<len;j++)
            {
                if(thisPiece[i]!=null && thisPiece[j]!=null)
                {
                    LinkInfo linkInfo = link(thisPiece[i],thisPiece[j]);
                    if(linkInfo!=null)
                    {
                        linked[0]=thisPiece[i];
                        linked[1]=thisPiece[j];
                        return linked;
                    }
                }
            }


        return linked;
    }

    //移动Piece
    @Override
    public void movePieces(Piece piece1,Piece piece2)
    {
        int index = config.getPieceMoveType();
        switch (index)
        {
            case 1:
                downMovePieces(piece1,piece2);
                break;
            case 2:
                upMovePieces(piece1,piece2);
                break;
            case 3:
                leftMovePieces(piece1,piece2);
                break;
            case 4:
                rightMovePieces(piece1,piece2);
                break;
            case 5:
                midLeftRightMovePices(piece1,piece2);
                break;
            case 6:
                midUpDownMovePieces(piece1,piece2);
                break;
        }
    }

    //左移
    @Override
    public void leftMovePieces(Piece piece1,Piece piece2)
    {
        int index1X=piece1.getIndexX();
        int index1Y=piece1.getIndexY();
        if(piece2==null) //只消除一个
        {
            for (int i = index1X; i < config.getxSize()-1; i++) {
                //左移操作
                if (pieces[i + 1][index1Y] == null) {
                    pieces[i][index1Y] = null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i + 1][index1Y].getImage());
            }
            pieces[config.getxSize()-1][index1Y]=null;
            return;
        }




        int index2X=piece2.getIndexX();
        int index2Y=piece2.getIndexY();

        if(index1Y==index2Y) //同一列
        {
            int index = index1X<index2X ? index1X : index2X; //小
            int index2= index1X+index2X-index;//大
            for (int i = index; i < config.getxSize()-1; i++) { //小的先左移
                //左移操作
                if (pieces[i + 1][index1Y] == null) {
                    pieces[i][index1Y] = null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i + 1][index1Y].getImage());

            }
            pieces[config.getxSize()-1][index1Y]=null;
            for (int i = index2-1; i < config.getxSize()-1; i++) { //大的后左移
                //左移操作
                if (pieces[i + 1][index1Y] == null) {
                    pieces[i][index1Y] = null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i + 1][index1Y].getImage());
            }
            pieces[config.getxSize()-2][index1Y]=null;
        }
        else
        {
            for (int i = index1X; i < config.getxSize()-1; i++) {
                //左移操作
                if (pieces[i + 1][index1Y] == null) {
                    pieces[i][index1Y] = null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i + 1][index1Y].getImage());

            }
            for (int i = index2X; i < config.getxSize()-1; i++) {
                //左移操作
                if (pieces[i + 1][index2Y] == null) {
                    pieces[i][index2Y] = null;
                    break;
                }
                pieces[i][index2Y].setImage(pieces[i + 1][index2Y].getImage());
            }

            pieces[config.getxSize()-1][index1Y]=null;
            pieces[config.getxSize()-1][index2Y]=null;
        }
    }

    //右移
    @Override
    public void rightMovePieces(Piece piece1,Piece piece2)
    {
        int index1X=piece1.getIndexX();
        int index1Y=piece1.getIndexY();
        if(piece2==null) //只消除一个
        {
            for(int i=index1X;i>1;i--)
            {
                if(pieces[i-1][index1Y]==null)
                {
                    pieces[i][index1Y]=null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i-1][index1Y].getImage());
            }
            pieces[1][index1Y]=null;
            return;
        }


        int index2X=piece2.getIndexX();
        int index2Y=piece2.getIndexY();

        if(index1Y==index2Y) //同一列
        {
            int index = index1X>index2X ? index1X : index2X; //大
            int index2= index1X+index2X-index;//小
            //大的先右移动
            for(int i=index;i>1;i--)
            {
                if(pieces[i-1][index1Y]==null)
                {
                    pieces[i][index1Y]=null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i-1][index1Y].getImage());
            }
            pieces[1][index1Y]=null;
            //小的右移动
            for(int i=index2+1;i>1;i--)
            {
                if(pieces[i-1][index1Y]==null)
                {
                    pieces[i][index1Y]=null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i-1][index1Y].getImage());
            }
            pieces[2][index1Y]=null;
        }
        else
        {
            for(int i=index1X;i>1;i--)
            {
                if(pieces[i-1][index1Y]==null)
                {
                    pieces[i][index1Y]=null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i-1][index1Y].getImage());
            }
            for(int i=index2X;i>1;i--)
            {
                if(pieces[i-1][index2Y]==null)
                {
                    pieces[i][index2Y]=null;
                    break;
                }
                pieces[i][index2Y].setImage(pieces[i-1][index2Y].getImage());
            }

            pieces[1][index1Y]=null;
            pieces[1][index2Y]=null;

        }
    }

    //上移
    @Override
    public void upMovePieces(Piece piece1,Piece piece2)
    {
        int index1X=piece1.getIndexX();
        int index1Y=piece1.getIndexY();
        if(piece2==null) //只消除一个
        {
            for(int i= index1Y; i<config.getySize()-1;i++)
            {
                if(pieces[index1X][i+1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i+1].getImage());
            }
            pieces[index1X][config.getySize()-1]=null;
            return;
        }


        int index2X=piece2.getIndexX();
        int index2Y=piece2.getIndexY();


        if(index1X==index2X) //在同一列
        {
            int index = index1Y<index2Y?index1Y:index2Y;//小的
            int index2 = index1Y+index2Y-index; //大的
            //小的先上移
            for(int i= index; i<config.getySize()-1;i++)
            {
                if(pieces[index1X][i+1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i+1].getImage());
            }
            pieces[index1X][config.getySize()-1]=null;
            //大的后上移
            for(int i= index2-1; i<config.getySize()-1;i++)
            {
                if(pieces[index1X][i+1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i+1].getImage());
            }
            pieces[index1X][config.getySize()-2]=null;
        }
        else
        {
            for(int i= index1Y; i<config.getySize()-1;i++)
            {
                if(pieces[index1X][i+1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i+1].getImage());
            }
            for(int i= index2Y; i<config.getySize()-1;i++)
            {
                if(pieces[index2X][i+1]==null)
                {
                    pieces[index2X][i]=null;
                    break;
                }
                pieces[index2X][i].setImage(pieces[index2X][i+1].getImage());
            }
            pieces[index1X][config.getySize()-1]=null;
            pieces[index2X][config.getySize()-1]=null;
        }
    }

    //下移
    @Override
    public void downMovePieces(Piece piece1,Piece piece2)
    {
        int index1X=piece1.getIndexX();
        int index1Y=piece1.getIndexY();

        if(piece2==null) //只消除一个
        {
            for(int i=index1Y;i>1;i--)
            {
                if(pieces[index1X][i-1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            pieces[index1X][1]=null;
            return;
        }

        int index2X=piece2.getIndexX();
        int index2Y=piece2.getIndexY();


        if(index1X==index2X) //在同一列
        {
            int index = index1Y > index2Y ? index1Y : index2Y;//大的
            int index2 = index1Y + index2Y - index; //小的
            //大的先移动
            for(int i=index;i>1;i--)
            {
                if(pieces[index1X][i-1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            pieces[index1X][1]=null;
            //小的后移动
            for(int i=index2+1;i>1;i--)
            {
                if(pieces[index1X][i-1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            pieces[index1X][2]=null;
        }
        else
        {
            for(int i=index1Y;i>1;i--)
            {
                if(pieces[index1X][i-1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            for(int i=index2Y;i>1;i--)
            {
                if(pieces[index2X][i-1]==null)
                {
                    pieces[index2X][i]=null;
                    break;
                }
                pieces[index2X][i].setImage(pieces[index2X][i-1].getImage());
            }
            pieces[index1X][1]=null;
            pieces[index2X][1]=null;
        }
    }

    //中左右移
    @Override
    public void midLeftRightMovePices(Piece piece1,Piece piece2)
    {
        int index1X=piece1.getIndexX();
        int index1Y=piece1.getIndexY();
        int midLeft = (config.getxSize()-1)/2;
        int midRight = midLeft+1;

        if(piece2==null)//只消除一个
        {
            if(index1X<=midLeft)
            {
                rightMovePieces(piece1,null);
            }
            else
            {
                leftMovePieces(piece1,null);
            }
            return;
        }
        int index2X=piece2.getIndexX();
        int index2Y=piece2.getIndexY();
        if(index1X>index2X)
        {
            midLeftRightMovePices(piece2,piece1); //保证piece1的indexX小于piece2的indexX
            return;
        }
        //判断在左边还是右边

        if(index2X<=midLeft)
        {
            //都在左半边
            rightMovePieces(piece1,piece2);
        }
        else if (index1X<=midLeft && index2X>=midRight)
        {
            //piece1在左边,piece2在右边
            //对piece1所在列进行右移,piece2进行左移动
            for(int i=index1X;i>1;i--)
            {
                if(pieces[i-1][index1Y]==null)
                {
                    pieces[i][index1Y]=null;
                    break;
                }
                pieces[i][index1Y].setImage(pieces[i-1][index1Y].getImage());
            }
            pieces[1][index1Y]=null;

            for (int i = index2X; i < config.getxSize()-1; i++) {
                //左移操作
                if (pieces[i + 1][index2Y] == null) {
                    pieces[i][index2Y] = null;
                    break;
                }
                pieces[i][index2Y].setImage(pieces[i + 1][index2Y].getImage());
            }
            pieces[config.getxSize()-1][index2Y]=null;

        }
        else if(index1X>=midRight)
        {
            //都在右边
            leftMovePieces(piece1,piece2);
        }


    }

    //中上下移
    @Override
    public void midUpDownMovePieces(Piece piece1,Piece piece2)
    {
        int index1X=piece1.getIndexX();
        int index1Y=piece1.getIndexY();
        int midUp = (config.getySize()-1)/2;
        int midDown = midUp+1;

        if(piece2==null)//只消除一个
        {
            if(index1Y<=midUp)
            {
                downMovePieces(piece1,null);
            }
            else
            {
                upMovePieces(piece1,null);
            }
            return;
        }

        int index2X=piece2.getIndexX();
        int index2Y=piece2.getIndexY();

        if(index1Y>index2Y)
        {
            midUpDownMovePieces(piece2,piece1); //保证piece1的indexY要小于piece2的indexY
            return;
        }

        if(index2Y<=midUp)
        {
            //都在上半部分
            downMovePieces(piece1,piece2);
        }
        else if(index1Y<=midUp && index2Y>=midDown)
        {
            //piece1在上,piece2在下
            for(int i=index1Y;i>1;i--)
            {
                if(pieces[index1X][i-1]==null)
                {
                    pieces[index1X][i]=null;
                    break;
                }
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            pieces[index1X][1]=null;

            for(int i= index2Y; i<config.getySize()-1;i++)
            {
                if(pieces[index2X][i+1]==null)
                {
                    pieces[index2X][i]=null;
                    break;
                }
                pieces[index2X][i].setImage(pieces[index2X][i+1].getImage());
            }
            pieces[index2X][config.getySize()-1]=null;


        }
        else if(index1Y>=midDown)
        {
            //都在下半部分
            upMovePieces(piece1,piece2);
        }


    }

    //清除一类图标
    @Override
    public void clearOneKindPiece()
    {
        //普通模式
        //随机获取一个piece
        Piece clearPiece=new Piece(null,0,0,0,0);
        boolean continueSearch = true;
        for(int i=0;i<config.getxSize() && continueSearch;i++)
            for(int j=0;j<config.getySize() && continueSearch;j++)
            {
                if(pieces[i][j]!=null)
                {
                    clearPiece.setImage(pieces[i][j].getImage());
                    continueSearch=false;
                }
            }

        //清除所有相同的Piece
        for(int i=0;i<config.getxSize();i++) {
            for (int j = 0; j < config.getySize(); j++) {
                if (pieces[i][j] != null) {
                    if (clearPiece.isSameImage(pieces[i][j])) {
                        if(config.getPieceMoveType()==0) {
                            pieces[i][j] = null;
                        }else
                        {
                            clearOnePieceInMove(pieces[i][j]);
                            j--;
                        }

                    }
                }
            }
        }

    }

    //移动模式清除一个图标
    @Override
    public void clearOnePieceInMove(Piece piece)
    {
        int index = config.getPieceMoveType();
        switch (index) {
            case 1:
                downMovePieces(piece,null);
                break;
            case 2:
                upMovePieces(piece,null);
                break;
            case 3:
                leftMovePieces(piece,null);
                break;
            case 4:
                rightMovePieces(piece,null);
                break;
            case 5:
                midLeftRightMovePices(piece,null);
                break;
            case 6:
                midUpDownMovePieces(piece,null);
                break;
        }
    }

    //下移补满
    @Override
    public void downMovePiecesAndFilling(Piece piece1,Piece piece2)
    {
        int index1X=piece1.getIndexX();
        int index1Y=piece1.getIndexY();
        int index2X=piece2.getIndexX();
        int index2Y=piece2.getIndexY();


        if(index1X==index2X) //在同一列
        {
            int index = index1Y > index2Y ? index1Y : index2Y;//大的
            int index2 = index1Y + index2Y - index; //小的
            //大的先移动
            for(int i=index;i>1;i--)
            {
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            pieces[index1X][1].setImage(ImageUtil.getRandomPieceImage());
            //小的后移动
            for(int i=index2+1;i>1;i--)
            {
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            pieces[index1X][1].setImage(ImageUtil.getRandomPieceImage());
        }
        else
        {
            for(int i=index1Y;i>1;i--)
            {
                pieces[index1X][i].setImage(pieces[index1X][i-1].getImage());
            }
            for(int i=index2Y;i>1;i--)
            {
                pieces[index2X][i].setImage(pieces[index2X][i-1].getImage());
            }
            pieces[index1X][1].setImage(ImageUtil.getRandomPieceImage());
            pieces[index2X][1].setImage(ImageUtil.getRandomPieceImage());
        }
    }

    //下移特殊补满
    @Override
    public void downMoveSpecialPiecesAndFilling(Piece piece1,Piece piece2)
    {
        if(!piece1.isSpecial() && !piece2.isSpecial())
        {
            downMovePiecesAndFilling(piece1,piece2);
            return;
        }
        else if(piece1.isSpecial() && piece2.isSpecial())
        {
            return;
        }
        else
        {
            int indexX = piece1.isSpecial()? piece2.getIndexX():piece1.getIndexX();
            int indexY = piece1.isSpecial()? piece2.getIndexY():piece1.getIndexY();
            for(int i=indexY;i>1;i--)
            {
                pieces[indexX][i].setImage(pieces[indexX][i-1].getImage());
            }
            pieces[indexX][1].setImage(ImageUtil.getRandomPieceImage());
        }
    }

    //添加特殊Piece
    @Override
    public void addSpecialPieces(int num)
    {
        //将二维数组转换为一维非空数组
        Piece[] thisPiece;
        int len = 0;
        // 计算一维数组长度
        for (Piece[] element : pieces) {
            len += element.length;
        }

        // 复制元素非空元素
        thisPiece = new Piece[len];
        int index = 0;
        for (Piece [] element : pieces) {
            for (Piece element2 : element)
            {
                if(element2!=null) {
                    thisPiece[index++] = element2;
                }
            }
        }

        //随机设置3个特殊图片
        for(int i=0;i<num;i++)
        {
            Random random = new Random();
            int index1= random.nextInt(index-1);
            if(thisPiece[index1].isSpecial())
            {
                i--;
            }
            else
            {
                thisPiece[index1].setSpecial(true);
            }
        }

    }


    /**
     * 获取两个转折点的情况
     *
     * @param point1
     * @param point2
     * @return Map对象的每个key-value对代表一种连接方式，
     *   其中key、value分别代表第1个、第2个连接点
     */
    private Map<Point, Point> getLinkPoints(Point point1, Point point2,
                                            int pieceWidth, int pieceHeight)
    {
        Map<Point, Point> result = new HashMap<Point, Point>();
        // 获取以point1为中心的向上, 向右, 向下的通道
        List<Point> p1UpChanel = getUpChanel(point1, point2.y, pieceHeight);
        List<Point> p1RightChanel = getRightChanel(point1, point2.x, pieceWidth);
        List<Point> p1DownChanel = getDownChanel(point1, point2.y, pieceHeight);
        // 获取以point2为中心的向下, 向左, 向上的通道
        List<Point> p2DownChanel = getDownChanel(point2, point1.y, pieceHeight);
        List<Point> p2LeftChanel = getLeftChanel(point2, point1.x, pieceWidth);
        List<Point> p2UpChanel = getUpChanel(point2, point1.y, pieceHeight);
        // 获取Board的最大高度
        int heightMax = (this.config.getySize() + 1) * pieceHeight
                + this.config.getBeginImageY();
        // 获取Board的最大宽度
        int widthMax = (this.config.getxSize() + 1) * pieceWidth
                + this.config.getBeginImageX();
        // 先确定两个点的关系
        // point2在point1的左上角或者左下角
        if (isLeftUp(point1, point2) || isLeftDown(point1, point2))
        {
            // 参数换位, 调用本方法
            return getLinkPoints(point2, point1, pieceWidth, pieceHeight);
        }
        // p1、p2位于同一行不能直接相连
        if (point1.y == point2.y)
        {
            // 在同一行
            // 向上遍历
            // 以p1的中心点向上遍历获取点集合
            p1UpChanel = getUpChanel(point1, 0, pieceHeight);
            // 以p2的中心点向上遍历获取点集合
            p2UpChanel = getUpChanel(point2, 0, pieceHeight);
            Map<Point, Point> upLinkPoints = getXLinkPoints(p1UpChanel,
                    p2UpChanel, pieceHeight);
            // 向下遍历, 不超过Board(有方块的地方)的边框
            // 以p1中心点向下遍历获取点集合
            p1DownChanel = getDownChanel(point1, heightMax, pieceHeight);
            // 以p2中心点向下遍历获取点集合
            p2DownChanel = getDownChanel(point2, heightMax, pieceHeight);
            Map<Point, Point> downLinkPoints = getXLinkPoints(p1DownChanel,
                    p2DownChanel, pieceHeight);
            result.putAll(upLinkPoints);
            result.putAll(downLinkPoints);
        }
        // p1、p2位于同一列不能直接相连
        if (point1.x == point2.x)
        {
            // 在同一列
            // 向左遍历
            // 以p1的中心点向左遍历获取点集合
            List<Point> p1LeftChanel = getLeftChanel(point1, 0, pieceWidth);
            // 以p2的中心点向左遍历获取点集合
            p2LeftChanel = getLeftChanel(point2, 0, pieceWidth);
            Map<Point, Point> leftLinkPoints = getYLinkPoints(p1LeftChanel,
                    p2LeftChanel, pieceWidth);
            // 向右遍历, 不得超过Board的边框（有方块的地方）
            // 以p1的中心点向右遍历获取点集合
            p1RightChanel = getRightChanel(point1, widthMax, pieceWidth);
            // 以p2的中心点向右遍历获取点集合
            List<Point> p2RightChanel = getRightChanel(point2, widthMax,
                    pieceWidth);
            Map<Point, Point> rightLinkPoints = getYLinkPoints(p1RightChanel,
                    p2RightChanel, pieceWidth);
            result.putAll(leftLinkPoints);
            result.putAll(rightLinkPoints);
        }
        // point2位于point1的右上角
        if (isRightUp(point1, point2))
        {
            // 获取point1向上遍历, point2向下遍历时横向可以连接的点
            Map<Point, Point> upDownLinkPoints = getXLinkPoints(p1UpChanel,
                    p2DownChanel, pieceWidth);
            // 获取point1向右遍历, point2向左遍历时纵向可以连接的点
            Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(
                    p1RightChanel, p2LeftChanel, pieceHeight);
            // 获取以p1为中心的向上通道
            p1UpChanel = getUpChanel(point1, 0, pieceHeight);
            // 获取以p2为中心的向上通道
            p2UpChanel = getUpChanel(point2, 0, pieceHeight);
            // 获取point1向上遍历, point2向上遍历时横向可以连接的点
            Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChanel,
                    p2UpChanel, pieceWidth);
            // 获取以p1为中心的向下通道
            p1DownChanel = getDownChanel(point1, heightMax, pieceHeight);
            // 获取以p2为中心的向下通道
            p2DownChanel = getDownChanel(point2, heightMax, pieceHeight);
            // 获取point1向下遍历, point2向下遍历时横向可以连接的点
            Map<Point, Point> downDownLinkPoints = getXLinkPoints(p1DownChanel,
                    p2DownChanel, pieceWidth);
            // 获取以p1为中心的向右通道
            p1RightChanel = getRightChanel(point1, widthMax, pieceWidth);
            // 获取以p2为中心的向右通道
            List<Point> p2RightChanel = getRightChanel(point2, widthMax,
                    pieceWidth);
            // 获取point1向右遍历, point2向右遍历时纵向可以连接的点
            Map<Point, Point> rightRightLinkPoints = getYLinkPoints(
                    p1RightChanel, p2RightChanel, pieceHeight);
            // 获取以p1为中心的向左通道
            List<Point> p1LeftChanel = getLeftChanel(point1, 0, pieceWidth);
            // 获取以p2为中心的向左通道
            p2LeftChanel = getLeftChanel(point2, 0, pieceWidth);
            // 获取point1向左遍历, point2向右遍历时纵向可以连接的点
            Map<Point, Point> leftLeftLinkPoints = getYLinkPoints(p1LeftChanel,
                    p2LeftChanel, pieceHeight);
            result.putAll(upDownLinkPoints);
            result.putAll(rightLeftLinkPoints);
            result.putAll(upUpLinkPoints);
            result.putAll(downDownLinkPoints);
            result.putAll(rightRightLinkPoints);
            result.putAll(leftLeftLinkPoints);
        }
        // point2位于point1的右下角
        if (isRightDown(point1, point2))
        {
            // 获取point1向下遍历, point2向上遍历时横向可连接的点
            Map<Point, Point> downUpLinkPoints = getXLinkPoints(p1DownChanel,
                    p2UpChanel, pieceWidth);
            // 获取point1向右遍历, point2向左遍历时纵向可连接的点
            Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(
                    p1RightChanel, p2LeftChanel, pieceHeight);
            // 获取以p1为中心的向上通道
            p1UpChanel = getUpChanel(point1, 0, pieceHeight);
            // 获取以p2为中心的向上通道
            p2UpChanel = getUpChanel(point2, 0, pieceHeight);
            // 获取point1向上遍历, point2向上遍历时横向可连接的点
            Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChanel,
                    p2UpChanel, pieceWidth);
            // 获取以p1为中心的向下通道
            p1DownChanel = getDownChanel(point1, heightMax, pieceHeight);
            // 获取以p2为中心的向下通道
            p2DownChanel = getDownChanel(point2, heightMax, pieceHeight);
            // 获取point1向下遍历, point2向下遍历时横向可连接的点
            Map<Point, Point> downDownLinkPoints = getXLinkPoints(p1DownChanel,
                    p2DownChanel, pieceWidth);
            // 获取以p1为中心的向左通道
            List<Point> p1LeftChanel = getLeftChanel(point1, 0, pieceWidth);
            // 获取以p2为中心的向左通道
            p2LeftChanel = getLeftChanel(point2, 0, pieceWidth);
            // 获取point1向左遍历, point2向左遍历时纵向可连接的点
            Map<Point, Point> leftLeftLinkPoints = getYLinkPoints(p1LeftChanel,
                    p2LeftChanel, pieceHeight);
            // 获取以p1为中心的向右通道
            p1RightChanel = getRightChanel(point1, widthMax, pieceWidth);
            // 获取以p2为中心的向右通道
            List<Point> p2RightChanel = getRightChanel(point2, widthMax,
                    pieceWidth);
            // 获取point1向右遍历, point2向右遍历时纵向可以连接的点
            Map<Point, Point> rightRightLinkPoints = getYLinkPoints(
                    p1RightChanel, p2RightChanel, pieceHeight);
            result.putAll(downUpLinkPoints);
            result.putAll(rightLeftLinkPoints);
            result.putAll(upUpLinkPoints);
            result.putAll(downDownLinkPoints);
            result.putAll(leftLeftLinkPoints);
            result.putAll(rightRightLinkPoints);
        }
        return result;
    }

    /**
     * 获取p1和p2之间最短的连接信息
     *
     * @param p1
     * @param p2
     * @param turns 放转折点的map
     * @param shortDistance 两点之间的最短距离
     * @return p1和p2之间最短的连接信息
     */
    private LinkInfo getShortcut(Point p1, Point p2, Map<Point, Point> turns,
                                 int shortDistance)
    {
        List<LinkInfo> infos = new ArrayList<LinkInfo>();
        // 遍历结果Map,
        for (Point point1 : turns.keySet())
        {
            Point point2 = turns.get(point1);
            // 将转折点与选择点封装成LinkInfo对象, 放到List集合中
            infos.add(new LinkInfo(p1, point1, point2, p2));
        }
        return getShortcut(infos, shortDistance);
    }

    /**
     * 从infos中获取连接线最短的那个LinkInfo对象
     *
     * @param infos
     * @return 连接线最短的那个LinkInfo对象
     */
    private LinkInfo getShortcut(List<LinkInfo> infos, int shortDistance)
    {
        int temp1 = 0;
        LinkInfo result = null;
        for (int i = 0; i < infos.size(); i++)
        {
            LinkInfo info = infos.get(i);
            // 计算出几个点的总距离
            int distance = countAll(info.getLinkPoints());
            // 将循环第一个的差距用temp1保存
            if (i == 0)
            {
                temp1 = distance - shortDistance;
                result = info;
            }
            // 如果下一次循环的值比temp1的还小, 则用当前的值作为temp1
            if (distance - shortDistance < temp1)
            {
                temp1 = distance - shortDistance;
                result = info;
            }
        }
        return result;
    }

    /**
     * 计算List<Point>中所有点的距离总和
     *
     * @param points 需要计算的连接点
     * @return 所有点的距离的总和
     */
    private int countAll(List<Point> points)
    {
        int result = 0;
        for (int i = 0; i < points.size() - 1; i++)
        {
            // 获取第i个点
            Point point1 = points.get(i);
            // 获取第i + 1个点
            Point point2 = points.get(i + 1);
            // 计算第i个点与第i + 1个点的距离，并添加到总距离中
            result += getDistance(point1, point2);
        }
        return result;
    }

    /**
     * 获取两个LinkPoint之间的最短距离
     *
     * @param p1 第一个点
     * @param p2 第二个点
     * @return 两个点的距离距离总和
     */
    private int getDistance(Point p1, Point p2)
    {
        int xDistance = Math.abs(p1.x - p2.x);
        int yDistance = Math.abs(p1.y - p2.y);
        return xDistance + yDistance;
    }

    /**
     * 遍历两个集合, 先判断第一个集合的元素的x座标与另一个集合中的元素x座标相同(纵向),
     * 如果相同, 即在同一列, 再判断是否有障碍, 没有则加到结果的Map中去
     *
     * @param p1Chanel
     * @param p2Chanel
     * @param pieceHeight
     * @return
     */
    private Map<Point, Point> getYLinkPoints(List<Point> p1Chanel,
                                             List<Point> p2Chanel, int pieceHeight)
    {
        Map<Point, Point> result = new HashMap<Point, Point>();
        for (int i = 0; i < p1Chanel.size(); i++)
        {
            Point temp1 = p1Chanel.get(i);
            for (int j = 0; j < p2Chanel.size(); j++)
            {
                Point temp2 = p2Chanel.get(j);
                // 如果x座标相同(在同一列)
                if (temp1.x == temp2.x)
                {
                    // 没有障碍, 放到map中去
                    if (!isYBlock(temp1, temp2, pieceHeight))
                    {
                        result.put(temp1, temp2);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 遍历两个集合, 先判断第一个集合的元素的y座标与另一个集合中的元素y座标相同(横向),
     * 如果相同, 即在同一行, 再判断是否有障碍, 没有 则加到结果的map中去
     *
     * @param p1Chanel
     * @param p2Chanel
     * @param pieceWidth
     * @return 存放可以横向直线连接的连接点的键值对
     */
    private Map<Point, Point> getXLinkPoints(List<Point> p1Chanel,
                                             List<Point> p2Chanel, int pieceWidth)
    {
        Map<Point, Point> result = new HashMap<Point, Point>();
        for (int i = 0; i < p1Chanel.size(); i++)
        {
            // 从第一通道中取一个点
            Point temp1 = p1Chanel.get(i);
            // 再遍历第二个通道, 看下第二通道中是否有点可以与temp1横向相连
            for (int j = 0; j < p2Chanel.size(); j++)
            {
                Point temp2 = p2Chanel.get(j);
                // 如果y座标相同(在同一行), 再判断它们之间是否有直接障碍
                if (temp1.y == temp2.y)
                {
                    if (!isXBlock(temp1, temp2, pieceWidth))
                    {
                        // 没有障碍则直接加到结果的map中
                        result.put(temp1, temp2);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 判断point2是否在point1的左上角
     *
     * @param point1
     * @param point2
     * @return p2位于p1的左上角时返回true，否则返回false
     */
    private boolean isLeftUp(Point point1, Point point2)
    {
        return (point2.x < point1.x && point2.y < point1.y);
    }

    /**
     * 判断point2是否在point1的左下角
     *
     * @param point1
     * @param point2
     * @return p2位于p1的左下角时返回true，否则返回false
     */
    private boolean isLeftDown(Point point1, Point point2)
    {
        return (point2.x < point1.x && point2.y > point1.y);
    }

    /**
     * 判断point2是否在point1的右上角
     *
     * @param point1
     * @param point2
     * @return p2位于p1的右上角时返回true，否则返回false
     */
    private boolean isRightUp(Point point1, Point point2)
    {
        return (point2.x > point1.x && point2.y < point1.y);
    }

    /**
     * 判断point2是否在point1的右下角
     *
     * @param point1
     * @param point2
     * @return p2位于p1的右下角时返回true，否则返回false
     */
    private boolean isRightDown(Point point1, Point point2)
    {
        return (point2.x > point1.x && point2.y > point1.y);
    }

    /**
     * 获取两个不在同一行或者同一列的座标点的直角连接点, 即只有一个转折点
     *
     * @param point1 第一个点
     * @param point2 第二个点
     * @return 两个不在同一行或者同一列的座标点的直角连接点
     */
    private Point getCornerPoint(Point point1, Point point2, int pieceWidth,
                                 int pieceHeight)
    {
        // 先判断这两个点的位置关系
        // point2在point1的左上角, point2在point1的左下角
        if (isLeftUp(point1, point2) || isLeftDown(point1, point2))
        {
            // 参数换位, 重新调用本方法
            return getCornerPoint(point2, point1, pieceWidth, pieceHeight);
        }
        // 获取p1向右, 向上, 向下的三个通道
        List<Point> point1RightChanel = getRightChanel(point1, point2.x,
                pieceWidth);
        List<Point> point1UpChanel = getUpChanel(point1, point2.y, pieceHeight);
        List<Point> point1DownChanel = getDownChanel(point1, point2.y,
                pieceHeight);
        // 获取p2向下, 向左, 向下的三个通道
        List<Point> point2DownChanel = getDownChanel(point2, point1.y,
                pieceHeight);
        List<Point> point2LeftChanel = getLeftChanel(point2, point1.x,
                pieceWidth);
        List<Point> point2UpChanel = getUpChanel(point2, point1.y, pieceHeight);
        if (isRightUp(point1, point2))
        {
            // point2在point1的右上角
            // 获取p1向右和p2向下的交点
            Point linkPoint1 = getWrapPoint(point1RightChanel, point2DownChanel);
            // 获取p1向上和p2向左的交点
            Point linkPoint2 = getWrapPoint(point1UpChanel, point2LeftChanel);
            // 返回其中一个交点, 如果没有交点, 则返回null
            return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
        }
        if (isRightDown(point1, point2))
        {
            // point2在point1的右下角
            // 获取p1向下和p2向左的交点
            Point linkPoint1 = getWrapPoint(point1DownChanel, point2LeftChanel);
            // 获取p1向右和p2向下的交点
            Point linkPoint2 = getWrapPoint(point1RightChanel, point2UpChanel);
            return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
        }
        return null;
    }

    /**
     * 遍历两个通道, 获取它们的交点
     *
     * @param p1Chanel 第一个点的通道
     * @param p2Chanel 第二个点的通道
     * @return 两个通道有交点，返回交点，否则返回null
     */
    private Point getWrapPoint(List<Point> p1Chanel, List<Point> p2Chanel)
    {
        for (int i = 0; i < p1Chanel.size(); i++)
        {
            Point temp1 = p1Chanel.get(i);
            for (int j = 0; j < p2Chanel.size(); j++)
            {
                Point temp2 = p2Chanel.get(j);
                if (temp1.equals(temp2))
                {
                    // 如果两个List中有元素有同一个, 表明这两个通道有交点
                    return temp1;
                }
            }
        }
        return null;
    }

    /**
     * 判断两个y座标相同的点对象之间是否有障碍, 以p1为中心向右遍历
     *
     * @param p1
     * @param p2
     * @param pieceWidth
     * @return 两个Piece之间有障碍返回true，否则返回false
     */
    private boolean isXBlock(Point p1, Point p2, int pieceWidth)
    {
        if (p2.x < p1.x)
        {
            // 如果p2在p1左边, 调换参数位置调用本方法
            return isXBlock(p2, p1, pieceWidth);
        }
        for (int i = p1.x + pieceWidth; i < p2.x; i = i + pieceWidth)
        {
            if (hasPiece(i, p1.y))
            {// 有障碍
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个x座标相同的点对象之间是否有障碍, 以p1为中心向下遍历
     *
     * @param p1
     * @param p2
     * @param pieceHeight
     * @return 两个Piece之间有障碍返回true，否则返回false
     */
    private boolean isYBlock(Point p1, Point p2, int pieceHeight)
    {
        if (p2.y < p1.y)
        {
            // 如果p2在p1的上面, 调换参数位置重新调用本方法
            return isYBlock(p2, p1, pieceHeight);
        }
        for (int i = p1.y + pieceHeight; i < p2.y; i = i + pieceHeight)
        {
            if (hasPiece(p1.x, i))
            {
                // 有障碍
                return true;
            }
        }
        return false;
    }

    /**
     * 判断GamePanel中的x, y座标中是否有Piece对象
     *
     * @param x
     * @param y
     * @return true 表示有该座标有piece对象 false 表示没有
     */
    private boolean hasPiece(int x, int y)
    {
        return findPiece(x, y) != null;
    }

    /**
     * 给一个Point对象,返回它的左边通道
     *
     * @param p
     * @param pieceWidth piece图片的宽
     * @param min 向左遍历时最小的界限
     * @return 给定Point左边的通道
     */
    private List<Point> getLeftChanel(Point p, int min, int pieceWidth)
    {
        List<Point> result = new ArrayList<Point>();
        // 获取向左通道, 由一个点向左遍历, 步长为Piece图片的宽
        for (int i = p.x - pieceWidth; i >= min
                ; i = i - pieceWidth)
        {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(i, p.y))
            {
                return result;
            }
            result.add(new Point(i, p.y));
        }
        return result;
    }

    /**
     * 给一个Point对象, 返回它的右边通道
     *
     * @param p
     * @param pieceWidth
     * @param max 向右时的最右界限
     * @return 给定Point右边的通道
     */
    private List<Point> getRightChanel(Point p, int max, int pieceWidth)
    {
        List<Point> result = new ArrayList<Point>();
        // 获取向右通道, 由一个点向右遍历, 步长为Piece图片的宽
        for (int i = p.x + pieceWidth; i <= max
                ; i = i + pieceWidth)
        {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(i, p.y))
            {
                return result;
            }
            result.add(new Point(i, p.y));
        }
        return result;
    }

    /**
     * 给一个Point对象, 返回它的上面通道
     *
     * @param p
     * @param min 向上遍历时最小的界限
     * @param pieceHeight
     * @return 给定Point上面的通道
     */
    private List<Point> getUpChanel(Point p, int min, int pieceHeight)
    {
        List<Point> result = new ArrayList<Point>();
        // 获取向上通道, 由一个点向右遍历, 步长为Piece图片的高
        for (int i = p.y - pieceHeight; i >= min
                ; i = i - pieceHeight)
        {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(p.x, i))
            {
                // 如果遇到障碍, 直接返回
                return result;
            }
            result.add(new Point(p.x, i));
        }
        return result;
    }

    /**
     * 给一个Point对象, 返回它的下面通道
     *
     * @param p
     * @param max 向上遍历时的最大界限
     * @return 给定Point下面的通道
     */
    private List<Point> getDownChanel(Point p, int max, int pieceHeight)
    {
        List<Point> result = new ArrayList<Point>();
        // 获取向下通道, 由一个点向右遍历, 步长为Piece图片的高
        for (int i = p.y + pieceHeight; i <= max
                ; i = i + pieceHeight)
        {
            // 遇到障碍, 表示通道已经到尽头, 直接返回
            if (hasPiece(p.x, i))
            {
                // 如果遇到障碍, 直接返回
                return result;
            }
            result.add(new Point(p.x, i));
        }
        return result;
    }

}
