/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-17 20:39
 * @Since:
 */
package com.zja.detectudisk;

import com.zja.detectudisk.config.AutoConfig;
import com.zja.detectudisk.util.YamlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@EnableScheduling   //开启定时任务
public class SaticScheduleTask {

    //现有的盘符
    private static CopyOnWriteArrayList<File> onlineUDisk = new CopyOnWriteArrayList<File>();
    //新插入的盘符
    private static CopyOnWriteArrayList<File> newlyInsertedUDisk = new CopyOnWriteArrayList<File>();
    //当前盘符列表
    private static File[] driveLetter = File.listRoots();

    @Value("${udisk.globalSwitch}")
    private boolean globalSwitch;
    @Value("${udisk.autorun_file}")
    private String autorunFile;

    @Autowired
    private DetectUDisk detectUDisk;

    /**
     * 检测新插入的U盘
     * 每隔3秒
     */
    @Scheduled(fixedRate = 3000)
    private void detectNewlyInsertedUDisk() throws InterruptedException {
        if (!globalSwitch) {
            return;
        }
        //新盘符列表
        File[] newfiles = File.listRoots();
        if (newfiles.length > driveLetter.length) {
            //找到新插入的盘符
            onlineUDisk.addAll(Arrays.asList(driveLetter));
            newlyInsertedUDisk.addAll(Arrays.asList(newfiles));
            newlyInsertedUDisk.removeAll(onlineUDisk);
            //立刻更新盘符，避免多次执行
            driveLetter = newfiles;
            //新插入的盘符
            for (File file : newlyInsertedUDisk) {
                System.out.println("新插入盘符：" + file);
                AutoConfig config = YamlUtil.read(file + autorunFile, AutoConfig.class);
                //不存在 Autorun.yaml 时，不操作插入的盘符
                if (!ObjectUtils.isEmpty(config)) {
                    detectUDisk.executeAll(file, config);
                }
            }
            newlyInsertedUDisk.clear();
        }
    }

    /**
     * 检测U盘退出  没有与检测新插入的U盘一起，是因为避免U盘驱动短时间内多次加载问题
     * 每隔2秒
     */
    @Scheduled(fixedRate = 2000)
    private void detectQuitUDisk() {
        if (!globalSwitch) {
            return;
        }
        //新盘符列表
        File[] newfiles = File.listRoots();
        if (newfiles.length < driveLetter.length) {
            System.out.println("盘符已退出!");
            //更新盘符
            driveLetter = newfiles;
        }
    }

}
