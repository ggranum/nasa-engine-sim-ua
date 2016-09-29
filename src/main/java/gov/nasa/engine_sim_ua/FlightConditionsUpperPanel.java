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
 *
 */
class FlightConditionsUpperPanel extends Panel {

    private final FlightConditionsPanel flightConditionsPanel;
    private final Turbo turbo;
    Choice chcTemplate;
    Choice chcMode;
    Choice chcOutput;
    Choice chcUnits;

    FlightConditionsUpperPanel(FlightConditionsPanel flightConditionsPanel, Turbo target) {
        this.flightConditionsPanel = flightConditionsPanel;
        this.turbo = target;
        setLayout(new GridLayout(4, 3, 5, 5));

        chcMode = new Choice();
        chcMode.addItem("Design");
        chcMode.addItem("Test");
        chcMode.select(0);

        chcTemplate = new Choice();
        chcTemplate.addItem("My Design");
        chcTemplate.addItem("J85 Model");
        chcTemplate.addItem("F100 Model");
        chcTemplate.addItem("CF6 Model");
        chcTemplate.addItem("Ramjet Model");
        chcTemplate.select(0);

        Label l3 = new Label("Output :", Label.RIGHT);
        l3.setForeground(Color.red);
        chcOutput = new Choice();
        chcOutput.addItem("Graphs");
        chcOutput.addItem("Engine Performance");
        chcOutput.addItem("Component Performance");
        chcOutput.select(1);
        chcOutput.setBackground(Color.white);
        chcOutput.setForeground(Color.red);

        chcUnits = new Choice();
        chcUnits.addItem("English");
        chcUnits.addItem("Metric");
        //     untch.addItem("% Change");
        chcUnits.select(0);

        Button btnReset = new Button("Reset");
        btnReset.setBackground(Color.orange);
        btnReset.setForeground(Color.black);

        Button btnPrintData = new Button("Print Data");
        btnPrintData.setBackground(Color.blue);
        btnPrintData.setForeground(Color.green);

        Button btnExit = new Button("Exit");
        btnExit.setBackground(Color.red);
        btnExit.setForeground(Color.green);

        add(new Label("Mode:", Label.RIGHT));
        add(chcMode);
        add(btnExit);

        add(new Label("Load:", Label.RIGHT));
        add(chcTemplate);
        add(btnReset);

        add(new Label("Units:", Label.RIGHT));
        add(chcUnits);
        add(btnPrintData);

        add(l3);
        add(chcOutput);
        add(new Label("to record", Label.CENTER));

    }

    public Insets getInsets() {
        return new Insets(0, 5, 5, 0);
    }

    public boolean action(Event evt, Object arg) {
        if(evt.target instanceof Choice) {
            this.handleProb(arg);
            return true;
        }
        if(evt.target instanceof Button) {
            this.handleRefs(arg);
            return true;
        } else {
            return false;
        }
    }

    public void handleRefs(Object arg) {
        String label = (String)arg;

        if(label.equals("Reset")) {
            handleResetClicked();
        }
        if(label.equals("Print Data")) {
            if(handlePrintDataClicked()) { return; }
        }

        if(label.equals("Exit")) {
            turbo.mainFrame.dispose();
            System.exit(1);
        }
    }

