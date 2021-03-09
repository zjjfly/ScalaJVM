package com.github.zjjfly.sj.ch4;

/**
 * @author zjjfly[https://github.com/zjjfly]
 * @date 2021/2/10
 */
public class Parent {

    private String bigString;

    public Parent(String bigString) {
        this.bigString = bigString;
    }

    public class Child {
    }

    /**
     * 内部类Child类不是static的,所以Child对象会持有生成它的Parent对象的引用
     * 这也算一种内存泄露
     */
    public Child spawnChild() {
        //从Child内部访问生成它的Parent对象的方式
        Parent parent = Parent.this;
        return new Child();
    }

    public static void main(String[] args) {
        Parent foo = new Parent("foo");
        //从外部初始化Child的方式
        Child child = foo.new Child();
    }
}
