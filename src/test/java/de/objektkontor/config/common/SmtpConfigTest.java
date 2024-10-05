package de.objektkontor.config.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import de.objektkontor.config.common.smtp.AuthConfig;
import de.objektkontor.config.common.smtp.HttpProxyConfig;
import de.objektkontor.config.common.smtp.SaslConfig;
import de.objektkontor.config.common.smtp.SocketFactoryConfig;
import de.objektkontor.config.common.smtp.SocksProxyConfig;
import de.objektkontor.config.common.smtp.SslConfig;
import de.objektkontor.config.common.smtp.StartTlsConfig;

public class SmtpConfigTest {

	private SmtpConfig createConfig() {
		SmtpConfig config = new SmtpConfig();
		config.setAuth(new AuthConfig());
		config.setHttpProxy(new HttpProxyConfig());
		config.setSasl(new SaslConfig());
		config.setSocketFactory(new SocketFactoryConfig());
		config.setSocksProxy(new SocksProxyConfig());
		config.setSsl(new SslConfig());
		config.getSsl().setSocketFactory(new SocketFactoryConfig());
		config.setStartTls(new StartTlsConfig());
		return config;
	}

	private void fillTestData(SmtpConfig config) {
		config.setUser("testUser");
		config.setHost("testHost");
		config.setPort(25);
		config.setConnectionTimeout(1000);
		config.setTimeout(2000);
		config.setWriteTimeout(3000);
		config.setFrom("testFrom");
		config.setLocalHost("testLocalHost");
		config.setLocalAddress("testLocalAddress");
		config.setLocalPort(225);
		config.setEhlo(true);
		config.getAuth().setEnable(true);
		config.getAuth().setMechanisms("testAuthMechanisms");
		config.getAuth().setLoginDisable(true);
		config.getAuth().setPlainDisable(true);
		config.getAuth().setDigestMd5Disable(true);
		config.getAuth().setNtlmDisable(true);
		config.getAuth().setNtlmDomain("testDomain");
		config.getAuth().setNtlmFlags(1234);
		config.getAuth().setXoauth2Disable(true);
		config.setSubmitter("testSubmitter");
		config.setDsnNotify("testDsnNotify");
		config.setDsnRet("testDsnRet");
		config.setAllow8BitMime(true);
		config.setSendPartial(true);
		config.getSasl().setEnable(true);
		config.getSasl().setMechanisms("testSaslMechanisms");
		config.getSasl().setAuthorizationId("testAuthorizationId");
		config.getSasl().setRealm("testRealm");
		config.getSasl().setUseCanonicalHostName(true);
		config.setQuitWait(true);
		config.setReportsuccess(true);
		config.getSocketFactory().setClassName("testClassName");
		config.getSocketFactory().setFallback(false);
		config.getSocketFactory().setPort(2225);
		config.getSsl().setEnable(true);
		config.getSsl().setCheckServerIdentity(true);
		config.getSsl().setTrust("testTrust");
		config.getSsl().getSocketFactory().setClassName("testSslClassName");
		config.getSsl().getSocketFactory().setPort(22225);
		config.getSsl().setProtocols("testProtocols");
		config.getSsl().setCipherSuites("testCipherSuites");
		config.getStartTls().setEnable(true);
		config.getStartTls().setRequired(true);
		config.getHttpProxy().setHost("testHttpProxyHost");
		config.getHttpProxy().setPort(80);
		config.getSocksProxy().setHost("testSocksProxyHost");
		config.getSocksProxy().setPort(1080);
		config.setMailExtension("testMailExtensions");
		config.setUseRset(true);
		config.setNoopStrict(true);
	}

