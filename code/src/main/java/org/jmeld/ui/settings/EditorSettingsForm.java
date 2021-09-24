/*
 * EditorPreferencePanel.java
 *
 * Created on January 10, 2007, 6:31 PM
 */

package org.jmeld.ui.settings;

/**
 *
 * @author  kees
 */
public class EditorSettingsForm
    extends javax.swing.JPanel
{
  /** Creates new form EditorPreferencePanel */
  public EditorSettingsForm()
  {
    initComponents();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        defaultFontRadioButton = new javax.swing.JRadioButton();
        customFontRadioButton = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        tabSizeSpinner = new javax.swing.JSpinner();
        showLineNumbersCheckBox = new javax.swing.JCheckBox();
        detailHeader1 = new org.jmeld.ui.swing.DetailHeader();
        detailHeader2 = new org.jmeld.ui.swing.DetailHeader();
        detailHeader3 = new org.jmeld.ui.swing.DetailHeader();
        colorAddedButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        colorDeletedButton = new javax.swing.JButton();
        colorChangedButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        restoreOriginalColorsButton = new javax.swing.JButton();
        antialiasCheckBox = new javax.swing.JCheckBox();
        rightsideReadonlyCheckBox = new javax.swing.JCheckBox();
        fontChooserButton = new javax.swing.JButton();
        leftsideReadonlyCheckBox = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        lookAndFeelComboBox = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        detailHeader4 = new org.jmeld.ui.swing.DetailHeader();
        ignoreWhitespaceAtBeginCheckBox = new javax.swing.JCheckBox();
        specificEncodingComboBox = new javax.swing.JComboBox();
        specificEncodingRadioButton = new javax.swing.JRadioButton();
        detectEncodingRadioButton = new javax.swing.JRadioButton();
        defaultEncodingRadioButton = new javax.swing.JRadioButton();
        detailHeader5 = new org.jmeld.ui.swing.DetailHeader();
        ignoreCaseCheckBox = new javax.swing.JCheckBox();
        ignoreBlankLinesCheckBox = new javax.swing.JCheckBox();
        ignoreEOLCheckBox = new javax.swing.JCheckBox();
        ignoreWhitespaceAtEndCheckBox = new javax.swing.JCheckBox();
        ignoreWhitespaceInBetweenCheckBox = new javax.swing.JCheckBox();
        detailHeader6 = new org.jmeld.ui.swing.DetailHeader();
        jLabel6 = new javax.swing.JLabel();
        toolbarButtonTextEnabledCheckBox = new javax.swing.JCheckBox();
        toolbarButtonIconComboBox = new javax.swing.JComboBox();
        detailHeader7 = new org.jmeld.ui.swing.DetailHeader();
        showTreeChunksCheckBox = new javax.swing.JCheckBox();
        showLevensteinCheckBox = new javax.swing.JCheckBox();
        showTreeRawCheckBox = new javax.swing.JCheckBox();
        gradientLabel1 = new org.jmeld.ui.swing.GradientLabel();

        buttonGroup1.add(defaultFontRadioButton);
        defaultFontRadioButton.setText("Use default font");
        defaultFontRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        buttonGroup1.add(customFontRadioButton);
        customFontRadioButton.setText("Use custom font");
        customFontRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jLabel3.setText("Tab size");

        showLineNumbersCheckBox.setText("Show line numbers");
        showLineNumbersCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        detailHeader1.setText("Font");

        detailHeader2.setText("Miscellaneous");

        detailHeader3.setText("Colors");

        colorAddedButton.setText("C");
        colorAddedButton.setFocusable(false);

        jLabel1.setText("Lines have been added");

        colorDeletedButton.setText("C");
        colorDeletedButton.setFocusable(false);

        colorChangedButton.setText("C");
        colorChangedButton.setFocusable(false);

        jLabel2.setText("Lines have been deleted");

        jLabel4.setText("Lines have been changed");

        restoreOriginalColorsButton.setText("Restore original colors");

        antialiasCheckBox.setText("antialias on");
        antialiasCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        rightsideReadonlyCheckBox.setText("Rightside readonly");
        rightsideReadonlyCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        fontChooserButton.setText("fontName");

        leftsideReadonlyCheckBox.setText("Leftside readonly");
        leftsideReadonlyCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        jLabel5.setText("Look and feel");

        lookAndFeelComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(detailHeader1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(detailHeader3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(detailHeader2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(customFontRadioButton)
                                .add(6, 6, 6)
                                .add(fontChooserButton))
                            .add(defaultFontRadioButton)
                            .add(leftsideReadonlyCheckBox)
                            .add(antialiasCheckBox)
                            .add(rightsideReadonlyCheckBox)
                            .add(showLineNumbersCheckBox)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel5)
                                    .add(jLabel3))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(tabSizeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(lookAndFeelComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(colorAddedButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel1))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(colorDeletedButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel2))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(colorChangedButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel4))
                            .add(restoreOriginalColorsButton))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(detailHeader1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(defaultFontRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(customFontRadioButton)
                    .add(fontChooserButton))
                .add(18, 18, 18)
                .add(detailHeader2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(tabSizeSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(lookAndFeelComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(showLineNumbersCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(rightsideReadonlyCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(leftsideReadonlyCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(antialiasCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailHeader3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(colorAddedButton)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(colorDeletedButton)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(colorChangedButton)
                    .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 13, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(restoreOriginalColorsButton)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        detailHeader4.setText("Ignore");

        ignoreWhitespaceAtBeginCheckBox.setText("Ignore whitespace at the begin of a line");
        ignoreWhitespaceAtBeginCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        specificEncodingComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        specificEncodingRadioButton.setText("Use encoding");

        detectEncodingRadioButton.setText("Try to detect encoding");

        defaultEncodingRadioButton.setText("Default encoding of this computer");

        detailHeader5.setText("File encoding");

        ignoreCaseCheckBox.setText("Ignore case");
        ignoreCaseCheckBox.setBorder(null);

        ignoreBlankLinesCheckBox.setText("Ignore blank lines");
        ignoreBlankLinesCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        ignoreEOLCheckBox.setText("Ignore EOL (End of line markers)");
        ignoreEOLCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        ignoreWhitespaceAtEndCheckBox.setText("Ignore whitespace at the end of a line");
        ignoreWhitespaceAtEndCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        ignoreWhitespaceInBetweenCheckBox.setText("Ignore whitespace between begin and end of a line");
        ignoreWhitespaceInBetweenCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        detailHeader6.setText("Toolbar appearance");

        jLabel6.setText("Icon in button");

        toolbarButtonTextEnabledCheckBox.setText("Text in button");
        toolbarButtonTextEnabledCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        toolbarButtonIconComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        detailHeader7.setText("Layout");

        showTreeChunksCheckBox.setText("Show Tree chunks");
        showTreeChunksCheckBox.setBorder(null);

        showLevensteinCheckBox.setText("Show Levensthein chunk editor");
        showLevensteinCheckBox.setToolTipText("");
        showLevensteinCheckBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        showTreeRawCheckBox.setText("Show Tree Nodes Raw");
        showTreeRawCheckBox.setBorder(null);
        showTreeRawCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTreeRawCheckBoxActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(detailHeader4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(detailHeader5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(detailHeader6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(detectEncodingRadioButton)
                                    .add(jPanel2Layout.createSequentialGroup()
                                        .add(specificEncodingRadioButton)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(specificEncodingComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(defaultEncodingRadioButton)
                                    .add(toolbarButtonTextEnabledCheckBox)
                                    .add(jPanel2Layout.createSequentialGroup()
                                        .add(jLabel6)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(toolbarButtonIconComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                            .add(detailHeader7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(0, 0, Short.MAX_VALUE))
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(showLevensteinCheckBox)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(showTreeChunksCheckBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(showTreeRawCheckBox))
                            .add(ignoreEOLCheckBox)
                            .add(ignoreWhitespaceAtBeginCheckBox)
                            .add(ignoreBlankLinesCheckBox)
                            .add(ignoreCaseCheckBox)
                            .add(ignoreWhitespaceAtEndCheckBox)
                            .add(ignoreWhitespaceInBetweenCheckBox, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(detailHeader4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ignoreWhitespaceAtBeginCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ignoreWhitespaceInBetweenCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ignoreWhitespaceAtEndCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ignoreEOLCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ignoreBlankLinesCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ignoreCaseCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailHeader7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(showLevensteinCheckBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(showTreeChunksCheckBox)
                    .add(showTreeRawCheckBox))
                .add(14, 14, 14)
                .add(detailHeader5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(defaultEncodingRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detectEncodingRadioButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(specificEncodingRadioButton)
                    .add(specificEncodingComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(detailHeader6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(toolbarButtonIconComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(toolbarButtonTextEnabledCheckBox)
                .addContainerGap())
        );

        gradientLabel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        gradientLabel1.setText("Editor settings");
        gradientLabel1.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(18, Short.MAX_VALUE))
            .add(gradientLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 653, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(gradientLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void showTreeRawCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTreeRawCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showTreeRawCheckBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JCheckBox antialiasCheckBox;
    protected javax.swing.ButtonGroup buttonGroup1;
    protected javax.swing.JButton colorAddedButton;
    protected javax.swing.JButton colorChangedButton;
    protected javax.swing.JButton colorDeletedButton;
    protected javax.swing.JRadioButton customFontRadioButton;
    protected javax.swing.JRadioButton defaultEncodingRadioButton;
    protected javax.swing.JRadioButton defaultFontRadioButton;
    protected org.jmeld.ui.swing.DetailHeader detailHeader1;
    protected org.jmeld.ui.swing.DetailHeader detailHeader2;
    protected org.jmeld.ui.swing.DetailHeader detailHeader3;
    protected org.jmeld.ui.swing.DetailHeader detailHeader4;
    protected org.jmeld.ui.swing.DetailHeader detailHeader5;
    protected org.jmeld.ui.swing.DetailHeader detailHeader6;
    protected org.jmeld.ui.swing.DetailHeader detailHeader7;
    protected javax.swing.JRadioButton detectEncodingRadioButton;
    protected javax.swing.JButton fontChooserButton;
    protected org.jmeld.ui.swing.GradientLabel gradientLabel1;
    protected javax.swing.JCheckBox ignoreBlankLinesCheckBox;
    protected javax.swing.JCheckBox ignoreCaseCheckBox;
    protected javax.swing.JCheckBox ignoreEOLCheckBox;
    protected javax.swing.JCheckBox ignoreWhitespaceAtBeginCheckBox;
    protected javax.swing.JCheckBox ignoreWhitespaceAtEndCheckBox;
    protected javax.swing.JCheckBox ignoreWhitespaceInBetweenCheckBox;
    protected javax.swing.JLabel jLabel1;
    protected javax.swing.JLabel jLabel2;
    protected javax.swing.JLabel jLabel3;
    protected javax.swing.JLabel jLabel4;
    protected javax.swing.JLabel jLabel5;
    protected javax.swing.JLabel jLabel6;
    protected javax.swing.JPanel jPanel1;
    protected javax.swing.JPanel jPanel2;
    protected javax.swing.JCheckBox leftsideReadonlyCheckBox;
    protected javax.swing.JComboBox lookAndFeelComboBox;
    protected javax.swing.JButton restoreOriginalColorsButton;
    protected javax.swing.JCheckBox rightsideReadonlyCheckBox;
    protected javax.swing.JCheckBox showLevensteinCheckBox;
    protected javax.swing.JCheckBox showLineNumbersCheckBox;
    protected javax.swing.JCheckBox showTreeChunksCheckBox;
    protected javax.swing.JCheckBox showTreeRawCheckBox;
    protected javax.swing.JComboBox specificEncodingComboBox;
    protected javax.swing.JRadioButton specificEncodingRadioButton;
    protected javax.swing.JSpinner tabSizeSpinner;
    protected javax.swing.JComboBox toolbarButtonIconComboBox;
    protected javax.swing.JCheckBox toolbarButtonTextEnabledCheckBox;
    // End of variables declaration//GEN-END:variables

}
