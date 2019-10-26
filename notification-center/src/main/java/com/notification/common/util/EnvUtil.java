package com.notification.common.util;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @Description: 判断当前环境
 * @author youben
 */
public class EnvUtil {
	private static final Log logger = LogFactory.getLog(EnvUtil.class);
	
	private static final String filePath = "application.properties";
	
	private static final String envKey = "spring.profiles.active";
	
	/**
     * 读取属性文件的值
     */
	public static String getConfigInfo(String fileName, String key) {
        String value = "";
        Properties pop = new Properties();
        try {
            InputStream in =EnvUtil.class.getClassLoader().getResourceAsStream(fileName);
            pop.load(in);
            value = pop.getProperty(key);
        } catch (Exception e) {
            logger.error("读取" + fileName + "失败，原因：", e);
        }

        return value;
    }
	
	
	
	
	/**
     * 根据属性文件判断当前是否是生产环境
     * @return
     */
    public static boolean isPrd() {
        String env = getConfigInfo(filePath, envKey);
        if (!env.equalsIgnoreCase("prod")) {
            return false;
        }
        return true;
    }

    /**
     * 根据属性文件判断当前是否是开发环境
     * @return
     */
    public static boolean isDev() {
        String env = getConfigInfo(filePath, envKey);
        if (!env.equalsIgnoreCase("dev")) {
            return false;
        }
        return true;
    }

    /**
     * 根据属性文件判断当前是否是开发环境
     * @return
     */
    public static String getEnv() {
        return getConfigInfo(filePath, envKey);
    }
    
}
