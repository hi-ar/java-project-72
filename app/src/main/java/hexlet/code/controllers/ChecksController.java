package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.javalin.http.Handler;

import java.util.ArrayList;
import java.util.List;

public class ChecksController {

    public static Handler createCheck = ctx -> {
        //getting id of Url (from path) and it's instance (from db)
        long idOfUrl = ctx.pathParamAsClass("id", Integer.class).getOrDefault(0);
        //long idOfUrl = ctx.queryParamAsClass("id", Integer.class).getOrDefault(0);
        Url currentUrl = new QUrl().id.equalTo(idOfUrl).findOne();

        //making new check, saving it to db of checks
        UrlCheck newCheck = makeNewCheck(currentUrl.getName());

        //id of last check saving to list, that's store in current Url
        int idOfCheck = (int) newCheck.getId();
        currentUrl.addCheck(idOfCheck);

        //ctx.redirect("/urls/" + idOfUrl);
        ctx.render("/urls/" + idOfUrl + "/show.html");
        ctx.sessionAttribute("flash", "Проверка добавлена");
        ctx.sessionAttribute("flash-type", "success");
    };

    private static UrlCheck makeNewCheck(String urlString){
        int statusCode = 0;
        String title = null;
        String h1 = null;
        String description = null;
        String url = urlString;
        UrlCheck newCheck = new UrlCheck(statusCode, title, h1, description, url);
        newCheck.save();
        return newCheck;
    }
}
