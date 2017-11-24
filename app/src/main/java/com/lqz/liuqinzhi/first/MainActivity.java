package com.lqz.liuqinzhi.first;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final String TAG = this.getClass().getSimpleName();

    private List<Book> mBooks;

    private BookManager mBookManager;

    //与服务端是否连接
    private boolean mBind;

    private LinearLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindServiceMessenger();

        mLayout = (LinearLayout) findViewById(R.id.layout_container);
        mLv = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "start...");
        if (!mBind) {
            attemptBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "stop...");
        unbindService(mServiceConn);
        mBind = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isConnMessenger = false;
        unbindService(mConnMessenger);
        if (mClientSocket != null) {
            try {
                mClientSocket.shutdownInput();
                mClientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //------------------------------aidl-----------------------------
    public void addBookIn(View view) {

        if (!mBind) {
            attemptBindService();
        }

        if (mBookManager == null) {
            Log.e(TAG, "mBookManager = null");
            return;
        }
        Book book = new Book("Android高级 in", 30);
        Log.e(TAG, "before book: " + book);
        Book resultBook = null;
        try {
            resultBook = mBookManager.addBookIn(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "after book: " + book);
        Log.e(TAG, "after book return: " + resultBook);
    }

    public void addBookOut(View view) {

        if (!mBind) {
            attemptBindService();
        }

        if (mBookManager == null) {
            Log.e(TAG, "mBookManager = null");
            return;
        }
        Book book = new Book("Android高级 out", 40);
        Log.e(TAG, "before book: " + book);
        Book resultBook = null;
        try {
            resultBook = mBookManager.addBookOut(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "after book: " + book);
        Log.e(TAG, "after book return: " + resultBook);
    }

    public void addBookInOut(View view) {

        if (!mBind) {
            attemptBindService();
        }

        if (mBookManager == null) {
            Log.e(TAG, "mBookManager = null");
            return;
        }
        Book book = new Book("Android高级 in out", 60);
        Log.e(TAG, "before book: " + book);
        Book resultBook = null;
        try {
            resultBook = mBookManager.addBookInOut(book);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "after book: " + book);
        Log.e(TAG, "after book return: " + resultBook);
    }

    public void testInOut(View view) throws RemoteException {

        Book in = new Book("book in", 10);
        Book out = new Book("book out", 20);

        if (mBookManager == null) {
            return;
        }
        mBookManager.testInOut(in, out);
    }

    public void getBooks(View view) {
        if (null != mBookManager) {
            try {
                mBooks = mBookManager.getBooks();
                Log.e(TAG, mBooks == null ? "null" : mBooks.toString());
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    private void attemptBindService() {
        Intent intent = new Intent();
        intent.setAction("com.lqz.first.aidl");
//        intent.setPackage("com.lqz.liuqinzhi.first");
        intent.setPackage("com.lqz.liuqinzhi.aidl_server");
        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.e(TAG, "service connect...");

            mBookManager = BookManager.Stub.asInterface(service);
            mBind = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.e(TAG, "service disconnect...");
            mBind = false;
        }
    };

    //--------------------------------messenger-----------------------------

    private static final int MSG_NUM = 0x110;

    private Messenger mServiceMessenger;
    //是否连接服务端
    private boolean isConnMessenger;

    private Messenger mMessenger = new Messenger(new Handler(){
        @Override
        public void handleMessage(Message msgFromServer) {

            switch (msgFromServer.what) {

                case MSG_NUM:
                    TextView tv = (TextView) mLayout.findViewById(msgFromServer.arg1);
                    tv.setText(tv.getText() + " => " + msgFromServer.arg2);
                    break;
            }
            super.handleMessage(msgFromServer);
        }
    });

    private ServiceConnection mConnMessenger = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.e(TAG, "service messenger connect");

            mServiceMessenger = new Messenger(service);
            isConnMessenger = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.e(TAG, "service messenger disconnect");
            isConnMessenger = false;
            mServiceMessenger = null;
        }
    };

    private void bindServiceMessenger() {

        Intent intent = new Intent();
        intent.setAction("com.lqz.first.messenger");
        intent.setPackage("com.lqz.liuqinzhi.first");
        bindService(intent, mConnMessenger, Context.BIND_AUTO_CREATE);
    }

    private int mId;

    public void sendMsg(View view) throws RemoteException {

        int a = mId++;
        int b = (int) (Math.random() * 100);

        TextView tv = new TextView(this);
        tv.setId(a);
        tv.setText(String.format("%d + %d caculating...", a, b));
        mLayout.addView(tv);

        Message msgFromClient = Message.obtain(null, MSG_NUM, a, b);
        msgFromClient.replyTo = mMessenger;
        if (isConnMessenger) {
            //往服务端发送消息
            mServiceMessenger.send(msgFromClient);
        }
    }

    //--------------------------------mClientSocket-----------------------------

    Socket mClientSocket = null;

    public void socketServerStart(View view) {

        Intent intent = new Intent();
        intent.setAction("com.lqz.first.tcpsocket");
        intent.setPackage("com.lqz.liuqinzhi.first");
        startService(intent);
    }

    public void socket(View view) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
//                    mClientSocket = new Socket("172.16.96.55", 30000);
                    mClientSocket = new Socket("localhost", 30000);
                    //设置超时
                    mClientSocket.setSoTimeout(5000);
                    BufferedReader br = new BufferedReader(new InputStreamReader(mClientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mClientSocket.getOutputStream())));
                    out.println("你好呀!");
                    while (!MainActivity.this.isFinishing()) {
                        final String line = br.readLine();
                        if (!TextUtils.isEmpty(line)) {

                            Log.e(TAG, "来自服务器的数据: " + line);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(MainActivity.this, "来自服务器的数据: " + line, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

                    br.close();
                    out.close();
                    mClientSocket.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException 来自服务器的数据");
                    e.printStackTrace();
                }

            }
        }).start();
    }

    //--------------------------------Content Provider-----------------------------

    private ContentResolver contentResolver;
    private Cursor cursor;
    private MyAdapter myAdapter;
    private ListView mLv;

    public void contentProviderCall(View view) {

        if (contentResolver == null) {
            contentResolver = getContentResolver();
        }
        String method = "isSuccess";
        String arg = "arguments";
        Bundle extras = new Bundle();
        extras.putString("name", "Jack");
        Bundle bundleResult = contentResolver.call(Uri.parse("content://com.lqz.first.myprovider/contact"), method, arg, extras);
        Log.e(TAG, "调用结果: " + bundleResult.get(method));

        Log.e(TAG, "extras size: " + extras.size());
        Set<String> setExtras = extras.keySet();
        for (String key : setExtras) {
            Log.e(TAG, "extras: " + key + ": " + extras.get(key));
        }

        Log.e(TAG, "result size: " + bundleResult.size());
        Set<String> setResult = bundleResult.keySet();
        for (String key : setResult) {
            Log.e(TAG, "result: " + key + ": " + bundleResult.get(key));
        }
    }

    public void contentProvider(View view) {

        if (contentResolver == null) {
            contentResolver = getContentResolver();
            contentResolver.registerContentObserver(Uri.parse("content://com.lqz.first.myprovider/contact"), true, new MyContentObserver(new Handler()));
        }
        if (cursor == null) {
            cursor = contentResolver.query(Uri.parse("content://com.lqz.first.myprovider/contact"), null, null, null, null);
        }
        if (myAdapter == null) {
            myAdapter = new MyAdapter(this, cursor);
            mLv.setAdapter(myAdapter);
        }
    }

    public void addContact(View view) {
        startActivityForResult(new Intent(this, AddContactActivity.class), 0);
    }

    private class MyContentObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            myAdapter.notifyDataSetChanged();
        }
    }

    private static class MyAdapter extends CursorAdapter {

        private Cursor cursor;
        private Context context;

        private LayoutInflater inflater;

        public MyAdapter(Context context, Cursor c) {
            super(context, c);
            this.context = context;
            this.cursor = c;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            if (null == inflater) {
                inflater = LayoutInflater.from(context);
            }
            View view = inflater.inflate(R.layout.item, null);
            if (cursor != null) {
                view.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
            }
            return view;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView name = (TextView) view.findViewById(R.id.name);
            TextView num = (TextView) view.findViewById(R.id.number);
            if (cursor != null) {
                name.setText(cursor.getString(cursor.getColumnIndex("name")));
                num.setText(cursor.getString(cursor.getColumnIndex("number")));
            }
        }
    }

    //--------------------------------BinderPool-----------------------------
    //极大地提高aidl的开发效率,并且避免大量的service创建

    public void bindConnectPool(View view) {

        startActivity(new Intent(this, BinderPollActivity.class));
    }

}
