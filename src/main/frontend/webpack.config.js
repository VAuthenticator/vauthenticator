var path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const BUID_DIR = path.resolve(__dirname + "../../../../target/classes/static");

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
    plugins: [
        new HtmlWebpackPlugin({
            chunks: ['login'],
            filename: "../templates/login.html",
            template: path.resolve(__dirname, "../resources/templates/template.html")
        }),
        new HtmlWebpackPlugin({
            chunks: ['mfa'],
            filename: "../templates/mfa/index.html",
            template: path.resolve(__dirname, "../resources/templates/template.html")
        }),
        new HtmlWebpackPlugin({
            chunks: ['signup'],
            filename: "../templates/signup.html",
            template: path.resolve(__dirname, "../resources/templates/template.html")
        }),
        new HtmlWebpackPlugin({
            chunks: ['resetPassword'],
            filename: "../templates/account/reset-password/reset-password.html",
            template: path.resolve(__dirname, "../resources/templates/template.html")
        }),
        new HtmlWebpackPlugin({
            chunks: ['successfulPasswordReset'],
            filename: "../templates/account/reset-password/successful-password-reset.html",
            template: path.resolve(__dirname, "../resources/templates/template.html")
        })
    ],
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
        publicPath: "/",
        path: BUID_DIR
    }
};