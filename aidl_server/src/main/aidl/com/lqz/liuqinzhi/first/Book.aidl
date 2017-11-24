//有2种aidl文件,第一种是定义一个序列化对象,供其它aidl文件使用AIDL中非默认支持的数据类型;
//第二种是定义 方法接口,以供系统完成跨进程通信

// Book.aidl
//这个文件的作用是引入了一个一个序列化对象 Book 供其它的aidl文件使用
//注意:Book.aidl 与 Book.java的包名应该是一致的

package com.lqz.liuqinzhi.first;


parcelable Book;
