package com.ligl.demo;

import com.ligl.demo.activiti.modeler.JsonpCallbackFilter;
import com.ligl.demo.firstFlow.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 参考资料：
 http://blog.csdn.net/chenhai201/article/details/72668275		ACTIVITI整合介绍
 http://blog.csdn.net/chq1988/article/details/75699792			ACTIVITI加入数据库
 http://blog.csdn.net/hj7jay/article/details/51302829   		ACTIVITI涉及的表介绍
 http://doc.okbase.net/wiselyman/archive/209439.html			整合实战
 */

@SpringBootApplication
@Configuration   //标注一个类是配置类，spring boot在扫到这个注解时自动加载这个类相关的功能，比如前面的文章中介绍的配置AOP和拦截器时加在类上的Configuration
@ComponentScan({"org.activiti.rest.diagram", "com.ligl.demo"})//扫描组建
@EntityScan("com.ligl.demo.firstFlow") //entity对应的包路径
@EnableJpaRepositories(basePackages={"com.ligl.demo.firstFlow"})//dao层对应的包路径
//@EnableAutoConfiguration()  //启用自动配置 该框架就能够进行行为的配置，以引导应用程序的启动与运行, 根据导入的starter-pom 自动加载配置
@EnableAutoConfiguration(exclude = {
		org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
		org.activiti.spring.boot.SecurityAutoConfiguration.class,
		org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class
})
//@EnableAsync
public class BootWithActivitiApplication extends WebMvcConfigurerAdapter {

	@Autowired
	private PersonRepository personRepository;
	@Autowired
	private CompRepository compRepository;


	public static void main(String[] args) {
		SpringApplication.run(BootWithActivitiApplication.class, args);
	}

	@Bean
	public JsonpCallbackFilter filter(){
		return new JsonpCallbackFilter();
	}


//	//初始化模拟数据
	@Bean
	public CommandLineRunner init(final ActivitiService myService){
		return new CommandLineRunner(){
			public void run(String... strings)throws Exception {
				if(personRepository.findAll().size() == 0){
					personRepository.save(new Person("wtr"));
					personRepository.save(new Person("wyf"));
					personRepository.save(new Person("admin"));
				}
				if(compRepository.findAll().size() == 0){
					Comp group = new Comp("great company");
					compRepository.save(group);
					Person admin = personRepository.findByPersonName("admin");
					Person wtr = personRepository.findByPersonName("wtr");
					admin.setComp(group);
					wtr.setComp(group);
					personRepository.save(admin);
					personRepository.save(wtr);
				}
			}
		};
	}


}
