package com.kousenit.starfleetvoiceinterface.config;

/**
 * Constants for UI elements to improve maintainability and consistency.
 */
public class UIConstants {
    // Colors
    public static final String BACKGROUND_COLOR = "#000000";
    public static final String TITLE_COLOR = "#FF9900";
    public static final String STATUS_READY_COLOR = "#99CCFF";
    public static final String STATUS_RECORDING_COLOR = "#FF0000";
    public static final String STATUS_PROCESSING_COLOR = "#FFFF00";
    public static final String TRANSCRIPT_COLOR = "#66AA66";
    public static final String BUTTON_BACKGROUND_COLOR = "#1a1a1a";
    public static final String BUTTON_STROKE_COLOR = "#FF9900";
    
    // Sizes
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 800;
    public static final int BUTTON_RADIUS = 80;
    public static final int PADDING = 30;
    public static final int SPACING = 20;
    
    // Styles
    public static final String TITLE_STYLE = "-fx-font-size: 24px; -fx-text-fill: " + TITLE_COLOR + "; -fx-font-family: 'Arial Black';";
    public static final String STATUS_STYLE = "-fx-font-size: 16px; -fx-text-fill: " + STATUS_READY_COLOR + ";";
    public static final String TRANSCRIPT_STYLE = "-fx-font-size: 14px; -fx-text-fill: " + TRANSCRIPT_COLOR + ";";
    public static final String BUTTON_LABEL_STYLE = "-fx-text-fill: " + BUTTON_STROKE_COLOR + "; -fx-font-weight: bold;";
    public static final String RESPONSE_AREA_STYLE = """
            -fx-control-inner-background: #1a1a1a;
            -fx-text-fill: #99CCFF;
            -fx-font-family: 'Courier New';
            -fx-font-size: 14px;
            """;
    
    // Animation durations
    public static final double PULSE_ANIMATION_DURATION = 0.5; // seconds
    
    // Button stroke widths
    public static final int BUTTON_STROKE_WIDTH = 3;
    public static final int BUTTON_STROKE_WIDTH_ACTIVE = 5;
    
    // Text area properties
    public static final int RESPONSE_AREA_ROWS = 15;
}