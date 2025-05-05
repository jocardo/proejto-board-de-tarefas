package br.com.dio.persistence.dao;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARD_COLUMNS (name, `order, kind`, board_id) VALUES (?, ?, ?, ?)";
        try(var statement = connection.prepareStatement(sql)){
            var i = 1;
            statement.setString(i ++, entity.getName());
            statement.setInt(i ++, entity.getOrder());
            statement.setString(i ++, entity.getKind().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();
            if (statement instanceof StatementImpl impl){
                entity.setId(impl.getLastInsertId());
            }
            return entity;

        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } 
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException{
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, kind FROM BOARD_COLUMNS WHERE board_id = ? ORDER BY `order`";
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setOrder(resultSet.getInt("order"));
                entity.setKind(findByName(resultSet.getString("kind")));
                entities.add(entity);
            }
        }
        return null;
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException{
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = """
                      SELECT bc.id,
                             bc.name, 
                             bc.`order`, 
                             bc.kind,
                             (SELECT COUNT(c.id)
                                   FROM CARDS AS c 
                                        WHERE c.board_column_id = cb.id) AS cards_amount
                      FROM BOARD_COLUMNS AS bc
                           WHERE board_id = ? 
                           ORDER BY bc.`order`;
                """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()){
                var dto = new BoardColumnDTO(
                    resultSet.getLong("bc.id"),
                    resultSet.getString("bc.name"),
                    resultSet.getInt("bc.order"),
                    BoardColumnKindEnum.valueOf(resultSet.getString("bc.kind")),
                    resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
        }
        return null;
    }

    
    public Optional<BoardColumnEntity> findBydId(final Long boardId) throws SQLException{
        var sql = """
                        SELECT bc.name, bc.kind
                               ,c.ide, c.title, c.description 
                        FROM BOARD_COLUMNS AS bc
                        LEFT JOIN CARDS AS c ON c.board_column_id = bc.id
                        WHERE bc.id = ? ;
                  """;
        try(var statement = connection.prepareStatement(sql)){
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if (resultSet.next()){                
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.name"));
                entity.setKind(findByName(resultSet.getString("bc.kind")));
                do {
                    if (isNull(resultSet.getLong("c.id"))){
                        break;
                    }                        
                    var card = new CardEntity();
                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.title"));
                    card.setDescription(resultSet.getString("c.description"));
                    entity.getCards().add(card);
                } while (resultSet.next());
                return Optional.of(entity);
            }
        }
        return Optional.empty();
    }


}
