import { ContentTypeEnum } from '/@/enums/httpEnum';
import { defHttp } from '/@/utils/http/axios';

let areaOptions: any[] = [];

export function getAreaCascaderProps() {
  return {
    options: areaOptions,
    allowClear: true,
    showSearch: true,
    changeOnSelect: true,
    placeholder: '请选择',
  };
}

export async function loadAreaOptions() {
  const data = await defHttp.post({
    url: '/safety/field/fieldManageTree',
    data: { fieldType: 'buildingAccessFactoryManage' },
    headers: { 'Content-Type': ContentTypeEnum.FORM_URLENCODED },
  });
  areaOptions = formatAreaOptions(data || []);
  return areaOptions;
}

export function getAreaCodes(value) {
  const values = Array.isArray(value) ? value : String(value || '').split(',').filter(Boolean);
  return {
    factoryCode: values[0],
    buildCode: values[1],
  };
}

export function getAreaNames(value) {
  const values = Array.isArray(value) ? value : String(value || '').split(',').filter(Boolean);
  const factory = areaOptions.find((item) => item.value === values[0]);
  const building = factory?.children?.find((item) => item.value === values[1]);
  return {
    factoryName: factory?.label,
    buildName: building?.label,
  };
}

export function getAreaValue(record) {
  const factoryCode = record?.factoryCode;
  const buildCode = record?.buildCode;
  return [factoryCode, buildCode].filter(Boolean);
}

function formatAreaOptions(data) {
  return data.map((item) => ({
    label: item.fieldName,
    value: item.fieldCode,
    children: (item.regionList || []).map((child) => ({
      label: child.fieldName,
      value: child.fieldCode,
    })),
  }));
}
