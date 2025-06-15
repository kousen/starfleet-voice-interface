# Starfleet Voice Interface

A JavaFX desktop application that provides a voice-controlled interface for system diagnostics, inspired by Star Trek's computer interface.

## Features

- Voice command recording and transcription using OpenAI's Whisper model
- Natural language processing of commands via OpenAI's GPT models
- System diagnostics capabilities through osquery integration
- Star Trek-inspired user interface

## Prerequisites

- Java 21 or higher
- Gradle
- OpenAI API key
- OsqueryMcpServer (for system diagnostics)

## Setup

1. Clone the repository
2. Set your OpenAI API key as an environment variable:
   ```
   export OPENAI_API_KEY=your_api_key_here
   ```
3. Update the path to OsqueryMcpServer in `application.properties` if needed
4. Build the project:
   ```
   ./gradlew build
   ```

## Running the Application

Use the custom Gradle task to run the application with JavaFX support:

```
./gradlew runFX
```

## Usage

1. Click and hold the comm badge button to start recording
2. Speak your command clearly
3. Release the button to process the command
4. View the system's response in the text area

Example commands:
- "What is the system uptime?"
- "Show me the top running processes"
- "Display system information"
- "List network connections"

## Architecture

The application uses:
- Spring Boot for dependency injection and application configuration
- JavaFX for the user interface
- Spring AI for integration with OpenAI's models
- Model Context Protocol (MCP) for tool integration
- osquery for system diagnostics

## Project Structure

- `StarfleetVoiceInterfaceApplication.java`: Main application class and JavaFX UI setup
- `VoiceController.java`: Handles UI interactions and coordinates services
- `TranscriptionService.java`: Records audio and transcribes it using OpenAI
- `McpClientService.java`: Processes commands using OpenAI and MCP tools

## Potential Improvements

- Add more robust error handling for network and API failures
- Implement a command history feature
- Add unit tests for the UI components
- Create a configuration UI for API keys and settings
- Support for additional voice commands and system diagnostics
- Implement voice synthesis for spoken responses

## License

[MIT License](LICENSE)