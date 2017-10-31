package com.demo.echo;

public class EchoServiceImpl implements EchoService {
	public String echo(String input) {
		return "Echo: " + input;
	}
}
