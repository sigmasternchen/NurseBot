package asylum.nursebot.persistence.modules;

import asylum.nursebot.persistence.selfbuilding.*;
import org.javalite.activejdbc.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class RandomHugsOptin extends Model implements Selfbuilding {

	public static RandomHugsOptin find(Long chatid, Integer userid) {
		List<RandomHugsOptin> list = find("chatid = ? AND userid = ?", chatid, userid);
		if (list.size() != 1)
			return null;
		return list.get(0);
	}

	public RandomHugsOptin() {
	}

	public RandomHugsOptin(Long chatid, Integer userid) {
		Objects.requireNonNull(chatid);
		Objects.requireNonNull(userid);

		set("chatid", chatid);
		set("userid", userid);
	}

	public Long getChatId() {
		return getLong("chatid");
	}

	public Integer getUserId() {
		return getInteger("userid");
	}

	@Override
	public String getSelfbuildingName() {
		return getTableName();
	}
	
	@Override
	public List<Column> getSelfbuildingColumns() {
		return Arrays.asList(new Column[]{
			new Column("id", new Type(DataType.BIGINT))
				.setKey(Key.PRIMARY_KEY)
				.setAutoincrement(true),
			new Column("chatid", new Type(DataType.BIGINT))
				.setNotNullFlag(true),
			new Column("userid", new Type(DataType.INT))
				.setNotNullFlag(true)
		});
	}
}
