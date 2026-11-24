#!/bin/bash
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "停止所有服务..."

# 读取 PID 并停止
for pidfile in .temporal.pid .mokapi.pid .worker.pid; do
    pidfile_path="${SCRIPT_DIR}/${pidfile}"
    if [ -f "$pidfile_path" ]; then
        PID=$(cat "$pidfile_path")
        if kill -0 "$PID" 2>/dev/null; then
            kill "$PID"
            echo "  已停止 ${pidfile#.} (PID: ${PID})"
        else
            echo "  ${pidfile#.} (PID: ${PID}) 已不在运行"
        fi
        rm -f "$pidfile_path"
    fi
done

echo "服务已停止。"
