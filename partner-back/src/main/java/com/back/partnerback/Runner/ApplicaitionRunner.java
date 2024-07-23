package com.back.partnerback.Runner;

import com.back.partnerback.utils.RunnerUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * @author yang
 * @create 2023-07-10 20:17
 */
@Component
@RequiredArgsConstructor
public class ApplicaitionRunner implements ApplicationRunner {

    private final ConfigurableApplicationContext context;
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (context.isActive()) {
            RunnerUtil.runnerServicePrint(environment);
        }
    }
}
