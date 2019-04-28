package hello;

import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.security.wss4j2.Wss4jSecurityInterceptor;
import org.springframework.ws.soap.security.wss4j2.callback.SimplePasswordValidationCallbackHandler;
import org.springframework.ws.soap.security.wss4j2.callback.SpringSecurityPasswordValidationCallbackHandler;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
	
	@Autowired
	private DataSource dataSource;
	
	
    @Bean
    public SimplePasswordValidationCallbackHandler securityCallbackHandler(){
        SimplePasswordValidationCallbackHandler callbackHandler = new SimplePasswordValidationCallbackHandler();
        Properties users = new Properties();
        users.setProperty("admin", "secret");
        callbackHandler.setUsers(users);
        return callbackHandler;
    }
    
    @Bean
    public SpringSecurityPasswordValidationCallbackHandler springBootSecurityCallbackHandler(){
        SpringSecurityPasswordValidationCallbackHandler callbackHandler = new SpringSecurityPasswordValidationCallbackHandler();
        callbackHandler.setUserDetailsService(jdbcUserDetailsManager(dataSource));;
        return callbackHandler;
    }

    @Bean
    public Wss4jSecurityInterceptor securityInterceptor(){
        Wss4jSecurityInterceptor securityInterceptor = new Wss4jSecurityInterceptor();
        securityInterceptor.setValidationActions("UsernameToken");
        securityInterceptor.setValidationCallbackHandler(securityCallbackHandler());
        return securityInterceptor;
    }

    @Override
    public void addInterceptors(List interceptors) {
        interceptors.add(securityInterceptor());
    }
	
	
	@Bean
	public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
		MessageDispatcherServlet servlet = new MessageDispatcherServlet();
		servlet.setApplicationContext(applicationContext);
		servlet.setTransformWsdlLocations(true);
		return new ServletRegistrationBean(servlet, "/ws/*");
	}

	@Bean(name = "countries")
	public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema countriesSchema) {
		DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
		wsdl11Definition.setPortTypeName("CountriesPort");
		wsdl11Definition.setLocationUri("/ws");
		wsdl11Definition.setTargetNamespace("http://spring.io/guides/gs-producing-web-service");
		wsdl11Definition.setSchema(countriesSchema);
		return wsdl11Definition;
	}

	@Bean
	public XsdSchema countriesSchema() {
		return new SimpleXsdSchema(new ClassPathResource("countries.xsd"));
	}
	
	@Bean
	public JdbcUserDetailsManager jdbcUserDetailsManager(DataSource dataSource) {
		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);

		UserDetails user = User.withUsername("user").password("pass").roles("USER", "OPERATOR")
				.passwordEncoder(x -> new BCryptPasswordEncoder().encode(x)).build();

		UserDetails admin = User.withUsername("admin").password("pass").roles("USER", "OPERATOR", "ADMIN")
				.passwordEncoder(x -> new BCryptPasswordEncoder().encode(x)).build();

		UserDetails oUser = User.withUsername("esaenz").password("pass").roles("USER", "OPERATOR")
				.passwordEncoder(x -> new BCryptPasswordEncoder().encode(x)).build();

		addUser(jdbcUserDetailsManager, user);
		addUser(jdbcUserDetailsManager, admin);
		addUser(jdbcUserDetailsManager, oUser);
		return jdbcUserDetailsManager;
	}
	
	private void addUser(JdbcUserDetailsManager jdbcUserDetailsManager, UserDetails user) {
		if (!jdbcUserDetailsManager.userExists(user.getUsername())) {
			jdbcUserDetailsManager.createUser(user);
		} else {
			jdbcUserDetailsManager.updateUser(user);
		}
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
