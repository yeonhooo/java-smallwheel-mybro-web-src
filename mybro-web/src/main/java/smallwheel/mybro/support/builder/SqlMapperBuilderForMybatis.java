package smallwheel.mybro.support.builder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import smallwheel.mybro.common.Constants;
import smallwheel.mybro.common.Constants.Delimiter;
import smallwheel.mybro.common.Constants.Mapper;
import smallwheel.mybro.common.Constants.Path;
import smallwheel.mybro.domain.ClassFileInfo;
import smallwheel.mybro.domain.ColumnInfo;
import smallwheel.mybro.domain.MapperInterfaceInfo;
import smallwheel.mybro.domain.PropertyInfo;
import smallwheel.mybro.domain.SqlMapInfo;
import smallwheel.mybro.domain.TableInfo;

/**
 * Mybatis용 SqlMapperBuilder 클래스
 * 
 * @author yeonhooo@gmail.com
 */
public class SqlMapperBuilderForMybatis extends SqlMapperBuilder {
	
	private final static Logger LOGGER = Logger.getLogger(SqlMapperBuilderForMybatis.class);
	
	/** 
	 * SqlMap.xml 파일을 만든다. 
	 * @param table list 
	 * */
	@Override
	public void build(String userId, TableInfo table, ClassFileInfo classFile) {
		
		String sqlMapFilePath = Path.RESULT_DES_DIR + userId + Delimiter.VERTICAL_SLUSH + "mapper/sqlmap/";
		MapperInterfaceInfo mapperInterfaceFile = new MapperInterfaceInfo();
		
		String tableName = table.getName();
		String entityName = table.getEntityName();
		mapperInterfaceFile.setName(classFile.getName() + Constants.Mapper.MAPPER_INTERFACE_SUFFIX);
		
		final Element root = new Element("mapper");
		final Element typeAlias = new Element("typeAlias");
		final Element resultMap = new Element("resultMap");
		final Element sql = new Element("sql");
		final Element insert = new Element("insert");
		final Element select = new Element("select");
		final Element selectOne = new Element("select");
		final Element update = new Element("update");
		final Element delete = new Element("delete");
		String sqlMapId;
		
		// root 노드 설정
		root.setAttribute(makeAttribute("namespace", table.getEntityName() + Mapper.MAPPER_INTERFACE_SUFFIX));
		
		// typeAlias 노드 설정
		final String typeAliasText = classFile.getName();
		typeAlias.setAttribute(makeAttribute("alias", typeAliasText));
		typeAlias.setAttribute(makeAttribute("type", classFile.getName()));		
		
		// resultMap 노드 설정
		final String resultMapText = "ret" + classFile.getName();
		resultMap.setAttribute(makeAttribute("type", typeAliasText));
		resultMap.setAttribute(makeAttribute("id", resultMapText));		
		
		// result 노드 설정
		for (int j = 0; j < classFile.getPropertyList().size(); j++) {
			Element result = new Element("result");
			result.setAttribute(makeAttribute("property", classFile.getPropertyList().get(j).getName()));
			result.setAttribute(makeAttribute("javaType", classFile.getPropertyList().get(j).getType()));
			result.setAttribute(makeAttribute("column", table.getColumnInfoList().get(j).getName()));
//			result.setAttribute(makeAttribute("jdbcType", classFile.dbColumnTypeList[i]));
			resultMap.addContent(result);
		}
		
		// dynamicWhere sql map 생성
		sql.setAttribute(makeAttribute("id", "dynamicWhere"));
		sql.addContent(makeDynamicWhere(table.getColumnInfoList(), classFile.getPropertyList()));
		
		// insert sql map 생성
		sqlMapId = "insert" + entityName;
		mapperInterfaceFile.getSqlMapInfoList().add(new SqlMapInfo(sqlMapId, "int"));
		insert.setAttribute(makeAttribute("id", sqlMapId));
		insert.setAttribute(makeAttribute("parameterType", typeAliasText));
		insert.addContent(makeInsertSqlMap(table, classFile));
		
		// select list sql map 생성
		sqlMapId = "select" + entityName + "List";
		mapperInterfaceFile.getSqlMapInfoList().add(new SqlMapInfo(sqlMapId, "List<" + classFile.getName() + ">"));
		select.setAttribute(makeAttribute("id", sqlMapId));
		select.setAttribute(makeAttribute("parameterType", typeAliasText));
		select.setAttribute(makeAttribute("resultType", typeAliasText));
		select.addContent(makeSelectSqlMap(table, classFile));
		// 동적 WHERE절 생성
		select.addContent(addDynamicWhere(tableName));
		
		// select sql map 생성
		sqlMapId = "select" + entityName;
		mapperInterfaceFile.getSqlMapInfoList().add(new SqlMapInfo(sqlMapId, classFile.getName()));
		selectOne.setAttribute(makeAttribute("id", sqlMapId));
		selectOne.setAttribute(makeAttribute("parameterType", typeAliasText));
		selectOne.setAttribute(makeAttribute("resultType", typeAliasText));
		selectOne.addContent(makeSelectSqlMap(table, classFile));
		selectOne.addContent(makePrimaryKeyWhere(table.getPrimaryKeyColumnNameList(), classFile.getPropertyPrimaryKeyNameList()));
		
		// update sql map 생성
		sqlMapId = "update" + entityName;
		mapperInterfaceFile.getSqlMapInfoList().add(new SqlMapInfo(sqlMapId, "int"));
		update.setAttribute(makeAttribute("id", sqlMapId));
		update.setAttribute(makeAttribute("parameterType", typeAliasText));
		update.addContent(makeUpdateSqlMapHead(tableName));
		update.addContent(makeDynamicUpdateSqlMap(table, classFile));
		update.addContent(makePrimaryKeyWhere(table.getPrimaryKeyColumnNameList(), classFile.getPropertyPrimaryKeyNameList()));
		
		// delete sql map 생성
		sqlMapId = "delete" + entityName;
		mapperInterfaceFile.getSqlMapInfoList().add(new SqlMapInfo(sqlMapId, "int"));
		delete.setAttribute(makeAttribute("id", sqlMapId));
		delete.setAttribute(makeAttribute("parameterType", typeAliasText));
		delete.addContent(makeDeleteSqlMap(tableName));
		delete.addContent(makePrimaryKeyWhere(table.getPrimaryKeyColumnNameList(), classFile.getPropertyPrimaryKeyNameList()));
		
		// root 에 추가
		root.addContent(new Comment(" Use type aliases to avoid typing the full class name every time. "));
		root.addContent(typeAlias);
		root.addContent(resultMap);
		root.addContent("\n");
		
		root.addContent(new Comment(" Dynamic Where Condition "));
		root.addContent(sql);
		root.addContent("\n");
		
		root.addContent(new Comment(" Insert " + tableName + " "));
		root.addContent(insert);
		root.addContent("\n");
		
		root.addContent(new Comment(" Select " + tableName + " List "));
		root.addContent(select);
		root.addContent("\n");
		
		root.addContent(new Comment(" Select " + tableName + " "));
		root.addContent(selectOne);
		root.addContent("\n");
		
		root.addContent(new Comment(" Update " + tableName + " "));
		root.addContent(update);
		root.addContent("\n");
		
		root.addContent(new Comment(" Delete " + tableName + " "));
		root.addContent(delete);
		
		/* DTD 지정 후, 파일로 저장
		 * iBatis
		 * 	<!DOCTYPE sqlMap      
		 * 		PUBLIC "-//ibatis.apache.org//DTD SQL Map 2.0//EN"	
		 * 		"http://ibatis.apache.org/dtd/sql-map-2.dtd">
		 * 
		 * MyBatis
		 * 	<!DOCTYPE mapper
		 * 		PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
		 * 		"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
		 */
		DocType docType = new DocType(Constants.Mapper.MYBATIS_ELEMENT_NAME, Constants.Mapper.MYBATIS_PUBLIC_ID, Constants.Mapper.MYBATIS_SYSTEM_ID);
		Document doc = new Document(root, docType);
		try (FileOutputStream fos = new FileOutputStream(sqlMapFilePath + entityName + "Mapper.xml")) {
			// 저장할 XML 파일 생성한다.
			XMLOutputter serializer = new XMLOutputter();
			
			// 기본 포맷 형태를 불러와 수정한다.
			Format fm = serializer.getFormat();
			// 인코딩 변경
			fm.setEncoding("UTF-8");
			// 부모, 자식 태그를 구별하기 위한 탭 범위를 정한다.
			fm.setIndent("\t");
			// 태그간 줄바꿈을 지정한다.
			fm.setLineSeparator("\n");
			
			// 설정한 XML 파일의 포맷을 set 한다.
			serializer.setFormat(fm);
			
			// doc 의 내용을 fos 하여 파일을 생성한다.
			serializer.output(doc, fos);
			fos.flush();
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		buildMapperInterface(userId, classFile, mapperInterfaceFile);
	}

	/**
	 * 동적 WHERE절 생성
	 * @param columnList 
	 * @param propertyList 
	 * @param tableName
	 * @return
	 */
	private Element makeDynamicWhere(List<ColumnInfo> columnList, List<PropertyInfo> propertyList) {
		Element dynamic = new Element("where");
		Element ifTest = null;
		
		/* if 노드 설정
		 * <if test = 'propertyName != null and propertyName != "" >
		 */
		for (int i = 0; i < columnList.size(); i++) {

			if ("INT".equals(columnList.get(i).getDataType().toUpperCase())) {
				ifTest = new Element("if");
				ifTest.setAttribute(makeAttribute("test", propertyList.get(i).getName() + " > 0"));
				ifTest.addContent("\n\t\t\t\tAND " + columnList.get(i).getName() + " = #{" + propertyList.get(i).getName() + "}\n\t\t\t");
				dynamic.addContent(ifTest);
			} else {
				ifTest = new Element("if");
				ifTest.setAttribute(makeAttribute("test", propertyList.get(i).getName() + " != null and " + propertyList.get(i).getName() + " != ''"));
				ifTest.addContent("\n\t\t\t\tAND " + columnList.get(i).getName() + " = #{" + propertyList.get(i).getName() + "}\n\t\t\t");
				dynamic.addContent(ifTest);
			}
		}
		
		return dynamic;
	}
	
	/**
	 * PK 조건으로 이뤄진 WHERE절 생성
	 * @param primaryKeyColumnNameList
	 * @param propertyPrimaryKeyNameList 
	 * @return
	 */
	private String makePrimaryKeyWhere(List<String> primaryKeyColumnNameList, List<String> propertyPrimaryKeyNameList) {
		String sql = "\n\t\t" + "WHERE";

		for (int i = 0; i < primaryKeyColumnNameList.size(); i++) {
			if (i == 0) {
				sql = sql + "\n\t\t\t" + primaryKeyColumnNameList.get(i) + " = #{" + propertyPrimaryKeyNameList.get(i) + "}";
			} else {
				sql = sql + "\n\t\t\t" + "AND " + primaryKeyColumnNameList.get(i) + " = #{" + propertyPrimaryKeyNameList.get(i) + "}";
			}
		}
		sql += "\n\t";
		return sql;
	}
	
	/**
	 * 생성한 WHERE절 추가
	 * @param tableName
	 * @return
	 */
	private Element addDynamicWhere(String tableName) {
		Element include = new Element("include");
		include.setAttribute(makeAttribute("refid", "dynamicWhere"));
		return include;
	}
	
	/** insert 쿼리문 작성 */
	private String makeInsertSqlMap(TableInfo table, ClassFileInfo classFile) {
		String sql = "\n\t\tINSERT INTO " + table.getName() + " ( ";
		for (int i = 0; i < classFile.getPropertyList().size(); i++) {
			if (i == 0) {
				sql = sql + "\n\t\t\t" + table.getColumnInfoList().get(i).getName();
			} else {
				sql = sql + "\n\t\t\t" + "," + table.getColumnInfoList().get(i).getName();
			}
		}
		sql += "\n\t\t) VALUES (";
		for (int i = 0; i < classFile.getPropertyList().size(); i++) {
			if (i == 0) {
				sql = sql + "\n\t\t\t#{" + classFile.getPropertyList().get(i).getName() + "} ";
			} else {
				sql = sql + "\n\t\t\t," + "#{" + classFile.getPropertyList().get(i).getName() + "} ";
			}
		}
		sql += "\n\t\t)\n\t";
		return sql;
	}
	
	/** select 쿼리문 작성 */
	private String makeSelectSqlMap(TableInfo table, ClassFileInfo classFile) {
		String sql = "\n\t\tSELECT ";
		for (int i = 0; i < classFile.getPropertyList().size(); i++) {
			if (i == 0) {
				sql = sql + "\n\t\t\t" + table.getColumnInfoList().get(i).getName() + "\tAS " + classFile.getPropertyList().get(i).getName();
			} else {
				sql = sql + "\n\t\t\t" + "," + table.getColumnInfoList().get(i).getName() + "\tAS " + classFile.getPropertyList().get(i).getName();
			}
		}
		sql = sql + "\n\t\tFROM " + table.getName() + "\t\t";
		return sql;
	}
	
	/** update 쿼리문 작성 */
	@SuppressWarnings("unused")
	private String makeUpdateSqlMap(TableInfo table, ClassFileInfo classFile) {
		String sql = "\n\t\tUPDATE " + table.getName() + " \n\t\tSET";
		for (int i = 0; i < classFile.getPropertyList().size(); i++) {
			if (i == 0) {
				sql = sql + "\n\t\t\t" + table.getColumnInfoList().get(i).getName() + " = " + "#{" + classFile.getPropertyList().get(i).getName() + "} ";
			} else {
				sql = sql + "\n\t\t\t" + "," + table.getColumnInfoList().get(i).getName() + " = " + "#{" + classFile.getPropertyList().get(i).getName() + "} ";
			}
		}
		sql += "\n\t\t";
		return sql;
	}
	
	/**
	 * update 쿼리문 헤더
	 * @param tableName
	 * @return
	 */
	private String makeUpdateSqlMapHead(String tableName) {
		String sql = "\n\t\tUPDATE " + tableName;
		return sql;
	}
	
	/**
	 * 동적 update 쿼리문 작성 
	 * 예) <isNotEmpty property="applyName">,APPLY_NAME = #applyName# </isNotEmpty>
	 * 
	 * prepend 를 사용하지 않는 것으로 수정
	 * @param tableName
	 * @return
	 */
	private Element makeDynamicUpdateSqlMap(TableInfo table, ClassFileInfo classFile) {
		
		Element dynamic = new Element("trim");
		dynamic.setAttribute(makeAttribute("prefix", "SET"));
		dynamic.setAttribute(makeAttribute("prefixOverrides", ","));
		
		Element ifTest = null;
		
		PropertyListLoop: for (int i = 0; i < classFile.getPropertyList().size(); i++) {
			
			for (String pkPropertyName : classFile.getPropertyPrimaryKeyNameList()) {
				if (pkPropertyName.equals(classFile.getPropertyList().get(i).getName())) {
					continue PropertyListLoop;
				}
			}
			
			if ("INT".equals(table.getColumnInfoList().get(i).getDataType().toUpperCase())) {
				ifTest = new Element("if");
				ifTest.setAttribute(makeAttribute("test", classFile.getPropertyList().get(i).getName() + " > 0"));
				ifTest.addContent("\n\t\t\t\t, " + table.getColumnInfoList().get(i).getName() + " = #{" + classFile.getPropertyList().get(i).getName() + "}\n\t\t\t");
				dynamic.addContent(ifTest);
			} else {
				ifTest = new Element("if");
				ifTest.setAttribute(makeAttribute("test", classFile.getPropertyList().get(i).getName() + " != null and " + classFile.getPropertyList().get(i).getName() + " != ''"));
				ifTest.addContent("\n\t\t\t\t, " + table.getColumnInfoList().get(i).getName() + " = #{" + classFile.getPropertyList().get(i).getName() + "}\n\t\t\t");
				dynamic.addContent(ifTest);
			}
			
		}
		
		return dynamic;
	}
	
	/** delete 쿼리문 작성 */
	private String makeDeleteSqlMap(String tableName) {
		String sql = "\n\t\tDELETE FROM " + tableName + "\t\t";
		return sql;
	}
	
	/**
	 * Attribute 를 생성하여 반환한다.
	 * 
	 * @param attributeName
	 * @param attributeValue
	 * @return
	 */
	private Attribute makeAttribute(String attributeName, String attributeValue) {
		Attribute attribute = new Attribute(attributeName, attributeValue); 
		return attribute;
	}
	
	/**
	 * java mapper interface을 생성한다.
	 * 
	 * @param classFile
	 * @param mapperInterfaceFile
	 */
	private void buildMapperInterface(String userId, ClassFileInfo classFile, MapperInterfaceInfo mapperInterfaceFile) {
		String interfaceName = makeInterfaceName(classFile.getName());

		try (
			// File Name 을 만든다.
			FileWriter writer = new FileWriter(Constants.Path.RESULT_DES_DIR + userId + Delimiter.VERTICAL_SLUSH
					+ "mapper" + Delimiter.VERTICAL_SLUSH + interfaceName + Path.FILENAME_EXTENSION_JAVA)) 
		{

			// interface 작성 시작
			writer.write("import java.util.List;\n");
			writer.write("import org.springframework.stereotype.Repository;\n");
			writer.write("\n@Repository\n");
			writer.write("public interface " + interfaceName + " {\n");

			// method 작성
			for (int j = 0; j < mapperInterfaceFile.getSqlMapInfoList().size(); j++) {
				writer.write("\n\t" + mapperInterfaceFile.getSqlMapInfoList().get(j).getType()
						+ " " + mapperInterfaceFile.getSqlMapInfoList().get(j).getId() 
						+ "(" + classFile.getName() + " " + convertParameterName(classFile.getName()) + ");\n");
			}

			// interface 닫기
			writer.write("\n}");
			writer.close();
		}
		catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	/** 클래스명을 만든다. */
	private String makeInterfaceName(String classFileName) {
		return classFileName + Mapper.MAPPER_INTERFACE_SUFFIX;
	}
	
	/** 파라미터명을 만든다. */
	private String convertParameterName(String entityName) {
		// 첫 글자를 소문자로 시작한다.
		return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
	}
	
}
