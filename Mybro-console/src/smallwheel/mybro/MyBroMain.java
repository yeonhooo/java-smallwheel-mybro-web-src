package smallwheel.mybro;
import java.sql.Connection;

import org.apache.log4j.Logger;

import smallwheel.mybro.builder.dto.DtoClassBuilder;
import smallwheel.mybro.common.DBManager;
import smallwheel.mybro.common.ENV;
import smallwheel.mybro.support.SqlMapperBuilderFactory;
import smallwheel.mybro.support.builder.SqlMapperBuilder;

/**
 * MyBro 메인 클래스
 * 
 * @author yeonhooo
 * 
 * TODO: Connection 관리
 * TODO: 패키지 경로 정보 입력받아, 패키지 생성 및 해당 패키지 경로에 파일 추가
 * TODO: Mapper.java 파일 생성 기능 
 *
 */
public class MyBroMain {
	
	private final static Logger LOGGER = Logger.getLogger(MyBroMain.class);
	
	private DBManager dbm;
	private Connection con;
	private SqlMapperBuilder sqlMapperBuilder;
	private DtoClassBuilder dtoClassBuilder;

	public static void main(String[] args) {
		MyBroMain main = new MyBroMain();
		main.run();
	}

	private void init() {
		// 환경초기화 , 환경파일에서 변수값 로딩
		ENV.init();

		// 타입별(ibatis, mybatis) sqlMapperBuilder 생성
		SqlMapperBuilderFactory factory = new SqlMapperBuilderFactory();
		sqlMapperBuilder = factory.createSqlMapperBuilder(ENV.mapperType);

		// DB 연결
		dbm = new DBManager();
		dbm.checkConnection(ENV.dbms);
		con = dbm.getConnection(ENV.dbms);

		// dtoClassBuilder 생성
		dtoClassBuilder = new DtoClassBuilder();
	}

	private void run() {
		
		init();
		
		// DAO 클래스와 SqlMap.xml 파일을 만들려는 테이블 목록
		final String[] TABLE_LIST = { 
				"city"
				, "country"
		};

		final String[] PROCEDURE_LIST = {
		// "EXEC [PROC_BB_KBO_APP_SDMS_MOBILE_VOTE_CNT_S]"
		// "PROC_BB_KBO_XMLMAKER_STATS_LIVEPLAYER_BATTER_S '2010', '20100504HHHT0'"
		};

		// 테이블에 대한 클래스 파일과 XML 파일 생성
		for (int i = 0; i < TABLE_LIST.length; i++) {
			// step1. 모델 클래스 파일을 생성한다.
			dtoClassBuilder.makeModelClassFileByTable(i, con, TABLE_LIST[i], ENV.prefixExcept);

			// step2. SqlMap.xml 파일을 생성한다.
			sqlMapperBuilder.writeSqlMap(TABLE_LIST[i]);
		}

		// 프로시저에 대한 클래스 파일과 XML 파일 생성
		for (int i = 0; i < PROCEDURE_LIST.length; i++) {
			// step1. 모델 클래스 파일을 생성한다.
			dtoClassBuilder.makeModelClassFileByProcedure(i, con, PROCEDURE_LIST[i]);

			// step2. SqlMap.xml 파일을 생성한다.
			sqlMapperBuilder.writeSqlMap(null);
		}
		
		LOGGER.info("### " + TABLE_LIST.length + "개 테이블에 대한 작업이 완료되었습니다." );
	}
}
