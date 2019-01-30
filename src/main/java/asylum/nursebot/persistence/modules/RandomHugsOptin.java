package asylum.nursebot.persistence.modules;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import asylum.nursebot.persistence.selfbuilding.Column;
import asylum.nursebot.persistence.selfbuilding.DataType;
import asylum.nursebot.persistence.selfbuilding.Key;
import asylum.nursebot.persistence.selfbuilding.Selfbuilding;
import asylum.nursebot.persistence.selfbuilding.Type;
import org.javalite.activejdbc.Model;

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
		return Arrays.asList(
				new Column("id", new Type(DataType.BIGINT))
					.setKey(Key.PRIMARY_KEY)
					.setAutoincrement(true),
				new Column("chatid", new Type(DataType.BIGINT))
					.setNotNullFlag(true),
				new Column("userid", new Type(DataType.INT))
					.setNotNullFlag(true));
	}
}
