package course.gui;

/**
 *
 * @author jstar
 */
import course.logic.GradingScale;
import course.logic.Group;
import course.logic.IOTools;
import course.logic.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import javax.swing.table.DefaultTableCellRenderer;
import course.logic.ScaleClassAnnotation;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.table.JTableHeader;

public class GUI extends JFrame {

    // ===== Components =====
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;

    private JPanel bottomPanel;
    private JPanel bottomPanelRow1;
    private JPanel bottomPanelRow2;
    private JList<String> scaleList;
    private JLabel inputLabel1, inputLabel2;
    private JTextField inputField;
    private JButton addButton;

    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu helpMenu;
    private JMenuItem loadItem;
    private JMenuItem saveItem;
    private JMenuItem exitItem;
    private JMenuItem aboutItem;

    // ===== Other =====
    private static Font currentFont = new Font("Courier", Font.PLAIN, 18);
    private static final String CONFIG_FILE = ".course_gui_config";
    private final Configuration configuration = new Configuration(CONFIG_FILE);
    private final String LAST_DIR = "CourseGUI.last.dir";

    Group group;
    GradingScale scale;

    public GUI() {
        super("Course grading GUI");

        initFrame();
        initComponents();
        initMenu();
        initLayout();
        initActions();
        setFontRecursively(this, currentFont, 0);
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        String[] columnNames = {"Full name", "Points", "Grade"};
        Object[][] data = {};

        tableModel = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        /* Wszystkie nagłówki wycentrowane
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
            }
        });
         */
 /* Tylko 2 i 3 kolumna wycentrowane */
        table.getColumnModel().getColumn(0).setHeaderRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.LEFT);
                setBackground(Color.lightGray);
            }
        });
        table.getColumnModel().getColumn(1).setHeaderRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(Color.lightGray);
            }
        });
        table.getColumnModel().getColumn(2).setHeaderRenderer(new DefaultTableCellRenderer() {
            {
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(Color.lightGray);
            }
        });
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i : new int[]{1, 2}) {
            centerRenderer.setBackground(Color.white);
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setFont(currentFont);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setFillsViewportHeight(
                true);
        tableScrollPane = new JScrollPane(table);

        bottomPanelRow1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanelRow2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel = new JPanel(new BorderLayout());

        inputLabel1 = new JLabel("Choose grading scale: ");
        inputLabel2 = new JLabel(" or provide your own: ");
        DefaultListModel<String> sclModel = getSCLModel(System.getProperty("java.class.path").split(System.getProperty("path.separator")));
        scaleList = new JList<>(sclModel);

        scaleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inputField = new JTextField(25);
        addButton = new JButton("Apply");
    }

    private void initMenu() {
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        helpMenu = new JMenu("Help");

        loadItem = new JMenuItem("Load students");
        saveItem = new JMenuItem("Save students");
        exitItem = new JMenuItem("Exit");
        aboutItem = new JMenuItem("About");

        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void initLayout() {
        setLayout(new BorderLayout());

        add(tableScrollPane, BorderLayout.CENTER);

        bottomPanelRow1.add(inputLabel1);
        scaleList.setVisibleRowCount(1);
        bottomPanelRow1.add(new JScrollPane(scaleList));

        bottomPanelRow2.add(inputLabel2);
        bottomPanelRow2.add(inputField);
        bottomPanelRow2.add(addButton);
        bottomPanel.add(bottomPanelRow1, BorderLayout.CENTER);
        bottomPanel.add(bottomPanelRow2, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initActions() {
        loadItem.addActionListener(this::loadFile);
        saveItem.addActionListener(this::saveFile);
        exitItem.addActionListener(this::onExit);
        aboutItem.addActionListener(this::onAbout);
        scaleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                setGradingScale(scaleList.getSelectedValue());
            }
        });
        addButton.addActionListener(this::onInput);
        inputField.addActionListener(this::onInput);
        JTableHeader th = table.getTableHeader();
        th.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int colIndex = table.columnAtPoint(e.getPoint());
                if (colIndex == 0) {
                    group.sort(new Comparator<Student>() {
                        @Override
                        public int compare(Student a, Student b) {
                            String[] na = a.getFullName().split("\\s+");
                            String[] nb = b.getFullName().split("\\s+");
                            if (na.length == 2 && nb.length == 2) {
                                int surnameCmp = na[1].compareTo(nb[1]);
                                return surnameCmp != 0 ? surnameCmp : na[0].compareTo(nb[0]);
                            } else {
                                return a.getFullName().compareTo(b.getFullName());
                            }
                        }
                    });
                    updateTable();
                }
                if (colIndex > 0) {
                    group.sort(new Comparator<Student>() {
                        @Override
                        public int compare(Student a, Student b) {
                            return Double.compare(a.getPoints(), b.getPoints());
                        }
                    });
                    updateTable();
                }
            }
        });
    }

    private void scanDir(File dir, DefaultListModel<String> model) {
        System.out.println(dir + ":");
        File[] files = dir.listFiles();
        if (files != null) {
            System.out.println("\t " + files.length + " files");
            for (File f : files) {
                //System.out.println("\t" + f);
                if (f.isDirectory()) {
                    scanDir(f, model);
                } else if (f.getAbsolutePath().endsWith(".class")) {
                    try {
                        String className = f.getParent().replaceAll(".*/", "") + "." + f.getName().replace(".class", "");
                        System.out.println("\t" + className);
                        Class<?> c = Class.forName(className);
                        ScaleClassAnnotation ann = c.getAnnotation(ScaleClassAnnotation.class);
                        if (ann != null) {
                            model.addElement(className);
                            System.out.println("\t\t ScaleClass");
                        }
                    } catch (Exception ex) {
                        System.out.println("\t-> " + ex.getClass());
                        // ignore this class
                    }
                }
            }
        } else {
            System.out.println("\t no files");
        }
    }

    private DefaultListModel<String> getSCLModel(String[] path) {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String pe : path) {
            File dir = new File(pe);
            scanDir(dir, model);
        }
        return model;
    }

    private void onExit(ActionEvent e) {
        dispose();
    }

    private void onAbout(ActionEvent e) {
        JOptionPane.showMessageDialog(
                this,
                "Przykładowy graficzny interfejs\ndo programu Gading Scale",
                "O programie",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void loadFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(getLastUsedDirectory());
        setFontRecursively(fileChooser, currentFont, 0);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File gFile = fileChooser.getSelectedFile();
            try {
                group = IOTools.readGroupPatiently(gFile.getAbsolutePath());
                saveLastUsedDirectory(gFile.getParent());
                updateTable();
            } catch (Exception ex) {
                group = null;
                JOptionPane.showMessageDialog(this, "Unable to load students' data from: " + gFile.getAbsolutePath() + "\n" + ex.getClass() + ": " + ex.getMessage());
            }
        }
    }

    private void saveFile(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser(getLastUsedDirectory());
        setFontRecursively(fileChooser, currentFont, 0);
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File gFile = fileChooser.getSelectedFile();
            try {
                IOTools.saveGroup(group, gFile.getAbsolutePath());
                saveLastUsedDirectory(gFile.getParent());
                JOptionPane.showMessageDialog(this, "Students' data saved to: " + gFile.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unable to save students' data to: " + gFile.getAbsolutePath());
            }
        }
    }

    private void onInput(ActionEvent e) {
        String text = inputField.getText().trim();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No classname given.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            inputField.requestFocusInWindow();
            return;
        }

        setGradingScale(text);
    }

    private void setGradingScale(String text) {
        try {
            Class c = Class.forName(text);
            Object o = c.getConstructor().newInstance();
            scale = (GradingScale) o;
        } catch (ClassNotFoundException ex) {
            System.err.println("Can't find " + text + ".class");
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
            System.err.println(ex.getMessage());
        } catch (ClassCastException ex) {
            System.err.println(text + " does not implement GradingScale");
        }
        updateTable();
    }

    /*
    private void onAdd(ActionEvent e) {
        String text = inputField.getText().trim();

        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No data given.",
                    "Error",
                    JOptionPane.WARNING_MESSAGE
            );
            inputField.requestFocusInWindow();
            return;
        }

        int newId = tableModel.getRowCount() + 1;
        tableModel.addRow(new Object[]{newId, text, 0});

        inputField.setText("");
        inputField.requestFocusInWindow();
    }
     */
    // Helper: 
    private void updateTable() {
        if (group == null) {
            return;
        }
        int dH = 2 + (int) currentFont.getLineMetrics("A", ((Graphics2D) tableScrollPane.getGraphics()).getFontRenderContext()).getHeight();
        table.setRowHeight(dH);
        int i = 0;
        tableModel.setRowCount(group.size());
        for (Student s : group) {
            tableModel.setValueAt(s.getFullName(), i, 0);
            tableModel.setValueAt(s.getPoints(), i, 1);
            if (scale != null) {
                tableModel.setValueAt(scale.grade(s.getPoints()), i, 2);
            }
            i++;
        }
    }

    // Helper - retrieves the last used directory from the config file
    private String getLastUsedDirectory() {
        String lsd = configuration.getValue(LAST_DIR);
        if (lsd == null) {
            lsd = ".";
        }
        return lsd;
    }

    // Helper - saves the last used directory
    private void saveLastUsedDirectory(String directory) {
        try {
            configuration.saveValue(LAST_DIR, directory);
        } catch (IOException e) {
            //message.setText(e.getLocalizedMessage());
        }
    }

    private void setFontRecursively(Component comp, Font font, int d) {
        if (comp == null) {
            return;
        }
        comp.setFont(font);
        //
        if (comp instanceof Container container) {
            for (Component child : container.getComponents()) {
                setFontRecursively(child, font, d + 1);
            }
        }
        // Needs specific navigation, since JMenu does not show menu components as Components
        if (comp instanceof JMenu menu) {
            for (int i = 0; i < menu.getItemCount(); i++) {
                setFontRecursively(menu.getItem(i), font, d + 1);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI frame = new GUI();
            frame.setVisible(true);
        });
    }
}
