package asylum.nursebot.modules.dices;

import asylum.nursebot.exceptions.NurseException;

import java.util.Random;

public class ContinuousUniformDistribution implements Distribution {
    private double min;
    private double max;

    private Random random;

    public ContinuousUniformDistribution() {
    	this.min = 0;
    	this.max = 10;
	}

    public ContinuousUniformDistribution(double min, double max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String getName() {
        return "Kontinuierliche Gleichverteilung";
    }

    @Override
    public String getParameters() {
        return "a = " + min + ", b = " + max;
    }

	@Override
	public void setParameter(Number... parameter) throws NurseException {
		if (parameter.length != 2)
			throw new NurseException("Die kontinuierliche Gleichverteilung braucht zwei Parameter.");

		this.min = parameter[0].doubleValue();
		this.max = parameter[1].doubleValue();
	}

	@Override
    public void setRandom(Random random) {
        this.random = random;
    }

    @Override
    public double getExpectedValue() {
        return (min + max) / 2.0;
    }

    @Override
    public double getVariance() {
    	double tmp = (max - min);
        return tmp * tmp / 12.0;
    }

    @Override
    public double generateValue() {
		if (min == max)
			return min;

		/*
		 * This is not exact but close enough.
		 */
		return min + random.nextDouble() * (max - min);
    }
}
