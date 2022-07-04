# `IsAfter` Jakarta Bean Validation custom constraint

Кастомный constraint для [Jakarta Bean Validation](https://beanvalidation.org).

Проверяет, что дата в поле не раньше, чем переданная в `value()`. Формат указания даты
можно переопределить в `format()`.

Дата, равная переданной в `value()` считается валидной. `null` считается невалидным значением (передавать можно,
результат валидации – всегда `false`).

Пример использования:

```java
import util.validators.isafter.IsAfter;

class SomeDataClass {

    // проверит, что дата не раньше, чем 4 Февраля 2021
    @IsAfter(value = "04.02.2021", format = "dd.MM.yyyy")
    private LocalDate date;
}
```

Похожая функциональность есть
в [java-bean-validation-extension](https://github.com/nomemory/java-bean-validation-extension).
Это решение не используется, потому что мало скачиваний и есть незакрытые vulnerabilities.
