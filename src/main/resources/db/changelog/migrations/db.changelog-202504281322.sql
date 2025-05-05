--liquisabse formatted sql
--changeset ricardo:202504291122
--commernt: boards_columns table created

CREATE TABLE BOARDS_COLUMNS (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    `order` int NOT NULL,
    kind VARCHAR(7).
    board_id BIGINT NOT NULL,
    CONSTRAINT boards__boards_columns_fk
    FOREIGN KEY (board_id) REFERENCES BOARDS(id) ON DELETE CASCADE,
    CONSTRAINT id_order_ik UNIQUE KEY unique_board_id_order (board_id, `order`)
) ENGINE=InnoDB;

--rollback: DROP TABLE BOARDS_COLUMNS