/**
 * @Company: 上海数慧系统技术有限公司
 * @Department: 数据中心
 * @Author: 郑家骜[ào]
 * @Email: zhengja@dist.com.cn
 * @Date: 2022-04-16 20:24
 * @Since:
 */
package com.zja.detectudisk.config;

import java.util.List;

public class AutoConfig {

    private boolean globalSwitch = true;
    private boolean openUDisk = false;

    private List<SyncData> syncdata;
    private List<Autorun> autorun;

    public List<SyncData> getSyncdata() {
        return syncdata;
    }

    public void setSyncdata(List<SyncData> syncdata) {
        this.syncdata = syncdata;
    }

    public List<Autorun> getAutorun() {
        return autorun;
    }

    public void setAutorun(List<Autorun> autorun) {
        this.autorun = autorun;
    }

    public boolean isGlobalSwitch() {
        return globalSwitch;
    }

    public void setGlobalSwitch(boolean globalSwitch) {
        this.globalSwitch = globalSwitch;
    }

    public boolean isOpenUDisk() {
        return openUDisk;
    }

    public void setOpenUDisk(boolean openUDisk) {
        this.openUDisk = openUDisk;
    }

    @Override
    public String toString() {
        return "AutoConfig{" +
                "globalSwitch=" + globalSwitch +
                ", openUDisk=" + openUDisk +
                ", syncdata=" + syncdata +
                ", autorun=" + autorun +
                '}';
    }
}
