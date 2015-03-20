package smallwheel.mybro.builder.dto;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import smallwheel.mybro.common.Constants;
import smallwheel.mybro.common.ENV;

/**
 * 
 * @author yeonhooo
 *
 */
public class DtoClassBuilder {
	
	private final static Logger LOGGER = Logger.getLogger(DtoClassBuilder.class);
	static String classNameSuffix = "Dto";
	public static String entityName;
	public static String className;
	public static String[] dbColumnNameList;	// from
	public static String[] dbColumnTypeList;	// from
	public static List<String> dbPrimaryKeyColumnNameList;	// from
	public static String[] propertyNameList;	// to
	public static String[] propertyTypeList;	// to
	public static List<String> propertyPrimaryKeyNameList;	// to
	
	/**
	 * @param index 
	 * @param con 
	 * @param TABLE_LIST
	 */
	public void makeModelClassFileByTable(int index, Connection con, String tableName, String prefixExcept) {
		PreparedStatement pstmt;
		DatabaseMetaData databaseMetaData;
		ResultSet rs;
		ResultSetMetaData rm;
		
		try
		{
			pstmt = con.prepareStatement("select * from " + tableName + " where 1=0");
			databaseMetaData = con.getMetaData();
			rs = pstmt.executeQuery();			
			rm = rs.getMetaData();
			
			// DB Column 정보를 가져온다.
			dbColumnNameList = new String[rm.getColumnCount()];
			dbColumnTypeList = new String[rm.getColumnCount()];
			for (int i = 1; i <= rm.getColumnCount(); i++) {
				dbColumnNameList[i - 1] = rm.getColumnName(i);
				dbColumnTypeList[i - 1] = rm.getColumnTypeName(i);
			}
			
			// PK 확인
			dbPrimaryKeyColumnNameList = new ArrayList<String>();
			propertyPrimaryKeyNameList = new ArrayList<String>();
			ResultSet keys = databaseMetaData.getPrimaryKeys(null, null, tableName);
			while (keys.next()) {
				dbPrimaryKeyColumnNameList.add(keys.getString("COLUMN_NAME"));
				propertyPrimaryKeyNameList.add(makePropertyName(keys.getString("COLUMN_NAME")));
			}
			
			// Check
			LOGGER.info("[Table Name: " + tableName + " / Column Count: " + rm.getColumnCount() + "]");
			LOGGER.info("PK Columns" );
			for(String key : dbPrimaryKeyColumnNameList){
				LOGGER.info("\t" + key);
			}
			
			// EntityName 을 만든다
			entityName = makeEntityName(tableName, prefixExcept);
			
			// ClassName 을 만든다.
			className = makeClassName(entityName);
			FileWriter writer = new FileWriter(Constants.Path.DTO_CLASS_DES_DIR + className + ".java");
			
			// Class 작성 시작
			writer.write("public class " + className + " {");
			
			// Property 작성
			writer.write("\n\t" + "/* properties */" + "\n");
			propertyNameList = new String[rm.getColumnCount()];
			propertyTypeList = new String[rm.getColumnCount()];
			for (int i = 0; i < rm.getColumnCount(); i++) {
				propertyNameList[i] = makePropertyName(dbColumnNameList[i]);
				propertyTypeList[i] = makePropertyType(dbColumnTypeList[i]);
				writer.write("\t" + "private " + propertyTypeList[i] + " " + propertyNameList[i] + ";" + "\n");
			}
			
			// Getter, Setter 작성
			writer.write("\n\t" + "/* getter, setter */" + "\n");
			for (int i = 0; i < rm.getColumnCount(); i++) {
				String columnName = propertyNameList[i];				
				columnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
				
				// Getter
				writer.write("\t" + "public " + propertyTypeList[i] + " get" + columnName + "() {" + "\n");
				writer.write("\t\t" + "return " + propertyNameList[i] + ";" + "\n\t" + "}" + "\n");
				
				// Setter
				writer.write("\t" + "public void set" + columnName + "(" + propertyTypeList[i] + " " + propertyNameList[i] + ") {" + "\n");
				writer.write("\t\t" + "this." + propertyNameList[i] + " = " + propertyNameList[i] + ";" + "\n\t" + "}" + "\n");
			}
			
			// Class 닫기
			writer.write("\n}");
			
			rs.close();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param index 
	 * @param con
	 * @param 프로시저 리스트
	 */
	public void makeModelClassFileByProcedure(int index, Connection con, String procedureName) {
		PreparedStatement pstmt;
		ResultSet rs;
		ResultSetMetaData rm;
		
		try
		{
			pstmt = con.prepareStatement(procedureName);
			rs = pstmt.executeQuery();			
			rm = rs.getMetaData();
			
			// DB Column 을 가져온다.
			dbColumnNameList = new String[rm.getColumnCount()];
			for (int i = 1; i <= rm.getColumnCount(); i++) {
				dbColumnNameList[i - 1] = rm.getColumnName(i);
			}
			
			// Check
			LOGGER.info("[Procedure Name: " + procedureName + " / Column Count: " + rm.getColumnCount() + "]\n");						
			
			// ClassName 을 만든다.
			className = makeClassName(procedureName);
			FileWriter writer = new FileWriter(Constants.Path.DTO_CLASS_DES_DIR + className+ ".java");
			
			// Class 작성 시작
			writer.write("public class " + className + " {");
			
			// Property 작성
			writer.write("\n\t" + "/** properties */" + "\n");
			propertyNameList = new String[rm.getColumnCount()];
			propertyTypeList = new String[rm.getColumnCount()];
			for (int i = 0; i < rm.getColumnCount(); i++) {
				propertyNameList[i] = makePropertyName(dbColumnNameList[i]);
				propertyTypeList[i] = makePropertyType(dbColumnTypeList[i]);
				writer.write("\t" + "private " + propertyTypeList[i] + " " + propertyNameList[i] + " = \"\";" + "\n");
			}
			
			// Getter, Setter 작성
			writer.write("\n\t" + "/** getter, setter */" + "\n");
			for (int i = 0; i < rm.getColumnCount(); i++) {
				String columnName = propertyNameList[i];				
				columnName = columnName.substring(0, 1).toUpperCase() + columnName.substring(1);
				
				// Getter
				writer.write("\t" + "public " + propertyTypeList[i] + " get" + columnName + "() {" + "\n");
				writer.write("\t\t" + "return " + propertyNameList[i] + ";" + "\n\t" + "}" + "\n");
				
				// Setter
				writer.write("\t" + "public void set" + columnName + "(" + propertyTypeList[i] + " " + propertyNameList[i] + ") {" + "\n");
				writer.write("\t\t" + "this." + propertyNameList[i] + " = " + propertyNameList[i] + ";" + "\n\t" + "}" + "\n");
			}
			
			// Class 닫기
			writer.write("\n}");
			
			rs.close();
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 엔티티명을 만든다.
	 * 
	 * @param prefixExcept 엔티티명에서 제외할 문자열 
	 */
	private String makeEntityName(String tableName, String prefixExcept) {
		
		// prefixExcept을 엔티티명에서 제외한다.
		tableName = tableName.replaceAll(prefixExcept, "");
		tableName = tableName.toLowerCase(Locale.ENGLISH);
		
		while (true) {
			if (tableName.indexOf("_") > -1) {
				tableName = (tableName.substring(0, tableName.indexOf("_"))
				+ tableName.substring(tableName.indexOf("_") + 1, tableName.indexOf("_") + 2).toUpperCase()
				+ tableName.substring(tableName.indexOf("_") + 2)).trim();
			} else {
				break;
			}
		}
		
		// 첫 글자를 대문자로 시작한다.
		tableName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);		
		return tableName;
	}
	
	/** 클래스명을 만든다. */
	private String makeClassName(String entityName) {
		return entityName + classNameSuffix;
	}
	
	/** 클래스내의 프로퍼티명(property)을 만든다. 
	 * @param 변환 될 실제 DB 컬럼명
	 * @return 변환 된 프로퍼티명(DB 컬럼명과 매칭)
	 * */
	private String makePropertyName(String columnName) {
		columnName = columnName.toLowerCase(Locale.ENGLISH);
		
		while (true) {
			if (columnName.indexOf("_") > -1) {
				columnName = (columnName.substring(0, columnName.indexOf("_"))
				+ columnName.substring(columnName.indexOf("_") + 1, columnName.indexOf("_") + 2).toUpperCase()
				+ columnName.substring(columnName.indexOf("_") + 2)).trim();
			} else {
				break;
			}
		}
		
//		// 변경된 프로퍼티명이 자바 예약어이거나, 비정상적일 경우에 대한 처리		
//		if (columnName.equals("continue")) {
//			columnName = "continues";
//		} else if (columnName.equals("r")) {
//			columnName = "run";
//		} else if (columnName.equals("w")) {
//			columnName = "win";
//		} else if (columnName.equals("l")) {
//			columnName = "lose";
//		} else if (columnName.equals("d")) {
//			columnName = "draw";
//		} else if (columnName.equals("s")) {
//			columnName = "save";
//		}
//				
		return columnName;
	}
	
	
	/** 환경 설정 파일에 설정된 결합도에 따라 클래스의 프로퍼티 타입을 만든다.<br /><br />
	 * <strong>결합도 타입</strong><br /> 
	 * 
	 * 강함(<code>HIGH</code>): DB와 동일한 타입의 자바 프로퍼티 타입 반환<br />
	 * 보통(<code>MIDDLE</code>): DB 타입 중 숫자형과 날짜형만 변경. 그 외 타입은 문자열 타입으로 변환<br />
	 * 약함(<code>LOW</code>): DB 타입 중 숫자형만 변환. 그 외 타입은 문자열 타입으로 변환<br />
	 * 없음(<code>NO</code>): 모든 DB 타입을 문자열 타입으로 변환
	 * 
	 * @param 변환 될 실제 DB 컬럼 타입
	 * @return 변환 된 프로퍼티 타입(DB 컬럼 타입과 매칭)
	 * */
	private String makePropertyType(String columnType) {
		String propertyType = columnType.toUpperCase();
		
		if ("HIGH".equals(ENV.couplingType)) {
			// 결합도 강함
			if (propertyType.equals("TINYINT")
					|| propertyType.equals("SMALLINT")
					|| propertyType.equals("MEDIUMINT")
					|| propertyType.equals("INT")
					|| propertyType.equals("BIGINT")) {
				propertyType = "int";
			} else if (propertyType.equals("FLOAT")) {
				propertyType = "float";
			} else if (propertyType.equals("DOUBLE")
					|| propertyType.equals("DECIMAL")) {
				propertyType = "double";
			} else if (propertyType.equals("CHAR")
					|| propertyType.equals("VARCHAR")
					|| propertyType.equals("TEXT")
					|| propertyType.indexOf("BLOB") > -1) {
				propertyType = "String";
			} else if (propertyType.equals("DATE")
					|| propertyType.equals("DATETIME")
					|| propertyType.equals("TIMESTAMP")	) {
				propertyType = "Date";
			} else {
				propertyType = "String";
			}

		} else if ("MIDDLE".equals(ENV.couplingType)) {
			// 결합도 보통
			if (propertyType.equals("TINYINT")
					|| propertyType.equals("SMALLINT")
					|| propertyType.equals("MEDIUMINT")
					|| propertyType.equals("INT")
					|| propertyType.equals("BIGINT")) {
				propertyType = "int";
			} else if (propertyType.equals("DATE")
					|| propertyType.equals("DATETIME")
					|| propertyType.equals("TIMESTAMP")	) {
				propertyType = "Date";
			} else {
				propertyType = "String";
			}

		} else if ("LOW".equals(ENV.couplingType)) {
			// 결합도 약함
			if (propertyType.equals("TINYINT")
					|| propertyType.equals("SMALLINT")
					|| propertyType.equals("MEDIUMINT")
					|| propertyType.equals("INT")
					|| propertyType.equals("BIGINT")) {
				propertyType = "int";
			} else {
				propertyType = "String";
			}
			
		} else if ("NO".equals(ENV.couplingType)) {
			// 결합도 없음
			propertyType = "String";
		}
				
		return propertyType;
	}
}
