package GaMe;

public class GameState {
        public String player;
        public ChainReactionGame game;

        public GameState(String player, ChainReactionGame game) {
            this.player = player;
            this.game = game;
        }
}