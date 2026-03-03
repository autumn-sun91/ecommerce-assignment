package com.example.ecommerceassignment.adapter.input.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.util.UUID

@Component
class MdcLoggingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val traceId =
                request.getHeader("X-Trace-Id")
                    ?: UUID.randomUUID().toString().replace("-", "")

            MDC.put("traceId", traceId)

            response.setHeader("X-Trace-Id", traceId)

            filterChain.doFilter(request, response)
        } finally {
            MDC.clear()
        }
    }
}
