<template>
  <UserCardList :user-list="userList"/>
  <van-empty v-if="!userList || userList.length < 1" description="搜索结果为空" />
</template>

<script setup lang="ts">
import {onMounted, ref} from 'vue';
import {useRoute} from "vue-router";
import myAxios from "../plugins/my-axios";
import qs from "qs";
import {UserType} from "../models/user";
import UserCardList from "../components/UserCardList.vue";

const route = useRoute();

const userList = ref([]);

const {tags} = route.query;

onMounted(async () => {
  console.log('tags =' + tags);
  const userListData = await myAxios.get('/user/matchUserByTag', {
    params: {
      tags: tags
    },
    paramsSerializer: params => {
      return qs.stringify(params, {indices: false})
    }
  }).then(function (response) {
    return response.data;
  }).catch(function (error) {
    console.log(error);
  })

  if (userListData) {
    userListData.forEach(user =>{
      if (user.tags) {
        user.tags = JSON.parse(user.tags);
      }
    })
    userList.value = userListData as UserType[];
  }
})


// const user = {
//   id: 1,
//   username: 'yang',
//   userAccount: '123456',
//   avatarUrl: 'https://q4.itc.cn/q_70/images03/20240528/298d4abda5e4469d98fa77e7cde46525.jpeg',
//   profile: '哈哈哈哈哈哈哈哈哈，你这个小伙子很6 啊',
//   gender: '0',
//   phone: '1567777777',
//   email: '9322222@qq.com',
//   planetCode: '121321',
//   tags: ['java', 'c++', 'python', 'mysql', 'redis'],
//   createTime: Date,
// };



</script>

<style scoped>

</style>
