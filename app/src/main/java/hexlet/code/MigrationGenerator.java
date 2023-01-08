package hexlet.code;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

public class MigrationGenerator {
    public static void main(String[] args) throws IOException {
        DbMigration dbMig = DbMigration.create();

        dbMig.addPlatform(Platform.H2, "h2");
        dbMig.addPlatform(Platform.POSTGRES, "postgres");

        dbMig.generateMigration();
    }
}
