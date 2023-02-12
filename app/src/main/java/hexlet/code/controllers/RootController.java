package hexlet.code.controllers;

import io.javalin.http.Handler;

public class RootController {
//    public static Handler welcome = ctx -> {
//        PrintWriter pw = ctx.res.getWriter();
//        pw.write("Hello, Web");
//    };

    public static Handler welcome = ctx -> {
        ctx.render("index.html");
    };
}
