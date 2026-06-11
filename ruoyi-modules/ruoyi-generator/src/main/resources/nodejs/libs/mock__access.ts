/**
 * 按钮权限前缀
 */
export const permissionPrefix = "permission:button";

/**
 * 常见按钮权限：
 * - get: 获取
 * - update: 更新
 * - delete: 删除
 * - add: 新增
 */
export const accessControlCodes = {
    get: `${permissionPrefix}:get`,
    update: `${permissionPrefix}:update`,
    delete: `${permissionPrefix}:delete`,
    create: `${permissionPrefix}:create`,
};

export const AccessControlRoles = {
    admin: "admin",
    common: "common",
    // user: "user",
};

/**
 * @zh 权限判断
 * @en Access judgment
 */
export function useAccess() {
    return { hasPerms: () => true, hasRoles: () => true };
}
