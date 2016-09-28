package gov.nasa.engine_sim_ua;

/*
                 EngineSim - Design and Wind Tunnel Mode
                    University Version .. change limits
                  Application Version ... stands alone
                                          reads and writes files

     Program to perform turbomachinery design and analysis
                a)   dry turbojet
                b)   afterburning turbojet
                c)   turbofan with separate nozzle
                d)   ramjet

                     Version 1.7a   - 27 Oct 05

                         Written by Tom Benson
                       NASA Glenn Research Center


      New Test  -
                 * size for the portal
                 * re-size graphics
                 * change outputs
                     add pexit, pfexit and M2 to output
                 * correct gross thrust calculation
                 * fix translation for viewer


      Old Test  -
                   clean up

                                                     TJB 27 Oct 05
*/

import java.awt.*;
import java.lang.Math ;
import java.io.* ;

public class Turbo extends java.applet.Applet {

   final double convdr = 3.14515926/180.;
 
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
                 // plot variables
   int lines,nord,nabs,param,npt,ntikx,ntiky ;
   int counter ;
   int ordkeep,abskeep ;
   static double begx,endx,begy,endy ;
   static double[] pltx = new double[26] ;
   static double[] plty = new double[26] ;
   static String labx,laby,labyu,labxu ;
                 // print variables
   int pall,pfs,peng,pth,pprat,ppres,pvol,ptrat,pttot,pentr,pgam,peta,parea ;

   Solver solve ;
   Viewer view ;
   CardLayout layin,layout ;
   Con con ;
   In in ;
   Out out ;
   Image offscreenImg ;
   Graphics offsGg ;
   Image offImg1 ;
   Graphics off1Gg ;

   static Frame f ;
   static PrintStream prnt ;
   static OutputStream pfile,sfilo ;
   static InputStream sfili ;
   static DataInputStream savin ;
   static DataOutputStream savout ;

   public void init() {
     int i;
     solve = new Solver() ;

     offscreenImg = createImage(this.size().width,
                      this.size().height) ;
     offsGg = offscreenImg.getGraphics() ;
     offImg1 = createImage(this.size().width,
                      this.size().height) ;
     off1Gg = offImg1.getGraphics() ;

     setLayout(new GridLayout(2,2,5,5)) ;

     solve.setDefaults () ;
 
     view   = new Viewer(this) ;
     con = new Con(this) ;
     in = new In(this) ;
     out = new Out(this) ;

     add(view) ;
     add(con) ;
     add(in) ;
     add(out) ;

       Turbo.f.show() ;

     solve.comPute() ;
     layout.show(out, "first")  ;
     out.plot.repaint() ;
     view.start() ;
  }
 
  public Insets insets() {
     return new Insets(10,10,10,10) ;
  }

  public int filter0(double inumbr) {
     //  output only to .
     float number ;
     int intermed ;
    
     intermed = (int) (inumbr) ;
     number = (float) (intermed);
     return intermed ;
  }
 
  public float filter1(double inumbr) {
      //  output only to .1
      float number ;
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
      }
      else {
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
      }
      else {
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
      }
      else {
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
    fac2 = (gamma+1.0)/(2.0*(gamma-1.0)) ;
    fac1 = Math.pow((1.0+.5*(gamma-1.0)*mach*mach),fac2);
    number =  .50161*Math.sqrt(gamma) * mach/ fac1 ;

    return(number) ;
  }

  public class Solver {

     Solver () {
     }

     public void comPute() {

        numeng = 1 ;
         Turbo.fireflag = 0 ;

        getFreeStream ();

        getThermo() ;

        if (inflag == 0) {
            getGeo(); /* determine engine size and geometry */
        }
        if (inflag == 1) {
           if (entype < 3) {
               Turbo.a8 = Turbo.a8d * Math.sqrt(Turbo.trat[7]) / Turbo.prat[7];
           }
        }

        view.getDrawGeo() ;

        getPerform() ;

        out.box.loadOut() ;
        out.vars.loadOut() ;
        in.fillBox() ;

        if (plttyp >= 3 && plttyp <= 7)  {
            out.plot.loadPlot () ;
            out.plot.repaint() ;
        }

        view.repaint() ;

        if (inflag == 0) {
            myDesign();
        }
     }

     public void setDefaults() {
        int i ;

        move = 0 ;
        inptype = 0 ;
        siztype = 0 ;
        lunits = 0 ;
         Turbo.lconv1 = 1.0 ;
         Turbo.lconv2 = 1.0 ;
         Turbo.fconv = 1.0 ;
         Turbo.mconv1 = 1.0 ;
         Turbo.pconv = 1.0 ;
         Turbo.econv = 1.0 ;
         Turbo.aconv = 1.0 ;
         Turbo.bconv = 1.0 ;
         Turbo.mconv2 = 1.0 ;
         Turbo.dconv = 1.0 ;
         Turbo.flconv = 1.0 ;
         Turbo.econv2 = 1.0 ;
         Turbo.tconv = 1.0 ;
         Turbo.tref = 459.6;
         Turbo.g0 = Turbo.g0d = 32.2 ;

        counter = 0 ;
        showcom = 0 ;
        plttyp  = 0 ;
        pltkeep = 0 ;
        entype  = 0 ;
        inflag  = 0 ;
        varflag = 0 ;
        pt2flag = 0 ;
        wtflag  = 0 ;
         Turbo.fireflag = 0 ;
         Turbo.gama = 1.4 ;
        gamopt = 1 ;
         Turbo.u0d = 0.0 ;
         Turbo.altd = 0.0 ;
         Turbo.throtl = 100. ;

        for (i=0; i<=19; ++i) {
            Turbo.trat[i] = 1.0 ;
            Turbo.tt[i]   = 518.6 ;
            Turbo.prat[i] = 1.0 ;
            Turbo.pt[i]   = 14.7 ;
            Turbo.eta[i]  = 1.0 ;
        }
         Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2500. ;
         Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 2500. ;
         Turbo.prat[3] = Turbo.p3p2d = 8.0 ;
         Turbo.prat[13] = Turbo.p3fp2d = 2.0 ;
         Turbo.byprat = 1.0 ;
        abflag = 0 ;

        fueltype = 0 ;
         Turbo.fhvd = Turbo.fhv = 18600. ;
         Turbo.a2d = Turbo.a2 = Turbo.acore = 2.0 ;
         Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
         Turbo.ac = .9 * Turbo.a2;
         Turbo.a8rat = .35 ;
         Turbo.a8 = .7 ;
         Turbo.a8d = .40 ;
        arsched = 0 ;
         Turbo.afan = 2.0 ;
         Turbo.a4 = .418 ;

        athsched = 1 ;
        aexsched = 1 ;
         Turbo.arthmn = 0.1;
         Turbo.arthmx = 1.5 ;
         Turbo.arexmn = 1.0;
         Turbo.arexmx = 10.0 ;
         Turbo.arthd = Turbo.arth = .4 ;
         Turbo.arexit = Turbo.arexitd = 3.0 ;

         Turbo.u0mt = 1500.;
         Turbo.u0mr = 4500. ;
         Turbo.altmt = 60000.;
         Turbo.altmr = 100000. ;

         Turbo.u0min = 0.0 ;
         Turbo.u0max = Turbo.u0mt;
         Turbo.altmin = 0.0 ;
         Turbo.altmax = Turbo.altmt;
         Turbo.thrmin = 30;
         Turbo.thrmax = 100 ;
         Turbo.etmin = .5;
         Turbo.etmax = 1.0 ;
         Turbo.cprmin = 1.0;
         Turbo.cprmax = 50.0 ;
         Turbo.bypmin = 0.0;
         Turbo.bypmax = 10.0 ;
         Turbo.fprmin = 1.0;
         Turbo.fprmax = 2.0 ;
         Turbo.t4min = 1000.0;
         Turbo.t4max = 3200.0 ;
         Turbo.t7min = 1000.0;
         Turbo.t7max = 4000.0 ;
         Turbo.a8min = 0.1;
         Turbo.a8max = 0.4 ;
         Turbo.a2min = .001;
         Turbo.a2max = 50.;
         Turbo.pt4max = 1.0;
         Turbo.diamin = Math.sqrt(4.0 * Turbo.a2min / 3.14159);
         Turbo.diamax = Math.sqrt(4.0 * Turbo.a2max / 3.14159);
         Turbo.pmax = 20.0;
         Turbo.tmin = -100.0 + Turbo.tref;
         Turbo.tmax = 100.0 + Turbo.tref;
         Turbo.vmn1 = Turbo.u0min;
         Turbo.vmx1 = Turbo.u0max;
         Turbo.vmn2 = Turbo.altmin;
         Turbo.vmx2 = Turbo.altmax;
         Turbo.vmn3 = Turbo.thrmin;
         Turbo.vmx3 = Turbo.thrmax;
         Turbo.vmn4 = Turbo.arexmn;
         Turbo.vmx4 = Turbo.arexmx;

         Turbo.xtrans = 125.0 ;
         Turbo.ytrans = 115.0 ;
         Turbo.factor = 35. ;
         Turbo.sldloc = 75 ;

         Turbo.xtranp = 80.0 ;
         Turbo.ytranp = 180.0 ;
         Turbo.factp = 27. ;
         Turbo.sldplt = 130 ;

         Turbo.weight = 1000. ;
         Turbo.minlt = 1;
         Turbo.dinlt = 170.2 ;
         Turbo.tinlt = 900. ;
         Turbo.mfan = 2;
         Turbo.dfan = 293.02 ;
         Turbo.tfan = 1500. ;
         Turbo.mcomp = 2;
         Turbo.dcomp = 293.02 ;
         Turbo.tcomp = 1500. ;
         Turbo.mburner = 4 ;
         Turbo.dburner = 515.2 ;
         Turbo.tburner = 2500. ;
         Turbo.mturbin = 4 ;
         Turbo.dturbin = 515.2 ;
         Turbo.tturbin = 2500. ;
         Turbo.mnozl = 3;
         Turbo.dnozl = 515.2 ;
         Turbo.tnozl = 2500. ;
         Turbo.mnozr = 5;
         Turbo.dnozr = 515.2 ;
         Turbo.tnozr = 4500. ;
         Turbo.ncflag = 0 ;
         Turbo.ntflag = 0 ;

        iprint = 0 ;
        pall = 0; pfs = 1; peng = 1; pth = 1; ptrat = 0; ppres = 0;
        pvol = 0; ptrat = 0; pttot = 0; pentr = 0 ; pgam = 0;
        peta = 0 ; parea = 0;

        return ;
     }

     public void myDesign() {

        ensav = entype ;
        absav = abflag ;
        flsav = fueltype ;
         Turbo.fhsav = Turbo.fhvd / Turbo.flconv;
         Turbo.t4sav = Turbo.tt4d / Turbo.tconv;
         Turbo.t7sav = Turbo.tt7d / Turbo.tconv;
         Turbo.p3sav = Turbo.p3p2d;
         Turbo.p3fsav = Turbo.p3fp2d;
         Turbo.bysav = Turbo.byprat;
         Turbo.acsav = Turbo.acore;
         Turbo.a2sav = Turbo.a2d / Turbo.aconv;
         Turbo.a4sav = Turbo.a4;
         Turbo.a4psav = Turbo.a4p;
         Turbo.gamsav = Turbo.gama;
        gamosav = gamopt;
        ptfsav = pt2flag ;
         Turbo.et2sav = Turbo.eta[2] ;
         Turbo.pr2sav = Turbo.prat[2];
         Turbo.pr4sav = Turbo.prat[4];
         Turbo.et3sav = Turbo.eta[3];
         Turbo.et4sav = Turbo.eta[4];
         Turbo.et5sav = Turbo.eta[5];
         Turbo.et7sav = Turbo.eta[7];
         Turbo.et13sav = Turbo.eta[13];
         Turbo.a8sav = Turbo.a8d / Turbo.aconv;
         Turbo.a8mxsav = Turbo.a8max / Turbo.aconv;
         Turbo.a8rtsav = Turbo.a8rat;

         Turbo.u0mxsav = Turbo.u0max / Turbo.lconv2;
         Turbo.u0sav = Turbo.u0d / Turbo.lconv2;
         Turbo.altsav = Turbo.altd / Turbo.lconv1;
        arssav = arsched ;

        wtfsav = wtflag;
         Turbo.wtsav = Turbo.weight;
        minsav = Turbo.minlt;
         Turbo.dinsav = Turbo.dinlt;
         Turbo.tinsav = Turbo.tinlt;
        mfnsav = Turbo.mfan;
         Turbo.dfnsav = Turbo.dfan;
         Turbo.tfnsav = Turbo.tfan;
        mcmsav = Turbo.mcomp;
         Turbo.dcmsav = Turbo.dcomp;
         Turbo.tcmsav = Turbo.tcomp;
        mbrsav = Turbo.mburner;
         Turbo.dbrsav = Turbo.dburner;
         Turbo.tbrsav = Turbo.tburner;
        mtrsav = Turbo.mturbin;
         Turbo.dtrsav = Turbo.dturbin;
         Turbo.ttrsav = Turbo.tturbin;
        mnlsav = Turbo.mnozl;
         Turbo.dnlsav = Turbo.dnozl;
         Turbo.tnlsav = Turbo.tnozl;
        mnrsav = Turbo.mnozr;
         Turbo.dnrsav = Turbo.dnozr;
         Turbo.tnrsav = Turbo.tnozr;
        ncsav = Turbo.ncflag;ntsav = Turbo.ntflag;

        if (entype == 3) {
           arthsav = athsched ;
           arxsav = aexsched ;
            Turbo.artsav = Turbo.arthd;
            Turbo.arexsav = Turbo.arexitd;
        }

        return ;
     }

     public void loadMine() {

        entype = ensav ;
        abflag = absav ;
        fueltype = flsav ;
         Turbo.fhvd = Turbo.fhv = Turbo.fhsav;
         Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = Turbo.t4sav;
         Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = Turbo.t7sav;
         Turbo.prat[3] = Turbo.p3p2d = Turbo.p3sav;
         Turbo.prat[13] = Turbo.p3fp2d = Turbo.p3fsav;
         Turbo.byprat = Turbo.bysav;
         Turbo.acore = Turbo.acsav;
         Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
         Turbo.a2d = Turbo.a2 = Turbo.a2sav;
         Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
         Turbo.a4 = Turbo.a4sav;
         Turbo.a4p = Turbo.a4psav;
         Turbo.ac = .9 * Turbo.a2;
         Turbo.gama = Turbo.gamsav;
        gamopt = gamosav ;
        pt2flag = ptfsav ;
         Turbo.eta[2] = Turbo.et2sav;
         Turbo.prat[2] = Turbo.pr2sav;
         Turbo.prat[4] = Turbo.pr4sav;
         Turbo.eta[3] = Turbo.et3sav;
         Turbo.eta[4] = Turbo.et4sav;
         Turbo.eta[5] = Turbo.et5sav;
         Turbo.eta[7] = Turbo.et7sav;
         Turbo.eta[13] = Turbo.et13sav;
         Turbo.a8d = Turbo.a8sav;
         Turbo.a8max = Turbo.a8mxsav;
         Turbo.a8rat = Turbo.a8rtsav;

         Turbo.u0max = Turbo.u0mxsav;
         Turbo.u0d = Turbo.u0sav;
         Turbo.altd = Turbo.altsav;
        arsched = arssav ;

        wtflag = wtfsav;
         Turbo.weight = Turbo.wtsav;
         Turbo.minlt = minsav;
         Turbo.dinlt = Turbo.dinsav;
         Turbo.tinlt = Turbo.tinsav;
         Turbo.mfan = mfnsav;
         Turbo.dfan = Turbo.dfnsav;
         Turbo.tfan = Turbo.tfnsav;
         Turbo.mcomp = mcmsav;
         Turbo.dcomp = Turbo.dcmsav;
         Turbo.tcomp = Turbo.tcmsav;
         Turbo.mburner = mbrsav;
         Turbo.dburner = Turbo.dbrsav;
         Turbo.tburner = Turbo.tbrsav;
         Turbo.mturbin = mtrsav;
         Turbo.dturbin = Turbo.dtrsav;
         Turbo.tturbin = Turbo.ttrsav;
         Turbo.mnozl = mnlsav;
         Turbo.dnozl = Turbo.dnlsav;
         Turbo.tnozl = Turbo.tnlsav;
         Turbo.mnozr = mnrsav;
         Turbo.dnozr = Turbo.dnrsav;
         Turbo.tnozr = Turbo.tnrsav;
         Turbo.ncflag = ncsav;
         Turbo.ntflag = ntsav;

        if (entype == 3) {
           athsched = arthsav  ;
           aexsched = arxsav ;
            Turbo.arthd = Turbo.artsav;
            Turbo.arexitd = Turbo.arexsav;
        }

        con.setPanl() ;
        return ;
     }

     public void loadCF6() {

        entype = 2 ;
        abflag = 0 ;
        fueltype = 0;
         Turbo.fhvd = Turbo.fhv = 18600. ;
         Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2500. ;
         Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 2500. ;
         Turbo.prat[3] = Turbo.p3p2d = 21.86 ;
         Turbo.prat[13] = Turbo.p3fp2d = 1.745 ;
         Turbo.byprat = 3.3 ;
         Turbo.acore = 6.965 ;
         Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
         Turbo.a2d = Turbo.a2 = Turbo.afan;
         Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
         Turbo.a4 = .290 ;
         Turbo.a4p = 1.131 ;
         Turbo.ac = .9 * Turbo.a2;
         Turbo.gama = 1.4 ;
        gamopt = 1 ;
        pt2flag = 0 ;
         Turbo.eta[2] = 1.0 ;
         Turbo.prat[2] = 1.0 ;
         Turbo.prat[4] = 1.0 ;
         Turbo.eta[3] = .959 ;
         Turbo.eta[4] = .984 ;
         Turbo.eta[5] = .982 ;
         Turbo.eta[7] = 1.0 ;
         Turbo.eta[13] = 1.0 ;
         Turbo.a8d = 2.436 ;
         Turbo.a8max = .35 ;
         Turbo.a8rat = .35 ;

         Turbo.u0max = Turbo.u0mt;
         Turbo.u0d = 0.0 ;
         Turbo.altmax = Turbo.altmt;
         Turbo.altd = 0.0 ;
        arsched = 0 ;

        wtflag = 0;
         Turbo.weight = 8229.;
         Turbo.minlt = 1;
         Turbo.dinlt = 170.;
         Turbo.tinlt = 900.;
         Turbo.mfan = 2;
         Turbo.dfan = 293.;
         Turbo.tfan = 1500.;
         Turbo.mcomp = 0;
         Turbo.dcomp = 293.;
         Turbo.tcomp = 1600.;
         Turbo.mburner = 4;
         Turbo.dburner = 515.;
         Turbo.tburner = 2500.;
         Turbo.mturbin = 4;
         Turbo.dturbin = 515.;
         Turbo.tturbin = 2500.;
         Turbo.mnozl = 3;
         Turbo.dnozl = 515.;
         Turbo.tnozl = 2500.;
         Turbo.ncflag = 0;
         Turbo.ntflag = 0;

        con.setPanl() ;
        return ;
     }

     public void loadJ85() {

        entype = 0 ;
        abflag = 0 ;
        fueltype = 0;
         Turbo.fhvd = Turbo.fhv = 18600. ;
         Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2260. ;
         Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 4000. ;
         Turbo.prat[3] = Turbo.p3p2d = 8.3 ;
         Turbo.prat[13] = Turbo.p3fp2d = 1.0 ;
         Turbo.byprat = 0.0 ;
         Turbo.a2d = Turbo.a2 = Turbo.acore = 1.753 ;
         Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
         Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
         Turbo.a4 = .323 ;
         Turbo.a4p = .818 ;
         Turbo.ac = .9 * Turbo.a2;
         Turbo.gama = 1.4 ;
        gamopt = 1 ;
        pt2flag = 0 ;
         Turbo.eta[2] = 1.0 ;
         Turbo.prat[2] = 1.0 ;
         Turbo.prat[4] = .85 ;
         Turbo.eta[3] = .822 ;
         Turbo.eta[4] = .982 ;
         Turbo.eta[5] = .882;
         Turbo.eta[7] = .978 ;
         Turbo.eta[13] = 1.0 ;
         Turbo.a8d = .818 ;
         Turbo.a8max = .467 ;
         Turbo.a8rat = .467 ;

         Turbo.u0max = Turbo.u0mt;
         Turbo.u0d = 0.0 ;
         Turbo.altmax = Turbo.altmt;
         Turbo.altd = 0.0 ;
        arsched = 1 ;

        wtflag = 0;
         Turbo.weight = 561.;
         Turbo.minlt = 1;
         Turbo.dinlt = 170.;
         Turbo.tinlt = 900.;
         Turbo.mfan = 2;
         Turbo.dfan = 293.;
         Turbo.tfan = 1500.;
         Turbo.mcomp = 2;
         Turbo.dcomp = 293.;
         Turbo.tcomp = 1500.;
         Turbo.mburner = 4;
         Turbo.dburner = 515.;
         Turbo.tburner = 2500.;
         Turbo.mturbin = 4;
         Turbo.dturbin = 515.;
         Turbo.tturbin = 2500.;
         Turbo.mnozl = 5;
         Turbo.dnozl = 600.;
         Turbo.tnozl = 4100.;
         Turbo.ncflag = 0;
         Turbo.ntflag = 0;

        con.setPanl() ;
        return ;
     }

     public void loadF100() {

        entype = 1 ;
        abflag = 1 ;
        fueltype = 0;
         Turbo.fhvd = Turbo.fhv = 18600. ;
         Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2499. ;
         Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 3905. ;
         Turbo.prat[3] = Turbo.p3p2d = 20.04 ;
         Turbo.prat[13] = Turbo.p3fp2d = 1.745 ;
         Turbo.byprat = 0.0 ;
         Turbo.a2d = Turbo.a2 = Turbo.acore = 6.00 ;
         Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
         Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
         Turbo.a4 = .472 ;
         Turbo.a4p = 1.524 ;
         Turbo.ac = .9 * Turbo.a2;
         Turbo.gama = 1.4 ;
        gamopt = 1 ;
        pt2flag = 0 ;
         Turbo.eta[2] = 1.0 ;
         Turbo.prat[2] = 1.0 ;
         Turbo.prat[4] = 1.0 ;
         Turbo.eta[3] = .959 ;
         Turbo.eta[4] = .984 ;
         Turbo.eta[5] = .982 ;
         Turbo.eta[7] = .92 ;
         Turbo.eta[13] = 1.0 ;
         Turbo.a8d = 1.524 ;
         Turbo.a8max = .335 ;
         Turbo.a8rat = .335 ;

         Turbo.u0max = Turbo.u0mt;
         Turbo.u0d = 0.0 ;
         Turbo.altmax = Turbo.altmt;
         Turbo.altd = 0.0 ;
        arsched = 0 ;

        wtflag = 0;
         Turbo.weight = 3875.;
         Turbo.minlt = 1;
         Turbo.dinlt = 170.;
         Turbo.tinlt = 900.;
         Turbo.mfan = 2;
         Turbo.dfan = 293.;
         Turbo.tfan = 1500.;
         Turbo.mcomp = 2;
         Turbo.dcomp = 293.;
         Turbo.tcomp = 1500.;
         Turbo.mburner = 4;
         Turbo.dburner = 515.;
         Turbo.tburner = 2500.;
         Turbo.mturbin = 4;
         Turbo.dturbin = 515.;
         Turbo.tturbin = 2500.;
         Turbo.mnozl = 5;
         Turbo.dnozl = 400.2 ;
         Turbo.tnozl = 4100. ;
         Turbo.ncflag = 0;
         Turbo.ntflag = 0;

        con.setPanl() ;
        return ;
     }

     public void loadRamj() {

        entype = 3 ;
        athsched = 1  ;
        aexsched = 1 ;
         Turbo.arthd = .4 ;
         Turbo.arexitd = 3.0 ;
        abflag = 0 ;
        fueltype = 0;
         Turbo.fhvd = Turbo.fhv = 18600. ;
         Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 4000. ;
         Turbo.t4max = 4500. ;
         Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 4000. ;
         Turbo.prat[3] = Turbo.p3p2d = 1.0 ;
         Turbo.prat[13] = Turbo.p3fp2d = 1.0 ;
         Turbo.byprat = 0.0 ;
         Turbo.a2d = Turbo.a2 = Turbo.acore = 1.753 ;
         Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
         Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
         Turbo.a4 = .323 ;
         Turbo.a4p = .818 ;
         Turbo.ac = .9 * Turbo.a2;
         Turbo.gama = 1.4 ;
        gamopt = 1 ;
        pt2flag = 0 ;
         Turbo.eta[2] = 1.0 ;
         Turbo.prat[2] = 1.0 ;
         Turbo.prat[4] = 1.0 ;
         Turbo.eta[3] = 1.0 ;
         Turbo.eta[4] = .982 ;
         Turbo.eta[5] = 1.0 ;
         Turbo.eta[7] = 1.0 ;
         Turbo.eta[13] = 1.0 ;
         Turbo.a8 = Turbo.a8d = 2.00 ;
         Turbo.a8max = 15. ;
         Turbo.a8rat = 4.0 ;
         Turbo.a7 = .50 ;

         Turbo.u0max = Turbo.u0mr;
         Turbo.u0d = 2200.0 ;
         Turbo.altmax = Turbo.altmr;
         Turbo.altd = 10000.0 ;
        arsched = 0 ;

        wtflag = 0;
         Turbo.weight = 976.;
         Turbo.minlt = 2;
         Turbo.dinlt = 293.;
         Turbo.tinlt = 1500.;
         Turbo.mfan = 2;
         Turbo.dfan = 293.;
         Turbo.tfan = 1500.;
         Turbo.mcomp = 2;
         Turbo.dcomp = 293.;
         Turbo.tcomp = 1500.;
         Turbo.mburner = 7;
         Turbo.dburner = 515.;
         Turbo.tburner = 4500.;
         Turbo.mturbin = 4;
         Turbo.dturbin = 515.;
         Turbo.tturbin = 2500.;
         Turbo.mnozr = 5;
         Turbo.dnozr = 515.2 ;
         Turbo.tnozr = 4500. ;
         Turbo.ncflag = 0;
         Turbo.ntflag = 0;

        con.setPanl() ;
        return ;
     }

     public void getFreeStream() {
         Turbo.rgas = 1718. ;                /* ft2/sec2 R */
       if (inptype >= 2) {
           Turbo.ps0 = Turbo.ps0 * 144. ;
       }
       if (inptype <= 1) {            /* input altitude */
           Turbo.alt = Turbo.altd / Turbo.lconv1;
         if (Turbo.alt < 36152. ) {
             Turbo.ts0 = 518.6 - 3.56 * Turbo.alt / 1000. ;
             Turbo.ps0 = 2116. * Math.pow(Turbo.ts0 / 518.6, 5.256) ;
         }
         if (Turbo.alt >= 36152. && Turbo.alt <= 82345.) {   // Stratosphere
             Turbo.ts0 = 389.98 ;
             Turbo.ps0 = 2116. * .2236 *
                Math.exp((36000. - Turbo.alt) / (53.35 * 389.98)) ;
         }
         if (Turbo.alt >= 82345.) {
             Turbo.ts0 = 389.98 + 1.645 * (Turbo.alt - 82345) / 1000. ;
             Turbo.ps0 = 2116. *.02456 * Math.pow(Turbo.ts0 / 389.98, -11.388) ;
         }
       }
         Turbo.a0 = Math.sqrt(Turbo.gama * Turbo.rgas * Turbo.ts0) ;             /* speed of sound ft/sec */
       if (inptype == 0 || inptype == 2) {           /* input speed  */
           Turbo.u0 = Turbo.u0d / Turbo.lconv2 * 5280. / 3600. ;           /* airspeed ft/sec */
           Turbo.fsmach = Turbo.u0 / Turbo.a0;
           Turbo.q0 = Turbo.gama / 2.0 * Turbo.fsmach * Turbo.fsmach * Turbo.ps0;
       }
       if (inptype == 1 || inptype == 3) {            /* input mach */
           Turbo.u0 = Turbo.fsmach * Turbo.a0;
           Turbo.u0d = Turbo.u0 * Turbo.lconv2 / 5280. * 3600. ;      /* airspeed ft/sec */
           Turbo.q0 = Turbo.gama / 2.0 * Turbo.fsmach * Turbo.fsmach * Turbo.ps0;
       }
       if (Turbo.u0 > .0001) {
           Turbo.rho0 = Turbo.q0 / (Turbo.u0 * Turbo.u0);
       } else {
           Turbo.rho0 = 1.0;
       }

         Turbo.tt[0] = Turbo.ts0 * (1.0 + .5 * (Turbo.gama - 1.0) * Turbo.fsmach * Turbo.fsmach) ;
         Turbo.pt[0] = Turbo.ps0 * Math.pow(Turbo.tt[0] / Turbo.ts0, Turbo.gama / (Turbo.gama - 1.0)) ;
         Turbo.ps0 = Turbo.ps0 / 144. ;
         Turbo.pt[0] = Turbo.pt[0] / 144. ;
         Turbo.cpair = getCp(Turbo.tt[0], gamopt);              /*BTU/lbm R */
         Turbo.tsout = Turbo.ts0;
         Turbo.psout = Turbo.ps0;

       return ;
     }

     public void getThermo() {
       double dela,t5t4n,deriv,delan,m5;
       double delhc,delhht,delhf,delhlt;
       double deltc,deltht,deltf,deltlt;
       int itcount,index ;
       float fl1 ;
       int i1 ;
                                         /*   inlet recovery  */
       if (pt2flag == 0) {                    /*     mil spec      */
          if (Turbo.fsmach > 1.0 ) {          /* supersonic */
              Turbo.prat[2] = 1.0 - .075 * Math.pow(Turbo.fsmach - 1.0, 1.35) ;
          }
          else {
              Turbo.prat[2] = 1.0 ;
          }
           Turbo.eta[2] = Turbo.prat[2] ;
          fl1 = filter3(Turbo.prat[2]) ;
          in.inlet.left.f1.setText(String.valueOf(fl1)) ;
          i1 = (int) (((Turbo.prat[2] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
          in.inlet.right.s1.setValue(i1) ;
       }
       else {                       /* enter value */
           Turbo.prat[2] = Turbo.eta[2] ;
       }
                               /* protection for overwriting input */
       if (Turbo.eta[3] < .5) {
           Turbo.eta[3] = .5;
       }
       if (Turbo.eta[5] < .5) {
           Turbo.eta[5] = .5;
       }
         Turbo.trat[7] = 1.0 ;
         Turbo.prat[7] = 1.0 ;
         Turbo.tt[2] = Turbo.tt[1] = Turbo.tt[0] ;
         Turbo.pt[1] = Turbo.pt[0] ;
         Turbo.gam[2] = getGama(Turbo.tt[2], gamopt) ;
         Turbo.cp[2]  = getCp(Turbo.tt[2], gamopt);
         Turbo.pt[2] = Turbo.pt[1] * Turbo.prat[2] ;
        /* design - p3p2 specified - tt4 specified */
       if(inflag == 0) {

        if (entype <= 1) {              /* turbojet */
            Turbo.prat[3] = Turbo.p3p2d;                      /* core compressor */
          if (Turbo.prat[3] < .5) {
              Turbo.prat[3] = .5;
          }
          delhc = (Turbo.cp[2] * Turbo.tt[2] / Turbo.eta[3]) *
                  (Math.pow(Turbo.prat[3], (Turbo.gam[2] - 1.0) / Turbo.gam[2]) - 1.0) ;
          deltc = delhc / Turbo.cp[2] ;
            Turbo.pt[3] = Turbo.pt[2] * Turbo.prat[3] ;
            Turbo.tt[3] = Turbo.tt[2] + deltc ;
            Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[2] ;
            Turbo.gam[3] = getGama(Turbo.tt[3], gamopt) ;
            Turbo.cp[3]  = getCp(Turbo.tt[3], gamopt);
            Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0 ;
            Turbo.gam[4] = getGama(Turbo.tt[4], gamopt) ;
            Turbo.cp[4]  = getCp(Turbo.tt[4], gamopt);
            Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3] ;
            Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4] ;
          delhht = delhc ;
          deltht = delhht / Turbo.cp[4] ;
            Turbo.tt[5] = Turbo.tt[4] - deltht ;
            Turbo.gam[5] = getGama(Turbo.tt[5], gamopt) ;
            Turbo.cp[5]  = getCp(Turbo.tt[5], gamopt);
            Turbo.trat[5] = Turbo.tt[5] / Turbo.tt[4] ;
            Turbo.prat[5] = Math.pow((1.0 - delhht / Turbo.cp[4] / Turbo.tt[4] / Turbo.eta[5]),
                                     (Turbo.gam[4] / (Turbo.gam[4] - 1.0))) ;
            Turbo.pt[5] = Turbo.pt[4] * Turbo.prat[5] ;
                                        /* fan conditions */
            Turbo.prat[13] = 1.0 ;
            Turbo.trat[13] = 1.0 ;
            Turbo.tt[13]   = Turbo.tt[2] ;
            Turbo.pt[13]   = Turbo.pt[2] ;
            Turbo.gam[13]  = Turbo.gam[2] ;
            Turbo.cp[13]   = Turbo.cp[2] ;
            Turbo.prat[15] = 1.0 ;
            Turbo.pt[15]   = Turbo.pt[5] ;
            Turbo.trat[15] = 1.0 ;
            Turbo.tt[15]   = Turbo.tt[5] ;
            Turbo.gam[15]  = Turbo.gam[5] ;
            Turbo.cp[15]   = Turbo.cp[5] ;
       }

       if(entype == 2) {                         /* turbofan */
           Turbo.prat[13] = Turbo.p3fp2d;
          if (Turbo.prat[13] < .5) {
              Turbo.prat[13] = .5;
          }
          delhf = (Turbo.cp[2] * Turbo.tt[2] / Turbo.eta[13]) *
                  (Math.pow(Turbo.prat[13], (Turbo.gam[2] - 1.0) / Turbo.gam[2]) - 1.0) ;
          deltf = delhf / Turbo.cp[2] ;
           Turbo.tt[13] = Turbo.tt[2] + deltf ;
           Turbo.pt[13] = Turbo.pt[2] * Turbo.prat[13] ;
           Turbo.trat[13] = Turbo.tt[13] / Turbo.tt[2] ;
           Turbo.gam[13] = getGama(Turbo.tt[13], gamopt) ;
           Turbo.cp[13]  = getCp(Turbo.tt[13], gamopt);
           Turbo.prat[3] = Turbo.p3p2d;                      /* core compressor */
          if (Turbo.prat[3] < .5) {
              Turbo.prat[3] = .5;
          }
          delhc = (Turbo.cp[13] * Turbo.tt[13] / Turbo.eta[3]) *
                  (Math.pow(Turbo.prat[3], (Turbo.gam[13] - 1.0) / Turbo.gam[13]) - 1.0) ;
          deltc = delhc / Turbo.cp[13] ;
           Turbo.tt[3] = Turbo.tt[13] + deltc ;
           Turbo.pt[3] = Turbo.pt[13] * Turbo.prat[3] ;
           Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[13] ;
           Turbo.gam[3] = getGama(Turbo.tt[3], gamopt) ;
           Turbo.cp[3]  = getCp(Turbo.tt[3], gamopt);
           Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0 ;
           Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4] ;
           Turbo.gam[4] = getGama(Turbo.tt[4], gamopt) ;
           Turbo.cp[4]  = getCp(Turbo.tt[4], gamopt);
           Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3] ;
          delhht = delhc ;
          deltht = delhht / Turbo.cp[4] ;
           Turbo.tt[5] = Turbo.tt[4] - deltht ;
           Turbo.gam[5] = getGama(Turbo.tt[5], gamopt) ;
           Turbo.cp[5]  = getCp(Turbo.tt[5], gamopt);
           Turbo.trat[5] = Turbo.tt[5] / Turbo.tt[4] ;
           Turbo.prat[5] = Math.pow((1.0 - delhht / Turbo.cp[4] / Turbo.tt[4] / Turbo.eta[5]),
                                    (Turbo.gam[4] / (Turbo.gam[4] - 1.0))) ;
           Turbo.pt[5] = Turbo.pt[4] * Turbo.prat[5] ;
          delhlt = (1.0 + Turbo.byprat) * delhf ;
          deltlt = delhlt / Turbo.cp[5] ;
           Turbo.tt[15] = Turbo.tt[5] - deltlt ;
           Turbo.gam[15] = getGama(Turbo.tt[15], gamopt) ;
           Turbo.cp[15]  = getCp(Turbo.tt[15], gamopt);
           Turbo.trat[15] = Turbo.tt[15] / Turbo.tt[5] ;
           Turbo.prat[15] = Math.pow((1.0 - delhlt / Turbo.cp[5] / Turbo.tt[5] / Turbo.eta[5]),
                                     (Turbo.gam[5] / (Turbo.gam[5] - 1.0))) ;
           Turbo.pt[15] = Turbo.pt[5] * Turbo.prat[15] ;
        }

        if (entype == 3) {              /* ramjet */
            Turbo.prat[3] = 1.0 ;
            Turbo.pt[3] = Turbo.pt[2] * Turbo.prat[3] ;
            Turbo.tt[3] = Turbo.tt[2] ;
            Turbo.trat[3] = 1.0 ;
            Turbo.gam[3] = getGama(Turbo.tt[3], gamopt) ;
            Turbo.cp[3]  = getCp(Turbo.tt[3], gamopt);
            Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0 ;
            Turbo.gam[4] = getGama(Turbo.tt[4], gamopt) ;
            Turbo.cp[4]  = getCp(Turbo.tt[4], gamopt);
            Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3] ;
            Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4] ;
            Turbo.tt[5] = Turbo.tt[4] ;
            Turbo.gam[5] = getGama(Turbo.tt[5], gamopt) ;
            Turbo.cp[5]  = getCp(Turbo.tt[5], gamopt);
            Turbo.trat[5] = 1.0 ;
            Turbo.prat[5] = 1.0 ;
            Turbo.pt[5] = Turbo.pt[4] ;
                                        /* fan conditions */
            Turbo.prat[13] = 1.0 ;
            Turbo.trat[13] = 1.0 ;
            Turbo.tt[13]   = Turbo.tt[2] ;
            Turbo.pt[13]   = Turbo.pt[2] ;
            Turbo.gam[13]  = Turbo.gam[2] ;
            Turbo.cp[13]   = Turbo.cp[2] ;
            Turbo.prat[15] = 1.0 ;
            Turbo.pt[15]   = Turbo.pt[5] ;
            Turbo.trat[15] = 1.0 ;
            Turbo.tt[15]   = Turbo.tt[5] ;
            Turbo.gam[15]  = Turbo.gam[5] ;
            Turbo.cp[15]   = Turbo.cp[5] ;
        }

           Turbo.tt[7] = Turbo.tt7;
      }
             /* analysis -assume flow choked at both turbine entrances */
                                  /* and nozzle throat ... then*/
      else {
           Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0 ;
           Turbo.gam[4] = getGama(Turbo.tt[4], gamopt) ;
           Turbo.cp[4]  = getCp(Turbo.tt[4], gamopt);
        if (Turbo.a4 < .02) {
            Turbo.a4 = .02;
        }

        if (entype <= 1) {              /* turbojet */
           dela = .2 ;                           /* iterate to get t5t4 */
            Turbo.trat[5] = 1.0 ;
           t5t4n = .5 ;
           itcount = 0 ;
           while(Math.abs(dela) > .001 && itcount < 20) {
              ++ itcount ;
              delan = Turbo.a8d / Turbo.a4 - Math.sqrt(t5t4n) *
                                             Math.pow((1.0- (1.0 / Turbo.eta[5]) * (1.0 - t5t4n)),
                            -Turbo.gam[4] / (Turbo.gam[4] - 1.0)) ;
              deriv = (delan-dela)/(t5t4n - Turbo.trat[5]) ;
              dela = delan ;
               Turbo.trat[5] = t5t4n ;
              t5t4n = Turbo.trat[5] - dela / deriv ;
           }
            Turbo.tt[5] = Turbo.tt[4] * Turbo.trat[5] ;
            Turbo.gam[5] = getGama(Turbo.tt[5], gamopt) ;
            Turbo.cp[5]  = getCp(Turbo.tt[5], gamopt);
           deltht = Turbo.tt[5] - Turbo.tt[4] ;
           delhht  = Turbo.cp[4] * deltht ;
            Turbo.prat[5] = Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - Turbo.trat[5])),
                              Turbo.gam[4] / (Turbo.gam[4] - 1.0)) ;
           delhc = delhht  ;           /* compressor work */
           deltc = -delhc / Turbo.cp[2] ;
            Turbo.tt[3] = Turbo.tt[2] + deltc ;
            Turbo.gam[3] = getGama(Turbo.tt[3], gamopt) ;
            Turbo.cp[3]  = getCp(Turbo.tt[3], gamopt);
            Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[2] ;
            Turbo.prat[3] = Math.pow((1.0 + Turbo.eta[3] * (Turbo.trat[3] - 1.0)),
                              Turbo.gam[2] / (Turbo.gam[2] - 1.0)) ;
            Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3] ;
            Turbo.pt[3]   = Turbo.pt[2] * Turbo.prat[3] ;
            Turbo.pt[4]   = Turbo.pt[3] * Turbo.prat[4] ;
            Turbo.pt[5]   = Turbo.pt[4] * Turbo.prat[5] ;
                                        /* fan conditions */
            Turbo.prat[13] = 1.0 ;
            Turbo.trat[13] = 1.0 ;
            Turbo.tt[13]   = Turbo.tt[2] ;
            Turbo.pt[13]   = Turbo.pt[2] ;
            Turbo.gam[13]  = Turbo.gam[2] ;
            Turbo.cp[13]   = Turbo.cp[2] ;
            Turbo.prat[15] = 1.0 ;
            Turbo.pt[15]   = Turbo.pt[5] ;
            Turbo.trat[15] = 1.0 ;
            Turbo.tt[15]   = Turbo.tt[5] ;
            Turbo.gam[15]  = Turbo.gam[5] ;
            Turbo.cp[15]   = Turbo.cp[5] ;
        }

        if(entype == 2) {                        /*  turbofan */
           dela = .2 ;                           /* iterate to get t5t4 */
            Turbo.trat[5] = 1.0 ;
           t5t4n = .5 ;
           itcount = 0 ;
           while(Math.abs(dela) > .001 && itcount < 20) {
              ++ itcount ;
              delan = Turbo.a4p / Turbo.a4 - Math.sqrt(t5t4n) *
                                             Math.pow((1.0- (1.0 / Turbo.eta[5]) * (1.0 - t5t4n)),
                               -Turbo.gam[4] / (Turbo.gam[4] - 1.0)) ;
              deriv = (delan-dela)/(t5t4n - Turbo.trat[5]) ;
              dela = delan ;
               Turbo.trat[5] = t5t4n ;
              t5t4n = Turbo.trat[5] - dela / deriv ;
           }
            Turbo.tt[5] = Turbo.tt[4] * Turbo.trat[5] ;
            Turbo.gam[5] = getGama(Turbo.tt[5], gamopt) ;
            Turbo.cp[5]  = getCp(Turbo.tt[5], gamopt);
           deltht = Turbo.tt[5] - Turbo.tt[4] ;
           delhht  = Turbo.cp[4] * deltht ;
            Turbo.prat[5] = Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - Turbo.trat[5])),
                              Turbo.gam[4] / (Turbo.gam[4] - 1.0)) ;
                                       /* iterate to get t15t14 */
           dela = .2 ;
            Turbo.trat[15] = 1.0 ;
           t5t4n = .5 ;
           itcount = 0 ;
           while(Math.abs(dela) > .001 && itcount < 20) {
               ++ itcount ;
               delan = Turbo.a8d / Turbo.a4p - Math.sqrt(t5t4n) *
                                               Math.pow((1.0- (1.0 / Turbo.eta[5]) * (1.0 - t5t4n)),
                                 -Turbo.gam[5] / (Turbo.gam[5] - 1.0)) ;
               deriv = (delan-dela)/(t5t4n - Turbo.trat[15]) ;
               dela = delan ;
               Turbo.trat[15] = t5t4n ;
               t5t4n = Turbo.trat[15] - dela / deriv ;
           }
            Turbo.tt[15] = Turbo.tt[5] * Turbo.trat[15] ;
            Turbo.gam[15] = getGama(Turbo.tt[15], gamopt) ;
            Turbo.cp[15]  = getCp(Turbo.tt[15], gamopt);
           deltlt = Turbo.tt[15] - Turbo.tt[5] ;
           delhlt = Turbo.cp[5] * deltlt ;
            Turbo.prat[15] = Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - Turbo.trat[15])),
                               Turbo.gam[5] / (Turbo.gam[5] - 1.0)) ;
            Turbo.byprat = Turbo.afan / Turbo.acore - 1.0  ;
           delhf = delhlt / (1.0 + Turbo.byprat) ;              /* fan work */
           deltf = - delhf / Turbo.cp[2] ;
            Turbo.tt[13] = Turbo.tt[2] + deltf ;
            Turbo.gam[13] = getGama(Turbo.tt[13], gamopt) ;
            Turbo.cp[13]  = getCp(Turbo.tt[13], gamopt);
            Turbo.trat[13] = Turbo.tt[13] / Turbo.tt[2] ;
            Turbo.prat[13] = Math.pow((1.0 + Turbo.eta[13] * (Turbo.trat[13] - 1.0)),
                               Turbo.gam[2] / (Turbo.gam[2] - 1.0)) ;
           delhc = delhht  ;                         /* compressor work */
           deltc = -delhc / Turbo.cp[13] ;
            Turbo.tt[3] = Turbo.tt[13] + deltc ;
            Turbo.gam[3] = getGama(Turbo.tt[3], gamopt) ;
            Turbo.cp[3]  = getCp(Turbo.tt[3], gamopt);
            Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[13] ;
            Turbo.prat[3] = Math.pow((1.0 + Turbo.eta[3] * (Turbo.trat[3] - 1.0)),
                              Turbo.gam[13] / (Turbo.gam[13] - 1.0)) ;
            Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3] ;
            Turbo.pt[13]  = Turbo.pt[2] * Turbo.prat[13] ;
            Turbo.pt[3]   = Turbo.pt[13] * Turbo.prat[3] ;
            Turbo.pt[4]   = Turbo.pt[3] * Turbo.prat[4] ;
            Turbo.pt[5]   = Turbo.pt[4] * Turbo.prat[5] ;
            Turbo.pt[15]  = Turbo.pt[5] * Turbo.prat[15] ;
         }

         if (entype == 3) {              /* ramjet */
             Turbo.prat[3] = 1.0 ;
             Turbo.pt[3] = Turbo.pt[2] * Turbo.prat[3] ;
             Turbo.tt[3] = Turbo.tt[2] ;
             Turbo.trat[3] = 1.0 ;
             Turbo.gam[3] = getGama(Turbo.tt[3], gamopt) ;
             Turbo.cp[3]  = getCp(Turbo.tt[3], gamopt);
             Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0  ;
             Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3] ;
             Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4] ;
             Turbo.tt[5] = Turbo.tt[4] ;
             Turbo.gam[5] = getGama(Turbo.tt[5], gamopt) ;
             Turbo.cp[5]  = getCp(Turbo.tt[5], gamopt);
             Turbo.trat[5] = 1.0 ;
             Turbo.prat[5] = 1.0 ;
             Turbo.pt[5] = Turbo.pt[4] ;
                                         /* fan conditions */
             Turbo.prat[13] = 1.0 ;
             Turbo.trat[13] = 1.0 ;
             Turbo.tt[13]   = Turbo.tt[2] ;
             Turbo.pt[13]   = Turbo.pt[2] ;
             Turbo.gam[13]  = Turbo.gam[2] ;
             Turbo.cp[13]   = Turbo.cp[2] ;
             Turbo.prat[15] = 1.0 ;
             Turbo.pt[15]   = Turbo.pt[5] ;
             Turbo.trat[15] = 1.0 ;
             Turbo.tt[15]   = Turbo.tt[5] ;
             Turbo.gam[15]  = Turbo.gam[5] ;
             Turbo.cp[15]   = Turbo.cp[5] ;
         }

         if (abflag == 1) {
             Turbo.tt[7] = Turbo.tt7;
         }
       }

         Turbo.prat[6] = 1.0;
         Turbo.pt[6] = Turbo.pt[15];
         Turbo.trat[6] = 1.0 ;
         Turbo.tt[6] = Turbo.tt[15] ;
         Turbo.gam[6] = getGama(Turbo.tt[6], gamopt) ;
         Turbo.cp[6]  = getCp(Turbo.tt[6], gamopt);
       if (abflag > 0) {                   /* afterburner */
           Turbo.trat[7] = Turbo.tt[7] / Turbo.tt[6] ;
             m5 = getMach(0, getAir(1.0, Turbo.gam[5]) * Turbo.a4 / Turbo.acore, Turbo.gam[5]) ;
           Turbo.prat[7] = getRayleighLoss(m5, Turbo.trat[7], Turbo.tt[6]) ;
       }
         Turbo.tt[7] = Turbo.tt[6] * Turbo.trat[7] ;
         Turbo.pt[7] = Turbo.pt[6] * Turbo.prat[7] ;
         Turbo.gam[7] = getGama(Turbo.tt[7], gamopt) ;
         Turbo.cp[7]  = getCp(Turbo.tt[7], gamopt);
                 /* engine press ratio EPR*/
         Turbo.epr = Turbo.prat[7] * Turbo.prat[15] * Turbo.prat[5] * Turbo.prat[4] * Turbo.prat[3] * Turbo.prat[13];
              /* engine temp ratio ETR */
         Turbo.etr = Turbo.trat[7] * Turbo.trat[15] * Turbo.trat[5] * Turbo.trat[4] * Turbo.trat[3] * Turbo.trat[13];
       return;
     }

     public void getPerform ()  {       /* determine engine performance */
       double fac1,game,cpe,cp3,rg,p8p5,rg1 ;
       int index ;

       rg1 = 53.3 ;
       rg = Turbo.cpair * (Turbo.gama - 1.0) / Turbo.gama;
       cp3 = getCp(Turbo.tt[3], gamopt);                  /*BTU/lbm R */
         Turbo.g0 = 32.2 ;
         Turbo.ues = 0.0 ;
       game = getGama(Turbo.tt[5], gamopt) ;
       fac1 = (game - 1.0)/game ;
       cpe = getCp(Turbo.tt[5], gamopt) ;
       if (Turbo.eta[7] < .8) {
           Turbo.eta[7] = .8;    /* protection during overwriting */
       }
       if (Turbo.eta[4] < .8) {
           Turbo.eta[4] = .8;
       }

       /*  specific net thrust  - thrust / (g0*airflow) -   lbf/lbm/sec  */
// turbine engine core
       if (entype <=2) {
                            /* airflow determined at choked nozzle exit */
           Turbo.pt[8] = Turbo.epr * Turbo.prat[2] * Turbo.pt[0] ;
           Turbo.eair = getAir(1.0,game) * 144. * Turbo.a8 * Turbo.pt[8] / 14.7 /
                   Math.sqrt(Turbo.etr * Turbo.tt[0] / 518.)   ;
           Turbo.m2 = getMach(0, Turbo.eair * Math.sqrt(Turbo.tt[0] / 518.) /
                            (Turbo.prat[2] * Turbo.pt[0] / 14.7 * Turbo.acore * 144.), Turbo.gama) ;
           Turbo.npr = Turbo.pt[8] / Turbo.ps0;
           Turbo.uexit = Math.sqrt(2.0 * Turbo.rgas / fac1 * Turbo.etr * Turbo.tt[0] * Turbo.eta[7] *
                              (1.0-Math.pow(1.0 / Turbo.npr, fac1)));
            if (Turbo.npr <= 1.893) {
                Turbo.pexit = Turbo.ps0;
            } else {
                Turbo.pexit = .52828 * Turbo.pt[8];
            }
           Turbo.fgros = (Turbo.uexit + (Turbo.pexit - Turbo.ps0) * 144. * Turbo.a8 / Turbo.eair) / Turbo.g0;
       }

// turbo fan -- added terms for fan flow
       if (entype == 2) {
            fac1 = (Turbo.gama - 1.0) / Turbo.gama;
           Turbo.snpr = Turbo.pt[13] / Turbo.ps0;
           Turbo.ues = Math.sqrt(2.0 * Turbo.rgas / fac1 * Turbo.tt[13] * Turbo.eta[7] *
                            (1.0-Math.pow(1.0 / Turbo.snpr, fac1)));
           Turbo.m2 = getMach(0, Turbo.eair * (1.0 + Turbo.byprat) * Math.sqrt(Turbo.tt[0] / 518.) /
                            (Turbo.prat[2] * Turbo.pt[0] / 14.7 * Turbo.afan * 144.), Turbo.gama) ;
            if (Turbo.snpr <= 1.893) {
                Turbo.pfexit = Turbo.ps0;
            } else {
                Turbo.pfexit = .52828 * Turbo.pt[13];
            }
           Turbo.fgros = Turbo.fgros + (Turbo.byprat * Turbo.ues + (Turbo.pfexit - Turbo.ps0) * 144. * Turbo.byprat * Turbo.acore / Turbo.eair) / Turbo.g0;
       }

// ramjets
       if (entype == 3) {
                           /* airflow determined at nozzle throat */
           Turbo.eair = getAir(1.0,game) * 144.0 * Turbo.a2 * Turbo.arthd * Turbo.epr * Turbo.prat[2] * Turbo.pt[0] / 14.7 /
                   Math.sqrt(Turbo.etr * Turbo.tt[0] / 518.)   ;
           Turbo.m2 = getMach(0, Turbo.eair * Math.sqrt(Turbo.tt[0] / 518.) /
                            (Turbo.prat[2] * Turbo.pt[0] / 14.7 * Turbo.acore * 144.), Turbo.gama) ;
           Turbo.mexit = getMach(2, (getAir(1.0,game) / Turbo.arexitd), game) ;
           Turbo.uexit = Turbo.mexit * Math.sqrt(game * Turbo.rgas * Turbo.etr * Turbo.tt[0] * Turbo.eta[7] /
                                            (1.0 + .5 * (game-1.0) * Turbo.mexit * Turbo.mexit)) ;
           Turbo.pexit = Math.pow((1.0 + .5 * (game-1.0) * Turbo.mexit * Turbo.mexit), (-game / (game - 1.0)))
                    * Turbo.epr * Turbo.prat[2] * Turbo.pt[0] ;
           Turbo.fgros = (Turbo.uexit + (Turbo.pexit - Turbo.ps0) * Turbo.arexitd * Turbo.arthd * Turbo.a2 / Turbo.eair / 144.) / Turbo.g0;
       }

// ram drag
         Turbo.dram = Turbo.u0 / Turbo.g0;
       if (entype == 2) {
           Turbo.dram = Turbo.dram + Turbo.u0 * Turbo.byprat / Turbo.g0;
       }
// mass flow ratio
       if (Turbo.fsmach > .01) {
           Turbo.mfr = getAir(Turbo.m2, Turbo.gama) * Turbo.prat[2] / getAir(Turbo.fsmach, Turbo.gama);
       } else {
           Turbo.mfr = 5.;
       }

// net thrust
         Turbo.fnet = Turbo.fgros - Turbo.dram;
       if (entype == 3 && Turbo.fsmach < .3) {
           Turbo.fnet = 0.0 ;
           Turbo.fgros = 0.0 ;
       }

// thrust in pounds
         Turbo.fnlb = Turbo.fnet * Turbo.eair;
         Turbo.fglb = Turbo.fgros * Turbo.eair;
         Turbo.drlb = Turbo.dram * Turbo.eair;

//fuel-air ratio and sfc
         Turbo.fa = (Turbo.trat[4] - 1.0) / (Turbo.eta[4] * Turbo.fhv / (cp3 * Turbo.tt[3]) - Turbo.trat[4]) +
            (Turbo.trat[7] - 1.0) / (Turbo.fhv / (cpe * Turbo.tt[15]) - Turbo.trat[7]) ;
       if (Turbo.fnet > 0.0)  {
           Turbo.sfc = 3600. * Turbo.fa / Turbo.fnet;
           Turbo.flflo = Turbo.sfc * Turbo.fnlb;
           Turbo.isp = (Turbo.fnlb / Turbo.flflo) * 3600. ;
       }
       else {
           Turbo.fnlb = 0.0 ;
           Turbo.flflo = 0.0 ;
           Turbo.sfc = 0.0 ;
           Turbo.isp = 0.0 ;
       }
         Turbo.tt[8] = Turbo.tt[7] ;
         Turbo.t8 = Turbo.etr * Turbo.tt[0] - Turbo.uexit * Turbo.uexit / (2.0 * Turbo.rgas * game / (game - 1.0)) ;
         Turbo.trat[8] = 1.0 ;
       p8p5 = Turbo.ps0 / (Turbo.epr * Turbo.prat[2] * Turbo.pt[0]) ;
         Turbo.cp[8] = getCp(Turbo.tt[8], gamopt) ;
         Turbo.pt[8] = Turbo.pt[7] ;
         Turbo.prat[8] = Turbo.pt[8] / Turbo.pt[7] ;
        /* thermal effeciency */
       if (entype == 2) {
           Turbo.eteng = (Turbo.a0 * Turbo.a0 * ((1.0 + Turbo.fa) * (Turbo.uexit * Turbo.uexit / (Turbo.a0 * Turbo.a0))
                                                 + Turbo.byprat * (Turbo.ues * Turbo.ues / (Turbo.a0 * Turbo.a0))
                                                 - (1.0 + Turbo.byprat) * Turbo.fsmach * Turbo.fsmach)) / (2.0 * Turbo.g0 * Turbo.fa * Turbo.fhv * 778.16)    ;
       }
       else {
           Turbo.eteng = (Turbo.a0 * Turbo.a0 * ((1.0 + Turbo.fa) * (Turbo.uexit * Turbo.uexit / (Turbo.a0 * Turbo.a0))
                                                 - Turbo.fsmach * Turbo.fsmach)) / (2.0 * Turbo.g0 * Turbo.fa * Turbo.fhv * 778.16)    ;
       }

         Turbo.s[0] = Turbo.s[1] = .2 ;
         Turbo.v[0] = Turbo.v[1] = rg1 * Turbo.ts0 / (Turbo.ps0 * 144.) ;
       for (index=2; index <=7 ; ++index ) {     /* compute entropy */
           Turbo.s[index] = Turbo.s[index - 1] + Turbo.cpair * Math.log(Turbo.trat[index])
                            - rg*Math.log(Turbo.prat[index])  ;
           Turbo.v[index] = rg1 * Turbo.tt[index] / (Turbo.pt[index] * 144.) ;
       }
         Turbo.s[13] = Turbo.s[2] + Turbo.cpair * Math.log(Turbo.trat[13]) - rg * Math.log(Turbo.prat[13]);
         Turbo.v[13] = rg1 * Turbo.tt[13] / (Turbo.pt[13] * 144.) ;
         Turbo.s[15] = Turbo.s[5] + Turbo.cpair * Math.log(Turbo.trat[15]) - rg * Math.log(Turbo.prat[15]);
         Turbo.v[15] = rg1 * Turbo.tt[15] / (Turbo.pt[15] * 144.) ;
         Turbo.s[8]  = Turbo.s[7] + Turbo.cpair * Math.log(Turbo.t8 / (Turbo.etr * Turbo.tt[0])) - rg * Math.log(p8p5)  ;
         Turbo.v[8]  = rg1 * Turbo.t8 / (Turbo.ps0 * 144.) ;
         Turbo.cp[0] = getCp(Turbo.tt[0], gamopt) ;

         Turbo.fntot = numeng * Turbo.fnlb;
         Turbo.fuelrat = numeng * Turbo.flflo;
    // weight  calculation
       if (wtflag == 0) {
          if (entype == 0) {
              Turbo.weight = .132 * Math.sqrt(Turbo.acore * Turbo.acore * Turbo.acore) *
                     (Turbo.dcomp * Turbo.lcomp + Turbo.dburner * Turbo.lburn + Turbo.dturbin * Turbo.lturb + Turbo.dnozl * Turbo.lnoz);
          }
          if (entype == 1) {
              Turbo.weight = .100 * Math.sqrt(Turbo.acore * Turbo.acore * Turbo.acore) *
                     (Turbo.dcomp * Turbo.lcomp + Turbo.dburner * Turbo.lburn + Turbo.dturbin * Turbo.lturb + Turbo.dnozl * Turbo.lnoz);
          }
          if (entype == 2) {
              Turbo.weight = .0932 * Turbo.acore * ((1.0 + Turbo.byprat) * Turbo.dfan * 4.0 + Turbo.dcomp * (Turbo.ncomp - 3) +
                                            Turbo.dburner + Turbo.dturbin * Turbo.nturb + Turbo.dburner * 2.0) * Math.sqrt(Turbo.acore / 6.965) ;
          }
          if (entype == 3) {
              Turbo.weight = .1242 * Turbo.acore * (Turbo.dburner + Turbo.dnozr * 6. + Turbo.dinlt * 3.) * Math.sqrt(Turbo.acore / 1.753) ;
          }
       }
     // check for temp limits
       out.vars.to1.setForeground(Color.yellow) ;
       out.vars.to2.setForeground(Color.yellow) ;
       out.vars.to3.setForeground(Color.yellow) ;
       out.vars.to4.setForeground(Color.yellow) ;
       out.vars.to5.setForeground(Color.yellow) ;
       out.vars.to6.setForeground(Color.yellow) ;
       out.vars.to7.setForeground(Color.yellow) ;
       if (entype < 3) {
          if (Turbo.tt[2] > Turbo.tinlt) {
              Turbo.fireflag =1 ;
             out.vars.to1.setForeground(Color.red) ;
             out.vars.to2.setForeground(Color.red) ;
          }
          if (Turbo.tt[13] > Turbo.tfan) {
              Turbo.fireflag =1 ;
             out.vars.to2.setForeground(Color.red) ;
          }
          if (Turbo.tt[3] > Turbo.tcomp) {
              Turbo.fireflag =1 ;
             out.vars.to3.setForeground(Color.red) ;
          }
          if (Turbo.tt[4] > Turbo.tburner) {
              Turbo.fireflag =1 ;
             out.vars.to4.setForeground(Color.red) ;
          }
          if (Turbo.tt[5] > Turbo.tturbin) {
              Turbo.fireflag =1 ;
             out.vars.to5.setForeground(Color.red) ;
          }
          if (Turbo.tt[7] > Turbo.tnozl) {
              Turbo.fireflag =1 ;
             out.vars.to6.setForeground(Color.red) ;
             out.vars.to7.setForeground(Color.red) ;
          }
       }
       if (entype == 3) {
          if (Turbo.tt[3] > Turbo.tinlt) {
              Turbo.fireflag =1 ;
             out.vars.to1.setForeground(Color.red) ;
             out.vars.to2.setForeground(Color.red) ;
             out.vars.to3.setForeground(Color.red) ;
          }
          if (Turbo.tt[4] > Turbo.tburner) {
              Turbo.fireflag =1 ;
             out.vars.to4.setForeground(Color.red) ;
          }
          if (Turbo.tt[7] > Turbo.tnozr) {
              Turbo.fireflag =1 ;
             out.vars.to5.setForeground(Color.red) ;
             out.vars.to6.setForeground(Color.red) ;
             out.vars.to7.setForeground(Color.red) ;
          }
       }
       if (Turbo.fireflag == 1) {
           view.start();
       }
     }

     public void getGeo () {
                            /* determine geometric variables */
        double game ;
        float fl1 ;
        int i1 ;

        if (entype <= 2) {          // turbine engines
          if (Turbo.afan < Turbo.acore) {
              Turbo.afan = Turbo.acore;
          }
            Turbo.a8max = .75 * Math.sqrt(Turbo.etr) / Turbo.epr; /* limits compressor face  */
                                               /*  mach number  to < .5   */
          if (Turbo.a8max > 1.0) {
              Turbo.a8max = 1.0;
          }
          if (Turbo.a8rat > Turbo.a8max) {
              Turbo.a8rat = Turbo.a8max;
           if (lunits <= 1) {
               fl1 = filter3(Turbo.a8rat) ;
               in.nozl.left.f3.setText(String.valueOf(fl1)) ;
               i1 = (int) (((Turbo.a8rat - Turbo.a8min) / (Turbo.a8max - Turbo.a8min)) * 1000.) ;
               in.nozl.right.s3.setValue(i1) ;
           }
           if (lunits == 2) {
               fl1 = filter3(100.*(Turbo.a8rat - Turbo.a8ref) / Turbo.a8ref) ;
               in.nozl.left.f3.setText(String.valueOf(fl1)) ;
               i1 = (int) ((((100.*(Turbo.a8rat - Turbo.a8ref) / Turbo.a8ref) + 10.0) / 20.0) * 1000.) ;
               in.nozl.right.s3.setValue(i1) ;
           }
          }
              /*    dumb down limit - a8 schedule */
          if (arsched == 0) {
              Turbo.a8rat = Turbo.a8max;
           fl1 = filter3(Turbo.a8rat) ;
           in.nozl.left.f3.setText(String.valueOf(fl1)) ;
           i1 = (int) (((Turbo.a8rat - Turbo.a8min) / (Turbo.a8max - Turbo.a8min)) * 1000.) ;
           in.nozl.right.s3.setValue(i1) ;
          }
            Turbo.a8 = Turbo.a8rat * Turbo.acore;
            Turbo.a8d = Turbo.a8 * Turbo.prat[7] / Math.sqrt(Turbo.trat[7]) ;
             /* assumes choked a8 and a4 */
            Turbo.a4 = Turbo.a8 * Turbo.prat[5] * Turbo.prat[15] * Turbo.prat[7] /
               Math.sqrt(Turbo.trat[7] * Turbo.trat[5] * Turbo.trat[15]);
            Turbo.a4p = Turbo.a8 * Turbo.prat[15] * Turbo.prat[7] / Math.sqrt(Turbo.trat[7] * Turbo.trat[15]);
            Turbo.ac = .9 * Turbo.a2;
        }

        if (entype == 3) {      // ramjets
          game = getGama(Turbo.tt[4], gamopt) ;
          if (athsched == 0) {   // scheduled throat area
              Turbo.arthd = getAir(Turbo.fsmach, Turbo.gama) * Math.sqrt(Turbo.etr) /
                     (getAir(1.0,game) * Turbo.epr * Turbo.prat[2]) ;
             if (Turbo.arthd < Turbo.arthmn) {
                 Turbo.arthd = Turbo.arthmn;
             }
             if (Turbo.arthd > Turbo.arthmx) {
                 Turbo.arthd = Turbo.arthmx;
             }
             fl1 = filter3(Turbo.arthd) ;
             in.nozr.left.f3.setText(String.valueOf(fl1)) ;
             i1 = (int) (((Turbo.arthd - Turbo.arthmn) / (Turbo.arthmx - Turbo.arthmn)) * 1000.) ;
             in.nozr.right.s3.setValue(i1) ;
          }
          if (aexsched == 0) {   // scheduled exit area
              Turbo.mexit = Math.sqrt((2.0/(game-1.0))*((1.0+ .5 * (Turbo.gama - 1.0) * Turbo.fsmach * Turbo.fsmach)
                  *Math.pow((Turbo.epr * Turbo.prat[2]), (game - 1.0) / game) - 1.0) ) ;
              Turbo.arexitd = getAir(1.0,game) / getAir(Turbo.mexit, game) ;
             if (Turbo.arexitd < Turbo.arexmn) {
                 Turbo.arexitd = Turbo.arexmn;
             }
             if (Turbo.arexitd > Turbo.arexmx) {
                 Turbo.arexitd = Turbo.arexmx;
             }
             fl1 = filter3(Turbo.arexitd) ;
             in.nozr.left.f4.setText(String.valueOf(fl1)) ;
             i1 = (int) (((Turbo.arexitd - Turbo.arexmn) / (Turbo.arexmx - Turbo.arexmn)) * 1000.) ;
             in.nozr.right.s4.setValue(i1) ;
          }
        }
     }
  }    // end Solver

  class Con extends Panel {
     Turbo outerparent ;
     Up up ;
     Down down ;

     Con (Turbo target) { 
                               
       outerparent = target ;
       setLayout(new GridLayout(2,1,5,5)) ;

       up = new Up(outerparent) ; 
       down = new Down() ;
 
       add(up) ;
       add(down) ;
    }

    class Up extends Panel {
       Button setref,endit,record ;
       Turbo outerparent ;
       Choice engch,modch,arch,pltch,untch ;
       Label l1, l2, l3 ;

       Up (Turbo target) { 
          outerparent = target ;
          setLayout(new GridLayout(4,3,5,5)) ;
 
          modch = new Choice() ;
          modch.addItem("Design") ;
          modch.addItem("Test");
          modch.select(0) ;

          engch = new Choice() ;
          engch.addItem("My Design") ;
          engch.addItem("J85 Model") ;
          engch.addItem("F100 Model");
          engch.addItem("CF6 Model");
          engch.addItem("Ramjet Model");
          engch.select(0) ;

          l3 = new Label("Output :", Label.RIGHT) ;
          l3.setForeground(Color.red) ;
          pltch = new Choice() ;
          pltch.addItem("Graphs") ;
          pltch.addItem("Engine Performance") ;
          pltch.addItem("Component Performance") ;
          pltch.select(1) ;
          pltch.setBackground(Color.white) ;
          pltch.setForeground(Color.red) ;

          untch = new Choice() ;
          untch.addItem("English") ;
          untch.addItem("Metric");
     //     untch.addItem("% Change");
          untch.select(0) ;

          setref = new Button("Reset") ;
          setref.setBackground(Color.orange) ;
          setref.setForeground(Color.black) ;

          record = new Button("Print Data") ;
          record.setBackground(Color.blue) ;
          record.setForeground(Color.white) ;

          endit = new Button("Exit") ;
          endit.setBackground(Color.red) ;
          endit.setForeground(Color.white) ;

          add(new Label("Mode:", Label.RIGHT)) ;
          add(modch) ;
          add(endit) ;

          add(new Label("Load:", Label.RIGHT)) ;
          add(engch) ;
          add(setref) ;

          add(new Label("Units:", Label.RIGHT)) ;
          add(untch) ;  
          add(record) ;

          add(l3) ;
          add(pltch) ;
          add(new Label("to record", Label.CENTER)) ;
       }

       public Insets insets() {
          return new Insets(0,5,5,0) ;
       }

       public boolean action(Event evt, Object arg) {
         if(evt.target instanceof Choice) {
            this.handleProb(evt,arg) ;
            return true ;
         }
         if(evt.target instanceof Button) {
            this.handleRefs(evt,arg) ;
            return true ;
         }
         else {
             return false;
         }
       }

       public void handleRefs(Event evt, Object arg) {
           String label = (String)arg ;
           String seng,sgamop,smode ;

           if(label.equals("Reset")) {
              if (lunits == 2) {
                // reset reference variables
                  Turbo.u0ref = Turbo.u0d;
                  Turbo.altref = Turbo.altd;
                  Turbo.thrref = Turbo.throtl;
                  Turbo.a2ref = Turbo.a2d;
                  Turbo.et2ref = Turbo.eta[2] ;
                  Turbo.fpref = Turbo.p3fp2d;
                  Turbo.et13ref = Turbo.eta[13];
                  Turbo.bpref = Turbo.byprat;
                  Turbo.cpref = Turbo.p3p2d;
                  Turbo.et3ref = Turbo.eta[3];
                  Turbo.et4ref = Turbo.eta[4];
                  Turbo.et5ref = Turbo.eta[5] ;
                  Turbo.t4ref = Turbo.tt4d;
                  Turbo.p4ref = Turbo.prat[4] ;
                  Turbo.t7ref = Turbo.tt7d;
                  Turbo.et7ref = Turbo.eta[7];
                  Turbo.a8ref = Turbo.a8rat;
                  Turbo.fnref = Turbo.fnlb;
                  Turbo.fuelref = Turbo.fuelrat;
                  Turbo.sfcref = Turbo.sfc;
                  Turbo.airref = Turbo.eair;
                  Turbo.epref = Turbo.epr;
                  Turbo.etref = Turbo.etr;
                  Turbo.faref = Turbo.fa;
                  Turbo.wtref = Turbo.weight;
                  Turbo.wfref = Turbo.fnlb / Turbo.weight;
                 setPanl() ;
       
                 out.box.loadOut () ;
                 out.vars.loadOut () ;
              }
              else {
                 solve.setDefaults() ;
                 setUnits() ;
                 con.setPanl() ;
                 layin.show(in, "first")  ;
                 layout.show(out, "first")  ;
                 con.up.engch.select(0) ;
                 con.up.modch.select(0) ;
                 con.up.pltch.select(1) ;
                 con.up.untch.select(0) ;
                 in.flight.left.o1.setBackground(Color.black) ;
                 in.flight.left.o1.setForeground(Color.yellow) ;
                 in.flight.left.o2.setBackground(Color.black) ;
                 in.flight.left.o2.setForeground(Color.yellow) ;
                 in.flight.left.o3.setBackground(Color.black) ;
                 in.flight.left.o3.setForeground(Color.yellow) ;
                 in.flight.left.f1.setBackground(Color.white) ;
                 in.flight.left.f1.setForeground(Color.black) ;
                 in.flight.left.f2.setBackground(Color.white) ;
                 in.flight.left.f2.setForeground(Color.black) ;
                 in.size.right.sizch.select(0) ;
                 in.size.left.f1.setBackground(Color.white) ;
                 in.size.left.f1.setForeground(Color.black) ;
                 in.size.left.f3.setBackground(Color.black) ;
                 in.size.left.f3.setForeground(Color.yellow) ;
                 in.comp.right.stgch.select(0) ;
                 in.comp.left.f3.setBackground(Color.black) ;
                 in.comp.left.f3.setForeground(Color.yellow) ;
                 in.turb.right.stgch.select(0) ;
                 in.turb.left.f3.setBackground(Color.black) ;
                 in.turb.left.f3.setForeground(Color.yellow) ;
                 in.inlet.right.imat.select(Turbo.minlt) ;
                 in.fan.right.fmat.select(Turbo.mfan) ;
                 in.comp.right.cmat.select(Turbo.mcomp) ;
                 in.burn.right.bmat.select(Turbo.mburner) ;
                 in.turb.right.tmat.select(Turbo.mturbin) ;
                 in.nozl.right.nmat.select(Turbo.mnozl) ;
                 in.nozr.right.nrmat.select(Turbo.mnozr) ;
                 solve.comPute() ;
                 out.plot.repaint() ;
              }
           }
           if(label.equals("Print Data")) {
              if (iprint == 1) {  // file open - print data
                  Turbo.prnt.println("----------------------------------------- ");
                  Turbo.prnt.println(" ") ;
                 seng = "Simple Turbojet";
                 if (entype == 1) {
                     seng = "Turbojet with Afterburner ";
                 }
                 if (entype == 2) {
                     seng = "Turbofan";
                 }
                 if (entype == 3) {
                     seng = "Ramjet";
                 }
                  Turbo.prnt.println(seng);
                 if (entype == 2) {
                     Turbo.prnt.println("  Bypass Ratio  = " + String.valueOf(filter3(Turbo.byprat)));
                 }
                 if (entype == 1) {
                    if (abflag == 0) {
                        Turbo.prnt.println("  Afterburner  OFF ");
                    }
                    if (abflag == 1) {
                        Turbo.prnt.println("  Afterburner  ON ");
                    }
                 }
                 if (lunits == 0) {
                     Turbo.prnt.println("  Diameter  = " + String.valueOf(filter3(Turbo.diameng)) + " ft ") ;
                     Turbo.prnt.println("  Estimated Weight  = " + String.valueOf(filter3(Turbo.weight)) + " lbs ") ;
                 }
                 if (lunits == 1) {
                     Turbo.prnt.println("  Diameter  = " + String.valueOf(filter3(Turbo.diameng)) + " m ") ;
                     Turbo.prnt.println("  Estimated Weight  = " + String.valueOf(filter3(Turbo.weight * Turbo.fconv)) + " N ") ;
                 }
                 if (gamopt == 1) {
                     sgamop = "  -  Gamma and Cp = f(Temp)";
                 } else {
                     sgamop = "  -  Constant Gamma and Cp)";
                 }
                 if (inflag == 0) {
                     smode = "  Design Mode";
                 } else {
                     smode = "  Test Mode";
                 }
                  Turbo.prnt.println(smode + sgamop);
                 if(pall == 1 || pfs == 1) {
                     Turbo.prnt.println(" ") ;
                     Turbo.prnt.println("Flight Conditions: ");
                    if (lunits == 0) {
                        Turbo.prnt.println("  Mach = " + String.valueOf(filter3(Turbo.fsmach))
                                           + ",  V0 = " + String.valueOf(filter0(Turbo.u0d)) + " mph ");
                        Turbo.prnt.println("  Alt = " + String.valueOf(filter0(Turbo.altd)) + " ft ");
                        Turbo.prnt.println("  p0 = " + String.valueOf(filter3(Turbo.ps0))
                                           + ",  pt0 = " + String.valueOf(filter3(Turbo.pt[0])) + " psi");
                        Turbo.prnt.println("  T0 = " + String.valueOf(filter0(Turbo.ts0))
                                           + ",  Tt0 = " + String.valueOf(filter0(Turbo.tt[0])) + " R ");
                    }
                    if (lunits == 1) {
                        Turbo.prnt.println("  Mach = " + String.valueOf(filter3(Turbo.fsmach))
                                           + ",  V0 = " + String.valueOf(filter0(Turbo.u0d)) + " km/h ");
                        Turbo.prnt.println("  Alt = " + String.valueOf(filter0(Turbo.altd)) + " m ");
                        Turbo.prnt.println("  p0 = " + String.valueOf(filter3(Turbo.ps0 * Turbo.pconv))
                                           + ",  pt0 = " + String.valueOf(filter3(Turbo.pt[0] * Turbo.pconv)) + " k Pa");
                        Turbo.prnt.println("  T0 = " + String.valueOf(filter0(Turbo.ts0 * Turbo.tconv))
                                           + ",  Tt0 = " + String.valueOf(filter0(Turbo.tt[0] * Turbo.tconv)) + " K ");
                    }
                 }
                 if(pall == 1 || peng == 1 || pth == 1) {
                     Turbo.prnt.println(" ") ;
                     Turbo.prnt.println("Engine Thrust and Fuel Flow: ");
                    if (lunits == 0) {
                        Turbo.prnt.println(" F gross  = " + String.valueOf(filter0(Turbo.fglb))
                                           + ",  D ram = " + String.valueOf(filter0(Turbo.drlb))
                                           + ",  F net = " + String.valueOf(filter0(Turbo.fnlb)) + "  lbs");
                        Turbo.prnt.println(" Fuel Flow = " + String.valueOf(filter0(Turbo.fuelrat)) + " lbm/hr"
                                           + ",  TSFC = " + String.valueOf(filter3(Turbo.sfc)) + " lbm/(lbs*hr)");
                        Turbo.prnt.println(" Thrust/Weight = " + String.valueOf(filter3(Turbo.fnlb / Turbo.weight)));
                    }
                    if (lunits == 1) {
                        Turbo.prnt.println(" F gross  = " + String.valueOf(filter0(Turbo.fglb * Turbo.fconv))
                                           + ",  D ram = " + String.valueOf(filter0(Turbo.drlb * Turbo.fconv))
                                           + ",  F net = " + String.valueOf(filter0(Turbo.fnlb * Turbo.fconv)) + " N ");
                        Turbo.prnt.println(" Fuel Flow = " + String.valueOf(filter0(Turbo.fuelrat * Turbo.mconv1)) + " kg/hr"
                                           + ",  TSFC = " + String.valueOf(filter3(Turbo.sfc * Turbo.mconv1 / Turbo.fconv)) + " kg/(N*hr)");
                        Turbo.prnt.println(" Thrust/Weight = " + String.valueOf(filter3(Turbo.fnlb / Turbo.weight)));
                    }
                 }
                 if(pall == 1 || peng == 1) {
                     Turbo.prnt.println(" ") ;
                     Turbo.prnt.println("Engine Performance :") ;
                    if (lunits == 0) {
                        Turbo.prnt.println(" Throttle  = " + String.valueOf(filter3(Turbo.throtl)) + " %"
                                           + ",  core airflow (m)  = " + String.valueOf(filter3(Turbo.eair)) + " lbm/sec" ) ;
                        Turbo.prnt.println(" EPR  = " + String.valueOf(filter3(Turbo.epr))
                                           + ",  ETR  = " + String.valueOf(filter3(Turbo.etr))
                                           + ",  fuel/air  = " + String.valueOf(filter3(Turbo.fa))) ;
                        Turbo.prnt.println(" Nozzle Pressure Ratio  = " + String.valueOf(filter3(Turbo.npr))
                                           + ",  Vexit  = " + String.valueOf(filter0(Turbo.uexit)) + " fps ") ;
                        Turbo.prnt.println(" Fg/m  = " + String.valueOf(filter3(Turbo.fgros))
                                           + ",  Dram/m  = " + String.valueOf(filter3(Turbo.dram))
                                           + ",  Fn/m  = " + String.valueOf(filter3(Turbo.fnet)) + " lbs/(lbm/sec)" );
                    }
                    if (lunits == 1) {
                        Turbo.prnt.println(" Throttle  = " + String.valueOf(filter3(Turbo.throtl)) + " %"
                                           + ",  core airflow (m)  = " + String.valueOf(filter3(Turbo.mconv1 * Turbo.eair)) + " kg/sec" ) ;
                        Turbo.prnt.println(" EPR  = " + String.valueOf(filter3(Turbo.epr))
                                           + ",  ETR  = " + String.valueOf(filter3(Turbo.etr))
                                           + ",  fuel/air  = " + String.valueOf(filter3(Turbo.fa))) ;
                        Turbo.prnt.println(" Nozzle Pressure Ratio  = " + String.valueOf(filter3(Turbo.npr))
                                           + ",  Vexit  = " + String.valueOf(filter0(Turbo.lconv1 * Turbo.uexit)) + " m/s ") ;
                        Turbo.prnt.println(" Fg/m  = " + String.valueOf(filter3(Turbo.fgros * Turbo.fconv / Turbo.mconv1))
                                           + ",  Dram/m  = " + String.valueOf(filter3(Turbo.dram * Turbo.fconv / Turbo.mconv1))
                                           + ",  Fn/m  = " + String.valueOf(filter3(Turbo.fnet * Turbo.fconv / Turbo.mconv1)) + " N/(kg/sec)" );
                    }
                 }
                 if(pall ==1 || peta ==1 || pprat==1 || ppres ==1 || pvol ==1 ||
                    ptrat==1 || pttot==1 || pentr==1 || pgam  ==1 || parea  ==1) {
                     Turbo.prnt.println(" ") ;
                     Turbo.prnt.println("Component Performance :") ;
                     Turbo.prnt.println("   Variable \tInlet \tFan \tComp \tBurn \tH-Tur \tL-Tur \tNoz \tExhst");
                 }
                 if(pall ==1 || peta == 1) {
                     Turbo.prnt.println(" Efficiency"
                                        + "\t" + String.valueOf(filter3(Turbo.eta[2]))
                                        + "\t" + String.valueOf(filter3(Turbo.eta[13]))
                                        + "\t" + String.valueOf(filter3(Turbo.eta[3]))
                                        + "\t" + String.valueOf(filter3(Turbo.eta[4]))
                                        + "\t" + String.valueOf(filter3(Turbo.eta[5]))
                                        + "\t" + String.valueOf(filter3(Turbo.eta[5]))
                                        + "\t" + String.valueOf(filter3(Turbo.eta[7])) ) ;
                 }
                 if(pall ==1 || pprat == 1) {
                   if (entype <= 1 ) {
                       Turbo.prnt.println(" Press Rat "
                                          + "\t" + String.valueOf(filter3(Turbo.prat[2]))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.prat[3]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[4]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[5]))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.prat[7])) ) ;
                   }
                   if (entype == 2 ) {
                       Turbo.prnt.println(" Press Rat "
                                          + "\t" + String.valueOf(filter3(Turbo.prat[2]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[13]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[3]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[4]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[5]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[15]))
                                          + "\t" + String.valueOf(filter3(Turbo.prat[7])) ) ;
                   }
                   if (entype == 3 ) {
                       Turbo.prnt.println(" Press Rat "
                                          + "\t" + String.valueOf(filter3(Turbo.prat[2]))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.prat[4]))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.prat[7])) ) ;
                   }
                 }
                 if(pall ==1 || ppres == 1) {
                   if (entype <= 1 ) {
                       Turbo.prnt.println(" Press - p"
                                          + "\t" + String.valueOf(filter3(Turbo.pt[2] * Turbo.pconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.pt[3] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[4] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[5] * Turbo.pconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.pt[7] * Turbo.pconv)) ) ;
                   }
                   if (entype == 2 ) {
                       Turbo.prnt.println(" Press - p"
                                          + "\t" + String.valueOf(filter3(Turbo.pt[2] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[13] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[3] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[4] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[5] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[15] * Turbo.pconv))
                                          + "\t" + String.valueOf(filter3(Turbo.pt[7] * Turbo.pconv)) ) ;
                   }
                   if (entype == 3 ) {
                       Turbo.prnt.println(" Press - p"
                                          + "\t" + String.valueOf(filter3(Turbo.pt[2] * Turbo.pconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.pt[4] * Turbo.pconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.pt[7] * Turbo.pconv)) ) ;
                   }
                 }
                 if(pall ==1 || pvol == 1) {
                   if (entype <= 1 ) {
                       Turbo.prnt.println(" Spec Vol - v"
                                          + "\t" + String.valueOf(filter3(Turbo.v[2] * Turbo.dconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.v[3] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[4] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[5] * Turbo.dconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.v[7] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[8] * Turbo.dconv)) ) ;
                   }
                   if (entype == 2 ) {
                       Turbo.prnt.println(" Spec Vol - v"
                                          + "\t" + String.valueOf(filter3(Turbo.v[2] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[13] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[3] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[4] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[5] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[15] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[7] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[8] * Turbo.dconv)) ) ;
                   }
                   if (entype == 3 ) {
                       Turbo.prnt.println(" Spec Vol - v"
                                          + "\t" + String.valueOf(filter3(Turbo.v[2] * Turbo.dconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.v[4] * Turbo.dconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.v[7] * Turbo.dconv))
                                          + "\t" + String.valueOf(filter3(Turbo.v[8] * Turbo.dconv)) ) ;
                   }
                 }
                 if(pall ==1 || ptrat == 1) {
                   if (entype <= 1 ) {
                       Turbo.prnt.println(" Temp Rat"
                                          + "\t" + String.valueOf(filter3(Turbo.trat[2]))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.trat[3]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[4]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[5]))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.trat[7])) ) ;
                   }
                   if (entype == 2 ) {
                       Turbo.prnt.println(" Temp Rat"
                                          + "\t" + String.valueOf(filter3(Turbo.trat[2]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[13]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[3]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[4]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[5]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[15]))
                                          + "\t" + String.valueOf(filter3(Turbo.trat[7])) ) ;
                   }
                   if (entype == 3 ) {
                       Turbo.prnt.println(" Temp Rat"
                                          + "\t" + String.valueOf(filter3(Turbo.trat[2]))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.trat[4]))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.trat[7])) ) ;
                   }
                 }
                 if(pall ==1 || pttot == 1) {
                   if (entype <= 1 ) {
                       Turbo.prnt.println(" Temp - T"
                                          + "\t" + String.valueOf(filter0(Turbo.tt[2] * Turbo.tconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter0(Turbo.tt[3] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[4] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[5] * Turbo.tconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter0(Turbo.tt[7] * Turbo.tconv)) ) ;
                   }
                   if (entype == 2 ) {
                       Turbo.prnt.println(" Temp - T"
                                          + "\t" + String.valueOf(filter0(Turbo.tt[2] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[13] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[3] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[4] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[5] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[15] * Turbo.tconv))
                                          + "\t" + String.valueOf(filter0(Turbo.tt[7] * Turbo.tconv)) ) ;
                   }
                   if (entype == 3 ) {
                       Turbo.prnt.println(" Temp - T"
                                          + "\t" + String.valueOf(filter0(Turbo.tt[2] * Turbo.tconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter0(Turbo.tt[4] * Turbo.tconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter0(Turbo.tt[7] * Turbo.tconv)) ) ;
                   }
                 }
                 if(pall ==1 || pentr == 1) {
                   if (entype <= 1 ) {
                       Turbo.prnt.println(" Entropy - s "
                                          + "\t" + String.valueOf(filter3(Turbo.s[2] * Turbo.bconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.s[3] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[4] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[5] * Turbo.bconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.s[7] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[8] * Turbo.bconv)) ) ;
                   }
                   if (entype == 2 ) {
                       Turbo.prnt.println(" Entropy   "
                                          + "\t" + String.valueOf(filter3(Turbo.s[2] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[13] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[3] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[4] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[5] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[15] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[7] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[8] * Turbo.bconv)) ) ;
                   }
                   if (entype == 3 ) {
                       Turbo.prnt.println(" Entropy   "
                                          + "\t" + String.valueOf(filter3(Turbo.s[2] * Turbo.bconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.s[4] * Turbo.bconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.s[7] * Turbo.bconv))
                                          + "\t" + String.valueOf(filter3(Turbo.s[8] * Turbo.bconv)) ) ;
                   }
                 }
                 if(pall ==1 || pgam == 1) {
                     Turbo.prnt.println(" Gamma     "
                                        + "\t" + String.valueOf(filter3(Turbo.gam[2]))
                                        + "\t" + String.valueOf(filter3(Turbo.gam[13]))
                                        + "\t" + String.valueOf(filter3(Turbo.gam[3]))
                                        + "\t" + String.valueOf(filter3(Turbo.gam[4]))
                                        + "\t" + String.valueOf(filter3(Turbo.gam[5]))
                                        + "\t" + String.valueOf(filter3(Turbo.gam[5]))
                                        + "\t" + String.valueOf(filter3(Turbo.gam[7])) ) ;
                 }
                 if(pall ==1 || parea == 1) {
                   if (entype <= 1 ) {
                       Turbo.prnt.println(" Area - A"
                                          + "\t" + String.valueOf(filter3(Turbo.ac * Turbo.aconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.acore * Turbo.aconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.a4 * Turbo.aconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.a8 * Turbo.aconv)) );
                   }
                   if (entype == 2 ) {
                       Turbo.prnt.println(" Area - A"
                                          + "\t" + String.valueOf(filter3(Turbo.ac * Turbo.aconv))
                                          + "\t" + String.valueOf(filter3(Turbo.afan * Turbo.aconv))
                                          + "\t" + String.valueOf(filter3(Turbo.acore * Turbo.aconv))
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.a4 * Turbo.aconv))
                                          + "\t" + String.valueOf(filter3(Turbo.a4p * Turbo.aconv))
                                          + "\t" + String.valueOf(filter3(Turbo.a8 * Turbo.aconv)) );
                   }
                   if (entype == 3 ) {
                       Turbo.prnt.println(" Area - A"
                                          + "\t" + String.valueOf(filter3(Turbo.ac * Turbo.aconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.acore * Turbo.aconv))
                                          + "\t" + " - "
                                          + "\t" + " - "
                                          + "\t" + String.valueOf(filter3(Turbo.a8 * Turbo.aconv)) ) ;
                   }
                 }
                 if(pall ==1 || peta ==1 || pprat==1 || ppres ==1 || pvol ==1 ||
                    ptrat==1 || pttot==1 || pentr==1 || pgam  ==1 || parea  ==1) {
                    if (lunits == 0) {
                        Turbo.prnt.println(" p = psi,  v = ft3/lbm,  T = R,  s = BTU/lbm R,  A = ft2 ") ;
                    }
                    if (lunits == 1) {
                        Turbo.prnt.println(" p = kPa,  v = m3/kg,  T = K,   s = kJ/kg K,   A = m2 ") ;
                    }
                 }
              }
              if (iprint == 0) {  // file closed
                 return;
              }
           }

           if(label.equals("Exit")) {
               Turbo.f.dispose() ;
              System.exit(1) ;
           }
       }

       public void handleProb(Event evt, Object obj) {
        String label = (String)obj ;
 
        if (plttyp != 7) {
    // units change
          if(label.equals("English")) {
             lunits = 0 ;
             setUnits () ;
             setPanl() ;
             if (plttyp >=3) {
                 setPlot();
             }
          }
          if(label.equals("Metric")) {
             lunits = 1 ;
             setUnits () ;
             setPanl() ;
             if (plttyp >=3) {
                 setPlot();
             }
          }
          if(label.equals("% Change")) {
             lunits = 2 ;
             setUnits () ;
             setPanl() ;
             if (plttyp >=3) {
                 setPlot();
             }
          }
 // mode
          if(label.equals("Design")) {
             inflag = 0 ;
          }
          if(label.equals("Test")) {
             inflag = 1 ;
             lunits = 0 ;
             setUnits () ;
             untch.select(lunits) ;
             solve.comPute() ;
             solve.myDesign() ;
              Turbo.ytrans = 115.0 ;
             view.start() ;
             varflag = 0 ;
             layin.show(in, "first")  ;
          }

          if(label.equals("My Design")) {
             varflag = 0 ;
             layin.show(in, "first")  ;
             lunits = 0 ;
             setUnits () ;
             untch.select(lunits) ;
             solve.loadMine() ;
          }
          if(label.equals("J85 Model")) {
             varflag = 0 ;
             layin.show(in, "first")  ;
             lunits = 0 ;
             setUnits () ;
             untch.select(lunits) ;
             solve.loadJ85() ;
          }
          if(label.equals("F100 Model")) {
             varflag = 0 ;
             layin.show(in, "first")  ;
             lunits = 0 ;
             setUnits () ;
             untch.select(lunits) ;
             solve.loadF100() ;
          }
          if(label.equals("CF6 Model")) {
             varflag = 0 ;
             layin.show(in, "first")  ;
             lunits = 0 ;
             setUnits () ;
             untch.select(lunits) ;
             solve.loadCF6() ;
          }
          if(label.equals("Ramjet Model")) {
             varflag = 0 ;
             layin.show(in, "first")  ;
             lunits = 0 ;
             setUnits () ;
             untch.select(lunits) ;
             solve.loadRamj() ;
          }
  
          if(label.equals("Engine Performance")) {
              plttyp = 0 ;
              layout.show(out, "first")  ;
              showcom = 0 ;
          }
          if(label.equals("Component Performance")) {
              plttyp = 0 ;
              layout.show(out, "third")  ;
              showcom = 1 ;
          }
          if(label.equals("Graphs")) {
              plttyp = 3 ;
              showcom = 0 ;
              layout.show(out, "second")  ;
              setPlot () ;
              out.plot.repaint() ;
          }
          solve.comPute() ; 
         }
       }
     }

     class Down extends Panel {
        TextField o4, o5, o6, o10, o11, o12, o14, o15 ;

        Down () { 
            setLayout(new GridLayout(4,4,1,5)) ;

            o4 = new TextField() ;
            o4.setBackground(Color.black) ;
            o4.setForeground(Color.yellow) ;
            o5 = new TextField() ;
            o5.setBackground(Color.black) ;
            o5.setForeground(Color.yellow) ;
            o6 = new TextField() ;
            o6.setBackground(Color.black) ;
            o6.setForeground(Color.yellow) ;
            o10 = new TextField() ;
            o10.setBackground(Color.black) ;
            o10.setForeground(Color.yellow) ;
            o11 = new TextField() ;
            o11.setBackground(Color.black) ;
            o11.setForeground(Color.yellow) ;
            o12 = new TextField() ;
            o12.setBackground(Color.black) ;
            o12.setForeground(Color.yellow) ;
            o14 = new TextField() ;
            o14.setBackground(Color.black) ;
            o14.setForeground(Color.yellow) ;
            o15 = new TextField() ;
            o15.setBackground(Color.black) ;
            o15.setForeground(Color.yellow) ;

            add(new Label("Net Thrust", Label.CENTER)) ;
            add(o4) ;
            add(new Label("Fuel Flow", Label.CENTER)) ;
            add(o5) ;

            add(new Label("Gross Thrust", Label.CENTER)) ;
            add(o14) ;
            add(new Label("TSFC", Label.CENTER)) ;
            add(o6) ;

            add(new Label("Ram Drag", Label.CENTER)) ;
            add(o15) ;
            add(new Label("Core Airflow", Label.CENTER)) ;
            add(o10) ;

            add(new Label("Fnet / W ", Label.CENTER)) ;
            add(o12) ;
            add(new Label("Weight", Label.CENTER)) ;
            add(o11) ;
        }

     }  // end Down

     public void setPanl() {
        double v1,v2,v3,v4 ;
        float fl1, fl2, fl3, fl4 ;
        int i1,i2,i3,i4 ;

// set limits and labels
   // flight conditions
        v1 = 0.0 ;
         Turbo.vmn1 = -10.0 ;
         Turbo.vmx1 = 10.0 ;
        v2 = 0.0 ;
         Turbo.vmn2 = -10.0 ;
         Turbo.vmx2 = 10.0 ;
        v3 = 0.0 ;
         Turbo.vmn3 = -10.0 ;
         Turbo.vmx3 = 10.0 ;
        v4 = Turbo.gama;

        if (lunits <= 1) {
           v1 = Turbo.u0d;
            Turbo.vmn1 = Turbo.u0min;
            Turbo.vmx1 = Turbo.u0max;
           v2 = Turbo.altd;
            Turbo.vmn2 = Turbo.altmin;
            Turbo.vmx2 = Turbo.altmax;
           v3 = Turbo.throtl;
            Turbo.vmn3 = Turbo.thrmin;
            Turbo.vmx3 = Turbo.thrmax;
        }

        in.flight.left.f1.setText(String.valueOf(filter0(v1))) ;
        in.flight.left.f2.setText(String.valueOf(filter0(v2))) ;
        in.flight.left.f3.setText(String.valueOf(filter3(v3))) ;
        in.flight.left.f4.setText(String.valueOf(filter3(v4))) ;

        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
        i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
        i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;

        in.flight.right.s1.setValue(i1) ;
        in.flight.right.s2.setValue(i2) ;
        in.flight.right.s3.setValue(i3) ;

        in.flight.right.inptch.select(inptype) ;
        in.flight.right.nozch.select(abflag) ;
        in.flight.left.inpch.select(gamopt) ;

   // size
        v1 = 0.0 ;
         Turbo.vmn1 = -10.0 ;
         Turbo.vmx1 = 10.0 ;
         Turbo.vmn3 = -10.0 ;
         Turbo.vmx3 = 10.0 ;
        if (lunits <= 1) {
           v1 = Turbo.a2d;
            Turbo.vmn1 = Turbo.a2min;
            Turbo.vmx1 = Turbo.a2max;
           v3 = Turbo.diameng;
        }
        fl1 = filter3(v1) ;
        fl3 = filter3(v3) ;
        in.size.left.f1.setText(String.valueOf(fl1)) ;
        in.size.left.f3.setText(String.valueOf(fl3)) ;

        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;

        in.size.right.sizch.select(siztype) ;
        in.size.right.s1.setValue(i1) ;

        in.size.left.f2.setText(String.valueOf(filter0(Turbo.weight))) ;
        in.size.right.chmat.select(wtflag) ;

   // inlet
        if (pt2flag == 0) {             /*     mil spec      */
          if (Turbo.fsmach > 1.0 ) {          /* supersonic */
              Turbo.eta[2] = 1.0 - .075 * Math.pow(Turbo.fsmach - 1.0, 1.35) ;
          }
          else {
              Turbo.eta[2] = 1.0 ;
          }
        }

        v1 = Turbo.eta[2] ;
         Turbo.vmn1 = Turbo.etmin;
         Turbo.vmx1 = Turbo.etmax;

        if (lunits == 2) {
          v1 = 0.0 ;
            Turbo.vmx1 = 100.0 - 100.0 * Turbo.et2ref;
            Turbo.vmn1 = Turbo.vmx1 - 20.0 ;
        }
        fl1 = filter3(v1) ;
        in.inlet.left.f1.setText(String.valueOf(fl1)) ;
        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
        in.inlet.right.s1.setValue(i1) ;
          // materials
        in.inlet.right.imat.select(Turbo.minlt) ;
        in.inlet.left.di.setText(String.valueOf(filter0(Turbo.dinlt))) ;
        in.inlet.left.ti.setText(String.valueOf(filter0(Turbo.tinlt))) ;
  //  fan
        v1 = Turbo.p3fp2d;
         Turbo.vmn1 = Turbo.fprmin;
         Turbo.vmx1 = Turbo.fprmax;
        v2 = Turbo.eta[13] ;
         Turbo.vmn2 = Turbo.etmin;
         Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.byprat;
         Turbo.vmn3 = Turbo.bypmin;
         Turbo.vmx3 = Turbo.bypmax;

        if (lunits == 2) {
          v1 = 0.0 ;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0 ;
          v2 = 0.0 ;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et13ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
          v3 = 0.0 ;
            Turbo.vmn3 = -10.0;
            Turbo.vmx3 = 10.0 ;
        }
        fl1 = (float) v1 ;
        fl2 = (float) v2 ;
        fl3 = (float) v3 ;

        in.fan.left.f1.setText(String.valueOf(fl1)) ;
        in.fan.left.f2.setText(String.valueOf(fl2)) ;
        in.fan.left.f3.setText(String.valueOf(fl3)) ;

        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
        i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
        i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;

        in.fan.right.s1.setValue(i1) ;
        in.fan.right.s2.setValue(i2) ;
        in.fan.right.s3.setValue(i3) ;

          // materials
        in.fan.right.fmat.select(Turbo.mfan) ;
        in.fan.left.df.setText(String.valueOf(filter0(Turbo.dfan))) ;
        in.fan.left.tf.setText(String.valueOf(filter0(Turbo.tfan))) ;
  // compressor 
        v1 = Turbo.p3p2d;
         Turbo.vmn1 = Turbo.cprmin;
         Turbo.vmx1 = Turbo.cprmax;
        v2 = Turbo.eta[3] ;
         Turbo.vmn2 = Turbo.etmin;
         Turbo.vmx2 = Turbo.etmax;

        if (lunits == 2) {
          v1 = 0.0 ;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0 ;
          v2 = 0.0 ;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et3ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
        }
        fl1 = (float) v1 ;
        fl2 = (float) v2 ;

        in.comp.left.f1.setText(String.valueOf(fl1)) ;
        in.comp.left.f2.setText(String.valueOf(fl2)) ;

        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
        i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;

        in.comp.right.s1.setValue(i1) ;
        in.comp.right.s2.setValue(i2) ;
          // materials
        in.comp.right.cmat.select(Turbo.mcomp) ;
        in.comp.left.dc.setText(String.valueOf(filter0(Turbo.dcomp))) ;
        in.comp.left.tc.setText(String.valueOf(filter0(Turbo.tcomp))) ;
  //  burner
        v1 = Turbo.tt4d;
         Turbo.vmn1 = Turbo.t4min;
         Turbo.vmx1 = Turbo.t4max;
        v2 = Turbo.eta[4] ;
         Turbo.vmn2 = Turbo.etmin;
         Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.prat[4] ;
         Turbo.vmn3 = Turbo.etmin;
         Turbo.vmx3 = Turbo.pt4max;
        v4 = Turbo.fhvd;

        if (lunits == 2) {
          v1 = 0.0 ;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0 ;
          v2 = 0.0 ;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et4ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
          v3 = 0.0 ;
            Turbo.vmx3 = 100.0 - 100.0 * Turbo.p4ref;
            Turbo.vmn3 = Turbo.vmx3 - 20.0 ;
        }
        fl1 = (float) v1 ;
        fl2 = (float) v2 ;
        fl3 = (float) v3 ;

        in.burn.left.f1.setText(String.valueOf(filter0(v1))) ;
        in.burn.left.f2.setText(String.valueOf(fl2)) ;
        in.burn.left.f3.setText(String.valueOf(fl3)) ;
        in.burn.left.f4.setText(String.valueOf(filter0(v4))) ;

        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
        i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
        i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;

        in.burn.right.s1.setValue(i1) ;
        in.burn.right.s2.setValue(i2) ;
        in.burn.right.s3.setValue(i3) ;
        in.burn.right.fuelch.select(fueltype) ;
          // materials
        in.burn.right.bmat.select(Turbo.mburner) ;
        in.burn.left.db.setText(String.valueOf(filter0(Turbo.dburner))) ;
        in.burn.left.tb.setText(String.valueOf(filter0(Turbo.tburner))) ;
  //  turbine
        v1 = Turbo.eta[5] ;
         Turbo.vmn1 = Turbo.etmin;
         Turbo.vmx1 = Turbo.etmax;
        if (lunits == 2) {
          v1 = 0.0 ;
            Turbo.vmx1 = 100.0 - 100.0 * Turbo.et5ref;
            Turbo.vmn1 = Turbo.vmx1 - 20.0 ;
        }
        fl1 = (float) v1 ;
        in.turb.left.f1.setText(String.valueOf(fl1)) ;
        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
        in.turb.right.s1.setValue(i1) ;
          // materials
        in.turb.right.tmat.select(Turbo.mturbin) ;
        in.turb.left.dt.setText(String.valueOf(filter0(Turbo.dturbin))) ;
        in.turb.left.tt.setText(String.valueOf(filter0(Turbo.tturbin))) ;
  //  turbine nozzle 
        v1 = Turbo.tt7d;
         Turbo.vmn1 = Turbo.t7min;
         Turbo.vmx1 = Turbo.t7max;
        v2 = Turbo.eta[7] ;
         Turbo.vmn2 = Turbo.etmin;
         Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.a8rat;
         Turbo.vmn3 = Turbo.a8min;
         Turbo.vmx3 = Turbo.a8max;

        if (lunits == 2) {
          v1 = 0.0 ;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0 ;
          v2 = 0.0 ;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
          v3 = 0.0 ;
            Turbo.vmn3 = -10.0;
            Turbo.vmx3 = 10.0 ;
        }
        fl1 = filter0(v1) ;
        fl2 = filter3(v2) ;
        fl3 = filter3(v3) ;

        in.nozl.left.f1.setText(String.valueOf(fl1)) ;
        in.nozl.left.f2.setText(String.valueOf(fl2)) ;
        in.nozl.left.f3.setText(String.valueOf(fl3)) ;

        i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
        i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
        i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;

        in.nozl.right.s1.setValue(i1) ;
        in.nozl.right.s2.setValue(i2) ;
        in.nozl.right.s3.setValue(i3) ;
        in.nozl.right.arch.select(arsched) ;
          // materials
        in.nozl.right.nmat.select(Turbo.mnozl) ;
        in.nozl.left.dn.setText(String.valueOf(filter0(Turbo.dnozl))) ;
        in.nozl.left.tn.setText(String.valueOf(filter0(Turbo.tnozl))) ;
  //  ramjet nozzle 
        v2 = Turbo.eta[7] ;
         Turbo.vmn2 = Turbo.etmin;
         Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.arthd;
         Turbo.vmn3 = Turbo.arthmn;
         Turbo.vmx3 = Turbo.arthmx;
        v4 = Turbo.arexitd;
         Turbo.vmn4 = Turbo.arexmn;
         Turbo.vmx4 = Turbo.arexmx;

        if (lunits == 2) {
          v2 = 0.0 ;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
          v3 = 0.0 ;
            Turbo.vmn3 = -10.0;
            Turbo.vmx3 = 10.0 ;
          v4 = 0.0 ;
            Turbo.vmn4 = -10.0;
            Turbo.vmx4 = 10.0 ;
        }
        fl2 = filter3(v2) ;
        fl3 = filter3(v3) ;
        fl4 = filter3(v4) ;

        in.nozr.left.f2.setText(String.valueOf(fl2)) ;
        in.nozr.left.f3.setText(String.valueOf(fl3)) ;
        in.nozr.left.f4.setText(String.valueOf(fl4)) ;

        i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
        i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;
        i4 = (int) (((v4 - Turbo.vmn4) / (Turbo.vmx4 - Turbo.vmn4)) * 1000.) ;

        in.nozr.right.s2.setValue(i2) ;
        in.nozr.right.s3.setValue(i3) ;
        in.nozr.right.s4.setValue(i4) ;
          // materials
        in.nozr.right.nrmat.select(Turbo.mnozr) ;
        in.nozr.left.dn.setText(String.valueOf(filter0(Turbo.dnozr))) ;
        in.nozr.left.tn.setText(String.valueOf(filter0(Turbo.tnozr))) ;
 
  //  variable limits
        v1 = Turbo.u0max;
        v2 = Turbo.altmax;
        v3 = Turbo.a2min;
        v4 = Turbo.a2max;

        fl1 = filter0(v1) ;
        fl2 = filter0(v2) ;
        fl3 = filter5(v3) ;
        fl4 = filter0(v4) ;

        in.limt.f1.setText(String.valueOf(fl1)) ;
        in.limt.f2.setText(String.valueOf(fl2)) ;
        in.limt.f3.setText(String.valueOf(fl3)) ;
        in.limt.f4.setText(String.valueOf(fl4)) ;

        v1 = Turbo.cprmax;
        v2 = Turbo.t4max;
        v3 = Turbo.t7max;

        fl1 = (float) v1 ;
        fl2 = filter0(v2) ;
        fl3 = filter0(v3) ;

        in.limt.f5.setText(String.valueOf(fl1)) ;
        in.limt.f6.setText(String.valueOf(fl2)) ;
        in.limt.f7.setText(String.valueOf(fl3)) ;

        v1 = Turbo.fprmax;
        v2 = Turbo.bypmax;
        v3 = Turbo.pt4max;

        fl1 = (float) v1 ;
        fl2 = (float) v2 ;
        fl3 = (float) v3 ;

        in.limt.f9.setText(String.valueOf(fl1)) ;
        in.limt.f10.setText(String.valueOf(fl2)) ;
        in.limt.f11.setText(String.valueOf(fl3)) ;

        return ;
     }

     public void setPlot() {   // Plot Scales

        showcom = 1 ;

        switch (plttyp) {
           case 3: {                              // press variation 
             nabs = nord = 1 ;
             ordkeep = abskeep = 1 ;
             lines = 1;
             npt = 9;
               Turbo.laby = String.valueOf("Press");
               Turbo.begy = 0.0;
             if (lunits == 0) {
                 Turbo.labyu = String.valueOf("psi");
                 Turbo.endy = 1000.;
             }
             if (lunits == 1) {
                 Turbo.labyu = String.valueOf("kPa");
                 Turbo.endy = 5000.;
             }
             ntiky=11;
               Turbo.labx = String.valueOf("Station");
               Turbo.labxu = String.valueOf(" ");
               Turbo.begx = 0.0;
               Turbo.endx = 8.0;
             ntikx=9;
             break ;                 
           }
           case 4: {                              // temp variation 
             nabs = nord = 1 ;
             ordkeep = abskeep = 1 ;
             lines = 1;
             npt = 9;
               Turbo.laby = String.valueOf("Temp");
             if (lunits == 0) {
                 Turbo.labyu = String.valueOf("R");
             }
             if (lunits == 1) {
                 Turbo.labyu = String.valueOf("K");
             }
             if (lunits == 2) {
                 Turbo.labyu = String.valueOf("%");
             }
               Turbo.begy = 0.0;
               Turbo.endy = 5000.;
             ntiky=11;
               Turbo.labx = String.valueOf("Station");
               Turbo.labxu = String.valueOf(" ");
               Turbo.begx = 0.0;
               Turbo.endx = 8.0;
             ntikx=9;
             break ;                 
           }
           case 5: {                              //  T - s plot
             nabs = nord = 2 ;
             ordkeep = abskeep = 1 ;
             lines = 1;
             npt = 7;
               Turbo.laby = String.valueOf("Temp");
             if (lunits == 0) {
                 Turbo.labyu = String.valueOf("R");
             }
             if (lunits == 1) {
                 Turbo.labyu = String.valueOf("K");
             }
               Turbo.begy = 0.0;
               Turbo.endy = 5000.;
             ntiky=11;
               Turbo.labx = String.valueOf("s");
             if (lunits == 0) {
                 Turbo.labxu = String.valueOf("Btu/lbm R");
             }
             if (lunits == 1) {
                 Turbo.labxu = String.valueOf("kJ/kg K");
             }
               Turbo.begx = 0.0;
               Turbo.endx = 1.0 * Turbo.bconv;
             ntikx=2;
             break;
           }
           case 6: {                              //  p - v plot
             nord = nabs = 3 ;
             ordkeep = abskeep = 2 ;
             lines = 1;
             npt = 25;
               Turbo.laby = String.valueOf("Press");
               Turbo.begy = 0.0;
             if (lunits == 0) {
                 Turbo.labyu = String.valueOf("psi");
                 Turbo.endy = 1000.;
             }
             if (lunits == 1) {
                 Turbo.labyu = String.valueOf("kPa");
                 Turbo.endy = 5000.;
             }
             ntiky=11;
               Turbo.labx = String.valueOf("v");
             if (lunits == 0) {
                 Turbo.labxu = String.valueOf("ft^3/lb");
             }
             if (lunits == 1) {
                 Turbo.labxu = String.valueOf("m^3/Kg");
             }
               Turbo.begx =0.0;
               Turbo.endx = 100.0 * Turbo.dconv;
             ntikx=2;
             break;
           }
           case 7: {                              //  generate plot 
             nord = nabs = 3 ;
             ordkeep = abskeep = 3 ;
             lines = 0;
             npt = 0;
               Turbo.laby = String.valueOf("Fn");
             if (lunits == 0) {
                 Turbo.labyu = String.valueOf("lb");
             }
             if (lunits == 1) {
                 Turbo.labyu = String.valueOf("N");
             }
               Turbo.begy =0.0;
               Turbo.endy =100000.;
             ntiky=11 ;
               Turbo.labx = String.valueOf("Mach");
               Turbo.labxu = String.valueOf(" ");
             if (entype <=2) {
                 Turbo.begx =0.0;
                 Turbo.endx =2.0;
             }
             if (entype ==3) {
                 Turbo.begx =0.0;
                 Turbo.endx =6.0;
             }
             ntikx=5;
             break;
           }
        }
     }
   
     public void setUnits() {   // Switching Units
       double alts,alm1s,ars,arm1s,arm2s,t4s,t7s,t4m1s,t4m2s,t7m1s,t7m2s ;
       double u0s,pmxs,tmns,tmxs,diars,dim1s,dim2s ;
       double u0mts,u0mrs,altmts,altmrs,fhvs ;
       int i1 ;
   
       alts  = Turbo.altd / Turbo.lconv1;
       alm1s = Turbo.altmin / Turbo.lconv1;
       altmts = Turbo.altmt / Turbo.lconv1;
       altmrs = Turbo.altmr / Turbo.lconv1;
       ars   = Turbo.a2d / Turbo.aconv;
       arm1s = Turbo.a2min / Turbo.aconv;
       arm2s = Turbo.a2max / Turbo.aconv;
       diars = Turbo.diameng / Turbo.lconv1;
       dim1s = Turbo.diamin / Turbo.lconv1;
       dim2s = Turbo.diamax / Turbo.lconv1;
       u0s   = Turbo.u0d / Turbo.lconv2;
       u0mts = Turbo.u0mt / Turbo.lconv2;
       u0mrs = Turbo.u0mr / Turbo.lconv2;
       pmxs  = Turbo.pmax / Turbo.pconv;
       tmns  = Turbo.tmin / Turbo.tconv;
       tmxs  = Turbo.tmax / Turbo.tconv;
       t4s   = Turbo.tt4d / Turbo.tconv;
       t4m1s = Turbo.t4min / Turbo.tconv;
       t4m2s = Turbo.t4max / Turbo.tconv;
       t7s   = Turbo.tt7d / Turbo.tconv;
       t7m1s = Turbo.t7min / Turbo.tconv;
       t7m2s = Turbo.t7max / Turbo.tconv;
       fhvs = Turbo.fhvd / Turbo.flconv;
       switch (lunits) {
          case 0:{                   /* English Units */
              Turbo.lconv1 = 1.0 ;
              Turbo.lconv2 = 1.0 ;
              Turbo.fconv = 1.0 ;
              Turbo.econv = 1.0 ;
              Turbo.mconv1 = 1.0 ;
              Turbo.pconv = 1.0 ;
              Turbo.tconv = 1.0 ;
              Turbo.mconv2 = 1.0 ;
              Turbo.econv2 = 1.0 ;
              Turbo.bconv = Turbo.econv / Turbo.tconv / Turbo.mconv1;
              Turbo.tref = 459.7 ;
                 out.vars.lpa.setText("Pres-psi") ;
                 out.vars.lpb.setText("Pres-psi") ;
                 out.vars.lta.setText("Temp-R") ;
                 out.vars.ltb.setText("Temp-R") ;
                 in.flight.right.l2.setText("lb/sq in") ;
                 in.flight.right.l3.setText("F") ;
                 in.flight.left.l1.setText("Speed-mph") ;
                 in.flight.left.l2.setText("Altitude-ft") ;
                 in.size.left.l2.setText("Weight-lbs") ;
                 in.size.left.l1.setText("Area-sq ft") ;
                 in.size.left.l3.setText("Diameter-ft") ;
                 in.burn.left.l1.setText("Tmax -R") ;
                 in.burn.left.l4.setText("FHV BTU/lb") ;
                 in.nozl.left.l1.setText("Tmax -R") ;
                 in.inlet.right.lmat.setText("lbm/ft^3");
                 in.fan.right.lmat.setText("lbm/ft^3");
                 in.comp.right.lmat.setText("lbm/ft^3");
                 in.burn.right.lmat.setText("lbm/ft^3");
                 in.turb.right.lmat.setText("lbm/ft^3");
                 in.nozl.right.lmat.setText("lbm/ft^3");
                 in.nozr.right.lmat.setText("lbm/ft^3");
                 in.inlet.left.lmat.setText("T lim -R");
                 in.fan.left.lmat.setText("T lim -R");
                 in.comp.left.lmat.setText("T lim -R");
                 in.burn.left.lmat.setText("T lim -R");
                 in.turb.left.lmat.setText("T lim -R");
                 in.nozl.left.lmat.setText("T lim -R");
                 in.nozr.left.lmat.setText("T lim -R");
              Turbo.g0d = 32.2 ;
//                 setref.setVisible(false) ;
                 break ;
          }
          case 1:{                   /* Metric Units */
              Turbo.lconv1 = .3048 ;
              Turbo.lconv2 = 1.609 ;
              Turbo.fconv = 4.448 ;
              Turbo.econv = 1055.;
              Turbo.econv2 = 1.055 ;
              Turbo.mconv1 = .4536 ;
              Turbo.pconv = 6.891 ;
              Turbo.tconv = 0.555555 ;
              Turbo.bconv = Turbo.econv / Turbo.tconv / Turbo.mconv1 / 1000. ;
              Turbo.mconv2 = 14.59;
              Turbo.tref = 273.1 ;
                 out.vars.lpa.setText("Pres-kPa") ;
                 out.vars.lpb.setText("Pres-kPa") ;
                 out.vars.lta.setText("Temp-K") ;
                 out.vars.ltb.setText("Temp-K") ;
                 in.flight.right.l2.setText("k Pa") ;
                 in.flight.right.l3.setText("C") ;
                 in.flight.left.l1.setText("Speed-kmh") ;
                 in.flight.left.l2.setText("Altitude-m") ;
                 in.size.left.l2.setText("Weight-N") ;
                 in.size.left.l1.setText("Area-sq m") ;
                 in.size.left.l3.setText("Diameter-m") ;
                 in.burn.left.l1.setText("Tmax -K") ;
                 in.burn.left.l4.setText("FHV kJ/kg") ;
                 in.nozl.left.l1.setText("Tmax -K") ;
                 in.inlet.right.lmat.setText("kg/m^3");
                 in.fan.right.lmat.setText("kg/m^3");
                 in.comp.right.lmat.setText("kg/m^3");
                 in.burn.right.lmat.setText("kg/m^3");
                 in.turb.right.lmat.setText("kg/m^3");
                 in.nozl.right.lmat.setText("kg/m^3");
                 in.nozr.right.lmat.setText("kg/m^3");
                 in.inlet.left.lmat.setText("T lim -K");
                 in.fan.left.lmat.setText("T lim -K");
                 in.comp.left.lmat.setText("T lim -K");
                 in.burn.left.lmat.setText("T lim -K");
                 in.turb.left.lmat.setText("T lim -K");
                 in.nozl.left.lmat.setText("T lim -K");
                 in.nozr.left.lmat.setText("T lim -K");
              Turbo.g0d = 9.81 ;
//                 setref.setVisible(false) ;
                 break ;
          }
          case 2:{            /* Percent Change .. convert to English */
              Turbo.lconv1 = 1.0 ;
              Turbo.lconv2 = 1.0 ;
              Turbo.fconv = 1.0 ;
              Turbo.econv = 1.0 ;
              Turbo.mconv1 = 1.0 ;
              Turbo.pconv = 1.0 ;
              Turbo.tconv = 1.0 ;
              Turbo.mconv2 = 1.0 ;
              Turbo.tref = 459.7 ;
                 in.flight.right.l2.setText("lb/sq in") ;
                 in.flight.right.l3.setText("F") ;
                 in.flight.left.l1.setText("Speed-%") ;
                 in.flight.left.l2.setText("Altitude-%") ;
                 in.size.left.l2.setText("Weight-lbs") ;
                 in.size.left.l1.setText("Area-%") ;
                 in.burn.left.l1.setText("Tmax -%") ;
                 in.nozl.left.l1.setText("Tmax -%") ;
                 in.inlet.right.lmat.setText("<-lbm/ft^3 -Rankine");
                 in.fan.right.lmat.setText("<-lbm/ft^3 -Rankine");
                 in.comp.right.lmat.setText("<-lbm/ft^3 -Rankine");
                 in.burn.right.lmat.setText("<-lbm/ft^3 -Rankine");
                 in.turb.right.lmat.setText("<-lbm/ft^3 -Rankine");
                 in.nozl.right.lmat.setText("<-lbm/ft^3 -Rankine");
                 in.nozr.right.lmat.setText("<-lbm/ft^3 -Rankine");
              Turbo.g0d = 32.2 ;
//                 setref.setVisible(true) ;
                 pt2flag = 1 ;
                 in.inlet.right.inltch.select(pt2flag) ;
                 arsched = 1 ;
                 in.nozl.right.arch.select(arsched) ;
                 athsched = 1 ;
                 in.nozr.right.atch.select(athsched) ;
                 aexsched = 1 ;
                 in.nozr.right.aech.select(aexsched) ;
                 break ;
          }
        }
         Turbo.aconv = Turbo.lconv1 * Turbo.lconv1;
         Turbo.dconv = Turbo.mconv1 / Turbo.aconv / Turbo.lconv1;
         Turbo.flconv = Turbo.econv2 / Turbo.mconv1;

         Turbo.altd = alts * Turbo.lconv1;
         Turbo.altmin = alm1s * Turbo.lconv1;
         Turbo.altmt = altmts * Turbo.lconv1;
         Turbo.altmr = altmrs * Turbo.lconv1;
         Turbo.a2d = ars * Turbo.aconv;
         Turbo.a2min = arm1s * Turbo.aconv;
         Turbo.a2max = arm2s * Turbo.aconv;
         Turbo.diameng = diars * Turbo.lconv1;
         Turbo.diamin = dim1s * Turbo.lconv1;
         Turbo.diamax = dim2s * Turbo.lconv1;
         Turbo.u0d = u0s * Turbo.lconv2;
         Turbo.u0mt = u0mts * Turbo.lconv2;
         Turbo.u0mr = u0mrs * Turbo.lconv2;
         Turbo.u0max = Turbo.u0mt;
         Turbo.altmax = Turbo.altmt;
        if (entype == 3) {
            Turbo.u0max = Turbo.u0mr;
            Turbo.altmax = Turbo.altmr;
        }

         Turbo.pmax = pmxs * Turbo.pconv;
         Turbo.tmax = tmxs * Turbo.tconv;
         Turbo.tmin = tmns * Turbo.tconv;
         Turbo.tt4d = t4s * Turbo.tconv;
         Turbo.t4min = t4m1s * Turbo.tconv;
         Turbo.t4max = t4m2s * Turbo.tconv;
         Turbo.tt7d = t7s * Turbo.tconv;
         Turbo.t7min = t7m1s * Turbo.tconv;
         Turbo.t7max = t7m2s * Turbo.tconv;
         Turbo.fhvd = fhvs * Turbo.flconv;

        if (lunits == 2) {     // initialization of reference variables
           if (Turbo.u0d <= 10.0) {
               Turbo.u0d = 10.0;
           }
            Turbo.u0ref = Turbo.u0d;
           if (Turbo.altd <= 10.0) {
               Turbo.altd = 10.0;
           }
            Turbo.altref = Turbo.altd;
            Turbo.thrref = Turbo.throtl;
            Turbo.a2ref = Turbo.a2d;
            Turbo.et2ref = Turbo.eta[2] ;
            Turbo.fpref = Turbo.p3fp2d;
            Turbo.et13ref = Turbo.eta[13];
            Turbo.bpref = Turbo.byprat;
            Turbo.cpref = Turbo.p3p2d;
            Turbo.et3ref = Turbo.eta[3];
            Turbo.et4ref = Turbo.eta[4];
            Turbo.et5ref = Turbo.eta[5] ;
            Turbo.t4ref = Turbo.tt4d;
            Turbo.p4ref = Turbo.prat[4] ;
            Turbo.t7ref = Turbo.tt7d;
            Turbo.et7ref = Turbo.eta[7];
            Turbo.a8ref = Turbo.a8rat;
            Turbo.fnref = Turbo.fnlb;
            Turbo.fuelref = Turbo.fuelrat;
            Turbo.sfcref = Turbo.sfc;
            Turbo.airref = Turbo.eair;
            Turbo.epref = Turbo.epr;
            Turbo.etref = Turbo.etr;
            Turbo.faref = Turbo.fa;
            Turbo.wtref = Turbo.weight;
            Turbo.wfref = Turbo.fnlb / Turbo.weight;
        }
               //  Ouput panel
        out.box.loadOut () ;
        out.vars.loadOut () ;
        in.fillBox () ;
   
        return ;
     }
  }  // end Control Panel

  class In extends Panel {
     Turbo outerparent ;
     Flight flight ;
     Size size ;
     Inlet inlet ;
     Fan fan ;
     Comp comp ;
     Burn burn ;
     Turb turb ;
     Nozl nozl ;
     Plot plot ;
     Nozr nozr ;
     Limt limt ;
     Files files ;
     Filep filep ;

     In (Turbo target) {
                            
          outerparent = target ;
          layin = new CardLayout() ;
          setLayout(layin) ;

          flight = new Flight(outerparent) ; 
          size = new Size(outerparent) ;
          inlet = new Inlet(outerparent) ;
          fan = new Fan(outerparent) ;
          comp = new Comp(outerparent) ;
          burn = new Burn(outerparent) ;
          turb = new Turb(outerparent) ;
          nozl = new Nozl(outerparent) ;
          plot = new Plot(outerparent) ;
          nozr = new Nozr(outerparent) ;
          limt = new Limt(outerparent) ;
          files = new Files(outerparent) ;
          filep = new Filep(outerparent) ;
 
          add ("first", flight) ;
          add ("second", size) ;
          add ("third", inlet) ;
          add ("fourth", fan) ;
          add ("fifth", comp) ;
          add ("sixth", burn) ;
          add ("seventh", turb) ;
          add ("eighth", nozl) ;
          add ("ninth", plot) ;
          add ("tenth", nozr) ;
          add ("eleven", limt) ;
          add ("twelve", files) ;
          add ("thirteen", filep) ;
     }

     class Flight extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Flight (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,5,5)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1,s2,s3;
           Label l2, l3 ;
           Choice nozch,inptch ;

           Right (Turbo target) {
    
               int i1, i2, i3  ;
   
               outerparent = target ;
               setLayout(new GridLayout(7,1,10,5)) ;
    
               i1 = (int) (((Turbo.u0d - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
               i2 = (int) (((Turbo.altd - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
               i3 = (int) (((Turbo.throtl - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;
   
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
               s2 = new Scrollbar(Scrollbar.HORIZONTAL,i2,10,0,1000);
               s3 = new Scrollbar(Scrollbar.HORIZONTAL,i3,1,0,1000);
  
               l2 = new Label("lb/sq in", Label.LEFT) ;
               l3 = new Label("F", Label.LEFT) ;

               nozch = new Choice() ;
               nozch.addItem("Afterburner OFF") ;
               nozch.addItem("Afterburner ON");
               nozch.select(0) ;

               inptch = new Choice() ;
               inptch.addItem("Input Speed + Altitude") ;
               inptch.addItem("Input Mach + Altitude");
               inptch.addItem("Input Speed+Pres+Temp") ;
               inptch.addItem("Input Mach+Pres+Temp") ;
               inptch.select(0) ;

               add(inptch) ;
               add(l2) ;  
               add(l3) ;  
               add(s1) ;
               add(s2) ;
               add(s3) ;
               add(nozch) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
          public void handleBar(Event evt) {     // flight conditions
            int i1, i2,i3 ;
            Double V6,V7 ;
            double v1,v2,v3,v6,v7 ;
            float fl1, fl2, fl3 ;

            i1 = s1.getValue() ;
            i2 = s2.getValue() ;
            i3 = s3.getValue() ;
 
            inptype = inptch.getSelectedIndex() ;
            if (inptype == 0 || inptype == 2) {
               left.f1.setBackground(Color.white) ;
               left.f1.setForeground(Color.black) ;
               left.o1.setBackground(Color.black) ;
               left.o1.setForeground(Color.yellow) ;
            }
            if (inptype == 1 || inptype == 3 ) {
               left.f1.setBackground(Color.black) ;
               left.f1.setForeground(Color.yellow) ;
               left.o1.setBackground(Color.white) ;
               left.o1.setForeground(Color.black) ;
            }
            if (inptype <= 1) {
               left.o2.setBackground(Color.black) ;
               left.o2.setForeground(Color.yellow) ;
               left.o3.setBackground(Color.black) ;
               left.o3.setForeground(Color.yellow) ;
               left.f2.setBackground(Color.white) ;
               left.f2.setForeground(Color.black) ;
            }
            if (inptype >= 2) {
               left.o2.setBackground(Color.white) ;
               left.o2.setForeground(Color.black) ;
               left.o3.setBackground(Color.white) ;
               left.o3.setForeground(Color.black) ;
               left.f2.setBackground(Color.black) ;
               left.f2.setForeground(Color.yellow) ;
            }

            if (lunits <= 1) {
                Turbo.vmn1 = Turbo.u0min;
                Turbo.vmx1 = Turbo.u0max;
                Turbo.vmn2 = Turbo.altmin;
                Turbo.vmx2 = Turbo.altmax;
                Turbo.vmn3 = Turbo.thrmin;
                Turbo.vmx3 = Turbo.thrmax;
            }
            if (lunits == 2) {
                Turbo.vmn1 = -10.0 ;
                Turbo.vmx1 = 10.0 ;
                Turbo.vmn2 = -10.0 ;
                Turbo.vmx2 = 10.0 ;
                Turbo.vmn3 = -10.0 ;
                Turbo.vmx3 = 10.0 ;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;
 
            if (inptype >= 2) {
               v2 = 0.0 ;
               i2 = 0 ;
               s2.setValue(i2) ;

               V6 = Double.valueOf(left.o2.getText()) ;
               v6 = V6.doubleValue() ;
               V7 = Double.valueOf(left.o3.getText()) ;
               v7 = V7.doubleValue() ;
                Turbo.ps0 = v6 ;
               if (v6 <= 0.0) {
                   Turbo.ps0 = v6 = 0.0 ;
                 fl1 = (float) v6 ;
                 left.o2.setText(String.valueOf(fl1)) ;
               }
               if (v6 >= Turbo.pmax) {
                   Turbo.ps0 = v6 = Turbo.pmax;
                 fl1 = (float) v6 ;
                 left.o2.setText(String.valueOf(fl1)) ;
               }
                Turbo.ps0 = Turbo.ps0 / Turbo.pconv;
                Turbo.ts0 = v7 + Turbo.tref;
               if (Turbo.ts0 <= Turbo.tmin) {
                   Turbo.ts0 = Turbo.tmin;
                 v7 = Turbo.ts0 - Turbo.tref;
                 fl1 = (float) v7 ;
                 left.o3.setText(String.valueOf(fl1)) ;
               }
               if (Turbo.ts0 >= Turbo.tmax) {
                   Turbo.ts0 = Turbo.tmax;
                 v7 = Turbo.ts0 - Turbo.tref;
                 fl1 = (float) v7 ;
                 left.o3.setText(String.valueOf(fl1)) ;
               }
                Turbo.ts0 = Turbo.ts0 / Turbo.tconv;
            }
         
   // flight conditions
            if (lunits <= 1) {
                Turbo.u0d = v1 ;
                Turbo.altd = v2 ;
                Turbo.throtl = v3 ;
            }
            if (lunits == 2) {
                Turbo.u0d = v1 * Turbo.u0ref / 100. + Turbo.u0ref;
                Turbo.altd = v2 * Turbo.altref / 100. + Turbo.altref;
                Turbo.throtl = v3 * Turbo.thrref / 100. + Turbo.thrref;
            }
   
            if(entype == 1) {
                abflag = nozch.getSelectedIndex();
            }

            left.f1.setText(String.valueOf(filter0(v1))) ;
            left.f2.setText(String.valueOf(filter0(v2))) ;
            left.f3.setText(String.valueOf(filter3(v3))) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, f2, f3, f4 ;
           TextField o1, o2, o3 ;
           Label l1, l2, l3, lmach ;
           Choice inpch ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(7,2,5,5)) ;
     
              l1 = new Label("Speed-mph", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.u0d), 5) ;
              f1.setBackground(Color.white) ;
              f1.setForeground(Color.black) ;

              l2 = new Label("Altitude-ft", Label.CENTER) ;
              f2 = new TextField(String.valueOf((float)Turbo.altd), 5) ;
              f2.setBackground(Color.white) ;
              f2.setForeground(Color.black) ;

              l3 = new Label("Throttle", Label.CENTER) ;
              f3 = new TextField(String.valueOf((float)Turbo.throtl), 5) ;
   
              inpch = new Choice() ;
              inpch.addItem("Gamma") ;
              inpch.addItem("Gam(T)");
              inpch.select(1) ;
              f4 = new TextField(String.valueOf((float)Turbo.gama), 5) ;

              lmach = new Label("Mach", Label.CENTER) ;
              o1 = new TextField(String.valueOf((float)Turbo.fsmach), 5) ;
              o1.setBackground(Color.black) ;
              o1.setForeground(Color.yellow) ;

              o2 = new TextField() ;
              o2.setBackground(Color.black) ;
              o2.setForeground(Color.yellow) ;

              o3 = new TextField() ;
              o3.setBackground(Color.black) ;
              o3.setForeground(Color.yellow) ;

              add(lmach) ;
              add(o1) ;

              add(new Label(" Press ", Label.CENTER)) ;  
              add(o2) ;  

              add(new Label(" Temp  ", Label.CENTER)) ;  
              add(o3) ;  

              add(l1) ;
              add(f1) ;

              add(l2) ;
              add(f2) ;

              add(l3) ;
              add(f3) ;

              add(inpch) ;
              add(f4) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V2,V3,V4,V5,V6,V7 ;
             double v1,v2,v3,v4,v5,v6,v7 ;
             int i1,i2,i3 ;
             float fl1 ;

             gamopt  = inpch.getSelectedIndex() ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V2 = Double.valueOf(f2.getText()) ;
             v2 = V2.doubleValue() ;
             V3 = Double.valueOf(f3.getText()) ;
             v3 = V3.doubleValue() ;
             V4 = Double.valueOf(f4.getText()) ;
             v4 = V4.doubleValue() ;
             V5 = Double.valueOf(o1.getText()) ;
             v5 = V5.doubleValue() ;
             V6 = Double.valueOf(o2.getText()) ;
             v6 = V6.doubleValue() ;
             V7 = Double.valueOf(o3.getText()) ;
             v7 = V7.doubleValue() ;

             if (lunits <= 1) {
     // Airspeed 
                 if (inptype == 0 || inptype == 2) {
                     Turbo.u0d = v1 ;
                     Turbo.vmn1 = Turbo.u0min;
                     Turbo.vmx1 = Turbo.u0max;
                    if(v1 < Turbo.vmn1) {
                        Turbo.u0d = v1 = Turbo.vmn1;
                       fl1 = (float) v1 ;
                       f1.setText(String.valueOf(fl1)) ;
                    }
                    if(v1 > Turbo.vmx1) {
                        Turbo.u0d = v1 = Turbo.vmx1;
                       fl1 = (float) v1 ;
                       f1.setText(String.valueOf(fl1)) ;
                    }
                 }
     // Mach 
                 if (inptype == 1 || inptype == 3) {
                     Turbo.fsmach = v5 ;
                    if (Turbo.fsmach < 0.0) {
                        Turbo.fsmach = v5 = 0.0 ;
                       fl1 = (float) v5 ;
                       o1.setText(String.valueOf(fl1)) ;
                    }
                    if (Turbo.fsmach > 2.25 && entype <= 2) {
                        Turbo.fsmach = v5 = 2.25 ;
                       fl1 = (float) v5 ;
                       o1.setText(String.valueOf(fl1)) ;
                    }
                    if (Turbo.fsmach > 6.75 && entype == 3) {
                        Turbo.fsmach = v5 = 6.75 ;
                       fl1 = (float) v5 ;
                       o1.setText(String.valueOf(fl1)) ;
                    }
                 }
     // Altitude
                 if (inptype <= 1) {
                     Turbo.altd = v2 ;
                     Turbo.vmn2 = Turbo.altmin;
                     Turbo.vmx2 = Turbo.altmax;
                    if(v2 < Turbo.vmn2) {
                        Turbo.altd = v2 = Turbo.vmn2;
                       fl1 = (float) v2 ;
                       f2.setText(String.valueOf(fl1)) ;
                    }
                    if(v2 > Turbo.vmx2) {
                        Turbo.altd = v2 = Turbo.vmx2;
                       fl1 = (float) v2 ;
                       f2.setText(String.valueOf(fl1)) ;
                    }
                 }
     // Pres and Temp
                 if (inptype >= 2) {
                     Turbo.altd = v2 = 0.0 ;
                    fl1 = (float) v2 ;
                    f2.setText(String.valueOf(fl1)) ;
                     Turbo.ps0 = v6 ;
                    if (v6 <= 0.0) {
                        Turbo.ps0 = v6 = 0.0 ;
                      fl1 = (float) v6 ;
                      o2.setText(String.valueOf(fl1)) ;
                    }
                    if (v6 >= Turbo.pmax) {
                        Turbo.ps0 = v6 = Turbo.pmax;
                      fl1 = (float) v6 ;
                      o2.setText(String.valueOf(fl1)) ;
                    }
                     Turbo.ps0 = Turbo.ps0 / Turbo.pconv;
                     Turbo.ts0 = v7 + Turbo.tref;
                    if (Turbo.ts0 <= Turbo.tmin) {
                        Turbo.ts0 = Turbo.tmin;
                      v7 = Turbo.ts0 - Turbo.tref;
                      fl1 = (float) v7 ;
                      o3.setText(String.valueOf(fl1)) ;
                    }
                    if (Turbo.ts0 >= Turbo.tmax) {
                        Turbo.ts0 = Turbo.tmax;
                      v7 = Turbo.ts0 - Turbo.tref;
                      fl1 = (float) v7 ;
                      o3.setText(String.valueOf(fl1)) ;
                    }
                     Turbo.ts0 = Turbo.ts0 / Turbo.tconv;
                 }
     // Throttle
                 Turbo.throtl = v3 ;
                 Turbo.vmn3 = Turbo.thrmin;
                 Turbo.vmx3 = Turbo.thrmax;
                 if(v3 < Turbo.vmn3) {
                     Turbo.throtl = v3 = Turbo.vmn3;
                    fl1 = (float) v3 ;
                    f3.setText(String.valueOf(fl1)) ;
                 }
                 if(v3 > Turbo.vmx3) {
                     Turbo.throtl = v3 = Turbo.vmx3;
                    fl1 = (float) v3 ;
                    f3.setText(String.valueOf(fl1)) ;
                 }
             }
             if (lunits == 2) {
     // Airspeed 
                 Turbo.vmn1 = -10.0;
                 Turbo.vmx1 = 10.0 ;
                 if(v1 < Turbo.vmn1) {
                    v1 = Turbo.vmn1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                 if(v1 > Turbo.vmx1) {
                    v1 = Turbo.vmx1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                 Turbo.u0d = v1 * Turbo.u0ref / 100. + Turbo.u0ref;
     // Altitude 
                 Turbo.vmn2 = -10.0;
                 Turbo.vmx2 = 10.0 ;
                 if(v2 < Turbo.vmn2) {
                    v2 = Turbo.vmn2;
                    fl1 = (float) v2 ;
                    f2.setText(String.valueOf(fl1)) ;
                 }
                 if(v2 > Turbo.vmx2) {
                    v2 = Turbo.vmx2;
                    fl1 = (float) v2 ;
                    f2.setText(String.valueOf(fl1)) ;
                 }
                 Turbo.altd = v2 * Turbo.altref / 100. + Turbo.altref;
     // Throttle 
                 Turbo.vmn3 = -10.0;
                 Turbo.vmx3 = 10.0 ;
                 if(v3 < Turbo.vmn3) {
                    v3 = Turbo.vmn3;
                    fl1 = (float) v3 ;
                    f3.setText(String.valueOf(fl1)) ;
                 }
                 if(v3 > Turbo.vmx3) {
                    v3 = Turbo.vmx3;
                    fl1 = (float) v3 ;
                    f3.setText(String.valueOf(fl1)) ;
                 }
                 Turbo.throtl = v3 * Turbo.thrref / 100. + Turbo.thrref;
            }
     // Gamma 
               Turbo.gama = v4 ;
            if(v4 < 1.0) {
                Turbo.gama = v4 =  1.0 ;
               fl1 = (float) v4 ;
               f4.setText(String.valueOf(fl1)) ;
            }
            if(v4 > 2.0) {
                Turbo.gama = v4 = 2.0 ;
               fl1 = (float) v4 ;
               f4.setText(String.valueOf(fl1)) ;
            }
        
            i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
            i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
            i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;
   
            right.s1.setValue(i1) ;
            right.s2.setValue(i2) ;
            right.s3.setValue(i3) ;

            solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end Flight input

     class Size extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Size (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1;
           Choice chmat,sizch ;

           Right (Turbo target) {
    
               int i1 ;
   
               outerparent = target ;
               setLayout(new GridLayout(6,1,10,5)) ;
    
               i1 = (int) (((Turbo.a2d - Turbo.a2min) / (Turbo.a2max - Turbo.a2min)) * 1000.) ;
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
   
               chmat = new Choice() ;
               chmat.addItem("Computed Weight") ;
               chmat.addItem("Input Weight ");
               chmat.select(0) ;
   
               sizch = new Choice() ;
               sizch.addItem("Input Frontal Area") ;
               sizch.addItem("Input Diameter ");
               sizch.select(0) ;
   
               add(sizch) ;
               add(s1) ;
               add(new Label(" ", Label.CENTER)) ;
               add(chmat) ;
               add(new Label(" ", Label.CENTER)) ;
               add(new Label(" ", Label.CENTER)) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
          public void handleBar(Event evt) {     // engine size
            int i1,i3 ;
            Double V2,V3 ;
            double v1,v2,v3 ;
            float fl1,fl2,fl3 ;

            siztype = sizch.getSelectedIndex() ;

            if (siztype == 0) {
// area input
               i1 = s1.getValue() ;
                Turbo.vmn1 = Turbo.a2min;
                Turbo.vmx1 = Turbo.a2max;

                Turbo.a2d = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
                Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;

               left.f1.setBackground(Color.white) ;
               left.f1.setForeground(Color.black) ;
               left.f3.setBackground(Color.black) ;
               left.f3.setForeground(Color.yellow) ;
            }

            if (siztype == 1) {
// diameter input
               V3 = Double.valueOf(left.f3.getText()) ;
                Turbo.diameng = v3 = V3.doubleValue() ;

                Turbo.a2d = 3.14159 * Turbo.diameng * Turbo.diameng / 4.0 ;

               left.f1.setBackground(Color.black) ;
               left.f1.setForeground(Color.yellow) ;
               left.f3.setBackground(Color.white) ;
               left.f3.setForeground(Color.black) ;
            }

              Turbo.a2 = Turbo.a2d / Turbo.aconv;
            if (entype == 2) {
                Turbo.afan = Turbo.a2;
                Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat) ;
            }
            else {
                Turbo.acore = Turbo.a2;
            }

// compute or input weight 
            wtflag = chmat.getSelectedIndex() ;
            if (wtflag == 1) {
              left.f2.setForeground(Color.black) ;
              left.f2.setBackground(Color.white) ;
              V2 = Double.valueOf(left.f2.getText()) ;
              v2 = V2.doubleValue() ;
                Turbo.weight = v2 / Turbo.fconv;
              if (Turbo.weight < 10.0) {
                  Turbo.weight = v2 = 10.0  ;
                 fl2 = (float) v2 ;
                 left.f2.setText(String.valueOf(fl2)) ;
              }
            }
            if (wtflag == 0) {
              left.f2.setForeground(Color.yellow) ;
              left.f2.setBackground(Color.black) ;
            }

            fl1 = filter3(Turbo.a2d) ;
            fl3 = filter3(Turbo.diameng) ;

            left.f1.setText(String.valueOf(fl1)) ;
            left.f3.setText(String.valueOf(fl3)) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, f2, f3 ;
           Label l1, l2, l3, lab ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(6,2,5,5)) ;
     
              l1 = new Label("Area-sq ft", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.a2d), 5) ;
              f1.setBackground(Color.white) ;
              f1.setForeground(Color.black) ;
  
              l2 = new Label("Weight-lbs", Label.CENTER) ;
              f2 = new TextField(String.valueOf((float)Turbo.weight), 5) ;
              f2.setBackground(Color.black) ;
              f2.setForeground(Color.yellow) ;
  
              l3 = new Label("Diameter-ft", Label.CENTER) ;
              f3 = new TextField(String.valueOf((float)Turbo.diameng), 5) ;
              f3.setBackground(Color.black) ;
              f3.setForeground(Color.yellow) ;
  
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
              add(l1) ;
              add(f1) ;
              add(l3) ;
              add(f3) ;
              add(l2) ;
              add(f2) ;
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V2,V3 ;
             double v1,v2,v3 ;
             int i1,i3 ;
             float fl1,fl2,fl3 ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V2 = Double.valueOf(f2.getText()) ;
             v2 = V2.doubleValue() ;
             V3 = Double.valueOf(f3.getText()) ;
             v3 = V3.doubleValue() ;
     // area input
             if (siztype == 0) {
                 Turbo.a2d = v1 ;
                 Turbo.vmn1 = Turbo.a2min;
                 Turbo.vmx1 = Turbo.a2max;
                if(v1 < Turbo.vmn1) {
                    Turbo.a2d = v1 = Turbo.vmn1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.a2d =  v1 = Turbo.vmx1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                 Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
                fl3 = filter3(Turbo.diameng) ;
                f3.setText(String.valueOf(fl3)) ;
             }
     // diameter input
             if (siztype == 1) {
                 Turbo.diameng = v3 ;
                 Turbo.vmn1 = Turbo.diamin;
                 Turbo.vmx1 = Turbo.diamax;
                if(v3 < Turbo.vmn1) {
                    Turbo.diameng = v3 = Turbo.vmn1;
                   fl3 = (float) v3 ;
                   f3.setText(String.valueOf(fl3)) ;
                }
                if(v3 > Turbo.vmx1) {
                    Turbo.diameng =  v3 = Turbo.vmx1;
                   fl3 = (float) v3 ;
                   f3.setText(String.valueOf(fl3)) ;
                }
                 Turbo.a2d = 3.14159 * Turbo.diameng * Turbo.diameng / 4.0 ;
                fl1 = filter3(Turbo.a2d) ;
                f1.setText(String.valueOf(fl1)) ;
              }

               Turbo.a2 = Turbo.a2d / Turbo.aconv;
              if (entype == 2) {
                  Turbo.afan = Turbo.a2;
                  Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat) ;
              }
              else {
                  Turbo.acore = Turbo.a2;
              }

               Turbo.weight = v2 / Turbo.fconv;
              if (Turbo.weight < 10.0 ) {
                  Turbo.weight = v2 = 10.0 ;
                 fl2 = (float) v2 ;
                 f2.setText(String.valueOf(fl2)) ;
              }
        
              i1 = (int) (((Turbo.a2d - Turbo.a2min) / (Turbo.a2max - Turbo.a2min)) * 1000.) ;

              right.s1.setValue(i1) ;

              solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end Size input

     class Inlet extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Inlet (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1;
           Choice inltch,imat ;
           Label lmat;

           Right (Turbo target) {
    
               int i1 ;
   
               outerparent = target ;
               setLayout(new GridLayout(6,1,10,5)) ;
    
               inltch = new Choice() ;
               inltch.addItem("Mil Spec Recovery") ;
               inltch.addItem("Input Recovery");
               inltch.select(0) ;
 
               i1 = (int) (((Turbo.eta[2] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
   
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
   
               imat = new Choice() ;
               imat.setBackground(Color.white) ;
               imat.setForeground(Color.blue) ;
               imat.addItem("<-- My Material") ;
               imat.addItem("Aluminum") ;
               imat.addItem("Titanium ");
               imat.addItem("Stainless Steel");
               imat.addItem("Nickel Alloy");
               imat.addItem("Actively Cooled");
               imat.select(1) ;
   
               lmat = new Label("lbm/ft^3 ", Label.LEFT) ;
               lmat.setForeground(Color.blue) ;

               add(inltch) ;
               add(s1) ;
               add(new Label(" ", Label.CENTER)) ;
               add(new Label(" ", Label.CENTER)) ;
               add(imat) ;
               add(lmat) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleMat(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
           public void handleMat(Event evt) {  // materials
              Double V1,V2 ;
              double v1,v2 ;

         // inlet
               pt2flag = inltch.getSelectedIndex() ;
               if (pt2flag == 0) {
                  left.f1.setBackground(Color.black) ;
                  left.f1.setForeground(Color.yellow) ;
               }
               if (pt2flag == 1) {
                  left.f1.setBackground(Color.white) ;
                  left.f1.setForeground(Color.black) ;
               }
               Turbo.minlt = imat.getSelectedIndex() ;
               if (Turbo.minlt > 0) {
                  left.di.setBackground(Color.black) ;
                  left.di.setForeground(Color.yellow) ;
                  left.ti.setBackground(Color.black) ;
                  left.ti.setForeground(Color.yellow) ;
               }
               if (Turbo.minlt == 0) {
                  left.di.setBackground(Color.white) ;
                  left.di.setForeground(Color.blue) ;
                  left.ti.setBackground(Color.white) ;
                  left.ti.setForeground(Color.blue) ;
               }
               switch (Turbo.minlt) {
                   case 0: {
                        V1 = Double.valueOf(left.di.getText()) ;
                        v1 = V1.doubleValue() ;
                        V2 = Double.valueOf(left.ti.getText()) ;
                        v2 = V2.doubleValue() ;
                       Turbo.dinlt = v1 / Turbo.dconv;
                       Turbo.tinlt = v2 / Turbo.tconv;
                        break ;
                   }
                   case 1:
                       Turbo.dinlt = 170.7 ;
                       Turbo.tinlt = 900.; break ;
                   case 2:
                       Turbo.dinlt = 293.02 ;
                       Turbo.tinlt = 1500.; break ;
                   case 3:
                       Turbo.dinlt = 476.56 ;
                       Turbo.tinlt = 2000.; break ;
                   case 4:
                       Turbo.dinlt = 515.2 ;
                       Turbo.tinlt = 2500.; break ;
                   case 5:
                       Turbo.dinlt = 515.2 ;
                       Turbo.tinlt = 4000.; break ;
               }
               solve.comPute() ;
          }

          public void handleBar(Event evt) {     // inlet recovery
            int i1 ;
            double v1 ;
            float fl1 ;

            i1 = s1.getValue() ;

            if (lunits <= 1) {
                Turbo.vmn1 = Turbo.etmin;
                Turbo.vmx1 = Turbo.etmax;
            }
            if (lunits == 2) {
                Turbo.vmx1 = 100.0 - 100.0 * Turbo.et2ref;
                Turbo.vmn1 = Turbo.vmx1 - 20.0 ;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
         
            fl1 = filter3(v1) ;
// inlet design
            if (lunits <= 1) {
                Turbo.eta[2] = v1;
            }
            if (lunits == 2) {
                Turbo.eta[2] = Turbo.et2ref + v1 / 100.;
            }

            left.f1.setText(String.valueOf(fl1)) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, ti, di ;
           Label l1, l5, lmat, lm2 ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(6,2,5,5)) ;
     
              l1 = new Label("Pres Recov.", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.eta[2]), 5) ;
              f1.setBackground(Color.black) ;
              f1.setForeground(Color.yellow) ;
              lmat = new Label("T lim -R", Label.CENTER) ;
              lmat.setForeground(Color.blue) ;
              lm2 = new Label("Materials:", Label.CENTER) ;
              lm2.setForeground(Color.blue) ;
              l5 = new Label("Density", Label.CENTER) ;
              l5.setForeground(Color.blue) ;
  
              ti = new TextField(String.valueOf((float)Turbo.tinlt), 5) ;
              ti.setBackground(Color.black) ;
              ti.setForeground(Color.yellow) ;
              di = new TextField(String.valueOf((float)Turbo.dinlt), 5) ;
              di.setBackground(Color.black) ;
              di.setForeground(Color.yellow) ;

              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
              add(l1) ;
              add(f1) ;
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lm2) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lmat) ;
              add(ti) ;
              add(l5) ;
              add(di) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V3,V5 ;
             double v1,v3,v5 ;
             int i1 ;
             float fl1 ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V3 = Double.valueOf(di.getText()) ;
             v3 = V3.doubleValue() ;
             V5 = Double.valueOf(ti.getText()) ;
             v5 = V5.doubleValue() ;

     // materials
              if (Turbo.minlt == 0) {
                if (v3 <= 1.0 * Turbo.dconv) {
                   v3 = 1.0 * Turbo.dconv;
                   di.setText(String.valueOf(filter0(v3 * Turbo.dconv))) ;
                }
                  Turbo.dinlt = v3 / Turbo.dconv;
                if (v5 <= 500. * Turbo.tconv) {
                   v5 = 500. * Turbo.tconv;
                   ti.setText(String.valueOf(filter0(v5 * Turbo.tconv))) ;
                }
                  Turbo.tinlt = v5 / Turbo.tconv;
              }
     // Inlet pressure ratio
             if (lunits <= 1) {
                 Turbo.eta[2]  = v1 ;
                 Turbo.vmn1 = Turbo.etmin;
                 Turbo.vmx1 = Turbo.etmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.eta[2] = v1 = Turbo.vmn1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.eta[2] = v1 = Turbo.vmx1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
              }
              if (lunits == 2) {
                  Turbo.vmx1 = 100.0 - 100.0 * Turbo.et2ref;
                  Turbo.vmn1 = Turbo.vmx1 - 20.0 ;
                if(v1 < Turbo.vmn1) {
                   v1 = Turbo.vmn1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                if(v1 > Turbo.vmx1) {
                   v1 = Turbo.vmx1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                  Turbo.eta[2] = Turbo.et2ref + v1 / 100. ;
              }
        
              i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
   
              right.s1.setValue(i1) ;

              solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end Inlet panel

     class Fan extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Fan (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1,s2,s3;
           Label lmat;
           Choice fmat;

           Right (Turbo target) {
    
               int i1, i2, i3  ;
   
               outerparent = target ;
               setLayout(new GridLayout(6,1,10,5)) ;
    
               i1 = (int) (((Turbo.p3fp2d - Turbo.fprmin) / (Turbo.fprmax - Turbo.fprmin)) * 1000.) ;
               i2 = (int) (((Turbo.eta[13] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
               i3 = (int) (((Turbo.byprat - Turbo.bypmin) / (Turbo.bypmax - Turbo.bypmin)) * 1000.) ;
   
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
               s2 = new Scrollbar(Scrollbar.HORIZONTAL,i2,10,0,1000);
               s3 = new Scrollbar(Scrollbar.HORIZONTAL,i3,10,0,1000);
   
               fmat = new Choice() ;
               fmat.setBackground(Color.white) ;
               fmat.setForeground(Color.blue) ;
               fmat.addItem("<-- My Material") ;
               fmat.addItem("Aluminum") ;
               fmat.addItem("Titanium ");
               fmat.addItem("Stainless Steel");
               fmat.addItem("Nickel Alloy");
               fmat.addItem("Nickel Crystal");
               fmat.addItem("Ceramic");
               fmat.select(2) ;

               lmat = new Label("lbm/ft^3 ", Label.LEFT) ;
               lmat.setForeground(Color.blue) ;

               add(s3) ;
               add(s1) ;
               add(s2) ;
               add(new Label(" ", Label.LEFT)) ;
               add(fmat) ;
               add(lmat) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleMat(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
           }
   
           public void handleMat(Event evt) {
              Double V1,V2 ;
              double v1,v2 ;

                // fan
               Turbo.mfan = fmat.getSelectedIndex() ;
               if(Turbo.mfan > 0) {
                  left.df.setBackground(Color.black) ;
                  left.df.setForeground(Color.yellow) ;
                  left.tf.setBackground(Color.black) ;
                  left.tf.setForeground(Color.yellow) ;
               }
               if (Turbo.mfan == 0) {
                  left.df.setBackground(Color.white) ;
                  left.df.setForeground(Color.blue) ;
                  left.tf.setBackground(Color.white) ;
                  left.tf.setForeground(Color.blue) ;
               }
               switch (Turbo.mfan) {
                   case 0: {
                        V1 = Double.valueOf(left.df.getText()) ;
                        v1 = V1.doubleValue() ;
                        V2 = Double.valueOf(left.tf.getText()) ;
                        v2 = V2.doubleValue() ;
                       Turbo.dfan = v1 / Turbo.dconv;
                       Turbo.tfan = v2 / Turbo.tconv;
                        break ;
                   }
                   case 1:
                       Turbo.dfan = 170.7;
                       Turbo.tfan = 900.; break ;
                   case 2:
                       Turbo.dfan = 293.02 ;
                       Turbo.tfan = 1500.; break ;
                   case 3:
                       Turbo.dfan = 476.56 ;
                       Turbo.tfan = 2000.; break ;
                   case 4:
                       Turbo.dfan = 515.2 ;
                       Turbo.tfan = 2500.; break ;
                   case 5:
                       Turbo.dfan = 515.2 ;
                       Turbo.tfan = 3000.; break ;
                   case 6:
                       Turbo.dfan = 164.2 ;
                       Turbo.tfan = 3000.; break ;
               }
               solve.comPute() ;
          }

          public void handleBar(Event evt) {     // fan design
            int i1, i2,i3 ;
            double v1,v2,v3 ;
            float fl1, fl2, fl3 ;

            i1 = s1.getValue() ;
            i2 = s2.getValue() ;
            i3 = s3.getValue() ;

            if (lunits <= 1) {
                Turbo.vmn1 = Turbo.fprmin;
                Turbo.vmx1 = Turbo.fprmax;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                Turbo.vmn3 = Turbo.bypmin;
                Turbo.vmx3 = Turbo.bypmax;
            }
            if (lunits == 2) {
                Turbo.vmn1 = -10.0 ;
                Turbo.vmx1 = 10.0 ;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et13ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
                Turbo.vmn3 = -10.0 ;
                Turbo.vmx3 = 10.0 ;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;
         
            fl1 = (float) v1 ;
            fl2 = (float) v2 ;
            fl3 = (float) v3 ;

// fan design
            if (lunits <= 1) {
                Turbo.prat[13] = Turbo.p3fp2d = v1 ;
                Turbo.eta[13]  = v2 ;
                Turbo.byprat = v3 ;
            }
            if (lunits == 2) {
                Turbo.prat[13] = Turbo.p3fp2d = v1 * Turbo.fpref / 100. + Turbo.fpref;
                Turbo.eta[13]  = Turbo.et13ref + v2 / 100. ;
                Turbo.byprat = v3 * Turbo.bpref / 100. + Turbo.bpref;
            }
            if (entype == 2) {
                Turbo.a2 = Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
                Turbo.a2d = Turbo.a2 * Turbo.aconv;
            }
              Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;

            left.f1.setText(String.valueOf(fl1)) ;
            left.f2.setText(String.valueOf(fl2)) ;
            left.f3.setText(String.valueOf(fl3)) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, f2, f3 ;
           TextField df,tf;

           Label l1, l2, l3, l5, lmat, lm2 ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(6,2,5,5)) ;
     
              l1 = new Label("Press. Ratio", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.p3fp2d), 5) ;
              l2 = new Label("Efficiency", Label.CENTER) ;
              f2 = new TextField(String.valueOf((float)Turbo.eta[13]), 5) ;
              l3 = new Label("Bypass Rat.", Label.CENTER) ;
              f3 = new TextField(String.valueOf((float)Turbo.byprat), 5) ;
              lmat = new Label("T lim-R", Label.CENTER) ;
              lmat.setForeground(Color.blue) ;
              lm2 = new Label("Materials:", Label.CENTER) ;
              lm2.setForeground(Color.blue) ;
              l5 = new Label("Density", Label.CENTER) ;
              l5.setForeground(Color.blue) ;
  
              df = new TextField(String.valueOf((float)Turbo.dfan), 5) ;
              df.setBackground(Color.black) ;
              df.setForeground(Color.yellow) ;
              tf = new TextField(String.valueOf((float)Turbo.tfan), 5) ;
              tf.setBackground(Color.black) ;
              tf.setForeground(Color.yellow) ;
   
              add(l3) ;
              add(f3) ;
              add(l1) ;
              add(f1) ;
              add(l2) ;
              add(f2) ;
              add(lm2) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lmat) ;
              add(tf) ;
              add(l5) ;
              add(df) ;
   
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V2,V3,V4,V5 ;
             double v1,v2,v3,v4,v5 ;
             int i1,i2,i3 ;
             float fl1 ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V2 = Double.valueOf(f2.getText()) ;
             v2 = V2.doubleValue() ;
             V3 = Double.valueOf(f3.getText()) ;
             v3 = V3.doubleValue() ;
             V4 = Double.valueOf(df.getText()) ;
             v4 = V4.doubleValue() ;
             V5 = Double.valueOf(tf.getText()) ;
             v5 = V5.doubleValue() ;

             if (lunits <= 1) {
   // Fan pressure ratio
                 Turbo.prat[13] = Turbo.p3fp2d = v1 ;
                 Turbo.vmn1 = Turbo.fprmin;
                 Turbo.vmx1 = Turbo.fprmax;
               if(v1 < Turbo.vmn1) {
                   Turbo.prat[13] = Turbo.p3fp2d = v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                   Turbo.prat[13] = Turbo.p3fp2d = v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
   // Fan efficiency
                 Turbo.eta[13] = v2 ;
                 Turbo.vmn2 = Turbo.etmin;
                 Turbo.vmx2 = Turbo.etmax;
               if(v2 < Turbo.vmn2) {
                   Turbo.eta[13] = v2 = Turbo.vmn2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
               if(v2 > Turbo.vmx2) {
                   Turbo.eta[13] = v2 = Turbo.vmx2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
   // bypass ratio
                 Turbo.byprat = v3 ;
                 Turbo.vmn3 = Turbo.bypmin;
                 Turbo.vmx3 = Turbo.bypmax;
               if(v3 < Turbo.vmn3) {
                   Turbo.byprat = v3 = Turbo.vmn3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
               if(v3 > Turbo.vmx3) {
                   Turbo.byprat = v3 = Turbo.vmx3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
            }
            if (lunits == 2) {
   // Fan pressure ratio
                Turbo.vmn1 = -10.0;
                Turbo.vmx1 = 10.0 ;
               if(v1 < Turbo.vmn1) {
                  v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                  v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
                Turbo.prat[13] = Turbo.p3fp2d = v1 * Turbo.fpref / 100. + Turbo.fpref;
     // Fan efficiency
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et13ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
               if(v2 < Turbo.vmn2) {
                  v2 = Turbo.vmn2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
               if(v2 > Turbo.vmx2) {
                  v2 = Turbo.vmx2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
                Turbo.eta[13] = Turbo.et13ref + v2 / 100. ;
     // bypass ratio
                Turbo.vmn3 = -10.0;
                Turbo.vmx3 = 10.0 ;
               if(v3 < Turbo.vmn3) {
                  v3 = Turbo.vmn3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
               if(v3 > Turbo.vmx3) {
                  v3 = Turbo.vmx3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
                }
                Turbo.byprat = v3 * Turbo.bpref / 100. + Turbo.bpref;
             }
             if (entype == 2) {
                 Turbo.a2 = Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
                 Turbo.a2d = Turbo.a2 * Turbo.aconv;
             }
               Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
   // materials
            if (Turbo.mfan == 0) {
                if (v4 <= 1.0 * Turbo.dconv) {
                   v4 = 1.0 * Turbo.dconv;
                   df.setText(String.valueOf(filter0(v4 * Turbo.dconv))) ;
                }
                Turbo.dfan = v4 / Turbo.dconv;
                if (v5 <= 500. * Turbo.tconv) {
                   v5 = 500. * Turbo.tconv;
                   tf.setText(String.valueOf(filter0(v5 * Turbo.tconv))) ;
                }
                Turbo.tfan = v5 / Turbo.tconv;
             }

             i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
             i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
             i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;
   
             right.s1.setValue(i1) ;
             right.s2.setValue(i2) ;
             right.s3.setValue(i3) ;

             solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end Fan

     class Comp extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Comp (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1,s2;
           Choice stgch,cmat ;
           Label lmat ;

           Right (Turbo target) {
    
               int i1, i2 ;
   
               outerparent = target ;
               setLayout(new GridLayout(6,1,10,5)) ;
    
               i1 = (int) (((Turbo.p3p2d - Turbo.cprmin) / (Turbo.cprmax - Turbo.cprmin)) * 1000.) ;
               i2 = (int) (((Turbo.eta[3] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
   
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
               s2 = new Scrollbar(Scrollbar.HORIZONTAL,i2,10,0,1000);
   
               cmat = new Choice() ;
               cmat.setBackground(Color.white) ;
               cmat.setForeground(Color.blue) ;
               cmat.addItem("<-- My Material") ;
               cmat.addItem("Aluminum") ;
               cmat.addItem("Titanium ");
               cmat.addItem("Stainless Steel");
               cmat.addItem("Nickel Alloy");
               cmat.addItem("Nickel Crystal");
               cmat.addItem("Ceramic");
               cmat.select(2) ;

               lmat = new Label("lbm/ft^3", Label.LEFT) ;
               lmat.setForeground(Color.blue) ;

               stgch = new Choice() ;
               stgch.addItem("Compute # Stages") ;
               stgch.addItem("Input # Stages");
               stgch.select(0) ;
   
               add(stgch) ;
               add(s1) ;
               add(s2) ;
               add(new Label(" ", Label.LEFT)) ;
               add(cmat) ;
               add(lmat) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleMat(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
           public void handleMat(Event evt) {
              Double V1,V2 ;
              double v1,v2 ;

             // compressor
               Turbo.ncflag = stgch.getSelectedIndex() ;
               if (Turbo.ncflag == 0) {
                  left.f3.setBackground(Color.black) ;
                  left.f3.setForeground(Color.yellow) ;
               }
               if (Turbo.ncflag == 1) {
                  left.f3.setBackground(Color.white) ;
                  left.f3.setForeground(Color.black) ;
               }

               Turbo.mcomp = cmat.getSelectedIndex() ;
               if(Turbo.mcomp > 0) {
                  left.dc.setBackground(Color.black) ;
                  left.dc.setForeground(Color.yellow) ;
                  left.tc.setBackground(Color.black) ;
                  left.tc.setForeground(Color.yellow) ;
               }
               if (Turbo.mcomp == 0) {
                  left.dc.setBackground(Color.white) ;
                  left.dc.setForeground(Color.blue) ;
                  left.tc.setBackground(Color.white) ;
                  left.tc.setForeground(Color.blue) ;
               }
               switch (Turbo.mcomp) {
                   case 0: {
                        V1 = Double.valueOf(left.dc.getText()) ;
                        v1 = V1.doubleValue() ;
                        V2 = Double.valueOf(left.tc.getText()) ;
                        v2 = V2.doubleValue() ;
                       Turbo.dcomp = v1 / Turbo.dconv;
                       Turbo.tcomp = v2 / Turbo.tconv;
                        break ;
                   }
                   case 1:
                       Turbo.dcomp = 170.7 ;
                       Turbo.tcomp = 900.; break ;
                   case 2:
                       Turbo.dcomp = 293.02 ;
                       Turbo.tcomp = 1500.; break ;
                   case 3:
                       Turbo.dcomp = 476.56 ;
                       Turbo.tcomp = 2000.; break ;
                   case 4:
                       Turbo.dcomp = 515.2 ;
                       Turbo.tcomp = 2500.; break ;
                   case 5:
                       Turbo.dcomp = 515.2 ;
                       Turbo.tcomp = 3000.; break ;
                   case 6:
                       Turbo.dcomp = 164.2 ;
                       Turbo.tcomp = 3000.; break ;
               }
               solve.comPute() ;
          }

          public void handleBar(Event evt) {  // compressor design
            int i1, i2 ;
            double v1,v2 ;
            float fl1, fl2 ;

            i1 = s1.getValue() ;
            i2 = s2.getValue() ;

            if (lunits <= 1) {
                Turbo.vmn1 = Turbo.cprmin;
                Turbo.vmx1 = Turbo.cprmax;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
            }
            if (lunits == 2) {
                Turbo.vmn1 = -10.0 ;
                Turbo.vmx1 = 10.0 ;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et3ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
         
            fl1 = (float) v1 ;
            fl2 = (float) v2 ;

//  compressor design
            if (lunits <= 1) {
                Turbo.prat[3] = Turbo.p3p2d = v1 ;
                Turbo.eta[3]  = v2 ;
            }
            if (lunits == 2) {
                Turbo.prat[3] = Turbo.p3p2d = v1 * Turbo.cpref / 100. + Turbo.cpref;
                Turbo.eta[3]  = Turbo.et3ref + v2 / 100.  ;
            }

            left.f1.setText(String.valueOf(fl1)) ;
            left.f2.setText(String.valueOf(fl2)) ;
 
            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, f2, f3, dc, tc ;
           Label l1, l2, l5, lmat, lm2 ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(6,2,5,5)) ;
     
              l1 = new Label("Press. Ratio", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.p3p2d), 5) ;
              l2 = new Label("Efficiency", Label.CENTER) ;
              f2 = new TextField(String.valueOf((float)Turbo.eta[13]), 5) ;
              lmat = new Label("T lim-R", Label.CENTER) ;
              lmat.setForeground(Color.blue) ;
              lm2 = new Label("Materials:", Label.CENTER) ;
              lm2.setForeground(Color.blue) ;
              l5 = new Label("Density", Label.CENTER) ;
              l5.setForeground(Color.blue) ;
  
              f3 = new TextField(String.valueOf((int)Turbo.ncomp), 5) ;
              f3.setBackground(Color.black) ;
              f3.setForeground(Color.yellow) ;

              dc = new TextField(String.valueOf((float)Turbo.dcomp), 5) ;
              dc.setBackground(Color.black) ;
              dc.setForeground(Color.yellow) ;
              tc = new TextField(String.valueOf((float)Turbo.tcomp), 5) ;
              tc.setBackground(Color.black) ;
              tc.setForeground(Color.yellow) ;
   
              add(new Label("Stages ", Label.CENTER)) ;
              add(f3) ;
              add(l1) ;
              add(f1) ;
              add(l2) ;
              add(f2) ;
              add(lm2) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lmat) ;
              add(tc) ;
              add(l5) ;
              add(dc) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V2,V4,V6 ;
             double v1,v2,v4,v6 ;
             Integer I3 ;
             int i1,i2,i3 ;
             float fl1 ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V2 = Double.valueOf(f2.getText()) ;
             v2 = V2.doubleValue() ;
             V4 = Double.valueOf(dc.getText()) ;
             v4 = V4.doubleValue() ;
             V6 = Double.valueOf(tc.getText()) ;
             v6 = V6.doubleValue() ;

             I3 = Integer.valueOf(f3.getText()) ;
             i3 = I3.intValue() ;

      // materials
              if (Turbo.mcomp == 0) {
                if (v4 <= 1.0 * Turbo.dconv) {
                   v4 = 1.0 * Turbo.dconv;
                   dc.setText(String.valueOf(filter0(v4 * Turbo.dconv))) ;
                }
                  Turbo.dcomp = v4 / Turbo.dconv;
                if (v6 <= 500. * Turbo.tconv) {
                   v6 = 500. * Turbo.tconv;
                   tc.setText(String.valueOf(filter0(v6 * Turbo.tconv))) ;
                }
                  Turbo.tcomp = v6 / Turbo.tconv;
              }
      // number of stages
             if (Turbo.ncflag == 1) {
                 Turbo.ncomp = i3 ;
                if (Turbo.ncomp <= 0) {
                    Turbo.ncomp = 1;
                   f3.setText(String.valueOf(Turbo.ncomp)) ;
                }
             }

             if (lunits <= 1) {
      // Compressor pressure ratio
                 Turbo.prat[3] = Turbo.p3p2d = v1 ;
                 Turbo.vmn1 = Turbo.cprmin;
                 Turbo.vmx1 = Turbo.cprmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmn1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmx1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
     // Compressor efficiency
                 Turbo.eta[3] = v2 ;
                 Turbo.vmn2 = Turbo.etmin;
                 Turbo.vmx2 = Turbo.etmax;
                if(v2 < Turbo.vmn2) {
                    Turbo.eta[3] = v2 = Turbo.vmn2;
                   fl1 = (float) v2 ;
                   f2.setText(String.valueOf(fl1)) ;
                }
                if(v2 > Turbo.vmx2) {
                    Turbo.eta[3] = v2 = Turbo.vmx2;
                   fl1 = (float) v2 ;
                   f2.setText(String.valueOf(fl1)) ;
                }
              }
              if (lunits == 2) {
       // Compressor pressure ratio
                  Turbo.vmn1 = -10.0;
                  Turbo.vmx1 = 10.0 ;
                 if(v1 < Turbo.vmn1) {
                    v1 = Turbo.vmn1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                 if(v1 > Turbo.vmx1) {
                    v1 = Turbo.vmx1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                  Turbo.prat[3] = Turbo.p3p2d = v1 * Turbo.cpref / 100. + Turbo.cpref;
      // Compressor efficiency
                  Turbo.vmx2 = 100.0 - 100.0 * Turbo.et3ref;
                  Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
                 if(v2 < Turbo.vmn2) {
                    v2 = Turbo.vmn2;
                    fl1 = (float) v2 ;
                    f2.setText(String.valueOf(fl1)) ;
                 }
                 if(v2 > Turbo.vmx2) {
                    v2 = Turbo.vmx2;
                    fl1 = (float) v2 ;
                    f2.setText(String.valueOf(fl1)) ;
                 }
                  Turbo.eta[3] = Turbo.et3ref + v2 / 100. ;
             }

             i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
             i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
   
             right.s1.setValue(i1) ;
             right.s2.setValue(i2) ;

             solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end Comp panel

     class Burn extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Burn (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1,s2,s3;
           Label lmat ;
           Choice bmat,fuelch ;

           Right (Turbo target) {
    
               int i1, i2, i3  ;
   
               outerparent = target ;
               setLayout(new GridLayout(7,1,10,5)) ;
    
               i1 = (int) (((Turbo.tt4d - Turbo.t4min) / (Turbo.t4max - Turbo.t4min)) * 1000.) ;
               i2 = (int) (((Turbo.eta[4] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
               i3 = (int) (((Turbo.prat[4] - Turbo.etmin) / (Turbo.pt4max - Turbo.etmin)) * 1000.) ;
   
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
               s2 = new Scrollbar(Scrollbar.HORIZONTAL,i2,10,0,1000);
               s3 = new Scrollbar(Scrollbar.HORIZONTAL,i3,10,0,1000);
   
               bmat = new Choice() ;
               bmat.setBackground(Color.white) ;
               bmat.setForeground(Color.blue) ;
               bmat.addItem("<-- My Material") ;
               bmat.addItem("Aluminum") ;
               bmat.addItem("Titanium ");
               bmat.addItem("Stainless Steel");
               bmat.addItem("Nickel Alloy");
               bmat.addItem("Nickel Crystal");
               bmat.addItem("Ceramic");
               bmat.addItem("Actively Cooled");
               bmat.select(4) ;
 
               fuelch = new Choice() ;
               fuelch.addItem("Jet - A") ;
               fuelch.addItem("Hydrogen");
               fuelch.addItem("<-- Your Fuel");
               fuelch.select(0) ;

               lmat = new Label("lbm/ft^3 ", Label.LEFT) ;
               lmat.setForeground(Color.blue) ;

               add(fuelch) ;
               add(s1) ;
               add(s3) ;
               add(s2) ;
               add(new Label(" ", Label.LEFT)) ;
               add(bmat) ;
               add(lmat) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleMat(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
          public void handleMat(Event evt) {
              Double V1,V2 ;
              double v1,v2 ;
              float fl1;

               fueltype = fuelch.getSelectedIndex() ;
                if (fueltype == 0) {
                    Turbo.fhv = 18600.;
                }
                if (fueltype == 1) {
                    Turbo.fhv = 49900.;
                }
                left.f4.setBackground(Color.black) ;
                left.f4.setForeground(Color.yellow) ;
              Turbo.fhvd = Turbo.fhv * Turbo.flconv;
                fl1 = (float) (Turbo.fhvd) ;
                left.f4.setText(String.valueOf(filter0(Turbo.fhvd))) ;

                if (fueltype == 2) {
                   left.f4.setBackground(Color.white) ;
                   left.f4.setForeground(Color.black) ;
                }

                // burner
              Turbo.mburner = bmat.getSelectedIndex() ;
               if(Turbo.mburner > 0) {
                  left.db.setBackground(Color.black) ;
                  left.db.setForeground(Color.yellow) ;
                  left.tb.setBackground(Color.black) ;
                  left.tb.setForeground(Color.yellow) ;
               }
               if (Turbo.mburner == 0) {
                  left.db.setBackground(Color.white) ;
                  left.db.setForeground(Color.blue) ;
                  left.tb.setBackground(Color.white) ;
                  left.tb.setForeground(Color.blue) ;
               }
               switch (Turbo.mburner) {
                   case 0: {
                        V1 = Double.valueOf(left.db.getText()) ;
                        v1 = V1.doubleValue() ;
                        V2 = Double.valueOf(left.tb.getText()) ;
                        v2 = V2.doubleValue() ;
                       Turbo.dburner = v1 / Turbo.dconv;
                       Turbo.tburner = v2 / Turbo.tconv;
                        break ;
                   }
                   case 1:
                       Turbo.dburner = 170.7 ;
                       Turbo.tburner = 900.; break ;
                   case 2:
                       Turbo.dburner = 293.02 ;
                       Turbo.tburner = 1500.; break ;
                   case 3:
                       Turbo.dburner = 476.56 ;
                       Turbo.tburner = 2000.; break ;
                   case 4:
                       Turbo.dburner = 515.2 ;
                       Turbo.tburner = 2500.; break ;
                   case 5:
                       Turbo.dburner = 515.2 ;
                       Turbo.tburner = 3000.; break ;
                   case 6:
                       Turbo.dburner = 164.2 ;
                       Turbo.tburner = 3000.; break ;
                   case 7:
                       Turbo.dburner = 515.2 ;
                       Turbo.tburner = 4500.; break ;
               }
               solve.comPute() ;
          }

          public void handleBar(Event evt) {     // burner design
            int i1, i2,i3 ;
            double v1,v2,v3 ;
            float fl1, fl2, fl3 ;

            i1 = s1.getValue() ;
            i2 = s2.getValue() ;
            i3 = s3.getValue() ;

            if (lunits <= 1) {
                Turbo.vmn1 = Turbo.t4min;
                Turbo.vmx1 = Turbo.t4max;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                Turbo.vmn3 = Turbo.etmin;
                Turbo.vmx3 = Turbo.pt4max;
            }
            if (lunits == 2) {
                Turbo.vmn1 = -10.0 ;
                Turbo.vmx1 = 10.0 ;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et4ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
                Turbo.vmx3 = 100.0 - 100.0 * Turbo.p4ref;
                Turbo.vmn3 = Turbo.vmx3 - 20.0 ;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;
         
            fl1 = (float) v1 ;
            fl2 = (float) v2 ;
            fl3 = (float) v3 ;
// burner design
            if (lunits <= 1) {
                Turbo.tt4d = v1 ;
                Turbo.eta[4]  = v2 ;
                Turbo.prat[4] = v3 ;
            }
            if (lunits == 2) {
                Turbo.tt4d = v1 * Turbo.t4ref / 100. + Turbo.t4ref;
                Turbo.eta[4]  = Turbo.et4ref + v2 / 100. ;
                Turbo.prat[4] = Turbo.p4ref + v3 / 100.  ;
            }
              Turbo.tt4 = Turbo.tt4d / Turbo.tconv;

            left.f1.setText(String.valueOf(fl1)) ;
            left.f2.setText(String.valueOf(fl2)) ;
            left.f3.setText(String.valueOf(fl3)) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, f2, f3, f4 ;
           Label l1, l2, l3, l4, l5, lmat, lm2 ;
           TextField db, tb;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(7,2,5,5)) ;
     
              l1 = new Label("Tmax -R", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.tt4d), 5) ;
              l2 = new Label("Efficiency", Label.CENTER) ;
              f2 = new TextField(String.valueOf((float)Turbo.eta[4]), 5) ;
              l3 = new Label("Press. Ratio", Label.CENTER) ;
              f3 = new TextField(String.valueOf((float)Turbo.prat[4]), 5) ;
              l4 = new Label("FHV Btu/lb", Label.CENTER) ;
              f4 = new TextField(String.valueOf((float)Turbo.fhv), 5) ;
              f4.setBackground(Color.black) ;
              f4.setForeground(Color.yellow) ;

              lmat = new Label("T lim-R", Label.CENTER) ;
              lmat.setForeground(Color.blue) ;
              lm2 = new Label("Materials:", Label.CENTER) ;
              lm2.setForeground(Color.blue) ;
              l5 = new Label("Density", Label.CENTER) ;
              l5.setForeground(Color.blue) ;
  
              db = new TextField(String.valueOf((float)Turbo.dburner), 5) ;
              db.setBackground(Color.black) ;
              db.setForeground(Color.yellow) ;
              tb = new TextField(String.valueOf((float)Turbo.tburner), 5) ;
              tb.setBackground(Color.black) ;
              tb.setForeground(Color.yellow) ;
      
              add(l4) ;
              add(f4) ;
              add(l1) ;
              add(f1) ;
              add(l3) ;
              add(f3) ;
              add(l2) ;
              add(f2) ;
              add(lm2) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lmat) ;
              add(tb) ;
              add(l5) ;
              add(db) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V2,V3,V4,V5,V6 ;
             double v1,v2,v3,v4,v5,v6 ;
             int i1,i2,i3 ;
             float fl1 ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V2 = Double.valueOf(f2.getText()) ;
             v2 = V2.doubleValue() ;
             V3 = Double.valueOf(f3.getText()) ;
             v3 = V3.doubleValue() ;
             V6 = Double.valueOf(f4.getText()) ;
             v6 = V6.doubleValue() ;
             V4 = Double.valueOf(db.getText()) ;
             v4 = V4.doubleValue() ;
             V5 = Double.valueOf(tb.getText()) ;
             v5 = V5.doubleValue() ;

     // Materials
             if (Turbo.mburner == 0) {
                if (v4 <= 1.0 * Turbo.dconv) {
                   v4 = 1.0 * Turbo.dconv;
                   db.setText(String.valueOf(filter0(v4 * Turbo.dconv))) ;
                }
                 Turbo.dburner = v4 / Turbo.dconv;
                if (v5 <= 500. * Turbo.tconv) {
                   v5 = 500. * Turbo.tconv;
                   tb.setText(String.valueOf(filter0(v5 * Turbo.tconv))) ;
                }
                 Turbo.tburner = v5 / Turbo.tconv;
             }

             if (lunits <= 1) {
     // Max burner temp
                 Turbo.tt4d = v1 ;
                 Turbo.vmn1 = Turbo.t4min;
                 Turbo.vmx1 = Turbo.t4max;
                 if(v1 < Turbo.vmn1) {
                     Turbo.tt4d = v1 = Turbo.vmn1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                 if(v1 > Turbo.vmx1) {
                     Turbo.tt4d = v1 = Turbo.vmx1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                 Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
     // burner  efficiency
                 Turbo.eta[4] = v2 ;
                 Turbo.vmn2 = Turbo.etmin;
                 Turbo.vmx2 = Turbo.etmax;
                 if(v2 < Turbo.vmn2) {
                     Turbo.eta[4] = v2 = Turbo.vmn2;
                    fl1 = (float) v2 ;
                    f2.setText(String.valueOf(fl1)) ;
                 }
                 if(v2 > Turbo.vmx2) {
                     Turbo.eta[4] = v2 = Turbo.vmx2;
                    fl1 = (float) v2 ;
                    f2.setText(String.valueOf(fl1)) ;
                 }
     //  burner pressure ratio
                 Turbo.prat[4] = v3 ;
                 Turbo.vmn3 = Turbo.etmin;
                 Turbo.vmx3 = Turbo.pt4max;
                 if(v3 < Turbo.vmn3) {
                     Turbo.prat[4] = v3 = Turbo.vmn3;
                    fl1 = (float) v3 ;
                    f3.setText(String.valueOf(fl1)) ;
                 }
                 if(v3 > Turbo.vmx3) {
                     Turbo.prat[4] = v3 = Turbo.vmx3;
                    fl1 = (float) v3 ;
                    f3.setText(String.valueOf(fl1)) ;
                 }
     // fuel heating value
                 if (fueltype == 2) {
                     Turbo.fhvd = v6 ;
                     Turbo.fhv = Turbo.fhvd / Turbo.flconv;
                 }
             }

             if (lunits == 2) {
     // Max burner temp
                 Turbo.vmn1 = -10.0;
                 Turbo.vmx1 = 10.0 ;
               if(v1 < Turbo.vmn1) {
                  v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                  v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
                 Turbo.tt4d = v1 * Turbo.t4ref / 100. + Turbo.t4ref;
                 Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
     // burner  efficiency
                 Turbo.vmx2 = 100.0 - 100.0 * Turbo.et4ref;
                 Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
               if(v2 < Turbo.vmn2) {
                  v2 = Turbo.vmn2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
               if(v2 > Turbo.vmx2) {
                  v2 = Turbo.vmx2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
                 Turbo.eta[4] = Turbo.et4ref + v2 / 100. ;
     //  burner pressure ratio
                 Turbo.vmx3 = 100.0 - 100.0 * Turbo.p4ref;
                 Turbo.vmn3 = Turbo.vmx3 - 20.0 ;
               if(v3 < Turbo.vmn3) {
                  v3 = Turbo.vmn3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
               if(v3 > Turbo.vmx3) {
                  v3 = Turbo.vmx3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
                 Turbo.prat[4] = Turbo.p4ref + v3 / 100.  ;
             }

             i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
             i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
             i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;
   
             right.s1.setValue(i1) ;
             right.s2.setValue(i2) ;
             right.s3.setValue(i3) ;

             solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end Burn

     class Turb extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Turb (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1;
           Label lmat ;
           Choice tmat,stgch ;

           Right (Turbo target) {
    
               int i1 ;
   
               outerparent = target ;
               setLayout(new GridLayout(6,1,10,5)) ;
    
               i1 = (int) (((Turbo.eta[5] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
   
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
   
               stgch = new Choice() ;
               stgch.addItem("Compute # Stages") ;
               stgch.addItem("Input # Stages");
               stgch.select(0) ;
   
               tmat = new Choice() ;
               tmat.setBackground(Color.white) ;
               tmat.setForeground(Color.blue) ;
               tmat.addItem("<-- My Material") ;
               tmat.addItem("Aluminum") ;
               tmat.addItem("Titanium ");
               tmat.addItem("Stainless Steel");
               tmat.addItem("Nickel Alloy");
               tmat.addItem("Nickel Crystal");
               tmat.addItem("Ceramic");
               tmat.select(4) ;
   
               lmat = new Label("lbm/ft^3", Label.LEFT) ;
               lmat.setForeground(Color.blue) ;

               add(stgch) ;
               add(s1) ;
               add(new Label(" ", Label.CENTER)) ;
               add(new Label(" ", Label.CENTER)) ;
               add(tmat) ;
               add(lmat) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleMat(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
          public void handleMat(Event evt) {
              Double V1,V2 ;
              double v1,v2 ;

              Turbo.ntflag = stgch.getSelectedIndex() ;
              if (Turbo.ntflag == 0) {
                 left.f3.setBackground(Color.black) ;
                 left.f3.setForeground(Color.yellow) ;
              }
              if (Turbo.ntflag == 1) {
                 left.f3.setBackground(Color.white) ;
                 left.f3.setForeground(Color.black) ;
              }
                // turnine
              Turbo.mturbin = tmat.getSelectedIndex() ;
              if(Turbo.mturbin > 0) {
                  left.dt.setBackground(Color.black) ;
                  left.dt.setForeground(Color.yellow) ;
                  left.tt.setBackground(Color.black) ;
                  left.tt.setForeground(Color.yellow) ;
              }
              if (Turbo.mturbin == 0) {
                  left.dt.setBackground(Color.white) ;
                  left.dt.setForeground(Color.blue) ;
                  left.tt.setBackground(Color.white) ;
                  left.tt.setForeground(Color.blue) ;
              }
              switch (Turbo.mturbin) {
                   case 0: {
                        V1 = Double.valueOf(left.dt.getText()) ;
                        v1 = V1.doubleValue() ;
                        V2 = Double.valueOf(left.tt.getText()) ;
                        v2 = V2.doubleValue() ;
                       Turbo.dturbin = v1 / Turbo.dconv;
                       Turbo.tturbin = v2 / Turbo.tconv;
                        break ;
                   }
                   case 1:
                       Turbo.dturbin = 170.7 ;
                       Turbo.tturbin = 900.; break ;
                   case 2:
                       Turbo.dturbin = 293.02 ;
                       Turbo.tturbin = 1500.; break ;
                   case 3:
                       Turbo.dturbin = 476.56 ;
                       Turbo.tturbin = 2000.; break ;
                   case 4:
                       Turbo.dturbin = 515.2 ;
                       Turbo.tturbin = 2500.; break ;
                   case 5:
                       Turbo.dturbin = 515.2 ;
                       Turbo.tturbin = 3000.; break ;
                   case 6:
                       Turbo.dturbin = 164.2 ;
                       Turbo.tturbin = 3000.; break ;
              }
              solve.comPute() ;
          }

          public void handleBar(Event evt) {     // turbine
            int i1 ;
            double v1 ;
            float fl1 ;

            i1 = s1.getValue() ;

            if (lunits <= 1) {
                Turbo.vmn1 = Turbo.etmin;
                Turbo.vmx1 = Turbo.etmax;
            }
            if (lunits == 2) {
                Turbo.vmx1 = 100.0 - 100.0 * Turbo.et5ref;
                Turbo.vmn1 = Turbo.vmx1 - 20.0 ;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
         
            fl1 = (float) v1 ;

            if (lunits <= 1) {
                Turbo.eta[5]  = v1 ;
            }
            if (lunits == 2) {
                Turbo.eta[5]  = Turbo.et5ref + v1 / 100.  ;
            }
 
            left.f1.setText(String.valueOf(fl1)) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1,f3,dt,tt ;
           Label l1, l5, lmat, lm2;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(6,2,5,5)) ;
     
              f3 = new TextField(String.valueOf((int)Turbo.nturb), 5) ;
              f3.setBackground(Color.black) ;
              f3.setForeground(Color.yellow) ;

              l1 = new Label("Efficiency", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.eta[5]), 5) ;
              lmat = new Label("T lim-R", Label.CENTER) ;
              lmat.setForeground(Color.blue) ;
              lm2 = new Label("Materials:", Label.CENTER) ;
              lm2.setForeground(Color.blue) ;
              l5 = new Label("Density", Label.CENTER) ;
              l5.setForeground(Color.blue) ;
  
  
              dt = new TextField(String.valueOf((float)Turbo.dturbin), 5) ;
              dt.setBackground(Color.black) ;
              dt.setForeground(Color.yellow) ;
              tt = new TextField(String.valueOf((float)Turbo.tturbin), 5) ;
              tt.setBackground(Color.black) ;
              tt.setForeground(Color.yellow) ;
      
              add(new Label("Stages ", Label.CENTER)) ;
              add(f3) ;
              add(l1) ;
              add(f1) ;
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lm2) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lmat) ;
              add(tt) ;
              add(l5) ;
              add(dt) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V4,V8 ;
             double v1,v4,v8 ;
             Integer I3 ;
             int i1,i3 ;
             float fl1 ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V4 = Double.valueOf(dt.getText()) ;
             v4 = V4.doubleValue() ;
             V8 = Double.valueOf(tt.getText()) ;
             v8 = V8.doubleValue() ;

             I3 = Integer.valueOf(f3.getText()) ;
             i3 = I3.intValue() ;
     // number of stages
             if (Turbo.ntflag == 1 && i3 >= 1) {
                 Turbo.nturb = i3 ;
             }
     // materials
             if (Turbo.mturbin == 0) {
               if (v4 <= 1.0 * Turbo.dconv) {
                  v4 = 1.0 * Turbo.dconv;
                  dt.setText(String.valueOf(filter0(v4 * Turbo.dconv))) ;
               }
                 Turbo.dturbin = v4 / Turbo.dconv;
               if (v8 <= 500. * Turbo.tconv) {
                  v8 = 500. * Turbo.tconv;
                  tt.setText(String.valueOf(filter0(v8 * Turbo.tconv))) ;
               }
                 Turbo.tturbin = v8 / Turbo.tconv;
             }
     // turbine efficiency
             if (lunits <= 1) {
                 Turbo.eta[5]  = v1 ;
                 Turbo.vmn1 = Turbo.etmin;
                 Turbo.vmx1 = Turbo.etmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.eta[5] = v1 = Turbo.vmn1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.eta[5] = v1 = Turbo.vmx1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
              }
              if (lunits == 2) {
      // Turbine efficiency
                  Turbo.vmx1 = 100.0 - 100.0 * Turbo.et5ref;
                  Turbo.vmn1 = Turbo.vmx1 - 20.0 ;
                 if(v1 < Turbo.vmn1) {
                    v1 = Turbo.vmn1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                 if(v1 > Turbo.vmx1) {
                    v1 = Turbo.vmx1;
                    fl1 = (float) v1 ;
                    f1.setText(String.valueOf(fl1)) ;
                 }
                  Turbo.eta[5] = Turbo.et5ref + v1 / 100. ;
              }
        
              i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
   
              right.s1.setValue(i1) ;

              solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end Turb

     class Nozl extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Nozl (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1,s2,s3;
           Label lmat ;
           Choice arch, nmat ;

           Right (Turbo target) {
    
               int i1, i2, i3  ;
   
               outerparent = target ;
               setLayout(new GridLayout(7,1,10,5)) ;
    
               i1 = (int) (((Turbo.tt7d - Turbo.t7min) / (Turbo.t7max - Turbo.t7min)) * 1000.) ;
               i2 = (int) (((Turbo.eta[7] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
               i3 = (int) (((Turbo.a8rat - Turbo.a8min) / (Turbo.a8max - Turbo.a8min)) * 1000.) ;
   
               s1 = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
               s2 = new Scrollbar(Scrollbar.HORIZONTAL,i2,10,0,1000);
               s3 = new Scrollbar(Scrollbar.HORIZONTAL,i3,10,0,1000);
   
               nmat = new Choice() ;
               nmat.setBackground(Color.white) ;
               nmat.setForeground(Color.blue) ;
               nmat.addItem("<-- My Material") ;
               nmat.addItem("Titanium ");
               nmat.addItem("Stainless Steel");
               nmat.addItem("Nickel Alloy");
               nmat.addItem("Ceramic");
               nmat.addItem("Passively Cooled");
               nmat.select(3) ;
   
               arch = new Choice() ;
               arch.addItem("Compute A8/A2") ;
               arch.addItem("Input A8/A2");
               arch.select(0) ;
 
               lmat = new Label("lbm/ft^3 ", Label.LEFT) ;
               lmat.setForeground(Color.blue) ;

               add(arch) ;
               add(s3) ;
               add(s1) ;
               add(s2) ;
               add(new Label(" ", Label.LEFT)) ;
               add(nmat) ;
               add(lmat) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleMat(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
          public void handleMat(Event evt) {
               Double V1,V2 ;
               double v1,v2 ;

               arsched = arch.getSelectedIndex() ;
               if (arsched == 0) {
                  left.f3.setBackground(Color.black) ;
                  left.f3.setForeground(Color.yellow) ;
               }
               if (arsched == 1) {
                  left.f3.setBackground(Color.white) ;
                  left.f3.setForeground(Color.black) ;
               }
                // nozzle
              Turbo.mnozl = nmat.getSelectedIndex() ;
               if(Turbo.mnozl > 0) {
                  left.tn.setBackground(Color.black) ;
                  left.tn.setForeground(Color.yellow) ;
                  left.dn.setBackground(Color.black) ;
                  left.dn.setForeground(Color.yellow) ;
               }
               if (Turbo.mnozl == 0) {
                  left.tn.setBackground(Color.white) ;
                  left.tn.setForeground(Color.black) ;
                  left.dn.setBackground(Color.white) ;
                  left.dn.setForeground(Color.black) ;
               }
               switch (Turbo.mnozl) {
                   case 0: {
                        V1 = Double.valueOf(left.dn.getText()) ;
                        v1 = V1.doubleValue() ;
                        V2 = Double.valueOf(left.tn.getText()) ;
                        v2 = V2.doubleValue() ;
                       Turbo.dnozl = v1 / Turbo.dconv;
                       Turbo.tnozl = v2 / Turbo.tconv;
                        break ;
                   }
                   case 1:
                       Turbo.dnozl = 293.02 ;
                       Turbo.tnozl = 1500.; break ;
                   case 2:
                       Turbo.dnozl = 476.56 ;
                       Turbo.tnozl = 2000.; break ;
                   case 3:
                       Turbo.dnozl = 515.2 ;
                       Turbo.tnozl = 2500.; break ;
                   case 4:
                       Turbo.dnozl = 164.2 ;
                       Turbo.tnozl = 3000.; break ;
                   case 5:
                       Turbo.dnozl = 400.2 ;
                       Turbo.tnozl = 4100.; break ;
               }
               solve.comPute() ;
          }

          public void handleBar(Event evt) {     // nozzle design
            int i1, i2,i3 ;
            double v1,v2,v3 ;
            float fl1, fl2, fl3 ;

            i1 = s1.getValue() ;
            i2 = s2.getValue() ;
            i3 = s3.getValue() ;

            if (lunits <= 1) {
                Turbo.vmn1 = Turbo.t7min;
                Turbo.vmx1 = Turbo.t7max;
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                Turbo.vmn3 = Turbo.a8min;
                Turbo.vmx3 = Turbo.a8max;
            }
            if (lunits == 2) {
                Turbo.vmn1 = -10.0 ;
                Turbo.vmx1 = 10.0 ;
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
                Turbo.vmn3 = -10.0 ;
                Turbo.vmx3 = 10.0 ;
            }

            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;
         
            fl1 = (float) v1 ;
            fl2 = (float) v2 ;
            fl3 = filter3(v3) ;

// nozzle design
            if (lunits <= 1) {
                Turbo.tt7d = v1 ;
                Turbo.eta[7]  = v2 ;
                Turbo.a8rat = v3 ;
            }
            if (lunits == 2) {
                Turbo.tt7d = v1 * Turbo.t7ref / 100. + Turbo.t7ref;
                Turbo.eta[7]  = Turbo.et7ref + v2 / 100. ;
                Turbo.a8rat = v3 * Turbo.a8ref / 100. + Turbo.a8ref;
            }
              Turbo.tt7 = Turbo.tt7d / Turbo.tconv;

            left.f1.setText(String.valueOf(fl1)) ;
            left.f2.setText(String.valueOf(fl2)) ;
            left.f3.setText(String.valueOf(fl3)) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, f2, f3, dn, tn ;
           Label l1, l2, l3, l5, lmat, lm2 ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(7,2,5,5)) ;
     
              l1 = new Label("Tmax -R", Label.CENTER) ;
              f1 = new TextField(String.valueOf((float)Turbo.tt7d), 5) ;

              l2 = new Label("Efficiency", Label.CENTER) ;
              f2 = new TextField(String.valueOf((float)Turbo.eta[7]), 5) ;

              f3 = new TextField(String.valueOf((float)Turbo.a8rat), 5) ;
              f3.setBackground(Color.black) ;
              f3.setForeground(Color.yellow) ;

              lmat = new Label("T lim-R", Label.CENTER) ;
              lmat.setForeground(Color.blue) ;
              lm2 = new Label("Materials:", Label.CENTER) ;
              lm2.setForeground(Color.blue) ;
              l5 = new Label("Density", Label.CENTER) ;
              l5.setForeground(Color.blue) ;
  
              dn = new TextField(String.valueOf((float)Turbo.dnozl), 5) ;
              dn.setBackground(Color.black) ;
              dn.setForeground(Color.yellow) ;
              tn = new TextField(String.valueOf((float)Turbo.tnozl), 5) ;
              tn.setBackground(Color.black) ;
              tn.setForeground(Color.yellow) ;
      
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;

              add(new Label("A8/A2 ", Label.CENTER)) ;
              add(f3) ;

              add(l1) ;
              add(f1) ;

              add(l2) ;
              add(f2) ;

              add(lm2) ;
              add(new Label(" ", Label.CENTER)) ;

              add(lmat) ;
              add(tn) ;

              add(l5) ;
              add(dn) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V1,V2,V3,V7,V8 ;
             double v1,v2,v3,v7,v8 ;
             int i1,i2,i3 ;
             float fl1 ;

             V1 = Double.valueOf(f1.getText()) ;
             v1 = V1.doubleValue() ;
             V2 = Double.valueOf(f2.getText()) ;
             v2 = V2.doubleValue() ;
             V3 = Double.valueOf(f3.getText()) ;
             v3 = V3.doubleValue() ;
             V7 = Double.valueOf(dn.getText()) ;
             v7 = V7.doubleValue() ;
             V8 = Double.valueOf(tn.getText()) ;
             v8 = V8.doubleValue() ;

    // Materials
             if (Turbo.mnozl == 0) {
                if (v7 <= 1.0 * Turbo.dconv) {
                   v7 = 1.0 * Turbo.dconv;
                   dn.setText(String.valueOf(filter0(v7 * Turbo.dconv))) ;
                }
                 Turbo.dnozl = v7 / Turbo.dconv;
                if (v8 <= 500. * Turbo.tconv) {
                   v8 = 500. * Turbo.tconv;
                   tn.setText(String.valueOf(filter0(v8 * Turbo.tconv))) ;
                }
                 Turbo.tnozl = v8 / Turbo.tconv;
             }

             if (lunits <= 1) {
    // Max afterburner temp
                 Turbo.tt7d = v1 ;
                 Turbo.vmn1 = Turbo.t7min;
                 Turbo.vmx1 = Turbo.t7max;
                if(v1 < Turbo.vmn1) {
                    Turbo.tt7d = v1 = Turbo.vmn1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.tt7d = v1 = Turbo.vmx1;
                   fl1 = (float) v1 ;
                   f1.setText(String.valueOf(fl1)) ;
                }
                 Turbo.tt7 = Turbo.tt7d / Turbo.tconv;
    // nozzle  efficiency
                 Turbo.eta[7] = v2 ;
                 Turbo.vmn2 = Turbo.etmin;
                 Turbo.vmx2 = Turbo.etmax;
                if(v2 < Turbo.vmn2) {
                    Turbo.eta[7] = v2 = Turbo.vmn2;
                   fl1 = (float) v2 ;
                   f2.setText(String.valueOf(fl1)) ;
                }
                if(v2 > Turbo.vmx2) {
                    Turbo.eta[7] = v2 = Turbo.vmx2;
                   fl1 = (float) v2 ;
                   f2.setText(String.valueOf(fl1)) ;
                }
    //  nozzle area ratio
                 Turbo.a8rat = v3 ;
                 Turbo.vmn3 = Turbo.a8min;
                 Turbo.vmx3 = Turbo.a8max;
                if(v3 < Turbo.vmn3) {
                    Turbo.a8rat = v3 = Turbo.vmn3;
                   fl1 = (float) v3 ;
                   f3.setText(String.valueOf(fl1)) ;
                }
                if(v3 > Turbo.vmx3) {
                    Turbo.a8rat = v3 = Turbo.vmx3;
                   fl1 = (float) v3 ;
                   f3.setText(String.valueOf(fl1)) ;
                }
              }
              if (lunits == 2) {
    // Max afterburner temp
                  Turbo.vmn1 = -10.0;
                  Turbo.vmx1 = 10.0 ;
               if(v1 < Turbo.vmn1) {
                  v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                  v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  f1.setText(String.valueOf(fl1)) ;
               }
                  Turbo.tt7d = v1 * Turbo.t7ref / 100. + Turbo.t7ref;
                  Turbo.tt7 = Turbo.tt7d / Turbo.tconv;
    // nozzl e  efficiency
                  Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
                  Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
               if(v2 < Turbo.vmn2) {
                  v2 = Turbo.vmn2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
               if(v2 > Turbo.vmx2) {
                  v2 = Turbo.vmx2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
                  Turbo.eta[7] = Turbo.et7ref + v2 / 100. ;
     //  nozzle area ratio
                  Turbo.vmn3 = -10.0 ;
                  Turbo.vmx3 = 10.0 ;
               if(v3 < Turbo.vmn3) {
                  v3 = Turbo.vmn3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
               if(v3 > Turbo.vmx3) {
                  v3 = Turbo.vmx3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
                  Turbo.a8rat = v3 * Turbo.a8ref / 100. + Turbo.a8ref;
             }

             i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
             i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
             i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;
   
             right.s1.setValue(i1) ;
             right.s2.setValue(i2) ;
             right.s3.setValue(i3) ;

             solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end nozl

     class Plot extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Plot (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Button takbt ;
           Scrollbar splt;

           Right (Turbo target) {
    
               int i1 ;
   
               outerparent = target ;
               setLayout(new GridLayout(5,1,10,5)) ;
    
               takbt = new Button("Take Data") ;
               takbt.setBackground(Color.blue) ;
               takbt.setForeground(Color.white) ;

               i1 = (int) (((Turbo.u0d - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
   
               splt = new Scrollbar(Scrollbar.HORIZONTAL,i1,10,0,1000);
               splt.setBackground(Color.white) ;
               splt.setForeground(Color.red) ;
 
               add(takbt) ;
               add(new Label(" ", Label.CENTER)) ;
               add(new Label(" ", Label.CENTER)) ;
               add(splt) ;
               add(new Label(" ", Label.CENTER)) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleBut(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
          public void handleBar(Event evt) {     //  generate plot
            int i1 ;
            double v1 ;
            float fl1 ;

            i1 = splt.getValue() ;
            if (nabs == 3) {  //  speed
                Turbo.vmn1 = Turbo.u0min;
                Turbo.vmx1 = Turbo.u0max;
            }
            if (nabs == 4) {  //  altitude
                Turbo.vmn2 = Turbo.altmin;
                Turbo.vmx2 = Turbo.altmax;
            }
            if (nabs == 5) {  //  throttle
                Turbo.vmn3 = Turbo.thrmin;
                Turbo.vmx3 = Turbo.thrmax;
            }
            if (nabs == 6) {  //  cpr
                Turbo.vmn1 = Turbo.cprmin;
                Turbo.vmx1 = Turbo.cprmax;
            }
            if (nabs == 7) {  // burner temp
                Turbo.vmn1 = Turbo.t4min;
                Turbo.vmx1 = Turbo.t4max;
            }
            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            fl1 = (float) v1 ;
            if (nabs == 3) {
                Turbo.u0d = v1;
            }
            if (nabs == 4) {
                Turbo.altd = v1;
            }
            if (nabs == 5) {
                Turbo.throtl = v1;
            }
            if (nabs == 6) {
                Turbo.prat[3] = Turbo.p3p2d = v1;
            }
            if (nabs == 7) {
                Turbo.tt4d = v1 ;
                Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
            }
            left.fplt.setText(String.valueOf(fl1)) ;

            solve.comPute() ; 

            switch (nord) {
                case 3: fl1 = (float)Turbo.fnlb; break ;
                case 4: fl1 = (float)Turbo.flflo; break ;
                case 5: fl1 = (float)Turbo.sfc; break ;
                case 6: fl1 = (float)Turbo.epr; break ;
                case 7: fl1 = (float)Turbo.etr; break ;
            }
            left.oplt.setText(String.valueOf(fl1)) ;

          }  // end handle

          public void handleBut(Event evt) {     //  generate plot
             if (npt == 25 ) {
                 return;
             }
             ++npt ;
             switch (nord) {
                 case 3:
                     Turbo.plty[npt] = Turbo.fnlb; break ;
                 case 4:
                     Turbo.plty[npt] = Turbo.flflo; break ;
                 case 5:
                     Turbo.plty[npt] = Turbo.sfc; break ;
                 case 6:
                     Turbo.plty[npt] = Turbo.epr; break ;
                 case 7:
                     Turbo.plty[npt] = Turbo.etr; break ;
             }
             switch (nabs) {
                 case 3:
                     Turbo.pltx[npt] = Turbo.fsmach; break ;
                 case 4:
                     Turbo.pltx[npt] = Turbo.alt; break ;
                 case 5:
                     Turbo.pltx[npt] = Turbo.throtl; break ;
                 case 6:
                     Turbo.pltx[npt] = Turbo.prat[3] ; break ;
                 case 7:
                     Turbo.pltx[npt] = Turbo.tt[4] ; break ;
             }
      
             out.plot.repaint() ;
          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField fplt, oplt ;
           Button strbt, endbt, exitpan ;
           Choice absch, ordch ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(5,2,5,5)) ;
     
              strbt = new Button("Begin") ;
              strbt.setBackground(Color.blue) ;
              strbt.setForeground(Color.white) ;
              endbt = new Button("End") ;
              endbt.setBackground(Color.blue) ;
              endbt.setForeground(Color.white) ;

              ordch = new Choice() ;
              ordch.addItem("Fn") ;
              ordch.addItem("Fuel");
              ordch.addItem("SFC");
              ordch.addItem("EPR");
              ordch.addItem("ETR");
              ordch.select(0) ;
              ordch.setBackground(Color.red) ;
              ordch.setForeground(Color.white) ;
 
              oplt = new TextField(String.valueOf(Turbo.fnlb), 5) ;
              oplt.setBackground(Color.black) ;
              oplt.setForeground(Color.yellow) ;
  
              absch = new Choice() ;
              absch.addItem("Speed") ;
              absch.addItem("Altitude ");
              absch.addItem("Throttle");
              absch.addItem(" CPR   ");
              absch.addItem("Temp 4");
              absch.select(0) ;
              absch.setBackground(Color.red) ;
              absch.setForeground(Color.white) ;

              fplt = new TextField(String.valueOf(Turbo.u0d), 5) ;
              fplt.setBackground(Color.white) ;
              fplt.setForeground(Color.red) ;

              exitpan = new Button("Exit") ;
              exitpan.setBackground(Color.red) ;
              exitpan.setForeground(Color.white) ;

              add(strbt) ;  
              add(endbt) ;  

              add(ordch) ;  
              add(oplt) ;
 
              add(new Label("vs ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;

              add(absch) ;
              add(fplt) ;

              add(exitpan) ;
              add(new Label(" ", Label.CENTER)) ;
           }
     
           public boolean action(Event evt, Object arg) {
               if(evt.target instanceof Button) {
                  this.handlePlot(arg) ;
                  return true ;
               }
               if(evt.target instanceof Choice) {
                  this.handlePlot(arg) ;
                  return true ;
               }
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleText(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
           }

           public void handlePlot(Object arg) {
             String label = (String)arg ;
             int item,i;
             double tempx,tempy;
             double v1 ;
             int i1 ;
             float fl1 ;
      
              nord = 3 + ordch.getSelectedIndex();
              if (nord != ordkeep) {  // set the plot parameters
                if (nord == 3) {  // Thrust 
                    Turbo.laby = String.valueOf("Fn");
                    Turbo.labyu = String.valueOf("lb");
                    Turbo.begy = 0.0 ;
                    Turbo.endy = 100000.0; ntiky = 11 ;
                }
                if (nord == 4) {  //  Fuel
                    Turbo.laby = String.valueOf("Fuel Rate");
                    Turbo.labyu = String.valueOf("lbs/hr");
                    Turbo.begy = 0.0 ;
                    Turbo.endy = 100000.0; ntiky = 11 ;
                }
                if (nord == 5) {  //  TSFC
                    Turbo.laby = String.valueOf("TSFC");
                    Turbo.labyu = String.valueOf("lbm/hr/lb");
                    Turbo.begy = 0.0 ;
                    Turbo.endy = 2.0; ntiky = 11 ;
                }
                if (nord == 6) {  //  EPR
                    Turbo.laby = String.valueOf("EPR");
                    Turbo.labyu = String.valueOf(" ");
                    Turbo.begy = 0.0 ;
                    Turbo.endy = 50.0; ntiky = 11 ;
                }
                if (nord == 7) {  //  ETR
                    Turbo.laby = String.valueOf("ETR");
                    Turbo.labyu = String.valueOf(" ");
                    Turbo.begy = 0.0 ;
                    Turbo.endy = 50.0; ntiky = 11 ;
                }
                ordkeep = nord ;
                npt = 0 ;
                lines = 0 ;
              }

              nabs = 3 + absch.getSelectedIndex();
               v1 = Turbo.u0d;
               if (nabs != abskeep) {  // set the plot parameters
                if (nabs == 3) {  //  speed
                    Turbo.labx = String.valueOf("Mach");
                    Turbo.labxu = String.valueOf(" ");
                   if (entype <=2) {
                       Turbo.begx = 0.0 ;
                       Turbo.endx = 2.0; ntikx = 5 ;
                   }
                   if (entype ==3) {
                       Turbo.begx = 0.0 ;
                       Turbo.endx = 6.0; ntikx = 5 ;
                   }
                   v1 = Turbo.u0d;
                    Turbo.vmn1 = Turbo.u0min;
                    Turbo.vmx1 = Turbo.u0max;
                }
                if (nabs == 4) {  //  altitude
                    Turbo.labx = String.valueOf("Alt");
                    Turbo.labxu = String.valueOf("ft");
                    Turbo.begx = 0.0 ;
                    Turbo.endx = 60000.0; ntikx = 4 ;
                   v1 = Turbo.altd;
                    Turbo.vmn1 = Turbo.altmin;
                    Turbo.vmx1 = Turbo.altmax;
                }
                if (nabs == 5) {  //  throttle
                    Turbo.labx = String.valueOf("Throttle");
                    Turbo.labxu = String.valueOf(" %");
                    Turbo.begx = 0.0 ;
                    Turbo.endx = 100.0; ntikx = 5 ;
                   v1 = Turbo.throtl;
                    Turbo.vmn1 = Turbo.thrmin;
                    Turbo.vmx1 = Turbo.thrmax;
                }
                if (nabs == 6) {  //  Compressor pressure ratio
                    Turbo.labx = String.valueOf("CPR");
                    Turbo.labxu = String.valueOf(" ");
                    Turbo.begx = 0.0 ;
                    Turbo.endx = 50.0; ntikx = 6 ;
                   v1 = Turbo.p3p2d;
                    Turbo.vmn1 = Turbo.cprmin;
                    Turbo.vmx1 = Turbo.cprmax;
                }
                if (nabs == 7) {  // Burner temp
                    Turbo.labx = String.valueOf("Temp");
                    Turbo.labxu = String.valueOf("R");
                    Turbo.begx = 1000.0 ;
                    Turbo.endx = 4000.0; ntikx = 4 ;
                   v1 = Turbo.tt4d;
                    Turbo.vmn1 = Turbo.t4min;
                    Turbo.vmx1 = Turbo.t4max;
                }
                fl1 = (float) v1 ;
                fplt.setText(String.valueOf(fl1)) ;
                i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
                right.splt.setValue(i1) ;
                abskeep = nabs ;
                npt = 0 ;
                lines = 0 ;
              }

              if (label.equals("Begin")) {
                npt = 0 ;
                lines = 0 ;
              }
      
              if (label.equals("End")) {
                lines = 1 ;
                for (item=1; item<=npt-1; ++item) {
                  for (i=item+1; i<=npt; ++i) {
                     if (Turbo.pltx[i] < Turbo.pltx[item]) {
                          tempx = Turbo.pltx[item];
                          tempy = Turbo.plty[item];
                         Turbo.pltx[item] = Turbo.pltx[i];
                         Turbo.plty[item] = Turbo.plty[i];
                         Turbo.pltx[i] = tempx;
                         Turbo.plty[i] = tempy;
                      }
                  }
                }
              }
   
              if (label.equals("Exit")) {
                 varflag = 0 ;
                 layin.show(in, "first")  ;
                 con.up.pltch.select(0) ;
                 solve.loadMine() ;
                 plttyp = 3 ;
                 con.setPlot() ;
                 con.up.untch.select(0) ;
                 con.up.engch.select(0) ;
                 con.up.modch.select(inflag) ;
              }
      
              solve.comPute() ;
           }
       
           public void handleText(Event evt) {
             Double V1 ;
             double v1 ;
             int i1 ;
             float fl1 ;
  
             V1 = Double.valueOf(fplt.getText()) ;
             v1 = V1.doubleValue() ;
             fl1 = (float) v1 ;
             if (nabs == 3) {  //  speed
                 Turbo.u0d = v1 ;
                 Turbo.vmn1 = Turbo.u0min;
                 Turbo.vmx1 = Turbo.u0max;
               if(v1 < Turbo.vmn1) {
                   Turbo.u0d = v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                   Turbo.u0d = v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
             }
             if (nabs == 4) {  //  altitude
                 Turbo.altd = v1 ;
                 Turbo.vmn1 = Turbo.altmin;
                 Turbo.vmx1 = Turbo.altmax;
               if(v1 < Turbo.vmn1) {
                   Turbo.altd = v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                   Turbo.altd = v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
             }
             if (nabs == 5) {  //  throttle
                 Turbo.throtl = v1 ;
                 Turbo.vmn1 = Turbo.thrmin;
                 Turbo.vmx1 = Turbo.thrmax;
               if(v1 < Turbo.vmn1) {
                   Turbo.throtl = v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                   Turbo.throtl = v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
             }
             if (nabs == 6) {  //  Compressor pressure ratio
                 Turbo.prat[3] = Turbo.p3p2d = v1 ;
                 Turbo.vmn1 = Turbo.cprmin;
                 Turbo.vmx1 = Turbo.cprmax;
               if(v1 < Turbo.vmn1) {
                   Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmn1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
               if(v1 > Turbo.vmx1) {
                   Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmx1;
                  fl1 = (float) v1 ;
                  fplt.setText(String.valueOf(fl1)) ;
               }
             }
             if (nabs == 7) {  // Burner temp
                 Turbo.tt4d = v1 ;
                 Turbo.vmn1 = Turbo.t4min;
                 Turbo.vmx1 = Turbo.t4max;
                  if(v1 < Turbo.vmn1) {
                      Turbo.tt4d = v1 = Turbo.vmn1;
                     fl1 = (float) v1 ;
                     fplt.setText(String.valueOf(fl1)) ;
                  }
                  if(v1 > Turbo.vmx1) {
                      Turbo.tt4d = v1 = Turbo.vmx1;
                     fl1 = (float) v1 ;
                     fplt.setText(String.valueOf(fl1)) ;
                  }
                 Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
             }
             i1 = (int) (((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
             right.splt.setValue(i1) ;

             solve.comPute() ;

             switch (nord) {
                case 3: fl1 = (float)Turbo.fnlb; break ;
                case 4: fl1 = (float)Turbo.flflo; break ;
                case 5: fl1 = (float)Turbo.sfc; break ;
                case 6: fl1 = (float)Turbo.epr; break ;
                case 7: fl1 = (float)Turbo.etr; break ;
             }
             oplt.setText(String.valueOf(fl1)) ;

           }  // end handle
         }  //  end  left
     }  // end Plot

     class Nozr extends Panel {
        Turbo outerparent ;
        Right right ;
        Left left ;

        Nozr (Turbo target) {
                               
          outerparent = target ;
          setLayout(new GridLayout(1,2,10,10)) ;

          left = new Left(outerparent) ; 
          right = new Right(outerparent) ;
 
          add(left) ;
          add(right) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }
 
        class Right extends Panel {
           Turbo outerparent ;
           Scrollbar s1,s2,s3,s4;
           Label lmat ;
           Choice nrmat ;
           Choice atch,aech ;

           Right (Turbo target) {
    
               int i2, i3, i4  ;
   
               outerparent = target ;
               setLayout(new GridLayout(7,1,10,5)) ;
    
               i2 = (int) (((Turbo.eta[7] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.) ;
               i3 = (int) (((Turbo.arthd - Turbo.arthmn) / (Turbo.arthmx - Turbo.arthmn)) * 1000.) ;
               i4 = (int) (((Turbo.arexitd - Turbo.arexmn) / (Turbo.arexmx - Turbo.arexmn)) * 1000.) ;
   
               s2 = new Scrollbar(Scrollbar.HORIZONTAL,i2,10,0,1000);
               s3 = new Scrollbar(Scrollbar.HORIZONTAL,i3,10,0,1000);
               s4 = new Scrollbar(Scrollbar.HORIZONTAL,i4,10,0,1000);
   
               nrmat = new Choice() ;
               nrmat.setBackground(Color.white) ;
               nrmat.setForeground(Color.blue) ;
               nrmat.addItem("<-- My Material") ;
               nrmat.addItem("Titanium ");
               nrmat.addItem("Stainless Steel");
               nrmat.addItem("Nickel Alloy");
               nrmat.addItem("Ceramic");
               nrmat.addItem("Actively Cooled");
               nrmat.select(5) ;

               atch = new Choice() ;
               atch.addItem("Calculate A7/A2") ;
               atch.addItem("Input A7/A2");
               atch.select(1) ;

               aech = new Choice() ;
               aech.addItem("Calculate A8/A7") ;
               aech.addItem("Input A8/A7");
               aech.select(1) ;
   
               lmat = new Label("lbm/ft^3", Label.LEFT) ;
               lmat.setForeground(Color.blue) ;

               add(atch) ;
               add(s3) ;
               add(s2) ;
               add(s4) ;
               add(aech) ;
               add(nrmat) ;
               add(lmat) ;
           }

           public boolean handleEvent(Event evt) {
               if(evt.id == Event.ACTION_EVENT) {
                  this.handleMat(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_ABSOLUTE) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_LINE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_DOWN) {
                  this.handleBar(evt) ;
                  return true ;
               }
               if(evt.id == Event.SCROLL_PAGE_UP) {
                  this.handleBar(evt) ;
                  return true ;
               }
               else {
                   return false;
               }
          }
   
          public void handleMat(Event evt) {
              Double V1,V2 ;
              double v1,v2 ;

              athsched = atch.getSelectedIndex() ;
               if (athsched == 0) {
                  left.f3.setBackground(Color.black) ;
                  left.f3.setForeground(Color.yellow) ;
               }
               if (athsched == 1) {
                  left.f3.setBackground(Color.white) ;
                  left.f3.setForeground(Color.black) ;
               }
              aexsched = aech.getSelectedIndex() ;
               if (aexsched == 0) {
                  left.f4.setBackground(Color.black) ;
                  left.f4.setForeground(Color.yellow) ;
               }
               if (aexsched == 1) {
                  left.f4.setBackground(Color.white) ;
                  left.f4.setForeground(Color.black) ;
               }

                // ramjet burner - nozzle
              Turbo.mnozr = nrmat.getSelectedIndex() ;
               if(Turbo.mnozr > 0) {
                  left.tn.setBackground(Color.black) ;
                  left.tn.setForeground(Color.yellow) ;
               }
               if (Turbo.mnozr == 0) {
                  left.tn.setBackground(Color.white) ;
                  left.tn.setForeground(Color.black) ;
               }
               switch (Turbo.mnozr) {
                   case 0: {
                        V1 = Double.valueOf(left.dn.getText()) ;
                        v1 = V1.doubleValue() ;
                        V2 = Double.valueOf(left.tn.getText()) ;
                        v2 = V2.doubleValue() ;
                       Turbo.dnozr = v1 / Turbo.dconv;
                       Turbo.tnozr = v2 / Turbo.tconv;
                        break ;
                   }
                   case 1:
                       Turbo.dnozr = 293.02 ;
                       Turbo.tnozr = 1500.; break ;
                   case 2:
                       Turbo.dnozr = 476.56 ;
                       Turbo.tnozr = 2000.; break ;
                   case 3:
                       Turbo.dnozr = 515.2 ;
                       Turbo.tnozr = 2500.; break ;
                   case 4:
                       Turbo.dnozr = 164.2 ;
                       Turbo.tnozr = 3000.; break ;
                   case 5:
                       Turbo.dnozr = 515.2 ;
                       Turbo.tnozr = 4500.; break ;
               }
               solve.comPute() ;
          }

          public void handleBar(Event evt) { // ramjet burn -nozzle design
            int i2,i3,i4 ;
            double v2,v3,v4 ;
            float  fl2, fl3, fl4 ;

            i2 = s2.getValue() ;
            i3 = s3.getValue() ;
            i4 = s4.getValue() ;

            if (lunits <= 1) {
                Turbo.vmn2 = Turbo.etmin;
                Turbo.vmx2 = Turbo.etmax;
                Turbo.vmn3 = Turbo.arthmn;
                Turbo.vmx3 = Turbo.arthmx;
                Turbo.vmn4 = Turbo.arexmn;
                Turbo.vmx4 = Turbo.arexmx;
            }
            if (lunits == 2) {
                Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
                Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
                Turbo.vmn3 = -10.0 ;
                Turbo.vmx3 = 10.0 ;
                Turbo.vmn4 = -10.0 ;
                Turbo.vmx4 = 10.0 ;
            }

            v2 = i2 * (Turbo.vmx2 - Turbo.vmn2) / 1000. + Turbo.vmn2;
            v3 = i3 * (Turbo.vmx3 - Turbo.vmn3) / 1000. + Turbo.vmn3;
            v4 = i4 * (Turbo.vmx4 - Turbo.vmn4) / 1000. + Turbo.vmn4;
         
            fl2 = (float) v2 ;
            fl3 = (float) v3 ;
            fl4 = (float) v4 ;

// nozzle design
            if (lunits <= 1) {
                Turbo.eta[7]  = v2 ;
                Turbo.arthd = v3 ;
                Turbo.arexitd = v4 ;
            }
            if (lunits == 2) {
                Turbo.eta[7]  = Turbo.et7ref + v2 / 100. ;
                Turbo.arthd = v3 * Turbo.a8ref / 100. + Turbo.a8ref;
                Turbo.arexitd = v4 * Turbo.a8ref / 100. + Turbo.a8ref;
            }

            left.f2.setText(String.valueOf(fl2)) ;
            left.f3.setText(String.valueOf(fl3)) ;
            left.f4.setText(String.valueOf(fl4)) ;

            solve.comPute() ; 

          }  // end handle
        }  // end right
   
        class Left extends Panel {
           Turbo outerparent ;
           TextField f1, f2, f3, f4, dn, tn ;
           Label l1, l2, l3, l5, lmat, lm2 ;
   
           Left (Turbo target) { 
               
              outerparent = target ;
              setLayout(new GridLayout(7,2,5,5)) ;
     
              l2 = new Label("Efficiency", Label.CENTER) ;
              f2 = new TextField(String.valueOf((float)Turbo.eta[7]), 5) ;
              f3 = new TextField(String.valueOf((float)Turbo.arthd), 5) ;
              f3.setForeground(Color.black) ;
              f3.setBackground(Color.white) ;
              f4 = new TextField(String.valueOf((float)Turbo.arexitd), 5) ;
              f4.setForeground(Color.black) ;
              f4.setBackground(Color.white) ;
              lmat = new Label("T lim-R", Label.CENTER) ;
              lmat.setForeground(Color.blue) ;
              lm2 = new Label("Materials:", Label.CENTER) ;
              lm2.setForeground(Color.blue) ;
              l5 = new Label("Density", Label.CENTER) ;
              l5.setForeground(Color.blue) ;
  
              dn = new TextField(String.valueOf((float)Turbo.dnozr), 5) ;
              dn.setBackground(Color.black) ;
              dn.setForeground(Color.yellow) ;
              tn = new TextField(String.valueOf((float)Turbo.tnozr), 5) ;
              tn.setBackground(Color.black) ;
              tn.setForeground(Color.yellow) ;
      
              add(new Label(" ", Label.CENTER)) ;
              add(new Label(" ", Label.CENTER)) ;
              add(new Label("A7/A2", Label.CENTER)) ;
              add(f3) ;
              add(l2) ;
              add(f2) ;
              add(new Label("A8/A7", Label.CENTER)) ;
              add(f4) ;
              add(lm2) ;
              add(new Label(" ", Label.CENTER)) ;
              add(lmat) ;
              add(tn) ;
              add(l5) ;
              add(dn) ;
           }
     
           public boolean handleEvent(Event evt) {
             if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt) ;
                return true ;
             }
             else {
                 return false;
             }
           }
   
           public void handleText(Event evt) {
             Double V2,V3,V4,V7,V8 ;
             double v2,v3,v4,v7,v8 ;
             int i2,i3,i4 ;
             float fl1 ;

             V2 = Double.valueOf(f2.getText()) ;
             v2 = V2.doubleValue() ;
             V3 = Double.valueOf(f3.getText()) ;
             v3 = V3.doubleValue() ;
             V4 = Double.valueOf(f4.getText()) ;
             v4 = V4.doubleValue() ;
             V7 = Double.valueOf(dn.getText()) ;
             v7 = V7.doubleValue() ;
             V8 = Double.valueOf(tn.getText()) ;
             v8 = V8.doubleValue() ;

    // Materials
             if (Turbo.mnozr == 0) {
                if (v7 <= 1.0 * Turbo.dconv) {
                   v7 = 1.0 * Turbo.dconv;
                   dn.setText(String.valueOf(filter0(v7 * Turbo.dconv))) ;
                }
                 Turbo.dnozr = v7 / Turbo.dconv;
                if (v8 <= 500. * Turbo.tconv) {
                   v8 = 500. * Turbo.tconv;
                   tn.setText(String.valueOf(filter0(v8 * Turbo.tconv))) ;
                }
                 Turbo.tnozr = v8 / Turbo.tconv;
             }

             if (lunits <= 1) {
    // nozzle  efficiency
                 Turbo.eta[7] = v2 ;
                 Turbo.vmn2 = Turbo.etmin;
                 Turbo.vmx2 = Turbo.etmax;
                if(v2 < Turbo.vmn2) {
                    Turbo.eta[7] = v2 = Turbo.vmn2;
                   fl1 = (float) v2 ;
                   f2.setText(String.valueOf(fl1)) ;
                }
                if(v2 > Turbo.vmx2) {
                    Turbo.eta[7] = v2 = Turbo.vmx2;
                   fl1 = (float) v2 ;
                   f2.setText(String.valueOf(fl1)) ;
                }
    //  throat area ratio
                 Turbo.arthd = v3 ;
                 Turbo.vmn3 = Turbo.arthmn;
                 Turbo.vmx3 = Turbo.arthmx;
                if(v3 < Turbo.vmn3) {
                    Turbo.arthd = v3 = Turbo.vmn3;
                   fl1 = (float) v3 ;
                   f3.setText(String.valueOf(fl1)) ;
                }
                if(v3 > Turbo.vmx3) {
                    Turbo.arthd = v3 = Turbo.vmx3;
                   fl1 = (float) v3 ;
                   f3.setText(String.valueOf(fl1)) ;
                }
    //  exit area ratio
                 Turbo.arexitd = v4 ;
                 Turbo.vmn4 = Turbo.arexmn;
                 Turbo.vmx4 = Turbo.arexmx;
                if(v4 < Turbo.vmn4) {
                    Turbo.arexitd = v4 = Turbo.vmn4;
                   fl1 = (float) v4 ;
                   f4.setText(String.valueOf(fl1)) ;
                }
                if(v4 > Turbo.vmx4) {
                    Turbo.arexitd = v4 = Turbo.vmx4;
                   fl1 = (float) v4 ;
                   f4.setText(String.valueOf(fl1)) ;
                }
              }

              if (lunits == 2) {
    // nozzle efficiency
                  Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
                  Turbo.vmn2 = Turbo.vmx2 - 20.0 ;
               if(v2 < Turbo.vmn2) {
                  v2 = Turbo.vmn2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
               if(v2 > Turbo.vmx2) {
                  v2 = Turbo.vmx2;
                  fl1 = (float) v2 ;
                  f2.setText(String.valueOf(fl1)) ;
               }
                  Turbo.eta[7] = Turbo.et7ref + v2 / 100. ;
     //  throat area ratio
                  Turbo.vmn3 = -10.0 ;
                  Turbo.vmx3 = 10.0 ;
               if(v3 < Turbo.vmn3) {
                  v3 = Turbo.vmn3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
               if(v3 > Turbo.vmx3) {
                  v3 = Turbo.vmx3;
                  fl1 = (float) v3 ;
                  f3.setText(String.valueOf(fl1)) ;
               }
                  Turbo.arthd = v3 * Turbo.a8ref / 100. + Turbo.a8ref;
     //  exit area ratio
                  Turbo.vmn4 = -10.0 ;
                  Turbo.vmx4 = 10.0 ;
               if(v4 < Turbo.vmn4) {
                  v4 = Turbo.vmn4;
                  fl1 = (float) v4 ;
                  f4.setText(String.valueOf(fl1)) ;
               }
               if(v4 > Turbo.vmx4) {
                  v4 = Turbo.vmx4;
                  fl1 = (float) v4 ;
                  f4.setText(String.valueOf(fl1)) ;
               }
                  Turbo.arexitd = v4 * Turbo.a8ref / 100. + Turbo.a8ref;
             }

             i2 = (int) (((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.) ;
             i3 = (int) (((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.) ;
             i4 = (int) (((v4 - Turbo.vmn4) / (Turbo.vmx4 - Turbo.vmn4)) * 1000.) ;
   
             right.s2.setValue(i2) ;
             right.s3.setValue(i3) ;
             right.s4.setValue(i4) ;

             solve.comPute() ;

          }  // end handle
        }  //  end  left
     }  // end nozr
 
     class Limt extends Panel {
        Turbo outerparent ;
        TextField f1, f2, f3, f4, f5, f6, f7, f8 ;
        TextField f9, f10, f11, f12 ;
        Label l1, l2, l3, l4, l5, l6, l7, l8 ;
        Label l9, l10, l11, l12 ;
        Button submit ;

        Limt (Turbo target) {

          outerparent = target ;
          setLayout(new GridLayout(6,4,10,10)) ;

          l1 = new Label("Speed-max", Label.CENTER) ;
          f1 = new TextField(String.valueOf((float)Turbo.u0max), 5) ;
          l2 = new Label("Alt-max", Label.CENTER) ;
          f2 = new TextField(String.valueOf((float)Turbo.altmax), 5) ;
          l3 = new Label("A2-min", Label.CENTER) ;
          f3 = new TextField(String.valueOf((float)Turbo.a2min), 3) ;
          l4 = new Label("A2-max", Label.CENTER) ;
          f4 = new TextField(String.valueOf((float)Turbo.a2max), 5) ;
          l5 = new Label("CPR-max", Label.CENTER) ;
          f5 = new TextField(String.valueOf((float)Turbo.cprmax), 5) ;
          l6 = new Label("T4-max", Label.CENTER) ;
          f6 = new TextField(String.valueOf((float)Turbo.t4max), 5) ;
          l7 = new Label("T7-max", Label.CENTER) ;
          f7 = new TextField(String.valueOf((float)Turbo.t7max), 5) ;
          l9 = new Label("FPR-max", Label.CENTER) ;
          f9 = new TextField(String.valueOf((float)Turbo.fprmax), 5) ;
          l10 = new Label("BPR-max", Label.CENTER) ;
          f10 = new TextField(String.valueOf((float)Turbo.bypmax), 5) ;
          l11 = new Label("Pt4/Pt3-max", Label.CENTER) ;
          f11 = new TextField(String.valueOf((float)Turbo.pt4max), 5) ;

          submit = new Button("Submit") ;
          submit.setBackground(Color.blue) ;
          submit.setForeground(Color.white) ;

          add(l1) ;
          add(f1) ;
          add(l2) ;
          add(f2) ;

          add(l3) ;
          add(f3) ;
          add(l4) ;
          add(f4) ;

          add(l5) ;
          add(f5) ;
          add(l11) ;
          add(f11) ;

          add(l6) ;
          add(f6) ;
          add(l7) ;
          add(f7) ;

          add(l9) ;
          add(f9) ;
          add(l10) ;
          add(f10) ;

          add(new Label("  ", Label.RIGHT)) ;
          add(new Label(" ", Label.RIGHT)) ;
          add(new Label(" Push --> ", Label.RIGHT)) ;
          add(submit) ;
        }

        public Insets insets() {
           return new Insets(5,0,5,0) ;
        }

        public boolean action(Event evt, Object arg) {
          if(evt.target instanceof Button) {
             this.handleText(evt) ;
             return true ;
          }
          else {
              return false;
          }
        }

        public void handleText(Event evt) {
          Double V1,V2,V3,V4 ;
          double v1,v2,v3,v4 ;
          int i1,i2,i3 ;
          float fl1 ;

          V1 = Double.valueOf(f1.getText()) ;
          v1 = V1.doubleValue() ;
          V2 = Double.valueOf(f2.getText()) ;
          v2 = V2.doubleValue() ;
          V3 = Double.valueOf(f3.getText()) ;
          v3 = V3.doubleValue() ;
          V4 = Double.valueOf(f4.getText()) ;
          v4 = V4.doubleValue() ;

            Turbo.u0max = v1 ;
            Turbo.altmax = v2 ;
            Turbo.a2min = v3 ;
            Turbo.a2max = v4 ;
          if (entype <= 2) {
              Turbo.u0mt = Turbo.u0max;
              Turbo.altmt = Turbo.altmax;
          }
          if (entype == 3) {
              Turbo.u0mr = Turbo.u0max;
              Turbo.altmr = Turbo.altmax;
          }

     // look for exceeding limits

          if (Turbo.u0d > Turbo.u0max) {
             if (Turbo.u0max < 0) {
                 Turbo.u0max = Turbo.u0d + .1;
             }
              Turbo.u0d = Turbo.u0max;
          }
          if (Turbo.altd > Turbo.altmax) {
             if (Turbo.altmax < 0) {
                 Turbo.altmax = Turbo.altd + .1;
             }
              Turbo.altd = Turbo.altmax;
          }
          if (Turbo.a2max <= Turbo.a2min) {
              Turbo.a2max = Turbo.a2min + .1;
          }
          if (Turbo.a2d > Turbo.a2max) {
              Turbo.a2d = Turbo.a2max;
              Turbo.a2 = Turbo.a2d / Turbo.aconv;
             if (entype != 2) {
                 Turbo.acore = Turbo.a2;
             }
             if (entype == 2) {
                 Turbo.afan = Turbo.a2;
                 Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat) ;
             }
          }
          if (Turbo.a2d < Turbo.a2min) {
              Turbo.a2d = Turbo.a2min;
              Turbo.a2 = Turbo.a2d / Turbo.aconv;
             if (entype != 2) {
                 Turbo.acore = Turbo.a2;
             }
             if (entype == 2) {
                 Turbo.afan = Turbo.a2;
                 Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat) ;
             }
          }

          V1 = Double.valueOf(f5.getText()) ;
          v1 = V1.doubleValue() ;
          V2 = Double.valueOf(f6.getText()) ;
          v2 = V2.doubleValue() ;
          V3 = Double.valueOf(f7.getText()) ;
          v3 = V3.doubleValue() ;

            Turbo.cprmax = v1 ;
            Turbo.t4max = v2 ;
            Turbo.t7max = v3 ;

     // look for exceeding limits

          if (Turbo.cprmax <= Turbo.cprmin) {
              Turbo.cprmax = Turbo.cprmin + .1;
          }
          if (Turbo.p3p2d > Turbo.cprmax) {
              Turbo.p3p2d = Turbo.cprmax;
          }
          if (Turbo.t4max <= Turbo.t4min) {
              Turbo.t4max = Turbo.t4min + .1;
          }
          if (Turbo.tt4d > Turbo.t4max) {
              Turbo.tt4d = Turbo.t4max;
              Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
          }
          if (Turbo.t7max <= Turbo.t7min) {
              Turbo.t7max = Turbo.t7min + .1;
          }
          if (Turbo.tt7d > Turbo.t7max) {
              Turbo.tt7d = Turbo.t7max;
              Turbo.tt7 = Turbo.tt7d / Turbo.tconv;
          }

          V1 = Double.valueOf(f9.getText()) ;
          v1 = V1.doubleValue() ;
          V2 = Double.valueOf(f10.getText()) ;
          v2 = V2.doubleValue() ;
          V3 = Double.valueOf(f11.getText()) ;
          v3 = V3.doubleValue() ;

            Turbo.fprmax = v1 ;
            Turbo.bypmax = v2 ;
            Turbo.pt4max = v3 ;

          if (Turbo.fprmax <= Turbo.fprmin) {
              Turbo.fprmax = Turbo.fprmin + .1;
          }
          if (Turbo.p3fp2d > Turbo.fprmax) {
              Turbo.p3fp2d = Turbo.fprmax;
          }
          if (Turbo.bypmax <= Turbo.bypmin) {
              Turbo.bypmax = Turbo.bypmin + .1;
          }
          if (Turbo.byprat > Turbo.bypmax) {
              Turbo.byprat = Turbo.bypmax;
              Turbo.acore = Turbo.afan / (1.0 + Turbo.byprat) ;
          }
          if (Turbo.pt4max <= Turbo.etmin) {
              Turbo.pt4max = Turbo.etmin + .1;
          }
          if (Turbo.prat[4] > Turbo.pt4max) {
              Turbo.prat[4] = Turbo.pt4max;
          }

          varflag = 0 ;
          layin.show(in, "first")  ;
          solve.comPute() ;
          con.setPanl() ;

        }  // end handle
     }  // end inlimit
 
     class Files extends Panel {  // save file
        Turbo outerparent ;
        TextField nsavin,nsavout ;
        Button savread,savwrit,cancel ;

        Files (Turbo target) {

          outerparent = target ;
          setLayout(new GridLayout(6,2,10,10)) ;

          nsavin = new TextField() ;
          nsavin.setBackground(Color.white) ;
          nsavin.setForeground(Color.black) ;

          nsavout = new TextField() ;
          nsavout.setBackground(Color.white) ;
          nsavout.setForeground(Color.black) ;

          savread = new Button("Retrieve Data") ;
          savread.setBackground(Color.blue) ;
          savread.setForeground(Color.white) ;

          savwrit = new Button("Save Data") ;
          savwrit.setBackground(Color.red) ;
          savwrit.setForeground(Color.white) ;

          cancel = new Button("Cancel") ;
          cancel.setBackground(Color.yellow) ;
          cancel.setForeground(Color.black) ;

          add(new Label("Enter File Name -  ", Label.RIGHT)) ;
          add(new Label("Then Push Button", Label.LEFT)) ;

          add(new Label("Save Data to File:", Label.RIGHT)) ;
          add(nsavout) ;

          add(new Label(" ", Label.CENTER)) ;
          add(savwrit) ;

          add(new Label("Get Data from File:", Label.RIGHT)) ;
          add(nsavin) ;

          add(new Label(" ", Label.CENTER)) ;
          add(savread) ;

          add(new Label(" ", Label.CENTER)) ;
          add(cancel) ;
       }

       public boolean action(Event evt, Object arg) {
          if(evt.target instanceof Button) {
            this.handleRefs(evt,arg) ;
            return true ;
          }
          else {
              return false;
          }
       }

       public void handleRefs(Event evt, Object arg) {
          String filnam ;
          String label = (String)arg ;

          if(label.equals("Retrieve Data")) {  // Read in saved case
             filnam = nsavin.getText() ;

             try{
                 Turbo.sfili = new FileInputStream(filnam) ;
                 Turbo.savin = new DataInputStream(Turbo.sfili) ;

              inflag = 0 ;
              con.up.modch.select(0) ;
              varflag = 0 ;
              layin.show(in, "first") ;
              lunits = 0 ;
              con.setUnits () ;
              con.up.untch.select(lunits) ;

              entype = Turbo.savin.readInt() ;
              abflag = Turbo.savin.readInt() ;
              fueltype = Turbo.savin.readInt() ;
                 Turbo.fhvd = Turbo.fhv = Turbo.savin.readDouble() ;
                 Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = Turbo.savin.readDouble() ;
                 Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = Turbo.savin.readDouble() ;
                 Turbo.prat[3] = Turbo.p3p2d = Turbo.savin.readDouble() ;
                 Turbo.prat[13] = Turbo.p3fp2d = Turbo.savin.readDouble() ;
                 Turbo.byprat = Turbo.savin.readDouble();
                 Turbo.acore = Turbo.savin.readDouble() ;
                 Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
                 Turbo.a2d = Turbo.a2 = Turbo.savin.readDouble() ;
                 Turbo.a4 = Turbo.savin.readDouble() ;
                 Turbo.a4p = Turbo.savin.readDouble() ;
                 Turbo.ac = .9 * Turbo.a2;
                 Turbo.gama = Turbo.savin.readDouble() ;
              gamopt = Turbo.savin.readInt() ;
              pt2flag = Turbo.savin.readInt() ;
                 Turbo.eta[2] = Turbo.savin.readDouble() ;
                 Turbo.prat[2] = Turbo.savin.readDouble() ;
                 Turbo.prat[4] = Turbo.savin.readDouble() ;
                 Turbo.eta[3] = Turbo.savin.readDouble() ;
                 Turbo.eta[4] = Turbo.savin.readDouble() ;
                 Turbo.eta[5] = Turbo.savin.readDouble() ;
                 Turbo.eta[7] = Turbo.savin.readDouble() ;
                 Turbo.eta[13] = Turbo.savin.readDouble() ;
                 Turbo.a8d = Turbo.savin.readDouble() ;
                 Turbo.a8max = Turbo.savin.readDouble() ;
                 Turbo.a8rat = Turbo.savin.readDouble() ;

                 Turbo.u0max = Turbo.savin.readDouble() ;
                 Turbo.u0d = Turbo.savin.readDouble() ;
                 Turbo.altmax = Turbo.savin.readDouble();
                 Turbo.altd = Turbo.savin.readDouble() ;
              arsched = Turbo.savin.readInt() ;
  
              wtflag = Turbo.savin.readInt() ;
                 Turbo.weight = Turbo.savin.readDouble() ;
                 Turbo.minlt = Turbo.savin.readInt() ;
                 Turbo.dinlt = Turbo.savin.readDouble() ;
                 Turbo.tinlt = Turbo.savin.readDouble() ;
                 Turbo.mfan = Turbo.savin.readInt() ;
                 Turbo.dfan = Turbo.savin.readDouble() ;
                 Turbo.tfan = Turbo.savin.readDouble() ;
                 Turbo.mcomp = Turbo.savin.readInt() ;
                 Turbo.dcomp = Turbo.savin.readDouble() ;
                 Turbo.tcomp = Turbo.savin.readDouble() ;
                 Turbo.mburner = Turbo.savin.readInt() ;
                 Turbo.dburner = Turbo.savin.readDouble() ;
                 Turbo.tburner = Turbo.savin.readDouble() ;
                 Turbo.mturbin = Turbo.savin.readInt() ;
                 Turbo.dturbin = Turbo.savin.readDouble() ;
                 Turbo.tturbin = Turbo.savin.readDouble() ;
                 Turbo.mnozl = Turbo.savin.readInt() ;
                 Turbo.dnozl = Turbo.savin.readDouble() ;
                 Turbo.tnozl = Turbo.savin.readDouble() ;
                 Turbo.mnozr = Turbo.savin.readInt() ;
                 Turbo.dnozr = Turbo.savin.readDouble() ;
                 Turbo.tnozr = Turbo.savin.readDouble() ;
                 Turbo.ncflag = Turbo.savin.readInt() ;
                 Turbo.ntflag = Turbo.savin.readInt() ;

              if (entype == 3) {
                 athsched = Turbo.savin.readInt()  ;
                 aexsched = Turbo.savin.readInt() ;
                  Turbo.arthd = Turbo.savin.readDouble() ;
                  Turbo.arexitd = Turbo.savin.readDouble() ;
              }
  
              con.setPanl() ;
              solve.comPute() ;
            } catch (IOException n) {
            }
          }

          if(label.equals("Save Data")) {  // Restart Write
             filnam = nsavout.getText() ;

             try{
                 Turbo.sfilo = new FileOutputStream(filnam) ;
                 Turbo.savout = new DataOutputStream(Turbo.sfilo) ;

                 Turbo.savout.writeInt(entype) ;
                 Turbo.savout.writeInt(abflag) ;
                 Turbo.savout.writeInt(fueltype) ;
                 Turbo.savout.writeDouble(Turbo.fhv / Turbo.flconv) ;
                 Turbo.savout.writeDouble(Turbo.tt4d / Turbo.tconv) ;
                 Turbo.savout.writeDouble(Turbo.tt7d / Turbo.tconv) ;
                 Turbo.savout.writeDouble(Turbo.p3p2d) ;
                 Turbo.savout.writeDouble(Turbo.p3fp2d) ;
                 Turbo.savout.writeDouble(Turbo.byprat);
                 Turbo.savout.writeDouble(Turbo.acore) ;
                 Turbo.savout.writeDouble(Turbo.a2d / Turbo.aconv);
                 Turbo.savout.writeDouble(Turbo.a4) ;
                 Turbo.savout.writeDouble(Turbo.a4p);
                 Turbo.savout.writeDouble(Turbo.gama) ;
                 Turbo.savout.writeInt(gamopt);
                 Turbo.savout.writeInt(pt2flag) ;
                 Turbo.savout.writeDouble(Turbo.eta[2]) ;
                 Turbo.savout.writeDouble(Turbo.prat[2]);
                 Turbo.savout.writeDouble(Turbo.prat[4]);
                 Turbo.savout.writeDouble(Turbo.eta[3]);
                 Turbo.savout.writeDouble(Turbo.eta[4]);
                 Turbo.savout.writeDouble(Turbo.eta[5]);
                 Turbo.savout.writeDouble(Turbo.eta[7]);
                 Turbo.savout.writeDouble(Turbo.eta[13]);
                 Turbo.savout.writeDouble(Turbo.a8d / Turbo.aconv) ;
                 Turbo.savout.writeDouble(Turbo.a8max / Turbo.aconv) ;
                 Turbo.savout.writeDouble(Turbo.a8rat) ;

                 Turbo.savout.writeDouble(Turbo.u0max / Turbo.lconv2) ;
                 Turbo.savout.writeDouble(Turbo.u0d / Turbo.lconv2) ;
                 Turbo.savout.writeDouble(Turbo.altmax / Turbo.lconv1);
                 Turbo.savout.writeDouble(Turbo.altd / Turbo.lconv1) ;
                 Turbo.savout.writeInt(arsched) ;

                 Turbo.savout.writeInt(wtflag) ;
                 Turbo.savout.writeDouble(Turbo.weight) ;
                 Turbo.savout.writeInt(Turbo.minlt) ;
                 Turbo.savout.writeDouble(Turbo.dinlt) ;
                 Turbo.savout.writeDouble(Turbo.tinlt) ;
                 Turbo.savout.writeInt(Turbo.mfan) ;
                 Turbo.savout.writeDouble(Turbo.dfan) ;
                 Turbo.savout.writeDouble(Turbo.tfan) ;
                 Turbo.savout.writeInt(Turbo.mcomp) ;
                 Turbo.savout.writeDouble(Turbo.dcomp) ;
                 Turbo.savout.writeDouble(Turbo.tcomp) ;
                 Turbo.savout.writeInt(Turbo.mburner) ;
                 Turbo.savout.writeDouble(Turbo.dburner) ;
                 Turbo.savout.writeDouble(Turbo.tburner) ;
                 Turbo.savout.writeInt(Turbo.mturbin) ;
                 Turbo.savout.writeDouble(Turbo.dturbin) ;
                 Turbo.savout.writeDouble(Turbo.tturbin) ;
                 Turbo.savout.writeInt(Turbo.mnozl) ;
                 Turbo.savout.writeDouble(Turbo.dnozl) ;
                 Turbo.savout.writeDouble(Turbo.tnozl) ;
                 Turbo.savout.writeInt(Turbo.mnozr) ;
                 Turbo.savout.writeDouble(Turbo.dnozr) ;
                 Turbo.savout.writeDouble(Turbo.tnozr) ;
                 Turbo.savout.writeInt(Turbo.ncflag) ;
                 Turbo.savout.writeInt(Turbo.ntflag) ;

              if (entype == 3) {
                  Turbo.savout.writeInt(athsched) ;
                  Turbo.savout.writeInt(aexsched) ;
                  Turbo.savout.writeDouble(Turbo.arthd) ;
                  Turbo.savout.writeDouble(Turbo.arexitd) ;
              }

              varflag = 0 ;
              layin.show(in, "first")  ;
            } catch (IOException n) {
            }
          }
          if(label.equals("Cancel")) {  // Forget it
             varflag = 0 ;
             layin.show(in, "first")  ;
          }
       }  // end handler
     } // end Files
 
     class Filep extends Panel {
        Turbo outerparent ;
        TextField namprnt,namlab ;
        Button pbopen,pball,pbfs,pbeng,pbth,pbprat,pbpres,pbvol,
               pbtrat,pbttot,pbentr,pbgam,pbeta,pbarea ;

        Filep (Turbo target) {

          outerparent = target ;
          setLayout(new GridLayout(7,3,5,5)) ;

          namprnt = new TextField() ;
          namprnt.setBackground(Color.white) ;
          namprnt.setForeground(Color.black) ;

          namlab = new TextField() ;
          namlab.setBackground(Color.white) ;
          namlab.setForeground(Color.black) ;

          pbopen = new Button("Open File") ;
          pbopen.setBackground(Color.red) ;
          pbopen.setForeground(Color.white) ;

          pball = new Button("All") ;
          pball.setBackground(Color.white) ;
          pball.setForeground(Color.blue) ;

          pbfs = new Button("Flight") ;
          pbfs.setBackground(Color.blue) ;
          pbfs.setForeground(Color.white) ;

          pbeng = new Button("Engine") ;
          pbeng.setBackground(Color.blue) ;
          pbeng.setForeground(Color.white) ;

          pbth = new Button("Thrust") ;
          pbth.setBackground(Color.blue) ;
          pbth.setForeground(Color.white) ;

          pbprat = new Button("Pt Ratio") ;
          pbprat.setBackground(Color.white) ;
          pbprat.setForeground(Color.blue) ;

          pbpres = new Button("Total Pres") ;
          pbpres.setBackground(Color.white) ;
          pbpres.setForeground(Color.blue) ;

          pbvol = new Button("Spec Vol") ;
          pbvol.setBackground(Color.white) ;
          pbvol.setForeground(Color.blue) ;

          pbtrat = new Button("Tt Ratio") ;
          pbtrat.setBackground(Color.white) ;
          pbtrat.setForeground(Color.blue) ;

          pbttot = new Button("Total Temp") ;
          pbttot.setBackground(Color.white) ;
          pbttot.setForeground(Color.blue) ;

          pbentr = new Button("Entropy") ;
          pbentr.setBackground(Color.white) ;
          pbentr.setForeground(Color.blue) ;

          pbgam = new Button("Gamma") ;
          pbgam.setBackground(Color.white) ;
          pbgam.setForeground(Color.blue) ;

          pbeta = new Button("Efficiency") ;
          pbeta.setBackground(Color.white) ;
          pbeta.setForeground(Color.blue) ;

          pbarea = new Button("Area") ;
          pbarea.setBackground(Color.white) ;
          pbarea.setForeground(Color.blue) ;

          add(new Label("File Name: ", Label.RIGHT)) ;
          add(namprnt) ;
          add(new Label(" ", Label.LEFT)) ;

          add(new Label("Label: ", Label.RIGHT)) ;
          add(namlab) ;
          add(new Label("Blue = Print", Label.CENTER)) ;

          add(pbfs) ;
          add(pbeng) ;
          add(pbth) ;

          add(pbprat) ;
          add(pbpres) ;
          add(pbvol) ;

          add(pbtrat) ;
          add(pbttot) ;
          add(pbentr) ;

          add(pbgam) ;
          add(pbeta) ;
          add(pbarea) ;

          add(pball) ;
          add(new Label("Push ->", Label.RIGHT)) ;
          add(pbopen) ;
        }

        public boolean action(Event evt, Object arg) {
          if(evt.target instanceof Button) {
            this.handleRefs(evt,arg) ;
            return true ;
          }
          else {
              return false;
          }
        }

        public void handleRefs(Event evt, Object arg) {
          String filnam, fillab ;
          String label = (String)arg ;

          if(label.equals("All")) {
             if (pall == 1) {
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pall = 1; pfs = 1; peng = 1; pth = 1; ptrat = 1; ppres = 1;
                pvol = 1; ptrat = 1; pttot = 1; pentr = 1 ; pgam = 1;
                peta = 1 ; parea = 1;
                pball.setBackground(Color.blue) ;
                pball.setForeground(Color.white) ;
                pbfs.setBackground(Color.blue) ;
                pbfs.setForeground(Color.white) ;
                pbeng.setBackground(Color.blue) ;
                pbeng.setForeground(Color.white) ;
                pbth.setBackground(Color.blue) ;
                pbth.setForeground(Color.white) ;
                pbprat.setBackground(Color.blue) ;
                pbprat.setForeground(Color.white) ;
                pbpres.setBackground(Color.blue) ;
                pbpres.setForeground(Color.white) ;
                pbvol.setBackground(Color.blue) ;
                pbvol.setForeground(Color.white) ;
                pbtrat.setBackground(Color.blue) ;
                pbtrat.setForeground(Color.white) ;
                pbttot.setBackground(Color.blue) ;
                pbttot.setForeground(Color.white) ;
                pbentr.setBackground(Color.blue) ;
                pbentr.setForeground(Color.white) ;
                pbgam.setBackground(Color.blue) ;
                pbgam.setForeground(Color.white) ;
                pbeta.setBackground(Color.blue) ;
                pbeta.setForeground(Color.white) ;
                pbarea.setBackground(Color.blue) ;
                pbarea.setForeground(Color.white) ;
             }
          }
          if(label.equals("Flight")) {
             if (pfs == 1) {
                pfs = 0 ;
                pbfs.setBackground(Color.white) ;
                pbfs.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pfs = 1 ;
                pbfs.setBackground(Color.blue) ;
                pbfs.setForeground(Color.white) ;
             }
          }
          if(label.equals("Engine")) {
             if (peng == 1) {
                peng = 0 ;
                pbeng.setBackground(Color.white) ;
                pbeng.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                peng = 1 ;
                pbeng.setBackground(Color.blue) ;
                pbeng.setForeground(Color.white) ;
             }
          }
          if(label.equals("Thrust")) {
             if (pth == 1) {
                pth = 0 ;
                pbth.setBackground(Color.white) ;
                pbth.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pth = 1 ;
                pbth.setBackground(Color.blue) ;
                pbth.setForeground(Color.white) ;
             }
          }
          if(label.equals("Pt Ratio")) {
             if (pprat == 1) {
                pprat = 0 ;
                pbprat.setBackground(Color.white) ;
                pbprat.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pprat = 1 ;
                pbprat.setBackground(Color.blue) ;
                pbprat.setForeground(Color.white) ;
             }
          }
          if(label.equals("Total Pres")) {
             if (ppres == 1) {
                ppres = 0 ;
                pbpres.setBackground(Color.white) ;
                pbpres.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                ppres = 1 ;
                pbpres.setBackground(Color.blue) ;
                pbpres.setForeground(Color.white) ;
             }
          }
          if(label.equals("Spec Vol")) {
             if (pvol == 1) {
                pvol = 0 ;
                pbvol.setBackground(Color.white) ;
                pbvol.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pvol = 1 ;
                pbvol.setBackground(Color.blue) ;
                pbvol.setForeground(Color.white) ;
             }
          }
          if(label.equals("Tt Ratio")) {
             if (ptrat == 1) {
                ptrat = 0 ;
                pbtrat.setBackground(Color.white) ;
                pbtrat.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                ptrat = 1 ;
                pbtrat.setBackground(Color.blue) ;
                pbtrat.setForeground(Color.white) ;
             }
          }
          if(label.equals("Total Temp")) {
             if (pttot == 1) {
                pttot = 0 ;
                pbttot.setBackground(Color.white) ;
                pbttot.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pttot = 1 ;
                pbttot.setBackground(Color.blue) ;
                pbttot.setForeground(Color.white) ;
             }
          }
          if(label.equals("Entropy")) {
             if (pentr == 1) {
                pentr = 0 ;
                pbentr.setBackground(Color.white) ;
                pbentr.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pentr = 1 ;
                pbentr.setBackground(Color.blue) ;
                pbentr.setForeground(Color.white) ;
             }
          }
          if(label.equals("Gamma")) {
             if (pgam == 1) {
                pgam = 0 ;
                pbgam.setBackground(Color.white) ;
                pbgam.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                pgam = 1 ;
                pbgam.setBackground(Color.blue) ;
                pbgam.setForeground(Color.white) ;
             }
          }
          if(label.equals("Efficiency")) {
             if (peta == 1) {
                peta = 0 ;
                pbeta.setBackground(Color.white) ;
                pbeta.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                peta = 1 ;
                pbeta.setBackground(Color.blue) ;
                pbeta.setForeground(Color.white) ;
             }
          }
          if(label.equals("Area")) {
             if (parea == 1) {
                parea = 0 ;
                pbarea.setBackground(Color.white) ;
                pbarea.setForeground(Color.blue) ;
                pall = 0 ;
                pball.setBackground(Color.white) ;
                pball.setForeground(Color.blue) ;
             }
             else {
                parea = 1 ;
                pbarea.setBackground(Color.blue) ;
                pbarea.setForeground(Color.white) ;
             }
          }

          if(label.equals("Open File")) {
             filnam = namprnt.getText() ;
             fillab = namlab.getText() ;
             try{
                 Turbo.pfile = new FileOutputStream(filnam) ;
              pbopen.setBackground(Color.red) ;
              pbopen.setForeground(Color.white) ;

                 Turbo.prnt = new PrintStream(Turbo.pfile) ;

                 Turbo.prnt.println("  ");
                 Turbo.prnt.println(" EngineSim Application Version 1.7a - Oct 05 ");
                 Turbo.prnt.println("  ");
                 Turbo.prnt.println(fillab);
                 Turbo.prnt.println("  ");
              iprint = 1;
              layin.show(in, "first")  ;
            } catch (IOException n) {
            }
          }
        } // end handler
     }  // end Filep

     public void fillBox() {
       inlet.left.di.setText(String.valueOf(filter0(Turbo.dinlt * Turbo.dconv))) ;
       fan.left.df.setText(String.valueOf(filter0(Turbo.dfan * Turbo.dconv))) ;
       comp.left.dc.setText(String.valueOf(filter0(Turbo.dcomp * Turbo.dconv))) ;
       burn.left.db.setText(String.valueOf(filter0(Turbo.dburner * Turbo.dconv))) ;
       turb.left.dt.setText(String.valueOf(filter0(Turbo.dturbin * Turbo.dconv))) ;
       nozl.left.dn.setText(String.valueOf(filter0(Turbo.dnozl * Turbo.dconv))) ;
       nozr.left.dn.setText(String.valueOf(filter0(Turbo.dnozr * Turbo.dconv))) ;
       inlet.left.ti.setText(String.valueOf(filter0(Turbo.tinlt * Turbo.tconv))) ;
       fan.left.tf.setText(String.valueOf(filter0(Turbo.tfan * Turbo.tconv))) ;
       comp.left.tc.setText(String.valueOf(filter0(Turbo.tcomp * Turbo.tconv))) ;
       burn.left.tb.setText(String.valueOf(filter0(Turbo.tburner * Turbo.tconv))) ;
       turb.left.tt.setText(String.valueOf(filter0(Turbo.tturbin * Turbo.tconv))) ;
       nozl.left.tn.setText(String.valueOf(filter0(Turbo.tnozl * Turbo.tconv))) ;
       nozr.left.tn.setText(String.valueOf(filter0(Turbo.tnozr * Turbo.tconv))) ;
     }
  }  // end Inppnl

  class Out extends Panel {
     Turbo outerparent ;
     Box box ;
     Plot plot ;
     Vars vars ;

     Out (Turbo target) {

          outerparent = target ;
          layout = new CardLayout() ;
          setLayout(layout) ;

          box = new Box(outerparent) ; 
          plot = new Plot(outerparent) ; 
          vars = new Vars(outerparent) ; 
 
          add ("first", box) ;
          add ("second", plot) ;
          add ("third", vars) ;
     }

     class Box extends Panel {
        Turbo outerparent ;
        TextField o7, o8, o9, o13, o16, o17 ;
        TextField o18, o19, o20, o21, o22, o23, o24, o25 ;

        Box (Turbo target) {

            outerparent = target ;
            setLayout(new GridLayout(7,4,1,5)) ;

            o7 = new TextField() ;
            o7.setBackground(Color.black) ;
            o7.setForeground(Color.yellow) ;
            o8 = new TextField() ;
            o8.setBackground(Color.black) ;
            o8.setForeground(Color.yellow) ;
            o9 = new TextField() ;
            o9.setBackground(Color.black) ;
            o9.setForeground(Color.yellow) ;
            o13 = new TextField() ;
            o13.setBackground(Color.black) ;
            o13.setForeground(Color.yellow) ;
            o16 = new TextField() ;
            o16.setBackground(Color.black) ;
            o16.setForeground(Color.yellow) ;
            o17 = new TextField() ;
            o17.setBackground(Color.black) ;
            o17.setForeground(Color.yellow) ;
            o18 = new TextField() ;
            o18.setBackground(Color.black) ;
            o18.setForeground(Color.yellow) ;
            o19 = new TextField() ;
            o19.setBackground(Color.black) ;
            o19.setForeground(Color.yellow) ;
            o20 = new TextField() ;
            o20.setBackground(Color.black) ;
            o20.setForeground(Color.yellow) ;
            o21 = new TextField() ;
            o21.setBackground(Color.black) ;
            o21.setForeground(Color.yellow) ;
            o22 = new TextField() ;
            o22.setBackground(Color.black) ;
            o22.setForeground(Color.yellow) ;
            o23 = new TextField() ;
            o23.setBackground(Color.black) ;
            o23.setForeground(Color.yellow) ;
            o24 = new TextField() ;
            o24.setBackground(Color.black) ;
            o24.setForeground(Color.yellow) ;
            o25 = new TextField() ;
            o25.setBackground(Color.black) ;
            o25.setForeground(Color.yellow) ;

            add(new Label("Fn/air", Label.CENTER)) ;
            add(o13) ;
            add(new Label("fuel/air", Label.CENTER)) ;
            add(o9) ;

            add(new Label("EPR ", Label.CENTER)) ;
            add(o7) ;
            add(new Label("ETR ", Label.CENTER)) ;
            add(o8) ;

            add(new Label("M2 ", Label.CENTER)) ;
            add(o23) ;
            add(new Label("q0 ", Label.CENTER)) ;
            add(o19) ;

            add(new Label("NPR ", Label.CENTER)) ;
            add(o17) ;
            add(new Label("V-exit", Label.CENTER)) ;
            add(o16) ;

            add(new Label("Pexit", Label.CENTER)) ;
            add(o22) ;
            add(new Label("T8 ", Label.CENTER)) ;
            add(o21) ;

            add(new Label("P Fan exit", Label.CENTER)) ;
            add(o24) ;
            add(new Label(" ", Label.CENTER)) ;
            add(o25) ;

            add(new Label("ISP", Label.CENTER)) ;
            add(o20) ;
            add(new Label("Efficiency", Label.CENTER)) ;
            add(o18) ;
        }
   
        public Insets insets() {
           return new Insets(5,0,0,0) ;
        }
   
        public void loadOut() {
           String outfor,outful,outair,outvel,outprs,outtmp,outtim,outpri ;
           int i1 ;

           outfor = " lbs" ;
           if (lunits == 1) {
               outfor = " N";
           }
           outful = " lb/hr" ;
           if (lunits == 1) {
               outful = " kg/hr";
           }
           outair = " lb/s" ;
           if (lunits == 1) {
               outair = " kg/s";
           }
           outvel = " fps" ;
           if (lunits == 1) {
               outvel = " mps";
           }
           outprs = " psf" ;
           if (lunits == 1) {
               outprs = " Pa";
           }
           outpri = " psi" ;
           if (lunits == 1) {
               outpri = " kPa";
           }
           outtmp = " R" ;
           if (lunits == 1) {
               outtmp = " K";
           }
           outtim = " sec" ;

           if (inptype == 0 || inptype == 2) {
             in.flight.left.o1.setText(String.valueOf(filter3(Turbo.fsmach))) ;
           }
           if (inptype == 1 || inptype == 3) {
               Turbo.vmn1 = Turbo.u0min;
               Turbo.vmx1 = Turbo.u0max;
             in.flight.left.f1.setText(String.valueOf(filter0(Turbo.u0d))) ;
             i1 = (int) (((Turbo.u0d - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.) ;
             in.flight.right.s1.setValue(i1) ;
           }
           in.flight.left.o2.setText(String.valueOf(filter3(Turbo.psout * Turbo.pconv)));
           in.flight.left.o3.setText(String.valueOf(filter3(Turbo.tsout * Turbo.tconv - Turbo.tref))) ;
           if (lunits <= 1) {
             if (Turbo.etr >= 1.0) {
               con.down.o4.setForeground(Color.yellow) ;
               con.down.o4.setText(String.valueOf(filter0(Turbo.fnlb * Turbo.fconv)) + outfor) ;
               con.down.o5.setForeground(Color.yellow) ;
               con.down.o5.setText(String.valueOf(filter0(Turbo.mconv1 * Turbo.fuelrat)) + outful);
               con.down.o6.setForeground(Color.yellow) ;
               con.down.o6.setText(String.valueOf(filter3(Turbo.sfc * Turbo.mconv1 / Turbo.fconv))) ;
               con.down.o14.setForeground(Color.yellow) ;
               con.down.o14.setText(String.valueOf(filter0(Turbo.fglb * Turbo.fconv)) + outfor) ;
               con.down.o15.setForeground(Color.yellow) ;
               con.down.o15.setText(String.valueOf(filter0(Turbo.drlb * Turbo.fconv)) + outfor) ;
             }
             if (Turbo.etr < 1.0) {
               con.down.o4.setForeground(Color.yellow) ;
               con.down.o4.setText("0.0") ;
               con.down.o5.setForeground(Color.yellow) ;
               con.down.o5.setText("0.0");
               con.down.o6.setForeground(Color.yellow) ;
               con.down.o6.setText("-") ;
               con.down.o14.setForeground(Color.yellow) ;
               con.down.o14.setText("0.0") ;
               con.down.o15.setForeground(Color.yellow) ;
               con.down.o15.setText(String.valueOf(filter0(Turbo.drlb * Turbo.fconv)) + outfor) ;
             }
             o7.setForeground(Color.yellow) ;
             o7.setText(String.valueOf(filter3(Turbo.epr))) ;
             o8.setForeground(Color.yellow) ;
             o8.setText(String.valueOf(filter3(Turbo.etr))) ;
             o9.setForeground(Color.yellow) ;
             o9.setText(String.valueOf(filter3(Turbo.fa))) ;
             con.down.o10.setForeground(Color.yellow) ;
             con.down.o10.setText(String.valueOf(filter3(Turbo.mconv1 * Turbo.eair)) + outair) ;
             con.down.o11.setForeground(Color.yellow) ;
             con.down.o11.setText(String.valueOf(filter3(Turbo.fconv * Turbo.weight)) + outfor) ;
             in.size.left.f2.setText(String.valueOf(filter0(Turbo.fconv * Turbo.weight))) ;
             con.down.o12.setForeground(Color.yellow) ;
             con.down.o12.setText(String.valueOf(filter3(Turbo.fnlb / Turbo.weight))) ;
             o13.setForeground(Color.yellow) ;
             o13.setText(String.valueOf(filter1(Turbo.fnet * Turbo.fconv / Turbo.mconv1))) ;
             o16.setForeground(Color.yellow) ;
             o16.setText(String.valueOf(filter0(Turbo.uexit * Turbo.lconv1)) + outvel) ;
             o17.setForeground(Color.yellow) ;
             o17.setText(String.valueOf(filter3(Turbo.npr))) ;
             o18.setForeground(Color.yellow) ;
             o18.setText(String.valueOf(filter3(Turbo.eteng))) ;
             o19.setForeground(Color.yellow) ;
             o19.setText(String.valueOf(filter0(Turbo.q0 * Turbo.fconv / Turbo.aconv)) + outprs) ;
             o20.setForeground(Color.yellow) ;
             o20.setText(String.valueOf(filter0(Turbo.isp)) + outtim) ;
             o21.setText(String.valueOf(filter0(Turbo.t8 * Turbo.tconv)) + outtmp) ;
             o22.setText(String.valueOf(filter3(Turbo.pexit * Turbo.pconv)) + outpri) ;
             o23.setText(String.valueOf(filter3(Turbo.m2))) ;
             if (entype == 2) {
                 o24.setText(String.valueOf(filter3(Turbo.pfexit * Turbo.pconv)) + outpri);
             } else {
                 o24.setText("-");
             }
           }
           if (lunits == 2) {
             if (Turbo.etr >= 1.0) {
               con.down.o4.setForeground(Color.green) ;
               con.down.o4.setText(String.valueOf(filter3(100.*(Turbo.fnlb - Turbo.fnref) / Turbo.fnref))) ;
               con.down.o5.setForeground(Color.green) ;
               con.down.o5.setText(String.valueOf(filter3(100.*(Turbo.fuelrat - Turbo.fuelref) / Turbo.fuelref)));
               con.down.o6.setForeground(Color.green) ;
               con.down.o6.setText(String.valueOf(filter3(100.*(Turbo.sfc - Turbo.sfcref) / Turbo.sfcref))) ;
             }
             if (Turbo.etr < 1.0) {
               con.down.o4.setForeground(Color.yellow) ;
               con.down.o4.setText("0.0") ;
               con.down.o5.setForeground(Color.yellow) ;
               con.down.o5.setText("0.0");
               con.down.o6.setForeground(Color.yellow) ;
               con.down.o6.setText("-") ;
             }
             o7.setForeground(Color.green) ;
             o7.setText(String.valueOf(filter3(100.*(Turbo.epr - Turbo.epref) / Turbo.epref))) ;
             o8.setForeground(Color.green) ;
             o8.setText(String.valueOf(filter3(100.*(Turbo.etr - Turbo.etref) / Turbo.etref))) ;
             o9.setForeground(Color.green) ;
             o9.setText(String.valueOf(filter3(100.*(Turbo.fa - Turbo.faref) / Turbo.faref))) ;
             con.down.o10.setForeground(Color.green) ;
             con.down.o10.setText(String.valueOf(filter3(100.*(Turbo.eair - Turbo.airref) / Turbo.airref))) ;
             con.down.o11.setForeground(Color.green) ;
             con.down.o11.setText(String.valueOf(filter3(100.*(Turbo.weight - Turbo.wtref) / Turbo.wtref))) ;
             con.down.o12.setForeground(Color.green) ;
             con.down.o12.setText(String.valueOf(filter3(100.*(Turbo.fnlb / Turbo.weight - Turbo.wfref) / Turbo.wfref))) ;
           }
        }
     } //  end Box Output

     class Vars extends Panel {
        Turbo outerparent ;
        TextField po1, po2, po3, po4, po5, po6, po7, po8 ;
        TextField to1, to2, to3, to4, to5, to6, to7, to8 ;
        Label lpa, lpb, lta, ltb;

        Vars (Turbo target) {

            outerparent = target ;
            setLayout(new GridLayout(6,6,1,5)) ;

            po1 = new TextField() ;
            po1.setBackground(Color.black) ;
            po1.setForeground(Color.yellow) ;
            po2 = new TextField() ;
            po2.setBackground(Color.black) ;
            po2.setForeground(Color.yellow) ;
            po3 = new TextField() ;
            po3.setBackground(Color.black) ;
            po3.setForeground(Color.yellow) ;
            po4 = new TextField() ;
            po4.setBackground(Color.black) ;
            po4.setForeground(Color.yellow) ;
            po5 = new TextField() ;
            po5.setBackground(Color.black) ;
            po5.setForeground(Color.yellow) ;
            po6 = new TextField() ;
            po6.setBackground(Color.black) ;
            po6.setForeground(Color.yellow) ;
            po7 = new TextField() ;
            po7.setBackground(Color.black) ;
            po7.setForeground(Color.yellow) ;
            po8 = new TextField() ;
            po8.setBackground(Color.black) ;
            po8.setForeground(Color.yellow) ;
   
            to1 = new TextField() ;
            to1.setBackground(Color.black) ;
            to1.setForeground(Color.yellow) ;
            to2 = new TextField() ;
            to2.setBackground(Color.black) ;
            to2.setForeground(Color.yellow) ;
            to3 = new TextField() ;
            to3.setBackground(Color.black) ;
            to3.setForeground(Color.yellow) ;
            to4 = new TextField() ;
            to4.setBackground(Color.black) ;
            to4.setForeground(Color.yellow) ;
            to5 = new TextField() ;
            to5.setBackground(Color.black) ;
            to5.setForeground(Color.yellow) ;
            to6 = new TextField() ;
            to6.setBackground(Color.black) ;
            to6.setForeground(Color.yellow) ;
            to7 = new TextField() ;
            to7.setBackground(Color.black) ;
            to7.setForeground(Color.yellow) ;
            to8 = new TextField() ;
            to8.setBackground(Color.black) ;
            to8.setForeground(Color.yellow) ;
   
            lpa = new Label("Pres-psi", Label.CENTER) ;
            lpb = new Label("Pres-psi", Label.CENTER) ;
            lta = new Label("Temp-R", Label.CENTER) ;
            ltb = new Label("Temp-R", Label.CENTER) ;

            add(new Label(" ", Label.CENTER)) ;
            add(new Label("Total ", Label.RIGHT)) ;
            add(new Label("Press.", Label.LEFT)) ;
            add(new Label("and", Label.CENTER)) ;
            add(new Label("Temp.", Label.LEFT)) ;
            add(new Label(" ", Label.CENTER)) ;

            add(new Label("Station", Label.CENTER)) ;
            add(lpa) ;
            add(lta) ;
            add(new Label("Station", Label.CENTER)) ;
            add(lpb) ;
            add(ltb) ;

            add(new Label("1", Label.CENTER)) ;
            add(po1) ; 
            add(to1) ; 
            add(new Label("5", Label.CENTER)) ;
            add(po5) ; 
            add(to5) ; 

            add(new Label("2", Label.CENTER)) ;
            add(po2) ; 
            add(to2) ; 
            add(new Label("6", Label.CENTER)) ;
            add(po6) ; 
            add(to6) ; 

            add(new Label("3", Label.CENTER)) ;
            add(po3) ; 
            add(to3) ; 
            add(new Label("7", Label.CENTER)) ;
            add(po7) ; 
            add(to7) ; 

            add(new Label("4", Label.CENTER)) ;
            add(po4) ; 
            add(to4) ; 
            add(new Label("8", Label.CENTER)) ;
            add(po8) ; 
            add(to8) ; 
        }
   
        public Insets insets() {
           return new Insets(5,0,0,0) ;
        }
   
        public void loadOut() {
           po1.setText(String.valueOf(filter1(Turbo.pt[2] * Turbo.pconv))) ;
           po2.setText(String.valueOf(filter1(Turbo.pt[13] * Turbo.pconv))) ;
           po3.setText(String.valueOf(filter1(Turbo.pt[3] * Turbo.pconv))) ;
           po4.setText(String.valueOf(filter1(Turbo.pt[4] * Turbo.pconv))) ;
           po5.setText(String.valueOf(filter1(Turbo.pt[5] * Turbo.pconv))) ;
           po6.setText(String.valueOf(filter1(Turbo.pt[15] * Turbo.pconv))) ;
           po7.setText(String.valueOf(filter1(Turbo.pt[7] * Turbo.pconv))) ;
           po8.setText(String.valueOf(filter1(Turbo.pt[8] * Turbo.pconv))) ;
           to1.setText(String.valueOf(filter0(Turbo.tt[2] * Turbo.tconv))) ;
           to2.setText(String.valueOf(filter0(Turbo.tt[13] * Turbo.tconv))) ;
           to3.setText(String.valueOf(filter0(Turbo.tt[3] * Turbo.tconv))) ;
           to4.setText(String.valueOf(filter0(Turbo.tt[4] * Turbo.tconv))) ;
           to5.setText(String.valueOf(filter0(Turbo.tt[5] * Turbo.tconv))) ;
           to6.setText(String.valueOf(filter0(Turbo.tt[15] * Turbo.tconv))) ;
           to7.setText(String.valueOf(filter0(Turbo.tt[7] * Turbo.tconv))) ;
           to8.setText(String.valueOf(filter0(Turbo.tt[8] * Turbo.tconv))) ;
        }
     } //  end Vars Output

     class Plot extends Canvas {
        Turbo outerparent ;

        Plot (Turbo target) {
            setBackground(Color.black) ;
        }

        public Insets insets() {
           return new Insets(0,10,0,10) ;
        }

        public boolean mouseDrag(Event evt, int x, int y) {
           handle(x,y) ;
           return true;
        }

        public boolean mouseUp(Event evt, int x, int y) {
           handle(x,y) ;
           return true;
        }

        public void handle(int x, int y) {
           if (y <= 27) {    // labels
              if ( x <= 71) {    // pressure variation
                 plttyp = 3 ;
                 if(pltkeep == 7) {
                    varflag = 0 ;
                    layin.show(in, "first")  ;
                 }
              }
              if ( x > 71 && x <= 151) {    // temperature variation
                 plttyp = 4 ;
                 if(pltkeep == 7) {
                    varflag = 0 ;
                    layin.show(in, "first")  ;
                 }
              }
              if ( x > 151 && x <= 181) {    //  T - s
                 plttyp = 5 ;
                 if(pltkeep == 7) {
                    varflag = 0 ;
                    layin.show(in, "first")  ;
                 }
              }
              if ( x > 181 && x <= 211) {    //  p - v
                 plttyp = 6 ;
                 if(pltkeep == 7) {
                    varflag = 0 ;
                    layin.show(in, "first")  ;
                 }
              }
              if ( x > 211 && x < 290) {    //  generate plot
                 plttyp = 7 ;
                 layin.show(in, "ninth")  ;
                 lunits = 0 ;
                 varflag = 0 ;
                 con.setUnits () ;
                 con.up.untch.select(lunits) ;
                 in.plot.left.ordch.select(0) ;
                 in.plot.left.absch.select(0) ;
              }
              pltkeep = plttyp ;
              con.setPlot() ;
           }
           if (y > 27) {
              if (x >= 256) {   // zoom widget
                  Turbo.sldplt = y ;
                if (Turbo.sldplt < 45) {
                    Turbo.sldplt = 45;
                }
                if (Turbo.sldplt > 155) {
                    Turbo.sldplt = 155;
                }
                  Turbo.factp = 120.0 - (Turbo.sldplt - 45) * 1.0 ;
              }
           } 
           solve.comPute() ;
           plot.repaint() ;
           return ;
        }

        public void update(Graphics g) {
           plot.paint(g) ;
        }

        public void loadPlot() {
          double cnst,delp ;
          int ic;
   
          switch (plttyp) {
           case 3:   {                       /*  press variation */
               npt = 9 ;
               Turbo.pltx[1] = 0.0 ;
               Turbo.plty[1] = Turbo.ps0 * Turbo.pconv;
               Turbo.pltx[2] = 1.0 ;
               Turbo.plty[2] = Turbo.pt[2] * Turbo.pconv;
               Turbo.pltx[3] = 2.0 ;
               Turbo.plty[3] = Turbo.pt[13] * Turbo.pconv;
               Turbo.pltx[4] = 3.0 ;
               Turbo.plty[4] = Turbo.pt[3] * Turbo.pconv;
               Turbo.pltx[5] = 4.0 ;
               Turbo.plty[5] = Turbo.pt[4] * Turbo.pconv;
               Turbo.pltx[6] = 5.0 ;
               Turbo.plty[6] = Turbo.pt[5] * Turbo.pconv;
               Turbo.pltx[7] = 6.0 ;
               Turbo.plty[7] = Turbo.pt[15] * Turbo.pconv;
               Turbo.pltx[8] = 7.0 ;
               Turbo.plty[8] = Turbo.pt[7] * Turbo.pconv;
               Turbo.pltx[9] = 8.0 ;
               Turbo.plty[9] = Turbo.pt[8] * Turbo.pconv;
               return;
           }
           case 4:   {                       /*  temp variation */
               npt = 9 ;
               Turbo.pltx[1] = 0.0 ;
               Turbo.plty[1] = Turbo.ts0 * Turbo.tconv;
               Turbo.pltx[2] = 1.0 ;
               Turbo.plty[2] = Turbo.tt[2] * Turbo.tconv;
               Turbo.pltx[3] = 2.0 ;
               Turbo.plty[3] = Turbo.tt[13] * Turbo.tconv;
               Turbo.pltx[4] = 3.0 ;
               Turbo.plty[4] = Turbo.tt[3] * Turbo.tconv;
               Turbo.pltx[5] = 4.0 ;
               Turbo.plty[5] = Turbo.tt[4] * Turbo.tconv;
               Turbo.pltx[6] = 5.0 ;
               Turbo.plty[6] = Turbo.tt[5] * Turbo.tconv;
               Turbo.pltx[7] = 6.0 ;
               Turbo.plty[7] = Turbo.tt[15] * Turbo.tconv;
               Turbo.pltx[8] = 7.0 ;
               Turbo.plty[8] = Turbo.tt[7] * Turbo.tconv;
               Turbo.pltx[9] = 8.0 ;
               Turbo.plty[9] = Turbo.tt[8] * Turbo.tconv;
               return;
           }
           case 5:   {                       /*  t-s plot */
               npt = 7 ;
               Turbo.pltx[1] = Turbo.s[0] * Turbo.bconv;
               Turbo.plty[1] = Turbo.ts0 * Turbo.tconv;
               for(ic =2; ic<=5; ++ic) {
                   Turbo.pltx[ic] = Turbo.s[ic] * Turbo.bconv;
                   Turbo.plty[ic] = Turbo.tt[ic] * Turbo.tconv;
               }
               Turbo.pltx[6] = Turbo.s[7] * Turbo.bconv;
               Turbo.plty[6] = Turbo.tt[7] * Turbo.tconv;
               Turbo.pltx[7] = Turbo.s[8] * Turbo.bconv;
               Turbo.plty[7] = Turbo.t8 * Turbo.tconv;
               return;
           }
           case 6:  {                        /*  p-v plot */
               npt = 25 ;
               Turbo.plty[1] = Turbo.ps0 * Turbo.pconv;
               Turbo.pltx[1] = Turbo.v[0] * Turbo.dconv;
               cnst = Turbo.plty[1] * Math.pow(Turbo.pltx[1], Turbo.gama) ;
               Turbo.plty[11] = Turbo.pt[3] * Turbo.pconv;
               Turbo.pltx[11] = Turbo.v[3] * Turbo.dconv;
               delp = (Turbo.plty[11] - Turbo.plty[1]) / 11.0 ;
               for (ic=2; ic<=10; ++ic) {
                   Turbo.plty[ic] = Turbo.plty[1] + ic * delp ;
                   Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama) ;
               }
               Turbo.plty[12] = Turbo.pt[4] * Turbo.pconv;
               Turbo.pltx[12] = Turbo.v[4] * Turbo.dconv;
               cnst = Turbo.plty[12] * Math.pow(Turbo.pltx[12], Turbo.gama) ;
               if (abflag == 1) {
                   Turbo.plty[25] = Turbo.ps0 * Turbo.pconv;
                   Turbo.pltx[25] = Turbo.v[8] * Turbo.dconv;
                    delp = (Turbo.plty[25] - Turbo.plty[12]) / 13.0 ;
                    for (ic=13; ic<=24; ++ic) {
                        Turbo.plty[ic] = Turbo.plty[12] + (ic - 12) * delp ;
                        Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama) ;
                    }
               }
               else {
                   Turbo.plty[18] = Turbo.pt[5] * Turbo.pconv;
                   Turbo.pltx[18] = Turbo.v[5] * Turbo.dconv;
                    delp = (Turbo.plty[18] - Turbo.plty[12]) / 6.0 ;
                    for (ic=13; ic<=17; ++ic) {
                        Turbo.plty[ic] = Turbo.plty[12] + (ic - 12) * delp ;
                        Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama) ;
                    }
                   Turbo.plty[19] = Turbo.pt[7] * Turbo.pconv;
                   Turbo.pltx[19] = Turbo.v[7] * Turbo.dconv;
                    cnst = Turbo.plty[19] * Math.pow(Turbo.pltx[19], Turbo.gama) ;
                   Turbo.plty[25] = Turbo.ps0 * Turbo.pconv;
                   Turbo.pltx[25] = Turbo.v[8] * Turbo.dconv;
                    delp = (Turbo.plty[25] - Turbo.plty[19]) / 6.0 ;
                    for (ic=20; ic<=24; ++ic) {
                        Turbo.plty[ic] = Turbo.plty[19] + (ic - 19) * delp ;
                        Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama) ;
                    }
               }
               return;
           }
           case 7: break ;                   /* create plot */
          }
        }
    
        public void paint(Graphics g) {
//          int iwidth = partimg.getWidth(this) ;
 //         int iheight = partimg.getHeight(this) ;
          int i,j,k ;
          int exes[] = new int[8] ;
          int whys[] = new int[8] ;
          int xlabel, ylabel,ind;
          double xl,yl;
          double offx,scalex,offy,scaley,waste,incy,incx;
    
          if (plttyp >= 3 && plttyp <= 7) {         //  perform a plot
            off1Gg.setColor(Color.blue) ;
            off1Gg.fillRect(0,0,350,350) ;

            if (ntikx < 2) {
                ntikx = 2;     /* protection 13June96 */
            }
            if (ntiky < 2) {
                ntiky = 2;
            }
            offx = 0.0 - Turbo.begx;
            scalex = 6.5/(Turbo.endx - Turbo.begx) ;
            incx = (Turbo.endx - Turbo.begx) / (ntikx - 1);
            offy = 0.0 - Turbo.begy;
            scaley = 10.0/(Turbo.endy - Turbo.begy) ;
            incy = (Turbo.endy - Turbo.begy) / (ntiky - 1) ;
                                            /* draw axes */
            off1Gg.setColor(Color.white) ;
            exes[0] = (int) (Turbo.factp * 0.0 + Turbo.xtranp) ;
            whys[0] = (int) (-150. + Turbo.ytranp) ;
            exes[1] = (int) (Turbo.factp * 0.0 + Turbo.xtranp) ;
            whys[1] = (int) (Turbo.factp * 0.0 + Turbo.ytranp) ;
            exes[2] = (int) (215. + Turbo.xtranp) ;
            whys[2] = (int) (Turbo.factp * 0.0 + Turbo.ytranp) ;
            off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            off1Gg.drawLine(exes[1],whys[1],exes[2],whys[2]) ;

            xlabel = (int) (-75. + Turbo.xtranp) ;      /*     label y axis */
            ylabel = (int) (-65. + Turbo.ytranp) ;
            off1Gg.drawString(Turbo.laby, xlabel, ylabel) ;
            off1Gg.drawString(Turbo.labyu, xlabel, ylabel + 20) ;
                                             /* add tick values */
            for (ind= 1; ind<= ntiky; ++ind){
                  xlabel = (int) (-33. + Turbo.xtranp) ;
                  yl = Turbo.begy + (ind - 1) * incy ;
                  ylabel = (int) (Turbo.factp * -scaley * yl + Turbo.ytranp) ;
                  if (nord != 5) {
                     off1Gg.drawString(String.valueOf((int) yl),xlabel,ylabel) ; 
                  }
                  else {
                     off1Gg.drawString(String.valueOf(filter3(yl)),xlabel,ylabel) ; 
                  }
            }
            xlabel = (int) (75. + Turbo.xtranp) ;       /*   label x axis */
            ylabel = (int) (20. + Turbo.ytranp) ;
            off1Gg.drawString(Turbo.labx, xlabel, ylabel) ;
            off1Gg.drawString(Turbo.labxu, xlabel + 50, ylabel) ;
                                             /* add tick values */
            for (ind= 1; ind<= ntikx; ++ind){
                  ylabel = (int) (10. + Turbo.ytranp) ;
                  xl = Turbo.begx + (ind - 1) * incx ;
                  xlabel = (int) (33.*(scalex*(xl + offx) -.05) + Turbo.xtranp) ;
                  if (nabs >= 2 && nabs <= 3) {
                     off1Gg.drawString(String.valueOf(filter3(xl)),xlabel,ylabel) ; 
                  }
                  if (nabs < 2 || nabs > 3) {
                     off1Gg.drawString(String.valueOf((int) xl),xlabel,ylabel) ; 
                  }
            }
      
            if(lines == 0) {
                for (i=1; i<=npt; ++i) {
                    xlabel = (int) (33.*scalex*(offx + Turbo.pltx[i]) + Turbo.xtranp) ;
                    ylabel = (int) (Turbo.factp * -scaley * (offy + Turbo.plty[i]) + Turbo.ytranp + 7.) ;
                    off1Gg.drawString("*",xlabel,ylabel) ; 
                }
            }
            else {
              exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[1]) + Turbo.xtranp);
              whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.plty[1]) + Turbo.ytranp);
              for (i=2; i<=npt; ++i) {
                  exes[0] = exes[1] ;
                  whys[0] = whys[1] ;
                  exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[i]) + Turbo.xtranp);
                  whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.plty[i]) + Turbo.ytranp);
                  off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
              }
            }
            if (plttyp == 4) {       // draw temp limits
              off1Gg.setColor(Color.yellow) ;
              if (entype < 3) {
                 exes[0] = (int) (33.*scalex*(offx + Turbo.pltx[0]) + Turbo.xtranp);
                 whys[0] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                 exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[1]) + Turbo.xtranp);
                 whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 off1Gg.drawString("Limit",exes[0]+5,whys[0]) ; 
                 exes[0] = (int) (33.*scalex*(offx + Turbo.pltx[2]) + Turbo.xtranp);
                 whys[0] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[3]) + Turbo.xtranp);
                 whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                 if (entype == 2) {
                    whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tfan) + Turbo.ytranp);
                 }
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 exes[0] = (int) (33.*scalex*(offx + Turbo.pltx[4]) + Turbo.xtranp);
                 whys[0] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tcomp) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[5]) + Turbo.xtranp);
                 whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tburner) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 exes[0] = (int) (33.*scalex*(offx + Turbo.pltx[6]) + Turbo.xtranp);
                 whys[0] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tturbin) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[6]) + Turbo.xtranp);
                 whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tnozl) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 exes[0] = (int) (33.*scalex*(offx + Turbo.pltx[9]) + Turbo.xtranp);
                 whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tnozl) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
              }
              if (entype == 3) {
                 exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[0]) + Turbo.xtranp);
                 whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                 exes[0] = (int) (33.*scalex*(offx + Turbo.pltx[4]) + Turbo.xtranp);
                 whys[0] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 off1Gg.drawString("Limit",exes[1]+5,whys[1]) ;
                 exes[1] = (int) (33.*scalex*(offx + Turbo.pltx[5]) + Turbo.xtranp);
                 whys[1] = (int) (Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tburner) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
                 exes[0] = (int) (33.*scalex*(offx + Turbo.pltx[9]) + Turbo.xtranp);
                 whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tnozr) + Turbo.ytranp);
                 off1Gg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
              }
            }
              // plot  labels
            off1Gg.setColor(Color.blue) ;
            off1Gg.fillRect(0,0,300,27) ;
            off1Gg.setColor(Color.white) ;
            if (plttyp == 3) {
              off1Gg.setColor(Color.yellow) ;
              off1Gg.fillRect(0,0,70,12) ;
              off1Gg.setColor(Color.black) ;
            }
            off1Gg.drawString("Pressure",10,10) ; 
            off1Gg.setColor(Color.white) ;
            if (plttyp == 4) {
              off1Gg.setColor(Color.yellow) ;
              off1Gg.fillRect(71,0,80,12) ;
              off1Gg.setColor(Color.black) ;
            }
            off1Gg.drawString("Temperature",75,10) ; 
            off1Gg.setColor(Color.white) ;
            if (plttyp == 5) {
              off1Gg.setColor(Color.yellow) ;
              off1Gg.fillRect(151,0,30,12) ;
              off1Gg.setColor(Color.black) ;
            }
            off1Gg.drawString("T-s",155,10) ; 
            off1Gg.setColor(Color.white) ;
            if (plttyp == 6) {
              off1Gg.setColor(Color.yellow) ;
              off1Gg.fillRect(181,0,30,12) ;
              off1Gg.setColor(Color.black) ;
            }
            off1Gg.drawString("P-v",185,10) ; 
            off1Gg.setColor(Color.white) ;
            if (plttyp == 7) {
              off1Gg.setColor(Color.yellow) ;
              off1Gg.fillRect(211,0,80,12) ;
              off1Gg.setColor(Color.black) ;
            }
            off1Gg.drawString("Generate",220,10) ; 
                                 // zoom widget
            off1Gg.setColor(Color.blue) ;
            off1Gg.fillRect(305,15,35,145) ;
            off1Gg.setColor(Color.white) ;
            off1Gg.drawString("Scale",305,25) ;
            off1Gg.drawLine(320,35,320,155) ;
            off1Gg.fillRect(310, Turbo.sldplt, 20, 5) ;
          }

          if (plttyp == 2) {           // draw photo
             off1Gg.setColor(Color.white) ;
             off1Gg.fillRect(0,0,350,350) ;
          }

          g.drawImage(offImg1,0,0,this) ;   
        }  // end paint
      }  // end Plot Output

  } //  end Output panel

  class Viewer extends Canvas 
         implements Runnable{
     Point locate,anchor ;
     Turbo outerparent ;
     Thread runner ;
     Image displimg ;
     double r0,x0,xcowl,rcowl,liprad;  /* cowl  and free stream */
     double capa,capb,capc ;           /* capture tube coefficients */
     double cepa,cepb,cepc,lxhst ;     /* exhaust tube coefficients */
     double xfan,fblade ;              /* fan blade */
     double xcomp,hblade,tblade,sblade; /* compressor blades */
     double xburn,rburn,tsig,radius ;   /* combustor */
     double xturb,xturbh,rnoz,xnoz,xflame,xit,rthroat;

     Viewer (Turbo target) {
         setBackground(Color.black) ;
         runner = null ;
//         displimg = getImage(getCodeBase(),"ab1.gif") ;
     }

     public Insets insets() {
        return new Insets(0,10,0,10) ;
     }

     public void start() {
        if (runner == null) {
           runner = new Thread(this) ;
           runner.start() ;
        }
         Turbo.antim = 0 ;
         Turbo.ancol = 1 ;
        counter = 0 ;
     }
 
     public void run() {
       while (true) {
           counter ++ ;
           ++Turbo.antim;
/*
           if (inflag == 0 && fireflag == 0) {
              runner = null ;
              return;
           }
           if(entype == 0) displimg = antjimg[counter-1] ;
           if(entype == 1) displimg = anabimg[counter-1] ;
           if(entype == 2) displimg = anfnimg[counter-1] ;
           if(entype == 3) displimg = anrmimg[counter-1] ;
*/
           try { Thread.sleep(100); }
           catch (InterruptedException e) {}
           view.repaint() ;
           if (counter == 3) {
               counter = 0;
           }
           if (Turbo.antim == 3) {
               Turbo.antim = 0;
               Turbo.ancol = -Turbo.ancol;
          }
       }
     }
 
     public boolean mouseDown(Event evt, int x, int y) {
        anchor = new Point(x,y) ;
        return true;
     }

     public boolean mouseDrag(Event evt, int x, int y) {
        handle(x,y) ;
        return true;
     }

     public boolean mouseUp(Event evt, int x, int y) {
         handleb(x,y) ;
         return true;
     }

     public void handle(int x, int y) {
         // determine location and move
         if (plttyp == 7) {
             return;
         }

         if (y > 42 ) {      // Zoom widget 
           if (x <= 35) {
               Turbo.sldloc = y ;
             if (Turbo.sldloc < 50) {
                 Turbo.sldloc = 50;
             }
             if (Turbo.sldloc > 160) {
                 Turbo.sldloc = 160;
             }
               Turbo.factor = 10.0 + (Turbo.sldloc - 50) * 1.0 ;

             view.repaint();
             return ;
           }
         }

         if (y >= 42 && x >= 35) {      //  move the engine
           locate = new Point(x,y) ;
             Turbo.xtrans = Turbo.xtrans + (int) (.2 * (locate.x - anchor.x)) ;
             Turbo.ytrans = Turbo.ytrans + (int) (.2 * (locate.y - anchor.y)) ;
           if (Turbo.xtrans > 320) {
               Turbo.xtrans = 320;
           }
           if (Turbo.xtrans < -280) {
               Turbo.xtrans = -280;
           }
           if (Turbo.ytrans > 300) {
               Turbo.ytrans = 300;
           }
           if (Turbo.ytrans < -300) {
               Turbo.ytrans = -300;
           }
           view.repaint();
           return ;
         }

     }

     public void handleb(int x, int y) {
         // determine choices
         if (plttyp == 7) {
             return;
         }

         if (y < 12 ) {   //  top labels
           if (x >= 0   && x <= 60 ) {   // flight conditions
             layin.show(in, "first")  ;
             varflag = 0 ;
           }
           if (x >= 61  && x <= 100) {   // size
             layin.show(in, "second")  ;
             varflag = 1 ;
           }
           if (x >= 101  && x <= 161) {   // limits
             layin.show(in, "eleven")  ;
             varflag = 8 ;
           }
           if (x >= 162  && x <= 195) {   // save file
             layin.show(in, "twelve")  ;
             varflag = 10 ;
           }
           if (x >= 195  && x <= 245) {   // print file
             layin.show(in, "thirteen")  ;
             varflag = 9 ;
           }
           if (x >= 245) {   // find plot
               Turbo.xtrans = 125.0 ;
               Turbo.ytrans = 115.0 ;
               Turbo.factor = 35. ;
               Turbo.sldloc = 75 ;
           }
           solve.comPute () ; 
           view.repaint();
           out.plot.repaint() ;
           con.setPanl() ;
           return ;
         }

         if (inflag == 1) {
             return;  // end of functions for test mode
         }

         if (y >= 27 && y <= 42) {                // key off the words
           if (entype <= 2) {
             if (x >= 0 && x <= 39) {    //inlet
               layin.show(in, "third")  ;
               varflag = 2 ;
             }
             if (x >= 40 && x <= 69) {   //fan
               if (entype != 2) {
                   return;
               }
               layin.show(in, "fourth")  ;
               varflag = 3 ;
             }
             if (x >= 70 && x <= 149) {  //compress
               layin.show(in, "fifth")  ;
               varflag = 4 ;
             }
             if (x >= 150 && x <= 199) {  // burner
               layin.show(in, "sixth")  ;
               varflag = 5 ;
             }
             if (x >= 200 && x <= 249) {  //turbine
               layin.show(in, "seventh")  ;
               varflag = 6 ;
             }
             if (x >= 250 && x <= 299) {  // nozzle
               layin.show(in, "eighth")  ;
               varflag = 7 ;
             }
           }
           if (entype == 3) {
             if (x >= 0 && x <= 39) {
               layin.show(in, "third")  ;
               varflag = 2 ;
             }
             if (x >= 40 && x <= 150) {  // burner
               layin.show(in, "sixth")  ;
               varflag = 5 ;
             }
             if (x >= 151 && x <= 299) {
               layin.show(in, "tenth")  ;
               varflag = 7 ;
             }
           }
           view.repaint();
           out.plot.repaint() ;
           return ;
         }

         if (y > 12 && y < 27) {          // set engine type from words
           if (x >= 0   && x <= 60 ) {   // turbojet
               entype = 0 ; 
           }
           if (x >= 61  && x <= 141) {    // afterburner
               entype = 1 ;  
           }
           if (x >= 142 && x <= 212)  {   // turbo fan
               entype = 2 ;  
           }
           if (x >= 213 && x <= 300)   {   // ramjet
               entype = 3 ;
               Turbo.u0d = 1500. ;
               Turbo.altd = 35000. ;
           }
           varflag = 0 ;
           layin.show(in, "first")  ;
                                  // reset limits
           if (entype <=2) {
             if (lunits != 1) {
                 Turbo.u0max = 1500. ;
                 Turbo.altmax = 60000. ;
                 Turbo.t4max = 3200. ;
                 Turbo.t7max = 4100. ;
             }
             if (lunits == 1) {
                 Turbo.u0max = 2500. ;
                 Turbo.altmax = 20000. ;
                 Turbo.t4max = 1800. ;
                 Turbo.t7max = 2100. ;
             }
             if (Turbo.u0d > Turbo.u0max) {
                 Turbo.u0d = Turbo.u0max;
             }
             if (Turbo.altd > Turbo.altmax) {
                 Turbo.altd = Turbo.altmax;
             }
             if (Turbo.tt4d > Turbo.t4max) {
                 Turbo.tt4 = Turbo.tt4d = Turbo.t4max;
             }
             if (Turbo.tt7d > Turbo.t7max) {
                 Turbo.tt7 = Turbo.tt7d = Turbo.t7max;
             }
           }
           else {
             if (lunits != 1) {
                 Turbo.u0max = 4500. ;
                 Turbo.altmax = 100000. ;
                 Turbo.t4max = 4500. ;
                 Turbo.t7max = 4500. ;
             }
             if (lunits == 1) {
                 Turbo.u0max = 7500. ;
                 Turbo.altmax = 35000. ;
                 Turbo.t4max = 2500. ;
                 Turbo.t7max = 2200. ;
             }
           }
                  // get the areas correct
           if (entype != 2) {
               Turbo.a2 = Turbo.acore;
               Turbo.a2d = Turbo.a2 * Turbo.aconv;
           }
           if (entype == 2) {
               Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat) ;
               Turbo.a2 = Turbo.afan;
               Turbo.a2d = Turbo.a2 * Turbo.aconv;
           }
             Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159) ;
                 // set the abflag correctly
           if (entype == 1) {
                abflag = 1 ;
               Turbo.mnozl = 5;
               Turbo.dnozl = 400.2 ;
               Turbo.tnozl = 4100. ;
                in.flight.right.nozch.select(abflag) ;
           }
           if (entype != 1) {
                abflag = 0 ;
               Turbo.mnozl = 3;
               Turbo.dnozl = 515.2 ;
               Turbo.tnozl = 2500. ;
                in.flight.right.nozch.select(abflag) ;
           }

           con.setUnits() ;
           con.setPanl() ;
           solve.comPute () ; 
           view.repaint();
           out.plot.repaint() ;
           return ;
         }

     }


     public void update(Graphics g) {
        view.paint(g) ;
     }

     public void getDrawGeo()  { /* get the drawing geometry */
        double delx,delt ;
        int index,i,j ;

        lxhst = 5. ;

         Turbo.scale = Math.sqrt(Turbo.acore / 3.1415926) ;
        if (Turbo.scale > 10.0) {
            Turbo.scale = Turbo.scale / 10.0;
        }
    
        if (Turbo.ncflag == 0) {
            Turbo.ncomp = (int) (1.0 + Turbo.p3p2d / 1.5) ;
           if (Turbo.ncomp > 15) {
               Turbo.ncomp = 15;
           }
           in.comp.left.f3.setText(String.valueOf(Turbo.ncomp)) ;
        }
        sblade = .02;
        hblade = Math.sqrt(2.0/3.1415926);
        tblade = .2*hblade;
        r0 = Math.sqrt(2.0 * Turbo.mfr / 3.1415926);
        x0 = -4.0 * hblade ;
    
        radius = .3*hblade;
        rcowl = Math.sqrt(1.8/3.1415926);
        liprad = .1*hblade ;
        xcowl = - hblade - liprad;
        xfan = 0.0 ;
        xcomp = Turbo.ncomp * (tblade + sblade) ;
         Turbo.ncompd = Turbo.ncomp;
        if (entype == 2) {                    /* fan geometry */
            Turbo.ncompd = Turbo.ncomp + 3 ;
            fblade = Math.sqrt(2.0*(1.0 + Turbo.byprat) / 3.1415926);
            rcowl = fblade ;
            r0 = Math.sqrt(2.0 * (1.0 + Turbo.byprat) * Turbo.mfr / 3.1415926);
            xfan = 3.0 * (tblade+sblade) ;
            xcomp = Turbo.ncompd * (tblade + sblade) ;
        }
        if (r0 < rcowl) {
          capc = (rcowl - r0)/((xcowl-x0)*(xcowl-x0)) ;
          capb = -2.0 * capc * x0 ;
          capa = r0 + capc * x0*x0 ;
        }
        else {
          capc = (r0 - rcowl)/((xcowl-x0)*(xcowl-x0)) ;
          capb = -2.0 * capc * xcowl ;
          capa = rcowl + capc * xcowl*xcowl ;
        }
         Turbo.lcomp = xcomp ;
         Turbo.lburn = hblade ;
        xburn = xcomp + Turbo.lburn;
        rburn = .2*hblade ;

        if (Turbo.ntflag == 0) {
            Turbo.nturb = 1 + Turbo.ncomp / 4 ;
          in.turb.left.f3.setText(String.valueOf(Turbo.nturb)) ;
          if (entype == 2) {
              Turbo.nturb = Turbo.nturb + 1;
          }
        }
         Turbo.lturb = Turbo.nturb * (tblade + sblade) ;
        xturb = xburn + Turbo.lturb;
        xturbh = xturb - 2.0*(tblade+sblade) ;
         Turbo.lnoz = Turbo.lburn;
        if (entype == 1) {
            Turbo.lnoz = 3.0 * Turbo.lburn;
        }
        if (entype == 3) {
            Turbo.lnoz = 3.0 * Turbo.lburn;
        }
        xnoz = xturb + Turbo.lburn;
        xflame = xturb + Turbo.lnoz;
        xit = xflame + hblade ;
        if (entype <=2) {
          rnoz = Math.sqrt(Turbo.a8rat * 2.0 / 3.1415926);
          cepc = -rnoz/(lxhst*lxhst) ;
          cepb = -2.0*cepc*(xit + lxhst) ;
          cepa = rnoz - cepb*xit - cepc*xit*xit ;
        }
        if (entype == 3) {
          rnoz = Math.sqrt(Turbo.arthd * Turbo.arexitd * 2.0 / 3.1415926) ;
          rthroat = Math.sqrt(Turbo.arthd * 2.0 / 3.1415926) ;
       }
                                // animated flow field
       for(i=0; i<=5; ++ i) {   // upstream
           Turbo.xg[4][i] = Turbo.xg[0][i] = i * (xcowl - x0) / 5.0 + x0  ;
           Turbo.yg[0][i] = .9 * hblade;
           Turbo.yg[4][i] = 0.0 ;
       }
       for(i=6; i<=14; ++ i) {  // compress
           Turbo.xg[4][i] = Turbo.xg[0][i] = (i - 5) * (xcomp - xcowl) / 9.0 + xcowl ;
           Turbo.yg[0][i] = .9 * hblade ;
           Turbo.yg[4][i] = (i - 5) * (1.5 * radius) / 9.0 ;
       }
       for(i=15; i<=18; ++ i) {  // burn
           Turbo.xg[0][i] = (i - 14) * (xburn - xcomp) / 4.0 + xcomp ;
           Turbo.yg[0][i] = .9 * hblade ;
           Turbo.yg[4][i] = .5 * radius ;
       }
       for(i=19; i<=23; ++ i) {  // turb
           Turbo.xg[0][i] = (i - 18) * (xturb - xburn) / 5.0 + xburn ;
           Turbo.yg[0][i] = .9 * hblade ;
           Turbo.yg[4][i] = (i - 18) * (-.5 * radius) / 5.0 + radius ;
       }
       for(i=24; i<=29; ++ i) { // nozzl
           Turbo.xg[0][i] = (i - 23) * (xit - xturb) / 6.0 + xturb ;
           if (entype != 3) {
               Turbo.yg[0][i] = (i - 23) * (rnoz - hblade) / 6.0 + hblade ;
           }
           if (entype == 3) {
               Turbo.yg[0][i] = (i - 23) * (rthroat - hblade) / 6.0 + hblade ;
           }
           Turbo.yg[4][i] = 0.0 ;
       }
       for(i=29; i<=34; ++ i) { // external
           Turbo.xg[0][i] = (i - 28) * (3.0) / 3.0 + xit ;
           if (entype != 3) {
               Turbo.yg[0][i] = (i - 28) * (rnoz) / 3.0 + rnoz ;
           }
           if (entype == 3) {
               Turbo.yg[0][i] = (i - 28) * (rthroat) / 3.0 + rthroat ;
           }
           Turbo.yg[4][i] = 0.0 ;
       }

       for (j=1; j<=3; ++ j) { 
           for(i=0; i<=34; ++ i) {
               Turbo.xg[j][i] = Turbo.xg[0][i] ;
               Turbo.yg[j][i] = (1.0 - .25 * j) * (Turbo.yg[0][i] - Turbo.yg[4][i]) + Turbo.yg[4][i] ;
           }
       }
       for (j=5; j<=8; ++ j) { 
           for(i=0; i<=34; ++ i) {
               Turbo.xg[j][i] = Turbo.xg[0][i] ;
               Turbo.yg[j][i] = -Turbo.yg[8 - j][i] ;
           }
       }
       if (entype == 2) {  // fan flow
           for(i=0; i<=5; ++ i) {   // upstream
               Turbo.xg[9][i] = Turbo.xg[0][i] ;
               Turbo.xg[10][i] = Turbo.xg[0][i] ;
               Turbo.xg[11][i] = Turbo.xg[0][i] ;
               Turbo.xg[12][i] = Turbo.xg[0][i] ;
           }
           for(i=6; i<=34; ++ i) {  // compress
               Turbo.xg[9][i] = Turbo.xg[10][i] = Turbo.xg[11][i] = Turbo.xg[12][i] =
                     (i-6) * (7.0 - xcowl)/28.0 + xcowl ;
           }
           for(i=0; i<=34; ++ i) {  // compress
               Turbo.yg[9][i] = .5 * (hblade + .9 * rcowl) ;
               Turbo.yg[10][i] = .9 * rcowl ;
               Turbo.yg[11][i] = -.5 * (hblade + .9 * rcowl) ;
               Turbo.yg[12][i] = -.9 * rcowl ;
           }
       }
     }

  public void paint(Graphics g) {
    int i,j,k ;
    int bcol,dcol ;
    int exes[] = new int[8] ;
    int whys[] = new int[8] ;
    int xlabel, ylabel,ind;
    double xl,yl;
    double offx,scalex,offy,scaley,waste,incy,incx;
 
    bcol = 0 ;
    dcol = 7 ;
    xl = Turbo.factor * 0.0 + Turbo.xtrans;
    yl = Turbo.factor * 0.0 + Turbo.ytrans;

    offsGg.setColor(Color.black) ;
    offsGg.fillRect(0,0,500,500) ;
    offsGg.setColor(Color.blue) ;
    for (j=0; j<=20; ++j) {
        exes[0] = 0 ; exes[1] = 500 ;
        whys[0] = whys[1] = (int) (yl + Turbo.factor * (20. / Turbo.scale * j) / 25.0);
        offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
        whys[0] = whys[1] = (int) (yl - Turbo.factor * (20. / Turbo.scale * j) / 25.0);
        offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
    }
    for (j=0; j<=40; ++j) {
       whys[0] = 0 ; whys[1] = 500 ;
       exes[0] = exes[1] = (int) (xl + Turbo.factor * (20. / Turbo.scale * j) / 25.0) ;
       offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
       exes[0] = exes[1] = (int) (xl - Turbo.factor * (20. / Turbo.scale * j) / 25.0) ;
       offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
    }

    if (entype <=2) {
                          /* blades */
      offsGg.setColor(Color.white) ;
      for (j=1; j <= Turbo.ncompd; ++j) {
         exes[0] = (int) (xl + Turbo.factor * (.02 + (j - 1) * (tblade + sblade))) ;
         whys[0] = (int) (Turbo.factor * hblade + Turbo.ytrans)  ;
         exes[1] = exes[0] + (int) (Turbo.factor * tblade) ;
         whys[1] = whys[0] ;
         exes[2] = exes[1] ;
         whys[2] = (int) (Turbo.factor * -hblade + Turbo.ytrans) ;
         exes[3] = exes[0] ;
         whys[3] = whys[2] ;
         offsGg.fillPolygon(exes,whys,4) ;
      }

      if (entype == 2) {                        /*  fan blades */
        offsGg.setColor(Color.white) ;
        for (j=1; j<=3; ++j) {
          if (j==3 && bcol == 0) {
              offsGg.setColor(Color.black);
          }
          if (j==3 && bcol == 7) {
              offsGg.setColor(Color.white);
          }
          exes[0] = (int) (xl + Turbo.factor * (.02 + (j - 1) * (tblade + sblade))) ;
          whys[0] = (int) (Turbo.factor * fblade + Turbo.ytrans) ;
          exes[1] = exes[0] + (int) (Turbo.factor * tblade) ;
          whys[1] = whys[0] ;
          exes[2] = exes[1] ;
          whys[2] = (int) (Turbo.factor * -fblade + Turbo.ytrans) ;
          exes[3] = exes[0] ;
          whys[3] = whys[2] ;
          offsGg.fillPolygon(exes,whys,4) ;
        }
      }
                           /* core */
      offsGg.setColor(Color.cyan) ;
      if (varflag == 4) {
          offsGg.setColor(Color.yellow);
      }
      offsGg.fillArc((int)(xl - Turbo.factor * radius), (int)(yl - Turbo.factor * radius),
                     (int)(2.0 * Turbo.factor * radius), (int)(2.0 * Turbo.factor * radius), 90, 180) ;
      exes[0] = (int) (xl) ;
      whys[0] = (int) (Turbo.factor * radius + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans) ;
      exes[2] = exes[1] ;
      whys[2] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans) ;
      exes[3] = exes[0];
      whys[3] = (int) (Turbo.factor * -radius + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;
      if (entype == 2) {  // fan
        offsGg.setColor(Color.green) ;
        if (varflag == 3) {
            offsGg.setColor(Color.yellow);
        }
        offsGg.fillArc((int)(xl - Turbo.factor * radius), (int)(yl - Turbo.factor * radius),
                       (int)(2.0 * Turbo.factor * radius), (int)(2.0 * Turbo.factor * radius), 90, 180) ;
        exes[0] = (int) (xl) ;
        whys[0] = (int) (Turbo.factor * radius + Turbo.ytrans);
        exes[1] = (int) (Turbo.factor * xfan + Turbo.xtrans) ;
        whys[1] = (int) (Turbo.factor * 1.2 * radius + Turbo.ytrans) ;
        exes[2] = exes[1] ;
        whys[2] = (int) (Turbo.factor * -1.2 * radius + Turbo.ytrans) ;
        exes[3] = exes[0];
        whys[3] = (int) (Turbo.factor * -radius + Turbo.ytrans);
        offsGg.fillPolygon(exes,whys,4) ;
      }
  /* combustor */
      offsGg.setColor(Color.black) ;
      exes[0] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * hblade + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * hblade + Turbo.ytrans);
      exes[2] = exes[1] ;
      whys[2] = (int) (Turbo.factor * -hblade + Turbo.ytrans);
      exes[3] = exes[0] ;
      whys[3] = whys[2] ;
      offsGg.fillPolygon(exes,whys,4) ;

      offsGg.setColor(Color.white) ;
      xl = xcomp + .05 + rburn ;
      yl = .6*hblade ;
      offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (yl - rburn) + Turbo.ytrans),
                     (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180) ;
      offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (-yl - rburn) + Turbo.ytrans),
                     (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180) ;
                                   /* core */
      offsGg.setColor(Color.red) ;
      if (varflag == 5) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xcomp + .25 * Turbo.lburn + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * .8 * radius + Turbo.ytrans) ;
      exes[2] = (int) (Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans) ;
      whys[2] = (int) (Turbo.factor * .8 * radius + Turbo.ytrans);
      exes[3] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[3] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans) ;
      exes[4] = exes[3];
      whys[4] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans) ;
      exes[5] = exes[2] ;
      whys[5] = (int) (Turbo.factor * -.8 * radius + Turbo.ytrans) ;
      exes[6] = exes[1] ;
      whys[6] = (int) (Turbo.factor * -.8 * radius + Turbo.ytrans) ;
      exes[7] = exes[0] ;
      whys[7] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,8) ;
 /* turbine */
                          /* blades */
      for (j=1; j <= Turbo.nturb; ++j) {
         offsGg.setColor(Color.white) ;
         if (entype == 2) {
            if (j==(Turbo.nturb - 1) && bcol == 0) {
                offsGg.setColor(Color.black);
            }
            if (j==(Turbo.nturb - 1) && bcol == 7) {
                offsGg.setColor(Color.white);
            }
         }
         exes[0] = (int) (Turbo.factor * (xburn + .02 + (j - 1) * (tblade + sblade)) + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * hblade + Turbo.ytrans) ;
         exes[1] = exes[0] + (int) (Turbo.factor * tblade) ;
         whys[1] = whys[0] ;
         exes[2] = exes[1] ;
         whys[2] = (int) (Turbo.factor * -hblade + Turbo.ytrans) ;
         exes[3] = exes[0] ;
         whys[3] = whys[2] ;
         offsGg.fillPolygon(exes,whys,4) ;
      }
                         /* core */
      offsGg.setColor(Color.magenta) ;
      if (varflag == 6) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xnoz + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * 0.0 + Turbo.ytrans) ;
      exes[2] = exes[0];
      whys[2] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,3) ;
  /* afterburner */
      if(entype == 1) {
         if (dcol == 0) {
             offsGg.setColor(Color.black);
         }
         if (dcol == 7) {
             offsGg.setColor(Color.white);
         }
         if (varflag == 7) {
             offsGg.setColor(Color.yellow);
         }
         exes[0] = (int) (Turbo.factor * (xflame - .1 * Turbo.lnoz) + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * .6 * hblade + Turbo.ytrans) ;
         exes[1] = (int) (Turbo.factor * (xflame - .2 * Turbo.lnoz) + Turbo.xtrans) ;
         whys[1] = (int) (Turbo.factor * .5 * hblade + Turbo.ytrans) ;
         exes[2] = (int) (Turbo.factor * (xflame - .1 * Turbo.lnoz) + Turbo.xtrans) ;
         whys[2] = (int) (Turbo.factor * .4 * hblade + Turbo.ytrans) ;
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         offsGg.drawLine(exes[1],whys[1],exes[2],whys[2]) ;
         whys[0] = (int) (Turbo.factor * -.6 * hblade + Turbo.ytrans) ;
         whys[1] = (int) (Turbo.factor * -.5 * hblade + Turbo.ytrans) ;
         whys[2] = (int) (Turbo.factor * -.4 * hblade + Turbo.ytrans) ;
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         offsGg.drawLine(exes[1],whys[1],exes[2],whys[2]) ;
      }

/* cowl */
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      if (entype == 0 ) {   /*   turbojet  */
         if (varflag == 2) {
             offsGg.setColor(Color.yellow);
         }
         xl = xcowl + liprad ;                /*   core cowl */
         yl = rcowl ;
         offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                        (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180) ;
         offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans),
                        (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180) ;
         exes[0] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (yl + liprad) + Turbo.ytrans);
         exes[1] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
         whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
         exes[2] = exes[1];
         whys[2] = (int) (Turbo.factor * hblade + Turbo.ytrans);
         exes[3] = exes[0];
         whys[3] = (int) (Turbo.factor * (yl - liprad) + Turbo.ytrans) ;
         offsGg.fillPolygon(exes,whys,4) ;
         whys[0] = (int) (Turbo.factor * (-yl - liprad) + Turbo.ytrans) ;
         whys[1] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
         whys[2] = (int) (Turbo.factor * -hblade + Turbo.ytrans);
         whys[3] = (int) (Turbo.factor * (-yl + liprad) + Turbo.ytrans);
         offsGg.fillPolygon(exes,whys,4) ;
                                    // compressor
         offsGg.setColor(Color.cyan) ;
         if (varflag == 4) {
             offsGg.setColor(Color.yellow);
         }
         exes[0] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
         exes[1] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
         whys[1] = whys[0] ;
         exes[2] = exes[1] ;
         whys[2] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans) ;
         exes[3] = (int) (Turbo.factor * .02 + Turbo.xtrans);
         whys[3] = (int) (Turbo.factor * hblade + Turbo.ytrans);
         exes[4] = exes[0];
         whys[4] = whys[3];
         offsGg.fillPolygon(exes,whys,5) ;

         whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
         whys[1] = whys[0]  ;
         whys[2] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans) ;
         whys[3] = (int) (Turbo.factor * -hblade + Turbo.ytrans);
         whys[4] = whys[3];
         offsGg.fillPolygon(exes,whys,5) ;
      }
      if (entype == 1) {            /*   fighter plane  */
         offsGg.setColor(Color.white) ;
         if (varflag == 2) {
             offsGg.setColor(Color.yellow);
         }
         xl = xcowl + liprad ;                     /*   inlet */
         yl = rcowl ;
         offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                        (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180) ;
         exes[0] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (yl + liprad) + Turbo.ytrans) ;
         exes[1] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
         whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
         exes[2] = exes[1];
         whys[2] = (int) (Turbo.factor * hblade + Turbo.ytrans);
         exes[3] = exes[0];
         whys[3] = (int) (Turbo.factor * (yl - liprad) + Turbo.ytrans) ;
         offsGg.fillPolygon(exes,whys,4) ;
         exes[0] = (int) (Turbo.factor * (xl + 1.5 * xcowl) + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
         exes[1] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
         whys[1] = whys[0] ;
         exes[2] = exes[1] ;
         whys[2] = (int) (Turbo.factor * -hblade + Turbo.ytrans);
         exes[3] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[3] = (int) (Turbo.factor * -.7 * hblade + Turbo.ytrans) ;
         exes[4] = exes[0];
         whys[4] = whys[0];
         offsGg.fillPolygon(exes,whys,5) ;
                                    // compressor
         offsGg.setColor(Color.cyan) ;
         if (varflag == 4) {
             offsGg.setColor(Color.yellow);
         }
         exes[0] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
         exes[1] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
         whys[1] = whys[0] ;
         exes[2] = exes[1] ;
         whys[2] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans) ;
         exes[3] = (int) (Turbo.factor * .02 + Turbo.xtrans);
         whys[3] = (int) (Turbo.factor * hblade + Turbo.ytrans);
         exes[4] = exes[0];
         whys[4] = whys[3];
         offsGg.fillPolygon(exes,whys,5) ;

         whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
         whys[1] = whys[0]  ;
         whys[2] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans) ;
         whys[3] = (int) (Turbo.factor * -hblade + Turbo.ytrans);
         whys[4] = whys[3];
         offsGg.fillPolygon(exes,whys,5) ;
      }
      if(entype == 2) {                                  /* fan jet */
         if (dcol == 0) {
             offsGg.setColor(Color.black);
         }
         if (dcol == 7) {
             offsGg.setColor(Color.white);
         }
         if (varflag == 2) {
             offsGg.setColor(Color.yellow);
         }
         xl = xcowl + liprad ;                     /*   fan cowl inlet */
         yl = rcowl ;
         offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                        (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180) ;
         offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans),
                        (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180) ;
         exes[0] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (yl + liprad) + Turbo.ytrans);
         exes[1] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
         whys[1] = (int) (Turbo.factor * (fblade + liprad) + Turbo.ytrans)  ;
         exes[2] = exes[1];
         whys[2] = (int) (Turbo.factor * fblade + Turbo.ytrans);
         exes[3] = exes[0];
         whys[3] = (int) (Turbo.factor * (yl - liprad) + Turbo.ytrans) ;
         offsGg.fillPolygon(exes,whys,4) ;

         whys[0] = (int) (Turbo.factor * (-yl - liprad) + Turbo.ytrans);
         whys[1] = (int) (Turbo.factor * (-fblade - liprad) + Turbo.ytrans)  ;
         whys[2] = (int) (Turbo.factor * -fblade + Turbo.ytrans);
         whys[3] = (int) (Turbo.factor * (-yl + liprad) + Turbo.ytrans);
         offsGg.fillPolygon(exes,whys,4) ;

         offsGg.setColor(Color.green) ;
         if (varflag == 3) {
             offsGg.setColor(Color.yellow);
         }
         xl = xcowl + liprad ;                     /*   fan cowl */
         yl = rcowl ;
         exes[0] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (fblade + liprad) + Turbo.ytrans)  ;
         exes[1] = (int) (Turbo.factor * xcomp / 2.0 + Turbo.xtrans) ;
         whys[1] = whys[0]  ;
         exes[2] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
         whys[2] = (int) (Turbo.factor * fblade + Turbo.ytrans);
         exes[3] = (int) (Turbo.factor * .02 + Turbo.xtrans);
         whys[3] = (int) (Turbo.factor * fblade + Turbo.ytrans);
         exes[4] = exes[0];
         whys[4] = whys[3];
         offsGg.fillPolygon(exes,whys,5) ;

         whys[0] = (int) (Turbo.factor * (-fblade - liprad) + Turbo.ytrans)  ;
         whys[1] = whys[0] ;
         whys[2] = (int) (Turbo.factor * -fblade + Turbo.ytrans);
         whys[3] = whys[2];
         whys[4] = whys[2];
         offsGg.fillPolygon(exes,whys,5) ;

         xl = xfan + .02 ;             /* core cowl */
         yl = hblade ;
         offsGg.setColor(Color.cyan) ;
         if (varflag == 4) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                        (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180) ;
         offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans),
                        (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180) ;
         exes[0] = (int) (Turbo.factor * (xl - .01) + Turbo.xtrans);
         whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
         exes[1] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
         whys[1] = whys[0]  ;
         exes[2] = exes[1] ;
         whys[2] = (int) (Turbo.factor * (.8 * hblade) + Turbo.ytrans) ;
         exes[3] = exes[0];
         whys[3] = (int) (Turbo.factor * (hblade - liprad) + Turbo.ytrans);
         offsGg.fillPolygon(exes,whys,4) ;

         whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
         whys[1] = whys[0]  ;
         whys[2] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans) ;
         whys[3] = (int) (Turbo.factor * (-hblade + liprad) + Turbo.ytrans);
         offsGg.fillPolygon(exes,whys,4) ;
      }
                                                     /* combustor */
      offsGg.setColor(Color.red) ;
      if (varflag == 5) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
      exes[2] = exes[1] ;
      whys[2] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans) ;
      exes[3] = (int) (Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans);
      whys[3] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans);
      exes[4] = (int) (Turbo.factor * (xcomp + .25 * Turbo.lburn) + Turbo.xtrans);
      whys[4] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans);
      exes[5] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[5] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,6) ;

      whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
      whys[1] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
      whys[2] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans) ;
      whys[3] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans);
      whys[4] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans);
      whys[5] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,6) ;
                                                      /* turbine */
      offsGg.setColor(Color.magenta) ;
      if (varflag == 6) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xturb + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
      exes[2] = exes[1];
      whys[2] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans) ;
      exes[3] = (int) (Turbo.factor * xburn + Turbo.xtrans);
      whys[3] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;

      whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
      whys[1] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
      whys[2] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans) ;
      whys[3] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;
                                                     /* nozzle */
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      if (varflag == 7) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xturb + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xflame + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans);
      exes[2] = (int) (Turbo.factor * xit + Turbo.xtrans)  ;
      whys[2] = (int) (Turbo.factor * rnoz + Turbo.ytrans)  ;
      exes[3] = (int) (Turbo.factor * xflame + Turbo.xtrans) ;
      whys[3] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans);
      exes[4] = (int) (Turbo.factor * xturb + Turbo.xtrans);
      whys[4] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans) ;
      offsGg.fillPolygon(exes,whys,5) ;

      whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
      whys[1] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
      whys[2] = (int) (Turbo.factor * -rnoz + Turbo.ytrans)  ;
      whys[3] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans);
      whys[4] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,5) ;
                                           //   show stations 
      if (showcom == 1) {
         offsGg.setColor(Color.white) ;
         ylabel = (int) (Turbo.factor * 1.5 * hblade + 20. + Turbo.ytrans) ;
         whys[1] = 370 ;
  
         xl = xcomp -.1 ;                   /* burner entrance */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("3",xlabel,ylabel) ; 
  
         xl = xburn - .1 ;                   /* turbine entrance */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("4",xlabel,ylabel) ; 

         xl = xnoz ;            /* Afterburner entry */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("6",xlabel,ylabel) ; 
    
         if (entype == 1) {
            xl = xflame ;               /* Afterburner exit */
            exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
            whys[0] = (int) (Turbo.factor * .2 + Turbo.ytrans);
            offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            xlabel = exes[0] + (int) (Turbo.factor * .05) ;
            offsGg.drawString("7",xlabel,ylabel) ; 
         }
   
         xl = xit ;                    /* nozzle exit */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * .2 + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] - (int) (Turbo.factor * .2) ;
         offsGg.drawString("8",xlabel,ylabel) ; 
  
         if (entype < 2) {
            xl = -radius ;                   /* compressor entrance */
            exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
            whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
            offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            xlabel = exes[0] + (int) (Turbo.factor * .05) ;
            offsGg.drawString("2",xlabel,ylabel) ; 
  
            xl = xturb+.1 ;                   /* turbine exit */
            exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
            whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
            offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            xlabel = exes[0] + (int) (Turbo.factor * .05) ;
            offsGg.drawString("5",xlabel,ylabel) ; 
         }
         if (entype == 2) {
            xl = xturbh ;               /*high pressturbine exit*/
            exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
            whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
            offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            xlabel = exes[0] + (int) (Turbo.factor * .05) ;
            offsGg.drawString("5",xlabel,ylabel) ; 
  
            xl = 0.0 - .1 ;                            /* fan entrance */
            exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
            whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
            offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            xlabel = exes[0] - (int) (Turbo.factor * .2) ;
            offsGg.drawString("1",xlabel,ylabel) ; 
 
            xl = 3.0*tblade ;                            /* fan exit */
            exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
            whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
            offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            xlabel = exes[0] + (int) (Turbo.factor * .12) ;
            offsGg.drawString("2",xlabel,ylabel) ;

         }
    
         xl =  - 2.0 ;                   /* free stream */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("0",xlabel,ylabel) ; 
      }
      
      if (inflag == 0) {   // show labels for design mode
         offsGg.setColor(Color.black) ;
         offsGg.fillRect(0,27,300,15) ;
         offsGg.setColor(Color.white) ;
         if (varflag == 2) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Inlet",10,40) ; 
         if (entype == 2) { 
           offsGg.setColor(Color.green) ;
           if (varflag == 3) {
               offsGg.setColor(Color.yellow);
           }
           offsGg.drawString("Fan",40,40) ; 
         }
         offsGg.setColor(Color.cyan) ;
         if (varflag == 4) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Compressor",70,40) ; 
         offsGg.setColor(Color.red) ;
         if (varflag == 5) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Burner",150,40) ; 
         offsGg.setColor(Color.magenta) ;
         if (varflag == 6) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Turbine",200,40) ; 
         offsGg.setColor(Color.white) ;
         if (varflag == 7) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Nozzle",250,40) ; 
      }
    }

    if (entype == 3) {                  //ramjet geom
                           /* inlet spike */
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      exes[0] = (int) (Turbo.factor * -2.0 + Turbo.xtrans) ;
      whys[0] = (int) (0.0 + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans) ;
      exes[2] = exes[1] ;
      whys[2] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans) ;
      exes[3] = exes[0];
      whys[3] = (int) (0.0 + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;
                                 /* spraybars */
      offsGg.setColor(Color.white) ;
      if (varflag == 5) {
          offsGg.setColor(Color.yellow);
      }
      xl = xcomp + .05 + rburn ;
      yl = .6*hblade ;
      offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (yl - rburn) + Turbo.ytrans),
                     (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180) ;
      offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (-yl - rburn) + Turbo.ytrans),
                     (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180) ;
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      exes[0] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xcomp + .25 * Turbo.lburn + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * .8 * radius + Turbo.ytrans) ;
      exes[2] = (int) (Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans) ;
      whys[2] = (int) (Turbo.factor * .8 * radius + Turbo.ytrans);
      exes[3] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[3] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans) ;
      exes[4] = exes[3];
      whys[4] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans) ;
      exes[5] = exes[2] ;
      whys[5] = (int) (Turbo.factor * -.8 * radius + Turbo.ytrans) ;
      exes[6] = exes[1] ;
      whys[6] = (int) (Turbo.factor * -.8 * radius + Turbo.ytrans) ;
      exes[7] = exes[0] ;
      whys[7] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,8) ;
                         /* aft cone */
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      exes[0] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * 1.5 * radius + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xnoz + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * 0.0 + Turbo.ytrans) ;
      exes[2] = exes[0];
      whys[2] = (int) (Turbo.factor * -1.5 * radius + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,3) ;
                           /* fame holders */
      offsGg.setColor(Color.white) ;
      if (varflag == 5) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * (xnoz + .2 * Turbo.lnoz) + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * .6 * hblade + Turbo.ytrans) ;
      exes[1] = (int) (Turbo.factor * (xnoz + .1 * Turbo.lnoz) + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * .5 * hblade + Turbo.ytrans) ;
      exes[2] = (int) (Turbo.factor * (xnoz + .2 * Turbo.lnoz) + Turbo.xtrans) ;
      whys[2] = (int) (Turbo.factor * .4 * hblade + Turbo.ytrans) ;
      offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
      offsGg.drawLine(exes[1],whys[1],exes[2],whys[2]) ;
      whys[0] = (int) (Turbo.factor * -.6 * hblade + Turbo.ytrans) ;
      whys[1] = (int) (Turbo.factor * -.5 * hblade + Turbo.ytrans) ;
      whys[2] = (int) (Turbo.factor * -.4 * hblade + Turbo.ytrans) ;
      offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
      offsGg.drawLine(exes[1],whys[1],exes[2],whys[2]) ;

      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      if (varflag == 2) {
          offsGg.setColor(Color.yellow);
      }

      xl = xcowl + liprad ;                /*   core cowl */
      yl = rcowl ;
      exes[0] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (yl) + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
      exes[2] = exes[1];
      whys[2] = (int) (Turbo.factor * hblade + Turbo.ytrans);
      exes[3] = exes[0];
      whys[3] = (int) (Turbo.factor * (yl) + Turbo.ytrans) ;
      offsGg.fillPolygon(exes,whys,4) ;
      whys[0] = (int) (Turbo.factor * (-yl) + Turbo.ytrans) ;
      whys[1] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
      whys[2] = (int) (Turbo.factor * -hblade + Turbo.ytrans);
      whys[3] = (int) (Turbo.factor * (-yl) + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;
                                    // compressor
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      if (varflag == 2) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * -radius + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
      exes[1] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[1] = whys[0] ;
      exes[2] = exes[1] ;
      whys[2] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans) ;
      exes[3] = (int) (Turbo.factor * .02 + Turbo.xtrans);
      whys[3] = (int) (Turbo.factor * hblade + Turbo.ytrans);
      exes[4] = exes[0];
      whys[4] = whys[3];
      offsGg.fillPolygon(exes,whys,5) ;

      whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
      whys[1] = whys[0]  ;
      whys[2] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans) ;
      whys[3] = (int) (Turbo.factor * -hblade + Turbo.ytrans);
      whys[4] = whys[3];
      offsGg.fillPolygon(exes,whys,5) ;
                                                     /* combustor */
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      if (varflag == 5) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
      exes[2] = exes[1] ;
      whys[2] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans) ;
      exes[3] = (int) (Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans);
      whys[3] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans);
      exes[4] = (int) (Turbo.factor * (xcomp + .25 * Turbo.lburn) + Turbo.xtrans);
      whys[4] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans);
      exes[5] = (int) (Turbo.factor * xcomp + Turbo.xtrans) ;
      whys[5] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,6) ;

      whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
      whys[1] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
      whys[2] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans) ;
      whys[3] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans);
      whys[4] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans);
      whys[5] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,6) ;
                                                      /* turbine */
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      if (varflag == 5) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xburn + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xturb + Turbo.xtrans) ;
      whys[1] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans)  ;
      exes[2] = exes[1];
      whys[2] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans) ;
      exes[3] = (int) (Turbo.factor * xburn + Turbo.xtrans);
      whys[3] = (int) (Turbo.factor * .8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;

      whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
      whys[1] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans)  ;
      whys[2] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans) ;
      whys[3] = (int) (Turbo.factor * -.8 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;
                                                    /* nozzle */
      if (dcol == 0) {
          offsGg.setColor(Color.black);
      }
      if (dcol == 7) {
          offsGg.setColor(Color.white);
      }
      if (varflag == 7) {
          offsGg.setColor(Color.yellow);
      }
      exes[0] = (int) (Turbo.factor * xturb + Turbo.xtrans) ;
      whys[0] = (int) (Turbo.factor * (hblade + liprad) + Turbo.ytrans);
      exes[1] = (int) (Turbo.factor * xit + Turbo.xtrans)  ;
      whys[1] = (int) (Turbo.factor * rnoz + Turbo.ytrans)  ;
      exes[2] = (int) (Turbo.factor * xflame + Turbo.xtrans) ;
      whys[2] = (int) (Turbo.factor * rthroat + Turbo.ytrans);
      exes[3] = (int) (Turbo.factor * xturb + Turbo.xtrans);
      whys[3] = (int) (Turbo.factor * .9 * hblade + Turbo.ytrans) ;
      offsGg.fillPolygon(exes,whys,4) ;

      whys[0] = (int) (Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
      whys[1] = (int) (Turbo.factor * -rnoz + Turbo.ytrans)  ;
      whys[2] = (int) (Turbo.factor * -rthroat + Turbo.ytrans);
      whys[3] = (int) (Turbo.factor * -.9 * hblade + Turbo.ytrans);
      offsGg.fillPolygon(exes,whys,4) ;

                                           //   show stations 
      if (showcom == 1) {
         offsGg.setColor(Color.white) ;
         ylabel = (int) (Turbo.factor * 1.5 * hblade + 20. + Turbo.ytrans) ;
         whys[1] = 370 ;
  
         xl = xcomp -.1 ;                   /* burner entrance */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("3",xlabel,ylabel) ; 
  
         xl = xnoz + .1 * Turbo.lnoz;        /* flame holders */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * .2 + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("4",xlabel,ylabel) ; 

         xl = xflame ;               /* Afterburner exit */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * .2 + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("7",xlabel,ylabel) ; 
  
         xl = xit ;                    /* nozzle exit */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * .2 + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] - (int) (Turbo.factor * .2) ;
         offsGg.drawString("8",xlabel,ylabel) ; 
  
         xl =  - 2.0 ;                   /* free stream */
         exes[0] = exes[1] = (int) (Turbo.factor * xl + Turbo.xtrans) ;
         whys[0] = (int) (Turbo.factor * (hblade - .2) + Turbo.ytrans);
         offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
         xlabel = exes[0] + (int) (Turbo.factor * .05) ;
         offsGg.drawString("0",xlabel,ylabel) ; 
      }

      if (inflag == 0) {  // show labels for design mode
         offsGg.setColor(Color.black) ;
         offsGg.fillRect(0,27,300,15) ;
         offsGg.setColor(Color.white) ;
         if (varflag == 2) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Inlet",10,40) ; 
         offsGg.setColor(Color.white) ;
         if (varflag == 5) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Burner",100,40) ; 
         offsGg.setColor(Color.white) ;
         if (varflag == 7) {
             offsGg.setColor(Color.yellow);
         }
         offsGg.drawString("Nozzle",250,40) ; 
      }
    }
  /* animated flow */
    for (j=1 ; j<=8 ; ++ j) {
         exes[1] = (int) (Turbo.factor * Turbo.xg[j][0] + Turbo.xtrans) ;
         whys[1] = (int) (Turbo.factor * Turbo.yg[j][0] + Turbo.ytrans);
         for (i=1 ; i<= 34; ++i) {
            exes[0] = exes[1] ;
            whys[0] = whys[1] ;
            exes[1] = (int) (Turbo.factor * Turbo.xg[j][i] + Turbo.xtrans) ;
            whys[1] = (int) (Turbo.factor * Turbo.yg[j][i] + Turbo.ytrans);
            if ((i - Turbo.antim) / 3 * 3 == (i - Turbo.antim)) {
              if (i< 15) {
                 if (Turbo.ancol == -1) {
                   if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                       offsGg.setColor(Color.white);
                   }
                   if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                       offsGg.setColor(Color.cyan);
                   }
                 }
                 if (Turbo.ancol == 1) {
                   if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                       offsGg.setColor(Color.cyan);
                   }
                   if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                       offsGg.setColor(Color.white);
                   }
                 }
              }
              if (i >= 16) {
                 if (Turbo.ancol == -1) {
                   if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                       offsGg.setColor(Color.yellow);
                   }
                   if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                       offsGg.setColor(Color.red);
                   }
                 }
                 if (Turbo.ancol == 1) {
                   if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                       offsGg.setColor(Color.red);
                   }
                   if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                       offsGg.setColor(Color.yellow);
                   }
                 }
              }
              offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
            }
         }
    }
    if (entype == 2) {   // fan flow
       for (j=9 ; j<=12 ; ++ j) {
         exes[1] = (int) (Turbo.factor * Turbo.xg[j][0] + Turbo.xtrans) ;
         whys[1] = (int) (Turbo.factor * Turbo.yg[j][0] + Turbo.ytrans);
         for (i=1 ; i<= 34; ++i) {
            exes[0] = exes[1] ;
            whys[0] = whys[1] ;
            exes[1] = (int) (Turbo.factor * Turbo.xg[j][i] + Turbo.xtrans) ;
            whys[1] = (int) (Turbo.factor * Turbo.yg[j][i] + Turbo.ytrans);
            if ((i - Turbo.antim) / 3 * 3 == (i - Turbo.antim)) {
              if (Turbo.ancol == -1) {
                if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                    offsGg.setColor(Color.white);
                }
                if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                    offsGg.setColor(Color.cyan);
                }
              }
              if (Turbo.ancol == 1) {
                if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                    offsGg.setColor(Color.cyan);
                }
                if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                    offsGg.setColor(Color.white);
                }
              }
              offsGg.drawLine(exes[0],whys[0],exes[1],whys[1]) ;
           }
         }
       }
    }
 
    offsGg.setColor(Color.black) ;
    offsGg.fillRect(0,0,300,27) ;
    offsGg.setColor(Color.white) ;
    if (varflag == 0) {
        offsGg.setColor(Color.yellow);
    }
    offsGg.drawString("Flight",10,10) ; 
    offsGg.setColor(Color.white) ;
    if (varflag == 1) {
        offsGg.setColor(Color.yellow);
    }
    offsGg.drawString("Size",70,10) ; 
    offsGg.setColor(Color.white) ;
    if (varflag == 8) {
        offsGg.setColor(Color.yellow);
    }
    offsGg.drawString("Limits",120,10) ;
    offsGg.setColor(Color.white) ;
    if (varflag == 10) {
        offsGg.setColor(Color.yellow);
    }
    offsGg.drawString("Save",170,10) ;
    offsGg.setColor(Color.white) ;
    if (varflag == 9) {
        offsGg.setColor(Color.yellow);
    }
    offsGg.drawString("Print",215,10) ;
    offsGg.setColor(Color.cyan) ;
    offsGg.drawString("Find",260,10) ;
                               // zoom widget
    offsGg.setColor(Color.black) ;
    offsGg.fillRect(0,42,35,140) ;
    offsGg.setColor(Color.cyan) ;
    offsGg.drawString("Zoom",5,180) ;
    offsGg.drawLine(15,50,15,165) ;
    offsGg.fillRect(5, Turbo.sldloc, 20, 5) ;

    if (inflag == 0) { // engine labels for design mode
       offsGg.setColor(Color.green) ;
       if (entype == 0) {
         offsGg.setColor(Color.yellow) ;
         offsGg.fillRect(0,15,60,12) ;
         offsGg.setColor(Color.black) ;
       }
       offsGg.drawString("Turbojet",10,25) ; 
       offsGg.setColor(Color.green) ;
       if (entype == 1) {
         offsGg.setColor(Color.yellow) ;
         offsGg.fillRect(61,15,80,12) ;
         offsGg.setColor(Color.black) ;
       }
       offsGg.drawString("Afterburner",75,25) ; 
       offsGg.setColor(Color.green) ;
       if (entype == 2) {
         offsGg.setColor(Color.yellow) ;
         offsGg.fillRect(142,15,70,12) ;
         offsGg.setColor(Color.black) ;
       }
       offsGg.drawString("Turbo Fan",150,25) ; 
       offsGg.setColor(Color.green) ;
       if (entype == 3) {
         offsGg.setColor(Color.yellow) ;
         offsGg.fillRect(213,15,90,12) ;
         offsGg.setColor(Color.black) ;
       }
       offsGg.drawString("Ramjet",225,25) ; 
    }
                               // temp limit warning  
    if (Turbo.fireflag == 1) {
       offsGg.setColor(Color.yellow) ;
       offsGg.fillRect(50,80,200,30) ;
       if(counter==1) {
           offsGg.setColor(Color.black);
       }
       if(counter>=2) {
           offsGg.setColor(Color.white);
       }
       offsGg.fillRect(55,85,190,20) ;
       offsGg.setColor(Color.red) ;
       offsGg.drawString("Temperature  Limits Exceeded",60,100) ; 
    }

    g.drawImage(offscreenImg,0,0,this) ;   
  }
 }   // end viewer
 
 public static void main(String args[]) {
    Turbo turbo = new Turbo() ;

     Turbo.f = new Frame("EngineSim Application Version 1.7a") ;
     Turbo.f.add("Center", turbo) ;
     Turbo.f.resize(710, 450);
     Turbo.f.show() ;

    turbo.init() ;
    turbo.start() ;
 }
}
