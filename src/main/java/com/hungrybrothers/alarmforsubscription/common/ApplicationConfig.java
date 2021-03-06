package com.hungrybrothers.alarmforsubscription.common;

import java.util.Optional;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountAdapter;

@Configuration
@PropertySource("classpath:application.yml")
public class ApplicationConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public GracefulShutdown gracefulShutdown() {
        return new GracefulShutdown();
    }

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory(final GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        tomcatServletWebServerFactory.addConnectorCustomizers(gracefulShutdown);
        return tomcatServletWebServerFactory;
    }

    @Bean
    public AuditorAware<Account> auditorAware() {
        return () -> {
            Optional<Authentication> optionalAuthentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());

            if (optionalAuthentication.isPresent()) {
                Authentication authentication = optionalAuthentication.get();

                if (!authentication.getPrincipal().equals("anonymousUser")) {
                    AccountAdapter accountAdapter = (AccountAdapter) authentication.getPrincipal();
                    Account account = accountAdapter.getAccount();
                    return Optional.of(account);
                } else {
                    return Optional.empty();
                }
            } else {
                return Optional.empty();
            }
        };
    }
}
