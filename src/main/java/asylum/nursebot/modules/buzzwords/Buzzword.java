package asylum.nursebot.modules.buzzwords;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.semantics.SemanticAction;
import asylum.nursebot.semantics.SemanticContext;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
import asylum.nursebot.utils.ThreadHelper;

public class Buzzword extends SemanticInterpreter implements SemanticAction {
    private String[] buzzwords;
    private String[] reactions;
    private double chance;
    private int delay;

    public Buzzword(Module module, String[] buzzwords, String[] reactions) {
        this(module, buzzwords, reactions, 1, 0);
    }
    public Buzzword(Module module, String buzzword, String[] reactions) {
        this(module, new String[] {buzzword}, reactions);
    }
    public Buzzword(Module module, String[] buzzwords, String reaction) {
        this(module, buzzwords, new String[] {reaction});
    }
    public Buzzword(Module module, String buzzword, String reaction) {
        this(module,  new String[] {buzzword}, new String[] {reaction});
    }


    public Buzzword(Module module, String[] buzzwords, String[] reactions, WakeWordType type) {
        this(module, buzzwords, reactions, 1, 0, type);
    }
    public Buzzword(Module module, String buzzword, String[] reactions, WakeWordType type) {
        this(module, new String[] {buzzword}, reactions, type);
    }
    public Buzzword(Module module, String[] buzzwords, String reaction, WakeWordType type) {
        this(module, buzzwords, new String[] {reaction}, type);
    }
    public Buzzword(Module module, String buzzword, String reaction, WakeWordType type) {
        this(module, new String[] {buzzword}, new String[] {reaction}, type);
    }

    public Buzzword(Module module, String[] buzzwords, String[] reactions, double chance) {
        this(module, buzzwords, reactions, chance, 0);
    }
    public Buzzword(Module module, String buzzword, String[] reactions, double chance) {
        this(module, new String[] {buzzword}, reactions, chance);
    }
    public Buzzword(Module module, String[] buzzwords, String reaction, double chance) {
        this(module, buzzwords, new String[] {reaction}, chance);
    }
    public Buzzword(Module module, String buzzword, String reaction, double chance) {
        this(module, new String[] {buzzword}, new String[] {reaction}, chance);
    }

    public Buzzword(Module module, String[] buzzwords, String[] reactions, double chance, int delay) {
        this(module, buzzwords, reactions, chance, delay, WakeWordType.STANDALONE);
    }
    public Buzzword(Module module, String buzzword, String[] reactions, double chance, int delay) {
        this(module, new String[] {buzzword}, reactions, chance, delay);
    }
    public Buzzword(Module module, String[] buzzwords, String reaction, double chance, int delay) {
        this(module, buzzwords, new String[] {reaction}, chance, delay);
    }
    public Buzzword(Module module, String buzzword, String reaction, double chance, int delay) {
        this(module, new String[] {buzzword}, new String[] {reaction}, chance, delay);
    }


    public Buzzword(Module module, String[] buzzwords, String[] reactions, double chance, int delay, WakeWordType type) {
        super(module);

        setLocality(Locality.GROUPS);
        setPermission(Permission.ANY);
        setWakeWords(Arrays.stream(buzzwords).map(s -> new WakeWord(s, type, false)).collect(Collectors.toList()));
        setAction(this);

        this.buzzwords = buzzwords;
        this.reactions = reactions;
        this.chance = chance;
        this.delay = delay;

    }
    public Buzzword(Module module, String buzzword, String[] reactions, double chance, int delay, WakeWordType type) {
        this(module, new String[] {buzzword}, reactions, chance, delay, type);
    }
    public Buzzword(Module module, String[] buzzwords, String reaction, double chance, int delay, WakeWordType type) {
        this(module, buzzwords, new String[] {reaction}, chance, delay, type);
    }
    public Buzzword(Module module, String buzzword, String reaction, double chance, int delay, WakeWordType type) {
        this(module, new String[] {buzzword}, new String[] {reaction}, chance, delay, type);
    }

    @Override
    public void action(SemanticContext context) {
        Random random = new Random();

        if (random.nextDouble() >= chance)
            return;

        String reply = reactions[random.nextInt(reactions.length)];

		ThreadHelper.delay(() -> {
			context.getSender().reply(reply, context.getMessage());
		}, delay);
    }
}
