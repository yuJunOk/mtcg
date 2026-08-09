/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */

/**
 * 用户视图对象
 * 注意：此文件由 OpenAPI 生成，所有字段均为 required（后端实际保证）
 */
export type UserVO = {
    id: number;
    username: string;
    nickname?: string | null;
    avatar?: string | null;
    role: string;
    status: string;
    createTime: string;
};
