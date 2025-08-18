package com.moyoy.api.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Optional;

import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class CookieUtils {

	public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {

		Cookie[] cookies = request.getCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					return Optional.of(cookie);
				}
			}
		}

		return Optional.empty();
	}

	public static void addCookie(HttpServletResponse response, String name, String value) {

		ResponseCookie cookie = ResponseCookie.from(name, value)
			.path("/")
			.httpOnly(true)
			.maxAge(1800)
			.sameSite("None") // test : None , prod : Strict, 추후 환경 변수화
			.secure(true)
			.build();

		response.addHeader("Set-Cookie", cookie.toString());
	}

	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(name)) {
					cookie.setValue("");
					cookie.setPath("/");
					cookie.setMaxAge(0);
					response.addCookie(cookie);
				}
			}
		}
	}

	public static String serialize(Serializable object) {

		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
			 ObjectOutputStream oos = new ObjectOutputStream(baos)
		) {
			oos.writeObject(object);
			return Base64.getUrlEncoder().encodeToString(baos.toByteArray());
		}
		catch (IOException e) {
			throw new IllegalStateException("쿠키 직렬화 실패", e);
		}
	}

	public static <T> T deserialize(Cookie cookie, Class<T> cls) {
		byte[] data = Base64.getUrlDecoder().decode(cookie.getValue());
		try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
			 ObjectInputStream ois = new ObjectInputStream(bais)) {
			return (T) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new IllegalStateException("쿠키 역직렬화 실패", e);
		}
	}

}
