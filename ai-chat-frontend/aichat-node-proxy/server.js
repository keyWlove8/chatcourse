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

// 3. 服务地址
const BACKEND_URL = 'http://localhost:8031'; // ai-chat服务
const STATIC_URL = 'http://localhost:9000';   // ai-static服务
const FRONTEND_URL = 'http://127.0.0.1:3000'; // 修复：强制使用IPv4地址

// 4. 测试路由
app.get('/test', (req, res) => {
  res.json({ 
    message: '代理服务正常运行',
    timestamp: new Date().toISOString(),
    frontend: FRONTEND_URL,
    backend: BACKEND_URL
  });
});

// 4.1 处理favicon.ico
app.get('/favicon.ico', (req, res) => {
  res.redirect(`${FRONTEND_URL}/favicon.ico`);
});

// 4.2 处理WebSocket请求
app.get('/ws', (req, res) => {
  res.status(404).json({ message: 'WebSocket not supported in proxy mode' });
});

// 5. 代理路由处理
app.use('/', async (req, res) => {
  try {
    const path = req.originalUrl;
    
    // 如果是API请求，转发到ai-chat服务
    if (path.startsWith('/api')) {
      return await proxyToBackend(req, res);
    }
    
    // 如果是静态文件请求，转发到ai-static服务
    if (path.startsWith('/download')) {
      return await proxyToStatic(req, res);
    }
    
    // 其他请求转发到前端
    return await proxyToFrontend(req, res);
    
  } catch (error) {
    console.error('代理错误:', error);
    res.status(500).json({ message: '代理服务错误' });
  }
});

