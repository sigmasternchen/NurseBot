package asylum.nursebot.modules;

import java.util.List;

import org.javalite.activejdbc.Base;
import org.telegram.telegrambots.api.objects.User;

import com.google.inject.Inject;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoDependency;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.ModuleType;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.objects.Visibility;
import asylum.nursebot.persistence.ModelManager;
import asylum.nursebot.persistence.modules.UserLookupEntry;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
import asylum.nursebot.utils.MessageUtils;
import asylum.nursebot.utils.StringTools;

@AutoModule(load=true)
@AutoDependency
public class UserLookup implements Module {

	@Inject
	private SemanticsHandler semanticsHandler;
	
	@Inject
	private CommandHandler commandHandler;
	
	private CommandCategory category;
	
	@Override
	public String getName() {
		return "User Lookup";
	}

	@Override
	public ModuleType getType() {
		return new ModuleType()
				.set(ModuleType.SEMANTIC_MODULE)
				.set(ModuleType.COMMAND_MODULE)
				.set(ModuleType.DEPENDENCY_MODULE);
	}

	public UserLookup() {
		category = new CommandCategory("User Lookup");
		
		ModelManager.build(UserLookupEntry.class);
	}
	
	@Override
	public void init() {
		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord(null, WakeWordType.ANY_MESSAGE))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					
					User user = c.getMessage().getFrom();
					if (user.getUserName() == null)
						return;
					
					Base.openTransaction();
					UserLookupEntry entry = getUser(user.getId());
					if (entry == null)
						addUser(user);
					else if (!(entry.getUsername().equals(user.getUserName())))
						updateUser(entry, user);
					Base.commitTransaction();
				}));
		
		commandHandler.add(new CommandInterpreter(this)
				.setName("id")
				.setInfo("zeigt die UserID für einen Benutzernamen an")
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.ANY)
				.setVisibility(Visibility.PRIVATE)
				.setCategory(category)
				.setAction(c -> {
					List<User> users = MessageUtils.getMentionedUsers(c.getMessage());
					
					if (users.size() == 0) {
						c.getSender().reply("Deine UserID ist " + c.getMessage().getFrom().getId() + ".", c.getMessage());
					} else {
						User user = users.get(0);
						c.getSender().reply("Der User " + StringTools.makeMention(user) + " hat die ID " + user.getId() + ".", c.getMessage(), true);
					}
				}));
	}

	private void updateUser(UserLookupEntry entry, User user) {
		entry.setUsername(user.getUserName());
		entry.saveIt();
	}

	private void addUser(User user) {
		UserLookupEntry entry = new UserLookupEntry();
		entry.setUserid(user.getId());
		entry.setUsername(user.getUserName());
		entry.saveIt();
	}

	private UserLookupEntry getUser(int id) {
		return UserLookupEntry.getByUserid(id);
	}

	public String getUsername(int id) {
		UserLookupEntry entry = getUser(id);
		if (entry == null)
			return null;
		return entry.getUsername();
	}
	
	public Integer getUserId(String username) {
		UserLookupEntry entry = UserLookupEntry.getByUsername(username);
		if (entry == null)
			return null;
		return (Integer) entry.getUserid();
	}
	
	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}

	@Override
	public void shutdown() {
	}

}
