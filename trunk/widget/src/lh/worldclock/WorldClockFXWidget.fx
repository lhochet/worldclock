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
var board = new WorldClockPanel(cx, cy);
//board.updateSize(cx, cy);
var fxBoard = Component.fromJComponent(board);

/**/
Widget
{
    resizable: false
    stage: Stage 
    {
        width: cx
        height: cy
        content: ComponentView 
        {
            component: fxBoard 
            
        }
    }    
}

/** /
import javafx.application.*;
        Frame {
            visible: true, width: cx, height: cy;
            stage: Stage { content:  ComponentView 
        {
            //visible: true
            scaleX: 0.96
            scaleY: 0.85
            component: fxBoard 
            
        }  }
        }
 /**/          