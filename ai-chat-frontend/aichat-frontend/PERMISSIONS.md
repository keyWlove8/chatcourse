# 权限系统说明

## 概述
本系统实现了基于角色的权限控制（RBAC），主要区分管理员和普通用户两种角色。

## 用户角色

### 管理员 (isAdmin: true)
- 可以创建新的知识库
- 可以上传文档到知识库
- 可以查看所有知识库
- 可以管理用户和系统设置

### 普通用户 (isAdmin: false)
- 只能查看现有的知识库
- 不能创建知识库
- 不能上传文档
- 可以使用知识库进行聊天

## 权限控制实现

### 1. 组件级权限控制
使用 `PermissionGuard` 组件包装需要权限控制的UI元素：

```vue
<PermissionGuard permission="isAdmin">
  <button @click="createKnowledge">创建知识库</button>
</PermissionGuard>
```

### 2. 代码级权限检查
在 JavaScript 代码中检查权限：

```javascript
if (authStore.userInfo.isAdmin) {
  // 执行管理员操作
}
```

### 3. 后端权限验证
所有敏感操作都需要在后端进行权限验证，前端权限控制仅用于UI展示。

## 权限配置

权限配置在 `src/config/auth.js` 文件中定义：

```javascript
export const AUTH_CONFIG = {
  PERMISSIONS: {
    KNOWLEDGE: {
      CREATE: 'isAdmin',      // 创建知识库需要管理员权限
      UPLOAD: 'isAdmin',      // 上传文档需要管理员权限
      VIEW: true              // 查看知识库所有用户都可以
    }
  }
}
```

## 扩展权限

如需添加新的权限类型，可以：

1. 在 `PermissionGuard` 组件中添加新的权限检查逻辑
2. 在 `AUTH_CONFIG` 中添加新的权限配置
3. 在用户信息中添加新的权限字段

## 安全注意事项

- 前端权限控制仅用于UI展示，不能作为安全验证
- 所有权限验证必须在后端实现
- 敏感操作必须验证用户身份和权限
