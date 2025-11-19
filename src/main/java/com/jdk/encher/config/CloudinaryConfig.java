package com.jdk.encher.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dara2kftc",
                "api_key", "164568643853131",
                "api_secret", "TFFoSCyN4sNHrFs39MGuj_dHJXs",
                "secure", true
        ));
    }
}
