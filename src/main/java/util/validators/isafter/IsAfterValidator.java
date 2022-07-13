package util.validators.isafter;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Валидатор для {@link IsAfter} constraint.
 *
 * @see <a href="https://docs.jboss.org/hibernate/stable/validator/reference/en-US/html_single/#validator-customconstraints">
 * DOCS. Hibernate Validator. Creating Custom Constraints
 * </a>
 */
public final class IsAfterValidator implements ConstraintValidator<IsAfter, LocalDate> {

    private IsAfter isAfter;

    @Override
    public void initialize(final @NonNull IsAfter constraintAnnotation) {
        this.isAfter = constraintAnnotation;
    }

    @Override
    public boolean isValid(final @Nullable LocalDate localDate, final ConstraintValidatorContext constraintValidatorContext) {
        if (localDate == null) return false;

        final DateTimeFormatter dateToCompareWithDateTimeFormatter = DateTimeFormatter.ofPattern(isAfter.format());
        final LocalDate dateToCompareWith = LocalDate.parse(isAfter.value(), dateToCompareWithDateTimeFormatter);

        return !localDate.isBefore(dateToCompareWith);
    }
}
