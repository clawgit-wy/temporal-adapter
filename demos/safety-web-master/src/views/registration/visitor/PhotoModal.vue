<template>
  <BasicModal @register="registerModal" :title="title" :width="800" v-bind="$attrs" @ok="onSubmit">
    <div style="height: 413px" class="photobox">
      <video ref="thisVideo" :width="videoWidth" :height="videoHeight" autoplay></video>
      <canvas ref="thisCancas" :width="videoWidth" :height="videoHeight"></canvas>
    </div>
    <div style="margin-top: 20px">
      <a-button type="primary" @click="setImage">拍照</a-button>
    </div>
  </BasicModal>
</template>

<script lang="ts" setup>
  import { ref, unref } from 'vue';
  import { BasicModal, useModalInner } from '/@/components/Modal';
  import { uploadFile } from './visitor.api';
  import { useUserStoreWithOut } from '/@/store/modules/user';

  const thisVideo = ref(null);
  const thisCancas = ref(null);
  const videoWidth = ref(295);
  const videoHeight = ref(413);
  const imgSrc = ref('');
  let fileSteam;

  let thisContext;
  // 声明 emits
  const emit = defineEmits(['success', 'register']);
  const title = ref<string>('');
  // 注册 modal
  const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
    setModalProps({ confirmLoading: false, showCancelBtn: !!data?.showFooter, showOkBtn: !!data?.showFooter });
    title.value = unref(data.title);
    getCompetence();
  });

  //表单提交事件
  async function onSubmit() {
    uploadFile({ file: fileSteam }, (res) => {
      if (res && res.code === '0') {
        closeModal();
        stopNavigator();
        emit('success', res.data);
      } else if(res && res.code === "401") {
        const userStore = useUserStoreWithOut();
        userStore.setToken(undefined);
        userStore.logout(true);
      }
    });
  }

  // 调用权限（打开摄像头功能）
  function getCompetence() {
    // thisCancas.value = document.getElementById('canvasCamera');
    thisContext = thisCancas.value.getContext('2d');
    // thisVideo.value = document.getElementById('videoCamera');
    // 旧版本浏览器可能根本不支持mediaDevices，我们首先设置一个空对象
    if (window.navigator.mediaDevices === undefined) {
      window.navigator.mediaDevices = {};
    }
    // 一些浏览器实现了部分mediaDevices，我们不能只分配一个对象
    // 使用getUserMedia，因为它会覆盖现有的属性。
    // 这里，如果缺少getUserMedia属性，就添加它。
    if (window.navigator.mediaDevices.getUserMedia === undefined) {
      window.navigator.mediaDevices.getUserMedia = function (constraints) {
        // 首先获取现存的getUserMedia(如果存在)
        var getUserMedia = window.navigator.webkitGetUserMedia || window.navigator.mozGetUserMedia || window.navigator.getUserMedia;
        // 有些浏览器不支持，会返回错误信息
        // 保持接口一致
        if (!getUserMedia) {
          return Promise.reject(new Error('getUserMedia is not implemented in this browser'));
        }
        // 否则，使用Promise将调用包装到旧的window.navigator.getUserMedia
        return new Promise(function (resolve, reject) {
          getUserMedia.call(window.navigator, constraints, resolve, reject);
        });
      };
    }
    var constraints = { audio: false, video: { width: videoWidth.value, height: videoHeight.value, transform: 'scaleX(-1)' } };
    window.navigator.mediaDevices
      .getUserMedia(constraints)
      .then(function (stream) {
        // 旧的浏览器可能没有srcObject
        if ('srcObject' in thisVideo.value) {
          thisVideo.value.srcObject = stream;
        } else {
          // 避免在新的浏览器中使用它，因为它正在被弃用。
          thisVideo.value.src = window.URL.createObjectURL(stream);
        }
        thisVideo.value.onloadedmetadata = function (e) {
          thisVideo.value.play();
        };
      })
      .catch((err) => {
        console.log(err);
      });
  }
  // 绘制图片
  function setImage() {
    // 点击，canvas画图
    thisContext.drawImage(thisVideo.value, 0, 0, videoWidth.value, videoHeight.value);
    // 获取图片base64链接
    const image = thisCancas.value.toDataURL('image/png');
    imgSrc.value = image;
    const file = image;
    const time = new Date().valueOf();
    const name = time + '.png';
    fileSteam = base64ToFile(file, name);
    // const data = new FormData();
    // data.append('file', conversions);
  }

  // 关闭摄像头
  function stopNavigator() {
    thisVideo.value.srcObject.getTracks()[0].stop();
  }

  // base64图片转file的方法（base64图片, 设置生成file的文件名）
  function base64ToFile(base64, fileName) {
    // 将base64按照 , 进行分割 将前缀  与后续内容分隔开
    const data = base64.split(',');
    // 利用正则表达式 从前缀中获取图片的类型信息（image/png、image/jpeg、image/webp等）
    const type = data[0].match(/:(.*?);/)[1];
    // 从图片的类型信息中 获取具体的文件格式后缀（png、jpeg、webp）
    const suffix = type.split('/')[1];
    // 使用atob()对base64数据进行解码  结果是一个文件数据流 以字符串的格式输出
    const bstr = window.atob(data[1]);
    // 获取解码结果字符串的长度
    let n = bstr.length;
    // 根据解码结果字符串的长度创建一个等长的整形数字数组
    // 但在创建时 所有元素初始值都为 0
    const u8arr = new Uint8Array(n);
    // 将整形数组的每个元素填充为解码结果字符串对应位置字符的UTF-16 编码单元
    while (n--) {
      // charCodeAt()：获取给定索引处字符对应的 UTF-16 代码单元
      u8arr[n] = bstr.charCodeAt(n);
    }
    // 利用构造函数创建File文件对象
    // new File(bits, name, options)
    const file = new File([u8arr], `${fileName}.${suffix}`, {
      type: type,
    });
    // 将File文件对象返回给方法的调用者
    return file;
  }
</script>
<style lang="less">
  .photobox {
    height: 450px;
    display: flex;
    canvas {
      margin-left: 20px;
    }
  }
</style>
