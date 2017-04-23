package link.cjyong.com.linklink.element;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by cjyong on 2017/3/23.
 * 用于配置游戏相关属性类
 */

public class GameConf implements Parcelable
{
    // 设置连连看的每个方块的图片的宽、高
    public static final int PIECE_WIDTH = 120;
    public static final int PIECE_HEIGHT = 120;
    //记录关卡的标志
    private String barrierID;
    // Piece[][]数组第一维的长度
    private int xSize;
    // Piece[][]数组第二维的长度
    private int ySize;
    // Board中第一张图片出现的x座标
    private int beginImageX;
    // Board中第一张图片出现的y座标
    private int beginImageY;
    // 记录游戏的总时间, 单位是秒
    private int gameTime;
    // 记录关卡的评分标准
    private String taskRequirements;
    // 记录关卡的类型
    private int taskKind;
    // 记录各类道具最大使用次数
    private int timeTool;   //加时卡
    private int helpTool;   //提示卡
    private int boomTool;   //爆炸卡
    private int resetTool;  //重置卡
    //记录关卡的移动类型,默认为0:不移动.另外1:下移,2:上移,3:左移,4:右移,5:中间左右移动,6:中间上下移动
    private int pieceMoveType=0;

    public GameConf(){}

    /**
     * 带参数的构造函数,对GameConf进行初始化
     * @param barrierID
     * @param xSize
     * @param ySize
     * @param beginImageX
     * @param beginImageY
     * @param gameTime
     * @param taskRequirements
     * @param taskKind
     * @param timeTool
     * @param helpTool
     * @param boomTool
     * @param resetTool
     */
    public GameConf(String barrierID,int xSize, int ySize, int beginImageX,
                    int beginImageY, int gameTime, String taskRequirements,int taskKind,int timeTool,int helpTool,int boomTool,int resetTool)
    {
        this.barrierID = barrierID;
        this.xSize = xSize;
        this.ySize = ySize;
        this.beginImageX = beginImageX;
        this.beginImageY = beginImageY;
        this.gameTime = gameTime;
        this.taskRequirements =  taskRequirements;
        this.taskKind = taskKind;
        this.timeTool = timeTool;
        this.helpTool = helpTool;
        this.boomTool = boomTool;
        this.resetTool = resetTool;
    }

    /**
     * 带参数的构造函数,对GameConf进行初始化,额外添加了PieceMoveType用于第二大关的使用
     * @param barrierID
     * @param xSize
     * @param ySize
     * @param beginImageX
     * @param beginImageY
     * @param gameTime
     * @param taskRequirements
     * @param taskKind
     * @param timeTool
     * @param helpTool
     * @param boomTool
     * @param resetTool
     * @param pieceMoveType
     */
    public GameConf(String barrierID, int xSize, int ySize, int beginImageX, int beginImageY, int gameTime, String taskRequirements, int taskKind, int timeTool, int helpTool, int boomTool, int resetTool, int pieceMoveType) {
        this.barrierID = barrierID;
        this.xSize = xSize;
        this.ySize = ySize;
        this.beginImageX = beginImageX;
        this.beginImageY = beginImageY;
        this.gameTime = gameTime;
        this.taskRequirements = taskRequirements;
        this.taskKind = taskKind;
        this.timeTool = timeTool;
        this.helpTool = helpTool;
        this.boomTool = boomTool;
        this.resetTool = resetTool;
        this.pieceMoveType = pieceMoveType;
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(barrierID);
        dest.writeInt(xSize);
        dest.writeInt(ySize);
        dest.writeInt(beginImageX);
        dest.writeInt(beginImageY);
        dest.writeInt(gameTime);
        dest.writeString(taskRequirements);
        dest.writeInt(taskKind);
        dest.writeInt(timeTool);
        dest.writeInt(helpTool);
        dest.writeInt(boomTool);
        dest.writeInt(resetTool);
        dest.writeInt(pieceMoveType);
    }
    public static final Parcelable.Creator<GameConf> CREATOR = new Creator<GameConf>() {
        @Override
        public GameConf createFromParcel(Parcel source) {
            GameConf gameConf = new GameConf();
            gameConf.barrierID = source.readString();
            gameConf.xSize = source.readInt();
            gameConf.ySize = source.readInt();
            gameConf.beginImageX = source.readInt();
            gameConf.beginImageY = source.readInt();
            gameConf.gameTime = source.readInt();
            gameConf.taskRequirements = source.readString();
            gameConf.taskKind = source.readInt();
            gameConf.timeTool = source.readInt();
            gameConf.helpTool = source.readInt();
            gameConf.boomTool = source.readInt();
            gameConf.resetTool = source.readInt();
            gameConf.pieceMoveType = source.readInt();
            return gameConf;
        }
        @Override
        public GameConf[] newArray(int size) {
            return new GameConf[size];
        }
    };

    //Getter and Setter
    public String getBarrierID() {
        return barrierID;
    }

    public void setBarrierID(String barrierID) {
        this.barrierID = barrierID;
    }

    public int getxSize() {
        return xSize;
    }

    public void setxSize(int xSize) {
        this.xSize = xSize;
    }

    public int getySize() {
        return ySize;
    }

    public void setySize(int ySize) {
        this.ySize = ySize;
    }

    public int getBeginImageX() {
        return beginImageX;
    }

    public void setBeginImageX(int beginImageX) {
        this.beginImageX = beginImageX;
    }

    public int getBeginImageY() {
        return beginImageY;
    }

    public void setBeginImageY(int beginImageY) {
        this.beginImageY = beginImageY;
    }

    public int getGameTime() {
        return gameTime;
    }

    public void setGameTime(int gameTime) {
        this.gameTime = gameTime;
    }

    public String getTaskRequirements() {
        return taskRequirements;
    }

    public void setTaskRequirements(String taskRequirements) {
        this.taskRequirements = taskRequirements;
    }

    public int getTaskKind()
    {
        return taskKind;
    }

    public void setTaskKind(int taskKind)
    {
        this.taskKind = taskKind;
    }

    public int getTimeTool() {
        return timeTool;
    }

    public void setTimeTool(int timeTool) {
        this.timeTool = timeTool;
    }

    public int getHelpTool() {
        return helpTool;
    }

    public void setHelpTool(int helpTool) {
        this.helpTool = helpTool;
    }

    public int getBoomTool() {
        return boomTool;
    }

    public void setBoomTool(int boomTool) {
        this.boomTool = boomTool;
    }

    public int getResetTool() {
        return resetTool;
    }

    public void setResetTool(int resetTool) {
        this.resetTool = resetTool;
    }

    public int getPieceMoveType() {
        return pieceMoveType;
    }

    public void setPieceMoveType(int pieceMoveType) {
        this.pieceMoveType = pieceMoveType;
    }

}
