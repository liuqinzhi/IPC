// ISecurityCenter_binder_poll.aidl
package com.lqz.liuqinzhi.first;

// Declare any non-default types here with import statements

interface ISecurityCenter_binder_pool {

    String encrypt(String content);
    String decrypt(String password);
}
