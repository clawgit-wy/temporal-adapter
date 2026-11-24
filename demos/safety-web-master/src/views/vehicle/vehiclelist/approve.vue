<template>
  <div ref="refModal">
    <div class="visitor-box">
      <a-card :bordered="false" style="width: 900px">
        <a-form ref="formRef" :model="formState" class="myForm" :rules="rules">
          <div>
            <div class="visitor-title">
              <span> 物流车辆信息 </span>
            </div>
            <a-row class="row-zl">
              <a-col :span="8">
                <a-form-item label="车牌号" name="visitedName">
                  <Tooltip placement="top" :title="formState.fdCarNo">
                    <span>{{ formState.fdCarNo }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="入厂类型">
                  <span>{{ formState.fdEnterType }}</span>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="供应商名称">
                  <Tooltip placement="top" :title="formState.fdSupplierName">
                    <span>{{ formState.fdSupplierName }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row class="row-zl">
              <a-col :span="8">
                <a-form-item label="运输货物名称" name="visitedName">
                  <Tooltip placement="top" :title="formState.fdGoodsName">
                    <span>{{ formState.fdGoodsName }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="货物装载量">
                  <span>{{ formState.fdGoodsWeight }}</span>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="车辆类型">
                  <span>{{ formState.fdFilingCarTypeName }}</span>
                </a-form-item>
              </a-col>
              <a-col :span="8" v-show="formState.fdDetailedImg">
                <a-form-item label="随车清单">
                  <a :href="formState.fdDetailedImg" target="_blank" v-if="formState.fdDetailedImg">点击查看</a>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="排放标准">
                  <span>{{ formState.fdProtectGrade }}</span>
                </a-form-item>
              </a-col>
              <a-col :span="8" v-show="formState.fdCarImg">
                <a-form-item label="行驶证照片">
                  <a :href="formState.fdCarImg" target="_blank" v-if="formState.fdCarImg">点击查看</a>
                </a-form-item>
              </a-col>
              <a-col :span="8" v-show="formState.fdEngine">
                <a-form-item label="发动机号码" name="visitedName">
                  <Tooltip placement="top" :title="formState.fdEngine">
                    <span>{{ formState.fdEngine }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8" v-show="formState.fdCarDate">
                <a-form-item label="行驶证注册日期" name="visitedName">
                  <Tooltip placement="top" :title="formState.fdCarDate">
                    <span>{{ formState.fdCarDate }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8" v-show="formState.fdVin">
                <a-form-item label="车辆识别代码">
                  <Tooltip placement="top" :title="formState.fdVin">
                    <span>{{ formState.fdVin }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="车辆归属" name="visitedName">
                  <Tooltip placement="top" :title="formState.fdCarBelong">
                    <span>{{ formState.fdCarBelong }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="8">
                <a-form-item label="车主姓名">
                  <Tooltip placement="top" :title="formState.fdDriverName">
                    <span>{{ formState.fdDriverName }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
            </a-row>
          </div>
          <div>
            <div class="visitor-title">
              <span> 人员信息 </span>
            </div>
            <a-row class="row-zl2">
              <a-col :span="2">
                <a-form-item label="物流司机" :colon="false" style="font-weight: bold; color: black"> </a-form-item>
              </a-col>
              <a-col :span="22">
                <a-row v-for="(item, index) in logisticsDrivers" :key="index">
                  <a-col :span="8">
                    <a-form-item label="手机号" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                      <Tooltip placement="top" :title="item.fdPerPhone">
                        <span>{{ item.fdPerPhone }}</span>
                      </Tooltip>
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item label="姓名" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                      <Tooltip placement="top" :title="item.fdPerName">
                        <span>{{ item.fdPerName }}</span>
                      </Tooltip>
                    </a-form-item>
                  </a-col>
                  <a-col :span="10">
                    <a-form-item label="身份证号" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                      <Tooltip placement="top" :title="item.fdPerCard">
                        <span>{{ item.fdPerCard }}</span>
                      </Tooltip>
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-col>
            </a-row>
            <a-row class="row-zl2">
              <a-col :span="2">
                <a-form-item label="随行人员" :colon="false" style="font-weight: bold; color: black"> </a-form-item>
              </a-col>
              <a-col :span="22">
                <a-row class="sx-col" v-for="(item, index) in entourage" :key="index">
                  <a-col :span="8">
                    <a-form-item label="手机号" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                      <Tooltip placement="top" :title="item.fdPerPhone">
                        <span>{{ item.fdPerPhone }}</span>
                      </Tooltip>
                    </a-form-item>
                  </a-col>
                  <a-col :span="6">
                    <a-form-item label="姓名" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                      <Tooltip placement="top" :title="item.fdPerName">
                        <span>{{ item.fdPerName }}</span>
                      </Tooltip>
                    </a-form-item>
                  </a-col>
                  <a-col :span="10">
                    <a-form-item label="身份证号" name="visitedName" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                      <Tooltip placement="top" :title="item.fdPerCard">
                        <span>{{ item.fdPerCard }}</span>
                      </Tooltip>
                    </a-form-item>
                  </a-col>
                </a-row>
              </a-col>
            </a-row>
          </div>
          <div>
            <div class="visitor-title">
              <span>接口人信息</span>
            </div>
            <a-row class="row-zl2" :loading="true" v-for="(item, index) in dockLists" :key="index">
              <a-col :span="6">
                <a-form-item label="姓名">
                  <Tooltip placement="top" :title="item.fdDockName">
                    <span>{{ item.fdDockName }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="员工号" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                  <Tooltip placement="top" :title="item.fdDockNo">
                    <span>{{ item.fdDockNo }} </span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="手机号" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                  <Tooltip placement="top" :title="item.fdDockPhone">
                    <span>{{ item.fdDockPhone }}</span>
                  </Tooltip>
                </a-form-item>
              </a-col>
              <a-col :span="6">
                <a-form-item label="部门" :label-col="{ span: '8' }" :wrapper-col="{ span: '16' }">
                  <Tooltip placement="top" :title="item.fdDockDepartment">
                    <span>{{ item.fdDockDepartment }} </span>
                  </Tooltip>
                </a-form-item>
              </a-col>
            </a-row>
          </div>

          <div>
            <div class="visitor-title">
              <span> 备案信息 </span>
            </div>
            <a-row class="row-ft">
              <a-col>
                <a-form-item label="备案厂区" label-align="left" :wrapper-col="{ span: '16' }" name="fdFilingFactoryCode">
                  <a-checkbox-group v-model:value="formState.fdFilingFactoryCode" :options="factory" />
                  <!-- <a-select mode="multiple" v-model:value="formState.fdFilingFactoryCode">
                    <a-select-option v-for="(item, index) in factory" :value="item.itemCode" :label="item.itemName" :key="index">
                      {{ item.itemName }}
                    </a-select-option>
                  </a-select> -->
                </a-form-item>
              </a-col>
            </a-row>
            <a-row class="row-ft">
              <a-col>
                <a-form-item label="备案时间" :wrapper-col="{ span: '16' }" name="rangePicker">
                  <a-range-picker
                    v-model:value="formState.rangePicker"
                    :disabledDate="disabledDate"
                    value-format="YYYY-MM-DD"
                    :placeholder="['Start Time', 'End Time']"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </div>
        </a-form>
        <template #actions>
          <a-button
            @click="$router.replace({ name: 'Welcome' })"
            v-if="formState.fdStateCode == '30' || formState.fdStateCode == '31' || formState.fdStateCode == '40'"
            >取消</a-button
          >
          <a-button
            type="primary"
            v-if="formState.fdStateCode != '30' && formState.fdStateCode != '31' && formState.fdStateCode != '40'"
            @click="onAdopt(2)"
            danger
            >驳回</a-button
          >
          <a-button
            type="primary"
            v-if="formState.fdStateCode != '30' && formState.fdStateCode != '31' && formState.fdStateCode != '40'"
            @click="onAdopt(3)"
            danger
            >废弃</a-button
          >
          <a-button
            type="primary"
            v-if="formState.fdStateCode != '30' && formState.fdStateCode != '31' && formState.fdStateCode != '40'"
            @click="onAdopt(1)"
            >审核通过</a-button
          >
        </template>
      </a-card>
    </div>
  </div>
</template>

<script lang="ts" setup>
  import dayjs from 'dayjs';

  import { ref, unref, watch, reactive, onMounted, inject, createVNode } from 'vue';
  import { verifyBeforeApprove, approve, detail, fcList } from './api';
  import { factoryType, perLists, dockList } from './vehicle.data';
  import { useRoute, useRouter, RouteLocationNormalized } from 'vue-router';
  import { useTabs } from '/@/hooks/web/useTabs';
  import { Modal } from 'ant-design-vue';
  const route = useRoute();
  const router = useRouter();
  const entourage = ref<perLists[]>(); // 随行人员
  const logisticsDrivers = ref<perLists[]>(); // 物流司机
  const dockLists = ref<dockList[]>();
  const factory = ref<factoryType[]>(); // 备案工厂
  const title = ref<string>(''); // Modal title
  const rangePicker = ref<string[]>([]);
  import { useMultipleTabStore } from '/@/store/modules/multipleTab';
  import { ExclamationCircleOutlined } from '@ant-design/icons-vue/lib/icons';
  // let rangDate: string[] = [];
  const useTab = useMultipleTabStore();

  const formState = ref({
    fdStateCode: '',
    fdCarNo: '',
    fdEnterType: '',
    fdSupplierName: '',
    fdGoodsName: '',
    fdGoodsWeight: '',
    fdDetailedImg: '',
    fdProtectGrade: '',
    fdCarImg: '',
    fdCarDate: '',
    fdEngine: '',
    fdVin: '',
    fdFilingCarTypeName: '',
    fdCarBelong: '',
    fdDriverName: '',
    perList: [],
    fdFilingFactory: [],
    fdFilingFactoryCode: [],
    rangePicker: rangePicker,
    fdFilingBegin: '',
    fdFilingEnd: '',
    id: '',
    // dockList: [],
  }); // form 表单字段
  const isUpdate = ref<boolean>(false); // true:更新   false:添加
  const refModal = ref<any>();
  const formRef = ref();

  const rules = {
    fdFilingFactory: [
      {
        required: true,
        message: '请选择备案厂区',
        trigger: 'change',
      },
    ],
    rangePicker: [
      {
        required: true,
        message: '请选择备案时间',
        trigger: 'object',
      },
    ],
  };
  // 日期限制
  function disabledDate(current) {
    let rangDate = unref(rangePicker);

    let start = dayjs(rangDate[0]);
    let end = dayjs(rangDate[1]);
    return current && (current < start || current > end);
  }

  watch(
    () => formState.value.rangePicker,
    (n) => {
      if (n) {
        formState.value.fdFilingBegin = n[0];
        formState.value.fdFilingEnd = n[1];
      }
      console.log(formState.value, 987);
    },
    {
      deep: true, // 这是关键，确保监听对象的所有属性
    }
  );
  const { closeCurrent } = useTabs();
  // 审核通过后跳转到备案列表页
  const closeApproveToFiling = async () => {
    const close = (route: RouteLocationNormalized) => {
      const { fullPath, meta: { affix } = {} } = route;
      if (affix) {
        return;
      }
      const index = useTab.tabList.findIndex((item) => item.fullPath === fullPath);
      index !== -1 && useTab.tabList.splice(index, 1);
    };

    const { currentRoute, replace } = router;
    close(currentRoute.value);
    await router.push({ name: 'approveResult' });
  };
  const asyncModal = async (msg) => {
    console.log(22333);
    return new Promise((resolve, reject) => {
      Modal.confirm({
        title: '提示',
        content: msg,
        onOk() {
          resolve(true);
        },
        onCancel() {
          reject();
        },
      });
    });
  };
  // 审核按钮操作
  const onAdopt = async (e) => {
    let params = {
      approveType: e,
      fdFilingBegin: formState.value.fdFilingBegin,
      fdFilingEnd: formState.value.fdFilingEnd,
      // selectFactory: selectFactory.map(item => item.itemName).join(','),
      // fdFilingFactory: fac.join(','),
      fdFilingFactoryCode: formState.value.fdFilingFactoryCode.join(','),
      fdFilingId: route.query.formInstanceId,
    };
    const checkRes = await verifyBeforeApprove(params);
    if (checkRes.flag == false && e == 1) {
      await asyncModal(checkRes.mes);
    }
    if (e == 2 || e == 3) {
      try {
        await formRef.value.validateFields(['rangePicker']).then(() => {
          approve(params, e, closeApproveToFiling, router);
        });
      } catch (error) {
        console.log(error);
      }
    } else {
      try {
        console.log(formRef.value, '7878989845564556');
        await formRef.value.validate().then(() => {
          approve(params, e, closeApproveToFiling, router);
        });
      } catch (error) {
        console.log(error);
      }
    }
  };

  // 获取下拉框数据
  async function getFtList() {
    console.log(formState.value.id, '数据ID');
    const res = await fcList({ filingId: formState.value.id });
    factory.value = res.map((item) => ({ ...item, label: item.itemName, value: item.itemCode.toString(), itemCode: item.itemCode.toString() }));
  }

  // 获取详情
  async function getDetail() {
    let res = await detail({ id: route.query.formInstanceId });
    res = JSON.parse(res);

    if (res) {
      formState.value = res;

      console.log(formState.value, 'valueeee');

      console.log(res.fdFilingFactory, 877878);
      dockLists.value = res.dockList;
      rangePicker.value.push(res.fdFilingBegin, res.fdFilingEnd);
      // rangDate.push(res.fdFilingBegin, res.fdFilingEnd);
      logisticsDrivers.value = res.perList.filter((item) => item.fdPerType === '物流司机');
      entourage.value = res.perList.filter((item) => item.fdPerType === '随行人员');
      if (!res.fdFilingFactoryCode) {
        formState.value.fdFilingFactoryCode = [];
      } else {
        formState.value.fdFilingFactoryCode = res.fdFilingFactoryCode.split(',');
      }
      formState.value.rangePicker = rangePicker.value;
      console.log(formState.value, '数据');
      getFtList();
    }
  }

  // // 下拉框选择
  // function handelChange(v) {
  //   console.log(v, '下拉框');

  //   let fac = [];
  //   v.forEach((item) => {
  //     fac.push(item.option.label);
  //   });
  //   formState.value.fdFilingFactoryCode = fac.join(',');
  // }
  onMounted(() => {
    console.log(route.query, '路由参数');

    getDetail();
  });
</script>
<style lang="less" scoped>
  .visitor-box {
    display: flex;
    justify-content: center;
    width: 100%;
    ::v-deep(.ant-card-actions) {
      padding: 0 235px;
    }

    ::v-deep(.ant-card-actions > li:nth-child(2) .ant-btn) {
      background-color: rgba(255, 126, 0, 1) !important;
    }
    ::v-deep(.ant-card-body) {
      padding-bottom: 0px !important;
    }
  }
  ::v-deep(.ant-checkbox-group) {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
  }
  ::v-deep(.ant-checkbox-group-item + .ant-checkbox-group-item) {
    width: 100%;
  }
  ::v-deep(.ant-btn) {
    width: 80px !important;
    border-radius: 4px !important;
    //   .ant-btn:nth-child(1){
    //     margin-left: 100px
    //   }
  }
  ::v-deep(.ant-modal-content) {
    border-radius: 4px !important;
    padding: 20px !important;
  }
  ::v-deep(.ant-modal-footer) {
    text-align: center;
    .ant-btn:nth-child(1) {
      width: 80px;
      border-color: #797979;
      border-radius: 4px;
    }
    .ant-btn:nth-child(2) {
      width: 80px;

      border-radius: 4px;
      margin-left: 20px;
    }

    .ant-btn:nth-child(3) {
      width: 80px;
      margin-left: 20px;
      border-radius: 4px;
    }
  }
  .visitor-title {
    width: 100%;
    line-height: 30px;
    background-color: rgba(242, 242, 242, 1);
    color: #1664ff;
    margin-bottom: 30px;
    padding-left: 25px;
    border: 1px solid #797979;
    border-radius: 4px;
    span {
      border-left: 4px solid #1664ff;
      padding-left: 5px;
    }
  }
  .visitor-avatar {
    max-height: 100%;
  }
  .takePhoto {
    position: absolute;
    right: -27px;
    top: 40px;
    cursor: pointer;
    color: #1664ff;
  }

  .row-zl {
    // margin-left: 35px;
    // margin-bottom: 10px;
    margin-top: -10px;
    .ant-input {
      border: none; /* 移除边框 */
      outline: none; /* 移除焦点时的轮廓 */
      box-shadow: none; /* 移除阴影 */
      // background-color: rgba(242, 242, 242, 1); /* 透明背景，可以根据需要设置为特定颜色 */
      border-radius: 4px;
    }
    .sc-qd {
      border-bottom: 1px solid #1664ff;
      color: #1664ff;
    }
  }
  .row-zl2 {
    margin-top: -10px;
    .ant-input {
      // border-color: #797979;
      border-radius: 4px;
      border: none; /* 移除边框 */
      outline: none; /* 移除焦点时的轮廓 */
      box-shadow: none; /* 移除阴影 */
      // background-color: rgba(242, 242, 242, 1); /* 透明背景，可以根据需要设置为特定颜色 */
    }
  }

  .sx-col:nth-child(n + 2) {
    margin-top: -10px !important;
  }

  ::v-deep(.ant-select-selector) {
    border-color: #797979 !important;
    border-radius: 4px !important;
  }
  ::v-deep(.ant-picker) {
    border-color: #797979;
    border-radius: 4px;
    width: 100%;
  }

  ::v-deep(.ant-form-item-control-input-content) {
    white-space: nowrap;
    overflow: hidden; /* 隐藏超出部分的文本 */
    text-overflow: ellipsis;
    display: inline;
    cursor: pointer;
  }

  .row-zl::v-deep {
    .ant-col {
      .ant-form-item {
        display: flex;

        .ant-form-item-label {
          width: unset;
        }

        .ant-form-item-content {
          margin-left: unset;
          flex: 1;
        }
        a {
          text-decoration: underline;
        }
      }
    }
  }
  .row-zl2::v-deep {
    .ant-col {
      .ant-form-item {
        display: flex;

        .ant-form-item-label {
          width: unset;
        }

        .ant-form-item-content {
          margin-left: unset;
          flex: 1;
        }
      }
    }
  }

  .row-ft::v-deep {
    .ant-col {
      .ant-form-item {
        display: flex;

        .ant-form-item-label {
          width: unset;
        }

        .ant-form-item-content {
          margin-left: unset;
          flex: 1;
        }
      }
    }
  }
</style>
<style lang="less">
  .visitor-box {
    .ant-input[disabled] {
      color: #000000;
      width: 100%;
    }
    .ant-select-disabled {
      color: #000000;
    }
    .ant-picker .ant-picker-input > input[disabled] {
      color: #000000;
    }
    .ant-row .ant-select-single.ant-select-show-arrow.ant-select-disabled .ant-select-selection-item {
      color: #000000;
    }
  }
</style>
