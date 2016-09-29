package gov.nasa.engine_sim_ua;


import java.applet.Applet;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Panel;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * EngineSim - Design and Wind Tunnel Mode
 * University Version .. change limits
 * Application Version ... stands alone
 * reads and writes filesPanel
 * <p>
 * Program to perform turbomachinery design and analysis
 * a)   dry turbojet
 * b)   afterburning turbojet
 * c)   turbofan with separate nozzle
 * d)   ramjet
 * <p>
 * Version 1.7a   - 27 Oct 05
 * <p>
 * Written by Tom Benson
 * NASA Glenn Research Center
 * <p>
 * <p>
 * New Test  -
 * * sizePanel for the portal
 * * re-sizePanel graphics
 * * change outputs
 * add pexit, pfexit and M2 to output
 * * correct gross thrust calculation
 * * fix translation for viewer
 * <p>
 * <p>
 * Old Test  -
 * clean flightConditionsUpperPanel
 * <p>
 * TJB 27 Oct 05
 */
public class Turbo extends Applet {


    int abflag;
    int entype;
    int lunits;
    int inflag;
    int varflag;
    int pt2flag;
    int wtflag ;
    int pltkeep;
    int iprint;
    int numeng;
    int gamopt;
    int arsched;
    int plttyp;
    int showcom ;
    int athsched;
    int aexsched;
    int fueltype;
    int inptype;
    int siztype ;
    // Flow variables
    static double g0d;
    static double g0;
    static double rgas;
    static double gama;
    static double cpair ;
    static double tt4;
    static double tt4d;
    static double tt7;
    static double tt7d;
    static double t8;
    static double p3p2d;
    static double p3fp2d;
    static double byprat;
    static double throtl;
    static double fsmach;
    static double altd;
    static double alt;
    static double ts0;
    static double ps0;
    static double q0;
    static double u0d;
    static double u0;
    static double a0;
    static double rho0;
    static double tsout;
    static double psout;
    static double epr;
    static double etr;
    static double npr;
    static double snpr;
    static double fnet;
    static double fgros;
    static double dram;
    static double sfc;
    static double fa;
    static double eair;
    static double uexit;
    static double ues;
    static double fnlb;
    static double fglb;
    static double drlb;
    static double flflo;
    static double fuelrat;
    static double fntot;
    static double eteng;
    static double arth;
    static double arthd;
    static double arexit;
    static double arexitd ;
    static double mexit;
    static double pexit;
    static double pfexit ;
    static double arthmn;
    static double arthmx;
    static double arexmn;
    static double arexmx ;
    static double a8;
    static double a8rat;
    static double a8d;
    static double afan;
    static double a7;
    static double m2;
    static double isp;
    static double ac;
    static double a2;
    static double a2d;
    static double acore;
    static double a4;
    static double a4p;
    static double fhv;
    static double fhvd;
    static double mfr;
    static double diameng ;
    static double altmin;
    static double altmax;
    static double u0min;
    static double u0max;
    static double thrmin;
    static double thrmax;
    static double pmax;
    static double tmin;
    static double tmax;
    static double u0mt;
    static double u0mr;
    static double altmt;
    static double altmr;
    static double etmin;
    static double etmax;
    static double cprmin;
    static double cprmax;
    static double t4min;
    static double t4max;
    static double pt4max;
    static double a2min;
    static double a2max;
    static double a8min;
    static double a8max;
    static double t7min;
    static double t7max;
    static double diamin;
    static double diamax;
    static double bypmin;
    static double bypmax;
    static double fprmin;
    static double fprmax;
    static double vmn1;
    static double vmn2;
    static double vmn3;
    static double vmn4;
    static double vmx1;
    static double vmx2;
    static double vmx3;
    static double vmx4 ;
    static double lconv1;
    static double lconv2;
    static double fconv;
    static double pconv;
    static double tconv;
    static double tref;
    static double mconv1;
    static double mconv2;
    static double econv;
    static double econv2 ;
    static double aconv;
    static double bconv;
    static double dconv;
    static double flconv ;
    // weight and materials
    static double weight;
    static double wtref;
    static double wfref ;
    static int mcomp;
    static int mfan;
    static int mturbin;
    static int mburner;
    static int minlt;
    static int mnozl;
    static int mnozr ;
    static int ncflag;
    static int ncomp;
    static int ntflag;
    static int nturb;
    static int fireflag;
    static double dcomp;
    static double dfan;
    static double dturbin;
    static double dburner ;
    static double tcomp;
    static double tfan;
    static double tturbin;
    static double tburner ;
    static double tinlt;
    static double dinlt;
    static double tnozl;
    static double dnozl;
    static double tnozr;
    static double dnozr ;
    static double lcomp;
    static double lburn;
    static double lturb;
    static double lnoz;   // component length
    // Station Variables
    static double[] trat = new double[20] ;
    static double[] tt   = new double[20] ;
    static double[] prat = new double[20] ;
    static double[] pt   = new double[20] ;
    static double[] eta  = new double[20] ;
    static double[] gam  = new double[20] ;
    static double[] cp   = new double[20] ;
    static double[] s    = new double[20] ;
    static double[] v    = new double[20] ;
    /* drawing geometry  */
    static double xtrans;
    static double ytrans;
    static double factor;
    static double scale ;
    static double xtranp;
    static double ytranp;
    static double factp ;
    static double xg[][]  = new double[13][45] ;
    static double yg[][]  = new double[13][45] ;
    static int sldloc;
    static int sldplt;
    static int ncompd;
    static int antim;
    static int ancol ;
    //  Percentage  variables
    static double u0ref;
    static double altref;
    static double thrref;
    static double a2ref;
    static double et2ref;
    static double fpref;
    static double et13ref;
    static double bpref ;
    static double cpref;
    static double et3ref;
    static double et4ref;
    static double et5ref;
    static double t4ref;
    static double p4ref;
    static double t7ref;
    static double et7ref;
    static double a8ref;
    static double fnref;
    static double fuelref;
    static double sfcref;
    static double airref;
    static double epref;
    static double etref;
    static double faref ;
    // save design
    int ensav;
    int absav;
    int gamosav;
    int ptfsav;
    int arssav;
    int arthsav;
    int arxsav;
    int flsav ;
    static double fhsav;
    static double t4sav;
    static double t7sav;
    static double p3sav;
    static double p3fsav;
    static double bysav;
    static double acsav ;
    static double a2sav;
    static double a4sav;
    static double a4psav;
    static double gamsav;
    static double et2sav;
    static double pr2sav;
    static double pr4sav ;
    static double et3sav;
    static double et4sav;
    static double et5sav;
    static double et7sav;
    static double et13sav;
    static double a8sav;
    static double a8mxsav ;
    static double a8rtsav;
    static double u0mxsav;
    static double u0sav;
    static double altsav ;
    static double artsav;
    static double arexsav ;
    // save materials info
    int wtfsav;
    int minsav;
    int mfnsav;
    int mcmsav;
    int mbrsav;
    int mtrsav;
    int mnlsav;
    int mnrsav;
    int ncsav;
    int ntsav;
    static double wtsav;
    static double dinsav;
    static double tinsav;
    static double dfnsav;
    static double tfnsav;
    static double dcmsav;
    static double tcmsav;
    static double dbrsav;
    static double tbrsav;
    static double dtrsav;
    static double ttrsav;
    static double dnlsav;
    static double tnlsav;
    static double dnrsav;
    static double tnrsav;
    // plotPanel variables
    int lines;
    int nord;
    int nabs;
    int npt;
    int ntikx;
    int ntiky ;
    int counter ;
    int ordkeep;
    int abskeep ;
    static double begx;
    static double endx;
    static double begy;
    static double endy ;
    static double[] pltx = new double[26] ;
    static double[] plty = new double[26] ;
    static String labx;
    static String laby;
    static String labyu;
    static String labxu ;
    // print variables
    int pall;
    int pfs;
    int peng;
    int pth;
    int pprat;
    int ppres;
    int pvol;
    int ptrat;
    int pttot;
    int pentr;
    int pgam;
    int peta;
    int parea ;

