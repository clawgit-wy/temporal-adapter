<template>
  <div class="video-player-container">
    <div class="controls" v-if="playMode === '1'">
      <div class="date-range">
        <div class="date-item">
          <span class="date-label">开始时间：</span>
          <a-date-picker
            :value="props.urls[currentWindowIndex]?.startTime"
            show-time
            type="dateTime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="请选择开始时间"
            :style="{ width: '220px' }"
            @change="handleStartTimeChange"
          />
        </div>
        <div class="date-item">
          <span class="date-label">结束时间：</span>
          <a-date-picker
            :value="props.urls[currentWindowIndex]?.endTime"
            show-time
            type="dateTime"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DDTHH:mm:ss"
            placeholder="请选择结束时间"
            :style="{ width: '220px' }"
            @change="handleEndTimeChange"
          />
        </div>
      </div>
      <div class="preview-buttons">
        <a-space>
          <a-button type="primary" @click="playbackStart()">开始回放</a-button>
          <!-- <a-button type="primary" @click="reversekStart">开始倒放</a-button> -->
          <a-button type="primary" @click="playbackPause">暂停</a-button>
          <a-button type="primary" @click="playbackResume">恢复</a-button>
          <a-button type="primary" @click="playbackSlow">慢放</a-button>
          <a-button type="primary" @click="playbackFast">快放</a-button>
          <!-- 播放速度显示 -->
          <div class="playback-speed">
            <div class="speed-indicator">
              <span class="speed-label">当前播放速度</span>
              <div class="speed-value">{{ props.urls[currentWindowIndex]?.playbackSpeed }}x</div>
            </div>
          </div>
        </a-space>
      </div>
    </div>
    <div id="player"></div>
  </div>
</template>

