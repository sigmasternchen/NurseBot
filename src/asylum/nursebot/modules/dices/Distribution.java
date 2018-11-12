package asylum.nursebot.modules.dices;

import asylum.nursebot.exceptions.NurseException;

import java.util.Random;

public interface Distribution {
    String getName();
    String getParameters();
    void setParameter(Number... parameter) throws NurseException;
    void setRandom(Random random);
    double getExpectedValue();
    double getVariance();
    double generateValue();
}
