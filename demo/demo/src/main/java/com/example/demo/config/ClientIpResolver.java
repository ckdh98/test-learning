package com.example.demo.config;

import jakarta.servlet.http.HttpServletRequest;

public final class ClientIpResolver {

	private ClientIpResolver() {
	}

	public static String resolveIpv4(HttpServletRequest request) {
		String candidate = firstValue(request.getHeader("X-Forwarded-For"));
		if (candidate == null) {
			candidate = firstValue(request.getHeader("X-Real-IP"));
		}
		if (candidate == null) {
			candidate = request.getRemoteAddr();
		}

		if (candidate == null || candidate.isBlank()) {
			return "0.0.0.0";
		}

		candidate = candidate.trim();

		if ("::1".equals(candidate) || "0:0:0:0:0:0:0:1".equals(candidate)) {
			return "127.0.0.1";
		}

		if (candidate.startsWith("::ffff:")) {
			candidate = candidate.substring(7);
		}

		if (candidate.matches("^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$")) {
			return candidate;
		}

		return "0.0.0.0";
	}

	private static String firstValue(String headerValue) {
		if (headerValue == null || headerValue.isBlank()) {
			return null;
		}
		return headerValue.split(",")[0].trim();
	}
}
