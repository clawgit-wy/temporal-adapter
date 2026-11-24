<template>

    <TreeSelect
        tree-data-simple-mode style="width: 100%"
        @change="handleChange"
        :value="value"
        :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
        :tree-data="treeData" placeholder="Please select" :load-data="onLoadData" />
</template>
<script lang="ts">
import { TreeSelect, TreeSelectProps } from 'ant-design-vue'
import { watchEffect } from 'vue'
import { defineComponent, PropType, ref } from 'vue'
import { defHttp } from '../../../../utils/http/axios';
export default defineComponent({
    name: 'DeptTreeSelect',
    components: { TreeSelect },
    props: {
        api: {
            type: Function as PropType<(arg?: Recordable | string) => Promise<OptionsItem[]>>,
            default: null,
        },
        params: {
            type: [Object, String] as PropType<Recordable | string>,
            default: () => ({}),
        },
        invertIdsField: {
            type: String,
            default: null
        },
        value: {
            type: [String, Number, Boolean] as PropType<string | number | boolean>,
        },
    },
    emits: ['change'],
    setup(props, {attrs, emit }) {
        const treeData = ref<any>([])
        treeData.value = []
        const getGroupTreeApi = (key: string) => {
            return defHttp.post({url: '/safety/group/selectGroupTree', params: {key}})
        } 
        const getLevelGroup = (key: string, isLeaf: boolean) => {
            const api = props.api ? props.api() : getGroupTreeApi

            return new Promise((resolve, reject) => {
                api(key).then((res: any) => {
                    const { dataList } = res
                    const fDatas = dataList.map((item) => {
                        let pId = '0'
                        if (key) pId = key
                        delete item.parent
                        return {
                            ...item,
                            id: item.key,
                            isLeaf,
                            pId,
                            value: item.key
                        }
                    })
                    
                    resolve(fDatas)
                })
            })
        }
        getLevelGroup('', false).then(res => {
            treeData.value = res
        })
        const getReverseData = () => {
            const orgCodePath = attrs.formValues.values[props.invertIdsField]
            let orgCodeArr = orgCodePath ? orgCodePath.split(',') : []
            if(orgCodeArr.length > 0) {
                orgCodeArr.unshift('50004002')
            }
            
            if (props.value && props.invertIdsField && orgCodeArr.length > 1) {
                for (let index = 0; index < orgCodeArr.length - 1; index++) {
                    getLevelGroup(orgCodeArr[index], false).then(res => treeData.value = treeData.value.concat(res))
                }
            }
        }
        const onLoadData = (treeNode: TreeSelectProps['treeData'][number]) => new Promise((resolve, reject) => {
            const { id } = treeNode.dataRef;
            const checkKey = treeData.value.some((item) => item.pId == id)
            
            if (checkKey) return resolve(true)
            getLevelGroup(id, false).then(res => {
                treeData.value = treeData.value.concat(res)
                resolve(true)
            })
        });
        function handleChange(...args) {
            emit('change', ...args);
        }
        let cacheReverCodes = null
        watchEffect(() => {
            const reverCodes = attrs.formValues.values[props.invertIdsField]
            if (reverCodes && props.value && reverCodes != cacheReverCodes) {
                cacheReverCodes = reverCodes
                getReverseData()
            }
        })
        return {
            onLoadData,
            treeData,
            handleChange
        }
    }
})


</script>
