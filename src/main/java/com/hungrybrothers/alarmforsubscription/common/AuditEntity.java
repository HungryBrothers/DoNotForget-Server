package com.hungrybrothers.alarmforsubscription.common;

import com.hungrybrothers.alarmforsubscription.account.Account;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter @Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    @ManyToOne
    @CreatedBy
    @JoinColumn(updatable = false)
    private Account createUser;

    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updateDateTime;

    @ManyToOne
    @LastModifiedBy
    @JoinColumn(insertable = false)
    private Account updateUser;
}