// 6. 代理到前端
async function proxyToFrontend(req, res) {
  try {
    const frontendUrl = `${FRONTEND_URL}${req.originalUrl}`;
    console.log(`前端代理：${req.method} ${req.originalUrl} → ${frontendUrl}`);
    
    // 检查是否是字体文件或图片文件
    const isFontFile = req.originalUrl.includes('.woff') || 
                      req.originalUrl.includes('.woff2') || 
                      req.originalUrl.includes('.ttf') || 
                      req.originalUrl.includes('.eot');
    
    const isImageFile = req.originalUrl.includes('.svg') || 
                       req.originalUrl.includes('.png') || 
                       req.originalUrl.includes('.jpg') || 
                       req.originalUrl.includes('.jpeg') || 
                       req.originalUrl.includes('.gif') || 
                       req.originalUrl.includes('.ico') ||
                       req.originalUrl.includes('.webp') ||
                       req.originalUrl.includes('.avif') ||
                       req.originalUrl.includes('.bmp') ||
                       req.originalUrl.includes('.tiff');
    
    const isMediaFile = req.originalUrl.includes('.mp4') || 
                       req.originalUrl.includes('.webm') || 
                       req.originalUrl.includes('.avi') || 
                       req.originalUrl.includes('.mov') ||
                       req.originalUrl.includes('.mp3') || 
                       req.originalUrl.includes('.wav') || 
                       req.originalUrl.includes('.ogg') || 
                       req.originalUrl.includes('.aac') ||
                       req.originalUrl.includes('.m4a') ||
                       req.originalUrl.includes('.flac') ||
                       req.originalUrl.includes('.opus');
    
    const isDocumentFile = req.originalUrl.includes('.pdf') || 
                          req.originalUrl.includes('.doc') || 
                          req.originalUrl.includes('.docx') || 
                          req.originalUrl.includes('.xls') || 
                          req.originalUrl.includes('.xlsx') || 
                          req.originalUrl.includes('.ppt') || 
                          req.originalUrl.includes('.pptx') ||
                          req.originalUrl.includes('.txt') ||
                          req.originalUrl.includes('.csv');
    
    const isArchiveFile = req.originalUrl.includes('.zip') || 
                         req.originalUrl.includes('.rar') || 
                         req.originalUrl.includes('.7z') || 
                         req.originalUrl.includes('.tar') || 
                         req.originalUrl.includes('.gz');
    
    const isCodeFile = req.originalUrl.includes('.js') || 
                      req.originalUrl.includes('.ts') || 
                      req.originalUrl.includes('.jsx') || 
                      req.originalUrl.includes('.tsx') || 
                      req.originalUrl.includes('.vue') || 
                      req.originalUrl.includes('.css') || 
                      req.originalUrl.includes('.scss') || 
                      req.originalUrl.includes('.sass') || 
                      req.originalUrl.includes('.less') || 
                      req.originalUrl.includes('.styl') ||
                      req.originalUrl.includes('.json') ||
                      req.originalUrl.includes('.xml') ||
                      req.originalUrl.includes('.yaml') ||
                      req.originalUrl.includes('.yml') ||
                      req.originalUrl.includes('.md') ||
                      req.originalUrl.includes('.html') ||
                      req.originalUrl.includes('.htm');
    
    const isBinaryFile = isFontFile || isImageFile || isMediaFile || isDocumentFile || isArchiveFile || isCodeFile;
    
    const response = await axios({
      method: req.method,
      url: frontendUrl,
      data: req.body,
      params: req.query,
      headers: {
        'User-Agent': req.headers['user-agent'] || '',
        'Accept': isBinaryFile ? '*/*' : (req.headers['accept'] || '*/*'),
        'Accept-Language': req.headers['accept-language'] || '',
        'Accept-Encoding': req.headers['accept-encoding'] || ''
      },
      validateStatus: function (status) {
        return status < 500;
      },
      timeout: 10000,
      responseType: isBinaryFile ? 'arraybuffer' : 'text'
    });
    
    console.log(`前端服务响应成功：${response.status} - ${req.originalUrl}`);
    console.log(`文件类型检测：字体=${isFontFile}, 图片=${isImageFile}, 媒体=${isMediaFile}, 文档=${isDocumentFile}, 压缩=${isArchiveFile}, 代码=${isCodeFile}, 二进制=${isBinaryFile}`);
    
    // 设置正确的MIME类型
    if (isFontFile) {
      if (req.originalUrl.includes('.woff2')) {
        res.set('Content-Type', 'font/woff2');
      } else if (req.originalUrl.includes('.woff')) {
        res.set('Content-Type', 'font/woff');
      } else if (req.originalUrl.includes('.ttf')) {
        res.set('Content-Type', 'font/ttf');
      } else if (req.originalUrl.includes('.eot')) {
        res.set('Content-Type', 'application/vnd.ms-fontobject');
      }
    } else if (isImageFile) {
      if (req.originalUrl.includes('.svg')) {
        res.set('Content-Type', 'image/svg+xml');
      } else if (req.originalUrl.includes('.png')) {
        res.set('Content-Type', 'image/png');
      } else if (req.originalUrl.includes('.jpg') || req.originalUrl.includes('.jpeg')) {
        res.set('Content-Type', 'image/jpeg');
      } else if (req.originalUrl.includes('.gif')) {
        res.set('Content-Type', 'image/gif');
      } else if (req.originalUrl.includes('.ico')) {
        res.set('Content-Type', 'image/x-icon');
      } else if (req.originalUrl.includes('.webp')) {
        res.set('Content-Type', 'image/webp');
      } else if (req.originalUrl.includes('.avif')) {
        res.set('Content-Type', 'image/avif');
      } else if (req.originalUrl.includes('.bmp')) {
        res.set('Content-Type', 'image/bmp');
      } else if (req.originalUrl.includes('.tiff')) {
        res.set('Content-Type', 'image/tiff');
      }
    } else if (isMediaFile) {
      if (req.originalUrl.includes('.mp4')) {
        res.set('Content-Type', 'video/mp4');
      } else if (req.originalUrl.includes('.webm')) {
        res.set('Content-Type', 'video/webm');
      } else if (req.originalUrl.includes('.avi')) {
        res.set('Content-Type', 'video/x-msvideo');
      } else if (req.originalUrl.includes('.mov')) {
        res.set('Content-Type', 'video/quicktime');
      } else if (req.originalUrl.includes('.mp3')) {
        res.set('Content-Type', 'audio/mpeg');
      } else if (req.originalUrl.includes('.wav')) {
        res.set('Content-Type', 'audio/wav');
      } else if (req.originalUrl.includes('.ogg')) {
        res.set('Content-Type', 'audio/ogg');
      } else if (req.originalUrl.includes('.aac')) {
        res.set('Content-Type', 'audio/aac');
      }
    } else if (isDocumentFile) {
      if (req.originalUrl.includes('.pdf')) {
        res.set('Content-Type', 'application/pdf');
      } else if (req.originalUrl.includes('.doc') || req.originalUrl.includes('.docx')) {
        res.set('Content-Type', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document');
      } else if (req.originalUrl.includes('.xls') || req.originalUrl.includes('.xlsx')) {
        res.set('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
      } else if (req.originalUrl.includes('.ppt') || req.originalUrl.includes('.pptx')) {
        res.set('Content-Type', 'application/vnd.openxmlformats-officedocument.presentationml.presentation');
      } else if (req.originalUrl.includes('.txt')) {
        res.set('Content-Type', 'text/plain');
      } else if (req.originalUrl.includes('.csv')) {
        res.set('Content-Type', 'text/csv');
      }
    } else if (isArchiveFile) {
      if (req.originalUrl.includes('.zip')) {
        res.set('Content-Type', 'application/zip');
      } else if (req.originalUrl.includes('.rar')) {
        res.set('Content-Type', 'application/vnd.rar');
      } else if (req.originalUrl.includes('.7z')) {
        res.set('Content-Type', 'application/x-7z-compressed');
      } else if (req.originalUrl.includes('.tar')) {
        res.set('Content-Type', 'application/x-tar');
      } else if (req.originalUrl.includes('.gz')) {
        res.set('Content-Type', 'application/gzip');
      }
    } else if (isCodeFile) {
      if (req.originalUrl.includes('.js') || req.originalUrl.includes('.ts') || req.originalUrl.includes('.jsx') || req.originalUrl.includes('.tsx')) {
        res.set('Content-Type', 'application/javascript');
      } else if (req.originalUrl.includes('.vue')) {
        res.set('Content-Type', 'text/plain');
      } else if (req.originalUrl.includes('.css') || req.originalUrl.includes('.scss') || req.originalUrl.includes('.sass') || req.originalUrl.includes('.less') || req.originalUrl.includes('.styl')) {
        res.set('Content-Type', 'text/css');
      } else if (req.originalUrl.includes('.json')) {
        res.set('Content-Type', 'application/json');
      } else if (req.originalUrl.includes('.xml')) {
        res.set('Content-Type', 'application/xml');
      } else if (req.originalUrl.includes('.yaml') || req.originalUrl.includes('.yml')) {
        res.set('Content-Type', 'text/yaml');
      } else if (req.originalUrl.includes('.md')) {
        res.set('Content-Type', 'text/markdown');
      } else if (req.originalUrl.includes('.html') || req.originalUrl.includes('.htm')) {
        res.set('Content-Type', 'text/html');
      }
    }
    
    // 转发其他响应头
    Object.keys(response.headers).forEach(key => {
      if (key.toLowerCase() !== 'content-length' && 
          key.toLowerCase() !== 'transfer-encoding' &&
          key.toLowerCase() !== 'content-type') {
        res.set(key, response.headers[key]);
      }
    });
    
    // 转发响应体
    if (isBinaryFile) {
      res.send(Buffer.from(response.data));
    } else {
      res.status(response.status).send(response.data);
    }
    
  } catch (error) {
    console.error('前端代理错误:', error.message);
    console.error('请求路径:', req.originalUrl);
    console.error('错误详情:', error.response?.status, error.response?.data);
    
    res.status(500).json({ 
      message: '前端服务不可用',
      error: error.message,
      details: {
        status: error.response?.status,
        data: error.response?.data,
        frontendUrl: FRONTEND_URL,
        requestPath: req.originalUrl
      }
    });
  }
}

// 7. 代理到静态文件服务
async function proxyToStatic(req, res) {
  try {
    // 直接转发/download路径到ai-static服务
    const staticUrl = `${STATIC_URL}${req.originalUrl}`;
    console.log(`静态文件代理：${req.method} ${req.originalUrl} → ${staticUrl}`);
    
    // 检查是否是二进制文件
    const requestPath = req.originalUrl;
    const isImageFile = requestPath.includes('.svg') || 
                       requestPath.includes('.png') || 
                       requestPath.includes('.jpg') || 
                       requestPath.includes('.jpeg') || 
                       requestPath.includes('.gif') || 
                       requestPath.includes('.ico') ||
                       requestPath.includes('.webp') ||
                       requestPath.includes('.avif') ||
                       requestPath.includes('.bmp') ||
                       requestPath.includes('.tiff');
    
    const isMediaFile = requestPath.includes('.mp4') || 
                       requestPath.includes('.webm') || 
                       requestPath.includes('.avi') || 
                       requestPath.includes('.mov') ||
                       requestPath.includes('.mp3') || 
                       requestPath.includes('.wav') || 
                       requestPath.includes('.ogg') || 
                       requestPath.includes('.aac') ||
                       requestPath.includes('.m4a') ||
                       requestPath.includes('.flac') ||
                       requestPath.includes('.opus');
    
    const isDocumentFile = requestPath.includes('.pdf') || 
                          requestPath.includes('.doc') || 
                          requestPath.includes('.docx') || 
                          requestPath.includes('.xls') || 
                          requestPath.includes('.xlsx') || 
                          requestPath.includes('.ppt') || 
                          requestPath.includes('.pptx') ||
                          requestPath.includes('.txt') ||
                          requestPath.includes('.csv');
    
    const isBinaryFile = isImageFile || isMediaFile || isDocumentFile;
    
    const response = await axios({
      method: req.method,
      url: staticUrl,
      data: req.body,
      params: req.query,
      headers: {
        'User-Agent': req.headers['user-agent'] || '',
        'Accept': isBinaryFile ? '*/*' : (req.headers['accept'] || '*/*'),
        'Accept-Language': req.headers['accept-language'] || '',
        'Accept-Encoding': req.headers['accept-encoding'] || ''
      },
      validateStatus: function (status) {
        return status < 500;
      },
      timeout: 10000,
      responseType: isBinaryFile ? 'arraybuffer' : 'text'
    });
    
    console.log(`静态文件服务响应成功：${response.status} - ${req.originalUrl}`);
    
    // 设置正确的MIME类型
    if (isImageFile) {
      if (requestPath.includes('.svg')) {
        res.set('Content-Type', 'image/svg+xml');
      } else if (requestPath.includes('.png')) {
        res.set('Content-Type', 'image/png');
      } else if (requestPath.includes('.jpg') || requestPath.includes('.jpeg')) {
        res.set('Content-Type', 'image/jpeg');
      } else if (requestPath.includes('.gif')) {
        res.set('Content-Type', 'image/gif');
      } else if (requestPath.includes('.ico')) {
        res.set('Content-Type', 'image/x-icon');
      } else if (requestPath.includes('.webp')) {
        res.set('Content-Type', 'image/webp');
      } else if (requestPath.includes('.avif')) {
        res.set('Content-Type', 'image/avif');
      } else if (requestPath.includes('.bmp')) {
        res.set('Content-Type', 'image/bmp');
      } else if (requestPath.includes('.tiff')) {
        res.set('Content-Type', 'image/tiff');
      }
    } else if (isMediaFile) {
      if (requestPath.includes('.mp4')) {
        res.set('Content-Type', 'video/mp4');
      } else if (requestPath.includes('.webm')) {
        res.set('Content-Type', 'video/webm');
      } else if (requestPath.includes('.avi')) {
        res.set('Content-Type', 'video/x-msvideo');
      } else if (requestPath.includes('.mov')) {
        res.set('Content-Type', 'video/quicktime');
      } else if (requestPath.includes('.mp3')) {
        res.set('Content-Type', 'audio/mpeg');
      } else if (requestPath.includes('.wav')) {
        res.set('Content-Type', 'audio/wav');
      } else if (requestPath.includes('.ogg')) {
        res.set('Content-Type', 'audio/ogg');
      } else if (requestPath.includes('.aac')) {
        res.set('Content-Type', 'audio/aac');
      } else if (requestPath.includes('.m4a')) {
        res.set('Content-Type', 'audio/mp4');
      } else if (requestPath.includes('.flac')) {
        res.set('Content-Type', 'audio/flac');
      } else if (requestPath.includes('.opus')) {
        res.set('Content-Type', 'audio/opus');
      }
    } else if (isDocumentFile) {
      if (requestPath.includes('.pdf')) {
        res.set('Content-Type', 'application/pdf');
      } else if (requestPath.includes('.doc') || requestPath.includes('.docx')) {
        res.set('Content-Type', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document');
      } else if (requestPath.includes('.xls') || requestPath.includes('.xlsx')) {
        res.set('Content-Type', 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet');
      } else if (requestPath.includes('.ppt') || requestPath.includes('.pptx')) {
        res.set('Content-Type', 'application/vnd.openxmlformats-officedocument.presentationml.presentation');
      } else if (requestPath.includes('.txt')) {
        res.set('Content-Type', 'text/plain');
      } else if (requestPath.includes('.csv')) {
        res.set('Content-Type', 'text/csv');
      }
    }
    
    // 转发其他响应头
    Object.keys(response.headers).forEach(key => {
      if (key.toLowerCase() !== 'content-length' && 
          key.toLowerCase() !== 'transfer-encoding' &&
          key.toLowerCase() !== 'content-type') {
        res.set(key, response.headers[key]);
      }
    });
    
    // 转发响应体
    if (isBinaryFile) {
      res.send(Buffer.from(response.data));
    } else {
      res.status(response.status).send(response.data);
    }
    
  } catch (error) {
    console.error('静态文件代理错误:', error.message);
    console.error('请求路径:', req.originalUrl);
    console.error('错误详情:', error.response?.status, error.response?.data);
    
    res.status(500).json({ 
      message: '静态文件服务不可用',
      error: error.message,
      details: {
        status: error.response?.status,
        data: error.response?.data,
        staticUrl: STATIC_URL,
        requestPath: req.originalUrl
      }
    });
  }
}

// 8. 代理到后端
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

// 8. 处理文件上传
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
          console.log(`添加文本字段: ${key} = ${req.body[key]}`);
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

// 9. 启动服务
app.listen(PORT, () => {
  console.log(`统一代理服务已启动：http://localhost:${PORT}`);
  console.log(`AI聊天服务地址：${BACKEND_URL}`);
  console.log(`静态文件服务地址：${STATIC_URL}`);
  console.log(`前端服务地址：${FRONTEND_URL}`);
  console.log('路由规则：');
  console.log('  /api/* → AI聊天服务 (8031端口)');
  console.log('  /static/* → 静态文件服务 (9000端口)');
  console.log('  其他请求 → 前端服务 (3000端口)');
  console.log('所有请求将通过此代理分发...');
});
