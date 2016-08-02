package smallwheel.mybro.common.exception.handler;

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import smallwheel.mybro.common.exception.GeneralException;
import smallwheel.mybro.domain.ErrorResponse;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

	private HttpHeaders responseHeaders = new HttpHeaders();

	@ExceptionHandler(value = { ConnectException.class })
	protected ResponseEntity<Object> handleConnectException(ConnectException ex) {
		LOGGER.error("ConnectException", ex);
		ErrorResponse response = new ErrorResponse();
		response.setErrorCode("4001");
		response.setErrorMessage(getErrorMsg(ex.toString()));
		return sendData(HttpStatus.BAD_REQUEST, ex, response);
	}

	@ExceptionHandler(value = { GeneralException.class })
	protected ResponseEntity<Object> handleGeneralException(GeneralException ex) {
		LOGGER.error("GeneralException", ex);
		ErrorResponse response = new ErrorResponse();
		response.setErrorCode("5000");
		response.setErrorMessage(ex.getMessage());
		return sendData(HttpStatus.INTERNAL_SERVER_ERROR, ex, response);
	}

	@ExceptionHandler(value = { Exception.class })
	protected ResponseEntity<Object> handelUntypedException(Exception ex) {
		LOGGER.error("UntypedException", ex);
		return sendData(HttpStatus.INTERNAL_SERVER_ERROR, ex, null);
	}

	private ResponseEntity<Object> sendData(HttpStatus statusCode, Exception ex, ErrorResponse response) {

		if (response == null) {
			response = new ErrorResponse();
			response.setErrorMessage(ex.getClass().getSimpleName());

			if (ex instanceof NullPointerException) {
				response.setErrorCode("4901");
			} else {
				response.setErrorCode("5000");
			}
		} else {
			if (response.getErrorMessage() == null) {
				response.setErrorMessage(ex.getClass().getSimpleName());
			}
		}

		LOGGER.error("[errorCode] " + response.getErrorCode() + ":" + response.getErrorMessage());
		
		return new ResponseEntity<Object>(getJsonString(response), responseHeaders, statusCode);
	}

	private String getJsonString(Object object) {

		if (object == null) {
			return "";
		} else {
			String jsonValue = null;
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setSerializationInclusion(Include.NON_EMPTY);
			objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
			try {
				jsonValue = objectMapper.writeValueAsString(object);
			} catch (Exception e) {
				e.printStackTrace();
			}

			// request.getSession().invalidate();
			return jsonValue;
		}
	}

	@SuppressWarnings("unused")
	private void setHeadersInfo() {
		responseHeaders.set("Content-Type", "application/json;charset=utf-8");
	}

	private String getErrorMsg(String errorMsg) {
		List<Integer> idxList = new ArrayList<Integer>();
		int pos = errorMsg.indexOf(":");
		String exceptionMessage = "";

		while (pos > -1) {
			idxList.add(pos);
			pos = errorMsg.indexOf(":", pos + 1);
		}
		
		if (idxList != null && idxList.size() >= 2) {
			exceptionMessage = errorMsg.substring((idxList.get(idxList.size() - 2) + 1));
		} else {
			if (errorMsg.lastIndexOf(":") > 0) {
				exceptionMessage = errorMsg.substring(errorMsg.lastIndexOf(":"));
			} else {
				if (errorMsg.lastIndexOf(";") > 0) {
					exceptionMessage = errorMsg.substring(errorMsg.lastIndexOf(";"));
				} else {
					exceptionMessage = errorMsg.getClass().getSimpleName();
				}
			}
		}
		return exceptionMessage;
	}

}