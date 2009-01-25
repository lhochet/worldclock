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


var board = new WorldClockPanel();
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
var colourString: String = "Red";
var colourIndex: Integer = 0 on replace
{
  updateColourForIndex(colourIndex);
}

function updateColour(colStr: String)
{
  var colour: Color;
  if ("Red".equals(colStr)) colour = Color.RED
  else if ("White".equals(colStr)) colour = Color.WHITE
  else if ("Light Gray".equals(colStr)) colour = Color.LIGHT_GRAY
  else if ("Gray".equals(colStr)) colour = Color.GRAY
  else if ("Dark Gray".equals(colStr)) colour = Color.DARK_GRAY
  else if ("Black".equals(colStr)) colour = Color.BLACK
  else if ("Pink".equals(colStr)) colour = Color.PINK
  else if ("Orange".equals(colStr)) colour = Color.ORANGE
  else if ("Yellow".equals(colStr)) colour = Color.YELLOW
  else if ("Green".equals(colStr)) colour = Color.GREEN
  else if ("Magenta".equals(colStr)) colour = Color.MAGENTA
  else if ("Cyan".equals(colStr)) colour = Color.CYAN
  else if ("Blue".equals(colStr)) colour = Color.BLUE;

  board.setColour(colour);
  board.repaint();
}

function updateColourForIndex(index: Integer)
{
  var colour: Color = Color.RED;
  if (index == 0) colour = Color.RED
  else if (index == 1) colour = Color.WHITE
  else if (index == 2) colour = Color.LIGHT_GRAY
  else if (index == 3) colour = Color.GRAY
  else if (index == 4) colour = Color.DARK_GRAY
  else if (index == 5) colour = Color.BLACK
  else if (index == 6) colour = Color.PINK
  else if (index == 7) colour = Color.ORANGE
  else if (index == 8) colour = Color.YELLOW
  else if (index == 9) colour = Color.GREEN
  else if (index == 10) colour = Color.MAGENTA
  else if (index == 11) colour = Color.CYAN
  else if (index == 12) colour = Color.BLUE;

  board.setColour(colour);
  board.repaint();
}

function toColourString(index: Integer)
{
  var colour: String = "Red";
  if (index == 0) colour = "Red"
  else if (index == 1) colour = "White"
  else if (index == 2) colour = "Light Gray"
  else if (index == 3) colour = "Gray"
  else if (index == 4) colour = "Dark Gray"
  else if (index == 5) colour = "Black"
  else if (index == 6) colour = "Pink"
  else if (index == 7) colour = "Orange"
  else if (index == 8) colour = "Yellow"
  else if (index == 9) colour = "Green"
  else if (index == 10) colour = "Magenta"
  else if (index == 11) colour = "Cyan"
  else if (index == 12) colour = "Blue";

  return colour;
}

function toIndex(colStr: String): Integer
{
  var ret: Integer = 0;
  if ("Red".equals(colStr)) ret = 0
  else if ("White".equals(colStr)) ret = 1
  else if ("Light Gray".equals(colStr)) ret = 2
  else if ("Gray".equals(colStr)) ret = 3
  else if ("Dark Gray".equals(colStr)) ret = 4
  else if ("Black".equals(colStr)) ret = 5
  else if ("Pink".equals(colStr)) ret = 6
  else if ("Orange".equals(colStr)) ret = 7
  else if ("Yellow".equals(colStr)) ret = 8
  else if ("Green".equals(colStr)) ret = 9
  else if ("Magenta".equals(colStr)) ret = 10
  else if ("Cyan".equals(colStr)) ret = 11
  else if ("Blue".equals(colStr)) ret = 12;

  return ret;
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
        name: "textColour"
        value: bind colourString with inverse
      },
      StringProperty {
        name: "configFilePath"
        value: bind configFilePath with inverse
      }
    ]

    onLoad: function()
    {
      updateColour(colourString);
      colourIndex = toIndex(colourString);
    }
    onSave: function()
    {
      colourString = toColourString(colourIndex);
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
                      selectedIndex: bind colourIndex with inverse
                      //text: bind colourStringForDlg with inverse
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
