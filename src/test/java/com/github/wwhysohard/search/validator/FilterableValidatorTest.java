package com.github.wwhysohard.search.validator;

import com.github.wwhysohard.search.annotation.Filterable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilterableValidatorTest {

    @Test
    void testFilterableField() {
        boolean isFilterable = FilterableValidator.isValid(TestModel.class, "filterableField");
        assertTrue(isFilterable);
    }

    @Test
    void testNotFilterableField() {
        boolean isFilterable = FilterableValidator.isValid(TestModel.class, "notFilterableField");
        assertFalse(isFilterable);
    }

    @Test
    void testFilterableFieldWithNames() {
        // Note that field is still filterable by its field name too
        boolean isFilterableByFieldName = FilterableValidator.isValid(TestModel.class, "FilterableFieldWithNames");
        boolean isFilterableByFirstTestName = FilterableValidator.isValid(TestModel.class, "firstTestName");
        boolean isFilterableBySecondTestName = FilterableValidator.isValid(TestModel.class, "secondTestName");

        assertTrue(isFilterableByFieldName);
        assertTrue(isFilterableByFirstTestName);
        assertTrue(isFilterableBySecondTestName);
    }

    @Test
    void testJoinableField() {
        boolean filterableJoinedField = FilterableValidator.isValid(TestModel.class, "joinableField.filterableField");
        boolean notFilterableJoinedField = FilterableValidator.isValid(TestModel.class, "joinableField.notFilterableField");

        assertTrue(filterableJoinedField);
        assertFalse(notFilterableJoinedField);
    }

    @Test
    void testNotJoinableField() {
        boolean filterableJoinedField = FilterableValidator.isValid(TestModel.class, "notJoinableField.filterableField");
        boolean notFilterableJoinedField = FilterableValidator.isValid(TestModel.class, "notJoinableField.notFilterableField");

        assertFalse(filterableJoinedField);
        assertFalse(notFilterableJoinedField);
    }

    @Test
    void testFilteringJoinedFieldItself() {
        // joinable field cannot be filtered itself, only by its fields
        boolean isFilterable = FilterableValidator.isValid(TestModel.class, "joinableField");
        assertFalse(isFilterable);
    }

    private static class TestModel {

        @Filterable
        private String filterableField;

        private String notFilterableField;

        @Filterable(names = {"firstTestName", "secondTestName"})
        private String FilterableFieldWithNames;

        @Filterable(joinable = true)
        private JoinedModel joinableField;

        @Filterable
        private JoinedModel notJoinableField;

    }

    private static class JoinedModel {

        @Filterable
        private String filterableField;

        private String notFilterableField;

    }

}