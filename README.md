# Starfleet Voice Interface

A Star Trek-inspired voice-controlled computer interface built with JavaFX, Spring Boot, and OpenAI. Issue voice commands to query system diagnostics and receive real-time feedback in an authentic starship computer style.

![Starfleet Voice Interface](docs/screenshot.png)

## Features

- **Voice Recognition**: Powered by OpenAI Whisper for accurate speech transcription
- **System Diagnostics**: Query system information via MCP (Model Context Protocol) client
- **Audio Feedback**: Computer voice confirms command receipt
- **Star Trek UI**: Authentic LCARS-inspired interface with "COMM" button
- **Cross-Platform**: Runs on macOS, Windows, and Linux

## Prerequisites

- Java 21 or higher
- OpenAI API key
- MCP Server (for system queries)

## Setup

### 1. Clone the Repository

```bash
git clone https://github.com/kousen/starfleet-voice-interface.git
cd starfleet-voice-interface
```

### 2. Configure OpenAI API

Create an `application.properties` file in `src/main/resources/` or set environment variables:

```properties
spring.ai.openai.api-key=your-openai-api-key-here
spring.ai.openai.audio.transcription.options.model=whisper-1
spring.ai.openai.audio.transcription.options.language=en
```

Or set as environment variable:
```bash
export OPENAI_API_KEY=your-openai-api-key-here
```

### 3. MCP Server Setup

The application connects to an external MCP server for system diagnostics. Update the MCP server path in `application.properties`:

```properties
spring.ai.mcp.client.stdio.connections.osquery.command=java
spring.ai.mcp.client.stdio.connections.osquery.args=-jar,/path/to/your/OsqueryMcpServer.jar
```

### 4. Build and Run

Using Gradle:
```bash
./gradlew runFX
```

Or build and run manually:
```bash
./gradlew build
java -jar build/libs/starfleet-voice-interface-0.0.1-SNAPSHOT.jar
```

## Usage

1. **Launch the application** - The interface displays with a central "COMM" button
2. **Press and hold the COMM button** - Begin speaking your command
3. **Release the button** - Voice processing begins
4. **Hear confirmation** - Computer plays "Working" sound
5. **View results** - System diagnostic information appears in the response area

### Example Commands

- "Computer, run a Level 1 diagnostic"
- "Computer, what's the fan temperature?"
- "Computer, show system status"
- "Computer, check memory usage"

## Project Structure

```
src/main/java/com/kousenit/starfleetvoiceinterface/
├── StarfleetVoiceInterfaceApplication.java  # Main application class
├── VoiceController.java                     # UI event handling and voice processing
├── TranscriptionService.java                # OpenAI Whisper integration
├── McpClientService.java                    # MCP client for system queries
├── AudioPlayerService.java                  # Audio playback using jlayer
└── config/
    └── UIConstants.java                     # UI styling and configuration

src/main/resources/
├── application.properties                   # Configuration
└── sounds/
    └── working.mp3                         # Audio feedback file
```

## Technologies Used

- **Java 21** - Primary programming language
- **JavaFX 21** - User interface framework
- **Spring Boot 3.5** - Application framework
- **Spring AI** - OpenAI integration
- **OpenAI Whisper** - Speech-to-text transcription
- **MCP (Model Context Protocol)** - System data queries
- **jlayer** - MP3 audio playback
- **Gradle** - Build system

## Configuration Options

### UI Customization

Modify `UIConstants.java` to customize the interface:
- Window dimensions
- Colors and styling
- Animation timing
- Button sizes

### Audio Options

Replace `src/main/resources/sounds/working.mp3` with your preferred audio file:
- Supports MP3 format via jlayer
- Async playback prevents UI blocking
- Graceful fallback if file is missing

### Voice Recognition

Adjust transcription settings in `application.properties`:
```properties
# Transcription model (whisper-1 recommended)
spring.ai.openai.audio.transcription.options.model=whisper-1

# Language setting
spring.ai.openai.audio.transcription.options.language=en

# Temperature (0.0 = deterministic, 1.0 = creative)
spring.ai.openai.audio.transcription.options.temperature=0.0
```

## Development

### Running in Development

```bash
./gradlew runFX
```

### Running Tests

```bash
./gradlew test
```

### Building Distribution

```bash
./gradlew build
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Troubleshooting

### Common Issues

**"Audio file not found"**
- Ensure `working.mp3` exists in `src/main/resources/sounds/`
- Check file permissions

**"OpenAI API Error"**
- Verify your API key is valid and has sufficient credits
- Check network connectivity

**"MCP Connection Failed"**
- Ensure the MCP server is running and accessible
- Verify the server path in `application.properties`

**"Microphone not supported"**
- Check system audio permissions
- Ensure microphone is connected and functional

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by Star Trek's LCARS computer interface
- Built with Spring AI and OpenAI technologies
- Uses MCP for system integration
- Audio processing via jlayer library

---

**Live long and prosper!** 🖖