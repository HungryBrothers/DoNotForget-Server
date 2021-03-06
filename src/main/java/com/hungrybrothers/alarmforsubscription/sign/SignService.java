package com.hungrybrothers.alarmforsubscription.sign;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.hungrybrothers.alarmforsubscription.account.Account;
import com.hungrybrothers.alarmforsubscription.account.AccountAdapter;
import com.hungrybrothers.alarmforsubscription.account.AccountRepository;
import com.hungrybrothers.alarmforsubscription.account.AccountRole;
import com.hungrybrothers.alarmforsubscription.exception.CustomEntityExistsException;
import com.hungrybrothers.alarmforsubscription.exception.ErrorCode;
import com.hungrybrothers.alarmforsubscription.exception.VerifyCodeException;
import com.hungrybrothers.alarmforsubscription.security.JwtTokenProvider;
import com.hungrybrothers.alarmforsubscription.utils.MailUtils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Transactional
@Service
@RequiredArgsConstructor
public class SignService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MailUtils mailUtils;

    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        accountRepository.findByUserId(signUpRequest.getUserId()).ifPresent(user -> {
            throw new CustomEntityExistsException(ErrorCode.ACCOUNT_EXISTS);
        });

        Set<AccountRole> roles = new HashSet<>();
        roles.add(AccountRole.valueOf(signUpRequest.getAccountRole()));

        Account account = accountRepository.save(Account.builder()
            .userId(signUpRequest.getUserId())
            .username(signUpRequest.getUsername())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .roles(roles)
            .build());

        return SignUpResponse.builder()
            .userId(account.getUserId())
            .username(account.getUsername())
            .build();
    }

    @SneakyThrows
    public SignInResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        if (jwtTokenProvider.validateToken(refreshToken)) {
            Account account = accountRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new UsernameNotFoundException(refreshToken));

            return SignInResponse.builder()
                .jwtToken(jwtTokenProvider.createJwtToken(account))
                .refreshToken(jwtTokenProvider.createRefreshToken())
                .build();
        } else {
            throw new JWTVerificationException(ErrorCode.INVALID_TOKEN.getMessage());
        }
    }

    public void sendEmail(AccountAdapter accountAdapter) {
        Account account = accountAdapter.getAccount();

        String code = mailUtils.generateCode();

        mailUtils.sendMail(account.getUserId(), code);

        account.setVerifyCode(code);
        accountRepository.save(account);
    }

    public void verifyEmail(VerifyEmailRequest request, AccountAdapter accountAdapter) {
        Account account = accountAdapter.getAccount();

        if (Objects.equals(request.getVerifyCode(), account.getVerifyCode())) {
            account.setVerified(true);
            accountRepository.save(account);
            return;
        }

        throw new VerifyCodeException(ErrorCode.VERIFY_CODE_EXCEPTION);
    }
}
