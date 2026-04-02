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
public class CustomFieldOption extends AuditTimestamps{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "custom_field_option_id_seq")
    @SequenceGenerator(name = "custom_field_option_id_seq", sequenceName = "custom_field_option_id_seq", allocationSize = 1)
    private Long id;

    private String label;

    @ManyToOne
    @JoinColumn(name = "custom_field_id")
    private CustomField customField;
}
