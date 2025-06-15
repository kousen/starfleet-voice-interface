package com.kousenit.starfleetvoiceinterface;

import com.kousenit.starfleetvoiceinterface.config.UIConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class StarfleetVoiceInterfaceApplication extends Application {

    private static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        // Initialize Spring Boot context first, before JavaFX
        SpringApplicationBuilder builder = new SpringApplicationBuilder(StarfleetVoiceInterfaceApplication.class);
        builder.web(WebApplicationType.NONE);
        builder.headless(false); // Important for JavaFX
        context = builder.run();
        
        // Now launch JavaFX
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Get Spring beans
            VoiceController voiceController = context.getBean(VoiceController.class);

            // Create UI components
            VBox root = new VBox(UIConstants.SPACING);
            root.setAlignment(Pos.CENTER);
            root.setPadding(new Insets(UIConstants.PADDING));
            root.setStyle("-fx-background-color: " + UIConstants.BACKGROUND_COLOR + ";");

            // Title
            Label titleLabel = new Label("STARFLEET VOICE INTERFACE");
            titleLabel.setStyle(UIConstants.TITLE_STYLE);

            // Status label
            Label statusLabel = new Label("Ready");
            statusLabel.setStyle(UIConstants.STATUS_STYLE);

            // Record button (comm badge style)
            Circle recordButton = new Circle(UIConstants.BUTTON_RADIUS);
            recordButton.setFill(Color.web(UIConstants.BUTTON_BACKGROUND_COLOR));
            recordButton.setStroke(Color.web(UIConstants.BUTTON_STROKE_COLOR));
            recordButton.setStrokeWidth(UIConstants.BUTTON_STROKE_WIDTH);
            recordButton.setCursor(Cursor.HAND);

            StackPane buttonPane = new StackPane();
            buttonPane.getChildren().addAll(recordButton, new Label("COMM"));
            buttonPane.getChildren().get(1).setStyle(UIConstants.BUTTON_LABEL_STYLE);

            // Response area
            TextArea responseArea = new TextArea();
            responseArea.setPrefRowCount(UIConstants.RESPONSE_AREA_ROWS);
            responseArea.setWrapText(true);
            responseArea.setEditable(false);
            responseArea.setStyle(UIConstants.RESPONSE_AREA_STYLE);

            // Transcript label
            Label transcriptLabel = new Label("");
            transcriptLabel.setStyle(UIConstants.TRANSCRIPT_STYLE);
            transcriptLabel.setWrapText(true);

            // Assemble UI
            root.getChildren().addAll(
                    titleLabel,
                    statusLabel,
                    buttonPane,
                    transcriptLabel,
                    responseArea
            );

            // Wire up the controller
            voiceController.initialize(recordButton, statusLabel, transcriptLabel, responseArea);

            // Create scene
            Scene scene = new Scene(root, UIConstants.WINDOW_WIDTH, UIConstants.WINDOW_HEIGHT);
            scene.setFill(Color.web(UIConstants.BACKGROUND_COLOR));

            // Stage setup
            primaryStage.setTitle("Starfleet Computer Interface");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            System.out.println("Error starting JavaFX application: " + e.getMessage());
            Platform.exit();
        }
    }

    @Override
    public void stop() {
        context.close();
        Platform.exit();
    }
}
