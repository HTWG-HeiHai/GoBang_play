package controllers;

import views.html.*;
import de.htwg.gobang.controller.IGbLogic;
import de.htwg.gobang.game.GoBangGame;
import de.htwg.gobang.ui.TUI;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {
	
	GoBangGame games;
	IGbLogic controller;
	TUI printer;
	char lastAction = 'n';
   
    public Result test(String name) {
    	return ok("Hello " + name);
    }
    
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
    
}
