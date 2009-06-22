/*
 * EditorPane.java
 *
 * Created on 15 mars 2008, 13:30
 */
package lh.worldclock.editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.xml.bind.JAXBException;
import lh.worldclock.config.schema.City;
import lh.worldclock.config.schema.HorizontalDirection;
import lh.worldclock.config.schema.Plane;
import lh.worldclock.config.schema.VerticalDirection;

/**
 *
 * @author  Ludovic
 */
public class EditorPane extends javax.swing.JPanel
{

  private interface RowRemover
  {
    void remove(int row);
  }


  private static class ButtonColumn extends AbstractCellEditor
    implements TableCellRenderer, TableCellEditor, ActionListener
  {

    private JTable table;
    private JButton renderButton;
    private JButton editButton;
    private Object object;
    private RowRemover rowRemover;

    private static void createAndSetup(JTable table, int column, RowRemover rowRemover)
    {
      ButtonColumn buttonColumn = new ButtonColumn(table, rowRemover);
      TableColumnModel columnModel = table.getColumnModel();
      TableColumn tableColumn = columnModel.getColumn(column);
      tableColumn.setPreferredWidth(buttonColumn.renderButton.getPreferredSize().width);
      tableColumn.setCellRenderer(buttonColumn);
      tableColumn.setCellEditor(buttonColumn);
    }

    // adapted from http://forums.sun.com/thread.jspa?threadID=680674
    private ButtonColumn(JTable table, RowRemover rowRemover)
    {
      super();
      this.table = table;

      this.rowRemover = rowRemover;

      renderButton = new JButton();
      renderButton.setText("Delete");

      editButton = new JButton();
      editButton.setFocusPainted(false);
      editButton.addActionListener(this);
      editButton.setText("Delete");

    }

    @Override
    public Component getTableCellRendererComponent(
      JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      Color background = UIManager.getColor("Button.background");

      if (hasFocus)
      {
        renderButton.setForeground(table.getForeground());
        renderButton.setBackground(background);
      }
      else if (isSelected)
      {
        renderButton.setForeground(table.getSelectionForeground());
        renderButton.setBackground(table.getSelectionBackground());
      }
      else
      {
        renderButton.setForeground(table.getForeground());
        renderButton.setBackground(background);
      }

      return renderButton;
    }

    @Override
    public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column)
    {
      object = value;
      return editButton;
    }

