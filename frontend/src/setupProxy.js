const proxy = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(proxy('/seven-wonders-websocket', {
    "target": "http://localhost:8080",
    "ws": true
  }));
};
