package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.javalin.http.Handler;
import kong.unirest.JsonObjectMapper;

import java.util.List;

public class ChecksController {

    public static Handler createCheck = ctx -> {

        // getting id of Url (from path) and it's instance (from db)
        long currentUrlId = ctx.pathParamAsClass("id", Integer.class).getOrDefault(0);
        Url currentUrl = new QUrl().id.equalTo(currentUrlId).findOne();

        //making new check, saving it to db of checks
        UrlCheck newCheck = makeNewCheck(currentUrlId);

        //saving new check as last url check in current url instance
        String jsonLastUrlCheck = new JsonObjectMapper().writeValue(newCheck);
        new QUrl().id.equalTo(currentUrlId).asUpdate().set("jsonLastUrlCheck", jsonLastUrlCheck).update();

        //getting existing check ids list for current Url
        List<UrlCheck> currentUrlChecks = new QUrlCheck().urlId.equalTo(currentUrlId).findList();

        ctx.attribute("currentUrlChecks", currentUrlChecks);
        ctx.attribute("url", currentUrl);
        ctx.render("urls/show.html");
        ctx.sessionAttribute("flash", "Проверка добавлена");
        ctx.sessionAttribute("flash-type", "success");
    };

    private static UrlCheck makeNewCheck(long idOfUrl) {
        int statusCode = 0;
        String title = "";
        String h1 = "";
        String description = "";
        UrlCheck newCheck = new UrlCheck(statusCode, title, h1, description, idOfUrl);
        newCheck.save();
        return newCheck;
    }
}
