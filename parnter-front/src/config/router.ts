import Index from "../pages/Index.vue";
import Term from "../pages/TermPage.vue";
import User from "../pages/UserPage.vue";
import SearchTagPage from "../pages/SearchTagPage.vue";
import UserEditPage from "../pages/UserEditPage.vue";
import SearchResultPage from "../pages/SearchResultPage.vue";
import UserLoginPage from "../pages/UserLoginPage.vue";

const routes = [
    { path: '/', component: Index },
    { path: '/term', component: Term },
    { path: '/user', component: User },
    { path: '/search', component: SearchTagPage },
    { path: '/user/edit', component: UserEditPage },
    { path: '/user/list', component: SearchResultPage },
    { path: '/user/login', component: UserLoginPage },
]

export default routes;