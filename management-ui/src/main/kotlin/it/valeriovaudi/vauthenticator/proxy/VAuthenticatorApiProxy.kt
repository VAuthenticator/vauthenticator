package it.valeriovaudi.vauthenticator.proxy

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.client.RestTemplate
import org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.HandlerMapping
import org.springframework.web.util.UriComponentsBuilder.fromUriString
import java.lang.String.format
import java.util.*
import java.util.Arrays.stream
import java.util.stream.Collectors.toList


class VAuthenticatorApiProxy(@Value("\${vauthenticator.host}") private val vauthenticatorServiceUri: String,
                             private val apiServiceCallProxyService: ApiServiceCallProxyService,
                             private val vauthenticatorRestTemplate: RestTemplate) {

    @RequestMapping(value = ["/secure/api/**"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun proxy(webRequest: WebRequest, method: HttpMethod, @RequestBody(required = false) body: Any?): ResponseEntity<ByteArray> {
        val path = vauthenticatorServiceUri + apiServiceCallProxyService.pathFor(webRequest)
        val requestEntity: HttpEntity<*> = apiServiceCallProxyService.httpEntityFor(body)
        apiServiceCallProxyService.log(method, body, path, requestEntity)
        val response: ResponseEntity<ByteArray> = vauthenticatorRestTemplate.exchange(path, method, requestEntity, ByteArray::class.java)
        apiServiceCallProxyService.log(response)
        return ResponseEntity.status(response.statusCode)
                .headers(apiServiceCallProxyService.responseHeadersFrom(response.headers))
                .body<ByteArray>(response.body)
    }
}


class ApiServiceCallProxyService(val strippedPath : String) {
    fun pathFor(webRequest: WebRequest): String {
        val path = webRequest.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, SCOPE_REQUEST) as String?
        val strippedPath = path!!.substring(strippedPath.length)
        val parameters = webRequest.parameterMap
        val uriComponentsBuilder = fromUriString(strippedPath)
        parameters.forEach { (name: String?, values: Array<String?>?) -> uriComponentsBuilder.queryParam(name!!, stream(values).collect(toList())) }
        return uriComponentsBuilder.build().toUriString()
    }

    fun log(method: HttpMethod, body: Any?, path: String, requestEntity: HttpEntity<*>) {
        log.info("path: $path")
        log.info("method: $method")
        log.info("body: $body")
        log.info("requestEntity.body: " + requestEntity.getBody())
        log.info("requestEntity.header: " + requestEntity.getHeaders())
    }

    fun log(responseEntity: ResponseEntity<*>) {
        log.info("responseEntity.body: " + responseEntity.body)
        log.info("responseEntity.header: " + responseEntity.headers)
        log.info("responseEntity.statusCode: " + responseEntity.statusCode)
    }

    fun httpEntityFor(body: Any?): HttpEntity<*> {
        var requestEntity: HttpEntity<*> = HttpEntity.EMPTY
        if (body != null) {
            requestEntity = HttpEntity(body)
        }
        return requestEntity
    }

    fun responseHeadersFrom(responseHeaders: HttpHeaders): HttpHeaders {
        val result = HttpHeaders()
        setContentLengthHeader(responseHeaders, result)
        setContentTypeHeader(responseHeaders, result)
        setContentDispositionHeader(responseHeaders, result)
        return result
    }

    private fun setContentLengthHeader(responseHeaders: HttpHeaders, result: HttpHeaders) {
        result.set(HttpHeaders.CONTENT_LENGTH, java.lang.String.valueOf(responseHeaders.getContentLength()))
    }

    private fun setContentDispositionHeader(responseHeaders: HttpHeaders, result: HttpHeaders) {
        if (responseHeaders.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            val contentDisposition: ContentDisposition = responseHeaders.getContentDisposition()
            result.set(HttpHeaders.CONTENT_DISPOSITION, format("inline; filename=%s", contentDisposition.getFilename()))
        }
    }

    private fun setContentTypeHeader(responseHeaders: HttpHeaders, result: HttpHeaders) {
        Optional.ofNullable(responseHeaders.getContentType())
                .ifPresent { mediaType -> result.set(HttpHeaders.CONTENT_TYPE, mediaType.toString()) }
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ApiServiceCallProxyService::class.java)
    }
}