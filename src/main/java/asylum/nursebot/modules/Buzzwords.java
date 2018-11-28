package asylum.nursebot.modules;

import asylum.nursebot.NurseNoakes;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.modules.buzzwords.Buzzword;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.ModuleType;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWordType;
import com.google.inject.Inject;

import java.util.LinkedList;
import java.util.List;

@AutoModule(load=true)
public class Buzzwords implements Module {
    @Inject
    private SemanticsHandler semanticsHandler;


    private List<Buzzword> buzzwords = new LinkedList<>();
    @Override
    public String getName() {
        return "Buzzwords";
    }

    @Override
    public ModuleType getType() {
        return new ModuleType().set(ModuleType.SEMANTIC_MODULE);
    }

    @Override
    public void init() {
        buzzwords.add(new Buzzword(this, new String[] {
                    "scheiße", "scheiß", "fuck"
            }, new String[]{
                    "Ich dulde keine Kraftausdrücke hier!", "Hey! Achte auf deine Sprache!", "Hey! Es sind Kinder anwesend."
            }, 0.2, 0, WakeWordType.ANYWHERE));
        buzzwords.add(new Buzzword(this, new String[] {
                    "mau", "mau.", "miau", "miau.", "meow", "meow.", "nyan", "nyan."
            }, new String[] {
                    "*streichel*", "Mau", "*flausch*", ":3"
        }, 1, 2000));



        for (Buzzword buzzword : buzzwords) {
            semanticsHandler.add(buzzword);
        }
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
