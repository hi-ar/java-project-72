package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Transaction;
import io.javalin.Javalin;
//import io.javalin.testtools.JavalinTest;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static hexlet.code.App.getApp;
import static org.assertj.core.api.Assertions.assertThat;


public class AppTest {
    private static String wordPageAnalizer = "Анализатор страниц";
    private static String wordListUrls = "Последняя проверка";
    private static String wordIncorrect = "Некорректный URL";
    private static String wordExists = "Страница уже существует";
    private static String wordSuccess = "Страница успешно добавлена";
    private static String urlString = "https://ru.hexlet.io/projects/72/members/27753?step=6";
    private static String urlExpectString = "https://ru.hexlet.io";
    private static String urlWrongString = "www.hexlet.io";

    private static Javalin app;
    private static String baseUrl;
    private static Database db;
    private static Transaction transaction;

    @BeforeAll
    public static void beforeAll() {
        app = getApp();
        int port = 5001; //tgetApp().port();
        app.start(port);
        baseUrl = "http://localhost:" + port;
        db = DB.getDefault();
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
//        откр транзакция, создать подкл к БД, заполнить.
        transaction = db.beginTransaction();
    }

    @AfterEach
    void afterEach() {
        transaction.rollback();
    }

    @Test
    void showIndex() {
        HttpResponse<String> response = Unirest
                .get(baseUrl)
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains(wordPageAnalizer);
    }

    @Test
    void showListOfUrls() {
        HttpResponse<String> response = Unirest
                .get(baseUrl + "/urls/")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains(wordListUrls);
    }

    @Test
    void createUrl() {
        // getting success message and response 302, when creating
        HttpResponse<String> response1 = Unirest
                .post(baseUrl + "/urls")
                .field("url", urlString)
                .asString();

        //assertThat(response1.getBody()).contains(wordSuccess);
        assertThat(response1.getStatus()).isEqualTo(302);
        // looking at DB:
        Url expected = new QUrl()
                .name.iequalTo(urlExpectString)
                .findOne();

        assertThat(expected).isNotNull();
        assertThat(expected.getName()).isEqualTo(urlExpectString);
        // getting individual page
        long urlId = expected.getId();
        HttpResponse<String> response2 = Unirest
                .get(baseUrl + "/urls/" + urlId + "/")
                .asString();
        assertThat(response2.getStatus()).isEqualTo(200);
        assertThat(response2.getBody()).contains(urlExpectString);
        // getting error message, when trying to add again
        HttpResponse<String> response3 = Unirest
                .post(baseUrl + "/urls")
                .field("url", urlExpectString)
                .asString();

        assertThat(response3.getStatus()).isEqualTo(302);
        //assertThat(response3.getBody()).contains(wordExists);
    }

    @Test
    void createUrlErrIncorrect() {
        HttpResponse<String> response = Unirest
                .post(baseUrl + "/urls")
                .field("url", urlWrongString)
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains(wordIncorrect);
    }

//    @Test
//    void functionalTest() {
//        JavalinTest.test(app, (server, client) -> {
//            assertThat(client.get("urls/").code()).isEqualTo(200);
//        });
//    }

}
