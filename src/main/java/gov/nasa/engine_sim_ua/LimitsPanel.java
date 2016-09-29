package gov.nasa.engine_sim_ua;

import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;

public class LimitsPanel extends Panel {

    private Turbo turbo;
    TextField f1;
    TextField f2;
    TextField f3;
    TextField f4;
    TextField f5;
    TextField f6;
    TextField f7;
    TextField f9;
    TextField f10;
    TextField f11;
    Label l1;
    Label l2;
    Label l3;
    Label l4;
    Label l5;
    Label l6;
    Label l7;
    Label l9;
    Label l10;
    Label l11;
    Button submit;

    LimitsPanel(Turbo turbo) {
        this.turbo = turbo;
        setLayout(new GridLayout(6, 4, 10, 10));

        l1 = new Label("Speed-max", Label.CENTER);
        f1 = new TextField(String.valueOf((float)turbo.u0max), 5);
        l2 = new Label("Alt-max", Label.CENTER);
        f2 = new TextField(String.valueOf((float)turbo.altmax), 5);
        l3 = new Label("A2-min", Label.CENTER);
        f3 = new TextField(String.valueOf((float)turbo.a2min), 3);
        l4 = new Label("A2-max", Label.CENTER);
        f4 = new TextField(String.valueOf((float)turbo.a2max), 5);
        l5 = new Label("CPR-max", Label.CENTER);
        f5 = new TextField(String.valueOf((float)turbo.cprmax), 5);
        l6 = new Label("T4-max", Label.CENTER);
        f6 = new TextField(String.valueOf((float)turbo.t4max), 5);
        l7 = new Label("T7-max", Label.CENTER);
        f7 = new TextField(String.valueOf((float)turbo.t7max), 5);
        l9 = new Label("FPR-max", Label.CENTER);
        f9 = new TextField(String.valueOf((float)turbo.fprmax), 5);
        l10 = new Label("BPR-max", Label.CENTER);
        f10 = new TextField(String.valueOf((float)turbo.bypmax), 5);
        l11 = new Label("Pt4/Pt3-max", Label.CENTER);
        f11 = new TextField(String.valueOf((float)turbo.pt4max), 5);

        submit = new Button("Submit");
        submit.setBackground(Color.blue);
        submit.setForeground(Color.white);

        add(l1);
        add(f1);
        add(l2);
        add(f2);

        add(l3);
        add(f3);
        add(l4);
        add(f4);

        add(l5);
        add(f5);
        add(l11);
        add(f11);

        add(l6);
        add(f6);
        add(l7);
        add(f7);

        add(l9);
        add(f9);
        add(l10);
        add(f10);

        add(new Label("  ", Label.RIGHT));
        add(new Label(" ", Label.RIGHT));
        add(new Label(" Push --> ", Label.RIGHT));
        add(submit);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public boolean action(Event evt, Object arg) {
        if(evt.target instanceof Button) {
            this.handleText();
            return true;
        } else {
            return false;
        }
    }

    public void handleText() {
        Double V1;
        Double V2;
        Double V3;
        Double V4;
        double v1;
        double v2;
        double v3;
        double v4;

        V1 = Double.valueOf(f1.getText());
        v1 = V1;
        V2 = Double.valueOf(f2.getText());
        v2 = V2;
        V3 = Double.valueOf(f3.getText());
        v3 = V3;
        V4 = Double.valueOf(f4.getText());
        v4 = V4;

        turbo.u0max = v1;
        turbo.altmax = v2;
        turbo.a2min = v3;
        turbo.a2max = v4;
        if(turbo.entype <= 2) {
            turbo.u0mt = turbo.u0max;
            turbo.altmt = turbo.altmax;
        }
        if(turbo.entype == 3) {
            turbo.u0mr = turbo.u0max;
            turbo.altmr = turbo.altmax;
        }

        // look for exceeding limits

        if(turbo.u0d > turbo.u0max) {
            if(turbo.u0max < 0) {
                turbo.u0max = turbo.u0d + .1;
            }
            turbo.u0d = turbo.u0max;
        }
        if(turbo.altd > turbo.altmax) {
            if(turbo.altmax < 0) {
                turbo.altmax = turbo.altd + .1;
            }
            turbo.altd = turbo.altmax;
        }
        if(turbo.a2max <= turbo.a2min) {
            turbo.a2max = turbo.a2min + .1;
        }
        if(turbo.a2d > turbo.a2max) {
            turbo.a2d = turbo.a2max;
            turbo.a2 = turbo.a2d / turbo.aconv;
            if(turbo.entype != 2) {
                turbo.acore = turbo.a2;
            }
            if(turbo.entype == 2) {
                turbo.afan = turbo.a2;
                turbo.acore = turbo.afan / (1.0 + turbo.byprat);
            }
        }
        if(turbo.a2d < turbo.a2min) {
            turbo.a2d = turbo.a2min;
            turbo.a2 = turbo.a2d / turbo.aconv;
            if(turbo.entype != 2) {
                turbo.acore = turbo.a2;
            }
            if(turbo.entype == 2) {
                turbo.afan = turbo.a2;
                turbo.acore = turbo.afan / (1.0 + turbo.byprat);
            }
        }

        V1 = Double.valueOf(f5.getText());
        v1 = V1;
        V2 = Double.valueOf(f6.getText());
        v2 = V2;
        V3 = Double.valueOf(f7.getText());
        v3 = V3;

        turbo.cprmax = v1;
        turbo.t4max = v2;
        turbo.t7max = v3;

        // look for exceeding limits

        if(turbo.cprmax <= turbo.cprmin) {
            turbo.cprmax = turbo.cprmin + .1;
        }
        if(turbo.p3p2d > turbo.cprmax) {
            turbo.p3p2d = turbo.cprmax;
        }
        if(turbo.t4max <= turbo.t4min) {
            turbo.t4max = turbo.t4min + .1;
        }
        if(turbo.tt4d > turbo.t4max) {
            turbo.tt4d = turbo.t4max;
            turbo.tt4 = turbo.tt4d / turbo.tconv;
        }
        if(turbo.t7max <= turbo.t7min) {
            turbo.t7max = turbo.t7min + .1;
        }
        if(turbo.tt7d > turbo.t7max) {
            turbo.tt7d = turbo.t7max;
            turbo.tt7 = turbo.tt7d / turbo.tconv;
        }

        V1 = Double.valueOf(f9.getText());
        v1 = V1;
        V2 = Double.valueOf(f10.getText());
        v2 = V2;
        V3 = Double.valueOf(f11.getText());
        v3 = V3;

        turbo.fprmax = v1;
        turbo.bypmax = v2;
        turbo.pt4max = v3;

        if(turbo.fprmax <= turbo.fprmin) {
            turbo.fprmax = turbo.fprmin + .1;
        }
        if(turbo.p3fp2d > turbo.fprmax) {
            turbo.p3fp2d = turbo.fprmax;
        }
        if(turbo.bypmax <= turbo.bypmin) {
            turbo.bypmax = turbo.bypmin + .1;
        }
        if(turbo.byprat > turbo.bypmax) {
            turbo.byprat = turbo.bypmax;
            turbo.acore = turbo.afan / (1.0 + turbo.byprat);
        }
        if(turbo.pt4max <= turbo.etmin) {
            turbo.pt4max = turbo.etmin + .1;
        }
        if(turbo.prat[4] > turbo.pt4max) {
            turbo.prat[4] = turbo.pt4max;
        }

        turbo.varflag = 0;
        turbo.layin.show(turbo.inputPanel, "first");
        turbo.solve.comPute();
        turbo.flightConditionsPanel.setPanl();
    }  // end handle
}  // end inlimit
 