<script setup>
  import { ref, onMounted, onBeforeUnmount, reactive, watch } from 'vue';
  import dayjs from 'dayjs';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  // 表单状态
  const formState = reactive({
    startTime: null,
    endTime: null,
  });

  // 用于debounce的定时器
  let resizeTimer = null;

  // 组件属性定义
  const props = defineProps({
    // 播放参数配置
    splitNum: {
      type: Number,
      default: 1,
    },
    // 播放模式，0为普通模式(mse)，1为高级模式(decoder)
    mode: {
      type: Number,
      default: 1,
    },
    urls: {
      type: Array,
      default: () => [
        {
          realplay: '',
          talk: '',
          playback: '',
          startTime: '',
          endTime: '',
        },
      ],
    },
    playMode: {
      type: String,
      default: '1',
    },
  });

  // 组件事件
  const emit = defineEmits([]);

  // 播放器实例
  const player = ref(null);
  const currentWindowIndex = ref(0);
  const playbackSpeed = ref(1);
  // 初始化播放器
  const initPlayer = () => {
    player.value = new JSPlugin({
      szId: 'player', //父窗口id，需要英文字母开头 必填
      szBasePath: './', // 必填,与h5player.min.js的引用路径一致
      iMaxSplit: 4,
      iCurrentSplit: props.splitNum,
      openDebug: false,
      mseWorkerEnable: true, //是否开启多线程解码，分辨率大于1080P建议开启，否则可能卡顿
      bSupporDoubleClickFull: true, //是否支持双击全屏，true-双击是全屏；false-双击无响应
      oStyle: {
        borderSelect: '#FFCC00',
      },
    });

    // 设置播放事件回调
    player.value.JS_SetWindowControlCallback({
      windowEventSelect: (iWndIndex) => {
        console.log('窗口选中：', iWndIndex);
        currentWindowIndex.value = iWndIndex;
      },
      pluginErrorHandler: (iWndIndex, iErrorCode, oError) => {
        console.log('播放器错误：', iWndIndex, iErrorCode, oError);
      },
      windowFullCreenChange: (bFull) => {
        console.log('全屏状态变更：', bFull);
      },
      firstFrameDisplay: (iWndIndex, iWidth, iHeight) => {
        console.log('首帧显示：', iWndIndex, iWidth, iHeight);
      },
      performanceLack: (iWndIndex) => {
        console.log('性能不足：', iWndIndex);
      },
      StreamEnd: (iWndIndex) => {
        console.log('流结束：', iWndIndex);
      },
    });

    // 监听窗口大小变化
    window.addEventListener('resize', handleResize);
  };
  // 修改setFullWidthSquare函数，确保不干扰四方格布局
  const setFullWidthSquare = () => {
    // 调整播放器大小
    if (player.value) {
      // 获取容器元素
      const container = document.querySelector('.video-player-container');
      if (!container) return;

      // 获取容器宽度
      const containerWidth = container.clientWidth;

      // 计算16:9比例下的高度
      let playerHeight = (containerWidth * 9) / 16;

      // 计算视窗高度
      const windowHeight = window.innerHeight;

      // 获取控制区域高度（如果存在）
      const controlsHeight = props.playMode === '1' ? document.querySelector('.controls')?.offsetHeight || 0 : 0;

      // 获取容器到视窗顶部的距离
      const containerTop = container.getBoundingClientRect().top;

      // 计算可用高度（视窗高度减去容器顶部距离和底部预留空间）
      const availableHeight = windowHeight - containerTop - 20; // 底部预留20px空间

      // 计算最大可用播放器高度（可用高度减去控制区域高度）
      const maxPlayerHeight = availableHeight - controlsHeight;

      // 如果16:9比例的高度超过了可用高度，则调整宽度和高度以保持比例
      if (playerHeight > maxPlayerHeight) {
        playerHeight = maxPlayerHeight;
        // 按照16:9的比例重新计算宽度
        const playerWidth = (playerHeight * 16) / 9;
        // 设置播放器尺寸，居中显示
        const playerElement = document.getElementById('player');
        if (playerElement) {
          playerElement.style.height = `${playerHeight}px`;
          playerElement.style.width = `${playerWidth}px`;
          playerElement.style.margin = '0 auto'; // 居中显示
        }
      } else {
        // 如果高度合适，使用计算出的16:9高度
        const playerElement = document.getElementById('player');
        if (playerElement) {
          playerElement.style.height = `${playerHeight}px`;
          playerElement.style.width = '100%';
        }
      }

      // 调用播放器的resize方法重新调整尺寸
      player.value.JS_Resize();

      // 确认splitNum被正确应用
      if (props.splitNum > 1) {
        player.value.JS_ArrangeWindow(props.splitNum);
      }

      console.log(
        `调整播放器大小: 高度=${playerHeight}px, 宽高比=16:9, 控制区: ${controlsHeight}px, 模式: ${props.playMode}, 分屏数: ${props.splitNum}`
      );
    }
  };

  // 窗口大小变化处理
  const handleResize = () => {
    // 使用debounce防止频繁调整大小
    if (resizeTimer) {
      clearTimeout(resizeTimer);
    }

    resizeTimer = setTimeout(() => {
      if (player.value) {
        console.log('窗口大小变化，重新调整播放器');
        setFullWidthSquare();
      }
    }, 200); // 200ms的延迟
  };
  //开始时间改变
  const handleStartTimeChange = (value) => {
    props.urls[currentWindowIndex.value].startTime = value;
  };
  //结束时间改变
  const handleEndTimeChange = (value) => {
    props.urls[currentWindowIndex.value].endTime = value;
  };
  //校验时间
  const checkTime = () => {
    if (!props.urls[currentWindowIndex.value].startTime || !props.urls[currentWindowIndex.value].endTime) {
      createMessage.error('请选择回放时间');
      return false;
    }
    return true;
  };
  // 开始回放
  const playbackStart = async (payerIndex) => {
    if (!checkTime()) return false;
    if (!player.value) return false;
    // 如果没有传入窗口索引，使用当前选中的窗口
    let index = currentWindowIndex.value;
    if (payerIndex !== undefined) {
      index = payerIndex;
    }
    // 获取对应窗口的URL，如果数组中没有对应索引的URL，则使用第一个URL
    const playURL = props.urls[index].playback;
    let startTime = props.urls[index].startTime + '.000+08:00';
    let endTime = props.urls[index].endTime + '.000+08:00';
    const mode = props.mode;
    try {
      await player.value.JS_Play(
        playURL,
        {
          playURL,
          mode,
          keepDecoder: 1,
          token: '',
        },
        index,
        startTime,
        endTime
      );
      console.log('开始回放成功');
      playbackSpeed.value = 1;
      return true;
    } catch (e) {
      console.error('开始回放失败', e);
      return false;
    }
  };
  /* 开始预览 */
  async function realplay(payerIndex) {
    // 如果没有传入窗口索引，使用当前选中的窗口
    let index = currentWindowIndex.value;
    if (payerIndex !== undefined) {
      index = payerIndex;
    }
    // 获取对应窗口的URL，如果数组中没有对应索引的URL，则使用第一个URL
    const playURL = props.urls[index].realplay;
    const mode = props.mode;
    try {
      await player.value.JS_SetTraceId(index, true);
      await player.value.JS_Play(playURL, { playURL, mode, keepDecoder: 0, token: '' }, index);
      const id = await player.value.JS_GetTraceId(index);
      console.log('traceid:', id);
      return true;
    } catch (e) {
      console.error(e);
      return false;
    }
  }
  //开始倒放
  function reversekStart() {
    if (!player.value) return;
    // 如果没有传入窗口索引，使用当前选中的窗口
    const index = currentWindowIndex.value;
    // 获取对应窗口的URL，如果数组中没有对应索引的URL，则使用第一个URL
    const playURL = props.urls[index].playback;
    if (!playURL) {
      createMessage.error('此视频不能回放');
      return;
    }
    if (!formState.startTime || !formState.endTime) {
      createMessage.error('请选择回放时间');
      return;
    }

    let startTime = formState.startTime + '.000+08:00';
    let endTime = formState.endTime + '.000+08:00';
    const mode = props.mode;
    const token = '';
    console.log('倒放，参数：', { playURL, mode, startTime, endTime, index, token });

    player.value.JS_Play(playURL, { playURL, mode, keepDecoder: 0, PlayBackMode: 3, token }, index, startTime, endTime).then(
      () => {
        console.log('reversekStart success');
        playbackSpeed.value = 1;
      },
      (e) => {
        console.error(e);
      }
    );
  }
  //暂停回放
  function playbackPause() {
    player.value.JS_Pause(player.value.currentWindowIndex).then(
      () => {
        console.log('playbackPause success');
      },
      (e) => {
        console.error(e);
      }
    );
  }
  //播放所有预览
  async function playAllRealplay() {
    for (let i = 0; i < props.urls.length; i++) {
      realplay(i);
    }
  }
  //恢复回放
  function playbackResume() {
    player.value.JS_Resume(player.value.currentWindowIndex).then(() => {
      console.log('playbackResume success');
    });
  }
  //慢放
  function playbackSlow() {
    player.value.JS_Slow().then((rate) => {
      console.log('playbackSlow success');
      props.urls[currentWindowIndex.value].playbackSpeed = rate;
    });
  }
  //快放
  function playbackFast() {
    player.value.JS_Fast().then((rate) => {
      console.log('playbackFast success');
      props.urls[currentWindowIndex.value].playbackSpeed = rate;
    });
  }
  //停止所有播放
  async function stopAllPlay() {
    await player.value.JS_StopRealPlayAll();
  }
  //播放所有回放
  async function playAllPlayback() {
    for (let i = 0; i < props.urls.length; i++) {
      playbackStart(i);
    }
  }
  //停止回放
  function stopPlayback() {
    player.value.JS_Stop(player.value.currentWindowIndex).then(() => {
      console.log('stopPlayback success');
    });
  }
  //监听播放模式
  watch(
    () => props.playMode,
    async (newVal) => {
      console.log('播放模式变更为：', newVal);
      if (newVal === '1') {
        // await stopAllPlay();
        playAllPlayback();
      } else if (newVal === '2') {
        // 实时播放
        console.log('进入实时播放模式');
        if (player.value) {
          stopAllPlay();
          playAllRealplay();
        }
      }

      // 播放模式改变后重新计算大小
      // 给DOM足够时间更新后再调整大小
      setTimeout(setFullWidthSquare, 300);
    },
    { immediate: true } // 添加immediate选项，确保组件加载时立即执行一次
  );
  function arrangeWindow() {
    let splitNum = props.splitNum;
    // 使用splitNum设置窗口布局（如2表示2x2的四方格）
    player.value.JS_ArrangeWindow(splitNum).then(
      () => {
        console.log(`arrangeWindow to ${splitNum}x${splitNum} success`);
        // 延迟一点点时间，确保窗口排列完成后，重新调整大小
        setTimeout(() => {
          player.value.JS_Resize();
        }, 100);
      },
      (e) => {
        console.error(e);
      }
    );
  }
  watch(
    () => props.splitNum,
    (newVal) => {
      console.log(`分屏数变更为: ${newVal}`);
      arrangeWindow();
      // 窗口布局变化后重新设置播放器大小，延长等待时间确保布局已应用
      setTimeout(setFullWidthSquare, 500);
    }
  );
  // 组件挂载时初始化播放器
  onMounted(() => {
    initPlayer();
    // 确保播放器大小适应当前页面
    setTimeout(setFullWidthSquare, 300);
    // 初始化回放时间
    // formState.startTime = dayjs().format('YYYY-MM-DDT00:00:00'); //当日凌晨
    // formState.endTime = dayjs().format('YYYY-MM-DDTHH:mm:ss'); //当前时间
  });

  // 组件卸载前释放资源
  onBeforeUnmount(() => {
    if (player.value) {
      stopAllPlay();
    }
    // 清除定时器
    if (resizeTimer) {
      clearTimeout(resizeTimer);
    }
    // 移除窗口大小变化监听
    window.removeEventListener('resize', handleResize);
  });
  //对外暴露方法
  defineExpose({
    playbackStart,
    stopPlayback,
    stopAllPlay,
    playAllPlayback,
    playAllRealplay,
    realplay,
    setFullWidthSquare,
  });
