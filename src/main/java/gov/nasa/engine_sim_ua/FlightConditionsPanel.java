package gov.nasa.engine_sim_ua;

import java.awt.GridLayout;
import java.awt.Panel;

/**
 *
 */
class FlightConditionsPanel extends Panel {

    private final Turbo turbo;
    FlightConditionsUpperPanel flightConditionsUpperPanel;
    FlightConditionsLowerPanel flightConditionsLowerPanel;

    FlightConditionsPanel(Turbo turbo) {
        this.turbo = turbo;

        setLayout(new GridLayout(2, 1, 5, 5));

        flightConditionsUpperPanel = new FlightConditionsUpperPanel(this, turbo);
        flightConditionsLowerPanel = new FlightConditionsLowerPanel();

        add(flightConditionsUpperPanel);
        add(flightConditionsLowerPanel);
    }

    public void setPanl() {
        double v1;
        double v2;
        double v3;
        double v4;
        int i1;
        int i2;
        int i3;
        int i4;

        // set limits and labels
        // flightPanel conditions
        v1 = 0.0;
        turbo.vmn1 = -10.0;
        turbo.vmx1 = 10.0;
        v2 = 0.0;
        turbo.vmn2 = -10.0;
        turbo.vmx2 = 10.0;
        v3 = 0.0;
        turbo.vmn3 = -10.0;
        turbo.vmx3 = 10.0;
        v4 = turbo.gama;

        if(turbo.lunits <= 1) {
            v1 = turbo.u0d;
            turbo.vmn1 = turbo.u0min;
            turbo.vmx1 = turbo.u0max;
            v2 = turbo.altd;
            turbo.vmn2 = turbo.altmin;
            turbo.vmx2 = turbo.altmax;
            v3 = turbo.throtl;
            turbo.vmn3 = turbo.thrmin;
            turbo.vmx3 = turbo.thrmax;
        }

        turbo.inputPanel.flightPanel.flightLeftPanel.f1.setText(String.format("%.0f", v1));
        turbo.inputPanel.flightPanel.flightLeftPanel.f2.setText(String.format("%.0f", v2));
        turbo.inputPanel.flightPanel.flightLeftPanel.f3.setText(String.format("%.3f", v3));
        turbo.inputPanel.flightPanel.flightLeftPanel.f4.setText(String.format("%.3f", v4));

        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

        turbo.inputPanel.flightPanel.flightRightPanel.s1.setValue(i1);
        turbo.inputPanel.flightPanel.flightRightPanel.s2.setValue(i2);
        turbo.inputPanel.flightPanel.flightRightPanel.s3.setValue(i3);

        turbo.inputPanel.flightPanel.flightRightPanel.inptch.select(turbo.inptype);
        turbo.inputPanel.flightPanel.flightRightPanel.nozch.select(turbo.abflag);
        turbo.inputPanel.flightPanel.flightLeftPanel.inpch.select(turbo.gamopt);

        // sizePanel
        v1 = 0.0;
        turbo.vmn1 = -10.0;
        turbo.vmx1 = 10.0;
        turbo.vmn3 = -10.0;
        turbo.vmx3 = 10.0;
        if(turbo.lunits <= 1) {
            v1 = turbo.a2d;
            turbo.vmn1 = turbo.a2min;
            turbo.vmx1 = turbo.a2max;
            v3 = turbo.diameng;
        }
        turbo.inputPanel.sizePanel.sizeLeftPanel.f1.setText(String.format("%.3f", v1));
        turbo.inputPanel.sizePanel.sizeLeftPanel.f3.setText(String.format("%.3f", v3));

        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);

        turbo.inputPanel.sizePanel.sizeRightPanel.sizch.select(turbo.siztype);
        turbo.inputPanel.sizePanel.sizeRightPanel.getS1().setValue(i1);

        turbo.inputPanel.sizePanel.sizeLeftPanel.f2.setText(String.format("%.0f", turbo.weight));
        turbo.inputPanel.sizePanel.sizeRightPanel.getChmat().select(turbo.wtflag);

        // inletPanel
        if(turbo.pt2flag == 0) {             /*     mil spec      */
            if(turbo.fsmach > 1.0) {          /* supersonic */
                turbo.eta[2] = 1.0 - .075 * Math.pow(turbo.fsmach - 1.0, 1.35);
            } else {
                turbo.eta[2] = 1.0;
            }
        }

