/**
 * 用户类别
 */
export type UserType = {
    id: number;
    userName: string;
    userAccount: string;
    avatarUrl?: string;
    profile?: string;
    userGender:number;
    userPhone: string;
    userEmail: string;
    userStatus: number;
    userRole: number;
    tags: string;
    createTime: Date;
};
