package io.github.wwhysohard.search.exception;

import io.github.wwhysohard.search.enums.ErrorCode;

/**
 * Reports the cause of the query processing error
 */
public class FilterException extends RuntimeException {

    private final ErrorCode code;

    /**
     * Creates query processing error report
     * @param code error message ({@link ErrorCode})
     */
    public FilterException(ErrorCode code) {
        super(code.name());
        this.code = code;
    }

    /**
     *
     * @return error message ({@link ErrorCode})
     */
    public ErrorCode getCode() {
        return code;
    }

}
