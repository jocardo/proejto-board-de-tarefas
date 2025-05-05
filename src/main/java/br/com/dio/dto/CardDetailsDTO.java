package br.com.dio.dto;


public record CardDetailsDTO(
        LONG id,
        String title,
        String description,
        boolean Blocked,
        OffsetDateTime blockedAt,
        String blockReason,
        int blocksAmount,
        Long columnId,
        String columnName) {
}