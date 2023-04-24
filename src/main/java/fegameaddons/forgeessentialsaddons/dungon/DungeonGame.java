package fegameaddons.forgeessentialsaddons.dungon;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.forgeessentials.util.output.ChatOutputHandler;

import fegameaddons.forgeessentialsaddons.ModuleGames;
import net.minecraft.command.CommandSource;

public class DungeonGame implements CONSTANTS{
	public boolean invalid=false;
	private char[][] map=null;
	private int maxRow = 0;
	private int maxCol = 0;
	private int nextRow = 0;
	private int nextCol = 0;
	private Player player = new Player();
	private String dungeon;
	private int current_room=1;
	char input = 0;
    int status = 0;
	
	public DungeonGame(String name, CommandSource source) {
		dungeon = name;
		String fileName = dungeon + Integer.toString(current_room) + ".txt";
		loadLevel(fileName);
		if (map == null) {
        	ChatOutputHandler.chatWarning(source, "A map by that name does not exist!");
        	System.out.print("Null map found, check if map has correct name or internals");
        	invalid = true;
            return;
        }
	}
	
	
	public void printInstructions(CommandSource source) {
		ChatOutputHandler.chatConfirmation(source, "-----------------------------------------------------");
		ChatOutputHandler.chatConfirmation(source, "Good day, adventurer!"                                    );
		ChatOutputHandler.chatConfirmation(source, "Your goal is to get the treasure and escape the dungeon!" );
		ChatOutputHandler.chatConfirmation(source, " --- SYMBOLS ---"                                         );
		ChatOutputHandler.chatConfirmation(source, " o              : That is you, the adventurer!"              );
		ChatOutputHandler.chatConfirmation(source, " $              : These are treasures. Lots of money!"        );
	    ChatOutputHandler.chatConfirmation(source, " @              : These magical amulets resize the level."    );
	    ChatOutputHandler.chatConfirmation(source, " M              : These are monsters; avoid them!"            );
	    ChatOutputHandler.chatConfirmation(source, " +, -, #        : These are unpassable obstacles."            );
	    ChatOutputHandler.chatConfirmation(source, " ?              : A door to another level."                   );
	    ChatOutputHandler.chatConfirmation(source, " !               : A door to escape the dungeon."              );
	    ChatOutputHandler.chatConfirmation(source, " --- CONTROLS ---"                                        );
	    ChatOutputHandler.chatConfirmation(source, " w, a, s, d     : Keys for moving up, left, down, and right." );
	    ChatOutputHandler.chatConfirmation(source, " e              : Key for staying still for a turn."          );
	    ChatOutputHandler.chatConfirmation(source, " q              : Key for abandoning your quest."             );
	    ChatOutputHandler.chatConfirmation(source, "-----------------------------------------------------");
	}
	
	
	private void loadLevel(String fileName) {
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(new File(ModuleGames.moduleDir, fileName)));
			String line = reader.readLine();
			String[] dimData = line.split(" ");
			maxRow = Integer.parseInt(dimData[0]);
			maxCol = Integer.parseInt(dimData[1]);
			line = reader.readLine();
			String[] playerData = line.split(" ");
			player.row = Integer.parseInt(playerData[0]);
			player.col = Integer.parseInt(playerData[1]);
			char[][] mapNew = createMap(maxRow, maxCol);
			for(int row=0;row<maxRow;row++){
				line = reader.readLine();
				String[] temp = line.split(" ");
				StringBuilder sb = new StringBuilder();
			    for (String s : temp) {
			        sb.append(s);
			    }
				char[] colData = sb.toString().toCharArray();;
			    for(int col=0;col<maxCol;col++){
			    	mapNew[row][col]=colData[col];
			    	if(mapNew[row][col]==TILE_PLAYER){
			    		reader.close();
			    		return;
			    	}
			    	if(mapNew[row][col]!=TILE_EXIT&&mapNew[row][col]!=TILE_DOOR&&mapNew[row][col]!=TILE_PILLAR&&mapNew[row][col]!=TILE_MONSTER&&mapNew[row][col]!=TILE_AMULET&&mapNew[row][col]!=TILE_TREASURE&&mapNew[row][col]!=TILE_OPEN){
			    		reader.close();
			    		return;
			    	}
			    }
			}
			mapNew[player.row][player.col]=TILE_PLAYER;
			reader.close();
			map=mapNew;
		} catch (IOException e) {
			return;
		}
	}
	
	public void logic(String input, CommandSource source) {
		// increment dungeon movement counter
        player.moves++;
        if (input.equals(INPUT_STAY)) {
            status = STATUS_STAY;
        } else {
            // translate from the character input to a direction
            nextRow = player.row;
            nextCol = player.col;
            getDirection(input);
            // move player to new location index, if possible, and get player status
            status = playerMove(nextRow, nextCol);
        }
        // quit game if user escapes
        if (status == STATUS_ESCAPE) {
            outputMap(source);
            outputStatus(status, source);
            return ;
        }
        
        // go to next level if user goes through door
        if (status == STATUS_LEAVE) {
			outputMap(source);
            outputStatus(status, source);
            return;
        }
        
        // move monsters, end if player is caught
        if (doMonsterAttack(player)) {
            outputMap(source);
            ChatOutputHandler.chatConfirmation(source, "You died, adventurer! Better luck next time!");
            return;
        }
        
        // use amulet
        if (status == STATUS_AMULET) {
            resizeMap();
        }
        
        // display map and status
        outputMap(source);
        outputStatus(status, source);
	}
	
	public void tickRoom(CommandSource source) {
		String fileName = dungeon + Integer.toString(current_room+1) + ".txt";
		loadLevel(fileName);
		if (map == null) {
        	ChatOutputHandler.chatWarning(source, "Error finding next level: "+dungeon + Integer.toString(current_room+1) + ".txt");
        	invalid = true;
            return;
        }
		current_room+=1;
	}
	
	
	private char[][] createMap(int maxRow, int maxCol) {
		char[][] map = new char[maxRow][maxCol];
		
	    for (int row=0; row < maxRow; ++row) {
	        for (int col=0; col < maxCol; ++col) {
	            map[row][col] = TILE_OPEN;
	        }
	    }
	    return map;
	}
	
	
	public void outputMap(CommandSource source) {
		ChatOutputHandler.chatConfirmation(source, "Dungeon: "+dungeon+", Level: " + current_room);
		// output top border
	    String top="+";
	    for (int i = 0; i < maxCol*2; ++i) {
	    	top+="-";
	    }
	    top+="+";
	    ChatOutputHandler.chatConfirmation(source, top);

	    for (int i = 0; i < maxRow; ++i) {
	        // output left border
	    	String rows="#";
	        // output inner blocks
	        for (int j = 0; j < maxCol; ++j) {
	            // output current block
	        	rows+=" ";
	            if (map[i][j] == TILE_OPEN) {
	            	rows+=" ";
	            } else {
	            	rows+=map[i][j];
	            }
	            rows+=" ";
	        }
	        // output right border
	        rows+="#";
	        ChatOutputHandler.chatConfirmation(source, rows);
	    }
	    // output bottom border
	    String bottom="+";
	    for (int i = 0; i < maxCol*2; ++i) {
	    	bottom+="-";
	    }
	    bottom+="+";
	    ChatOutputHandler.chatConfirmation(source, bottom);
	}
	

	private int playerMove(int nextRow, int nextCol) {
		if(nextRow>=maxRow){
			return STATUS_STAY;
			}
		if(nextRow<0){
			return STATUS_STAY;
		}
		if(nextCol>maxCol){
			return STATUS_STAY;
		}
		if(nextCol<0){
			return STATUS_STAY;
		}
		int NextPlac= map[nextRow][nextCol];
		if(NextPlac==TILE_PILLAR){
			return STATUS_STAY;
		}
		if(NextPlac==TILE_OPEN){
			map[player.row][player.col]=TILE_OPEN;
			map[nextRow][nextCol]=TILE_PLAYER;
			player.row=nextRow;
			player.col=nextCol;
			return STATUS_MOVE;
		}
		if(NextPlac==TILE_TREASURE){
			player.treasure+=1;
			map[player.row][player.col]=TILE_OPEN;
			map[nextRow][nextCol]=TILE_PLAYER;
			player.row=nextRow;
			player.col=nextCol;
			return STATUS_TREASURE;
		}
		if(NextPlac==TILE_AMULET){
		    map[player.row][player.col]=TILE_OPEN;
		    map[nextRow][nextCol]=TILE_PLAYER;
		    player.row=nextRow;
		    player.col=nextCol;
		    return STATUS_AMULET;
		}
		if(NextPlac==TILE_DOOR){
		    map[player.row][player.col]=TILE_OPEN;
		    map[nextRow][nextCol]=TILE_PLAYER;
		    player.row=nextRow;
		    player.col=nextCol;
		    return STATUS_LEAVE;
		}
		if(NextPlac==TILE_EXIT&&player.treasure>0){
			map[player.row][player.col]=TILE_OPEN;
		    map[nextRow][nextCol]=TILE_PLAYER;
		    player.row=nextRow;
		    player.col=nextCol;
		    return STATUS_ESCAPE;
		}
		else{
			return STATUS_STAY;
		}
	}
	
	
	private void resizeMap() {
	    if(map == null||maxRow<1||maxCol<1){
	    	return;
	    }
	    int oldRow =maxRow;
	    int oldCol = maxCol;
	    maxRow*=2;
	    maxCol*=2;
	    char[][] mapNew = createMap(maxRow,maxCol);
	    for(int row =0; row <maxRow;row++){
	        for(int col =0; col <maxCol;col++){
	            //AB
	            //CD
	            //Area A
	            if(row<oldRow && col<oldCol){
	                mapNew[row][col]=map[row][col];
	            }
	            //Area B
	            if(row<oldRow && col>=oldCol){
	                if(map[row][col-oldCol]==TILE_PLAYER){
	                    mapNew[row][col]=TILE_OPEN;
	                }
	                else{
	                    mapNew[row][col]=map[row][col-oldCol];
	                }
	            }
	            //Area C
	            if(row>=oldRow && col<oldCol){
	                if(map[row-oldRow][col]==TILE_PLAYER){
	                    mapNew[row][col]=TILE_OPEN;
	                }
	                else{
	                    mapNew[row][col]=map[row-oldRow][col];
	                }
	            }
	            //Area D
	            if(row>=oldRow && col>=oldCol){
	                if(map[row-oldRow][col-oldCol]==TILE_PLAYER){
	                    mapNew[row][col]=TILE_OPEN;
	                }
	                else{
	                    mapNew[row][col]=map[row-oldRow][col-oldCol];
	                }
	            }
	        }
	    }
	    map=mapNew;
	}
	

	private boolean doMonsterAttack(Player player) {
		boolean dead = false;
		for(int col = player.col; col < maxCol;col++){
			if (map[player.row][col]==TILE_PILLAR){
				break;
				}
			if (map[player.row][col]==TILE_MONSTER&&col!=player.col){
				map[player.row][col]=TILE_OPEN;
				map[player.row][col-1]=TILE_MONSTER;
				if (map[player.row][player.col]==TILE_MONSTER){
			    	dead=true;
				}
			}
		}
		for(int col = player.col; col >= 0;col--){
			if (map[player.row][col]==TILE_PILLAR){
				break;
			}
			if (map[player.row][col]==TILE_MONSTER&&col!=player.col){
				map[player.row][col]=TILE_OPEN;
				map[player.row][col+1]=TILE_MONSTER;
				if (map[player.row][player.col]==TILE_MONSTER){
					dead=true;
				}
			}
		}
		for(int row = player.row; row < maxRow;row++){
			if (map[row][player.col]==TILE_PILLAR){
				break;
			}
			if (map[row][player.col]==TILE_MONSTER&&row!=player.row){
				map[row][player.col]=TILE_OPEN;
				map[row-1][player.col]=TILE_MONSTER;
				if (map[player.row][player.col]==TILE_MONSTER) {
					dead=true;
				}
			}
		}
		for(int row = player.row; row >= 0;row--){
			if (map[row][player.col]==TILE_PILLAR){
				break;
				}
			if (map[row][player.col]==TILE_MONSTER&&row!=player.row){
				map[row][player.col]=TILE_OPEN;
				map[row+1][player.col]=TILE_MONSTER;
				if (map[player.row][player.col]==TILE_MONSTER){
					dead=true;
				}
			}
		}
		if (dead){
			return true;
		}
		return false;
	}
	
	private void outputStatus(int status, CommandSource source) {
	    if (status != STATUS_STAY) {
	    	ChatOutputHandler.chatConfirmation(source, "You have moved to row " + player.row + " and column " + player.col);
	    }

	    switch (status) {
	        case STATUS_STAY :
	        	ChatOutputHandler.chatConfirmation(source, "You stayed at row " + player.row + " and column " + player.col);
				ChatOutputHandler.chatConfirmation(source, "You didn't move. Are you lost?");
	            break;
	        case STATUS_MOVE :
	            break;
	        case STATUS_TREASURE :
	        	ChatOutputHandler.chatConfirmation(source, "Well done, adventurer! You found some treasure.");
	        	ChatOutputHandler.chatConfirmation(source, "You now have " + player.treasure + (player.treasure > 1 ? " treasures." : " treasure."));
	            break;
	        case STATUS_AMULET :
	        	ChatOutputHandler.chatConfirmation(source, "The magic amulet sparkles and crumbles into dust.");
	        	ChatOutputHandler.chatConfirmation(source, "The ground begins to rumble. Are the walls moving?");
	            break;
	        case STATUS_LEAVE :
	        	ChatOutputHandler.chatConfirmation(source, "You go through the doorway into the unknown beyond...");
	        	tickRoom(source);
	            break;
	        case STATUS_ESCAPE :
	        	ChatOutputHandler.chatConfirmation(source, "Congratulations, adventurer! You have escaped the dungeon!");
	        	ChatOutputHandler.chatConfirmation(source, "You escaped with " + player.treasure + (player.treasure > 1 ? " treasures " : " treasure "));
	        	ChatOutputHandler.chatConfirmation(source, "and in " + player.moves + " total moves.");
	        	invalid=true;
	            break;
	    }
	}
	
	
	private void getDirection(String input) {
	    if(input.equals(MOVE_UP)){
	        nextRow-=1;
	    }
	    if(input.equals(MOVE_DOWN)){
	        nextRow+=1;
	    }
	    if(input.equals(MOVE_LEFT)){
	        nextCol-=1;
	    }
	    if(input.equals( MOVE_RIGHT)){
	        nextCol+=1;
	    }
	}
}
