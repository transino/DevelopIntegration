package net.zhangqiu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class Main extends SpringBootServletInitializer{

	public static void main(String[] args) throws Exception{
		SpringApplication.run(Main.class, args);
	}
	
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
		return application.sources(Main.class);
	}
}
