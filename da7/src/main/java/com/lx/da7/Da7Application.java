package com.lx.da7;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
@ComponentScan({"com.lx.authority","com.lx.da7"})
public class Da7Application {

    public static void main(String[] args) {
        SpringApplication.run(Da7Application.class, args);
    }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
