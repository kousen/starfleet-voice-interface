package com.kousenit.starfleetvoiceinterface;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.main.web-application-type=none",
        "logging.level.org.springframework.ai.mcp=DEBUG",
        "logging.level.io.modelcontextprotocol=DEBUG"
})
class McpConnectionTest {

    @Autowired(required = false)
    private List<McpSyncClient> mcpClients;

    @Autowired(required = false)
    private SyncMcpToolCallbackProvider toolCallbackProvider;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private OpenAiChatModel chatModel;

    @Test
    void testMcpClientsAreConfigured() {
        assertThat(mcpClients)
                .as("MCP clients should be configured")
                .isNotNull()
                .isNotEmpty();

        System.out.println("Number of MCP clients: " + mcpClients.size());
    }

    @Test
    void testMcpToolsAreAvailable() {
        assertThat(toolCallbackProvider)
                .as("Tool callback provider should exist")
                .isNotNull();

        ToolCallback[] tools = toolCallbackProvider.getToolCallbacks();

        assertThat(tools)
                .as("MCP tools should be available")
                .isNotEmpty();

        // Print available tools
        for (ToolCallback tool : tools) {
            System.out.println("Available tool: " + tool.getToolDefinition().name());
            System.out.println("  Description: " + tool.getToolDefinition().description());
        }

        // Check for osquery tool specifically
        assertThat(tools)
                .extracting(toolCallback -> toolCallback.getToolDefinition().name())
                .contains("spring_ai_mcp_client_osquery_executeOsquery");
    }

    @Test
    void testDirectMcpToolCall() {
        // Get the first MCP client
        McpSyncClient client = mcpClients.getFirst();

        // List available tools
        var toolsResponse = client.listTools();
        assertThat(toolsResponse.tools())
                .as("Should have tools available")
                .isNotEmpty();

        System.out.println("Direct MCP tools:");
        toolsResponse.tools().forEach(tool ->
                System.out.printf("  - %s: %s%n", tool.name(), tool.description()));
    }

    @Test
    void testOsqueryToolExecution() {
        // Test calling the osquery tool directly
        McpSyncClient client = mcpClients.getFirst();

        var result = client.callTool(
                new McpSchema.CallToolRequest(
                        "executeOsquery",
                        Map.of("sql", "SELECT days, hours, minutes FROM uptime")
                )
        );

        assertThat(result.content())
                .as("Should return uptime information")
                .isNotNull();

        System.out.println("Osquery result: " + result.content());
    }

    @Test
    void testChatClientWithMcpTools() {
        ChatClient chatClient = ChatClient.create(chatModel);

        String response = chatClient.prompt()
                .system("""
                        You are a system diagnostic assistant.
                        Use the spring_ai_mcp_client_osquery_executeOsquery tool to answer questions.
                        For uptime, use this query: SELECT * FROM system_info
                        """)
                .user("What is the system uptime?")
                .toolCallbacks(toolCallbackProvider.getToolCallbacks())
                .call()
                .content();

        assertThat(response)
                .as("Should get a response about uptime")
                .isNotBlank()
                .doesNotContainIgnoringCase("unable to retrieve");

        System.out.println("Chat response: " + response);
    }
}