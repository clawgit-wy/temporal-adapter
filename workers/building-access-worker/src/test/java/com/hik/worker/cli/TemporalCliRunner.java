package com.hik.worker.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hik.worker.model.HkSyncRequest;
import com.hik.worker.model.HkSyncResult;
import com.hik.worker.workflow.HikSyncWorkflow;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsRequest;
import io.temporal.api.workflowservice.v1.ListWorkflowExecutionsResponse;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowOptions;
import io.temporal.client.WorkflowStub;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Temporal CLI 替代工具：通过 Java SDK 直接操作工作流
 * 支持 start / query / describe / list 命令
 *
 * 使用方式:
 *   mvn exec:java -Dexec.mainClass="com.hik.worker.cli.TemporalCliRunner" -Dexec.args="<command> [args...]"
 */
public class TemporalCliRunner {

    private static final String DEFAULT_TEMPORAL_ADDRESS = "127.0.0.1:7233";
    private static final String DEFAULT_TASK_QUEUE = "HIK_SYNC_TASK_QUEUE";
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }

        String command = args[0].toLowerCase();
        String temporalAddress = System.getenv().getOrDefault("TEMPORAL_ADDRESS", DEFAULT_TEMPORAL_ADDRESS);

        WorkflowServiceStubs service = null;
        try {
            service = WorkflowServiceStubs.newConnectedServiceStubs(
                    WorkflowServiceStubsOptions.newBuilder().setTarget(temporalAddress).build(),
                    Duration.ofSeconds(10));

            WorkflowClient client = WorkflowClient.newInstance(service);

            switch (command) {
                case "start":
                    doStart(client, args);
                    break;
                case "query":
                    doQuery(client, args);
                    break;
                case "describe":
                    doDescribe(service, args);
                    break;
                case "list":
                    doList(service, args);
                    break;
                default:
                    System.err.println("未知命令: " + command);
                    printUsage();
            }
        } catch (Exception e) {
            System.err.println("执行失败: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (service != null) {
                service.shutdown();
            }
        }
    }

    private static void doStart(WorkflowClient client, String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("用法: start <workflowId> [选项...]");
            return;
        }
        String workflowId = args[1];
        Map<String, String> opts = parseOpts(args, 2);

        HkSyncRequest.HkSyncRequestBuilder builder = HkSyncRequest.builder();
        builder.syncType(opts.getOrDefault("syncType", "INCREMENTAL"));
        builder.timeWindowMinutes(Integer.parseInt(opts.getOrDefault("timeWindowMinutes", "123")));
        builder.batchSize(Integer.parseInt(opts.getOrDefault("batchSize", "900")));
        if (opts.containsKey("startTime")) {
            builder.customStartTime(opts.get("startTime"));
        }
        if (opts.containsKey("endTime")) {
            builder.customEndTime(opts.get("endTime"));
        }

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(DEFAULT_TASK_QUEUE)
                .setWorkflowId(workflowId)
                .build();

        HikSyncWorkflow workflow = client.newWorkflowStub(HikSyncWorkflow.class, options);

        HkSyncRequest request = builder.build();
        System.out.println(">>> 启动海康数据同步工作流...");
        System.out.println("    WorkflowId: " + workflowId);
        System.out.println("    TaskQueue:  " + DEFAULT_TASK_QUEUE);
        System.out.println("    参数: " + mapper.writeValueAsString(request));

        HkSyncResult result = workflow.syncToHik(request);

        System.out.println("\n>>> 同步结果:");
        System.out.println("    状态:        " + result.getStatus());
        System.out.println("    同步类型:    " + result.getSyncType());
        System.out.println("    黑名单数:    " + result.getBlackListCount());
        System.out.println("    员工数:      " + result.getEmployeeCount());
        System.out.println("    备案数:      " + result.getRegisterCount());
        System.out.println("    访客数:      " + result.getVisitorCount());
        System.out.println("    去重后总数:  " + result.getTotalAfterDedup());
        System.out.println("    成功写入数:  " + result.getSuccessCount());
        System.out.println("    失败数:      " + result.getFailCount());
        if (result.getErrorMessage() != null) {
            System.out.println("    错误信息:    " + result.getErrorMessage());
        }
    }

    private static void doQuery(WorkflowClient client, String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("用法: query <workflowId> [queryMethod]");
            return;
        }
        String workflowId = args[1];
        String queryMethod = args.length > 2 ? args[2] : "getSyncProgress";

        HikSyncWorkflow workflow = client.newWorkflowStub(HikSyncWorkflow.class,
                WorkflowOptions.newBuilder().setWorkflowId(workflowId).build());

        Object result;
        switch (queryMethod) {
            case "getSyncProgress":
                result = workflow.getSyncProgress();
                break;
            default:
                result = WorkflowStub.fromTyped(workflow).query(queryMethod, Object.class);
        }

        System.out.println(">>> 查询结果 [" + queryMethod + "]: " + result);
    }

    private static void doDescribe(WorkflowServiceStubs service, String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("用法: describe <workflowId>");
            return;
        }
        String workflowId = args[1];

        DescribeWorkflowExecutionRequest request = DescribeWorkflowExecutionRequest.newBuilder()
                .setNamespace("default")
                .setExecution(WorkflowExecution.newBuilder().setWorkflowId(workflowId).build())
                .build();

        io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionResponse resp =
                service.blockingStub().describeWorkflowExecution(request);

        System.out.println(">>> 工作流详情:");
        System.out.println("    WorkflowId:  " + resp.getWorkflowExecutionInfo().getExecution().getWorkflowId());
        System.out.println("    RunId:       " + resp.getWorkflowExecutionInfo().getExecution().getRunId());
        System.out.println("    Status:      " + resp.getWorkflowExecutionInfo().getStatus().name());
        System.out.println("    StartTime:   " + resp.getWorkflowExecutionInfo().getStartTime());
        System.out.println("    CloseTime:   " + resp.getWorkflowExecutionInfo().getCloseTime());
        System.out.println("    HistoryLen:  " + resp.getWorkflowExecutionInfo().getHistoryLength());
        System.out.println("    TaskQueue:   " + resp.getWorkflowExecutionInfo().getTaskQueue());

        String status = resp.getWorkflowExecutionInfo().getStatus().name();
        if ("COMPLETED".equals(status) || "FAILED".equals(status) || "CANCELED".equals(status)) {
            System.out.println("    工作流已结束，使用 result 命令获取结果");
        }
    }

    private static void doList(WorkflowServiceStubs service, String[] args) throws Exception {
        Map<String, String> opts = parseOpts(args, 1);
        String workflowType = opts.getOrDefault("workflowType", "HikSyncWorkflow");
        String statusFilter = opts.getOrDefault("status", "");

        StringBuilder query = new StringBuilder();
        query.append("WorkflowType = '").append(workflowType).append("'");
        if (!statusFilter.isEmpty()) {
            query.append(" AND WorkflowStatus = '").append(statusFilter).append("'");
        }
        query.append(" ORDER BY StartTime DESC");

        System.out.println(">>> 查询工作流列表 (Query: " + query + ")");

        ListWorkflowExecutionsRequest listRequest = ListWorkflowExecutionsRequest.newBuilder()
                .setNamespace("default")
                .setQuery(query.toString())
                .build();

        ListWorkflowExecutionsResponse response = service.blockingStub().listWorkflowExecutions(listRequest);

        int count = 0;
        for (io.temporal.api.workflow.v1.WorkflowExecutionInfo exec : response.getExecutionsList()) {
            System.out.printf("    [%d] workflowId=%s, runId=%s, status=%s, start=%s%n",
                    count,
                    exec.getExecution().getWorkflowId(),
                    exec.getExecution().getRunId(),
                    exec.getStatus().name(),
                    exec.getStartTime());
            count++;
        }
        System.out.println("    共 " + count + " 条记录");
    }

    private static Map<String, String> parseOpts(String[] args, int startIndex) {
        Map<String, String> opts = new HashMap<>();
        for (int i = startIndex; i < args.length - 1; i += 2) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                String value = args[i + 1];
                opts.put(key, value);
            }
        }
        return opts;
    }

    private static void printUsage() {
        System.out.println("Temporal CLI Runner - 海康数据同步工作流测试工具");
        System.out.println();
        System.out.println("用法:");
        System.out.println("  start <workflowId> [--syncType INCREMENTAL|FULL] [--timeWindowMinutes N] [--startTime ...] [--endTime ...] [--batchSize N]");
        System.out.println("    启动海康数据同步工作流（同步等待结果）");
        System.out.println();
        System.out.println("  query <workflowId> [queryMethod]");
        System.out.println("    查询工作流进度，默认 queryMethod=getSyncProgress");
        System.out.println();
        System.out.println("  describe <workflowId>");
        System.out.println("    查看工作流详情（状态、开始时间、历史长度等）");
        System.out.println();
        System.out.println("  list [--workflowType HikSyncWorkflow] [--status RUNNING|COMPLETED|FAILED]");
        System.out.println("    列出工作流执行记录");
        System.out.println();
        System.out.println("环境变量:");
        System.out.println("  TEMPORAL_ADDRESS  Temporal Server 地址 (默认: 127.0.0.1:7233)");
        System.out.println();
        System.out.println("示例:");
        System.out.println("  # 启动增量同步");
        System.out.println("  mvn exec:java -Dexec.mainClass=com.hik.worker.cli.TemporalCliRunner -Dexec.args=\"start hik-sync-001 --syncType INCREMENTAL --timeWindowMinutes 123\"");
        System.out.println();
        System.out.println("  # 查询进度");
        System.out.println("  mvn exec:java -Dexec.mainClass=com.hik.worker.cli.TemporalCliRunner -Dexec.args=\"query hik-sync-001\"");
        System.out.println();
        System.out.println("  # 查看详情");
        System.out.println("  mvn exec:java -Dexec.mainClass=com.hik.worker.cli.TemporalCliRunner -Dexec.args=\"describe hik-sync-001\"");
        System.out.println();
        System.out.println("  # 列出工作流");
        System.out.println("  mvn exec:java -Dexec.mainClass=com.hik.worker.cli.TemporalCliRunner -Dexec.args=\"list --workflowType HikSyncWorkflow\"");
    }
}
