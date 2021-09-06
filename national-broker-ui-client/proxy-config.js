const PROXY_CONFIG = {
  "/national-broker/**": {
    target: "http://localhost:8080",
    changeOrigin: true,

    secure: false,
    logLevel: "debug",
    // cookiePathRewrite: "/" // Doesn't work
    onProxyRes: function (proxyRes, req, res) {
      let cookies = proxyRes.headers["set-cookie"];
      if (cookies) {
        proxyRes.headers["set-cookie"] = cookies.map(cookie =>
          cookie.replace("path=/national-broker/dsd/", "path=/").replace("Path=/national-broker/dsd/", "Path=/").replace("//", "/"));
      }
    },
  }
};

module.exports = PROXY_CONFIG;
