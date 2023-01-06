/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-17 10:41
 * @Since:
 */
package com.zja.detectudisk.usb;

import java.io.File;

/*
 * 参考：https://blog.csdn.net/lljj888/article/details/88975133
 *
 * 实现当检测到有设备插入时，删除设备中的所有文件，使用生产者消费者模式进行检查和删除
 * 1.生产者：在设备插入之前先判断系统开始的盘符数，
 * 然后创建一个线程不断判断系统有多少个盘符，若判断出盘符数增多，则该线程等待并唤醒消费者；否则一直判断。
 * 2.消费者：在没有判断出有插入设备时，处于等待状态；若有，则将设备中的文件全部删除。
 * 3.资源：将插入的设备当作资源
 */
public class DelUSBFile {
    public static int count = 0;

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        File[] dir = File.listRoots();
        count = dir.length;
        ResFileByDelFile rf = new ResFileByDelFile(count);
        Thread t1 = new Thread(new ProducerUSBRootByDelFile(rf));
        Thread t2 = new Thread(new ConsumerUSBRootByDelFile(rf));
        t1.start();
        t2.start();
        //关闭
//        ProducerUSBRootByDelFile.isClose = true;
//        ConsumerUSBRootByDelFile.isClose = true;
    }
}

//资源
class ResFileByDelFile {
    private int count = 0;
    //判断是否有设备插入的标记
    private boolean flag = false;
    private File[] dirs;

    public ResFileByDelFile(int count) {
        this.count = count;
    }

    //递归删除文件
    public static void deleteDir(File dir) {

        File[] file = dir.listFiles();

        if (file != null) {
            for (File f : file) {
                if (f.isDirectory()) {
                    deleteDir(f);
                } else {
                    System.out.println(f + ":" + f.delete());
                }
            }
        }
        System.out.println(dir + ":" + dir.delete());
    }

    //查找资源--生产者使用
    public synchronized void searchFile() {
        //如果flag为true，说明检测出有设备插入，则等待；
        //如果flag为false，说明没有设备插入
        if (flag) {
            try {
                wait();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        dirs = File.listRoots();
        //一但有设备插入，当前盘符数会大于系统一开始的盘符数
        if (dirs.length > count) {
            flag = true;
            notify();
        }
    }

    //消费资源--消费者使用
    public synchronized void delFile() {
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
                deleteDir(dirs[i]);
            }
            flag = false;
            notify();
        }
    }
}

//消费者
class ConsumerUSBRootByDelFile implements Runnable {

    private ResFileByDelFile rf = null;

    public ConsumerUSBRootByDelFile(ResFileByDelFile rf) {
        this.rf = rf;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            rf.delFile();
        }
    }

}

//生产者
class ProducerUSBRootByDelFile implements Runnable {

    private ResFileByDelFile rf = null;

    public ProducerUSBRootByDelFile(ResFileByDelFile rf) {
        this.rf = rf;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            rf.searchFile();
        }
    }
}
