# Анализатор страниц
[Анализатор страниц](https://page-analyzer-6qut.onrender.com/) помогает контролировать, поддерживать в актуальном состоянии, содержание интересующих страниц. Это веб-приложение, позволяющее проанализировать добавленные пользователем сайты (их заглавные страницы), сохраняя в базе состояния – значения основных полей: доступность (код ответа), название страницы, заголовок, описание и др.

### Технологии и принцип работы
MVC Controller – Javaline ([движок]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/App.java#L28), [маршруты](https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/App.java#L61)), MVC View – Thymeleaf ([подключение]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/App.java#L46-L59), [передача в шаблон]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/controllers/UrlController.java#L89-L90), [пример шаблона]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/resources/templates/urls/show.html)), MVC Model – Ebean ([пример модели]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/domain/UrlCheck.java), [класс генератора миграции]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/MigrationGenerator.java), [обращение paged list]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/controllers/UrlController.java#L27-L31)). DB – PostgreSQL ([ENV production]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/resources/application.yaml)) / H2 ([ENV development]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/resources/application.yaml)). Деплой на Railway (стал платным), [деплой на Render]( https://page-analyzer-6qut.onrender.com/), деплой на Heroku (стал платным). Логирование [SLF4J]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/controllers/ChecksController.java#L37). Парсинг данных сайта [Jsoup]( https://github.com/hi-ar/java-project-72/blob/main/app/src/main/java/hexlet/code/controllers/ChecksController.java#L47-L56).  Тестирование [MockWebServer]( https://github.com/hi-ar/java-project-72/blob/main/app/src/test/java/hexlet/code/AppTest.java#L102-L107), [Unirest]( https://github.com/hi-ar/java-project-72/blob/main/app/src/test/java/hexlet/code/AppTest.java#L109-L112), [Github Actions]( https://github.com/hi-ar/java-project-72/tree/main/.github/workflows). Чистота и покрытие кода тестами [Jacoco](https://codeclimate.com/github/hi-ar/java-project-72/test_coverage), [Checkstyle]( https://github.com/hi-ar/java-project-72/blob/main/app/config/checkstyle/checkstyle.xml) checker.
### Hexlet tests and linter status:
[![Actions Status](https://github.com/hi-ar/java-project-72/workflows/hexlet-check/badge.svg)](https://github.com/hi-ar/java-project-72/actions)
[![hexlet-check](https://github.com/hi-ar/java-project-72/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/hi-ar/java-project-72/actions/workflows/hexlet-check.yml)
[![Maintainability](https://api.codeclimate.com/v1/badges/9cc6b2a997d7015c4838/maintainability)](https://codeclimate.com/github/hi-ar/java-project-72/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/9cc6b2a997d7015c4838/test_coverage)](https://codeclimate.com/github/hi-ar/java-project-72/test_coverage)
