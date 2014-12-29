package at.cpickl.grotlin.multi.service;

import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Test
public class GoogleAnalyticsTrackingServiceTest {

    public void trackIt() throws Exception {
        String trackingId = "testTrackingId";
        URLFetchService mockedFetcher = Mockito.mock(URLFetchService.class);
        HTTPResponse mockedResponse = Mockito.mock(HTTPResponse.class);
        Mockito.doReturn(200).when(mockedResponse).getResponseCode();
        Mockito.doReturn(mockedResponse).when(mockedFetcher).fetch(Mockito.<HTTPRequest>anyObject());

        GoogleAnalyticsTrackingService testee = new GoogleAnalyticsTrackingService(trackingId, mockedFetcher);
        PageTrack page = new PageTrack("testUrl", "testMethod");
        testee.track(page);

        ArgumentCaptor<HTTPRequest> argumentCaptor = ArgumentCaptor.forClass(HTTPRequest.class);
        Mockito.verify(mockedFetcher).fetch(argumentCaptor.capture());
        HTTPRequest actualRequest = argumentCaptor.getValue();
        String actualPayload = new String(actualRequest.getPayload());
        assertThat(actualPayload, equalTo("dt=" + page.getMethod() + "&t=pageview&v=1&dp=" + page.getUrl() + "&tid=" + trackingId + "&cid=555"));
        Mockito.verifyNoMoreInteractions(mockedFetcher);
    }
}
