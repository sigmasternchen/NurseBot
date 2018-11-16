package asylum.nursebot.persistence.modules;

import java.util.Arrays;
import java.util.List;

import asylum.nursebot.modules.UserDetails;
import org.javalite.activejdbc.Model;

import asylum.nursebot.persistence.selfbuilding.Column;
import asylum.nursebot.persistence.selfbuilding.DataType;
import asylum.nursebot.persistence.selfbuilding.Key;
import asylum.nursebot.persistence.selfbuilding.Selfbuilding;
import asylum.nursebot.persistence.selfbuilding.Type;

public class UserDetailsInfo extends Model implements Selfbuilding {
	public static List<UserDetailsInfo> getAll(int authoruserid, int userid) {
		return find("userid = ? AND (authoruserid = ? OR authoruserid = ?)", userid, authoruserid, userid);
	}

	public static UserDetailsInfo get(int authoruserid, int userid) {
		List<UserDetailsInfo> tmp = find("userid = ? AND authoruserid = ?", userid, authoruserid);
		if (tmp.size() != 1)
			return null;
		return tmp.get(0);
	}

	public UserDetailsInfo() {
	}

	public UserDetailsInfo(int authoruserid, int userid) {
		setAuthorUserId(authoruserid);
		setUserId(userid);
	}

	public int getAuthorUserId() {
		return getInteger("authoruserid");
	}

	public int getUserId() {
		return getInteger("userid");
	}

	public String getText() {
		return getString("text");
	}

	private void setAuthorUserId(int id) {
		set("authoruserid", id);
	}

	private void setUserId(int id) {
		set("userid", id);
	}

	public void setText(String text) {
		set("text", text);
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
				new Column("authoruserid", new Type(DataType.INT))
						.setNotNullFlag(true),
				new Column("userid", new Type(DataType.INT))
						.setNotNullFlag(true),
				new Column("text", new Type(DataType.TEXT))
						.setNotNullFlag(true)
		});
	}
}
