package com.fisa.wonq.merchant.domain;

import com.fisa.wonq.merchant.domain.enums.TableStatus;
import com.fisa.wonq.order.domain.Order;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import java.util.List;

/**
 * 식탁
 */
@Entity
@Table(name = "dining_table")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DiningTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long diningTableId;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TableStatus status;

    @Column(nullable = false)
    private Integer tableNumber;

    private Integer locationX;
    private Integer locationY;
    private Integer locationW;
    private Integer locationH;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @OneToMany(mappedBy = "diningTable", cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<Order> orders;

    // 테이블 상태 변경
    public void changeStatus(TableStatus newStatus) {
        this.status = newStatus;
    }

    public void setStatus(TableStatus tableStatus) {
        this.status = tableStatus;
    }

    // 일부 필드만 수정
    public void updateInfo(Integer newTableNumber, Integer newCapacity) {
        if (newTableNumber != null) {
            this.tableNumber = newTableNumber;
        }
        if (newCapacity != null) {
            this.capacity = newCapacity;
        }
    }
}
