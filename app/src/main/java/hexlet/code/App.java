package hexlet.code;

import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlController;
import io.javalin.Javalin;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

/*

 */
public class App {
    public static void main(String[] args) {
        Javalin app = getApp();
        app.start(getPort());
    }
    private static String getMode() {
        return System.getenv().getOrDefault("APP_ENV", "development");
    }

    private static boolean isProduction() {
        return getMode().equals("production");
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            if(!isProduction()) { //if APP_ENV not "production"
                config.enableDevLogging();  //enable logging for development
            }
            config.enableWebjars(); // ??? webjars:bootstrap (design pages)
            //Connect Thy to Javalin on Javalin instance creation
            JavalinThymeleaf.configure(getTemplateEngine());
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
        templateEngine.addTemplateResolver(templateResolver);

        return templateEngine;
    }

    private static void addRoutes(Javalin app) {
        app.get("/", RootController.welcome);
        app.post("/urls", UrlController.newurl);
    }

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "5001");
        return Integer.valueOf(port);
    }

}

