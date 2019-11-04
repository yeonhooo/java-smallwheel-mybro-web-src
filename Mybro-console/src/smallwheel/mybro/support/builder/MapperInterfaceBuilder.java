package smallwheel.mybro.support.builder;
import java.io.FileWriter;

import org.apache.log4j.Logger;

import smallwheel.mybro.common.ClassFileInfo;
import smallwheel.mybro.common.Constants;
import smallwheel.mybro.common.MapperInterfaceInfo;
import smallwheel.mybro.common.SharedInfo;

/**
 *
 * @author yeonhooo@gmail.com
 */
public class MapperInterfaceBuilder implements Builder {

    private final static Logger LOGGER = Logger.getLogger(MapperInterfaceBuilder.class);
    private final SharedInfo sharedInfo = SharedInfo.getInstance();
    private final String fileNameSuffix = "Mapper";

    @Override
    public void build() {

        MapperInterfaceInfo mapperInterfaceFile;
        ClassFileInfo classFile;

        try {
            for (int i = 0; i < sharedInfo.getMapperInterfaceInfoList().size(); i++) {

                mapperInterfaceFile = sharedInfo.getMapperInterfaceInfoList().get(i);
                classFile = sharedInfo.getClassFileInfoList().get(i);

                // File Name �� �����.
                String interfaceName = makeInterfaceName(sharedInfo.getTableInfoList().get(i).getEntityName());
                FileWriter writer = new FileWriter(Constants.Path.SQL_MAPPER_DES_DIR + interfaceName + Constants.Path.FILENAME_EXTENSION_JAVA);

                // interface �ۼ� ����
                writer.write("import java.util.List;\n");
                writer.write("import org.springframework.stereotype.Repository;\n");
                writer.write("\n@Repository\n");
                writer.write("public interface " + interfaceName + " {\n");

                // method �ۼ�
                for (int j = 0; j < mapperInterfaceFile.getSqlMapInfoList().size(); j++) {
                    writer.write("\n\t" + mapperInterfaceFile.getSqlMapInfoList().get(j).getType()
                            + " " + mapperInterfaceFile.getSqlMapInfoList().get(j).getId()
                            + "(" + classFile.getName() + " " + convertParameterName(classFile.getName()) + ");\n");
                }

                // interface �ݱ�
                writer.write("\n}");
                writer.close();
            }
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /** Ŭ�������� �����. */
    private String makeInterfaceName(String entityName) {
        return entityName + fileNameSuffix;
    }

    /** �Ķ���͸��� �����. */
    private String convertParameterName(String entityName) {
        // ù ���ڸ� �ҹ��ڷ� �����Ѵ�.
        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
    }

}
