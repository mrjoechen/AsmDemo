package com.chenqiao.asmdemo;

import android.util.Log;

/**
 * Created by chenqiao on 2019-12-14.
 * e-mail : mrjctech@gmail.com
 */
public class TestClass {

    void printTest(){
        Log.i("TAG", "-------> onCreate : " + this.getClass().getSimpleName());
    }
}