        v1 = turbo.eta[2];
        turbo.vmn1 = turbo.etmin;
        turbo.vmx1 = turbo.etmax;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            turbo.vmx1 = 100.0 - 100.0 * turbo.et2ref;
            turbo.vmn1 = turbo.vmx1 - 20.0;
        }

        turbo.inputPanel.inletPanel.inletLeftPanel.getF1().setText(String.format("%.3f", v1));
        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
        turbo.inputPanel.inletPanel.inletRightPanel.s1.setValue(i1);
        // materials
        turbo.inputPanel.inletPanel.inletRightPanel.imat.select(turbo.minlt);
        turbo.inputPanel.inletPanel.inletLeftPanel.getDi().setText(String.format("%.0f", turbo.dinlt));
        turbo.inputPanel.inletPanel.inletLeftPanel.getTi().setText(String.format("%.0f", turbo.tinlt));
        //  fanPanel
        v1 = turbo.p3fp2d;
        turbo.vmn1 = turbo.fprmin;
        turbo.vmx1 = turbo.fprmax;
        v2 = turbo.eta[13];
        turbo.vmn2 = turbo.etmin;
        turbo.vmx2 = turbo.etmax;
        v3 = turbo.byprat;
        turbo.vmn3 = turbo.bypmin;
        turbo.vmx3 = turbo.bypmax;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            turbo.vmn1 = -10.0;
            turbo.vmx1 = 10.0;
            v2 = 0.0;
            turbo.vmx2 = 100.0 - 100.0 * turbo.et13ref;
            turbo.vmn2 = turbo.vmx2 - 20.0;
            v3 = 0.0;
            turbo.vmn3 = -10.0;
            turbo.vmx3 = 10.0;
        }

        turbo.inputPanel.fanPanel.leftPanel.getF1().setText(String.valueOf((float)v1));
        turbo.inputPanel.fanPanel.leftPanel.getF2().setText(String.valueOf((float)v2));
        turbo.inputPanel.fanPanel.leftPanel.getF3().setText(String.valueOf(v3));

        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

        turbo.inputPanel.fanPanel.rightPanel.getS1().setValue(i1);
        turbo.inputPanel.fanPanel.rightPanel.getS2().setValue(i2);
        turbo.inputPanel.fanPanel.rightPanel.getS3().setValue(i3);

        // materials
        turbo.inputPanel.fanPanel.rightPanel.fmat.select(turbo.mfan);
        turbo.inputPanel.fanPanel.leftPanel.getDf().setText(String.format("%.0f", turbo.dfan));
        turbo.inputPanel.fanPanel.leftPanel.getTf().setText(String.format("%.0f", turbo.tfan));
        // compressor
        v1 = turbo.p3p2d;
        turbo.vmn1 = turbo.cprmin;
        turbo.vmx1 = turbo.cprmax;
        v2 = turbo.eta[3];
        turbo.vmn2 = turbo.etmin;
        turbo.vmx2 = turbo.etmax;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            turbo.vmn1 = -10.0;
            turbo.vmx1 = 10.0;
            v2 = 0.0;
            turbo.vmx2 = 100.0 - 100.0 * turbo.et3ref;
            turbo.vmn2 = turbo.vmx2 - 20.0;
        }
        
        turbo.inputPanel.compressorPanel.compressorLeftPanel.getF1().setText(String.valueOf((float)v1));
        turbo.inputPanel.compressorPanel.compressorLeftPanel.getF2().setText(String.valueOf((float)v2));

        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);

        turbo.inputPanel.compressorPanel.compressorRightPanel.s1.setValue(i1);
        turbo.inputPanel.compressorPanel.compressorRightPanel.s2.setValue(i2);
        // materials
        turbo.inputPanel.compressorPanel.compressorRightPanel.cmat.select(turbo.mcomp);
        turbo.inputPanel.compressorPanel.compressorLeftPanel.getDc().setText(String.format("%.0f", turbo.dcomp));
        turbo.inputPanel.compressorPanel.compressorLeftPanel.getTc().setText(String.format("%.0f", turbo.tcomp));
        //  burner
        v1 = turbo.tt4d;
        turbo.vmn1 = turbo.t4min;
        turbo.vmx1 = turbo.t4max;
        v2 = turbo.eta[4];
        turbo.vmn2 = turbo.etmin;
        turbo.vmx2 = turbo.etmax;
        v3 = turbo.prat[4];
        turbo.vmn3 = turbo.etmin;
        turbo.vmx3 = turbo.pt4max;
        v4 = turbo.fhvd;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            turbo.vmn1 = -10.0;
            turbo.vmx1 = 10.0;
            v2 = 0.0;
            turbo.vmx2 = 100.0 - 100.0 * turbo.et4ref;
            turbo.vmn2 = turbo.vmx2 - 20.0;
            v3 = 0.0;
            turbo.vmx3 = 100.0 - 100.0 * turbo.p4ref;
            turbo.vmn3 = turbo.vmx3 - 20.0;
        }

        turbo.inputPanel.burnerPanel.burnerLeftPanel.getF1().setText(String.format("%.0f", v1));
        turbo.inputPanel.burnerPanel.burnerLeftPanel.getF2().setText(String.valueOf(v2));
        turbo.inputPanel.burnerPanel.burnerLeftPanel.getF3().setText(String.valueOf(v3));
        turbo.inputPanel.burnerPanel.burnerLeftPanel.getF4().setText(String.format("%.0f", v4));

        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

        turbo.inputPanel.burnerPanel.burnerRightPanel.s1.setValue(i1);
        turbo.inputPanel.burnerPanel.burnerRightPanel.s2.setValue(i2);
        turbo.inputPanel.burnerPanel.burnerRightPanel.s3.setValue(i3);
        turbo.inputPanel.burnerPanel.burnerRightPanel.fuelch.select(turbo.fueltype);
        // materials
        turbo.inputPanel.burnerPanel.burnerRightPanel.bmat.select(turbo.mburner);
        turbo.inputPanel.burnerPanel.burnerLeftPanel.getDb().setText(String.format("%.0f", turbo.dburner));
        turbo.inputPanel.burnerPanel.burnerLeftPanel.getTb().setText(String.format("%.0f", turbo.tburner));
        //  turbine
        v1 = turbo.eta[5];
        turbo.vmn1 = turbo.etmin;
        turbo.vmx1 = turbo.etmax;
        if(turbo.lunits == 2) {
            v1 = 0.0;
            turbo.vmx1 = 100.0 - 100.0 * turbo.et5ref;
            turbo.vmn1 = turbo.vmx1 - 20.0;
        }
        turbo.inputPanel.turbinePanel.turbineLeftPanel.getF1().setText(String.valueOf(v1));
        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
        turbo.inputPanel.turbinePanel.turbineRightPanel.s1.setValue(i1);
        // materials
        turbo.inputPanel.turbinePanel.turbineRightPanel.tmat.select(turbo.mturbin);
        turbo.inputPanel.turbinePanel.turbineLeftPanel.getDt().setText(String.format("%.0f", turbo.dturbin));
        turbo.inputPanel.turbinePanel.turbineLeftPanel.getTt().setText(String.format("%.0f", turbo.tturbin));
        //  turbine nozzle
        v1 = turbo.tt7d;
        turbo.vmn1 = turbo.t7min;
        turbo.vmx1 = turbo.t7max;
        v2 = turbo.eta[7];
        turbo.vmn2 = turbo.etmin;
        turbo.vmx2 = turbo.etmax;
        v3 = turbo.a8rat;
        turbo.vmn3 = turbo.a8min;
        turbo.vmx3 = turbo.a8max;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            turbo.vmn1 = -10.0;
            turbo.vmx1 = 10.0;
            v2 = 0.0;
            turbo.vmx2 = 100.0 - 100.0 * turbo.et7ref;
            turbo.vmn2 = turbo.vmx2 - 20.0;
            v3 = 0.0;
            turbo.vmn3 = -10.0;
            turbo.vmx3 = 10.0;
        }

        turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF1().setText(String.valueOf(Math.round(v1)));
        turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF2().setText(String.format("%.3f", v2));
        turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF3().setText(String.format("%.3f", v3));

        i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

        turbo.inputPanel.nozzlePanel.nozzleRightPanel.s1.setValue(i1);
        turbo.inputPanel.nozzlePanel.nozzleRightPanel.s2.setValue(i2);
        turbo.inputPanel.nozzlePanel.nozzleRightPanel.s3.setValue(i3);
        turbo.inputPanel.nozzlePanel.nozzleRightPanel.arch.select(turbo.arsched);
        // materials
        turbo.inputPanel.nozzlePanel.nozzleRightPanel.nmat.select(turbo.mnozl);
        turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getDn().setText(String.format("%.0f", turbo.dnozl));
        turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getTn().setText(String.format("%.0f", turbo.tnozl));
        //  ramjet nozzle
        v2 = turbo.eta[7];
        turbo.vmn2 = turbo.etmin;
        turbo.vmx2 = turbo.etmax;
        v3 = turbo.arthd;
        turbo.vmn3 = turbo.arthmn;
        turbo.vmx3 = turbo.arthmx;
        v4 = turbo.arexitd;
        turbo.vmn4 = turbo.arexmn;
        turbo.vmx4 = turbo.arexmx;

        if(turbo.lunits == 2) {
            v2 = 0.0;
            turbo.vmx2 = 100.0 - 100.0 * turbo.et7ref;
            turbo.vmn2 = turbo.vmx2 - 20.0;
            v3 = 0.0;
            turbo.vmn3 = -10.0;
            turbo.vmx3 = 10.0;
            v4 = 0.0;
            turbo.vmn4 = -10.0;
            turbo.vmx4 = 10.0;
        }

        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getF2().setText(String.format("%.3f", v2));
        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getF3().setText(String.format("%.3f", v3));
        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getF4().setText(String.format("%.3f", v4));

        i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);
        i4 = (int)(((v4 - turbo.vmn4) / (turbo.vmx4 - turbo.vmn4)) * 1000.);

        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.s2.setValue(i2);
        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.s3.setValue(i3);
        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.s4.setValue(i4);
        // materials
        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.nrmat.select(turbo.mnozr);
        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getDn().setText(String.format("%.0f", turbo.dnozr));
        turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getTn().setText(String.format("%.0f", turbo.tnozr));

        //  variable limits
        v1 = turbo.u0max;
        v2 = turbo.altmax;
        v3 = turbo.a2min;
        v4 = turbo.a2max;

        turbo.inputPanel.limitsPanel.f1.setText(String.valueOf((float)Math.round(v1)));
        turbo.inputPanel.limitsPanel.f2.setText(String.valueOf((float)Math.round(v2)));
        //  output only to .00001
        turbo.inputPanel.limitsPanel.f3.setText(String.format("%.5f", v3));
        turbo.inputPanel.limitsPanel.f4.setText(String.valueOf((float)Math.round(v4)));

        v1 = turbo.cprmax;
        v2 = turbo.t4max;
        v3 = turbo.t7max;

        turbo.inputPanel.limitsPanel.f5.setText(String.valueOf(v1));
        turbo.inputPanel.limitsPanel.f6.setText(String.valueOf(Math.round(v2)));
        turbo.inputPanel.limitsPanel.f7.setText(String.valueOf(Math.round(v3)));

        v1 = turbo.fprmax;
        v2 = turbo.bypmax;
        v3 = turbo.pt4max;


        turbo.inputPanel.limitsPanel.f9.setText(String.valueOf(v1));
        turbo.inputPanel.limitsPanel.f10.setText(String.valueOf(v2));
        turbo.inputPanel.limitsPanel.f11.setText(String.valueOf(v3));

        return;
    }

    public void setPlot() {   // PlotPanel Scales

        turbo.showcom = 1;

        switch (turbo.plttyp) {
            case 3: {                              // press variation
                turbo.nabs = turbo.nord = 1;
                turbo.ordkeep = turbo.abskeep = 1;
                turbo.lines = 1;
                turbo.npt = 9;
                turbo.laby = String.valueOf("Press");
                turbo.begy = 0.0;
                if(turbo.lunits == 0) {
                    turbo.labyu = String.valueOf("psi");
                    turbo.endy = 1000.;
                }
                if(turbo.lunits == 1) {
                    turbo.labyu = String.valueOf("kPa");
                    turbo.endy = 5000.;
                }
                turbo.ntiky = 11;
                turbo.labx = String.valueOf("Station");
                turbo.labxu = String.valueOf(" ");
                turbo.begx = 0.0;
                turbo.endx = 8.0;
                turbo.ntikx = 9;
                break;
            }
            case 4: {                              // temp variation
                turbo.nabs = turbo.nord = 1;
                turbo.ordkeep = turbo.abskeep = 1;
                turbo.lines = 1;
                turbo.npt = 9;
                turbo.laby = String.valueOf("Temp");
                if(turbo.lunits == 0) {
                    turbo.labyu = String.valueOf("R");
                }
                if(turbo.lunits == 1) {
                    turbo.labyu = String.valueOf("K");
                }
                if(turbo.lunits == 2) {
                    turbo.labyu = String.valueOf("%");
                }
                turbo.begy = 0.0;
                turbo.endy = 5000.;
                turbo.ntiky = 11;
                turbo.labx = String.valueOf("Station");
                turbo.labxu = String.valueOf(" ");
                turbo.begx = 0.0;
                turbo.endx = 8.0;
                turbo.ntikx = 9;
                break;
            }
            case 5: {                              //  T - s plotPanel
                turbo.nabs = turbo.nord = 2;
                turbo.ordkeep = turbo.abskeep = 1;
                turbo.lines = 1;
                turbo.npt = 7;
                turbo.laby = String.valueOf("Temp");
                if(turbo.lunits == 0) {
                    turbo.labyu = String.valueOf("R");
                }
                if(turbo.lunits == 1) {
                    turbo.labyu = String.valueOf("K");
                }
                turbo.begy = 0.0;
                turbo.endy = 5000.;
                turbo.ntiky = 11;
                turbo.labx = String.valueOf("s");
                if(turbo.lunits == 0) {
                    turbo.labxu = String.valueOf("Btu/lbm R");
                }
                if(turbo.lunits == 1) {
                    turbo.labxu = String.valueOf("kJ/kg K");
                }
                turbo.begx = 0.0;
                turbo.endx = 1.0 * turbo.bconv;
                turbo.ntikx = 2;
                break;
            }
            case 6: {                              //  p - v plotPanel
                turbo.nord = turbo.nabs = 3;
                turbo.ordkeep = turbo.abskeep = 2;
                turbo.lines = 1;
                turbo.npt = 25;
                turbo.laby = String.valueOf("Press");
                turbo.begy = 0.0;
                if(turbo.lunits == 0) {
                    turbo.labyu = String.valueOf("psi");
                    turbo.endy = 1000.;
                }
                if(turbo.lunits == 1) {
                    turbo.labyu = String.valueOf("kPa");
                    turbo.endy = 5000.;
                }
                turbo.ntiky = 11;
                turbo.labx = String.valueOf("v");
                if(turbo.lunits == 0) {
                    turbo.labxu = String.valueOf("ft^3/lb");
                }
                if(turbo.lunits == 1) {
                    turbo.labxu = String.valueOf("m^3/Kg");
                }
                turbo.begx = 0.0;
                turbo.endx = 100.0 * turbo.dconv;
                turbo.ntikx = 2;
                break;
            }
            case 7: {                              //  generate plotPanel
                turbo.nord = turbo.nabs = 3;
                turbo.ordkeep = turbo.abskeep = 3;
                turbo.lines = 0;
                turbo.npt = 0;
                turbo.laby = String.valueOf("Fn");
                if(turbo.lunits == 0) {
                    turbo.labyu = String.valueOf("lb");
                }
                if(turbo.lunits == 1) {
                    turbo.labyu = String.valueOf("N");
                }
                turbo.begy = 0.0;
                turbo.endy = 100000.;
                turbo.ntiky = 11;
                turbo.labx = String.valueOf("Mach");
                turbo.labxu = String.valueOf(" ");
                if(turbo.entype <= 2) {
                    turbo.begx = 0.0;
                    turbo.endx = 2.0;
                }
                if(turbo.entype == 3) {
                    turbo.begx = 0.0;
                    turbo.endx = 6.0;
                }
                turbo.ntikx = 5;
                break;
            }
        }
    }

    public void setUnits() {   // Switching Units
        double alts;
        double alm1s;
        double ars;
        double arm1s;
        double arm2s;
        double t4s;
        double t7s;
        double t4m1s;
        double t4m2s;
        double t7m1s;
        double t7m2s;
        double u0s;
        double pmxs;
        double tmns;
        double tmxs;
        double diars;
        double dim1s;
        double dim2s;
        double u0mts;
        double u0mrs;
        double altmts;
        double altmrs;
        double fhvs;

        alts = turbo.altd / turbo.lconv1;
        alm1s = turbo.altmin / turbo.lconv1;
        altmts = turbo.altmt / turbo.lconv1;
        altmrs = turbo.altmr / turbo.lconv1;
        ars = turbo.a2d / turbo.aconv;
        arm1s = turbo.a2min / turbo.aconv;
        arm2s = turbo.a2max / turbo.aconv;
        diars = turbo.diameng / turbo.lconv1;
        dim1s = turbo.diamin / turbo.lconv1;
        dim2s = turbo.diamax / turbo.lconv1;
        u0s = turbo.u0d / turbo.lconv2;
        u0mts = turbo.u0mt / turbo.lconv2;
        u0mrs = turbo.u0mr / turbo.lconv2;
        pmxs = turbo.pmax / turbo.pconv;
        tmns = turbo.tmin / turbo.tconv;
        tmxs = turbo.tmax / turbo.tconv;
        t4s = turbo.tt4d / turbo.tconv;
        t4m1s = turbo.t4min / turbo.tconv;
        t4m2s = turbo.t4max / turbo.tconv;
        t7s = turbo.tt7d / turbo.tconv;
        t7m1s = turbo.t7min / turbo.tconv;
        t7m2s = turbo.t7max / turbo.tconv;
        fhvs = turbo.fhvd / turbo.flconv;
        switch (turbo.lunits) {
            case 0: {                   /* English Units */
                turbo.lconv1 = 1.0;
                turbo.lconv2 = 1.0;
                turbo.fconv = 1.0;
                turbo.econv = 1.0;
                turbo.mconv1 = 1.0;
                turbo.pconv = 1.0;
                turbo.tconv = 1.0;
                turbo.mconv2 = 1.0;
                turbo.econv2 = 1.0;
                turbo.bconv = turbo.econv / turbo.tconv / turbo.mconv1;
                turbo.tref = 459.7;
                turbo.outputPanel.outputVariablesPanel.lpa.setText("Pres-psi");
                turbo.outputPanel.outputVariablesPanel.lpb.setText("Pres-psi");
                turbo.outputPanel.outputVariablesPanel.lta.setText("Temp-R");
                turbo.outputPanel.outputVariablesPanel.ltb.setText("Temp-R");
                turbo.inputPanel.flightPanel.flightRightPanel.l2.setText("lb/sq inputPanel");
                turbo.inputPanel.flightPanel.flightRightPanel.l3.setText("F");
                turbo.inputPanel.flightPanel.flightLeftPanel.l1.setText("Speed-mph");
                turbo.inputPanel.flightPanel.flightLeftPanel.l2.setText("Altitude-ft");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l2.setText("Weight-lbs");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l1.setText("Area-sq ft");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l3.setText("Diameter-ft");
                turbo.inputPanel.burnerPanel.burnerLeftPanel.l1.setText("Tmax -R");
                turbo.inputPanel.burnerPanel.burnerLeftPanel.l4.setText("FHV BTU/lb");
                turbo.inputPanel.nozzlePanel.nozzleLeftPanel.l1.setText("Tmax -R");
                turbo.inputPanel.inletPanel.inletRightPanel.lmat.setText("lbm/ft^3");
                turbo.inputPanel.fanPanel.rightPanel.lmat.setText("lbm/ft^3");
                turbo.inputPanel.compressorPanel.compressorRightPanel.lmat.setText("lbm/ft^3");
                turbo.inputPanel.burnerPanel.burnerRightPanel.lmat.setText("lbm/ft^3");
                turbo.inputPanel.turbinePanel.turbineRightPanel.lmat.setText("lbm/ft^3");
                turbo.inputPanel.nozzlePanel.nozzleRightPanel.lmat.setText("lbm/ft^3");
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.lmat.setText("lbm/ft^3");
                turbo.inputPanel.inletPanel.inletLeftPanel.lmat.setText("T lim -R");
                turbo.inputPanel.fanPanel.leftPanel.lmat.setText("T lim -R");
                turbo.inputPanel.compressorPanel.compressorLeftPanel.lmat.setText("T lim -R");
                turbo.inputPanel.burnerPanel.burnerLeftPanel.lmat.setText("T lim -R");
                turbo.inputPanel.turbinePanel.turbineLeftPanel.lmat.setText("T lim -R");
                turbo.inputPanel.nozzlePanel.nozzleLeftPanel.lmat.setText("T lim -R");
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.lmat.setText("T lim -R");
                turbo.g0d = 32.2;
                //                 setref.setVisible(false);
                break;
            }
            case 1: {                   /* Metric Units */
                turbo.lconv1 = .3048;
                turbo.lconv2 = 1.609;
                turbo.fconv = 4.448;
                turbo.econv = 1055.;
                turbo.econv2 = 1.055;
                turbo.mconv1 = .4536;
                turbo.pconv = 6.891;
                turbo.tconv = 0.555555;
                turbo.bconv = turbo.econv / turbo.tconv / turbo.mconv1 / 1000.;
                turbo.mconv2 = 14.59;
                turbo.tref = 273.1;
                turbo.outputPanel.outputVariablesPanel.lpa.setText("Pres-kPa");
                turbo.outputPanel.outputVariablesPanel.lpb.setText("Pres-kPa");
                turbo.outputPanel.outputVariablesPanel.lta.setText("Temp-K");
                turbo.outputPanel.outputVariablesPanel.ltb.setText("Temp-K");
                turbo.inputPanel.flightPanel.flightRightPanel.l2.setText("k Pa");
                turbo.inputPanel.flightPanel.flightRightPanel.l3.setText("C");
                turbo.inputPanel.flightPanel.flightLeftPanel.l1.setText("Speed-kmh");
                turbo.inputPanel.flightPanel.flightLeftPanel.l2.setText("Altitude-m");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l2.setText("Weight-N");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l1.setText("Area-sq m");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l3.setText("Diameter-m");
                turbo.inputPanel.burnerPanel.burnerLeftPanel.l1.setText("Tmax -K");
                turbo.inputPanel.burnerPanel.burnerLeftPanel.l4.setText("FHV kJ/kg");
                turbo.inputPanel.nozzlePanel.nozzleLeftPanel.l1.setText("Tmax -K");
                turbo.inputPanel.inletPanel.inletRightPanel.lmat.setText("kg/m^3");
                turbo.inputPanel.fanPanel.rightPanel.lmat.setText("kg/m^3");
                turbo.inputPanel.compressorPanel.compressorRightPanel.lmat.setText("kg/m^3");
                turbo.inputPanel.burnerPanel.burnerRightPanel.lmat.setText("kg/m^3");
                turbo.inputPanel.turbinePanel.turbineRightPanel.lmat.setText("kg/m^3");
                turbo.inputPanel.nozzlePanel.nozzleRightPanel.lmat.setText("kg/m^3");
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.lmat.setText("kg/m^3");
                turbo.inputPanel.inletPanel.inletLeftPanel.lmat.setText("T lim -K");
                turbo.inputPanel.fanPanel.leftPanel.lmat.setText("T lim -K");
                turbo.inputPanel.compressorPanel.compressorLeftPanel.lmat.setText("T lim -K");
                turbo.inputPanel.burnerPanel.burnerLeftPanel.lmat.setText("T lim -K");
                turbo.inputPanel.turbinePanel.turbineLeftPanel.lmat.setText("T lim -K");
                turbo.inputPanel.nozzlePanel.nozzleLeftPanel.lmat.setText("T lim -K");
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.lmat.setText("T lim -K");
                turbo.g0d = 9.81;
                //                 setref.setVisible(false);
                break;
            }
            case 2: {            /* Percent Change .. convert to English */
                turbo.lconv1 = 1.0;
                turbo.lconv2 = 1.0;
                turbo.fconv = 1.0;
                turbo.econv = 1.0;
                turbo.mconv1 = 1.0;
                turbo.pconv = 1.0;
                turbo.tconv = 1.0;
                turbo.mconv2 = 1.0;
                turbo.tref = 459.7;
                turbo.inputPanel.flightPanel.flightRightPanel.l2.setText("lb/sq inputPanel");
                turbo.inputPanel.flightPanel.flightRightPanel.l3.setText("F");
                turbo.inputPanel.flightPanel.flightLeftPanel.l1.setText("Speed-%");
                turbo.inputPanel.flightPanel.flightLeftPanel.l2.setText("Altitude-%");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l2.setText("Weight-lbs");
                turbo.inputPanel.sizePanel.sizeLeftPanel.l1.setText("Area-%");
                turbo.inputPanel.burnerPanel.burnerLeftPanel.l1.setText("Tmax -%");
                turbo.inputPanel.nozzlePanel.nozzleLeftPanel.l1.setText("Tmax -%");
                turbo.inputPanel.inletPanel.inletRightPanel.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.inputPanel.fanPanel.rightPanel.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.inputPanel.compressorPanel.compressorRightPanel.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.inputPanel.burnerPanel.burnerRightPanel.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.inputPanel.turbinePanel.turbineRightPanel.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.inputPanel.nozzlePanel.nozzleRightPanel.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.g0d = 32.2;
                //                 setref.setVisible(true);
                turbo.pt2flag = 1;
                turbo.inputPanel.inletPanel.inletRightPanel.inltch.select(turbo.pt2flag);
                turbo.arsched = 1;
                turbo.inputPanel.nozzlePanel.nozzleRightPanel.arch.select(turbo.arsched);
                turbo.athsched = 1;
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.atch.select(turbo.athsched);
                turbo.aexsched = 1;
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.aech.select(turbo.aexsched);
                break;
            }
        }
        turbo.aconv = turbo.lconv1 * turbo.lconv1;
        turbo.dconv = turbo.mconv1 / turbo.aconv / turbo.lconv1;
        turbo.flconv = turbo.econv2 / turbo.mconv1;

        turbo.altd = alts * turbo.lconv1;
        turbo.altmin = alm1s * turbo.lconv1;
        turbo.altmt = altmts * turbo.lconv1;
        turbo.altmr = altmrs * turbo.lconv1;
        turbo.a2d = ars * turbo.aconv;
        turbo.a2min = arm1s * turbo.aconv;
        turbo.a2max = arm2s * turbo.aconv;
        turbo.diameng = diars * turbo.lconv1;
        turbo.diamin = dim1s * turbo.lconv1;
        turbo.diamax = dim2s * turbo.lconv1;
        turbo.u0d = u0s * turbo.lconv2;
        turbo.u0mt = u0mts * turbo.lconv2;
        turbo.u0mr = u0mrs * turbo.lconv2;
        turbo.u0max = turbo.u0mt;
        turbo.altmax = turbo.altmt;
        if(turbo.entype == 3) {
            turbo.u0max = turbo.u0mr;
            turbo.altmax = turbo.altmr;
        }

        turbo.pmax = pmxs * turbo.pconv;
        turbo.tmax = tmxs * turbo.tconv;
        turbo.tmin = tmns * turbo.tconv;
        turbo.tt4d = t4s * turbo.tconv;
        turbo.t4min = t4m1s * turbo.tconv;
        turbo.t4max = t4m2s * turbo.tconv;
        turbo.tt7d = t7s * turbo.tconv;
        turbo.t7min = t7m1s * turbo.tconv;
        turbo.t7max = t7m2s * turbo.tconv;
        turbo.fhvd = fhvs * turbo.flconv;

        if(turbo.lunits == 2) {     // initialization of reference variables
            if(turbo.u0d <= 10.0) {
                turbo.u0d = 10.0;
            }
            turbo.u0ref = turbo.u0d;
            if(turbo.altd <= 10.0) {
                turbo.altd = 10.0;
            }
            turbo.altref = turbo.altd;
            turbo.thrref = turbo.throtl;
            turbo.a2ref = turbo.a2d;
            turbo.et2ref = turbo.eta[2];
            turbo.fpref = turbo.p3fp2d;
            turbo.et13ref = turbo.eta[13];
            turbo.bpref = turbo.byprat;
            turbo.cpref = turbo.p3p2d;
            turbo.et3ref = turbo.eta[3];
            turbo.et4ref = turbo.eta[4];
            turbo.et5ref = turbo.eta[5];
            turbo.t4ref = turbo.tt4d;
            turbo.p4ref = turbo.prat[4];
            turbo.t7ref = turbo.tt7d;
            turbo.et7ref = turbo.eta[7];
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
        }
        //  Ouput panel
        turbo.outputPanel.outputMainPanel.loadOut();
        turbo.outputPanel.outputVariablesPanel.loadOut();
        turbo.inputPanel.fillBox();

        return;
    }
}  // end Control Panel
 
