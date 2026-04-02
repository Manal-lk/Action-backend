package com.xelops.actionplan.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class Workspace extends AuditDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "workspace_id_seq")
    @SequenceGenerator(name = "workspace_id_seq", sequenceName = "workspace_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "workspace")
    private List<Board> boards;

    @OneToMany(mappedBy = "workspace")
    private List<UserWorkspace> userWorkspaces;
}
