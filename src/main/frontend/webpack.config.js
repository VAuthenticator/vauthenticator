var path = require('path');

const BUID_DIR = path.resolve(__dirname + "/dist");

module.exports = {
    mode: 'development',
    entry: {
        login: path.resolve(__dirname, './app/login/index.js'),
        mfa: path.resolve(__dirname, './app/mfa/index.js'),
        signup: path.resolve(__dirname, './app/signup/index.js'),
        resetPassword: path.resolve(__dirname, './app/reset-password/reset-password.js'),
        successfulPasswordReset: path.resolve(__dirname, './app/reset-password/successful-password-reset.js')
    },
    resolve: {
        extensions: [".js", ".jsx"]
    },
    plugins: [],
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: path.join(__dirname, "."),
                exclude: path.resolve(__dirname, "node_modules"),
                use: {
                    loader: "babel-loader",
                    options: {
                        presets: ['@babel/env', '@babel/react']
                    }
                }

            }
        ]
    },
    output: {
        filename: 'asset/[name]_bundle.js',
        path: BUID_DIR
    }
};