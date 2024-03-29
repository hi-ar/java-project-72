package hexlet.code;

import hexlet.code.controllers.ChecksController;
import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

public class App {

    private static final String DEF_PORT = "5001";
    private static final String DEVELOPMENT = "development";
    private static final String PRODUCTION = "production";

    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if (!isProduction()) { //Utils. If APP_ENV not "production"
                config.plugins.enableDevLogging();  //enable logging for development
            }
            config.staticFiles.enableWebjars(); // ??? webjars:bootstrap (design pages)
            //Connect Thy to Javalin on Javalin instance creation
            JavalinThymeleaf.init(getTemplateEngine());
        });

        addRoutes(app); //adding routing

        app.before(ctx -> {   //???
            ctx.attribute("ctx", ctx);
        });
        return app;
    }

    private static TemplateEngine getTemplateEngine() {
        // Create Thy instance and plug in dialects
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());
        // Using ClassLoader Template Resolver, configure the template resover so that the files in the template
        // directory are processed. Add a template resolver to the template engine
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setCharacterEncoding("UTF-8");
        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController.welcome);

        app.routes(() -> {
            path("urls", () -> {
                get(UrlController.listUrls);
                post(UrlController.createUrl);
                path("{id}", () -> {
                    get(UrlController.showUrl);
                    post("checks", ChecksController.createCheck);
                });
            });
        });
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", DEF_PORT);
        return Integer.valueOf(port);
    }

    private static boolean isProduction() {
        return getMode().equals(PRODUCTION);
    }

    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", DEVELOPMENT);
    }

}

