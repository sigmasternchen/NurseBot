package asylum.nursebot.modules;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.exceptions.NurseException;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.modules.dices.*;
import asylum.nursebot.objects.*;
import asylum.nursebot.utils.StringTools;
import asylum.nursebot.utils.log.Logger;
import com.google.inject.Inject;
import com.mysql.cj.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@AutoModule(load=true)
public class Dices implements Module {
    @Inject
    private CommandHandler commandHandler;

    private CommandCategory category;

    public Dices() {
        category = new CommandCategory("Würfel");
    }

    private Logger logger = Logger.getModuleLogger("Dices");

    @Override
    public String getName() {
        return "Dices";
    }

    @Override
    public ModuleType getType() {
        return new ModuleType()
                .set(ModuleType.COMMAND_MODULE);
    }

    private Class<? extends Dice> parseDice(String string) throws NurseException {
    	string = string.toLowerCase();
    	switch (string.toLowerCase()) {
			case "d4":
				return D4.class;
			case "d5":
				return D5.class;
			case "d6":
				return D6.class;
			case "d8":
				return D8.class;
			case "d10":
				return D10.class;
			case "d12":
				return D12.class;
			case "d20":
				return D20.class;
			default:
				throw new NurseException("Den Würfel \"" + string + "\" kenne ich nicht.");
		}
    }

    @Override
    public void init() {
        commandHandler.add(new CommandInterpreter(this)
            .setName("dice")
            .setInfo("würfelt")
            .setPermission(Permission.ANY)
            .setVisibility(Visibility.PUBLIC)
            .setLocality(Locality.EVERYWHERE)
            .setCategory(category)
            .setAction(c -> {
                List<Dice> dices = new LinkedList<>();
                if (c.getParameter().isEmpty()) {
                    dices.add(new D6());
                } else {
                    List<String> tokens = StringTools.tokenize(c.getParameter());

                    try {
                        for (String token : tokens) {
                            String[] tmp = token.split("\\.");
                            if (tmp.length == 1) {
                                dices.add(parseDice(tmp[0]).newInstance());
                            } else if (tmp.length == 2) {
                                if (!StringUtils.isStrictlyNumeric(tmp[0]))
                                    throw new NurseException("Das ist keine gültige Zahl.");
                                int n = Integer.parseInt(tmp[0]);
                                if (n < 1 || n > 10)
                                    throw new NurseException("... Hast du dir schon mal überlegt, dir einfach ein eigenes Programm dafür zu schreiben?");
                                Class<? extends Dice> clazz = parseDice(tmp[1]);
                                for (int i = 0; i < n; i++) {
                                    dices.add(clazz.newInstance());
                                }
                            } else {
                                throw new NurseException("Synopsis: /dice {[ANZAHL.]WÜRFEL}");
                            }
                        }
                        if (dices.size() == 0)
                            throw new NurseException("Mathematisch gesehen gibt es für das Ergebnis nur eine Möglichkeit, wenn man mit gar keinen Würfel spielt. Nämlich das da:");
                        if (dices.size() > 10)
                            throw new NurseException("*stolpert, und verteilt " + dices.size() + " Würfel auf dem Boden*\nOh nein... \uD83D\uDE1E");
                    } catch(NurseException e) {
                        c.getSender().reply(e.getMessage(), c.getMessage());
                    } catch (IllegalAccessException | InstantiationException e) {
                        logger.error("This is bad.");
                        logger.exception(e);
                        c.getSender().reply("Irgendwas ist da schief gelaufen. Bitte schau in den Server-Log.", c.getMessage());
                    }
                }

                StringBuilder builder = new StringBuilder();

                if (dices.size() == 1) {
                    Dice dice = dices.get(0);
                    dice.roll();
                    builder.append(dice.display());
                } else {
                    boolean first = true;
                    for (Dice dice : dices) {
                        if (first) {
                            first = false;
                        } else {
                            builder.append(", ");
                        }
                        dice.roll();
                        builder.append(dice.display() + " (" + dice.toString() + ")");
                    }
                }

                c.getSender().reply(builder.toString(), c.getMessage());
            }));

        commandHandler.add(new CommandInterpreter(this)
                .setName("distribution")
                .setInfo("macht was mit Verteilungen")
                .setPermission(Permission.ANY)
                .setVisibility(Visibility.PUBLIC)
                .setLocality(Locality.EVERYWHERE)
                .setCategory(category)
                .setAction(c -> {
                    Distribution distribution = null;
                    if (c.getParameter().isEmpty()) {
                        distribution = new NormalDistribution(0, 1);
                    } else {
                        c.getSender().reply("Das kann ich leider noch nicht.", c.getMessage());
                        return;
                    }

                    distribution.setRandom(new Random());

                    String string = distribution.getName() + ": " + distribution.getParameters() + "\n" +
                            "Result: " + distribution.generateValue();

                    c.getSender().reply(string, c.getMessage());
                }));
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
