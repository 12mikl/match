<template>
  <van-card
      v-if="userList.length > 0"
      v-for="user in userList"
      :desc="user.profile"
      :title="user.userName"
      :thumb="user.avatarUrl"
  >
    <template #tags>
      <van-tag style="margin-right: 7px;margin-top: 7px" plain type="primary" v-for="tag in user.tags">
        {{ tag }}
      </van-tag>
    </template>
    <template #footer>
      <van-button size="mini">联系我</van-button>
      <van-button size="mini">关注</van-button>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import {UserType} from "../models/user";

export default {
  name: "UserCardList"
}

interface UserCardListProps{
  userList : UserType[];
}

const props = withDefaults(defineProps<UserCardListProps>(), {
  loading: true,
  // @ts-ignore
  userList: [] as UserType[],
});

</script>

<style scoped>

</style>