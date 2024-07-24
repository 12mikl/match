<template>
  <user-card-list :user-list="userList" />
  <van-empty v-if="!userList || userList.length < 1" description="搜索结果为空" />
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRoute} from "vue-router";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";
import qs from 'qs';
import UserCardList from "../components/UserCardList.vue";

const route = useRoute();
const {tags} = route.query;

const userList = ref([]);

onMounted(async () => {
  const userListData = await myAxios.get('/user/matchUserByTag', {
    params: {
      tags: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params, {indices: false})
    }
  })
      .then(function (response) {
        console.log('/user/matchUserByTag succeed', response);
        return response?.data;
      })
      .catch(function (error) {
        console.error('/user/matchUserByTag error', error);
        Toast.fail('请求失败');
      })
  console.log("userListData = " + userListData)
  if (userListData) {
    userListData.forEach((user: { tags: string; }) => {
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData;
  }
})




</script>

<style scoped>

</style>
