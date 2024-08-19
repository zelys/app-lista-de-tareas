package org.ipchile;


import org.ipchile.ui.TaskListUI;

import javax.swing.*;

public class AppTaskList {
    public static void main(String[] args) {
        TaskListUI tl = new TaskListUI();
        SwingUtilities.invokeLater(tl::showTaskListUI);
    }
}