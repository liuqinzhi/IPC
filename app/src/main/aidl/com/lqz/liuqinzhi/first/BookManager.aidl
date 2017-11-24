//有2种aidl文件,第一种是定义一个序列化对象,供其它aidl文件使用AIDL中非默认支持的数据类型;
//第二种是定义 方法接口,以供系统完成跨进程通信

//in表示数据只能由客户端流向服务端,out表示数据只能由服务端流向客户端,inout表示数据可以在服务端和客户端之间双向流动,
//其中数据流向是针对客户端中传入方法的对象而言

//in:       服务端会接收到那个对象完整的数据,但是客户端的那个对象不会因为服务端对传参的修改而发生改变
//out:      服务端会接受到那个对象的参数为空的对象(是一个对象,只是里面的参数均为默认值),而且客户端的那个对象会随着服务端对传参的修改而改变
//inout:    服务端会接收到客户端传来对象完整的数据,而且客户端的对象会随着服务端对传参的修改而改变

// BookManager.aidl
package com.lqz.liuqinzhi.first;

// Declare any non-default types here with import statements
//导入aidl非默认支持的数据类型的包,即便在同一个包下
import com.lqz.liuqinzhi.first.Book;

interface BookManager {

    //所有的返回值前都不需要加任何东西,不管是什么数据类型
    List<Book> getBooks();
    Book getBook();
    int getBookCounts();

    //传参时除了java基本数据类型以及String,CharSequence之外(这些都是默认且只能是in),其它的都要加上定向tag(in, out, inout)
    void setBookPrice(in Book book, int price);
    void setBookName(in Book book, String name);
    Book addBookIn(in Book book);
    Book addBookOut(out Book book);
    Book addBookInOut(inout Book book);
    void testInOut(in Book bookIn, out Book bookOut);
}
