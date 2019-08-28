const common = require('./webpack.common.js');

module.exports = Object.assign(common, {
    mode: "development",
    devtool: "inline-source-map"
});
