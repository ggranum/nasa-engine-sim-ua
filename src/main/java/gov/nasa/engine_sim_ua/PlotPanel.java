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

            i1 = (int)(((turbo.u0d - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);

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
                turbo.vmn1 = turbo.u0min;
                turbo.vmx1 = turbo.u0max;
            }
            if(turbo.nabs == 4) {  //  altitude
                turbo.vmn2 = turbo.altmin;
                turbo.vmx2 = turbo.altmax;
            }
            if(turbo.nabs == 5) {  //  throttle
                turbo.vmn3 = turbo.thrmin;
                turbo.vmx3 = turbo.thrmax;
            }
            if(turbo.nabs == 6) {  //  cpr
                turbo.vmn1 = turbo.cprmin;
                turbo.vmx1 = turbo.cprmax;
            }
            if(turbo.nabs == 7) {  // burner temp
                turbo.vmn1 = turbo.t4min;
                turbo.vmx1 = turbo.t4max;
            }
            v1 = i1 * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;
            fl1 = (float)v1;
            if(turbo.nabs == 3) {
                turbo.u0d = v1;
            }
            if(turbo.nabs == 4) {
                turbo.altd = v1;
            }
            if(turbo.nabs == 5) {
                turbo.throtl = v1;
            }
            if(turbo.nabs == 6) {
                turbo.prat[3] = turbo.p3p2d = v1;
            }
            if(turbo.nabs == 7) {
                turbo.tt4d = v1;
                turbo.tt4 = turbo.tt4d / turbo.tconv;
            }
            plotLeftPanel.fplt.setText(String.valueOf(fl1));

            turbo.solve.comPute();

            switch (turbo.nord) {
                case 3:
                    fl1 = (float)turbo.fnlb;
                    break;
                case 4:
                    fl1 = (float)turbo.flflo;
                    break;
                case 5:
                    fl1 = (float)turbo.sfc;
                    break;
                case 6:
                    fl1 = (float)turbo.epr;
                    break;
                case 7:
                    fl1 = (float)turbo.etr;
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
                    turbo.plty[turbo.npt] = turbo.fnlb;
                    break;
                case 4:
                    turbo.plty[turbo.npt] = turbo.flflo;
                    break;
                case 5:
                    turbo.plty[turbo.npt] = turbo.sfc;
                    break;
                case 6:
                    turbo.plty[turbo.npt] = turbo.epr;
                    break;
                case 7:
                    turbo.plty[turbo.npt] = turbo.etr;
                    break;
            }
            switch (turbo.nabs) {
                case 3:
                    turbo.pltx[turbo.npt] = turbo.fsmach;
                    break;
                case 4:
                    turbo.pltx[turbo.npt] = turbo.alt;
                    break;
                case 5:
                    turbo.pltx[turbo.npt] = turbo.throtl;
                    break;
                case 6:
                    turbo.pltx[turbo.npt] = turbo.prat[3];
                    break;
                case 7:
                    turbo.pltx[turbo.npt] = turbo.tt[4];
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

            oplt = new TextField(String.valueOf(turbo.fnlb), 5);
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

            fplt = new TextField(String.valueOf(turbo.u0d), 5);
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
                this.handleText();
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
                    turbo.laby = String.valueOf("Fn");
                    turbo.labyu = String.valueOf("lb");
                    turbo.begy = 0.0;
                    turbo.endy = 100000.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 4) {  //  Fuel
                    turbo.laby = String.valueOf("Fuel Rate");
                    turbo.labyu = String.valueOf("lbs/hr");
                    turbo.begy = 0.0;
                    turbo.endy = 100000.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 5) {  //  TSFC
                    turbo.laby = String.valueOf("TSFC");
                    turbo.labyu = String.valueOf("lbm/hr/lb");
                    turbo.begy = 0.0;
                    turbo.endy = 2.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 6) {  //  EPR
                    turbo.laby = String.valueOf("EPR");
                    turbo.labyu = String.valueOf(" ");
                    turbo.begy = 0.0;
                    turbo.endy = 50.0;
                    turbo.ntiky = 11;
                }
                if(turbo.nord == 7) {  //  ETR
                    turbo.laby = String.valueOf("ETR");
                    turbo.labyu = String.valueOf(" ");
                    turbo.begy = 0.0;
                    turbo.endy = 50.0;
                    turbo.ntiky = 11;
                }
                turbo.ordkeep = turbo.nord;
                turbo.npt = 0;
                turbo.lines = 0;
            }

            turbo.nabs = 3 + absch.getSelectedIndex();
            v1 = turbo.u0d;
            if(turbo.nabs != turbo.abskeep) {  // set the plotPanel parameters
                if(turbo.nabs == 3) {  //  speed
                    turbo.labx = String.valueOf("Mach");
                    turbo.labxu = String.valueOf(" ");
                    if(turbo.entype <= 2) {
                        turbo.begx = 0.0;
                        turbo.endx = 2.0;
                        turbo.ntikx = 5;
                    }
                    if(turbo.entype == 3) {
                        turbo.begx = 0.0;
                        turbo.endx = 6.0;
                        turbo.ntikx = 5;
                    }
                    v1 = turbo.u0d;
                    turbo.vmn1 = turbo.u0min;
                    turbo.vmx1 = turbo.u0max;
                }
                if(turbo.nabs == 4) {  //  altitude
                    turbo.labx = String.valueOf("Alt");
                    turbo.labxu = String.valueOf("ft");
                    turbo.begx = 0.0;
                    turbo.endx = 60000.0;
                    turbo.ntikx = 4;
                    v1 = turbo.altd;
                    turbo.vmn1 = turbo.altmin;
                    turbo.vmx1 = turbo.altmax;
                }
                if(turbo.nabs == 5) {  //  throttle
                    turbo.labx = String.valueOf("Throttle");
                    turbo.labxu = String.valueOf(" %");
                    turbo.begx = 0.0;
                    turbo.endx = 100.0;
                    turbo.ntikx = 5;
                    v1 = turbo.throtl;
                    turbo.vmn1 = turbo.thrmin;
                    turbo.vmx1 = turbo.thrmax;
                }
                if(turbo.nabs == 6) {  //  Compressor pressure ratio
                    turbo.labx = String.valueOf("CPR");
                    turbo.labxu = String.valueOf(" ");
                    turbo.begx = 0.0;
                    turbo.endx = 50.0;
                    turbo.ntikx = 6;
                    v1 = turbo.p3p2d;
                    turbo.vmn1 = turbo.cprmin;
                    turbo.vmx1 = turbo.cprmax;
                }
                if(turbo.nabs == 7) {  // Burner temp
                    turbo.labx = String.valueOf("Temp");
                    turbo.labxu = String.valueOf("R");
                    turbo.begx = 1000.0;
                    turbo.endx = 4000.0;
                    turbo.ntikx = 4;
                    v1 = turbo.tt4d;
                    turbo.vmn1 = turbo.t4min;
                    turbo.vmx1 = turbo.t4max;
                }
                fl1 = (float)v1;
                fplt.setText(String.valueOf(fl1));
                i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
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
                        if(turbo.pltx[i] < turbo.pltx[item]) {
                            tempx = turbo.pltx[item];
                            tempy = turbo.plty[item];
                            turbo.pltx[item] = turbo.pltx[i];
                            turbo.plty[item] = turbo.plty[i];
                            turbo.pltx[i] = tempx;
                            turbo.plty[i] = tempy;
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

        public void handleText() {
            Double V1;
            double v1;
            int i1;
            float fl1;

            V1 = Double.valueOf(fplt.getText());
            v1 = V1;
            fl1 = (float)v1;
            if(turbo.nabs == 3) {  //  speed
                turbo.u0d = v1;
                turbo.vmn1 = turbo.u0min;
                turbo.vmx1 = turbo.u0max;
                if(v1 < turbo.vmn1) {
                    turbo.u0d = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.u0d = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 4) {  //  altitude
                turbo.altd = v1;
                turbo.vmn1 = turbo.altmin;
                turbo.vmx1 = turbo.altmax;
                if(v1 < turbo.vmn1) {
                    turbo.altd = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.altd = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 5) {  //  throttle
                turbo.throtl = v1;
                turbo.vmn1 = turbo.thrmin;
                turbo.vmx1 = turbo.thrmax;
                if(v1 < turbo.vmn1) {
                    turbo.throtl = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.throtl = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 6) {  //  Compressor pressure ratio
                turbo.prat[3] = turbo.p3p2d = v1;
                turbo.vmn1 = turbo.cprmin;
                turbo.vmx1 = turbo.cprmax;
                if(v1 < turbo.vmn1) {
                    turbo.prat[3] = turbo.p3p2d = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.prat[3] = turbo.p3p2d = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
            }
            if(turbo.nabs == 7) {  // Burner temp
                turbo.tt4d = v1;
                turbo.vmn1 = turbo.t4min;
                turbo.vmx1 = turbo.t4max;
                if(v1 < turbo.vmn1) {
                    turbo.tt4d = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.tt4d = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    fplt.setText(String.valueOf(fl1));
                }
                turbo.tt4 = turbo.tt4d / turbo.tconv;
            }
            i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
            plotRightPanel.splt.setValue(i1);

            turbo.solve.comPute();

            switch (turbo.nord) {
                case 3:
                    fl1 = (float)turbo.fnlb;
                    break;
                case 4:
                    fl1 = (float)turbo.flflo;
                    break;
                case 5:
                    fl1 = (float)turbo.sfc;
                    break;
                case 6:
                    fl1 = (float)turbo.epr;
                    break;
                case 7:
                    fl1 = (float)turbo.etr;
                    break;
            }
            oplt.setText(String.valueOf(fl1));
        }  // end handle
    }  //  end  inletLeftPanel
}  // end PlotPanel
 
