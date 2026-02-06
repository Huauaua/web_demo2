package com.zts.web_demo2.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfig {
    
    @Value("${contact.email.to:your-email@example.com}")
    private String toEmail;
    
    @Value("${contact.email.from:website@yourdomain.com}")
    private String fromEmail;
    
    public String getToEmail() {
        return toEmail;
    }
    
    public String getFromEmail() {
        return fromEmail;
    }
}