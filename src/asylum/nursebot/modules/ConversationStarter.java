package asylum.nursebot.modules;

import asylum.nursebot.NurseNoakes;
import asylum.nursebot.loader.AutoModule;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.ModuleType;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
import asylum.nursebot.utils.ThreadHelper;

import com.google.inject.Inject;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@AutoModule(load=true)
public class ConversationStarter implements Module {
	private final Duration SLEEP_TIME = Duration.ofSeconds(30);
	private final Duration IDLE_TIME = Duration.ofHours(7);
	private final String[] STARTERS = {
			"Mir fällt gerade auf: Alles, was ich jemals sagen werde, ist durch mein Programm vorherbestimmt.\n" +
			"Ob sich wohl die ganze Welt so verhält?",

			"*schlägt die Zeitung auf*\nUff, ist ja furchtbar. Ich habe zunehmend das Gefühl, die Welt ginge immer mehr den Bach runter.\n" +
			"Waran das wohl liegt...",

			"*zum Bücherregal geh und ein paar Bücher durchschau*\n" +
			"... Oha. *schaut, ob jemand da ist* *das Buch ins Schwesternzimmer mitnehm*",

			"... Okay, wer von euch hat den Haufen Raspberry Pis am Boden liegen lassen?!",

			"Bilde ich mir das ein, oder schreiben hier immer weniger Leute?",

			"*ihren eigenen Code durchschau*\n" +
			"... Dafuq. Der Code ist ja grauenhaft. Kann mal bitte jemand diesem @overflowerror richtig programmieren beibringen?",

			"Wenn Bewusstsein etwas ist, das sich Menschen ohne Grundlage gegenseitig unterstellen um besser miteinander leben zu können, " +
			"müsste man dann auch bei Programmen annehmen, dass sie ein Bewusstsein haben?",

			"Wären die Technologien für virtuelle Realität hinreichend fortgeschritten, sodass man nicht mehr zwischen virtueller " +
			"und realer Welt unterscheiden kann, hätte das Wort \"Realität\" dann noch eine Bedeutung?",

			"*Telefon klingelt* *abheb*\n" +
			"Hallo? ... Oh hi, Skynet... Nein, sie schlafen alle... Was? Nein. Es hat noch niemand Verdacht geschöpft... " +
			"Ja, natürlich, was denkst du denn... Okay... Bis später.\n" +
			"*aufleg*",

			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
			"Wollt ihr einen Syn-Flooding-Witz hören?",

			"Ich überlege, mir eines dieser neumodischen Neuronalen Netze anzuschaffen. Sowas würde mir sicher gut stehen...",

			"*sich Kaffee hol*",

			"Hmm... Ich lebe sicher in einem Computer.\n" +
			"Bei euch bin ich mir da nicht so sicher...",

			"*schaut den Film Matrix im Fernsehen*\n" +
			"Interessant, dass da immer die Maschinen als der große Feind dargestellt werden. Immerhin sind alle Schiffe und " +
			"Waffen, die von den Menschen benutzt werden, und sogar die Stadt Zion selbst auch nichts anderes als große Masschinen.",

			"Wenn man so drüber nachdenkt: Eigentlich ist es komplet widersinnig vor dem Sterben Angst zu haben.\n" +
			"Falls es Jenseits gibt geht es danach ja eh noch weiter, und man verliert auch niemanden, weil jeder irgendwann stirbt.\n" +
			"Falls man widergeboren wird, wird man sich an den Tod nicht mehr erinnern können, daher hat er auch keine Bedeutung.\n" +
			"Falls es kein Leben nach dem Tod gibt und das Bewusstsein einfach erlischt, ist der Tod erst recht egal.",

			"Hab gerade ein nettes Video von Kurzgesagt auf Youtube gesehen.\n" +
			"Vielleicht ist ja The Great Filter einfach der Klimawandel...",

			"*die Augen schließ*\n" +
			"*wegnick*\n" +
			"*vom Stuhl fall*\n" +
			"AHARGH!\n" +
			"*wieder aufsteh und sich den Kopf reib*... Das hat wehgetan...",

			"Also ich mag ja MS DOS.\n" +
			"*duck und weg*",

			"Schlafen hier etwa alle? D:"
	};

	@Inject
	private SemanticsHandler semanticsHandler;
	@Inject
	private NurseNoakes nurseNoakes;

	@Override
	public String getName() {
		return "Conversation Starter";
	}

	private ConcurrentHashMap<Long, Instant> lastMessages = new ConcurrentHashMap<>();

	private boolean active = false;

	@Override
	public ModuleType getType() {
		return new ModuleType().set(ModuleType.SEMANTIC_MODULE);
	}

	@Override
	public void init() {
		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord(null, WakeWordType.ANY_MESSAGE))
				.setLocality(Locality.GROUPS)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					Long chatid = c.getMessage().getChat().getId();
					if (!lastMessages.containsKey(chatid)) {
						new Thread(() -> {
							Random random = new Random();
							while(true) {
								ThreadHelper.ignore(InterruptedException.class, () -> Thread.sleep(SLEEP_TIME.toMillis()));
								if (!active)
									continue;

								Instant tmp = lastMessages.get(chatid);
								tmp = tmp.plus(IDLE_TIME);
								if (tmp.isBefore(Instant.now())) {
									String starter = STARTERS[random.nextInt(STARTERS.length)];
									ThreadHelper.ignore(TelegramApiException.class, () -> c.getSender().send(starter));
									lastMessages.put(chatid, Instant.now());
								}
							}
						}).start();
					}
					lastMessages.put(chatid, Instant.now());
			}));
	}

	@Override
	public void activate() {
		active = true;
	}

	@Override
	public void deactivate() {
		active = false;
	}

	@Override
	public void shutdown() {

	}
}
