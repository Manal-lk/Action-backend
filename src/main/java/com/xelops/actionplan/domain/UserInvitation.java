package com.xelops.actionplan.domain;

import com.xelops.actionplan.enumeration.UserInvitationStatusEnum;
import com.xelops.actionplan.enumeration.UserProfileEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@AllArgsConstructor
public class UserInvitation extends AuditDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_invitation_id_seq")
    @SequenceGenerator(name = "user_invitation_id_seq", sequenceName = "user_invitation_id_seq", allocationSize = 1)
    private Long id;

    private String email;

    @Column(unique = true, length = 36)
    private String token;

    @Enumerated(EnumType.STRING)
    private UserInvitationStatusEnum status;

    @Enumerated(EnumType.STRING)
    private UserProfileEnum profile;

    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;
}

