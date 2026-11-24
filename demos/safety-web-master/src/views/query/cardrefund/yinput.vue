<template>
  <span>
    <a-input-number v-model:value="value"  :min="0" :max="9999" />
    <span>-</span>
    <a-input-number v-model:value="value2" :min="0" :max="9999" />
  </span>
</template>

<script lang="ts">
  export default {
    props: ['modelValue'],
    computed: {
      value:{
        get() {
            return this.modelValue?this.modelValue[0]:null;
        },
        // setter
        set(newValue) {
            if (newValue && this.value2 && newValue > this.value2) {
                this.$emit('update:modelValue', [newValue, null]);
            } else {
                this.$emit('update:modelValue', [newValue, this.value2]);
            }
            
        }
      },
      value2:{
        get() {
            return this.modelValue?this.modelValue[1]:null;
        },
        // setter
        set(newValue) {
            if (newValue && this.value && newValue < this.value) {
                this.$emit('update:modelValue', [null, newValue]);
            } else {
                this.$emit('update:modelValue', [this.value, newValue]);
            }
            
        }
      },
    },
    methods: { 
    }
  };
</script>
