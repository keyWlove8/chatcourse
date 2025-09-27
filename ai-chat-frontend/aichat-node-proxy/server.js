const express = require('express');
const axios = require('axios');
const cors = require('cors');
const multer = require('multer');

const app = express();
const PORT = 8080; // 统一代理监听8080端口

// 1. 必须的中间件
app.use(cors()); // 允许跨域
app.use(express.json()); // 解析JSON请求体
app.use(express.urlencoded({ extended: true })); // 解析表单请求体

// 2. 配置multer用于处理文件上传
const upload = multer({ storage: multer.memoryStorage() });

// 3. 服务地址（开发模式 - 前端使用pnpm serve运行）
const BACKEND_URL = 'http://localhost:8031'; // ai-chat服务
const STATIC_URL = 'http://localhost:9000';   // ai-static服务
const FRONTEND_URL = 'http://localhost:3000'; // 前端服务（pnpm serve）


// 4. 测试路由
app.get('/test', (req, res) => {
  res.json({ 
    message: '代理服务正常运行',
    timestamp: new Date().toISOString(),
    frontend: FRONTEND_URL,
    backend: BACKEND_URL
  });
});

// 5. 处理favicon.ico
app.get('/favicon.ico', (req, res) => {
  res.redirect(`${FRONTEND_URL}/favicon.ico`);
});

// 6. 处理WebSocket请求
app.get('/ws', (req, res) => {
  res.status(404).json({ message: 'WebSocket not supported in proxy mode' });
});

// 7. 代理路由处理
app.use('/', async (req, res) => {
  try {
    const requestPath = req.originalUrl;
    
    // 如果是API请求，转发到ai-chat服务
    if (requestPath.startsWith('/api')) {
      return await proxyToBackend(req, res);
    }
    
    // 如果是静态文件请求，转发到ai-static服务
    if (requestPath.startsWith('/download')) {
      return await proxyToStatic(req, res);
    }
    
    // 其他请求转发到前端服务（pnpm serve）
    return await proxyToFrontend(req, res);
    
  } catch (error) {
    console.error('代理错误:', error);
    res.status(500).json({ message: '代理服务错误' });
  }
});

// 8. 代理到前端服务
async function proxyToFrontend(req, res) {
  try {
    const frontendUrl = `${FRONTEND_URL}${req.originalUrl}`;
    console.log(`前端代理：${req.method} ${req.originalUrl} → ${frontendUrl}`);
    
    const response = await axios({
      method: req.method,
      url: frontendUrl,
      data: req.body,
      params: req.query,
      headers: {
        'User-Agent': req.headers['user-agent'] || '',
        'Accept': req.headers['accept'] || '*/*',
        'Accept-Language': req.headers['accept-language'] || '',
        'Accept-Encoding': req.headers['accept-encoding'] || ''
      },
      validateStatus: function (status) {
        return status < 500;
      },
      timeout: 10000,
      responseType: 'arraybuffer'  // 重要：设置为arraybuffer以正确处理二进制文件
    });
    
    console.log(`前端服务响应成功：${response.status} - ${req.originalUrl}`);
    
    // 设置响应头
    if (response.headers['content-type']) {
      res.set('Content-Type', response.headers['content-type']);
    }
    if (response.headers['content-length']) {
      res.set('Content-Length', response.headers['content-length']);
    }
    if (response.headers['last-modified']) {
      res.set('Last-Modified', response.headers['last-modified']);
    }
    if (response.headers['etag']) {
      res.set('ETag', response.headers['etag']);
    }
    if (response.headers['cache-control']) {
      res.set('Cache-Control', response.headers['cache-control']);
    }
    
    // 转发响应
    res.status(response.status).send(response.data);
    
  } catch (error) {
    console.error('前端代理错误:', error.message);
    res.status(500).json({ 
      message: '前端服务不可用',
      error: error.message
    });
  }
}

// 9. 代理到静态文件服务
async function proxyToStatic(req, res) {
  try {
    const staticUrl = `${STATIC_URL}${req.originalUrl}`;
    console.log(`静态文件代理：${req.method} ${req.originalUrl} → ${staticUrl}`);
    
    const response = await axios({
      method: req.method,
      url: staticUrl,
      data: req.body,
      params: req.query,
      headers: {
        'Authorization': req.headers.authorization || '',
        'Content-Type': req.headers['content-type'] || 'application/json',
        'User-Agent': req.headers['user-agent'] || '',
        'Accept': req.headers['accept'] || '*/*',
        'Accept-Language': req.headers['accept-language'] || '',
        'Accept-Encoding': req.headers['accept-encoding'] || '',
        'X-Forwarded-For': req.ip
      },
      validateStatus: function (status) {
        return status < 500;
      },
      timeout: 10000,
      responseType: 'arraybuffer'  // 重要：设置为arraybuffer以正确处理二进制文件
    });
    
    console.log(`静态文件服务响应成功：${response.status} - ${req.originalUrl}`);
    
    // 转发响应（静态文件直接返回内容）
    res.status(response.status);
    
    // 设置正确的Content-Type
    if (response.headers['content-type']) {
      res.set('Content-Type', response.headers['content-type']);
    }
    
    // 设置其他重要头部
    if (response.headers['content-length']) {
      res.set('Content-Length', response.headers['content-length']);
    }
    if (response.headers['last-modified']) {
      res.set('Last-Modified', response.headers['last-modified']);
    }
    if (response.headers['etag']) {
      res.set('ETag', response.headers['etag']);
    }
    if (response.headers['cache-control']) {
      res.set('Cache-Control', response.headers['cache-control']);
    }
    
    // 直接发送文件内容
    res.send(response.data);
    
  } catch (error) {
    const statusCode = error.response?.status || 500;
    const errorMessage = error.response?.data?.message || `静态文件代理失败：${error.message}`;
    
    console.error(`静态文件代理错误：${statusCode} - ${errorMessage}`);
    res.status(statusCode).json({
      code: statusCode,
      message: errorMessage
    });
  }
}

