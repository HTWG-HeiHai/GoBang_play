package controllers;

import views.html.*;
import de.htwg.gobang.controller.IGbLogic;
import de.htwg.gobang.entities.IGameField;
import de.htwg.gobang.entities.IGameToken;
import de.htwg.gobang.game.GoBangGame;
import de.htwg.gobang.ui.TUI;
import de.htwg.gobang.observer.IObserver;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import play.libs.F.Callback0;
import play.libs.F.Callback;

import java.io.File;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsonorg.JsonOrgModule;

import org.json.JSONArray;
import org.json.JSONObject;

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

//    public void channel(WebSocket.Out<JSONArray> out, int x, int y, String color) {
//    	out.write(x + "_" + y + "-" + color);
//    }

    public void channel(WebSocket.Out<JsonNode> out, JsonNode field) {
    	out.write(field);
    }

    public WebSocket<JsonNode> webSocket() {
    	return new WebSocket<JsonNode>() {
    		public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {//mit json statt String!!
    			new Watcher(controller, out);
    			
    			in.onMessage(new Callback<JsonNode>() {
    				public void invoke(JsonNode json) {
    					String command = json.get("command").textValue();
    					if(command.equals("newRound")) {
    						if (controller.getcPlayer() == controller.getPlayer1()) {
    							controller.newGame(true);
    						} else {
    							controller.newGame(false);
    						}
    					} else {
							int x = Integer.parseInt(command.split("_")[0]) - 1;
							int y = Integer.parseInt(command.split("_")[1]) - 1;
							lastAction = controller.setToken(x, y);
							channel(out, toJson(controller.getField()));//im controller: in newGame() fehlt notifyObservers()
    					}
//    					if(lastAction == 'e') {
//    						channel(out, x + 1, y + 1, controller.getcPlayer().getName());
//    						channel(out, toJson(controller.getField()));
//    					}
    				}
    			});
    			in.onClose(new Callback0() {
    				public void invoke() {
    					
    				}
    			});
//    			out.write("message");
    		}
    	};
    }

	public class Watcher implements IObserver {
		private Object out;
		public Watcher(IGbLogic engine, WebSocket.Out<JsonNode> out) {
			engine.addObserver(this);
			this.out = out;
		}

		@Override
		public void update() {
//			channel(out)
//			System.out.println("heyyo");
			channel((WebSocket.Out<JsonNode>)out, toJson(controller.getField()));
		}
	}

	public JsonNode toJson(IGameToken[][] field) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JsonOrgModule());
		JSONArray json = new JSONArray();
		int i = 1;
		for(IGameToken[] row : field) {
			JSONArray r = new JSONArray();
			int j = 1;
			for(IGameToken token : row) {
				JSONObject t = new JSONObject();
				t.put("id", i + "_" + j);
				t.put("name", token.getName());
				r.put(t);
				j++;
			}
			json.put(r);
			i++;
		}
		return mapper.valueToTree(json);
	}
//	public File toJson(IGameToken[][] field) {
//		try {
//			ObjectMapper mapper = new ObjectMapper();
//			File f;
//			mapper.writeValue(new File("field.json"), field);
//			return f;
//		} catch(Exception x) {
//			
//		}
//		return null;
//	}
//	public String toJson(IGameToken[][] field) {
//		StringBuilder jsonField = new StringBuilder("\"field\":[\n");
//		int i = 1;
//		for(IGameToken[] row : field) {
//			jsonField.append("    \"row\":[\n");
//			int j = 1;
//			for(IGameToken token : row) {
//				if(i != 19) {
//					jsonField.append("        {\"token\":\"" + i + "_" + j + "\", \"color\":\"" + token.getName() + "\"},\n");
//				} else {
//					jsonField.append("        {\"token\":\"" + i + "_" + j + "\", \"color\":\"" + token.getName() + "\"}\n");
//				}
//				j++;
//			}
//			if(i != 19) {
//				jsonField.append("    ],\n");
//			} else {
//				jsonField.append("    ]\n");
//			}
//			i++;
//		}
//		jsonField.append("];\n");
//		System.out.println(jsonField.toString());
//		return jsonField.toString();
//	}
}
