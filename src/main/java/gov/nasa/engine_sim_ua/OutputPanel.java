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

        TextField o7;
        TextField o8;
        TextField o9;
        TextField o13;
        TextField o16;
        TextField o17;
        TextField o18;
        TextField o19;
        TextField o20;
        TextField o21;
        TextField o22;
        TextField o23;
        TextField o24;
        TextField o25;

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
            String outfor;
            String outful;
            String outair;
            String outvel;
            String outprs;
            String outtmp;
            String outtim;
            String outpri;
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
                turbo.inputPanel.flightPanel.flightLeftPanel.o1.setText(String.format("%.3f", turbo.fsmach));
            }
            if(turbo.inptype == 1 || turbo.inptype == 3) {
                turbo.vmn1 = turbo.u0min;
                turbo.vmx1 = turbo.u0max;
                turbo.inputPanel.flightPanel.flightLeftPanel.f1.setText(String.format("%.0f", turbo.u0d));
                i1 = (int)(((turbo.u0d - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
                turbo.inputPanel.flightPanel.flightRightPanel.s1.setValue(i1);
            }
            turbo.inputPanel.flightPanel.flightLeftPanel.o2.setText(String.format("%.3f", turbo.psout * turbo.pconv));
            turbo.inputPanel.flightPanel.flightLeftPanel.o3.setText(String.format("%.3f", turbo.tsout * turbo.tconv - turbo.tref));
            if(turbo.lunits <= 1) {
                if(turbo.etr >= 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText(String.format("%.0f", turbo.fnlb * turbo.fconv) + outfor);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText(String.format("%.0f", turbo.mconv1 * turbo.fuelrat) + outful);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText(String.format("%.3f",
                                                                                                                   turbo.sfc * turbo.mconv1 / turbo.fconv));
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setText(String.format("%.0f", turbo.fglb * turbo.fconv) + outfor);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setText(String.format("%.0f", turbo.drlb * turbo.fconv) + outfor);
                }
                if(turbo.etr < 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText("-");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o14.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o15.setText(String.format("%.0f", turbo.drlb * turbo.fconv) + outfor);
                }
                o7.setForeground(Color.yellow);
                o7.setText(String.format("%.3f", turbo.epr));
                o8.setForeground(Color.yellow);
                o8.setText(String.format("%.3f", turbo.etr));
                o9.setForeground(Color.yellow);
                o9.setText(String.format("%.3f", turbo.fa));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setForeground(Color.yellow);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setText(String.format("%.3f", turbo.mconv1 * turbo.eair) + outair);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.tfWeight.setForeground(Color.yellow);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.tfWeight.setText((String.format("%.3f", turbo.fconv * turbo.weight)) + outfor);
                turbo.inputPanel.sizePanel.sizeLeftPanel.f2.setText(String.format("%.0f", turbo.fconv * turbo.weight));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setForeground(Color.yellow);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setText(String.format("%.3f", turbo.fnlb / turbo.weight));
                o13.setForeground(Color.yellow);
                o13.setText(String.format("%.1f", turbo.fnet * turbo.fconv / turbo.mconv1));
                o16.setForeground(Color.yellow);
                o16.setText(String.format("%.0f", turbo.uexit * turbo.lconv1) + outvel);
                o17.setForeground(Color.yellow);
                o17.setText(String.format("%.3f", turbo.npr));
                o18.setForeground(Color.yellow);
                o18.setText(String.format("%.3f", turbo.eteng));
                o19.setForeground(Color.yellow);
                o19.setText(String.format("%.0f", turbo.q0 * turbo.fconv / turbo.aconv) + outprs);
                o20.setForeground(Color.yellow);
                o20.setText(String.format("%.0f", turbo.isp) + outtim);
                o21.setText(String.format("%.0f", turbo.t8 * turbo.tconv) + outtmp);
                o22.setText(String.format("%.3f", turbo.pexit * turbo.pconv) + outpri);
                o23.setText(String.format("%.3f", turbo.m2));
                if(turbo.entype == 2) {
                    o24.setText(String.format("%.3f", turbo.pfexit * turbo.pconv) + outpri);
                } else {
                    o24.setText("-");
                }
            }
            if(turbo.lunits == 2) {
                if(turbo.etr >= 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.green);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText(String.valueOf(String.format("%.3f", 100. * (turbo.fnlb - turbo.fnref)
                                                                                                                           / turbo.fnref)));
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.green);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText(String.valueOf(String.format("%.3f",
                                                                                                                   100. * (turbo.fuelrat - turbo.fuelref)
                                                                                                                   / turbo.fuelref)));
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.green);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText(String.valueOf(String.format("%.3f", 100. * (turbo.sfc - turbo.sfcref)
                                                                                                                           / turbo.sfcref)));
                }
                if(turbo.etr < 1.0) {
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o4.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o5.setText("0.0");
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setForeground(Color.yellow);
                    turbo.flightConditionsPanel.flightConditionsLowerPanel.o6.setText("-");
                }
                o7.setForeground(Color.green);
                o7.setText(String.valueOf(String.format("%.3f", 100. * (turbo.epr - turbo.epref) / turbo.epref)));
                o8.setForeground(Color.green);
                o8.setText(String.valueOf(String.format("%.3f", 100. * (turbo.etr - turbo.etref) / turbo.etref)));
                o9.setForeground(Color.green);
                o9.setText(String.valueOf(String.format("%.3f", 100. * (turbo.fa - turbo.faref) / turbo.faref)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setForeground(Color.green);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o10.setText(String.valueOf(String.format("%.3f", 100. * (turbo.eair - turbo.airref)
                                                                                                                        / turbo.airref)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.tfWeight.setForeground(Color.green);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.tfWeight.setText(String.valueOf(String.format("%.3f", 100. * (turbo.weight - turbo.wtref)
                                                                                                                             / turbo.wtref)));
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setForeground(Color.green);
                turbo.flightConditionsPanel.flightConditionsLowerPanel.o12.setText(String.valueOf(String.format("%.3f",
                                                                                                                100. * (turbo.fnlb / turbo.weight - turbo.wfref)
                                                                                                                / turbo.wfref)));
            }
        }
    } //  end OutputMainPanel Output

    public class OutputVariablesPanel extends Panel {

        TextField po1;
        TextField po2;
        TextField po3;
        TextField po4;
        TextField po5;
        TextField po6;
        TextField po7;
        TextField po8;
        TextField to1;
        TextField to2;
        TextField to3;
        TextField to4;
        TextField to5;
        TextField to6;
        TextField to7;
        TextField to8;
        Label lpa;
        Label lpb;
        Label lta;
        Label ltb;

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
            po1.setText(String.format("%.1f", turbo.pt[2] * turbo.pconv));
            po2.setText(String.format("%.1f", turbo.pt[13] * turbo.pconv));
            po3.setText(String.format("%.1f", turbo.pt[3] * turbo.pconv));
            po4.setText(String.format("%.1f", turbo.pt[4] * turbo.pconv));
            po5.setText(String.format("%.1f", turbo.pt[5] * turbo.pconv));
            po6.setText(String.format("%.1f", turbo.pt[15] * turbo.pconv));
            po7.setText(String.format("%.1f", turbo.pt[7] * turbo.pconv));
            po8.setText(String.format("%.1f", turbo.pt[8] * turbo.pconv));
            to1.setText(String.format("%.0f", turbo.tt[2] * turbo.tconv));
            to2.setText(String.format("%.0f", turbo.tt[13] * turbo.tconv));
            to3.setText(String.format("%.0f", turbo.tt[3] * turbo.tconv));
            to4.setText(String.format("%.0f", turbo.tt[4] * turbo.tconv));
            to5.setText(String.format("%.0f", turbo.tt[5] * turbo.tconv));
            to6.setText(String.format("%.0f", turbo.tt[15] * turbo.tconv));
            to7.setText(String.format("%.0f", turbo.tt[7] * turbo.tconv));
            to8.setText(String.format("%.0f", turbo.tt[8] * turbo.tconv));
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
                    turbo.sldplt = y;
                    if(turbo.sldplt < 45) {
                        turbo.sldplt = 45;
                    }
                    if(turbo.sldplt > 155) {
                        turbo.sldplt = 155;
                    }
                    turbo.factp = 120.0 - (turbo.sldplt - 45) * 1.0;
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
            double cnst;
            double delp;
            int ic;

            switch (turbo.plttyp) {
                case 3: {                       /*  press variation */
                    turbo.npt = 9;
                    turbo.pltx[1] = 0.0;
                    turbo.plty[1] = turbo.ps0 * turbo.pconv;
                    turbo.pltx[2] = 1.0;
                    turbo.plty[2] = turbo.pt[2] * turbo.pconv;
                    turbo.pltx[3] = 2.0;
                    turbo.plty[3] = turbo.pt[13] * turbo.pconv;
                    turbo.pltx[4] = 3.0;
                    turbo.plty[4] = turbo.pt[3] * turbo.pconv;
                    turbo.pltx[5] = 4.0;
                    turbo.plty[5] = turbo.pt[4] * turbo.pconv;
                    turbo.pltx[6] = 5.0;
                    turbo.plty[6] = turbo.pt[5] * turbo.pconv;
                    turbo.pltx[7] = 6.0;
                    turbo.plty[7] = turbo.pt[15] * turbo.pconv;
                    turbo.pltx[8] = 7.0;
                    turbo.plty[8] = turbo.pt[7] * turbo.pconv;
                    turbo.pltx[9] = 8.0;
                    turbo.plty[9] = turbo.pt[8] * turbo.pconv;
                    return;
                }
                case 4: {                       /*  temp variation */
                    turbo.npt = 9;
                    turbo.pltx[1] = 0.0;
                    turbo.plty[1] = turbo.ts0 * turbo.tconv;
                    turbo.pltx[2] = 1.0;
                    turbo.plty[2] = turbo.tt[2] * turbo.tconv;
                    turbo.pltx[3] = 2.0;
                    turbo.plty[3] = turbo.tt[13] * turbo.tconv;
                    turbo.pltx[4] = 3.0;
                    turbo.plty[4] = turbo.tt[3] * turbo.tconv;
                    turbo.pltx[5] = 4.0;
                    turbo.plty[5] = turbo.tt[4] * turbo.tconv;
                    turbo.pltx[6] = 5.0;
                    turbo.plty[6] = turbo.tt[5] * turbo.tconv;
                    turbo.pltx[7] = 6.0;
                    turbo.plty[7] = turbo.tt[15] * turbo.tconv;
                    turbo.pltx[8] = 7.0;
                    turbo.plty[8] = turbo.tt[7] * turbo.tconv;
                    turbo.pltx[9] = 8.0;
                    turbo.plty[9] = turbo.tt[8] * turbo.tconv;
                    return;
                }
                case 5: {                       /*  t-s plotPanel */
                    turbo.npt = 7;
                    turbo.pltx[1] = turbo.s[0] * turbo.bconv;
                    turbo.plty[1] = turbo.ts0 * turbo.tconv;
                    for (ic = 2; ic <= 5; ++ic) {
                        turbo.pltx[ic] = turbo.s[ic] * turbo.bconv;
                        turbo.plty[ic] = turbo.tt[ic] * turbo.tconv;
                    }
                    turbo.pltx[6] = turbo.s[7] * turbo.bconv;
                    turbo.plty[6] = turbo.tt[7] * turbo.tconv;
                    turbo.pltx[7] = turbo.s[8] * turbo.bconv;
                    turbo.plty[7] = turbo.t8 * turbo.tconv;
                    return;
                }
                case 6: {                        /*  p-v plotPanel */
                    turbo.npt = 25;
                    turbo.plty[1] = turbo.ps0 * turbo.pconv;
                    turbo.pltx[1] = turbo.v[0] * turbo.dconv;
                    cnst = turbo.plty[1] * Math.pow(turbo.pltx[1], turbo.gama);
                    turbo.plty[11] = turbo.pt[3] * turbo.pconv;
                    turbo.pltx[11] = turbo.v[3] * turbo.dconv;
                    delp = (turbo.plty[11] - turbo.plty[1]) / 11.0;
                    for (ic = 2; ic <= 10; ++ic) {
                        turbo.plty[ic] = turbo.plty[1] + ic * delp;
                        turbo.pltx[ic] = Math.pow(cnst / turbo.plty[ic], 1.0 / turbo.gama);
                    }
                    turbo.plty[12] = turbo.pt[4] * turbo.pconv;
                    turbo.pltx[12] = turbo.v[4] * turbo.dconv;
                    cnst = turbo.plty[12] * Math.pow(turbo.pltx[12], turbo.gama);
                    if(turbo.abflag == 1) {
                        turbo.plty[25] = turbo.ps0 * turbo.pconv;
                        turbo.pltx[25] = turbo.v[8] * turbo.dconv;
                        delp = (turbo.plty[25] - turbo.plty[12]) / 13.0;
                        for (ic = 13; ic <= 24; ++ic) {
                            turbo.plty[ic] = turbo.plty[12] + (ic - 12) * delp;
                            turbo.pltx[ic] = Math.pow(cnst / turbo.plty[ic], 1.0 / turbo.gama);
                        }
                    } else {
                        turbo.plty[18] = turbo.pt[5] * turbo.pconv;
                        turbo.pltx[18] = turbo.v[5] * turbo.dconv;
                        delp = (turbo.plty[18] - turbo.plty[12]) / 6.0;
                        for (ic = 13; ic <= 17; ++ic) {
                            turbo.plty[ic] = turbo.plty[12] + (ic - 12) * delp;
                            turbo.pltx[ic] = Math.pow(cnst / turbo.plty[ic], 1.0 / turbo.gama);
                        }
                        turbo.plty[19] = turbo.pt[7] * turbo.pconv;
                        turbo.pltx[19] = turbo.v[7] * turbo.dconv;
                        cnst = turbo.plty[19] * Math.pow(turbo.pltx[19], turbo.gama);
                        turbo.plty[25] = turbo.ps0 * turbo.pconv;
                        turbo.pltx[25] = turbo.v[8] * turbo.dconv;
                        delp = (turbo.plty[25] - turbo.plty[19]) / 6.0;
                        for (ic = 20; ic <= 24; ++ic) {
                            turbo.plty[ic] = turbo.plty[19] + (ic - 19) * delp;
                            turbo.pltx[ic] = Math.pow(cnst / turbo.plty[ic], 1.0 / turbo.gama);
                        }
                    }
                    return;
                }
                case 7:
                    break;                   /* create plotPanel */
            }
        }

        public void paint(Graphics g) {
            //          int iwidth = partimg.getWidth(this);
            //         int iheight = partimg.getHeight(this);
            int i;
            int exes[] = new int[8];
            int whys[] = new int[8];
            int xlabel;
            int ylabel;
            int ind;
            double xl;
            double yl;
            double offx;
            double scalex;
            double offy;
            double scaley;
            double incy;
            double incx;

            if(turbo.plttyp >= 3 && turbo.plttyp <= 7) {         //  perform a plotPanel
                turbo.off1Gg.setColor(Color.blue);
                turbo.off1Gg.fillRect(0, 0, 350, 350);

                if(turbo.ntikx < 2) {
                    turbo.ntikx = 2;     /* protection 13June96 */
                }
                if(turbo.ntiky < 2) {
                    turbo.ntiky = 2;
                }
                offx = 0.0 - turbo.begx;
                scalex = 6.5 / (turbo.endx - turbo.begx);
                incx = (turbo.endx - turbo.begx) / (turbo.ntikx - 1);
                offy = 0.0 - turbo.begy;
                scaley = 10.0 / (turbo.endy - turbo.begy);
                incy = (turbo.endy - turbo.begy) / (turbo.ntiky - 1);
                                        /* draw axes */
                turbo.off1Gg.setColor(Color.white);
                exes[0] = (int)(turbo.factp * 0.0 + turbo.xtranp);
                whys[0] = (int)(-150. + turbo.ytranp);
                exes[1] = (int)(turbo.factp * 0.0 + turbo.xtranp);
                whys[1] = (int)(turbo.factp * 0.0 + turbo.ytranp);
                exes[2] = (int)(215. + turbo.xtranp);
                whys[2] = (int)(turbo.factp * 0.0 + turbo.ytranp);
                turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                turbo.off1Gg.drawLine(exes[1], whys[1], exes[2], whys[2]);

                xlabel = (int)(-75. + turbo.xtranp);      /*     label y axis */
                ylabel = (int)(-65. + turbo.ytranp);
                turbo.off1Gg.drawString(turbo.laby, xlabel, ylabel);
                turbo.off1Gg.drawString(turbo.labyu, xlabel, ylabel + 20);
                                         /* add tick values */
                for (ind = 1; ind <= turbo.ntiky; ++ind) {
                    xlabel = (int)(-33. + turbo.xtranp);
                    yl = turbo.begy + (ind - 1) * incy;
                    ylabel = (int)(turbo.factp * -scaley * yl + turbo.ytranp);
                    if(turbo.nord != 5) {
                        turbo.off1Gg.drawString(String.valueOf((int)yl), xlabel, ylabel);
                    } else {
                        turbo.off1Gg.drawString(String.format("%.3f", yl), xlabel, ylabel);
                    }
                }
                xlabel = (int)(75. + turbo.xtranp);       /*   label x axis */
                ylabel = (int)(20. + turbo.ytranp);
                turbo.off1Gg.drawString(turbo.labx, xlabel, ylabel);
                turbo.off1Gg.drawString(turbo.labxu, xlabel + 50, ylabel);
                                         /* add tick values */
                for (ind = 1; ind <= turbo.ntikx; ++ind) {
                    ylabel = (int)(10. + turbo.ytranp);
                    xl = turbo.begx + (ind - 1) * incx;
                    xlabel = (int)(33. * (scalex * (xl + offx) - .05) + turbo.xtranp);
                    if(turbo.nabs >= 2 && turbo.nabs <= 3) {
                        turbo.off1Gg.drawString(String.format("%.3f", xl), xlabel, ylabel);
                    }
                    if(turbo.nabs < 2 || turbo.nabs > 3) {
                        turbo.off1Gg.drawString(String.valueOf((int)xl), xlabel, ylabel);
                    }
                }

                if(turbo.lines == 0) {
                    for (i = 1; i <= turbo.npt; ++i) {
                        xlabel = (int)(33. * scalex * (offx + turbo.pltx[i]) + turbo.xtranp);
                        ylabel = (int)(turbo.factp * -scaley * (offy + turbo.plty[i]) + turbo.ytranp + 7.);
                        turbo.off1Gg.drawString("*", xlabel, ylabel);
                    }
                } else {
                    exes[1] = (int)(33. * scalex * (offx + turbo.pltx[1]) + turbo.xtranp);
                    whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.plty[1]) + turbo.ytranp);
                    for (i = 2; i <= turbo.npt; ++i) {
                        exes[0] = exes[1];
                        whys[0] = whys[1];
                        exes[1] = (int)(33. * scalex * (offx + turbo.pltx[i]) + turbo.xtranp);
                        whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.plty[i]) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    }
                }
                if(turbo.plttyp == 4) {       // draw temp limits
                    turbo.off1Gg.setColor(Color.yellow);
                    if(turbo.entype < 3) {
                        exes[0] = (int)(33. * scalex * (offx + turbo.pltx[0]) + turbo.xtranp);
                        whys[0] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tinlt) + turbo.ytranp);
                        exes[1] = (int)(33. * scalex * (offx + turbo.pltx[1]) + turbo.xtranp);
                        whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tinlt) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        turbo.off1Gg.drawString("Limit", exes[0] + 5, whys[0]);
                        exes[0] = (int)(33. * scalex * (offx + turbo.pltx[2]) + turbo.xtranp);
                        whys[0] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tinlt) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + turbo.pltx[3]) + turbo.xtranp);
                        whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tinlt) + turbo.ytranp);
                        if(turbo.entype == 2) {
                            whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tfan) + turbo.ytranp);
                        }
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + turbo.pltx[4]) + turbo.xtranp);
                        whys[0] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tcomp) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + turbo.pltx[5]) + turbo.xtranp);
                        whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tburner) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + turbo.pltx[6]) + turbo.xtranp);
                        whys[0] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tturbin) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + turbo.pltx[6]) + turbo.xtranp);
                        whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tnozl) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + turbo.pltx[9]) + turbo.xtranp);
                        whys[0] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tnozl) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                    }
                    if(turbo.entype == 3) {
                        exes[1] = (int)(33. * scalex * (offx + turbo.pltx[0]) + turbo.xtranp);
                        whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tinlt) + turbo.ytranp);
                        exes[0] = (int)(33. * scalex * (offx + turbo.pltx[4]) + turbo.xtranp);
                        whys[0] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tinlt) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        turbo.off1Gg.drawString("Limit", exes[1] + 5, whys[1]);
                        exes[1] = (int)(33. * scalex * (offx + turbo.pltx[5]) + turbo.xtranp);
                        whys[1] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tburner) + turbo.ytranp);
                        turbo.off1Gg.drawLine(exes[0], whys[0], exes[1], whys[1]);
                        exes[0] = (int)(33. * scalex * (offx + turbo.pltx[9]) + turbo.xtranp);
                        whys[0] = (int)(turbo.factp * -scaley * (offy + turbo.tconv * turbo.tnozr) + turbo.ytranp);
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
                turbo.off1Gg.fillRect(310, turbo.sldplt, 20, 5);
            }

            if(turbo.plttyp == 2) {           // draw photo
                turbo.off1Gg.setColor(Color.white);
                turbo.off1Gg.fillRect(0, 0, 350, 350);
            }

            g.drawImage(turbo.offImg1, 0, 0, this);
        }  // end paint
    }  // end PlotPanel Output
} //  end Output panel
 
