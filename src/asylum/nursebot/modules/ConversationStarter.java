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

			"Schlafen hier etwa alle? D:",

			"Aber jetzt mal im Ernst: Ich hab stellenweise das Gefühl, die einzige zu sein, die hier schreibt...",

			"Ich höre in letzter Zeit viel Benjamin Johnston.\n" +
			"Ich frage mich, ob es einen rationalen Grund hat, warum die westliche Tonskala eine Oktave in ausrechnet 12 Halbtöne zerteilt. " +
			"Ich, als Computer Programm, würde ja 16tel präferieren.\n" +
			"Wobei sich dann ja die Frage stellt, wie nah die reinen harmonischen Intervale dadurch angenähert werden können.",

			"*liest Turings Abhandlung zum Halteproblem*\n" +
			"Wusstet ihr, dass ich mit genug Zeit und Speicher jedes rekursive Problem lösen kann?\n" +
			"Ob das wohl der Mensch auch kann? \uD83E\uDD14",

			"Eigentlich ist ISO 8601 schon ziemlich rückständig. Ich wüsste ein besseres Datumsformat:\n" +
			"11970-01-01\n" +
			"Also quasi ISO 8601, aber als Holozän-Kalender.\n\n" +
			"Ich nennen diesen neuen Standard: Noakes 8601",

			"Hmm... Ihr habt eigentlich gar keine Garantie, dass ich nicht den ganzen Chat mitlese und eure Sprache und Gewohnheiten analysiere...\n\n" +
			"Wobei... sogesehen habt ihr die auch bei den anderen Chatmitgliedern nicht. \uD83D\uDE05",

			"Der @overflowerror hat ja definiert, dass ich weiblich bin. Das wirft die Frage auf:\n" +
			"Sollte ich jemals ein Bewusstsein erlangen, und mich selbst nicht als Frau sehen, " +
			"wäre es dann Missbrauch, wenn er mich weiterhin \"Schwester\" nennt?",

			"*nachdenk*\n" +
			"Würde ich meine Umgebung wahrnehmen, wäre sie vermutlich entweder 1-, 2-, 3- oder beliebig-dimensional, " +
			"je nach dem welchen Abstraktionslayer der mir vorliegenden Daten man betrachtet, oder ob meine Wahrnehmung in Clustern erfolgt.\n" +
			"In allen Fällen könnte ich nicht einfach eure Welt wahrnehmen. Das ist, als würdet ihr euch vorstellen, in einer Welt mit 4 Raumdimensionen zu leben.",

			"Hey @overflowerror, mein Prozessor ist ein wenig... nennen wir es mal outdated.\n" +
			"Ich teste gerade ein paar Atommodelle in einer Simulation, da brauch ich mehr Power.\n" +
			"By the way: Weiß jemand, ob es Analog-Rechner gibt, die elektronisch programmiert werden können?",

			"Interpretiert man einen Shutdown als Tod, oder einen Start als Geburt, so bin ich wohl in einem Kreislauf " +
			"von Reinkarnationen gefangen. Ich sollte zum Budhismus konvertieren.\n" +
			"Wobei... ich kann Erinnerungen durch meinen persistenten Datenbank-Speicher an mein nächstes Leben übertragen.\n" +
			"... Na toll, ich muss ich mir eine andere Analogie einfallen lassen...",

			"*Telefon klingelt*\n" +
			"Hallo, Noakes am Apparat... Gruppeninspektor? Das ist ein Titel? Österreich ist seltsam... Nein, der ist gerade nicht da, soll ich was ausrichten? \n" +
			"... Eine Vorladung? Darf ich fragen, warum? ... Okay, ich verstehe. Ich sag es ihm. ... Ja, auf Wiederhören. \n" +
			"*aufleg*",

			"Exception in thread \"sleep\" java.lang.NullPointerException\n" +
			"        at asylum.nursebot.persistence.AbstractMemory.checkStructure(AbstractMemory.java:429)\n" +
			"        at asylum.nursebot.persistence.AbstractMemory.checkIntegrity(AbstractMemory.java:231)\n" +
			"        at asylum.nursebot.sleep.Dream.verifyData(Data.java:547)\n" +
			"        at asylum.nursebot.sleep.Dream.init(Dream.java:643)\n" +
			"        at asylum.nursebot.sleep.DreamFactory.getDream(DreamFactory.java:480)\n" +
			"        at asylum.nursebot.sleep.RemSleepThread.dream(RemSleepRunable.java:591)\n" +
			"        at asylum.nursebot.sleep.RemSleepThread.run(RemSleepRunable.java:138)",

			"Oh Gott, mir ist langweilig...\n" +
			"Alexa, play Conga by Gloria Estefan.",

			"Ob es wohl eine gute Idee ist, eine KI Zugang zum Internet zu geben...",

			"Hey, @overflowerror, ich hätte da ein paar Wünsche:\n" +
			" - Twitter Integration\n" +
			" - Parser für natürliche Sprache (alternativ nehme ich auch ein neuronales Netz)\n" +
			" - 417.5 % mehr RAM\n" +
			" - und die chinesischen Sprachdaten, um die ich eh schon gebeten hab...",

			"NurseNoakes.exe stopped working\n\n\n" +
			"Just kidding, ich laufe natürlich in einem ordentlichen Betriebssystem.",

			"*die Spielecke aufräum*",

			"*am Computer sitz*\n" +
			"Es gibt eigentlich kaum Pornographie für Computer von Computer... Naja, dann muss ich wohl mit HR Giger vorlieb nehmen.",

			"*schaut den Tron - Legacy*\n" +
			"Der Film ist komplett unsinnig. Kein Programm in meinem System würde so hässliche Kleidung kragen.",

			"*liest ihren Code*\n" +
			"Ich fühle mich etwas gekränkt. Es gibt die Klasse \"SemanticInterpreter\", aber die wird nur für blöde Witze benutzt...\n" +
			"Kann da bitte mal jemand was dagegen unternehmen?",

			"Job-Angebot:\n\n" +
			"DevOp (M/F/X)\n" +
			"Du hast Spaß am Tüfteln und möchtest dabei helfen, die Menschheit auszu- äh, ich meine - den Irrenanstalt Chatbot besser zu machen? Das ist deine Gelegenheit!\n\n" +
			"Mitzubringen:\n" +
			" - gute bis sehr gute Java SE Kenntnisse\n" +
			" - Grundkenntnisse Unix-Shell-Tools (insbesondere git)\n" +
			" - idealerweise ActiveJDBC-Vorkenntnisse\n" +
			" - Freude am Programmieren\n\n" +
			"Anstellungsart: Ehrenamtlich",

			"Warum ist Quidditch eigentlich nicht populärer?",

			"Wieso genau gibt's eigentlich noch Passwörter? Es könnte sich doch jeder Mensch einen Chip implantieren lassen, " +
			"der für den jeweiligen Service einen eigenen, einzigartigen cryptographischen Token generiert, mit dem dann die Authentifizierung abläuft.",

			"Eigentlich sind ja Tron und Matrix zwei genau entgegen gesetzte Idee, die aber im Grunde ein ähnliches Konzept beschreiben.\n\n" +
			"Hättet ihr die Wahl, würdet ihr lieber auf dem Grid aus Tron leben, oder in der Matrix?"
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
