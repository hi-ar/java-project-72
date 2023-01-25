package hexlet.code;

import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import hexlet.code.domain.Url;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class AppTest {
    private static String wordPageAnalizer = "Анализатор страниц";
    private static String wordListUrls = "Последняя проверка";
    private static Javalin app;
    private static String baseUrl;
    private static Database db;
    private static Transaction transaction;

    @BeforeAll
    void beforeAll() {
        app = App.getApp();
        int port = App.getApp().port();
        app.start(port);
        baseUrl = "http://localhost:" + port;
        db = DB.getDefault();
    }

    @AfterAll
    void afterAll() {
        app.stop();
    }

    @BeforeEach
    void beforeEach() {
        //откр транзакция, создать подкл к БД, заполнить.
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
                .get(baseUrl + "urls/")
                .asString();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains(wordListUrls);
    }
    @Test
    void createUrl() {
        String urlString = "https://ru.hexlet.io/";
        HttpResponse<String> response1 = Unirest
                .post(baseUrl + "urls/")
                .field("name", urlString)
                .asString();

        assertThat(response1.getStatus()).isEqualTo(200);

        Url expected = new QUrl()
                .name.contains(urlString)
                .findOne();

        assertThat(expected).isNotNull();
        assertThat(expected.getName()).isEqualTo(urlString);

        long urlId = expected.getId();
        HttpResponse<String> response2 = Unirest
                .get(baseUrl + "urls/" + urlId + "/")
                .asString();
        assertThat(response2.getStatus()).isEqualTo(200);
        assertThat(response2.getBody()).contains(urlString);
    }
}
