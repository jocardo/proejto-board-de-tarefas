--liquisabse formatted sql
--changeset ricardo:202504281322
--commernt: boards table created

CREATE TABLE BOARDS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

--rollback: DROP TABLE BOARDS