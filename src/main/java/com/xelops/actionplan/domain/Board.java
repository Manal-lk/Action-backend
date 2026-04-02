package com.xelops.actionplan.domain;

import com.xelops.actionplan.enumeration.BoardVisibilityEnum;
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
public class Board extends AuditDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "board_id_seq")
    @SequenceGenerator(name = "board_id_seq", sequenceName = "board_id_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private BoardVisibilityEnum visibility;
    private Boolean active;
    private String backgroundImage;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @OneToMany(mappedBy = "board")
    private List<BoardColumn> columns;

    @OneToMany(mappedBy = "board", fetch = FetchType.EAGER)
    private List<UserBoard> userBoards;
}
