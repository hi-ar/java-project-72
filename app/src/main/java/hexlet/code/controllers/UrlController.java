package hexlet.code.controllers;


import io.ebean.EbeanServer;
import io.javalin.http.Handler;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlController {

    public static Handler newurl = ctx -> {
        String urlString = ctx.formParam("url");
        URL urlToSave;
        try {
            urlToSave = new URL(urlString);
        } catch (MalformedURLException e) {
            PrintWriter printWriter = ctx.res.getWriter(); //res - response
            printWriter.write(urlString + " is incorrect: " + e);
            return;
        }
        String protocol = urlToSave.getProtocol();
        String host = urlToSave.getHost();
        int port = urlToSave.getPort();
        urlToSave = new URL(protocol, host, port, "");

        //EbeanServer ebeanServer = DBSererInstance.getInstance();

        PrintWriter printWriter = ctx.res.getWriter(); //res - response
        printWriter.write(urlToSave + " added");

    };
}