// 10. 代理到后端
async function proxyToBackend(req, res) {
  try {
    const backendUrl = `${BACKEND_URL}${req.originalUrl}`;
    console.log(`API代理：${req.method} ${req.originalUrl} → ${backendUrl}`);
    
    // 检查是否是文件上传请求
    if (req.headers['content-type'] && req.headers['content-type'].includes('multipart/form-data')) {
      return await handleFileUpload(req, res, backendUrl);
    }
    
    // 普通请求处理
    const response = await axios({
      method: req.method,
      url: backendUrl,
      data: req.body,
      params: req.query,
      headers: {
        'Authorization': req.headers.authorization || '',
        'Content-Type': req.headers['content-type'] || 'application/json',
        'X-Forwarded-For': req.ip
      }
    });
    
    // 将后端响应返回给前端
    res.status(response.status).json(response.data);
    console.log(`API代理成功：${backendUrl} (状态码：${response.status})`);
    
  } catch (error) {
    const statusCode = error.response?.status || 500;
    const errorMessage = error.response?.data?.message || `API代理失败：${error.message}`;
    
    console.error(`API代理错误：${statusCode} - ${errorMessage}`);
    res.status(statusCode).json({
      code: statusCode,
      message: errorMessage
    });
  }
}

// 11. 处理文件上传
async function handleFileUpload(req, res, backendUrl) {
  upload.any()(req, res, async (err) => {
    if (err) {
      console.error('文件上传处理错误:', err);
      return res.status(400).json({ message: '文件上传处理失败' });
    }
    
    try {
      // 准备FormData数据
      const FormData = require('form-data');
      const formData = new FormData();
      
      // 添加文本字段
      if (req.body) {
        Object.keys(req.body).forEach(key => {
          formData.append(key, req.body[key]);
        });
      }
      
      // 添加文件字段
      if (req.files && req.files.length > 0) {
        req.files.forEach(file => {
          let fileFieldName = 'file';
          
          if (req.originalUrl.includes('/chat/send')) {
            fileFieldName = 'image';
          } else if (req.originalUrl.includes('/chat/voice')) {
            fileFieldName = 'audio';
          } else if (req.originalUrl.includes('/knowledge/upload')) {
            fileFieldName = 'file';
          } else if (req.originalUrl.includes('/character/upload-avatar')) {
            fileFieldName = 'file';
          }
          formData.append(fileFieldName, file.buffer, {
            filename: file.originalname,
            contentType: file.mimetype
          });
        });
      }
      
      // 转发请求到后端
      const response = await axios({
        method: req.method,
        url: backendUrl,
        data: formData,
        params: req.query,
        headers: {
          ...formData.getHeaders(),
          'Authorization': req.headers.authorization || '',
          'X-Forwarded-For': req.ip
        }
      });
      
      // 将后端响应返回给前端
      res.status(response.status).json(response.data);
      console.log(`文件上传转发成功：${backendUrl} (状态码：${response.status})`);
      
    } catch (error) {
      const statusCode = error.response?.status || 500;
      const errorMessage = error.response?.data?.message || `文件上传转发失败：${error.message}`;
      
      console.error(`文件上传转发错误：${statusCode} - ${errorMessage}`);
      res.status(statusCode).json({
        code: statusCode,
        message: errorMessage
      });
    }
  });
}

// 12. 启动服务
app.listen(PORT, () => {
  console.log(`统一代理服务已启动：http://localhost:${PORT}`);
  console.log(`AI聊天服务地址：${BACKEND_URL}`);
  console.log(`静态文件服务地址：${STATIC_URL}`);
  console.log(`前端服务地址：${FRONTEND_URL}`);
  console.log('路由规则：');
  console.log('  /api/* → AI聊天服务 (8031端口)');
  console.log('  /download/* → 静态文件服务 (9000端口)');
  console.log('  其他请求 → 前端服务 (3000端口)');
  console.log('所有请求将通过此代理分发...');
});
