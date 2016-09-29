package gov.nasa.engine_sim_ua;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
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
        turbo.antim = 0;
        turbo.ancol = 1;
        turbo.counter = 0;
    }

    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            turbo.counter++;
            ++turbo.antim;
/*
       if (inflag == 0 && fireflag == 0) {
          runner = null;
          return;
       }
       if(entype == 0) displimg = antjimg[counter-1];
       if(entype == 1) displimg = anabimg[counter-1];
       if(entype == 2) displimg = anfnimg[counter-1];
       if(entype == 3) displimg = anrmimg[counter-1];
*/
            try { Thread.sleep(100); } catch (InterruptedException ignored) {}
            turbo.view.repaint();
            if(turbo.counter == 3) {
                turbo.counter = 0;
            }
            if(turbo.antim == 3) {
                turbo.antim = 0;
                turbo.ancol = -turbo.ancol;
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
                turbo.sldloc = y;
                if(turbo.sldloc < 50) {
                    turbo.sldloc = 50;
                }
                if(turbo.sldloc > 160) {
                    turbo.sldloc = 160;
                }
                turbo.factor = 10.0 + (turbo.sldloc - 50) * 1.0;

                turbo.view.repaint();
                return;
            }
        }

        if(y >= 42 && x >= 35) {      //  move the engine
            locate = new Point(x, y);
            turbo.xtrans = turbo.xtrans + (int)(.2 * (locate.x - anchor.x));
            turbo.ytrans = turbo.ytrans + (int)(.2 * (locate.y - anchor.y));
            if(turbo.xtrans > 320) {
                turbo.xtrans = 320;
            }
            if(turbo.xtrans < -280) {
                turbo.xtrans = -280;
            }
            if(turbo.ytrans > 300) {
                turbo.ytrans = 300;
            }
            if(turbo.ytrans < -300) {
                turbo.ytrans = -300;
            }
            turbo.view.repaint();
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
                turbo.xtrans = 125.0;
                turbo.ytrans = 115.0;
                turbo.factor = 35.;
                turbo.sldloc = 75;
            }
            turbo.solve.compute();
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
                turbo.u0d = 1500.;
                turbo.altd = 35000.;
            }
            turbo.varflag = 0;
            turbo.layin.show(turbo.inputPanel, "first");
            // reset limits
            if(turbo.entype <= 2) {
                if(turbo.lunits != 1) {
                    turbo.u0max = 1500.;
                    turbo.altmax = 60000.;
                    turbo.t4max = 3200.;
                    turbo.t7max = 4100.;
                }
                if(turbo.lunits == 1) {
                    turbo.u0max = 2500.;
                    turbo.altmax = 20000.;
                    turbo.t4max = 1800.;
                    turbo.t7max = 2100.;
                }
                if(turbo.u0d > turbo.u0max) {
                    turbo.u0d = turbo.u0max;
                }
                if(turbo.altd > turbo.altmax) {
                    turbo.altd = turbo.altmax;
                }
                if(turbo.tt4d > turbo.t4max) {
                    turbo.tt4 = turbo.tt4d = turbo.t4max;
                }
                if(turbo.tt7d > turbo.t7max) {
                    turbo.tt7 = turbo.tt7d = turbo.t7max;
                }
            } else {
                if(turbo.lunits != 1) {
                    turbo.u0max = 4500.;
                    turbo.altmax = 100000.;
                    turbo.t4max = 4500.;
                    turbo.t7max = 4500.;
                }
                if(turbo.lunits == 1) {
                    turbo.u0max = 7500.;
                    turbo.altmax = 35000.;
                    turbo.t4max = 2500.;
                    turbo.t7max = 2200.;
                }
            }
            // get the areas correct
            if(turbo.entype != 2) {
                turbo.a2 = turbo.acore;
                turbo.a2d = turbo.a2 * turbo.aconv;
            }
            if(turbo.entype == 2) {
                turbo.afan = turbo.acore * (1.0 + turbo.byprat);
                turbo.a2 = turbo.afan;
                turbo.a2d = turbo.a2 * turbo.aconv;
            }
            turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
            // set the abflag correctly
            if(turbo.entype == 1) {
                turbo.abflag = 1;
                turbo.mnozl = 5;
                turbo.dnozl = 400.2;
                turbo.tnozl = 4100.;
                turbo.inputPanel.flightPanel.flightRightPanel.nozch.select(turbo.abflag);
            }
            if(turbo.entype != 1) {
                turbo.abflag = 0;
                turbo.mnozl = 3;
                turbo.dnozl = 515.2;
                turbo.tnozl = 2500.;
                turbo.inputPanel.flightPanel.flightRightPanel.nozch.select(turbo.abflag);
            }

            turbo.flightConditionsPanel.setUnits();
            turbo.flightConditionsPanel.setPanl();
            turbo.solve.compute();
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

        turbo.scale = Math.sqrt(turbo.acore / 3.1415926);
        if(turbo.scale > 10.0) {
            turbo.scale = turbo.scale / 10.0;
        }

        if(turbo.ncflag == 0) {
            turbo.ncomp = (int)(1.0 + turbo.p3p2d / 1.5);
            if(turbo.ncomp > 15) {
                turbo.ncomp = 15;
            }
            turbo.inputPanel.compressorPanel.compressorLeftPanel.getF3().setText(String.valueOf(turbo.ncomp));
        }
        sblade = .02;
        hblade = Math.sqrt(2.0 / 3.1415926);
        tblade = .2 * hblade;
        r0 = Math.sqrt(2.0 * turbo.mfr / 3.1415926);
        x0 = -4.0 * hblade;

        radius = .3 * hblade;
        rcowl = Math.sqrt(1.8 / 3.1415926);
        liprad = .1 * hblade;
        xcowl = -hblade - liprad;
        xfan = 0.0;
        xcomp = turbo.ncomp * (tblade + sblade);
        turbo.ncompd = turbo.ncomp;
        if(turbo.entype == 2) {                    /* fanPanel geometry */
            turbo.ncompd = turbo.ncomp + 3;
            fblade = Math.sqrt(2.0 * (1.0 + turbo.byprat) / 3.1415926);
            rcowl = fblade;
            r0 = Math.sqrt(2.0 * (1.0 + turbo.byprat) * turbo.mfr / 3.1415926);
            xfan = 3.0 * (tblade + sblade);
            xcomp = turbo.ncompd * (tblade + sblade);
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
        turbo.lcomp = xcomp;
        turbo.lburn = hblade;
        xburn = xcomp + turbo.lburn;
        rburn = .2 * hblade;

        if(turbo.ntflag == 0) {
            turbo.nturb = 1 + turbo.ncomp / 4;
            turbo.inputPanel.turbinePanel.turbineLeftPanel.getF3().setText(String.valueOf(turbo.nturb));
            if(turbo.entype == 2) {
                turbo.nturb = turbo.nturb + 1;
            }
        }
        turbo.lturb = turbo.nturb * (tblade + sblade);
        xturb = xburn + turbo.lturb;
        xturbh = xturb - 2.0 * (tblade + sblade);
        turbo.lnoz = turbo.lburn;
        if(turbo.entype == 1) {
            turbo.lnoz = 3.0 * turbo.lburn;
        }
        if(turbo.entype == 3) {
            turbo.lnoz = 3.0 * turbo.lburn;
        }
        xnoz = xturb + turbo.lburn;
        xflame = xturb + turbo.lnoz;
        xit = xflame + hblade;
        if(turbo.entype <= 2) {
            rnoz = Math.sqrt(turbo.a8rat * 2.0 / 3.1415926);
            cepc = -rnoz / (lxhst * lxhst);
            cepb = -2.0 * cepc * (xit + lxhst);
            cepa = rnoz - cepb * xit - cepc * xit * xit;
        }
        if(turbo.entype == 3) {
            rnoz = Math.sqrt(turbo.arthd * turbo.arexitd * 2.0 / 3.1415926);
            rthroat = Math.sqrt(turbo.arthd * 2.0 / 3.1415926);
        }
        // animated flow field
        for (i = 0; i <= 5; ++i) {   // upstream
            turbo.xg[4][i] = turbo.xg[0][i] = i * (xcowl - x0) / 5.0 + x0;
            turbo.yg[0][i] = .9 * hblade;
            turbo.yg[4][i] = 0.0;
        }
        for (i = 6; i <= 14; ++i) {  // compress
            turbo.xg[4][i] = turbo.xg[0][i] = (i - 5) * (xcomp - xcowl) / 9.0 + xcowl;
            turbo.yg[0][i] = .9 * hblade;
            turbo.yg[4][i] = (i - 5) * (1.5 * radius) / 9.0;
        }
        for (i = 15; i <= 18; ++i) {  // burnerPanel
            turbo.xg[0][i] = (i - 14) * (xburn - xcomp) / 4.0 + xcomp;
            turbo.yg[0][i] = .9 * hblade;
            turbo.yg[4][i] = .5 * radius;
        }
        for (i = 19; i <= 23; ++i) {  // turbinePanel
            turbo.xg[0][i] = (i - 18) * (xturb - xburn) / 5.0 + xburn;
            turbo.yg[0][i] = .9 * hblade;
            turbo.yg[4][i] = (i - 18) * (-.5 * radius) / 5.0 + radius;
        }
        for (i = 24; i <= 29; ++i) { // nozzl
            turbo.xg[0][i] = (i - 23) * (xit - xturb) / 6.0 + xturb;
            if(turbo.entype != 3) {
                turbo.yg[0][i] = (i - 23) * (rnoz - hblade) / 6.0 + hblade;
            }
            if(turbo.entype == 3) {
                turbo.yg[0][i] = (i - 23) * (rthroat - hblade) / 6.0 + hblade;
            }
            turbo.yg[4][i] = 0.0;
        }
        for (i = 29; i <= 34; ++i) { // external
            turbo.xg[0][i] = (i - 28) * (3.0) / 3.0 + xit;
            if(turbo.entype != 3) {
                turbo.yg[0][i] = (i - 28) * (rnoz) / 3.0 + rnoz;
            }
            if(turbo.entype == 3) {
                turbo.yg[0][i] = (i - 28) * (rthroat) / 3.0 + rthroat;
            }
            turbo.yg[4][i] = 0.0;
        }

        for (j = 1; j <= 3; ++j) {
            for (i = 0; i <= 34; ++i) {
                turbo.xg[j][i] = turbo.xg[0][i];
                turbo.yg[j][i] = (1.0 - .25 * j) * (turbo.yg[0][i] - turbo.yg[4][i]) + turbo.yg[4][i];
            }
        }
        for (j = 5; j <= 8; ++j) {
            for (i = 0; i <= 34; ++i) {
                turbo.xg[j][i] = turbo.xg[0][i];
                turbo.yg[j][i] = -turbo.yg[8 - j][i];
            }
        }
        if(turbo.entype == 2) {  // fanPanel flow
            for (i = 0; i <= 5; ++i) {   // upstream
                turbo.xg[9][i] = turbo.xg[0][i];
                turbo.xg[10][i] = turbo.xg[0][i];
                turbo.xg[11][i] = turbo.xg[0][i];
                turbo.xg[12][i] = turbo.xg[0][i];
            }
            for (i = 6; i <= 34; ++i) {  // compress
                turbo.xg[9][i] = turbo.xg[10][i] = turbo.xg[11][i] = turbo.xg[12][i] =
                    (i - 6) * (7.0 - xcowl) / 28.0 + xcowl;
            }
            for (i = 0; i <= 34; ++i) {  // compress
                turbo.yg[9][i] = .5 * (hblade + .9 * rcowl);
                turbo.yg[10][i] = .9 * rcowl;
                turbo.yg[11][i] = -.5 * (hblade + .9 * rcowl);
                turbo.yg[12][i] = -.9 * rcowl;
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
        xl = turbo.factor * 0.0 + turbo.xtrans;
        yl = turbo.factor * 0.0 + turbo.ytrans;

        turbo.offsGg.setColor(Color.black);
        turbo.offsGg.fillRect(0, 0, 500, 500);
        turbo.offsGg.setColor(Color.blue);
        for (j = 0; j <= 20; ++j) {
            exes[0] = 0;
            exes[1] = 500;
            whys[0] = whys[1] = (int)(yl + turbo.factor * (20. / turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
            whys[0] = whys[1] = (int)(yl - turbo.factor * (20. / turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
        }
        for (j = 0; j <= 40; ++j) {
            whys[0] = 0;
            whys[1] = 500;
            exes[0] = exes[1] = (int)(xl + turbo.factor * (20. / turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
            exes[0] = exes[1] = (int)(xl - turbo.factor * (20. / turbo.scale * j) / 25.0);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
        }

        if(turbo.entype <= 2) {
                      /* blades */
            turbo.offsGg.setColor(Color.white);
            for (j = 1; j <= turbo.ncompd; ++j) {
                exes[0] = (int)(xl + turbo.factor * (.02 + (j - 1) * (tblade + sblade)));
                whys[0] = (int)(turbo.factor * hblade + turbo.ytrans);
                exes[1] = exes[0] + (int)(turbo.factor * tblade);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * -hblade + turbo.ytrans);
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
                    exes[0] = (int)(xl + turbo.factor * (.02 + (j - 1) * (tblade + sblade)));
                    whys[0] = (int)(turbo.factor * fblade + turbo.ytrans);
                    exes[1] = exes[0] + (int)(turbo.factor * tblade);
                    whys[1] = whys[0];
                    exes[2] = exes[1];
                    whys[2] = (int)(turbo.factor * -fblade + turbo.ytrans);
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
            turbo.offsGg.fillArc((int)(xl - turbo.factor * radius), (int)(yl - turbo.factor * radius),
                                 (int)(2.0 * turbo.factor * radius), (int)(2.0 * turbo.factor * radius), 90, 180);
            exes[0] = (int)(xl);
            whys[0] = (int)(turbo.factor * radius + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[1] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = (int)(turbo.factor * -radius + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
            if(turbo.entype == 2) {  // fanPanel
                turbo.offsGg.setColor(Color.green);
                if(turbo.varflag == 3) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.fillArc((int)(xl - turbo.factor * radius), (int)(yl - turbo.factor * radius),
                                     (int)(2.0 * turbo.factor * radius), (int)(2.0 * turbo.factor * radius), 90, 180);
                exes[0] = (int)(xl);
                whys[0] = (int)(turbo.factor * radius + turbo.ytrans);
                exes[1] = (int)(turbo.factor * xfan + turbo.xtrans);
                whys[1] = (int)(turbo.factor * 1.2 * radius + turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * -1.2 * radius + turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(turbo.factor * -radius + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
            }
/* combustor */
            turbo.offsGg.setColor(Color.black);
            exes[0] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[0] = (int)(turbo.factor * hblade + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[1] = (int)(turbo.factor * hblade + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * -hblade + turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = whys[2];
            turbo.offsGg.fillPolygon(exes, whys, 4);

            turbo.offsGg.setColor(Color.white);
            xl = xcomp + .05 + rburn;
            yl = .6 * hblade;
            turbo.offsGg.drawArc((int)(turbo.factor * (xl - rburn) + turbo.xtrans), (int)(turbo.factor * (yl - rburn) + turbo.ytrans),
                                 (int)(2.0 * turbo.factor * rburn), (int)(2.0 * turbo.factor * rburn), 90, 180);
            turbo.offsGg.drawArc((int)(turbo.factor * (xl - rburn) + turbo.xtrans), (int)(turbo.factor * (-yl - rburn) + turbo.ytrans),
                                 (int)(2.0 * turbo.factor * rburn), (int)(2.0 * turbo.factor * rburn), 90, 180);
                               /* core */
            turbo.offsGg.setColor(Color.red);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[0] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xcomp + .25 * turbo.lburn + turbo.xtrans);
            whys[1] = (int)(turbo.factor * .8 * radius + turbo.ytrans);
            exes[2] = (int)(turbo.factor * (xcomp + .75 * turbo.lburn) + turbo.xtrans);
            whys[2] = (int)(turbo.factor * .8 * radius + turbo.ytrans);
            exes[3] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[3] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[4] = exes[3];
            whys[4] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
            exes[5] = exes[2];
            whys[5] = (int)(turbo.factor * -.8 * radius + turbo.ytrans);
            exes[6] = exes[1];
            whys[6] = (int)(turbo.factor * -.8 * radius + turbo.ytrans);
            exes[7] = exes[0];
            whys[7] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 8);
/* turbine */
                      /* blades */
            for (j = 1; j <= turbo.nturb; ++j) {
                turbo.offsGg.setColor(Color.white);
                if(turbo.entype == 2) {
                    if(j == (turbo.nturb - 1) && bcol == 0) {
                        turbo.offsGg.setColor(Color.black);
                    }
                    if(j == (turbo.nturb - 1) && bcol == 7) {
                        turbo.offsGg.setColor(Color.white);
                    }
                }
                exes[0] = (int)(turbo.factor * (xburn + .02 + (j - 1) * (tblade + sblade)) + turbo.xtrans);
                whys[0] = (int)(turbo.factor * hblade + turbo.ytrans);
                exes[1] = exes[0] + (int)(turbo.factor * tblade);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * -hblade + turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = whys[2];
                turbo.offsGg.fillPolygon(exes, whys, 4);
            }
                     /* core */
            turbo.offsGg.setColor(Color.magenta);
            if(turbo.varflag == 6) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[0] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xnoz + turbo.xtrans);
            whys[1] = (int)(turbo.factor * 0.0 + turbo.ytrans);
            exes[2] = exes[0];
            whys[2] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
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
                exes[0] = (int)(turbo.factor * (xflame - .1 * turbo.lnoz) + turbo.xtrans);
                whys[0] = (int)(turbo.factor * .6 * hblade + turbo.ytrans);
                exes[1] = (int)(turbo.factor * (xflame - .2 * turbo.lnoz) + turbo.xtrans);
                whys[1] = (int)(turbo.factor * .5 * hblade + turbo.ytrans);
                exes[2] = (int)(turbo.factor * (xflame - .1 * turbo.lnoz) + turbo.xtrans);
                whys[2] = (int)(turbo.factor * .4 * hblade + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                turbo.offsGg.drawLine(exes[1], whys[1], exes[2], whys[2]);
                whys[0] = (int)(turbo.factor * -.6 * hblade + turbo.ytrans);
                whys[1] = (int)(turbo.factor * -.5 * hblade + turbo.ytrans);
                whys[2] = (int)(turbo.factor * -.4 * hblade + turbo.ytrans);
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
                turbo.offsGg.fillArc((int)(turbo.factor * (xl - liprad) + turbo.xtrans), (int)(turbo.factor * (yl - liprad) + turbo.ytrans),
                                     (int)(2.0 * turbo.factor * liprad), (int)(2.0 * turbo.factor * liprad), 90, 180);
                turbo.offsGg.fillArc((int)(turbo.factor * (xl - liprad) + turbo.xtrans), (int)(turbo.factor * (-yl - liprad) + turbo.ytrans),
                                     (int)(2.0 * turbo.factor * liprad), (int)(2.0 * turbo.factor * liprad), 90, 180);
                exes[0] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (yl + liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * -radius + turbo.xtrans);
                whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * hblade + turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(turbo.factor * (yl - liprad) + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
                whys[0] = (int)(turbo.factor * (-yl - liprad) + turbo.ytrans);
                whys[1] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
                whys[2] = (int)(turbo.factor * -hblade + turbo.ytrans);
                whys[3] = (int)(turbo.factor * (-yl + liprad) + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
                // compressor
                turbo.offsGg.setColor(Color.cyan);
                if(turbo.varflag == 4) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                exes[0] = (int)(turbo.factor * -radius + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * xcomp + turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
                exes[3] = (int)(turbo.factor * .02 + turbo.xtrans);
                whys[3] = (int)(turbo.factor * hblade + turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
                whys[3] = (int)(turbo.factor * -hblade + turbo.ytrans);
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
                turbo.offsGg.fillArc((int)(turbo.factor * (xl - liprad) + turbo.xtrans), (int)(turbo.factor * (yl - liprad) + turbo.ytrans),
                                     (int)(2.0 * turbo.factor * liprad), (int)(2.0 * turbo.factor * liprad), 90, 180);
                exes[0] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (yl + liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * -radius + turbo.xtrans);
                whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * hblade + turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(turbo.factor * (yl - liprad) + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
                exes[0] = (int)(turbo.factor * (xl + 1.5 * xcowl) + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * -radius + turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * -hblade + turbo.ytrans);
                exes[3] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[3] = (int)(turbo.factor * -.7 * hblade + turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[0];
                turbo.offsGg.fillPolygon(exes, whys, 5);
                // compressor
                turbo.offsGg.setColor(Color.cyan);
                if(turbo.varflag == 4) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                exes[0] = (int)(turbo.factor * -radius + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * xcomp + turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
                exes[3] = (int)(turbo.factor * .02 + turbo.xtrans);
                whys[3] = (int)(turbo.factor * hblade + turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
                whys[3] = (int)(turbo.factor * -hblade + turbo.ytrans);
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
                turbo.offsGg.fillArc((int)(turbo.factor * (xl - liprad) + turbo.xtrans), (int)(turbo.factor * (yl - liprad) + turbo.ytrans),
                                     (int)(2.0 * turbo.factor * liprad), (int)(2.0 * turbo.factor * liprad), 90, 180);
                turbo.offsGg.fillArc((int)(turbo.factor * (xl - liprad) + turbo.xtrans), (int)(turbo.factor * (-yl - liprad) + turbo.ytrans),
                                     (int)(2.0 * turbo.factor * liprad), (int)(2.0 * turbo.factor * liprad), 90, 180);
                exes[0] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (yl + liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * -radius + turbo.xtrans);
                whys[1] = (int)(turbo.factor * (fblade + liprad) + turbo.ytrans);
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * fblade + turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(turbo.factor * (yl - liprad) + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);

                whys[0] = (int)(turbo.factor * (-yl - liprad) + turbo.ytrans);
                whys[1] = (int)(turbo.factor * (-fblade - liprad) + turbo.ytrans);
                whys[2] = (int)(turbo.factor * -fblade + turbo.ytrans);
                whys[3] = (int)(turbo.factor * (-yl + liprad) + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);

                turbo.offsGg.setColor(Color.green);
                if(turbo.varflag == 3) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                xl = xcowl + liprad;                     /*   fanPanel cowl */
                yl = rcowl;
                exes[0] = (int)(turbo.factor * -radius + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (fblade + liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * xcomp / 2.0 + turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = (int)(turbo.factor * xcomp + turbo.xtrans);
                whys[2] = (int)(turbo.factor * fblade + turbo.ytrans);
                exes[3] = (int)(turbo.factor * .02 + turbo.xtrans);
                whys[3] = (int)(turbo.factor * fblade + turbo.ytrans);
                exes[4] = exes[0];
                whys[4] = whys[3];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                whys[0] = (int)(turbo.factor * (-fblade - liprad) + turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(turbo.factor * -fblade + turbo.ytrans);
                whys[3] = whys[2];
                whys[4] = whys[2];
                turbo.offsGg.fillPolygon(exes, whys, 5);

                xl = xfan + .02;             /* core cowl */
                yl = hblade;
                turbo.offsGg.setColor(Color.cyan);
                if(turbo.varflag == 4) {
                    turbo.offsGg.setColor(Color.yellow);
                }
                turbo.offsGg.fillArc((int)(turbo.factor * (xl - liprad) + turbo.xtrans), (int)(turbo.factor * (yl - liprad) + turbo.ytrans),
                                     (int)(2.0 * turbo.factor * liprad), (int)(2.0 * turbo.factor * liprad), 90, 180);
                turbo.offsGg.fillArc((int)(turbo.factor * (xl - liprad) + turbo.xtrans), (int)(turbo.factor * (-yl - liprad) + turbo.ytrans),
                                     (int)(2.0 * turbo.factor * liprad), (int)(2.0 * turbo.factor * liprad), 90, 180);
                exes[0] = (int)(turbo.factor * (xl - .01) + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
                exes[1] = (int)(turbo.factor * xcomp + turbo.xtrans);
                whys[1] = whys[0];
                exes[2] = exes[1];
                whys[2] = (int)(turbo.factor * (.8 * hblade) + turbo.ytrans);
                exes[3] = exes[0];
                whys[3] = (int)(turbo.factor * (hblade - liprad) + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);

                whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
                whys[1] = whys[0];
                whys[2] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
                whys[3] = (int)(turbo.factor * (-hblade + liprad) + turbo.ytrans);
                turbo.offsGg.fillPolygon(exes, whys, 4);
            }
                                                 /* combustor */
            turbo.offsGg.setColor(Color.red);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
            exes[3] = (int)(turbo.factor * (xcomp + .75 * turbo.lburn) + turbo.xtrans);
            whys[3] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            exes[4] = (int)(turbo.factor * (xcomp + .25 * turbo.lburn) + turbo.xtrans);
            whys[4] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            exes[5] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[5] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 6);

            whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[1] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
            whys[3] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            whys[4] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            whys[5] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 6);
                                                  /* turbine */
            turbo.offsGg.setColor(Color.magenta);
            if(turbo.varflag == 6) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xturb + turbo.xtrans);
            whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            exes[3] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[3] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[1] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            whys[3] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
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
            exes[0] = (int)(turbo.factor * xturb + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xflame + turbo.xtrans);
            whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[2] = (int)(turbo.factor * xit + turbo.xtrans);
            whys[2] = (int)(turbo.factor * rnoz + turbo.ytrans);
            exes[3] = (int)(turbo.factor * xflame + turbo.xtrans);
            whys[3] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            exes[4] = (int)(turbo.factor * xturb + turbo.xtrans);
            whys[4] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 5);

            whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[1] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -rnoz + turbo.ytrans);
            whys[3] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            whys[4] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 5);
            //   show stations
            if(turbo.showcom == 1) {
                turbo.offsGg.setColor(Color.white);
                ylabel = (int)(turbo.factor * 1.5 * hblade + 20. + turbo.ytrans);
                whys[1] = 370;

                xl = xcomp - .1;                   /* burner entrance */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
                turbo.offsGg.drawString("3", xlabel, ylabel);

                xl = xburn - .1;                   /* turbine entrance */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
                turbo.offsGg.drawString("4", xlabel, ylabel);

                xl = xnoz;            /* Afterburner entry */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
                turbo.offsGg.drawString("6", xlabel, ylabel);

                if(turbo.entype == 1) {
                    xl = xflame;               /* Afterburner exit */
                    exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                    whys[0] = (int)(turbo.factor * .2 + turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(turbo.factor * .05);
                    turbo.offsGg.drawString("7", xlabel, ylabel);
                }

                xl = xit;                    /* nozzle exit */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * .2 + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] - (int)(turbo.factor * .2);
                turbo.offsGg.drawString("8", xlabel, ylabel);

                if(turbo.entype < 2) {
                    xl = -radius;                   /* compressor entrance */
                    exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                    whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(turbo.factor * .05);
                    turbo.offsGg.drawString("2", xlabel, ylabel);

                    xl = xturb + .1;                   /* turbine exit */
                    exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                    whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(turbo.factor * .05);
                    turbo.offsGg.drawString("5", xlabel, ylabel);
                }
                if(turbo.entype == 2) {
                    xl = xturbh;               /*high pressturbine exit*/
                    exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                    whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(turbo.factor * .05);
                    turbo.offsGg.drawString("5", xlabel, ylabel);

                    xl = 0.0 - .1;                            /* fanPanel entrance */
                    exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                    whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] - (int)(turbo.factor * .2);
                    turbo.offsGg.drawString("1", xlabel, ylabel);

                    xl = 3.0 * tblade;                            /* fanPanel exit */
                    exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                    whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                    turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    xlabel = exes[0] + (int)(turbo.factor * .12);
                    turbo.offsGg.drawString("2", xlabel, ylabel);
                }

                xl = -2.0;                   /* free stream */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
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
            exes[0] = (int)(turbo.factor * -2.0 + turbo.xtrans);
            whys[0] = (int)(0.0 + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[1] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = (int)(0.0 + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
                             /* spraybars */
            turbo.offsGg.setColor(Color.white);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            xl = xcomp + .05 + rburn;
            yl = .6 * hblade;
            turbo.offsGg.drawArc((int)(turbo.factor * (xl - rburn) + turbo.xtrans), (int)(turbo.factor * (yl - rburn) + turbo.ytrans),
                                 (int)(2.0 * turbo.factor * rburn), (int)(2.0 * turbo.factor * rburn), 90, 180);
            turbo.offsGg.drawArc((int)(turbo.factor * (xl - rburn) + turbo.xtrans), (int)(turbo.factor * (-yl - rburn) + turbo.ytrans),
                                 (int)(2.0 * turbo.factor * rburn), (int)(2.0 * turbo.factor * rburn), 90, 180);
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            exes[0] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[0] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xcomp + .25 * turbo.lburn + turbo.xtrans);
            whys[1] = (int)(turbo.factor * .8 * radius + turbo.ytrans);
            exes[2] = (int)(turbo.factor * (xcomp + .75 * turbo.lburn) + turbo.xtrans);
            whys[2] = (int)(turbo.factor * .8 * radius + turbo.ytrans);
            exes[3] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[3] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[4] = exes[3];
            whys[4] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
            exes[5] = exes[2];
            whys[5] = (int)(turbo.factor * -.8 * radius + turbo.ytrans);
            exes[6] = exes[1];
            whys[6] = (int)(turbo.factor * -.8 * radius + turbo.ytrans);
            exes[7] = exes[0];
            whys[7] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 8);
                     /* aft cone */
            if(dcol == 0) {
                turbo.offsGg.setColor(Color.black);
            }
            if(dcol == 7) {
                turbo.offsGg.setColor(Color.white);
            }
            exes[0] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[0] = (int)(turbo.factor * 1.5 * radius + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xnoz + turbo.xtrans);
            whys[1] = (int)(turbo.factor * 0.0 + turbo.ytrans);
            exes[2] = exes[0];
            whys[2] = (int)(turbo.factor * -1.5 * radius + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 3);
                       /* fame holders */
            turbo.offsGg.setColor(Color.white);
            if(turbo.varflag == 5) {
                turbo.offsGg.setColor(Color.yellow);
            }
            exes[0] = (int)(turbo.factor * (xnoz + .2 * turbo.lnoz) + turbo.xtrans);
            whys[0] = (int)(turbo.factor * .6 * hblade + turbo.ytrans);
            exes[1] = (int)(turbo.factor * (xnoz + .1 * turbo.lnoz) + turbo.xtrans);
            whys[1] = (int)(turbo.factor * .5 * hblade + turbo.ytrans);
            exes[2] = (int)(turbo.factor * (xnoz + .2 * turbo.lnoz) + turbo.xtrans);
            whys[2] = (int)(turbo.factor * .4 * hblade + turbo.ytrans);
            turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
            turbo.offsGg.drawLine(exes[1], whys[1], exes[2], whys[2]);
            whys[0] = (int)(turbo.factor * -.6 * hblade + turbo.ytrans);
            whys[1] = (int)(turbo.factor * -.5 * hblade + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -.4 * hblade + turbo.ytrans);
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
            exes[0] = (int)(turbo.factor * xl + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (yl) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * -radius + turbo.xtrans);
            whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * hblade + turbo.ytrans);
            exes[3] = exes[0];
            whys[3] = (int)(turbo.factor * (yl) + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);
            whys[0] = (int)(turbo.factor * (-yl) + turbo.ytrans);
            whys[1] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -hblade + turbo.ytrans);
            whys[3] = (int)(turbo.factor * (-yl) + turbo.ytrans);
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
            exes[0] = (int)(turbo.factor * -radius + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[1] = whys[0];
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
            exes[3] = (int)(turbo.factor * .02 + turbo.xtrans);
            whys[3] = (int)(turbo.factor * hblade + turbo.ytrans);
            exes[4] = exes[0];
            whys[4] = whys[3];
            turbo.offsGg.fillPolygon(exes, whys, 5);

            whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[1] = whys[0];
            whys[2] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
            whys[3] = (int)(turbo.factor * -hblade + turbo.ytrans);
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
            exes[0] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
            exes[3] = (int)(turbo.factor * (xcomp + .75 * turbo.lburn) + turbo.xtrans);
            whys[3] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            exes[4] = (int)(turbo.factor * (xcomp + .25 * turbo.lburn) + turbo.xtrans);
            whys[4] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            exes[5] = (int)(turbo.factor * xcomp + turbo.xtrans);
            whys[5] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 6);

            whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[1] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
            whys[3] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            whys[4] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            whys[5] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
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
            exes[0] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xturb + turbo.xtrans);
            whys[1] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[2] = exes[1];
            whys[2] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            exes[3] = (int)(turbo.factor * xburn + turbo.xtrans);
            whys[3] = (int)(turbo.factor * .8 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[1] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            whys[3] = (int)(turbo.factor * -.8 * hblade + turbo.ytrans);
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
            exes[0] = (int)(turbo.factor * xturb + turbo.xtrans);
            whys[0] = (int)(turbo.factor * (hblade + liprad) + turbo.ytrans);
            exes[1] = (int)(turbo.factor * xit + turbo.xtrans);
            whys[1] = (int)(turbo.factor * rnoz + turbo.ytrans);
            exes[2] = (int)(turbo.factor * xflame + turbo.xtrans);
            whys[2] = (int)(turbo.factor * rthroat + turbo.ytrans);
            exes[3] = (int)(turbo.factor * xturb + turbo.xtrans);
            whys[3] = (int)(turbo.factor * .9 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            whys[0] = (int)(turbo.factor * (-hblade - liprad) + turbo.ytrans);
            whys[1] = (int)(turbo.factor * -rnoz + turbo.ytrans);
            whys[2] = (int)(turbo.factor * -rthroat + turbo.ytrans);
            whys[3] = (int)(turbo.factor * -.9 * hblade + turbo.ytrans);
            turbo.offsGg.fillPolygon(exes, whys, 4);

            //   show stations
            if(turbo.showcom == 1) {
                turbo.offsGg.setColor(Color.white);
                ylabel = (int)(turbo.factor * 1.5 * hblade + 20. + turbo.ytrans);
                whys[1] = 370;

                xl = xcomp - .1;                   /* burner entrance */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
                turbo.offsGg.drawString("3", xlabel, ylabel);

                xl = xnoz + .1 * turbo.lnoz;        /* flame holders */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * .2 + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
                turbo.offsGg.drawString("4", xlabel, ylabel);

                xl = xflame;               /* Afterburner exit */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * .2 + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
                turbo.offsGg.drawString("7", xlabel, ylabel);

                xl = xit;                    /* nozzle exit */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * .2 + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] - (int)(turbo.factor * .2);
                turbo.offsGg.drawString("8", xlabel, ylabel);

                xl = -2.0;                   /* free stream */
                exes[0] = exes[1] = (int)(turbo.factor * xl + turbo.xtrans);
                whys[0] = (int)(turbo.factor * (hblade - .2) + turbo.ytrans);
                turbo.offsGg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                xlabel = exes[0] + (int)(turbo.factor * .05);
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
            exes[1] = (int)(turbo.factor * turbo.xg[j][0] + turbo.xtrans);
            whys[1] = (int)(turbo.factor * turbo.yg[j][0] + turbo.ytrans);
            for (i = 1; i <= 34; ++i) {
                exes[0] = exes[1];
                whys[0] = whys[1];
                exes[1] = (int)(turbo.factor * turbo.xg[j][i] + turbo.xtrans);
                whys[1] = (int)(turbo.factor * turbo.yg[j][i] + turbo.ytrans);
                if((i - turbo.antim) / 3 * 3 == (i - turbo.antim)) {
                    if(i < 15) {
                        if(turbo.ancol == -1) {
                            if((i - turbo.antim) / 6 * 6 == (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.white);
                            }
                            if((i - turbo.antim) / 6 * 6 != (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                        }
                        if(turbo.ancol == 1) {
                            if((i - turbo.antim) / 6 * 6 == (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                            if((i - turbo.antim) / 6 * 6 != (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.white);
                            }
                        }
                    }
                    if(i >= 16) {
                        if(turbo.ancol == -1) {
                            if((i - turbo.antim) / 6 * 6 == (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.yellow);
                            }
                            if((i - turbo.antim) / 6 * 6 != (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.red);
                            }
                        }
                        if(turbo.ancol == 1) {
                            if((i - turbo.antim) / 6 * 6 == (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.red);
                            }
                            if((i - turbo.antim) / 6 * 6 != (i - turbo.antim)) {
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
                exes[1] = (int)(turbo.factor * turbo.xg[j][0] + turbo.xtrans);
                whys[1] = (int)(turbo.factor * turbo.yg[j][0] + turbo.ytrans);
                for (i = 1; i <= 34; ++i) {
                    exes[0] = exes[1];
                    whys[0] = whys[1];
                    exes[1] = (int)(turbo.factor * turbo.xg[j][i] + turbo.xtrans);
                    whys[1] = (int)(turbo.factor * turbo.yg[j][i] + turbo.ytrans);
                    if((i - turbo.antim) / 3 * 3 == (i - turbo.antim)) {
                        if(turbo.ancol == -1) {
                            if((i - turbo.antim) / 6 * 6 == (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.white);
                            }
                            if((i - turbo.antim) / 6 * 6 != (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                        }
                        if(turbo.ancol == 1) {
                            if((i - turbo.antim) / 6 * 6 == (i - turbo.antim)) {
                                turbo.offsGg.setColor(Color.cyan);
                            }
                            if((i - turbo.antim) / 6 * 6 != (i - turbo.antim)) {
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
        turbo.offsGg.fillRect(5, turbo.sldloc, 20, 5);

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
        if(turbo.fireflag == 1) {
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
 
