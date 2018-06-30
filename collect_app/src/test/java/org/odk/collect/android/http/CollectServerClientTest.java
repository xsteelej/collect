package org.odk.collect.android.http;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.odk.collect.android.http.mock.MockHttpClientConnection;
import org.odk.collect.android.utilities.DocumentFetchResult;

@RunWith(JUnit4.class)
public class CollectServerClientTest {

    private TestableCollectServerClient collectServerClient;

    @Before
    public void setup() {
        collectServerClient = new TestableCollectServerClient(new MockHttpClientConnection());
    }

    @Test
    public void testGetXMLDocumentErrorResponse() {
        collectServerClient.setGetHttpShouldReturnNull(true);
        DocumentFetchResult fetchResult = collectServerClient.getXmlDocument("http://testurl");
        Assert.assertEquals(fetchResult.errorMessage, "Parsing failed with null while accessing http://testurl");
    }

    @Test
    public void testGetXMLDocument() {
        DocumentFetchResult fetchResult = collectServerClient.getXmlDocument("http://testurl");
        Assert.assertNull(fetchResult.errorMessage);
        Assert.assertEquals(fetchResult.responseCode, 0);
        Assert.assertEquals(fetchResult.isOpenRosaResponse, true);
    }

    @Test
    public void testGetPlainTextMimeType() {
        Assert.assertEquals(TestableCollectServerClient.getPlainTextMimeType(), "text/plain");
    }
}


