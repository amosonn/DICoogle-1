/*
 * SearchTips.java
 *
 * Created on December 8, 2007, 12:53 AM
 */

package pt.ua.dicoogle.rGUI.client.windows;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pt.ua.dicoogle.*;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.DefaultListModel;
import javax.swing.JTextField;
import pt.ua.dicoogle.rGUI.client.UserRefs;
import pt.ua.dicoogle.rGUI.interfaces.controllers.ISearch;

/**
 *
 * @author  filipe
 */
public class SearchTips extends javax.swing.JFrame {
    private MainWindow aThis = MainWindow.getInstance();
    private JTextField query = null;
    private ISearch search;
    private Hashtable tags;
    
    public SearchTips(JTextField query)
    {
        try {
            this.query = query;
            initComponents();

            //this.aThis.setEnabled(false);
            this.search = UserRefs.getInstance().getSearch();

            tags = search.getTagList();

            Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/pt/ua/dicoogle/gfx/trayicon.gif"));
            this.setIconImage(image);
            
            updateList(null);

        } catch (RemoteException ex) {
            Logger.getLogger(SearchTips.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jListElements = new javax.swing.JList();
        jTextFieldFiltering = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jButtonInsertQuery = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Search Tips");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Search tips:");

        jLabel2.setText("With default search, you can use boolean expressions, with operators like AND, OR.");

        jLabel3.setText("You can provide specific fields value. For example:");

        jLabel4.setText("PatientName:\"John Doe\"");

        jLabel5.setText("The fields supported are:");

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jListElements.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListElementsMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jListElements);

        jTextFieldFiltering.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldFilteringKeyReleased(evt);
            }
        });

        jLabel6.setText("Insert the tag name:");

        jButtonInsertQuery.setText("Insert into Query");
        jButtonInsertQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonInsertQueryActionPerformed(evt);
            }
        });

        jButton3.setText("See Details");
        jButton3.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jLabel5))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldFiltering))
                                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(30, 30, 30)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jButtonInsertQuery, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(130, 130, 130)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(182, 182, 182)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jTextFieldFiltering, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButtonInsertQuery)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
            this.setVisible(false);
            aThis.setEnabled(true);
            aThis.toFront();
            this.dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
            this.setVisible(false);
            aThis.setEnabled(true);
            aThis.toFront();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonInsertQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonInsertQueryActionPerformed
        String tagName = (String) jListElements.getSelectedValue() ;
        
        if (tagName == null)
            return;

        if(query.getText().equals(""))
            query.setText(tagName +":");
        else
            query.setText(query.getText()+ " " + tagName +":");
    }//GEN-LAST:event_jButtonInsertQueryActionPerformed

    private void jListElementsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListElementsMouseClicked
          if (evt.getClickCount() == 2)
              jButtonInsertQuery.doClick();
    }//GEN-LAST:event_jListElementsMouseClicked

    private void jTextFieldFilteringKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldFilteringKeyReleased
        updateList(jTextFieldFiltering.getText());
    }//GEN-LAST:event_jTextFieldFilteringKeyReleased



    private void updateList(String prefix)
    {

        DefaultListModel listModel;
        listModel = new DefaultListModel();

        Set<String> set = getTagList(prefix);

        Iterator<String> it = (Iterator<String>) set.iterator();
        while(it.hasNext())
        {
            String tagName = it.next();
            listModel.addElement(tagName);
        }
        jListElements.setModel(listModel);

    }


    private Set<String> getTagList(String prefix) {
        Set<String> set = new TreeSet<String>();

        Set<String> sset = tags.keySet();

        for (String item : sset) {
            if (prefix != null && !prefix.equals("")) {
                if (item.toLowerCase().startsWith(prefix.toLowerCase())) {
                    set.add(item);
                }
            } else {
                set.add(item);
            }
        }

        return set;
    }


        
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButtonInsertQuery;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jListElements;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextFieldFiltering;
    // End of variables declaration//GEN-END:variables


}
