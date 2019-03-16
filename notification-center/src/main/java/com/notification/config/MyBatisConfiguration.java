package com.notification.config;

import javax.sql.DataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.alibaba.druid.pool.DruidDataSource;
/**
 * mybatis相关配置(摒弃XML)
 * @author youben
 */
@Configuration
public class MyBatisConfiguration {
	 @Autowired  
	 private Environment env;  
	 
	 @Bean  
	 public DataSource getDataSource() {  
	        DruidDataSource dataSource = new DruidDataSource();  
	        dataSource.setUrl(env.getProperty("spring.datasource.url"));  
	        dataSource.setUsername(env.getProperty("spring.datasource.username"));  
	        dataSource.setPassword(env.getProperty("spring.datasource.password"));  
	        dataSource.setInitialSize(1);
	        dataSource.setMinIdle(1);
	        dataSource.setMaxActive(20);
	        
	        return dataSource;  
	 }  
	 
	
	@Bean(name = "sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactoryBean() throws Exception{
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(getDataSource());//注入数据源
        bean.setTypeAliasesPackage("com.notification.model");//设置别名

        //显式指定Mapper文件位置,当Mapper文件跟对应的Mapper接口处于同一位置的时候可以不用指定该属性的值
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        bean.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));
        
        return bean.getObject();
    }

 }
