package com.xelops.actionplan.domain;


import com.xelops.actionplan.enumeration.StorageType;
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
public class Attachment extends AuditDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_id_seq")
    @SequenceGenerator(name = "attachment_id_seq", sequenceName = "attachment_id_seq", allocationSize = 1)
    private Long id;

    private String name;
    private String url; // Local FS, S3, Azure, GCP or other URL (the whole path)
    private String type; // MIME type
    private Long size;

    @Enumerated(EnumType.STRING)
    private StorageType storageType;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private Action action;
}
