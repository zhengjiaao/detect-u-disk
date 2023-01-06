/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-16 13:09
 * @Since:
 */
package com.zja.detectudisk;

import com.zja.detectudisk.config.AutoConfig;
import com.zja.detectudisk.config.Autorun;
import com.zja.detectudisk.config.SyncData;
import com.zja.detectudisk.util.YamlUtil;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 参考：FileSystemView https://blog.csdn.net/interestedly/article/details/113916783
 */
@Component
public class DetectUDisk {

    @Value("${udisk.globalSwitch}")
    private boolean globalSwitch;
    @Value("${udisk.openUDiskEnable}")
    private boolean openUDiskEnable;
    @Value("${udisk.syncdataEnable}")
    private boolean syncdataEnable;
    @Value("${udisk.autorunEnable}")
    private boolean autorunEnable;
    @Value("${udisk.autorun_file}")
    private String autorunYamlFile;

    public void init() {
        if (!globalSwitch) {
            System.out.println("未启用U盘检测！");
            return;
        }
        //获取可使用的磁盘
        String uDisk = getuDisk();
        if (!StringUtils.hasText(uDisk)) {
            System.out.println("未找到可用的U盘！");
            return;
        }
        String autorunYaml = uDisk + autorunYamlFile;
        System.out.println("U盘=" + uDisk);
        System.out.println("U盘配置路径=" + autorunYaml);

        //读取磁盘配置
        AutoConfig read = YamlUtil.read(autorunYaml, AutoConfig.class);
        if (ObjectUtils.isEmpty(read)) {
            return;
        }
        System.out.println("U盘配置内容=" + read);
        executeAll(new File(uDisk), read);
    }

