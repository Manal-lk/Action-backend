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
@EqualsAndHashCode(callSuper = false)
public class BoardColumn extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_column_id_seq")
    @SequenceGenerator(name = "board_column_id_seq", sequenceName = "board_column_id_seq", allocationSize = 1)
    private Long id;

    private String name;
    @Column(name = "\"offset\"")
    private Integer offset;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;

    @OneToMany(mappedBy = "boardColumn", fetch = FetchType.LAZY)
    @OrderBy("offset ASC")
    private List<Action> actions;
}
