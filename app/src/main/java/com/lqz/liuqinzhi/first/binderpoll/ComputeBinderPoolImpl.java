package com.lqz.liuqinzhi.first.binderpoll;

import android.os.RemoteException;

import com.lqz.liuqinzhi.first.ICompute_binder_pool;

/**
 * Created by liu.qinzhi on 2017/11/23.
 */

public class ComputeBinderPoolImpl extends ICompute_binder_pool.Stub {

    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
}
