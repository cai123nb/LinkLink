package link.cjyong.com.linklink.service;

import android.util.Log;

/**
 * Created by cjyong on 2017/4/7.
 * 用于发送邮件的线程类
 */

public class SendMailThread extends Thread {
    public enum MailType{registerMail,feedbackMail}     //发用邮件的种类,后期可以进行拓展
    private String recipientAddress;                            //邮件接受人的地址
    private String username;                                    //邮件接收人的名字
    private String feedback;                                    //邮件反馈的内容
    private String activatedCode;                               //邮件的激活码
    private MailType mailType;                                  //邮件的种类

    /**
     * 带参数的构造函数,对该线程类内部成员进行赋值,一般用于注册操作
     * @param username
     * @param recipientAddress
     * @param activatedCode
     * @param mailType
     */
    public SendMailThread(String username,String recipientAddress,String activatedCode,MailType mailType){
        this.username=username;
        this.recipientAddress=recipientAddress;
        this.activatedCode=activatedCode;
        this.mailType=mailType;
    }

    /**
     * 带参数的构造函数,对该线程类内部成员进行赋值,一般用于反馈操作
     * @param username
     * @param feedback
     * @param mailType
     */
    public SendMailThread(String username,String feedback,MailType mailType)
    {
        this.username = username;
        this.feedback = feedback;
        this.mailType = mailType;
    }

    /**
     * 线程重载函数,用于发送邮件的操作
     */
    public void run(){
        if(mailType==MailType.registerMail) {
            //注册邮件的发送
            Mail m = new Mail("18375637632@163.com", "cai123nb");
            String[] toArr = {recipientAddress};
            m.set_to(toArr);
            m.set_from("18375637632@163.com");
            m.set_subject("LinkLink游戏注册成功");
            m.setBody("恭喜您: " + username + " 注册LinkLink游戏成功,你的激活码是: " + activatedCode + " \n请及时查收");
            try {
                m.send();
            } catch (Exception e) {
                Log.e("MailApp", "Could not send email", e);
            }

            //进行汇总统计
            Mail countM = new Mail("18375637632@163.com", "cai123nb");
            String[] toAddr = {"2686600303@qq.com"};
            countM.set_to(toAddr);
            countM.set_from("18375637632@163.com");
            countM.set_subject("LinkLink游戏用户"+username+"注册成功");
            countM.setBody("LinkLink 有新用户: "+username+"注册成功!");
            try
            {
                countM.send();
            }
            catch (Exception e)
            {
                Log.e("MailApp", "Could not send email", e);
            }
        }
        else if(mailType==MailType.feedbackMail)
        {
            //反馈邮件的发送
            Mail m = new Mail("18375637632@163.com", "cai123nb");
            String[] toAddr = {"2686600303@qq.com"};
            m.set_to(toAddr);
            m.set_from("18375637632@163.com");
            m.set_subject("LinkLink游戏用户"+username+"进行了反馈");
            m.setBody("LinkLink 用户"+username+"反馈内容: "+feedback);
            try
            {
                m.send();
            }
            catch (Exception e)
            {
                Log.e("MailApp", "Could not send email", e);
            }
        }
    }
}
