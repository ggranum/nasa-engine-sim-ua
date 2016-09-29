package gov.nasa.engine_sim_ua;

import java.awt.Color;

/**
 *
 */
class Solver {

    private final Turbo turbo;

    Solver(Turbo turbo) {
        this.turbo = turbo;
    }

    public void comPute() {

        turbo.numeng = 1;
        Turbo.fireflag = 0;

        getFreeStream();

        getThermo();

        if(turbo.inflag == 0) {
            getGeo(); /* determine engine sizePanel and geometry */
        }
        if(turbo.inflag == 1) {
            if(turbo.entype < 3) {
                Turbo.a8 = Turbo.a8d * Math.sqrt(Turbo.trat[7]) / Turbo.prat[7];
            }
        }

        turbo.view.getDrawGeo();

        getPerform();

        turbo.outputPanel.outputMainPanel.loadOut();
        turbo.outputPanel.outputVariablesPanel.loadOut();
        turbo.inputPanel.fillBox();

        if(turbo.plttyp >= 3 && turbo.plttyp <= 7) {
            turbo.outputPanel.outputPlotCanvas.loadPlot();
            turbo.outputPanel.outputPlotCanvas.repaint();
        }

        turbo.view.repaint();

        if(turbo.inflag == 0) {
            myDesign();
        }
    }

    public void setDefaults() {
        int i;

        turbo.move = 0;
        turbo.inptype = 0;
        turbo.siztype = 0;
        turbo.lunits = 0;
        Turbo.lconv1 = 1.0;
        Turbo.lconv2 = 1.0;
        Turbo.fconv = 1.0;
        Turbo.mconv1 = 1.0;
        Turbo.pconv = 1.0;
        Turbo.econv = 1.0;
        Turbo.aconv = 1.0;
        Turbo.bconv = 1.0;
        Turbo.mconv2 = 1.0;
        Turbo.dconv = 1.0;
        Turbo.flconv = 1.0;
        Turbo.econv2 = 1.0;
        Turbo.tconv = 1.0;
        Turbo.tref = 459.6;
        Turbo.g0 = Turbo.g0d = 32.2;

        turbo.counter = 0;
        turbo.showcom = 0;
        turbo.plttyp = 0;
        turbo.pltkeep = 0;
        turbo.entype = 0;
        turbo.inflag = 0;
        turbo.varflag = 0;
        turbo.pt2flag = 0;
        turbo.wtflag = 0;
        Turbo.fireflag = 0;
        Turbo.gama = 1.4;
        turbo.gamopt = 1;
        Turbo.u0d = 0.0;
        Turbo.altd = 0.0;
        Turbo.throtl = 100.;

        for (i = 0; i <= 19; ++i) {
            Turbo.trat[i] = 1.0;
            Turbo.tt[i] = 518.6;
            Turbo.prat[i] = 1.0;
            Turbo.pt[i] = 14.7;
            Turbo.eta[i] = 1.0;
        }
        Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2500.;
        Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 2500.;
        Turbo.prat[3] = Turbo.p3p2d = 8.0;
        Turbo.prat[13] = Turbo.p3fp2d = 2.0;
        Turbo.byprat = 1.0;
        turbo.abflag = 0;

        turbo.fueltype = 0;
        Turbo.fhvd = Turbo.fhv = 18600.;
        Turbo.a2d = Turbo.a2 = Turbo.acore = 2.0;
        Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
        Turbo.ac = .9 * Turbo.a2;
        Turbo.a8rat = .35;
        Turbo.a8 = .7;
        Turbo.a8d = .40;
        turbo.arsched = 0;
        Turbo.afan = 2.0;
        Turbo.a4 = .418;

        turbo.athsched = 1;
        turbo.aexsched = 1;
        Turbo.arthmn = 0.1;
        Turbo.arthmx = 1.5;
        Turbo.arexmn = 1.0;
        Turbo.arexmx = 10.0;
        Turbo.arthd = Turbo.arth = .4;
        Turbo.arexit = Turbo.arexitd = 3.0;

        Turbo.u0mt = 1500.;
        Turbo.u0mr = 4500.;
        Turbo.altmt = 60000.;
        Turbo.altmr = 100000.;

        Turbo.u0min = 0.0;
        Turbo.u0max = Turbo.u0mt;
        Turbo.altmin = 0.0;
        Turbo.altmax = Turbo.altmt;
        Turbo.thrmin = 30;
        Turbo.thrmax = 100;
        Turbo.etmin = .5;
        Turbo.etmax = 1.0;
        Turbo.cprmin = 1.0;
        Turbo.cprmax = 50.0;
        Turbo.bypmin = 0.0;
        Turbo.bypmax = 10.0;
        Turbo.fprmin = 1.0;
        Turbo.fprmax = 2.0;
        Turbo.t4min = 1000.0;
        Turbo.t4max = 3200.0;
        Turbo.t7min = 1000.0;
        Turbo.t7max = 4000.0;
        Turbo.a8min = 0.1;
        Turbo.a8max = 0.4;
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

        Turbo.xtrans = 125.0;
        Turbo.ytrans = 115.0;
        Turbo.factor = 35.;
        Turbo.sldloc = 75;

        Turbo.xtranp = 80.0;
        Turbo.ytranp = 180.0;
        Turbo.factp = 27.;
        Turbo.sldplt = 130;

        Turbo.weight = 1000.;
        Turbo.minlt = 1;
        Turbo.dinlt = 170.2;
        Turbo.tinlt = 900.;
        Turbo.mfan = 2;
        Turbo.dfan = 293.02;
        Turbo.tfan = 1500.;
        Turbo.mcomp = 2;
        Turbo.dcomp = 293.02;
        Turbo.tcomp = 1500.;
        Turbo.mburner = 4;
        Turbo.dburner = 515.2;
        Turbo.tburner = 2500.;
        Turbo.mturbin = 4;
        Turbo.dturbin = 515.2;
        Turbo.tturbin = 2500.;
        Turbo.mnozl = 3;
        Turbo.dnozl = 515.2;
        Turbo.tnozl = 2500.;
        Turbo.mnozr = 5;
        Turbo.dnozr = 515.2;
        Turbo.tnozr = 4500.;
        Turbo.ncflag = 0;
        Turbo.ntflag = 0;

        turbo.iprint = 0;
        turbo.pall = 0;
        turbo.pfs = 1;
        turbo.peng = 1;
        turbo.pth = 1;
        turbo.ptrat = 0;
        turbo.ppres = 0;
        turbo.pvol = 0;
        turbo.ptrat = 0;
        turbo.pttot = 0;
        turbo.pentr = 0;
        turbo.pgam = 0;
        turbo.peta = 0;
        turbo.parea = 0;

        return;
    }

