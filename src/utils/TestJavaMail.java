package utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TestJavaMail {

    public static void main(String[] args) {
        
        MailBean mb = new MailBean();
        mb.setHost("smtp.exmail.qq.com");                        // ����SMTP����(163)������126������Ϊ��smtp.126.com
        mb.setUsername("monitoring@webpowerchina.com");                // ���÷�����������û���
        mb.setPassword("Time0609");                        // ���÷�������������룬�轫*�Ÿĳ���ȷ������
        mb.setFrom("monitoring@webpowerchina.com");            // ���÷����˵�����
        mb.setTo("260806542@qq.com");                // �����ռ��˵�����
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
        
        
        mb.setSubject("ICP�������        "+df.format(new Date()));                    // �����ʼ�������
        mb.setContent("testestsetsetet");        // �����ʼ�������

       // mb.attachFile("c:/1.csv");            // ���ʼ�����Ӹ���
//        mb.attachFile("E:/test.txt");
//        mb.attachFile("E:/test.xls");
        
        SendMail sm = new SendMail();
        System.out.println("���ڷ����ʼ�...");
        
        if(sm.sendMail(mb))                                // �����ʼ�
            System.out.println("���ͳɹ�!");
        else
            System.out.println("����ʧ��!");
    }
    
}