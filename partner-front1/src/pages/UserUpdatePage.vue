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
            :src="user.avatarUrl"
            @click="updateHeadUrl"
        />
        <p style="margin: -1px auto;width: 110px;text-align: center;color: red">点击头像更换</p>
      </van-col>
    </van-row>
    <van-cell title="账户" :value="user.userAccount"/>
    <van-cell title="角色" :value="user.userRole === '0' ? '普通用户' : '管理员'"/>
    <van-cell title="名称" is-link :value="user.username" @click="userEdit('username','名称',user.username)"/>
    <van-cell title="性别" is-link :value="user.gender === 0 ? '男' : user.gender === 1 ? '女' : '保密'" @click="openPopup"></van-cell>
    <van-cell title="电话" is-link :value="user.phone" @click="userEdit('phone','电话',user.phone)"/>
    <van-cell title="邮箱" is-link :value="user.email" @click="userEdit('email','邮箱',user.email)"/>
    <van-cell title="标签" is-link :value="user.tags" @click="userEdit('tags','标签',user.tags)"/>
    <van-cell title="创建时间" :value="user.createTime"/>
    <van-cell
        id="personIntroducer" title="个人介绍" is-link :value="handleText(user.profile)"
        @click="userEdit('profile','个人介绍',user.profile)"/>
    <van-popup
        v-model:show="showBottom"
        position="bottom"
        :style="{ height: '30%' }"
        @click="cancelPopup"
    >
      <van-list finished-text="没有更多了">
        <van-cell v-for="item in sexList" :key="item.id" :title="item.value" @click="editSex({value : item.id})"/>
      </van-list>
    </van-popup>
  </template>
</template>

<script setup lang="ts">
import {useRouter} from "vue-router";
import {onMounted, ref} from "vue";
import {getCurrentUser} from "../services/user";
import myAxios from "../plugins/myAxios";
import {Toast} from "vant";

// 定义
const showBottom = ref(false);
const router = useRouter();


const user = ref();

onMounted(async () => {
  user.value = await getCurrentUser();

})

function handleText(value : string) {
  if (!value) return ''
  if (value.length > 18) {
    return value.slice(0, 18) + '...'
  }
  return value
}

// 修改其他信息
const userEdit = (name: string, label: string, currentValue: string) => {
  router.push({
    path: '/user/edit',
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
const editSex = async ({value}: { value: any }) => {
  const result = await myAxios.post('/user/updateUserInfoById', {
    'id': user.value.id,
    'gender': value
  })
  if (result.code === 0 && result.data > 0) {
    user.value = await getCurrentUser();
  }
}

const openPopup = () => {
  showBottom.value = true;
}
const cancelPopup = () => {
  showBottom.value = false;
}

// 更换头像
const updateHeadUrl = async () =>{
  Toast("开发中！！");
}



</script>

<style scoped>

</style>