package smallwheel.mybro.common;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

/**
 * 프로퍼티 파일 관리 
 * 
 * @author yeonhooo
 *
 */
public class ContextMaster {
	private final static Logger LOGGER = Logger.getLogger(ContextMaster.class);
	public static String filename;
	static ResourceBundle resources;

	public static void setFilename(String filename) {
		ContextMaster.filename = filename;
	}

	public static String getString(String name) {
		try {
			// resources = ResourceBundle.getBundle("conf.environment" + filename, Locale.getDefault());
			FileInputStream fis = new FileInputStream(".\\environment.properties");
			resources = new PropertyResourceBundle(fis);
		} catch (MissingResourceException | IOException mre) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" ContextMaster getString() ", mre);
			}
		}
		return resources.getString(name);
	}

	public static void reload() {
		try {
			resources = ResourceBundle.getBundle("conf.environment" + filename, Locale.getDefault());
		} catch (MissingResourceException mre) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" ContextMaster reload() ", mre);
			}
		}
	}
}
