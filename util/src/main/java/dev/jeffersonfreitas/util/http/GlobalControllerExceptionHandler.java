package dev.jeffersonfreitas.util.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import dev.jeffersonfreitas.api.exceptions.InvalidInputException;
import dev.jeffersonfreitas.api.exceptions.NotFoundException;

@RestControllerAdvice
public class GlobalControllerExceptionHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(GlobalControllerExceptionHandler.class);
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(NotFoundException.class)
	public @ResponseBody HttpErrorInfo handlerNotFoundExceptions(ServerHttpRequest request, NotFoundException ex) {
		return createHttpErrorInfo(HttpStatus.NOT_FOUND, request, ex);
	}
	
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler(InvalidInputException.class)
	public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, InvalidInputException ex) {
		return createHttpErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, request, ex);
	}

	
	private HttpErrorInfo createHttpErrorInfo(HttpStatus unprocessableEntity, ServerHttpRequest request,
			Exception ex) {
		final String path = request.getPath().pathWithinApplication().value();
		final String message = ex.getMessage();
		
		LOG.debug("Returning HTTP status: {} for path: {}, message: {}", unprocessableEntity, path, message);
		return new HttpErrorInfo(unprocessableEntity, path, message);
	}

}
