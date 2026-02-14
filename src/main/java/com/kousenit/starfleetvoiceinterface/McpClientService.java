package com.kousenit.starfleetvoiceinterface;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.function.Consumer;

@Service
public class McpClientService {

    private final ChatClient chatClient;

    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    public McpClientService(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider toolCallbackProvider) {
        this.chatClient = ChatClient.create(chatModel);
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public void processCommand(String command, Consumer<String> onChunk,
                               Consumer<String> onToolCall,
                               Runnable onComplete, Consumer<Throwable> onError) {
        ToolCallback[] callbacks = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .map(cb -> wrapWithNotification(cb, onToolCall))
                .toArray(ToolCallback[]::new);

        chatClient.prompt()
                .system("""
                        You are a system diagnostic assistant for macOS.
                        Use the spring_ai_mcp_client_osquery_executeOsquery tool to answer questions.

                        Common queries for macOS:
                        - System uptime: SELECT days, hours, minutes FROM uptime
                        - System info: SELECT hostname, cpu_brand, physical_memory FROM system_info
                        - Running processes: SELECT name, pid, resident_size FROM processes ORDER BY resident_size DESC LIMIT 10
                        - Network connections: SELECT DISTINCT process.name, listening.port FROM listening_ports listening JOIN processes process ON listening.pid = process.pid WHERE listening.port != 0

                        Format responses in a clear, conversational manner.
                        """)
                .user(command)
                .toolCallbacks(callbacks)
                .stream()
                .content()
                .doOnNext(onChunk)
                .doOnComplete(onComplete)
                .doOnError(onError)
                .subscribe();
    }

    private ToolCallback wrapWithNotification(ToolCallback delegate, Consumer<String> onToolCall) {
        return new ToolCallback() {
            @Override
            public ToolDefinition getToolDefinition() {
                return delegate.getToolDefinition();
            }

            @Override
            public ToolMetadata getToolMetadata() {
                return delegate.getToolMetadata();
            }

            @Override
            public String call(String toolInput) {
                onToolCall.accept(delegate.getToolDefinition().name());
                return delegate.call(toolInput);
            }
        };
    }
}
