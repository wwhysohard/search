package io.github.wwhysohard.search.validator;

import io.github.wwhysohard.search.annotation.Filterable;
import io.github.wwhysohard.search.dto.FilterRequest;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Utility class, which has static method to check whether the specified <code>field</code> is allowed for filtering
 */
public class FilterableValidator {

    /**
     * Creating an instance of {@link FilterableValidator} is illegal
     */
    private FilterableValidator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Checks whether the specified <code>field</code> is allowed for filtering.
     * Applied recursively to JPA related models if <code>joinable</code> is set to <code>true</code>.
     *
     * @param clazz {@link Class} of the given model
     * @param fullFieldName the specified <code>field</code> of {@link FilterRequest}
     *                      which is either field name or full join path
     * @param <T> generic type of the given model
     *
     * @return <code>true</code> if the specified field is allowed for filtering, <code>false</code> otherwise
     */
    public static <T> boolean isValid(Class<T> clazz, String fullFieldName) {
        if (fullFieldName == null) return false;

        int indexOfPoint = fullFieldName.indexOf(".");
        String fieldName = (indexOfPoint != -1) ? fullFieldName.substring(0, indexOfPoint) : fullFieldName;

        Field field = getField(clazz, fieldName);
        if (field == null) return false;

        Filterable filterable = field.getDeclaredAnnotation(Filterable.class);
        if (filterable == null) return false;

        String nextJoin = (indexOfPoint != -1) ? fullFieldName.substring(indexOfPoint + 1) : "";
        if (nextJoin.isEmpty()) return !filterable.joinable();

        return filterable.joinable() && isValid(getJoinObjectType(field), nextJoin);
    }

    /**
     * Searches for {@link Field} in the provided {@link Class}, first by name of the field,
     * then by <code>names</code> specified in @{@link Filterable} annotated fields
     *
     * @param clazz {@link Class} of the given model
     * @param fieldName <code>fieldName</code> by which {@link Field} will be searched in the given {@link Class}
     * @param <T> generic type of the given model
     *
     * @return {@link Field} by the specified <code>fieldName</code> if exists, <code>null</code> otherwise
     */
    private static <T> Field getField(Class<T> clazz, String fieldName) {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getFieldByFilterableNames(clazz, fieldName);
        }
    }

    /**
     * Searches for {@link Field} in the provided {@link Class} by <code>names</code> specified in
     * @{@link Filterable} annotated fields
     *
     * @param clazz {@link Class} of the given model
     * @param fieldName <code>fieldName</code> by which {@link Field} will be searched in the given {@link Class}'s fields
     *                  annotated with @{@link Filterable}
     * @param <T> generic type of the given model
     *
     * @return {@link Field} by the specified <code>fieldName</code> if exists, <code>null</code> otherwise
     */
    private static <T> Field getFieldByFilterableNames(Class<T> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            Filterable filterable = field.getDeclaredAnnotation(Filterable.class);

            if (filterable != null && Arrays.asList(filterable.names()).contains(fieldName)) {
                return field;
            }
        }

        return null;
    }

    /**
     * Determines and returns <code>field</code>'s {@link Class} type
     *
     * @param field which {@link Class} type is to be determined
     *
     * @return <code>field</code>'s {@link Class} type
     */
    private static Class<?> getJoinObjectType(Field field) {
        Class<?> joinObjectType = field.getType();

        if (joinObjectType.isAssignableFrom(List.class) || joinObjectType.isAssignableFrom(Set.class)) {
            ParameterizedType parameterizedType = (ParameterizedType) field.getGenericType();
            joinObjectType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
        }

        return joinObjectType;
    }

}