    /**
     * 检测新插入的U盘
     */
    public void detectNewlyInsertedUDisk() {
        //已存在的u盘
        ArrayList<File> onlineUDisk = new ArrayList<>();
        //新插入的u盘 open_uDisk
        ArrayList<File> newlyInsertedUDisk = new ArrayList<>();
        //当前盘符列表
        File[] files = File.listRoots();
        while (true) {
            //新盘符列表
            File[] newfiles = File.listRoots();
            if (newfiles.length > files.length) {
                //找到新插入的盘符
                onlineUDisk.addAll(Arrays.asList(files));
                newlyInsertedUDisk.addAll(Arrays.asList(newfiles));
                newlyInsertedUDisk.removeAll(onlineUDisk);
                //立刻更新盘符，避免多次执行
                files = newfiles;
                //新插入的盘符
                for (File file : newlyInsertedUDisk) {
                    System.out.println("新插入U盘符：" + file);
                    try {
                        //执行自动运行程序
                        String autorunYaml = file + autorunYamlFile;
                        AutoConfig config = YamlUtil.read(autorunYaml, AutoConfig.class);
                        if (!ObjectUtils.isEmpty(config)) {
                            //执行所有程序
                            executeAll(file, config);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                newlyInsertedUDisk.clear();
            } else if (newfiles.length < files.length) {
                System.out.println("U盘已拔出!");
                ;//更新盘符
                files = newfiles;
            }
        }
    }

    /**
     * 执行所有程序
     * @param file
     * @param config
     */
    public void executeAll(File file, AutoConfig config) {
        System.out.println(config);
        if (!config.isGlobalSwitch()) {
            System.out.println(file + "盘,全局开关未打开！");
            return;
        }
        if (config.isOpenUDisk() && openUDiskEnable) {
            try {
                //打开新插入的盘符
                Runtime.getRuntime().exec("cmd /c start " + file.getPath());
            } catch (IOException e) {
                System.err.println("打开新盘符失败：" + file.getPath());
                e.printStackTrace();
            }
        }
        //执行程序
        List<Autorun> autorunList = config.getAutorun();
        if (!ObjectUtils.isEmpty(autorunList) && autorunEnable) {
            for (Autorun autorun : autorunList) {
                System.out.println("可运行程序配置=" + autorun);
                try {
                    executeAutorun(file.getAbsolutePath(), autorun);
                } catch (IOException e) {
                    System.err.println(autorun);
                    e.printStackTrace();
                }
            }
        }

        //同步数据
        List<SyncData> syncdata = config.getSyncdata();
        if (!ObjectUtils.isEmpty(syncdata) && syncdataEnable) {
            for (SyncData syncData : syncdata) {
                System.out.println("可同步数据配置=" + syncData);
                try {
                    executeSyncData(file.getAbsolutePath(), syncData);
                } catch (Exception e) {
                    System.err.println(syncData);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 执行 自动运行的程序
     * @param uDisk U盘 位置
     * @param autorun 程序信息
     */
    private void executeAutorun(String uDisk, Autorun autorun) throws IOException {
        if (!autorun.isEnable()) {
            return;
        }
        String open = autorun.getOpen();
        if (open.contains("http://") || open.contains("https://")) {
            //打开网页
            Runtime.getRuntime().exec("cmd /c start " + open);
        } else if (open.contains(".")) {
            try {
                //运行U盘中的程序：exe、bat、doc、pdf、doc等资源程序
                Runtime.getRuntime().exec("cmd /c start " + uDisk + open);
            } catch (Exception e) {
                //执行常规的cmd命令
                Runtime.getRuntime().exec(open);
            }
        } else {
            //执行常规的cmd命令 看查 https://www.cnblogs.com/frostbelt/archive/2010/08/09/1795468.html
            Runtime.getRuntime().exec(open);
        }
    }

    /**
     * 执行 同步数据
     * @param uDisk
     * @param syncData
     */
    private void executeSyncData(String uDisk, SyncData syncData) {
        if (!syncData.isEnable()) {
            return;
        }
        //文件或目录, 必须存在
        File sourceFile = new File(uDisk + syncData.getSource());
        if (!sourceFile.exists()) {
            System.err.println("源文件找不到：" + sourceFile.getAbsolutePath());
            return;
        }
        //必须是目录
        File destDirFile = new File(syncData.getDest());
        if (!destDirFile.exists()) {
            destDirFile.mkdirs();
        }

        if (sourceFile.isDirectory()) {
            syncDirFile(sourceFile, destDirFile);
        } else {
            syncFile(sourceFile, new File(destDirFile.getAbsolutePath() + File.separator + sourceFile.getName()));
        }

    }

    /**
     * 同步文件
     * @param sourceFile 源文件
     * @param destFile 目的地文件
     * @throws IOException
     */
    private void syncFile(File sourceFile, File destFile) {
        try {
            if (!destFile.exists()) {
                IOUtils.copyLarge(new FileInputStream(sourceFile), new FileOutputStream(destFile));
            } else {
                //计算md5值
                String sourceFileMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(sourceFile));
                String destFileMd5 = DigestUtils.md5DigestAsHex(new FileInputStream(destFile));
                //判断md5值是否一致
                if (!sourceFileMd5.equals(destFileMd5)) {
                    IOUtils.copyLarge(new FileInputStream(sourceFile), new FileOutputStream(destFile));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步目录
     * @param sourceDirFile 源目录
     * @param destDirFile 目的地目录
     */
    private void syncDirFile(File sourceDirFile, File destDirFile) {
        if (!destDirFile.exists()) {
            destDirFile.mkdirs();
        }
        FileSystemView fsv = FileSystemView.getFileSystemView();
        // 获取子文件（包括隐藏文件）
        File[] childs = fsv.getFiles(sourceDirFile, false);
        for (int i = 0; i < childs.length; i++) {
            if (childs[i].isDirectory()) {
                System.out.println("扫描到文件夹：" + childs[i].getAbsolutePath());
                syncDirFile(childs[i], new File(destDirFile.getAbsolutePath() + File.separator + childs[i].getName()));
            } else {
                System.out.println("扫描到文件：" + childs[i].getAbsolutePath());
                syncFile(childs[i], new File(destDirFile.getAbsolutePath() + File.separator + childs[i].getName()));
            }
        }
    }

    /**
     * 找到 U盘，并且U盘存在Autorun.yaml配置文件，找到第一个就返回U盘位置
     * @return 示例 F:\
     */
    private String getuDisk() {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        //列出当前所有盘符
        File[] files = File.listRoots();
        for (int i = 0; i < files.length; i++) {
            if (fsv.getSystemTypeDescription(files[i]).equals("U 盘")) {
                File autorunFile = new File(files[i].getAbsolutePath() + "Autorun.yaml");
                if (autorunFile.exists()) {
                    return files[i].getAbsolutePath();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
//        System.out.println(getuDisk());
        /*FileSystemView fsv = FileSystemView.getFileSystemView();
        File[] files = File.listRoots();
        for (int i = 0; i < files.length; i++) {

            System.out.println(files[i] + " -- " + fsv.getSystemTypeDescription(files[i]));
            // 本地磁盘、CD 驱动器、可移动磁盘、U 盘

            File file = files[i];
            System.out.println("总量=" + file.getTotalSpace());
            System.out.println("可用空间=" + file.getUsableSpace());
            System.out.println("已用空间=" + (file.getTotalSpace() - file.getUsableSpace()));

            if (fsv.getSystemTypeDescription(files[i]).equals("U 盘")) {
                scan(files[i], fsv);
            }
        }*/
    }

    // 扫描文件夹
    private static void scan(File dir) throws IOException {
        FileSystemView fsv = FileSystemView.getFileSystemView();
        // 获取子文件（不包括隐藏文件）
        File[] childs = fsv.getFiles(dir, true);
        for (int i = 0; i < childs.length; i++) {
            if (childs[i].isDirectory()) {
//                System.out.println("扫描到文件夹：" + childs[i].getAbsolutePath());
                scan(childs[i]);
            } else {
//                System.out.println("扫描到文件：" + childs[i].getAbsolutePath());
                File child = childs[i];
                if (child.getName().equals("Autorun.yaml.bat")) {
                    File file = new File("D:\\Autorun.yaml.bat");
                    if (!file.exists()) {
                        IOUtils.copyLarge(new FileInputStream(child), new FileOutputStream(file));
                    } else {
                        //计算md5值是否一致
                        String childmd5DigestAsHex = DigestUtils.md5DigestAsHex(new FileInputStream(child));
                        String md5DigestAsHex = DigestUtils.md5DigestAsHex(new FileInputStream(file));
                        if (!childmd5DigestAsHex.equals(md5DigestAsHex)) {
                            IOUtils.copyLarge(new FileInputStream(child), new FileOutputStream(file));
                        }
                    }
                }
            }
        }
    }

}
