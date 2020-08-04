package com.mygdx.game.MainGame;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum EnemySubState implements State<EnemyAgent> {
    NONE(){
        @Override
        public void update(EnemyAgent entity) {
            if(entity.subState != NONE){
                entity.subStateMachine.changeState(entity.subState);
            }
        }
    },
    INRANGE(){
        @Override
        public void update(EnemyAgent entity) {
            if(entity.subState != INRANGE){
                entity.subStateMachine.changeState(entity.subState);
            }else{
                entity.placeBomb();
            }
        }
    },

    OVERLAPING(){
        @Override
        public void update(EnemyAgent entity) {
            if(entity.subState != OVERLAPING){
                entity.subStateMachine.changeState(entity.subState);
            }else{
                entity.moveAway();
            }
        }
    },

    OUTOFRANGE(){
        @Override
        public void update(EnemyAgent entity) {
            if(entity.subState != OUTOFRANGE){
                entity.subStateMachine.changeState(entity.subState);
            }else{
                entity.moveCloser();
            }
        }
    };

    //ko vstopi v novo stanje se mu poišče potrebna pot
    @Override
    public void enter(EnemyAgent entity) {

    }

    @Override
    public void exit(EnemyAgent entity) {

    }

    @Override
    public boolean onMessage(EnemyAgent entity, Telegram telegram) {
        return false;
    }
}
