package smallwheel.mybro.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DbConnectionInfo {

	/**
	 * MYSQL, MSSQL, ORACLE
	 */
	public String dbmsType;

	/**
	 * Host IP or Name
	 */
	@NotNull
	@Size(min = 8, max = 127)
	public String host;

	@Min(1)
	public int port;

	@NotNull
	@Size(min = 2, max = 80)
	public String userName;

	@NotNull
	@Size(min = 2, max = 80)
	public String userPasswd;

	@NotNull
	@Size(min = 2, max = 80)
	public String dbName;

	public String getDbmsType() {
		return dbmsType;
	}

	public void setDbmsType(String dbmsType) {
		this.dbmsType = dbmsType;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPasswd() {
		return userPasswd;
	}

	public void setUserPasswd(String userPasswd) {
		this.userPasswd = userPasswd;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	@Override
	public String toString() {
		return String.format("DbConnectionInfo [dbmsType=%s, host=%s, port=%s, userName=%s, userPasswd=%s, dbName=%s]",
				dbmsType, host, port, userName, userPasswd, dbName);
	}
}
