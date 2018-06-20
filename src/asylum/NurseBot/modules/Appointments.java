package asylum.NurseBot.modules;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import asylum.NurseBot.NurseNoakes;
import asylum.NurseBot.Sender;
import asylum.NurseBot.StringManager;
import asylum.NurseBot.commands.CommandInterpreter;
import asylum.NurseBot.semantics.SemanticsHandler;
import asylum.NurseBot.utils.Locality;
import asylum.NurseBot.utils.Module;
import asylum.NurseBot.utils.Permission;
import asylum.NurseBot.utils.Visibility;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import asylum.NurseBot.commands.CommandCategory;
import asylum.NurseBot.commands.CommandHandler;

public class Appointments implements Module {
	
	class Appointment {
		private User user;
		private String name;
		private long time;
		private Timer timer;
		
		public Appointment(User user, String name, long time, Timer timer) {
			super();
			this.user = user;
			this.name = name;
			this.time = time;
			this.timer = timer;
		}
	}
	
	private Collection<Appointment> appointments = new ConcurrentLinkedQueue<>();

	private StringManager stringManager;
	private CommandHandler commandHandler;
	
	private long getTimestamp(String format, boolean rel) {
		int Y = 1970;
		int M = 0;
		int D = 1;
		int h = 0;
		int m = 0;
		int s = 0;
		
		if (!rel) {
			Calendar calendar = Calendar.getInstance();
			Y = calendar.get(Calendar.YEAR);
			M = calendar.get(Calendar.MONTH);
			D = calendar.get(Calendar.DAY_OF_MONTH);
			h = calendar.get(Calendar.HOUR_OF_DAY);
			m = calendar.get(Calendar.MINUTE);
			s = calendar.get(Calendar.SECOND);
		}
		
		if (format.length() == 2) {
			m = Integer.parseInt(format);
		} else if (format.length() == 5) {
			m = Integer.parseInt(format.substring(0, 2));
			s = Integer.parseInt(format.substring(3));
		} else if (format.length() == 8) {
			h = Integer.parseInt(format.substring(0, 2));
			m = Integer.parseInt(format.substring(3, 5));
			s = Integer.parseInt(format.substring(6));
		} else if (format.length() == 20) {
			Y = Integer.parseInt(format.substring(0, 4));
			M = Integer.parseInt(format.substring(5, 7)) - 1;
			D = Integer.parseInt(format.substring(8, 10));
			h = Integer.parseInt(format.substring(11, 13));
			m = Integer.parseInt(format.substring(14, 16));
			s = Integer.parseInt(format.substring(17));
		} else
			throw new IllegalArgumentException();
		
		Calendar calendar = Calendar.getInstance();
		if (rel)
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		calendar.set(Calendar.YEAR, Y);
		calendar.set(Calendar.MONTH, M);
		calendar.set(Calendar.DAY_OF_MONTH, D);
		calendar.set(Calendar.HOUR_OF_DAY, h);
		calendar.set(Calendar.MINUTE, m);
		calendar.set(Calendar.SECOND, s);
		
		return calendar.getTimeInMillis();
	}
	
	private List<Appointment> getAllAppointments(User user) {
		List<Appointment> list = new LinkedList<>();
		for(Appointment appointment : appointments) {
			if (appointment.user.getId().equals(user.getId()))
				list.add(appointment);
		}
		return list;
	}
	
	private Appointment searchAppointment(User user, String name) {
		for(Appointment appointment : appointments) {
			if (appointment.user.getId().equals(user.getId()) && appointment.name.equals(name))
				return appointment;
		}
		return null;
	}
	
	private boolean removeAppointment(User user, String name) {
		Appointment appointment = searchAppointment(user, name);
		if (appointment == null)
			return false;
		appointment.timer.cancel();
		return appointments.remove(appointment);
	}
	
