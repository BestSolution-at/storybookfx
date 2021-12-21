package at.bestsolution.storybookfx;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

import at.bestsolution.storybookfx.impl.StorybookApplication;

public class Storybook {
	public static void launch(String[] args) {
		URL.setURLStreamHandlerFactory( new URLStreamHandlerFactory() {
			
			@Override
			public URLStreamHandler createURLStreamHandler(String protocol) {
				if( "nocache".equals(protocol) ) {
					return new NoCacheStreamHandler();
				}
				return null;
			}
		});
		StorybookApplication.launch(StorybookApplication.class, args);
	}
	
	private static class NoCacheStreamHandler extends URLStreamHandler {

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			String url = u.toExternalForm().replace("nocache:","");
			if( url.indexOf('?') != -1 ) {
				url = url.substring(0, url.indexOf('?'));
			}
			return new URL(url).openConnection();
		}
		
	}
}
