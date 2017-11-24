package com.lqz.liuqinzhi.first.binderpoll;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.lqz.liuqinzhi.first.IBinderPool;

import java.util.concurrent.CountDownLatch;

/**
 * Created by liu.qinzhi on 2017/11/23.
 */

public class BinderPool {

    private static final String TAG = "BinderPool";

    public static final int BINDER_POOL_NONE = -1;
    public static final int BINDER_POOL_SECURITY_POOL = 1;
    public static final int BINDER_POOL_COMPUTE = 2;

    private Context mContext;
    private IBinderPool mBinderPool;
    private static volatile BinderPool instance;
    private CountDownLatch mConnectBinderPoolCountDownLatch;


    private BinderPool(Context mContext) {
        this.mContext = mContext;
        connectBinderPoolService();
    }

    public static BinderPool getInstance(Context context) {

        if(instance == null) {
            synchronized (BinderPool.class) {
                if (instance == null) {
                    instance = new BinderPool(context);
                }
            }
        }
        return instance;
    }

    public IBinder queryBinder(int binderCode) {

        IBinder binder = null;

        if (mBinderPool != null) {
            try {
                binder = mBinderPool.queryBinder(binderCode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return binder;
    }

    private void connectBinderPoolService() {

        mConnectBinderPoolCountDownLatch = new CountDownLatch(1);
        Intent intent = new Intent();
        intent.setAction("com.lqz.first.binderpool");
        intent.setPackage("com.lqz.liuqinzhi.first");
        mContext.bindService(intent, mBinderPoolConnection, Context.BIND_AUTO_CREATE);
        try {
            mConnectBinderPoolCountDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void unbindService() {
        mContext.unbindService(mBinderPoolConnection);
    }

    private ServiceConnection mBinderPoolConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.e(TAG, "binder service connect...");
            mBinderPool = IBinderPool.Stub.asInterface(service);

            try {
                mBinderPool.asBinder().linkToDeath(mBinderPoolDeathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mConnectBinderPoolCountDownLatch.countDown();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private IBinder.DeathRecipient mBinderPoolDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {

            Log.e(TAG, "binder died...");
            mBinderPool.asBinder().unlinkToDeath(mBinderPoolDeathRecipient, 0);
            mBinderPool = null;
            connectBinderPoolService();
        }
    };

    public static class BinderPoolImpl extends IBinderPool.Stub {

        @Override
        public IBinder queryBinder(int binderCode) throws RemoteException {

            IBinder iBinder = null;

            switch (binderCode) {
                case BINDER_POOL_SECURITY_POOL:
                    iBinder = new SecurityCenterBinderPoolImpl();
                    break;
                case BINDER_POOL_COMPUTE:
                    iBinder = new ComputeBinderPoolImpl();
                    break;
                default:
                    break;
            }
            return iBinder;
        }
    }

}
