package gov.nasa.engine_sim_ua;

import java.awt.AWTEvent;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class BurnerPanel extends Panel {

    BurnerRightPanel burnerRightPanel;
    BurnerLeftPanel burnerLeftPanel;

    BurnerPanel(Turbo turbo) {

        setLayout(new GridLayout(1, 2, 10, 10));

        burnerLeftPanel = new BurnerLeftPanel(turbo);
        burnerRightPanel = new BurnerRightPanel(turbo);

        add(burnerLeftPanel);
        add(burnerRightPanel);
    }

    public Insets getInsets() {
        return new Insets(5, 0, 5, 0);
    }

    public class BurnerRightPanel extends Panel {

        Turbo turbo;
        JSlider sldMaxTemp;
        JSlider sldEfficiency;
        JSlider sldPressureRatio;
        Label lblMaterial;
        Choice chcMaterial;
        Choice chcFuel;

        BurnerRightPanel(Turbo target) {

            int maxTemp;
            int efficiency;
            int pressureRatio;

            turbo = target;
            setLayout(new GridLayout(7, 1, 10, 5));

            maxTemp = (int)(((turbo.tt4d - turbo.t4min) / (turbo.t4max - turbo.t4min)) * 1000.);
            efficiency = (int)(((turbo.efficiency[4] - turbo.etmin) / (turbo.etmax - turbo.etmin)) * 1000.);
            pressureRatio = (int)(((turbo.pressureRatio[4] - turbo.etmin) / (turbo.pt4max - turbo.etmin)) * 1000.);

            sldMaxTemp = new JSlider(JSlider.HORIZONTAL, 0, 1000, maxTemp);
            sldMaxTemp.setMajorTickSpacing(100);
            sldMaxTemp.setMinorTickSpacing(10);
            sldEfficiency = new JSlider(JSlider.HORIZONTAL, 0, 1000, efficiency);
            sldPressureRatio = new JSlider(JSlider.HORIZONTAL, 0, 1000, pressureRatio);

            chcMaterial = new Choice();
            chcMaterial.setBackground(Color.white);
            chcMaterial.setForeground(Color.blue);
            chcMaterial.addItem("<-- My Material");
            chcMaterial.addItem("Aluminum");
            chcMaterial.addItem("Titanium ");
            chcMaterial.addItem("Stainless Steel");
            chcMaterial.addItem("Nickel Alloy");
            chcMaterial.addItem("Nickel Crystal");
            chcMaterial.addItem("Ceramic");
            chcMaterial.addItem("Actively Cooled");
            chcMaterial.select(4);

            chcFuel = new Choice();
            chcFuel.addItem("Jet - A");
            chcFuel.addItem("Hydrogen");
            chcFuel.addItem("<-- Your Fuel");
            chcFuel.select(0);

            lblMaterial = new Label("lbm/ft^3 ", Label.LEFT);
            lblMaterial.setForeground(Color.blue);

            add(chcFuel);
            add(sldMaxTemp);
            add(sldPressureRatio);
            add(sldEfficiency);
            add(new Label(" ", Label.LEFT));
            add(chcMaterial);
            add(lblMaterial);

            ChangeListener sliderChanged = new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    onSliderValueChange();
                }
            };
            sldEfficiency.addChangeListener(sliderChanged);
            sldPressureRatio.addChangeListener(sliderChanged);
            sldMaxTemp.addChangeListener(sliderChanged);
        }


        public void handleMat() {
            Double V1;
            Double V2;
            double v1;
            double v2;

            turbo.fueltype = chcFuel.getSelectedIndex();
            if(turbo.fueltype == 0) {
                turbo.fuelHeatValue = 18600.;
            }
            if(turbo.fueltype == 1) {
                turbo.fuelHeatValue = 49900.;
            }
            burnerLeftPanel.getTfFuelHeatValue().setBackground(Color.black);
            burnerLeftPanel.getTfFuelHeatValue().setForeground(Color.yellow);
            turbo.fhvd = turbo.fuelHeatValue * turbo.flconv;
            burnerLeftPanel.getTfFuelHeatValue().setText(String.format("%.0f", turbo.fhvd));

            if(turbo.fueltype == 2) {
                burnerLeftPanel.getTfFuelHeatValue().setBackground(Color.white);
                burnerLeftPanel.getTfFuelHeatValue().setForeground(Color.black);
            }

            // burner
            turbo.burnerMaterial = chcMaterial.getSelectedIndex();
            if(turbo.burnerMaterial > 0) {
                burnerLeftPanel.getDb().setBackground(Color.black);
                burnerLeftPanel.getDb().setForeground(Color.yellow);
                burnerLeftPanel.getTb().setBackground(Color.black);
                burnerLeftPanel.getTb().setForeground(Color.yellow);
            }
            if(turbo.burnerMaterial == 0) {
                burnerLeftPanel.getDb().setBackground(Color.white);
                burnerLeftPanel.getDb().setForeground(Color.blue);
                burnerLeftPanel.getTb().setBackground(Color.white);
                burnerLeftPanel.getTb().setForeground(Color.blue);
            }
            switch (turbo.burnerMaterial) {
                case 0:
                    V1 = Double.valueOf(burnerLeftPanel.getDb().getText());
                    v1 = V1;
                    V2 = Double.valueOf(burnerLeftPanel.getTb().getText());
                    v2 = V2;
                    turbo.dburner = v1 / turbo.dconv;
                    turbo.tburner = v2 / turbo.tconv;
                    break;
                case 1:
                    turbo.dburner = 170.7;
                    turbo.tburner = 900.;
                    break;
                case 2:
                    turbo.dburner = 293.02;
                    turbo.tburner = 1500.;
                    break;
                case 3:
                    turbo.dburner = 476.56;
                    turbo.tburner = 2000.;
                    break;
                case 4:
                    turbo.dburner = 515.2;
                    turbo.tburner = 2500.;
                    break;
                case 5:
                    turbo.dburner = 515.2;
                    turbo.tburner = 3000.;
                    break;
                case 6:
                    turbo.dburner = 164.2;
                    turbo.tburner = 3000.;
                    break;
                case 7:
                    turbo.dburner = 515.2;
                    turbo.tburner = 4500.;
                    break;
            }
            turbo.solve.compute();
        }

        public void onSliderValueChange() {     // burner design
            int maxTemp = sldMaxTemp.getValue();
            int efficiency = sldEfficiency.getValue();
            int pressureRatio = sldPressureRatio.getValue();

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.vmn1 = turbo.t4min;
                turbo.vmx1 = turbo.t4max;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                turbo.vmn3 = turbo.etmin;
                turbo.vmx3 = turbo.pt4max;
            }
            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                turbo.vmx2 = 100.0 - 100.0 * turbo.et4ref;
                turbo.vmn2 = turbo.vmx2 - 20.0;
                turbo.vmx3 = 100.0 - 100.0 * turbo.p4ref;
                turbo.vmn3 = turbo.vmx3 - 20.0;
            }

            double tt4d = maxTemp * (turbo.vmx1 - turbo.vmn1) / 1000. + turbo.vmn1;
            double efficiency4 = efficiency * (turbo.vmx2 - turbo.vmn2) / 1000. + turbo.vmn2;
            double pressureRatio4 = pressureRatio * (turbo.vmx3 - turbo.vmn3) / 1000. + turbo.vmn3;

            // burner design
            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                turbo.tt4d = tt4d;
                turbo.efficiency[4] = efficiency4;
                turbo.pressureRatio[4] = pressureRatio4;
            }
            else if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                turbo.tt4d = tt4d * turbo.t4ref / 100. + turbo.t4ref;
                turbo.efficiency[4] = turbo.et4ref + efficiency4 / 100.;
                turbo.pressureRatio[4] = turbo.p4ref + pressureRatio4 / 100.;
            }
            turbo.tt4 = turbo.tt4d / turbo.tconv;

            burnerLeftPanel.getTfMaxTemp().setText(String.valueOf(tt4d));
            burnerLeftPanel.getTfEfficiency().setText(String.valueOf(efficiency4));
            burnerLeftPanel.getTfPressureRatio().setText(String.valueOf(pressureRatio4));

            turbo.solve.compute();
        }  // end handle
    }  // end rightPanel

    public class BurnerLeftPanel extends Panel {

        Turbo turbo;
        private TextField tfMaxTemp;
        private TextField tfEfficiency;
        private TextField tfPressureRatio;
        private TextField tfFuelHeatValue;
        Label lblTmax;
        Label lblEfficiency;
        Label lblPressureRatio;
        Label lblFuelHV;
        Label lblDensity;
        Label lblMaterialMaxTemp;
        Label lblMaterials;
        private TextField db;
        private TextField tb;

        BurnerLeftPanel(Turbo target) {

            turbo = target;
            setLayout(new GridLayout(7, 2, 5, 5));

            lblTmax = new Label("Tmax ºR", Label.CENTER);
            setTfMaxTemp(new TextField(String.valueOf((float)turbo.tt4d), 5));
            lblEfficiency = new Label("Efficiency", Label.CENTER);
            setTfEfficiency(new TextField(String.valueOf((float)turbo.efficiency[4]), 5));
            lblPressureRatio = new Label("Press. Ratio", Label.CENTER);
            setTfPressureRatio(new TextField(String.valueOf((float)turbo.pressureRatio[4]), 5));
            lblFuelHV = new Label("FHV Btu/lb", Label.CENTER);
            setTfFuelHeatValue(new TextField(String.valueOf((float)turbo.fuelHeatValue), 5));
            getTfFuelHeatValue().setBackground(Color.black);
            getTfFuelHeatValue().setForeground(Color.yellow);

            lblMaterialMaxTemp = new Label("T lim ºR", Label.CENTER);
            lblMaterialMaxTemp.setForeground(Color.blue);
            lblMaterials = new Label("Materials:", Label.CENTER);
            lblMaterials.setForeground(Color.blue);
            lblDensity = new Label("Density", Label.CENTER);
            lblDensity.setForeground(Color.blue);

            setDb(new TextField(String.valueOf((float)turbo.dburner), 5));
            getDb().setBackground(Color.black);
            getDb().setForeground(Color.yellow);
            setTb(new TextField(String.valueOf((float)turbo.tburner), 5));
            getTb().setBackground(Color.black);
            getTb().setForeground(Color.yellow);

            add(lblFuelHV);
            add(getTfFuelHeatValue());
            add(lblTmax);
            add(getTfMaxTemp());
            add(lblPressureRatio);
            add(getTfPressureRatio());
            add(lblEfficiency);
            add(getTfEfficiency());
            add(lblMaterials);
            add(new Label(" ", Label.CENTER));
            add(lblMaterialMaxTemp);
            add(getTb());
            add(lblDensity);
            add(getDb());
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
            Double V4;
            Double V5;
            Double V6;
            double v1;
            double v2;
            double v3;
            double v4;
            double v5;
            double v6;
            int i1;
            int i2;
            int i3;
            float fl1;

            V1 = Double.valueOf(getTfMaxTemp().getText());
            v1 = V1;
            V2 = Double.valueOf(getTfEfficiency().getText());
            v2 = V2;
            V3 = Double.valueOf(getTfPressureRatio().getText());
            v3 = V3;
            V6 = Double.valueOf(getTfFuelHeatValue().getText());
            v6 = V6;
            V4 = Double.valueOf(getDb().getText());
            v4 = V4;
            V5 = Double.valueOf(getTb().getText());
            v5 = V5;

            // Materials
            if(turbo.burnerMaterial == 0) {
                if(v4 <= 1.0 * turbo.dconv) {
                    v4 = 1.0 * turbo.dconv;
                    getDb().setText(String.format("%.0f", v4 * turbo.dconv));
                }
                turbo.dburner = v4 / turbo.dconv;
                if(v5 <= 500. * turbo.tconv) {
                    v5 = 500. * turbo.tconv;
                    getTb().setText(String.format("%.0f", v5 * turbo.tconv));
                }
                turbo.tburner = v5 / turbo.tconv;
            }

            if(turbo.units == Turbo.Unit.ENGLISH || turbo.units == Turbo.Unit.METRIC) {
                // Max burner temp
                turbo.tt4d = v1;
                turbo.vmn1 = turbo.t4min;
                turbo.vmx1 = turbo.t4max;
                if(v1 < turbo.vmn1) {
                    turbo.tt4d = v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    getTfMaxTemp().setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    turbo.tt4d = v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    getTfMaxTemp().setText(String.valueOf(fl1));
                }
                turbo.tt4 = turbo.tt4d / turbo.tconv;
                // burner  efficiency
                turbo.efficiency[4] = v2;
                turbo.vmn2 = turbo.etmin;
                turbo.vmx2 = turbo.etmax;
                if(v2 < turbo.vmn2) {
                    turbo.efficiency[4] = v2 = turbo.vmn2;
                    fl1 = (float)v2;
                    getTfEfficiency().setText(String.valueOf(fl1));
                }
                if(v2 > turbo.vmx2) {
                    turbo.efficiency[4] = v2 = turbo.vmx2;
                    fl1 = (float)v2;
                    getTfEfficiency().setText(String.valueOf(fl1));
                }
                //  burner pressure ratio
                turbo.pressureRatio[4] = v3;
                turbo.vmn3 = turbo.etmin;
                turbo.vmx3 = turbo.pt4max;
                if(v3 < turbo.vmn3) {
                    turbo.pressureRatio[4] = v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    getTfPressureRatio().setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    turbo.pressureRatio[4] = v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    getTfPressureRatio().setText(String.valueOf(fl1));
                }
                // fuel heating value
                if(turbo.fueltype == 2) {
                    turbo.fhvd = v6;
                    turbo.fuelHeatValue = turbo.fhvd / turbo.flconv;
                }
            }

            if(turbo.units == Turbo.Unit.PERCENT_CHANGE) {
                // Max burner temp
                turbo.vmn1 = -10.0;
                turbo.vmx1 = 10.0;
                if(v1 < turbo.vmn1) {
                    v1 = turbo.vmn1;
                    fl1 = (float)v1;
                    getTfMaxTemp().setText(String.valueOf(fl1));
                }
                if(v1 > turbo.vmx1) {
                    v1 = turbo.vmx1;
                    fl1 = (float)v1;
                    getTfMaxTemp().setText(String.valueOf(fl1));
                }
                turbo.tt4d = v1 * turbo.t4ref / 100. + turbo.t4ref;
                turbo.tt4 = turbo.tt4d / turbo.tconv;
                // burner  efficiency
                turbo.vmx2 = 100.0 - 100.0 * turbo.et4ref;
                turbo.vmn2 = turbo.vmx2 - 20.0;
                if(v2 < turbo.vmn2) {
                    v2 = turbo.vmn2;
                    fl1 = (float)v2;
                    getTfEfficiency().setText(String.valueOf(fl1));
                }
                if(v2 > turbo.vmx2) {
                    v2 = turbo.vmx2;
                    fl1 = (float)v2;
                    getTfEfficiency().setText(String.valueOf(fl1));
                }
                turbo.efficiency[4] = turbo.et4ref + v2 / 100.;
                //  burner pressure ratio
                turbo.vmx3 = 100.0 - 100.0 * turbo.p4ref;
                turbo.vmn3 = turbo.vmx3 - 20.0;
                if(v3 < turbo.vmn3) {
                    v3 = turbo.vmn3;
                    fl1 = (float)v3;
                    getTfPressureRatio().setText(String.valueOf(fl1));
                }
                if(v3 > turbo.vmx3) {
                    v3 = turbo.vmx3;
                    fl1 = (float)v3;
                    getTfPressureRatio().setText(String.valueOf(fl1));
                }
                turbo.pressureRatio[4] = turbo.p4ref + v3 / 100.;
            }

            i1 = (int)(((v1 - turbo.vmn1) / (turbo.vmx1 - turbo.vmn1)) * 1000.);
            i2 = (int)(((v2 - turbo.vmn2) / (turbo.vmx2 - turbo.vmn2)) * 1000.);
            i3 = (int)(((v3 - turbo.vmn3) / (turbo.vmx3 - turbo.vmn3)) * 1000.);

            burnerRightPanel.sldMaxTemp.setValue(i1);
            burnerRightPanel.sldEfficiency.setValue(i2);
            burnerRightPanel.sldPressureRatio.setValue(i3);

            turbo.solve.compute();
        }  // end handle

        public TextField getTfMaxTemp() {
            return tfMaxTemp;
        }

        public void setTfMaxTemp(TextField tfMaxTemp) {
            this.tfMaxTemp = tfMaxTemp;
        }

        public TextField getTfEfficiency() {
            return tfEfficiency;
        }

        public void setTfEfficiency(TextField tfEfficiency) {
            this.tfEfficiency = tfEfficiency;
        }

        public TextField getTfPressureRatio() {
            return tfPressureRatio;
        }

        public void setTfPressureRatio(TextField tfPressureRatio) {
            this.tfPressureRatio = tfPressureRatio;
        }

        public TextField getTfFuelHeatValue() {
            return tfFuelHeatValue;
        }

        public void setTfFuelHeatValue(TextField tfFuelHeatValue) {
            this.tfFuelHeatValue = tfFuelHeatValue;
        }

        public TextField getDb() {
            return db;
        }

        public void setDb(TextField db) {
            this.db = db;
        }

        public TextField getTb() {
            return tb;
        }

        public void setTb(TextField tb) {
            this.tb = tb;
        }
    }  //  end  inletLeftPanel
}  // end BurnerPanel
 
