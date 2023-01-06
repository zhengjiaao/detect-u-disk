package com.zja.detectudisk.util;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.zja.detectudisk.config.AutoConfig;
import org.springframework.core.io.ClassPathResource;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Company: 上海数慧系统技术有限公司
 * Department: 数据中心
 * Date: 2021-03-09 9:12
 * Author: zhengja
 * Email: zhengja@dist.com.cn
 * Desc：
 */
public class YamlUtil {

    /**
     * 加载流,获取yaml文件中的配置数据，然后转换为Map
     * @param yamlFile
     */
    public static Map loadConfYamlMap(String yamlFile) {
        Yaml yaml = loadConfYaml(yamlFile);
        return (Map) yaml;
    }

    /**
     * 获取yaml文件内容
     * @param yamlFile
     * @return
     */
    public static Yaml loadConfYaml(String yamlFile) {
        Yaml yaml = new Yaml();
        ClassPathResource classPathResource = new ClassPathResource(yamlFile);
        boolean exists = classPathResource.exists();
        if (exists) {
            try {
                yaml.load(classPathResource.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("未发现配置文件： [ " + yamlFile + " ] ,使用jpa默认注解，不启动自定义扩展的注解！");
        }
        return yaml;
    }

    /**
     * 读取对象属性
     * @param yamlFile
     * @param type
     * @param <T>
     */
    public static <T> T read(String yamlFile, Class<T> type) {
        File file = new File(yamlFile);
        if (file.exists()) {
            YamlReader reader = null;
            try {
                reader = new YamlReader(new FileReader(yamlFile));
                return reader.read(type);
            } catch (FileNotFoundException | YamlException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        AutoConfig read = read("F:\\Autorun.yaml", AutoConfig.class);
        System.out.println(read);
    }

}
