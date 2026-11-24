<template>
  <PageWrapper class="systemd-visitor" title="访客须知">
    <div class="systemd-visitor-footer">
      <a-button type="primary" @click="handelOk"> 保存</a-button>
    </div>
    <Tinymce v-model="value" :showImageUpload="false" width="100%" />
  </PageWrapper>
</template>

<script lang="ts">
  import { defineComponent, ref } from 'vue';
  import { Tinymce } from '/@/components/Tinymce/index';
  import { PageWrapper } from '/@/components/Page';
  import { saveOrUpdate, detail } from './visitor.api';
  import { useMessage } from '/@/hooks/web/useMessage';
  const { createMessage } = useMessage();
  export default defineComponent({
    components: { Tinymce, PageWrapper },
    setup() {
      const value = ref('');
      const loading = ref<Boolean>(false);
      const id = ref<any>('');
      // function handleChange(value: string) {
      //   console.log(value);
      // }
      function handelCancel() {
        console.log(value.value, '====');
      }
      function handelOk() {
        save();
      }

      // 保存或者更新
      async function save() {
        setLoading(true);
        console.log({ content: value.value }, '----');
        const params = { content: value.value, id: undefined };
        if (id.value !== '') {
          params.id = id.value;
        }
        try {
          await saveOrUpdate(params);
          createMessage.success('保存成功');
        } catch (error) {
          console.log(error);
          createMessage.error('保存失败');
        }
        setLoading(false);
      }

      // 获取详情
      async function getDetail() {
        setLoading(true);
        const res = await detail({});
        console.log(res, '9999');
        if (res) {
          id.value = res.id;
        }
        if (res.content) {
          value.value = res.content;
        }
        setLoading(false);
      }

      function setLoading(params: Boolean) {
        loading.value = params;
      }
      getDetail();

      return { handelOk, handelCancel, value };
    },
  });
</script>

<style lang="less" scoped>
  @import 'index';
  .systemd-visitor-footer {
    text-align: left;
    padding: 10px 0 20px 0;
  }
</style>
