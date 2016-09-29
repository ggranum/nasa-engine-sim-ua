package gov.nasa.engine_sim_ua;

import java.awt.AWTEvent;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Scrollbar;
import java.awt.TextField;

/**
 *
 */
public class SizePanel extends Panel {

    SizeRightPanel sizeRightPanel;
    SizeLeftPanel sizeLeftPanel;

    SizePanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        sizeLeftPanel = new SizeLeftPanel(turbo);
        sizeRightPanel = new SizeRightPanel(turbo);

        add(sizeLeftPanel);
        add(sizeRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class SizeRightPanel extends Panel {

        Turbo turbo;
        private Scrollbar s1;
        private Choice chmat;
        Choice sizch;

        SizeRightPanel(Turbo target) {

            int i1;

            turbo = target;
            setLayout(new GridLayout(6, 1, 10, 5));

            i1 = (int)(((turbo.a2d - turbo.a2min) / (turbo.a2max - turbo.a2min)) * 1000.);
            setS1(new Scrollbar(Scrollbar.HORIZONTAL, i1, 10, 0, 1000));

            setChmat(new Choice());
            getChmat().addItem("Computed Weight");
            getChmat().addItem("Input Weight ");
            getChmat().select(0);

            sizch = new Choice();
            sizch.addItem("Input Frontal Area");
            sizch.addItem("Input Diameter ");
            sizch.select(0);

            add(sizch);
            add(getS1());
            add(new Label(" ", Label.CENTER));
            add(getChmat());
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleBar();
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

        public void handleBar() {     // engine sizePanel
            int i1;
            Double V2;
            Double V3;
            double v2;
            double v3;

            turbo.siztype = sizch.getSelectedIndex();

            if(turbo.siztype == 0) {
                // area input
                i1 = getS1().getValue();
                turbo.vmn1 = turbo.a2min;
                turbo.vmx1 = turbo.a2max;

                turbo.a2d = i1 * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;
                turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);

                sizeLeftPanel.f1.setBackground(Color.white);
                sizeLeftPanel.f1.setForeground(Color.black);
                sizeLeftPanel.f3.setBackground(Color.black);
                sizeLeftPanel.f3.setForeground(Color.yellow);
            }

            if(turbo.siztype == 1) {
                // diameter input
                V3 = Double.valueOf(sizeLeftPanel.f3.getText());
                turbo.diameng = v3 = V3;

                turbo.a2d = 3.14159 * turbo.diameng * turbo.diameng / 4.0;

                sizeLeftPanel.f1.setBackground(Color.black);
                sizeLeftPanel.f1.setForeground(Color.yellow);
                sizeLeftPanel.f3.setBackground(Color.white);
                sizeLeftPanel.f3.setForeground(Color.black);
            }

            turbo.a2 = turbo.a2d / turbo.aconv;
            if(turbo.entype == 2) {
                turbo.afan = turbo.a2;
                turbo.acore = turbo.afan / (1.0 + turbo.byprat);
            } else {
                turbo.acore = turbo.a2;
            }

            // compute or input weight
            turbo.wtflag = getChmat().getSelectedIndex();
            if(turbo.wtflag == 1) {
                sizeLeftPanel.f2.setForeground(Color.black);
                sizeLeftPanel.f2.setBackground(Color.white);
                V2 = Double.valueOf(sizeLeftPanel.f2.getText());
                v2 = V2;
                turbo.weight = v2 / turbo.fconv;
                if(turbo.weight < 10.0) {
                    turbo.weight = v2 = 10.0;
                    sizeLeftPanel.f2.setText(String.valueOf((float)v2));
                }
            }
            if(turbo.wtflag == 0) {
                sizeLeftPanel.f2.setForeground(Color.yellow);
                sizeLeftPanel.f2.setBackground(Color.black);
            }

            sizeLeftPanel.f1.setText(String.format("%.3f", turbo.a2d));
            sizeLeftPanel.f3.setText(String.format("%.3f", turbo.diameng));

            turbo.solve.compute();
        }  // end handle

        public Scrollbar getS1() {
            return s1;
        }

        public void setS1(Scrollbar s1) {
            this.s1 = s1;
        }

        public Choice getChmat() {
            return chmat;
        }

        public void setChmat(Choice chmat) {
            this.chmat = chmat;
        }
    }  // end rightPanel

    public class SizeLeftPanel extends Panel {

        Turbo turbo;
        TextField f1;
        TextField f2;
        TextField f3;
        Label l1;
        Label l2;
        Label l3;

        SizeLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(6, 2, 5, 5));

            l1 = new Label("Area-sq ft", Label.CENTER);
            f1 = new TextField(String.valueOf((float)turbo.a2d), 5);
            f1.setBackground(Color.white);
            f1.setForeground(Color.black);

            l2 = new Label("Weight-lbs", Label.CENTER);
            f2 = new TextField(String.valueOf((float)turbo.weight), 5);
            f2.setBackground(Color.black);
            f2.setForeground(Color.yellow);

            l3 = new Label("Diameter-ft", Label.CENTER);
            f3 = new TextField(String.valueOf((float)turbo.diameng), 5);
            f3.setBackground(Color.black);
            f3.setForeground(Color.yellow);

            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(l1);
            add(f1);
            add(l3);
            add(f3);
            add(l2);
            add(f2);
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
            add(new Label(" ", Label.CENTER));
        }

