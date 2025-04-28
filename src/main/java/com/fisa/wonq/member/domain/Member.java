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
@Data
@Table(name = "member")
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

    /**
     * 양방향 연관관계 편의 메서드
     **/
    public void addMerchant(Merchant m) {
        this.merchant = m;
        m.setMember(this);
    }

    /**
     * 회원 가입 시 사용
     */
    public static Member of(String accountId, String encodedPassword) {
        return Member.builder()
                .accountId(accountId)
                .password(encodedPassword)
                .role(MemberRole.ROLE_USER)
                .status(MemberStatus.ACTIVE)
                .build();
    }

    /**
     * 비밀번호 변경
     **/
    public void changePassword(String encodedNewPassword) {
        this.password = encodedNewPassword;
    }
}
