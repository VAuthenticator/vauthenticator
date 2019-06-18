package it.valeriovaudi.vauthenticator.repository

data class S3Config(var accessKey: String = "",
                    var secretKey: String = "",
                    var region: String = "",
                    var bucketName: String = "")