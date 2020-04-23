var path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');

const BUID_DIR = path.resolve(__dirname + "../../../../target/classes/static");

const templatePath = path.resolve(__dirname, "../resources/templates/template.html");
module.exports = {
    mode: 'production',
    entry: {
        login: path.resolve(__dirname, './app/login/index.js'),
        registration: path.resolve(__dirname, './app/registration/index.js'),
    },
    resolve: {
        extensions: [".js", ".jsx"]
    },
    plugins: [
        new HtmlWebpackPlugin({
            chunks: ['login'],
            filename: "../templates/login.html",
            template: templatePath
        }),
        new HtmlWebpackPlugin({
            chunks: ['registration'],
            filename: "../templates/signup.html",
            template: templatePath
        }),
        new HtmlWebpackPlugin({
            chunks: ['thank-you'],
            filename: "../templates/thank-you.html",
            template: templatePath
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
        filename: 'vauthenticator/[name]_bundle.js',
        publicPath:"/",
        path: BUID_DIR
    }
};