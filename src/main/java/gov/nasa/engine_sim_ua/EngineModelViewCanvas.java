package gov.nasa.engine_sim_ua;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class EngineModelViewCanvas extends Canvas implements Runnable {

    private Turbo turbo;
    Point locate;
    Point anchor;
    Thread runner;
    double r0;
    double x0;
    double xcowl;
    double rcowl;
    double liprad;  /* cowl  and free stream */
    @SuppressWarnings("unused")
    double capa;
    @SuppressWarnings("unused")
    double capb;
    double capc;           /* capture tube coefficients */
    @SuppressWarnings("unused")
    double cepa;
    double cepb;
    double cepc;
    double lxhst;     /* exhaust tube coefficients */
    double xfan;
    double fblade;              /* fanPanel blade */
    double xcomp;
    double hblade;
    double tblade;
    double sblade; /* compressor blades */
    double xburn;
    double rburn;
    @SuppressWarnings("unused")
    double tsig;
    double radius;   /* combustor */
    double xturb;
    double xturbh;
    double rnoz;
    double xnoz;
    double xflame;
    double xit;
    double rthroat;

    EngineModelViewCanvas(Turbo turbo) {
        this.turbo = turbo;
        setBackground(Color.black);
        runner = null;

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                onMouseDown(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                onMouseUp(e);
            }


        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                onMouseDrag(e);
            }
        });

        //         displimg = getImage(getCodeBase(),"ab1.gif") ;
    }

    private void onMouseDown(MouseEvent event) {
        anchor = event.getPoint();
    }

    private void onMouseDrag(MouseEvent event) {
        handle(event.getX(), event.getY());
    }

    private void onMouseUp(MouseEvent event) {
        handle(event.getX(), event.getY());

        handleb(event.getX(), event.getY());
    }

    public void start() {
        if(runner == null) {
            runner = new Thread(this);
            runner.start();
        }
        Turbo.antim = 0;
        Turbo.ancol = 1;
        turbo.counter = 0;
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            turbo.counter++;
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
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            turbo.view.repaint();
            if(turbo.counter == 3) {
                turbo.counter = 0;
            }
            if(Turbo.antim == 3) {
                Turbo.antim = 0;
                Turbo.ancol = -Turbo.ancol;
            }
        }
    }



    public void handle(int x, int y) {
        // determine location and move
        if(turbo.plttyp == 7) {
            return;
        }

        if(y > 42) {      // Zoom widget
            if(x <= 35) {
                Turbo.sldloc = y;
                if(Turbo.sldloc < 50) {
                    Turbo.sldloc = 50;
                }
                if(Turbo.sldloc > 160) {
                    Turbo.sldloc = 160;
                }
                Turbo.factor = 10.0 + (Turbo.sldloc - 50) * 1.0;

                turbo.view.repaint();
                return;
            }
        }

        if(y >= 42 && x >= 35) {      //  move the engine
            locate = new Point(x, y);
            Turbo.xtrans = Turbo.xtrans + (int)(.2 * (locate.x - anchor.x));
            Turbo.ytrans = Turbo.ytrans + (int)(.2 * (locate.y - anchor.y));
            if(Turbo.xtrans > 320) {
                Turbo.xtrans = 320;
            }
            if(Turbo.xtrans < -280) {
                Turbo.xtrans = -280;
            }
            if(Turbo.ytrans > 300) {
                Turbo.ytrans = 300;
            }
            if(Turbo.ytrans < -300) {
                Turbo.ytrans = -300;
            }
            turbo.view.repaint();
            return;
        }
    }

    public void handleb(int x, int y) {
        // determine choices
        if(turbo.plttyp == 7) {
            return;
        }

        if(y < 12) {   //  top labels
            if(x >= 0 && x <= 60) {   // flightPanel conditions
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.varflag = 0;
            }
            if(x >= 61 && x <= 100) {   // sizePanel
                turbo.layin.show(turbo.inputPanel, "second");
                turbo.varflag = 1;
            }
            if(x >= 101 && x <= 161) {   // limits
                turbo.layin.show(turbo.inputPanel, "eleven");
                turbo.varflag = 8;
            }
            if(x >= 162 && x <= 195) {   // save file
                turbo.layin.show(turbo.inputPanel, "twelve");
                turbo.varflag = 10;
            }
            if(x >= 195 && x <= 245) {   // print file
                turbo.layin.show(turbo.inputPanel, "thirteen");
                turbo.varflag = 9;
            }
            if(x >= 245) {   // find plotPanel
                Turbo.xtrans = 125.0;
                Turbo.ytrans = 115.0;
                Turbo.factor = 35.;
                Turbo.sldloc = 75;
            }
            turbo.solve.comPute();
            turbo.view.repaint();
            turbo.outputPanel.outputPlotCanvas.repaint();
            turbo.flightConditionsPanel.setPanl();
            return;
        }

        if(turbo.inflag == 1) {
            return;  // end of functions for test mode
        }

        if(y >= 27 && y <= 42) {                // key off the words
            if(turbo.entype <= 2) {
                if(x >= 0 && x <= 39) {    //inletPanel
                    turbo.layin.show(turbo.inputPanel, "third");
                    turbo.varflag = 2;
                }
                if(x >= 40 && x <= 69) {   //fanPanel
                    if(turbo.entype != 2) {
                        return;
                    }
                    turbo.layin.show(turbo.inputPanel, "fourth");
                    turbo.varflag = 3;
                }
                if(x >= 70 && x <= 149) {  //compress
                    turbo.layin.show(turbo.inputPanel, "fifth");
                    turbo.varflag = 4;
                }
                if(x >= 150 && x <= 199) {  // burner
                    turbo.layin.show(turbo.inputPanel, "sixth");
                    turbo.varflag = 5;
                }
                if(x >= 200 && x <= 249) {  //turbine
                    turbo.layin.show(turbo.inputPanel, "seventh");
                    turbo.varflag = 6;
                }
                if(x >= 250 && x <= 299) {  // nozzle
                    turbo.layin.show(turbo.inputPanel, "eighth");
                    turbo.varflag = 7;
                }
            }
            if(turbo.entype == 3) {
                if(x >= 0 && x <= 39) {
                    turbo.layin.show(turbo.inputPanel, "third");
                    turbo.varflag = 2;
                }
                if(x >= 40 && x <= 150) {  // burner
                    turbo.layin.show(turbo.inputPanel, "sixth");
                    turbo.varflag = 5;
                }
                if(x >= 151 && x <= 299) {
                    turbo.layin.show(turbo.inputPanel, "tenth");
                    turbo.varflag = 7;
                }
            }
            turbo.view.repaint();
            turbo.outputPanel.outputPlotCanvas.repaint();
            return;
        }

        if(y > 12 && y < 27) {          // set engine type from words
            if(x >= 0 && x <= 60) {   // turbojet
                turbo.entype = 0;
            }
            if(x >= 61 && x <= 141) {    // afterburner
                turbo.entype = 1;
            }
            if(x >= 142 && x <= 212) {   // turbo fanPanel
                turbo.entype = 2;
            }
            if(x >= 213 && x <= 300) {   // ramjet
                turbo.entype = 3;
                Turbo.u0d = 1500.;
                Turbo.altd = 35000.;
            }
            turbo.varflag = 0;
            turbo.layin.show(turbo.inputPanel, "first");
            // reset limits
            if(turbo.entype <= 2) {
                if(turbo.lunits != 1) {
                    Turbo.u0max = 1500.;
                    Turbo.altmax = 60000.;
                    Turbo.t4max = 3200.;
                    Turbo.t7max = 4100.;
                }
                if(turbo.lunits == 1) {
                    Turbo.u0max = 2500.;
                    Turbo.altmax = 20000.;
                    Turbo.t4max = 1800.;
                    Turbo.t7max = 2100.;
                }
                if(Turbo.u0d > Turbo.u0max) {
                    Turbo.u0d = Turbo.u0max;
                }
                if(Turbo.altd > Turbo.altmax) {
                    Turbo.altd = Turbo.altmax;
                }
                if(Turbo.tt4d > Turbo.t4max) {
                    Turbo.tt4 = Turbo.tt4d = Turbo.t4max;
                }
                if(Turbo.tt7d > Turbo.t7max) {
                    Turbo.tt7 = Turbo.tt7d = Turbo.t7max;
                }
            } else {
                if(turbo.lunits != 1) {
                    Turbo.u0max = 4500.;
                    Turbo.altmax = 100000.;
                    Turbo.t4max = 4500.;
                    Turbo.t7max = 4500.;
                }
                if(turbo.lunits == 1) {
                    Turbo.u0max = 7500.;
                    Turbo.altmax = 35000.;
                    Turbo.t4max = 2500.;
                    Turbo.t7max = 2200.;
                }
            }
            // get the areas correct
            if(turbo.entype != 2) {
                Turbo.a2 = Turbo.acore;
                Turbo.a2d = Turbo.a2 * Turbo.aconv;
            }
            if(turbo.entype == 2) {
                Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
                Turbo.a2 = Turbo.afan;
                Turbo.a2d = Turbo.a2 * Turbo.aconv;
            }
            Turbo.diameng = Math.sqrt(4.0 * Turbo.a2d / 3.14159);
            // set the abflag correctly
            if(turbo.entype == 1) {
                turbo.abflag = 1;
                Turbo.mnozl = 5;
                Turbo.dnozl = 400.2;
                Turbo.tnozl = 4100.;
                turbo.inputPanel.flightPanel.flightRightPanel.nozch.select(turbo.abflag);
            }
            if(turbo.entype != 1) {
                turbo.abflag = 0;
                Turbo.mnozl = 3;
                Turbo.dnozl = 515.2;
                Turbo.tnozl = 2500.;
                turbo.inputPanel.flightPanel.flightRightPanel.nozch.select(turbo.abflag);
            }

            turbo.flightConditionsPanel.setUnits();
            turbo.flightConditionsPanel.setPanl();
            turbo.solve.comPute();
            turbo.view.repaint();
            turbo.outputPanel.outputPlotCanvas.repaint();
            return;
        }
    }

    public void update(Graphics g) {
        turbo.view.paint(g);
    }

    public void getDrawGeo() { /* get the drawing geometry */
        int i;
        int j;

        lxhst = 5.;

        Turbo.scale = Math.sqrt(Turbo.acore / 3.1415926);
        if(Turbo.scale > 10.0) {
            Turbo.scale = Turbo.scale / 10.0;
        }

        if(Turbo.ncflag == 0) {
            Turbo.ncomp = (int)(1.0 + Turbo.p3p2d / 1.5);
            if(Turbo.ncomp > 15) {
                Turbo.ncomp = 15;
            }
            turbo.inputPanel.compressorPanel.compressorLeftPanel.getF3().setText(String.valueOf(Turbo.ncomp));
        }
        sblade = .02;
        hblade = Math.sqrt(2.0 / 3.1415926);
        tblade = .2 * hblade;
        r0 = Math.sqrt(2.0 * Turbo.mfr / 3.1415926);
        x0 = -4.0 * hblade;

        radius = .3 * hblade;
        rcowl = Math.sqrt(1.8 / 3.1415926);
        liprad = .1 * hblade;
        xcowl = -hblade - liprad;
        xfan = 0.0;
        xcomp = Turbo.ncomp * (tblade + sblade);
        Turbo.ncompd = Turbo.ncomp;
        if(turbo.entype == 2) {                    /* fanPanel geometry */
            Turbo.ncompd = Turbo.ncomp + 3;
            fblade = Math.sqrt(2.0 * (1.0 + Turbo.byprat) / 3.1415926);
            rcowl = fblade;
            r0 = Math.sqrt(2.0 * (1.0 + Turbo.byprat) * Turbo.mfr / 3.1415926);
            xfan = 3.0 * (tblade + sblade);
            xcomp = Turbo.ncompd * (tblade + sblade);
        }
        if(r0 < rcowl) {
            capc = (rcowl - r0) / ((xcowl - x0) * (xcowl - x0));
            capb = -2.0 * capc * x0;
            capa = r0 + capc * x0 * x0;
        } else {
            capc = (r0 - rcowl) / ((xcowl - x0) * (xcowl - x0));
            capb = -2.0 * capc * xcowl;
            capa = rcowl + capc * xcowl * xcowl;
        }
        Turbo.lcomp = xcomp;
        Turbo.lburn = hblade;
        xburn = xcomp + Turbo.lburn;
        rburn = .2 * hblade;

        if(Turbo.ntflag == 0) {
            Turbo.nturb = 1 + Turbo.ncomp / 4;
            turbo.inputPanel.turbinePanel.turbineLeftPanel.getF3().setText(String.valueOf(Turbo.nturb));
            if(turbo.entype == 2) {
                Turbo.nturb = Turbo.nturb + 1;
            }
        }
        Turbo.lturb = Turbo.nturb * (tblade + sblade);
        xturb = xburn + Turbo.lturb;
        xturbh = xturb - 2.0 * (tblade + sblade);
        Turbo.lnoz = Turbo.lburn;
        if(turbo.entype == 1) {
            Turbo.lnoz = 3.0 * Turbo.lburn;
        }
        if(turbo.entype == 3) {
            Turbo.lnoz = 3.0 * Turbo.lburn;
        }
        xnoz = xturb + Turbo.lburn;
        xflame = xturb + Turbo.lnoz;
        xit = xflame + hblade;
        if(turbo.entype <= 2) {
            rnoz = Math.sqrt(Turbo.a8rat * 2.0 / 3.1415926);
            cepc = -rnoz / (lxhst * lxhst);
            cepb = -2.0 * cepc * (xit + lxhst);
            cepa = rnoz - cepb * xit - cepc * xit * xit;
        }
        if(turbo.entype == 3) {
            rnoz = Math.sqrt(Turbo.arthd * Turbo.arexitd * 2.0 / 3.1415926);
            rthroat = Math.sqrt(Turbo.arthd * 2.0 / 3.1415926);
        }
        // animated flow field
        for (i = 0; i <= 5; ++i) {   // upstream
            Turbo.xg[4][i] = Turbo.xg[0][i] = i * (xcowl - x0) / 5.0 + x0;
            Turbo.yg[0][i] = .9 * hblade;
            Turbo.yg[4][i] = 0.0;
        }
        for (i = 6; i <= 14; ++i) {  // compress
            Turbo.xg[4][i] = Turbo.xg[0][i] = (i - 5) * (xcomp - xcowl) / 9.0 + xcowl;
            Turbo.yg[0][i] = .9 * hblade;
            Turbo.yg[4][i] = (i - 5) * (1.5 * radius) / 9.0;
        }
        for (i = 15; i <= 18; ++i) {  // burnerPanel
            Turbo.xg[0][i] = (i - 14) * (xburn - xcomp) / 4.0 + xcomp;
            Turbo.yg[0][i] = .9 * hblade;
            Turbo.yg[4][i] = .5 * radius;
        }
        for (i = 19; i <= 23; ++i) {  // turbinePanel
            Turbo.xg[0][i] = (i - 18) * (xturb - xburn) / 5.0 + xburn;
            Turbo.yg[0][i] = .9 * hblade;
            Turbo.yg[4][i] = (i - 18) * (-.5 * radius) / 5.0 + radius;
        }
        for (i = 24; i <= 29; ++i) { // nozzl
            Turbo.xg[0][i] = (i - 23) * (xit - xturb) / 6.0 + xturb;
            if(turbo.entype != 3) {
                Turbo.yg[0][i] = (i - 23) * (rnoz - hblade) / 6.0 + hblade;
            }
            if(turbo.entype == 3) {
                Turbo.yg[0][i] = (i - 23) * (rthroat - hblade) / 6.0 + hblade;
            }
            Turbo.yg[4][i] = 0.0;
        }
        for (i = 29; i <= 34; ++i) { // external
            Turbo.xg[0][i] = (i - 28) * (3.0) / 3.0 + xit;
            if(turbo.entype != 3) {
                Turbo.yg[0][i] = (i - 28) * (rnoz) / 3.0 + rnoz;
            }
            if(turbo.entype == 3) {
                Turbo.yg[0][i] = (i - 28) * (rthroat) / 3.0 + rthroat;
            }
            Turbo.yg[4][i] = 0.0;
        }

        for (j = 1; j <= 3; ++j) {
            for (i = 0; i <= 34; ++i) {
                Turbo.xg[j][i] = Turbo.xg[0][i];
                Turbo.yg[j][i] = (1.0 - .25 * j) * (Turbo.yg[0][i] - Turbo.yg[4][i]) + Turbo.yg[4][i];
            }
        }
        for (j = 5; j <= 8; ++j) {
            for (i = 0; i <= 34; ++i) {
                Turbo.xg[j][i] = Turbo.xg[0][i];
                Turbo.yg[j][i] = -Turbo.yg[8 - j][i];
            }
        }
        if(turbo.entype == 2) {  // fanPanel flow
            for (i = 0; i <= 5; ++i) {   // upstream
                Turbo.xg[9][i] = Turbo.xg[0][i];
                Turbo.xg[10][i] = Turbo.xg[0][i];
                Turbo.xg[11][i] = Turbo.xg[0][i];
                Turbo.xg[12][i] = Turbo.xg[0][i];
            }
            for (i = 6; i <= 34; ++i) {  // compress
                Turbo.xg[9][i] = Turbo.xg[10][i] = Turbo.xg[11][i] = Turbo.xg[12][i] =
                    (i - 6) * (7.0 - xcowl) / 28.0 + xcowl;
            }
            for (i = 0; i <= 34; ++i) {  // compress
                Turbo.yg[9][i] = .5 * (hblade + .9 * rcowl);
                Turbo.yg[10][i] = .9 * rcowl;
                Turbo.yg[11][i] = -.5 * (hblade + .9 * rcowl);
                Turbo.yg[12][i] = -.9 * rcowl;
            }
        }
    }

    public void paint(Graphics g) {
        int i;
        int j;
        int bcol;
        int dcol;
        int exes[] = new int[8];
        int whys[] = new int[8];
        int xlabel;
        int ylabel;
        double xl;
        double yl;

        bcol = 0;
        dcol = 7;
        xl = Turbo.factor * 0.0 + Turbo.xtrans;
        yl = Turbo.factor * 0.0 + Turbo.ytrans;

        turbo.offsGg.setColor(Color.black);
        turbo.offsGg.fillRect(0, 0, 500, 500);
        turbo.offsGg.setColor(Color.blue);
        for (j = 0; j <= 20; ++j) {
            exes[0] = 0;
            exes[1] = 500;
            whys[0] = whys[1] = (int)(yl + Turbo.factor * (20. / Turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
            whys[0] = whys[1] = (int)(yl - Turbo.factor * (20. / Turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
        }
        for (j = 0; j <= 40; ++j) {
            whys[0] = 0;
            whys[1] = 500;
            exes[0] = exes[1] = (int)(xl + Turbo.factor * (20. / Turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
            exes[0] = exes[1] = (int)(xl - Turbo.factor * (20. / Turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
        }

        if(turbo.entype <= 2) {
                      /* blades */
            turbo.offsGg.setColor(Color.white);
            for (j = 1; j <= Turbo.ncompd; ++j) {
                exes[0] = (int)(xl + Turbo.factor * (.02 + (j - 1) * (tblade + sblade)));
                whys[0] = (int)(Turbo.factor * hblade + Turbo.ytrans);
                exes[1] = exes[0] + (int)(Turbo.factor * tblade);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = whys[2];
                turbo.offsGg.fillPolygon(exes, whys, 4);
            }

            if(turbo.entype == 2) {                        /*  fanPanel blades */
                turbo.offsGg.setColor(Color.white);
                for (j = 1; j <= 3; ++j) {
                    if(j == 3 && bcol == 0) {
                        turbo.offsGg.setColor(Color.black);
                    }
                    if(j == 3 && bcol == 7) {
                        turbo.offsGg.setColor(Color.white);
                    }
                    exes[0] = (int)(xl + Turbo.factor * (.02 + (j - 1) * (tblade + sblade)));
                    whys[0] = (int)(Turbo.factor * fblade + Turbo.ytrans);
                    exes[1] = exes[0] + (int)(Turbo.factor * tblade);
                    whys[1] = whys[0];
                    exes[2] = exes[1];
                    whys[2] = (int)(Turbo.factor * -fblade + Turbo.ytrans);
                    exes[3] = exes[0];
                    whys[3] = whys[2];
                    turbo.offsGg.fillPolygon(exes, whys, 4);
                }
            }
                       /* core */
            turbo.offsGg.setColor(Color.cyan);
            if(turbo.varflag == 4) {
                turbo.offsGg.setColor(Color.yellow);
            }
            turbo.offsGg.fillArc((int)(xl - Turbo.factor * radius), (int)(yl - Turbo.factor * radius),
                                 (int)(2.0 * Turbo.factor * radius), (int)(2.0 * Turbo.factor * radius), 90, 180);
            exes[0] = (int)(xl);
            whys[0] = (int)(Turbo.factor * radius + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = (int)(Turbo.factor * -radius + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
            if(turbo.entype == 2) {  // fanPanel
                turbo.offsGg.setColor(Color.green);
                if(turbo.varflag == 3) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.fillArc((int)(xl - Turbo.factor * radius), (int)(yl - Turbo.factor * radius),
                                     (int)(2.0 * Turbo.factor * radius), (int)(2.0 * Turbo.factor * radius), 90, 180);
                exes[0] = (int)(xl);
                whys[0] = (int)(Turbo.factor * radius + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * xfan + Turbo.xtrans);
                whys[1] = (int)(Turbo.factor * 1.2 * radius + Turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * -1.2 * radius + Turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(Turbo.factor * -radius + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
            }
/* combustor */
            turbo.offsGg.setColor(Color.black);
            exes[0] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * hblade + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * hblade + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = whys[2];
            turbo.offsGg.fillPolygon(exes, whys, 4);

            turbo.offsGg.setColor(Color.white);
            xl = xcomp + .05 + rburn;
            yl = .6 * hblade;
            turbo.offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (yl - rburn) + Turbo.ytrans),
                                 (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180);
            turbo.offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (-yl - rburn) + Turbo.ytrans),
                                 (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180);
                               /* core */
            turbo.offsGg.setColor(Color.red);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xcomp + .25 * Turbo.lburn + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * .8 * radius + Turbo.ytrans);
            exes[2] = (int)(Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans);
            whys[2] = (int)(Turbo.factor * .8 * radius + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[4] = exes[3];
            whys[4] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            exes[5] = exes[2];
            whys[5] = (int)(Turbo.factor * -.8 * radius + Turbo.ytrans);
            exes[6] = exes[1];
            whys[6] = (int)(Turbo.factor * -.8 * radius + Turbo.ytrans);
            exes[7] = exes[0];
            whys[7] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 8);
/* turbine */
                      /* blades */
            for (j = 1; j <= Turbo.nturb; ++j) {
                turbo.offsGg.setColor(Color.white);
                if(turbo.entype == 2) {
                    if(j == (Turbo.nturb - 1) && bcol == 0) {
                        turbo.offsGg.setColor(Color.black);
                    }
                    if(j == (Turbo.nturb - 1) && bcol == 7) {
                        turbo.offsGg.setColor(Color.white);
                    }
                }
                exes[0] = (int)(Turbo.factor * (xburn + .02 + (j - 1) * (tblade + sblade)) + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * hblade + Turbo.ytrans);
                exes[1] = exes[0] + (int)(Turbo.factor * tblade);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = whys[2];
                turbo.offsGg.fillPolygon(exes, whys, 4);
            }
                     /* core */
            turbo.offsGg.setColor(Color.magenta);
            if(turbo.varflag == 6) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xnoz + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * 0.0 + Turbo.ytrans);
            exes[2] = exes[0];
            whys[2] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 3);
/* afterburner */
            if(turbo.entype == 1) {
                if(dcol == 0) {
                    turbo.offsGg.setColor(Color.black);
                }
                if(dcol == 7) {
                    turbo.offsGg.setColor(Color.white);
                }
                if(turbo.varflag == 7) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                exes[0] = (int)(Turbo.factor * (xflame - .1 * Turbo.lnoz) + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * .6 * hblade + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * (xflame - .2 * Turbo.lnoz) + Turbo.xtrans);
                whys[1] = (int)(Turbo.factor * .5 * hblade + Turbo.ytrans);
                exes[2] = (int)(Turbo.factor * (xflame - .1 * Turbo.lnoz) + Turbo.xtrans);
                whys[2] = (int)(Turbo.factor * .4 * hblade + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                turbo.offsGg.drawLine(exes[1], whys[1], exes[2], whys[2]);
                whys[0] = (int)(Turbo.factor * -.6 * hblade + Turbo.ytrans);
                whys[1] = (int)(Turbo.factor * -.5 * hblade + Turbo.ytrans);
                whys[2] = (int)(Turbo.factor * -.4 * hblade + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                turbo.offsGg.drawLine(exes[1], whys[1], exes[2], whys[2]);
            }

/* cowl */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            if(turbo.entype == 0) {   /*   turbojet  */
                if(turbo.varflag == 2) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                xl = xcowl + liprad;                /*   core cowl */
                yl = rcowl;
                turbo.offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                                     (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180);
                turbo.offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans),
                                     (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180);
                exes[0] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (yl + liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * -radius + Turbo.xtrans);
                whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * hblade + Turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
                whys[0] = (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans);
                whys[1] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
                whys[2] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
                whys[3] = (int)(Turbo.factor * (-yl + liprad) + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
                // compressor
                turbo.offsGg.setColor(Color.cyan);
                if(turbo.varflag == 4) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                exes[0] = (int)(Turbo.factor * -radius + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
                exes[3] = (int)(Turbo.factor * .02 + Turbo.xtrans);
                whys[3] = (int)(Turbo.factor * hblade + Turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
                whys[3] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);
            }
            if(turbo.entype == 1) {            /*   fighter plane  */
                turbo.offsGg.setColor(Color.white);
                if(turbo.varflag == 2) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                xl = xcowl + liprad;                     /*   inletPanel */
                yl = rcowl;
                turbo.offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                                     (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180);
                exes[0] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (yl + liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * -radius + Turbo.xtrans);
                whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * hblade + Turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
                exes[0] = (int)(Turbo.factor * (xl + 1.5 * xcowl) + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * -radius + Turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
                exes[3] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[3] = (int)(Turbo.factor * -.7 * hblade + Turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[0];
                turbo.offsGg.fillPolygon(exes, whys, 5);
                // compressor
                turbo.offsGg.setColor(Color.cyan);
                if(turbo.varflag == 4) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                exes[0] = (int)(Turbo.factor * -radius + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
                exes[3] = (int)(Turbo.factor * .02 + Turbo.xtrans);
                whys[3] = (int)(Turbo.factor * hblade + Turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
                whys[3] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);
            }
            if(turbo.entype == 2) {                                  /* fanPanel jet */
                if(dcol == 0) {
                    turbo.offsGg.setColor(Color.black);
                }
                if(dcol == 7) {
                    turbo.offsGg.setColor(Color.white);
                }
                if(turbo.varflag == 2) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                xl = xcowl + liprad;                     /*   fanPanel cowl inletPanel */
                yl = rcowl;
                turbo.offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                                     (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180);
                turbo.offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans),
                                     (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180);
                exes[0] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (yl + liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * -radius + Turbo.xtrans);
                whys[1] = (int)(Turbo.factor * (fblade + liprad) + Turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * fblade + Turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);

                whys[0] = (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans);
                whys[1] = (int)(Turbo.factor * (-fblade - liprad) + Turbo.ytrans);
                whys[2] = (int)(Turbo.factor * -fblade + Turbo.ytrans);
                whys[3] = (int)(Turbo.factor * (-yl + liprad) + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);

                turbo.offsGg.setColor(Color.green);
                if(turbo.varflag == 3) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                xl = xcowl + liprad;                     /*   fanPanel cowl */
                yl = rcowl;
                exes[0] = (int)(Turbo.factor * -radius + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (fblade + liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * xcomp / 2.0 + Turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
                whys[2] = (int)(Turbo.factor * fblade + Turbo.ytrans);
                exes[3] = (int)(Turbo.factor * .02 + Turbo.xtrans);
                whys[3] = (int)(Turbo.factor * fblade + Turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                whys[0] = (int)(Turbo.factor * (-fblade - liprad) + Turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(Turbo.factor * -fblade + Turbo.ytrans);
                whys[3] = whys[2];
                whys[4] = whys[2];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                xl = xfan + .02;             /* core cowl */
                yl = hblade;
                turbo.offsGg.setColor(Color.cyan);
                if(turbo.varflag == 4) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (yl - liprad) + Turbo.ytrans),
                                     (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180);
                turbo.offsGg.fillArc((int)(Turbo.factor * (xl - liprad) + Turbo.xtrans), (int)(Turbo.factor * (-yl - liprad) + Turbo.ytrans),
                                     (int)(2.0 * Turbo.factor * liprad), (int)(2.0 * Turbo.factor * liprad), 90, 180);
                exes[0] = (int)(Turbo.factor * (xl - .01) + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
                exes[1] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(Turbo.factor * (.8 * hblade) + Turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(Turbo.factor * (hblade - liprad) + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);

                whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
                whys[3] = (int)(Turbo.factor * (-hblade + liprad) + Turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
            }
                                                 /* combustor */
            turbo.offsGg.setColor(Color.red);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            exes[4] = (int)(Turbo.factor * (xcomp + .25 * Turbo.lburn) + Turbo.xtrans);
            whys[4] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            exes[5] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[5] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 6);

            whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            whys[4] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            whys[5] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 6);
                                                  /* turbine */
            turbo.offsGg.setColor(Color.magenta);
            if(turbo.varflag == 6) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xturb + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
                                                 /* nozzle */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            if(turbo.varflag == 7) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xturb + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xflame + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[2] = (int)(Turbo.factor * xit + Turbo.xtrans);
            whys[2] = (int)(Turbo.factor * rnoz + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * xflame + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            exes[4] = (int)(Turbo.factor * xturb + Turbo.xtrans);
            whys[4] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 5);

            whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -rnoz + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            whys[4] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 5);
            //   show stations
            if(turbo.showcom == 1) {
                turbo.offsGg.setColor(Color.white);
                ylabel = (int)(Turbo.factor * 1.5 * hblade + 20. + Turbo.ytrans);
                whys[1] = 370;

                xl = xcomp - .1;                   /* burner entrance */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("3", xlabel, ylabel);

                xl = xburn - .1;                   /* turbine entrance */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("4", xlabel, ylabel);

                xl = xnoz;            /* Afterburner entry */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("6", xlabel, ylabel);

                if(turbo.entype == 1) {
                    xl = xflame;               /* Afterburner exit */
                    exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                    whys[0] = (int)(Turbo.factor * .2 + Turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(Turbo.factor * .05);
                    turbo.offsGg.drawString("7", xlabel, ylabel);
                }

                xl = xit;                    /* nozzle exit */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * .2 + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] - (int)(Turbo.factor * .2);
                turbo.offsGg.drawString("8", xlabel, ylabel);

                if(turbo.entype < 2) {
                    xl = -radius;                   /* compressor entrance */
                    exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                    whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(Turbo.factor * .05);
                    turbo.offsGg.drawString("2", xlabel, ylabel);

                    xl = xturb + .1;                   /* turbine exit */
                    exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                    whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(Turbo.factor * .05);
                    turbo.offsGg.drawString("5", xlabel, ylabel);
                }
                if(turbo.entype == 2) {
                    xl = xturbh;               /*high pressturbine exit*/
                    exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                    whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(Turbo.factor * .05);
                    turbo.offsGg.drawString("5", xlabel, ylabel);

                    xl = 0.0 - .1;                            /* fanPanel entrance */
                    exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                    whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] - (int)(Turbo.factor * .2);
                    turbo.offsGg.drawString("1", xlabel, ylabel);

                    xl = 3.0 * tblade;                            /* fanPanel exit */
                    exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                    whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(Turbo.factor * .12);
                    turbo.offsGg.drawString("2", xlabel, ylabel);
                }

                xl = -2.0;                   /* free stream */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("0", xlabel, ylabel);
            }

            if(turbo.inflag == 0) {   // show labels for design mode
                turbo.offsGg.setColor(Color.black);
                turbo.offsGg.fillRect(0, 27, 300, 15);
                turbo.offsGg.setColor(Color.white);
                if(turbo.varflag == 2) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Inlet", 10, 40);
                if(turbo.entype == 2) {
                    turbo.offsGg.setColor(Color.green);
                    if(turbo.varflag == 3) {
                        turbo.offsGg.setColor(Color.yellow);
                    }
                    turbo.offsGg.drawString("Fan", 40, 40);
                }
                turbo.offsGg.setColor(Color.cyan);
                if(turbo.varflag == 4) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Compressor", 70, 40);
                turbo.offsGg.setColor(Color.red);
                if(turbo.varflag == 5) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Burner", 150, 40);
                turbo.offsGg.setColor(Color.magenta);
                if(turbo.varflag == 6) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Turbine", 200, 40);
                turbo.offsGg.setColor(Color.white);
                if(turbo.varflag == 7) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Nozzle", 250, 40);
            }
        }

        if(turbo.entype == 3) {                  //ramjet geom
                       /* inletPanel spike */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            exes[0] = (int)(Turbo.factor * -2.0 + Turbo.xtrans);
            whys[0] = (int)(0.0 + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = (int)(0.0 + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
                             /* spraybars */
            turbo.offsGg.setColor(Color.white);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            xl = xcomp + .05 + rburn;
            yl = .6 * hblade;
            turbo.offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (yl - rburn) + Turbo.ytrans),
                                 (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180);
            turbo.offsGg.drawArc((int)(Turbo.factor * (xl - rburn) + Turbo.xtrans), (int)(Turbo.factor * (-yl - rburn) + Turbo.ytrans),
                                 (int)(2.0 * Turbo.factor * rburn), (int)(2.0 * Turbo.factor * rburn), 90, 180);
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            exes[0] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xcomp + .25 * Turbo.lburn + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * .8 * radius + Turbo.ytrans);
            exes[2] = (int)(Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans);
            whys[2] = (int)(Turbo.factor * .8 * radius + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[4] = exes[3];
            whys[4] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            exes[5] = exes[2];
            whys[5] = (int)(Turbo.factor * -.8 * radius + Turbo.ytrans);
            exes[6] = exes[1];
            whys[6] = (int)(Turbo.factor * -.8 * radius + Turbo.ytrans);
            exes[7] = exes[0];
            whys[7] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 8);
                     /* aft cone */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            exes[0] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * 1.5 * radius + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xnoz + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * 0.0 + Turbo.ytrans);
            exes[2] = exes[0];
            whys[2] = (int)(Turbo.factor * -1.5 * radius + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 3);
                       /* fame holders */
            turbo.offsGg.setColor(Color.white);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * (xnoz + .2 * Turbo.lnoz) + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * .6 * hblade + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * (xnoz + .1 * Turbo.lnoz) + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * .5 * hblade + Turbo.ytrans);
            exes[2] = (int)(Turbo.factor * (xnoz + .2 * Turbo.lnoz) + Turbo.xtrans);
            whys[2] = (int)(Turbo.factor * .4 * hblade + Turbo.ytrans);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
            turbo.offsGg.drawLine(exes[1], whys[1], exes[2], whys[2]);
            whys[0] = (int)(Turbo.factor * -.6 * hblade + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * -.5 * hblade + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -.4 * hblade + Turbo.ytrans);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
            turbo.offsGg.drawLine(exes[1], whys[1], exes[2], whys[2]);

            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            if(turbo.varflag == 2) {
                turbo.offsGg.setColor(Color.yellow);
            }

            xl = xcowl + liprad;                /*   core cowl */
            yl = rcowl;
            exes[0] = (int)(Turbo.factor * xl + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (yl) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * -radius + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * hblade + Turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = (int)(Turbo.factor * (yl) + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
            whys[0] = (int)(Turbo.factor * (-yl) + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * (-yl) + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
            // compressor
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            if(turbo.varflag == 2) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * -radius + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[1] = whys[0];
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * .02 + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * hblade + Turbo.ytrans);
            exes[4] = exes[0];
            whys[4] = whys[3];
            turbo.offsGg.fillPolygon(exes, whys, 5);

            whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[1] = whys[0];
            whys[2] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * -hblade + Turbo.ytrans);
            whys[4] = whys[3];
            turbo.offsGg.fillPolygon(exes, whys, 5);
                                                 /* combustor */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * (xcomp + .75 * Turbo.lburn) + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            exes[4] = (int)(Turbo.factor * (xcomp + .25 * Turbo.lburn) + Turbo.xtrans);
            whys[4] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            exes[5] = (int)(Turbo.factor * xcomp + Turbo.xtrans);
            whys[5] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 6);

            whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            whys[4] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            whys[5] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 6);
                                                  /* turbine */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xturb + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * xburn + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * .8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * -.8 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
                                                /* nozzle */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            if(turbo.varflag == 7) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(Turbo.factor * xturb + Turbo.xtrans);
            whys[0] = (int)(Turbo.factor * (hblade + liprad) + Turbo.ytrans);
            exes[1] = (int)(Turbo.factor * xit + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * rnoz + Turbo.ytrans);
            exes[2] = (int)(Turbo.factor * xflame + Turbo.xtrans);
            whys[2] = (int)(Turbo.factor * rthroat + Turbo.ytrans);
            exes[3] = (int)(Turbo.factor * xturb + Turbo.xtrans);
            whys[3] = (int)(Turbo.factor * .9 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            whys[0] = (int)(Turbo.factor * (-hblade - liprad) + Turbo.ytrans);
            whys[1] = (int)(Turbo.factor * -rnoz + Turbo.ytrans);
            whys[2] = (int)(Turbo.factor * -rthroat + Turbo.ytrans);
            whys[3] = (int)(Turbo.factor * -.9 * hblade + Turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            //   show stations
            if(turbo.showcom == 1) {
                turbo.offsGg.setColor(Color.white);
                ylabel = (int)(Turbo.factor * 1.5 * hblade + 20. + Turbo.ytrans);
                whys[1] = 370;

                xl = xcomp - .1;                   /* burner entrance */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("3", xlabel, ylabel);

                xl = xnoz + .1 * Turbo.lnoz;        /* flame holders */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * .2 + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("4", xlabel, ylabel);

                xl = xflame;               /* Afterburner exit */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * .2 + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("7", xlabel, ylabel);

                xl = xit;                    /* nozzle exit */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * .2 + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] - (int)(Turbo.factor * .2);
                turbo.offsGg.drawString("8", xlabel, ylabel);

                xl = -2.0;                   /* free stream */
                exes[0] = exes[1] = (int)(Turbo.factor * xl + Turbo.xtrans);
                whys[0] = (int)(Turbo.factor * (hblade - .2) + Turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(Turbo.factor * .05);
                turbo.offsGg.drawString("0", xlabel, ylabel);
            }

            if(turbo.inflag == 0) {  // show labels for design mode
                turbo.offsGg.setColor(Color.black);
                turbo.offsGg.fillRect(0, 27, 300, 15);
                turbo.offsGg.setColor(Color.white);
                if(turbo.varflag == 2) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Inlet", 10, 40);
                turbo.offsGg.setColor(Color.white);
                if(turbo.varflag == 5) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Burner", 100, 40);
                turbo.offsGg.setColor(Color.white);
                if(turbo.varflag == 7) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.drawString("Nozzle", 250, 40);
            }
        }
/* animated flow */
        for (j = 1; j <= 8; ++j) {
            exes[1] = (int)(Turbo.factor * Turbo.xg[j][0] + Turbo.xtrans);
            whys[1] = (int)(Turbo.factor * Turbo.yg[j][0] + Turbo.ytrans);
            for (i = 1; i <= 34; ++i) {
                exes[0] = exes[1];
                whys[0] = whys[1];
                exes[1] = (int)(Turbo.factor * Turbo.xg[j][i] + Turbo.xtrans);
                whys[1] = (int)(Turbo.factor * Turbo.yg[j][i] + Turbo.ytrans);
                if((i - Turbo.antim) / 3 * 3 == (i - Turbo.antim)) {
                    if(i < 15) {
                        if(Turbo.ancol == -1) {
                            if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.white);
                            }
                            if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                        }
                        if(Turbo.ancol == 1) {
                            if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                            if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.white);
                            }
                        }
                    }
                    if(i >= 16) {
                        if(Turbo.ancol == -1) {
                            if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.yellow);
                            }
                            if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.red);
                            }
                        }
                        if(Turbo.ancol == 1) {
                            if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.red);
                            }
                            if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.yellow);
                            }
                        }
                    }
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                }
            }
        }
        if(turbo.entype == 2) {   // fanPanel flow
            for (j = 9; j <= 12; ++j) {
                exes[1] = (int)(Turbo.factor * Turbo.xg[j][0] + Turbo.xtrans);
                whys[1] = (int)(Turbo.factor * Turbo.yg[j][0] + Turbo.ytrans);
                for (i = 1; i <= 34; ++i) {
                    exes[0] = exes[1];
                    whys[0] = whys[1];
                    exes[1] = (int)(Turbo.factor * Turbo.xg[j][i] + Turbo.xtrans);
                    whys[1] = (int)(Turbo.factor * Turbo.yg[j][i] + Turbo.ytrans);
                    if((i - Turbo.antim) / 3 * 3 == (i - Turbo.antim)) {
                        if(Turbo.ancol == -1) {
                            if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.white);
                            }
                            if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                        }
                        if(Turbo.ancol == 1) {
                            if((i - Turbo.antim) / 6 * 6 == (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                            if((i - Turbo.antim) / 6 * 6 != (i - Turbo.antim)) {
                                turbo.offsGg.setColor(Color.white);
                            }
                        }
                        turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    }
                }
            }
        }

        turbo.offsGg.setColor(Color.black);
        turbo.offsGg.fillRect(0, 0, 300, 27);
        turbo.offsGg.setColor(Color.white);
        if(turbo.varflag == 0) {
            turbo.offsGg.setColor(Color.yellow);
        }
        turbo.offsGg.drawString("Flight", 10, 10);
        turbo.offsGg.setColor(Color.white);
        if(turbo.varflag == 1) {
            turbo.offsGg.setColor(Color.yellow);
        }
        turbo.offsGg.drawString("Size", 70, 10);
        turbo.offsGg.setColor(Color.white);
        if(turbo.varflag == 8) {
            turbo.offsGg.setColor(Color.yellow);
        }
        turbo.offsGg.drawString("Limits", 120, 10);
        turbo.offsGg.setColor(Color.white);
        if(turbo.varflag == 10) {
            turbo.offsGg.setColor(Color.yellow);
        }
        turbo.offsGg.drawString("Save", 170, 10);
        turbo.offsGg.setColor(Color.white);
        if(turbo.varflag == 9) {
            turbo.offsGg.setColor(Color.yellow);
        }
        turbo.offsGg.drawString("Print", 215, 10);
        turbo.offsGg.setColor(Color.cyan);
        turbo.offsGg.drawString("Find", 260, 10);
        // zoom widget
        turbo.offsGg.setColor(Color.black);
        turbo.offsGg.fillRect(0, 42, 35, 140);
        turbo.offsGg.setColor(Color.cyan);
        turbo.offsGg.drawString("Zoom", 5, 180);
        turbo.offsGg.drawLine(15, 50, 15, 165);
        turbo.offsGg.fillRect(5, Turbo.sldloc, 20, 5);

        if(turbo.inflag == 0) { // engine labels for design mode
            turbo.offsGg.setColor(Color.green);
            if(turbo.entype == 0) {
                turbo.offsGg.setColor(Color.yellow);
                turbo.offsGg.fillRect(0, 15, 60, 12);
                turbo.offsGg.setColor(Color.black);
            }
            turbo.offsGg.drawString("Turbojet", 10, 25);
            turbo.offsGg.setColor(Color.green);
            if(turbo.entype == 1) {
                turbo.offsGg.setColor(Color.yellow);
                turbo.offsGg.fillRect(61, 15, 80, 12);
                turbo.offsGg.setColor(Color.black);
            }
            turbo.offsGg.drawString("Afterburner", 75, 25);
            turbo.offsGg.setColor(Color.green);
            if(turbo.entype == 2) {
                turbo.offsGg.setColor(Color.yellow);
                turbo.offsGg.fillRect(142, 15, 70, 12);
                turbo.offsGg.setColor(Color.black);
            }
            turbo.offsGg.drawString("Turbo Fan", 150, 25);
            turbo.offsGg.setColor(Color.green);
            if(turbo.entype == 3) {
                turbo.offsGg.setColor(Color.yellow);
                turbo.offsGg.fillRect(213, 15, 90, 12);
                turbo.offsGg.setColor(Color.black);
            }
            turbo.offsGg.drawString("Ramjet", 225, 25);
        }
        // temp limit warning
        if(Turbo.fireflag == 1) {
            turbo.offsGg.setColor(Color.yellow);
            turbo.offsGg.fillRect(50, 80, 200, 30);
            if(turbo.counter == 1) {
                turbo.offsGg.setColor(Color.black);
            }
            if(turbo.counter >= 2) {
                turbo.offsGg.setColor(Color.white);
            }
            turbo.offsGg.fillRect(55, 85, 190, 20);
            turbo.offsGg.setColor(Color.red);
            turbo.offsGg.drawString("Temperature  Limits Exceeded", 60, 100);
        }

        g.drawImage(turbo.offscreenImg, 0, 0, this);
    }
}   // end viewer
 
