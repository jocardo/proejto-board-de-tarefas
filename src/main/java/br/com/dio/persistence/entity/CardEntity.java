package br.com.dio.persistence.entity;

import lombok.Data;

@Data
Public class CardEntity {
    private Long id;
    private String title;
    private String description;
    private BoardColumnEntity boardColumn = new BoardColumnEntity();
}
