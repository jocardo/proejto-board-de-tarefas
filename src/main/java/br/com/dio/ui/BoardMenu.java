package br.com.dio.ui;

import br.com.dio.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BoardMenu {

    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");

    private final BoardEntity entity;

    public void execute() {
        try {
            System.out.println("welcome to the Board %s, please select an option:",entity.getID());
            var option =  -1;
            while (option != 9){
                System.out.println("Please select an option:");
                System.out.println("1. Create Card");
                System.out.println("2. Move Card");
                System.out.println("3. Block Card");
                System.out.println("4. Unblock Card");
                System.out.println("5. Cancel Card");;
                System.out.println("6. View Board");
                System.out.println("7. View Columns with Cards");
                System.out.println("8. View Cards");
                System.out.println("9. Go Back to Main Menu");
                System.out.println("10. Exit");  
                option = scanner.nextInt();

                switch (option){
                    case 1 -> createCard();
                    case 2 -> moveCardToNextColumn();
                    case 3 -> blockCard();
                    case 4 -> unblockCard();
                    case 5 -> cancelCard();
                    case 6 -> showBoard();
                    case 7 -> showColumn();
                    case 8 -> showCard();
                    case 9 -> System.out.println("Going back to the main menu...");
                    case 10 -> System.exit(0);
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException ex){
            ex.printStackTrace();
            System.exit(0);
        }
    }
    
    private void createCard() throws SQLException {
        var card = new CardEntity();
        System.out.println("Enter the card title:");
        card.setTitle(scanner.next());
        System.out.println("Enter the card description:");
        card.setDescription(scanner.next());
        card.setBoardColumn(entity.getInitialColumn());
        try(var connection = getConnection()){
            new CardService(connection).insert(card);
        }
    }

    private void moveCardToNextColumn() throws SQLException {
        System.out.println("Enter the card ID to move:");
        var cardId = scanner.nextLong();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn().getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void blockCard() throws SQLException {
        System.out.println("Enter the card ID to block:");
        var cardId = scanner.nextLong();
        System.out.println("Enter the reason for blocking the card:");
        var reason = scanner.next();
        var boardColumnsInfo = entity.getBoardColumns().stream()
        .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
        .toList();
        try (var connection = getConnection()){
            new BlockDAO(connection).block(reason, cardId, boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void unblockCard() throws SQLException {
        System.out.println("Enter the card ID to unblock:");
        var cardId = scanner.nextLong();
        System.out.println("Enter the reason for unblocking the card:");
        var reason = scanner.next();
        try(var connection = getConnection()){
            new BlockDAO(connection).unblock(reason, cardId);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void cancelCard() throws SQLException {
        System.out.println("Enter the card ID to cancel:");
        var cardId = scanner.nextLong();
        var cancelColumn = entity.getCancelColumn();
        var boardColumnsInfo = entity.getBoardColumns().stream()
                .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                .toList();
        try(var connection = getConnection()){
            new CardService(connection).cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
        } catch (RuntimeException ex){
            System.out.println(ex.getMessage());
        }
    }

    private void showBoard() throws SQLException {
        try(var connection = getConnection()){
            var optional = new BoardQueryService(connection).showBoardDetails(entity.getId());
            optional.isPresentaOrElse(b -> {
                System.out.println("Board [%s,%s] \n", b.id(), b.name());
                b.columns().forEach(c -> 
                    System.out.println("Column [%s] type: [%s] have [%s] cards \n", c.name(), c.kind(), c.cardsAmount())
                );
            });
                
        }
    }

    private void showColumn() throws SQLException {
        System.out.println("Choose a board column %s", entity.getId());
        var columnIds = entity.getBoardColumns().stream()
                .map(BoardColumnEntity::getId)
                .toList();
                // .collect(Collectors.toList());
        var selectedColumn = -1L;
        while (!columnIds.contains(selectedColumn)){
            entity.getBoardColumns().forEach(c -> 
                System.out.println("%s - %s [%s]", c.getId(), c.getName(), c.getKind())
            );
        }
        try(var connection = getConnection()){
            var column = new BoardColumnQueryService(connection).findById(selectedColumn);
            column.ifPresentOrElse(c -> {
                System.out.println("Column [%s] type: [%s] \n", c.getName(), c.getKind());
                co.getCards().forEach(card -> 
                    System.out.println("Card %s - %s \nDescription: %s", card.getId(), card.getTitle(), card.getDescription()));
            });
        }
    }

    private void showCard() throws SQLException {
        System.out.println("Choose id of the card to show details:");
        var selectedCardId = scanner.nextLong();
        try(var connection = getConnection()){
            new CardQueryService(connection).findById(selectedCardId)
                .ifPresentOrElse(c -> {
                    System.out.println("Card [%s] - [%s] ", c.id(), c.title());
                    System.out.println("Description: [%s] ", c.getDescription());
                    System.out.println(c.blocked() ? "Card is blocked. Reason" + c.blockReason() : "Card is not blocked");
                    System.out.println("You have been blocked %s times ", c.blocksAmount() );
                    System.out.println("It is currently in the column %s - %s", c.columnId(), c.columnName());
                }, () -> System.out.println("Card not found"));
        }

    }

}