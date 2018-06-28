# testforRMR

Тесты написаны с использованием следующих технологий: Selenide + Junit5 + Allure + Maven

# Сборка 
Для сборки используется Maven
 `mvn clean install -Dmaven.test.skip=true`
# Запуск всех тестов
 `mvn clean test -Dselenide.browser=chrome`
 Параметр `-Dselenide.browser` запускает браузер chrome (работает быстрее), по умолчанию firefox 

 Варианты запуска тестов из отдельных модулей:
- UI
 > Всех тестов UI cd module-UI/ 
 `mvn clean test`
 > Отдельных тестовых классов
 `mvn test -Dtest=LoginTest#checkLoginNegative`
- API
 > cd module-api/
 `mvn clean test` 

# Создание отчета
 После запуска тестов в Maven
- Создать отчет 
`mvn allure:report`
- Посмотреть отчет в браузере
`mvn allure:serve` 

# Структура тестов
В тестах используется параметризация от Junit5