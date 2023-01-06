/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-16 19:13
 * @Since:
 */
package com.zja.detectudisk.config;

import java.util.Arrays;

/**
 * 同步数据配置
 */
public class SyncData {
    private boolean enable = true;
    private String source;
    private String dest;
    private String excludesFileType;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getExcludesFileType() {
        return excludesFileType;
    }

    public void setExcludesFileType(String excludesFileType) {
        this.excludesFileType = excludesFileType;
    }

    @Override
    public String toString() {
        return "SyncData{" +
                "enable=" + enable +
                ", source='" + source + '\'' +
                ", dest='" + dest + '\'' +
                ", excludesFileType='" + excludesFileType + '\'' +
                '}';
    }
}
