/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-17 18:43
 * @Since:
 */
package com.zja.detectudisk.usb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class USBreadFile {
    public static void USB() {

        //列出当前所有盘符
        File[] files = File.listRoots();
        //准备好集合，把所有盘符数据添加进去
        ArrayList<File> f1 = new ArrayList<>();
        ArrayList<File> f2 = new ArrayList<>();
        long sum;// 盘符总大小
        long surplus;// 盘符剩余大小
        long use;// 盘符已使用大小

        while (true) {
            File[] newfiles = File.listRoots();//获得到新盘符
            if (newfiles.length > files.length) {//如果新的盘符大于原来盘符
                //用Arrays工具把数组转换成集合在添加进集合
                f1.addAll(Arrays.asList(files));
                f2.addAll(Arrays.asList(newfiles));
                f2.removeAll(f1);//在f2和f1里面找到相同的数据进行删除掉

                for (File file : f2) {
                    try {
                        char[] c = file.getPath().toCharArray();//把路径拆分成字符
                        System.out.print("插入:" + c[0] + " 盘");
                        sum = file.getTotalSpace() / 1024 / 1024 / 1024;
                        surplus = file.getUsableSpace() / 1024 / 1024 / 1024;
                        use = sum - surplus;
                        //列出每个盘符的信息
                        System.out.println(c[0] + "盘总大小:" + sum + "G" +
                                " 剩余:" + surplus + "G" + " 已用:" + use + "G");
                        //用Runtime方法打开插入进去的U盘
                        Runtime.getRuntime().exec("cmd /c start " + file.getPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                files = newfiles;//更新盘符
            } else if (newfiles.length < files.length) {//如果新的盘符小于了原来的盘符
                //说明U盘已经拔出
                System.out.println("U盘已拔出");
                files = newfiles;
            }
        }
    }

    public static void main(String[] args) {
        USB();
    }
}
