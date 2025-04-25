package com.fisa.wonq.merchant.domain;

import com.fisa.wonq.global.domain.BaseDateTimeEntity;
import com.fisa.wonq.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import java.util.List;

/**
 * 가맹점
 */
@Entity
@Table(name = "merchant")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@DynamicInsert
public class Merchant extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long merchantId;

    @Column(nullable = false)
    private String merchantName;

    @Column
    private String merchantOwnerName;

    @Column
    private String merchantOwnerPhoneNo;

    @Column
    private String merchantEmail;

    @Column
    private String businessRegistrationNo;

    @Column
    private String businessLaunchingDate;

    @Column
    private String merchantAddress;

    @Column
    private String merchantAccount;

    @Lob
    private String merchantImg;

    @Column
    private String openTime;

    @Column
    private String closeTime;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    private List<DiningTable> tables;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    private List<Menu> menus;
}