    private boolean handlePrintDataClicked() {
        String seng;
        String sgamop;
        String smode;
        if(turbo.iprint == 1) {  // file open - print data
            turbo.prnt.println("----------------------------------------- ");
            turbo.prnt.println(" ");
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
            turbo.prnt.println(seng);
            if(turbo.entype == 2) {
                turbo.prnt.println("  Bypass Ratio  = " + String.format("%.3f", turbo.byprat));
            }
            if(turbo.entype == 1) {
                if(turbo.abflag == 0) {
                    turbo.prnt.println("  Afterburner  OFF ");
                }
                if(turbo.abflag == 1) {
                    turbo.prnt.println("  Afterburner  ON ");
                }
            }
            if(turbo.units == Turbo.Unit.ENGLISH) {
                turbo.prnt.println("  Diameter  = " + String.format("%.3f", turbo.diameng) + " ft ");
                turbo.prnt.println("  Estimated Weight  = " + String.format("%.3f", turbo.weight) + " lbs ");
            }
            if(turbo.units == Turbo.Unit.METRIC) {
                turbo.prnt.println("  Diameter  = " + String.format("%.3f", turbo.diameng) + " m ");
                turbo.prnt.println("  Estimated Weight  = " + String.format("%.3f", turbo.weight * turbo.fconv) + " N ");
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
            turbo.prnt.println(smode + sgamop);
            if(turbo.pall == 1 || turbo.pfs == 1) {
                turbo.prnt.println(" ");
                turbo.prnt.println("FlightPanel Conditions: ");
                if(turbo.units == Turbo.Unit.ENGLISH) {
                    turbo.prnt.println("  Mach = " + String.format("%.3f", turbo.fsmach)
                                       + ",  V0 = " + String.format("%.0f", turbo.u0d) + " mph ");
                    turbo.prnt.println("  Alt = " + String.format("%.0f", turbo.altd) + " ft ");
                    turbo.prnt.println("  p0 = " + String.format("%.3f", turbo.ps0)
                                       + ",  pt0 = " + String.format("%.3f", turbo.pt[0]) + " psi");
                    turbo.prnt.println("  T0 = " + String.format("%.0f", turbo.ts0)
                                       + ",  Tt0 = " + String.format("%.0f", turbo.tt[0]) + " R ");
                }
                if(turbo.units == Turbo.Unit.METRIC) {
                    turbo.prnt.println("  Mach = " + String.format("%.3f", turbo.fsmach)
                                       + ",  V0 = " + String.format("%.0f", turbo.u0d) + " km/h ");
                    turbo.prnt.println("  Alt = " + String.format("%.0f", turbo.altd) + " m ");
                    turbo.prnt.println("  p0 = " + String.format("%.3f", turbo.ps0 * turbo.pconv)
                                       + ",  pt0 = " + String.format("%.3f", turbo.pt[0] * turbo.pconv) + " k Pa");
                    turbo.prnt.println("  T0 = " + String.format("%.0f", turbo.ts0 * turbo.tconv)
                                       + ",  Tt0 = " + String.format("%.0f", turbo.tt[0] * turbo.tconv) + " K ");
                }
            }
            if(turbo.pall == 1 || turbo.peng == 1 || turbo.pth == 1) {
                turbo.prnt.println(" ");
                turbo.prnt.println("Engine Thrust and Fuel Flow: ");
                if(turbo.units == Turbo.Unit.ENGLISH) {
                    turbo.prnt.println(" F gross  = " + String.format("%.0f", turbo.fglb)
                                       + ",  D ram = " + String.format("%.0f", turbo.drlb)
                                       + ",  F net = " + String.format("%.0f", turbo.fnlb) + "  lbs");
                    turbo.prnt.println(" Fuel Flow = " + String.format("%.0f", turbo.fuelrat) + " lbm/hr"
                                       + ",  TSFC = " + String.format("%.3f", turbo.sfc) + " lbm/(lbs*hr)");
                    turbo.prnt.println(" Thrust/Weight = " + String.format("%.3f", turbo.fnlb / turbo.weight));
                }
                if(turbo.units == Turbo.Unit.METRIC) {
                    turbo.prnt.println(" F gross  = " + String.format("%.0f", turbo.fglb * turbo.fconv)
                                       + ",  D ram = " + String.format("%.0f", turbo.drlb * turbo.fconv)
                                       + ",  F net = " + String.format("%.0f", turbo.fnlb * turbo.fconv) + " N ");
                    turbo.prnt.println(" Fuel Flow = " + String.format("%.0f", turbo.fuelrat * turbo.mconv1) + " kg/hr"
                                       + ",  TSFC = " + String.format("%.3f", turbo.sfc * turbo.mconv1 / turbo.fconv) + " kg/(N*hr)");
                    turbo.prnt.println(" Thrust/Weight = " + String.format("%.3f", turbo.fnlb / turbo.weight));
                }
            }
            if(turbo.pall == 1 || turbo.peng == 1) {
                turbo.prnt.println(" ");
                turbo.prnt.println("Engine Performance :");
                if(turbo.units == Turbo.Unit.ENGLISH) {
                    turbo.prnt.println(" Throttle  = " + String.format("%.3f", turbo.throtl) + " %"
                                       + ",  core airflow (m)  = " + String.format("%.3f", turbo.eair) + " lbm/sec");
                    turbo.prnt.println(" EPR  = " + String.format("%.3f", turbo.epr)
                                       + ",  ETR  = " + String.format("%.3f", turbo.etr)
                                       + ",  fuel/air  = " + String.format("%.3f", turbo.fa));
                    turbo.prnt.println(" Nozzle Pressure Ratio  = " + String.format("%.3f", turbo.npr)
                                       + ",  Vexit  = " + String.format("%.0f", turbo.uexit) + " fps ");
                    turbo.prnt.println(" Fg/m  = " + String.format("%.3f", turbo.fgros)
                                       + ",  Dram/m  = " + String.format("%.3f", turbo.dram)
                                       + ",  Fn/m  = " + String.format("%.3f", turbo.fnet) + " lbs/(lbm/sec)");
                }
                if(turbo.units == Turbo.Unit.METRIC) {
                    turbo.prnt.println(" Throttle  = " + String.format("%.3f", turbo.throtl) + " %"
                                       + ",  core airflow (m)  = " + String.format("%.3f", turbo.mconv1 * turbo.eair) + " kg/sec");
                    turbo.prnt.println(" EPR  = " + String.format("%.3f", turbo.epr)
                                       + ",  ETR  = " + String.format("%.3f", turbo.etr)
                                       + ",  fuel/air  = " + String.format("%.3f", turbo.fa));
                    turbo.prnt.println(" Nozzle Pressure Ratio  = " + String.format("%.3f", turbo.npr)
                                       + ",  Vexit  = " + String.format("%.0f", turbo.lconv1 * turbo.uexit) + " m/s ");
                    turbo.prnt.println(" Fg/m  = " + String.format("%.3f", turbo.fgros * turbo.fconv / turbo.mconv1)
                                       + ",  Dram/m  = " + String.format("%.3f", turbo.dram * turbo.fconv / turbo.mconv1)
                                       + ",  Fn/m  = " + String.format("%.3f", turbo.fnet * turbo.fconv / turbo.mconv1) + " N/(kg/sec)");
                }
            }
            if(turbo.pall == 1 || turbo.peta == 1 || turbo.pprat == 1 || turbo.ppres == 1 || turbo.pvol == 1 ||
               turbo.ptrat == 1 || turbo.pttot == 1 || turbo.pentr == 1 || turbo.pgam == 1 || turbo.parea == 1) {
                turbo.prnt.println(" ");
                turbo.prnt.println("Component Performance :");
                turbo.prnt.println("   Variable \tInletPanel \tFanPanel \tCompressorPanel \tBurnerPanel \tH-Tur \tL-Tur \tNoz \tExhst");
            }
            if(turbo.pall == 1 || turbo.peta == 1) {
                turbo.prnt.println(" Efficiency"
                                   + "\t" + String.format("%.3f", turbo.efficiency[2])
                                   + "\t" + String.format("%.3f", turbo.efficiency[13])
                                   + "\t" + String.format("%.3f", turbo.efficiency[3])
                                   + "\t" + String.format("%.3f", turbo.efficiency[4])
                                   + "\t" + String.format("%.3f", turbo.efficiency[5])
                                   + "\t" + String.format("%.3f", turbo.efficiency[5])
                                   + "\t" + String.format("%.3f", turbo.efficiency[7]));
            }
            if(turbo.pall == 1 || turbo.pprat == 1) {
                if(turbo.entype <= 1) {
                    turbo.prnt.println(" Press Rat "
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[2])
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[3])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[4])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[5])
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[7]));
                }
                if(turbo.entype == 2) {
                    turbo.prnt.println(" Press Rat "
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[2])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[13])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[3])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[4])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[5])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[15])
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[7]));
                }
                if(turbo.entype == 3) {
                    turbo.prnt.println(" Press Rat "
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[2])
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[4])
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pressureRatio[7]));
                }
            }
            if(turbo.pall == 1 || turbo.ppres == 1) {
                if(turbo.entype <= 1) {
                    turbo.prnt.println(" Press - p"
                                       + "\t" + String.format("%.3f", turbo.pt[2] * turbo.pconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pt[3] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[4] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[5] * turbo.pconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pt[7] * turbo.pconv));
                }
                if(turbo.entype == 2) {
                    turbo.prnt.println(" Press - p"
                                       + "\t" + String.format("%.3f", turbo.pt[2] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[13] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[3] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[4] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[5] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[15] * turbo.pconv)
                                       + "\t" + String.format("%.3f", turbo.pt[7] * turbo.pconv));
                }
                if(turbo.entype == 3) {
                    turbo.prnt.println(" Press - p"
                                       + "\t" + String.format("%.3f", turbo.pt[2] * turbo.pconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pt[4] * turbo.pconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.pt[7] * turbo.pconv));
                }
            }
            if(turbo.pall == 1 || turbo.pvol == 1) {
                if(turbo.entype <= 1) {
                    turbo.prnt.println(" Spec Vol - v"
                                       + "\t" + String.format("%.3f", turbo.v[2] * turbo.dconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.v[3] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[4] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[5] * turbo.dconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.v[7] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[8] * turbo.dconv));
                }
                if(turbo.entype == 2) {
                    turbo.prnt.println(" Spec Vol - v"
                                       + "\t" + String.format("%.3f", turbo.v[2] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[13] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[3] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[4] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[5] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[15] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[7] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[8] * turbo.dconv));
                }
                if(turbo.entype == 3) {
                    turbo.prnt.println(" Spec Vol - v"
                                       + "\t" + String.format("%.3f", turbo.v[2] * turbo.dconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.v[4] * turbo.dconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.v[7] * turbo.dconv)
                                       + "\t" + String.format("%.3f", turbo.v[8] * turbo.dconv));
                }
            }
            if(turbo.pall == 1 || turbo.ptrat == 1) {
                if(turbo.entype <= 1) {
                    turbo.prnt.println(" Temp Rat"
                                       + "\t" + String.format("%.3f", turbo.trat[2])
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.trat[3])
                                       + "\t" + String.format("%.3f", turbo.trat[4])
                                       + "\t" + String.format("%.3f", turbo.trat[5])
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.trat[7]));
                }
                if(turbo.entype == 2) {
                    turbo.prnt.println(" Temp Rat"
                                       + "\t" + String.format("%.3f", turbo.trat[2])
                                       + "\t" + String.format("%.3f", turbo.trat[13])
                                       + "\t" + String.format("%.3f", turbo.trat[3])
                                       + "\t" + String.format("%.3f", turbo.trat[4])
                                       + "\t" + String.format("%.3f", turbo.trat[5])
                                       + "\t" + String.format("%.3f", turbo.trat[15])
                                       + "\t" + String.format("%.3f", turbo.trat[7]));
                }
                if(turbo.entype == 3) {
                    turbo.prnt.println(" Temp Rat"
                                       + "\t" + String.format("%.3f", turbo.trat[2])
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.trat[4])
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.trat[7]));
                }
            }
            if(turbo.pall == 1 || turbo.pttot == 1) {
                if(turbo.entype <= 1) {
                    turbo.prnt.println(" Temp - T"
                                       + "\t" + String.format("%.0f", turbo.tt[2] * turbo.tconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.0f", turbo.tt[3] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[4] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[5] * turbo.tconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.0f", turbo.tt[7] * turbo.tconv));
                }
                if(turbo.entype == 2) {
                    turbo.prnt.println(" Temp - T"
                                       + "\t" + String.format("%.0f", turbo.tt[2] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[13] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[3] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[4] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[5] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[15] * turbo.tconv)
                                       + "\t" + String.format("%.0f", turbo.tt[7] * turbo.tconv));
                }
                if(turbo.entype == 3) {
                    turbo.prnt.println(" Temp - T"
                                       + "\t" + String.format("%.0f", turbo.tt[2] * turbo.tconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.0f", turbo.tt[4] * turbo.tconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.0f", turbo.tt[7] * turbo.tconv));
                }
            }
            if(turbo.pall == 1 || turbo.pentr == 1) {
                if(turbo.entype <= 1) {
                    turbo.prnt.println(" Entropy - s "
                                       + "\t" + String.format("%.3f", turbo.s[2] * turbo.bconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.s[3] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[4] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[5] * turbo.bconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.s[7] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[8] * turbo.bconv));
                }
                if(turbo.entype == 2) {
                    turbo.prnt.println(" Entropy   "
                                       + "\t" + String.format("%.3f", turbo.s[2] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[13] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[3] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[4] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[5] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[15] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[7] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[8] * turbo.bconv));
                }
                if(turbo.entype == 3) {
                    turbo.prnt.println(" Entropy   "
                                       + "\t" + String.format("%.3f", turbo.s[2] * turbo.bconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.s[4] * turbo.bconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.s[7] * turbo.bconv)
                                       + "\t" + String.format("%.3f", turbo.s[8] * turbo.bconv));
                }
            }
            if(turbo.pall == 1 || turbo.pgam == 1) {
                turbo.prnt.println(" Gamma     "
                                   + "\t" + String.format("%.3f", turbo.gam[2])
                                   + "\t" + String.format("%.3f", turbo.gam[13])
                                   + "\t" + String.format("%.3f", turbo.gam[3])
                                   + "\t" + String.format("%.3f", turbo.gam[4])
                                   + "\t" + String.format("%.3f", turbo.gam[5])
                                   + "\t" + String.format("%.3f", turbo.gam[5])
                                   + "\t" + String.format("%.3f", turbo.gam[7]));
            }
            if(turbo.pall == 1 || turbo.parea == 1) {
                if(turbo.entype <= 1) {
                    turbo.prnt.println(" Area - A"
                                       + "\t" + String.format("%.3f", turbo.ac * turbo.aconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.acore * turbo.aconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.a4 * turbo.aconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.a8 * turbo.aconv));
                }
                if(turbo.entype == 2) {
                    turbo.prnt.println(" Area - A"
                                       + "\t" + String.format("%.3f", turbo.ac * turbo.aconv)
                                       + "\t" + String.format("%.3f", turbo.afan * turbo.aconv)
                                       + "\t" + String.format("%.3f", turbo.acore * turbo.aconv)
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.a4 * turbo.aconv)
                                       + "\t" + String.format("%.3f", turbo.a4p * turbo.aconv)
                                       + "\t" + String.format("%.3f", turbo.a8 * turbo.aconv));
                }
                if(turbo.entype == 3) {
                    turbo.prnt.println(" Area - A"
                                       + "\t" + String.format("%.3f", turbo.ac * turbo.aconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.acore * turbo.aconv)
                                       + "\t" + " - "
                                       + "\t" + " - "
                                       + "\t" + String.format("%.3f", turbo.a8 * turbo.aconv));
                }
            }
            if(turbo.pall == 1 || turbo.peta == 1 || turbo.pprat == 1 || turbo.ppres == 1 || turbo.pvol == 1 ||
               turbo.ptrat == 1 || turbo.pttot == 1 || turbo.pentr == 1 || turbo.pgam == 1 || turbo.parea == 1) {
                if(turbo.units == Turbo.Unit.ENGLISH) {
                    turbo.prnt.println(" p = psi,  v = ft3/lbm,  T = R,  s = BTU/lbm R,  A = ft2 ");
                }
                if(turbo.units == Turbo.Unit.METRIC) {
                    turbo.prnt.println(" p = kPa,  v = m3/kg,  T = K,   s = kJ/kg K,   A = m2 ");
                }
            }
        }
        if(turbo.iprint == 0) {  // file closed
            return true;
        }
        return false;
    }

    private void handleResetClicked() {
        if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
            // reset reference variables
            turbo.u0ref = turbo.u0d;
            turbo.altref = turbo.altd;
            turbo.thrref = turbo.throtl;
            turbo.a2ref = turbo.a2d;
            turbo.et2ref = turbo.efficiency[2];
            turbo.fpref = turbo.p3fp2d;
            turbo.et13ref = turbo.efficiency[13];
            turbo.bpref = turbo.byprat;
            turbo.cpref = turbo.p3p2d;
            turbo.et3ref = turbo.efficiency[3];
            turbo.et4ref = turbo.efficiency[4];
            turbo.et5ref = turbo.efficiency[5];
            turbo.t4ref = turbo.tt4d;
            turbo.p4ref = turbo.pressureRatio[4];
            turbo.t7ref = turbo.tt7d;
            turbo.et7ref = turbo.efficiency[7];
            turbo.a8ref = turbo.a8rat;
            turbo.fnref = turbo.fnlb;
            turbo.fuelref = turbo.fuelrat;
            turbo.sfcref = turbo.sfc;
            turbo.airref = turbo.eair;
            turbo.epref = turbo.epr;
            turbo.etref = turbo.etr;
            turbo.faref = turbo.fa;
            turbo.wtref = turbo.weight;
            turbo.wfref = turbo.fnlb / turbo.weight;
            flightConditionsPanel.setPanl();

            turbo.outputPanel.outputMainPanel.loadOut();
            turbo.outputPanel.outputVariablesPanel.loadOut();
        } else {
            turbo.solve.setDefaults();
            flightConditionsPanel.setUnits();
            turbo.flightConditionsPanel.setPanl();
            turbo.layin.show(turbo.inputPanel, "first");
            turbo.layout.show(turbo.outputPanel, "first");
            turbo.flightConditionsPanel.flightConditionsUpperPanel.chcTemplate.select(0);
            turbo.flightConditionsPanel.flightConditionsUpperPanel.chcMode.select(0);
            turbo.flightConditionsPanel.flightConditionsUpperPanel.chcOutput.select(1);
            turbo.flightConditionsPanel.flightConditionsUpperPanel.chcUnits.select(0);
            turbo.inputPanel.flightPanel.flightLeftPanel.o1.setBackground(Color.black);
            turbo.inputPanel.flightPanel.flightLeftPanel.o1.setForeground(Color.yellow);
            turbo.inputPanel.flightPanel.flightLeftPanel.o2.setBackground(Color.black);
            turbo.inputPanel.flightPanel.flightLeftPanel.o2.setForeground(Color.yellow);
            turbo.inputPanel.flightPanel.flightLeftPanel.o3.setBackground(Color.black);
            turbo.inputPanel.flightPanel.flightLeftPanel.o3.setForeground(Color.yellow);
            turbo.inputPanel.flightPanel.flightLeftPanel.f1.setBackground(Color.white);
            turbo.inputPanel.flightPanel.flightLeftPanel.f1.setForeground(Color.black);
            turbo.inputPanel.flightPanel.flightLeftPanel.f2.setBackground(Color.white);
            turbo.inputPanel.flightPanel.flightLeftPanel.f2.setForeground(Color.black);
            turbo.inputPanel.sizePanel.sizeRightPanel.sizch.select(0);
            turbo.inputPanel.sizePanel.sizeLeftPanel.f1.setBackground(Color.white);
            turbo.inputPanel.sizePanel.sizeLeftPanel.f1.setForeground(Color.black);
            turbo.inputPanel.sizePanel.sizeLeftPanel.f3.setBackground(Color.black);
            turbo.inputPanel.sizePanel.sizeLeftPanel.f3.setForeground(Color.yellow);
            turbo.inputPanel.compressorPanel.compressorRightPanel.stgch.select(0);
            turbo.inputPanel.compressorPanel.compressorLeftPanel.getF3().setBackground(Color.black);
            turbo.inputPanel.compressorPanel.compressorLeftPanel.getF3().setForeground(Color.yellow);
            turbo.inputPanel.turbinePanel.turbineRightPanel.stgch.select(0);
            turbo.inputPanel.turbinePanel.turbineLeftPanel.getF3().setBackground(Color.black);
            turbo.inputPanel.turbinePanel.turbineLeftPanel.getF3().setForeground(Color.yellow);
            turbo.inputPanel.inletPanel.inletRightPanel.imat.select(turbo.minlt);
            turbo.inputPanel.fanPanel.rightPanel.fmat.select(turbo.mfan);
            turbo.inputPanel.compressorPanel.compressorRightPanel.cmat.select(turbo.mcomp);
            turbo.inputPanel.burnerPanel.burnerRightPanel.chcMaterial.select(turbo.burnerMaterial);
            turbo.inputPanel.turbinePanel.turbineRightPanel.tmat.select(turbo.mturbin);
            turbo.inputPanel.nozzlePanel.nozzleRightPanel.nmat.select(turbo.mnozl);
            turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.nrmat.select(turbo.mnozr);
            turbo.solve.compute();
            turbo.outputPanel.outputPlotCanvas.repaint();
        }
    }

    public void handleProb( Object obj) {
        String label = (String)obj;

        if(turbo.plttyp != 7) {
            // units change
            if(label.equals("English")) {
                turbo.units = Turbo.Unit.ENGLISH;
                flightConditionsPanel.setUnits();
                flightConditionsPanel.setPanl();
                if(turbo.plttyp >= 3) {
                    flightConditionsPanel.setPlot();
                }
            }
            if(label.equals("Metric")) {
                turbo.units = Turbo.Unit.METRIC;
                flightConditionsPanel.setUnits();
                flightConditionsPanel.setPanl();
                if(turbo.plttyp >= 3) {
                    flightConditionsPanel.setPlot();
                }
            }
            if(label.equals("% Change")) {
                turbo.units = Turbo.Unit.PERCENT_CHANGE;
                flightConditionsPanel.setUnits();
                flightConditionsPanel.setPanl();
                if(turbo.plttyp >= 3) {
                    flightConditionsPanel.setPlot();
                }
            }
            // mode
            if(label.equals("Design")) {
                turbo.inflag = 0;
            }
            if(label.equals("Test")) {
                turbo.inflag = 1;
                turbo.units = Turbo.Unit.ENGLISH;
                flightConditionsPanel.setUnits();
                chcUnits.select(turbo.units.value);
                turbo.solve.compute();
                turbo.solve.myDesign();
                turbo.ytrans = 115.0;
                turbo.view.start();
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
            }

            if(label.equals("My Design")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.units = Turbo.Unit.ENGLISH;
                flightConditionsPanel.setUnits();
                chcUnits.select(turbo.units.value);
                turbo.solve.loadMine();
            }
            if(label.equals("J85 Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.units = Turbo.Unit.ENGLISH;
                flightConditionsPanel.setUnits();
                chcUnits.select(turbo.units.value);
                turbo.solve.loadJ85();
            }
            if(label.equals("F100 Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.units = Turbo.Unit.ENGLISH;
                flightConditionsPanel.setUnits();
                chcUnits.select(turbo.units.value);
                turbo.solve.loadF100();
            }
            if(label.equals("CF6 Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.units = Turbo.Unit.ENGLISH;
                flightConditionsPanel.setUnits();
                chcUnits.select(turbo.units.value);
                turbo.solve.loadCF6();
            }
            if(label.equals("Ramjet Model")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.units = Turbo.Unit.ENGLISH;
                flightConditionsPanel.setUnits();
                chcUnits.select(turbo.units.value);
                turbo.solve.loadRamj();
            }

            if(label.equals("Engine Performance")) {
                turbo.plttyp = 0;
                turbo.layout.show(turbo.outputPanel, "first");
                turbo.showcom = 0;
            }
            if(label.equals("Component Performance")) {
                turbo.plttyp = 0;
                turbo.layout.show(turbo.outputPanel, "third");
                turbo.showcom = 1;
            }
            if(label.equals("Graphs")) {
                turbo.plttyp = 3;
                turbo.showcom = 0;
                turbo.layout.show(turbo.outputPanel, "second");
                flightConditionsPanel.setPlot();
                turbo.outputPanel.outputPlotCanvas.repaint();
            }
            turbo.solve.compute();
        }
    }
}
 
