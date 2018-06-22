package asylum.nursebot.modules;

import com.googlecode.charts4j.AxisLabels;
import com.googlecode.charts4j.AxisLabelsFactory;
import com.googlecode.charts4j.AxisStyle;
import com.googlecode.charts4j.AxisTextAlignment;
import com.googlecode.charts4j.Color;
import com.googlecode.charts4j.Data;
import com.googlecode.charts4j.Fills;
import com.googlecode.charts4j.GCharts;
import com.googlecode.charts4j.Line;
import com.googlecode.charts4j.LineChart;
import com.googlecode.charts4j.LineStyle;
import com.googlecode.charts4j.LinearGradientFill;
import com.googlecode.charts4j.Plots;
import com.googlecode.charts4j.Shape;

import asylum.nursebot.commands.CommandHandler;
import asylum.nursebot.objects.Locality;
import asylum.nursebot.objects.Module;
import asylum.nursebot.objects.Permission;
import asylum.nursebot.persistence.ModelManager;
import asylum.nursebot.persistence.modules.StatisticsMessage;
import asylum.nursebot.semantics.SemanticInterpreter;
import asylum.nursebot.semantics.SemanticsHandler;
import asylum.nursebot.semantics.WakeWord;
import asylum.nursebot.semantics.WakeWordType;
import asylum.nursebot.NurseNoakes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Statistics implements Module {

	private CommandHandler commandHandler;
	private SemanticsHandler semanticsHandler;

	@Override
	public String getName() {
		return "Statistics";
	}

	@Override
	public boolean isCommandModule() {
		return true;
	}

	@Override
	public boolean isSemanticModule() {
		return true;
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
		this.semanticsHandler = semanticHandler; 
	}

	public Statistics() {
		ModelManager.build(StatisticsMessage.class);
	}
	
	public String generateLineGraph(double data[], String title, String xLabels[]) {
		Color messageColor = Color.newColor("CA3D05");
		//Color wordColor = Color.newColor("6666FF");
		
		Line messageLine = Plots.newLine(Data.newData(data), messageColor);
		messageLine.setLineStyle(LineStyle.newLineStyle(3, 1, 0));
		messageLine.addShapeMarkers(Shape.DIAMOND, messageColor, 12);
		messageLine.addShapeMarkers(Shape.DIAMOND, Color.WHITE, 8);
		
		LineChart chart = GCharts.newLineChart(messageLine);
		
		chart.setSize(600, 450);
		chart.setTitle(title, Color.WHITE, 14);
		//chart.addHorizontalRangeMarker(40, 60, Color.newColor(Color.RED, 30));
        //chart.addVerticalRangeMarker(70, 90, Color.newColor(Color.GREEN, 30));
        chart.setGrid(25, 25, 3, 2);

        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.WHITE, 12, AxisTextAlignment.CENTER);
        AxisLabels xAxis = AxisLabelsFactory.newAxisLabels(xLabels);
        xAxis.setAxisStyle(axisStyle);
        
        chart.addXAxisLabels(xAxis);
        
        chart.setBackgroundFill(Fills.newSolidFill(Color.newColor("1F1D1D")));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("363433"), 100);
        fill.addColorAndOffset(Color.newColor("2E2B2A"), 0);
        chart.setAreaFill(fill);
        
        return chart.toURLString();
	}
	
	@Override
	public void init() {
		semanticsHandler.add(new SemanticInterpreter(this)
				.addWakeWord(new WakeWord(null, WakeWordType.TEXT_MESSAGE))
				.setLocality(Locality.GROUPS)
				.setPermission(Permission.ANY)
				.setAction(c -> {
					new StatisticsMessage()
						.setChatId(c.getMessage().getChatId())
						.setLength(c.getMessage().getText().split(" ").length)
						.saveIt();
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
