package com.mygdx.game.Ai.Pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.MainGame.GameField;
import com.mygdx.game.MainGame.Tile;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;

public class GameFieldGraph implements IndexedGraph<Tile> {
    public static final int WALL_PATH = 0;
    public static final int EMPTY_PATH = 1;

    int width;

    GameField gameField;
    public Array<Tile> tiles;
    public Array<TileConnection> connections;
    ObjectMap<Tile, Array<Connection<Tile>>> tileConnections;

    TileHeuristic tileHeuristic = new TileHeuristic();
    public int pathType;

    public GameFieldGraph(GameField gameField, int width, int pathType) {
        this.tiles = new Array<Tile>();
        this.tileConnections = new ObjectMap<>();
        this.width = width;
        this.connections = new Array<TileConnection>();
        this.pathType = pathType;
        this.gameField = gameField;
        update();
    }

    //ponovno zgradim graf - tile se spreminjajo in jih je potrebno zamenjat
    public void update(){
        if(this.pathType == WALL_PATH){
            findWallNodes();
        }else{
            findEmptyNodes();
        }
    }

    //med polji poiščem tista ki so ali prazna ali imajo navaden zid ali pa powerup
    public void findWallNodes(){
        this.tileConnections.clear();
        this.tiles.clear();
        int counter=0;
        //poiščem vsa polja, ki ustrezajo pogojem in jih dodam grafu
        for(Tile t: gameField.tiles){
            if(!((t.value & GameField.FIELD_UNBRAKABLE_WALL) == GameField.FIELD_UNBRAKABLE_WALL) &&
                    !((t.value & GameField.BOMB) == GameField.BOMB) &&
                    !((t.value & GameField.EXPLOSIONH) == GameField.EXPLOSIONH) &&
                    !((t.value & GameField.EXPLOSIONV) == GameField.EXPLOSIONV) &&
                    !((t.value & GameField.EXPLOSIONM) == GameField.EXPLOSIONM) &&
                    !t.getWillExplode()){
                t.setIndexGraphWalls(counter++);
                tiles.add(t);
            }
        }

        //dodamo povezave za vsaki tile ki je v grafu
        for(Tile t : this.tiles){
            tileConnections.put(t, new Array<Connection<Tile>>());
            Tile up = t.up(gameField);
            Tile down = t.down(gameField);
            Tile left = t.left(gameField);
            Tile right =t.right(gameField);

            int cost;
            //preverimo če je tile v grafu
            //UP
            if(tiles.contains(up, true)){
                //dodamo utež na povezavo, powerup -1 saj ga prioritiziramo, zid 4 saj vzame več časa
                if((up.value & GameField.FIELD_WALL) == GameField.FIELD_WALL) cost=4;
                else if((up.value & GameField.POWERUP_SPEED) == GameField.POWERUP_SPEED) cost = -1;
                else if((up.value & GameField.POWERUP_POWER) == GameField.POWERUP_POWER) cost = -1;
                else if((up.value & GameField.POWERUP_BOMBS) == GameField.POWERUP_BOMBS) cost = -1;
                else cost = 1;
                //dodamo povezavo
                TileConnection tmp = new TileConnection(t, up, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
            //DOWN
            if(this.tiles.contains(down, true)){
                if((up.value & GameField.FIELD_WALL) == GameField.FIELD_WALL) cost=4;
                else if((up.value & GameField.POWERUP_SPEED) == GameField.POWERUP_SPEED) cost = -1;
                else if((up.value & GameField.POWERUP_POWER) == GameField.POWERUP_POWER) cost = -1;
                else if((up.value & GameField.POWERUP_BOMBS) == GameField.POWERUP_BOMBS) cost = -1;
                else cost = 1;
                TileConnection tmp = new TileConnection(t, down, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
            //LEFT
            if(this.tiles.contains(left, true)){
                if((up.value & GameField.FIELD_WALL) == GameField.FIELD_WALL) cost=4;
                else if((up.value & GameField.POWERUP_SPEED) == GameField.POWERUP_SPEED) cost = -1;
                else if((up.value & GameField.POWERUP_POWER) == GameField.POWERUP_POWER) cost = -1;
                else if((up.value & GameField.POWERUP_BOMBS) == GameField.POWERUP_BOMBS) cost = -1;
                else cost = 1;
                TileConnection tmp = new TileConnection(t, left, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
            //RIGHT
            if(this.tiles.contains(right, true)){
                if((up.value & GameField.FIELD_WALL) == GameField.FIELD_WALL) cost=4;
                else if((up.value & GameField.POWERUP_SPEED) == GameField.POWERUP_SPEED) cost = -1;
                else if((up.value & GameField.POWERUP_POWER) == GameField.POWERUP_POWER) cost = -1;
                else if((up.value & GameField.POWERUP_BOMBS) == GameField.POWERUP_BOMBS) cost = -1;
                else cost = 1;
                TileConnection tmp = new TileConnection(t, right, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
        }
    }

    //med polji poiščem tista ki so prehodna in trenutno še niso eksplodirala
    public void findEmptyNodes(){
        //enako kot pri drugi pot le da so pogoji drugačni, ni uteži na povezavah saj zidovi in powerupi niso vklkjučeni
        this.tileConnections.clear();
        this.tiles.clear();
        int counter=0;
        for(Tile t: gameField.tiles){
            if(!((t.value & GameField.EXPLOSIONH) == GameField.EXPLOSIONH) &&
                    !((t.value & GameField.EXPLOSIONM) == GameField.EXPLOSIONM) &&
                    !((t.value & GameField.EXPLOSIONV) == GameField.EXPLOSIONV) &&
                    t.getIsPassable()){
                t.setIndexGraphEmpty(counter++);
                tiles.add(t);
            }
        }

        for(Tile t : this.tiles){
            tileConnections.put(t, new Array<Connection<Tile>>());
            Tile up = t.up(gameField);
            Tile down = t.down(gameField);
            Tile left = t.left(gameField);
            Tile right =t.right(gameField);

            int cost = 1;
            if(tiles.contains(up, true)){
                TileConnection tmp = new TileConnection(t, up, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
            if(this.tiles.contains(down, true)){
                TileConnection tmp = new TileConnection(t, down, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
            if(this.tiles.contains(left, true)){
                TileConnection tmp = new TileConnection(t, left, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
            if(this.tiles.contains(right, true)){
                TileConnection tmp = new TileConnection(t, right, cost);
                tileConnections.get(t).add(tmp);
                connections.add(tmp);
            }
        }
    }

    //poiščem pot med dvema tiloma v grafu
    public GraphPath<Tile> findPath(Tile startTile, Tile goalTile){
        GraphPath<Tile> tilePath = new DefaultGraphPath<>();
        new IndexedAStarPathFinder<>(this).searchNodePath(startTile, goalTile, tileHeuristic, tilePath);
        return tilePath;
    }


    @Override
    public int getIndex(Tile node) {
        if(pathType == WALL_PATH){
            return node.indexGraphWalls;
        }else{
            return node.indexGraphEmpty;
        }
    }

    @Override
    public int getNodeCount() {
        return tiles.size;
    }

    @Override
    public Array<Connection<Tile>> getConnections(Tile fromNode) {
        if(tileConnections.containsKey(fromNode)){
            return tileConnections.get(fromNode);
        }

        return new Array<>(0);
    }
}