    @Override
    public Object getCellEditorValue()
    {
      return object;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      fireEditingStopped();
//      System.out.println(e.getActionCommand() + " : " + table.getSelectedRow());
      rowRemover.remove(table.getSelectedRow());
    }
  }

  private class ImagePathRenderer extends DefaultTableCellRenderer
  {
    private String toURLString(String path)
    {
      if (file == null) return "";
      if (path == null || path.equals("")) return "";
      
      File f = new File(file.getParentFile(), path);
      try
      {
        String ret = f.toURI().toURL().toString();
//        System.out.println("ret = " + ret);
        return ret;
      }
      catch (MalformedURLException ex)
      {
        Logger.getLogger(EditorPane.class.getName()).log(Level.SEVERE, null, ex);
        ex.printStackTrace();
      }
      return "err";
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if (c instanceof JLabel)
      {
        JLabel l = (JLabel)c;
        l.setToolTipText("<html><body><img src = '" + toURLString((String)value) + "' width=600 height=250></body></html>");
      }

      return c;
    }

  }

  private static class CityNameEditor extends AbstractCellEditor
    implements TableCellEditor, ActionListener
  {

    private JTextField tfName;
    private JButton btnFind;
    private JPanel pane;
    private City city;
    private FindCityDialog dialog = null;

    private CityNameEditor()
    {
      super();

      pane = new JPanel();
      pane.setLayout(new BorderLayout());

      tfName = new JTextField();
      tfName.setBorder(null);
      pane.add(tfName, BorderLayout.CENTER);

      btnFind = new JButton();
      btnFind.addActionListener(this);
      btnFind.setText("...");
      pane.add(btnFind, BorderLayout.EAST);

      pane.validate();

    }

    @Override
    public Component getTableCellEditorComponent(
      JTable table, Object value, boolean isSelected, int row, int column)
    {
      city = new City();
      if (value != null)
      {
        City v = (City)value;
        city.setName(v.getName());
        city.setLat(v.getLat());
        city.setLong(v.getLong());
        city.setTimezone(v.getTimezone());
      }
      tfName.setText(city.getName());
      return pane;
    }

    @Override
    public Object getCellEditorValue()
    {
      return city;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      if (dialog == null)
      {
        dialog = new FindCityDialog(EditorApp.getApplication().getMainFrame(), true);
      }
      else
      {
        dialog.resetWith(city);
      }

      EditorApp.getApplication().show(dialog);

      if (dialog.isOk())
      {
        City dlgCity = dialog.getCity();
        if (dlgCity != null)
        {
          city.setName(dlgCity.getName());
          city.setLat(dlgCity.getLat());
          city.setLong(dlgCity.getLong());
          city.setTimezone(dlgCity.getTimezone());
          tfName.setText(city.getName());
          stopCellEditing();
        }
      }
    }
  }

  private class CityNameRenderer extends DefaultTableCellRenderer
  {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
      Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

      if (c instanceof JLabel)
      {
        JLabel l = (JLabel)c;
        if (value instanceof City)
        {
          City city = (City)value;
          String name = city.getName();
          l.setText(name);
          l.setToolTipText(name);
        }
      }

      return c;
    }

  }


  private class CitiesTableModel extends AbstractTableModel implements RowRemover
  {
    private String[] columns = new String[] {"Name", "Latitude", "Longitude", "Timezone", "" /*delete*/};

    @Override
    public int getRowCount()
    {
      return mgr.getCities().size() + 1;
    }

    @Override
    public int getColumnCount()
    {
      return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
      if (rowIndex == mgr.getCities().size())
      {
        if (columnIndex == 0)
        {
          return null;
        }
        return "";
      }

      City city = mgr.getCities().get(rowIndex);
      switch (columnIndex)
      {
        //{"Name", "Latitude", "Longitude", "Timezone", "" /*delete*/};
        case 0: // name
          return city;
        case 1: // latitude
          return city.getLat();
        case 2: // longitude
          return city.getLong();
        case 3: // timezone
          return city.getTimezone();
        case 4:
          return city;
      }
      return null;
    }

    @Override
    public String getColumnName(int column)
    {
      return columns[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
      return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
      City city;

      if (rowIndex == mgr.getCities().size())
      {
        city = mgr.createCity();
        mgr.getCities().add(city);
      }
      else
      {
        city = mgr.getCities().get(rowIndex);
      }
      
      switch (columnIndex)
      {
        //{"Name", "Latitude", "Longitude", "Timezone"};
        case 0: // name
          {
            City nvCity = (City)aValue;
            city.setName(nvCity.getName());
            city.setLat(nvCity.getLat());
            city.setLong(nvCity.getLong());
            city.setTimezone(nvCity.getTimezone());
          }
          break;
        case 1: // latitude
          city.setLat(new BigDecimal((String)aValue));
          break;
        case 2: // longitude
          city.setLong(new BigDecimal((String)aValue));
          break;
        case 3: // timezone
          city.setTimezone((String)aValue);
          break;
        case 4: // delete
          // do nothing, deletion occurs in remove(int)
          break;
      }
    }

    @Override
    public void remove(int row)
    {
      java.util.List<?> lst = mgr.getCities();
      if (row == lst.size())
      {
        return;
      }
      lst.remove(row);
      fireTableRowsDeleted(row, row);
    }

}

  private class PlanesTableModel extends AbstractTableModel implements RowRemover
  {
    private String[] columns = new String[] {"Name", "Horizontal", "Vertical", "Image Path", "" /* delete */};

    @Override
    public int getRowCount()
    {
      return mgr.getPlanes().size() + 1;
    }

    @Override
    public int getColumnCount()
    {
      return columns.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
      if (rowIndex == mgr.getPlanes().size())
      {
        switch (columnIndex)
        {
          //{"Name", "Horizontal", "Vertical", "Image Path"};
          case 0: // name
            return "";
          case 1: // horizontal
            return HorizontalDirection.NEUTRE.value().toUpperCase();
          case 2: // vertical
            return VerticalDirection.NEUTRE.value().toUpperCase();
          case 3: // path
            return "";
          case 4: // delete
            return "";
        }
      }
      Plane plane = mgr.getPlanes().get(rowIndex);
      if (plane == null)
      {
        return null;
      }
      switch (columnIndex)
      {
        //{"Name", "Horizontal", "Vertical", "Image Path"};
        case 0: // name
          return plane.getName();
        case 1: // horizontal
          return plane.getHorizontal();
        case 2: // vertical
          return plane.getVertical();
        case 3: // path
          return plane.getImage();
        case 4: // delete
          return plane;
      }
      return null;
    }

    @Override
    public String getColumnName(int column)
    {
      return columns[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
      return true;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
      Plane plane;
      if (rowIndex == mgr.getPlanes().size())
      {
        plane = mgr.createPlane();
        mgr.getPlanes().add(plane);
      }
      else
      {
        plane = mgr.getPlanes().get(rowIndex);
      }
      switch (columnIndex)
      {
        //{"Name", "Horizontal", "Vertical", "Image Path", "" /* delete */};
        case 0: // name
          plane.setName((String)aValue);
          break;
        case 1: // horizontal
          plane.setHorizontal(HorizontalDirection.fromValue(((String)aValue).toLowerCase()));
          break;
        case 2: // vertical
          plane.setVertical(VerticalDirection.fromValue(((String)aValue).toLowerCase()));
          break;
        case 3: // path
          plane.setImage((String)aValue);
          break;
        case 4: // delete
          // do nothing, deletion occurs in remove(int)
          break;
      }
    }

    @Override
    public void remove(int row)
    {
      java.util.List<?> lst = mgr.getPlanes();
      if (row == lst.size())
      {
        return;
      }
      lst.remove(row);
      fireTableRowsDeleted(row, row);
    }

  }


  private lh.worldclock.config.ConfigManager mgr = new lh.worldclock.config.ConfigManager();
  private CitiesTableModel citiesModel = new CitiesTableModel();
  private PlanesTableModel planesModel = new PlanesTableModel();
  private File file;

  /** Creates new form EditorPane */
  public EditorPane()
  {
    initComponents();
    postInitComponents();
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
    jScrollPane2 = new javax.swing.JScrollPane();
    tblCities = new javax.swing.JTable();
    jPanel2 = new javax.swing.JPanel();
    jScrollPane1 = new javax.swing.JScrollPane();
    tblPlanes = new javax.swing.JTable();

    setName("Form"); // NOI18N

    jTabbedPane1.setName("jTabbedPane1"); // NOI18N

    jPanel1.setName("jPanel1"); // NOI18N

    jScrollPane2.setName("jScrollPane2"); // NOI18N

    tblCities.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {"Paris", "0.0", "0.0", "Europe/Paris"},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Name", "Latitude", "Longitude", "Timezone"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }
    });
    tblCities.setName("tblCities"); // NOI18N
    jScrollPane2.setViewportView(tblCities);
    org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(lh.worldclock.editor.EditorApp.class).getContext().getResourceMap(EditorPane.class);
    tblCities.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblCities.columnModel.title0")); // NOI18N
    tblCities.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblCities.columnModel.title1")); // NOI18N
    tblCities.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblCities.columnModel.title2")); // NOI18N
    tblCities.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblCities.columnModel.title3")); // NOI18N

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
    );
    jPanel1Layout.setVerticalGroup(
      jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(jPanel1Layout.createSequentialGroup()
        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(27, Short.MAX_VALUE))
    );

    jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

    jPanel2.setName("jPanel2"); // NOI18N

    jScrollPane1.setName("jScrollPane1"); // NOI18N

    tblPlanes.setModel(new javax.swing.table.DefaultTableModel(
      new Object [][] {
        {"A380", "gauche", "haut", "imgs/a380.jpg"},
        {null, null, null, null},
        {null, null, null, null},
        {null, null, null, null}
      },
      new String [] {
        "Name", "Horizontal", "Vertical", "Image"
      }
    ) {
      Class[] types = new Class [] {
        java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
      };

      public Class getColumnClass(int columnIndex) {
        return types [columnIndex];
      }
    });
    tblPlanes.setName("tblPlanes"); // NOI18N
    jScrollPane1.setViewportView(tblPlanes);
    tblPlanes.getColumnModel().getColumn(0).setHeaderValue(resourceMap.getString("tblPlanes.columnModel.title0")); // NOI18N
    tblPlanes.getColumnModel().getColumn(1).setHeaderValue(resourceMap.getString("tblPlanes.columnModel.title1")); // NOI18N
    tblPlanes.getColumnModel().getColumn(2).setHeaderValue(resourceMap.getString("tblPlanes.columnModel.title2")); // NOI18N
    tblPlanes.getColumnModel().getColumn(3).setHeaderValue(resourceMap.getString("tblPlanes.columnModel.title3")); // NOI18N

    javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
    jPanel2.setLayout(jPanel2Layout);
    jPanel2Layout.setHorizontalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
    );
    jPanel2Layout.setVerticalGroup(
      jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
    );

    jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
    this.setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 415, Short.MAX_VALUE)
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 327, Short.MAX_VALUE)
    );
  }// </editor-fold>//GEN-END:initComponents

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JScrollPane jScrollPane2;
  private javax.swing.JTabbedPane jTabbedPane1;
  private javax.swing.JTable tblCities;
  private javax.swing.JTable tblPlanes;
  // End of variables declaration//GEN-END:variables
  private JComboBox cbHorizontal;
  private JComboBox cbVertical;

  private void postInitComponents()
  {
    tblCities.setModel(citiesModel);
    tblPlanes.setModel(planesModel);

    tblCities.getColumnModel().getColumn(0).setCellEditor(new CityNameEditor());
    tblCities.getColumnModel().getColumn(0).setCellRenderer(new CityNameRenderer());

    cbHorizontal = new JComboBox();
    cbHorizontal.addItem(HorizontalDirection.GAUCHE.value().toUpperCase());
    cbHorizontal.addItem(HorizontalDirection.DROITE.value().toUpperCase());
    tblPlanes.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(cbHorizontal));

    cbVertical = new JComboBox();
    cbVertical.addItem(VerticalDirection.HAUT.value().toUpperCase());
    cbVertical.addItem(VerticalDirection.BAS.value().toUpperCase());
    tblPlanes.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cbVertical));

    tblPlanes.getColumnModel().getColumn(3).setCellRenderer(new ImagePathRenderer());

    ButtonColumn.createAndSetup(tblCities, 4, citiesModel);
    ButtonColumn.createAndSetup(tblPlanes, 4, planesModel);

  }

  public void openFile(File f)
  {
    try
    {
      mgr.load(f);
      file = f;
    }
    catch (JAXBException ex)
    {
      Logger.getLogger(EditorPane.class.getName()).log(Level.SEVERE, null, ex);
      ex.printStackTrace();
    }
    tblCities.tableChanged(new TableModelEvent(citiesModel));
    tblPlanes.tableChanged(new TableModelEvent(planesModel));
  }

  void save()
  {
    if (file == null)
    {
      JFileChooser jfc = new JFileChooser();
      if (jfc.showSaveDialog(EditorApp.getApplication().getMainFrame()) == JFileChooser.APPROVE_OPTION)
      {
        file = jfc.getSelectedFile();
        if (file == null) return;
      }
    }

    try
    {
      mgr.save(file);
    }
    catch (JAXBException ex)
    {
      Logger.getLogger(EditorPane.class.getName()).log(Level.SEVERE, null, ex);
      ex.printStackTrace();
    }
  }
}
