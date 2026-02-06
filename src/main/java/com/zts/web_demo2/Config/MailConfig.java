package com.zts.web_demo2.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MailConfig {
    
    @Value("${contact.email.to:zhengtianhuo@qq.com}")
    private String toEmail;
    
    @Value("${contact.email.from:zhengtianhuo@qq.com}")
    private String fromEmail;

}