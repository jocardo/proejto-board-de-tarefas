package br.com.dio.ui;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in);

    public void execute() throws SQLException{
        System.out.println("Welcome to the Board Management System!");
        var option =  -1;
        while (true){
            System.out.println("Please select an option:");
            System.out.println("1. Create Board");
            System.out.println("2. Select Board Existing");
            System.out.println("3. Delete Board");
            System.out.println("4. Exit");  
            option = scanner.nextInt();

            switch (option){
                case 1 -> createBoard();
                case 2 -> selectBoard();
                case 3 -> deleteBoard();
                case 4 -> System.exit(0);
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private void createBoard() throws SQLException{
        var entity = new BoardEntity();
        System.out.println("Inform the name of the board:");
        entity.setName(scanner.next());

        System.out.println("Will your board have columns other than the standard 3? If so, please enter how many, otherwise enter '0'");
        var additionalColumns = scanner.nextInt();

        List<BoardColumnEntity> columns = new ArrayList<>();

        System.out.println("Enter the name of the initial column of the board:");
        var initialColumnName = scanner.next();
        var initialColumn = createColumn(initialColumnName, BoardColumnKindEnum.INITIAL, 0);
        columns.add(initialColumn);

        for (int i = 0; i < additionalColumns; i++){            
            System.out.println("Enter the name of the pending task column:");
            var pendingColumnName = scanner.next();
            var pendingColumn = createColumn(pendingColumnName, BoardColumnKindEnum.PENDEING, i + 1);
            columns.add(pendingColumn);
        }

        System.out.println("Enter the name of the final column of the board:");
        var finalColumnName = scanner.next();
        var finalColumn = createColumn(finalColumnName, BoardColumnKindEnum.FINAL, additionalColumns + 1);
        columns.add(finalColumn);

        System.out.println("Enter the name of the final cancel of the board:");
        var cancelColumnName = scanner.next();
        var cancelColumn = createColumn(cancelColumnName, BoardColumnKindEnum.CANCEL, additionalColumns + 2);
        columns.add(cancelColumn);

        entity.setBoardColumns(columns);
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            service.insert(entity);
        }

    }

    private void selectBoard() throws SQLException{        
        System.out.println("Inform the id of the board to selected:");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var queryService = new BoardQueryService(connection);
            var optional = queryService.findById(id);
            optional.ifPresentOrElse(
                b -> new BoardMenu(b).execute(),
                 () -> System.out.println("Board not found."));
        }
    }

    private void deleteBoard() throws SQLException {
        System.out.println("Inform the ID of the board to delete:");
        var id = scanner.nextLong();
        try(var connection = getConnection()){
            var service = new BoardService(connection);
            if (service.delete(id)){
                System.out.println("Board deleted successfully.");
            } else {
                System.out.println("Board not found.");
            }
        }
        // Logic to delete a board
    }

    private BoardColumnEntity createColumn(final String name, final BoardColumnKindEnum kind, final int order) {
        var boardColumn = new BoardColumnEntity();
        boardColumn.setName(name);
        boardColumn.setOrder(order);
        boardColumn.setKind(kind);
        return boardColumn;
    }
}