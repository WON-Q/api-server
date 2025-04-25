package com.fisa.wonq.member.domain;

import com.fisa.wonq.global.domain.BaseDateTimeEntity;
import com.fisa.wonq.member.domain.enums.MemberRole;
import com.fisa.wonq.member.domain.enums.MemberStatus;
import com.fisa.wonq.merchant.domain.Merchant;
import jakarta.persistence.*;
import lombok.*;

/**
 * 회원
 */
@Entity
@Table(name = "member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column
    private String accountId;

    @Column
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Merchant merchant;

    public boolean isDeleted() {
        return this.status == MemberStatus.DELETED;
    }

    public void delete() {
        this.status = MemberStatus.DELETED;
    }
}
