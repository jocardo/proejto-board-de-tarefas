package br.com.dio;

import br.com.dio.persistence.migration.MigrationStrategy;

import java.sql.SQLException;

import br.com.dio.persistence.config.ConnectionConfig.getConnection;

public class Main {

    public static void main(String[] args) thows SQLException {
        try(var connection = getConnection()){
            new MigrationStrategy(connection).executeMigration();
        }

        new MainMenu().execute();
    }

}
