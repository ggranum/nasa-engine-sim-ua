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

import java.applet.Applet;
import java.awt.*;
import java.lang.Math ;
import java.io.* ;

public class Turbo extends Applet {

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
     solve = new Solver(this) ;

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

    public class In extends Panel {
     Turbo turbo;
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

         turbo = target ;
          layin = new CardLayout() ;
          setLayout(layin) ;

          flight = new Flight(turbo) ;
          size = new Size(turbo) ;
          inlet = new Inlet(turbo) ;
          fan = new Fan(turbo) ;
          comp = new Comp(turbo) ;
          burn = new Burn(turbo) ;
          turb = new Turb(turbo) ;
          nozl = new Nozl(turbo) ;
          plot = new Plot(turbo) ;
          nozr = new Nozr(turbo) ;
          limt = new Limt(turbo) ;
          files = new Files(turbo) ;
          filep = new Filep(turbo) ;

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
