/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

/** 登录返回 */
export type LoginVO = {
    token: string;
    user: {
        id: number;
        username: string;
        nickname?: string | null;
        avatar?: string | null;
        role: string;
        status: string;
        createTime: string;
    };
};
