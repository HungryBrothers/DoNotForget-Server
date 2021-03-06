package com.hungrybrothers.alarmforsubscription.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
    private volatile Connector connector;

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        connector.pause();
        Executor executor = connector.getProtocolHandler().getExecutor();

        if (executor instanceof ThreadPoolExecutor) {
            try {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                threadPoolExecutor.shutdown();

                if (!threadPoolExecutor.awaitTermination(Const.TIMEOUT, TimeUnit.SECONDS)) {
                    log.warn("Tomcat thread pool did not shutdown gracefully within" +
                            Const.TIMEOUT + " seconds. Proceeding with forceful shutdown.");

                    threadPoolExecutor.shutdownNow();

                    if (!threadPoolExecutor.awaitTermination(Const.TIMEOUT, TimeUnit.SECONDS)) {
                        log.error("Tomcat thread pool did not terminate.");
                    }
                } else {
                    log.info("Tomcat thread pool has been gracefully shutdown.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
