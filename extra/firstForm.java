import javax.swing.*;
import java.io.*;
import java.text.Format;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.io.Files;
import org.apache.logging.log4j.message.StringFormattedMessage;

public class firstForm extends JFrame {
    private JPanel firstGUI;
    private JTabbedPane tabbedPane1;
    private JPanel OpenPane;
    private JButton openButton;
    private JTextArea sheetArea;
    private JTextField openFilenameInput;
    private JButton clearSheetButton;
    private JButton deleteSheetButton;
    private JButton restoreBackup;
    private JButton createTheSheetButton;
    private JFormattedTextField filenameCreated;
    private JButton addRecordButton;
    private JFormattedTextField RecordField1;
    private JFormattedTextField RecordField2;
    private JFormattedTextField RecordField4;
    private JFormattedTextField RecordField3;
    private JScrollPane sheetPane;
    private JButton backupCreateButton;
    private JButton toXSLX;
    private JButton updateTheSheetButton;
    private JTextField nameTextField;
    private JFormattedTextField numberTextField;
    private JButton searchByTheNumberButton;
    private JButton searchByTheNameButton;
    private JButton editRecordButton;
    private JButton deleteButton;
    final JFrame parent = new JFrame();

    public firstForm(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(firstGUI);
        this.pack();

        AtomicInteger identificator = new AtomicInteger();

        Hashtable phoneChains = new Hashtable(10000, (float) 0.5);
        Map<String, ArrayList> nameMap = new HashMap<>();

        openButton.addActionListener(e -> { //e is for lambda, stands for [new ActionListener() { @Override public void actionPerformed(ActionEvent e) {]
            nameMap.clear();
            File file = new File(filepathing(openFilenameInput.getText()));
            identificator.set(0);
            boolean permit = true;
            try (BufferedReader br = new BufferedReader(new FileReader(file));
                 FileWriter myWriter = new FileWriter(filepathing(openFilenameInput.getText()), true)) {
                sheetArea.setText(null);
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.substring(1, 4).equals("   ")) {
                        identificator.getAndAdd(56);
                        sheetArea.append(line + "\n");
                    } else {
                        String tempName = line.split(",")[0];
                        if (!nameMap.containsKey(tempName)) {
                            ArrayList<Integer> x = new ArrayList<>();
                            nameMap.put(tempName, x);
                            x.add(identificator.get());
                        } else {
                            nameMap.get(tempName).add(identificator.get());
                        }
                        phoneChains.put(Long.parseLong(line.split(",")[2]), identificator.get());
                        sheetArea.append(line + "\n");
                        identificator.getAndAdd(line.length() + 1); // additional 1 because of \n
                    }
                }
                permit = true;
            } catch (IOException ex) {
                sheetArea.setText("File is not found.");
                permit = false;
            }

            addRecordButton.setEnabled(permit);
            deleteSheetButton.setEnabled(permit);
            clearSheetButton.setEnabled(permit);
            backupCreateButton.setEnabled(permit);
            toXSLX.setEnabled(permit);
            updateTheSheetButton.setEnabled(permit);
            searchByTheNameButton.setEnabled(permit);
            searchByTheNumberButton.setEnabled(permit);
            editRecordButton.setEnabled(permit);
            deleteButton.setEnabled(permit);

            addRecordButton.addActionListener(d -> {
                try {
                    BufferedReader ReadFile = new BufferedReader(new FileReader(filepathing(openFilenameInput.getText())));
                    if (numberWasFound(Long.parseLong(RecordField3.getText().trim()), phoneChains)) {
                        JOptionPane.showMessageDialog(parent, "The person with this number has already been added to the sheet.");
                    } else {
                        FileWriter myWriter = new FileWriter(filepathing(openFilenameInput.getText()), true);
                        phoneChains.put(Long.parseLong(RecordField3.getText()), identificator.get());
                        if (!nameMap.containsKey(RecordField1.getText())) {
                            ArrayList<Integer> x = new ArrayList<>();
                            nameMap.put(RecordField1.getText(), x);
                            x.add(identificator.get());
                        } else {
                            nameMap.get(RecordField1.getText()).add(identificator.get());
                        }
                        identificator.getAndAdd(RecordField1.getText().length() + RecordField2.getText().length() + RecordField3.getText().length() + RecordField4.getText().length() + 1);
                        myWriter.write(RecordField1.getText() + "," + RecordField2.getText() + "," + RecordField3.getText() + "," + RecordField4.getText() + '\n');
                        myWriter.flush();
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                RecordField1.setText("");
                RecordField2.setText("");
                RecordField3.setText("");
                RecordField4.setText("");
                updateTheSheetButton.doClick();
            });
        });


        deleteSheetButton.addActionListener(e -> {
            File file = new File(filepathing(openFilenameInput.getText()));
            file.delete();
            sheetArea.setText("");
        });


        clearSheetButton.addActionListener(e -> {
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(filepathing(openFilenameInput.getText()));
                writer.print("");
                updateTheSheetButton.doClick();
            } catch (FileNotFoundException ex) {
                sheetArea.setText("File is not found.");
            }
        });


