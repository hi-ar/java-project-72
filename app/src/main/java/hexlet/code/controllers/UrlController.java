package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.ebean.PagedList;
import io.javalin.http.ExceptionHandler;
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
        ctx.render("urls/list.html");
    };

    public static Handler createUrl = ctx -> {
        String urlEntered = ctx.formParam("url");
        URL urlTemp = null;
        String incorrectErr = "Некорректный URL";
        String existsErr = "Страница уже существует";
        String successfulAdd = "Страница успешно добавлена";

        try {
            urlTemp = new URL(urlEntered);
        } catch (MalformedURLException e) {   // error if incorrect url (w/o http://)
            ctx.attribute("url", urlEntered);
            ctx.sessionAttribute("flash", incorrectErr);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("/index.html");
            return;
//            PrintWriter printWriter = ctx.res.getWriter(); //res - response
//            printWriter.write(urlEntered + " is incorrect: " + e);
        }
        // cheking already exists?
        String protocol = urlTemp.getProtocol();
        String host = urlTemp.getHost();
        int port = urlTemp.getPort();
        urlTemp = new URL(protocol, host, port, "");

        Url present = new QUrl()
                .name.iequalTo(urlTemp.toString())
                .findOne();
        //adding if doesn't exists:
        if (present == null || present.toString().isEmpty()) {
            Url u = new Url(new String(urlTemp.toString()));
            u.save();
            ctx.sessionAttribute("flash", successfulAdd);
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls");
        } else {
            //error if url already exists:
            ctx.sessionAttribute("flash", existsErr);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/");
        }
    };

    public static Handler showUrl = ctx -> {
        PrintWriter printWriter = ctx.res.getWriter();
        long idToFind = ctx.pathParamAsClass("id", long.class).getOrDefault(null);
        printWriter.write("id of url is: " + idToFind);
        //Url currentUrl = null;
        //try {
        Url currentUrl = new QUrl()
                .id.equalTo(idToFind)
                .findOne();
        printWriter.write("\ncurrent url is: " + currentUrl.getName());
//        } catch (Exception e) {
//            printWriter.write("Exc! trying to get Url instance from db Url: " + e);
//            return;
//        }

//        if (currentUrl.getChecksIds().size() != 0) {
        try {
        List<UrlCheck> currentUrlChecks = currentUrl.getChecksIds().stream()
                .map(checkId -> new QUrlCheck().id.equalTo(checkId).findOne())
                .toList();
//        ctx.attribute("currentUrlChecks", currentUrlChecks);
        printWriter.write("\nsize of checks list is: " + currentUrlChecks.size());
        } catch (IllegalStateException e) {
            printWriter.write("Exc! Trying to get list of checks by its ids, from db Checks: " + e);
            return;
        }

//        ctx.attribute("url", currentUrl);
//        ctx.render("urls/show.html");
    };
}
