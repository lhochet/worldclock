package lh.worldclock;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * <p>Title: WorldClockViewPreferencesPage</p>
 * 
 * <p>Description: Preference page for selecting the world clock configuration file</p>
 * 
 * <p>Copyright: Copyright (c) 2006 Ludovic HOCHET</p>
 * 
 * @author Ludovic HOCHET
 * @version $Revision: 1.1 $ $Date: 2006/04/13 20:50:56 $
 */
public class WorldClockViewPreferencesPage extends FieldEditorPreferencePage
    implements IWorkbenchPreferencePage
{

  public WorldClockViewPreferencesPage()
  {
    super("World Clock", FieldEditorPreferencePage.GRID);
    // Set the preference store for the preference page.
    IPreferenceStore store = WorldClockPlugin.getDefault().getPreferenceStore();
    setPreferenceStore(store);
  }

  protected void createFieldEditors()
  {
    addField(new FileFieldEditor("worldclockFilename", "File:",
        getFieldEditorParent()));
  }

  public void init(IWorkbench workbench)
  {
    // TODO Auto-generated method stub
    // LH: yet to find out what this is used for

  }

}