        createTheSheetButton.addActionListener(e -> {
            String filename = filenameCreated.getText();
            File f = new File(filepathing(filename));
            try {
                f.createNewFile();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        backupCreateButton.addActionListener(e -> {
            File backup = new File(backupFilepathing(openFilenameInput.getText()));
            File original = new File(filepathing(openFilenameInput.getText()));
            try {
                Files.copy(original, backup);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });


        toXSLX.addActionListener(e -> {
            String filepathNoExtension = filepathingforXSLX(openFilenameInput.getText());
            xlsxConverter.converter(filepathNoExtension);
        });


        restoreBackup.addActionListener(e -> {
            File backup = new File(filepathing(openFilenameInput.getText() + "backup"));
            File original = new File(filepathing(openFilenameInput.getText()));
            try {
                Files.copy(backup, original);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(parent, "The Backup File hasn't been found.");
                throw new RuntimeException(ex);
            }
            openButton.doClick();
        });


        updateTheSheetButton.addActionListener(e -> {
            File file = new File(filepathing(openFilenameInput.getText()));
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                sheetArea.setText(null);
                String line;
                while ((line = br.readLine()) != null) {
                    sheetArea.append(line + "\n");
                }
            } catch (IOException ignored) {
            }
        });


        deleteButton.addActionListener(s -> {
            if (!Objects.equals(numberTextField.getText(), "")) {
                if (!numberWasFound(Long.parseLong(numberTextField.getText().trim()), phoneChains)) {
                    JOptionPane.showMessageDialog(parent, "Woah there! We don't have 'em boys with the mentioned phone number.");
                } else {
                    String path = filepathing(openFilenameInput.getText());
                    RandomAccessFile fileWriter = null;
                    try {
                        fileWriter = new RandomAccessFile(path, "rw");
                    } catch (FileNotFoundException ignored) {
                    }
                    int index = (int) phoneChains.get(Long.parseLong(numberTextField.getText().trim()));
                    String foundLineNumber;
                    try {
                        fileWriter.seek(index);
                        foundLineNumber = fileWriter.readLine();
                        String spaces = "                                                        ";
                        spaces = spaces.substring(0, foundLineNumber.length());
                        fileWriter.seek(index);
                        fileWriter.writeBytes(spaces);
                        updateTheSheetButton.doClick();
                    } catch (IOException ignored) {
                    }
                    ;
                }
            } else if (!Objects.equals(nameTextField.getText(), "")) {
                if (!nameWasFound((nameTextField.getText().trim()), (HashMap) nameMap)) {
                    JOptionPane.showMessageDialog(parent, "Woah there! We don't have 'em boys called like that.");
                } else
                {
                    String path = filepathing(openFilenameInput.getText());
                    RandomAccessFile fileWriter = null;
                    try {fileWriter = new RandomAccessFile(path, "rw");} catch (FileNotFoundException ignored) {}
                    for(int i = 0; i < nameMap.get(nameTextField.getText().trim()).size(); i++) {
                        int index = (int) nameMap.get(nameTextField.getText().trim()).get(i);
                        String foundLineNumber;
                        try {
                            fileWriter.seek(index);
                            foundLineNumber = fileWriter.readLine();
                            String spaces = "                                                        ";
                            spaces = spaces.substring(0, foundLineNumber.length());
                            fileWriter.seek(index);
                            fileWriter.writeBytes(spaces);
                            updateTheSheetButton.doClick();
                        } catch (IOException ignored) {};
                    }
                }
            }
        });


        editRecordButton.addActionListener(e -> {
            if (!numberWasFound(Long.parseLong(numberTextField.getText().trim()), phoneChains)) {
                JOptionPane.showMessageDialog(parent, "Woah there! We don't have 'em boys with the mentioned phone number.");
            } else {
                String path = filepathing(openFilenameInput.getText());
                RandomAccessFile fileWriter = null;
                try {
                    fileWriter = new RandomAccessFile(path, "rw");
                } catch (FileNotFoundException ignored) {
                }
                int index = (int) phoneChains.get(Long.parseLong(numberTextField.getText().trim()));
                try {
                    String editedField = RecordField1.getText() + "," + RecordField2.getText() + "," + RecordField3.getText() + "," + RecordField4.getText() + "\n";
                    fileWriter.seek(index);
                    String receivedField = fileWriter.readLine();
                    if (editedField.length() != receivedField.length() + 1) {
                        JOptionPane.showMessageDialog(parent, "Lengths of the edited line and the original fine must not differ. ");
                    } else {
                        fileWriter.seek(index);
                        fileWriter.writeBytes(editedField);
                    }
                    openButton.doClick();
                } catch (IOException ignored) {
                }
                ;
            }
        });


        searchByTheNumberButton.addActionListener(e -> {
            long i = 0;
            long j = 0;
            float result = 0;
            for (int t = 0; t<1000; t++) {
                long constSearch = System.currentTimeMillis();
                if (!numberWasFound(Long.parseLong(numberTextField.getText().trim()), phoneChains)) {
                    JOptionPane.showMessageDialog(parent, "Woah there! We don't have 'em boys with the mentioned phone number.");
                } else {
                    sheetArea.setText("");
                    String path = filepathing(openFilenameInput.getText());
                    RandomAccessFile fileWriter = null;
                    try {
                        fileWriter = new RandomAccessFile(path, "rw");
                    } catch (FileNotFoundException ignored) {
                    }
                    int index = (int) phoneChains.get(Long.parseLong(numberTextField.getText().trim()));
                    String foundLineNumber;
                    try {
                        fileWriter.seek(index);
                        foundLineNumber = fileWriter.readLine();/*
                    sheetArea.append(foundLineNumber);
                    sheetArea.append("\n\nThe record was found via the const search.\nВремя, затраченное на константный поиск: " + (System.currentTimeMillis() - constSearch) + "\n");*/
                        i = i + (System.currentTimeMillis() - constSearch);
                    } catch (IOException ignored) {
                    }
                    ;
                }

                long linearSearch = System.currentTimeMillis();
                if (!numberWasFound(Long.parseLong(numberTextField.getText().trim()), phoneChains)) {
                    JOptionPane.showMessageDialog(parent, "Woah there! We don't have 'em boys with the mentioned phone number.");
                } else {
                    String path = filepathing(openFilenameInput.getText());
                    String line;
                    RandomAccessFile fileWriter = null;
                    try {
                        fileWriter = new RandomAccessFile(path, "rw");
                    } catch (FileNotFoundException ignored) {
                    }
                    while (true) {
                        try {
                            line = fileWriter.readLine();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        if (Objects.equals(line.split(",")[2], numberTextField.getText().trim())) {
                            break;
                        }
                    }
                }
                j = j + (System.currentTimeMillis() - constSearch);
                result = (float) i /1000;
                sheetArea.append("Const search average speed (in ms): " + result);
                result = (float) j /1000;
                sheetArea.append("\n\nLinear search average speed (in ms): " + result);
            }

        });

        searchByTheNameButton.addActionListener(e -> {

            long k = 0;
            long l = 0;
            float result = 0;
            long constSearch = System.currentTimeMillis();
            for (int t = 0; t < 1000; t++) {
                if (!nameWasFound(nameTextField.getText().trim(), (HashMap) nameMap)) {
                    JOptionPane.showMessageDialog(parent, "Woah there! We don't have 'em people named like that.");
                } else {
                    sheetArea.setText("");
                    String path = filepathing(openFilenameInput.getText());
                    RandomAccessFile fileWriter = null;
                    try {
                        fileWriter = new RandomAccessFile(path, "rw");
                    } catch (FileNotFoundException ignored) {
                    }

                    for (int i = 0; i < nameMap.get(nameTextField.getText().trim()).size(); i++) {

                        int index = (int) nameMap.get(nameTextField.getText().trim()).get(i);
                        String foundLineNumber;
                        try {
                            fileWriter.seek(index);
                            foundLineNumber = fileWriter.readLine();
                            /*
                            sheetArea.append(foundLineNumber + "\n");*/
                        } catch (IOException ignored) {
                        }
                    }
                    k = (System.currentTimeMillis() - constSearch);
                    result = (float) k / 1000;
                    sheetArea.append("\nConst search has finished.\nAverage time of const search (in ms): " + result + "\n");
                }
            }
            System.out.println("\n\n");
            long constSearch2 = System.currentTimeMillis();
            for (int t = 0; t < 1000; t++) {
                if (!nameWasFound((nameTextField.getText().trim()), (HashMap) nameMap)) {
                    JOptionPane.showMessageDialog(parent, "Woah there! We don't have 'em boys with the mentioned phone number.");
                } else {
                    String path = filepathing(openFilenameInput.getText());
                    String line;
                    RandomAccessFile fileWriter = null;
                    try {
                        fileWriter = new RandomAccessFile(path, "rw");
                    } catch (FileNotFoundException ignored) {
                    }
                    int counter = 0;
                    while (counter<1000) {
                        counter++;
                        try {
                            line = fileWriter.readLine();
                            if (Objects.equals(line.split(",")[0], nameTextField.getText().trim())) {
                                continue; //
                            }
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
            l = (System.currentTimeMillis() - constSearch2);
            result = (float) l / 1000;
            sheetArea.append("\nConst search has finished.\nAverage time of linear search (in ms): " + result + "\n");
        });
    }
    public static void main(String[] args) {
        JFrame frame = new firstForm("Database Editor for Lab #1");
        frame.setVisible(true);

    }
    public static String filepathing(String addition) {
        return ("/home/ruine/Desktop/DATABASES/" + addition + ".csv");
    }

    public static String filepathingforXSLX(String addition) {
        return ("/home/ruine/Desktop/DATABASES/" + addition);
    }

    public static String backupFilepathing(String addition) {
        return ("/home/ruine/Desktop/DATABASES/" + addition + "backup.csv");
    }

    public static boolean numberWasFound(long object, Hashtable source) {
        return source.containsKey(object);
    }

    public static boolean nameWasFound(String object, HashMap source) {
        return source.containsKey(object);
    }
}
