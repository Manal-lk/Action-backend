ALTER TABLE "user"
    RENAME COLUMN keycloakId TO keycloak_id;

ALTER TABLE "user"
    ADD COLUMN organization_id BIGINT REFERENCES organization(id);
