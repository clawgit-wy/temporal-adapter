<template>
    <div class="container">
        <div class="path">当前路径：外部拦截提醒</div>
        <div>
            <h1 class="title">外邮拦截提醒开关</h1>
            <div class="change-box">
                <a-button style="background-color: #47B5EA; color: #fff; margin-right: 16px;" @click="submit">保存修改</a-button>
                <!-- <a-button @click="cancel">取消</a-button> -->
            </div>
            <a-divider orientation="left" orientation-margin="0px" dashed  style="border-color: #DADADA">
                <h3 class="sub-title">外邮拦截提醒</h3>
            </a-divider>
            <ul>
                <li>
                    <div class="radio-box" @click="status = '0'">
                        <span :class="['radio', status == '0' ? 'active' : '']">
                        <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAAEKADAAQAAAABAAAAEAAAAAA0VXHyAAABqElEQVQ4EZWTu05CQRCG/91D8IIgXugUEmOjJBYab4mVhZcH8A2Mia2JVFppowUd0cI3sDXeCmu0sDCB0gQLNUGNCoGIsOvM4oGjcoxOczaz8/0zZ2ZW4JvNHj9GodSiAGY0dJivBcSNBk4h5d7JXFfKiVBc1RZS2pu7ycahxDKB0vY7vySkIPWOPxxa2Y+KEt8ZAQNnHo601tNOwO0shDjzR7rnWcRk4sx/hVmUY021dBb8z6KirtzKdlbR2STRbAncFircF6UtOSS5YX+Bw20WElMdWBrwGU3DECu523YWEm9ovT4LW+NBlJXGbjpfi2FWkpIZFXvXhgOITwbR6qkr9RC8PVGFY+fPuC+qmgCzX8Z1kCmiP+DB5mg7WqgcG65oDYbvCnXYVhFzh9kUdXXQdoyFvFgfCeD6tYxQiwVF8GrSBRYiTb9AG+awi2wJG5ev6KNKSBix5EvDzIww6zrGCHU9967x9PazbIZrYzS7TevJTqdl8hVX2MQRw6xpIu82r6dT4LezWWViOMYI8E7zbguJhHkwLjTfcYz9DjisPvBP6L/P+QO34sIGzZDeRQAAAABJRU5ErkJggg==" alt="">
                        </span>每天定时提醒</div>
                    <!-- <a-radio name="status" v-model:checked="status" :value="0">每天定时提醒</a-radio> -->
                    <div class="desc">
                        当您有外部邮件因病毒，攻击等威胁特征被邮件安全系统拦截时，系统会在每天1:30，7:30，13:30，16:30，20:30给您发送拦截提醒邮件（一封邮件仅提醒一次）
                    </div>
                </li>
                <li>
                    <div class="radio-box" @click="status='1'">
                        <span :class="['radio', status == '1' ? 'active' : '']">
                            <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAAEKADAAQAAAABAAAAEAAAAAA0VXHyAAABqElEQVQ4EZWTu05CQRCG/91D8IIgXugUEmOjJBYab4mVhZcH8A2Mia2JVFppowUd0cI3sDXeCmu0sDCB0gQLNUGNCoGIsOvM4oGjcoxOczaz8/0zZ2ZW4JvNHj9GodSiAGY0dJivBcSNBk4h5d7JXFfKiVBc1RZS2pu7ycahxDKB0vY7vySkIPWOPxxa2Y+KEt8ZAQNnHo601tNOwO0shDjzR7rnWcRk4sx/hVmUY021dBb8z6KirtzKdlbR2STRbAncFircF6UtOSS5YX+Bw20WElMdWBrwGU3DECu523YWEm9ovT4LW+NBlJXGbjpfi2FWkpIZFXvXhgOITwbR6qkr9RC8PVGFY+fPuC+qmgCzX8Z1kCmiP+DB5mg7WqgcG65oDYbvCnXYVhFzh9kUdXXQdoyFvFgfCeD6tYxQiwVF8GrSBRYiTb9AG+awi2wJG5ev6KNKSBix5EvDzIww6zrGCHU9967x9PazbIZrYzS7TevJTqdl8hVX2MQRw6xpIu82r6dT4LezWWViOMYI8E7zbguJhHkwLjTfcYz9DjisPvBP6L/P+QO34sIGzZDeRQAAAABJRU5ErkJggg==" alt="">
                        </span>不提醒
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>
<script setup lang="ts">
import {ref} from 'vue'
import { defHttp } from '/@/utils/http/axios';
import { Modal } from 'ant-design-vue';
import { getToken } from '/@/utils/auth';
console.log(getToken());

const status = ref('0')
const email = ref('')
defHttp.post({url: '/safety/sns/findOld', params: {token: getToken()}}).then(res => {
    status.value = res.data.status
    email.value = res.data.email
})
const submit = () => {
    defHttp.post({url: '/safety/sns/update', params: {email: email.value, status: status.value}}).then(() => {
        Modal.success({
            title: '保存成功',
        })
    })
}
const cancel = () => {
    window.opener = window
    const win = window.open("", "_self")
    win.close()
}
</script>
<style scoped>
.change-box{
    display: flex;
    justify-content: flex-end;
}
.container {
    padding: 26px 16px;
    font-size: 14px;
    margin: 0 auto;
}
.path{
    color: #2792C6;
    font-size: 12px;
    border-bottom: 1px dashed #DADADA;
    padding-bottom: 6px;
}
.title{
    font-size: 18px;
    font-weight: 800;
    color: #3E9ECE;
    text-align: center;
    margin-top: 30px;
    color: #000;
}
.sub-title{
    font-weight: 800;
    margin-bottom: 0;
}
li{
    color: #000;
    font-weight: 400;
    line-height: 18px;
    margin-top: 16px;
}
.desc {
    padding-left: 25px;
    line-height: 18px;
    color: #868686
}
.radio-box {
    display: flex;
    align-items: center;
    cursor: pointer;
}
.radio {
    display: inline-block;
    border: 1px solid #C5C5C5;
    width: 16px;
    height: 16px;
    line-height: 12px;
    border-radius: 50%;
    margin-right: 13px;
}
.radio img{
    display: none;
    width: 100%;
    height: 100%;
}
.active {
    border-color: transparent;
    background-color: #47B5EA;
}
.active img {
    display: inline;
}
</style>