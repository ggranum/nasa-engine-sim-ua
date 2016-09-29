package gov.nasa.engine_sim_ua;

import java.awt.AWTEvent;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;

public class PlotPanel extends Panel {

    private final PlotRightPanel plotRightPanel;
    PlotLeftPanel plotLeftPanel;

    PlotPanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        plotLeftPanel = new PlotLeftPanel(turbo);
        plotRightPanel = new PlotRightPanel(turbo);

        add(plotLeftPanel);
        add(plotRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class PlotRightPanel extends Panel {

        Turbo turbo;
        Button takbt;
        Scrollbar splt;

        PlotRightPanel(Turbo target) {

            int i1;

            turbo = target;
            setLayout(new GridLayout(5, 1, 10, 5));

            takbt = new Button("Take Data");
            takbt.setBackground(Color.blue);
            takbt.setForeground(Color.white);

            i1 = (int)(((Turbo.u0d - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);

            splt = new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000);
            splt.setBackground(Color.white);
            splt.setForeground(Color.red);

            add(takbt);
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(splt);
            add(new Label(" ", Label.CENTER));
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleBut();
            }
            if(evt.getID() == Event.SCROLL_ABSOLUTE) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_LINE_DOWN) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_LINE_UP) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_PAGE_DOWN) {
                this.handleBar();
            }
            if(evt.getID() == Event.SCROLL_PAGE_UP) {
                this.handleBar();
            }
        }

        public void handleBar() {     //  generate plotPanel
            int i1;
            double v1;
            float fl1;

            i1 = splt.getValue();
            if(turbo.nabs == 3) {  //  speed
                Turbo.vmn1 = Turbo.u0min;
                Turbo.vmx1 = Turbo.u0max;
            }
            if(turbo.nabs == 4) {  //  altitude
                Turbo.vmn2 = Turbo.altmin;
                Turbo.vmx2 = Turbo.altmax;
            }
            if(turbo.nabs == 5) {  //  throttle
                Turbo.vmn3 = Turbo.thrmin;
                Turbo.vmx3 = Turbo.thrmax;
            }
            if(turbo.nabs == 6) {  //  cpr
                Turbo.vmn1 = Turbo.cprmin;
                Turbo.vmx1 = Turbo.cprmax;
            }
            if(turbo.nabs == 7) {  // burner temp
                Turbo.vmn1 = Turbo.t4min;
                Turbo.vmx1 = Turbo.t4max;
            }
            v1 = i1 * (Turbo.vmx1 - Turbo.vmn1) / 1000. + Turbo.vmn1;
            fl1 = (float)v1;
            if(turbo.nabs == 3) {
                Turbo.u0d = v1;
            }
            if(turbo.nabs == 4) {
                Turbo.altd = v1;
            }
            if(turbo.nabs == 5) {
                Turbo.throtl = v1;
            }
            if(turbo.nabs == 6) {
                Turbo.prat[3] = Turbo.p3p2d = v1;
            }
            if(turbo.nabs == 7) {
                Turbo.tt4d = v1;
                Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
            }
            plotLeftPanel.fplt.setText(String.valueOf(fl1));

            turbo.solve.comPute();

            switch (turbo.nord) {
                case 3:
                    fl1 = (float)Turbo.fnlb;
                    break;
                case 4:
                    fl1 = (float)Turbo.flflo;
                    break;
                case 5:
                    fl1 = (float)Turbo.sfc;
                    break;
                case 6:
                    fl1 = (float)Turbo.epr;
                    break;
                case 7:
                    fl1 = (float)Turbo.etr;
                    break;
            }
            plotLeftPanel.oplt.setText(String.valueOf(fl1));
        }  // end handle

        public void handleBut() {     //  generate plotPanel
            if(turbo.npt == 25) {
                return;
            }
            ++turbo.npt;
            switch (turbo.nord) {
                case 3:
                    Turbo.plty[turbo.npt] = Turbo.fnlb;
                    break;
                case 4:
                    Turbo.plty[turbo.npt] = Turbo.flflo;
                    break;
                case 5:
                    Turbo.plty[turbo.npt] = Turbo.sfc;
                    break;
                case 6:
                    Turbo.plty[turbo.npt] = Turbo.epr;
                    break;
                case 7:
                    Turbo.plty[turbo.npt] = Turbo.etr;
                    break;
            }
            switch (turbo.nabs) {
                case 3:
                    Turbo.pltx[turbo.npt] = Turbo.fsmach;
                    break;
                case 4:
                    Turbo.pltx[turbo.npt] = Turbo.alt;
                    break;
                case 5:
                    Turbo.pltx[turbo.npt] = Turbo.throtl;
                    break;
                case 6:
                    Turbo.pltx[turbo.npt] = Turbo.prat[3];
                    break;
                case 7:
                    Turbo.pltx[turbo.npt] = Turbo.tt[4];
                    break;
            }

            turbo.outputPanel.outputPlotCanvas.repaint();
        }  // end handle
    }  // end rightPanel

    public class PlotLeftPanel extends Panel {

        Turbo turbo;
        TextField fplt;
        TextField oplt;
        Button strbt;
        Button endbt;
        Button exitpan;
        Choice absch;
        Choice ordch;

        PlotLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(5, 2, 5, 5));

            strbt = new Button("Begin");
            strbt.setBackground(Color.blue);
            strbt.setForeground(Color.white);
            endbt = new Button("End");
            endbt.setBackground(Color.blue);
            endbt.setForeground(Color.white);

            ordch = new Choice();
            ordch.addItem("Fn");
            ordch.addItem("Fuel");
            ordch.addItem("SFC");
            ordch.addItem("EPR");
            ordch.addItem("ETR");
            ordch.select(0);
            ordch.setBackground(Color.red);
            ordch.setForeground(Color.white);

            oplt = new TextField(String.valueOf(Turbo.fnlb), 5);
            oplt.setBackground(Color.black);
            oplt.setForeground(Color.yellow);

            absch = new Choice();
            absch.addItem("Speed");
            absch.addItem("Altitude ");
            absch.addItem("Throttle");
            absch.addItem(" CPR   ");
            absch.addItem("Temp 4");
            absch.select(0);
            absch.setBackground(Color.red);
            absch.setForeground(Color.white);

            fplt = new TextField(String.valueOf(Turbo.u0d), 5);
            fplt.setBackground(Color.white);
            fplt.setForeground(Color.red);

            exitpan = new Button("Exit");
            exitpan.setBackground(Color.red);
            exitpan.setForeground(Color.white);

            add(strbt);
            add(endbt);

            add(ordch);
            add(oplt);

            add(new Label("vs ", Label.CENTER));
            add(new Label(" ", Label.CENTER));

            add(absch);
            add(fplt);

            add(exitpan);
            add(new Label(" ", Label.CENTER));
        }

        public boolean action(Event evt, Object arg) {
            if(evt.target instanceof Button) {
                this.handlePlot(arg);
                return true;
            }
            if(evt.target instanceof Choice) {
                this.handlePlot(arg);
                return true;
            }
            if(evt.id == Event.ACTION_EVENT) {
                this.handleText(evt);
                return true;
            } else {
                return false;
            }
        }

        public void handlePlot(Object arg) {
            String label = (String)arg;
            int item;
            int i;
            double tempx;
            double tempy;
            double v1;
            int i1;
            float fl1;

            turbo.nord = 3 + ordch.getSelectedIndex();
            if(turbo.nord != turbo.ordkeep) {  // set the plotPanel parameters
                if(turbo.nord == 3) {  // Thrust
                    Turbo.laby = String.valueOf("Fn");
                    Turbo.labyu = String.valueOf("lb");
                    Turbo.begy = 0.0;
                    Turbo.endy = 100000.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 4) {  //  Fuel
                    Turbo.laby = String.valueOf("Fuel Rate");
                    Turbo.labyu = String.valueOf("lbs/hr");
                    Turbo.begy = 0.0;
                    Turbo.endy = 100000.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 5) {  //  TSFC
                    Turbo.laby = String.valueOf("TSFC");
                    Turbo.labyu = String.valueOf("lbm/hr/lb");
                    Turbo.begy = 0.0;
                    Turbo.endy = 2.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 6) {  //  EPR
                    Turbo.laby = String.valueOf("EPR");
                    Turbo.labyu = String.valueOf(" ");
                    Turbo.begy = 0.0;
                    Turbo.endy = 50.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 7) {  //  ETR
                    Turbo.laby = String.valueOf("ETR");
                    Turbo.labyu = String.valueOf(" ");
                    Turbo.begy = 0.0;
                    Turbo.endy = 50.0;
                    turbo.ntiky = 11;
                }
                turbo.ordkeep = turbo.nord;
                turbo.npt = 0;
                turbo.lines = 0;
            }

            turbo.nabs = 3 + absch.getSelectedIndex();
            v1 = Turbo.u0d;
            if(turbo.nabs != turbo.abskeep) {  // set the plotPanel parameters
                if(turbo.nabs == 3) {  //  speed
                    Turbo.labx = String.valueOf("Mach");
                    Turbo.labxu = String.valueOf(" ");
                    if(turbo.entype <= 2) {
                        Turbo.begx = 0.0;
                        Turbo.endx = 2.0;
                        turbo.ntikx = 5;
                    }
                    if(turbo.entype == 3) {
                        Turbo.begx = 0.0;
                        Turbo.endx = 6.0;
                        turbo.ntikx = 5;
                    }
                    v1 = Turbo.u0d;
                    Turbo.vmn1 = Turbo.u0min;
                    Turbo.vmx1 = Turbo.u0max;
                }
                if(turbo.nabs == 4) {  //  altitude
                    Turbo.labx = String.valueOf("Alt");
                    Turbo.labxu = String.valueOf("ft");
                    Turbo.begx = 0.0;
                    Turbo.endx = 60000.0;
                    turbo.ntikx = 4;
                    v1 = Turbo.altd;
                    Turbo.vmn1 = Turbo.altmin;
                    Turbo.vmx1 = Turbo.altmax;
                }
                if(turbo.nabs == 5) {  //  throttle
                    Turbo.labx = String.valueOf("Throttle");
                    Turbo.labxu = String.valueOf(" %");
                    Turbo.begx = 0.0;
                    Turbo.endx = 100.0;
                    turbo.ntikx = 5;
                    v1 = Turbo.throtl;
                    Turbo.vmn1 = Turbo.thrmin;
                    Turbo.vmx1 = Turbo.thrmax;
                }
                if(turbo.nabs == 6) {  //  Compressor pressure ratio
                    Turbo.labx = String.valueOf("CPR");
                    Turbo.labxu = String.valueOf(" ");
                    Turbo.begx = 0.0;
                    Turbo.endx = 50.0;
                    turbo.ntikx = 6;
                    v1 = Turbo.p3p2d;
                    Turbo.vmn1 = Turbo.cprmin;
                    Turbo.vmx1 = Turbo.cprmax;
                }
                if(turbo.nabs == 7) {  // Burner temp
                    Turbo.labx = String.valueOf("Temp");
                    Turbo.labxu = String.valueOf("R");
                    Turbo.begx = 1000.0;
                    Turbo.endx = 4000.0;
                    turbo.ntikx = 4;
                    v1 = Turbo.tt4d;
                    Turbo.vmn1 = Turbo.t4min;
                    Turbo.vmx1 = Turbo.t4max;
                }
                fl1 = (float)v1;
                fplt.setText(String.valueOf(fl1));
                i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
                plotRightPanel.splt.setValue(i1);
                turbo.abskeep = turbo.nabs;
                turbo.npt = 0;
                turbo.lines = 0;
            }

            if(label.equals("Begin")) {
                turbo.npt = 0;
                turbo.lines = 0;
            }

            if(label.equals("End")) {
                turbo.lines = 1;
                for (item = 1; item <= turbo.npt - 1; ++item) {
                    for (i = item + 1; i <= turbo.npt; ++i) {
                        if(Turbo.pltx[i] < Turbo.pltx[item]) {
                            tempx = Turbo.pltx[item];
                            tempy = Turbo.plty[item];
                            Turbo.pltx[item] = Turbo.pltx[i];
                            Turbo.plty[item] = Turbo.plty[i];
                            Turbo.pltx[i] = tempx;
                            Turbo.plty[i] = tempy;
                        }
                    }
                }
            }

            if(label.equals("Exit")) {
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcOutput.select(0);
                turbo.solve.loadMine();
                turbo.plttyp = 3;
                turbo.flightConditionsPanel.setPlot();
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcUnits.select(0);
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcTemplate.select(0);
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcMode.select(turbo.inflag);
            }

            turbo.solve.comPute();
        }

        public void handleText(Event evt) {
            Double V1;
            double v1;
            int i1;
            float fl1;

            V1 = Double.valueOf(fplt.getText());
            v1 = V1.doubleValue();
            fl1 = (float)v1;
            if(turbo.nabs == 3) {  //  speed
                Turbo.u0d = v1;
                Turbo.vmn1 = Turbo.u0min;
                Turbo.vmx1 = Turbo.u0max;
                if(v1 < Turbo.vmn1) {
                    Turbo.u0d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.u0d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 4) {  //  altitude
                Turbo.altd = v1;
                Turbo.vmn1 = Turbo.altmin;
                Turbo.vmx1 = Turbo.altmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.altd = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.altd = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 5) {  //  throttle
                Turbo.throtl = v1;
                Turbo.vmn1 = Turbo.thrmin;
                Turbo.vmx1 = Turbo.thrmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.throtl = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.throtl = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 6) {  //  Compressor pressure ratio
                Turbo.prat[3] = Turbo.p3p2d = v1;
                Turbo.vmn1 = Turbo.cprmin;
                Turbo.vmx1 = Turbo.cprmax;
                if(v1 < Turbo.vmn1) {
                    Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.prat[3] = Turbo.p3p2d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 7) {  // Burner temp
                Turbo.tt4d = v1;
                Turbo.vmn1 = Turbo.t4min;
                Turbo.vmx1 = Turbo.t4max;
                if(v1 < Turbo.vmn1) {
                    Turbo.tt4d = v1 = Turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > Turbo.vmx1) {
                    Turbo.tt4d = v1 = Turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                Turbo.tt4 = Turbo.tt4d / Turbo.tconv;
            }
            i1 = (int)(((v1 - Turbo.vmn1) / (Turbo.vmx1 - Turbo.vmn1)) * 1000.);
            plotRightPanel.splt.setValue(i1);

            turbo.solve.comPute();

            switch (turbo.nord) {
                case 3:
                    fl1 = (float)Turbo.fnlb;
                    break;
                case 4:
                    fl1 = (float)Turbo.flflo;
                    break;
                case 5:
                    fl1 = (float)Turbo.sfc;
                    break;
                case 6:
                    fl1 = (float)Turbo.epr;
                    break;
                case 7:
                    fl1 = (float)Turbo.etr;
                    break;
            }
            oplt.setText(String.valueOf(fl1));
        }  // end handle
    }  //  end  inletLeftPanel
}  // end PlotPanel
 
