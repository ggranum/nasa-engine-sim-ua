package gov.nasa.engine_sim_ua;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;

/**
 * @author Geoff M. Granum
 */
class Up extends Panel {

    private final Con con;
    private final Turbo turbo;
    Button setref, endit, record;
    Choice engch, modch, arch, pltch, untch;
    Label l1, l2, l3;

    Up(Con con, Turbo target) {
        this.con = con;
        this.turbo = target;
        setLayout(new GridLayout(4, 3, 5, 5));

        modch = new Choice();
        modch.addItem("Design");
        modch.addItem("Test");
        modch.select(0);

        engch = new Choice();
        engch.addItem("My Design");
        engch.addItem("J85 Model");
        engch.addItem("F100 Model");
        engch.addItem("CF6 Model");
        engch.addItem("Ramjet Model");
        engch.select(0);

        l3 = new Label("Output :", Label.RIGHT);
        l3.setForeground(Color.red);
        pltch = new Choice();
        pltch.addItem("Graphs");
        pltch.addItem("Engine Performance");
        pltch.addItem("Component Performance");
        pltch.select(1);
        pltch.setBackground(Color.white);
        pltch.setForeground(Color.red);

        untch = new Choice();
        untch.addItem("English");
        untch.addItem("Metric");
        //     untch.addItem("% Change");
        untch.select(0);

        setref = new Button("Reset");
        setref.setBackground(Color.orange);
        setref.setForeground(Color.black);

        record = new Button("Print Data");
        record.setBackground(Color.blue);
        record.setForeground(Color.white);

        endit = new Button("Exit");
        endit.setBackground(Color.red);
        endit.setForeground(Color.white);

        add(new Label("Mode:", Label.RIGHT));
        add(modch);
        add(endit);

        add(new Label("Load:", Label.RIGHT));
        add(engch);
        add(setref);

        add(new Label("Units:", Label.RIGHT));
        add(untch);
        add(record);

        add(l3);
        add(pltch);
        add(new Label("to record", Label.CENTER));
    }

    public Insets insets() {
        return new Insets(0, 5, 5, 0);
    }

    public boolean action(Event evt, Object arg) {
        if(evt.target instanceof Choice) {
            this.handleProb(evt, arg);
            return true;
        }
        if(evt.target instanceof Button) {
            this.handleRefs(evt, arg);
            return true;
        } else {
            return false;
        }
    }

