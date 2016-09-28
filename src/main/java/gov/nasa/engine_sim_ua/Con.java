package gov.nasa.engine_sim_ua;

import java.awt.GridLayout;
import java.awt.Panel;

/**
 * @author Geoff M. Granum
 */
class Con extends Panel {

    private final Turbo turbo;
    Up up;
    Down down;

    Con(Turbo turbo) {
        this.turbo = turbo;

        setLayout(new GridLayout(2, 1, 5, 5));

        up = new Up(this, turbo);
        down = new Down();

        add(up);
        add(down);
    }

    public void setPanl() {
        double v1, v2, v3, v4;
        float fl1, fl2, fl3, fl4;
        int i1, i2, i3, i4;

        // set limits and labels
        // flight conditions
        v1 = 0.0;
        Turbo.vmn1 = -10.0;
        Turbo.vmx1 = 10.0;
        v2 = 0.0;
        Turbo.vmn2 = -10.0;
        Turbo.vmx2 = 10.0;
        v3 = 0.0;
        Turbo.vmn3 = -10.0;
        Turbo.vmx3 = 10.0;
        v4 = Turbo.gama;

        if(turbo.lunits <= 1) {
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

        turbo.in.flight.left.f1.setText(String.valueOf(turbo.filter0(v1)));
        turbo.in.flight.left.f2.setText(String.valueOf(turbo.filter0(v2)));
        turbo.in.flight.left.f3.setText(String.valueOf(turbo.filter3(v3)));
        turbo.in.flight.left.f4.setText(String.valueOf(turbo.filter3(v4)));

        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

        turbo.in.flight.right.s1.setValue(i1);
        turbo.in.flight.right.s2.setValue(i2);
        turbo.in.flight.right.s3.setValue(i3);

        turbo.in.flight.right.inptch.select(turbo.inptype);
        turbo.in.flight.right.nozch.select(turbo.abflag);
        turbo.in.flight.left.inpch.select(turbo.gamopt);

        // size
        v1 = 0.0;
        Turbo.vmn1 = -10.0;
        Turbo.vmx1 = 10.0;
        Turbo.vmn3 = -10.0;
        Turbo.vmx3 = 10.0;
        if(turbo.lunits <= 1) {
            v1 = Turbo.a2d;
            Turbo.vmn1 = Turbo.a2min;
            Turbo.vmx1 = Turbo.a2max;
            v3 = Turbo.diameng;
        }
        fl1 = turbo.filter3(v1);
        fl3 = turbo.filter3(v3);
        turbo.in.size.left.f1.setText(String.valueOf(fl1));
        turbo.in.size.left.f3.setText(String.valueOf(fl3));

        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);

        turbo.in.size.right.sizch.select(turbo.siztype);
        turbo.in.size.right.s1.setValue(i1);

        turbo.in.size.left.f2.setText(String.valueOf(turbo.filter0(Turbo.weight)));
        turbo.in.size.right.chmat.select(turbo.wtflag);

        // inlet
        if(turbo.pt2flag == 0) {             /*     mil spec      */
            if(Turbo.fsmach > 1.0) {          /* supersonic */
                Turbo.eta[2] = 1.0 - .075 * Math.pow(Turbo.fsmach - 1.0, 1.35);
            } else {
                Turbo.eta[2] = 1.0;
            }
        }

        v1 = Turbo.eta[2];
        Turbo.vmn1 = Turbo.etmin;
        Turbo.vmx1 = Turbo.etmax;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            Turbo.vmx1 = 100.0 - 100.0 * Turbo.et2ref;
            Turbo.vmn1 = Turbo.vmx1 - 20.0;
        }
        fl1 = turbo.filter3(v1);
        turbo.in.inlet.left.f1.setText(String.valueOf(fl1));
        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
        turbo.in.inlet.right.s1.setValue(i1);
        // materials
        turbo.in.inlet.right.imat.select(Turbo.minlt);
        turbo.in.inlet.left.di.setText(String.valueOf(turbo.filter0(Turbo.dinlt)));
        turbo.in.inlet.left.ti.setText(String.valueOf(turbo.filter0(Turbo.tinlt)));
        //  fan
        v1 = Turbo.p3fp2d;
        Turbo.vmn1 = Turbo.fprmin;
        Turbo.vmx1 = Turbo.fprmax;
        v2 = Turbo.eta[13];
        Turbo.vmn2 = Turbo.etmin;
        Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.byprat;
        Turbo.vmn3 = Turbo.bypmin;
        Turbo.vmx3 = Turbo.bypmax;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0;
            v2 = 0.0;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et13ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0;
            v3 = 0.0;
            Turbo.vmn3 = -10.0;
            Turbo.vmx3 = 10.0;
        }
        fl1 = (float)v1;
        fl2 = (float)v2;
        fl3 = (float)v3;

        turbo.in.fan.left.f1.setText(String.valueOf(fl1));
        turbo.in.fan.left.f2.setText(String.valueOf(fl2));
        turbo.in.fan.left.f3.setText(String.valueOf(fl3));

        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

        turbo.in.fan.right.s1.setValue(i1);
        turbo.in.fan.right.s2.setValue(i2);
        turbo.in.fan.right.s3.setValue(i3);

        // materials
        turbo.in.fan.right.fmat.select(Turbo.mfan);
        turbo.in.fan.left.df.setText(String.valueOf(turbo.filter0(Turbo.dfan)));
        turbo.in.fan.left.tf.setText(String.valueOf(turbo.filter0(Turbo.tfan)));
        // compressor
        v1 = Turbo.p3p2d;
        Turbo.vmn1 = Turbo.cprmin;
        Turbo.vmx1 = Turbo.cprmax;
        v2 = Turbo.eta[3];
        Turbo.vmn2 = Turbo.etmin;
        Turbo.vmx2 = Turbo.etmax;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0;
            v2 = 0.0;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et3ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0;
        }
        fl1 = (float)v1;
        fl2 = (float)v2;

        turbo.in.comp.left.f1.setText(String.valueOf(fl1));
        turbo.in.comp.left.f2.setText(String.valueOf(fl2));

        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);

        turbo.in.comp.right.s1.setValue(i1);
        turbo.in.comp.right.s2.setValue(i2);
        // materials
        turbo.in.comp.right.cmat.select(Turbo.mcomp);
        turbo.in.comp.left.dc.setText(String.valueOf(turbo.filter0(Turbo.dcomp)));
        turbo.in.comp.left.tc.setText(String.valueOf(turbo.filter0(Turbo.tcomp)));
        //  burner
        v1 = Turbo.tt4d;
        Turbo.vmn1 = Turbo.t4min;
        Turbo.vmx1 = Turbo.t4max;
        v2 = Turbo.eta[4];
        Turbo.vmn2 = Turbo.etmin;
        Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.prat[4];
        Turbo.vmn3 = Turbo.etmin;
        Turbo.vmx3 = Turbo.pt4max;
        v4 = Turbo.fhvd;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0;
            v2 = 0.0;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et4ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0;
            v3 = 0.0;
            Turbo.vmx3 = 100.0 - 100.0 * Turbo.p4ref;
            Turbo.vmn3 = Turbo.vmx3 - 20.0;
        }
        fl1 = (float)v1;
        fl2 = (float)v2;
        fl3 = (float)v3;

        turbo.in.burn.left.f1.setText(String.valueOf(turbo.filter0(v1)));
        turbo.in.burn.left.f2.setText(String.valueOf(fl2));
        turbo.in.burn.left.f3.setText(String.valueOf(fl3));
        turbo.in.burn.left.f4.setText(String.valueOf(turbo.filter0(v4)));

        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

        turbo.in.burn.right.s1.setValue(i1);
        turbo.in.burn.right.s2.setValue(i2);
        turbo.in.burn.right.s3.setValue(i3);
        turbo.in.burn.right.fuelch.select(turbo.fueltype);
        // materials
        turbo.in.burn.right.bmat.select(Turbo.mburner);
        turbo.in.burn.left.db.setText(String.valueOf(turbo.filter0(Turbo.dburner)));
        turbo.in.burn.left.tb.setText(String.valueOf(turbo.filter0(Turbo.tburner)));
        //  turbine
        v1 = Turbo.eta[5];
        Turbo.vmn1 = Turbo.etmin;
        Turbo.vmx1 = Turbo.etmax;
        if(turbo.lunits == 2) {
            v1 = 0.0;
            Turbo.vmx1 = 100.0 - 100.0 * Turbo.et5ref;
            Turbo.vmn1 = Turbo.vmx1 - 20.0;
        }
        fl1 = (float)v1;
        turbo.in.turb.left.f1.setText(String.valueOf(fl1));
        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
        turbo.in.turb.right.s1.setValue(i1);
        // materials
        turbo.in.turb.right.tmat.select(Turbo.mturbin);
        turbo.in.turb.left.dt.setText(String.valueOf(turbo.filter0(Turbo.dturbin)));
        turbo.in.turb.left.tt.setText(String.valueOf(turbo.filter0(Turbo.tturbin)));
        //  turbine nozzle
        v1 = Turbo.tt7d;
        Turbo.vmn1 = Turbo.t7min;
        Turbo.vmx1 = Turbo.t7max;
        v2 = Turbo.eta[7];
        Turbo.vmn2 = Turbo.etmin;
        Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.a8rat;
        Turbo.vmn3 = Turbo.a8min;
        Turbo.vmx3 = Turbo.a8max;

        if(turbo.lunits == 2) {
            v1 = 0.0;
            Turbo.vmn1 = -10.0;
            Turbo.vmx1 = 10.0;
            v2 = 0.0;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0;
            v3 = 0.0;
            Turbo.vmn3 = -10.0;
            Turbo.vmx3 = 10.0;
        }
        fl1 = turbo.filter0(v1);
        fl2 = turbo.filter3(v2);
        fl3 = turbo.filter3(v3);

        turbo.in.nozl.left.f1.setText(String.valueOf(fl1));
        turbo.in.nozl.left.f2.setText(String.valueOf(fl2));
        turbo.in.nozl.left.f3.setText(String.valueOf(fl3));

        i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
        i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);

        turbo.in.nozl.right.s1.setValue(i1);
        turbo.in.nozl.right.s2.setValue(i2);
        turbo.in.nozl.right.s3.setValue(i3);
        turbo.in.nozl.right.arch.select(turbo.arsched);
        // materials
        turbo.in.nozl.right.nmat.select(Turbo.mnozl);
        turbo.in.nozl.left.dn.setText(String.valueOf(turbo.filter0(Turbo.dnozl)));
        turbo.in.nozl.left.tn.setText(String.valueOf(turbo.filter0(Turbo.tnozl)));
        //  ramjet nozzle
        v2 = Turbo.eta[7];
        Turbo.vmn2 = Turbo.etmin;
        Turbo.vmx2 = Turbo.etmax;
        v3 = Turbo.arthd;
        Turbo.vmn3 = Turbo.arthmn;
        Turbo.vmx3 = Turbo.arthmx;
        v4 = Turbo.arexitd;
        Turbo.vmn4 = Turbo.arexmn;
        Turbo.vmx4 = Turbo.arexmx;

        if(turbo.lunits == 2) {
            v2 = 0.0;
            Turbo.vmx2 = 100.0 - 100.0 * Turbo.et7ref;
            Turbo.vmn2 = Turbo.vmx2 - 20.0;
            v3 = 0.0;
            Turbo.vmn3 = -10.0;
            Turbo.vmx3 = 10.0;
            v4 = 0.0;
            Turbo.vmn4 = -10.0;
            Turbo.vmx4 = 10.0;
        }
        fl2 = turbo.filter3(v2);
        fl3 = turbo.filter3(v3);
        fl4 = turbo.filter3(v4);

        turbo.in.nozr.left.f2.setText(String.valueOf(fl2));
        turbo.in.nozr.left.f3.setText(String.valueOf(fl3));
        turbo.in.nozr.left.f4.setText(String.valueOf(fl4));

        i2 = (int)(((v2 - Turbo.vmn2) / (Turbo.vmx2 - Turbo.vmn2)) * 1000.);
        i3 = (int)(((v3 - Turbo.vmn3) / (Turbo.vmx3 - Turbo.vmn3)) * 1000.);
        i4 = (int)(((v4 - Turbo.vmn4) / (Turbo.vmx4 - Turbo.vmn4)) * 1000.);

        turbo.in.nozr.right.s2.setValue(i2);
        turbo.in.nozr.right.s3.setValue(i3);
        turbo.in.nozr.right.s4.setValue(i4);
        // materials
        turbo.in.nozr.right.nrmat.select(Turbo.mnozr);
        turbo.in.nozr.left.dn.setText(String.valueOf(turbo.filter0(Turbo.dnozr)));
        turbo.in.nozr.left.tn.setText(String.valueOf(turbo.filter0(Turbo.tnozr)));

        //  variable limits
        v1 = Turbo.u0max;
        v2 = Turbo.altmax;
        v3 = Turbo.a2min;
        v4 = Turbo.a2max;

        fl1 = turbo.filter0(v1);
        fl2 = turbo.filter0(v2);
        fl3 = turbo.filter5(v3);
        fl4 = turbo.filter0(v4);

        turbo.in.limt.f1.setText(String.valueOf(fl1));
        turbo.in.limt.f2.setText(String.valueOf(fl2));
        turbo.in.limt.f3.setText(String.valueOf(fl3));
        turbo.in.limt.f4.setText(String.valueOf(fl4));

        v1 = Turbo.cprmax;
        v2 = Turbo.t4max;
        v3 = Turbo.t7max;

        fl1 = (float)v1;
        fl2 = turbo.filter0(v2);
        fl3 = turbo.filter0(v3);

        turbo.in.limt.f5.setText(String.valueOf(fl1));
        turbo.in.limt.f6.setText(String.valueOf(fl2));
        turbo.in.limt.f7.setText(String.valueOf(fl3));

        v1 = Turbo.fprmax;
        v2 = Turbo.bypmax;
        v3 = Turbo.pt4max;

        fl1 = (float)v1;
        fl2 = (float)v2;
        fl3 = (float)v3;

        turbo.in.limt.f9.setText(String.valueOf(fl1));
        turbo.in.limt.f10.setText(String.valueOf(fl2));
        turbo.in.limt.f11.setText(String.valueOf(fl3));

        return;
    }

    public void setPlot() {   // Plot Scales

        turbo.showcom = 1;

        switch (turbo.plttyp) {
            case 3: {                              // press variation
                turbo.nabs = turbo.nord = 1;
                turbo.ordkeep = turbo.abskeep = 1;
                turbo.lines = 1;
                turbo.npt = 9;
                Turbo.laby = String.valueOf("Press");
                Turbo.begy = 0.0;
                if(turbo.lunits == 0) {
                    Turbo.labyu = String.valueOf("psi");
                    Turbo.endy = 1000.;
                }
                if(turbo.lunits == 1) {
                    Turbo.labyu = String.valueOf("kPa");
                    Turbo.endy = 5000.;
                }
                turbo.ntiky = 11;
                Turbo.labx = String.valueOf("Station");
                Turbo.labxu = String.valueOf(" ");
                Turbo.begx = 0.0;
                Turbo.endx = 8.0;
                turbo.ntikx = 9;
                break;
            }
            case 4: {                              // temp variation
                turbo.nabs = turbo.nord = 1;
                turbo.ordkeep = turbo.abskeep = 1;
                turbo.lines = 1;
                turbo.npt = 9;
                Turbo.laby = String.valueOf("Temp");
                if(turbo.lunits == 0) {
                    Turbo.labyu = String.valueOf("R");
                }
                if(turbo.lunits == 1) {
                    Turbo.labyu = String.valueOf("K");
                }
                if(turbo.lunits == 2) {
                    Turbo.labyu = String.valueOf("%");
                }
                Turbo.begy = 0.0;
                Turbo.endy = 5000.;
                turbo.ntiky = 11;
                Turbo.labx = String.valueOf("Station");
                Turbo.labxu = String.valueOf(" ");
                Turbo.begx = 0.0;
                Turbo.endx = 8.0;
                turbo.ntikx = 9;
                break;
            }
            case 5: {                              //  T - s plot
                turbo.nabs = turbo.nord = 2;
                turbo.ordkeep = turbo.abskeep = 1;
                turbo.lines = 1;
                turbo.npt = 7;
                Turbo.laby = String.valueOf("Temp");
                if(turbo.lunits == 0) {
                    Turbo.labyu = String.valueOf("R");
                }
                if(turbo.lunits == 1) {
                    Turbo.labyu = String.valueOf("K");
                }
                Turbo.begy = 0.0;
                Turbo.endy = 5000.;
                turbo.ntiky = 11;
                Turbo.labx = String.valueOf("s");
                if(turbo.lunits == 0) {
                    Turbo.labxu = String.valueOf("Btu/lbm R");
                }
                if(turbo.lunits == 1) {
                    Turbo.labxu = String.valueOf("kJ/kg K");
                }
                Turbo.begx = 0.0;
                Turbo.endx = 1.0 * Turbo.bconv;
                turbo.ntikx = 2;
                break;
            }
            case 6: {                              //  p - v plot
                turbo.nord = turbo.nabs = 3;
                turbo.ordkeep = turbo.abskeep = 2;
                turbo.lines = 1;
                turbo.npt = 25;
                Turbo.laby = String.valueOf("Press");
                Turbo.begy = 0.0;
                if(turbo.lunits == 0) {
                    Turbo.labyu = String.valueOf("psi");
                    Turbo.endy = 1000.;
                }
                if(turbo.lunits == 1) {
                    Turbo.labyu = String.valueOf("kPa");
                    Turbo.endy = 5000.;
                }
                turbo.ntiky = 11;
                Turbo.labx = String.valueOf("v");
                if(turbo.lunits == 0) {
                    Turbo.labxu = String.valueOf("ft^3/lb");
                }
                if(turbo.lunits == 1) {
                    Turbo.labxu = String.valueOf("m^3/Kg");
                }
                Turbo.begx = 0.0;
                Turbo.endx = 100.0 * Turbo.dconv;
                turbo.ntikx = 2;
                break;
            }
            case 7: {                              //  generate plot
                turbo.nord = turbo.nabs = 3;
                turbo.ordkeep = turbo.abskeep = 3;
                turbo.lines = 0;
                turbo.npt = 0;
                Turbo.laby = String.valueOf("Fn");
                if(turbo.lunits == 0) {
                    Turbo.labyu = String.valueOf("lb");
                }
                if(turbo.lunits == 1) {
                    Turbo.labyu = String.valueOf("N");
                }
                Turbo.begy = 0.0;
                Turbo.endy = 100000.;
                turbo.ntiky = 11;
                Turbo.labx = String.valueOf("Mach");
                Turbo.labxu = String.valueOf(" ");
                if(turbo.entype <= 2) {
                    Turbo.begx = 0.0;
                    Turbo.endx = 2.0;
                }
                if(turbo.entype == 3) {
                    Turbo.begx = 0.0;
                    Turbo.endx = 6.0;
                }
                turbo.ntikx = 5;
                break;
            }
        }
    }

    public void setUnits() {   // Switching Units
        double alts, alm1s, ars, arm1s, arm2s, t4s, t7s, t4m1s, t4m2s, t7m1s, t7m2s;
        double u0s, pmxs, tmns, tmxs, diars, dim1s, dim2s;
        double u0mts, u0mrs, altmts, altmrs, fhvs;
        int i1;

        alts = Turbo.altd / Turbo.lconv1;
        alm1s = Turbo.altmin / Turbo.lconv1;
        altmts = Turbo.altmt / Turbo.lconv1;
        altmrs = Turbo.altmr / Turbo.lconv1;
        ars = Turbo.a2d / Turbo.aconv;
        arm1s = Turbo.a2min / Turbo.aconv;
        arm2s = Turbo.a2max / Turbo.aconv;
        diars = Turbo.diameng / Turbo.lconv1;
        dim1s = Turbo.diamin / Turbo.lconv1;
        dim2s = Turbo.diamax / Turbo.lconv1;
        u0s = Turbo.u0d / Turbo.lconv2;
        u0mts = Turbo.u0mt / Turbo.lconv2;
        u0mrs = Turbo.u0mr / Turbo.lconv2;
        pmxs = Turbo.pmax / Turbo.pconv;
        tmns = Turbo.tmin / Turbo.tconv;
        tmxs = Turbo.tmax / Turbo.tconv;
        t4s = Turbo.tt4d / Turbo.tconv;
        t4m1s = Turbo.t4min / Turbo.tconv;
        t4m2s = Turbo.t4max / Turbo.tconv;
        t7s = Turbo.tt7d / Turbo.tconv;
        t7m1s = Turbo.t7min / Turbo.tconv;
        t7m2s = Turbo.t7max / Turbo.tconv;
        fhvs = Turbo.fhvd / Turbo.flconv;
        switch (turbo.lunits) {
            case 0: {                   /* English Units */
                Turbo.lconv1 = 1.0;
                Turbo.lconv2 = 1.0;
                Turbo.fconv = 1.0;
                Turbo.econv = 1.0;
                Turbo.mconv1 = 1.0;
                Turbo.pconv = 1.0;
                Turbo.tconv = 1.0;
                Turbo.mconv2 = 1.0;
                Turbo.econv2 = 1.0;
                Turbo.bconv = Turbo.econv / Turbo.tconv / Turbo.mconv1;
                Turbo.tref = 459.7;
                turbo.out.vars.lpa.setText("Pres-psi");
                turbo.out.vars.lpb.setText("Pres-psi");
                turbo.out.vars.lta.setText("Temp-R");
                turbo.out.vars.ltb.setText("Temp-R");
                turbo.in.flight.right.l2.setText("lb/sq in");
                turbo.in.flight.right.l3.setText("F");
                turbo.in.flight.left.l1.setText("Speed-mph");
                turbo.in.flight.left.l2.setText("Altitude-ft");
                turbo.in.size.left.l2.setText("Weight-lbs");
                turbo.in.size.left.l1.setText("Area-sq ft");
                turbo.in.size.left.l3.setText("Diameter-ft");
                turbo.in.burn.left.l1.setText("Tmax -R");
                turbo.in.burn.left.l4.setText("FHV BTU/lb");
                turbo.in.nozl.left.l1.setText("Tmax -R");
                turbo.in.inlet.right.lmat.setText("lbm/ft^3");
                turbo.in.fan.right.lmat.setText("lbm/ft^3");
                turbo.in.comp.right.lmat.setText("lbm/ft^3");
                turbo.in.burn.right.lmat.setText("lbm/ft^3");
                turbo.in.turb.right.lmat.setText("lbm/ft^3");
                turbo.in.nozl.right.lmat.setText("lbm/ft^3");
                turbo.in.nozr.right.lmat.setText("lbm/ft^3");
                turbo.in.inlet.left.lmat.setText("T lim -R");
                turbo.in.fan.left.lmat.setText("T lim -R");
                turbo.in.comp.left.lmat.setText("T lim -R");
                turbo.in.burn.left.lmat.setText("T lim -R");
                turbo.in.turb.left.lmat.setText("T lim -R");
                turbo.in.nozl.left.lmat.setText("T lim -R");
                turbo.in.nozr.left.lmat.setText("T lim -R");
                Turbo.g0d = 32.2;
                //                 setref.setVisible(false) ;
                break;
            }
            case 1: {                   /* Metric Units */
                Turbo.lconv1 = .3048;
                Turbo.lconv2 = 1.609;
                Turbo.fconv = 4.448;
                Turbo.econv = 1055.;
                Turbo.econv2 = 1.055;
                Turbo.mconv1 = .4536;
                Turbo.pconv = 6.891;
                Turbo.tconv = 0.555555;
                Turbo.bconv = Turbo.econv / Turbo.tconv / Turbo.mconv1 / 1000.;
                Turbo.mconv2 = 14.59;
                Turbo.tref = 273.1;
                turbo.out.vars.lpa.setText("Pres-kPa");
                turbo.out.vars.lpb.setText("Pres-kPa");
                turbo.out.vars.lta.setText("Temp-K");
                turbo.out.vars.ltb.setText("Temp-K");
                turbo.in.flight.right.l2.setText("k Pa");
                turbo.in.flight.right.l3.setText("C");
                turbo.in.flight.left.l1.setText("Speed-kmh");
                turbo.in.flight.left.l2.setText("Altitude-m");
                turbo.in.size.left.l2.setText("Weight-N");
                turbo.in.size.left.l1.setText("Area-sq m");
                turbo.in.size.left.l3.setText("Diameter-m");
                turbo.in.burn.left.l1.setText("Tmax -K");
                turbo.in.burn.left.l4.setText("FHV kJ/kg");
                turbo.in.nozl.left.l1.setText("Tmax -K");
                turbo.in.inlet.right.lmat.setText("kg/m^3");
                turbo.in.fan.right.lmat.setText("kg/m^3");
                turbo.in.comp.right.lmat.setText("kg/m^3");
                turbo.in.burn.right.lmat.setText("kg/m^3");
                turbo.in.turb.right.lmat.setText("kg/m^3");
                turbo.in.nozl.right.lmat.setText("kg/m^3");
                turbo.in.nozr.right.lmat.setText("kg/m^3");
                turbo.in.inlet.left.lmat.setText("T lim -K");
                turbo.in.fan.left.lmat.setText("T lim -K");
                turbo.in.comp.left.lmat.setText("T lim -K");
                turbo.in.burn.left.lmat.setText("T lim -K");
                turbo.in.turb.left.lmat.setText("T lim -K");
                turbo.in.nozl.left.lmat.setText("T lim -K");
                turbo.in.nozr.left.lmat.setText("T lim -K");
                Turbo.g0d = 9.81;
                //                 setref.setVisible(false) ;
                break;
            }
            case 2: {            /* Percent Change .. convert to English */
                Turbo.lconv1 = 1.0;
                Turbo.lconv2 = 1.0;
                Turbo.fconv = 1.0;
                Turbo.econv = 1.0;
                Turbo.mconv1 = 1.0;
                Turbo.pconv = 1.0;
                Turbo.tconv = 1.0;
                Turbo.mconv2 = 1.0;
                Turbo.tref = 459.7;
                turbo.in.flight.right.l2.setText("lb/sq in");
                turbo.in.flight.right.l3.setText("F");
                turbo.in.flight.left.l1.setText("Speed-%");
                turbo.in.flight.left.l2.setText("Altitude-%");
                turbo.in.size.left.l2.setText("Weight-lbs");
                turbo.in.size.left.l1.setText("Area-%");
                turbo.in.burn.left.l1.setText("Tmax -%");
                turbo.in.nozl.left.l1.setText("Tmax -%");
                turbo.in.inlet.right.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.in.fan.right.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.in.comp.right.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.in.burn.right.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.in.turb.right.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.in.nozl.right.lmat.setText("<-lbm/ft^3 -Rankine");
                turbo.in.nozr.right.lmat.setText("<-lbm/ft^3 -Rankine");
                Turbo.g0d = 32.2;
                //                 setref.setVisible(true) ;
                turbo.pt2flag = 1;
                turbo.in.inlet.right.inltch.select(turbo.pt2flag);
                turbo.arsched = 1;
                turbo.in.nozl.right.arch.select(turbo.arsched);
                turbo.athsched = 1;
                turbo.in.nozr.right.atch.select(turbo.athsched);
                turbo.aexsched = 1;
                turbo.in.nozr.right.aech.select(turbo.aexsched);
                break;
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
        if(turbo.entype == 3) {
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

        if(turbo.lunits == 2) {     // initialization of reference variables
            if(Turbo.u0d <= 10.0) {
                Turbo.u0d = 10.0;
            }
            Turbo.u0ref = Turbo.u0d;
            if(Turbo.altd <= 10.0) {
                Turbo.altd = 10.0;
            }
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
        }
        //  Ouput panel
        turbo.out.box.loadOut();
        turbo.out.vars.loadOut();
        turbo.in.fillBox();

        return;
    }
}  // end Control Panel
 
