package asylum.nursebot.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;

import asylum.nursebot.utils.ThreadHelper;
import asylum.nursebot.utils.log.Logger;
import org.javalite.activejdbc.Base;

public class Connector {
	private static final long CONNECTION_SLEEP = 30*1000;
	private Connection connection;
	private String host;
	private String schema;
	private String user;
	private String password;

	private Logger logger = Logger.getModuleLogger("Connector");
	
	private void connect() {
		Base.open(
				"com.mysql.cj.jdbc.Driver", 
				"jdbc:mysql://" + host + "/" + schema + "?useUnicode=true" +
				"&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false" +
				"&serverTimezone=UTC", user, password);
		connection = Base.connection();
	}
	
	public Connector(String host, String schema, String user, String password) {
		this.host = host;
		this.schema = schema;
		this.user = user;
		this.password = password;
		
		connect();
		
		new Thread(() -> {
			Base.attach(connection);
			
			while(true) {
					ThreadHelper.ignore(InterruptedException.class, () -> Thread.sleep(CONNECTION_SLEEP));
				
				try {
					PreparedStatement statement = Base.connection().prepareStatement("SELECT 1");
					statement.execute();
				} catch (Exception e1) {
					logger.warn("Connection closed. Reopening...");
					logger.exception(e1);
					try {
						Base.close();
					} catch (Exception ignored) {
					}
					connect();
				}
			}
		}).start();
		
		Base.detach();
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