    public void handleRefs(Event evt, Object arg) {
        String label = (String)arg;
        String seng, sgamop, smode;

        if(label.equals("Reset")) {
            if(turbo.lunits == 2) {
                // reset reference variables
                Turbo.u0ref = Turbo.u0d;
                Turbo.altref = Turbo.altd;
                Turbo.thrref = Turbo.throtl;
                Turbo.a2ref = Turbo.a2d;
                Turbo.et2ref = Turbo.eta[2];
                Turbo.fpref = Turbo.p3fp2d;
                Turbo.et13ref = Turbo.eta[13];
                Turbo.bpref = Turbo.byprat;
                Turbo.cpref = Turbo.p3p2d;
                Turbo.et3ref = Turbo.eta[3];
                Turbo.et4ref = Turbo.eta[4];
                Turbo.et5ref = Turbo.eta[5];
                Turbo.t4ref = Turbo.tt4d;
                Turbo.p4ref = Turbo.prat[4];
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
                con.setPanl();

                turbo.out.box.loadOut();
                turbo.out.vars.loadOut();
            } else {
                turbo.solve.setDefaults();
                con.setUnits();
                turbo.con.setPanl();
                turbo.layin.show(turbo.in, "first");
                turbo.layout.show(turbo.out, "first");
                turbo.con.up.engch.select(0);
                turbo.con.up.modch.select(0);
                turbo.con.up.pltch.select(1);
                turbo.con.up.untch.select(0);
                turbo.in.flight.left.o1.setBackground(Color.black);
                turbo.in.flight.left.o1.setForeground(Color.yellow);
                turbo.in.flight.left.o2.setBackground(Color.black);
                turbo.in.flight.left.o2.setForeground(Color.yellow);
                turbo.in.flight.left.o3.setBackground(Color.black);
                turbo.in.flight.left.o3.setForeground(Color.yellow);
                turbo.in.flight.left.f1.setBackground(Color.white);
                turbo.in.flight.left.f1.setForeground(Color.black);
                turbo.in.flight.left.f2.setBackground(Color.white);
                turbo.in.flight.left.f2.setForeground(Color.black);
                turbo.in.size.right.sizch.select(0);
                turbo.in.size.left.f1.setBackground(Color.white);
                turbo.in.size.left.f1.setForeground(Color.black);
                turbo.in.size.left.f3.setBackground(Color.black);
                turbo.in.size.left.f3.setForeground(Color.yellow);
                turbo.in.comp.right.stgch.select(0);
                turbo.in.comp.left.f3.setBackground(Color.black);
                turbo.in.comp.left.f3.setForeground(Color.yellow);
                turbo.in.turb.right.stgch.select(0);
                turbo.in.turb.left.f3.setBackground(Color.black);
                turbo.in.turb.left.f3.setForeground(Color.yellow);
                turbo.in.inlet.right.imat.select(Turbo.minlt);
                turbo.in.fan.right.fmat.select(Turbo.mfan);
                turbo.in.comp.right.cmat.select(Turbo.mcomp);
                turbo.in.burn.right.bmat.select(Turbo.mburner);
                turbo.in.turb.right.tmat.select(Turbo.mturbin);
                turbo.in.nozl.right.nmat.select(Turbo.mnozl);
                turbo.in.nozr.right.nrmat.select(Turbo.mnozr);
                turbo.solve.comPute();
                turbo.out.plot.repaint();
            }
        }
        if(label.equals("Print Data")) {
            if(turbo.iprint == 1) {  // file open - print data
                Turbo.prnt.println("----------------------------------------- ");
                Turbo.prnt.println(" ");
                seng = "Simple Turbojet";
                if(turbo.entype == 1) {
                    seng = "Turbojet with Afterburner ";
                }
                if(turbo.entype == 2) {
                    seng = "Turbofan";
                }
                if(turbo.entype == 3) {
                    seng = "Ramjet";
                }
                Turbo.prnt.println(seng);
                if(turbo.entype == 2) {
                    Turbo.prnt.println("  Bypass Ratio  = " + String.valueOf(turbo.filter3(Turbo.byprat)));
                }
                if(turbo.entype == 1) {
                    if(turbo.abflag == 0) {
                        Turbo.prnt.println("  Afterburner  OFF ");
                    }
                    if(turbo.abflag == 1) {
                        Turbo.prnt.println("  Afterburner  ON ");
                    }
                }
                if(turbo.lunits == 0) {
                    Turbo.prnt.println("  Diameter  = " + String.valueOf(turbo.filter3(Turbo.diameng)) + " ft ");
                    Turbo.prnt.println("  Estimated Weight  = " + String.valueOf(turbo.filter3(Turbo.weight)) + " lbs ");
                }
                if(turbo.lunits == 1) {
                    Turbo.prnt.println("  Diameter  = " + String.valueOf(turbo.filter3(Turbo.diameng)) + " m ");
                    Turbo.prnt.println("  Estimated Weight  = " + String.valueOf(turbo.filter3(Turbo.weight * Turbo.fconv)) + " N ");
                }
                if(turbo.gamopt == 1) {
                    sgamop = "  -  Gamma and Cp = f(Temp)";
                } else {
                    sgamop = "  -  Constant Gamma and Cp)";
                }
                if(turbo.inflag == 0) {
                    smode = "  Design Mode";
                } else {
                    smode = "  Test Mode";
                }
                Turbo.prnt.println(smode + sgamop);
                if(turbo.pall == 1 || turbo.pfs == 1) {
                    Turbo.prnt.println(" ");
                    Turbo.prnt.println("Flight Conditions: ");
                    if(turbo.lunits == 0) {
                        Turbo.prnt.println("  Mach = " + String.valueOf(turbo.filter3(Turbo.fsmach))
                                           + ",  V0 = " + String.valueOf(turbo.filter0(Turbo.u0d)) + " mph ");
                        Turbo.prnt.println("  Alt = " + String.valueOf(turbo.filter0(Turbo.altd)) + " ft ");
                        Turbo.prnt.println("  p0 = " + String.valueOf(turbo.filter3(Turbo.ps0))
                                           + ",  pt0 = " + String.valueOf(turbo.filter3(Turbo.pt[0])) + " psi");
                        Turbo.prnt.println("  T0 = " + String.valueOf(turbo.filter0(Turbo.ts0))
                                           + ",  Tt0 = " + String.valueOf(turbo.filter0(Turbo.tt[0])) + " R ");
                    }
                    if(turbo.lunits == 1) {
                        Turbo.prnt.println("  Mach = " + String.valueOf(turbo.filter3(Turbo.fsmach))
                                           + ",  V0 = " + String.valueOf(turbo.filter0(Turbo.u0d)) + " km/h ");
                        Turbo.prnt.println("  Alt = " + String.valueOf(turbo.filter0(Turbo.altd)) + " m ");
                        Turbo.prnt.println("  p0 = " + String.valueOf(turbo.filter3(Turbo.ps0 * Turbo.pconv))
                                           + ",  pt0 = " + String.valueOf(turbo.filter3(Turbo.pt[0] * Turbo.pconv)) + " k Pa");
                        Turbo.prnt.println("  T0 = " + String.valueOf(turbo.filter0(Turbo.ts0 * Turbo.tconv))
                                           + ",  Tt0 = " + String.valueOf(turbo.filter0(Turbo.tt[0] * Turbo.tconv)) + " K ");
                    }
                }
                if(turbo.pall == 1 || turbo.peng == 1 || turbo.pth == 1) {
                    Turbo.prnt.println(" ");
                    Turbo.prnt.println("Engine Thrust and Fuel Flow: ");
                    if(turbo.lunits == 0) {
                        Turbo.prnt.println(" F gross  = " + String.valueOf(turbo.filter0(Turbo.fglb))
                                           + ",  D ram = " + String.valueOf(turbo.filter0(Turbo.drlb))
                                           + ",  F net = " + String.valueOf(turbo.filter0(Turbo.fnlb)) + "  lbs");
                        Turbo.prnt.println(" Fuel Flow = " + String.valueOf(turbo.filter0(Turbo.fuelrat)) + " lbm/hr"
                                           + ",  TSFC = " + String.valueOf(turbo.filter3(Turbo.sfc)) + " lbm/(lbs*hr)");
                        Turbo.prnt.println(" Thrust/Weight = " + String.valueOf(turbo.filter3(Turbo.fnlb / Turbo.weight)));
                    }
                    if(turbo.lunits == 1) {
                        Turbo.prnt.println(" F gross  = " + String.valueOf(turbo.filter0(Turbo.fglb * Turbo.fconv))
                                           + ",  D ram = " + String.valueOf(turbo.filter0(Turbo.drlb * Turbo.fconv))
                                           + ",  F net = " + String.valueOf(turbo.filter0(Turbo.fnlb * Turbo.fconv)) + " N ");
                        Turbo.prnt.println(" Fuel Flow = " + String.valueOf(turbo.filter0(Turbo.fuelrat * Turbo.mconv1)) + " kg/hr"
                                           + ",  TSFC = " + String.valueOf(turbo.filter3(Turbo.sfc * Turbo.mconv1 / Turbo.fconv)) + " kg/(N*hr)");
                        Turbo.prnt.println(" Thrust/Weight = " + String.valueOf(turbo.filter3(Turbo.fnlb / Turbo.weight)));
                    }
                }
                if(turbo.pall == 1 || turbo.peng == 1) {
                    Turbo.prnt.println(" ");
                    Turbo.prnt.println("Engine Performance :");
                    if(turbo.lunits == 0) {
                        Turbo.prnt.println(" Throttle  = " + String.valueOf(turbo.filter3(Turbo.throtl)) + " %"
                                           + ",  core airflow (m)  = " + String.valueOf(turbo.filter3(Turbo.eair)) + " lbm/sec");
                        Turbo.prnt.println(" EPR  = " + String.valueOf(turbo.filter3(Turbo.epr))
                                           + ",  ETR  = " + String.valueOf(turbo.filter3(Turbo.etr))
                                           + ",  fuel/air  = " + String.valueOf(turbo.filter3(Turbo.fa)));
                        Turbo.prnt.println(" Nozzle Pressure Ratio  = " + String.valueOf(turbo.filter3(Turbo.npr))
                                           + ",  Vexit  = " + String.valueOf(turbo.filter0(Turbo.uexit)) + " fps ");
                        Turbo.prnt.println(" Fg/m  = " + String.valueOf(turbo.filter3(Turbo.fgros))
                                           + ",  Dram/m  = " + String.valueOf(turbo.filter3(Turbo.dram))
                                           + ",  Fn/m  = " + String.valueOf(turbo.filter3(Turbo.fnet)) + " lbs/(lbm/sec)");
                    }
                    if(turbo.lunits == 1) {
                        Turbo.prnt.println(" Throttle  = " + String.valueOf(turbo.filter3(Turbo.throtl)) + " %"
                                           + ",  core airflow (m)  = " + String.valueOf(turbo.filter3(Turbo.mconv1 * Turbo.eair)) + " kg/sec");
                        Turbo.prnt.println(" EPR  = " + String.valueOf(turbo.filter3(Turbo.epr))
                                           + ",  ETR  = " + String.valueOf(turbo.filter3(Turbo.etr))
                                           + ",  fuel/air  = " + String.valueOf(turbo.filter3(Turbo.fa)));
                        Turbo.prnt.println(" Nozzle Pressure Ratio  = " + String.valueOf(turbo.filter3(Turbo.npr))
                                           + ",  Vexit  = " + String.valueOf(turbo.filter0(Turbo.lconv1 * Turbo.uexit)) + " m/s ");
                        Turbo.prnt.println(" Fg/m  = " + String.valueOf(turbo.filter3(Turbo.fgros * Turbo.fconv / Turbo.mconv1))
                                           + ",  Dram/m  = " + String.valueOf(turbo.filter3(Turbo.dram * Turbo.fconv / Turbo.mconv1))
                                           + ",  Fn/m  = " + String.valueOf(turbo.filter3(Turbo.fnet * Turbo.fconv / Turbo.mconv1)) + " N/(kg/sec)");
                    }
                }
                if(turbo.pall == 1 || turbo.peta == 1 || turbo.pprat == 1 || turbo.ppres == 1 || turbo.pvol == 1 ||
                   turbo.ptrat == 1 || turbo.pttot == 1 || turbo.pentr == 1 || turbo.pgam == 1 || turbo.parea == 1) {
                    Turbo.prnt.println(" ");
                    Turbo.prnt.println("Component Performance :");
                    Turbo.prnt.println("   Variable \tInlet \tFan \tComp \tBurn \tH-Tur \tL-Tur \tNoz \tExhst");
                }
                if(turbo.pall == 1 || turbo.peta == 1) {
                    Turbo.prnt.println(" Efficiency"
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.eta[2]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.eta[13]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.eta[3]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.eta[4]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.eta[5]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.eta[5]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.eta[7])));
                }
                if(turbo.pall == 1 || turbo.pprat == 1) {
                    if(turbo.entype <= 1) {
                        Turbo.prnt.println(" Press Rat "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[2]))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[3]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[4]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[5]))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[7])));
                    }
                    if(turbo.entype == 2) {
                        Turbo.prnt.println(" Press Rat "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[2]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[13]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[3]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[4]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[5]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[15]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[7])));
                    }
                    if(turbo.entype == 3) {
                        Turbo.prnt.println(" Press Rat "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[2]))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[4]))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.prat[7])));
                    }
                }
                if(turbo.pall == 1 || turbo.ppres == 1) {
                    if(turbo.entype <= 1) {
                        Turbo.prnt.println(" Press - p"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[2] * Turbo.pconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[3] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[4] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[5] * Turbo.pconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[7] * Turbo.pconv)));
                    }
                    if(turbo.entype == 2) {
                        Turbo.prnt.println(" Press - p"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[2] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[13] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[3] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[4] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[5] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[15] * Turbo.pconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[7] * Turbo.pconv)));
                    }
                    if(turbo.entype == 3) {
                        Turbo.prnt.println(" Press - p"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[2] * Turbo.pconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[4] * Turbo.pconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.pt[7] * Turbo.pconv)));
                    }
                }
                if(turbo.pall == 1 || turbo.pvol == 1) {
                    if(turbo.entype <= 1) {
                        Turbo.prnt.println(" Spec Vol - v"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[2] * Turbo.dconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[3] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[4] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[5] * Turbo.dconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[7] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[8] * Turbo.dconv)));
                    }
                    if(turbo.entype == 2) {
                        Turbo.prnt.println(" Spec Vol - v"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[2] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[13] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[3] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[4] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[5] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[15] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[7] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[8] * Turbo.dconv)));
                    }
                    if(turbo.entype == 3) {
                        Turbo.prnt.println(" Spec Vol - v"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[2] * Turbo.dconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[4] * Turbo.dconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[7] * Turbo.dconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.v[8] * Turbo.dconv)));
                    }
                }
                if(turbo.pall == 1 || turbo.ptrat == 1) {
                    if(turbo.entype <= 1) {
                        Turbo.prnt.println(" Temp Rat"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[2]))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[3]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[4]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[5]))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[7])));
                    }
                    if(turbo.entype == 2) {
                        Turbo.prnt.println(" Temp Rat"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[2]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[13]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[3]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[4]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[5]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[15]))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[7])));
                    }
                    if(turbo.entype == 3) {
                        Turbo.prnt.println(" Temp Rat"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[2]))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[4]))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.trat[7])));
                    }
                }
                if(turbo.pall == 1 || turbo.pttot == 1) {
                    if(turbo.entype <= 1) {
                        Turbo.prnt.println(" Temp - T"
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[2] * Turbo.tconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[3] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[4] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[5] * Turbo.tconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[7] * Turbo.tconv)));
                    }
                    if(turbo.entype == 2) {
                        Turbo.prnt.println(" Temp - T"
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[2] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[13] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[3] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[4] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[5] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[15] * Turbo.tconv))
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[7] * Turbo.tconv)));
                    }
                    if(turbo.entype == 3) {
                        Turbo.prnt.println(" Temp - T"
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[2] * Turbo.tconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[4] * Turbo.tconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter0(Turbo.tt[7] * Turbo.tconv)));
                    }
                }
                if(turbo.pall == 1 || turbo.pentr == 1) {
                    if(turbo.entype <= 1) {
                        Turbo.prnt.println(" Entropy - s "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[2] * Turbo.bconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[3] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[4] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[5] * Turbo.bconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[7] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[8] * Turbo.bconv)));
                    }
                    if(turbo.entype == 2) {
                        Turbo.prnt.println(" Entropy   "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[2] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[13] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[3] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[4] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[5] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[15] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[7] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[8] * Turbo.bconv)));
                    }
                    if(turbo.entype == 3) {
                        Turbo.prnt.println(" Entropy   "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[2] * Turbo.bconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[4] * Turbo.bconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[7] * Turbo.bconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.s[8] * Turbo.bconv)));
                    }
                }
                if(turbo.pall == 1 || turbo.pgam == 1) {
                    Turbo.prnt.println(" Gamma     "
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.gam[2]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.gam[13]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.gam[3]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.gam[4]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.gam[5]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.gam[5]))
                                       + "\t" + String.valueOf(turbo.filter3(Turbo.gam[7])));
                }
                if(turbo.pall == 1 || turbo.parea == 1) {
                    if(turbo.entype <= 1) {
                        Turbo.prnt.println(" Area - A"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.ac * Turbo.aconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.acore * Turbo.aconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.a4 * Turbo.aconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.a8 * Turbo.aconv)));
                    }
                    if(turbo.entype == 2) {
                        Turbo.prnt.println(" Area - A"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.ac * Turbo.aconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.afan * Turbo.aconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.acore * Turbo.aconv))
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.a4 * Turbo.aconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.a4p * Turbo.aconv))
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.a8 * Turbo.aconv)));
                    }
                    if(turbo.entype == 3) {
                        Turbo.prnt.println(" Area - A"
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.ac * Turbo.aconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.acore * Turbo.aconv))
                                           + "\t" + " - "
                                           + "\t" + " - "
                                           + "\t" + String.valueOf(turbo.filter3(Turbo.a8 * Turbo.aconv)));
                    }
                }
                if(turbo.pall == 1 || turbo.peta == 1 || turbo.pprat == 1 || turbo.ppres == 1 || turbo.pvol == 1 ||
                   turbo.ptrat == 1 || turbo.pttot == 1 || turbo.pentr == 1 || turbo.pgam == 1 || turbo.parea == 1) {
                    if(turbo.lunits == 0) {
                        Turbo.prnt.println(" p = psi,  v = ft3/lbm,  T = R,  s = BTU/lbm R,  A = ft2 ");
                    }
                    if(turbo.lunits == 1) {
                        Turbo.prnt.println(" p = kPa,  v = m3/kg,  T = K,   s = kJ/kg K,   A = m2 ");
                    }
                }
            }
            if(turbo.iprint == 0) {  // file closed
                return;
            }
        }

        if(label.equals("Exit")) {
            Turbo.f.dispose();
            System.exit(1);
        }
    }

    public void handleProb(Event evt, Object obj) {
        String label = (String)obj;

        if(turbo.plttyp != 7) {
            // units change
            if(label.equals("English")) {
                turbo.lunits = 0;
                con.setUnits();
                con.setPanl();
                if(turbo.plttyp >= 3) {
                    con.setPlot();
                }
            }
            if(label.equals("Metric")) {
                turbo.lunits = 1;
                con.setUnits();
                con.setPanl();
                if(turbo.plttyp >= 3) {
                    con.setPlot();
                }
            }
            if(label.equals("% Change")) {
                turbo.lunits = 2;
                con.setUnits();
                con.setPanl();
                if(turbo.plttyp >= 3) {
                    con.setPlot();
                }
            }
            // mode
            if(label.equals("Design")) {
                turbo.inflag = 0;
            }
            if(label.equals("Test")) {
                turbo.inflag = 1;
                turbo.lunits = 0;
                con.setUnits();
                untch.select(turbo.lunits);
                turbo.solve.comPute();
                turbo.solve.myDesign();
                Turbo.ytrans = 115.0;
                turbo.view.start();
                turbo.varflag = 0;
                turbo.layin.show(turbo.in, "first");
            }

            if(label.equals("My Design")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.in, "first");
                turbo.lunits = 0;
                con.setUnits();
                untch.select(turbo.lunits);
                turbo.solve.loadMine();
            }
            if(label.equals("J85 Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.in, "first");
                turbo.lunits = 0;
                con.setUnits();
                untch.select(turbo.lunits);
                turbo.solve.loadJ85();
            }
            if(label.equals("F100 Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.in, "first");
                turbo.lunits = 0;
                con.setUnits();
                untch.select(turbo.lunits);
                turbo.solve.loadF100();
            }
            if(label.equals("CF6 Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.in, "first");
                turbo.lunits = 0;
                con.setUnits();
                untch.select(turbo.lunits);
                turbo.solve.loadCF6();
            }
            if(label.equals("Ramjet Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.in, "first");
                turbo.lunits = 0;
                con.setUnits();
                untch.select(turbo.lunits);
                turbo.solve.loadRamj();
            }

            if(label.equals("Engine Performance")) {
                turbo.plttyp = 0;
                turbo.layout.show(turbo.out, "first");
                turbo.showcom = 0;
            }
            if(label.equals("Component Performance")) {
                turbo.plttyp = 0;
                turbo.layout.show(turbo.out, "third");
                turbo.showcom = 1;
            }
            if(label.equals("Graphs")) {
                turbo.plttyp = 3;
                turbo.showcom = 0;
                turbo.layout.show(turbo.out, "second");
                con.setPlot();
                turbo.out.plot.repaint();
            }
            turbo.solve.comPute();
        }
    }
}
 
