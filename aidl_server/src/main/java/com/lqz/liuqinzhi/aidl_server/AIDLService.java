package com.lqz.liuqinzhi.aidl_server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.lqz.liuqinzhi.first.Book;
import com.lqz.liuqinzhi.first.BookManager;

import java.util.ArrayList;
import java.util.List;

public class AIDLService extends Service {

    private final String TAG = this.getClass().getSimpleName();

    private List<Book> mBooks = new ArrayList<>();

    private final BookManager.Stub mBookManager = new BookManager.Stub() {

        @Override
        public List<Book> getBooks() throws RemoteException {

            Log.e(TAG, "invoking getBooks() method, now the list is: " + mBooks.toString());

            return mBooks;
        }

        @Override
        public Book getBook() throws RemoteException {
            return mBooks.get(mBooks.size() - 1);
        }

        @Override
        public int getBookCounts() throws RemoteException {
            return 0;
        }

        @Override
        public void setBookPrice(Book book, int price) throws RemoteException {

        }

        @Override
        public void setBookName(Book book, String name) throws RemoteException {

        }

        @Override
        public Book addBookIn(Book book) throws RemoteException {

            Log.e(TAG, "param [book] is: " + book);
            book.setPrice(1111);
            mBooks.add(book);
            Log.e(TAG, "invoking addBookIn() method, now the list is: " + mBooks.toString());
            return book;
        }

        @Override
        public Book addBookOut(Book book) throws RemoteException {

            Log.e(TAG, "param [book] is: " + book);
            book.setPrice(2222);
            mBooks.add(book);
            Log.e(TAG, "invoking addBookOut() method, now the list is: " + mBooks.toString());
            return new Book();
        }

        @Override
        public Book addBookInOut(Book book) throws RemoteException {

            Log.e(TAG, "param [book] is: " + book);
            book.setPrice(3333);
            mBooks.add(book);
            Log.e(TAG, "invoking addBookInOut() method, now the list is: " + mBooks.toString());
            return book;
        }

        @Override
        public void testInOut(Book bookIn, Book bookOut) throws RemoteException {
            Log.e(TAG, "param [bookIn] is: " + bookIn);
            Log.e(TAG, "param [bookOut] is: " + bookOut);
        }
    };

    public AIDLService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Book b = new Book("Android开发艺术探索", 50);
        mBooks.add(b);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, String.format("on bind, intent = %s", intent.toString()));
        return mBookManager;
    }
}
