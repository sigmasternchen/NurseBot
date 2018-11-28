package asylum.nursebot.modules;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.inject.Inject;

import asylum.nursebot.commands.CommandCategory;
import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.commands.CommandInterpreter;
import asylum.nursebot.exceptions.NurseException;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.modules.dices.ContinuousUniformDistribution;
import asylum.nursebot.modules.dices.D10;
import asylum.nursebot.modules.dices.D12;
import asylum.nursebot.modules.dices.D20;
import asylum.nursebot.modules.dices.D4;
import asylum.nursebot.modules.dices.D6;
import asylum.nursebot.modules.dices.D8;
import asylum.nursebot.modules.dices.Dice;
import asylum.nursebot.modules.dices.DiscreteUniformDistribution;
import asylum.nursebot.modules.dices.Distribution;
import asylum.nursebot.modules.dices.NormalDistribution;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.ModuleType;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.objects.Visibility;
import asylum.nursebot.utils.StringTools;
import asylum.nursebot.utils.log.Logger;

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
    	switch (string) {
			case "d4":
				return D4.class;
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
				if (string.startsWith("d")) {
					String tmp = string.substring(1);
					if (!StringTools.isInteger(tmp))
						throw new NurseException("\"" + string + "\" ist keine gültige Würfel-Bezeichnung.");
					int n = Integer.parseInt(tmp);
					if (n <= 0)
						throw new NurseException("... Betreibst du da Boundary Testing?");

					throw new NurseException("Das ist kein Standard-Würfel.\nBitte benutze /distribution discrete-uniform 1 " + n);

				}
				throw new NurseException("Das äh hab ich nicht ganz verstanden.");
		}
    }

    private Class<? extends Distribution> parseDistribution(String string) throws NurseException {
    	string = string.toLowerCase();
		switch (string) {
			case "discrete-uniform":
				return DiscreteUniformDistribution.class;
			case "normal":
				return NormalDistribution.class;
			case "uniform":
			case "continuous-uniform":
				return ContinuousUniformDistribution.class;
			default:
				throw new NurseException("Diese Verteilung kenne ich noch nicht.");
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
                                if (!StringTools.isInteger(tmp[0]))
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
                        return;
                    } catch (IllegalAccessException | InstantiationException e) {
                        logger.error("This is bad.");
                        logger.exception(e);
                        c.getSender().reply("Irgendwas ist da schief gelaufen. Bitte schau in den Server-Log.", c.getMessage());
                        return;
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
                        distribution = new NormalDistribution();
                    } else {
                    	try {
							List<String> tokens = StringTools.tokenize(c.getParameter());

							distribution = parseDistribution(tokens.get(0)).newInstance();

							tokens.remove(0);

							if (tokens.size() > 0) {
								Number[] parameters = new Number[tokens.size()];
								for (int i = 0; i < tokens.size(); i++) {
									String tmp = tokens.get(i);
									if (!StringTools.isNumeric(tmp))
										throw new NurseException("Die Parameter müssen Zahlen sein.");
									if (StringTools.isInteger(tmp))
										parameters[i] = new Integer(tmp);
									else
										parameters[i] = new Double(tmp);
								}

								distribution.setParameter(parameters);
							}
						} catch(NurseException e) {
							c.getSender().reply(e.getMessage(), c.getMessage());
							return;
						} catch (IllegalAccessException | InstantiationException e) {
							logger.error("This is bad.");
							logger.exception(e);
							c.getSender().reply("Irgendwas ist da schief gelaufen. Bitte schau in den Server-Log.", c.getMessage());
							return;
						}
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
