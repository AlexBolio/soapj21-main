package com.mx.edifact.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mx.edifact.utils.Propiedades;

@Configuration
public class DataSourceConfig {
	private Propiedades prop = null;

	@Bean
	public org.apache.tomcat.jdbc.pool.DataSource dataSource() throws IOException {
		prop = new Propiedades();
//		################################## MySql ###########################################
		String user = prop.getPropValues("mysql_user");
		String passw = prop.getPropValues("mysql_password");
		String host = prop.getPropValues("mysql_host");
		String port = prop.getPropValues("mysql_port");
		String shema = prop.getPropValues("mysql_database");
		          
		org.apache.tomcat.jdbc.pool.PoolProperties poolProperties = new org.apache.tomcat.jdbc.pool.PoolProperties();
		poolProperties.setUsername(user);
		poolProperties.setPassword(passw);
		poolProperties.setDriverClassName("org.mariadb.jdbc.Driver");
		poolProperties.setUrl("jdbc:mariadb://" + host + ":" + port + "/" + shema);
		poolProperties.setInitialSize(1);
		poolProperties.setRemoveAbandoned(true);
		poolProperties.setTestOnBorrow(true);
		poolProperties.setValidationQuery("SELECT 1");
		
		org.apache.tomcat.jdbc.pool.DataSource poolDataSource = new org.apache.tomcat.jdbc.pool.DataSource();
		poolDataSource.setPoolProperties(poolProperties);
		
		return poolDataSource;
	}
}
