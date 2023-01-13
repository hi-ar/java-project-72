package hexlet.code.controllers;


import io.ebean.EbeanServer;
import io.javalin.http.Handler;

import java.io.PrintWriter;
import java.net.URL;

public class UrlController {

    public static Handler newurl = ctx -> {
        String urlString = ctx.formParam("url");
        URL url = new URL(urlString);
        URL toAdd = null;
        try {
            toAdd = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
        } catch (Exception e) {
            PrintWriter printWriter = ctx.res.getWriter(); //res - response
            printWriter.write(urlString + " is incorrect");    
        }
        //EbeanServer ebeanServer = DBSererInstance.getInstance();

        PrintWriter printWriter = ctx.res.getWriter(); //res - response
        printWriter.write(toAdd + " added");

    };
}
