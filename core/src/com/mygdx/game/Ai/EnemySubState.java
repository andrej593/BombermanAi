package com.mygdx.game.Ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.mygdx.game.Ai.EnemyAgent;

public enum EnemySubState implements State<com.mygdx.game.Ai.EnemyAgent> {
    NONE(){
        @Override
        public void update(com.mygdx.game.Ai.EnemyAgent entity) {
            if(entity.subState != NONE){
                entity.subStateMachine.changeState(entity.subState);
            }
        }
    },
    INRANGE(){
        @Override
        public void update(com.mygdx.game.Ai.EnemyAgent entity) {
            if(entity.subState != INRANGE){
                entity.subStateMachine.changeState(entity.subState);
            }else{
                entity.placeBomb();
            }
        }
    },

    OVERLAPING(){
        @Override
        public void update(com.mygdx.game.Ai.EnemyAgent entity) {
            if(entity.subState != OVERLAPING){
                entity.subStateMachine.changeState(entity.subState);
            }else{
                entity.moveAway();
            }
        }
    },

    OUTOFRANGE(){
        @Override
        public void update(com.mygdx.game.Ai.EnemyAgent entity) {
            if(entity.subState != OUTOFRANGE){
                entity.subStateMachine.changeState(entity.subState);
            }else{
                entity.moveCloser();
            }
        }
    };

    //ko vstopi v novo stanje se mu poišče potrebna pot
    @Override
    public void enter(com.mygdx.game.Ai.EnemyAgent entity) {

    }

    @Override
    public void exit(com.mygdx.game.Ai.EnemyAgent entity) {

    }

    @Override
    public boolean onMessage(EnemyAgent entity, Telegram telegram) {
        return false;
    }
}
