package asylum.nursebot.modules.dices;

import java.util.Random;

import asylum.nursebot.exceptions.NurseException;

public class DiscreteUniformDistribution implements Distribution {
    private int min;
    private int max;

    private Random random;

    public DiscreteUniformDistribution() {
    	this.min = 0;
    	this.max = 10;
	}

    public DiscreteUniformDistribution(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String getName() {
        return "Diskrete Gleichverteilung";
    }

    @Override
    public String getParameters() {
        return "a = " + min + ", b = " + max;
    }

	@Override
	public void setParameter(Number... parameter) throws NurseException {
		if (parameter.length != 2)
			throw new NurseException("Die diskrete Gleichverteilung braucht zwei Parameter.");

		this.min = parameter[0].intValue();
		this.max = parameter[1].intValue();

		if (min > max)
			throw new NurseException("Der erste Parameter muss kleiner sein.");
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
    	int tmp = (max - min + 1);
        return (tmp * tmp - 1) / 12.0;
    }

    @Override
    public double generateValue() {
		if (min == max)
			return min;

		return min + random.nextInt(max - min);
    }
}