    Solver solve ;
    EngineModelViewCanvas view ;
    CardLayout layin;
    CardLayout layout ;
    FlightConditionsPanel flightConditionsPanel;
    InputPanel inputPanel;
    OutputPanel outputPanel;
    Image offscreenImg ;
    Graphics offsGg ;
    Image offImg1 ;
    Graphics off1Gg ;

    static Frame f ;
    static PrintStream prnt ;
    static OutputStream pfile;
    static OutputStream sfilo ;
    static InputStream sfili;
    static DataInputStream savin ;
    static DataOutputStream savout;

    public void init() {
        solve = new Solver(this);

        offscreenImg = createImage(this.getSize().width,
                                   this.getSize().height) ;
        offsGg = offscreenImg.getGraphics() ;
        offImg1 = createImage(this.getSize().width,
                              this.getSize().height) ;
        off1Gg = offImg1.getGraphics() ;

        setLayout(new GridLayout(2, 2, 5, 5)) ;

        solve.setDefaults();

        view = new EngineModelViewCanvas(this);
        flightConditionsPanel = new FlightConditionsPanel(this) ;
        inputPanel = new InputPanel(this);
        outputPanel = new OutputPanel(this);

        add(view);
        add(flightConditionsPanel) ;
        add(inputPanel);
        add(outputPanel) ;

        Turbo.f.setVisible(true); ;

        solve.comPute();
        layout.show(outputPanel, "first");
        outputPanel.outputPlotCanvas.repaint();
        view.start();
    }

