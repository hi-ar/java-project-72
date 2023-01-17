package hexlet.code.controllers;


import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlController {

    public static Handler newurl = ctx -> {
        String urlEntered = ctx.formParam("url");
        URL urlToSave;
        try {
            urlToSave = new URL(urlEntered);
        } catch (MalformedURLException e) {
            PrintWriter printWriter = ctx.res.getWriter(); //res - response
            printWriter.write(urlEntered + " is incorrect: " + e);
            return;
        }
        String protocol = urlToSave.getProtocol();
        String host = urlToSave.getHost();
        int port = urlToSave.getPort();
        urlToSave = new URL(protocol, host, port, "");

        Url present = new QUrl()
                .name.iequalTo(urlToSave.toString())
                .findOne();

        if(present == null || present.toString().isEmpty()) {
            PrintWriter printWriter = ctx.res.getWriter(); //res - response
            printWriter.write(urlToSave + " added");
        } else {
            Url u = new Url(new String(urlToSave.toString()));
            u.save();
            PrintWriter printWriter = ctx.res.getWriter(); //res - response
            printWriter.write(urlToSave + " added");
        }

//        EbeanServer ebeanServer = DBSererInstance.getInstance();
//        Database ebeanServer = ;

    };
}
