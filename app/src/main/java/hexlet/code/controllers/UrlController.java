package hexlet.code.controllers;


import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UrlController {

    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = 10;
        int offset = (page - 1) * rowsPerPage;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(offset)
                .setMaxRows(rowsPerPage)
                .orderBy()
                .id.asc()
                .findPagedList();

        List<Url> listUrls = pagedUrls.getList();

        ctx.attribute("listUrls", listUrls);
        ctx.attribute("page", page);
        ctx.render("/urls/list.html");
    };

    public static Handler createUrl = ctx -> {
        String urlEntered = ctx.formParam("url");
        URL urlTemp = null;
        String incorrectErr = "Некорректный URL";
        String existsErr = "Страница уже существует";
        String successfulAdd = "Страница успешно добавлена";

        try {
            urlTemp = new URL(urlEntered);
        } catch (MalformedURLException e) {
            ctx.attribute("name", urlEntered);
            ctx.sessionAttribute("flash", incorrectErr);
//            PrintWriter printWriter = ctx.res.getWriter(); //res - response
//            printWriter.write(urlEntered + " is incorrect: " + e);
//            return;
        }
        String protocol = urlTemp.getProtocol();
        String host = urlTemp.getHost();
        int port = urlTemp.getPort();
        urlTemp = new URL(protocol, host, port, "");

        Url present = new QUrl()
                .name.iequalTo(urlTemp.toString())
                .findOne();

        if (present == null || present.toString().isEmpty()) { //if no such url in DB
            Url u = new Url(new String(urlTemp.toString()));
            u.save();
            ctx.sessionAttribute("flash", successfulAdd);
            ctx.redirect("/urls");
//            PrintWriter printWriter = ctx.res.getWriter(); //res - response
//            printWriter.write(urlTemp + " added");
        } else {
            ctx.sessionAttribute("flash", existsErr);
//            PrintWriter printWriter = ctx.res.getWriter(); //res - response
//            printWriter.write(urlTemp + " already exists");
//            ctx.render("/urls");
        }
    };
}
