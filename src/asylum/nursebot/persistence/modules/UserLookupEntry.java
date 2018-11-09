package asylum.nursebot.persistence.modules;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;

import asylum.nursebot.persistence.selfbuilding.Column;
import asylum.nursebot.persistence.selfbuilding.DataType;
import asylum.nursebot.persistence.selfbuilding.Key;
import asylum.nursebot.persistence.selfbuilding.Selfbuilding;
import asylum.nursebot.persistence.selfbuilding.Type;

public class UserLookupEntry extends Model implements Selfbuilding{

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
				new Column("username", new Type(DataType.TEXT))
					.setNotNullFlag(false),
				new Column("firstname", new Type(DataType.TEXT))
					.setNotNullFlag(false),
				new Column("surname", new Type(DataType.TEXT))
					.setNotNullFlag(false)
			});
	}

	public String getUsername() {
		return getString("username");
	}

	public String getFirstname() {
		return getString("firstname");
	}

	public String getSurname() {
		return getString("surname");
	}

	public Integer getUserid() {
		return getInteger("userid");
	}

	public void setUsername(String userName) {
		set("username", userName);
	}

	public void setFirstname(String firstname) {
		set("firstname", firstname);
	}

	public void setSurname(String surname) {
		set("surname", surname);
	}

	public void setUserid(Integer id) {
		set("userid", id);
	}
	
	public static UserLookupEntry getByUserid(Integer id) {
		List<UserLookupEntry> entries = find("userid = ?", id);
		if (entries == null || entries.isEmpty())
			return null;
		return entries.get(0);
	}
	
	public static UserLookupEntry getByUsername(String username) {
		List<UserLookupEntry> entries = find("username = ?", username);
		if (entries == null || entries.isEmpty())
			return null;
		return entries.get(0);
	}

}
