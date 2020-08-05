package com.mygdx.game.Ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum EnemyState implements State<com.mygdx.game.Ai.EnemyAgent> {
    //če je v nevarnosti
    INDANGER(){
        @Override
        public void update(com.mygdx.game.Ai.EnemyAgent entity) {
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
        public void update(com.mygdx.game.Ai.EnemyAgent entity) {
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
    public void enter(com.mygdx.game.Ai.EnemyAgent entity) {
        entity.updateCounter=0;
        if(entity.stateMachine.getCurrentState() == EnemyState.INDANGER){
            entity.findSafePath();

        }else if(entity.stateMachine.getCurrentState() == EnemyState.ISSAFE){
            entity.findPlayerPath();
        }
    }

    @Override
    public void exit(com.mygdx.game.Ai.EnemyAgent entity) {
    }

    @Override
    public boolean onMessage(EnemyAgent entity, Telegram telegram) {
        return false;
    }
}
