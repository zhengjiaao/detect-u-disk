# 检测U盘插入

检测到U盘插入后，可以支持自动同步数据(目录或文件)、自动运行程序(cmd命令、程序、文档等)

### 部署 detect-u-disk


### U盘中配置 Autorun.yaml

```yaml
# 全局开关 , 默 true
globalSwitch: true

# U盘自动打开 , 默 false
openUDisk: true

# 自动同步数据(目录或文件)
syncdata:
  - enable: false     # 启用, 默 true。每次U盘插入后执行一次(u盘中文件的修改、删除、新增等操作,会在下次插入时同步)
    source: \test   # 要备份的源文件或源目录(必须存在，且以U盘根目录查找) , 示例 : \test 或 \test\a.pdf
    dest: D:\test        # 要备份到的哪个目录下(必须是目录)
    excludes:        # 排除指定文件类型(可选的)，source为目录时起作用, 示例: txt,pdf

# 自动运行程序(cmd命令、程序、文档等)
autorun:
  - enable: true    # 启用, 默 true。每次U盘插入后执行一次(必须存在，且以U盘根目录查找) 根目录标识为: \
    open: \待打印.pdf  # 支持 http(s)、cmd、bat、exe、doc、pdf等,示例：http://www.baidu.com、a.exe、a.doc
  - enable: true
    open: notepad
```
