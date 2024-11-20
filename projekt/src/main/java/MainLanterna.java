import Controller.DictionaryController;
import View.DictionaryView;
import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.gui.GUIScreen;
import com.googlecode.lanterna.screen.Screen;

public class MainLanterna {
    public static void main(String[] args) {
        // Inicjalizacja ekranu
        Screen screen = TerminalFacade.createScreen();
        GUIScreen guiScreen = TerminalFacade.createGUIScreen(screen);
        guiScreen.getScreen().startScreen();

        // Tworzymy instancje widoku i kontrolera
        DictionaryView view = new DictionaryView(guiScreen);
        DictionaryController controller = new DictionaryController(view);

        // Wyświetlamy główne menu
        controller.showMainMenu();
    }
}
