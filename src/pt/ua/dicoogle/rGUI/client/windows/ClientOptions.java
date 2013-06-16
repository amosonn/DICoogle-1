/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ClientOptions.java
 *
 * Created on 4/Mai/2010, 13:03:33
 */
package pt.ua.dicoogle.rGUI.client.windows;

import java.awt.Image;
import java.awt.Toolkit;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import pt.ua.dicoogle.Main;
import pt.ua.dicoogle.core.ClientSettings;
import pt.ua.dicoogle.core.XMLClientSupport;
import pt.ua.dicoogle.rGUI.server.users.HashService;

/**
 *
 * @author samuelcampos
 */
public class ClientOptions extends javax.swing.JFrame {
    private ClientSettings settings;

    private static ClientOptions instance = null;
    private static Semaphore sem = new Semaphore(1, true);

    public static synchronized ClientOptions getInstance()
    {
        try
        {
            sem.acquire();
            if (instance == null)
            {
                instance = new ClientOptions();
            }
            sem.release();

        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(ServerOptions.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instance;
    }

    /** Creates new form ClientOptions */
    private ClientOptions() {
        initComponents();

        Image image = Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/pt/ua/dicoogle/gfx/trayicon.gif"));
        this.setIconImage(image);

        settings = ClientSettings.getInstance();

        jLabelExternalViewer.setText(settings.getExtV());
        Host.setText(settings.getDefaultServerHost());
        Port.setText(String.valueOf(settings.getDefaultServerPort()));
        Username.setText(settings.getDefaultUserName());
        jLabelTempFilesDir.setText(settings.getTempFilesDir());

        if(settings.getDefaultPassword() != null && !settings.getDefaultPassword().equals(""))
            Password.setText("lixo_lixo_lixo");

        jCheckBoxAutoConnect.setSelected(settings.getAutoConnect());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabelExternalViewer = new javax.swing.JLabel();
        jButtoViewerPath = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabelTempFilesDir = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jButtonTempDir = new javax.swing.JButton();
        jButtonRemoveEV = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        Port = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        Host = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        Username = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        Password = new javax.swing.JPasswordField();
        jCheckBoxAutoConnect = new javax.swing.JCheckBox();
        jButtonWrite = new javax.swing.JButton();
        jButtonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Client Options");
        setMinimumSize(new java.awt.Dimension(453, 311));

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(0, 0));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pt/ua/dicoogle/gfx/aboutico.gif"))); // NOI18N
        jLabel34.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel34MouseClicked(evt);
            }
        });

        jLabelExternalViewer.setText("<External Viewer Path>");

        jButtoViewerPath.setText("Change External Viewer Path");
        jButtoViewerPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtoViewerPathActionPerformed(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel23.setText("External Viewer Path:");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel3.setText("Temporary Files Directory: ");

        jLabelTempFilesDir.setText("<Temporary Files Directory>");

        jLabel35.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pt/ua/dicoogle/gfx/aboutico.gif"))); // NOI18N
        jLabel35.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel35MouseClicked(evt);
            }
        });

        jButtonTempDir.setText("Change External Viewer Path");
        jButtonTempDir.setActionCommand("Change Temporary Dir");
        jButtonTempDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTempDirActionPerformed(evt);
            }
        });

        jButtonRemoveEV.setText("Remove E.V.");
        jButtonRemoveEV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoveEVActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel23)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButtoViewerPath)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButtonRemoveEV))
                    .add(jLabelExternalViewer, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .add(jLabel3)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabelTempFilesDir, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(jLabel35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(18, 18, 18)
                        .add(jButtonTempDir)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel23)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelExternalViewer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                        .add(jButtoViewerPath)
                        .add(jButtonRemoveEV))
                    .add(jLabel34, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabelTempFilesDir, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButtonTempDir)
                    .add(jLabel35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Directory Settings", jPanel1);

        jLabel4.setText("Port:");

        Port.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                PortFocusLost(evt);
            }
        });

        jLabel1.setText("Host:");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13));
        jLabel2.setText("Default GUI Server:");

        jLabel5.setText("Username:");

        jLabel6.setText("Password:");

        Password.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PasswordFocusGained(evt);
            }
        });

        jCheckBoxAutoConnect.setText("AutoConnect at Startup");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(jLabel4)
                            .add(jLabel2)
                            .add(jLabel5)
                            .add(jLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(Password, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                            .add(Username, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                            .add(Port, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                            .add(Host, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE))
                        .add(163, 163, 163))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jCheckBoxAutoConnect)
                        .addContainerGap(272, Short.MAX_VALUE))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(Host, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(Port, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(Username, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(Password, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jCheckBoxAutoConnect)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Default GUI Server", jPanel2);

        jButtonWrite.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pt/ua/dicoogle/gfx/floopy-icon.png"))); // NOI18N
        jButtonWrite.setText("Save Configurations");
        jButtonWrite.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonWriteActionPerformed(evt);
            }
        });

        jButtonClose.setText("Close");
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 492, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(jButtonWrite)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButtonClose)
                        .add(18, 18, 18))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(23, 23, 23)
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButtonWrite)
                    .add(jButtonClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 38, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonWriteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonWriteActionPerformed
        saveSettings();
}//GEN-LAST:event_jButtonWriteActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        this.dispatchEvent(new java.awt.event.WindowEvent(this, java.awt.Event.WINDOW_DESTROY));
}//GEN-LAST:event_jButtonCloseActionPerformed

    private void jLabel34MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel34MouseClicked
        JOptionPane.showMessageDialog(this, "The external viewer path sets the aplication that Dicoogle Client will use to open DICOM files", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_jLabel34MouseClicked

    private void jButtoViewerPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtoViewerPathActionPerformed
        JFileChooser chooser = new JFileChooser(); 
         chooser.setCurrentDirectory(new java.io.File("."));
         chooser.setDialogTitle("External Viewer Path");
         chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
         chooser.setAcceptAllFileFilterUsed(false);

         if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
         {
             jLabelExternalViewer.setText(chooser.getSelectedFile().toString());
             jLabelExternalViewer.setToolTipText(jLabelExternalViewer.getText());
        }
}//GEN-LAST:event_jButtoViewerPathActionPerformed

    private void PortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PortFocusLost
        try {
            Integer.valueOf(Port.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Only numbers are accepted!",
                    "Invalid Input", JOptionPane.ERROR_MESSAGE);

            Port.grabFocus();
        }
    }//GEN-LAST:event_PortFocusLost

    private void jLabel35MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel35MouseClicked
        JOptionPane.showMessageDialog(this, "The temporary files directory sets the directory that Dicoogle Client will use to save temporary DICOM files", "Did you know?", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jLabel35MouseClicked

    private void jButtonTempDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTempDirActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Temporary Files Directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false);

        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
             jLabelTempFilesDir.setText(chooser.getSelectedFile().toString());
             jLabelTempFilesDir.setToolTipText(jLabelTempFilesDir.getText());
        }
    }//GEN-LAST:event_jButtonTempDirActionPerformed

    private void PasswordFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_PasswordFocusGained
        String pass = new String(Password.getPassword());

        if(pass.equals("lixo_lixo_lixo"))
            Password.setText("");
    }//GEN-LAST:event_PasswordFocusGained

    private void jButtonRemoveEVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoveEVActionPerformed
        jLabelExternalViewer.setText("");
    }//GEN-LAST:event_jButtonRemoveEVActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField Host;
    private javax.swing.JPasswordField Password;
    private javax.swing.JTextField Port;
    private javax.swing.JTextField Username;
    private javax.swing.JButton jButtoViewerPath;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonRemoveEV;
    private javax.swing.JButton jButtonTempDir;
    private javax.swing.JButton jButtonWrite;
    private javax.swing.JCheckBox jCheckBoxAutoConnect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelExternalViewer;
    private javax.swing.JLabel jLabelTempFilesDir;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables

    public boolean unsavedSettings(){
        if(!jLabelExternalViewer.getText().equals(settings.getExtV())
                || !Host.getText().equals(settings.getDefaultServerHost())
                || Integer.valueOf(Port.getText()) != settings.getDefaultServerPort()
                || !Username.getText().equals(settings.getDefaultUserName())
                || !jLabelTempFilesDir.getText().equals(settings.getTempFilesDir())
                || jCheckBoxAutoConnect.isSelected() != settings.getAutoConnect())
            return true;
        
        String pass = new String(Password.getPassword());

        if(!pass.equals("lixo_lixo_lixo") && !HashService.getSHA1Hash(pass).equals(settings.getDefaultPassword()))
            return true;

        return false;
    }

    public void saveSettings(){
        // save settings to ClientSettings
        settings.setExtV(jLabelExternalViewer.getText());
        settings.setDefaultServerHost(Host.getText());
        settings.setDefaultServerPort(Integer.valueOf(Port.getText()));
        settings.setDefaultUserName(Username.getText());
        settings.setTempFilesDir(jLabelTempFilesDir.getText());

        String pass = new String(Password.getPassword());
        
        if(!pass.equals("lixo_lixo_lixo"))
            settings.setDefaultPassword(HashService.getSHA1Hash(pass));

        settings.setAutoConnect(jCheckBoxAutoConnect.isSelected());

        // save settings to xml
        XMLClientSupport xmlClient = new XMLClientSupport();
        xmlClient.printXML();
    }
}
