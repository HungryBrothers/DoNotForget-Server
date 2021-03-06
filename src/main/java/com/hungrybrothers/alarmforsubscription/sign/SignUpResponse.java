package com.hungrybrothers.alarmforsubscription.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpResponse {
    private String userId;

    private String username;
}
