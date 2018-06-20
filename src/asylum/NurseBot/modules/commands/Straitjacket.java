package asylum.NurseBot.modules.commands;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.telegram.telegrambots.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.NurseNoakes;
import asylum.NurseBot.Sender;
import asylum.NurseBot.StringManager;
import asylum.NurseBot.commands.CommandInterpreter;
import asylum.NurseBot.utils.Locality;
import asylum.NurseBot.utils.Module;
import asylum.NurseBot.utils.Permission;
import asylum.NurseBot.utils.Visibility;
import asylum.NurseBot.commands.CommandCategory;
import asylum.NurseBot.commands.CommandHandler;

public class Straitjacket implements Module {
	public static final int STRIKES_TO_RESTRICT = 5;
	public static final int RESTRICT_TIME = 5*60*1000;
	public static final int STRIKE_TIMEOUT = 5*60*1000;
	
	private NurseNoakes nurse;
	
	class Strike {
		private User target;
		private User source;
		private long time;
		
		public Strike(User target, User source, long time) {
			super();
			this.target = target;
			this.source = source;
			this.time = time;
		}
		
		public String toString() {
			return target.getUserName() + " - " + source.getUserName() + " - " + time;
		}
	}
	
	private Collection<Strike> strikes = new ConcurrentLinkedQueue<Strike>();
	
	private void deleteOldStrikes() {
		long now = System.currentTimeMillis();
		
		for(Strike strike : strikes) {
			if (strike.time < now - STRIKE_TIMEOUT)
				strikes.remove(strike);
		}
	}
	
	private boolean addStrike(User target, User source) {
		for(Strike strike : strikes) {
			if (strike.target.getId().equals(target.getId()) && strike.source.getId().equals(source.getId())) {
				return false;
			}
		}
		strikes.add(new Strike(target, source, System.currentTimeMillis()));
		return true;
	}
	
	private int countStrikes(User target) {
		int i = 0; 
		
		for (Strike strike : strikes) {
			if (strike.target.getId().equals(target.getId()))
				i++;
		}
		return i;
	}
	
	private boolean checkRestrict(User target) {
		return (countStrikes(target) >= STRIKES_TO_RESTRICT);
	}
	
	private void restrict(long chatid, User target, Sender sender) {
		RestrictChatMember restrict = new RestrictChatMember(chatid, target.getId());
		restrict.setCanSendMessages(false);
		restrict.setUntilDate(0);
		
		try {
			nurse.execute(restrict);
			
			sender.mention(target, ", sei nett zu den anderen Patienten. Du bist jetzt für " + (RESTRICT_TIME / 1000 / 60) + " Minuten gesperrt.");
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
		
		
		new Thread(() -> {
			try {
				Thread.sleep(STRIKE_TIMEOUT);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			RestrictChatMember undo = new RestrictChatMember(chatid, target.getId());
			undo.setCanSendOtherMessages(true);
			undo.setCanSendMessages(true);
			undo.setCanAddWebPagePreviews(true);
			undo.setCanSendMediaMessages(true);
			
			try {
				nurse.execute(undo);
				
				sender.mention(target, ", ich hoffe, du hast deine Lektion gelernt.");
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
	private StringManager stringManager;
	
	private CommandCategory category;
	
	public Straitjacket(NurseNoakes nurse, CommandHandler commandHandler) {
		this.nurse = nurse;
		
		this.stringManager = new StringManager();
		
		this.category = new CommandCategory("Zwangsjacke");
		
		commandHandler.add(new CommandInterpreter()
				.setName("strikes")
				.setInfo("zeigt die Anzahl der eigenen Strikes an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.USER)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					deleteOldStrikes();
					c.getSender().reply("Du hast " + countStrikes(c.getMessage().getFrom()) + " Strikes.", c.getMessage());
				}));
		
		commandHandler.add(new CommandInterpreter()
				.setName("clearstrikes")
				.setInfo("")
				.setVisibility(Visibility.PRIVATE)
				.setPermission(Permission.ADMIN)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					strikes.clear();
					c.getSender().reply("Alle Strikes wurden entfernt.", c.getMessage());
				}));
		
		commandHandler.add(new CommandInterpreter()
				.setName("strike")
				.setInfo("fügt einem User einen neuen Strike hinzu")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.USER)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					if (c.getMessage().getReplyToMessage() == null) {
						c.getSender().reply("Um jemanden zu striken, musst du auf eine Nachricht desjenigen antworten.", c.getMessage());
						return;
					}
					User target = c.getMessage().getReplyToMessage().getFrom();
					User source = c.getMessage().getFrom();
					
					if (NurseNoakes.USERNAME.equals(target.getUserName())) {
						c.getSender().reply("Haha, sehr witzig.", c.getMessage());
						return;
					}

					if (target.getId().equals(source.getId())) {
						c.getSender().reply(stringManager.makeMention(target) + " waurde ge-... warte, was?! Du willst dich selbst striken. Ähm. Nein.", c.getMessage(), true);
						return;
					}
					
					deleteOldStrikes();
					if (!addStrike(target, source)) {
						c.getSender().reply("Du hast " + stringManager.makeMention(target) + " bereits gestrikt.", c.getMessage(), true);
						return;
					}
					c.getSender().reply(stringManager.makeMention(target) + " wurde gestrikt.", c.getMessage(), true);
					if (checkRestrict(target)) {
						restrict(c.getMessage().getChatId(), target, c.getSender());
					}
				}));
	}

	@Override
	public String getName() {
		return "Straitjacket";
	}
}
