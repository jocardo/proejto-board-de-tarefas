package br.com.dio.dto;


public record BoardDetailsDTO(
        Long id,
        String name,
        List<BoardColumnDTO> columns) {
}