/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AdminResetPasswordDTO } from '../models/AdminResetPasswordDTO';
import type { AdminUserCreateDTO } from '../models/AdminUserCreateDTO';
import type { AdminUserUpdateDTO } from '../models/AdminUserUpdateDTO';
import type { CardCreateDTO } from '../models/CardCreateDTO';
import type { CardQueryDTO } from '../models/CardQueryDTO';
import type { CardUpdateDTO } from '../models/CardUpdateDTO';
import type { ChangePasswordDTO } from '../models/ChangePasswordDTO';
import type { ProductCreateDTO } from '../models/ProductCreateDTO';
import type { ProductQueryDTO } from '../models/ProductQueryDTO';
import type { ProductUpdateDTO } from '../models/ProductUpdateDTO';
import type { ResultCardVO } from '../models/ResultCardVO';
import type { ResultDashboardStatsVO } from '../models/ResultDashboardStatsVO';
import type { ResultHealthVO } from '../models/ResultHealthVO';
import type { ResultLoginVO } from '../models/ResultLoginVO';
import type { ResultLong } from '../models/ResultLong';
import type { ResultPageVOCardVO } from '../models/ResultPageVOCardVO';
import type { ResultPageVOProductVO } from '../models/ResultPageVOProductVO';
import type { ResultPageVOUserVO } from '../models/ResultPageVOUserVO';
import type { ResultProductVO } from '../models/ResultProductVO';
import type { ResultUserVO } from '../models/ResultUserVO';
import type { ResultVoid } from '../models/ResultVoid';
import type { UserLoginDTO } from '../models/UserLoginDTO';
import type { UserQueryDTO } from '../models/UserQueryDTO';
import type { UserRegisterDTO } from '../models/UserRegisterDTO';
import type { UserUpdateDTO } from '../models/UserUpdateDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export class Service {
    constructor(public readonly httpRequest: BaseHttpRequest) {}
    /**
     * 获取当前用户信息
     * @returns ResultUserVO OK
     * @throws ApiError
     */
    public me(): CancelablePromise<ResultUserVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/users/me',
        });
    }
    /**
     * 更新当前用户资料
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public updateMe(
        requestBody: UserUpdateDTO,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/users/me',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 修改当前用户密码
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public changePassword(
        requestBody: ChangePasswordDTO,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/users/me/password',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 根据ID查询用户
     * @param id
     * @returns ResultUserVO OK
     * @throws ApiError
     */
    public get(
        id: number,
    ): CancelablePromise<ResultUserVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/admin/users/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * 更新用户
     * @param id
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public update(
        id: number,
        requestBody: AdminUserUpdateDTO,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/admin/users/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 删除用户
     * @param id
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public delete(
        id: number,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/admin/users/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * 根据ID查询产品
     * @param id
     * @returns ResultProductVO OK
     * @throws ApiError
     */
    public get1(
        id: number,
    ): CancelablePromise<ResultProductVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/admin/products/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * 更新产品
     * @param id
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public update1(
        id: number,
        requestBody: ProductUpdateDTO,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/admin/products/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 删除产品
     * @param id
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public delete1(
        id: number,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/admin/products/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * 根据ID查询卡牌
     * @param id
     * @returns ResultCardVO OK
     * @throws ApiError
     */
    public get2(
        id: number,
    ): CancelablePromise<ResultCardVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/admin/cards/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * 更新卡牌
     * @param id
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public update2(
        id: number,
        requestBody: CardUpdateDTO,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'PUT',
            url: '/admin/cards/{id}',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 删除卡牌
     * @param id
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public delete2(
        id: number,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'DELETE',
            url: '/admin/cards/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * 用户注册
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public register(
        requestBody: UserRegisterDTO,
    ): CancelablePromise<ResultLong> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/auth/register',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 用户登录
     * @param requestBody
     * @returns ResultLoginVO OK
     * @throws ApiError
     */
    public login(
        requestBody: UserLoginDTO,
    ): CancelablePromise<ResultLoginVO> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/auth/login',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 分页查询用户列表
     * @param query
     * @returns ResultPageVOUserVO OK
     * @throws ApiError
     */
    public list(
        query: UserQueryDTO,
    ): CancelablePromise<ResultPageVOUserVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/admin/users',
            query: {
                'query': query,
            },
        });
    }
    /**
     * 创建用户
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public create(
        requestBody: AdminUserCreateDTO,
    ): CancelablePromise<ResultLong> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/admin/users',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 分页查询产品列表
     * @param query
     * @returns ResultPageVOProductVO OK
     * @throws ApiError
     */
    public list1(
        query: ProductQueryDTO,
    ): CancelablePromise<ResultPageVOProductVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/admin/products',
            query: {
                'query': query,
            },
        });
    }
    /**
     * 新增产品
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public create1(
        requestBody: ProductCreateDTO,
    ): CancelablePromise<ResultLong> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/admin/products',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 分页查询卡牌列表
     * @param query
     * @returns ResultPageVOCardVO OK
     * @throws ApiError
     */
    public list2(
        query: CardQueryDTO,
    ): CancelablePromise<ResultPageVOCardVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/admin/cards',
            query: {
                'query': query,
            },
        });
    }
    /**
     * 新增卡牌
     * @param requestBody
     * @returns ResultLong OK
     * @throws ApiError
     */
    public create2(
        requestBody: CardCreateDTO,
    ): CancelablePromise<ResultLong> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/admin/cards',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 更新用户状态
     * @param id
     * @param status
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public updateStatus(
        id: number,
        status: string,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/admin/users/{id}/status',
            path: {
                'id': id,
            },
            query: {
                'status': status,
            },
        });
    }
    /**
     * 重置用户密码
     * @param id
     * @param requestBody
     * @returns ResultVoid OK
     * @throws ApiError
     */
    public resetPassword(
        id: number,
        requestBody: AdminResetPasswordDTO,
    ): CancelablePromise<ResultVoid> {
        return this.httpRequest.request({
            method: 'PATCH',
            url: '/admin/users/{id}/password',
            path: {
                'id': id,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 健康检查
     * @returns ResultHealthVO OK
     * @throws ApiError
     */
    public health(): CancelablePromise<ResultHealthVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/health',
        });
    }
    /**
     * 获取仪表盘统计
     * @returns ResultDashboardStatsVO OK
     * @throws ApiError
     */
    public stats(): CancelablePromise<ResultDashboardStatsVO> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/admin/dashboard/stats',
        });
    }
}