	private void assertConfig(Properties properties, String prefix) {
		assertEquals("testUser", properties.getProperty(prefix + "user"));
		assertEquals("testHost", properties.getProperty(prefix + "host"));
		assertEquals("25", properties.getProperty(prefix + "port"));
		assertEquals("1000", properties.getProperty(prefix + "connectiontimeout"));
		assertEquals("2000", properties.getProperty(prefix + "timeout"));
		assertEquals("3000", properties.getProperty(prefix + "writetimeout"));
		assertEquals("testFrom", properties.getProperty(prefix + "from"));
		assertEquals("testLocalHost", properties.getProperty(prefix + "localhost"));
		assertEquals("testLocalAddress", properties.getProperty(prefix + "localaddress"));
		assertEquals("225", properties.getProperty(prefix + "localport"));
		assertEquals("true", properties.getProperty(prefix + "ehlo"));
		assertEquals("true", properties.getProperty(prefix + "auth.enable"));
		assertEquals("testAuthMechanisms", properties.getProperty(prefix + "auth.mechanisms"));
		assertEquals("true", properties.getProperty(prefix + "auth.login.disable"));
		assertEquals("true", properties.getProperty(prefix + "auth.plain.disable"));
		assertEquals("true", properties.getProperty(prefix + "auth.digest-md5.disable"));
		assertEquals("true", properties.getProperty(prefix + "auth.ntlm.disable"));
		assertEquals("testDomain", properties.getProperty(prefix + "auth.ntlm.domain"));
		assertEquals("1234", properties.getProperty(prefix + "auth.ntlm.flags"));
		assertEquals("true", properties.getProperty(prefix + "auth.xoauth2.disable"));
		assertEquals("testSubmitter", properties.getProperty(prefix + "submitter"));
		assertEquals("testDsnNotify", properties.getProperty(prefix + "dsn.notify"));
		assertEquals("testDsnRet", properties.getProperty(prefix + "dsn.ret"));
		assertEquals("true", properties.getProperty(prefix + "allow8bitmime"));
		assertEquals("true", properties.getProperty(prefix + "sendpartial"));
		assertEquals("true", properties.getProperty(prefix + "sasl.enable"));
		assertEquals("testSaslMechanisms", properties.getProperty(prefix + "sasl.mechanisms"));
		assertEquals("testAuthorizationId", properties.getProperty(prefix + "sasl.authorizationid"));
		assertEquals("testRealm", properties.getProperty(prefix + "sasl.realm"));
		assertEquals("true", properties.getProperty(prefix + "sasl.usecanonicalhostname"));
		assertEquals("true", properties.getProperty(prefix + "quitwait"));
		assertEquals("true", properties.getProperty(prefix + "reportsuccess"));
		assertEquals("testClassName", properties.getProperty(prefix + "socketFactory.class"));
		assertEquals("false", properties.getProperty(prefix + "socketFactory.fallback"));
		assertEquals("2225", properties.getProperty(prefix + "socketFactory.port"));
		assertEquals("true", properties.getProperty(prefix + "ssl.enable"));
		assertEquals("true", properties.getProperty(prefix + "ssl.checkserveridentity"));
		assertEquals("testTrust", properties.getProperty(prefix + "ssl.trust"));
		assertEquals("testSslClassName", properties.getProperty(prefix + "ssl.socketFactory.class"));
		assertEquals("22225", properties.getProperty(prefix + "ssl.socketFactory.port"));
		assertEquals("testProtocols", properties.getProperty(prefix + "ssl.protocols"));
		assertEquals("testCipherSuites", properties.getProperty(prefix + "ssl.ciphersuites"));
		assertEquals("true", properties.getProperty(prefix + "starttls.enable"));
		assertEquals("true", properties.getProperty(prefix + "starttls.required"));
		assertEquals("testHttpProxyHost", properties.getProperty(prefix + "proxy.host"));
		assertEquals("80", properties.getProperty(prefix + "proxy.port"));
		assertEquals("testSocksProxyHost", properties.getProperty(prefix + "socks.host"));
		assertEquals("1080", properties.getProperty(prefix + "socks.port"));
		assertEquals("testMailExtensions", properties.getProperty(prefix + "mailextension"));
		assertEquals("true", properties.getProperty(prefix + "userset"));
		assertEquals("true", properties.getProperty(prefix + "noop.strict"));
	}

	@Test
	public void testApplyTo() {
		SmtpConfig config = createConfig();
		fillTestData(config);
		Properties properties = new Properties();
		config.applyTo(properties);
		assertEquals(51, properties.size());
		assertConfig(properties, "mail.smtp.");
	}

	@Test
	public void testApplyToSmtps() {
		SmtpConfig config = createConfig();
		fillTestData(config);
		Properties properties = new Properties();
		config.applyToSmtps(properties);
		assertEquals(51, properties.size());
		assertConfig(properties, "mail.smtps.");
	}

	@Test
	public void testApplyToOfEmptyConfig() {
		SmtpConfig config = createConfig();
		Properties properties = new Properties();
		config.applyTo(properties);
		assertEquals(0, properties.size());
	}

	@Test
	public void testApplyToOfNullConfig() {
		SmtpConfig config = new SmtpConfig();
		Properties properties = new Properties();
		config.applyTo(properties);
		assertEquals(0, properties.size());
	}
}
