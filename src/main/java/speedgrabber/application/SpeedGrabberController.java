package speedgrabber.application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import speedgrabber.ApiDataGrabber;
import speedgrabber.SGUtils;
import speedgrabber.records.Category;
import speedgrabber.records.Game;
import speedgrabber.records.Leaderboard;
import speedgrabber.records.Run;

import javax.naming.NameNotFoundException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@SuppressWarnings({"unused"})
public class SpeedGrabberController {

    @FXML
    private Label gameLabel;
    @FXML
    private TextField gameSearchField;
    @FXML
    private Button gameSearchButton;

    @FXML
    private ChoiceBox<Category> categoryDropdown;

    @FXML
    private TextArea leaderboardArea;

    public void searchGame() {
        try {
            if (gameSearchField.getText().isEmpty())
                throw new NameNotFoundException("Please enter a game abbreviation or ID.");

            categoryDropdown.getItems().clear();

            leaderboardArea.setText("");
            gameSearchField.setDisable(true);

            Game game = ApiDataGrabber.getGame(gameSearchField.getText());
            gameLabel.setText(String.format("Game found: %s", game.name()));

            List<Category> categories = ApiDataGrabber.getListOfCategories(game);
            for (Category category : categories)
                categoryDropdown.getItems().add(category);
        }
        catch (FileNotFoundException e) {
            showErrorDialog(new FileNotFoundException(gameSearchField.getText()));
        }
        catch (Exception e) {
            showErrorDialog(e);
        }
        finally {
            gameSearchField.setText("");
            gameSearchField.setDisable(false);
        }
    }
    public void showCategoryLeaderboard() {
        try {
            if (categoryDropdown.getValue() == null)
                throw new NullPointerException("Category dropdown is empty. Please select a category first.");

            Leaderboard leaderboard = ApiDataGrabber.getLeaderboard(categoryDropdown.getValue(), 20);
            List<Run> leaderboardRuns = ApiDataGrabber.getListOfRuns(leaderboard);

            StringBuilder leaderboardBuilder = new StringBuilder();
            for (Run run : leaderboardRuns)
                leaderboardBuilder.append(String.format("%s%n", run));

            leaderboardArea.setText(leaderboardBuilder.toString());
        }
        catch (Exception e) {
            showErrorDialog(e);
        }
    }

    private void showErrorDialog(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        String exceptionName = e.getClass().getSimpleName();
        String exceptionDetails = String.format(
                "Whoops! We couldn't find the game \"%s\"%nPress 'Search' to look for it online.",
                e.getMessage()
        );
        ObservableList<ButtonType> buttonTypes = alert.getButtonTypes();

        e.printStackTrace(System.err);

        if (e instanceof FileNotFoundException) {
            buttonTypes.add(new ButtonType("Search", ButtonBar.ButtonData.HELP));
        }

        alert.setTitle("Error");
        alert.setHeaderText(exceptionName);
        alert.setContentText(exceptionDetails);
        alert.showAndWait();

        try {
            if (alert.getResult().equals(buttonTypes.get(1)))
                openLink(URI.create(String.format(
                        "https://www.speedrun.com/search?q=%s",
                        SGUtils.encodeForSearchResults(e.getMessage())
                )));
        } catch (IOException uriError) {
            showErrorDialog(new IOException("There was a problem opening the link."));
        }
    }

    private void openLink(URI uri) throws IOException {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(uri);
        }
    }
}
