package br.com.dio.persistence.migration;

import lombok.AllArgsConstructor;
import java.io.FileOutputStream;
import java.sql.Connection;

@AllArgsConstructor
public class MigrationStrategy {

    private final Connection connection;

    public void executeMigration() {
        // Configuração especifica para projeto de terminal
        var originalOut = System.out;
        var originalErr = System.err;
        try(var fos = new FileOutputStream("liquibase.log")) {
            System.setOut(new PrintStream(fos));
            System.setErr(new PrintStream(fos));
            try(var connerction = getConnection();
                var jdbcConnection = new JdbcConnection(connerction)
            ) {
                var liquibase = new Liquibase(
                    "/db/changelog/db.changelog-master.yml",
                    jdbcConnection
                    );
                    liquibase.update();
            } catch (SQLException | LiquibaseException e) {
                e.printStackTrace();
            }
            
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            System.setOut(originalOut);
            System.setErr(originalErr);
        }
    }

}
        