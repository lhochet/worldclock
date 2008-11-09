/*
 * WorldClockFXWidget.fx
 *
 * Created on 5 oct. 2008, 21:48:41
 */

package lh.worldclock;

import org.widgetfx.*;
import javafx.application.Stage;
import lh.worldclock.core.WorldClockPanel;
import javafx.application.Frame;
import javafx.ext.swing.*;

/**
 * @author Ludovic
 * @since 5 oct. 2008, 21:48:41
 */

// place your code here

var cx = 320;
var cy = 200;

var board = new WorldClockPanel();
var fxBoard = Component.fromJComponent(board);

/**/
Widget
{
    // hack to set the initial board height based on its width
    var ratio = 320.0 / 200.0;
    var isFirstDock = true;
    private attribute widgetWidth: Integer = bind cx on replace
    {
      if (isFirstDock)
      {
          cy = (widgetWidth / ratio) as Integer;
          board.updateSize(widgetWidth, cy);
      }
    }

    resizable: true
    onResize: function (ncx, ncy):Void
    {
      isFirstDock = false;
      board.updateSize(ncx, ncy); 
    }
    stage: Stage 
    {
      width: bind cx with inverse
      height: bind cy with inverse
      content: ComponentView 
      {
        component: fxBoard 
      }
    }    
}

/** /
import javafx.application.*;

 
        Frame {
            visible: true
            width: cx
            height: cy
            
            stage: Stage { content:  ComponentView 
        {
            //visible: true
            scaleX: 0.96
            scaleY: 0.85
            component: fxBoard 
            
        }  }
        }
 /**/          