package com.xelops.actionplan.domain;

import com.xelops.actionplan.enumeration.ActionStatusEnum;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Action extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "action_id_seq")
    @SequenceGenerator(name = "action_id_seq", sequenceName = "action_id_seq", allocationSize = 1)
    private Long id;

    private String title;
    private String description;
    private String coverImageUrl;
    private LocalDate startDate;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private ActionStatusEnum status;

    @Column(name = "\"offset\"")
    private Integer offset;

    private Integer estimation;

    private boolean completed;
    private LocalDate completionDate;

    @ManyToOne
    @JoinColumn(name = "board_column_id")
    private BoardColumn boardColumn;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne
    @JoinColumn(name = "priority_id")
    private CustomFieldOption priority;

    @OneToMany(mappedBy = "action")
    private List<ActionMember> members;

    @OneToMany(mappedBy = "action")
    private List<CheckList> checkLists;
}
