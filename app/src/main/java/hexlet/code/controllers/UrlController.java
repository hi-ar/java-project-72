package hexlet.code.controllers;


import io.ebean.EbeanServer;
import io.javalin.http.Handler;

import java.io.PrintWriter;
import java.net.URL;

public class UrlController {

    public static Handler newurl = ctx -> {
        String urlString = ctx.formParam("url");
        String protocol = new URL(urlString).getProtocol();
        String host = new URL(urlString).getHost();
        int port = new URL(urlString).getPort();

        if (protocol.isEmpty() || host.isEmpty()) {
            PrintWriter printWriter = ctx.res.getWriter(); //res - response
            printWriter.write(urlString + " is incorrect: " + protocol + " " + host);
        }

        URL toAdd = new URL(protocol, host, port, "");

        //EbeanServer ebeanServer = DBSererInstance.getInstance();

        PrintWriter printWriter = ctx.res.getWriter(); //res - response
        printWriter.write(toAdd + " added");

    };
}
