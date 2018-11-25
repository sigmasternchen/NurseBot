package asylum.nursebot.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import asylum.nursebot.objects.*;
import org.javalite.activejdbc.Base;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.MessageEntity;
import org.telegram.telegrambots.api.objects.User;

import com.google.inject.Inject;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.loader.AutoDependency;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.persistence.ModelManager;
import asylum.nursebot.persistence.modules.UserLookupEntry;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
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
					
					Base.openTransaction();
					UserLookupEntry entry = getUserEntry(user.getId());
					if (entry == null)
						addUser(user);
					else if ((entry.getUsername() != null) && !(entry.getUsername().equals(user.getUserName())))
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
					User user;
					
					if (c.getMessage().isReply()) {
						user = c.getMessage().getReplyToMessage().getFrom();
					} else {
						List<User> users = getMentions(c.getMessage());
						if (users.size() == 0) {
							user = c.getMessage().getFrom();	
						} else {
							user = users.get(0);
						}
					}
					
					c.getSender().reply("Der User " + StringTools.makeMention(user) + " hat die ID " + user.getId() + ".", c.getMessage(), true);
					
				}));
	}

	private void updateUser(UserLookupEntry entry, User user) {
		entry.setUsername(user.getUserName());
		entry.setFirstname(StringTools.nullCheck(user.getFirstName(), ""));
		entry.setSurname(StringTools.nullCheck(user.getLastName(), ""));
		entry.saveIt();
	}

	private void addUser(User user) {
		UserLookupEntry entry = new UserLookupEntry();
		entry.setUserid(user.getId());
		entry.setUsername(user.getUserName());
		entry.setFirstname(StringTools.nullCheck(user.getFirstName(), ""));
		entry.setSurname(StringTools.nullCheck(user.getLastName(), ""));
		entry.saveIt();
	}

	private UserLookupEntry getUserEntry(int id) {
		return UserLookupEntry.getByUserid(id);
	}

	private UserLookupEntry getUserEntry(String username) {
		return UserLookupEntry.getByUsername(username);
	}

	public User getUser(int id) {
		UserLookupEntry entry = getUserEntry(id);
		if (entry == null)
			return null;
		return new MinimalUser(entry);
	}

	public User getUser(String name) {
		if (name.startsWith("@"))
			name = name.substring(1);

		UserLookupEntry entry = getUserEntry(name);
		if (entry == null)
			return null;
		return new MinimalUser(entry);
	}
	
	public List<User> getMentions(Message message) {
		List<User> list = getUsernameMentions(message);
		list.addAll(getNonUsernameMentions(message));
		return list;
	}
	
	private List<User> getUsernameMentions(Message message) {
		List<User> list = new LinkedList<>();
		
		for (MessageEntity entity : message.getEntities()) {
			if (!entity.getType().equals("mention"))
				continue;

			User user = getUser(entity.getText());
			if (user != null)
				list.add(user);
		}
		
		return list;
	}

	private List<User> getNonUsernameMentions(Message message) {
		List<User> list = new LinkedList<>();
		
		for (MessageEntity entity : message.getEntities()) {
			if (!entity.getType().equals("text_mention"))
				continue;
			list.add(entity.getUser());
		}
		
		return list;
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
