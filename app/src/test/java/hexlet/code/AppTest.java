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
import static hexlet.code.Utils.ALERT_EXISTS_URL;
import static hexlet.code.Utils.ALERT_INCORR_URL;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.assertj.core.api.Assertions.assertThat;


public class AppTest {
    private static String wordPageAnalizer = "Анализатор страниц";
    private static String wordListUrls = "Последняя проверка";
    private static String urlString = "https://ru.hexlet.io/projects/72/members/27753?step=6";
    private static String urlExpectString = "https://ru.hexlet.io";
    private static String urlWrongString = "www.hexlet.io";
    private static String mockUrl = "not set";
    private static String mockHtmlFileName = "mock.html";
    private static String mockHtml;
    private static String url404 = "https://ru.hexlet.moscow";

    private static Javalin app;
    private static String baseUrl;
    private static Database database;
    private static Transaction transaction;

    @BeforeAll
    public static void beforeAll() {
        app = getApp();
        app.start(0);
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();


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
    void createMockUrlAndAddCheck() throws IOException {
        Path mockHtmlPath = Paths.get("src", "test", "resources", mockHtmlFileName)
                .toAbsolutePath()
                .normalize();
        mockHtml = Files.readString(mockHtmlPath);

        MockWebServer mockServer = new MockWebServer();
        mockUrl = mockServer.url("/").toString();
        MockResponse mockResp1 = new MockResponse()
                .setBody(mockHtml);
        mockServer.enqueue(mockResp1);

        HttpResponse<String> response1 = Unirest
                .post(baseUrl + "/urls")
                .field("url", mockUrl)
                .asString();

        assertThat(response1.getStatus()).isEqualTo(HTTP_MOVED_TEMP);
        System.out.println("Current URL of mockserver is: " + mockUrl);
        long mockId = new QUrl().findCount();
        System.out.println("id of URL is: " + mockId);

        HttpResponse<String> response2 = Unirest
                .post(baseUrl + "/urls/" + mockId + "/checks")
                .asString();

        assertThat(response2.getStatus()).isEqualTo(HTTP_OK);
        assertThat(response2.getBody()).contains("7 days of the week");
        assertThat(response2.getBody()).contains("The days of the week");
        assertThat(response2.getBody()).contains("listing the names of 7 days of the week");
        assertThat(response2.getBody()).doesNotContain("Friday", "Here are seven days of the week");
    }

    @Test
    void getCheckStatus404() {
        HttpResponse<String> response1 = Unirest
                .post(baseUrl + "/urls")
                .field("url", url404)
                .asString();
        assertThat(response1.getStatus()).isEqualTo(HTTP_MOVED_TEMP);
        long idOf404 = new QUrl().findCount();
        System.out.println("id of URL is: " + idOf404);

        HttpResponse<String> response2 = Unirest
                .post(baseUrl + "/urls/" + idOf404 + "/checks")
                .asString();

        assertThat(response2.getBody()).contains("404");
    }

    @Test
    void createDBEntry() {
        // getting success message and response 302, when creating
        HttpResponse<String> response1 = Unirest
                .post(baseUrl + "/urls")
                .field("url", urlString)
                .asString();

        assertThat(response1.getStatus()).isEqualTo(HTTP_MOVED_TEMP);
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
        assertThat(response2.getStatus()).isEqualTo(HTTP_OK);
        assertThat(response2.getBody()).contains(urlExpectString);
        // getting error message, when trying to add again
        HttpResponse<String> response3 = Unirest
                .post(baseUrl + "/urls")
                .field("url", urlExpectString)
                .asString();

        assertThat(response3.getStatus()).isEqualTo(HTTP_OK);
        assertThat(response3.getBody()).contains(ALERT_EXISTS_URL);
    }

    @Test
    void getAlertAlreadyExists() {
        HttpResponse<String> response = Unirest
                .post(baseUrl + "/urls")
                .field("url", urlWrongString)
                .asString();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getBody()).contains(ALERT_INCORR_URL);
    }
}
