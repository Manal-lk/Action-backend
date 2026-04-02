package com.xelops.actionplan.config;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RealmConfig {
    private String name;
    private String issuerUri;
    private String jwkSetUri;
    private List<ClientConfig> clients;
}
