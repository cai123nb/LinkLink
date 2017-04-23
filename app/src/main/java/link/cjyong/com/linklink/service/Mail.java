package link.cjyong.com.linklink.service;

/**
 * Created by cjyong on 2017/4/8.
 * 用于Android发送邮件的工具类Mail
 */

import java.util.Date;
import java.util.Properties;
import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;


public class Mail extends javax.mail.Authenticator {
    private String _user;       //邮箱服务器的账号
    private String _pass;       //邮箱服务器的密码,不是登录客户端的密码,而是开放客户端发送邮件的密码

    private String[] _to;       //收件人邮件地址,使用数组可以保存多人收件地址,进行群发
    private String _from;       //发件人的邮箱地址

    private String _port;       //邮件发送所使用的的端口号
    private String _sport;      //处理邮件所使用的端口号

    private String _host;       //邮件服务器的名称

    private String _subject;    //邮件的标题
    private String _body;       //邮件的内容

    private boolean _auth;      //邮件的授权

    private boolean _debuggable;    //是否可以进行调试

    private Multipart _multipart;   //存储邮件携带的附件

    /**
     * Mail的默认构造函数,用于设置Mail类的一些基本信息
     */
    public Mail() {
        _host = "smtp.163.com"; // default 163 server
        _port = "465"; // default smtp port
        _sport = "465"; // default socketfactory port

        _user = ""; // username
        _pass = ""; // password
        _from = ""; // email sent from
        _subject = ""; // email subject
        _body = ""; // email body

        _debuggable = false; // debug mode on or off - default off
        _auth = true; // smtp authentication - default on

        _multipart = new MimeMultipart();

        // There is something wrong with MailCap, javamail can not find a handler for the multipart/mixed part, so this bit needs to be added.
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    /**
     * 用户名和密码的Mail构造函数,用于设置Mail的邮件服务器的账号和密码
     * @param user
     * @param pass
     */
    public Mail(String user, String pass) {
        this();

        _user = user;
        _pass = pass;
    }

    /**
     * 发用邮件主方法
     * @return 邮件是否发送成功
     * @throws Exception
     */
    public boolean send() throws Exception {
        Properties props = _setProperties();        //对邮件携带的属性进行初始化赋值

        if(!_user.equals("") && !_pass.equals("") && _to.length > 0 && !_from.equals("") && !_subject.equals("") && !_body.equals("")) {
            Session session = Session.getInstance(props, this);     //获取一个Session的实例

            MimeMessage msg = new MimeMessage(session);             //创建消息对象

            msg.setFrom(new InternetAddress(_from));                //设置消息来源

            InternetAddress[] addressTo = new InternetAddress[_to.length];  //构造收件人的地址
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);     //设置消息的接受方

            msg.setSubject(_subject);           //设置消息的标题
            msg.setSentDate(new Date());        //设置消息发送的日期

            // setup message body
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);

            // Put parts in message
            msg.setContent(_multipart);

            // send email
            Transport.send(msg);

            return true;
        } else {
            return false;
        }
    }

    /**
     * 在邮件上添加附件
     * @param filename
     * @throws Exception
     */
    public void addAttachment(String filename) throws Exception {
        BodyPart messageBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);

        _multipart.addBodyPart(messageBodyPart);
    }


    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    /**
     * 设置邮件发送所需要的一些属性
     * @return
     */
    private Properties _setProperties() {
        Properties props = new Properties();

        props.put("mail.smtp.host", _host);

        if(_debuggable) {
            props.put("mail.debug", "true");
        }

        if(_auth) {
            props.put("mail.smtp.auth", "true");
        }

        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.socketFactory.port", _sport);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");

        return props;
    }

    // getters and setters
    public String getBody() {
        return _body;
    }

    public void setBody(String _body) {
        this._body = _body;
    }

    public String get_user() {
        return _user;
    }

    public void set_user(String _user) {
        this._user = _user;
    }

    public String get_pass() {
        return _pass;
    }

    public void set_pass(String _pass) {
        this._pass = _pass;
    }

    public String[] get_to() {
        return _to;
    }

    public void set_to(String[] _to) {
        this._to = _to;
    }

    public String get_from() {
        return _from;
    }

    public void set_from(String _from) {
        this._from = _from;
    }

    public String get_port() {
        return _port;
    }

    public void set_port(String _port) {
        this._port = _port;
    }

    public String get_sport() {
        return _sport;
    }

    public void set_sport(String _sport) {
        this._sport = _sport;
    }

    public String get_host() {
        return _host;
    }

    public void set_host(String _host) {
        this._host = _host;
    }

    public String get_subject() {
        return _subject;
    }

    public void set_subject(String _subject) {
        this._subject = _subject;
    }

    public String get_body() {
        return _body;
    }

    public void set_body(String _body) {
        this._body = _body;
    }

    public boolean is_auth() {
        return _auth;
    }

    public void set_auth(boolean _auth) {
        this._auth = _auth;
    }

    public boolean is_debuggable() {
        return _debuggable;
    }

    public void set_debuggable(boolean _debuggable) {
        this._debuggable = _debuggable;
    }

    public Multipart get_multipart() {
        return _multipart;
    }

    public void set_multipart(Multipart _multipart) {
        this._multipart = _multipart;
    }

}
