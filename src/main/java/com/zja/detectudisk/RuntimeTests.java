/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-16 14:28
 * @Since:
 */
package com.zja.detectudisk;

import java.io.File;
import java.io.IOException;

/**
 * cmd命令参考：https://www.cnblogs.com/frostbelt/archive/2010/08/09/1795468.html
 */
public class RuntimeTests {
    public static void main(String[] args) throws IOException {

        //方式一
//        Runtime.getRuntime().exec("cmd /c del c:\\Autorun.yaml.doc");
//        Runtime.getRuntime().exec("notepad");
//        Runtime.getRuntime().exec("cmd /c start c:\\Autorun.yaml.doc");
        Runtime.getRuntime().exec("cmd /c start D:\\Autorun.yaml.pdf");
//        Runtime.getRuntime().exec("cmd /c start splitting.exe");
//        Runtime.getRuntime().exec("cmd /c start http://www.baidu.com");
//        Runtime.getRuntime().exec("cmd /k start c:\\test.bat");   //java调用bat文件

        //方式二
        /*Runtime runtime = Runtime.getRuntime();

        String command = "cmd /c start splitting.exe";
        //执行文件所在的路径
        String path = "F:\\demo\\src\\main\\resources\\static";
        File exeFile = new File(path);

        final Process process = runtime.exec(command, null, exeFile);*/
    }


}
