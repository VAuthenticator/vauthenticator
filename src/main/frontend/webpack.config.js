var path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const BUID_DIR = path.resolve(__dirname + "../../../../target/classes/static");

module.exports = {
    mode: 'production',
    entry: {
        login: path.resolve(__dirname, './app/login/index.js'),
        signup: path.resolve(__dirname, './app/registration/index.js'),
        thankyou: path.resolve(__dirname, './app/thankyou/index.js'),
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
            chunks: ['signup'],
            filename: "../templates/signup.html",
            template: path.resolve(__dirname, "../resources/templates/template.html")
        }),
        new HtmlWebpackPlugin({
            chunks: ['thankyou'],
            filename: "../templates/thank-you.html",
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
        filename: 'asset/[name]_[hash]_bundle.js',
        publicPath:"/vauthenticator/",
        path: BUID_DIR
    }
};