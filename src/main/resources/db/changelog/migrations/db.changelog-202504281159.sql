--liquisabse formatted sql
--changeset ricardo:202504291159
--commernt: blocks table created

CREATE TABLE BLOCKS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    blocked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    block_reason VARCHAR(255) NOT NULL,
    unblocked_at TIMESTAMP,
    unblock_reason VARCHAR(255),
    card_id BIGINT NOT NULL,
    CONSTRAINT cards__blocks_fk
    FOREIGN KEY (cards_id) REFERENCES CARDS(id) ON DELETE CASCADE
) ENGINE=InnoDB;

--rollback: DROP TABLE BLOCKS