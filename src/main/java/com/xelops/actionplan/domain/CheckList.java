package com.xelops.actionplan.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "checklist")
public class CheckList extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "checklist_id_seq")
    @SequenceGenerator(name = "checklist_id_seq", sequenceName = "checklist_id_seq", allocationSize = 1)
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;

    @OneToMany(mappedBy = "checkList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CheckListItem> items;
}
