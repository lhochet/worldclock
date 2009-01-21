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
      }
    ]

    scene: Scene {
      content: Grid {
        rows: [
          Row {
            cells: [SwingCheckBox {
                text: " Keep aspect ratio ",
                selected: bind keepRatio with inverse }]
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
