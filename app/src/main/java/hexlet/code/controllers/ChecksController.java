package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChecksController {
    public static final String ALERT_INCORR_URL = "Некорректный адрес";
    public static final String ALERT_ID_NOTSET = "ID страницы не определен: ";
    public static final String ALERT_NOT_FOUND = "Нет соединения с БД или отсутствует запись с ID: ";
    public static final String ALERT_SUCCES_CHECK = "Страница успешно проверена";
    private static Logger log = LoggerFactory.getLogger(ChecksController.class);
    public static Handler createCheck = ctx -> {

        // getting id of Url from path, and it's instance (from db)
        Integer currentUrlId = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
        if (currentUrlId == null) {
            ctx.redirect("/");
            ctx.sessionAttribute("flash", ALERT_ID_NOTSET);
            ctx.sessionAttribute("flash-type", "danger");
            log.warn("Attempt to get check for Url with NULL id");
            return;
        }

        Url currentUrl = new QUrl().id.equalTo(currentUrlId).findOne();
        if (currentUrl == null) {
            ctx.redirect("/");
            ctx.sessionAttribute("flash", ALERT_NOT_FOUND + currentUrlId);
            ctx.sessionAttribute("flash-type", "danger");
            log.warn("Attempt to get check for not existing Url id {}", currentUrlId);
            return;
        }

        //making new check, save it to db of checks
        try {
            HttpResponse<String> response = Unirest
                    .get(currentUrl.getName())
                    .asString();

            Document doc = Jsoup.parse((String) response.getBody());
            log.info("Got Http resp for {}, and successfully parsed", currentUrl);

            String title = doc.title().isEmpty() ? "" : doc.title();
            String h1 = doc.getElementsByTag("h1").isEmpty() ? ""
                    : doc.getElementsByTag("h1").first().text();
            String content = doc.getElementsByAttributeValue("name", "description").isEmpty() ? ""
                    : doc.getElementsByAttributeValue("name", "description").attr("content")
                    .toString();
            int statusCode = response.getStatus();
            log.info("Got title: {}, h1: {}, content: {}, status-code: {}", title, h1, content, statusCode);

            UrlCheck newCheck = new UrlCheck(statusCode, title, h1, content, currentUrl);
            newCheck.save();
            log.info("successfully saved the new Check to list of checks");

        } catch (Exception e) {
            ctx.sessionAttribute("flash", ALERT_INCORR_URL);
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect("/urls/" + currentUrlId);
            log.error("Can't make the new check for Url {}", currentUrl);
            return;
        }

        //getting existing check ids list for current Url
        //List<UrlCheck> currentUrlChecks = currentUrl.getUrlCheckList(); //debug
        //printWriter.write("\n2/2 list of checks size : " + currentUrlChecks.size()); //debug

        ctx.sessionAttribute("flash", ALERT_SUCCES_CHECK);
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect("/urls/" + currentUrlId);
    };
}
