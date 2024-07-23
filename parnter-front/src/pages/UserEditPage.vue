<template>
  <van-form @submit="onSubmit">
    <van-field
        v-model="editUser.currentValue"
        :name="editUser.name"
        :label="editUser.label"
        :placeholder="`请输入${editUser.label}`"
    />
    <div style="margin: 16px;">
      <van-button round block type="primary" native-type="submit">
        提交
      </van-button>
    </div>
  </van-form>

</template>

<script setup lang="ts">

import {useRoute, useRouter} from "vue-router";
import {ref} from "vue";
import myAxios from "../plugins/my-axios";
import {showToast} from "vant";

const route = useRoute();

const router = useRouter();

const editUser = ref({
  name: route.query.name,
  currentValue: route.query.currentValue,
  label: route.query.label,
  id:route.query.id,
})

const onSubmit = async () => {
  console.log("editUser.value.id " + editUser.value.id)
  console.log("editUser.currentValue.id " + editUser.value.currentValue)
  console.log("editUser.currentValue.id " + editUser.value.name)
  const result = await myAxios.post('/user/updateUserInfoByUser', {
    id: editUser.value.id,
    [editUser.value.name as string]: editUser.value.currentValue
  })
  if (result.code === 0 && result.data > 0) {
    showToast("修改成功");
    router.back();
  } else {
    showToast("修改失败");
  }
};

</script>

<style scoped>

</style>