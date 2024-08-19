package org.ipchile.ui;

import org.ipchile.model.Task;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.regex.PatternSyntaxException;

public class TaskListUI extends JFrame {
    private ArrayList<Task> taskList;
    private JTable tableTask;
    private DefaultTableModel tableModel;
    private JButton completeButton, deleteButton, addButton, searchButton;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public TaskListUI() {
        initComponent();
        setupListener();
        addExampleContacts();
    }

    private void initComponent() {
        setTitle("Lista de Tareas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);

        // SE CREA EL MODELO DE LA TABLA
        tableModel = new DefaultTableModel(new Object[]{"TAREA", "ESTADO"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };
        tableTask = new JTable(tableModel);
        tableTask.setAutoResizeMode(0);
        JScrollPane scrollPane = new JScrollPane(tableTask);
        TableColumnModel columnModel = tableTask.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(320);
        columnModel.getColumn(1).setPreferredWidth(170);

        sorter = new TableRowSorter<>(tableModel);
        tableTask.setRowSorter(sorter);

        addButton = new JButton("Agregar Tarea");
        completeButton = new JButton("Marcar como Completada");
        deleteButton = new JButton("Eliminar");

        searchButton = new JButton("Buscar");
        searchField = new JTextField(20);

        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(addButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(deleteButton);

        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(searchPanel, BorderLayout.NORTH);
        contentPane.add(scrollPane, BorderLayout.CENTER);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(contentPane);
    }

    private void addExampleContacts() {
        taskList = new ArrayList<>();
        taskList.add(new Task("Curso de Python"));
        taskList.add(new Task("Crear App Gestor de tareas"));
        taskList.add(new Task("Informe de programación"));
    }

    private void setupListener() {
        addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                refreshTable();
            }
        });

        tableTask.getSelectionModel().addListSelectionListener(e -> updateButtonState());
        addButton.addActionListener(e -> addTask());
        completeButton.addActionListener(e -> taskCompleted());
        deleteButton.addActionListener(e -> removeTask());
        searchButton.addActionListener(e -> findTask());
        searchField.addActionListener(e -> findTask());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {findTask();}
            public void removeUpdate(DocumentEvent e) {findText();}
            public void changedUpdate(DocumentEvent e) {findText();}
        });
    }

    private void addTask() {
        String newTask = JOptionPane.showInputDialog(this, "Ingrese la nueva tarea:");
        if (newTask != null && !newTask.trim().isEmpty()) {
            if (taskExists(newTask)) {
                JOptionPane.showMessageDialog(this, "Esta tarea ya existe o es inválida", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                taskList.add(new Task(newTask));
                refreshTable();
            }
        }
    }

    private void taskCompleted() {
        int i = findTaskIndex(taskList);
        taskList.get(i).setCompleted(true);
        refreshTable();
    }

    private void removeTask() {
        int i = findTaskIndex(taskList);
        taskList.remove(i);
        refreshTable();
    }

    public boolean taskExists(String task) {
        return taskList.stream().anyMatch(t -> t.getDescription().equalsIgnoreCase(task));
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (taskList != null) {
            for (Task t : taskList) {
                tableModel.addRow(new Object[]{t.getDescription(), t.getStatus()});
            }
        }
        updateButtonState();
    }

    private void findTask() {
        String text = searchField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            } catch (java.util.regex.PatternSyntaxException e) {
                JOptionPane.showMessageDialog(this, "Patrón de búsqueda inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateButtonState() {
        int i = tableTask.getSelectedRow();
        if (i != -1) {
            boolean status = tableTask.getValueAt(i, 1).equals("Pendiente");
            if (status) {
                completeButton.setEnabled(true);
                deleteButton.setEnabled(false);
            } else {
                completeButton.setEnabled(false);
                deleteButton.setEnabled(true);
            }
        } else {
            completeButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
    }

    public void findText() {
        String text = searchField.getText();
        if (text.trim().isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            } catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(searchField, "Patrón de búsqueda inválido",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private int findTaskIndex(ArrayList<Task> taskList) {
        String text = tableTask.getValueAt(tableTask.getSelectedRow(), 0).toString();
        return taskList.indexOf(taskList.stream()
                .filter(task -> task.getDescription().equals(text))
                .findFirst()
                .orElse(null));
    }

    public void showTaskListUI() {
        setLocationRelativeTo(null);
        setVisible(true);
    }
}