    public Insets getInsets() {
        return new Insets(10, 10, 10, 10) ;
    }

    public double getGama(double temp, int opt) {
        // Utility to get gamma as a function of temp
        double number;
        double a;
        double b;
        double c;
        double d ;
        a =  -7.6942651e-13;
        b =  1.3764661e-08;
        c =  -7.8185709e-05;
        d =  1.436914;
        if(opt == 0) {
            number = 1.4 ;
        } else {
            number = a*temp*temp*temp + b*temp*temp + c*temp +d ;
        }
        return(number) ;
    }

    public double getCp(double temp, int opt)  {
        // Utility to get cp as a function of temp
        double number;
        double a;
        double b;
        double c;
        double d ;
                              /* BTU/R */
        a =  -4.4702130e-13;
        b =  -5.1286514e-10;
        c =   2.8323331e-05;
        d =  0.2245283;
        if(opt == 0) {
            number = .2399 ;
        } else {
            number = a*temp*temp*temp + b*temp*temp + c*temp +d ;
        }
        return(number) ;
    }

    public double getMach (int sub, double corair, double gamma) {
/* Utility to get the Mach number given the corrected airflow per area */
        double number;               /* iterate for mach number */
        double chokair;
        double deriv;
        double machn;
        double macho;
        double airo;
        double airn;
        int iter ;

        chokair = getAir(1.0, gamma) ;
        if (corair > chokair) {
            number = 1.0 ;
            return (number) ;
        } else {
            airo = .25618 ;                 /* initial guess */
            if (sub == 1) {
                macho = 1.0;   /* sonic */
            } else {
                if (sub == 2) {
                    macho = 1.703; /* supersonic */
                } else {
                    macho = .5;                /* subsonic */
                }
                iter = 1 ;
                machn = macho - .2  ;
                while (Math.abs(corair - airo) > .0001 && iter < 20) {
                    airn =  getAir(machn,gamma) ;
                    deriv = (airn-airo)/(machn-macho) ;
                    airo = airn ;
                    macho = machn ;
                    machn = macho + (corair - airo)/deriv ;
                    ++ iter ;
                }
            }
            number = macho ;
        }
        return(number) ;
    }

    public double getRayleighLoss(double mach1, double ttrat, double tlow) {
                                         /* analysis for rayleigh flow */
        double number ;
        double wc1;
        double wc2;
        double mgueso;
        double mach2;
        double g1;
        double gm1;
        double g2;
        double gm2 ;
        double fac1;
        double fac2;
        double fac3;
        double fac4;

        g1 = getGama(tlow,gamopt);
        gm1 = g1 - 1.0 ;
        wc1 = getAir(mach1,g1);
        g2 = getGama(tlow*ttrat,gamopt);
        gm2 = g2 - 1.0 ;
        number = .95 ;
                             /* iterate for mach downstream */
        mgueso = .4 ;                 /* initial guess */
        mach2 = .5 ;
        while (Math.abs(mach2 - mgueso) > .0001) {
            mgueso = mach2 ;
            fac1 = 1.0 + g1 * mach1 * mach1 ;
            fac2 = 1.0 + g2 * mach2 * mach2 ;
            fac3 = Math.pow((1.0 + .5 * gm1 * mach1 * mach1),(g1/gm1)) ;
            fac4 = Math.pow((1.0 + .5 * gm2 * mach2 * mach2),(g2/gm2)) ;
            number = fac1 * fac4 / fac2 / fac3 ;
            wc2 = wc1 * Math.sqrt(ttrat) / number ;
            mach2 = getMach(0,wc2,g2) ;
        }
        return(number) ;
    }

