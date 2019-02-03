import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;


public class Main extends Application {

    private final DataFormat buttonFormat = new DataFormat("MyButton");

    private Button draggingButton;

    @Override
    public void start(Stage primaryStage) {
        try {
            HBox root = new HBox();
            GridPane gp = new GridPane();
            Scene scene = new Scene(root,400,400);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            StackPane row0Col0 = new StackPane();
            StackPane row0Col1 = new StackPane();
            StackPane row1Col1 = new StackPane();
            StackPane row1Col0 = new StackPane();

            row0Col0.setPrefHeight(75);
            row0Col0.setPrefWidth(75);

            row0Col1.setPrefHeight(75);
            row0Col1.setPrefWidth(75);

            row1Col0.setPrefHeight(75);
            row1Col0.setPrefWidth(75);

            row1Col1.setPrefHeight(75);
            row1Col1.setPrefWidth(75);



            row0Col0.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            row0Col1.setBackground(new Background(new BackgroundFill(Color.AQUA, CornerRadii.EMPTY, Insets.EMPTY)));
            row1Col0.setBackground(new Background(new BackgroundFill(Color.SALMON, CornerRadii.EMPTY, Insets.EMPTY)));
            row1Col1.setBackground(new Background(new BackgroundFill(Color.YELLOW, CornerRadii.EMPTY, Insets.EMPTY)));


            gp.add(row0Col0, 0, 0);
            gp.add(row0Col1, 0, 1);
            gp.add(row1Col0, 1, 1);
            gp.add(row1Col1, 1, 0);


            GridPane draggableButtons = new GridPane();


            for(int i = 0; i < 2; i++){
                for(int j = 0; j < 2; j++){
                    Button b = new Button();
                    b.setPrefHeight(35);
                    b.setPrefWidth(35);
                    draggableButtons.add(b, i,j);
                    dragButton(b);
                }
            }

            root.getChildren().addAll(gp, draggableButtons);

            addDropHandling(row0Col0);
            addDropHandling(row0Col1);
            addDropHandling(row1Col0);
            addDropHandling(row1Col1);


            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void dragButton(Button b) {
        b.setOnDragDetected(e -> {
            Dragboard db = b.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(b.snapshot(null, null));
            ClipboardContent cc = new ClipboardContent();
            cc.put(buttonFormat, " ");
            db.setContent(cc);
            draggingButton = b;
        });
    }

    private void addDropHandling(StackPane pane) {
        pane.setOnDragOver(e -> {
            Dragboard db = e.getDragboard();
            if (db.hasContent(buttonFormat) && draggingButton != null) {
                e.acceptTransferModes(TransferMode.MOVE);

            }
        });

        pane.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();

            if (db.hasContent(buttonFormat)) {
                ((Pane)draggingButton.getParent()).getChildren().remove(draggingButton);
                pane.getChildren().add(draggingButton);
                e.setDropCompleted(true);

                draggingButton = null;
            }
        });

    }


    public static void main(String[] args) {
        launch(args);
    }
}