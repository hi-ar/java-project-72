package hexlet.code.controllers;

import io.javalin.http.Handler;

import java.io.PrintWriter;

public class RootController {
    public static Handler welcome = ctx -> {
        PrintWriter pw = ctx.res.getWriter();
        pw.write("Hello, Web");
    };
}
