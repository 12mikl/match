import { createApp } from 'vue'

import App from './App.vue'
import * as VueRouter from 'vue-router'
//import {Button, Icon, NavBar, TabbarItem} from 'vant';
import routes from "./config/router";
import Vant from 'vant'
import 'vant/lib/index.css'

const app = createApp(App);
app.use(Vant);
// app.use(Button);
// app.use(NavBar);
// app.use(TabbarItem);
// app.use(Icon);

const router = VueRouter.createRouter({
    history: VueRouter.createMemoryHistory(),  // 这里使用到了hash模式
    routes,
})


app.use(router);
app.mount('#app');












