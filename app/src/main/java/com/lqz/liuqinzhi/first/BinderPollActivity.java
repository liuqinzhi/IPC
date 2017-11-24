package com.lqz.liuqinzhi.first;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.lqz.liuqinzhi.first.binderpoll.BinderPool;
import com.lqz.liuqinzhi.first.binderpoll.ComputeBinderPoolImpl;
import com.lqz.liuqinzhi.first.binderpoll.SecurityCenterBinderPoolImpl;

public class BinderPollActivity extends Activity {

    private static final String TAG = "BinderPollActivity";
    private BinderPool mBinderPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_binder_poll);

        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
            }
        }).start();

    }

    private void doWork() {

        mBinderPool = BinderPool.getInstance(this);

        IBinder securityIBinder = mBinderPool.queryBinder(BinderPool.BINDER_POOL_SECURITY_POOL);
        ISecurityCenter_binder_pool iSecurityCenter_binder_pool = SecurityCenterBinderPoolImpl.asInterface(securityIBinder);
        Log.e(TAG, "visit ISecurityCenter..");
        String msg = "hello-安卓";
        Log.e(TAG, "content: " + msg);
        try {
            String password = iSecurityCenter_binder_pool.encrypt(msg);
            Log.e(TAG, "encrypt: " + password);
            Log.e(TAG, "decrypt: " + iSecurityCenter_binder_pool.decrypt(password));

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        IBinder computeIBinder = mBinderPool.queryBinder(BinderPool.BINDER_POOL_COMPUTE);
        ICompute_binder_pool iCompute_binder_pool = ComputeBinderPoolImpl.asInterface(computeIBinder);
        Log.e(TAG, "visit compute..");
        try {
            Log.e(TAG, "3 + 5 = " + iCompute_binder_pool.add(3, 5));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinderPool != null) {
            mBinderPool.unbindService();
        }
    }
}
