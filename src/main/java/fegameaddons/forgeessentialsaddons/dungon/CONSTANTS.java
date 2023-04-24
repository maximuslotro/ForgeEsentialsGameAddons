package fegameaddons.forgeessentialsaddons.dungon;

public interface CONSTANTS {


	// constants for tile status
	public static char TILE_OPEN='-';// blank tile
	public static char TILE_PLAYER ='o';// tile for player's current location
	public static char TILE_TREASURE='$';// tile for treasure location
	public static char TILE_AMULET='@';// tile for hazard that enlarges the dungeon
	public static char TILE_MONSTER='M';// tile for monster current location
	public static char TILE_PILLAR='+';// tile for unpassable pillar location
	public static char TILE_DOOR='?';// tile for door to the next room
	public static char TILE_EXIT='!';// tile for exit door out of dungeon
	
	// constants for movement status flags 
	public static int STATUS_STAY=0;//flag indicating player has stayed still
	public static int STATUS_MOVE=1;//flag indicating player has moved in a direction
	public static int STATUS_TREASURE=2;//flag indicating player has stepped onto the treasure
	public static int STATUS_AMULET=3;//flag indicating player has stepped onto an amulet
	public static int STATUS_LEAVE=4;//flag indicating player has left the current room
	public static int STATUS_ESCAPE=5;//flag indicating player has gone through the dungeon exit
	
	// constants for user's keyboard inputs
	public static String INPUT_QUIT="q";//quit command
	public static String INPUT_STAY= "e";// no movement
	public static String MOVE_UP= "w";//up movement
	public static String MOVE_LEFT= "a";//left movement
	public static String MOVE_DOWN= "s";//down movement
	public static String MOVE_RIGHT= "d";//right movement
}
