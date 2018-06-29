package asylum.nursebot.persistence.modules;

import java.util.Arrays;
import java.util.List;

import org.javalite.activejdbc.Model;

import asylum.nursebot.persistence.selfbuilding.Column;
import asylum.nursebot.persistence.selfbuilding.DataType;
import asylum.nursebot.persistence.selfbuilding.Key;
import asylum.nursebot.persistence.selfbuilding.Selfbuilding;
import asylum.nursebot.persistence.selfbuilding.Type;

public class BusinessCardsCardsFields extends Model implements Selfbuilding {

	@Override
	public String getSelfbuildingName() {
		return getTableName();
	}

	@Override
	public List<Column> getSelfbuildingColumns() {
		return Arrays.asList(new Column[]{
				new Column("id", new Type(DataType.INT))
					.setKey(Key.PRIMARY_KEY)
					.setAutoincrement(true),
				new Column("business_cards_card_id", new Type(DataType.INT))
					.setNotNullFlag(true),
				new Column("business_cards_field_id", new Type(DataType.INT))
					.setNotNullFlag(true),
				new Column("value", new Type(DataType.TEXT))
					.setNotNullFlag(true)
			});
	}

}
