package com.example.springboot.ws_wss;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.security.crypto.password.PasswordEncoder;

public class AppPasswordEncoder implements PasswordEncoder{
	private static final Logger logger = Logger.getLogger(AppPasswordEncoder.class.getName());
	
    @Override
    public String encode(CharSequence rawPassword) {
    	logger.log(Level.INFO, "encode()" + "::" + rawPassword);
        return rawPassword.toString();
    }
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
    	logger.log(Level.INFO, "matches()" + "::" + rawPassword +"::"+encodedPassword);
        return rawPassword.toString().equals(encodedPassword);
    }

}
