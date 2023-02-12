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

public class ChecksController {

    public static Handler createCheck = ctx -> {
        //PrintWriter printWriter = ctx.res.getWriter(); //debug

        // getting id of Url (from path) and it's instance (from db)
        long currentUrlId = ctx.pathParamAsClass("id", Integer.class).getOrDefault(0);
        Url currentUrl = new QUrl().id.equalTo(currentUrlId).findOne();

        //making new check, saving it to db of checks
        UrlCheck newCheck = makeNewCheck(currentUrl);
        //printWriter.write("1/2 new check created at " + newCheck.getCreatedAt().toString()); //debug

        //getting existing check ids list for current Url
        List<UrlCheck> currentUrlChecks = currentUrl.getUrlCheckList();
        //printWriter.write("\n2/2 list of checks size : " + currentUrlChecks.size()); //debug

        ctx.attribute("url", currentUrl);
        ctx.render("urls/show.html");
        ctx.sessionAttribute("flash", "Проверка добавлена");
        ctx.sessionAttribute("flash-type", "success");
    };

    private static UrlCheck makeNewCheck(Url url) {
        HttpResponse<String> response = Unirest
                .get(url.getName())
                .asString();

        int statusCode = response.getStatus();

        Document doc = Jsoup.parse(response.getBody()); //parsing html into DOM (doc object model)

        String title = doc.title();
        String h1 = doc.getElementsByTag("h1").first().text();
        String content = doc.getElementsByAttributeValue("name", "description").attr("content").toString();

        UrlCheck newCheck = new UrlCheck(statusCode, title, h1, content, url);
        newCheck.save();
        return newCheck;
    }
}
