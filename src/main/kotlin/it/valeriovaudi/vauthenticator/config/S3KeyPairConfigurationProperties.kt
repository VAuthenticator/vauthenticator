package it.valeriovaudi.vauthenticator.config

data class S3KeyPairConfigurationProperties(var accessKey: String = "", var secretKey: String = "",
                                            var region: String = "", var bucketName: String = "")