    public double getAir(double mach, double gamma) {
/* Utility to get the corrected airflow per area given the Mach number */
        double number;
        double fac1;
        double fac2;
        fac2 = (gamma + 1.0) / (2.0 * (gamma - 1.0)) ;
        fac1 = Math.pow((1.0 + .5 * (gamma - 1.0) * mach * mach), fac2);
        number = .50161 * Math.sqrt(gamma) * mach / fac1 ;

        return(number) ;
    }

    public class InputPanel extends Panel {

        FlightPanel flightPanel;
        InletPanel inletPanel;
        SizePanel sizePanel;
        FanPanel fanPanel;
        CompressorPanel compressorPanel;
        BurnerPanel burnerPanel;
        TurbinePanel turbinePanel;
        NozzlePanel nozzlePanel;
        PlotPanel plotPanel;
        RamjetNozzlePanel ramjetNozzlePanel;
        LimitsPanel limitsPanel;
        FilesPanel filesPanel;
        FilePrintPanel filePrintPanel;

        InputPanel(Turbo turbo) {

            layin = new CardLayout();
            setLayout(layin);

            flightPanel = new FlightPanel(turbo);
            sizePanel = new SizePanel(turbo);
            inletPanel = new InletPanel(turbo);
            fanPanel = new FanPanel(turbo);
            compressorPanel = new CompressorPanel(turbo);
            burnerPanel = new BurnerPanel(turbo);
            turbinePanel = new TurbinePanel(turbo);
            nozzlePanel = new NozzlePanel(turbo);
            plotPanel = new PlotPanel(turbo);
            ramjetNozzlePanel = new RamjetNozzlePanel(turbo);
            limitsPanel = new LimitsPanel(turbo);
            filesPanel = new FilesPanel(turbo);
            filePrintPanel = new FilePrintPanel(turbo);

            add("first", flightPanel);
            add("second", sizePanel);
            add("third", inletPanel);
            add("fourth", fanPanel);
            add("fifth", compressorPanel);
            add("sixth", burnerPanel);
            add("seventh", turbinePanel);
            add("eighth", nozzlePanel);
            add("ninth", plotPanel);
            add("tenth", ramjetNozzlePanel);
            add("eleven", limitsPanel);
            add("twelve", filesPanel);
            add("thirteen", filePrintPanel);
        }

        public void fillBox() {
            inletPanel.inletLeftPanel.getDi().setText(String.format("%.0f", Turbo.dinlt * Turbo.dconv));
            fanPanel.leftPanel.getDf().setText(String.format("%.0f", Turbo.dfan * Turbo.dconv));
            compressorPanel.compressorLeftPanel.getDc().setText(String.format("%.0f", Turbo.dcomp * Turbo.dconv));
            burnerPanel.burnerLeftPanel.getDb().setText(String.format("%.0f", Turbo.dburner * Turbo.dconv));
            turbinePanel.turbineLeftPanel.getDt().setText(String.format("%.0f", Turbo.dturbin * Turbo.dconv));
            nozzlePanel.nozzleLeftPanel.getDn().setText(String.format("%.0f", Turbo.dnozl * Turbo.dconv));
            ramjetNozzlePanel.ramjetNozzleLeftPanel.getDn().setText(String.format("%.0f", Turbo.dnozr * Turbo.dconv));
            inletPanel.inletLeftPanel.getTi().setText(String.format("%.0f", Turbo.tinlt * Turbo.tconv));
            fanPanel.leftPanel.getTf().setText(String.format("%.0f", Turbo.tfan * Turbo.tconv));
            compressorPanel.compressorLeftPanel.getTc().setText(String.format("%.0f", Turbo.tcomp * Turbo.tconv));
            burnerPanel.burnerLeftPanel.getTb().setText(String.format("%.0f", Turbo.tburner * Turbo.tconv));
            turbinePanel.turbineLeftPanel.getTt().setText(String.format("%.0f", Turbo.tturbin * Turbo.tconv));
            nozzlePanel.nozzleLeftPanel.getTn().setText(String.format("%.0f", Turbo.tnozl * Turbo.tconv));
            ramjetNozzlePanel.ramjetNozzleLeftPanel.getTn().setText(String.format("%.0f", Turbo.tnozr * Turbo.tconv));
        }
    }  // end Inppnl

    public static void main(String args[]) {
        Turbo turbo = new Turbo();

        Turbo.f = new Frame("EngineSim Application Version 1.7a");
        Turbo.f.add("Center", turbo);
        Turbo.f.setSize(710, 450);
        Turbo.f.setVisible(true);

        turbo.init();
        turbo.start();
    }
}
