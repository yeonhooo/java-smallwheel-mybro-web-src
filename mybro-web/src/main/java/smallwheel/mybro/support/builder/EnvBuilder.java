package smallwheel.mybro.support.builder;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.stereotype.Component;

import smallwheel.mybro.common.Constants.Delimiter;
import smallwheel.mybro.common.Constants.Path;

@Component
public class EnvBuilder {

	private final static Logger LOGGER = Logger.getLogger(EnvBuilder.class);

	public void init(String userId) {

		// 기존 디렉로리 삭제 후 재생성
		try {
			FileUtils.deleteDirectory(new File(Path.RESULT_DES_DIR + userId));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		// DTO 디렉토리 생성
		File dir = new File(Path.RESULT_DES_DIR + userId + Delimiter.VERTICAL_SLUSH + "dto/");
		if (!dir.isDirectory()) {
			// 디렉토리가 존재하지 않는다면 디렉토리 생성
			dir.mkdirs();
		}
		
		// mapper 디렉토리 생성
		String sqlMapFilePath = Path.RESULT_DES_DIR + userId + Delimiter.VERTICAL_SLUSH + "mapper/sqlmap/";
		dir = new File(sqlMapFilePath);
		if (!dir.isDirectory()) {
			// 디렉토리가 존재하지 않는다면 디렉토리 생성
			dir.mkdirs();
		}
	}

}
