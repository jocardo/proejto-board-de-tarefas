package br.com.dio.service;

import lombok.AllArgsConstructor;

import java.sql.Connection;

public class BoardQueryService {

    private final Connection connection;

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);       
        try {
            var optional = dao.findById(id);
            var boardColumnDAO = new BoardColumnDAO(connection);
            if (optional.isPresent()) {
                var entity = optional.get();
                entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
                return Optional.of(entity);
            } 
            return optional.empty();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    public Optional<aBoardDetailsDTO> showBoardDetails(final Long id) throws SQLException {
        var dao = new BoardDAO(connection);
        try {
            var boardColumnDAO = new BoardColumnDAO(connection);
            var optional = dao.findById(id);
            if (optional.isPresent()) {
                var entity = optional.get();
                var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
                var dto = new BoardDetailsDTO(entity.getId(), entity.getName(), columns);
                return Optional.of(dto);
            }
            return optional.empty();
        }
    }

}