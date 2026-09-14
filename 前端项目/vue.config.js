const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  chainWebpack: config => {
    // 项目路径含括号等 glob 特殊字符时，Vue CLI 对 public/index.html 的忽略规则会失效，
    // 导致 public/index.html 被 copy 插件与 HtmlWebpackPlugin 重复产出而报冲突。
    config.plugin('copy').tap(args => {
      const patterns = args[0] && args[0].patterns
      if (patterns && patterns[0]) {
        patterns[0].globOptions = patterns[0].globOptions || {}
        patterns[0].globOptions.ignore = (patterns[0].globOptions.ignore || []).concat('**/index.html')
      }
      return args
    })
  }
})
