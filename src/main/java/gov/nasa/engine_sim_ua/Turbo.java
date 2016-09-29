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

    final double convdr = 3.14515926 / 180.;

    int abflag,entype,lunits,inflag,varflag,pt2flag,wtflag ;
    int abkeep,pltkeep,iprint,move ;
    int numeng,gamopt,arsched,plttyp,showcom ;
    int athsched,aexsched,fueltype,inptype,siztype ;
    // Flow variables
    static double g0d,g0,rgas,gama,cpair ;
    static double tt4,tt4d,tt7,tt7d,t8,p3p2d,p3fp2d,byprat,throtl;
    static double fsmach,altd,alt,ts0,ps0,q0,u0d,u0,a0,rho0,tsout,psout;
    static double epr,etr,npr,snpr,fnet,fgros,dram,sfc,fa,eair,uexit,ues;
    static double fnd,fnlb,fglb,drlb,flflo,fuelrat,fntot,eteng;
    static double arth,arthd,arexit,arexitd ;
    static double mexit,pexit,pfexit ;
    static double arthmn,arthmx,arexmn,arexmx ;
    static double a8,a8rat,a8d,afan,a7,m2,isp;
    static double ac,a2,a2d,acore,a4,a4p,fhv,fhvd,mfr,diameng ;
    static double altmin,altmax,u0min,u0max,thrmin,thrmax,pmax,tmin,tmax;
    static double u0mt,u0mr,altmt,altmr;
    static double etmin,etmax,cprmin,cprmax,t4min,t4max,pt4max;
    static double a2min,a2max,a8min,a8max,t7min,t7max,diamin,diamax;
    static double bypmin,bypmax,fprmin,fprmax;
    static double vmn1,vmn2,vmn3,vmn4,vmx1,vmx2,vmx3,vmx4 ;
    static double lconv1,lconv2,fconv,pconv,tconv,tref,mconv1,mconv2,econv,econv2 ;
    static double aconv,bconv,dconv,flconv ;
    // weight and materials
    static double weight,wtref,wfref ;
    static int mcomp,mfan,mturbin,mburner,minlt,mnozl,mnozr ;
    static int ncflag,ncomp,ntflag,nturb,fireflag;
    static double dcomp,dfan,dturbin,dburner ;
    static double tcomp,tfan,tturbin,tburner ;
    static double tinlt,dinlt,tnozl,dnozl,tnozr,dnozr ;
    static double lcomp,lburn,lturb,lnoz;   // component length
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
    static double xtrans,ytrans,factor,gains,scale ;
    static double xtranp,ytranp,factp ;
    static double xg[][]  = new double[13][45] ;
    static double yg[][]  = new double[13][45] ;
    static int sldloc,sldplt,ncompd;
    static int antim,ancol ;
    //  Percentage  variables
    static double u0ref,altref,thrref,a2ref,et2ref,fpref,et13ref,bpref ;
    static double cpref,et3ref,et4ref,et5ref,t4ref,p4ref,t7ref,et7ref,a8ref;
    static double fnref,fuelref,sfcref,airref,epref,etref,faref ;
    // save design
    int ensav,absav,gamosav,ptfsav,arssav,arthsav,arxsav,flsav ;
    static double fhsav,t4sav,t7sav,p3sav,p3fsav,bysav,acsav ;
    static double a2sav,a4sav,a4psav,gamsav,et2sav,pr2sav,pr4sav ;
    static double et3sav,et4sav,et5sav,et7sav,et13sav,a8sav,a8mxsav ;
    static double a8rtsav,u0mxsav,u0sav,altsav ;
    static double trsav,artsav,arexsav ;
    // save materials info
    int wtfsav,minsav,mfnsav,mcmsav,mbrsav,mtrsav,mnlsav,mnrsav,ncsav,ntsav;
    static double wtsav, dinsav, tinsav, dfnsav, tfnsav, dcmsav, tcmsav;
    static double dbrsav, tbrsav, dtrsav, ttrsav, dnlsav, tnlsav, dnrsav, tnrsav;
    // plotPanel variables
    int lines,nord,nabs,param,npt,ntikx,ntiky ;
    int counter ;
    int ordkeep,abskeep ;
    static double begx,endx,begy,endy ;
    static double[] pltx = new double[26] ;
    static double[] plty = new double[26] ;
    static String labx,laby,labyu,labxu ;
    // print variables
    int pall, pfs, peng, pth, pprat, ppres, pvol, ptrat,pttot,pentr,pgam,peta,parea ;

    Solver solve ;
    EngineModelViewCanvas view ;
    CardLayout layin,layout ;
    FlightConditionsPanel flightConditionsPanel;
    InputPanel inputPanel;
    OutputPanel outputPanel;
    Image offscreenImg ;
    Graphics offsGg ;
    Image offImg1 ;
    Graphics off1Gg ;

    static Frame f ;
    static PrintStream prnt ;
    static OutputStream pfile,sfilo ;
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

    public int filter0(double inumbr) {
        //  output only to .
        float number;
        int intermed;

        intermed = (int) (inumbr) ;
        number = (float)(intermed);
        return intermed;
    }

    public float filter1(double inumbr) {
        //  output only to .1
        float number;
        int intermed ;

        intermed = (int) (inumbr * 10.) ;
        number = (float) (intermed / 10. );
        return number ;
    }

    public float filter3(double inumbr) {
        //  output only to .001
        float number ;
        int intermed ;

        intermed = (int) (inumbr * 1000.) ;
        number = (float) (intermed / 1000. );
        return number ;
    }

    public float filter5(double inumbr) {
        //  output only to .00001
        float number ;
        int intermed ;

        intermed = (int) (inumbr * 100000.) ;
        number = (float) (intermed / 100000. );
        return number ;
    }

    public double getGama(double temp, int opt) {
        // Utility to get gamma as a function of temp
        double number,a,b,c,d ;
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
        double number,a,b,c,d ;
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
        double number,chokair;              /* iterate for mach number */
        double deriv,machn,macho,airo,airn;
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
        double wc1,wc2,mgueso,mach2,g1,gm1,g2,gm2 ;
        double fac1,fac2,fac3,fac4;

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
        double number,fac1,fac2;
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
            inletPanel.inletLeftPanel.getDi().setText(String.valueOf(filter0(Turbo.dinlt * Turbo.dconv)));
            fanPanel.leftPanel.getDf().setText(String.valueOf(filter0(Turbo.dfan * Turbo.dconv)));
            compressorPanel.compressorLeftPanel.getDc().setText(String.valueOf(filter0(Turbo.dcomp * Turbo.dconv)));
            burnerPanel.burnerLeftPanel.getDb().setText(String.valueOf(filter0(Turbo.dburner * Turbo.dconv)));
            turbinePanel.turbineLeftPanel.getDt().setText(String.valueOf(filter0(Turbo.dturbin * Turbo.dconv)));
            nozzlePanel.nozzleLeftPanel.getDn().setText(String.valueOf(filter0(Turbo.dnozl * Turbo.dconv)));
            ramjetNozzlePanel.ramjetNozzleLeftPanel.getDn().setText(String.valueOf(filter0(Turbo.dnozr * Turbo.dconv)));
            inletPanel.inletLeftPanel.getTi().setText(String.valueOf(filter0(Turbo.tinlt * Turbo.tconv)));
            fanPanel.leftPanel.getTf().setText(String.valueOf(filter0(Turbo.tfan * Turbo.tconv)));
            compressorPanel.compressorLeftPanel.getTc().setText(String.valueOf(filter0(Turbo.tcomp * Turbo.tconv)));
            burnerPanel.burnerLeftPanel.getTb().setText(String.valueOf(filter0(Turbo.tburner * Turbo.tconv)));
            turbinePanel.turbineLeftPanel.getTt().setText(String.valueOf(filter0(Turbo.tturbin * Turbo.tconv)));
            nozzlePanel.nozzleLeftPanel.getTn().setText(String.valueOf(filter0(Turbo.tnozl * Turbo.tconv)));
            ramjetNozzlePanel.ramjetNozzleLeftPanel.getTn().setText(String.valueOf(filter0(Turbo.tnozr * Turbo.tconv)));
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
