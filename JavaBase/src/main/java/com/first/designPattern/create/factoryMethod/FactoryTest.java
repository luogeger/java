package com.first.designPattern.create.factoryMethod;

public class FactoryTest {
    // 发送 mail 和 msg 两种不同类型
    public static void main(String[] args) {
        SendFactory sf = new SendFactory();
        sf.send("sms");
        sf.send("mail");
    }
}