package smallwheel.mybro.repository;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import smallwheel.mybro.common.Constants.DBMS;
import smallwheel.mybro.common.exception.GeneralException;
import smallwheel.mybro.domain.ClassFileInfo;
import smallwheel.mybro.domain.ColumnInfo;
import smallwheel.mybro.domain.MappingOption;
import smallwheel.mybro.domain.PropertyInfo;
import smallwheel.mybro.domain.TableInfo;

@Repository
public class DatabaseRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseRepository.class);

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<String> getTables(String userId, String dbmsType) {
		String beanName = "dataSource-" + userId;
//		DataSource dataSource = (DataSource) applicationContext.getBean(beanName);
//		jdbcTemplate = new JdbcTemplate(dataSource);
		
		jdbcTemplate.setDataSource((DataSource) applicationContext.getBean(beanName));
		
		LOGGER.info("DataSource bean name: " + beanName + "\n" + ((DataSource) applicationContext.getBean(beanName)).toString());
		LOGGER.info("DataSource bean name: " + jdbcTemplate);
		
		return jdbcTemplate.queryForList(getTableListQuery(dbmsType), String.class);
	}
	
	private String getTableListQuery(String dbmsType) {

		if (DBMS.MYSQL.equals(dbmsType)) {
			return "show tables";
		} else if (DBMS.MSSQL.equals(dbmsType)) {
			return "SELECT * FROM information_schema.tables WHERE TABLE_TYPE='BASE TABLE'";
		} else if (DBMS.ORACLE.equals(dbmsType)) {
			return "select table_name from user_tables";
		} else {
			return null;
		}
	}

	public TableInfo getTableInfo(String userId, String tableName) {

		String beanName = "dataSource-" + userId;
		DataSource dataSource = (DataSource) applicationContext.getBean(beanName);
		MappingOption mappingOption = (MappingOption) applicationContext.getBean("mappingOption-" + userId);
		
		TableInfo tableInfo = new TableInfo(tableName);
		
		try (
			Connection con = dataSource.getConnection();
			PreparedStatement pstmt = con.prepareStatement("select * from " + tableName + " where 1=0");
			ResultSet rs = pstmt.executeQuery();
		) {
			DatabaseMetaData databaseMetaData = con.getMetaData();
			ResultSetMetaData rm = rs.getMetaData();
			
			// Table의 PK 정보를 가져온다.
			ResultSet keys = databaseMetaData.getPrimaryKeys(null, null, tableName);
			while (keys.next()) {
				tableInfo.getPrimaryKeyColumnNameList().add(keys.getString("COLUMN_NAME"));
			}
			
			// Table의 Column 정보를 가져온다.
			for (int i = 1; i <= rm.getColumnCount(); i++) {
				tableInfo.getColumnInfoList().add(new ColumnInfo(rm.getColumnName(i), rm.getColumnTypeName(i),
						isPrivateKey(keys, rm.getColumnName(i))));
			}
			
			// EntityName 을 만든다
			tableInfo.setEntityName(makeEntityName(tableInfo.getName(), mappingOption.getPrefixExcept()));

			LOGGER.info("[Table Name: " + tableName + " / Column Count: " + rm.getColumnCount() + "]");
			LOGGER.info("PK Columns");
			for (String key : tableInfo.getPrimaryKeyColumnNameList()) {
				LOGGER.info("\t" + key);
			}

		} catch (SQLException e) {
			throw new GeneralException("Connection faild!", e);
		}

		return tableInfo;
	}
	
	public ClassFileInfo getClassFileInfo(final String userId, final TableInfo tableInfo) {

		ClassFileInfo classInfo = new ClassFileInfo();

		// Table의 PK 정보를 가져온다.
		for (String pkColumn : tableInfo.getPrimaryKeyColumnNameList()) {
			classInfo.getPropertyPrimaryKeyNameList().add(makePropertyName(pkColumn));
		}
		
		// Table의 Column 정보를 가져온다.
		for (ColumnInfo column : tableInfo.getColumnInfoList()) {
			classInfo.getPropertyList().add(new PropertyInfo(makePropertyName(column.getName()), makePropertyType(userId, column.getDataType())));
		}
		
		// ClassName 을 만든다.
		classInfo.setName(makeClassName(userId, tableInfo.getEntityName()));
		
		LOGGER.info("Class Name: " + classInfo.getName() + " / Property Count: " + classInfo.getPropertyList().size());
		LOGGER.info("Properties");
		for (PropertyInfo key : classInfo.getPropertyList()) {
			LOGGER.info("\t" + key.getName() + "(" + key.getType() + ")");
		}

		return classInfo;
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
						+ tableName.substring(tableName.indexOf("_") + 1, tableName.indexOf("_") + 2).toUpperCase() + tableName
						.substring(tableName.indexOf("_") + 2)).trim();
			} else {
				break;
			}
		}

		// 첫 글자를 대문자로 시작한다.
		tableName = tableName.substring(0, 1).toUpperCase() + tableName.substring(1);
		return tableName;
	}

	
	/** 클래스명을 만든다. */
	private String makeClassName(String userId, String entityName) {
		MappingOption mappingOption = (MappingOption) applicationContext.getBean("mappingOption-" + userId);
		return entityName + mappingOption.getClassNameSuffix();
	}

	/**
	 * 클래스내의 프로퍼티명(property)을 만든다.
	 * 
	 * @param 변환 될 실제 DB 컬럼명
	 * @return 변환 된 프로퍼티명(DB 컬럼명과 매칭)
	 * */
	private String makePropertyName(String columnName) {
		columnName = columnName.toLowerCase(Locale.ENGLISH);

		while (true) {
			if (columnName.indexOf("_") > -1) {
				columnName = (columnName.substring(0, columnName.indexOf("_"))
						+ columnName.substring(columnName.indexOf("_") + 1, columnName.indexOf("_") + 2).toUpperCase() + columnName
						.substring(columnName.indexOf("_") + 2)).trim();
			} else {
				break;
			}
		}

		return columnName;
	}
	
	/**
	 * 환경 설정 파일에 설정된 결합도에 따라 클래스의 프로퍼티 타입을 만든다.<br />
	 * <br />
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
	private String makePropertyType(String userId, String columnType) {
		MappingOption mappingOption = (MappingOption) applicationContext.getBean("mappingOption-" + userId);
		String propertyType = columnType.toUpperCase();

		if ("HIGH".equals(mappingOption.getCouplingType())) {
			// 결합도 강함
			if (propertyType.equals("TINYINT") || propertyType.equals("SMALLINT") || propertyType.equals("MEDIUMINT")
					|| propertyType.equals("INT")) {
				propertyType = "int";
			} else if (propertyType.equals("BIGINT")) {
				propertyType = "long";
			} else if (propertyType.equals("FLOAT")) {
				propertyType = "float";
			} else if (propertyType.equals("DOUBLE") || propertyType.equals("DECIMAL")) {
				propertyType = "double";
			} else if (propertyType.equals("CHAR") || propertyType.equals("VARCHAR") || propertyType.equals("TEXT")
					|| propertyType.indexOf("BLOB") > -1) {
				propertyType = "String";
			} else if (propertyType.equals("DATE") || propertyType.equals("DATETIME") || propertyType.equals("TIMESTAMP")) {
				propertyType = "Date";
			} else {
				propertyType = "String";
			}

		} else if ("MIDDLE".equals(mappingOption.getCouplingType())) {
			// 결합도 보통
			if (propertyType.equals("TINYINT") || propertyType.equals("SMALLINT") || propertyType.equals("MEDIUMINT")
					|| propertyType.equals("INT")) {
				propertyType = "int";
			} else if (propertyType.equals("BIGINT")) {
				propertyType = "long";
			} else if (propertyType.equals("DATE") || propertyType.equals("DATETIME") || propertyType.equals("TIMESTAMP")) {
				propertyType = "Date";
			} else {
				propertyType = "String";
			}

		} else if ("LOW".equals(mappingOption.getCouplingType())) {
			// 결합도 약함
			if (propertyType.equals("TINYINT") || propertyType.equals("SMALLINT") || propertyType.equals("MEDIUMINT")
					|| propertyType.equals("INT")) {
				propertyType = "int";
			} else if (propertyType.equals("BIGINT")) {
				propertyType = "long";
			} else {
				propertyType = "String";
			}

		} else if ("NO".equals(mappingOption.getCouplingType())) {
			// 결합도 없음
			propertyType = "String";
		}

		return propertyType;
	}

	/**
	 * 해당 컬럼의 PK 포함 여부를 확인한다.
	 * 
	 * @param keys
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	private boolean isPrivateKey(ResultSet keys, String columnName) throws SQLException {
		boolean isPrivateKey = false;
		while (keys.next()) {
			if (keys.getString("COLUMN_NAME").equals(columnName)) {
				isPrivateKey = true;
				break;
			}
		}
		return isPrivateKey;
	}
}
