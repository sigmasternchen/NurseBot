package asylum.nursebot.persistence.modules;

import asylum.nursebot.modules.birthdays.Privacy;
import asylum.nursebot.persistence.selfbuilding.*;
import org.javalite.activejdbc.Model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class BirthdaysBirthdays extends Model implements Selfbuilding {

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
				new Column("userid", new Type(DataType.INT))
						.setNotNullFlag(true),
				new Column("birthday", new Type(DataType.DATE))
						.setNotNullFlag(true),
				new Column("privacy",
						new Type(DataType.CHAR)
							.setLength(1))
						.setNotNullFlag(true)
		});
	}

	public BirthdaysBirthdays() {
	}

	public BirthdaysBirthdays(int userid) {
		set("userid", userid);
	}

	public static BirthdaysBirthdays findByUserid(int userid) {
		List<BirthdaysBirthdays> tmp = find("userid = ?", userid);
		if (tmp.size() != 1)
			return null;
		return tmp.get(0);
	}

	public LocalDate getBirthday() {
		return LocalDate.parse(getString("birthday"));
	}

	public void setBirthday(LocalDate date) {
		set("birthday", date.toString());
	}

	public Privacy getPrivacy() {
		return Privacy.fromCharacter(getString("privacy").charAt(0));
	}

	public void setPrivacy(Privacy privacy) {
		set("privacy", privacy.getCharacter());
	}
}
