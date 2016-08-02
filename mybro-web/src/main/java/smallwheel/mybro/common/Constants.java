package smallwheel.mybro.common;

/**
 * @author yeonhooo
 */
public class Constants {

	public static class DBMS {
		public static final String MYSQL = "MYSQL";
		public static final String MSSQL = "MSSQL";
		public static final String ORACLE = "ORACLE";
	}

	public static class Mapper {
		public static final String IBATIS = "IBATIS";
		public static final String MYBATIS = "MYBATIS";
		
		/**
		 * iBatis element name
		 * <pre>
		 * < !DOCTYPE <b>sqlMap</b>      
		 * PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN"	
		 * "http://ibatis.apache.org/dtd/sql-map-2.dtd">
		 * </pre>
		 */
		public static final String IBATIS_ELEMENT_NAME = "sqlMap";
		
		/**
		 * The public ID of the iBatis DOCTYPE
		 * <pre>
		 * < !DOCTYPE sqlMap      
		 * PUBLIC <b>"-//ibatis.apache.org//DTD SQL Map 2.0//EN"</b>
		 * "http://ibatis.apache.org/dtd/sql-map-2.dtd">
		 * </pre>
		 */
		public static final String IBATIS_PUBLIC_ID = "-//ibatis.apache.org//DTD SQL Map 2.0//EN";
		
		/**
		 * The system ID of the iBatis DOCTYPE
		 * <pre>
		 * < !DOCTYPE sqlMap
		 * PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN"	
		 * <b>"http://ibatis.apache.org/dtd/sql-map-2.dtd"</b>>
		 * </pre>
		 */
		public static final String IBATIS_SYSTEM_ID = "http://ibatis.apache.org/dtd/sql-map-2.dtd";
		
		/**
		 * MyBatis element name
		 * <pre>
		 * < !DOCTYPE <b>mapper</b> PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
		 * "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
		 * </pre>
		 */
		public static final String MYBATIS_ELEMENT_NAME = "mapper";
		
		/**
		 * The public ID of the MyBatis DOCTYPE
		 * <pre>
		 * < !DOCTYPE mapper PUBLIC <b>"-//mybatis.org//DTD Mapper 3.0//EN"</b>
		 * "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
		 * </pre>
		 */
		public static final String MYBATIS_PUBLIC_ID = "-//mybatis.org//DTD Mapper 3.0//EN";
		
		/**
		 * The system ID of the MyBatis DOCTYPE
		 * <pre>
		 * < !DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
		 * <b>"http://mybatis.org/dtd/mybatis-3-mapper.dtd"</b> >
		 * </pre>
		 */
		public static final String MYBATIS_SYSTEM_ID = "http://mybatis.org/dtd/mybatis-3-mapper.dtd";
		
		/** Suffix for mybatis mapper interface name  */
		public static final String MAPPER_INTERFACE_SUFFIX = "Mapper"; 
	}

	public static class Path {
		/** 결과 파일 우치 */
		public static final String RESULT_DES_DIR = "src/main/resources/export/";
		
		/** java DTO class 파일 위치 */
		public static final String DTO_CLASS_DES_DIR = RESULT_DES_DIR + "dto/";
		
		/** sqlmap 파일 위치 */
		public static final String SQL_MAPPER_DES_DIR = RESULT_DES_DIR + "mapper/";
		public static final String FILENAME_EXTENSION_JAVA = ".java";
		public static final String FILENAME_EXTENSION_XML = ".xml";
	}
	
	public static final class Delimiter {
		public static final String BLANK = " ";
		public static final String COMMA = ",";
		public static final String POINT = ".";
		public static final String GREATER_THAN = ">";
		public static final String LESS_THAN = "<";
		public static final String VERTICAL_BAR = "|";
		public static final String VERTICAL_SLUSH = "/";
	}
}
