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
        turbo.fireflag = 0;

        getFreeStream();

        getThermo();

        if(turbo.inflag == 0) {
            getGeo(); /* determine engine sizePanel and geometry */
        }
        if(turbo.inflag == 1) {
            if(turbo.entype < 3) {
                turbo.a8 = turbo.a8d * Math.sqrt(turbo.trat[7]) / turbo.prat[7];
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

        turbo.inptype = 0;
        turbo.siztype = 0;
        turbo.lunits = 0;
        turbo.lconv1 = 1.0;
        turbo.lconv2 = 1.0;
        turbo.fconv = 1.0;
        turbo.mconv1 = 1.0;
        turbo.pconv = 1.0;
        turbo.econv = 1.0;
        turbo.aconv = 1.0;
        turbo.bconv = 1.0;
        turbo.mconv2 = 1.0;
        turbo.dconv = 1.0;
        turbo.flconv = 1.0;
        turbo.econv2 = 1.0;
        turbo.tconv = 1.0;
        turbo.tref = 459.6;
        turbo.g0 = turbo.g0d = 32.2;

        turbo.counter = 0;
        turbo.showcom = 0;
        turbo.plttyp = 0;
        turbo.pltkeep = 0;
        turbo.entype = 0;
        turbo.inflag = 0;
        turbo.varflag = 0;
        turbo.pt2flag = 0;
        turbo.wtflag = 0;
        turbo.fireflag = 0;
        turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.u0d = 0.0;
        turbo.altd = 0.0;
        turbo.throtl = 100.;

        for (i = 0; i <= 19; ++i) {
            turbo.trat[i] = 1.0;
            turbo.tt[i] = 518.6;
            turbo.prat[i] = 1.0;
            turbo.pt[i] = 14.7;
            turbo.eta[i] = 1.0;
        }
        turbo.tt[4] = turbo.tt4 = turbo.tt4d = 2500.;
        turbo.tt[7] = turbo.tt7 = turbo.tt7d = 2500.;
        turbo.prat[3] = turbo.p3p2d = 8.0;
        turbo.prat[13] = turbo.p3fp2d = 2.0;
        turbo.byprat = 1.0;
        turbo.abflag = 0;

        turbo.fueltype = 0;
        turbo.fhvd = turbo.fhv = 18600.;
        turbo.a2d = turbo.a2 = turbo.acore = 2.0;
        turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
        turbo.ac = .9 * turbo.a2;
        turbo.a8rat = .35;
        turbo.a8 = .7;
        turbo.a8d = .40;
        turbo.arsched = 0;
        turbo.afan = 2.0;
        turbo.a4 = .418;

        turbo.athsched = 1;
        turbo.aexsched = 1;
        turbo.arthmn = 0.1;
        turbo.arthmx = 1.5;
        turbo.arexmn = 1.0;
        turbo.arexmx = 10.0;
        turbo.arthd = turbo.arth = .4;
        turbo.arexit = turbo.arexitd = 3.0;

        turbo.u0mt = 1500.;
        turbo.u0mr = 4500.;
        turbo.altmt = 60000.;
        turbo.altmr = 100000.;

        turbo.u0min = 0.0;
        turbo.u0max = turbo.u0mt;
        turbo.altmin = 0.0;
        turbo.altmax = turbo.altmt;
        turbo.thrmin = 30;
        turbo.thrmax = 100;
        turbo.etmin = .5;
        turbo.etmax = 1.0;
        turbo.cprmin = 1.0;
        turbo.cprmax = 50.0;
        turbo.bypmin = 0.0;
        turbo.bypmax = 10.0;
        turbo.fprmin = 1.0;
        turbo.fprmax = 2.0;
        turbo.t4min = 1000.0;
        turbo.t4max = 3200.0;
        turbo.t7min = 1000.0;
        turbo.t7max = 4000.0;
        turbo.a8min = 0.1;
        turbo.a8max = 0.4;
        turbo.a2min = .001;
        turbo.a2max = 50.;
        turbo.pt4max = 1.0;
        turbo.diamin = Math.sqrt(4.0 * turbo.a2min / 3.14159);
        turbo.diamax = Math.sqrt(4.0 * turbo.a2max / 3.14159);
        turbo.pmax = 20.0;
        turbo.tmin = -100.0 + turbo.tref;
        turbo.tmax = 100.0 + turbo.tref;
        turbo.vmn1 = turbo.u0min;
        turbo.vmx1 = turbo.u0max;
        turbo.vmn2 = turbo.altmin;
        turbo.vmx2 = turbo.altmax;
        turbo.vmn3 = turbo.thrmin;
        turbo.vmx3 = turbo.thrmax;
        turbo.vmn4 = turbo.arexmn;
        turbo.vmx4 = turbo.arexmx;

        turbo.xtrans = 125.0;
        turbo.ytrans = 115.0;
        turbo.factor = 35.;
        turbo.sldloc = 75;

        turbo.xtranp = 80.0;
        turbo.ytranp = 180.0;
        turbo.factp = 27.;
        turbo.sldplt = 130;

        turbo.weight = 1000.;
        turbo.minlt = 1;
        turbo.dinlt = 170.2;
        turbo.tinlt = 900.;
        turbo.mfan = 2;
        turbo.dfan = 293.02;
        turbo.tfan = 1500.;
        turbo.mcomp = 2;
        turbo.dcomp = 293.02;
        turbo.tcomp = 1500.;
        turbo.mburner = 4;
        turbo.dburner = 515.2;
        turbo.tburner = 2500.;
        turbo.mturbin = 4;
        turbo.dturbin = 515.2;
        turbo.tturbin = 2500.;
        turbo.mnozl = 3;
        turbo.dnozl = 515.2;
        turbo.tnozl = 2500.;
        turbo.mnozr = 5;
        turbo.dnozr = 515.2;
        turbo.tnozr = 4500.;
        turbo.ncflag = 0;
        turbo.ntflag = 0;

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
        turbo.fhsav = turbo.fhvd / turbo.flconv;
        turbo.t4sav = turbo.tt4d / turbo.tconv;
        turbo.t7sav = turbo.tt7d / turbo.tconv;
        turbo.p3sav = turbo.p3p2d;
        turbo.p3fsav = turbo.p3fp2d;
        turbo.bysav = turbo.byprat;
        turbo.acsav = turbo.acore;
        turbo.a2sav = turbo.a2d / turbo.aconv;
        turbo.a4sav = turbo.a4;
        turbo.a4psav = turbo.a4p;
        turbo.gamsav = turbo.gama;
        turbo.gamosav = turbo.gamopt;
        turbo.ptfsav = turbo.pt2flag;
        turbo.et2sav = turbo.eta[2];
        turbo.pr2sav = turbo.prat[2];
        turbo.pr4sav = turbo.prat[4];
        turbo.et3sav = turbo.eta[3];
        turbo.et4sav = turbo.eta[4];
        turbo.et5sav = turbo.eta[5];
        turbo.et7sav = turbo.eta[7];
        turbo.et13sav = turbo.eta[13];
        turbo.a8sav = turbo.a8d / turbo.aconv;
        turbo.a8mxsav = turbo.a8max / turbo.aconv;
        turbo.a8rtsav = turbo.a8rat;

        turbo.u0mxsav = turbo.u0max / turbo.lconv2;
        turbo.u0sav = turbo.u0d / turbo.lconv2;
        turbo.altsav = turbo.altd / turbo.lconv1;
        turbo.arssav = turbo.arsched;

        turbo.wtfsav = turbo.wtflag;
        turbo.wtsav = turbo.weight;
        turbo.minsav = turbo.minlt;
        turbo.dinsav = turbo.dinlt;
        turbo.tinsav = turbo.tinlt;
        turbo.mfnsav = turbo.mfan;
        turbo.dfnsav = turbo.dfan;
        turbo.tfnsav = turbo.tfan;
        turbo.mcmsav = turbo.mcomp;
        turbo.dcmsav = turbo.dcomp;
        turbo.tcmsav = turbo.tcomp;
        turbo.mbrsav = turbo.mburner;
        turbo.dbrsav = turbo.dburner;
        turbo.tbrsav = turbo.tburner;
        turbo.mtrsav = turbo.mturbin;
        turbo.dtrsav = turbo.dturbin;
        turbo.ttrsav = turbo.tturbin;
        turbo.mnlsav = turbo.mnozl;
        turbo.dnlsav = turbo.dnozl;
        turbo.tnlsav = turbo.tnozl;
        turbo.mnrsav = turbo.mnozr;
        turbo.dnrsav = turbo.dnozr;
        turbo.tnrsav = turbo.tnozr;
        turbo.ncsav = turbo.ncflag;
        turbo.ntsav = turbo.ntflag;

        if(turbo.entype == 3) {
            turbo.arthsav = turbo.athsched;
            turbo.arxsav = turbo.aexsched;
            turbo.artsav = turbo.arthd;
            turbo.arexsav = turbo.arexitd;
        }

        return;
    }

    public void loadMine() {

        turbo.entype = turbo.ensav;
        turbo.abflag = turbo.absav;
        turbo.fueltype = turbo.flsav;
        turbo.fhvd = turbo.fhv = turbo.fhsav;
        turbo.tt[4] = turbo.tt4 = turbo.tt4d = turbo.t4sav;
        turbo.tt[7] = turbo.tt7 = turbo.tt7d = turbo.t7sav;
        turbo.prat[3] = turbo.p3p2d = turbo.p3sav;
        turbo.prat[13] = turbo.p3fp2d = turbo.p3fsav;
        turbo.byprat = turbo.bysav;
        turbo.acore = turbo.acsav;
        turbo.afan = turbo.acore * (1.0 + turbo.byprat);
        turbo.a2d = turbo.a2 = turbo.a2sav;
        turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
        turbo.a4 = turbo.a4sav;
        turbo.a4p = turbo.a4psav;
        turbo.ac = .9 * turbo.a2;
        turbo.gama = turbo.gamsav;
        turbo.gamopt = turbo.gamosav;
        turbo.pt2flag = turbo.ptfsav;
        turbo.eta[2] = turbo.et2sav;
        turbo.prat[2] = turbo.pr2sav;
        turbo.prat[4] = turbo.pr4sav;
        turbo.eta[3] = turbo.et3sav;
        turbo.eta[4] = turbo.et4sav;
        turbo.eta[5] = turbo.et5sav;
        turbo.eta[7] = turbo.et7sav;
        turbo.eta[13] = turbo.et13sav;
        turbo.a8d = turbo.a8sav;
        turbo.a8max = turbo.a8mxsav;
        turbo.a8rat = turbo.a8rtsav;

        turbo.u0max = turbo.u0mxsav;
        turbo.u0d = turbo.u0sav;
        turbo.altd = turbo.altsav;
        turbo.arsched = turbo.arssav;

        turbo.wtflag = turbo.wtfsav;
        turbo.weight = turbo.wtsav;
        turbo.minlt = turbo.minsav;
        turbo.dinlt = turbo.dinsav;
        turbo.tinlt = turbo.tinsav;
        turbo.mfan = turbo.mfnsav;
        turbo.dfan = turbo.dfnsav;
        turbo.tfan = turbo.tfnsav;
        turbo.mcomp = turbo.mcmsav;
        turbo.dcomp = turbo.dcmsav;
        turbo.tcomp = turbo.tcmsav;
        turbo.mburner = turbo.mbrsav;
        turbo.dburner = turbo.dbrsav;
        turbo.tburner = turbo.tbrsav;
        turbo.mturbin = turbo.mtrsav;
        turbo.dturbin = turbo.dtrsav;
        turbo.tturbin = turbo.ttrsav;
        turbo.mnozl = turbo.mnlsav;
        turbo.dnozl = turbo.dnlsav;
        turbo.tnozl = turbo.tnlsav;
        turbo.mnozr = turbo.mnrsav;
        turbo.dnozr = turbo.dnrsav;
        turbo.tnozr = turbo.tnrsav;
        turbo.ncflag = turbo.ncsav;
        turbo.ntflag = turbo.ntsav;

        if(turbo.entype == 3) {
            turbo.athsched = turbo.arthsav;
            turbo.aexsched = turbo.arxsav;
            turbo.arthd = turbo.artsav;
            turbo.arexitd = turbo.arexsav;
        }

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadCF6() {

        turbo.entype = 2;
        turbo.abflag = 0;
        turbo.fueltype = 0;
        turbo.fhvd = turbo.fhv = 18600.;
        turbo.tt[4] = turbo.tt4 = turbo.tt4d = 2500.;
        turbo.tt[7] = turbo.tt7 = turbo.tt7d = 2500.;
        turbo.prat[3] = turbo.p3p2d = 21.86;
        turbo.prat[13] = turbo.p3fp2d = 1.745;
        turbo.byprat = 3.3;
        turbo.acore = 6.965;
        turbo.afan = turbo.acore * (1.0 + turbo.byprat);
        turbo.a2d = turbo.a2 = turbo.afan;
        turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
        turbo.a4 = .290;
        turbo.a4p = 1.131;
        turbo.ac = .9 * turbo.a2;
        turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        turbo.eta[2] = 1.0;
        turbo.prat[2] = 1.0;
        turbo.prat[4] = 1.0;
        turbo.eta[3] = .959;
        turbo.eta[4] = .984;
        turbo.eta[5] = .982;
        turbo.eta[7] = 1.0;
        turbo.eta[13] = 1.0;
        turbo.a8d = 2.436;
        turbo.a8max = .35;
        turbo.a8rat = .35;

        turbo.u0max = turbo.u0mt;
        turbo.u0d = 0.0;
        turbo.altmax = turbo.altmt;
        turbo.altd = 0.0;
        turbo.arsched = 0;

        turbo.wtflag = 0;
        turbo.weight = 8229.;
        turbo.minlt = 1;
        turbo.dinlt = 170.;
        turbo.tinlt = 900.;
        turbo.mfan = 2;
        turbo.dfan = 293.;
        turbo.tfan = 1500.;
        turbo.mcomp = 0;
        turbo.dcomp = 293.;
        turbo.tcomp = 1600.;
        turbo.mburner = 4;
        turbo.dburner = 515.;
        turbo.tburner = 2500.;
        turbo.mturbin = 4;
        turbo.dturbin = 515.;
        turbo.tturbin = 2500.;
        turbo.mnozl = 3;
        turbo.dnozl = 515.;
        turbo.tnozl = 2500.;
        turbo.ncflag = 0;
        turbo.ntflag = 0;

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadJ85() {

        turbo.entype = 0;
        turbo.abflag = 0;
        turbo.fueltype = 0;
        turbo.fhvd = turbo.fhv = 18600.;
        turbo.tt[4] = turbo.tt4 = turbo.tt4d = 2260.;
        turbo.tt[7] = turbo.tt7 = turbo.tt7d = 4000.;
        turbo.prat[3] = turbo.p3p2d = 8.3;
        turbo.prat[13] = turbo.p3fp2d = 1.0;
        turbo.byprat = 0.0;
        turbo.a2d = turbo.a2 = turbo.acore = 1.753;
        turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
        turbo.afan = turbo.acore * (1.0 + turbo.byprat);
        turbo.a4 = .323;
        turbo.a4p = .818;
        turbo.ac = .9 * turbo.a2;
        turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        turbo.eta[2] = 1.0;
        turbo.prat[2] = 1.0;
        turbo.prat[4] = .85;
        turbo.eta[3] = .822;
        turbo.eta[4] = .982;
        turbo.eta[5] = .882;
        turbo.eta[7] = .978;
        turbo.eta[13] = 1.0;
        turbo.a8d = .818;
        turbo.a8max = .467;
        turbo.a8rat = .467;

        turbo.u0max = turbo.u0mt;
        turbo.u0d = 0.0;
        turbo.altmax = turbo.altmt;
        turbo.altd = 0.0;
        turbo.arsched = 1;

        turbo.wtflag = 0;
        turbo.weight = 561.;
        turbo.minlt = 1;
        turbo.dinlt = 170.;
        turbo.tinlt = 900.;
        turbo.mfan = 2;
        turbo.dfan = 293.;
        turbo.tfan = 1500.;
        turbo.mcomp = 2;
        turbo.dcomp = 293.;
        turbo.tcomp = 1500.;
        turbo.mburner = 4;
        turbo.dburner = 515.;
        turbo.tburner = 2500.;
        turbo.mturbin = 4;
        turbo.dturbin = 515.;
        turbo.tturbin = 2500.;
        turbo.mnozl = 5;
        turbo.dnozl = 600.;
        turbo.tnozl = 4100.;
        turbo.ncflag = 0;
        turbo.ntflag = 0;

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadF100() {

        turbo.entype = 1;
        turbo.abflag = 1;
        turbo.fueltype = 0;
        turbo.fhvd = turbo.fhv = 18600.;
        turbo.tt[4] = turbo.tt4 = turbo.tt4d = 2499.;
        turbo.tt[7] = turbo.tt7 = turbo.tt7d = 3905.;
        turbo.prat[3] = turbo.p3p2d = 20.04;
        turbo.prat[13] = turbo.p3fp2d = 1.745;
        turbo.byprat = 0.0;
        turbo.a2d = turbo.a2 = turbo.acore = 6.00;
        turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
        turbo.afan = turbo.acore * (1.0 + turbo.byprat);
        turbo.a4 = .472;
        turbo.a4p = 1.524;
        turbo.ac = .9 * turbo.a2;
        turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        turbo.eta[2] = 1.0;
        turbo.prat[2] = 1.0;
        turbo.prat[4] = 1.0;
        turbo.eta[3] = .959;
        turbo.eta[4] = .984;
        turbo.eta[5] = .982;
        turbo.eta[7] = .92;
        turbo.eta[13] = 1.0;
        turbo.a8d = 1.524;
        turbo.a8max = .335;
        turbo.a8rat = .335;

        turbo.u0max = turbo.u0mt;
        turbo.u0d = 0.0;
        turbo.altmax = turbo.altmt;
        turbo.altd = 0.0;
        turbo.arsched = 0;

        turbo.wtflag = 0;
        turbo.weight = 3875.;
        turbo.minlt = 1;
        turbo.dinlt = 170.;
        turbo.tinlt = 900.;
        turbo.mfan = 2;
        turbo.dfan = 293.;
        turbo.tfan = 1500.;
        turbo.mcomp = 2;
        turbo.dcomp = 293.;
        turbo.tcomp = 1500.;
        turbo.mburner = 4;
        turbo.dburner = 515.;
        turbo.tburner = 2500.;
        turbo.mturbin = 4;
        turbo.dturbin = 515.;
        turbo.tturbin = 2500.;
        turbo.mnozl = 5;
        turbo.dnozl = 400.2;
        turbo.tnozl = 4100.;
        turbo.ncflag = 0;
        turbo.ntflag = 0;

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void loadRamj() {

        turbo.entype = 3;
        turbo.athsched = 1;
        turbo.aexsched = 1;
        turbo.arthd = .4;
        turbo.arexitd = 3.0;
        turbo.abflag = 0;
        turbo.fueltype = 0;
        turbo.fhvd = turbo.fhv = 18600.;
        turbo.tt[4] = turbo.tt4 = turbo.tt4d = 4000.;
        turbo.t4max = 4500.;
        turbo.tt[7] = turbo.tt7 = turbo.tt7d = 4000.;
        turbo.prat[3] = turbo.p3p2d = 1.0;
        turbo.prat[13] = turbo.p3fp2d = 1.0;
        turbo.byprat = 0.0;
        turbo.a2d = turbo.a2 = turbo.acore = 1.753;
        turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
        turbo.afan = turbo.acore * (1.0 + turbo.byprat);
        turbo.a4 = .323;
        turbo.a4p = .818;
        turbo.ac = .9 * turbo.a2;
        turbo.gama = 1.4;
        turbo.gamopt = 1;
        turbo.pt2flag = 0;
        turbo.eta[2] = 1.0;
        turbo.prat[2] = 1.0;
        turbo.prat[4] = 1.0;
        turbo.eta[3] = 1.0;
        turbo.eta[4] = .982;
        turbo.eta[5] = 1.0;
        turbo.eta[7] = 1.0;
        turbo.eta[13] = 1.0;
        turbo.a8 = turbo.a8d = 2.00;
        turbo.a8max = 15.;
        turbo.a8rat = 4.0;
        turbo.a7 = .50;

        turbo.u0max = turbo.u0mr;
        turbo.u0d = 2200.0;
        turbo.altmax = turbo.altmr;
        turbo.altd = 10000.0;
        turbo.arsched = 0;

        turbo.wtflag = 0;
        turbo.weight = 976.;
        turbo.minlt = 2;
        turbo.dinlt = 293.;
        turbo.tinlt = 1500.;
        turbo.mfan = 2;
        turbo.dfan = 293.;
        turbo.tfan = 1500.;
        turbo.mcomp = 2;
        turbo.dcomp = 293.;
        turbo.tcomp = 1500.;
        turbo.mburner = 7;
        turbo.dburner = 515.;
        turbo.tburner = 4500.;
        turbo.mturbin = 4;
        turbo.dturbin = 515.;
        turbo.tturbin = 2500.;
        turbo.mnozr = 5;
        turbo.dnozr = 515.2;
        turbo.tnozr = 4500.;
        turbo.ncflag = 0;
        turbo.ntflag = 0;

        turbo.flightConditionsPanel.setPanl();
        return;
    }

    public void getFreeStream() {
        turbo.rgas = 1718.;                /* ft2/sec2 R */
        if(turbo.inptype >= 2) {
            turbo.ps0 = turbo.ps0 * 144.;
        }
        if(turbo.inptype <= 1) {            /* input altitude */
            turbo.alt = turbo.altd / turbo.lconv1;
            if(turbo.alt < 36152.) {
                turbo.ts0 = 518.6 - 3.56 * turbo.alt / 1000.;
                turbo.ps0 = 2116. * Math.pow(turbo.ts0 / 518.6, 5.256);
            }
            if(turbo.alt >= 36152. && turbo.alt <= 82345.) {   // Stratosphere
                turbo.ts0 = 389.98;
                turbo.ps0 = 2116. * .2236 *
                            Math.exp((36000. - turbo.alt) / (53.35 * 389.98));
            }
            if(turbo.alt >= 82345.) {
                turbo.ts0 = 389.98 + 1.645 * (turbo.alt - 82345) / 1000.;
                turbo.ps0 = 2116. * .02456 * Math.pow(turbo.ts0 / 389.98, -11.388);
            }
        }
        turbo.a0 = Math.sqrt(turbo.gama * turbo.rgas * turbo.ts0);             /* speed of sound ft/sec */
        if(turbo.inptype == 0 || turbo.inptype == 2) {           /* input speed  */
            turbo.u0 = turbo.u0d / turbo.lconv2 * 5280. / 3600.;           /* airspeed ft/sec */
            turbo.fsmach = turbo.u0 / turbo.a0;
            turbo.q0 = turbo.gama / 2.0 * turbo.fsmach * turbo.fsmach * turbo.ps0;
        }
        if(turbo.inptype == 1 || turbo.inptype == 3) {            /* input mach */
            turbo.u0 = turbo.fsmach * turbo.a0;
            turbo.u0d = turbo.u0 * turbo.lconv2 / 5280. * 3600.;      /* airspeed ft/sec */
            turbo.q0 = turbo.gama / 2.0 * turbo.fsmach * turbo.fsmach * turbo.ps0;
        }
        if(turbo.u0 > .0001) {
            turbo.rho0 = turbo.q0 / (turbo.u0 * turbo.u0);
        } else {
            turbo.rho0 = 1.0;
        }

        turbo.tt[0] = turbo.ts0 * (1.0 + .5 * (turbo.gama - 1.0) * turbo.fsmach * turbo.fsmach);
        turbo.pt[0] = turbo.ps0 * Math.pow(turbo.tt[0] / turbo.ts0, turbo.gama / (turbo.gama - 1.0));
        turbo.ps0 = turbo.ps0 / 144.;
        turbo.pt[0] = turbo.pt[0] / 144.;
        turbo.cpair = turbo.getCp(turbo.tt[0], turbo.gamopt);              /*BTU/lbm R */
        turbo.tsout = turbo.ts0;
        turbo.psout = turbo.ps0;

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

        int i1;
                                     /*   inletPanel recovery  */
        if(turbo.pt2flag == 0) {                    /*     mil spec      */
            if(turbo.fsmach > 1.0) {          /* supersonic */
                turbo.prat[2] = 1.0 - .075 * Math.pow(turbo.fsmach - 1.0, 1.35);
            } else {
                turbo.prat[2] = 1.0;
            }
            turbo.eta[2] = turbo.prat[2];
            turbo.inputPanel.inletPanel.inletLeftPanel.getF1().setText(String.format("%.3f", turbo.prat[2]));
            i1 = (int)(((turbo.prat[2] - turbo.etmin) / (turbo.etmax - turbo.etmin)) * 1000.);
            turbo.inputPanel.inletPanel.inletRightPanel.s1.setValue(i1);
        } else {                       /* enter value */
            turbo.prat[2] = turbo.eta[2];
        }
                           /* protection for overwriting input */
        if(turbo.eta[3] < .5) {
            turbo.eta[3] = .5;
        }
        if(turbo.eta[5] < .5) {
            turbo.eta[5] = .5;
        }
        turbo.trat[7] = 1.0;
        turbo.prat[7] = 1.0;
        turbo.tt[2] = turbo.tt[1] = turbo.tt[0];
        turbo.pt[1] = turbo.pt[0];
        turbo.gam[2] = turbo.getGama(turbo.tt[2], turbo.gamopt);
        turbo.cp[2] = turbo.getCp(turbo.tt[2], turbo.gamopt);
        turbo.pt[2] = turbo.pt[1] * turbo.prat[2];
    /* design - p3p2 specified - tt4 specified */
        if(turbo.inflag == 0) {

            if(turbo.entype <= 1) {              /* turbojet */
                turbo.prat[3] = turbo.p3p2d;                      /* core compressor */
                if(turbo.prat[3] < .5) {
                    turbo.prat[3] = .5;
                }
                delhc = (turbo.cp[2] * turbo.tt[2] / turbo.eta[3]) *
                        (Math.pow(turbo.prat[3], (turbo.gam[2] - 1.0) / turbo.gam[2]) - 1.0);
                deltc = delhc / turbo.cp[2];
                turbo.pt[3] = turbo.pt[2] * turbo.prat[3];
                turbo.tt[3] = turbo.tt[2] + deltc;
                turbo.trat[3] = turbo.tt[3] / turbo.tt[2];
                turbo.gam[3] = turbo.getGama(turbo.tt[3], turbo.gamopt);
                turbo.cp[3] = turbo.getCp(turbo.tt[3], turbo.gamopt);
                turbo.tt[4] = turbo.tt4 * turbo.throtl / 100.0;
                turbo.gam[4] = turbo.getGama(turbo.tt[4], turbo.gamopt);
                turbo.cp[4] = turbo.getCp(turbo.tt[4], turbo.gamopt);
                turbo.trat[4] = turbo.tt[4] / turbo.tt[3];
                turbo.pt[4] = turbo.pt[3] * turbo.prat[4];
                delhht = delhc;
                deltht = delhht / turbo.cp[4];
                turbo.tt[5] = turbo.tt[4] - deltht;
                turbo.gam[5] = turbo.getGama(turbo.tt[5], turbo.gamopt);
                turbo.cp[5] = turbo.getCp(turbo.tt[5], turbo.gamopt);
                turbo.trat[5] = turbo.tt[5] / turbo.tt[4];
                turbo.prat[5] = Math.pow((1.0 - delhht / turbo.cp[4] / turbo.tt[4] / turbo.eta[5]),
                                         (turbo.gam[4] / (turbo.gam[4] - 1.0)));
                turbo.pt[5] = turbo.pt[4] * turbo.prat[5];
                                    /* fanPanel conditions */
                turbo.prat[13] = 1.0;
                turbo.trat[13] = 1.0;
                turbo.tt[13] = turbo.tt[2];
                turbo.pt[13] = turbo.pt[2];
                turbo.gam[13] = turbo.gam[2];
                turbo.cp[13] = turbo.cp[2];
                turbo.prat[15] = 1.0;
                turbo.pt[15] = turbo.pt[5];
                turbo.trat[15] = 1.0;
                turbo.tt[15] = turbo.tt[5];
                turbo.gam[15] = turbo.gam[5];
                turbo.cp[15] = turbo.cp[5];
            }

            if(turbo.entype == 2) {                         /* turbofan */
                turbo.prat[13] = turbo.p3fp2d;
                if(turbo.prat[13] < .5) {
                    turbo.prat[13] = .5;
                }
                delhf = (turbo.cp[2] * turbo.tt[2] / turbo.eta[13]) *
                        (Math.pow(turbo.prat[13], (turbo.gam[2] - 1.0) / turbo.gam[2]) - 1.0);
                deltf = delhf / turbo.cp[2];
                turbo.tt[13] = turbo.tt[2] + deltf;
                turbo.pt[13] = turbo.pt[2] * turbo.prat[13];
                turbo.trat[13] = turbo.tt[13] / turbo.tt[2];
                turbo.gam[13] = turbo.getGama(turbo.tt[13], turbo.gamopt);
                turbo.cp[13] = turbo.getCp(turbo.tt[13], turbo.gamopt);
                turbo.prat[3] = turbo.p3p2d;                      /* core compressor */
                if(turbo.prat[3] < .5) {
                    turbo.prat[3] = .5;
                }
                delhc = (turbo.cp[13] * turbo.tt[13] / turbo.eta[3]) *
                        (Math.pow(turbo.prat[3], (turbo.gam[13] - 1.0) / turbo.gam[13]) - 1.0);
                deltc = delhc / turbo.cp[13];
                turbo.tt[3] = turbo.tt[13] + deltc;
                turbo.pt[3] = turbo.pt[13] * turbo.prat[3];
                turbo.trat[3] = turbo.tt[3] / turbo.tt[13];
                turbo.gam[3] = turbo.getGama(turbo.tt[3], turbo.gamopt);
                turbo.cp[3] = turbo.getCp(turbo.tt[3], turbo.gamopt);
                turbo.tt[4] = turbo.tt4 * turbo.throtl / 100.0;
                turbo.pt[4] = turbo.pt[3] * turbo.prat[4];
                turbo.gam[4] = turbo.getGama(turbo.tt[4], turbo.gamopt);
                turbo.cp[4] = turbo.getCp(turbo.tt[4], turbo.gamopt);
                turbo.trat[4] = turbo.tt[4] / turbo.tt[3];
                delhht = delhc;
                deltht = delhht / turbo.cp[4];
                turbo.tt[5] = turbo.tt[4] - deltht;
                turbo.gam[5] = turbo.getGama(turbo.tt[5], turbo.gamopt);
                turbo.cp[5] = turbo.getCp(turbo.tt[5], turbo.gamopt);
                turbo.trat[5] = turbo.tt[5] / turbo.tt[4];
                turbo.prat[5] = Math.pow((1.0 - delhht / turbo.cp[4] / turbo.tt[4] / turbo.eta[5]),
                                         (turbo.gam[4] / (turbo.gam[4] - 1.0)));
                turbo.pt[5] = turbo.pt[4] * turbo.prat[5];
                delhlt = (1.0 + turbo.byprat) * delhf;
                deltlt = delhlt / turbo.cp[5];
                turbo.tt[15] = turbo.tt[5] - deltlt;
                turbo.gam[15] = turbo.getGama(turbo.tt[15], turbo.gamopt);
                turbo.cp[15] = turbo.getCp(turbo.tt[15], turbo.gamopt);
                turbo.trat[15] = turbo.tt[15] / turbo.tt[5];
                turbo.prat[15] = Math.pow((1.0 - delhlt / turbo.cp[5] / turbo.tt[5] / turbo.eta[5]),
                                          (turbo.gam[5] / (turbo.gam[5] - 1.0)));
                turbo.pt[15] = turbo.pt[5] * turbo.prat[15];
            }

            if(turbo.entype == 3) {              /* ramjet */
                turbo.prat[3] = 1.0;
                turbo.pt[3] = turbo.pt[2] * turbo.prat[3];
                turbo.tt[3] = turbo.tt[2];
                turbo.trat[3] = 1.0;
                turbo.gam[3] = turbo.getGama(turbo.tt[3], turbo.gamopt);
                turbo.cp[3] = turbo.getCp(turbo.tt[3], turbo.gamopt);
                turbo.tt[4] = turbo.tt4 * turbo.throtl / 100.0;
                turbo.gam[4] = turbo.getGama(turbo.tt[4], turbo.gamopt);
                turbo.cp[4] = turbo.getCp(turbo.tt[4], turbo.gamopt);
                turbo.trat[4] = turbo.tt[4] / turbo.tt[3];
                turbo.pt[4] = turbo.pt[3] * turbo.prat[4];
                turbo.tt[5] = turbo.tt[4];
                turbo.gam[5] = turbo.getGama(turbo.tt[5], turbo.gamopt);
                turbo.cp[5] = turbo.getCp(turbo.tt[5], turbo.gamopt);
                turbo.trat[5] = 1.0;
                turbo.prat[5] = 1.0;
                turbo.pt[5] = turbo.pt[4];
                                    /* fanPanel conditions */
                turbo.prat[13] = 1.0;
                turbo.trat[13] = 1.0;
                turbo.tt[13] = turbo.tt[2];
                turbo.pt[13] = turbo.pt[2];
                turbo.gam[13] = turbo.gam[2];
                turbo.cp[13] = turbo.cp[2];
                turbo.prat[15] = 1.0;
                turbo.pt[15] = turbo.pt[5];
                turbo.trat[15] = 1.0;
                turbo.tt[15] = turbo.tt[5];
                turbo.gam[15] = turbo.gam[5];
                turbo.cp[15] = turbo.cp[5];
            }

            turbo.tt[7] = turbo.tt7;
        }
         /* analysis -assume flow choked at both turbine entrances */
                              /* and nozzle throat ... then*/
        else {
            turbo.tt[4] = turbo.tt4 * turbo.throtl / 100.0;
            turbo.gam[4] = turbo.getGama(turbo.tt[4], turbo.gamopt);
            turbo.cp[4] = turbo.getCp(turbo.tt[4], turbo.gamopt);
            if(turbo.a4 < .02) {
                turbo.a4 = .02;
            }

            if(turbo.entype <= 1) {              /* turbojet */
                dela = .2;                           /* iterate to get t5t4 */
                turbo.trat[5] = 1.0;
                t5t4n = .5;
                itcount = 0;
                while (Math.abs(dela) > .001 && itcount < 20) {
                    ++itcount;
                    delan = turbo.a8d / turbo.a4 - Math.sqrt(t5t4n) *
                                                   Math.pow((1.0 - (1.0 / turbo.eta[5]) * (1.0 - t5t4n)),
                                                            -turbo.gam[4] / (turbo.gam[4] - 1.0));
                    deriv = (delan - dela) / (t5t4n - turbo.trat[5]);
                    dela = delan;
                    turbo.trat[5] = t5t4n;
                    t5t4n = turbo.trat[5] - dela / deriv;
                }
                turbo.tt[5] = turbo.tt[4] * turbo.trat[5];
                turbo.gam[5] = turbo.getGama(turbo.tt[5], turbo.gamopt);
                turbo.cp[5] = turbo.getCp(turbo.tt[5], turbo.gamopt);
                deltht = turbo.tt[5] - turbo.tt[4];
                delhht = turbo.cp[4] * deltht;
                turbo.prat[5] = Math.pow((1.0 - (1.0 / turbo.eta[5]) * (1.0 - turbo.trat[5])),
                                         turbo.gam[4] / (turbo.gam[4] - 1.0));
                delhc = delhht;           /* compressor work */
                deltc = -delhc / turbo.cp[2];
                turbo.tt[3] = turbo.tt[2] + deltc;
                turbo.gam[3] = turbo.getGama(turbo.tt[3], turbo.gamopt);
                turbo.cp[3] = turbo.getCp(turbo.tt[3], turbo.gamopt);
                turbo.trat[3] = turbo.tt[3] / turbo.tt[2];
                turbo.prat[3] = Math.pow((1.0 + turbo.eta[3] * (turbo.trat[3] - 1.0)),
                                         turbo.gam[2] / (turbo.gam[2] - 1.0));
                turbo.trat[4] = turbo.tt[4] / turbo.tt[3];
                turbo.pt[3] = turbo.pt[2] * turbo.prat[3];
                turbo.pt[4] = turbo.pt[3] * turbo.prat[4];
                turbo.pt[5] = turbo.pt[4] * turbo.prat[5];
                                    /* fanPanel conditions */
                turbo.prat[13] = 1.0;
                turbo.trat[13] = 1.0;
                turbo.tt[13] = turbo.tt[2];
                turbo.pt[13] = turbo.pt[2];
                turbo.gam[13] = turbo.gam[2];
                turbo.cp[13] = turbo.cp[2];
                turbo.prat[15] = 1.0;
                turbo.pt[15] = turbo.pt[5];
                turbo.trat[15] = 1.0;
                turbo.tt[15] = turbo.tt[5];
                turbo.gam[15] = turbo.gam[5];
                turbo.cp[15] = turbo.cp[5];
            }

            if(turbo.entype == 2) {                        /*  turbofan */
                dela = .2;                           /* iterate to get t5t4 */
                turbo.trat[5] = 1.0;
                t5t4n = .5;
                itcount = 0;
                while (Math.abs(dela) > .001 && itcount < 20) {
                    ++itcount;
                    delan = turbo.a4p / turbo.a4 - Math.sqrt(t5t4n) *
                                                   Math.pow((1.0 - (1.0 / turbo.eta[5]) * (1.0 - t5t4n)),
                                                            -turbo.gam[4] / (turbo.gam[4] - 1.0));
                    deriv = (delan - dela) / (t5t4n - turbo.trat[5]);
                    dela = delan;
                    turbo.trat[5] = t5t4n;
                    t5t4n = turbo.trat[5] - dela / deriv;
                }
                turbo.tt[5] = turbo.tt[4] * turbo.trat[5];
                turbo.gam[5] = turbo.getGama(turbo.tt[5], turbo.gamopt);
                turbo.cp[5] = turbo.getCp(turbo.tt[5], turbo.gamopt);
                deltht = turbo.tt[5] - turbo.tt[4];
                delhht = turbo.cp[4] * deltht;
                turbo.prat[5] = Math.pow((1.0 - (1.0 / turbo.eta[5]) * (1.0 - turbo.trat[5])),
                                         turbo.gam[4] / (turbo.gam[4] - 1.0));
                                   /* iterate to get t15t14 */
                dela = .2;
                turbo.trat[15] = 1.0;
                t5t4n = .5;
                itcount = 0;
                while (Math.abs(dela) > .001 && itcount < 20) {
                    ++itcount;
                    delan = turbo.a8d / turbo.a4p - Math.sqrt(t5t4n) *
                                                    Math.pow((1.0 - (1.0 / turbo.eta[5]) * (1.0 - t5t4n)),
                                                             -turbo.gam[5] / (turbo.gam[5] - 1.0));
                    deriv = (delan - dela) / (t5t4n - turbo.trat[15]);
                    dela = delan;
                    turbo.trat[15] = t5t4n;
                    t5t4n = turbo.trat[15] - dela / deriv;
                }
                turbo.tt[15] = turbo.tt[5] * turbo.trat[15];
                turbo.gam[15] = turbo.getGama(turbo.tt[15], turbo.gamopt);
                turbo.cp[15] = turbo.getCp(turbo.tt[15], turbo.gamopt);
                deltlt = turbo.tt[15] - turbo.tt[5];
                delhlt = turbo.cp[5] * deltlt;
                turbo.prat[15] = Math.pow((1.0 - (1.0 / turbo.eta[5]) * (1.0 - turbo.trat[15])),
                                          turbo.gam[5] / (turbo.gam[5] - 1.0));
                turbo.byprat = turbo.afan / turbo.acore - 1.0;
                delhf = delhlt / (1.0 + turbo.byprat);              /* fanPanel work */
                deltf = -delhf / turbo.cp[2];
                turbo.tt[13] = turbo.tt[2] + deltf;
                turbo.gam[13] = turbo.getGama(turbo.tt[13], turbo.gamopt);
                turbo.cp[13] = turbo.getCp(turbo.tt[13], turbo.gamopt);
                turbo.trat[13] = turbo.tt[13] / turbo.tt[2];
                turbo.prat[13] = Math.pow((1.0 + turbo.eta[13] * (turbo.trat[13] - 1.0)),
                                          turbo.gam[2] / (turbo.gam[2] - 1.0));
                delhc = delhht;                         /* compressor work */
                deltc = -delhc / turbo.cp[13];
                turbo.tt[3] = turbo.tt[13] + deltc;
                turbo.gam[3] = turbo.getGama(turbo.tt[3], turbo.gamopt);
                turbo.cp[3] = turbo.getCp(turbo.tt[3], turbo.gamopt);
                turbo.trat[3] = turbo.tt[3] / turbo.tt[13];
                turbo.prat[3] = Math.pow((1.0 + turbo.eta[3] * (turbo.trat[3] - 1.0)),
                                         turbo.gam[13] / (turbo.gam[13] - 1.0));
                turbo.trat[4] = turbo.tt[4] / turbo.tt[3];
                turbo.pt[13] = turbo.pt[2] * turbo.prat[13];
                turbo.pt[3] = turbo.pt[13] * turbo.prat[3];
                turbo.pt[4] = turbo.pt[3] * turbo.prat[4];
                turbo.pt[5] = turbo.pt[4] * turbo.prat[5];
                turbo.pt[15] = turbo.pt[5] * turbo.prat[15];
            }

            if(turbo.entype == 3) {              /* ramjet */
                turbo.prat[3] = 1.0;
                turbo.pt[3] = turbo.pt[2] * turbo.prat[3];
                turbo.tt[3] = turbo.tt[2];
                turbo.trat[3] = 1.0;
                turbo.gam[3] = turbo.getGama(turbo.tt[3], turbo.gamopt);
                turbo.cp[3] = turbo.getCp(turbo.tt[3], turbo.gamopt);
                turbo.tt[4] = turbo.tt4 * turbo.throtl / 100.0;
                turbo.trat[4] = turbo.tt[4] / turbo.tt[3];
                turbo.pt[4] = turbo.pt[3] * turbo.prat[4];
                turbo.tt[5] = turbo.tt[4];
                turbo.gam[5] = turbo.getGama(turbo.tt[5], turbo.gamopt);
                turbo.cp[5] = turbo.getCp(turbo.tt[5], turbo.gamopt);
                turbo.trat[5] = 1.0;
                turbo.prat[5] = 1.0;
                turbo.pt[5] = turbo.pt[4];
                                     /* fanPanel conditions */
                turbo.prat[13] = 1.0;
                turbo.trat[13] = 1.0;
                turbo.tt[13] = turbo.tt[2];
                turbo.pt[13] = turbo.pt[2];
                turbo.gam[13] = turbo.gam[2];
                turbo.cp[13] = turbo.cp[2];
                turbo.prat[15] = 1.0;
                turbo.pt[15] = turbo.pt[5];
                turbo.trat[15] = 1.0;
                turbo.tt[15] = turbo.tt[5];
                turbo.gam[15] = turbo.gam[5];
                turbo.cp[15] = turbo.cp[5];
            }

            if(turbo.abflag == 1) {
                turbo.tt[7] = turbo.tt7;
            }
        }

        turbo.prat[6] = 1.0;
        turbo.pt[6] = turbo.pt[15];
        turbo.trat[6] = 1.0;
        turbo.tt[6] = turbo.tt[15];
        turbo.gam[6] = turbo.getGama(turbo.tt[6], turbo.gamopt);
        turbo.cp[6] = turbo.getCp(turbo.tt[6], turbo.gamopt);
        if(turbo.abflag > 0) {                   /* afterburner */
            turbo.trat[7] = turbo.tt[7] / turbo.tt[6];
            m5 = turbo.getMach(0, turbo.getAir(1.0, turbo.gam[5]) * turbo.a4 / turbo.acore, turbo.gam[5]);
            turbo.prat[7] = turbo.getRayleighLoss(m5, turbo.trat[7], turbo.tt[6]);
        }
        turbo.tt[7] = turbo.tt[6] * turbo.trat[7];
        turbo.pt[7] = turbo.pt[6] * turbo.prat[7];
        turbo.gam[7] = turbo.getGama(turbo.tt[7], turbo.gamopt);
        turbo.cp[7] = turbo.getCp(turbo.tt[7], turbo.gamopt);
             /* engine press ratio EPR*/
        turbo.epr = turbo.prat[7] * turbo.prat[15] * turbo.prat[5] * turbo.prat[4] * turbo.prat[3] * turbo.prat[13];
          /* engine temp ratio ETR */
        turbo.etr = turbo.trat[7] * turbo.trat[15] * turbo.trat[5] * turbo.trat[4] * turbo.trat[3] * turbo.trat[13];
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
        rg = turbo.cpair * (turbo.gama - 1.0) / turbo.gama;
        cp3 = turbo.getCp(turbo.tt[3], turbo.gamopt);                  /*BTU/lbm R */
        turbo.g0 = 32.2;
        turbo.ues = 0.0;
        game = turbo.getGama(turbo.tt[5], turbo.gamopt);
        fac1 = (game - 1.0) / game;
        cpe = turbo.getCp(turbo.tt[5], turbo.gamopt);
        if(turbo.eta[7] < .8) {
            turbo.eta[7] = .8;    /* protection during overwriting */
        }
        if(turbo.eta[4] < .8) {
            turbo.eta[4] = .8;
        }

   /*  specific net thrust  - thrust / (g0*airflow) -   lbf/lbm/sec  */
        // turbine engine core
        if(turbo.entype <= 2) {
                        /* airflow determined at choked nozzle exit */
            turbo.pt[8] = turbo.epr * turbo.prat[2] * turbo.pt[0];
            turbo.eair = turbo.getAir(1.0, game) * 144. * turbo.a8 * turbo.pt[8] / 14.7 /
                         Math.sqrt(turbo.etr * turbo.tt[0] / 518.);
            turbo.m2 = turbo.getMach(0, turbo.eair * Math.sqrt(turbo.tt[0] / 518.) /
                                        (turbo.prat[2] * turbo.pt[0] / 14.7 * turbo.acore * 144.), turbo.gama);
            turbo.npr = turbo.pt[8] / turbo.ps0;
            turbo.uexit = Math.sqrt(2.0 * turbo.rgas / fac1 * turbo.etr * turbo.tt[0] * turbo.eta[7] *
                                    (1.0 - Math.pow(1.0 / turbo.npr, fac1)));
            if(turbo.npr <= 1.893) {
                turbo.pexit = turbo.ps0;
            } else {
                turbo.pexit = .52828 * turbo.pt[8];
            }
            turbo.fgros = (turbo.uexit + (turbo.pexit - turbo.ps0) * 144. * turbo.a8 / turbo.eair) / turbo.g0;
        }

        // turbo fanPanel -- added terms for fanPanel flow
        if(turbo.entype == 2) {
            fac1 = (turbo.gama - 1.0) / turbo.gama;
            turbo.snpr = turbo.pt[13] / turbo.ps0;
            turbo.ues = Math.sqrt(2.0 * turbo.rgas / fac1 * turbo.tt[13] * turbo.eta[7] *
                                  (1.0 - Math.pow(1.0 / turbo.snpr, fac1)));
            turbo.m2 = turbo.getMach(0, turbo.eair * (1.0 + turbo.byprat) * Math.sqrt(turbo.tt[0] / 518.) /
                                        (turbo.prat[2] * turbo.pt[0] / 14.7 * turbo.afan * 144.), turbo.gama);
            if(turbo.snpr <= 1.893) {
                turbo.pfexit = turbo.ps0;
            } else {
                turbo.pfexit = .52828 * turbo.pt[13];
            }
            turbo.fgros = turbo.fgros + (turbo.byprat * turbo.ues + (turbo.pfexit - turbo.ps0) * 144. * turbo.byprat * turbo.acore / turbo.eair) / turbo.g0;
        }

        // ramjets
        if(turbo.entype == 3) {
                       /* airflow determined at nozzle throat */
            turbo.eair = turbo.getAir(1.0, game) * 144.0 * turbo.a2 * turbo.arthd * turbo.epr * turbo.prat[2] * turbo.pt[0] / 14.7 /
                         Math.sqrt(turbo.etr * turbo.tt[0] / 518.);
            turbo.m2 = turbo.getMach(0, turbo.eair * Math.sqrt(turbo.tt[0] / 518.) /
                                        (turbo.prat[2] * turbo.pt[0] / 14.7 * turbo.acore * 144.), turbo.gama);
            turbo.mexit = turbo.getMach(2, (turbo.getAir(1.0, game) / turbo.arexitd), game);
            turbo.uexit = turbo.mexit * Math.sqrt(game * turbo.rgas * turbo.etr * turbo.tt[0] * turbo.eta[7] /
                                                  (1.0 + .5 * (game - 1.0) * turbo.mexit * turbo.mexit));
            turbo.pexit = Math.pow((1.0 + .5 * (game - 1.0) * turbo.mexit * turbo.mexit), (-game / (game - 1.0)))
                          * turbo.epr * turbo.prat[2] * turbo.pt[0];
            turbo.fgros = (turbo.uexit + (turbo.pexit - turbo.ps0) * turbo.arexitd * turbo.arthd * turbo.a2 / turbo.eair / 144.) / turbo.g0;
        }

        // ram drag
        turbo.dram = turbo.u0 / turbo.g0;
        if(turbo.entype == 2) {
            turbo.dram = turbo.dram + turbo.u0 * turbo.byprat / turbo.g0;
        }
        // mass flow ratio
        if(turbo.fsmach > .01) {
            turbo.mfr = turbo.getAir(turbo.m2, turbo.gama) * turbo.prat[2] / turbo.getAir(turbo.fsmach, turbo.gama);
        } else {
            turbo.mfr = 5.;
        }

        // net thrust
        turbo.fnet = turbo.fgros - turbo.dram;
        if(turbo.entype == 3 && turbo.fsmach < .3) {
            turbo.fnet = 0.0;
            turbo.fgros = 0.0;
        }

        // thrust inputPanel pounds
        turbo.fnlb = turbo.fnet * turbo.eair;
        turbo.fglb = turbo.fgros * turbo.eair;
        turbo.drlb = turbo.dram * turbo.eair;

        //fuel-air ratio and sfc
        turbo.fa = (turbo.trat[4] - 1.0) / (turbo.eta[4] * turbo.fhv / (cp3 * turbo.tt[3]) - turbo.trat[4]) +
                   (turbo.trat[7] - 1.0) / (turbo.fhv / (cpe * turbo.tt[15]) - turbo.trat[7]);
        if(turbo.fnet > 0.0) {
            turbo.sfc = 3600. * turbo.fa / turbo.fnet;
            turbo.flflo = turbo.sfc * turbo.fnlb;
            turbo.isp = (turbo.fnlb / turbo.flflo) * 3600.;
        } else {
            turbo.fnlb = 0.0;
            turbo.flflo = 0.0;
            turbo.sfc = 0.0;
            turbo.isp = 0.0;
        }
        turbo.tt[8] = turbo.tt[7];
        turbo.t8 = turbo.etr * turbo.tt[0] - turbo.uexit * turbo.uexit / (2.0 * turbo.rgas * game / (game - 1.0));
        turbo.trat[8] = 1.0;
        p8p5 = turbo.ps0 / (turbo.epr * turbo.prat[2] * turbo.pt[0]);
        turbo.cp[8] = turbo.getCp(turbo.tt[8], turbo.gamopt);
        turbo.pt[8] = turbo.pt[7];
        turbo.prat[8] = turbo.pt[8] / turbo.pt[7];
    /* thermal effeciency */
        if(turbo.entype == 2) {
            turbo.eteng = (turbo.a0 * turbo.a0 * ((1.0 + turbo.fa) * (turbo.uexit * turbo.uexit / (turbo.a0 * turbo.a0))
                                                  + turbo.byprat * (turbo.ues * turbo.ues / (turbo.a0 * turbo.a0))
                                                  - (1.0 + turbo.byprat) * turbo.fsmach * turbo.fsmach)) / (2.0 * turbo.g0 * turbo.fa * turbo.fhv * 778.16);
        } else {
            turbo.eteng = (turbo.a0 * turbo.a0 * ((1.0 + turbo.fa) * (turbo.uexit * turbo.uexit / (turbo.a0 * turbo.a0))
                                                  - turbo.fsmach * turbo.fsmach)) / (2.0 * turbo.g0 * turbo.fa * turbo.fhv * 778.16);
        }

        turbo.s[0] = turbo.s[1] = .2;
        turbo.v[0] = turbo.v[1] = rg1 * turbo.ts0 / (turbo.ps0 * 144.);
        for (index = 2; index <= 7; ++index) {     /* compute entropy */
            turbo.s[index] = turbo.s[index - 1] + turbo.cpair * Math.log(turbo.trat[index])
                             - rg * Math.log(turbo.prat[index]);
            turbo.v[index] = rg1 * turbo.tt[index] / (turbo.pt[index] * 144.);
        }
        turbo.s[13] = turbo.s[2] + turbo.cpair * Math.log(turbo.trat[13]) - rg * Math.log(turbo.prat[13]);
        turbo.v[13] = rg1 * turbo.tt[13] / (turbo.pt[13] * 144.);
        turbo.s[15] = turbo.s[5] + turbo.cpair * Math.log(turbo.trat[15]) - rg * Math.log(turbo.prat[15]);
        turbo.v[15] = rg1 * turbo.tt[15] / (turbo.pt[15] * 144.);
        turbo.s[8] = turbo.s[7] + turbo.cpair * Math.log(turbo.t8 / (turbo.etr * turbo.tt[0])) - rg * Math.log(p8p5);
        turbo.v[8] = rg1 * turbo.t8 / (turbo.ps0 * 144.);
        turbo.cp[0] = turbo.getCp(turbo.tt[0], turbo.gamopt);

        turbo.fntot = turbo.numeng * turbo.fnlb;
        turbo.fuelrat = turbo.numeng * turbo.flflo;
        // weight  calculation
        if(turbo.wtflag == 0) {
            if(turbo.entype == 0) {
                turbo.weight = .132 * Math.sqrt(turbo.acore * turbo.acore * turbo.acore) *
                               (turbo.dcomp * turbo.lcomp + turbo.dburner * turbo.lburn + turbo.dturbin * turbo.lturb + turbo.dnozl * turbo.lnoz);
            }
            if(turbo.entype == 1) {
                turbo.weight = .100 * Math.sqrt(turbo.acore * turbo.acore * turbo.acore) *
                               (turbo.dcomp * turbo.lcomp + turbo.dburner * turbo.lburn + turbo.dturbin * turbo.lturb + turbo.dnozl * turbo.lnoz);
            }
            if(turbo.entype == 2) {
                turbo.weight = .0932 * turbo.acore * ((1.0 + turbo.byprat) * turbo.dfan * 4.0 + turbo.dcomp * (turbo.ncomp - 3) +
                                                      turbo.dburner + turbo.dturbin * turbo.nturb + turbo.dburner * 2.0) * Math.sqrt(turbo.acore / 6.965);
            }
            if(turbo.entype == 3) {
                turbo.weight = .1242 * turbo.acore * (turbo.dburner + turbo.dnozr * 6. + turbo.dinlt * 3.) * Math.sqrt(turbo.acore / 1.753);
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
            if(turbo.tt[2] > turbo.tinlt) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to1.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to2.setForeground(Color.red);
            }
            if(turbo.tt[13] > turbo.tfan) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to2.setForeground(Color.red);
            }
            if(turbo.tt[3] > turbo.tcomp) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to3.setForeground(Color.red);
            }
            if(turbo.tt[4] > turbo.tburner) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to4.setForeground(Color.red);
            }
            if(turbo.tt[5] > turbo.tturbin) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to5.setForeground(Color.red);
            }
            if(turbo.tt[7] > turbo.tnozl) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to6.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to7.setForeground(Color.red);
            }
        }
        if(turbo.entype == 3) {
            if(turbo.tt[3] > turbo.tinlt) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to1.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to2.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to3.setForeground(Color.red);
            }
            if(turbo.tt[4] > turbo.tburner) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to4.setForeground(Color.red);
            }
            if(turbo.tt[7] > turbo.tnozr) {
                turbo.fireflag = 1;
                turbo.outputPanel.outputVariablesPanel.to5.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to6.setForeground(Color.red);
                turbo.outputPanel.outputVariablesPanel.to7.setForeground(Color.red);
            }
        }
        if(turbo.fireflag == 1) {
            turbo.view.start();
        }
    }

    public void getGeo() {
                        /* determine geometric variables */
        double game;
        int i1;

        if(turbo.entype <= 2) {          // turbine engines
            if(turbo.afan < turbo.acore) {
                turbo.afan = turbo.acore;
            }
            turbo.a8max = .75 * Math.sqrt(turbo.etr) / turbo.epr; /* limits compressor face  */
                                           /*  mach number  to < .5   */
            if(turbo.a8max > 1.0) {
                turbo.a8max = 1.0;
            }
            if(turbo.a8rat > turbo.a8max) {
                turbo.a8rat = turbo.a8max;
                if(turbo.lunits <= 1) {
                    turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF3().setText(String.format("%.3f", turbo.a8rat));
                    i1 = (int)(((turbo.a8rat - turbo.a8min) / (turbo.a8max - turbo.a8min)) * 1000.);
                    turbo.inputPanel.nozzlePanel.nozzleRightPanel.s3.setValue(i1);
                }
                if(turbo.lunits == 2) {
                    turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF3().setText(String.valueOf(String.format("%.3f",
                                                                                                              100. * (turbo.a8rat - turbo.a8ref) / turbo.a8ref)));
                    i1 = (int)((((100. * (turbo.a8rat - turbo.a8ref) / turbo.a8ref) + 10.0) / 20.0) * 1000.);
                    turbo.inputPanel.nozzlePanel.nozzleRightPanel.s3.setValue(i1);
                }
            }
          /*    dumb flightConditionsLowerPanel limit - a8 schedule */
            if(turbo.arsched == 0) {
                turbo.a8rat = turbo.a8max;
                turbo.inputPanel.nozzlePanel.nozzleLeftPanel.getF3().setText(String.format("%.3f", turbo.a8rat));
                i1 = (int)(((turbo.a8rat - turbo.a8min) / (turbo.a8max - turbo.a8min)) * 1000.);
                turbo.inputPanel.nozzlePanel.nozzleRightPanel.s3.setValue(i1);
            }
            turbo.a8 = turbo.a8rat * turbo.acore;
            turbo.a8d = turbo.a8 * turbo.prat[7] / Math.sqrt(turbo.trat[7]);
         /* assumes choked a8 and a4 */
            turbo.a4 = turbo.a8 * turbo.prat[5] * turbo.prat[15] * turbo.prat[7] /
                       Math.sqrt(turbo.trat[7] * turbo.trat[5] * turbo.trat[15]);
            turbo.a4p = turbo.a8 * turbo.prat[15] * turbo.prat[7] / Math.sqrt(turbo.trat[7] * turbo.trat[15]);
            turbo.ac = .9 * turbo.a2;
        }

        if(turbo.entype == 3) {      // ramjets
            game = turbo.getGama(turbo.tt[4], turbo.gamopt);
            if(turbo.athsched == 0) {   // scheduled throat area
                turbo.arthd = turbo.getAir(turbo.fsmach, turbo.gama) * Math.sqrt(turbo.etr) /
                              (turbo.getAir(1.0, game) * turbo.epr * turbo.prat[2]);
                if(turbo.arthd < turbo.arthmn) {
                    turbo.arthd = turbo.arthmn;
                }
                if(turbo.arthd > turbo.arthmx) {
                    turbo.arthd = turbo.arthmx;
                }
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getF3().setText(String.format("%.3f", turbo.arthd));
                i1 = (int)(((turbo.arthd - turbo.arthmn) / (turbo.arthmx - turbo.arthmn)) * 1000.);
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.s3.setValue(i1);
            }
            if(turbo.aexsched == 0) {   // scheduled exit area
                turbo.mexit = Math.sqrt((2.0 / (game - 1.0)) * ((1.0 + .5 * (turbo.gama - 1.0) * turbo.fsmach * turbo.fsmach)
                                                                * Math.pow((turbo.epr * turbo.prat[2]), (game - 1.0) / game) - 1.0));
                turbo.arexitd = turbo.getAir(1.0, game) / turbo.getAir(turbo.mexit, game);
                if(turbo.arexitd < turbo.arexmn) {
                    turbo.arexitd = turbo.arexmn;
                }
                if(turbo.arexitd > turbo.arexmx) {
                    turbo.arexitd = turbo.arexmx;
                }
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleLeftPanel.getF4().setText(String.format("%.3f", turbo.arexitd));
                i1 = (int)(((turbo.arexitd - turbo.arexmn) / (turbo.arexmx - turbo.arexmn)) * 1000.);
                turbo.inputPanel.ramjetNozzlePanel.ramjetNozzleRightPanel.s4.setValue(i1);
            }
        }
    }
}    // end Solver
 
