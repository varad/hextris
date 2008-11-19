package net.hextris;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.table.TableModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

/**
 * This class enables adding new high score and showing local list of
 * the highest scores.
 * @author Radek Varbuchta
 */
public class HighScore extends JDialog {

    private class EntryComparator implements Comparator<Entry> {

        public int compare(Entry e1, Entry e2) {
            if (e1.lines < e2.lines) {
                return -1;
            } else if (e1.lines > e2.lines) {
                return 1;
            }
            return 0;
        }
    }

    private class Entry {

        private String name;
        private int lines;

        public Entry(String name, int score) {
            this.name = name;
            this.lines = score;
        }

        @Override
        public String toString() {
            return lines + " " + name;
        }
    }
    /**
     * A path to the file where the highest scores are stored.
     */
    private static final String path = System.getProperty("user.home") + "/.hextris/highscore.list";
    private static final int SCORES_CAPACITY = 10;
    private final List<Entry> scores = new ArrayList<Entry>(SCORES_CAPACITY);
    private static ResourceBundle rb = java.util.ResourceBundle.getBundle("net/hextris/language");

    /** Creates new form HighScore */
    public HighScore() {
        initComponents();
        load();
        updateTable();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButtonOk = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(rb.getString("High score"));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/hextris/language"); // NOI18N
        jButtonOk.setText(bundle.getString("OK")); // NOI18N
        jButtonOk.setFocusCycleRoot(true);
        jButtonOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOkActionPerformed(evt);
            }
        });

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                rb.getString("Position"), rb.getString("Name"), rb.getString("Lines")
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable.setEnabled(false);
        jTable.setRowSelectionAllowed(false);
        jTable.setShowHorizontalLines(false);
        jTable.setShowVerticalLines(false);
        jTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(jTable);
        jTable.getColumnModel().getColumn(0).setResizable(false);
        jTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        jTable.getColumnModel().getColumn(1).setResizable(false);
        jTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable.getColumnModel().getColumn(2).setResizable(false);
        jTable.getColumnModel().getColumn(2).setPreferredWidth(60);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addComponent(jButtonOk, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(82, 82, 82))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonOk)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOkActionPerformed
        setVisible(false);
}//GEN-LAST:event_jButtonOkActionPerformed

    /**
     * Loads the high score list from the permanent storage.
     */
    private void load() {
        File file = new File(path);
        LineIterator it = null;
        try {
            file.createNewFile();
            it = FileUtils.lineIterator(file);
            int i = 0;
            while (it.hasNext()) {
                String line = it.nextLine();
                String[] chunks = line.split(" ", 2);
                scores.add(new Entry(chunks[1], Integer.valueOf(chunks[0])));
            }
        } catch (IOException ex) {
            Logger.getLogger(HighScore.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            LineIterator.closeQuietly(it);
        }
    }

    /**
     * Saves the high score list to the permanent storage.
     */
    private void save() {
        Collections.sort(scores, Collections.reverseOrder(new EntryComparator()));
        File file = new File(path);
        try {
            FileUtils.writeLines(file, scores);
        } catch (IOException ex) {
            Logger.getLogger(HighScore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Updates data in the table.
     */
    private void updateTable() {
        Collections.sort(scores, Collections.reverseOrder(new EntryComparator()));
        TableModel model = jTable.getModel();
        int position = 1;
        for (Entry e : scores) {
            model.setValueAt(position + ".", position - 1, 0);
            model.setValueAt(e.name, position - 1, 1);
            model.setValueAt(e.lines, position - 1, 2);
            position++;
        }
        jTable.setBackground(getBackground());
        jTable.setGridColor(getBackground());
        jTable.setModel(model);
    }

    /**
     * Returns the index in the scores list of entry with minimal
     * number of lines.
     * @return index in the scores list
     */
    private int getMinEntryIdx() {
        int min = 0;
        for (int i = 1; i < scores.size(); i++) {
            if (scores.get(i).lines < scores.get(min).lines) {
                min = i;
            }

        }
        return min;
    }

    /**
     * Checks whether the given number of lines is high enough
     * to go tothe high score list.
     * @param number of lines
     * @return true=high enought, false otherwise
     */
    public boolean isHighScore(int lines) {
        int minEntryIdx = getMinEntryIdx();
        if (scores.size() == SCORES_CAPACITY && lines <= scores.get(minEntryIdx).lines) {
            return false;
        }
        return true;
    }

    /**
     * Adds the score to the high score list if it is high enough.
     * @param player's name
     * @param lines
     * @return true if added, false otherwise
     */
    public boolean addScore(String name, int lines) {
        if (!isHighScore(lines)) {
            return false;
        }

        if (scores.size() == SCORES_CAPACITY) {
            scores.remove(getMinEntryIdx());
        }

        scores.add(new Entry(name, lines));
        save();
        updateTable();

        return true;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonOk;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables
}
