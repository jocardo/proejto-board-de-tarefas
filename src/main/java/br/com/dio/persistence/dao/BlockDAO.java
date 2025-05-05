package br.com.dio.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;

@AllArgsConstructor
public class BlockDAO {
    private final Connection connection;

    public void block(final String reason, final Long cardId) throws SQLException {
        var sql = """
                      INSERT INTO BLOCKS (blocked_at, block_reason, card_id)
                      VALUES (?, ?, ?)
                  """;
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i++, cardId);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            connection.rollback();
            throw ex;
        } 
    }

    public void unblock(final String reason, final Long cardId) throws SQLException {
        var sql = """
                      UPADATE BLOCKS
                      SET unblocked_at = ?, unblock_reason = ?
                      WHERE card_id = ? AND unblocked_at IS NULL;
                  """;
        try (var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i++, cardId);
            statement.executeUpdate();
            connection.commit();
        } 
    }
}
    