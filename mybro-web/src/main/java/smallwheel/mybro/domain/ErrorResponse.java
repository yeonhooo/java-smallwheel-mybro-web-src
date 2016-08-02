package smallwheel.mybro.domain;

/**
 * <pre>
 * JSON response 메세지 포멧.
 * 
 * 일반적인 응답 예시
 * {   
 *     resultCode : '0000',
 *     resultMessage : 'SUCCESS',
 *     info : {
 *     		count : 100
 *     		page: 1
 *          totalCount : 5
 *     }
 *     , items : [
 *     		// 기본적으로, items 하위 영역은 3rd Party의 연동 규격을 따른다 
 *     ]
 * }
 * 
 * 오류 응답 예시
 * {
 *   "resultCode": "4001"
 *   "resultMessage": "UnsupportedMediaType",
 * }
 * </pre>
 * 
 * @param <E>
 *
 */
public class ErrorResponse {

	/** The code. */
	private String errorCode;

	/** The message. */
	private String errorMessage;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
