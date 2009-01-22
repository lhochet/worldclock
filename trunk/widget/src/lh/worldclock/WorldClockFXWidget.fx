/*
 * WorldClockFXWidget.fx
 *
 * Created on 5 oct. 2008, 21:48:41
 */

package lh.worldclock;

import org.widgetfx.*;
import org.widgetfx.config.*;
import javafx.scene.control.*;
import javafx.scene.*;
import javafx.ext.swing.*;
import javax.swing.*;
import org.jfxtras.scene.layout.*;
import javafx.scene.text.*;
import java.awt.Color;

/**
 * @author Ludovic
 * @since 5 oct. 2008, 21:48:41
 */


var cx: Number = 320;
var cy: Number = 200;
def ratio = 320.0 / 200.0;


var board = new WorldClockPanel(); //cx, cy);
var fxBoard = SwingComponent.wrap(board);
var keepRatio: Boolean = true  on replace
{
  if (keepRatio)
  {
    widget.aspectRatio = ratio
  }
    else
  {
    widget.aspectRatio = 0
  }
}
var configFilePath: String = "" on replace
{
  if ((configFilePath == null) or ("".equals(configFilePath)))
  {
    board.clearCities();
  }
    else
  {
    board.loadConfig(configFilePath);
  }
  board.repaint();
}
var colourString: String = "Red" on replace
{
  var colour: Color; // = Color.RED;

  if ("Red".equals(colourString)) colour = Color.RED
  else if ("White".equals(colourString)) colour = Color.WHITE
  else if ("Light Gray".equals(colourString)) colour = Color.LIGHT_GRAY
  else if ("Gray".equals(colourString)) colour = Color.GRAY
  else if ("Dark Gray".equals(colourString)) colour = Color.DARK_GRAY
  else if ("Black".equals(colourString)) colour = Color.BLACK
  else if ("Pink".equals(colourString)) colour = Color.PINK
  else if ("Orange".equals(colourString)) colour = Color.ORANGE
  else if ("Yellow".equals(colourString)) colour = Color.YELLOW
  else if ("Green".equals(colourString)) colour = Color.GREEN
  else if ("Magenta".equals(colourString)) colour = Color.MAGENTA
  else if ("Cyan".equals(colourString)) colour = Color.CYAN
  else if ("Blue".equals(colourString)) colour = Color.BLUE;

  java.lang.System.out.println("colourString = {colourString}");
  java.lang.System.out.println("colour = {colour}");
  board.setColour(colour);
  board.repaint();
}


var browseButton:SwingButton = SwingButton {
  text: "Browse...";
  action: function() {
    var chooser:JFileChooser = new JFileChooser(configFilePath);
    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    var returnVal = chooser.showOpenDialog(browseButton.getJButton());
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      configFilePath = chooser.getSelectedFile().getAbsolutePath();
    }
  }
}


var widget: Widget = Widget
{
  width: bind cx with inverse
  height: bind cy with inverse
    
  configuration: Configuration {
    properties: [
      BooleanProperty {
        name: "keepRatio"
        value: bind keepRatio with inverse
      },
      StringProperty {
        name: "configFilePath"
        value: bind configFilePath with inverse
      },
      StringProperty {
        name: "textColour"
        value: bind colourString with inverse
        autoSave: true
      }
    ]

    onLoad: function()
    {
  java.lang.System.out.println("OLcolourString = {colourString}");
    }

    scene: Scene {
      content: Grid {
        rows: [
          Row {
            cells: [SwingCheckBox {
                text: " Keep aspect ratio ",
                selected: bind keepRatio with inverse }]
          }
          Row {
            cells: [Text {content: "Text colour:"},
                    SwingComboBox {
                      items: [
                        SwingComboBoxItem { text: "Red", value: Color.RED},
                        SwingComboBoxItem { text: "White", value: Color.WHITE},
                        SwingComboBoxItem { text: "Light Gray", value: Color.LIGHT_GRAY},
                        SwingComboBoxItem { text: "Gray", value: Color.GRAY},
                        SwingComboBoxItem { text: "Dark Gray", value: Color.DARK_GRAY},
                        SwingComboBoxItem { text: "Black", value: Color.BLACK},
                        SwingComboBoxItem { text: "Pink", value: Color.PINK},
                        SwingComboBoxItem { text: "Orange", value: Color.ORANGE},
                        SwingComboBoxItem { text: "Yellow", value: Color.YELLOW},
                        SwingComboBoxItem { text: "Green", value: Color.GREEN},
                        SwingComboBoxItem { text: "Magenta", value: Color.MAGENTA},
                        SwingComboBoxItem { text: "Cyan", value: Color.CYAN},
                        SwingComboBoxItem { text: "Blue", value: Color.BLUE}
                      ]
                      text: bind colourString with inverse
                    }]
          }
          Row {
            cells: [Text {
                content: "Cities file:"}, TextBox {
                text: bind configFilePath with inverse,
                columns: 40}, browseButton]
          }
        ]
      }
    }
  }

  // hack to set the initial board height based on its width
  var isFirstDock = true;
  aspectRatio: ratio;
  var widgetWidth: Number = bind cx on replace
  {
    if (isFirstDock)
    {
      var lcy = widgetWidth / ratio;
          board.updateSize(widgetWidth, lcy);
      fxBoard.width = widgetWidth;
      fxBoard.height = lcy;
    }
  }
    
  onResize: function (cx, cy):Void
  {
    isFirstDock = false;
      board.updateSize(cx.intValue(), cy.intValue());
    fxBoard.width = cx;
    fxBoard.height = cy;
  }

  skin: Skin
  {
    scene: fxBoard
  }
}
