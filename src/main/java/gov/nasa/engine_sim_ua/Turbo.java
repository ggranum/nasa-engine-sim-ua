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

    public static enum Unit {
        ENGLISH(0),
        METRIC(1),
        PERCENT_CHANGE(2);

        public final int value;

        Unit(int value) {
            this.value = value;
        }


    }

    int abflag;
    int entype;
    Unit units = Unit.ENGLISH;
    int inflag;
    int varflag;
    int pt2flag;
    int wtflag;
    int pltkeep;
    int iprint;
    int numeng;
    int gamopt;
    int arsched;
    int plttyp;
    int showcom;
    int athsched;
    int aexsched;
    int fueltype;
    int inptype;
    int siztype;
    // Flow variables
    double g0d;
    double g0;
    double rgas;
    double gama;
    double cpair;
    double tt4;
    double tt4d;
    double tt7;
    double tt7d;
    double t8;
    double p3p2d;
    double p3fp2d;
    double byprat;
    double throtl;
    double fsmach;
    double altd;
    double alt;
    double ts0;
    double ps0;
    double q0;
    double u0d;
    double u0;
    double a0;
    double rho0;
    double tsout;
    double psout;
    double epr;
    double etr;
    double npr;
    double snpr;
    double fnet;
    double fgros;
    double dram;
    double sfc;
    double fa;
    double eair;
    double uexit;
    double ues;
    double fnlb;
    double fglb;
    double drlb;
    double flflo;
    double fuelrat;
    double fntot;
    double eteng;
    double arth;
    double arthd;
    double arexit;
    double arexitd;
    double mexit;
    double pexit;
    double pfexit;
    double arthmn;
    double arthmx;
    double arexmn;
    double arexmx;
    double a8;
    double a8rat;
    double a8d;
    double afan;
    double a7;
    double m2;
    double isp;
    double ac;
    double a2;
    double a2d;
    double acore;
    double a4;
    double a4p;
    double fuelHeatValue;
    double fhvd;
    double mfr;
    double diameng;
    double altmin;
    double altmax;
    double u0min;
    double u0max;
    double thrmin;
    double thrmax;
    double pmax;
    double tmin;
    double tmax;
    double u0mt;
    double u0mr;
    double altmt;
    double altmr;
    double etmin;
    double etmax;
    double cprmin;
    double cprmax;
    double t4min;
    double t4max;
    double pt4max;
    double a2min;
    double a2max;
    double a8min;
    double a8max;
    double t7min;
    double t7max;
    double diamin;
    double diamax;
    double bypmin;
    double bypmax;
    double fprmin;
    double fprmax;
    double vmn1;
    double vmn2;
    double vmn3;
    double vmn4;
    double vmx1;
    double vmx2;
    double vmx3;
    double vmx4;
    double lconv1;
    double lconv2;
    double fconv;
    double pconv;
    double tconv;
    double tref;
    double mconv1;
    double mconv2;
    double econv;
    double econv2;
    double aconv;
    double bconv;
    double dconv;
    double flconv;
    // weight and materials
    double weight;
    double wtref;
    double wfref;
    int mcomp;
    int mfan;
    int mturbin;
    int burnerMaterial;
    int minlt;
    int mnozl;
    int mnozr;
    int ncflag;
    int ncomp;
    int ntflag;
    int nturb;
    int fireflag;
    double dcomp;
    double dfan;
    double dturbin;
    double dburner;
    double tcomp;
    double tfan;
    double tturbin;
    double tburner;
    double tinlt;
    double dinlt;
    double tnozl;
    double dnozl;
    double tnozr;
    double dnozr;
    double lcomp;
    double lburn;
    double lturb;
    double lnoz;   // component length
    // Station Variables
    double[] trat = new double[20];
    double[] tt   = new double[20];
    double[] pressureRatio = new double[20];
    double[] pt   = new double[20];
    double[] efficiency = new double[20];
    double[] gam  = new double[20];
    double[] cp   = new double[20];
    double[] s    = new double[20];
    double[] v    = new double[20];
    /* drawing geometry  */
    double xtrans;
    double ytrans;
    double factor;
    double scale;
    double xtranp;
    double ytranp;
    double factp;
    double xg[][]  = new double[13][45];
    double yg[][]  = new double[13][45];
    int sldloc;
    int sldplt;
    int ncompd;
    int antim;
    int ancol;
    //  Percentage  variables
    double u0ref;
    double altref;
    double thrref;
    double a2ref;
    double et2ref;
    double fpref;
    double et13ref;
    double bpref;
    double cpref;
    double et3ref;
    double et4ref;
    double et5ref;
    double t4ref;
    double p4ref;
    double t7ref;
    double et7ref;
    double a8ref;
    double fnref;
    double fuelref;
    double sfcref;
    double airref;
    double epref;
    double etref;
    double faref;
    // save design
    int ensav;
    int absav;
    int gamosav;
    int ptfsav;
    int arssav;
    int arthsav;
    int arxsav;
    int flsav;
    double fhsav;
    double t4sav;
    double t7sav;
    double p3sav;
    double p3fsav;
    double bysav;
    double acsav;
    double a2sav;
    double a4sav;
    double a4psav;
    double gamsav;
    double et2sav;
    double pr2sav;
    double pr4sav;
    double et3sav;
    double et4sav;
    double et5sav;
    double et7sav;
    double et13sav;
    double a8sav;
    double a8mxsav;
    double a8rtsav;
    double u0mxsav;
    double u0sav;
    double altsav;
    double artsav;
    double arexsav;
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
    double wtsav;
    double dinsav;
    double tinsav;
    double dfnsav;
    double tfnsav;
    double dcmsav;
    double tcmsav;
    double dbrsav;
    double tbrsav;
    double dtrsav;
    double ttrsav;
    double dnlsav;
    double tnlsav;
    double dnrsav;
    double tnrsav;
    // plotPanel variables
    int lines;
    int nord;
    int nabs;
    int npt;
    int ntikx;
    int ntiky;
    int counter;
    int ordkeep;
    int abskeep;
    double begx;
    double endx;
    double begy;
    double endy;
    double[] pltx = new double[26];
    double[] plty = new double[26];
    String labx;
    String laby;
    String labyu;
    String labxu;
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
    int parea;

    Solver solve;
    EngineModelViewCanvas view;
    CardLayout layin;
    CardLayout layout;
    FlightConditionsPanel flightConditionsPanel;
    InputPanel inputPanel;
    OutputPanel outputPanel;
    Image offscreenImg;
    Graphics offsGg;
    Image offImg1;
    Graphics off1Gg;

    Frame mainFrame;
    PrintStream prnt;
    OutputStream pfile;
    OutputStream sfilo;
    InputStream sfili;
    DataInputStream savin;
    DataOutputStream savout;

    public void init() {
        solve = new Solver(this);

        offscreenImg = createImage(this.getSize().width,
                                   this.getSize().height);
        offsGg = offscreenImg.getGraphics();
        offImg1 = createImage(this.getSize().width,
                              this.getSize().height);
        off1Gg = offImg1.getGraphics();

        setLayout(new GridLayout(2, 2, 5, 5));

        solve.setDefaults();

        view = new EngineModelViewCanvas(this);
        flightConditionsPanel = new FlightConditionsPanel(this);
        inputPanel = new InputPanel(this);
        outputPanel = new OutputPanel(this);

        add(view);
        add(flightConditionsPanel);
        add(inputPanel);
        add(outputPanel);

        mainFrame.setVisible(true);

        solve.compute();
        layout.show(outputPanel, "first");
        outputPanel.outputPlotCanvas.repaint();
        view.start();
    }

    public Insets getInsets() {
        return new Insets(10, 10, 10, 10);
    }

    public class InputPanel extends Panel {

        private final Turbo turbo;
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
            this.turbo = turbo;

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
            inletPanel.inletLeftPanel.getDi().setText(String.format("%.0f", turbo.dinlt * turbo.dconv));
            fanPanel.leftPanel.getDf().setText(String.format("%.0f", turbo.dfan * turbo.dconv));
            compressorPanel.compressorLeftPanel.getDc().setText(String.format("%.0f", turbo.dcomp * turbo.dconv));
            burnerPanel.burnerLeftPanel.getDb().setText(String.format("%.0f", turbo.dburner * turbo.dconv));
            turbinePanel.turbineLeftPanel.getDt().setText(String.format("%.0f", turbo.dturbin * turbo.dconv));
            nozzlePanel.nozzleLeftPanel.getDn().setText(String.format("%.0f", turbo.dnozl * turbo.dconv));
            ramjetNozzlePanel.ramjetNozzleLeftPanel.getDn().setText(String.format("%.0f", turbo.dnozr * turbo.dconv));
            inletPanel.inletLeftPanel.getTi().setText(String.format("%.0f", turbo.tinlt * turbo.tconv));
            fanPanel.leftPanel.getTf().setText(String.format("%.0f", turbo.tfan * turbo.tconv));
            compressorPanel.compressorLeftPanel.getTc().setText(String.format("%.0f", turbo.tcomp * turbo.tconv));
            burnerPanel.burnerLeftPanel.getTb().setText(String.format("%.0f", turbo.tburner * turbo.tconv));
            turbinePanel.turbineLeftPanel.getTt().setText(String.format("%.0f", turbo.tturbin * turbo.tconv));
            nozzlePanel.nozzleLeftPanel.getTn().setText(String.format("%.0f", turbo.tnozl * turbo.tconv));
            ramjetNozzlePanel.ramjetNozzleLeftPanel.getTn().setText(String.format("%.0f", turbo.tnozr * turbo.tconv));
        }
    }  // end Inppnl

    public static void main(String args[]) {
        Turbo turbo = new Turbo();

        turbo.mainFrame = new Frame("EngineSim Application Version 1.7a");
        turbo.mainFrame.add("Center", turbo);
        turbo.mainFrame.setSize(710, 450);
        turbo.mainFrame.setVisible(true);

        turbo.init();
        turbo.start();
    }
}
