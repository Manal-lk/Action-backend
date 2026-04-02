package com.xelops.actionplan.config;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientConfig {
    private String name;
    private String clientId;
    private String clientSecret;
}
