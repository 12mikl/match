<template>
  <template v-if="user">
    <van-row justify="center">
      <van-col span="6">
        <van-image
            round
            width="110px"
            height="110px"
            fit="cover"
            position="right"
            src="https://q4.itc.cn/q_70/images03/20240528/298d4abda5e4469d98fa77e7cde46525.jpeg"
        />
        <p style="margin: -1px auto;width: 110px;text-align: center">点击更换头像</p>
      </van-col>
    </van-row>
    <van-cell title="账户" :value="user.userAccount"/>
    <van-cell title="角色" :value="roleStr"/>
    <van-cell title="名称" is-link :value="user.userName" @click="userEdit('userName','名称',user.userName)"/>
    <van-cell title="性别" is-link :value="genderStr" @click="openPopup"></van-cell>
    <van-cell title="电话" is-link :value="user.userPhone" @click="userEdit('userPhone','电话',user.userPhone)"/>
    <van-cell title="邮箱" is-link :value="user.userEmail" @click="userEdit('userEmail','邮箱',user.userEmail)"/>
    <van-cell title="标签" is-link :value="user.tags" @click="userEdit('tags','标签',user.tags)"/>
    <van-cell title="创建时间" :value="user.createTime"/>
    <van-text-ellipsis
        style="padding: 16px"
        :content="user.profile"
        @click="userEdit('profile','自我介绍',user.profile)"
    />
    <van-popup
        v-model:show="showBottom"
        position="bottom"
        :style="{ height: '30%' }"
        @click="cancelPopup"
    >
      <van-list finished-text="没有更多了">
        <van-cell v-for="item in sexList" :key="item.id" :title="item.value" @click="editSex(item.id)"/>
      </van-list>
    </van-popup>
  </template>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {showToast} from "vant";
import myAxios from "../plugins/my-axios";
import {getCurrentUser} from "../services/currentUser";

// 定义
const showBottom = ref(false);
const router = useRouter();

// const user = {
//   id:2,
//   username: 'yang',
//   userAccount: '123456',
//   avatarUrl: '',
//   userGender: 1,
//   phone: '15676202511111',
//   email: '9310637777@qq.com',
//   userRole: '管理员',
//   tags: 'java,c++,python',
//   createTime: new Date(),
// };

const user = ref();

let genderStr: string = '';
let roleStr: string ='';

onMounted(async () => {
  user.value = await getCurrentUser();
  getUserRoleAndGender(user.value);
})

function getUserRoleAndGender (userData){
  // 性别
  if (userData.userGender === 0) {
    genderStr = '男';
  } else if (userData.userGender === 1) {
    genderStr = '女';
  } else {
    genderStr = '保密';
  }

  // 角色
  if (userData.userRole === 0) {
    roleStr = '用户';
  } else {
    roleStr = '管理员';
  }
}

// 修改其他信息
const userEdit = (name: string, label: string, currentValue: string) => {
  router.push({
    path: 'user/edit',
    query: {
      name,
      label,
      currentValue,
      id: user.value.id,
    }
  })
};

const sexList = [
  {value: '男', id: 0},
  {value: '女', id: 1},
  {value: '保密', id: 2},
];



// 修改性别
const editSex = async (value) => {
  const result = await myAxios.post('/user/updateUserInfoByUser', {
    'id': user.value.id,
    'userGender': value
  })
  if (result.code === 0 && result.data > 0) {
    showToast("修改成功");
    user.value = await getCurrentUser();
    getUserRoleAndGender(user.value);
  }
}

const openPopup = () => {
  showBottom.value = true;
}
const cancelPopup = () => {
  showBottom.value = false;
}

</script>

<style scoped>

</style>