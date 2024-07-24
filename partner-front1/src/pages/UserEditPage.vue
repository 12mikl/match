<template>
  <van-form @submit="onSubmit">
    <van-field
        v-model="editUser.currentValue"
        :name="editUser.name"
        :label="editUser.label"
        :placeholder="`请输入${editUser.label}`"
    />
    <van-divider v-if="editUser.name === 'tags'">请以,分隔开</van-divider>
    <van-text-ellipsis :content="editUser.currentValue"  v-if="editUser.name === 'profile'"/>

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
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";

const route = useRoute();

const router = useRouter();

const editUser = ref({
  name: route.query.name,
  currentValue: route.query.currentValue,
  label: route.query.label,
  id:route.query.id,
})

const onSubmit = async () => {
  const result = await myAxios.post('/user/updateUserInfoById', {
    id: editUser.value.id,
    [editUser.value.name as string]: editUser.value.currentValue
  });
  if (result.code === 0 && result.data > 0) {
    Toast("修改成功");
    router.back();
  } else {
    Toast(result.message);
  }
};

</script>

<style scoped>


</style>