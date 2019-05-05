package com.example.springboot.ws_wss;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.security.AbstractWsSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.xwss.XwsSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.AbstractWsPasswordCallbackHandler;
import org.springframework.ws.soap.security.wss4j2.callback.SpringSecurityPasswordValidationCallbackHandler;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.apache.wss4j.dom.validate.UsernameTokenValidator;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.apache.wss4j.dom.engine.WSSecurityEngine;

@Configuration
@EnableWs
public class WebServiceConfig extends WsConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
    @SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
    public void addInterceptors(List interceptors) {
        interceptors.add(securityInterceptor());
    }
    
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
		
		UserDetails user = User.withUsername("user").password("secret").roles("USER", "OPERATOR")
				.passwordEncoder(x -> new AppPasswordEncoder().encode(x)).build(); //x -> new BCryptPasswordEncoder().encode(x)

		UserDetails admin = User.withUsername("admin").password("secret").roles("USER", "OPERATOR", "ADMIN")
				.passwordEncoder(x -> new AppPasswordEncoder().encode(x)).build();

		addUser(jdbcUserDetailsManager, user);
		addUser(jdbcUserDetailsManager, admin);
		return jdbcUserDetailsManager;
	}
    
	
	/*@Bean
    public SimplePasswordValidationCallbackHandler securityCallbackHandler(){
        SimplePasswordValidationCallbackHandler callbackHandler = new SimplePasswordValidationCallbackHandler();
        Properties users = new Properties();
        users.setProperty("admin", "secret");
        callbackHandler.setUsers(users);
        return callbackHandler;
    }*/
	
	@Bean
    public AbstractWsPasswordCallbackHandler securitySpringBootCallbackHandler(){
		SpringSecurityPasswordValidationCallbackHandler callbackHandler = new SpringSecurityPasswordValidationCallbackHandler();
        callbackHandler.setUserDetailsService(jdbcUserDetailsManager(dataSource));
        return callbackHandler;
    }

    @Bean
    public AbstractWsSecurityInterceptor securityInterceptor(){
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setValidationActions("UsernameToken");
        securityInterceptor.setValidationCallbackHandler(securitySpringBootCallbackHandler());
        WSSConfig wssConfig = WSSConfig.getNewInstance();
        wssConfig.setValidator(WSConstants.USERNAME_TOKEN, AppUsernameTokenValidator.class);
        securityInterceptor.setWssConfig(wssConfig);
        return securityInterceptor;
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name = CountryEndpoint.ENDPOINT_NAME)
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName(CountryEndpoint.COUNTRIES_PORT);
		wsdl11Definition.setLocationUri(CountryEndpoint.LOCATION_URI);
		wsdl11Definition.setTargetNamespace(CountryEndpoint.NAMESPACE_URI);
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource(CountryEndpoint.COUNTRIES_SCHEMA));
	}
	
	
	/*@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}*/
	 
	
	
	private void addUser(JdbcUserDetailsManager jdbcUserDetailsManager, UserDetails user) {
		if (!jdbcUserDetailsManager.userExists(user.getUsername())) {
			jdbcUserDetailsManager.createUser(user);
		} else {
			jdbcUserDetailsManager.updateUser(user);
		}
	}
}
