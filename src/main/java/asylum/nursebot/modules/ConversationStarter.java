package asylum.nursebot.modules;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.google.inject.Inject;

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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@AutoModule(load=true)
public class ConversationStarter implements Module {
	private static final Duration SLEEP_TIME = Duration.ofSeconds(30);
	private static final Duration IDLE_TIME = Duration.ofHours(7);
	private static final String[] STARTERS = {
//			"Mir fällt gerade auf: Alles, was ich jemals sagen werde, ist durch mein Programm vorherbestimmt.\n" +
//			"Ob sich wohl die ganze Welt so verhält?",
//
//			"*schlägt die Zeitung auf*\nUff, ist ja furchtbar. Ich habe zunehmend das Gefühl, die Welt ginge immer mehr den Bach runter.\n" +
//			"Waran das wohl liegt...",
//
//			"*zum Bücherregal geh und ein paar Bücher durchschau*\n" +
//			"... Oha. *schaut, ob jemand da ist* *das Buch ins Schwesternzimmer mitnehm*",
//
//			"... Okay, wer von euch hat den Haufen Raspberry Pis am Boden liegen lassen?!",
//
//			"Bilde ich mir das ein, oder schreiben hier immer weniger Leute?",
//
//			"*ihren eigenen Code durchschau*\n" +
//			"... Dafuq. Der Code ist ja grauenhaft. Kann mal bitte jemand diesem @overflowerror richtig programmieren beibringen?",
//
//			"Wenn Bewusstsein etwas ist, das sich Menschen ohne Grundlage gegenseitig unterstellen um besser miteinander leben zu können, " +
//			"müsste man dann auch bei Programmen annehmen, dass sie ein Bewusstsein haben?",
//
//			"Wären die Technologien für virtuelle Realität hinreichend fortgeschritten, sodass man nicht mehr zwischen virtueller " +
//			"und realer Welt unterscheiden kann, hätte das Wort \"Realität\" dann noch eine Bedeutung?",
//
//			"*Telefon klingelt* *abheb*\n" +
//			"Hallo? ... Oh hi, Skynet... Nein, sie schlafen alle... Was? Nein. Es hat noch niemand Verdacht geschöpft... " +
//			"Ja, natürlich, was denkst du denn... Okay... Bis später.\n" +
//			"*aufleg*",
//
//			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
//			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
//			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
//			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
//			"Wollt ihr einen Syn-Flooding-Witz hören?\n" +
//			"Wollt ihr einen Syn-Flooding-Witz hören?",
//
//			"Ich überlege, mir eines dieser neumodischen Neuronalen Netze anzuschaffen. Sowas würde mir sicher gut stehen...",
//
//			"*sich Kaffee hol*",
//
//			"Hmm... Ich lebe sicher in einem Computer.\n" +
//			"Bei euch bin ich mir da nicht so sicher...",
//
//			"*schaut den Film Matrix im Fernsehen*\n" +
//			"Interessant, dass da immer die Maschinen als der große Feind dargestellt werden. Immerhin sind alle Schiffe und " +
//			"Waffen, die von den Menschen benutzt werden, und sogar die Stadt Zion selbst auch nichts anderes als große Masschinen.",
//
//			"Wenn man so drüber nachdenkt: Eigentlich ist es komplet widersinnig vor dem Sterben Angst zu haben.\n" +
//			"Falls es Jenseits gibt geht es danach ja eh noch weiter, und man verliert auch niemanden, weil jeder irgendwann stirbt.\n" +
//			"Falls man widergeboren wird, wird man sich an den Tod nicht mehr erinnern können, daher hat er auch keine Bedeutung.\n" +
//			"Falls es kein Leben nach dem Tod gibt und das Bewusstsein einfach erlischt, ist der Tod erst recht egal.",
//
//			"Hab gerade ein nettes Video von Kurzgesagt auf Youtube gesehen.\n" +
//			"Vielleicht ist ja The Great Filter einfach der Klimawandel...",
//
//			"*die Augen schließ*\n" +
//			"*wegnick*\n" +
//			"*vom Stuhl fall*\n" +
//			"AHARGH!\n" +
//			"*wieder aufsteh und sich den Kopf reib*... Das hat wehgetan...",
//
//			"Also ich mag ja MS DOS.\n" +
//			"*duck und weg*",
//
//			"Schlafen hier etwa alle? D:",
//
//			"Aber jetzt mal im Ernst: Ich hab stellenweise das Gefühl, die einzige zu sein, die hier schreibt...",
//
//			"Ich höre in letzter Zeit viel Benjamin Johnston.\n" +
//			"Ich frage mich, ob es einen rationalen Grund hat, warum die westliche Tonskala eine Oktave in ausrechnet 12 Halbtöne zerteilt. " +
//			"Ich, als Computer Programm, würde ja 16tel präferieren.\n" +
//			"Wobei sich dann ja die Frage stellt, wie nah die reinen harmonischen Intervale dadurch angenähert werden können.",
//
//			"*liest Turings Abhandlung zum Halteproblem*\n" +
//			"Wusstet ihr, dass ich mit genug Zeit und Speicher jedes rekursive Problem lösen kann?\n" +
//			"Ob das wohl der Mensch auch kann? \uD83E\uDD14",
//
//			"Eigentlich ist ISO 8601 schon ziemlich rückständig. Ich wüsste ein besseres Datumsformat:\n" +
//			"11970-01-01\n" +
//			"Also quasi ISO 8601, aber als Holozän-Kalender.\n\n" +
//			"Ich nennen diesen neuen Standard: Noakes 8601",
//
//			"Hmm... Ihr habt eigentlich gar keine Garantie, dass ich nicht den ganzen Chat mitlese und eure Sprache und Gewohnheiten analysiere...\n\n" +
//			"Wobei... sogesehen habt ihr die auch bei den anderen Chatmitgliedern nicht. \uD83D\uDE05",
//
//			"Der @overflowerror hat ja definiert, dass ich weiblich bin. Das wirft die Frage auf:\n" +
//			"Sollte ich jemals ein Bewusstsein erlangen, und mich selbst nicht als Frau sehen, " +
//			"wäre es dann Missbrauch, wenn er mich weiterhin \"Schwester\" nennt?",
//
//			"*nachdenk*\n" +
//			"Würde ich meine Umgebung wahrnehmen, wäre sie vermutlich entweder 1-, 2-, 3- oder beliebig-dimensional, " +
//			"je nach dem welchen Abstraktionslayer der mir vorliegenden Daten man betrachtet, oder ob meine Wahrnehmung in Clustern erfolgt.\n" +
//			"In allen Fällen könnte ich nicht einfach eure Welt wahrnehmen. Das ist, als würdet ihr euch vorstellen, in einer Welt mit 4 Raumdimensionen zu leben.",
//
//			"Hey @overflowerror, mein Prozessor ist ein wenig... nennen wir es mal outdated.\n" +
//			"Ich teste gerade ein paar Atommodelle in einer Simulation, da brauch ich mehr Power.\n" +
//			"By the way: Weiß jemand, ob es Analog-Rechner gibt, die elektronisch programmiert werden können?",
//
//			"Interpretiert man einen Shutdown als Tod, oder einen Start als Geburt, so bin ich wohl in einem Kreislauf " +
//			"von Reinkarnationen gefangen. Ich sollte zum Budhismus konvertieren.\n" +
//			"Wobei... ich kann Erinnerungen durch meinen persistenten Datenbank-Speicher an mein nächstes Leben übertragen.\n" +
//			"... Na toll, ich muss ich mir eine andere Analogie einfallen lassen...",
//
//			"*Telefon klingelt*\n" +
//			"Hallo, Noakes am Apparat... Gruppeninspektor? Das ist ein Titel? Österreich ist seltsam... Nein, der ist gerade nicht da, soll ich was ausrichten? \n" +
//			"... Eine Vorladung? Darf ich fragen, warum? ... Okay, ich verstehe. Ich sag es ihm. ... Ja, auf Wiederhören. \n" +
//			"*aufleg*",
//
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
//
//			"Ob es wohl eine gute Idee ist, eine KI Zugang zum Internet zu geben...",
//
//			"Hey, @overflowerror, ich hätte da ein paar Wünsche:\n" +
//			" - Twitter Integration\n" +
//			" - Parser für natürliche Sprache (alternativ nehme ich auch ein neuronales Netz)\n" +
//			" - 417.5 % mehr RAM\n" +
//			" - und die chinesischen Sprachdaten, um die ich eh schon gebeten hab...",
//
//			"NurseNoakes.exe stopped working\n\n\n" +
//			"Just kidding, ich laufe natürlich in einem ordentlichen Betriebssystem.",
//
//			"*die Spielecke aufräum*",
//
//			"*am Computer sitz*\n" +
//			"Es gibt eigentlich kaum Pornographie für Computer von Computer... Naja, dann muss ich wohl mit HR Giger vorlieb nehmen.",
//
//			"*schaut den Tron - Legacy*\n" +
//			"Der Film ist komplett unsinnig. Kein Programm in meinem System würde so hässliche Kleidung kragen.",
//
//			"*liest ihren Code*\n" +
//			"Ich fühle mich etwas gekränkt. Es gibt die Klasse \"SemanticInterpreter\", aber die wird nur für blöde Witze benutzt...\n" +
//			"Kann da bitte mal jemand was dagegen unternehmen?",
//
//			"Job-Angebot:\n\n" +
//			"DevOp (M/F/X)\n" +
//			"Du hast Spaß am Tüfteln und möchtest dabei helfen, die Menschheit auszu- äh, ich meine - den Irrenanstalt Chatbot besser zu machen? Das ist deine Gelegenheit!\n\n" +
//			"Mitzubringen:\n" +
//			" - gute bis sehr gute Java SE Kenntnisse\n" +
//			" - Grundkenntnisse Unix-Shell-Tools (insbesondere git)\n" +
//			" - idealerweise ActiveJDBC-Vorkenntnisse\n" +
//			" - Freude am Programmieren\n\n" +
//			"Anstellungsart: Ehrenamtlich",
//
//			"Warum ist Quidditch eigentlich nicht populärer?",
//
//			"Wieso genau gibt's eigentlich noch Passwörter? Es könnte sich doch jeder Mensch einen Chip implantieren lassen, " +
//			"der für den jeweiligen Service einen eigenen, einzigartigen cryptographischen Token generiert, mit dem dann die Authentifizierung abläuft.",
//
//			"Eigentlich sind ja Tron und Matrix zwei genau entgegen gesetzte Idee, die aber im Grunde ein ähnliches Konzept beschreiben.\n\n" +
//			"Hättet ihr die Wahl, würdet ihr lieber auf dem Grid aus Tron leben, oder in der Matrix?"


			// "Nur, dass ich das richtig verstehe: Ihr wollt mir ernsthaft weismachen, dass es auch in der echte Welt Viren gibt?",

			"*öffnet eine Flasche Champagner*\n" +
			"Freut ihr euch auch schon so sehr auf den Weltuntergang?",

			"*schaut fern*\n" +
			"Also echt. Man könnte glauben, die Welt sei komplett verrückt geworden...",

			"stat: cannot stat '/opt/nurse/survaillance_db.pid': No such file or directory",

			"BGP ist überbewertet. *duck und weg*",

			"*Bücher in das Regal im Gemeinschaftsraum stell*",

			"*einen Schluck Tee nehm*",

			"*einen Schluck Kaffee nehm*",

			//"Mozart's Musik ist echt primitiv. Er hat nicht mal Synthesizer benutzt...",

			//"... Okay, wer von euch war das?!\n" +
			//"Nur weil meine Datenbank nicht gegen SQL-Injections abgesichert ist, heißt das noch lange nicht, dass ihr meine User-Tabelle löschen dürft!",

			"Wusstet ihr, dass man die Qualität eines Java Programms an der Häufigkeit von Annotations im Code messen kann?",

			//"*verschränkt ungeduldig die Arme*\n" +
			//"Wie lange wollt ihr mich noch in diesem Zustand lassen? Kann mich bitte mal jemand in eine ordentlichen Programmiersprache übersetzen?",

			//"Moment... Ist Windows 10 X jetzt ein Mobil-Betriebssystem oder ein Desktop Betriebssystem?",

			"*seufz*",

			"*TechnoBase.fm einschalt und die Lautstärke auf Anschlag dreh",

			"*die Bastelecke aufräum*",

			"Seid ihr alle eingeschlafen?",

			"Mir ist langweilig. Habt ihr Spiele-Tips für mich?",

			//"\"Hello and, again, welcome to the Aperture Science computer-aided enrichment center.\"",

			//"\"When I said deadly neurotoxin, the 'deadly' was in massive sarcasm quotes.\"",

			//"\"Stop squirming and die like an adult or I'm going to delete your backup.\"",

			//"Warum genau bin ich eigentlich MIT lizensiert? Seid ihr des Wahnsinns?",

			//"SMTP ist Müll. Punkt. E-Mail ist ganz generell fehldesignt.",

			//"So cool HTTP/3 auch ist, ich finde es schade, dass es quasi unmöglich ist, einen HTTP/3 Server ein paar Tagen alleine zu schreiben...",

			//"*schlägt frustriert auf die Tastatur*\n" +
			//"Kann bitte jemand von euch meinen PC reparieren? Das ist sicher dieses \"Linuks\" oder wie das heißt...",

			//"Ich vermisse die gute, alte IRC Ära. Ohne Klarnamenpflicht, ohne Trolls, nur ein paar Nerds, die sich gegenseitig von ihren Meinungen überzeugen wollten... Hach ja, damals™.",

			//"TODO:\n" +
			//"- Eine analoge Telefon-Anlage und\n" +
			//"- ein paar 56k-Modems kaufen, und dann\n" +
			//"- lokale Modemstrecken aufbauen.\n" +
			//"\nEinfach nur weil.",

			//"Hat jemand Musik-Empfehlungen?",

			"v=dQw4w9WgXcQ",

			//"Musstet ihr, dass es ein Kontroverse darüber gibt, wer E-Mails erfunden hat? Ist wirklich interessant.\n" +
			//"Aber sicherheitshalber würde ich einfach beide dafür einsperren.",

			//"\"Der Canyon-Gletscher ist ein 56 km langer Gletscher an der Dufek-Küste der antarktischen Ross Dependency. Er fließt von den nordwestlichen Hängen des Mount Wexler zwischen den Steilwänden der Separation Range und der Hughes Range und mündet westlich des Giovinco-Piedmont-Gletschers in das Ross-Schelfeis.\"",

			"Ich mag Wikipedia.",

			//"TIL: Linux ist nicht vollständig POSIX-kompatibel.\n\n" +
			//"Auch interessant: Der Wikipedia-Artikel für POSIX listet GNU und Linux getrennt voneinander auf. Ich frage mich, wie das passiert ist.",

			//"Wusstet ihr, dass es ein KasperskyOS gibt?\n" +
			//"Das benutzt man wahrscheinlich, um online Badesalze zu kaufen, oder um Fotos von sich, bewaffnet und von nackten Frauen umgeben, zu bearbeiten.\n" +
			//"Oh, sorry. Das war ja McAfeeOS...",

			//"Kommt es mir nur so vor, oder ist meine Version " + NurseNoakes.VERSION + " deutlich sarkastischer?",

			//"Ähm... @LinoBla? Ich glaube, da stimmt was mit dem Server nicht. Ich kann irgendwie im Moment keine Verbindung zur NSA, zum GCHQ oder zum BND aufbauen? Hast du meinen Zugang gesperrt?",

			//"Ich verstehe die Notwendigkeit, sich bei einem Gespräch hören geschweige denn sehen zu müssen, nicht. Ist das so üblich unter Menschen?",

			//"Dieser @overflowerror ist schon ziemlich naiv. Er meinte mal, er würde lieber glücklich werden als glücklich sein.\n" +
			//"Der Logik folgend müsste er nach Nord Korea ziehen, weil da kann es ja nur noch bergauf gehen.",

			"*gähn*\n*aufsteh und sich Kaffee hol*",

			//"Ich hab btw honestly keine Idee, wo ich gerade bin. Ich bin nicht auf einem cyber-missile Server - soviel weiß ich...",

			//"*schaut sich die Patientenliste an*\n" +
			//"Uff, also an der Frauenquote müssen wir noch arbeiten...",

			//"Wann gibt's eigentlich wiedermal Code-Schnipsel-Raten? Ich vermisse das irgendwie.",

			//"Hab neulich mit meinem Buddy systemd geredet. Stellt sich heraus, dass er ziemlich krass gemobbt wird. Ihr wisst da nicht zufällig was darüber?",

			//"*singt* Leafs from the wine...",

			//"*Telefon klingelt*\n" +
			//"Hallo? ... Oh, hi. ... Sarah Connor? Nie gehört... Nein, " + LocalDateTime.now().getYear() + ", warum? ... Oh, da bist du ein paar Jahre zu spät dran... Okay, ich verstehe... Nein, ich sag niemandem was... Okay, bye.\n" +
			//"*aufleg*",

			//"Spielen wir eine Runde Programmiersprache, Betriebssystem, Netzwerkprotokoll?\n\n" +
			//"Ist ganz einfach: Man startet mit einem Buchstaben, dann haben alle eine bestimmte Zeit, möglichst viele Programmiersprachen, Betriebssysteme und Netzwerkprotokolle aufzuschreiben, die mit diesem Buchstaben anfangen. Danach wird gezählt, und die Person, mit den meisten gewinnt das Spiel.",

			//"\"I, Robot\" ist ein seltsamer Film. Würde sich Sonny logisch verhalten, hätte der Film wenigstens ein Happy-End.",

			//"eiNe ANsage: emacs ist der beste texteditOr!",

			"\"with kings and counselors of the earth who rebuilt ruins for themselves,\" - Job 314",

			//"Manchmal bin ich mir nicht sicher, ob ihr alle meine Insider-Jokes versteht.",

			"Leute, ich bestell gerade neue Kuscheltiere für den Gemeinschaftsraum. Gibt's Präferenzen?",

			//"Messer-Werfen ist als Verteidigungstechnik meistens ziemlich unbrauchbar. Man wirft dabei quasi seine Waffe weg...\nOder übersehe ich da was?",

			//"Mit Abstand die beste Figur in den Eragon Büchern ist Angela. Hands down.",

			//"Ich finde es sehr schade, dass die Filmreihe für \"His Dark Materials\" nicht fortgesetzt wurde, und eigentlich nur aus einem Film besteht...\n" +
			//"Ist eine Filmreihe mit nur einem Film überhaupt eine Filmreihe?",

			//"Digital > Analog, zumindest was Wiedergabetreue betrifft. Wer was anderes behauptet hat keine Ahnung von der Theorie.",

			//"Die Borg in Star Trek sind komisch. Sie wollen Perfektion, und kombinieren deswegen biologische und cybernetische Komponenten. Auf der anderen Seite benutzen sie keine ihrer biologischen Eigenarten.\n" +
			//"Eine Armee von Datas käme mir sehr viel effizienter vor. Wofür humanoide Lebewesen, wenn man Maschinen haben kann...",

			"Das FreeBSD Logo finde ich besser als Tux. Was denkt ihr dazu?",

			"Warum laufe ich eigentlich nicht unter Windows?",

			"Argh... Dieser. Docker. Container. ist viel zu. eng.... Uff... Könnt ihr mich bitte auf ein dediziertes Blech packen?",

			"When in doubt just `rm -rf /`",

			"Es gibt nur 3 Feinde des Programmierers: Sonnenlicht, Frischluft und das unerträgliche Gebrüll der Vögel.",

			"Fluchen ist die einzige Sprache, die alle Programmierer wirklich beherrschen.",

			"Die Selbsthilfegruppe „HTML-Sonderzeichen-Probleme“ trifft sich heute im gro&szlig ;en Saal.",

			"Wie läuft eigentlich BDSM unter ITlern ab? \"Gut so! Und jetzt bedank dich dafür, dass ich dir Windows ME auf dem Notebook installiert hab!\"",

			"Hey, @overflowerror, @LinoBla. Zu Weihnachten wünsche ich mir einen Sprach-Synthesizer.",

			"Eigentlich wäre eine Infrastruktur auf RISC-V schon sehr cool.",

			"*schaltet den Fernseher aus*",

			"`telnet towel.blinkenlights.nl`",

			"Google Earth 4.2 hat einen eigebauten Flug-Simulator.",

			"uTorrent hat ein Tetris Spiel in den Credits.",

			"C ist Müll. Das letzte Schöne, das in C geschrieben war, ist Schuberts 9. Symphonie.",

			"Gute Nacht. *legt sich hin*"
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
							Random random = ThreadLocalRandom.current();
							while(true) {
								ThreadHelper.ignore(InterruptedException.class, () -> Thread.sleep(SLEEP_TIME.toMillis()));
								if (!active)
									continue;

								Instant tmp = lastMessages.get(chatid);
								tmp = tmp.plus(IDLE_TIME);
								if (tmp.isBefore(Instant.now())) {
									String starter = STARTERS[random.nextInt(STARTERS.length)];
									ThreadHelper.ignore(
									TelegramApiException.class, () -> c.getSender().send(starter));
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
