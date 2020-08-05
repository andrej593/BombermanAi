package com.mygdx.game.Ai.Pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame.Tile;

public class TileHeuristic implements Heuristic<Tile>{

    @Override
    public float estimate(Tile currentTile, Tile goalTile) {
        float distance = Math.abs(currentTile.pos.x - goalTile.pos.x)+Math.abs(currentTile.pos.y - goalTile.pos.y);
        return distance;
    }
}
