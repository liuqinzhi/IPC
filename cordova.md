# 概括
通过 Cordova 官方提供的插件，我们可以轻松的在 H5 上调用 Android封装好的调用手机硬件的方法
# 安装cordova
==npm install -g cordova==
  
  更新cordova命令：**npm update -g cordova** 或 **npm update -g cordova@7.1.0**指定一个具体的版本号。
  
  使用**cordova -v**命令查看版本号。默认安装了Node.js，没有安装Node.js的请先下载安装Node.js。
# 创建工程
==cordova create <path>==
  
  举例：**cordova create hello com.example.hello HelloWorld**
  
  参数1：hello是文件夹名字
  
  参数2：com.example.hello是java的包名
  
  参数3：HelloWorld是应用程序的标题
  
  具体帮助查看命令：**cordova help create**
  
  创建后的文件夹内，plugins文件存放插件，www文件夹存放 html 项目，platforms文件夹存放不同平台的源码
  
  注意：创建工程完成后，使用cd命令进入创建好的文件夹内，继续下一步操作。
# 添加平台
==cordova platform add <platform name>==
  
  举例：**cordova platform add android** 添加android平台
  
  删除平台命令：**cordova platform rm android**
  
  更新平台命令：**cordova platform update android** 或 **cordova platform update android@6.3.0**某一个具体的版本号
  
  使用**cordova platform** 命令查看可以添加的版本列表
# 运行app
==cordova run <platform name>==
  
  举例：**cordova run android**
# 添加插件
==cordova plugin add <plugin name>==
  
  举例：**cordova plugin add cordova-plugin-device**
  
  删除插件：**cordova plugin rm cordova-plugin-device**
  
  查看已安装的插件列表：**cordova plugin ls**
  
  具体每一个插件如何使用，请去官方提供的github上查看。也可以搜索得到，如：在github上搜索cordova-plugin-camera，就能得到结果。
# 自定义插件（安卓）
默认安卓环境ok。添加android平台后，在platforms文件夹下会有一个名字为android的文件夹，这个文件夹就是一个安卓项目，可以用AndroidStudio打开这个文件夹。这个文件夹有2个特殊的地方，第一个就是assets/www目录，这个目录就是存放html项目的目录，第二个就是res/xml下有一个config.xml文件。
1. 编写插件的源文件
在src下新建一个包，包名随便起

```
package org.apache.cordova.dialog;

import android.app.AlertDialog;
import android.content.DialogInterface;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

public class CustomDialogPlugin extends CordovaPlugin {

    @Override
    public boolean execute(String action, CordovaArgs args, final CallbackContext callbackContext) throws JSONException {

        if("showDialog".equals(action)){
            AlertDialog.Builder builder = new AlertDialog.Builder(cordova.getActivity());
            builder.setTitle("提示");
            builder.setMessage(args.getString(0));
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    callbackContext.success("点击了确定");
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    callbackContext.error("点击了取消");
                }
            });
            builder.create().show();
            return true;
        }

        return super.execute(action, args, callbackContext);
    }
}
```
类继承了CordovaPlugin，重写了execute（）方法，action为html中调用的方法名，args为参数，callbackContext为回调。
2. 修改config.xml文件
添加
```
<feature name="CustomDialogPlugin">
    <param name="android-package" value="org.apache.cordova.dialog.CustomDialogPlugin" />
</feature>
```
feature属性name是在html中调用的值,param的value是插件的完整类名。
3. 调用自定义插件
在html中添加一个button，点击事件函数执行下面代码

```
cordova.exec(function(){}, function(){}, "CustomDialogPlugin", "showDialog", ["dialog"]);
```
  
  参数1：插件调用成功的回调
  
  参数2：插件调用失败的回调
  
  参数3：与config.xml文件中的feature属性name的值一致
  
  参数4：action，就是插件的execute方法的第一个参数action
  
  参数5：传递的参数
  
  注意：在cordova项目的根目录下不要轻易执行cordova build命令，此命令会把根目录下的www文件夹和plugins文件夹下的内容编译到每一个添加的平台下面(安卓下会修改assets/www文件夹和res/xml/config.xml文件的内容)，所以如果在安卓平台下修改了assets/www文件夹下的内容或者res/xml/config.xml中的内容，然后又在项目根目录下执行了cordova build命令，安卓平台下的修改就会被覆盖掉。