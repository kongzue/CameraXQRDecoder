package com.kongzue.cameraxqrdecoder.interfaces;

/**
 * @author: Kongzue
 * @github: https://github.com/kongzue/
 * @homepage: http://kongzue.com/
 * @mail: myzcxhh@live.cn
 * @createTime: 2021/8/28 9:41
 */
public abstract class OnWorkFinish<D> {
    public abstract void finish(D d);
    public void failed(Object e){};
    public void close(){};
}
