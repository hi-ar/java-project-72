package hexlet.code.controllers;

import hexlet.code.domain.Url;
import hexlet.code.domain.UrlCheck;
import hexlet.code.domain.query.QUrl;
import hexlet.code.domain.query.QUrlCheck;
import io.javalin.http.Handler;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ChecksController {

    public static Handler createCheck = ctx -> {
        PrintWriter printWriter = ctx.res.getWriter(); //res - response
        // getting id of Url (from path) and it's instance (from db)
        long idOfUrl = ctx.pathParamAsClass("id", Integer.class).getOrDefault(0);
        Url currentUrl = null;
        try {
            currentUrl = new QUrl().id.equalTo(idOfUrl).findOne();
        } catch (Exception e) {
//            printWriter.write("\nexc: " + e);
        }

//        printWriter.write(currentUrl.getName() + ", id: " + idOfUrl);

        //making new check, saving it to db of checks
        UrlCheck newCheck = makeNewCheck(currentUrl.getName());

        //getting id of last check
        int idOfLastCheck = (int) newCheck.getId();
        printWriter.write("\nid of check: " + idOfLastCheck);
        //getting existing check ids list for current Url, adding last check
        List<Integer> currentUrlCheckIds = currentUrl.getChecksIds();
        currentUrlCheckIds.add(idOfLastCheck);
        //saving check ids list to db
        new QUrl().id.equalTo(idOfUrl).asUpdate().set("checks", currentUrlCheckIds);
        printWriter.write("\nList of check ids: " + currentUrl.getChecksIds().toString());

//        ctx.redirect("/urls/" + idOfUrl);
        ctx.render("/urls/show.html");
        ctx.sessionAttribute("flash", "Проверка добавлена");
        ctx.sessionAttribute("flash-type", "success");
    };

    private static UrlCheck makeNewCheck(String urlString){
        int statusCode = 0;
        String title = "";
        String h1 = "";
        String description = "";
        String url = urlString;
        UrlCheck newCheck = new UrlCheck(statusCode, title, h1, description, url);
        newCheck.save();
        return newCheck;
    }
}
