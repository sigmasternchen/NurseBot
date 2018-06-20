package asylum.NurseBot.modules.semantics;

import java.util.Random;

import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.semantics.SemanticInterpreter;
import asylum.NurseBot.semantics.SemanticsHandler;
import asylum.NurseBot.semantics.WakeWord;
import asylum.NurseBot.semantics.WakeWordType;
import asylum.NurseBot.utils.Locality;
import asylum.NurseBot.utils.Module;
import asylum.NurseBot.utils.Permission;

public class BasicInteractions implements Module {

	public BasicInteractions(SemanticsHandler semanticsHandler) {
		
		
		semanticsHandler.add(new SemanticInterpreter()
				.addWakeWord(new WakeWord("mau", WakeWordType.STANDALONE, false))
				.setLocality(Locality.EVERYWHERE)
				.setPermission(Permission.USER)
				.setAction(c -> {
					String[] replys = new String[] {
							"*streichel*", "Mau", "*flausch*"
					};
					
					new Thread(() -> {
						Random random = new Random();
						
						try {
							Thread.sleep((random.nextInt(5) + 1) * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						try {
							c.getSender().reply(replys[random.nextInt(replys.length)], c.getMessage());
						} catch (TelegramApiException e) {
							e.printStackTrace();
						}
					}).start();
				}));
	}
	
	@Override
	public String getName() {
		return "BasicInteractions";
	}

}
