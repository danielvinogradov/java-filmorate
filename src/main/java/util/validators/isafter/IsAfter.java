package util.validators.isafter;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация кастомного constraint для Jakarta Bean Validation.
 *
 * Проверяет, что дата не раньше, указанной в аннотации.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IsAfterValidator.class)
@Documented
public @interface IsAfter {

    /**
     * Сообщение об ошибке.
     *
     * @return Сообщение об ошибке.
     */
    String message() default "date is too early";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Дата, с которой будет производиться сравнение.
     *
     * @return Дата для сравнения.
     */
    String value();

    /**
     * Формат даты, указанной в {@link #value()}. Будет передан для создания
     * {@link java.time.format.DateTimeFormatter#ofPattern(String)}, поэтому
     * должен быть совместимым с ним.
     *
     * @return Формат даты.
     */
    String format() default "dd-MM-yyyy";
}
