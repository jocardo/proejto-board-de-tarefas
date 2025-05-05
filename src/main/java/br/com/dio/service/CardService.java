package br.com.dio.service;

import br.com.dio.persistence.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;


@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        try {
            var dao = new CardDAO(connection);
            dao.insert(card);
            connection.commit();
            return entity;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } 
    }

    public void moveCardToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(()  -> new EntityNotFoundException("Card id %s not found".formatted(cardId)));
            if (dto.blocked()){
                throw new CardBlockedException("The %s card is locked, you need to unlock it to move.".formatted(dto.id()));
            }
            var currentColumn = boardColumnInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateExceptiona("The informed card belongs to another board"));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("The card %s is already in the final column".formatted(dto.id()));
            }
            var nextColumn = boardColumnInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst()
                    .orElseThrow( () -> new IllegalStateException("The card is cancelled, it cannot be moved to another column"));    
            dao.moveToColumn(boardcolumnInfo.id(), cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } 
    }

    public void cancel(final Long cardId, final cancelColumnId,
                       final List<BoardColumnInfoDTO> boardColumnInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var optional = dao.findById(cardId);
            var dto = optional.orElseThrow(()  -> new EntityNotFoundException("Card id %s not found".formatted(cardId)));
            if (dto.blocked()){
                throw new CardBlockedException("The %s card is locked, you need to unlock it to move.".formatted(dto.id()));
            }
            var currentColumn = boardColumnInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateExceptiona("The informed card belongs to another board"));
            if (currentColumn.kind().equals(FINAL)){
                throw new CardFinishedException("The card %s is already in the final column".formatted(dto.id()));
            }
            boardColumnInfo.stream()
                    .filter(bc -> bc.order() == currentColumn.order() + 1)
                    .findFirst()
                    .orElseThrow( () -> new IllegalStateException("The card is cancelled, it cannot be moved to another column"));
        
            dao.moveToColumn(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } 
    }

    private void checkIfCanBeMoved(final CardDetailsDTO dto, final List<BoardColumnInfoDTO> boardColumnInfo){
        if (dto.blocked()){
            throw new CardBlockedException("The %s card is locked, you need to unlock it to move.".formatted(dto.id()));
        }
        var currentColumn = boardColumnInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateExceptiona("The informed card belongs to another board"));
        if (currentColumn.kind().equals(FINAL)){
            throw new CardFinishedException("The card %s is already in the final column".formatted(dto.id()));
        }
        var nextColumn = boardColumnInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst()
                .orElseThrow( () -> new IllegalStateException("The card is cancelled, it cannot be moved to another column"));
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnInfo) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var blockDao = new BlockDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(()  -> new EntityNotFoundException("Card id %s not found".formatted(id)));
            if (dto.blocked()){
                throw new CardBlockedException("The %s card is locked.".formatted(dto.id()));
            }
            var currentColumn = boardColumnInfo.stream()
                    .filter(bc -> bc.id().equals(dto.columnId()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateExceptiona("The informed card belongs to another board"));
            if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)){
                throw new IllegalStateException("The card %s is already in the %s column".formatted(dto.id(), currentColumn.kind()));
            }
            blockDao.unblock(reason, id);
            conection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } 
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        try{
            var dao = new CardDAO(connection);
            var blockDao = new BlockDAO(connection);
            var optional = dao.findById(id);
            var dto = optional.orElseThrow(()  -> new EntityNotFoundException("Card id %s not found".formatted(id)));
            if (!dto.blocked()){
                throw new CardBlockedException("The %s card is not locked.".formatted(dto.id()));
            }
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        }
    }
}