package com.xelops.actionplan.domain;

import com.xelops.actionplan.enumeration.ActionHistoryType;
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
public class History extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_id_seq")
    @SequenceGenerator(name = "history_id_seq", sequenceName = "history_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "concerned_user_id")
    private User concernedUser;

    @ManyToOne
    @JoinColumn(name = "old_concerned_user_id")
    private User oldConcernedUser;

    @ManyToOne
    @JoinColumn(name = "source_column_id")
    private BoardColumn sourceColumn;

    @ManyToOne
    @JoinColumn(name = "target_column_id")
    private BoardColumn targetColumn;

    @Enumerated(EnumType.STRING)
    private ActionHistoryType actionHistoryType;

    private String oldData;

    private String newData;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;
}
