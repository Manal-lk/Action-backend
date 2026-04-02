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
@Table(name = "checklist_item")
public class CheckListItem extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checklist_item_id_seq")
    @SequenceGenerator(name = "checklist_item_id_seq", sequenceName = "checklist_item_id_seq", allocationSize = 1)
    private Long id;

    private String description;

    @Column(name = "checked")
    private boolean checked;

    @ManyToOne
    @JoinColumn(name = "checklist_id")
    private CheckList checkList;
}
