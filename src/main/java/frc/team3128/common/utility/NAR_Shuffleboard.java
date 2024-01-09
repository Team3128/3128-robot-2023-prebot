package frc.team3128.common.utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import com.ctre.phoenix.sensors.WPI_Pigeon2;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.team3128.subsystems.Elevator;
import frc.team3128.subsystems.Manipulator;
import frc.team3128.subsystems.NAR_PIDSubsystem;

import static frc.team3128.Constants.ShuffleboardConstants.*;

/**
 * Wrapper for {@link Shuffleboard}
 * @since 2022 RAPID REACT
 * @author Mason Lam
 */
public class NAR_Shuffleboard {

    /**
     * Storage class for NAR_Shuffleboard
     */
    private static class entryInfo {
        
        private GenericEntry m_data;

        private Supplier<Object> m_supply;

        private SimpleWidget m_entry;

        /**
         * Creates a new entry Info
         *
         * @param entry Widget where the entry 
         * @param supply supplier updating the entry
         */
        public entryInfo(SimpleWidget entry, Supplier<Object> supply){
            m_supply = supply;
            m_entry = entry;
            m_data = entry.getEntry();
        }

        public void update() {
            if(m_supply == null) return;
            m_data.setValue(m_supply.get());
        }
    }

    private static HashMap<String, HashMap<String, entryInfo>> tabs = new HashMap<String, HashMap<String,entryInfo>>();
    public static HashMap<String, boolean[][]> entryPositions = new HashMap<String, boolean[][]>();

    /**
    * Creates a new tab entry
    *
    * @param tabName the title of the new tab
    */
    private static void create_tab(String tabName) {
        tabs.put(tabName, new HashMap<String,entryInfo>());
        entryPositions.put(tabName, new boolean[WINDOW_WIDTH][WINDOW_HEIGHT]); // TODO: added this
    }

    /**
    * Displays a value in Shuffleboard
    *
    * @param tabName the title of the tab to select
    * @param name the name of the entry
    * @param data value to display
    * @param x -coord of the entry starting from 0
    * @param y -coord of the entry starting from 0
    * @return simple widget that can be modified
    */
    public static SimpleWidget addData(String tabName, String name, Object data, int x, int y) {
        return addData(tabName, name, data, x, y, 1, 1);
    }

    /**
     * Displays an updating value in Shuffleboard
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @param supply object supplier to constantly update value
     * @param x -coord of the entry starting from 0
     * @param y -coord of the entry starting from 0
     * @return simple widget that can be modified
     */
    public static SimpleWidget addData(String tabName, String name, Supplier<Object> supply, int x, int y) {
        return addData(tabName, name, supply, x, y, 1, 1);
    }

    /**
     * Displays an updating value in Shuffleboard
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @param supply object supplier to constantly update value
     * @param x -coord of the entry starting from 0
     * @param y -coord of the entry starting from 0
     * @param width -of the entry
     * @param height -of the entry
     * @return simple widget that can be modified
     */
    public static SimpleWidget addData(String tabName, String name, Supplier<Object> supply, int x, int y, int width, int height){
        if(!tabs.containsKey(tabName)) create_tab(tabName);
        fillEntryPositions(x,y,width,height, tabName);
        if(tabs.get(tabName).containsKey(name)) {
            tabs.get(tabName).get(name).m_supply = supply;
            return tabs.get(tabName).get(name).m_entry;
        }
        SimpleWidget entry = Shuffleboard.getTab(tabName).add(name,supply.get()).withPosition(x, y).withSize(width, height);
        tabs.get(tabName).put(name, new entryInfo(entry,supply));
        return entry;
    }

    /**
    * Displays a value in Shuffleboard
    *
    * @param tabName the title of the tab to select
    * @param name the name of the entry
    * @param data value to display
    * @param x -coord of the entry starting from 0
    * @param y -coord of the entry starting from 0
    * @param width -of the entry
    * @param height -of the entry
    * @return simple widget that can be modified
    */

    public static SimpleWidget addData(String tabName, String name, Object data, int x, int y, int width, int height) {
        if(!tabs.containsKey(tabName)) create_tab(tabName);
        fillEntryPositions(x,y,width,height,tabName);
        if (tabs.get(tabName).containsKey(name)) {
            tabs.get(tabName).get(name).m_data.setValue(data);
            return tabs.get(tabName).get(name).m_entry;
        }
        SimpleWidget entry = Shuffleboard.getTab(tabName).add(name,data).withPosition(x, y).withSize(width,height);
        tabs.get(tabName).put(name, new entryInfo(entry,null));
        return entry;
    }

    /**
     * Displays sendable values, like subsystems and command, works on all classes that extend sendable
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @param data sendable value to display
     * @param x x-coord of the entry
     * @param y y-coord of the entry
     * @return sendable widget that can be modified
     */
    public static ComplexWidget addSendable(String tabName, String name, Sendable data, int x, int y) {
        if (data instanceof SubsystemBase) return addSendable(tabName, name, data, x, y, 2, 1);
        if (data instanceof PIDController) return addSendable(tabName, name, data, x, y, 1, 2);
        if (data instanceof WPI_Pigeon2) return addSendable(tabName, name, data, x, y, 2, 2);
        return addSendable(tabName, name, data, x, y, 1, 1); // Default width and height
    }

