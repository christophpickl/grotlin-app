package at.cpickl.grotlin.multi;

import at.cpickl.grotlin.channel.ChannelNotification;
import at.cpickl.grotlin.multi.service.ChannelApiService;
import at.cpickl.grotlin.multi.service.User;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NotificationTestUtil {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationTestUtil.class);

    public static void assertSentNotifications(ChannelApiService mockedChannelApiService, NotificationMatcher... matchers) {
        ArgumentCaptor<ChannelNotification> sentNotifications = ArgumentCaptor.forClass(ChannelNotification.class);
        ArgumentCaptor<Collection> receivers = ArgumentCaptor.forClass(Collection.class);
        verify(mockedChannelApiService, times(matchers.length)).sendNotification(sentNotifications.capture(), receivers.capture());
        for (int i = 0; i < matchers.length; i++) {
            ChannelNotification sentNotification = sentNotifications.getAllValues().get(i);
            Collection receivedUsers = receivers.getAllValues().get(i);
            LOG.trace("Verifying sent message at index {}: {}", i, sentNotification);
            matchers[i].matchNotification(sentNotification);
            matchers[i].matchReceivers(receivedUsers);
        }
        Mockito.verifyNoMoreInteractions(mockedChannelApiService);
    }

    public static class AllOfMatcher extends NotificationMatcher {
        private final Collection<NotificationMatcher> matchers;

        public AllOfMatcher(NotificationMatcher... matchers) {
            this.matchers = Arrays.asList(matchers);
        }

        @Override
        void matchNotification(ChannelNotification notification) {
            for (NotificationMatcher matcher : matchers) {
                matcher.matchNotification(notification);
            }
        }

        @Override
        void matchReceivers(Collection<User> receivers) {
            for (NotificationMatcher matcher : matchers) {
                matcher.matchReceivers(receivers);
            }
        }
    }

    public static class ReceiversMatcher extends NotificationMatcher {

        private final Collection<User> expectedReceivers;

        public ReceiversMatcher(User... expectedReceivers) {
            this.expectedReceivers = Arrays.asList(expectedReceivers);
        }

        @Override
        void matchReceivers(Collection<User> receivers) {
            assertThat(receivers, equalTo(expectedReceivers));
        }
    }

    public static class InstanceOfMatcher extends NotificationMatcher {

        private final Class<? extends ChannelNotification> notificationType;

        public InstanceOfMatcher(Class<? extends ChannelNotification> notificationType) {
            this.notificationType = notificationType;
        }

        @Override
        void matchNotification(ChannelNotification notification) {
            assertThat(notification, instanceOf(notificationType));
        }
    }

    public static class EqualsMatcher extends NotificationMatcher {

        private final ChannelNotification expectedNotification;

        public EqualsMatcher(ChannelNotification expectedNotification) {
            this.expectedNotification = expectedNotification;
        }

        @Override
        void matchNotification(ChannelNotification notification) {
            assertThat(notification, equalTo(expectedNotification));
        }

    }

    static abstract class NotificationMatcher {
        void matchNotification(ChannelNotification notification) {
            // no-opt
        }

        void matchReceivers(Collection<User> receivers) {
            // no-opt
        }
    }
}
