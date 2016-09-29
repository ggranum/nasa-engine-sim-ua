package gov.nasa.engine_sim_ua;

public class AeroUtil {

    public static double getGamma(double temp, int opt) {
        // Utility to get gamma as a function of temp
        double gamma;
        double a = -7.6942651e-13;
        double b = 1.3764661e-08;
        double c = -7.8185709e-05;
        double d = 1.436914;
        if(opt == 0) {
            gamma = 1.4;
        } else {
            gamma = a * temp * temp * temp + b * temp * temp + c * temp + d;
        }
        return gamma;
    }

    public static double getCp(double temp, int opt)  {
        // Utility to get cp as a function of temp
        double Cp;
        double a = -4.4702130e-13;
        double b = -5.1286514e-10;
        double c = 2.8323331e-05;
        double d = 0.2245283;
        /* BTU/R */
        if(opt == 0) {
            Cp = .2399;
        } else {
            Cp = a * temp * temp * temp + b * temp * temp + c * temp + d;
        }
        return Cp;
    }

    public static double getMach(int sub, double correctedAirflow, double gamma) {
/* Utility to get the Mach number given the corrected airflow per area */
        double number;               /* iterate for mach number */
        double chokair;
        double deriv;
        double machn;
        double macho;
        double airo;
        double airn;
        int iter;

        chokair = getAirflowPerArea(1.0, gamma);
        if (correctedAirflow > chokair) {
            number = 1.0;
        } else {
            airo = .25618;                 /* initial guess */
            if (sub == 1) {
                macho = 1.0;   /* sonic */
            } else {
                if (sub == 2) {
                    macho = 1.703; /* supersonic */
                } else {
                    macho = .5;                /* subsonic */
                }
                iter = 1;
                machn = macho - .2 ;
                while (Math.abs(correctedAirflow - airo) > .0001 && iter < 20) {
                    airn = getAirflowPerArea(machn, gamma);
                    deriv = (airn-airo)/(machn-macho);
                    airo = airn;
                    macho = machn;
                    machn = macho + (correctedAirflow - airo)/deriv;
                    ++ iter;
                }
            }
            number = macho;
        }
        return number;
    }

    /**
     * analysis for rayleigh flow
     */
    public static double getRayleighLoss(double mach1, double ttrat, double tlow, int gamopt) {

        double number;
        double wc1;
        double wc2;
        double mgueso;
        double mach2;
        double g1;
        double gm1;
        double g2;
        double gm2;
        double fac1;
        double fac2;
        double fac3;
        double fac4;

        g1 = getGamma(tlow, gamopt);
        gm1 = g1 - 1.0;
        wc1 = getAirflowPerArea(mach1, g1);
        g2 = getGamma(tlow * ttrat, gamopt);
        gm2 = g2 - 1.0;
        number = .95;
                             /* iterate for mach downstream */
        mgueso = .4;                 /* initial guess */
        mach2 = .5;
        while (Math.abs(mach2 - mgueso) > .0001) {
            mgueso = mach2;
            fac1 = 1.0 + g1 * mach1 * mach1;
            fac2 = 1.0 + g2 * mach2 * mach2;
            fac3 = Math.pow(1.0 + .5 * gm1 * mach1 * mach1, g1 / gm1);
            fac4 = Math.pow(1.0 + .5 * gm2 * mach2 * mach2, g2 / gm2);
            number = fac1 * fac4 / fac2 / fac3;
            wc2 = wc1 * Math.sqrt(ttrat) / number;
            mach2 = getMach(0, wc2, g2);
        }
        return number;
    }

    /**
     * /* Utility to get the corrected airflow per area given the Mach number
     *
     * @param mach  Mach number
     * @param gamma Ratio of specific heats
     *
     * @return Airflow per unit area
     */
    public static double getAirflowPerArea(double mach, double gamma) {
        double number;
        double fac1;
        double fac2;
        fac2 = (gamma + 1.0) / (2.0 * (gamma - 1.0));
        fac1 = Math.pow(1.0 + .5 * (gamma - 1.0) * mach * mach, fac2);
        number = .50161 * Math.sqrt(gamma) * mach / fac1;
        return number;
    }
}
 
