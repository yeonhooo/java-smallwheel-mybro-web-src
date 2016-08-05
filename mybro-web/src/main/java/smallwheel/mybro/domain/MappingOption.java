package smallwheel.mybro.domain;

public class MappingOption {
	
	private String couplingType;
	
	/** mapper type */
	private String mapperType;
	
	/**
	 * 엔티티명에서 제외할 문자열
	 * <pre>
	 * e.g. 테이블명이 NQR_BILL_DETAIL 의 경우, 기본 엔티티명은 NqrBillDetail 이 된다
	 * 이 때 PREFIX_EXCEPT 을 "NQR_" 로 설정할 경우, 엔티티명은 NqrBillDetail 가 아닌 BillDetail가 된다.
	 * </pre>
	 */
	private String prefixExcept;
	
	/** 
	 * 자바 클래스 파일명 접미어
	 * <pre>
	 * e.g. 테이블명이 USER_INFO 인 경우, 기본 자바파일명은 UserInfo.java가 된다.
	 * 이 때 classNameSuffix를 "Dto"로 설정할 경우 자바파일명은 UserInfoDto.java가 되며,
	 * classNameSuffix를 "Vo"로 설정할 경우 자바파일명은 UserInfoVo.java가 된다.
	 * </pre>
	 */
	private String classNameSuffix;
	
	private String dtoStyle;

	public String getCouplingType() {
		return couplingType;
	}

	public void setCouplingType(String couplingType) {
		this.couplingType = couplingType;
	}

	public String getMapperType() {
		return mapperType;
	}

	public void setMapperType(String mapperType) {
		this.mapperType = mapperType;
	}

	public String getPrefixExcept() {
		return prefixExcept;
	}

	public void setPrefixExcept(String prefixExcept) {
		this.prefixExcept = prefixExcept;
	}

	public String getClassNameSuffix() {
		return classNameSuffix;
	}

	public void setClassNameSuffix(String classNameSuffix) {
		this.classNameSuffix = classNameSuffix;
	}
	
	public String getDtoStyle() {
		return dtoStyle;
	}

	public void setDtoStyle(String dtoStyle) {
		this.dtoStyle = dtoStyle;
	}
}
