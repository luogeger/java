package com.first.design.pattern.structure.proxy.z;

public class TestImpl implements Image{
    // 实例化的时候，重写方法会不会执行
    @Override
    public void display() {
        System.out.println(8081);
    }
}
