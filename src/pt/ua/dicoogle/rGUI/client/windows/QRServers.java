/*
 * QRSettings.java
 *
 * Created on 15/Dez/2009, 11:34:31
 */

package pt.ua.dicoogle.rGUI.client.windows;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.awt.Image;
import java.awt.Toolkit;

import java.util.Hashtable;
import java.util.concurrent.Semaphore;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import pt.ua.dicoogle.rGUI.interfaces.controllers.IQRServers;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.core.MoveDestination;
import pt.ua.dicoogle.rGUI.client.AdminRefs;
import pt.ua.dicoogle.rGUI.client.UIHelper.AllowBlankMaskFormatter;

/**
 * Query/Retrieve Storage Servers configuration
 *
 * @author Samuel Campos <samuelcampos@ua.pt>
 */
public class QRServers extends javax.swing.JFrame {

    private static Semaphore sem = new Semaphore(1, true);
    private static QRServers instance = null;
    private static IQRServers qrserv;

    private Hashtable<String, MoveDestination> listMove = new Hashtable<String, MoveDestination>();


    public static synchronized QRServers getInstance() {
        try {
            sem.acquire();
            if (instance == null) {
                instance = new QRServers();
            }
            sem.release();
        } catch (InterruptedException ex) {
//            Logger.getLogger(MainWindow.class.getName()).log(Level.FATAL, null, ex);
        }
        return instance;
    }