    /**
     * Displays sendable values, like subsystems and command, works on all classes that extend sendable
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @param data sendable value to display
     * @param x x-coord of the entry
     * @param y y-coord of the entry
     * @return sendable widget that can be modified
     */
    public static ComplexWidget addSendable(String tabName, String name, Sendable data, int x, int y, int width, int height) {
        try {
            if(!tabs.containsKey(tabName)) create_tab(tabName);
            fillEntryPositions(x, y, width, height, tabName);
            return Shuffleboard.getTab(tabName).add(name, data).withPosition(x,y).withSize(width, height);
        }
        catch(Exception e) {
            return null;
        }
        // return addSendable(tabName, name, data, x, y).withSize(width,height);
    }

    public static ComplexWidget addVideoStream(String tabName, String name, String cameraName, String URL, int x, int y, int width, int height) {
        try {
            if (!tabs.containsKey(tabName)) create_tab(tabName);
            fillEntryPositions(x, y, width, height, tabName);

            ComplexWidget videoStream = Shuffleboard.getTab(tabName)
            .addCamera(name, cameraName, URL)
            .withProperties(Map.of("showControls", false))
            .withPosition(x, y)
            .withSize(width, height);            
            return videoStream;
        }
        catch(Exception e) {
            return null;
        }
    }

    public static void addAutos(String[] autoNames) {
        if (!tabs.containsKey("Autos")) create_tab("Autos");
        ShuffleboardLayout autoLayout = Shuffleboard.getTab("Autos")
        .getLayout("Auto Names", BuiltInLayouts.kList)
        .withSize(2,4);
        // .withProperties(Map.of("Label position", "HIDDEN"));
        for (int i = 1; i <= autoNames.length; i++) {
            autoLayout.add(""+i, autoNames[i-1]); // Shuffleboard doesn't display elements in order of String[] names so we can't use an auto name's index to reference it
        }
        // DoubleSupplier autoNumber = NAR_Shuffleboard.debug("Autos", "Selector", -1, 3, 0);
        // var autoEntry = NAR_Shuffleboard.addData("Autos", "TOGGLE", false, 2, 0).withWidget("Toggle Button"); // TODO why use var vs SimpleWidget?
        // debug = ()-> debugEntry.getEntry().getBoolean(false);
        // NAR_Shuffleboard.addData(getName(), "DEBUG", ()-> debug.getAsBoolean(), 2, 1);
    }

    /**
     * Creates a debug entry, allows user to edit variable from Shuffleboard
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @param Default starting value for the entry
     * @param x x-coord of the entry
     * @param y y-coord of the entry
     * @return DoubleSupplier containing the value in the entry
     */
    public static DoubleSupplier debug(String tabName, String name, double Default, int x, int y) {
        if(!tabs.containsKey(tabName)){
            create_tab(tabName);
        }
        SimpleWidget tab = addData(tabName, name, Default, x, y);
        return ()-> tab.getEntry().getDouble(Default);
    }

    /**
     * Creates a quick PID Tuning setup
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @param prefix String that goes before PID entry names
     * @return HashMap with keys "KF","KP","KI","KD", and "SETPOINT"
     */
    public static HashMap<String,DoubleSupplier> PID_Setup(String tabName, String prefix) {
        ShuffleboardTab tab = Shuffleboard.getTab(tabName);
        HashMap<String,DoubleSupplier> PID = new HashMap<String,DoubleSupplier>();
        for (String i : new String[]{"KF","KP","KI","KD","SETPOINT"}) {
            GenericEntry entry = tab.add(prefix + "_" + i,0).getEntry();
            PID.put(i,()-> entry.getDouble(0));
        }
        return PID;
    }

    /**
     * Get the value from an entry
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @return Object stored in the entry
     */
    public static Object getValue(String tabName, String name){
        return tabs.get(tabName).get(name).m_data.get().getValue();
    }

    /**
     * Get the Simple Widget object from an entry
     * 
     * @param tabName the title of the tab to select
     * @param name the name of the entry
     * @return SimpleWidget stored in the entry
     */
    public static SimpleWidget getEntry(String tabName,String name) {
        return tabs.get(tabName).get(name).m_entry;
    }

    /**
     * Updates every entry
     */
    public static void update() {
        for(String i : tabs.keySet()){
            for(String j : tabs.get(i).keySet()){
                tabs.get(i).get(j).update();
            }
        }
    }

    private static void fillEntryPositions(int x, int y, int width, int height, String tabName) {
        if (x + width > WINDOW_WIDTH || y + height > WINDOW_HEIGHT) { throw new IllegalArgumentException("Widget Position Out of Bounds (" + x + "," + y + ") at Tab: " + tabName); }
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if (entryPositions.get(tabName)[i][j]) { throw new IllegalArgumentException("Widget Position Overlapping (" + i + "," + j + ") at Tab: " + tabName); }
                entryPositions.get(tabName)[i][j] = true;
            }
        }
    }
}