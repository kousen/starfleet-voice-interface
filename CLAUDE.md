# Claude Code Memory

This file contains information to help Claude Code understand this project and provide better assistance.

## Project Overview

**Starfleet Voice Interface** - A Star Trek-inspired voice-controlled computer interface that allows users to issue voice commands for system diagnostics.

### Architecture
- **Frontend**: JavaFX 21 with LCARS-inspired UI
- **Backend**: Spring Boot 3.5 with Spring AI
- **Voice Recognition**: OpenAI Whisper API
- **System Queries**: MCP (Model Context Protocol) client
- **Audio Playback**: jlayer for MP3 files

## Key Components

### Core Services
- `VoiceController.java` - Main UI controller, handles voice recording/playback cycle
- `TranscriptionService.java` - OpenAI Whisper integration for speech-to-text
- `McpClientService.java` - Connects to external MCP server for system diagnostics
- `AudioPlayerService.java` - Cross-platform audio playback using jlayer

### Configuration
- `UIConstants.java` - All UI styling, colors, dimensions, animations
- `application.properties` - OpenAI API key, MCP server configuration

## Development Commands

### Build and Run
```bash
./gradlew runFX          # Run the JavaFX application
./gradlew build          # Build the project
./gradlew test           # Run tests
```

### Dependencies
- Java 21 required
- JavaFX modules: controls, fxml, media
- Spring AI with OpenAI integration
- jlayer for MP3 playback

## Recent Changes

### Audio System Evolution
1. **Started with**: macOS-only `say` command (Zarvox voice)
2. **Moved to**: Cross-platform `AudioPlayerService` with jlayer
3. **Current**: Plays `src/main/resources/sounds/working.mp3` after voice transcription

### UI Issue Fixed
- **Problem**: Transcription text wrapping caused closing quotes to appear on new line
- **Root Cause**: OpenAI transcription API returning text with trailing whitespace/newlines
- **Solution**: Added `.trim()` to transcription result in `TranscriptionService.java:136`

### Spring Boot Lifecycle
- **Problem**: "Timeout while waiting for app reactivation" warning
- **Solution**: Moved Spring context initialization from `init()` to `main()` method
- **Location**: `StarfleetVoiceInterfaceApplication.java:27-35`

## Configuration Notes

### OpenAI Setup
```properties
spring.ai.openai.api-key=your-key-here
spring.ai.openai.audio.transcription.options.model=whisper-1
spring.ai.openai.audio.transcription.options.language=en
spring.ai.openai.audio.transcription.options.temperature=0.0
```

### MCP Server Connection
```properties
spring.ai.mcp.client.stdio.connections.osquery.command=java
spring.ai.mcp.client.stdio.connections.osquery.args=-jar,/path/to/OsqueryMcpServer.jar
```

### Audio Files
- Location: `src/main/resources/sounds/working.mp3`
- Generated using OpenAI TTS with "Rachel" voice (ElevenLabs)
- Cross-platform playback via jlayer Player class

## Known Issues

### Voice Quality
- Current "working.mp3" uses Rachel voice from ElevenLabs
- User has custom ElevenLabs voice that would be better
- Future: Replace with custom voice for more authentic computer sound

### Platform Compatibility
- Audio playback is now cross-platform (jlayer)
- All macOS-specific code removed
- JavaFX Media module included but not currently used

## Future Enhancements

### Potential Additions
1. **Response Reading**: Use Spring AI TTS to read diagnostic results aloud
2. **Better Voice**: Replace working.mp3 with user's custom ElevenLabs voice
3. **Visual Feedback**: Add spinner or progress indicator during processing
4. **Command History**: Store and replay previous commands
5. **Multiple Voices**: Allow voice selection for different feedback types

### Technical Considerations
- Spring AI TTS integration already researched (see OpenAI speech docs)
- ElevenLabs API could be integrated for premium voices
- JavaFX Media could replace jlayer for more features
- MCP server path should be configurable via environment variable

## Code Patterns

### Async Operations
- Voice transcription uses `CompletableFuture.supplyAsync()`
- Audio playback runs asynchronously to avoid UI blocking
- All UI updates wrapped in `Platform.runLater()`

### Error Handling
- Audio failures log warnings but don't interrupt main workflow
- Transcription errors display in response area
- Graceful degradation when audio files missing

### Spring Integration
- All services are Spring components with dependency injection
- Configuration via application.properties
- Clean separation of concerns between UI and business logic

## Testing Notes

### Manual Testing
- Microphone permissions required on first run
- Test with various command lengths to verify text wrapping fix
- Audio playback should be immediate and clear
- MCP server must be running for diagnostic commands to work

### Known Working Commands
- "Computer, run a Level 1 diagnostic"
- "Computer, what's the fan temperature?"
- "Computer, show system status"

## File Structure
```
src/main/java/com/kousenit/starfleetvoiceinterface/
├── StarfleetVoiceInterfaceApplication.java
├── VoiceController.java
├── TranscriptionService.java
├── McpClientService.java
├── AudioPlayerService.java
└── config/UIConstants.java

src/main/resources/
├── application.properties
└── sounds/working.mp3
```