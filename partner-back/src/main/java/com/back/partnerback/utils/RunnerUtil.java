package com.back.partnerback.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;

/**
 * @author yang
 * @create 2023-07-10 20:07
 */
@Slf4j
public class RunnerUtil {

    public static void runnerServicePrint(Environment environment){
        String serverName = environment.getProperty("spring.application.name");
        String serverPort = environment.getProperty("server.port");
        String info =
                "---------------------------"+"项目启动成功，时间："+LocalDateTime.now()+"\n"+
                "---------------------------"+"项目名称："+serverName+"\n"+
                "---------------------------"+"端口号："+serverPort;
        System.out.println(info);
    }
}