</script>

<style lang="less" scoped>
  .video-player-container {
    display: flex;
    flex-direction: column;
    gap: 10px;
    width: 100%; /* 确保占满父容器宽度 */
    height: 100%;
    max-width: 100%;
    overflow: hidden; /* 防止溢出 */

    #player {
      // flex: 1;
      overflow: hidden;
      background-color: #000;
      position: relative; /* 创建一个新的定位上下文 */
      width: 100%; /* 确保占满容器宽度 */
      // height: 700px; // 移除固定高度，由JS动态控制
      //
      // :deep(#player-container-3){
      //   background: #fff !important;
      //   border: none !important;
      // }
    }

    /* 注释掉可能干扰四方格布局的样式 */
    /*
    :deep([id^="player-container-"]) {
      width: 100% !important;
      max-width: 100% !important;
    }

    :deep(.window-container) {
      width: 100% !important;
    }
    */

    .controls {
      display: flex;
      flex-direction: column;
      background-color: #f8f8f8;
      padding: 12px;
      border-radius: 4px;
      flex-shrink: 0; /* 防止控制区域被压缩 */
      width: 100%; /* 确保控制区域也占满宽度 */

      .date-range {
        display: flex;
        flex-wrap: wrap;
        margin-bottom: 12px;

        .date-item {
          display: flex;
          align-items: center;
          margin-right: 20px;
          margin-bottom: 8px;

          .date-label {
            width: 80px;
            text-align: right;
            margin-right: 8px;
            font-weight: 500;
          }
        }
      }

      .preview-buttons {
        display: flex;
        flex-wrap: wrap;
        margin-bottom: 12px;
      }

      .playback-speed {
        display: flex;
        justify-content: flex-start;

        .speed-indicator {
          display: flex;
          align-items: center;
          background-color: #e6f7ff;
          border: 1px solid #91d5ff;
          border-radius: 20px;
          padding: 4px 16px;

          .speed-label {
            color: #666;
            margin-right: 8px;
            font-size: 14px;
          }

          .speed-value {
            font-weight: 600;
            color: #1890ff;
            font-size: 16px;
          }
        }
      }
    }
  }
</style>
