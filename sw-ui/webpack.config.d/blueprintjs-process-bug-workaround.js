const webpack = require('webpack');

config.plugins.push(
    // Workaround for `process.env` usages in blueprintjs's classes.ts
    // https://github.com/palantir/blueprint/issues/3739
    new webpack.DefinePlugin({
        "process.env": "{}"
    })
)