	private void setTimer(User user, String name, boolean relative, long time, Sender sender) {
		if (!relative) {
			time -= System.currentTimeMillis();
		}
		
		if (time < 0)
			throw new IllegalArgumentException();
		
		Timer timer = new Timer();
		
		appointments.add(new Appointment(user, name, time + System.currentTimeMillis(), timer));
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					sender.mention(user, ", du hast einen Termin: " + name);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
				
				removeAppointment(user, name);
			}
		}, time);
	}
	
	private CommandCategory category;
	
	public Appointments() {
		this.stringManager = new StringManager();
		category = new CommandCategory("Termine");
	}
	
	@Override
	public void init() {
		commandHandler.add(new CommandInterpreter(this)
				.setName("appointment")
				.setInfo("Termin anlegen")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.USER)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					String help = "Synopsis: /appointment \"Name des Termines\" relativ|absolut mm[:ss]|[YYYY-MM-DD-]hh:mm:ss";
					List<String> list = stringManager.tokenize(c.getParameter());
					
					if (list.size() != 3) {
						System.out.println("Wrong number of parameters.");
						c.getSender().reply(help, c.getMessage());
						return;
					}
					
					boolean abs = "absolut".equals(list.get(1).toLowerCase());
					boolean rel = "relativ".equals(list.get(1).toLowerCase());
					long time = 0;
					boolean fail = false;
					try {
						time = getTimestamp(list.get(2), rel);
					} catch (Exception e) {
						System.out.println("Wrong time format.");
						fail = true;
					}
					
					if (list.size() != 3 || !(abs ^ rel) || fail) {
						System.out.println("Wrong parameter");
						c.getSender().reply(help, c.getMessage());
						return;
					}
					
					if (searchAppointment(c.getMessage().getFrom(), list.get(0)) != null) {
						c.getSender().reply("Du hast bereits einen Termin mit diesem Namen.", c.getMessage());
					}
					
					try {
						setTimer(c.getMessage().getFrom(), list.get(0), rel, time, c.getSender());
					} catch (Exception e) {
						System.out.println("Problem setting timer.");
						c.getSender().reply(help, c.getMessage());
						return;
					}
						
					Date date = new Date(rel ? (time + System.currentTimeMillis()) : time);
					
					c.getSender().reply("Der Termin \"" + list.get(0) + "\" wurde für " + date.toString() + " eingetragen.", c.getMessage());
				}));
		commandHandler.add(new CommandInterpreter(this)
				.setName("appointmentinfo")
				.setInfo("zeigt Informationen zu einem Termin an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.USER)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					String help = "Synopsis: /appointmentinfo [\"Name des Termines\"]";
					List<String> list = stringManager.tokenize(c.getParameter());
					
					if (list.size() != 1) {
						StringBuilder builder = new StringBuilder();
						builder.append(help).append("\n\n");
						
						List<Appointment> appointments = getAllAppointments(c.getMessage().getFrom());
						if (appointments.isEmpty()) {
							builder.append("Ich habe zurzeit keine Termine für dich gespeichert.");
						} else {
							builder.append("Ich habe folgende Termine für dich:\n");
							for(Appointment appointment : appointments) {
								builder.append("- ");
								builder.append(appointment.name);
								builder.append(": ");
								builder.append(new Date(appointment.time).toString());
								builder.append("\n");
							}
						}
						
						c.getSender().reply(builder.toString(), c.getMessage());
						return;
					}
					
					Appointment appointment = searchAppointment(c.getMessage().getFrom(), list.get(0));
					if (appointment == null) {
						c.getSender().reply("Ich habe keinen Termin mit diesem Namen gefunden.", c.getMessage());
						return;
					}
					
					c.getSender().reply("Dieser Termin ist für " + (new Date(appointment.time).toString()) + " eingetragen.", c.getMessage());
					
				}));
		commandHandler.add(new CommandInterpreter(this)
				.setName("appointmentdelete")
				.setInfo("zeigt Informationen zu einem Termin an")
				.setVisibility(Visibility.PUBLIC)
				.setPermission(Permission.USER)
				.setLocality(Locality.GROUPS)
				.setCategory(category)
				.setAction(c -> {
					String help = "Synopsis: /appointmentdelete \"Name des Termines\"";
					List<String> list = stringManager.tokenize(c.getParameter());
					
					if (list.size() != 1) {
						c.getSender().reply(help, c.getMessage());
						return;
					}
					
					if (!removeAppointment(c.getMessage().getFrom(), list.get(0))) {
						c.getSender().reply("Ich habe keinen Termin mit diesem Namen gefunden.", c.getMessage());
						return;
					}
					
					c.getSender().reply("Ich habe den Termin \"" + list.get(0) + "\" gelöscht.", c.getMessage());
					
				}));
	}

	@Override
	public String getName() {
		return "Appointments";
	}

	@Override
	public boolean isCommandModule() {
		return true;
	}

	@Override
	public boolean isSemanticModule() {
		return false;
	}

	@Override
	public boolean needsNurse() {
		return false;
	}

	@Override
	public void setNurse(NurseNoakes nurse) {
		throw new NotImplementedException();
	}

	@Override
	public void setCommandHandler(CommandHandler commandHandler) {
		this.commandHandler = commandHandler;
	}

	@Override
	public void setSemanticsHandler(SemanticsHandler semanticHandler) {
		throw new NotImplementedException();
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
		for(Appointment appointment : appointments) {
			appointment.timer.cancel();
			appointments.remove(appointment);
		}
	}

	@Override
	public void shutdown() {
		deactivate();
	}
}
