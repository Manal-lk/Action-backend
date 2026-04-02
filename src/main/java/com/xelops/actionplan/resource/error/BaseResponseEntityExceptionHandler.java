package com.xelops.actionplan.resource.error;


import com.nimbusds.jwt.proc.BadJWTException;
import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.exception.TechnicalException;
import com.xelops.actionplan.utils.constants.GlobalConstants;
import io.micrometer.tracing.Tracer;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Used for handling base Java / Spring Exceptions
 */

@Slf4j
@RequiredArgsConstructor
public class BaseResponseEntityExceptionHandler {


    private final Tracer tracer;
    private final Messages messages;

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProblemDetail onMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    @ResponseStatus(code = HttpStatus.REQUEST_TIMEOUT)
    @ResponseBody
    public ProblemDetail onSocketTimeoutException(SocketTimeoutException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.REQUEST_TIMEOUT);
    }

    @ExceptionHandler(IOException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProblemDetail onIOException(IOException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ProblemDetail> onBindException(BindException e) {
        log.error(e.getMessage(), e);
        return e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(f -> buildProblem(f.getField(), HttpStatus.BAD_REQUEST, f.getDefaultMessage()))
                .toList();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ProblemDetail> onConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        return violations.stream()
                .map(violation -> buildProblem(violation.getPropertyPath()
                        .toString(), HttpStatus.BAD_REQUEST, violation.getMessage()))
                .toList();
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProblemDetail onHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadJWTException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ResponseBody
    public ProblemDetail onBadJWTException(BadJWTException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FileUploadException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProblemDetail onFileUploadException(FileUploadException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ProblemDetail> handleValidationException(HandlerMethodValidationException e) {
        String title = e.getClass().getSimpleName();

        return e.getAllValidationResults().stream()
                .flatMap(validationResult -> validationResult.getResolvableErrors().stream()
                        .map(error -> buildProblem(title, HttpStatus.BAD_REQUEST, error.getDefaultMessage())))
                .toList();
    }


    ProblemDetail buildProblem(Exception e, HttpStatus status) {
        String title = e.getClass()
                .getSimpleName();
        String detail = e.getMessage();
        if (e instanceof TechnicalException) {
            detail = messages.get(GlobalConstants.ERROR_WS_TECHNICAL, getTraceId());
        }
        return buildProblem(title, status, detail);
    }

    ProblemDetail buildProblem(String title, HttpStatus status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setProperty("traceId", getTraceId());
        return problem;
    }

    String getTraceId() {
        return Objects.requireNonNull(tracer.currentSpan())
                .context()
                .traceId();
    }
}
