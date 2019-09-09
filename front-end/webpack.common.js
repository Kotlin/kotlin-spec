const path = require("path");

module.exports = {
    entry: {
        main: path.resolve(__dirname, "./build/kotlin-js-min/main/front-end")
    },
    output: {
        path: path.resolve(__dirname, "./build/js"),
        filename: "[name].js",
        chunkFilename: "[id].js",
        publicPath: "/"
    },
    resolve: {
        modules: [
            "node_modules",
            "kotlin-js-min/main"
        ]
    }
};
