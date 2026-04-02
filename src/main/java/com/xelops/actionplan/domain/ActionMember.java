package com.xelops.actionplan.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ActionMember extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "action_member_id_seq")
    @SequenceGenerator(name = "action_member_id_seq", sequenceName = "action_member_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private User member;
}
