package gov.nasa.engine_sim_ua;

import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class OutputPanel extends Panel {

    private final Turbo turbo;
    OutputMainPanel outputMainPanel;
    OutputPlotCanvas outputPlotCanvas;
    OutputVariablesPanel outputVariablesPanel;

    OutputPanel(Turbo turbo) {
        this.turbo = turbo;

        turbo.layout = new CardLayout();
        setLayout(turbo.layout);

        outputMainPanel = new OutputMainPanel();
        outputPlotCanvas = new OutputPlotCanvas();
        outputVariablesPanel = new OutputVariablesPanel();

        add("first", outputMainPanel);
        add("second", outputPlotCanvas);
        add("third", outputVariablesPanel);
    }

    public class OutputMainPanel extends Panel {

        TextField o7, o8, o9, o13, o16, o17;
        TextField o18, o19, o20, o21, o22, o23, o24, o25;

        OutputMainPanel() {

            setLayout(new GridLayout(7, 4, 1, 5));

            o7 = new TextField();
            o7.setBackground(Color.black);
            o7.setForeground(Color.yellow);
            o8 = new TextField();
            o8.setBackground(Color.black);
            o8.setForeground(Color.yellow);
            o9 = new TextField();
            o9.setBackground(Color.black);
            o9.setForeground(Color.yellow);
            o13 = new TextField();
            o13.setBackground(Color.black);
            o13.setForeground(Color.yellow);
            o16 = new TextField();
            o16.setBackground(Color.black);
            o16.setForeground(Color.yellow);
            o17 = new TextField();
            o17.setBackground(Color.black);
            o17.setForeground(Color.yellow);
            o18 = new TextField();
            o18.setBackground(Color.black);
            o18.setForeground(Color.yellow);
            o19 = new TextField();
            o19.setBackground(Color.black);
            o19.setForeground(Color.yellow);
            o20 = new TextField();
            o20.setBackground(Color.black);
            o20.setForeground(Color.yellow);
            o21 = new TextField();
            o21.setBackground(Color.black);
            o21.setForeground(Color.yellow);
            o22 = new TextField();
            o22.setBackground(Color.black);
            o22.setForeground(Color.yellow);
            o23 = new TextField();
            o23.setBackground(Color.black);
            o23.setForeground(Color.yellow);
            o24 = new TextField();
            o24.setBackground(Color.black);
            o24.setForeground(Color.yellow);
            o25 = new TextField();
            o25.setBackground(Color.black);
            o25.setForeground(Color.yellow);

            add(new Label("Fn/air", Label.CENTER));
            add(o13);
            add(new Label("fuel/air", Label.CENTER));
            add(o9);

            add(new Label("EPR ", Label.CENTER));
            add(o7);
            add(new Label("ETR ", Label.CENTER));
            add(o8);

            add(new Label("M2 ", Label.CENTER));
            add(o23);
            add(new Label("q0 ", Label.CENTER));
            add(o19);

            add(new Label("NPR ", Label.CENTER));
            add(o17);
            add(new Label("V-exit", Label.CENTER));
            add(o16);

            add(new Label("Pexit", Label.CENTER));
            add(o22);
            add(new Label("T8 ", Label.CENTER));
            add(o21);

            add(new Label("P FanPanel exit", Label.CENTER));
            add(o24);
            add(new Label(" ", Label.CENTER));
            add(o25);

            add(new Label("ISP", Label.CENTER));
            add(o20);
            add(new Label("Efficiency", Label.CENTER));
            add(o18);
        }

        public Insets getInsets() {
            return new Insets(5, 0, 0, 0);
        }

        public void loadOut() {
            String outfor, outful, outair, outvel, outprs, outtmp, outtim, outpri;
            int i1;

            outfor = " lbs";
            if(turbo.lunits == 1) {
                outfor = " N";
            }
            outful = " lb/hr";
            if(turbo.lunits == 1) {
                outful = " kg/hr";
            }
            outair = " lb/s";
            if(turbo.lunits == 1) {
                outair = " kg/s";
            }
            outvel = " fps";
            if(turbo.lunits == 1) {
                outvel = " mps";
            }
            outprs = " psf";
            if(turbo.lunits == 1) {
                outprs = " Pa";
            }
            outpri = " psi";
            if(turbo.lunits == 1) {
                outpri = " kPa";
            }
            outtmp = " R";
            if(turbo.lunits == 1) {
                outtmp = " K";
            }
            outtim = " sec";

            if(turbo.inptype == 0 || turbo.inptype == 2) {
                turbo.inputPanel.flightPanel.flightLeftPanel.o1.setText(String.valueOf(turbo.filter3(Turbo.fsmach)));
            }
            if(turbo.inptype == 1 || turbo.inptype == 3) {
                Turbo.vmn1 = Turbo.u0min;
                Turbo.vmx1 = Turbo.u0max;
                turbo.inputPanel.flightPanel.flightLeftPanel.f1.setText(String.valueOf(turbo.filter0(Turbo.u0d)));
                i1 = (int)(((Turbo.u0d - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
                turbo.inputPanel.flightPanel.flightRightPanel.s1.setValue(i1);
            }
            turbo.inputPanel.flightPanel.flightLeftPanel.o2.setText(String.valueOf(turbo.filter3(Turbo.psout * Turbo.pconv)));
            turbo.inputPanel.flightPanel.flightLeftPanel.o3.setText(String.valueOf(turbo.filter3(Turbo.tsout * Turbo.tconv - Turbo.tref)));
            if(turbo.lunits <= 1) {
                if(Turbo.etr >= 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText(String.valueOf(turbo.filter0(Turbo.fnlb * Turbo.fconv)) + outfor);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText(String.valueOf(turbo.filter0(Turbo.mconv1 * Turbo.fuelrat)) + outful);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText(String.valueOf(turbo.filter3(Turbo.sfc * Turbo.mconv1 / Turbo.fconv)));
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setText(String.valueOf(turbo.filter0(Turbo.fglb * Turbo.fconv)) + outfor);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setText(String.valueOf(turbo.filter0(Turbo.drlb * Turbo.fconv)) + outfor);
                }
                if(Turbo.etr < 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText("-");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setText(String.valueOf(turbo.filter0(Turbo.drlb * Turbo.fconv)) + outfor);
                }
                o7.setForeground(Color.yellow);
                o7.setText(String.valueOf(turbo.filter3(Turbo.epr)));
                o8.setForeground(Color.yellow);
                o8.setText(String.valueOf(turbo.filter3(Turbo.etr)));
                o9.setForeground(Color.yellow);
                o9.setText(String.valueOf(turbo.filter3(Turbo.fa)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setForeground(Color.yellow);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setText(String.valueOf(turbo.filter3(Turbo.mconv1 * Turbo.eair)) + outair);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o11.setForeground(Color.yellow);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o11.setText(String.valueOf(turbo.filter3(Turbo.fconv * Turbo.weight)) + outfor);
                turbo.inputPanel.sizePanel.sizeLeftPanel.f2.setText(String.valueOf(turbo.filter0(Turbo.fconv * Turbo.weight)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setForeground(Color.yellow);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setText(String.valueOf(turbo.filter3(Turbo.fnlb / Turbo.weight)));
                o13.setForeground(Color.yellow);
                o13.setText(String.valueOf(turbo.filter1(Turbo.fnet * Turbo.fconv / Turbo.mconv1)));
                o16.setForeground(Color.yellow);
                o16.setText(String.valueOf(turbo.filter0(Turbo.uexit * Turbo.lconv1)) + outvel);
                o17.setForeground(Color.yellow);
                o17.setText(String.valueOf(turbo.filter3(Turbo.npr)));
                o18.setForeground(Color.yellow);
                o18.setText(String.valueOf(turbo.filter3(Turbo.eteng)));
                o19.setForeground(Color.yellow);
                o19.setText(String.valueOf(turbo.filter0(Turbo.q0 * Turbo.fconv / Turbo.aconv)) + outprs);
                o20.setForeground(Color.yellow);
                o20.setText(String.valueOf(turbo.filter0(Turbo.isp)) + outtim);
                o21.setText(String.valueOf(turbo.filter0(Turbo.t8 * Turbo.tconv)) + outtmp);
                o22.setText(String.valueOf(turbo.filter3(Turbo.pexit * Turbo.pconv)) + outpri);
                o23.setText(String.valueOf(turbo.filter3(Turbo.m2)));
                if(turbo.entype == 2) {
                    o24.setText(String.valueOf(turbo.filter3(Turbo.pfexit * Turbo.pconv)) + outpri);
                } else {
                    o24.setText("-");
                }
            }
            if(turbo.lunits == 2) {
                if(Turbo.etr >= 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.green);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText(String.valueOf(turbo.filter3(100. * (Turbo.fnlb - Turbo.fnref)
                                                                                                                   / Turbo.fnref)));
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.green);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText(String.valueOf(turbo.filter3(100. * (Turbo.fuelrat - Turbo.fuelref)
                                                                                                                   / Turbo.fuelref)));
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.green);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText(String.valueOf(turbo.filter3(100. * (Turbo.sfc - Turbo.sfcref)
                                                                                                                   / Turbo.sfcref)));
                }
                if(Turbo.etr < 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText("-");
                }
                o7.setForeground(Color.green);
                o7.setText(String.valueOf(turbo.filter3(100. * (Turbo.epr - Turbo.epref) / Turbo.epref)));
                o8.setForeground(Color.green);
                o8.setText(String.valueOf(turbo.filter3(100. * (Turbo.etr - Turbo.etref) / Turbo.etref)));
                o9.setForeground(Color.green);
                o9.setText(String.valueOf(turbo.filter3(100. * (Turbo.fa - Turbo.faref) / Turbo.faref)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setForeground(Color.green);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setText(String.valueOf(turbo.filter3(100. * (Turbo.eair - Turbo.airref)
                                                                                                                / Turbo.airref)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o11.setForeground(Color.green);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o11.setText(String.valueOf(turbo.filter3(100. * (Turbo.weight - Turbo.wtref)
                                                                                                                / Turbo.wtref)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setForeground(Color.green);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setText(String.valueOf(turbo.filter3(100. * (Turbo.fnlb / Turbo.weight - Turbo.wfref)
                                                                                                                / Turbo.wfref)));
            }
        }
    } //  end OutputMainPanel Output

    public class OutputVariablesPanel extends Panel {

        TextField po1, po2, po3, po4, po5, po6, po7, po8;
        TextField to1, to2, to3, to4, to5, to6, to7, to8;
        Label lpa, lpb, lta, ltb;

        OutputVariablesPanel() {

            setLayout(new GridLayout(6, 6, 1, 5));

            po1 = new TextField();
            po1.setBackground(Color.black);
            po1.setForeground(Color.yellow);
            po2 = new TextField();
            po2.setBackground(Color.black);
            po2.setForeground(Color.yellow);
            po3 = new TextField();
            po3.setBackground(Color.black);
            po3.setForeground(Color.yellow);
            po4 = new TextField();
            po4.setBackground(Color.black);
            po4.setForeground(Color.yellow);
            po5 = new TextField();
            po5.setBackground(Color.black);
            po5.setForeground(Color.yellow);
            po6 = new TextField();
            po6.setBackground(Color.black);
            po6.setForeground(Color.yellow);
            po7 = new TextField();
            po7.setBackground(Color.black);
            po7.setForeground(Color.yellow);
            po8 = new TextField();
            po8.setBackground(Color.black);
            po8.setForeground(Color.yellow);

            to1 = new TextField();
            to1.setBackground(Color.black);
            to1.setForeground(Color.yellow);
            to2 = new TextField();
            to2.setBackground(Color.black);
            to2.setForeground(Color.yellow);
            to3 = new TextField();
            to3.setBackground(Color.black);
            to3.setForeground(Color.yellow);
            to4 = new TextField();
            to4.setBackground(Color.black);
            to4.setForeground(Color.yellow);
            to5 = new TextField();
            to5.setBackground(Color.black);
            to5.setForeground(Color.yellow);
            to6 = new TextField();
            to6.setBackground(Color.black);
            to6.setForeground(Color.yellow);
            to7 = new TextField();
            to7.setBackground(Color.black);
            to7.setForeground(Color.yellow);
            to8 = new TextField();
            to8.setBackground(Color.black);
            to8.setForeground(Color.yellow);

            lpa = new Label("Pres-psi", Label.CENTER);
            lpb = new Label("Pres-psi", Label.CENTER);
            lta = new Label("Temp-R", Label.CENTER);
            ltb = new Label("Temp-R", Label.CENTER);

            add(new Label(" ", Label.CENTER));
            add(new Label("Total ", Label.RIGHT));
            add(new Label("Press.", Label.LEFT));
            add(new Label("and", Label.CENTER));
            add(new Label("Temp.", Label.LEFT));
            add(new Label(" ", Label.CENTER));

            add(new Label("Station", Label.CENTER));
            add(lpa);
            add(lta);
            add(new Label("Station", Label.CENTER));
            add(lpb);
            add(ltb);

            add(new Label("1", Label.CENTER));
            add(po1);
            add(to1);
            add(new Label("5", Label.CENTER));
            add(po5);
            add(to5);

            add(new Label("2", Label.CENTER));
            add(po2);
            add(to2);
            add(new Label("6", Label.CENTER));
            add(po6);
            add(to6);

            add(new Label("3", Label.CENTER));
            add(po3);
            add(to3);
            add(new Label("7", Label.CENTER));
            add(po7);
            add(to7);

            add(new Label("4", Label.CENTER));
            add(po4);
            add(to4);
            add(new Label("8", Label.CENTER));
            add(po8);
            add(to8);
        }

        public Insets getInsets() {
            return new Insets(5, 0, 0, 0);
        }

        public void loadOut() {
            po1.setText(String.valueOf(turbo.filter1(Turbo.pt[2] * Turbo.pconv)));
            po2.setText(String.valueOf(turbo.filter1(Turbo.pt[13] * Turbo.pconv)));
            po3.setText(String.valueOf(turbo.filter1(Turbo.pt[3] * Turbo.pconv)));
            po4.setText(String.valueOf(turbo.filter1(Turbo.pt[4] * Turbo.pconv)));
            po5.setText(String.valueOf(turbo.filter1(Turbo.pt[5] * Turbo.pconv)));
            po6.setText(String.valueOf(turbo.filter1(Turbo.pt[15] * Turbo.pconv)));
            po7.setText(String.valueOf(turbo.filter1(Turbo.pt[7] * Turbo.pconv)));
            po8.setText(String.valueOf(turbo.filter1(Turbo.pt[8] * Turbo.pconv)));
            to1.setText(String.valueOf(turbo.filter0(Turbo.tt[2] * Turbo.tconv)));
            to2.setText(String.valueOf(turbo.filter0(Turbo.tt[13] * Turbo.tconv)));
            to3.setText(String.valueOf(turbo.filter0(Turbo.tt[3] * Turbo.tconv)));
            to4.setText(String.valueOf(turbo.filter0(Turbo.tt[4] * Turbo.tconv)));
            to5.setText(String.valueOf(turbo.filter0(Turbo.tt[5] * Turbo.tconv)));
            to6.setText(String.valueOf(turbo.filter0(Turbo.tt[15] * Turbo.tconv)));
            to7.setText(String.valueOf(turbo.filter0(Turbo.tt[7] * Turbo.tconv)));
            to8.setText(String.valueOf(turbo.filter0(Turbo.tt[8] * Turbo.tconv)));
        }
    } //  end OutputVariablesPanel Output

    public class OutputPlotCanvas extends Canvas {

        OutputPlotCanvas() {

            setBackground(Color.black);
            this.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    onMouseUp(e);
                }

                @Override
                public void mouseDragged(MouseEvent e) {
                    onMouseDrag(e);
                }
            });
        }

        public Insets getInsets() {
            return new Insets(0, 10, 0, 10);
        }

        public void onMouseDrag(MouseEvent event) {
            handle(event.getX(), event.getY());
        }

        public void onMouseUp(MouseEvent event) {
            handle(event.getX(), event.getY());
        }

        public void handle(int x, int y) {
            if(y <= 27) {    // labels
                if(x <= 71) {    // pressure variation
                    turbo.plttyp = 3;
                    if(turbo.pltkeep == 7) {
                        turbo.varflag = 0;
                        turbo.layin.show(turbo.inputPanel, "first");
                    }
                }
                if(x > 71 && x <= 151) {    // temperature variation
                    turbo.plttyp = 4;
                    if(turbo.pltkeep == 7) {
                        turbo.varflag = 0;
                        turbo.layin.show(turbo.inputPanel, "first");
                    }
                }
                if(x > 151 && x <= 181) {    //  T - s
                    turbo.plttyp = 5;
                    if(turbo.pltkeep == 7) {
                        turbo.varflag = 0;
                        turbo.layin.show(turbo.inputPanel, "first");
                    }
                }
                if(x > 181 && x <= 211) {    //  p - v
                    turbo.plttyp = 6;
                    if(turbo.pltkeep == 7) {
                        turbo.varflag = 0;
                        turbo.layin.show(turbo.inputPanel, "first");
                    }
                }
                if(x > 211 && x < 290) {    //  generate plotPanel
                    turbo.plttyp = 7;
                    turbo.layin.show(turbo.inputPanel, "ninth");
                    turbo.lunits = 0;
                    turbo.varflag = 0;
                    turbo.flightConditionsPanel.setUnits();
                    turbo.flightConditionsPanel.flightConditionsUpperPanel.chcUnits.select(turbo.lunits);
                    turbo.inputPanel.plotPanel.plotLeftPanel.ordch.select(0);
                    turbo.inputPanel.plotPanel.plotLeftPanel.absch.select(0);
                }
                turbo.pltkeep = turbo.plttyp;
                turbo.flightConditionsPanel.setPlot();
            }
            if(y > 27) {
                if(x >= 256) {   // zoom widget
                    Turbo.sldplt = y;
                    if(Turbo.sldplt < 45) {
                        Turbo.sldplt = 45;
                    }
                    if(Turbo.sldplt > 155) {
                        Turbo.sldplt = 155;
                    }
                    Turbo.factp = 120.0 - (Turbo.sldplt - 45) * 1.0;
                }
            }
            turbo.solve.comPute();
            outputPlotCanvas.repaint();
            return;
        }

        public void update(Graphics g) {
            outputPlotCanvas.paint(g);
        }

        public void loadPlot() {
            double cnst, delp;
            int ic;

            switch (turbo.plttyp) {
                case 3: {                       /*  press variation */
                    turbo.npt = 9;
                    Turbo.pltx[1] = 0.0;
                    Turbo.plty[1] = Turbo.ps0 * Turbo.pconv;
                    Turbo.pltx[2] = 1.0;
                    Turbo.plty[2] = Turbo.pt[2] * Turbo.pconv;
                    Turbo.pltx[3] = 2.0;
                    Turbo.plty[3] = Turbo.pt[13] * Turbo.pconv;
                    Turbo.pltx[4] = 3.0;
                    Turbo.plty[4] = Turbo.pt[3] * Turbo.pconv;
                    Turbo.pltx[5] = 4.0;
                    Turbo.plty[5] = Turbo.pt[4] * Turbo.pconv;
                    Turbo.pltx[6] = 5.0;
                    Turbo.plty[6] = Turbo.pt[5] * Turbo.pconv;
                    Turbo.pltx[7] = 6.0;
                    Turbo.plty[7] = Turbo.pt[15] * Turbo.pconv;
                    Turbo.pltx[8] = 7.0;
                    Turbo.plty[8] = Turbo.pt[7] * Turbo.pconv;
                    Turbo.pltx[9] = 8.0;
                    Turbo.plty[9] = Turbo.pt[8] * Turbo.pconv;
                    return;
                }
                case 4: {                       /*  temp variation */
                    turbo.npt = 9;
                    Turbo.pltx[1] = 0.0;
                    Turbo.plty[1] = Turbo.ts0 * Turbo.tconv;
                    Turbo.pltx[2] = 1.0;
                    Turbo.plty[2] = Turbo.tt[2] * Turbo.tconv;
                    Turbo.pltx[3] = 2.0;
                    Turbo.plty[3] = Turbo.tt[13] * Turbo.tconv;
                    Turbo.pltx[4] = 3.0;
                    Turbo.plty[4] = Turbo.tt[3] * Turbo.tconv;
                    Turbo.pltx[5] = 4.0;
                    Turbo.plty[5] = Turbo.tt[4] * Turbo.tconv;
                    Turbo.pltx[6] = 5.0;
                    Turbo.plty[6] = Turbo.tt[5] * Turbo.tconv;
                    Turbo.pltx[7] = 6.0;
                    Turbo.plty[7] = Turbo.tt[15] * Turbo.tconv;
                    Turbo.pltx[8] = 7.0;
                    Turbo.plty[8] = Turbo.tt[7] * Turbo.tconv;
                    Turbo.pltx[9] = 8.0;
                    Turbo.plty[9] = Turbo.tt[8] * Turbo.tconv;
                    return;
                }
                case 5: {                       /*  t-s plotPanel */
                    turbo.npt = 7;
                    Turbo.pltx[1] = Turbo.s[0] * Turbo.bconv;
                    Turbo.plty[1] = Turbo.ts0 * Turbo.tconv;
                    for (ic = 2; ic <= 5; ++ic) {
                        Turbo.pltx[ic] = Turbo.s[ic] * Turbo.bconv;
                        Turbo.plty[ic] = Turbo.tt[ic] * Turbo.tconv;
                    }
                    Turbo.pltx[6] = Turbo.s[7] * Turbo.bconv;
                    Turbo.plty[6] = Turbo.tt[7] * Turbo.tconv;
                    Turbo.pltx[7] = Turbo.s[8] * Turbo.bconv;
                    Turbo.plty[7] = Turbo.t8 * Turbo.tconv;
                    return;
                }
                case 6: {                        /*  p-v plotPanel */
                    turbo.npt = 25;
                    Turbo.plty[1] = Turbo.ps0 * Turbo.pconv;
                    Turbo.pltx[1] = Turbo.v[0] * Turbo.dconv;
                    cnst = Turbo.plty[1] * Math.pow(Turbo.pltx[1], Turbo.gama);
                    Turbo.plty[11] = Turbo.pt[3] * Turbo.pconv;
                    Turbo.pltx[11] = Turbo.v[3] * Turbo.dconv;
                    delp = (Turbo.plty[11] - Turbo.plty[1]) / 11.0;
                    for (ic = 2; ic <= 10; ++ic) {
                        Turbo.plty[ic] = Turbo.plty[1] + ic * delp;
                        Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama);
                    }
                    Turbo.plty[12] = Turbo.pt[4] * Turbo.pconv;
                    Turbo.pltx[12] = Turbo.v[4] * Turbo.dconv;
                    cnst = Turbo.plty[12] * Math.pow(Turbo.pltx[12], Turbo.gama);
                    if(turbo.abflag == 1) {
                        Turbo.plty[25] = Turbo.ps0 * Turbo.pconv;
                        Turbo.pltx[25] = Turbo.v[8] * Turbo.dconv;
                        delp = (Turbo.plty[25] - Turbo.plty[12]) / 13.0;
                        for (ic = 13; ic <= 24; ++ic) {
                            Turbo.plty[ic] = Turbo.plty[12] + (ic - 12) * delp;
                            Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama);
                        }
                    } else {
                        Turbo.plty[18] = Turbo.pt[5] * Turbo.pconv;
                        Turbo.pltx[18] = Turbo.v[5] * Turbo.dconv;
                        delp = (Turbo.plty[18] - Turbo.plty[12]) / 6.0;
                        for (ic = 13; ic <= 17; ++ic) {
                            Turbo.plty[ic] = Turbo.plty[12] + (ic - 12) * delp;
                            Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama);
                        }
                        Turbo.plty[19] = Turbo.pt[7] * Turbo.pconv;
                        Turbo.pltx[19] = Turbo.v[7] * Turbo.dconv;
                        cnst = Turbo.plty[19] * Math.pow(Turbo.pltx[19], Turbo.gama);
                        Turbo.plty[25] = Turbo.ps0 * Turbo.pconv;
                        Turbo.pltx[25] = Turbo.v[8] * Turbo.dconv;
                        delp = (Turbo.plty[25] - Turbo.plty[19]) / 6.0;
                        for (ic = 20; ic <= 24; ++ic) {
                            Turbo.plty[ic] = Turbo.plty[19] + (ic - 19) * delp;
                            Turbo.pltx[ic] = Math.pow(cnst / Turbo.plty[ic], 1.0 / Turbo.gama);
                        }
                    }
                    return;
                }
                case 7:
                    break;                   /* create plotPanel */
            }
        }

        public void paint(Graphics g) {
            //          int iwidth = partimg.getWidth(this) ;
            //         int iheight = partimg.getHeight(this) ;
            int i, j, k;
            int exes[] = new int[8];
            int whys[] = new int[8];
            int xlabel, ylabel, ind;
            double xl, yl;
            double offx, scalex, offy, scaley, waste, incy, incx;

            if(turbo.plttyp >= 3 && turbo.plttyp <= 7) {         //  perform a plotPanel
                turbo.off1Gg.setColor(Color.blue);
                turbo.off1Gg.fillRect(0, 0, 350, 350);

                if(turbo.ntikx < 2) {
                    turbo.ntikx = 2;     /* protection 13June96 */
                }
                if(turbo.ntiky < 2) {
                    turbo.ntiky = 2;
                }
                offx = 0.0 - Turbo.begx;
                scalex = 6.5 / (Turbo.endx - Turbo.begx);
                incx = (Turbo.endx - Turbo.begx) / (turbo.ntikx - 1);
                offy = 0.0 - Turbo.begy;
                scaley = 10.0 / (Turbo.endy - Turbo.begy);
                incy = (Turbo.endy - Turbo.begy) / (turbo.ntiky - 1);
                                        /* draw axes */
                turbo.off1Gg.setColor(Color.white);
                exes[0] = (int)(Turbo.factp * 0.0 + Turbo.xtranp);
                whys[0] = (int)(-150. + Turbo.ytranp);
                exes[1] = (int)(Turbo.factp * 0.0 + Turbo.xtranp);
                whys[1] = (int)(Turbo.factp * 0.0 + Turbo.ytranp);
                exes[2] = (int)(215. + Turbo.xtranp);
                whys[2] = (int)(Turbo.factp * 0.0 + Turbo.ytranp);
                turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                turbo.off1Gg.drawLine(exes[1], whys[1], exes[2], whys[2]);

                xlabel = (int)(-75. + Turbo.xtranp);      /*     label y axis */
                ylabel = (int)(-65. + Turbo.ytranp);
                turbo.off1Gg.drawString(Turbo.laby, xlabel, ylabel);
                turbo.off1Gg.drawString(Turbo.labyu, xlabel, ylabel + 20);
                                         /* add tick values */
                for (ind = 1; ind <= turbo.ntiky; ++ind) {
                    xlabel = (int)(-33. + Turbo.xtranp);
                    yl = Turbo.begy + (ind - 1) * incy;
                    ylabel = (int)(Turbo.factp * -scaley * yl + Turbo.ytranp);
                    if(turbo.nord != 5) {
                        turbo.off1Gg.drawString(String.valueOf((int)yl), xlabel, ylabel);
                    } else {
                        turbo.off1Gg.drawString(String.valueOf(turbo.filter3(yl)), xlabel, ylabel);
                    }
                }
                xlabel = (int)(75. + Turbo.xtranp);       /*   label x axis */
                ylabel = (int)(20. + Turbo.ytranp);
                turbo.off1Gg.drawString(Turbo.labx, xlabel, ylabel);
                turbo.off1Gg.drawString(Turbo.labxu, xlabel + 50, ylabel);
                                         /* add tick values */
                for (ind = 1; ind <= turbo.ntikx; ++ind) {
                    ylabel = (int)(10. + Turbo.ytranp);
                    xl = Turbo.begx + (ind - 1) * incx;
                    xlabel = (int)(33. * (scalex * (xl + offx) - .05) + Turbo.xtranp);
                    if(turbo.nabs >= 2 && turbo.nabs <= 3) {
                        turbo.off1Gg.drawString(String.valueOf(turbo.filter3(xl)), xlabel, ylabel);
                    }
                    if(turbo.nabs < 2 || turbo.nabs > 3) {
                        turbo.off1Gg.drawString(String.valueOf((int)xl), xlabel, ylabel);
                    }
                }

                if(turbo.lines == 0) {
                    for (i = 1; i <= turbo.npt; ++i) {
                        xlabel = (int)(33. * scalex * (offx + Turbo.pltx[i]) + Turbo.xtranp);
                        ylabel = (int)(Turbo.factp * -scaley * (offy + Turbo.plty[i]) + Turbo.ytranp + 7.);
                        turbo.off1Gg.drawString("*", xlabel, ylabel);
                    }
                } else {
                    exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[1]) + Turbo.xtranp);
                    whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.plty[1]) + Turbo.ytranp);
                    for (i = 2; i <= turbo.npt; ++i) {
                        exes[0] = exes[1];
                        whys[0] = whys[1];
                        exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[i]) + Turbo.xtranp);
                        whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.plty[i]) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    }
                }
                if(turbo.plttyp == 4) {       // draw temp limits
                    turbo.off1Gg.setColor(Color.yellow);
                    if(turbo.entype < 3) {
                        exes[0] = (int)(33. * scalex * (offx + Turbo.pltx[0]) + Turbo.xtranp);
                        whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                        exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[1]) + Turbo.xtranp);
                        whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        turbo.off1Gg.drawString("Limit", exes[0] + 5, whys[0]);
                        exes[0] = (int)(33. * scalex * (offx + Turbo.pltx[2]) + Turbo.xtranp);
                        whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[3]) + Turbo.xtranp);
                        whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                        if(turbo.entype == 2) {
                            whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tfan) + Turbo.ytranp);
                        }
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + Turbo.pltx[4]) + Turbo.xtranp);
                        whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tcomp) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[5]) + Turbo.xtranp);
                        whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tburner) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + Turbo.pltx[6]) + Turbo.xtranp);
                        whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tturbin) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[6]) + Turbo.xtranp);
                        whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tnozl) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + Turbo.pltx[9]) + Turbo.xtranp);
                        whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tnozl) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    }
                    if(turbo.entype == 3) {
                        exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[0]) + Turbo.xtranp);
                        whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                        exes[0] = (int)(33. * scalex * (offx + Turbo.pltx[4]) + Turbo.xtranp);
                        whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tinlt) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        turbo.off1Gg.drawString("Limit", exes[1] + 5, whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + Turbo.pltx[5]) + Turbo.xtranp);
                        whys[1] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tburner) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + Turbo.pltx[9]) + Turbo.xtranp);
                        whys[0] = (int)(Turbo.factp * -scaley * (offy + Turbo.tconv * Turbo.tnozr) + Turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    }
                }
                // plotPanel  labels
                turbo.off1Gg.setColor(Color.blue);
                turbo.off1Gg.fillRect(0, 0, 300, 27);
                turbo.off1Gg.setColor(Color.white);
                if(turbo.plttyp == 3) {
                    turbo.off1Gg.setColor(Color.yellow);
                    turbo.off1Gg.fillRect(0, 0, 70, 12);
                    turbo.off1Gg.setColor(Color.black);
                }
                turbo.off1Gg.drawString("Pressure", 10, 10);
                turbo.off1Gg.setColor(Color.white);
                if(turbo.plttyp == 4) {
                    turbo.off1Gg.setColor(Color.yellow);
                    turbo.off1Gg.fillRect(71, 0, 80, 12);
                    turbo.off1Gg.setColor(Color.black);
                }
                turbo.off1Gg.drawString("Temperature", 75, 10);
                turbo.off1Gg.setColor(Color.white);
                if(turbo.plttyp == 5) {
                    turbo.off1Gg.setColor(Color.yellow);
                    turbo.off1Gg.fillRect(151, 0, 30, 12);
                    turbo.off1Gg.setColor(Color.black);
                }
                turbo.off1Gg.drawString("T-s", 155, 10);
                turbo.off1Gg.setColor(Color.white);
                if(turbo.plttyp == 6) {
                    turbo.off1Gg.setColor(Color.yellow);
                    turbo.off1Gg.fillRect(181, 0, 30, 12);
                    turbo.off1Gg.setColor(Color.black);
                }
                turbo.off1Gg.drawString("P-v", 185, 10);
                turbo.off1Gg.setColor(Color.white);
                if(turbo.plttyp == 7) {
                    turbo.off1Gg.setColor(Color.yellow);
                    turbo.off1Gg.fillRect(211, 0, 80, 12);
                    turbo.off1Gg.setColor(Color.black);
                }
                turbo.off1Gg.drawString("Generate", 220, 10);
                // zoom widget
                turbo.off1Gg.setColor(Color.blue);
                turbo.off1Gg.fillRect(305, 15, 35, 145);
                turbo.off1Gg.setColor(Color.white);
                turbo.off1Gg.drawString("Scale", 305, 25);
                turbo.off1Gg.drawLine(320, 35, 320, 155);
                turbo.off1Gg.fillRect(310, Turbo.sldplt, 20, 5);
            }

            if(turbo.plttyp == 2) {           // draw photo
                turbo.off1Gg.setColor(Color.white);
                turbo.off1Gg.fillRect(0, 0, 350, 350);
            }

            g.drawImage(turbo.offImg1, 0, 0, this);
        }  // end paint
    }  // end PlotPanel Output
} //  end Output panel
 
