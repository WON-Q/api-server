package com.fisa.wonq.member.domain;

import com.fisa.wonq.global.config.BaseDateTimeEntity;
import com.fisa.wonq.member.domain.enums.MemberRole;
import com.fisa.wonq.member.domain.enums.MemberStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@Setter
@Builder
@DynamicInsert
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseDateTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column
    private String userId;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public boolean isDeleted() {
        return this.status == MemberStatus.DELETED;
    }

    public void delete() {
        this.status = MemberStatus.DELETED;
    }
}
