package smallwheel.mybro.service;

import java.util.List;

import javax.sql.DataSource;

import smallwheel.mybro.domain.DbConnectionInfo;
import smallwheel.mybro.domain.GeneratingInfo;

public interface DatabaseService {

	DataSource createDataSource(String userId, DbConnectionInfo connectionInfo);

	List<String> getTables(String userId, String dbmsType);

	void generate(String userId, GeneratingInfo generatingInfo);

}
