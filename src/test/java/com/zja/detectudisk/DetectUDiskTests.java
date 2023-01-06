/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-16 19:54
 * @Since:
 */
package com.zja.detectudisk;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DetectUDiskTests {

    @Autowired
    DetectUDisk detectUDisk;

    /**
     * 检测新插入盘符
     */
    @Test
    public void detectNewlyInsertedUDisk() {
//        detectUDisk.init();
        detectUDisk.detectNewlyInsertedUDisk();
    }
}
