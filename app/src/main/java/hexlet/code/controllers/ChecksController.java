package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import io.javalin.http.Handler;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

import static hexlet.code.Utils.ALERT_ID_NOTSET;
import static hexlet.code.Utils.ALERT_NOT_FOUND;
import static hexlet.code.Utils.ALERT_SUCCES_CHECK;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

public class ChecksController {

    public static Handler createCheck = ctx -> {
        //PrintWriter printWriter = ctx.res.getWriter(); //debug

        // getting id of Url (from path) and it's instance (from db)
        long currentUrlId = ctx.pathParamAsClass("id", Integer.class).getOrDefault(0);
        if (currentUrlId == 0) {
            ctx.redirect("/urls/" + currentUrlId);
            ctx.sessionAttribute("flash", ALERT_ID_NOTSET + currentUrlId);
            ctx.sessionAttribute("flash-type", "danger");
        }

        Url currentUrl = new QUrl().id.equalTo(currentUrlId).findOne();
        if (currentUrl == null) {
            ctx.redirect("/urls/" + currentUrlId);
            ctx.sessionAttribute("flash", ALERT_NOT_FOUND + currentUrlId);
            ctx.sessionAttribute("flash-type", "danger");
        }

        //making new check, saving it to db of checks
        makeNewCheck(currentUrl);

        //getting existing check ids list for current Url
        List<UrlCheck> currentUrlChecks = currentUrl.getUrlCheckList();
        //printWriter.write("\n2/2 list of checks size : " + currentUrlChecks.size()); //debug

        ctx.sessionAttribute("flash", ALERT_SUCCES_CHECK);
        ctx.sessionAttribute("flash-type", "success");
        ctx.attribute("url", currentUrl);
        ctx.render("urls/show.html");
        //ctx.redirect("/urls/" + currentUrlId);
    };

    private static UrlCheck makeNewCheck(Url url) throws Exception {

        HttpResponse<String> response = null;

        try {
            response = Unirest
                    .get(url.getName())
                    .asString();
        } catch (Exception e) {
            System.out.println("~~~UNIREST GET ERR: " + e.getMessage());
            //in this case, I can't get statuscode 404 etc
        }

        int statusCode = response == null ? HTTP_NOT_FOUND : response.getStatus();

        String title = "";
        String h1 = "";
        String content = "";

        if (response != null && !response.getBody().isEmpty()) {
            Document doc = null; //parsing html into DOM (doc object model)
            try {
                doc = Jsoup.parse((String) response.getBody());
                if (doc != null) {
                    title = doc.title().isEmpty() ? "" : doc.title();
                    h1 = doc.getElementsByTag("h1").isEmpty() ? ""
                            : doc.getElementsByTag("h1").first().text();
                    content = doc.getElementsByAttributeValue("name", "description").isEmpty() ? ""
                            : doc.getElementsByAttributeValue("name", "description").attr("content")
                            .toString();
                }
            } catch (Exception e) {
                System.out.println("~~~PARSING ERRRR: " + e.getMessage());
            }
        }

        UrlCheck newCheck = new UrlCheck(statusCode, title, h1, content, url);
        newCheck.save();
        return newCheck;
    }
}
