package asylum.nursebot.persistence.selfbuilding;

import java.util.List;

import asylum.nursebot.utils.log.Logger;
import org.javalite.activejdbc.Base;

public interface Selfbuilding {
	
	String getSelfbuildingName();
	List<Column> getSelfbuildingColumns();
	
	default boolean selfbuild() {
		List<Column> list = getSelfbuildingColumns();

		Logger logger = Logger.getModuleLogger("Selfbuilding");
		
		logger.verbose("Checking for table " + getSelfbuildingName() + "...");
		boolean present = Base.firstCell("SHOW TABLES LIKE '" + getSelfbuildingName() + "'") != null;
		
		if (present) {
			logger.verbose("... present");
		} else {
			logger.verbose("... missing");
			StringBuilder builder = new StringBuilder();
			builder.append("CREATE TABLE IF NOT EXISTS `").append(getSelfbuildingName()).append("`\n(\n");
			boolean first = true;
			for(Column column : list) {
				if (first) {
					first = false;
				} else {
					builder.append(", \n");
				}
				builder.append(column.toString());
			}
			builder.append("\n)");
			
			logger.info("Creating table " + getSelfbuildingName() + "...");
			try {
				Base.exec(builder.toString());
			} catch (Exception e) {
				logger.info("... failed.");
				throw e;
			}
			logger.info("... success");
		}
		
		return !present;
	}
}
