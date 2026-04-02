package com.xelops.actionplan.domain;

import com.xelops.actionplan.enumeration.OrganizationTypeEnum;
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
public class Organization extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organization_id_seq")
    @SequenceGenerator(name = "organization_id_seq", sequenceName = "organization_id_seq", allocationSize = 1)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private OrganizationTypeEnum type;

    private String description;

    private String realm;

    private String logo;

    private String primaryColor;

    private String secondaryColor;

    @OneToMany(mappedBy = "organization")
    private List<User> users;

}
