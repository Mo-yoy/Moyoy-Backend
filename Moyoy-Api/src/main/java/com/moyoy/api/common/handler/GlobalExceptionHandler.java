package com.moyoy.api.common.handler;

import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.moyoy.api.common.response.ApiResponse;
import com.moyoy.common.exception.CommonErrorCode;
import com.moyoy.common.exception.ErrorReason;
import com.moyoy.common.exception.MoyoException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// Custom Exception
	@ExceptionHandler(MoyoException.class)
	public ResponseEntity<ApiResponse<Void>> handleMoyoException(MoyoException ex) {

		ErrorReason errorReason = ex.getErrorReason();

		log.warn("Custom Exception 발생 : {}|{}", errorReason.getCode(), errorReason.getErrorMessage());

		return ResponseEntity.status(errorReason.getStatus()).body(ApiResponse.fail(errorReason));
	}

	// HTTP Client Error Exception
	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ApiResponse<?>> handleHttpClientErrorException(HttpClientErrorException ex) {

		ErrorReason errorReason = CommonErrorCode.HTTP_CLIENT_ERROR.getErrorReason();

		String detailMessage = " Github Error Message : " + ex.getResponseBodyAsString();

		errorReason.addDetailErrorMessage(detailMessage);

		log.error("HTTP Client 에러 발생", ex);

		return ResponseEntity.status(500).body(ApiResponse.fail(errorReason));
	}

	// @Valid
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		String detailErrorMessage = ex.getBindingResult().getAllErrors().stream()
			.map(ObjectError::getDefaultMessage)
			.collect(Collectors.joining(", "));
		detailErrorMessage += ".";

		ErrorReason errorReason = CommonErrorCode.INVALID_PARAM.getErrorReason();
		errorReason.addDetailErrorMessage(detailErrorMessage);

		log.warn("API 파라미터 유효성 검증 실패: {}", detailErrorMessage);

		return ResponseEntity.status(errorReason.getStatus()).body(ApiResponse.fail(errorReason));
	}

	// @Validated
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidated(ConstraintViolationException ex) {

		String detailErrorMessage = ex.getConstraintViolations().stream()
			.map(ConstraintViolation::getMessage)
			.collect(Collectors.joining(" , "));
		detailErrorMessage += ".";

		ErrorReason errorReason = CommonErrorCode.INVALID_PARAM.getErrorReason();
		errorReason.addDetailErrorMessage(detailErrorMessage);

		log.warn("API 파라미터 유효성 검증 실패: {}", detailErrorMessage);

		return ResponseEntity.status(errorReason.getStatus()).body(ApiResponse.fail(errorReason));
	}

	// Type MisMatch
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ErrorReason errorReason = CommonErrorCode.INVALID_PARAM.getErrorReason();

		String detailErrorMessage = "입력 값: " + ex.getValue();
		errorReason.addDetailErrorMessage(detailErrorMessage);

		log.warn("API 파라미터 유효성 검증 실패: {}", detailErrorMessage);

		return ResponseEntity.status(errorReason.getStatus()).body(ApiResponse.fail(errorReason));
	}

	// @RequestParam Validation
	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ErrorReason errorReason = CommonErrorCode.INVALID_PARAM.getErrorReason();

		String detailErrorMessage = ex.getParameterValidationResults().stream()
			.map(result -> {
				String paramName = result.getMethodParameter().getParameterName();

				String messages = result.getResolvableErrors().stream()
					.map(MessageSourceResolvable::getDefaultMessage)
					.collect(Collectors.joining("; "));
				return "[" + paramName + "] " + ": " + messages;
			})
			.collect(Collectors.joining(", "));

		errorReason.addDetailErrorMessage(detailErrorMessage);

		log.warn("API 파라미터 유효성 검증 실패: {}", detailErrorMessage);

		return ResponseEntity.status(errorReason.getStatus()).body(ApiResponse.fail(errorReason));
	}

	// 404
	@Override
	protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ErrorReason errorReason = CommonErrorCode.RESOURCE_NOT_FOUND.getErrorReason();

		log.warn("{}|{}", errorReason.getCode(), errorReason.getErrorMessage());

		return ResponseEntity.status(status).body(ApiResponse.fail(errorReason));
	}

	// 405
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

		ErrorReason errorReason = CommonErrorCode.NOT_ALLOWED_METHOD.getErrorReason();

		log.warn("{}|{}", errorReason.getCode(), errorReason.getErrorMessage());

		return ResponseEntity.status(errorReason.getStatus()).body(ApiResponse.fail(errorReason));
	}

	// Delegate to ResponseEntityExceptionHandler
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {

		ErrorReason errorReason = new ErrorReason(statusCode.value(), "COMMON_" + statusCode.value(), ex.getMessage());

		log.warn("Spring ResponseEntityExceptionHandler 에서 알 수 없는 에러를 처리했습니다.", ex);

		return ResponseEntity.status(statusCode).body(ApiResponse.fail(errorReason));
	}

	// 500
	@ExceptionHandler(Exception.class)
	private ResponseEntity<ApiResponse<Void>> handleUnknownInternalException(Exception e) {

		ErrorReason errorReason = CommonErrorCode.UNKNOWN_INTERNAL_SERVER_ERROR.getErrorReason();

		log.error("서버 내부에서 알 수 없는 에러가 발생했습니다.  : ", e);

		return ResponseEntity.status(errorReason.getStatus()).body(ApiResponse.fail(errorReason));
	}
}
