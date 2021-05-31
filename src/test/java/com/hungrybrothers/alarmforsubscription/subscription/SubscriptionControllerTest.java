package com.hungrybrothers.alarmforsubscription.subscription;

import com.hungrybrothers.alarmforsubscription.common.CommonTest;
import com.hungrybrothers.alarmforsubscription.common.Const;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SubscriptionControllerTest extends CommonTest {

    @Test
    public void readSubscription() throws Exception {
        mockMvc.perform(
                get(Const.API_SUBSCRIPTION + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaTypes.HAL_JSON)
        )
                .andExpect(status().isOk())
                .andDo(print());
    }

}