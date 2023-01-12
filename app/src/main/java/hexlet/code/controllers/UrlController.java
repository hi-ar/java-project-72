package hexlet.code.controllers;


import io.ebean.EbeanServer;
import io.javalin.http.Handler;

import java.io.PrintWriter;

public class UrlController {

    public static Handler newurl = ctx -> {
        String url = ctx.bodyAsClass(String.class);
        //EbeanServer ebeanServer = DBSererInstance.getInstance();

        PrintWriter printWriter = ctx.res.getWriter(); //res - response
        printWriter.write(url + "added");

    };
}
