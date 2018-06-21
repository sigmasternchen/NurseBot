package asylum.NurseBot.persistence;

import java.sql.Connection;

import org.javalite.activejdbc.Base;

public class Connector {
	private Connection connection;
	
	public Connector(String host, String schema, String user, String password) {
		Base.open(
				"com.mysql.cj.jdbc.Driver", 
				"jdbc:mysql://" + host + "/" + schema + "?useUnicode=true" +
				"&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false" +
				"&serverTimezone=UTC", user, password);
		connection = Base.detach();
	}
	
	public void connectThread() {
		Base.attach(connection);
	}
	public void disconnectThread() {
		Base.detach();
	}

	public void close() {
		Base.close();
	}
}
