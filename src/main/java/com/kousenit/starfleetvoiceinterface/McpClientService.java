package com.kousenit.starfleetvoiceinterface;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class McpClientService {

    private final ChatClient chatClient;

    private final SyncMcpToolCallbackProvider toolCallbackProvider;

    public McpClientService(OpenAiChatModel chatModel, SyncMcpToolCallbackProvider toolCallbackProvider) {
        this.chatClient = ChatClient.create(chatModel);
        this.toolCallbackProvider = toolCallbackProvider;
    }

    public CompletableFuture<String> processCommand(String command) {
        return CompletableFuture.supplyAsync(
                () -> chatClient.prompt()
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
                        .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                        .call()
                        .content());
    }
}