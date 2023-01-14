package hexlet.code.controllers;


import io.ebean.EbeanServer;
import io.javalin.http.Handler;

import java.io.PrintWriter;
import java.net.URL;

public class UrlController {

    public static Handler newurl = ctx -> {
        String urlString = ctx.formParam("url");
        String protocol = new String();
        String host = new String();
        int port = new URL(urlString).getPort();
        try {
            protocol = new URL(urlString).getProtocol();
            host = new URL(urlString).getHost();
        } catch (RuntimeException e) {
            PrintWriter printWriter = ctx.res.getWriter(); //res - response
            printWriter.write(urlString + " is incorrect: " + protocol + " " + host);
        }

//        if (protocol.isEmpty() || host.isEmpty()) {
//            PrintWriter printWriter = ctx.res.getWriter(); //res - response
//            printWriter.write(urlString + " is incorrect: " + protocol + " " + host);
//        }
        URL toAdd = new URL(protocol, host, port, "");

        //EbeanServer ebeanServer = DBSererInstance.getInstance();

        PrintWriter printWriter = ctx.res.getWriter(); //res - response
        printWriter.write(toAdd + " added");

    };
}
