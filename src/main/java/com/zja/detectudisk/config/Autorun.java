/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-16 19:46
 * @Since:
 */
package com.zja.detectudisk.config;

/**
 * 自动运行程序
 */
public class Autorun {
    private boolean enable = true;
    //一般带后缀 示例：Autorun.yaml.exe、b.bat、c.pdf、d.doc等
    private String open;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    @Override
    public String toString() {
        return "Autorun{" +
                "enable=" + enable +
                ", open='" + open + '\'' +
                '}';
    }
}
