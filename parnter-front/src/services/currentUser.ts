import myAxios from "../plugins/my-axios";
import {useRoute} from "vue-router";


export const getCurrentUser = async () =>{
    const result = await myAxios.get('/user/currentUser');
    return result.data;
}




