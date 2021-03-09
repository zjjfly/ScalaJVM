package com.github.zjjfly.sj.ch4;

/**
 * @author zjjfly[https://github.com/zjjfly]
 * @date 2021/2/10
 */
abstract class ToOverride {
    public abstract String getText();
}

class Outer {
    private String text;

    public Outer(String text) {
        this.text = text;
    }

    ToOverride getOverriden() {
        //匿名内部类,这个类内部会有一个字段存储调用该方法的Outer对象,这也可能造成内存泄露
        return new ToOverride() {
            @Override
            public String getText() {
                return text;
            }
        };
    }
}
