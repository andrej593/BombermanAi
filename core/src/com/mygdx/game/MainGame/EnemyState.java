package com.mygdx.game.MainGame;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.pathfinding.GameFieldGraph;

public enum EnemyState implements State<EnemyAgent> {
    //če je v nevarnosti
    INDANGER(){
        @Override
        public void update(EnemyAgent entity) {
            //sprememba stanja če je varen
            if(entity.isSafe){
                entity.stateMachine.changeState(ISSAFE);
            }else{
                //premik na varno
                entity.moveToSafety();
            }
        }
    },

    //Če je varen
    ISSAFE(){
        @Override
        public void update(EnemyAgent entity) {
            //sprememba stanja če je v nevarnosti
            if(!entity.isSafe){
                entity.stateMachine.changeState(INDANGER);
            }else{
                //premik proti tarči
                entity.determineSubState();
            }

        }
    };

    //ko vstopi v novo stanje se mu poišče potrebna pot
    @Override
    public void enter(EnemyAgent entity) {
        entity.updateCounter=0;
        if(entity.stateMachine.getCurrentState() == EnemyState.INDANGER){
            entity.findSafePath();

        }else if(entity.stateMachine.getCurrentState() == EnemyState.ISSAFE){
            entity.findPlayerPath();
        }
    }

    @Override
    public void exit(EnemyAgent entity) {

    }

    @Override
    public boolean onMessage(EnemyAgent entity, Telegram telegram) {
        return false;
    }
}
