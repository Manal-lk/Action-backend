package com.xelops.actionplan.resource.error;


import io.micrometer.tracing.Tracer;
import lombok.extern.slf4j.Slf4j;
import com.xelops.actionplan.config.Messages;
import com.xelops.actionplan.exception.FunctionalException;
import com.xelops.actionplan.exception.NotFoundException;
import com.xelops.actionplan.exception.TechnicalException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends BaseResponseEntityExceptionHandler {


    public CustomizedResponseEntityExceptionHandler(Tracer tracer, Messages messages) {
        super(tracer, messages);
    }

    @ExceptionHandler(TechnicalException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ProblemDetail onTechnicalException(TechnicalException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FunctionalException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ProblemDetail onFunctionalException(FunctionalException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ProblemDetail onNotFoundException(NotFoundException e) {
        log.error(e.getMessage(), e);
        return buildProblem(e, HttpStatus.NOT_FOUND);
    }

}