    public void myDesign() {

        turbo.ensav = turbo.entype;
        turbo.absav = turbo.abflag;
        turbo.flsav = turbo.fueltype;
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
        turbo.gamosav = turbo.gamopt;
        turbo.ptfsav = turbo.pt2flag;
        Turbo.et2sav = Turbo.eta[2];
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
        turbo.arssav = turbo.arsched;

        turbo.wtfsav = turbo.wtflag;
        Turbo.wtsav = Turbo.weight;
        turbo.minsav = Turbo.minlt;
        Turbo.dinsav = Turbo.dinlt;
        Turbo.tinsav = Turbo.tinlt;
        turbo.mfnsav = Turbo.mfan;
        Turbo.dfnsav = Turbo.dfan;
        Turbo.tfnsav = Turbo.tfan;
        turbo.mcmsav = Turbo.mcomp;
        Turbo.dcmsav = Turbo.dcomp;
        Turbo.tcmsav = Turbo.tcomp;
        turbo.mbrsav = Turbo.mburner;
        Turbo.dbrsav = Turbo.dburner;
        Turbo.tbrsav = Turbo.tburner;
        turbo.mtrsav = Turbo.mturbin;
        Turbo.dtrsav = Turbo.dturbin;
        Turbo.ttrsav = Turbo.tturbin;
        turbo.mnlsav = Turbo.mnozl;
        Turbo.dnlsav = Turbo.dnozl;
        Turbo.tnlsav = Turbo.tnozl;
        turbo.mnrsav = Turbo.mnozr;
        Turbo.dnrsav = Turbo.dnozr;
        Turbo.tnrsav = Turbo.tnozr;
        turbo.ncsav = Turbo.ncflag;
        turbo.ntsav = Turbo.ntflag;

        if(turbo.entype == 3) {
            turbo.arthsav = turbo.athsched;
            turbo.arxsav = turbo.aexsched;
            Turbo.artsav = Turbo.arthd;
            Turbo.arexsav = Turbo.arexitd;
        }

        return;
    }

