package gov.nasa.engine_sim_ua;

import java.awt.Button;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilesPanel extends Panel {  // save file

    private Turbo turbo;
    TextField nsavin;
    TextField nsavout;
    Button savread;
    Button savwrit;
    Button cancel;

    FilesPanel(Turbo turbo) {
        this.turbo = turbo;

        setLayout(new GridLayout(6, 2, 10, 10));

        nsavin = new TextField();
        nsavin.setBackground(Color.white);
        nsavin.setForeground(Color.black);

        nsavout = new TextField();
        nsavout.setBackground(Color.white);
        nsavout.setForeground(Color.black);

        savread = new Button("Retrieve Data");
        savread.setBackground(Color.blue);
        savread.setForeground(Color.white);

        savwrit = new Button("Save Data");
        savwrit.setBackground(Color.red);
        savwrit.setForeground(Color.white);

        cancel = new Button("Cancel");
        cancel.setBackground(Color.yellow);
        cancel.setForeground(Color.black);

        add(new Label("Enter File Name -  ", Label.RIGHT));
        add(new Label("Then Push Button", Label.LEFT));

        add(new Label("Save Data to File:", Label.RIGHT));
        add(nsavout);

        add(new Label(" ", Label.CENTER));
        add(savwrit);

        add(new Label("Get Data from File:", Label.RIGHT));
        add(nsavin);

        add(new Label(" ", Label.CENTER));
        add(savread);

        add(new Label(" ", Label.CENTER));
        add(cancel);
    }

    public boolean action(Event evt, Object arg) {
        if(evt.target instanceof Button) {
            this.handleRefs(evt, arg);
            return true;
        } else {
            return false;
        }
    }

    public void handleRefs(Event evt, Object arg) {
        String filnam;
        String label = (String)arg;

        if(label.equals("Retrieve Data")) {  // Read inputPanel saved case
            filnam = nsavin.getText();

            try {
                Turbo.sfili = new FileInputStream(filnam);
                Turbo.savin = new DataInputStream(Turbo.sfili);

                turbo.inflag = 0;
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcMode.select(0);
                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
                turbo.lunits = 0;
                turbo.flightConditionsPanel.setUnits();
                turbo.flightConditionsPanel.flightConditionsUpperPanel.chcUnits.select(turbo.lunits);

                turbo.entype = Turbo.savin.readInt();
                turbo.abflag = Turbo.savin.readInt();
                turbo.fueltype = Turbo.savin.readInt();
                Turbo.fhvd = Turbo.fhv = Turbo.savin.readDouble();
                Turbo.tt[4] = Turbo.tt4 = Turbo.tt4d = Turbo.savin.readDouble();
                Turbo.tt[7] = Turbo.tt7 = Turbo.tt7d = Turbo.savin.readDouble();
                Turbo.prat[3] = Turbo.p3p2d = Turbo.savin.readDouble();
                Turbo.prat[13] = Turbo.p3fp2d = Turbo.savin.readDouble();
                Turbo.byprat = Turbo.savin.readDouble();
                Turbo.acore = Turbo.savin.readDouble();
                Turbo.afan = Turbo.acore * (1.0 + Turbo.byprat);
                Turbo.a2d = Turbo.a2 = Turbo.savin.readDouble();
                Turbo.a4 = Turbo.savin.readDouble();
                Turbo.a4p = Turbo.savin.readDouble();
                Turbo.ac = .9 * Turbo.a2;
                Turbo.gama = Turbo.savin.readDouble();
                turbo.gamopt = Turbo.savin.readInt();
                turbo.pt2flag = Turbo.savin.readInt();
                Turbo.eta[2] = Turbo.savin.readDouble();
                Turbo.prat[2] = Turbo.savin.readDouble();
                Turbo.prat[4] = Turbo.savin.readDouble();
                Turbo.eta[3] = Turbo.savin.readDouble();
                Turbo.eta[4] = Turbo.savin.readDouble();
                Turbo.eta[5] = Turbo.savin.readDouble();
                Turbo.eta[7] = Turbo.savin.readDouble();
                Turbo.eta[13] = Turbo.savin.readDouble();
                Turbo.a8d = Turbo.savin.readDouble();
                Turbo.a8max = Turbo.savin.readDouble();
                Turbo.a8rat = Turbo.savin.readDouble();

                Turbo.u0max = Turbo.savin.readDouble();
                Turbo.u0d = Turbo.savin.readDouble();
                Turbo.altmax = Turbo.savin.readDouble();
                Turbo.altd = Turbo.savin.readDouble();
                turbo.arsched = Turbo.savin.readInt();

                turbo.wtflag = Turbo.savin.readInt();
                Turbo.weight = Turbo.savin.readDouble();
                Turbo.minlt = Turbo.savin.readInt();
                Turbo.dinlt = Turbo.savin.readDouble();
                Turbo.tinlt = Turbo.savin.readDouble();
                Turbo.mfan = Turbo.savin.readInt();
                Turbo.dfan = Turbo.savin.readDouble();
                Turbo.tfan = Turbo.savin.readDouble();
                Turbo.mcomp = Turbo.savin.readInt();
                Turbo.dcomp = Turbo.savin.readDouble();
                Turbo.tcomp = Turbo.savin.readDouble();
                Turbo.mburner = Turbo.savin.readInt();
                Turbo.dburner = Turbo.savin.readDouble();
                Turbo.tburner = Turbo.savin.readDouble();
                Turbo.mturbin = Turbo.savin.readInt();
                Turbo.dturbin = Turbo.savin.readDouble();
                Turbo.tturbin = Turbo.savin.readDouble();
                Turbo.mnozl = Turbo.savin.readInt();
                Turbo.dnozl = Turbo.savin.readDouble();
                Turbo.tnozl = Turbo.savin.readDouble();
                Turbo.mnozr = Turbo.savin.readInt();
                Turbo.dnozr = Turbo.savin.readDouble();
                Turbo.tnozr = Turbo.savin.readDouble();
                Turbo.ncflag = Turbo.savin.readInt();
                Turbo.ntflag = Turbo.savin.readInt();

                if(turbo.entype == 3) {
                    turbo.athsched = Turbo.savin.readInt();
                    turbo.aexsched = Turbo.savin.readInt();
                    Turbo.arthd = Turbo.savin.readDouble();
                    Turbo.arexitd = Turbo.savin.readDouble();
                }

                turbo.flightConditionsPanel.setPanl();
                turbo.solve.comPute();
            } catch (IOException n) {
            }
        }

        if(label.equals("Save Data")) {  // Restart Write
            filnam = nsavout.getText();

            try {
                Turbo.sfilo = new FileOutputStream(filnam);
                Turbo.savout = new DataOutputStream(Turbo.sfilo);

                Turbo.savout.writeInt(turbo.entype);
                Turbo.savout.writeInt(turbo.abflag);
                Turbo.savout.writeInt(turbo.fueltype);
                Turbo.savout.writeDouble(Turbo.fhv / Turbo.flconv);
                Turbo.savout.writeDouble(Turbo.tt4d / Turbo.tconv);
                Turbo.savout.writeDouble(Turbo.tt7d / Turbo.tconv);
                Turbo.savout.writeDouble(Turbo.p3p2d);
                Turbo.savout.writeDouble(Turbo.p3fp2d);
                Turbo.savout.writeDouble(Turbo.byprat);
                Turbo.savout.writeDouble(Turbo.acore);
                Turbo.savout.writeDouble(Turbo.a2d / Turbo.aconv);
                Turbo.savout.writeDouble(Turbo.a4);
                Turbo.savout.writeDouble(Turbo.a4p);
                Turbo.savout.writeDouble(Turbo.gama);
                Turbo.savout.writeInt(turbo.gamopt);
                Turbo.savout.writeInt(turbo.pt2flag);
                Turbo.savout.writeDouble(Turbo.eta[2]);
                Turbo.savout.writeDouble(Turbo.prat[2]);
                Turbo.savout.writeDouble(Turbo.prat[4]);
                Turbo.savout.writeDouble(Turbo.eta[3]);
                Turbo.savout.writeDouble(Turbo.eta[4]);
                Turbo.savout.writeDouble(Turbo.eta[5]);
                Turbo.savout.writeDouble(Turbo.eta[7]);
                Turbo.savout.writeDouble(Turbo.eta[13]);
                Turbo.savout.writeDouble(Turbo.a8d / Turbo.aconv);
                Turbo.savout.writeDouble(Turbo.a8max / Turbo.aconv);
                Turbo.savout.writeDouble(Turbo.a8rat);

                Turbo.savout.writeDouble(Turbo.u0max / Turbo.lconv2);
                Turbo.savout.writeDouble(Turbo.u0d / Turbo.lconv2);
                Turbo.savout.writeDouble(Turbo.altmax / Turbo.lconv1);
                Turbo.savout.writeDouble(Turbo.altd / Turbo.lconv1);
                Turbo.savout.writeInt(turbo.arsched);

                Turbo.savout.writeInt(turbo.wtflag);
                Turbo.savout.writeDouble(Turbo.weight);
                Turbo.savout.writeInt(Turbo.minlt);
                Turbo.savout.writeDouble(Turbo.dinlt);
                Turbo.savout.writeDouble(Turbo.tinlt);
                Turbo.savout.writeInt(Turbo.mfan);
                Turbo.savout.writeDouble(Turbo.dfan);
                Turbo.savout.writeDouble(Turbo.tfan);
                Turbo.savout.writeInt(Turbo.mcomp);
                Turbo.savout.writeDouble(Turbo.dcomp);
                Turbo.savout.writeDouble(Turbo.tcomp);
                Turbo.savout.writeInt(Turbo.mburner);
                Turbo.savout.writeDouble(Turbo.dburner);
                Turbo.savout.writeDouble(Turbo.tburner);
                Turbo.savout.writeInt(Turbo.mturbin);
                Turbo.savout.writeDouble(Turbo.dturbin);
                Turbo.savout.writeDouble(Turbo.tturbin);
                Turbo.savout.writeInt(Turbo.mnozl);
                Turbo.savout.writeDouble(Turbo.dnozl);
                Turbo.savout.writeDouble(Turbo.tnozl);
                Turbo.savout.writeInt(Turbo.mnozr);
                Turbo.savout.writeDouble(Turbo.dnozr);
                Turbo.savout.writeDouble(Turbo.tnozr);
                Turbo.savout.writeInt(Turbo.ncflag);
                Turbo.savout.writeInt(Turbo.ntflag);

                if(turbo.entype == 3) {
                    Turbo.savout.writeInt(turbo.athsched);
                    Turbo.savout.writeInt(turbo.aexsched);
                    Turbo.savout.writeDouble(Turbo.arthd);
                    Turbo.savout.writeDouble(Turbo.arexitd);
                }

                turbo.varflag = 0;
                turbo.layin.show(turbo.inputPanel, "first");
            } catch (IOException n) {
            }
        }
        if(label.equals("Cancel")) {  // Forget it
            turbo.varflag = 0;
            turbo.layin.show(turbo.inputPanel, "first");
        }
    }  // end handler
} // end FilesPanel
 
