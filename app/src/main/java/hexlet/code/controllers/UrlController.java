package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class UrlController {

    public static final String ALERT_INCORR_URL = "Некорректный адрес";
    public static final String ALERT_EXISTS_URL = "Страница уже существует";
    public static final String ALERT_NOT_FOUND = "Нет соединения с БД или отсутствует запись с ID: ";
    public static final String ALERT_SUCCES_ADD = "Страница успешно добавлена";
    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = 10;
        int offset = (page - 1) * rowsPerPage;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(offset) //first url 0 or 10
                .setMaxRows(rowsPerPage)
                .orderBy().id.asc()
                .findPagedList();

        List<Url> listUrls = pagedUrls.getList();

        ctx.attribute("listUrls", listUrls);
        ctx.attribute("page", page);
        ctx.render("urls/list.html");
    };

    public static Handler createUrl = ctx -> {
        String urlEntered = ctx.formParam("url");
        URL urlTemp = null;

        try {
            urlTemp = new URL(urlEntered);
        } catch (MalformedURLException e) {   // error if incorrect url (w/o http://)
            ctx.attribute("url", urlEntered);
            ctx.sessionAttribute("flash", ALERT_INCORR_URL);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("/index.html");
            return;
//            PrintWriter printWriter = ctx.res.getWriter(); //res - response
//            printWriter.write(urlEntered + " is incorrect: " + e); //debug
        }
        // cheking already exists?
        String protocol = urlTemp.getProtocol();
        String host = urlTemp.getHost();
        int port = urlTemp.getPort();
        urlTemp = new URL(protocol, host, port, "");

        Url present = new QUrl()
                .name.iequalTo(urlTemp.toString())
                .findOne();
        if (present != null) {
            //error if url already exists:
            ctx.sessionAttribute("flash", ALERT_EXISTS_URL);
            ctx.sessionAttribute("flash-type", "danger");
//            ctx.redirect("/");
            ctx.render("/index.html");

        } else { //adding if doesn't exists:
            Url u = new Url(urlTemp.toString());
            u.save();
            ctx.sessionAttribute("flash", ALERT_SUCCES_ADD);
            ctx.sessionAttribute("flash-type", "success");
//            ctx.attribute("listUrls", listUrls);
//            ctx.render("urls/list.html");
            ctx.redirect("/urls");
        }
    };

    public static Handler showUrl = ctx -> {
        long currentUrlId = ctx.pathParamAsClass("id", long.class).getOrDefault(null);
        try {
            Url currentUrl = new QUrl()
                    .id.equalTo(currentUrlId)
                    .findOne();
            ctx.attribute("url", currentUrl);
            ctx.render("urls/show.html");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", ALERT_NOT_FOUND + currentUrlId);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("/index.html");
        }
    };
}
