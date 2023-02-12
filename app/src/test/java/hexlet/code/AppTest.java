package hexlet.code;

import hexlet.code.domain.Url;
import hexlet.code.domain.query.QUrl;
import io.ebean.DB;
import io.ebean.Database;
import io.ebean.Transaction;
import io.javalin.Javalin;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
    private static String mockUrl = "not set";
    private static String mockHtmlFileName = "mock.html";
    private static String mockHtml;

    private static Javalin app;
    private static String baseUrl;
    private static Database database;
    private static Transaction transaction;

    @BeforeAll
    public static void beforeAll() throws IOException {
        app = getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();

        Path mockHtmlPath = Paths.get("src", "test", "resources", mockHtmlFileName)
                .toAbsolutePath()
                .normalize();
        mockHtml = Files.readString(mockHtmlPath);

        MockWebServer mockServer = new MockWebServer();
        mockUrl = mockServer.url("/").toString();
        MockResponse mockResp1 = new MockResponse()
                .setBody(mockHtml);
        mockServer.enqueue(mockResp1);
    }

    @AfterAll
    public static void afterAll() {
        app.stop();
    }

    /**
     * opening transaction.
     */
    @BeforeEach
    void beforeEach() {
        transaction = database.beginTransaction();
    }

    /**
     * rolling transaction back.
     */
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
    void createMockUrlAndAddCheck() {
        HttpResponse<String> response1 = Unirest
                .post(baseUrl + "/urls")
                .field("url", mockUrl)
                .asString();
        assertThat(response1.getStatus()).isEqualTo(302);
        System.out.println("Current URL of mockserver is: " + mockUrl);
        long mockId = new QUrl().findCount();
        System.out.println("id of URL is: " + mockId);

        HttpResponse<String> response2 = Unirest
                .post(baseUrl + "/urls/" + mockId + "/checks")
                .asString();

        assertThat(response2.getStatus()).isEqualTo(200);
        assertThat(response2.getBody()).contains("7 days of the week");
        assertThat(response2.getBody()).contains("The days of the week");
        assertThat(response2.getBody()).contains("listing the names of 7 days of the week");
        assertThat(response2.getBody()).doesNotContain("Friday", "Here are seven days of the week");
    }
    @Test
    void createDBEntry() {
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
}
