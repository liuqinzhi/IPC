package com.lqz.liuqinzhi.first.binderpoll;

import android.os.RemoteException;

import com.lqz.liuqinzhi.first.ISecurityCenter_binder_pool;

public class SecurityCenterBinderPoolImpl extends ISecurityCenter_binder_pool.Stub {

    private static final char SECRET_CODE = '^';

    @Override
    public String encrypt(String content) throws RemoteException {

        char[] chars = content.toCharArray();
        for(int i = 0; i < chars.length; i++) {
            chars[i] ^= SECRET_CODE;
        }
        return new String(chars);
    }

    @Override
    public String decrypt(String password) throws RemoteException {
        return encrypt(password);
    }
}