        public void processEvent(AWTEvent evt) {
            if(evt.getID() == Event.ACTION_EVENT) {
                this.handleText();
            }
        }

        public void handleText() {
            Double V1;
            Double V2;
            Double V3;
            double v1;
            double v2;
            double v3;
            int i1;

            V1 = Double.valueOf(f1.getText());
            v1 = V1;
            V2 = Double.valueOf(f2.getText());
            v2 = V2;
            V3 = Double.valueOf(f3.getText());
            v3 = V3;
            // area input
            if(turbo.siztype == 0) {
                turbo.a2d = v1;
                turbo.vmn1 = turbo.a2min;
                turbo.vmx1 = turbo.a2max;
                if(v1 < turbo.vmn1) {
                    turbo.a2d = v1 = turbo.vmn1;
                    f1.setText(String.valueOf((float)v1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.a2d = v1 = turbo.vmx1;
                    f1.setText(String.valueOf((float)v1));
                }
                turbo.diameng = Math.sqrt(4.0 * turbo.a2d / 3.14159);
                f3.setText(String.format("%.3f", turbo.diameng));
            }
            // diameter input
            if(turbo.siztype == 1) {
                turbo.diameng = v3;
                turbo.vmn1 = turbo.diamin;
                turbo.vmx1 = turbo.diamax;
                if(v3 < turbo.vmn1) {
                    turbo.diameng = v3 = turbo.vmn1;
                    f3.setText(String.valueOf((float)v3));
                }
                if(v3 > turbo.vmx1) {
                    turbo.diameng = v3 = turbo.vmx1;
                    f3.setText(String.valueOf((float)v3));
                }
                turbo.a2d = 3.14159 * turbo.diameng * turbo.diameng / 4.0;
                f1.setText(String.format("%.3f", turbo.a2d));
            }

            turbo.a2 = turbo.a2d / turbo.aconv;
            if(turbo.entype == 2) {
                turbo.afan = turbo.a2;
                turbo.acore = turbo.afan / (1.0 + turbo.byprat);
            } else {
                turbo.acore = turbo.a2;
            }

            turbo.weight = v2 / turbo.fconv;
            if(turbo.weight < 10.0) {
                turbo.weight = v2 = 10.0;
                f2.setText(String.valueOf((float)v2));
            }

            i1 = (int)(((turbo.a2d - turbo.a2min) / (turbo.a2max - turbo.a2min)) * 1000.);

            sizeRightPanel.getS1().setValue(i1);

            turbo.solve.compute();
        }  // end handle
    }  //  end  inletLeftPanel
}  // end SizePanel input
 
