package smallwheel.mybro.service.impl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import smallwheel.mybro.common.Constants.DBMS;
import smallwheel.mybro.domain.ClassFileInfo;
import smallwheel.mybro.domain.DbConnectionInfo;
import smallwheel.mybro.domain.GeneratingInfo;
import smallwheel.mybro.domain.MappingOption;
import smallwheel.mybro.domain.TableInfo;
import smallwheel.mybro.repository.DatabaseRepository;
import smallwheel.mybro.service.DatabaseService;
import smallwheel.mybro.support.SqlMapperBuilderFactory;
import smallwheel.mybro.support.builder.DtoClassBuilder;
import smallwheel.mybro.support.builder.EnvBuilder;
import smallwheel.mybro.support.builder.SqlMapperBuilder;


@Service
public class DatabaseServiceImpl implements DatabaseService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);
	
	@Autowired
	public DatabaseRepository databaseRepository;
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@Autowired
	private EnvBuilder envBuilder;
	
	@Autowired
	private DtoClassBuilder dtoClassBuilder;
	
	@Override
	public List<String> getTables(String userId, String dbmsType) {
		return databaseRepository.getTables(userId, dbmsType);
	}

	/**
	 * DBMS별 드라이버 클래스명을 가져온다.
	 * 
	 * @param dbmsType
	 * @return
	 */
	private String getDriverClassName(String dbmsType) {

		if (DBMS.MYSQL.equals(dbmsType)) {
			return "com.mysql.jdbc.Driver";
		} else if (DBMS.MSSQL.equals(dbmsType)) {
			return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		} else if (DBMS.ORACLE.equals(dbmsType)) {
			return "oracle.jdbc.OracleDriver";
		} else {
			return null;
		}

	}
	
	/**
	 * DBMS별 검증 쿼리를 가져온다.
	 * 
	 * @param dbmsType
	 * @return
	 */
	private String getValidationQuery(String dbmsType) {

		if (DBMS.MYSQL.equals(dbmsType)) {
			return "select 1";
		} else if (DBMS.MSSQL.equals(dbmsType)) {
			return "select 1";
		} else if (DBMS.ORACLE.equals(dbmsType)) {
			return "select 1 from dual";
		} else {
			return null;
		}
	}

	/**
	 * 조건별 JDBC URL을 가져온다.
	 * 
	 * @param dbmsType
	 * @param host
	 * @param port
	 * @param dbName
	 * @return
	 */
	private String getConnectionUrl(String dbmsType, String host, int port, String dbName) {

		if (DBMS.MYSQL.equals(dbmsType)) {
			return "jdbc:mysql://" + host + ":" + port + "/" + dbName;
		} else if (DBMS.MSSQL.equals(dbmsType)) {
			return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName;
		} else if (DBMS.ORACLE.equals(dbmsType)) {
			return "jdbc:oracle:thin:@" + host + ":" + port + ":" + dbName;
		} else {
			return null;
		}

	}

	@Override
	public DataSource createDataSource(String userId, DbConnectionInfo connectionInfo) {
		
		String beanName = "dataSource-" + userId;
		LOGGER.info("Create {} bean({}).", beanName, connectionInfo);

		MutablePropertyValues values = new MutablePropertyValues();
		values.addPropertyValue("driverClassName", getDriverClassName(connectionInfo.getDbmsType()));
		values.addPropertyValue("url", getConnectionUrl(connectionInfo.getDbmsType(), connectionInfo.getHost(), connectionInfo.getPort(), connectionInfo.getDbName()));
		values.addPropertyValue("username", connectionInfo.getUserName());
		values.addPropertyValue("password", connectionInfo.getUserPasswd());
		values.addPropertyValue("validationQuery", getValidationQuery(connectionInfo.getDbmsType()));
		
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass(BasicDataSource.class);
//		beanDef.setScope("session");
		beanDef.setPropertyValues(values);
//		dataSourceBeanDefinition.setLazyInit(false);
//		dataSourceBeanDefinition.setAbstract(false);
//		dataSourceBeanDefinition.setAutowireCandidate(true);
		registerBeanDefinition(beanName, beanDef);
		
//		// JdbcTemplate Bean
//		GenericBeanDefinition jdbcTplBeanDef = new GenericBeanDefinition();
//		jdbcTplBeanDef.setBeanClass(JdbcTemplate.class);
//		jdbcTplBeanDef.getPropertyValues().addPropertyValue("dataSource", beanDef);
//		registerBeanDefinition("jdbcTemplate", jdbcTplBeanDef);

		return (DataSource) applicationContext.getBean(beanName);
	}
	
	/**
	 * bean 수동 등록
	 * 
	 * @param beanName
	 * @param beanDefinition2
	 */
	private void registerBeanDefinition(String beanName, GenericBeanDefinition beanDefinition) {
		AutowireCapableBeanFactory factory = applicationContext.getAutowireCapableBeanFactory();
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) factory;
		registry.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void generate(String userId, GeneratingInfo generatingInfo) {
		
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass(MappingOption.class);
		beanDef.getPropertyValues().addPropertyValue("mapperType", generatingInfo.getMappingOption().getMapperType());
		beanDef.getPropertyValues().addPropertyValue("couplingType", generatingInfo.getMappingOption().getCouplingType());
		beanDef.getPropertyValues().addPropertyValue("prefixExcept", generatingInfo.getMappingOption().getPrefixExcept());
		beanDef.getPropertyValues().addPropertyValue("classNameSuffix", generatingInfo.getMappingOption().getClassNameSuffix());
		
		registerBeanDefinition("mappingOption-" + userId, beanDef);
		
		// 결과 파일 디렉토리 구성
		envBuilder.init(userId);
		
		TableInfo tableInfo = new TableInfo();
		ClassFileInfo classInfo = new ClassFileInfo();
		for (String tableName : generatingInfo.getTables()) {
			// 테이블 정보 생성
			tableInfo = databaseRepository.getTableInfo(userId, tableName);
			
			// 테이블에 매핑되는 클래스 정보 생성
			classInfo = databaseRepository.getClassFileInfo(userId, tableInfo);

			// step1. 모델 클래스 파일을 생성한다.
			dtoClassBuilder.build(userId, classInfo);
			
			// step2. SqlMap.xml 파일 및 java mapper interface 파일을 생성한다.
			SqlMapperBuilder sqlMapperBuilder = new SqlMapperBuilderFactory().createSqlMapperBuilder(generatingInfo.getMappingOption().getMapperType());
			sqlMapperBuilder.build(userId, tableInfo, classInfo);
		}
	}

}