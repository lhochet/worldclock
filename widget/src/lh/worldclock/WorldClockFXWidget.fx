/*
 * WorldClockFXWidget.fx
 *
 * Created on 5 oct. 2008, 21:48:41
 */

package lh.worldclock;

import org.widgetfx.*;
import javafx.scene.control.Skin;
import javafx.scene.Scene;
import lh.worldclock.core.WorldClockPanel;
import javafx.ext.swing.*;
import javax.swing.*;

/**
 * @author Ludovic
 * @since 5 oct. 2008, 21:48:41
 */

// place your code here

var cx: Number = 320;
var cy: Number = 200;
def ratio = 320.0 / 200.0;

var board = new WorldClockPanel(cx, cy);
var fxBoard = SwingComponent.wrap(board);

/**/

//board.updateSize(320, 200);
//board.setPreferredSize(new java.awt.Dimension(320, 200));


Widget
{
    width : bind cx with inverse
    height : bind cy with inverse
    // hack to set the initial board height based on its width
    
    var isFirstDock = true;
//    aspectRatio: ratio;
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

//    resizable: false
    
    onResize: function (cx, cy):Void
    {
      isFirstDock = false;
      board.updateSize(cx.intValue(), cy.intValue());
      fxBoard.width = cx;
      fxBoard.height = cy;
    }

    skin : Skin
    {

      scene: fxBoard
    }    
}

/** /
import javafx.stage.*;

java.lang.System.out.println(ratio+1);
 board.updateSize(cx, cy);

        Stage
        {
            visible: true
            width: cx
            height: cy
            
            scene: Scene
            {
                content: fxBoard
            }
            
        }
 /**/          