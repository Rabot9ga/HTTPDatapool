package ru.sbt.util.HTTPDatapool.threadpools;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class AsyncDownloadPool {

    @Value("${countThread:100}")
    private int countThread;

    @Getter
    private ThreadPoolExecutor service;

    @PostConstruct
    private void init() {
        CustomizableThreadFactory selectThread = new CustomizableThreadFactory("selectThread");
        service = new ThreadPoolExecutor(countThread, countThread,
                30, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(),
                selectThread);
        service.allowCoreThreadTimeOut(true);
    }

}
