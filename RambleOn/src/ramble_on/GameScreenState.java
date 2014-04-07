package ramble_on;

/**
 * This enum stores the screens used by our Game application.
 *
 * WELCOME_SCREEN refers to when the application has begun, but the user has to
 * click to start the game.
 *
 * ACCOUNT_CREATION_SCREEN refers to the screen that the user wants to create an
 * account.
 *
 * CURRENT_ACCOUNT_SCREEN refers to the screen that the user has either selected
 * an existing account or created a new one and allow users to view stats of the
 * account.
 *
 * ACCOUNT_SCREEN refers to the screen that the screen that allows the user to
 * select or create an account.
 *
 *
 * @author Leixiang Wu
 * @version 1.0
 */
public enum GameScreenState {

    WELCOME_SCREEN, ACCOUNT_CREATION_SCREEN, CURRENT_ACCOUNT_SCREEN, ACCOUNT_SCREEN,GAME_PLAY_SCREEN,GAME_END_SCREEN
};