    public void loadMine() {

        turbo.entype = turbo.ensav;
        turbo.abflag = turbo.absav;
        turbo.fueltype = turbo.flsav;
        Turbo.fhvd = Turbo.fhv = Turbo.fhsav;
        Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = Turbo.t4sav;
        Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = Turbo.t7sav;
        Turbo.prat[3] = Turbo.p3p2d = Turbo.p3sav;
        Turbo.prat[13] = Turbo.p3fp2d = Turbo.p3fsav;
        Turbo.byprat = Turbo.bysav;
        Turbo.acore = Turbo.acsav;
        Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
        Turbo.a2d = Turbo.a2 = Turbo.a2sav;
        Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
        Turbo.a4 = Turbo.a4sav;
        Turbo.a4p = Turbo.a4psav;
        Turbo.ac = .9 * Turbo.a2;
        Turbo.gama = Turbo.gamsav;
        turbo.gamopt = turbo.gamosav;
        turbo.pt2flag = turbo.ptfsav;
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
        turbo.arsched = turbo.arssav;

        turbo.wtflag = turbo.wtfsav;
        Turbo.weight = Turbo.wtsav;
        Turbo.minlt = turbo.minsav;
        Turbo.dinlt = Turbo.dinsav;
        Turbo.tinlt = Turbo.tinsav;
        Turbo.mfan = turbo.mfnsav;
        Turbo.dfan = Turbo.dfnsav;
        Turbo.tfan = Turbo.tfnsav;
        Turbo.mcomp = turbo.mcmsav;
        Turbo.dcomp = Turbo.dcmsav;
        Turbo.tcomp = Turbo.tcmsav;
        Turbo.mburner = turbo.mbrsav;
        Turbo.dburner = Turbo.dbrsav;
        Turbo.tburner = Turbo.tbrsav;
        Turbo.mturbin = turbo.mtrsav;
        Turbo.dturbin = Turbo.dtrsav;
        Turbo.tturbin = Turbo.ttrsav;
        Turbo.mnozl = turbo.mnlsav;
        Turbo.dnozl = Turbo.dnlsav;
        Turbo.tnozl = Turbo.tnlsav;
        Turbo.mnozr = turbo.mnrsav;
        Turbo.dnozr = Turbo.dnrsav;
        Turbo.tnozr = Turbo.tnrsav;
        Turbo.ncflag = turbo.ncsav;
        Turbo.ntflag = turbo.ntsav;

        if(turbo.entype == 3) {
            turbo.athsched = turbo.arthsav;
            turbo.aexsched = turbo.arxsav;
            Turbo.arthd = Turbo.artsav;
            Turbo.arexitd = Turbo.arexsav;
        }

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadCF6() {

        turbo.entype = 2;
        turbo.abflag = 0;
        turbo.fueltype = 0;
        Turbo.fhvd = Turbo.fhv = 18600.;
        Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2500.;
        Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 2500.;
        Turbo.prat[3] = Turbo.p3p2d = 21.86;
        Turbo.prat[13] = Turbo.p3fp2d = 1.745;
        Turbo.byprat = 3.3;
        Turbo.acore = 6.965;
        Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
        Turbo.a2d = Turbo.a2 = Turbo.afan;
        Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
        Turbo.a4 = .290;
        Turbo.a4p = 1.131;
        Turbo.ac = .9 * Turbo.a2;
        Turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        Turbo.eta[2] = 1.0;
        Turbo.prat[2] = 1.0;
        Turbo.prat[4] = 1.0;
        Turbo.eta[3] = .959;
        Turbo.eta[4] = .984;
        Turbo.eta[5] = .982;
        Turbo.eta[7] = 1.0;
        Turbo.eta[13] = 1.0;
        Turbo.a8d = 2.436;
        Turbo.a8max = .35;
        Turbo.a8rat = .35;

        Turbo.u0max = Turbo.u0mt;
        Turbo.u0d = 0.0;
        Turbo.altmax = Turbo.altmt;
        Turbo.altd = 0.0;
        turbo.arsched = 0;

        turbo.wtflag = 0;
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

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadJ85() {

        turbo.entype = 0;
        turbo.abflag = 0;
        turbo.fueltype = 0;
        Turbo.fhvd = Turbo.fhv = 18600.;
        Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2260.;
        Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 4000.;
        Turbo.prat[3] = Turbo.p3p2d = 8.3;
        Turbo.prat[13] = Turbo.p3fp2d = 1.0;
        Turbo.byprat = 0.0;
        Turbo.a2d = Turbo.a2 = Turbo.acore = 1.753;
        Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
        Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
        Turbo.a4 = .323;
        Turbo.a4p = .818;
        Turbo.ac = .9 * Turbo.a2;
        Turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        Turbo.eta[2] = 1.0;
        Turbo.prat[2] = 1.0;
        Turbo.prat[4] = .85;
        Turbo.eta[3] = .822;
        Turbo.eta[4] = .982;
        Turbo.eta[5] = .882;
        Turbo.eta[7] = .978;
        Turbo.eta[13] = 1.0;
        Turbo.a8d = .818;
        Turbo.a8max = .467;
        Turbo.a8rat = .467;

        Turbo.u0max = Turbo.u0mt;
        Turbo.u0d = 0.0;
        Turbo.altmax = Turbo.altmt;
        Turbo.altd = 0.0;
        turbo.arsched = 1;

        turbo.wtflag = 0;
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

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadF100() {

        turbo.entype = 1;
        turbo.abflag = 1;
        turbo.fueltype = 0;
        Turbo.fhvd = Turbo.fhv = 18600.;
        Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 2499.;
        Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 3905.;
        Turbo.prat[3] = Turbo.p3p2d = 20.04;
        Turbo.prat[13] = Turbo.p3fp2d = 1.745;
        Turbo.byprat = 0.0;
        Turbo.a2d = Turbo.a2 = Turbo.acore = 6.00;
        Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
        Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
        Turbo.a4 = .472;
        Turbo.a4p = 1.524;
        Turbo.ac = .9 * Turbo.a2;
        Turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        Turbo.eta[2] = 1.0;
        Turbo.prat[2] = 1.0;
        Turbo.prat[4] = 1.0;
        Turbo.eta[3] = .959;
        Turbo.eta[4] = .984;
        Turbo.eta[5] = .982;
        Turbo.eta[7] = .92;
        Turbo.eta[13] = 1.0;
        Turbo.a8d = 1.524;
        Turbo.a8max = .335;
        Turbo.a8rat = .335;

        Turbo.u0max = Turbo.u0mt;
        Turbo.u0d = 0.0;
        Turbo.altmax = Turbo.altmt;
        Turbo.altd = 0.0;
        turbo.arsched = 0;

        turbo.wtflag = 0;
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
        Turbo.dnozl = 400.2;
        Turbo.tnozl = 4100.;
        Turbo.ncflag = 0;
        Turbo.ntflag = 0;

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadRamj() {

        turbo.entype = 3;
        turbo.athsched = 1;
        turbo.aexsched = 1;
        Turbo.arthd = .4;
        Turbo.arexitd = 3.0;
        turbo.abflag = 0;
        turbo.fueltype = 0;
        Turbo.fhvd = Turbo.fhv = 18600.;
        Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = 4000.;
        Turbo.t4max = 4500.;
        Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = 4000.;
        Turbo.prat[3] = Turbo.p3p2d = 1.0;
        Turbo.prat[13] = Turbo.p3fp2d = 1.0;
        Turbo.byprat = 0.0;
        Turbo.a2d = Turbo.a2 = Turbo.acore = 1.753;
        Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
        Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
        Turbo.a4 = .323;
        Turbo.a4p = .818;
        Turbo.ac = .9 * Turbo.a2;
        Turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        Turbo.eta[2] = 1.0;
        Turbo.prat[2] = 1.0;
        Turbo.prat[4] = 1.0;
        Turbo.eta[3] = 1.0;
        Turbo.eta[4] = .982;
        Turbo.eta[5] = 1.0;
        Turbo.eta[7] = 1.0;
        Turbo.eta[13] = 1.0;
        Turbo.a8 = Turbo.a8d = 2.00;
        Turbo.a8max = 15.;
        Turbo.a8rat = 4.0;
        Turbo.a7 = .50;

        Turbo.u0max = Turbo.u0mr;
        Turbo.u0d = 2200.0;
        Turbo.altmax = Turbo.altmr;
        Turbo.altd = 10000.0;
        turbo.arsched = 0;

        turbo.wtflag = 0;
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
        Turbo.dnozr = 515.2;
        Turbo.tnozr = 4500.;
        Turbo.ncflag = 0;
        Turbo.ntflag = 0;

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void getFreeStream() {
        Turbo.rgas = 1718.;                /* ft2/sec2 R */
        if(turbo.inptype >= 2) {
            Turbo.ps0 = Turbo.ps0 * 144.;
        }
        if(turbo.inptype <= 1) {            /* input altitude */
            Turbo.alt = Turbo.altd / Turbo.lconv1;
            if(Turbo.alt < 36152.) {
                Turbo.ts0 = 518.6 - 3.56 * Turbo.alt / 1000.;
                Turbo.ps0 = 2116. * Math.pow(Turbo.ts0 / 518.6, 5.256);
            }
            if(Turbo.alt >= 36152. && Turbo.alt <= 82345.) {   // Stratosphere
                Turbo.ts0 = 389.98;
                Turbo.ps0 = 2116. * .2236 *
                            Math.exp((36000. - Turbo.alt) / (53.35 * 389.98));
            }
            if(Turbo.alt >= 82345.) {
                Turbo.ts0 = 389.98 + 1.645 * (Turbo.alt - 82345) / 1000.;
                Turbo.ps0 = 2116. * .02456 * Math.pow(Turbo.ts0 / 389.98, -11.388);
            }
        }
        Turbo.a0 = Math.sqrt(Turbo.gama * Turbo.rgas * Turbo.ts0);             /* speed of sound ft/sec */
        if(turbo.inptype == 0 || turbo.inptype == 2) {           /* input speed  */
            Turbo.u0 = Turbo.u0d / Turbo.lconv2 * 5280. / 3600.;           /* airspeed ft/sec */
            Turbo.fsmach = Turbo.u0 / Turbo.a0;
            Turbo.q0 = Turbo.gama / 2.0 * Turbo.fsmach * Turbo.fsmach * Turbo.ps0;
        }
        if(turbo.inptype == 1 || turbo.inptype == 3) {            /* input mach */
            Turbo.u0 = Turbo.fsmach * Turbo.a0;
            Turbo.u0d = Turbo.u0 * Turbo.lconv2 / 5280. * 3600.;      /* airspeed ft/sec */
            Turbo.q0 = Turbo.gama / 2.0 * Turbo.fsmach * Turbo.fsmach * Turbo.ps0;
        }
        if(Turbo.u0 > .0001) {
            Turbo.rho0 = Turbo.q0 / (Turbo.u0 * Turbo.u0);
        } else {
            Turbo.rho0 = 1.0;
        }

        Turbo.tt[0] = Turbo.ts0 * (1.0 + .5 * (Turbo.gama - 1.0) * Turbo.fsmach * Turbo.fsmach);
        Turbo.pt[0] = Turbo.ps0 * Math.pow(Turbo.tt[0] / Turbo.ts0, Turbo.gama / (Turbo.gama - 1.0));
        Turbo.ps0 = Turbo.ps0 / 144.;
        Turbo.pt[0] = Turbo.pt[0] / 144.;
        Turbo.cpair = turbo.getCp(Turbo.tt[0], turbo.gamopt);              /*BTU/lbm R */
        Turbo.tsout = Turbo.ts0;
        Turbo.psout = Turbo.ps0;

        return;
    }

    public void getThermo() {
        double dela;
        double t5t4n;
        double deriv;
        double delan;
        double m5;
        double delhc;
        double delhht;
        double delhf;
        double delhlt;
        double deltc;
        double deltht;
        double deltf;
        double deltlt;
        int itcount;
        int index;
        float fl1;
        int i1;
                                     /*   inletPanel recovery  */
        if(turbo.pt2flag == 0) {                    /*     mil spec      */
            if(Turbo.fsmach > 1.0) {          /* supersonic */
                Turbo.prat[2] = 1.0 - .075 * Math.pow(Turbo.fsmach - 1.0, 1.35);
            } else {
                Turbo.prat[2] = 1.0;
            }
            Turbo.eta[2] = Turbo.prat[2];
            fl1 = turbo.filter3(Turbo.prat[2]);
            turbo.inputPanel.inletPanel.inletLeftPanel.getF1().setText(String.valueOf(fl1));
            i1 = (int)(((Turbo.prat[2] - Turbo.etmin) / (Turbo.etmax - Turbo.etmin)) * 1000.);
            turbo.inputPanel.inletPanel.inletRightPanel.s1.setValue(i1);
        } else {                       /* enter value */
            Turbo.prat[2] = Turbo.eta[2];
        }
                           /* protection for overwriting input */
        if(Turbo.eta[3] < .5) {
            Turbo.eta[3] = .5;
        }
        if(Turbo.eta[5] < .5) {
            Turbo.eta[5] = .5;
        }
        Turbo.trat[7] = 1.0;
        Turbo.prat[7] = 1.0;
        Turbo.tt[2] = Turbo.tt[1] = Turbo.tt[0];
        Turbo.pt[1] = Turbo.pt[0];
        Turbo.gam[2] = turbo.getGama(Turbo.tt[2], turbo.gamopt);
        Turbo.cp[2] = turbo.getCp(Turbo.tt[2], turbo.gamopt);
        Turbo.pt[2] = Turbo.pt[1] * Turbo.prat[2];
    /* design - p3p2 specified - tt4 specified */
        if(turbo.inflag == 0) {

            if(turbo.entype <= 1) {              /* turbojet */
                Turbo.prat[3] = Turbo.p3p2d;                      /* core compressor */
                if(Turbo.prat[3] < .5) {
                    Turbo.prat[3] = .5;
                }
                delhc = (Turbo.cp[2] * Turbo.tt[2] / Turbo.eta[3]) *
                        (Math.pow(Turbo.prat[3], (Turbo.gam[2] - 1.0) / Turbo.gam[2]) - 1.0);
                deltc = delhc / Turbo.cp[2];
                Turbo.pt[3] = Turbo.pt[2] * Turbo.prat[3];
                Turbo.tt[3] = Turbo.tt[2] + deltc;
                Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[2];
                Turbo.gam[3] = turbo.getGama(Turbo.tt[3], turbo.gamopt);
                Turbo.cp[3] = turbo.getCp(Turbo.tt[3], turbo.gamopt);
                Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0;
                Turbo.gam[4] = turbo.getGama(Turbo.tt[4], turbo.gamopt);
                Turbo.cp[4] = turbo.getCp(Turbo.tt[4], turbo.gamopt);
                Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3];
                Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4];
                delhht = delhc;
                deltht = delhht / Turbo.cp[4];
                Turbo.tt[5] = Turbo.tt[4] - deltht;
                Turbo.gam[5] = turbo.getGama(Turbo.tt[5], turbo.gamopt);
                Turbo.cp[5] = turbo.getCp(Turbo.tt[5], turbo.gamopt);
                Turbo.trat[5] = Turbo.tt[5] / Turbo.tt[4];
                Turbo.prat[5] = Math.pow((1.0 - delhht / Turbo.cp[4] / Turbo.tt[4] / Turbo.eta[5]),
                                         (Turbo.gam[4] / (Turbo.gam[4] - 1.0)));
                Turbo.pt[5] = Turbo.pt[4] * Turbo.prat[5];
                                    /* fanPanel conditions */
                Turbo.prat[13] = 1.0;
                Turbo.trat[13] = 1.0;
                Turbo.tt[13] = Turbo.tt[2];
                Turbo.pt[13] = Turbo.pt[2];
                Turbo.gam[13] = Turbo.gam[2];
                Turbo.cp[13] = Turbo.cp[2];
                Turbo.prat[15] = 1.0;
                Turbo.pt[15] = Turbo.pt[5];
                Turbo.trat[15] = 1.0;
                Turbo.tt[15] = Turbo.tt[5];
                Turbo.gam[15] = Turbo.gam[5];
                Turbo.cp[15] = Turbo.cp[5];
            }

            if(turbo.entype == 2) {                         /* turbofan */
                Turbo.prat[13] = Turbo.p3fp2d;
                if(Turbo.prat[13] < .5) {
                    Turbo.prat[13] = .5;
                }
                delhf = (Turbo.cp[2] * Turbo.tt[2] / Turbo.eta[13]) *
                        (Math.pow(Turbo.prat[13], (Turbo.gam[2] - 1.0) / Turbo.gam[2]) - 1.0);
                deltf = delhf / Turbo.cp[2];
                Turbo.tt[13] = Turbo.tt[2] + deltf;
                Turbo.pt[13] = Turbo.pt[2] * Turbo.prat[13];
                Turbo.trat[13] = Turbo.tt[13] / Turbo.tt[2];
                Turbo.gam[13] = turbo.getGama(Turbo.tt[13], turbo.gamopt);
                Turbo.cp[13] = turbo.getCp(Turbo.tt[13], turbo.gamopt);
                Turbo.prat[3] = Turbo.p3p2d;                      /* core compressor */
                if(Turbo.prat[3] < .5) {
                    Turbo.prat[3] = .5;
                }
                delhc = (Turbo.cp[13] * Turbo.tt[13] / Turbo.eta[3]) *
                        (Math.pow(Turbo.prat[3], (Turbo.gam[13] - 1.0) / Turbo.gam[13]) - 1.0);
                deltc = delhc / Turbo.cp[13];
                Turbo.tt[3] = Turbo.tt[13] + deltc;
                Turbo.pt[3] = Turbo.pt[13] * Turbo.prat[3];
                Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[13];
                Turbo.gam[3] = turbo.getGama(Turbo.tt[3], turbo.gamopt);
                Turbo.cp[3] = turbo.getCp(Turbo.tt[3], turbo.gamopt);
                Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0;
                Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4];
                Turbo.gam[4] = turbo.getGama(Turbo.tt[4], turbo.gamopt);
                Turbo.cp[4] = turbo.getCp(Turbo.tt[4], turbo.gamopt);
                Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3];
                delhht = delhc;
                deltht = delhht / Turbo.cp[4];
                Turbo.tt[5] = Turbo.tt[4] - deltht;
                Turbo.gam[5] = turbo.getGama(Turbo.tt[5], turbo.gamopt);
                Turbo.cp[5] = turbo.getCp(Turbo.tt[5], turbo.gamopt);
                Turbo.trat[5] = Turbo.tt[5] / Turbo.tt[4];
                Turbo.prat[5] = Math.pow((1.0 - delhht / Turbo.cp[4] / Turbo.tt[4] / Turbo.eta[5]),
                                         (Turbo.gam[4] / (Turbo.gam[4] - 1.0)));
                Turbo.pt[5] = Turbo.pt[4] * Turbo.prat[5];
                delhlt = (1.0 + Turbo.byprat) * delhf;
                deltlt = delhlt / Turbo.cp[5];
                Turbo.tt[15] = Turbo.tt[5] - deltlt;
                Turbo.gam[15] = turbo.getGama(Turbo.tt[15], turbo.gamopt);
                Turbo.cp[15] = turbo.getCp(Turbo.tt[15], turbo.gamopt);
                Turbo.trat[15] = Turbo.tt[15] / Turbo.tt[5];
                Turbo.prat[15] = Math.pow((1.0 - delhlt / Turbo.cp[5] / Turbo.tt[5] / Turbo.eta[5]),
                                          (Turbo.gam[5] / (Turbo.gam[5] - 1.0)));
                Turbo.pt[15] = Turbo.pt[5] * Turbo.prat[15];
            }

            if(turbo.entype == 3) {              /* ramjet */
                Turbo.prat[3] = 1.0;
                Turbo.pt[3] = Turbo.pt[2] * Turbo.prat[3];
                Turbo.tt[3] = Turbo.tt[2];
                Turbo.trat[3] = 1.0;
                Turbo.gam[3] = turbo.getGama(Turbo.tt[3], turbo.gamopt);
                Turbo.cp[3] = turbo.getCp(Turbo.tt[3], turbo.gamopt);
                Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0;
                Turbo.gam[4] = turbo.getGama(Turbo.tt[4], turbo.gamopt);
                Turbo.cp[4] = turbo.getCp(Turbo.tt[4], turbo.gamopt);
                Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3];
                Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4];
                Turbo.tt[5] = Turbo.tt[4];
                Turbo.gam[5] = turbo.getGama(Turbo.tt[5], turbo.gamopt);
                Turbo.cp[5] = turbo.getCp(Turbo.tt[5], turbo.gamopt);
                Turbo.trat[5] = 1.0;
                Turbo.prat[5] = 1.0;
                Turbo.pt[5] = Turbo.pt[4];
                                    /* fanPanel conditions */
                Turbo.prat[13] = 1.0;
                Turbo.trat[13] = 1.0;
                Turbo.tt[13] = Turbo.tt[2];
                Turbo.pt[13] = Turbo.pt[2];
                Turbo.gam[13] = Turbo.gam[2];
                Turbo.cp[13] = Turbo.cp[2];
                Turbo.prat[15] = 1.0;
                Turbo.pt[15] = Turbo.pt[5];
                Turbo.trat[15] = 1.0;
                Turbo.tt[15] = Turbo.tt[5];
                Turbo.gam[15] = Turbo.gam[5];
                Turbo.cp[15] = Turbo.cp[5];
            }

            Turbo.tt[7] = Turbo.tt7;
        }
         /* analysis -assume flow choked at both turbine entrances */
                              /* and nozzle throat ... then*/
        else {
            Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0;
            Turbo.gam[4] = turbo.getGama(Turbo.tt[4], turbo.gamopt);
            Turbo.cp[4] = turbo.getCp(Turbo.tt[4], turbo.gamopt);
            if(Turbo.a4 < .02) {
                Turbo.a4 = .02;
            }

            if(turbo.entype <= 1) {              /* turbojet */
                dela = .2;                           /* iterate to get t5t4 */
                Turbo.trat[5] = 1.0;
                t5t4n = .5;
                itcount = 0;
                while (Math.abs(dela) > .001 && itcount < 20) {
                    ++itcount;
                    delan = Turbo.a8d / Turbo.a4 - Math.sqrt(t5t4n) *
                                                   Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - t5t4n)),
                                                            -Turbo.gam[4] / (Turbo.gam[4] - 1.0));
                    deriv = (delan - dela) / (t5t4n - Turbo.trat[5]);
                    dela = delan;
                    Turbo.trat[5] = t5t4n;
                    t5t4n = Turbo.trat[5] - dela / deriv;
                }
                Turbo.tt[5] = Turbo.tt[4] * Turbo.trat[5];
                Turbo.gam[5] = turbo.getGama(Turbo.tt[5], turbo.gamopt);
                Turbo.cp[5] = turbo.getCp(Turbo.tt[5], turbo.gamopt);
                deltht = Turbo.tt[5] - Turbo.tt[4];
                delhht = Turbo.cp[4] * deltht;
                Turbo.prat[5] = Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - Turbo.trat[5])),
                                         Turbo.gam[4] / (Turbo.gam[4] - 1.0));
                delhc = delhht;           /* compressor work */
                deltc = -delhc / Turbo.cp[2];
                Turbo.tt[3] = Turbo.tt[2] + deltc;
                Turbo.gam[3] = turbo.getGama(Turbo.tt[3], turbo.gamopt);
                Turbo.cp[3] = turbo.getCp(Turbo.tt[3], turbo.gamopt);
                Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[2];
                Turbo.prat[3] = Math.pow((1.0 + Turbo.eta[3] * (Turbo.trat[3] - 1.0)),
                                         Turbo.gam[2] / (Turbo.gam[2] - 1.0));
                Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3];
                Turbo.pt[3] = Turbo.pt[2] * Turbo.prat[3];
                Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4];
                Turbo.pt[5] = Turbo.pt[4] * Turbo.prat[5];
                                    /* fanPanel conditions */
                Turbo.prat[13] = 1.0;
                Turbo.trat[13] = 1.0;
                Turbo.tt[13] = Turbo.tt[2];
                Turbo.pt[13] = Turbo.pt[2];
                Turbo.gam[13] = Turbo.gam[2];
                Turbo.cp[13] = Turbo.cp[2];
                Turbo.prat[15] = 1.0;
                Turbo.pt[15] = Turbo.pt[5];
                Turbo.trat[15] = 1.0;
                Turbo.tt[15] = Turbo.tt[5];
                Turbo.gam[15] = Turbo.gam[5];
                Turbo.cp[15] = Turbo.cp[5];
            }

            if(turbo.entype == 2) {                        /*  turbofan */
                dela = .2;                           /* iterate to get t5t4 */
                Turbo.trat[5] = 1.0;
                t5t4n = .5;
                itcount = 0;
                while (Math.abs(dela) > .001 && itcount < 20) {
                    ++itcount;
                    delan = Turbo.a4p / Turbo.a4 - Math.sqrt(t5t4n) *
                                                   Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - t5t4n)),
                                                            -Turbo.gam[4] / (Turbo.gam[4] - 1.0));
                    deriv = (delan - dela) / (t5t4n - Turbo.trat[5]);
                    dela = delan;
                    Turbo.trat[5] = t5t4n;
                    t5t4n = Turbo.trat[5] - dela / deriv;
                }
                Turbo.tt[5] = Turbo.tt[4] * Turbo.trat[5];
                Turbo.gam[5] = turbo.getGama(Turbo.tt[5], turbo.gamopt);
                Turbo.cp[5] = turbo.getCp(Turbo.tt[5], turbo.gamopt);
                deltht = Turbo.tt[5] - Turbo.tt[4];
                delhht = Turbo.cp[4] * deltht;
                Turbo.prat[5] = Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - Turbo.trat[5])),
                                         Turbo.gam[4] / (Turbo.gam[4] - 1.0));
                                   /* iterate to get t15t14 */
                dela = .2;
                Turbo.trat[15] = 1.0;
                t5t4n = .5;
                itcount = 0;
                while (Math.abs(dela) > .001 && itcount < 20) {
                    ++itcount;
                    delan = Turbo.a8d / Turbo.a4p - Math.sqrt(t5t4n) *
                                                    Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - t5t4n)),
                                                             -Turbo.gam[5] / (Turbo.gam[5] - 1.0));
                    deriv = (delan - dela) / (t5t4n - Turbo.trat[15]);
                    dela = delan;
                    Turbo.trat[15] = t5t4n;
                    t5t4n = Turbo.trat[15] - dela / deriv;
                }
                Turbo.tt[15] = Turbo.tt[5] * Turbo.trat[15];
                Turbo.gam[15] = turbo.getGama(Turbo.tt[15], turbo.gamopt);
                Turbo.cp[15] = turbo.getCp(Turbo.tt[15], turbo.gamopt);
                deltlt = Turbo.tt[15] - Turbo.tt[5];
                delhlt = Turbo.cp[5] * deltlt;
                Turbo.prat[15] = Math.pow((1.0 - (1.0 / Turbo.eta[5]) * (1.0 - Turbo.trat[15])),
                                          Turbo.gam[5] / (Turbo.gam[5] - 1.0));
                Turbo.byprat = Turbo.afan / Turbo.acore - 1.0;
                delhf = delhlt / (1.0 + Turbo.byprat);              /* fanPanel work */
                deltf = -delhf / Turbo.cp[2];
                Turbo.tt[13] = Turbo.tt[2] + deltf;
                Turbo.gam[13] = turbo.getGama(Turbo.tt[13], turbo.gamopt);
                Turbo.cp[13] = turbo.getCp(Turbo.tt[13], turbo.gamopt);
                Turbo.trat[13] = Turbo.tt[13] / Turbo.tt[2];
                Turbo.prat[13] = Math.pow((1.0 + Turbo.eta[13] * (Turbo.trat[13] - 1.0)),
                                          Turbo.gam[2] / (Turbo.gam[2] - 1.0));
                delhc = delhht;                         /* compressor work */
                deltc = -delhc / Turbo.cp[13];
                Turbo.tt[3] = Turbo.tt[13] + deltc;
                Turbo.gam[3] = turbo.getGama(Turbo.tt[3], turbo.gamopt);
                Turbo.cp[3] = turbo.getCp(Turbo.tt[3], turbo.gamopt);
                Turbo.trat[3] = Turbo.tt[3] / Turbo.tt[13];
                Turbo.prat[3] = Math.pow((1.0 + Turbo.eta[3] * (Turbo.trat[3] - 1.0)),
                                         Turbo.gam[13] / (Turbo.gam[13] - 1.0));
                Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3];
                Turbo.pt[13] = Turbo.pt[2] * Turbo.prat[13];
                Turbo.pt[3] = Turbo.pt[13] * Turbo.prat[3];
                Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4];
                Turbo.pt[5] = Turbo.pt[4] * Turbo.prat[5];
                Turbo.pt[15] = Turbo.pt[5] * Turbo.prat[15];
            }

            if(turbo.entype == 3) {              /* ramjet */
                Turbo.prat[3] = 1.0;
                Turbo.pt[3] = Turbo.pt[2] * Turbo.prat[3];
                Turbo.tt[3] = Turbo.tt[2];
                Turbo.trat[3] = 1.0;
                Turbo.gam[3] = turbo.getGama(Turbo.tt[3], turbo.gamopt);
                Turbo.cp[3] = turbo.getCp(Turbo.tt[3], turbo.gamopt);
                Turbo.tt[4] = Turbo.tt4 * Turbo.throtl / 100.0;
                Turbo.trat[4] = Turbo.tt[4] / Turbo.tt[3];
                Turbo.pt[4] = Turbo.pt[3] * Turbo.prat[4];
                Turbo.tt[5] = Turbo.tt[4];
                Turbo.gam[5] = turbo.getGama(Turbo.tt[5], turbo.gamopt);
                Turbo.cp[5] = turbo.getCp(Turbo.tt[5], turbo.gamopt);
                Turbo.trat[5] = 1.0;
                Turbo.prat[5] = 1.0;
                Turbo.pt[5] = Turbo.pt[4];
                                     /* fanPanel conditions */
                Turbo.prat[13] = 1.0;
                Turbo.trat[13] = 1.0;
                Turbo.tt[13] = Turbo.tt[2];
                Turbo.pt[13] = Turbo.pt[2];
                Turbo.gam[13] = Turbo.gam[2];
                Turbo.cp[13] = Turbo.cp[2];
                Turbo.prat[15] = 1.0;
                Turbo.pt[15] = Turbo.pt[5];
                Turbo.trat[15] = 1.0;
                Turbo.tt[15] = Turbo.tt[5];
                Turbo.gam[15] = Turbo.gam[5];
                Turbo.cp[15] = Turbo.cp[5];
            }

            if(turbo.abflag == 1) {
                Turbo.tt[7] = Turbo.tt7;
            }
        }

        Turbo.prat[6] = 1.0;
        Turbo.pt[6] = Turbo.pt[15];
        Turbo.trat[6] = 1.0;
        Turbo.tt[6] = Turbo.tt[15];
        Turbo.gam[6] = turbo.getGama(Turbo.tt[6], turbo.gamopt);
        Turbo.cp[6] = turbo.getCp(Turbo.tt[6], turbo.gamopt);
        if(turbo.abflag > 0) {                   /* afterburner */
            Turbo.trat[7] = Turbo.tt[7] / Turbo.tt[6];
            m5 = turbo.getMach(0, turbo.getAir(1.0, Turbo.gam[5]) * Turbo.a4 / Turbo.acore, Turbo.gam[5]);
            Turbo.prat[7] = turbo.getRayleighLoss(m5, Turbo.trat[7], Turbo.tt[6]);
        }
        Turbo.tt[7] = Turbo.tt[6] * Turbo.trat[7];
        Turbo.pt[7] = Turbo.pt[6] * Turbo.prat[7];
        Turbo.gam[7] = turbo.getGama(Turbo.tt[7], turbo.gamopt);
        Turbo.cp[7] = turbo.getCp(Turbo.tt[7], turbo.gamopt);
             /* engine press ratio EPR*/
        Turbo.epr = Turbo.prat[7] * Turbo.prat[15] * Turbo.prat[5] * Turbo.prat[4] * Turbo.prat[3] * Turbo.prat[13];
          /* engine temp ratio ETR */
        Turbo.etr = Turbo.trat[7] * Turbo.trat[15] * Turbo.trat[5] * Turbo.trat[4] * Turbo.trat[3] * Turbo.trat[13];
        return;
    }

    public void getPerform() {       /* determine engine performance */
        double fac1;
        double game;
        double cpe;
        double cp3;
        double rg;
        double p8p5;
        double rg1;
        int index;

        rg1 = 53.3;
        rg = Turbo.cpair * (Turbo.gama - 1.0) / Turbo.gama;
        cp3 = turbo.getCp(Turbo.tt[3], turbo.gamopt);                  /*BTU/lbm R */
        Turbo.g0 = 32.2;
        Turbo.ues = 0.0;
        game = turbo.getGama(Turbo.tt[5], turbo.gamopt);
        fac1 = (game - 1.0) / game;
        cpe = turbo.getCp(Turbo.tt[5], turbo.gamopt);
        if(Turbo.eta[7] < .8) {
            Turbo.eta[7] = .8;    /* protection during overwriting */
        }
        if(Turbo.eta[4] < .8) {
            Turbo.eta[4] = .8;
        }

   /*  specific net thrust  - thrust / (g0*airflow) -   lbf/lbm/sec  */
        // turbine engine core
        if(turbo.entype <= 2) {
                        /* airflow determined at choked nozzle exit */
            Turbo.pt[8] = Turbo.epr * Turbo.prat[2] * Turbo.pt[0];
            Turbo.eair = turbo.getAir(1.0, game) * 144. * Turbo.a8 * Turbo.pt[8] / 14.7 /
                         Math.sqrt(Turbo.etr * Turbo.tt[0] / 518.);
            Turbo.m2 = turbo.getMach(0, Turbo.eair * Math.sqrt(Turbo.tt[0] / 518.) /
                                        (Turbo.prat[2] * Turbo.pt[0] / 14.7 * Turbo.acore * 144.), Turbo.gama);
            Turbo.npr = Turbo.pt[8] / Turbo.ps0;
            Turbo.uexit = Math.sqrt(2.0 * Turbo.rgas / fac1 * Turbo.etr * Turbo.tt[0] * Turbo.eta[7] *
                                    (1.0 - Math.pow(1.0 / Turbo.npr, fac1)));
            if(Turbo.npr <= 1.893) {
                Turbo.pexit = Turbo.ps0;
            } else {
                Turbo.pexit = .52828 * Turbo.pt[8];
            }
            Turbo.fgros = (Turbo.uexit + (Turbo.pexit - Turbo.ps0) * 144. * Turbo.a8 / Turbo.eair) / Turbo.g0;
        }

        // turbo fanPanel -- added terms for fanPanel flow
        if(turbo.entype == 2) {
            fac1 = (Turbo.gama - 1.0) / Turbo.gama;
            Turbo.snpr = Turbo.pt[13] / Turbo.ps0;
            Turbo.ues = Math.sqrt(2.0 * Turbo.rgas / fac1 * Turbo.tt[13] * Turbo.eta[7] *
                                  (1.0 - Math.pow(1.0 / Turbo.snpr, fac1)));
            Turbo.m2 = turbo.getMach(0, Turbo.eair * (1.0 + Turbo.byprat) * Math.sqrt(Turbo.tt[0] / 518.) /
                                        (Turbo.prat[2] * Turbo.pt[0] / 14.7 * Turbo.afan * 144.), Turbo.gama);
            if(Turbo.snpr <= 1.893) {
                Turbo.pfexit = Turbo.ps0;
            } else {
                Turbo.pfexit = .52828 * Turbo.pt[13];
            }
            Turbo.fgros = Turbo.fgros + (Turbo.byprat * Turbo.ues + (Turbo.pfexit - Turbo.ps0) * 144. * Turbo.byprat * Turbo.acore / Turbo.eair) / Turbo.g0;
        }

        // ramjets
        if(turbo.entype == 3) {
                       /* airflow determined at nozzle throat */
            Turbo.eair = turbo.getAir(1.0, game) * 144.0 * Turbo.a2 * Turbo.arthd * Turbo.epr * Turbo.prat[2] * Turbo.pt[0] / 14.7 /
                         Math.sqrt(Turbo.etr * Turbo.tt[0] / 518.);
            Turbo.m2 = turbo.getMach(0, Turbo.eair * Math.sqrt(Turbo.tt[0] / 518.) /
                                        (Turbo.prat[2] * Turbo.pt[0] / 14.7 * Turbo.acore * 144.), Turbo.gama);
            Turbo.mexit = turbo.getMach(2, (turbo.getAir(1.0, game) / Turbo.arexitd), game);
            Turbo.uexit = Turbo.mexit * Math.sqrt(game * Turbo.rgas * Turbo.etr * Turbo.tt[0] * Turbo.eta[7] /
                                                  (1.0 + .5 * (game - 1.0) * Turbo.mexit * Turbo.mexit));
            Turbo.pexit = Math.pow((1.0 + .5 * (game - 1.0) * Turbo.mexit * Turbo.mexit), (-game / (game - 1.0)))
                          * Turbo.epr * Turbo.prat[2] * Turbo.pt[0];
            Turbo.fgros = (Turbo.uexit + (Turbo.pexit - Turbo.ps0) * Turbo.arexitd * Turbo.arthd * Turbo.a2 / Turbo.eair / 144.) / Turbo.g0;
        }

        // ram drag
        Turbo.dram = Turbo.u0 / Turbo.g0;
        if(turbo.entype == 2) {
            Turbo.dram = Turbo.dram + Turbo.u0 * Turbo.byprat / Turbo.g0;
        }
        // mass flow ratio
        if(Turbo.fsmach > .01) {
            Turbo.mfr = turbo.getAir(Turbo.m2, Turbo.gama) * Turbo.prat[2] / turbo.getAir(Turbo.fsmach, Turbo.gama);
        } else {
            Turbo.mfr = 5.;
        }

        // net thrust
        Turbo.fnet = Turbo.fgros - Turbo.dram;
        if(turbo.entype == 3 && Turbo.fsmach < .3) {
            Turbo.fnet = 0.0;
            Turbo.fgros = 0.0;
        }

        // thrust inputPanel pounds
        Turbo.fnlb = Turbo.fnet * Turbo.eair;
        Turbo.fglb = Turbo.fgros * Turbo.eair;
        Turbo.drlb = Turbo.dram * Turbo.eair;

        //fuel-air ratio and sfc
        Turbo.fa = (Turbo.trat[4] - 1.0) / (Turbo.eta[4] * Turbo.fhv / (cp3 * Turbo.tt[3]) - Turbo.trat[4]) +
                   (Turbo.trat[7] - 1.0) / (Turbo.fhv / (cpe * Turbo.tt[15]) - Turbo.trat[7]);
        if(Turbo.fnet > 0.0) {
            Turbo.sfc = 3600. * Turbo.fa / Turbo.fnet;
            Turbo.flflo = Turbo.sfc * Turbo.fnlb;
            Turbo.isp = (Turbo.fnlb / Turbo.flflo) * 3600.;
        } else {
            Turbo.fnlb = 0.0;
            Turbo.flflo = 0.0;
            Turbo.sfc = 0.0;
            Turbo.isp = 0.0;
        }
        Turbo.tt[8] = Turbo.tt[7];
        Turbo.t8 = Turbo.etr * Turbo.tt[0] - Turbo.uexit * Turbo.uexit / (2.0 * Turbo.rgas * game / (game - 1.0));
        Turbo.trat[8] = 1.0;
        p8p5 = Turbo.ps0 / (Turbo.epr * Turbo.prat[2] * Turbo.pt[0]);
        Turbo.cp[8] = turbo.getCp(Turbo.tt[8], turbo.gamopt);
        Turbo.pt[8] = Turbo.pt[7];
        Turbo.prat[8] = Turbo.pt[8] / Turbo.pt[7];
    /* thermal effeciency */
        if(turbo.entype == 2) {
            Turbo.eteng = (Turbo.a0 * Turbo.a0 * ((1.0 + Turbo.fa) * (Turbo.uexit * Turbo.uexit / (Turbo.a0 * Turbo.a0))
                                                  + Turbo.byprat * (Turbo.ues * Turbo.ues / (Turbo.a0 * Turbo.a0))
                                                  - (1.0 + Turbo.byprat) * Turbo.fsmach * Turbo.fsmach)) / (2.0 * Turbo.g0 * Turbo.fa * Turbo.fhv * 778.16);
        } else {
            Turbo.eteng = (Turbo.a0 * Turbo.a0 * ((1.0 + Turbo.fa) * (Turbo.uexit * Turbo.uexit / (Turbo.a0 * Turbo.a0))
                                                  - Turbo.fsmach * Turbo.fsmach)) / (2.0 * Turbo.g0 * Turbo.fa * Turbo.fhv * 778.16);
        }

        Turbo.s[0] = Turbo.s[1] = .2;
        Turbo.v[0] = Turbo.v[1] = rg1 * Turbo.ts0 / (Turbo.ps0 * 144.);
        for (index = 2; index <= 7; ++index) {     /* compute entropy */
            Turbo.s[index] = Turbo.s[index - 1] + Turbo.cpair * Math.log(Turbo.trat[index])
                             - rg * Math.log(Turbo.prat[index]);
            Turbo.v[index] = rg1 * Turbo.tt[index] / (Turbo.pt[index] * 144.);
        }
        Turbo.s[13] = Turbo.s[2] + Turbo.cpair * Math.log(Turbo.trat[13]) - rg * Math.log(Turbo.prat[13]);
        Turbo.v[13] = rg1 * Turbo.tt[13] / (Turbo.pt[13] * 144.);
        Turbo.s[15] = Turbo.s[5] + Turbo.cpair * Math.log(Turbo.trat[15]) - rg * Math.log(Turbo.prat[15]);
        Turbo.v[15] = rg1 * Turbo.tt[15] / (Turbo.pt[15] * 144.);
        Turbo.s[8] = Turbo.s[7] + Turbo.cpair * Math.log(Turbo.t8 / (Turbo.etr * Turbo.tt[0])) - rg * Math.log(p8p5);
        Turbo.v[8] = rg1 * Turbo.t8 / (Turbo.ps0 * 144.);
        Turbo.cp[0] = turbo.getCp(Turbo.tt[0], turbo.gamopt);

        Turbo.fntot = turbo.numeng * Turbo.fnlb;
        Turbo.fuelrat = turbo.numeng * Turbo.flflo;
        // weight  calculation
        if(turbo.wtflag == 0) {
            if(turbo.entype == 0) {
                Turbo.weight = .132 * Math.sqrt(Turbo.acore * Turbo.acore * Turbo.acore) *
                               (Turbo.dcomp * Turbo.lcomp + Turbo.dburner * Turbo.lburn + Turbo.dturbin * Turbo.lturb + Turbo.dnozl * Turbo.lnoz);
            }
            if(turbo.entype == 1) {
                Turbo.weight = .100 * Math.sqrt(Turbo.acore * Turbo.acore * Turbo.acore) *
                               (Turbo.dcomp * Turbo.lcomp + Turbo.dburner * Turbo.lburn + Turbo.dturbin * Turbo.lturb + Turbo.dnozl * Turbo.lnoz);
            }
            if(turbo.entype == 2) {
                Turbo.weight = .0932 * Turbo.acore * ((1.0 + Turbo.byprat) * Turbo.dfan * 4.0 + Turbo.dcomp * (Turbo.ncomp - 3) +
                                                      Turbo.dburner + Turbo.dturbin * Turbo.nturb + Turbo.dburner * 2.0) * Math.sqrt(Turbo.acore / 6.965);
            }
            if(turbo.entype == 3) {
                Turbo.weight = .1242 * Turbo.acore * (Turbo.dburner + Turbo.dnozr * 6. + Turbo.dinlt * 3.) * Math.sqrt(Turbo.acore / 1.753);
            }
        }
        // check for temp limits
        turbo.outputPanel.outputVariablesPanel.to1.setForeground(Color.yellow);
        turbo.outputPanel.outputVariablesPanel.to2.setForeground(Color.yellow);
        turbo.outputPanel.outputVariablesPanel.to3.setForeground(Color.yellow);
        turbo.outputPanel.outputVariablesPanel.to4.setForeground(Color.yellow);
        turbo.outputPanel.outputVariablesPanel.to5.setForeground(Color.yellow);
        turbo.outputPanel.outputVariablesPanel.to6.setForeground(Color.yellow);
        turbo.outputPanel.outputVariablesPanel.to7.setForeground(Color.yellow);
        if(turbo.entype < 3) {
            if(Turbo.tt[2] > Turbo.tinlt) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to1.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to2.setForeground(Color.red);
            }
            if(Turbo.tt[13] > Turbo.tfan) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to2.setForeground(Color.red);
            }
            if(Turbo.tt[3] > Turbo.tcomp) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to3.setForeground(Color.red);
            }
            if(Turbo.tt[4] > Turbo.tburner) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to4.setForeground(Color.red);
            }
            if(Turbo.tt[5] > Turbo.tturbin) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to5.setForeground(Color.red);
            }
            if(Turbo.tt[7] > Turbo.tnozl) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to6.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to7.setForeground(Color.red);
            }
        }
        if(turbo.entype == 3) {
            if(Turbo.tt[3] > Turbo.tinlt) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to1.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to2.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to3.setForeground(Color.red);
            }
            if(Turbo.tt[4] > Turbo.tburner) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to4.setForeground(Color.red);
            }
            if(Turbo.tt[7] > Turbo.tnozr) {
                Turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to5.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to6.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to7.setForeground(Color.red);
            }
        }
        if(Turbo.fireflag == 1) {
            turbo.view.start();
        }
    }

    public void getGeo() {
                        /* determine geometric variables */
        double game;
        float fl1;
        int i1;

        if(turbo.entype <= 2) {          // turbine engines
            if(Turbo.afan < Turbo.acore) {
                Turbo.afan = Turbo.acore;
            }
            Turbo.a8max = .75 * Math.sqrt(Turbo.etr) / Turbo.epr; /* limits compressor face  */
                                           /*  mach number  to < .5   */
            if(Turbo.a8max > 1.0) {
                Turbo.a8max = 1.0;
            }
            if(Turbo.a8rat > Turbo.a8max) {
                Turbo.a8rat = Turbo.a8max;
                if(turbo.lunits <= 1) {
                    fl1 = turbo.filter3(Turbo.a8rat);
                    turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF3().setText(String.valueOf(fl1));
                    i1 = (int)(((Turbo.a8rat - Turbo.a8min) / (Turbo.a8max - Turbo.a8min)) * 1000.);
                    turbo.inputPanel.nozzlePanel.nozzleRightPanel.s3.setValue(i1);
                }
                if(turbo.lunits == 2) {
                    fl1 = turbo.filter3(100. * (Turbo.a8rat - Turbo.a8ref) / Turbo.a8ref);
                    turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF3().setText(String.valueOf(fl1));
                    i1 = (int)((((100. * (Turbo.a8rat - Turbo.a8ref) / Turbo.a8ref) + 10.0) / 20.0) * 1000.);
                    turbo.inputPanel.nozzlePanel.nozzleRightPanel.s3.setValue(i1);
                }
            }
          /*    dumb flightConditionsLowerPanel limit - a8 schedule */
            if(turbo.arsched == 0) {
                Turbo.a8rat = Turbo.a8max;
                fl1 = turbo.filter3(Turbo.a8rat);
                turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF3().setText(String.valueOf(fl1));
                i1 = (int)(((Turbo.a8rat - Turbo.a8min) / (Turbo.a8max - Turbo.a8min)) * 1000.);
                turbo.inputPanel.nozzlePanel.nozzleRightPanel.s3.setValue(i1);
            }
            Turbo.a8 = Turbo.a8rat * Turbo.acore;
            Turbo.a8d = Turbo.a8 * Turbo.prat[7] / Math.sqrt(Turbo.trat[7]);
         /* assumes choked a8 and a4 */
            Turbo.a4 = Turbo.a8 * Turbo.prat[5] * Turbo.prat[15] * Turbo.prat[7] /
                       Math.sqrt(Turbo.trat[7] * Turbo.trat[5] * Turbo.trat[15]);
            Turbo.a4p = Turbo.a8 * Turbo.prat[15] * Turbo.prat[7] / Math.sqrt(Turbo.trat[7] * Turbo.trat[15]);
            Turbo.ac = .9 * Turbo.a2;
        }

        if(turbo.entype == 3) {      // ramjets
            game = turbo.getGama(Turbo.tt[4], turbo.gamopt);
            if(turbo.athsched == 0) {   // scheduled throat area
                Turbo.arthd = turbo.getAir(Turbo.fsmach, Turbo.gama) * Math.sqrt(Turbo.etr) /
                              (turbo.getAir(1.0, game) * Turbo.epr * Turbo.prat[2]);
                if(Turbo.arthd < Turbo.arthmn) {
                    Turbo.arthd = Turbo.arthmn;
                }
                if(Turbo.arthd > Turbo.arthmx) {
                    Turbo.arthd = Turbo.arthmx;
                }
                fl1 = turbo.filter3(Turbo.arthd);
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getF3().setText(String.valueOf(fl1));
                i1 = (int)(((Turbo.arthd - Turbo.arthmn) / (Turbo.arthmx - Turbo.arthmn)) * 1000.);
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.s3.setValue(i1);
            }
            if(turbo.aexsched == 0) {   // scheduled exit area
                Turbo.mexit = Math.sqrt((2.0 / (game - 1.0)) * ((1.0 + .5 * (Turbo.gama - 1.0) * Turbo.fsmach * Turbo.fsmach)
                                                                * Math.pow((Turbo.epr * Turbo.prat[2]), (game - 1.0) / game) - 1.0));
                Turbo.arexitd = turbo.getAir(1.0, game) / turbo.getAir(Turbo.mexit, game);
                if(Turbo.arexitd < Turbo.arexmn) {
                    Turbo.arexitd = Turbo.arexmn;
                }
                if(Turbo.arexitd > Turbo.arexmx) {
                    Turbo.arexitd = Turbo.arexmx;
                }
                fl1 = turbo.filter3(Turbo.arexitd);
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getF4().setText(String.valueOf(fl1));
                i1 = (int)(((Turbo.arexitd - Turbo.arexmn) / (Turbo.arexmx - Turbo.arexmn)) * 1000.);
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.s4.setValue(i1);
            }
        }
    }
}    // end Solver
 
