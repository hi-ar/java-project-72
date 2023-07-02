package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.PagedList;
import io.javalin.http.Handler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UrlController {

    public static final String ALERT_INCORR_URL = "Некорректный адрес";
    public static final String ALERT_EXISTS_URL = "Страница уже существует";
    public static final String ALERT_NOT_FOUND = "Нет соединения с БД или отсутствует запись с ID: ";
    public static final String ALERT_SUCCES_ADD = "Страница успешно добавлена";
    private static Logger log = LoggerFactory.getLogger(UrlController.class);
    public static Handler listUrls = ctx -> {
        int page = ctx.queryParamAsClass("page", Integer.class).getOrDefault(1);
        int rowsPerPage = 10;
        int offset = (page - 1) * rowsPerPage;

        PagedList<Url> pagedUrls = new QUrl()
                .setFirstRow(offset) //first url on current page 0/10/20/...
                .setMaxRows(rowsPerPage)
                .orderBy().id.asc()
                .findPagedList();
        log.info(String.valueOf(pagedUrls.getTotalCount()) + " Urls loaded from db");
        List<Url> listUrls = pagedUrls.getList();

        ctx.attribute("listUrls", listUrls);
        ctx.attribute("page", page);
        ctx.render("urls/list.html");
        log.info("Urls displayed");
    };

    public static Handler createUrl = ctx -> {
        String stringEntered = ctx.formParam("url");
        URL urlEntered = null;

        try {
            urlEntered = new URL(stringEntered);
            log.info("string was successfully converted to Url: " + urlEntered.toString());
        } catch (MalformedURLException e) {   // error if incorrect url (w/o http://)
            log.error("string {} wasn't converted to Url", stringEntered);
            ctx.attribute("url", stringEntered);
            ctx.sessionAttribute("flash", ALERT_INCORR_URL);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("/index.html");
            return;
        }
        // cheking already exists?
        String protocol = urlEntered.getProtocol();
        String host = urlEntered.getHost();
        int port = urlEntered.getPort();
        String urlNormalized = protocol + "://" + host + (port == -1 ? "" : ":" + port);
        log.info("normalized Url is " + urlNormalized);

        Url present = new QUrl()
                .name.iequalTo(urlNormalized)
                .findOne();
        if (present != null) {
            //error if url already exists:
            ctx.sessionAttribute("flash", ALERT_EXISTS_URL);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("/index.html");
            log.warn("Such Url {} already exists", urlNormalized);

        } else { //adding if doesn't exists:
            Url u = new Url(urlNormalized);
            u.save();
            ctx.sessionAttribute("flash", ALERT_SUCCES_ADD);
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect("/urls");
            log.info("Url {} successfully added", urlEntered);
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
            log.info("Details about Url {} with id {} successfully displayed", currentUrl, currentUrlId);
        } catch (Exception e) {
            ctx.sessionAttribute("flash", ALERT_NOT_FOUND + currentUrlId);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.render("/index.html");
            log.error("Url with id {} not found", currentUrlId);
        }
    };
}
