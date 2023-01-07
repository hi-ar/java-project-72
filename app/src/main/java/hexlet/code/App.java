package hexlet.code;

import hexlet.code.controllers.RootController;
import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;

/*

 */
public class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(JavalinConfig::enableDevLogging); //with logging
        addRoutes(app); //adding routing
        return app;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController.welcome);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5001");
        return Integer.valueOf(port);
    }

}

