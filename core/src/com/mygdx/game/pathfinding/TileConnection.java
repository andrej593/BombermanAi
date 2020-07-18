package com.mygdx.game.pathfinding;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MainGame.Tile;
import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TileConnection implements Connection<Tile> {
    Tile fromTile;
    Tile toTile;
    float cost;

    public TileConnection(Tile fromTile, Tile toTile, float cost) {
        this.fromTile = fromTile;
        this.toTile = toTile;
        this.cost = cost;
    }

    public void render(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(0, 0, 0, 1);
        shapeRenderer.rectLine(fromTile.pos.x, fromTile.pos.y, toTile.pos.x, toTile.pos.y, 1);
    }

    @Override
    public String toString() {
        return "TileConnection{" +
                "fromTile=" + fromTile.pos.x + ", "+fromTile.pos.y +
                ", toTile=" + toTile.pos.x +", "+toTile.pos.y +
                ", cost=" + cost +
                '}';
    }

    @Override
    public float getCost() {
        return cost;
    }

    @Override
    public Tile getFromNode() {
        return fromTile;
    }

    @Override
    public Tile getToNode() {
        return toTile;
    }
}