    /** Creates new form QRSettings */
    private QRServers() {
        initComponents();

        Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/pt/ua/dicoogle/gfx/trayicon.gif"));
        this.setIconImage(image);

        QRServers.qrserv = AdminRefs.getInstance().getQRservers();

        loadMove();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel5 = new javax.swing.JPanel();
        jButtonQRAddEntry = new javax.swing.JButton();
        jButtonQRRemoveEntry = new javax.swing.JButton();
        jScrollPane5 = new javax.swing.JScrollPane();
        DefaultListModel modelQRMoveDest = new DefaultListModel();
        jListQRMoveDest = new JList(modelQRMoveDest);
        jLabel1 = new javax.swing.JLabel();
        jTextFieldQRAETitle = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        AllowBlankMaskFormatter maskSub = null;
        try
        {
            maskSub = new AllowBlankMaskFormatter("###.###.###.###");
            maskSub.setPlaceholderCharacter(' ');
            maskSub.setAllowBlankField(true);
            //maskSub.setValidCharacters("0123456789");
            //maskSub.setInvalidCharacters("AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz");
        }
        catch (java.text.ParseException e)
        {
            String pass = null ;
        }
        //jTextFieldQRIP = new JFormattedTextField(maskSub);
        jTextFieldQRIP = new JTextField();
        jTextFieldQRPort = new javax.swing.JTextField();
        jButtonWrite = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Query/Retrieve Storage Servers");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Storage Servers Destinations"));

        jButtonQRAddEntry.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pt/ua/dicoogle/gfx/add.png"))); // NOI18N
        jButtonQRAddEntry.setText("Add Entry");
        jButtonQRAddEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQRAddEntryActionPerformed(evt);
            }
        });

        jButtonQRRemoveEntry.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pt/ua/dicoogle/gfx/remove.png"))); // NOI18N
        jButtonQRRemoveEntry.setText("Remove");
        jButtonQRRemoveEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonQRRemoveEntryActionPerformed(evt);
            }
        });

        jListQRMoveDest.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListQRMoveDestMouseClicked(evt);
            }
        });
        jListQRMoveDest.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListQRMoveDestValueChanged(evt);
            }
        });
        jScrollPane5.setViewportView(jListQRMoveDest);

        jLabel1.setText("AETitle:");

        jLabel2.setText("IP:");

        jLabel3.setText("Port:");

        jButtonWrite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pt/ua/dicoogle/gfx/floopy-icon.png"))); // NOI18N
        jButtonWrite.setText("Save Configurations");
        jButtonWrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWriteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldQRAETitle, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                                    .addComponent(jTextFieldQRIP, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(jButtonQRRemoveEntry, 0, 0, Short.MAX_VALUE)
                                    .addComponent(jButtonQRAddEntry, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addComponent(jTextFieldQRPort, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButtonWrite, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(20, 20, 20))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel1)
                                .addComponent(jTextFieldQRAETitle, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jButtonQRAddEntry)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jButtonQRRemoveEntry)
                                    .addComponent(jTextFieldQRIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jTextFieldQRPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonWrite))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    public void loadMove()
    {
        try {

            DefaultListModel m = (DefaultListModel) jListQRMoveDest.getModel();
            m.clear();

            for (MoveDestination s : qrserv.getMoves()) {
                //DebugManager.getInstance().debug(s.getAETitle());

                int pos = jListQRMoveDest.getModel().getSize();
                m.add(pos, s.getAETitle());
                listMove.put(s.getAETitle(), s);
            }
            
        } catch (RemoteException ex) {
            Logger.getLogger(QRServers.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void jButtonQRAddEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQRAddEntryActionPerformed
        if (jTextFieldQRAETitle.getText().equals("") || jTextFieldQRIP.getText().equals("") || jTextFieldQRPort.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Please, fill all fields!",
                    "Missing fields", JOptionPane.WARNING_MESSAGE);

        } else {
            String aeTitleDest = jTextFieldQRAETitle.getText();
            if (!listMove.containsKey(aeTitleDest)) {
                DefaultListModel m = (DefaultListModel) jListQRMoveDest.getModel();
                m.addElement(aeTitleDest);

                MoveDestination tmpMove = new MoveDestination(aeTitleDest,
                        jTextFieldQRIP.getText(), Integer.parseInt(jTextFieldQRPort.getText()));
                
                listMove.put(aeTitleDest, tmpMove);
                
                try {
                    qrserv.AddEntry(tmpMove);
                    
                } catch (RemoteException ex) {
                    Logger.getLogger(QRServers.class.getName()).log(Level.SEVERE, null, ex);
                }

            
            } else {
                JOptionPane.showMessageDialog(this, "The AETtitle already exists in the list",
                        "AETitle", JOptionPane.WARNING_MESSAGE);
            }

        }
}//GEN-LAST:event_jButtonQRAddEntryActionPerformed

    private void jButtonQRRemoveEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonQRRemoveEntryActionPerformed


        // Remove Destination

        if (jListQRMoveDest.getSelectedIndex() == -1 ) {
            JOptionPane.showMessageDialog(this, "Please select an AETitle in the list",
                    "No selected item", JOptionPane.WARNING_MESSAGE);
        } else {
            String aeTitleSelected = null;
            aeTitleSelected = (String) jListQRMoveDest.getSelectedValue();
            DefaultListModel m = (DefaultListModel) jListQRMoveDest.getModel();

            m.removeElement(jListQRMoveDest.getSelectedValue());
            MoveDestination tmp = listMove.get(aeTitleSelected);
            listMove.remove(aeTitleSelected);
            
            try {
                qrserv.RemoveEntry(tmp);
            } catch (RemoteException ex) {
                Logger.getLogger(QRServers.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
}//GEN-LAST:event_jButtonQRRemoveEntryActionPerformed

    private void jListQRMoveDestValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListQRMoveDestValueChanged
        // Show ip address and port number ;

        if (jListQRMoveDest.getSelectedIndex() == -1 )
            return ;
        String sel = (String) jListQRMoveDest.getSelectedValue();
        MoveDestination tmp = listMove.get(sel);

        jTextFieldQRAETitle.setText(tmp.getAETitle());
        jTextFieldQRIP.setText(tmp.getIpAddrs());
        jTextFieldQRPort.setText(String.valueOf(tmp.getPort()));
    }//GEN-LAST:event_jListQRMoveDestValueChanged

    private void jListQRMoveDestMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListQRMoveDestMouseClicked

}//GEN-LAST:event_jListQRMoveDestMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        Services services = Services.getInstance();

        //if(services != null && services.isVisible()){
            services.toFront();
            services.setEnabled(true);
        /*
        }
        else{
            ServerOptions serverOptions = ServerOptions.getInstance();

            serverOptions.toFront();
            serverOptions.setEnabled(true);
        }
         *
         */

        this.dispose();
    }//GEN-LAST:event_formWindowClosing

    private void jButtonWriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWriteActionPerformed
        AdminRefs.getInstance().saveSettings();
}//GEN-LAST:event_jButtonWriteActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonQRAddEntry;
    private javax.swing.JButton jButtonQRRemoveEntry;
    private javax.swing.JButton jButtonWrite;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JList jListQRMoveDest;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JTextField jTextFieldQRAETitle;
    private javax.swing.JTextField jTextFieldQRIP;
    private javax.swing.JTextField jTextFieldQRPort;
    // End of variables declaration//GEN-END:variables

}
