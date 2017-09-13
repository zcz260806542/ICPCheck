package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestJavaMail {

    public static void main(String[] args) {
        
        MailBean mb = new MailBean();
        mb.setHost("smtp.exmail.qq.com");                        // 设置SMTP主机(163)，若用126，则设为：smtp.126.com
        mb.setUsername("monitoring@webpowerchina.com");                // 设置发件人邮箱的用户名
        mb.setPassword("Time0609");                        // 设置发件人邮箱的密码，需将*号改成正确的密码
        mb.setFrom("monitoring@webpowerchina.com");            // 设置发件人的邮箱
        mb.setTo("260806542@qq.com");                // 设置收件人的邮箱
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        
        
        mb.setSubject("ICP备案监测        "+df.format(new Date()));                    // 设置邮件的主题
        mb.setContent("testestsetsetet");        // 设置邮件的正文

       // mb.attachFile("c:/1.csv");            // 往邮件中添加附件
//        mb.attachFile("E:/test.txt");
//        mb.attachFile("E:/test.xls");
        
        SendMail sm = new SendMail();
        System.out.println("正在发送邮件...");
        
        if(sm.sendMail(mb))                                // 发送邮件
            System.out.println("发送成功!");
        else
            System.out.println("发送失败!");
    }
    
}