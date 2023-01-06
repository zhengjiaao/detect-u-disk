/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-17 10:43
 * @Since:
 */
package com.zja.detectudisk.usb;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * 实现当检测到有设备插入时，查找设备中的所有文件，使用生产者消费者模式进行检查和查找
 * 1.生产者：在设备插入之前先判断系统开始的盘符数，
 * 然后创建一个线程不断判断系统有多少个盘符，若判断出盘符数增多，则该线程等待并唤醒消费者；否则一直判断。
 * 2.消费者：在没有判断出有插入设备时，处于等待状态；若有，则查找设备中是否包含指定文件，有则关机。
 * 3.资源：将插入的设备当作资源
 */
public class SearchFileShutDown {

    public static int count = 0;

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        File[] dir = File.listRoots();
        count = dir.length;
        ResFileByShutdown rf = new ResFileByShutdown(count);
        Thread t1 = new Thread(new ProducerUSBRootByShutdown(rf));
        Thread t2 = new Thread(new ConsumerUSBRootByShutdown(rf));
        t1.start();
        t2.start();
    }
}

//资源
class ResFileByShutdown {
    private int count = 0;
    //判断是否有设备插入的标记
    private boolean flag = false;
    private File[] dirs;
    //保存设备中的文件
    private static Set<String> fileSet = new LinkedHashSet<String>();
    //要查找的指定文件名
    private static final String filename = "Autorun.yaml";

    public ResFileByShutdown(int count) {
        this.count = count;
    }

    //获取所有文件名
    public static void getAllFiles(File dir) throws Exception {
        File[] files = dir.listFiles();
        if (files != null) {
            System.out.println("dir:" + dir);
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllFiles(f);
                } else {
                    fileSet.add(f.getName());
                    System.out.println("file:" + f);
                }
            }

        }
        //如果有指定的文件，则执行
        if (fileSet.contains(filename)) {
            System.out.println("存在文件=" + filename);
            //Runtime.getRuntime().exec("shutdown -s -t 10");
        }
    }

    //查找资源--生产者使用
    public synchronized void searchFile() {
        if (ProducerUSBRootByShutdown.isClose) {
            notifyAll();
            return;
        }
        if (flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        dirs = File.listRoots();
        if (dirs.length > count) {
            flag = true;
            notify();
        }
    }

    //消费资源--消费者使用
    public synchronized void delFile() {
        if (ConsumerUSBRootByShutdown.isClose) {
            notifyAll();
            return;
        }
        if (!flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (dirs.length > count) {
            for (int i = count; i < dirs.length; i++) {
                try {
                    getAllFiles(dirs[i]);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            // count = dirs.length;
            flag = false;
            notify();
        }
    }
}

//消费者
class ConsumerUSBRootByShutdown implements Runnable {
    public static boolean isClose = false;

    private ResFileByShutdown rf = null;

    public ConsumerUSBRootByShutdown(ResFileByShutdown rf) {
        this.rf = rf;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            if (isClose) {
                System.out.println("关闭usb消费者监听...");
                break;
            }
            rf.delFile();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

//生产者
class ProducerUSBRootByShutdown implements Runnable {
    public static boolean isClose = false;

    private ResFileByShutdown rf = null;

    public ProducerUSBRootByShutdown(ResFileByShutdown rf) {
        this.rf = rf;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            rf.searchFile();
            if (isClose) {
                System.out.println("关闭usb生产者监听...");
                break;
            }
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
