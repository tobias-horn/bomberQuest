package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.files.FileHandle;

import java.util.*;

/**
 * Parses map files and initializes game objects based on the map data.
 */
public class MapParser {

    /**
     * Parses the map file and populates the provided GameMap with objects.
     *
     * @param gameMap    the GameMap instance to populate with objects
     * @param fileHandle the file handle pointing to the map file
     */
    public static void parseMap(GameMap gameMap, FileHandle fileHandle){
        boolean containsExit = false;

        HashMap<String, Integer> tileMap = new HashMap<>();

        String rawMap = fileHandle.readString();
        ArrayList<String> lines = new ArrayList<>(Arrays.asList(rawMap.split("\n")));

        for (String line : lines){

            if(line.isEmpty() || line.startsWith("#")) continue;

            ArrayList<String> coordinateTypePair = new ArrayList<>(Arrays.asList(line.split("=")));

            tileMap.put(coordinateTypePair.get(0).trim(), Integer.valueOf(coordinateTypePair.get(1).trim()));

            if(Integer.parseInt(coordinateTypePair.get(1)) == 4) containsExit = true;

        }

        if(!containsExit){
            placeRandomExit(tileMap);
        }

        addSpeedPowerUps(tileMap);

        for(Map.Entry<String, Integer> entry : tileMap.entrySet()){
            int x = Integer.parseInt(entry.getKey().split(",")[0]);
            int y = Integer.parseInt(entry.getKey().split(",")[1]);

            gameMap.createObject(x, y, entry.getValue());
        }
    }

    /**
     * Places a random exit on one of the destructible walls if no exit exists.
     *
     * @param tileMap the map of tile coordinates to their corresponding types
     */
    public static void placeRandomExit(HashMap<String, Integer> tileMap){
        HashMap<String, Integer> destructibleWalls = new HashMap<>();

        for (Map.Entry<String, Integer> entry : tileMap.entrySet()){
            if(entry.getValue().equals(1)) destructibleWalls.put(entry.getKey(), entry.getValue());
        }

        Random random = new Random();
        int randomExit = random.nextInt(destructibleWalls.size());

        List<String> keys = new ArrayList<>(destructibleWalls.keySet());
        String selectedKey = keys.get(randomExit);

        tileMap.put(selectedKey, 4);
    }

    /**
     * Adds speed power-ups to random destructible wall positions on the map.
     *
     * @param tileMap the map of tile coordinates to their corresponding types
     */
    public static void addSpeedPowerUps(HashMap<String, Integer> tileMap) {
        List<String> possiblePositions = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : tileMap.entrySet()) {
            if (entry.getValue() == 1) {
                possiblePositions.add(entry.getKey());
            }
        }

        Collections.shuffle(possiblePositions);
        int maxSpeedPowerUps = Math.min(4, possiblePositions.size());

        for (int i = 0; i < maxSpeedPowerUps; i++) {
            String key = possiblePositions.get(i);
            tileMap.put(key, 7);
        }
    }

}
