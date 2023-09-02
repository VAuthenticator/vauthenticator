var path = require('path');

const BUID_DIR = path.resolve(__dirname + "/dist");

module.exports = {
    entry: {
        "default_error": path.resolve(__dirname, './app/errors/DefaultGenericErrorPage.tsx'),
        "404_error": path.resolve(__dirname, './app/errors/DefaultGenericErrorPage.tsx'),
        "400_error": path.resolve(__dirname, './app/errors/DefaultGenericErrorPage.tsx'),
        "500_error": path.resolve(__dirname, './app/errors/DefaultGenericErrorPage.tsx'),
        login: path.resolve(__dirname, './app/login/LoginPage.js'),
        mfa: path.resolve(__dirname, './app/mfa/index.js'),
        signup: path.resolve(__dirname, './app/signup/SignUpPage.js'),
        successfulSignUp: path.resolve(__dirname, './app/signup/SuccessfulSignUpPage.js'),
        successfulMailVerify: path.resolve(__dirname, './app/mail-verify/SuccessfulMailVerifyPage.js'),
        resetPasswordChallengeSender: path.resolve(__dirname, './app/reset-password/ResetPasswordChallengeSenderPage.js'),
        successfulResetPasswordMailChallenge: path.resolve(__dirname, './app/reset-password/SuccessfulResetPasswordMailChallengePage.js'),
        resetPassword: path.resolve(__dirname, './app/reset-password/ResetPasswordPage.js'),
        successfulPasswordReset: path.resolve(__dirname, './app/reset-password/SuccessfulPasswordReset.js')
    },
    resolve: {
        extensions: ['.tsx', '.ts', ".js", ".jsx"]
    },
    plugins: [],
    module: {
        rules: [
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader']
            },
            {
                test: /\.tsx?$/,
                use: ['ts-loader'],
                exclude: /node_modules$/,
            },
            {
                test: /\.js$/,
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