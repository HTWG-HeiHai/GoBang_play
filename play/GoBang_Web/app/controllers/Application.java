package controllers;

import views.html.*;
import de.htwg.gobang.controller.IGbLogic;
import de.htwg.gobang.game.GoBangGame;
import de.htwg.gobang.ui.TUI;
import de.htwg.gobang.observer.IObserver;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.libs.F.Callback0;
import play.libs.F.Callback;

public class Application extends Controller {
	
	GoBangGame games;
	IGbLogic controller;
	TUI printer;
	char lastAction = 'n';
    
    public Result game(){
    	games = GoBangGame.getInstance();
    	controller = games.getController();
    	printer = games.getTui();
    	String tField = printer.drawField();
    	String tHead = printer.pTurn();
    	tHead = tHead.replaceAll("\n", "<br>");
    	tField = tField.replaceAll("\n", "<br>");
    	tField = tField.replaceAll(" ", "&nbsp;");
    	return ok(game.render(tHead, tField));
    }
    
    public Result gobang(){
    	return ok(gobang.render("GoBang"));
    }

    public Result setToken(int x, int y) {
//    	if(coord.length() != 2) {
//    		return 
//    	}
    	lastAction = controller.setToken(x-1, y-1);
    	return ok();
    }
    
    public Result checkField() {
    	return ok();
    }

    public void channel(WebSocket.Out<String> out, int x, int y, String color) {
    	out.write(x + "_" + y + "-" + color);
    }

    public WebSocket<String> webSocket() {
    	return new WebSocket<String>() {
    		public void onReady(WebSocket.In<String> in, WebSocket.Out<String> out) {//mit json statt String!!
    			new Watcher(controller);
    			
    			in.onMessage(new Callback<String>() {
    				public void invoke(String field) {
    					int x = Integer.parseInt(field.split("_")[0]) - 1;
    					int y = Integer.parseInt(field.split("_")[1]) - 1;
    					lastAction = controller.setToken(x, y);
    					if(lastAction == 'e') {
    						channel(out, x + 1, y + 1, controller.getcPlayer().getName());
    					}
    				}
    			});
    			in.onClose(new Callback0() {
    				public void invoke() {
    					
    				}
    			});
    			out.write("message");
    		}
    	};
    }

	public class Watcher implements IObserver {
		public Watcher(IGbLogic engine) {
			engine.addObserver(this);
		}

		@Override
		public void update() {
//			field = controller.getField();
//			channel(out)
			System.out.println("heyyo");
		}
	}
}
