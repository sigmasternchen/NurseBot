package asylum.nursebot.modules.dices;

import java.util.Random;

import asylum.nursebot.exceptions.NurseException;

public interface Distribution {
    String getName();
    String getParameters();
    void setParameter(Number... parameter) throws NurseException;
    void setRandom(Random random);
    double getExpectedValue();
    double getVariance();
    double generateValue();
}
